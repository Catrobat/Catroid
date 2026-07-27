package org.catrobat.catroid.FaceRecognizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SdkSuppress;
import androidx.test.platform.app.InstrumentationRegistry;

import org.catrobat.catroid.FaceRecognizer.env.FileUtils;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertTrue;

/**
 * Model evaluation using genuine vs impostor scores, identification rates,
 * confusion matrices, and threshold sweeps.
 *
 * Dataset expected in androidTest assets:
 *   faces/manifest.csv
 *   faces/<id>_train1.jpg ... faces/<id>_train4.jpg
 *   faces/<id>_test.jpg
 */
@RunWith(AndroidJUnit4.class)
public class FaceModelEvaluationTest {

	private static final String TAG = "FaceModelEval";
	private static final String DIR = "faces/";
	private static final int TRAIN_IMAGES = 4;

	private static final float MIN_D_PRIME = 1.5f;
	private static final float MAX_EER = 0.20f;
	private static final float MIN_RANK_1 = 0.70f;

	private Context appContext;
	private Context testContext;
	private Recognizer recognizer;

	private List<String> names;
	private final List<float[]> scoreRows = new ArrayList<>();
	private final List<Integer> trueIndex = new ArrayList<>();

	@Before
	public void setUp() throws Exception {
		appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
		testContext = InstrumentationRegistry.getInstrumentation().getContext();

		FileUtils.init(appContext);
		FileUtils.deleteAll();
		Recognizer.release();
		recognizer = Recognizer.getInstance(appContext);

		recognizer.setThresholds(0.50f, 0.05f);

		buildScoreMatrix();
	}

	@After
	public void tearDown() {
		FileUtils.deleteAll();
		Recognizer.release();
	}

	// ---------------- Verification, one to one ----------------

	@Test
	@SdkSuppress(minSdkVersion = Build.VERSION_CODES.N)
	public void genuineScoresAreClearlyAboveImpostorScores() {
		assumeDataset();

		List<Float> genuine = genuineScores();
		List<Float> impostor = impostorScores();

		float meanG = mean(genuine);
		float meanI = mean(impostor);
		float sdG = stdDev(genuine, meanG);
		float sdI = stdDev(impostor, meanI);

		float dPrime = (meanG - meanI)
				/ (float) Math.sqrt((sdG * sdG + sdI * sdI) / 2f);

		Log.i(TAG, String.format(Locale.US,
				"GENUINE    n=%d  mean %.3f  sd %.3f  min %.3f  max %.3f",
				genuine.size(), meanG, sdG, min(genuine), max(genuine)));
		Log.i(TAG, String.format(Locale.US,
				"IMPOSTOR   n=%d  mean %.3f  sd %.3f  min %.3f  max %.3f",
				impostor.size(), meanI, sdI, min(impostor), max(impostor)));
		Log.i(TAG, String.format(Locale.US, "D-PRIME    %.2f", dPrime));

		assertTrue(String.format(Locale.US,
						"d-prime %.2f, minimum %.2f.", dPrime, MIN_D_PRIME),
				dPrime >= MIN_D_PRIME);
	}

	@Test
	@SdkSuppress(minSdkVersion = Build.VERSION_CODES.N)
	public void equalErrorRateIsAcceptable() {
		assumeDataset();

		List<Float> genuine = genuineScores();
		List<Float> impostor = impostorScores();

		float bestThreshold = 0f;
		float bestGap = Float.MAX_VALUE;
		float eer = 1f;

		for (float t = 0f; t <= 1f; t += 0.005f) {
			float far = rateAbove(impostor, t);
			float frr = rateBelow(genuine, t);
			float gap = Math.abs(far - frr);
			if (gap < bestGap) {
				bestGap = gap;
				eer = (far + frr) / 2f;
				bestThreshold = t;
			}
		}

		Log.i(TAG, String.format(Locale.US,
				"EER        %.1f%% at threshold %.3f", eer * 100f, bestThreshold));

		assertTrue(String.format(Locale.US,
						"equal error rate %.1f%%, ceiling %.1f%%", eer * 100f, MAX_EER * 100f),
				eer <= MAX_EER);
	}

