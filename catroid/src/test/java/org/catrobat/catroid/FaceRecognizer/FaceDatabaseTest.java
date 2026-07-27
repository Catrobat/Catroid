package org.catrobat.catroid.FaceRecognizer;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.catrobat.catroid.FaceRecognizer.env.FileUtils;
import org.catrobat.catroid.FaceRecognizer.ml.FaceNet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Core logic tests. No camera, no TensorFlow, no dialogs.
 *
 * These cover the parts that actually broke during development: index alignment
 * between the three files after a delete, the label and data files drifting apart,
 * and the threshold rules.
 */
@RunWith(RobolectricTestRunner.class)
public class FaceDatabaseTest {

	private static final int DIM = FaceNet.EMBEDDING_SIZE;

	private FaceDatabase database;

	@Before
	public void setUp() {
		Context context = ApplicationProvider.getApplicationContext();
		FileUtils.init(context);
		FileUtils.deleteAll();

		// Static, so it leaks between tests unless reset.
		FaceDatabase.minSimilarity = 0.50f;
		FaceDatabase.minMargin = 0.05f;

		database = new FaceDatabase();
		database.load();
	}

	// ---------------- People ----------------

	@Test
	public void emptyDatabaseHasNobody() {
		assertEquals(0, database.getPersonCount());
		assertTrue(database.getNames().isEmpty());
	}

	@Test
	public void addPersonReturnsSequentialIndexes() {
		assertEquals(0, database.addPerson("salah"));
		assertEquals(1, database.addPerson("karim"));
		assertEquals(2, database.addPerson("rahim"));
		assertEquals(3, database.getPersonCount());
	}

	@Test
	public void indexOfIsCaseInsensitive() {
		database.addPerson("Salah");
		assertEquals(0, database.indexOf("salah"));
		assertEquals(0, database.indexOf("SALAH"));
		assertEquals(-1, database.indexOf("karim"));
	}

	@Test
	public void namesListIsACopyAndCannotCorruptTheDatabase() {
		database.addPerson("salah");
		List<String> names = database.getNames();
		names.clear();
		assertEquals(1, database.getPersonCount());
	}

	// ---------------- Files ----------------

	@Test
	public void saveWritesAllThreeFiles() {
		database.addPerson("salah");
		database.addEmbeddings(0, Arrays.asList(unitVector(0), unitVector(1)));
		assertTrue(database.save());

		assertTrue(FileUtils.file(FileUtils.LABEL_FILE).length() > 0);
		assertTrue(FileUtils.file(FileUtils.DATA_FILE).length() > 0);
		assertTrue(FileUtils.file(FileUtils.MODEL_FILE).length() > 0);
	}

	@Test
	public void labelFileHasOneLinePerPerson() {
		database.addPerson("salah");
		database.addPerson("karim");
		database.save();

		List<String> lines = FileUtils.readLines(FileUtils.LABEL_FILE);
		assertEquals(Arrays.asList("salah", "karim"), lines);
	}

	@Test
	public void dataFileHasOneLinePerEmbedding() {
		database.addPerson("salah");
		database.addEmbeddings(0, Arrays.asList(unitVector(0), unitVector(1), unitVector(2)));
		database.save();

		assertEquals(3, FileUtils.readLines(FileUtils.DATA_FILE).size());
	}

	@Test
	public void saveThenLoadRestoresEverything() {
		database.addPerson("salah");
		database.addPerson("karim");
		database.addEmbeddings(0, Arrays.asList(unitVector(0), unitVector(1)));
		database.addEmbeddings(1, Arrays.asList(unitVector(2)));
		database.save();

		FaceDatabase reloaded = new FaceDatabase();
		reloaded.load();

		assertEquals(Arrays.asList("salah", "karim"), reloaded.getNames());
		assertEquals(2, reloaded.getEmbeddingCount(0));
		assertEquals(1, reloaded.getEmbeddingCount(1));
	}

