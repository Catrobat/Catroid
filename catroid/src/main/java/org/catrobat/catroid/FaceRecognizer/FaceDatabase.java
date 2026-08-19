package org.catrobat.catroid.FaceRecognizer;

import android.util.Log;

import org.catrobat.catroid.FaceRecognizer.env.FileUtils;
import org.catrobat.catroid.FaceRecognizer.ml.FaceNet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Owns the three files and does the matching.
 *
 *   label : one person name per line. The line number is the person index.
 *   data  : one training embedding per line, "<index> v0 v1 ... v511".
 *   model : compiled matcher, rebuilt from data on every change.
 *             line 1  "v1 <embeddingSize> <personCount> <minSimilarity> <minMargin>"
 *             then    "<index> <sampleCount> c0 c1 ... c511"   centroid, unit length
 *
 * All three are written together from memory, so their indices can never drift apart.
 * Matching score for a person is a blend of the nearest training samples and the
 * centroid, both cosine similarities on unit length embeddings.
 */
public class FaceDatabase {
	public static final String TAG = "FaceDatabase";

	/**
	 * Accept a match only above this cosine similarity.
	 * Raise it if strangers get a name. Lower it if known people come back Unknown.
	 * Useful range 0.45 to 0.72.
	 */
	public static float minSimilarity = 0.50f;

	/**
	 * The winner must beat the runner up by this much. This is what stops the app
	 * putting the wrong person's name on a face.
	 */
	public static float minMargin = 0.05f;
	/** Never accept collapsed/no-detail embeddings even if an old model saved 0.50. */
	private static final float ABSOLUTE_SAFETY_FLOOR = 0.60f;

	/**
	 * Nearest neighbour. A person is scored against their single closest training
	 * photo, not an average of several.
	 *
	 * This matters when one person is enrolled at different ages and angles. Only
	 * the age matched and angle matched photo will be close, and averaging several
	 * photos throws that signal away. Raise this only if every photo of a person
	 * was taken in one sitting.
	 */
	private static final int TOP_K = 1;

	/** Blend between nearest samples and the centroid. */
	// Weighted towards the nearest training photos. The centroid of a few varied
// photos scores low against any single query, so leaning on it pushed correct
// matches under the threshold.
	/**
	 * 1.0 means the centroid is ignored when scoring. The centroid of photos
	 * spanning several years is a blur that resembles nobody, so mixing it in
	 * dragged correct matches down. The centroid is still written to the model
	 * file and used as a fallback when a person has no samples loaded.
	 */
	private static final float SAMPLE_WEIGHT = 1.0f;

	private static final float NO_SCORE = -2f;
	private static final int VERSION = 1;

	/**
	 * Bumped whenever the way an embedding is produced changes: the face crop, the
	 * alignment, the illumination normalisation, the model itself.
	 *
	 * Embeddings from an older pipeline cannot match anything from a newer one, at
	 * any threshold. Without this stamp that failure is silent and looks exactly
	 * like a broken camera or a bad threshold.
	 *
	 * 1  original square crop, no illumination normalisation
	 * 2  illumination normalisation, eye alignment, multi scale variants
	 */
	private static final int PIPELINE_VERSION = 2;

	/** True when the stored embeddings were made by a different pipeline. */
	private boolean staleEmbeddings = false;

	public synchronized boolean hasStaleEmbeddings() {
		return staleEmbeddings;
	}

	public static class Match {
		public final int index;
		public final String name;
		public final float similarity;
		public final float margin;

		Match(int index, String name, float similarity, float margin) {
			this.index = index;
			this.name = name;
			this.similarity = similarity;
			this.margin = margin;
		}
	}

	private final List<String> names = new ArrayList<>();
	private final List<List<float[]>> samples = new ArrayList<>();
	private final List<float[]> centroids = new ArrayList<>();

	private long loadedStamp = -1L;

	// ---------------- Load and save ----------------