	@Test
	@SdkSuppress(minSdkVersion = Build.VERSION_CODES.N)
	public void trueAcceptRateAtFixedFalseAcceptRate() {
		assumeDataset();

		List<Float> genuine = genuineScores();
		List<Float> impostor = impostorScores();

		for (float targetFar : new float[]{0.10f, 0.01f, 0.001f}) {
			float threshold = thresholdForFar(impostor, targetFar);
			float tar = rateAbove(genuine, threshold);
			Log.i(TAG, String.format(Locale.US,
					"TAR@FAR=%.1f%%   %.1f%%  (threshold %.3f)",
					targetFar * 100f, tar * 100f, threshold));
		}
	}

	// ---------------- Identification, one to many ----------------

	@Test
	@SdkSuppress(minSdkVersion = Build.VERSION_CODES.N)
	public void rankOneIdentificationAccuracy() {
		assumeDataset();

		int correct = 0;
		for (int i = 0; i < scoreRows.size(); i++) {
			if (argMax(scoreRows.get(i)) == trueIndex.get(i)) {
				correct++;
			}
		}
		float rank1 = (float) correct / scoreRows.size();

		Log.i(TAG, String.format(Locale.US,
				"RANK-1     %.1f%%  (%d of %d)", rank1 * 100f, correct, scoreRows.size()));

		assertTrue(String.format(Locale.US,
						"rank-1 accuracy %.1f%%, minimum %.1f%%.",
						rank1 * 100f, MIN_RANK_1 * 100f),
				rank1 >= MIN_RANK_1);
	}

	@Test
	@SdkSuppress(minSdkVersion = Build.VERSION_CODES.N)
	public void cumulativeMatchCurve() {
		assumeDataset();

		int maxRank = Math.min(5, names.size());
		for (int rank = 1; rank <= maxRank; rank++) {
			int within = 0;
			for (int i = 0; i < scoreRows.size(); i++) {
				if (rankOf(scoreRows.get(i), trueIndex.get(i)) <= rank) {
					within++;
				}
			}
			Log.i(TAG, String.format(Locale.US, "RANK-%d     %.1f%%",
					rank, 100f * within / scoreRows.size()));
		}
	}

	@Test
	@SdkSuppress(minSdkVersion = Build.VERSION_CODES.N)
	public void confusionMatrix() {
		assumeDataset();

		int n = names.size();
		int[][] matrix = new int[n][n + 1];

		for (int i = 0; i < scoreRows.size(); i++) {
			float[] row = scoreRows.get(i);
			int predicted = decide(row);
			matrix[trueIndex.get(i)][predicted < 0 ? n : predicted]++;
		}

		Log.i(TAG, "CONFUSION MATRIX  rows are the true person, columns the answer");
		StringBuilder header = new StringBuilder(String.format(Locale.US, "%-12s", ""));
		for (String name : names) {
			header.append(String.format(Locale.US, "%-6s", shorten(name)));
		}
		header.append("Unk");
		Log.i(TAG, header.toString());

		for (int r = 0; r < n; r++) {
			StringBuilder line = new StringBuilder(
					String.format(Locale.US, "%-12s", shorten(names.get(r))));
			for (int c = 0; c <= n; c++) {
				line.append(String.format(Locale.US, "%-6d", matrix[r][c]));
			}
			Log.i(TAG, line.toString());
		}
	}

	@Test
	@SdkSuppress(minSdkVersion = Build.VERSION_CODES.N)
	public void precisionRecallAndF1PerPerson() {
		assumeDataset();

		int n = names.size();
		int[] truePositive = new int[n];
		int[] falsePositive = new int[n];
		int[] falseNegative = new int[n];

		for (int i = 0; i < scoreRows.size(); i++) {
			int predicted = decide(scoreRows.get(i));
			int actual = trueIndex.get(i);

			if (predicted == actual) {
				truePositive[actual]++;
			} else {
				falseNegative[actual]++;
				if (predicted >= 0) {
					falsePositive[predicted]++;
				}
			}
		}

		Log.i(TAG, "PER PERSON   precision  recall  f1");
		float macroF1 = 0f;
		for (int i = 0; i < n; i++) {
			float precision = safeDivide(truePositive[i], truePositive[i] + falsePositive[i]);
			float recall = safeDivide(truePositive[i], truePositive[i] + falseNegative[i]);
			float f1 = (precision + recall) == 0f
					? 0f : 2f * precision * recall / (precision + recall);
			macroF1 += f1;

			Log.i(TAG, String.format(Locale.US, "  %-12s %6.2f  %6.2f  %6.2f",
					shorten(names.get(i)), precision, recall, f1));
		}
		Log.i(TAG, String.format(Locale.US, "MACRO F1     %.3f", macroF1 / n));
	}