	@Test
	public void reloadedEmbeddingIsIdenticalToTheStoredOne() {
		database.addPerson("salah");
		float[] original = unitVector(7);
		database.addEmbeddings(0, Arrays.asList(original));
		database.save();

		FaceDatabase reloaded = new FaceDatabase();
		reloaded.load();
		float[] restored = reloaded.getFirstEmbedding(0);

		assertNotNull(restored);
		// A vector matched against itself must be 1.0. Anything less means the
		// text round trip is losing precision.
		assertEquals(1.0f, dot(original, restored), 0.0005f);
	}

	@Test
	public void corruptDataLinesAreSkippedNotFatal() {
		database.addPerson("salah");
		database.addEmbeddings(0, Arrays.asList(unitVector(0)));
		database.save();

		List<String> lines = new ArrayList<>(FileUtils.readLines(FileUtils.DATA_FILE));
		lines.add("0 not a number at all");
		lines.add("garbage");
		lines.add("");
		FileUtils.writeLines(FileUtils.DATA_FILE, lines);

		FaceDatabase reloaded = new FaceDatabase();
		reloaded.load();
		assertEquals(1, reloaded.getEmbeddingCount(0));
	}

	@Test
	public void embeddingsPointingAtAMissingPersonAreDropped() {
		database.addPerson("salah");
		database.addEmbeddings(0, Arrays.asList(unitVector(0)));
		database.save();

		List<String> lines = new ArrayList<>(FileUtils.readLines(FileUtils.DATA_FILE));
		lines.add(lines.get(0).replaceFirst("^0 ", "9 "));
		FileUtils.writeLines(FileUtils.DATA_FILE, lines);

		FaceDatabase reloaded = new FaceDatabase();
		reloaded.load();
		assertEquals(1, reloaded.getEmbeddingCount(0));
		assertEquals(1, reloaded.getPersonCount());
	}

	// ---------------- Delete ----------------

	@Test
	public void deletePersonRemovesTheirNameAndPhotos() {
		database.addPerson("salah");
		database.addEmbeddings(0, Arrays.asList(unitVector(0), unitVector(1)));
		database.deletePerson(0);
		database.save();

		assertEquals(0, database.getPersonCount());
		assertEquals(0, FileUtils.readLines(FileUtils.LABEL_FILE).size());
		assertEquals(0, FileUtils.readLines(FileUtils.DATA_FILE).size());
	}

	/** This is the bug that made detection return the wrong person's name. */
	@Test
	public void deletingTheMiddlePersonKeepsTheRestAligned() {
		database.addPerson("a");
		database.addPerson("b");
		database.addPerson("c");
		database.addEmbeddings(0, Arrays.asList(unitVector(0)));
		database.addEmbeddings(1, Arrays.asList(unitVector(1)));
		database.addEmbeddings(2, Arrays.asList(unitVector(2)));
		database.save();

		database.deletePerson(1);
		database.save();

		FaceDatabase reloaded = new FaceDatabase();
		reloaded.load();

		assertEquals(Arrays.asList("a", "c"), reloaded.getNames());
		assertEquals(1, reloaded.getEmbeddingCount(0));
		assertEquals(1, reloaded.getEmbeddingCount(1));

		// "a" must still answer to vector 0 and "c" to vector 2.
		FaceDatabase.Match matchA = reloaded.match(unitVector(0));
		FaceDatabase.Match matchC = reloaded.match(unitVector(2));
		assertNotNull(matchA);
		assertNotNull(matchC);
		assertEquals("a", matchA.name);
		assertEquals("c", matchC.name);
	}

	@Test
	public void deletingEveryoneLeavesACleanDatabase() {
		database.addPerson("a");
		database.addPerson("b");
		database.addEmbeddings(0, Arrays.asList(unitVector(0)));
		database.addEmbeddings(1, Arrays.asList(unitVector(1)));
		database.save();

		database.deletePerson(1);
		database.deletePerson(0);
		database.save();

		FaceDatabase reloaded = new FaceDatabase();
		reloaded.load();
		assertEquals(0, reloaded.getPersonCount());
		assertNull(reloaded.match(unitVector(0)));
	}

	@Test
	public void deletingAnInvalidIndexDoesNothing() {
		database.addPerson("salah");
		database.deletePerson(5);
		database.deletePerson(-1);
		assertEquals(1, database.getPersonCount());
	}