	public synchronized void load() {
		names.clear();
		samples.clear();
		centroids.clear();

		for (String name : FileUtils.readLines(FileUtils.LABEL_FILE)) {
			names.add(name);
			samples.add(new ArrayList<float[]>());
			centroids.add(null);
		}

		int loaded = 0;
		int skipped = 0;
		for (String line : FileUtils.readLines(FileUtils.DATA_FILE)) {
			String[] parts = line.split(" ");
			if (parts.length != FaceNet.EMBEDDING_SIZE + 1) {
				skipped++;
				continue;
			}
			try {
				int index = Integer.parseInt(parts[0]);
				if (index < 0 || index >= names.size()) {
					skipped++;
					continue;
				}
				float[] v = new float[FaceNet.EMBEDDING_SIZE];
				for (int i = 0; i < v.length; i++) {
					v[i] = Float.parseFloat(parts[i + 1]);
				}
				samples.get(index).add(v);
				loaded++;
			} catch (NumberFormatException e) {
				skipped++;
			}
		}

		staleEmbeddings = false;
		if (!loadModel()) {
			rebuildCentroids();
			// No readable model file, so the pipeline that made the data is unknown.
			if (!samples.isEmpty() && !FileUtils.readLines(FileUtils.DATA_FILE).isEmpty()) {
				staleEmbeddings = true;
				Log.w(TAG, "No model header, cannot tell which pipeline made these "
						+ "photos. Retrain if detection returns Unknown.");
			}
		}
		stamp();

		Log.i(TAG, "Loaded " + names.size() + " people, " + loaded
				+ " embeddings, " + skipped + " bad lines skipped");
	}

	/** Reloads if another process changed the files. Cheap, safe to call often. */
	public synchronized void ensureFresh() {
		if (currentStamp() != loadedStamp) {
			Log.i(TAG, "Face files changed on disk, reloading");
			load();
		}
	}

	public synchronized boolean save() {
		rebuildCentroids();

		List<String> dataLines = new ArrayList<>();
		for (int index = 0; index < samples.size(); index++) {
			for (float[] v : samples.get(index)) {
				dataLines.add(vectorLine(index, v, -1));
			}
		}

		List<String> modelLines = new ArrayList<>();
		modelLines.add(String.format(Locale.US, "v%d %d %d %.4f %.4f p%d",
				VERSION, FaceNet.EMBEDDING_SIZE, names.size(), minSimilarity, minMargin,
				PIPELINE_VERSION));
		staleEmbeddings = false;
		for (int index = 0; index < centroids.size(); index++) {
			float[] c = centroids.get(index);
			if (c != null) {
				modelLines.add(vectorLine(index, c, samples.get(index).size()));
			}
		}

		boolean ok = FileUtils.writeLines(FileUtils.LABEL_FILE, new ArrayList<>(names));
		ok &= FileUtils.writeLines(FileUtils.DATA_FILE, dataLines);
		ok &= FileUtils.writeLines(FileUtils.MODEL_FILE, modelLines);
		stamp();

		Log.i(TAG, "Saved label(" + names.size() + ") data(" + dataLines.size()
				+ ") model(" + (modelLines.size() - 1) + ") ok=" + ok);
		return ok;
	}

	private boolean loadModel() {
		List<String> lines = FileUtils.readLines(FileUtils.MODEL_FILE);
		if (lines.isEmpty()) {
			return false;
		}
		String[] header = lines.get(0).split(" ");
		if (header.length < 3 || !header[0].equals("v" + VERSION)) {
			Log.w(TAG, "Unknown model format, rebuilding from data");
			return false;
		}
		try {
			if (Integer.parseInt(header[1]) != FaceNet.EMBEDDING_SIZE
					|| Integer.parseInt(header[2]) != names.size()) {
				Log.w(TAG, "Model does not match label file, rebuilding from data");
				return false;
			}
			if (header.length >= 5) {
				minSimilarity = Float.parseFloat(header[3]);
				minMargin = Float.parseFloat(header[4]);
				Log.i(TAG, "Thresholds from model: " + minSimilarity + " / " + minMargin);
			}

			int storedPipeline = 1;
			if (header.length >= 6 && header[5].startsWith("p")) {
				storedPipeline = Integer.parseInt(header[5].substring(1));
			}
			if (storedPipeline != PIPELINE_VERSION) {
				staleEmbeddings = true;
				Log.e(TAG, "STORED PHOTOS ARE OUT OF DATE. They were made by face "
						+ "pipeline v" + storedPipeline + ", this build uses v"
						+ PIPELINE_VERSION + ". They cannot match a new capture at "
						+ "any threshold. Delete each person and add their photos "
						+ "again.");
			}
		} catch (NumberFormatException e) {
			return false;
		}

		int read = 0;
		for (int i = 1; i < lines.size(); i++) {
			String[] parts = lines.get(i).split(" ");
			if (parts.length != FaceNet.EMBEDDING_SIZE + 2) {
				continue;
			}
			try {
				int index = Integer.parseInt(parts[0]);
				if (index < 0 || index >= names.size()) {
					continue;
				}
				float[] c = new float[FaceNet.EMBEDDING_SIZE];
				for (int j = 0; j < c.length; j++) {
					c[j] = Float.parseFloat(parts[j + 2]);
				}
				centroids.set(index, c);
				read++;
			} catch (NumberFormatException ignored) {
				// skip malformed line
			}
		}
		return read > 0;
	}