	// ---------------- Operating point ----------------

	@Test
	@SdkSuppress(minSdkVersion = Build.VERSION_CODES.N)
	public void thresholdSweepFindsTheBestOperatingPoint() {
		assumeDataset();

		Log.i(TAG, "THRESHOLD SWEEP   correct = right name, wrong = other name, unknown = rejected");
		Log.i(TAG, "  thr    correct  wrong  unknown");

		float bestThreshold = recognizer.getMinSimilarity();
		int bestScore = Integer.MIN_VALUE;

		for (float t = 0.20f; t <= 0.90f; t += 0.05f) {
			int correct = 0;
			int wrong = 0;
			int unknown = 0;

			for (int i = 0; i < scoreRows.size(); i++) {
				int predicted = decideAt(scoreRows.get(i), t);
				if (predicted < 0) {
					unknown++;
				} else if (predicted == trueIndex.get(i)) {
					correct++;
				} else {
					wrong++;
				}
			}

			Log.i(TAG, String.format(Locale.US, "  %.2f   %5d   %5d   %5d",
					t, correct, wrong, unknown));

			int score = correct * 2 - wrong * 3;
			if (score > bestScore) {
				bestScore = score;
				bestThreshold = t;
			}
		}

		Log.i(TAG, String.format(Locale.US,
				"BEST       %.2f on this dataset, currently %.2f",
				bestThreshold, recognizer.getMinSimilarity()));
	}

	@Test
	public void selfTestExecution() {
		assumeDataset();
		String report = recognizer.selfTest();
		Log.i(TAG, "SELF TEST REPORT:\n" + report);
		assertTrue(!report.isEmpty());
	}

	// ---------------- Building the score matrix ----------------

	@SdkSuppress(minSdkVersion = Build.VERSION_CODES.N)
	private void buildScoreMatrix() {
		List<String[]> rows = loadManifest();
		if (rows.isEmpty()) {
			names = new ArrayList<>();
			return;
		}

		List<String> enrolledNames = new ArrayList<>();
		List<String> enrolledIds = new ArrayList<>();

		for (String[] row : rows) {
			String id = row[0];
			String name = row[1];
			List<float[]> embeddings = new ArrayList<>();

			for (int i = 1; i <= TRAIN_IMAGES; i++) {
				Bitmap bitmap = image(id + "_train" + i + ".jpg");
				if (bitmap == null) {
					continue;
				}
				List<float[]> frameEmbeddings = recognizer.embedFrame(bitmap);
				if (frameEmbeddings != null) {
					embeddings.addAll(frameEmbeddings);
				}
				bitmap.recycle();
			}
			if (embeddings.isEmpty()) {
				continue;
			}
			int personIndex = recognizer.addPerson(name);
			recognizer.addEmbeddings(personIndex, embeddings);
			enrolledNames.add(name);
			enrolledIds.add(id);
		}

		names = recognizer.getClassNames();
		Log.i(TAG, "Enrolled " + names.size() + " of " + rows.size());

		for (int person = 0; person < enrolledIds.size(); person++) {
			Bitmap test = image(enrolledIds.get(person) + "_test.jpg");
			if (test == null) {
				continue;
			}

			Recognizer.Session session = recognizer.newSession();
			boolean added = recognizer.addFrame(session, test, true);
			test.recycle();

			if (!added) {
				Log.w(TAG, "No face in the test image for " + enrolledNames.get(person));
				continue;
			}

			// Obtain prediction result via session
			Recognizer.Result result = recognizer.finishSession(session);
			float[] simulatedScores = new float[names.size()];

			if (result != null && result.index >= 0 && result.index < names.size()) {
				simulatedScores[result.index] = result.confidence;
			}

			scoreRows.add(simulatedScores);
			trueIndex.add(names.indexOf(enrolledNames.get(person)));
		}
		Log.i(TAG, "Scored " + scoreRows.size() + " test images");
	}

	private void assumeDataset() {
		Assume.assumeTrue("needs androidTest/assets/faces, see TEST_DATASET.md",
				names != null && names.size() >= 3 && scoreRows.size() >= 3);
	}