	// ---------------- Matching ----------------

	@Test
	public void identicalEmbeddingMatchesWithScoreOne() {
		database.addPerson("salah");
		float[] v = unitVector(3);
		database.addEmbeddings(0, Arrays.asList(v));

		FaceDatabase.Match match = database.match(v);
		assertNotNull(match);
		assertEquals("salah", match.name);
		assertEquals(1.0f, match.similarity, 0.001f);
	}

	@Test
	public void scoreBelowThresholdIsRejected() {
		database.addPerson("salah");
		database.addEmbeddings(0, Arrays.asList(unitVector(0)));

		// 0.30 similarity, under the 0.50 default.
		assertNull(database.match(rotated(0, 1, 0.30f)));
	}

	@Test
	public void scoreAboveThresholdIsAccepted() {
		database.addPerson("salah");
		database.addEmbeddings(0, Arrays.asList(unitVector(0)));

		FaceDatabase.Match match = database.match(rotated(0, 1, 0.80f));
		assertNotNull(match);
		assertEquals("salah", match.name);
	}

	@Test
	public void twoPeopleTooCloseTogetherAreRejectedOnMargin() {
		database.addPerson("a");
		database.addPerson("b");
		// Both stored vectors are the same, so no query can separate them.
		float[] shared = unitVector(0);
		database.addEmbeddings(0, Arrays.asList(shared));
		database.addEmbeddings(1, Arrays.asList(shared));

		assertNull(database.match(shared));
	}

	@Test
	public void nearestPhotoWinsNotTheAverage() {
		// One close photo and several distant ones. Nearest neighbour must accept.
		database.addPerson("salah");
		database.addEmbeddings(0, Arrays.asList(
				unitVector(0),
				rotated(0, 1, 0.10f),
				rotated(0, 2, 0.10f),
				rotated(0, 3, 0.10f)));

		FaceDatabase.Match match = database.match(unitVector(0));
		assertNotNull(match);
		assertEquals(1.0f, match.similarity, 0.001f);
	}

	@Test
	public void matchOnAnEmptyDatabaseIsNull() {
		assertNull(database.match(unitVector(0)));
	}

	@Test
	public void matchWithAWrongSizedVectorIsNullNotACrash() {
		database.addPerson("salah");
		database.addEmbeddings(0, Arrays.asList(unitVector(0)));
		assertNull(database.match(new float[8]));
		assertNull(database.match(null));
	}

	@Test
	public void scoreAllVariantsTakesTheBestOfEachVariant() {
		database.addPerson("a");
		database.addPerson("b");
		database.addEmbeddings(0, Arrays.asList(unitVector(0)));
		database.addEmbeddings(1, Arrays.asList(unitVector(1)));

		List<float[]> variants = Arrays.asList(unitVector(0), unitVector(1));
		float[] scores = database.scoreAllVariants(variants);

		assertNotNull(scores);
		assertEquals(2, scores.length);
		assertEquals(1.0f, scores[0], 0.001f);
		assertEquals(1.0f, scores[1], 0.001f);
	}

	@Test
	public void decideUsesTheCombinedScores() {
		database.addPerson("a");
		database.addPerson("b");
		database.addEmbeddings(0, Arrays.asList(unitVector(0)));
		database.addEmbeddings(1, Arrays.asList(unitVector(1)));

		FaceDatabase.Match match = database.decide(new float[]{0.90f, 0.40f});
		assertNotNull(match);
		assertEquals("a", match.name);
		assertEquals(0.50f, match.margin, 0.001f);
	}

	// ---------------- Photo quality helpers ----------------

	@Test
	public void internalConsistencyNeedsAtLeastTwoPhotos() {
		database.addPerson("salah");
		database.addEmbeddings(0, Arrays.asList(unitVector(0)));
		assertTrue(database.internalConsistency(0) < -1f);
	}

	@Test
	public void internalConsistencyOfIdenticalPhotosIsOne() {
		database.addPerson("salah");
		float[] v = unitVector(0);
		database.addEmbeddings(0, Arrays.asList(v, v.clone(), v.clone()));
		assertEquals(1.0f, database.internalConsistency(0), 0.001f);
	}