	private void rebuildCentroids() {
		centroids.clear();
		for (List<float[]> list : samples) {
			centroids.add(meanUnit(list));
		}
	}

	private static String vectorLine(int index, float[] v, int count) {
		StringBuilder sb = new StringBuilder(v.length * 12);
		sb.append(index);
		if (count >= 0) {
			sb.append(' ').append(count);
		}
		for (float value : v) {
			sb.append(' ').append(value);
		}
		return sb.toString();
	}

	private void stamp() {
		loadedStamp = currentStamp();
	}

	private long currentStamp() {
		if (!FileUtils.isReady()) {
			return -1L;
		}
		return FileUtils.file(FileUtils.LABEL_FILE).lastModified() * 31L
				+ FileUtils.file(FileUtils.DATA_FILE).lastModified();
	}

	// ---------------- People ----------------

	/** Changes the thresholds and writes them into the model file header. */
	public synchronized void setThresholds(float similarity, float margin) {
		minSimilarity = Math.max(0.05f, Math.min(0.95f, similarity));
		minMargin = Math.max(0f, Math.min(0.5f, margin));
		Log.i(TAG, "Thresholds set to " + minSimilarity + " / " + minMargin);
		save();
	}

	public synchronized List<String> getNames() {
		return new ArrayList<>(names);
	}

	public synchronized int getPersonCount() {
		return names.size();
	}

	public synchronized int getEmbeddingCount(int index) {
		if (index < 0 || index >= samples.size()) {
			return 0;
		}
		return samples.get(index).size();
	}

	/**
	 * Average cosine similarity between a person's own training photos.
	 * Healthy FaceNet embeddings of one person sit around 0.6 to 0.9 here.
	 * A value near 0.2 means the embeddings carry no identity information at all,
	 * which is a preprocessing or model problem, not a threshold problem.
	 */
	/**
	 * For each stored photo of a person, its similarity to the closest OTHER photo
	 * of the same person. A value near zero means that photo resembles none of the
	 * others, which usually means a wrong person or a bad crop rather than a
	 * genuine age difference.
	 */
	public synchronized float[] photoAffinities(int index) {
		if (index < 0 || index >= samples.size()) {
			return null;
		}
		List<float[]> list = samples.get(index);
		float[] out = new float[list.size()];
		for (int i = 0; i < list.size(); i++) {
			float best = NO_SCORE;
			for (int j = 0; j < list.size(); j++) {
				if (i == j) {
					continue;
				}
				best = Math.max(best, dot(list.get(i), list.get(j)));
			}
			out[i] = best;
		}
		return out;
	}

	/** Drops photos that resemble none of the person's other photos. */
	public synchronized int removeStrayPhotos(int index, float minAffinity) {
		float[] affinities = photoAffinities(index);
		if (affinities == null || affinities.length < 3) {
			return 0;
		}
		List<float[]> list = samples.get(index);
		int removed = 0;
		for (int i = affinities.length - 1; i >= 0; i--) {
			if (affinities[i] < minAffinity) {
				list.remove(i);
				removed++;
			}
		}
		if (removed > 0) {
			save();
		}
		return removed;
	}