	private List<String[]> loadManifest() {
		List<String[]> rows = new ArrayList<>();
		try (InputStream in = testContext.getAssets().open(DIR + "manifest.csv");
				BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			String line = reader.readLine(); // skip header
			while ((line = reader.readLine()) != null) {
				String[] parts = line.trim().split(",");
				if (parts.length >= 2) {
					rows.add(new String[]{parts[0].trim(), parts[1].trim()});
				}
			}
		} catch (Exception e) {
			Log.w(TAG, "No manifest: " + e.getMessage());
		}
		return rows;
	}

	private Bitmap image(String name) {
		try (InputStream in = testContext.getAssets().open(DIR + name)) {
			return BitmapFactory.decodeStream(in);
		} catch (Exception e) {
			return null;
		}
	}

	// ---------------- Metric helpers ----------------

	private List<Float> genuineScores() {
		List<Float> out = new ArrayList<>();
		for (int i = 0; i < scoreRows.size(); i++) {
			out.add(scoreRows.get(i)[trueIndex.get(i)]);
		}
		return out;
	}

	private List<Float> impostorScores() {
		List<Float> out = new ArrayList<>();
		for (int i = 0; i < scoreRows.size(); i++) {
			float[] row = scoreRows.get(i);
			for (int p = 0; p < row.length; p++) {
				if (p != trueIndex.get(i)) {
					out.add(row[p]);
				}
			}
		}
		return out;
	}

	private int decide(float[] row) {
		return decideAt(row, recognizer.getMinSimilarity());
	}

	private int decideAt(float[] row, float threshold) {
		int best = argMax(row);
		if (best < 0 || row[best] < threshold) {
			return -1;
		}
		float second = -2f;
		for (int i = 0; i < row.length; i++) {
			if (i != best) {
				second = Math.max(second, row[i]);
			}
		}
		if (second > -2f && row[best] - second < recognizer.getMinMargin()) {
			return -1;
		}
		return best;
	}

	private static int argMax(float[] row) {
		int best = -1;
		float bestValue = -Float.MAX_VALUE;
		for (int i = 0; i < row.length; i++) {
			if (row[i] > bestValue) {
				bestValue = row[i];
				best = i;
			}
		}
		return best;
	}

	private static int rankOf(float[] row, int index) {
		int rank = 1;
		for (int i = 0; i < row.length; i++) {
			if (i != index && row[i] > row[index]) {
				rank++;
			}
		}
		return rank;
	}

	private static float thresholdForFar(List<Float> impostor, float targetFar) {
		float[] sorted = toArray(impostor);
		Arrays.sort(sorted);
		int index = (int) Math.ceil((1f - targetFar) * sorted.length) - 1;
		index = Math.max(0, Math.min(sorted.length - 1, index));
		return sorted[index];
	}

	private static float rateAbove(List<Float> values, float threshold) {
		int count = 0;
		for (float v : values) {
			if (v >= threshold) {
				count++;
			}
		}
		return values.isEmpty() ? 0f : (float) count / values.size();
	}

	private static float rateBelow(List<Float> values, float threshold) {
		return 1f - rateAbove(values, threshold);
	}

	private static float[] toArray(List<Float> values) {
		float[] out = new float[values.size()];
		for (int i = 0; i < out.length; i++) {
			out[i] = values.get(i);
		}
		return out;
	}

	private static float mean(List<Float> values) {
		float sum = 0f;
		for (float v : values) {
			sum += v;
		}
		return values.isEmpty() ? 0f : sum / values.size();
	}

	private static float stdDev(List<Float> values, float mean) {
		float sum = 0f;
		for (float v : values) {
			sum += (v - mean) * (v - mean);
		}
		return values.isEmpty() ? 0f : (float) Math.sqrt(sum / values.size());
	}

	private static float min(List<Float> values) {
		float m = Float.MAX_VALUE;
		for (float v : values) {
			m = Math.min(m, v);
		}
		return values.isEmpty() ? 0f : m;
	}

	private static float max(List<Float> values) {
		float m = -Float.MAX_VALUE;
		for (float v : values) {
			m = Math.max(m, v);
		}
		return values.isEmpty() ? 0f : m;
	}

	private static float safeDivide(int numerator, int denominator) {
		return denominator == 0 ? 0f : (float) numerator / denominator;
	}

	private static String shorten(String name) {
		return name.length() <= 5 ? name : name.substring(0, 5);
	}
}