	@Test
	public void photoAffinitiesFindsTheStrayPhoto() {
		database.addPerson("salah");
		database.addEmbeddings(0, Arrays.asList(
				unitVector(0),
				rotated(0, 1, 0.90f),
				unitVector(5)));

		float[] affinities = database.photoAffinities(0);
		assertNotNull(affinities);
		assertEquals(3, affinities.length);
		assertTrue(affinities[0] > 0.85f);
		assertTrue(affinities[1] > 0.85f);
		assertTrue("stray photo should resemble nothing", affinities[2] < 0.35f);
	}

	@Test
	public void removeStrayPhotosDropsOnlyTheStray() {
		database.addPerson("salah");
		database.addEmbeddings(0, Arrays.asList(
				unitVector(0),
				rotated(0, 1, 0.90f),
				rotated(0, 2, 0.90f),
				unitVector(5)));

		assertEquals(1, database.removeStrayPhotos(0, 0.35f));
		assertEquals(3, database.getEmbeddingCount(0));
	}

	@Test
	public void removeStrayPhotosSkipsPeopleWithTooFewPhotos() {
		database.addPerson("salah");
		database.addEmbeddings(0, Arrays.asList(unitVector(0), unitVector(5)));
		assertEquals(0, database.removeStrayPhotos(0, 0.35f));
		assertEquals(2, database.getEmbeddingCount(0));
	}

	// ---------------- Thresholds ----------------

	@Test
	public void thresholdsSurviveAReload() {
		database.addPerson("salah");
		database.addEmbeddings(0, Arrays.asList(unitVector(0)));
		database.setThresholds(0.33f, 0.02f);

		FaceDatabase reloaded = new FaceDatabase();
		reloaded.load();

		assertEquals(0.33f, FaceDatabase.minSimilarity, 0.001f);
		assertEquals(0.02f, FaceDatabase.minMargin, 0.001f);
	}

	@Test
	public void thresholdsAreClampedToSaneValues() {
		database.setThresholds(9f, 9f);
		assertTrue(FaceDatabase.minSimilarity <= 0.95f);
		assertTrue(FaceDatabase.minMargin <= 0.5f);

		database.setThresholds(-1f, -1f);
		assertTrue(FaceDatabase.minSimilarity >= 0.05f);
		assertTrue(FaceDatabase.minMargin >= 0f);
	}

	@Test
	public void loweringTheThresholdAcceptsAPreviouslyRejectedFace() {
		database.addPerson("salah");
		database.addEmbeddings(0, Arrays.asList(unitVector(0)));
		float[] query = rotated(0, 1, 0.40f);

		assertNull(database.match(query));
		database.setThresholds(0.30f, 0.01f);
		assertNotNull(database.match(query));
	}

	// ---------------- Helpers ----------------

	/** Unit vector along one axis. Two different axes have similarity 0. */
	private static float[] unitVector(int axis) {
		float[] v = new float[DIM];
		v[axis % DIM] = 1f;
		return v;
	}

	/** Unit vector whose cosine similarity with unitVector(axisA) is exactly target. */
	private static float[] rotated(int axisA, int axisB, float target) {
		float[] v = new float[DIM];
		v[axisA % DIM] = target;
		v[axisB % DIM] = (float) Math.sqrt(1f - target * target);
		return v;
	}

	private static float dot(float[] a, float[] b) {
		float sum = 0f;
		for (int i = 0; i < a.length; i++) {
			sum += a[i] * b[i];
		}
		return sum;
	}

	@Test
	public void helperVectorsHaveTheSimilarityTheyClaim() {
		assertEquals(1.0f, dot(unitVector(0), unitVector(0)), 0.0001f);
		assertEquals(0.0f, dot(unitVector(0), unitVector(1)), 0.0001f);
		assertEquals(0.7f, dot(unitVector(0), rotated(0, 1, 0.7f)), 0.0001f);
		assertFalse(Float.isNaN(dot(unitVector(0), rotated(0, 1, 1.0f))));
	}
}