	public synchronized float internalConsistency(int index) {
		if (index < 0 || index >= samples.size()) {
			return NO_SCORE;
		}
		List<float[]> list = samples.get(index);
		if (list.size() < 2) {
			return NO_SCORE;
		}
		float sum = 0f;
		int pairs = 0;
		for (int i = 0; i < list.size(); i++) {
			for (int j = i + 1; j < list.size(); j++) {
				sum += dot(list.get(i), list.get(j));
				pairs++;
			}
		}
		return pairs == 0 ? NO_SCORE : sum / pairs;
	}

	/** Average similarity between two different people's photos. Should be low. */
	public synchronized float crossSimilarity(int a, int b) {
		if (a < 0 || b < 0 || a >= samples.size() || b >= samples.size()) {
			return NO_SCORE;
		}
		List<float[]> la = samples.get(a);
		List<float[]> lb = samples.get(b);
		if (la.isEmpty() || lb.isEmpty()) {
			return NO_SCORE;
		}
		float sum = 0f;
		int pairs = 0;
		for (float[] va : la) {
			for (float[] vb : lb) {
				sum += dot(va, vb);
				pairs++;
			}
		}
		return pairs == 0 ? NO_SCORE : sum / pairs;
	}

	/** First stored embedding for a person, or null. Used by the self test. */
	public synchronized float[] getFirstEmbedding(int index) {
		if (index < 0 || index >= samples.size() || samples.get(index).isEmpty()) {
			return null;
		}
		return samples.get(index).get(0).clone();
	}

	public synchronized int indexOf(String name) {
		for (int i = 0; i < names.size(); i++) {
			if (names.get(i).equalsIgnoreCase(name)) {
				return i;
			}
		}
		return -1;
	}

	public synchronized int addPerson(String name) {
		names.add(name);
		samples.add(new ArrayList<float[]>());
		centroids.add(null);
		return names.size() - 1;
	}

	public synchronized void addEmbeddings(int index, List<float[]> list) {
		if (index < 0 || index >= samples.size() || list == null) {
			return;
		}
		for (float[] v : list) {
			if (v != null && v.length == FaceNet.EMBEDDING_SIZE) {
				samples.get(index).add(v);
			}
		}
	}

	public synchronized void deletePerson(int index) {
		if (index < 0 || index >= names.size()) {
			return;
		}
		names.remove(index);
		samples.remove(index);
		centroids.remove(index);
	}

	// ---------------- Matching ----------------

	/** Returns the matched person, or null when nothing is close enough. */
	/** Score for every enrolled person, in index order. Used for multi frame voting. */
	public synchronized float[] scoreAll(float[] query) {
		if (query == null || query.length != FaceNet.EMBEDDING_SIZE || names.isEmpty()) {
			return null;
		}
		float[] scores = new float[names.size()];
		for (int i = 0; i < names.size(); i++) {
			scores[i] = personScore(i, query);
		}
		return scores;
	}

	/**
	 * Best score across several query variants of the same face. Element by element
	 * maximum, so a person wins on whichever variant fits them best.
	 */
	public synchronized float[] scoreAllVariants(List<float[]> queries) {
		if (queries == null || queries.isEmpty()) {
			return null;
		}
		float[] best = null;
		for (float[] q : queries) {
			float[] s = scoreAll(q);
			if (s == null) {
				continue;
			}
			if (best == null) {
				best = s;
			} else {
				for (int i = 0; i < best.length && i < s.length; i++) {
					if (s[i] > best[i]) {
						best[i] = s[i];
					}
				}
			}
		}
		return best;
	}

	/** Applies the two thresholds to an already combined score array. */
	public synchronized Match decide(float[] scores) {
		if (scores == null || scores.length != names.size() || names.isEmpty()) {
			return null;
		}

		float best = NO_SCORE;
		float second = NO_SCORE;
		int bestIndex = -1;

		for (int i = 0; i < scores.length; i++) {
			if (scores[i] == NO_SCORE) {
				continue;
			}
			if (scores[i] > best) {
				second = best;
				best = scores[i];
				bestIndex = i;
			} else if (scores[i] > second) {
				second = scores[i];
			}
		}

		if (bestIndex < 0) {
			return null;
		}
		float margin = (second == NO_SCORE) ? 1f : best - second;
		float requiredSimilarity = Math.max(minSimilarity, ABSOLUTE_SAFETY_FLOOR);
		if (best < requiredSimilarity || margin < minMargin) {
			Log.i(TAG, String.format(Locale.US,
					"Rejected: best=%.3f margin=%.3f (need %.2f / %.2f)",
					best, margin, requiredSimilarity, minMargin));
			return null;
		}
		return new Match(bestIndex, names.get(bestIndex), best, margin);
	}

	public synchronized String describeScoreArray(float[] scores) {
		if (scores == null) {
			return "no scores";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < names.size() && i < scores.length; i++) {
			sb.append(String.format(Locale.US, "%s=%.3f ", names.get(i), scores[i]));
		}
		return sb.toString().trim();
	}

	public synchronized Match match(float[] query) {
		if (query == null || query.length != FaceNet.EMBEDDING_SIZE || names.isEmpty()) {
			return null;
		}

		float best = NO_SCORE;
		float second = NO_SCORE;
		int bestIndex = -1;

		for (int i = 0; i < names.size(); i++) {
			float score = personScore(i, query);
			if (score == NO_SCORE) {
				continue;
			}
			if (score > best) {
				second = best;
				best = score;
				bestIndex = i;
			} else if (score > second) {
				second = score;
			}
		}

		if (bestIndex < 0) {
			return null;
		}

		float margin = (second == NO_SCORE) ? 1f : best - second;
		float requiredSimilarity = Math.max(minSimilarity, ABSOLUTE_SAFETY_FLOOR);
		if (best < requiredSimilarity || margin < minMargin) {
			Log.i(TAG, String.format(Locale.US,
					"Rejected: best=%.3f margin=%.3f (need %.2f / %.2f)",
					best, margin, requiredSimilarity, minMargin));
			return null;
		}
		return new Match(bestIndex, names.get(bestIndex), best, margin);
	}

	/** One line of every person's score. Use this to tune the two thresholds. */
	public synchronized String describeScores(float[] query) {
		if (query == null) {
			return "no query embedding";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < names.size(); i++) {
			sb.append(String.format(Locale.US, "%s=%.3f(%d photos) ",
					names.get(i), personScore(i, query), samples.get(i).size()));
		}
		return sb.toString().trim();
	}

	private float personScore(int index, float[] query) {
		List<float[]> list = samples.get(index);
		float[] centroid = centroids.get(index);

		if (list.isEmpty() && centroid == null) {
			return NO_SCORE;
		}
		if (list.isEmpty()) {
			return dot(centroid, query);
		}

		float[] top = new float[TOP_K];
		Arrays.fill(top, NO_SCORE);
		for (float[] v : list) {
			float s = dot(v, query);
			for (int i = 0; i < TOP_K; i++) {
				if (s > top[i]) {
					for (int j = TOP_K - 1; j > i; j--) {
						top[j] = top[j - 1];
					}
					top[i] = s;
					break;
				}
			}
		}

		float sum = 0f;
		int count = 0;
		for (float t : top) {
			if (t != NO_SCORE) {
				sum += t;
				count++;
			}
		}
		float sampleScore = sum / count;

		if (centroid == null) {
			return sampleScore;
		}
		return SAMPLE_WEIGHT * sampleScore + (1f - SAMPLE_WEIGHT) * dot(centroid, query);
	}

	private static float[] meanUnit(List<float[]> list) {
		if (list == null || list.isEmpty()) {
			return null;
		}
		float[] mean = new float[FaceNet.EMBEDDING_SIZE];
		for (float[] v : list) {
			for (int i = 0; i < mean.length; i++) {
				mean[i] += v[i];
			}
		}
		double sum = 0.0;
		for (float value : mean) {
			sum += value * value;
		}
		float norm = (float) Math.sqrt(sum);
		if (norm < 1e-10f) {
			return null;
		}
		for (int i = 0; i < mean.length; i++) {
			mean[i] /= norm;
		}
		return mean;
	}

	/** Both vectors are unit length, so this is the cosine similarity. */
	private static float dot(float[] a, float[] b) {
		float sum = 0f;
		for (int i = 0; i < a.length; i++) {
			sum += a[i] * b[i];
		}
		return sum;
	}
}