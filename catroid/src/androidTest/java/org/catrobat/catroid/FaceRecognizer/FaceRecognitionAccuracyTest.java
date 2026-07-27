package org.catrobat.catroid.FaceRecognizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Accuracy across a set of real faces, reported per demographic group.
 *
 * Four training images and one test image per person. Every test image is also
 * scored against a database that excludes its own person, which is what happens
 * when an unenrolled child stands in front of the camera.
 *
 * The point of the group breakdown is that an overall figure can hide a large
 * gap. Face embedding models are known to perform worse on darker skin tones and
 * on women, and a nearest neighbour matcher inherits that. An overall 85% that
 * is 67% for one group is not an 85% system for those children.
 *
 * Needs catroid/src/androidTest/assets/faces/ with manifest.csv and the images.
 * See TEST_DATASET.md. People whose images are missing are skipped, so the suite
 * runs with five people or twenty.
 */
@RunWith(AndroidJUnit4.class)
public class FaceRecognitionAccuracyTest {

	private static final String TAG = "FaceAccuracy";
	private static final String DIR = "faces/";
	private static final int TRAIN_IMAGES = 4;

	/** Overall correct rate below this fails the build. */
	private static final float MIN_OVERALL_ACCURACY = 0.70f;

	/** No group may fall more than this far below the overall rate. */
	private static final float MAX_GROUP_GAP = 0.25f;

	/** Wrong name returned. Worse than Unknown, so held to a tighter bound. */
	private static final float MAX_FALSE_ACCEPT_RATE = 0.05f;

	private Context appContext;
	private Context testContext;
	private Recognizer recognizer;

	private static final class Person {
		String id;
		String name;
		String age;
		String gender;
		String glasses;
		String beard;
		String skin;
	}

	private static final class Tally {
		int total;
		int correct;
		int falseReject;
		int falseAccept;

		float accuracy() {
			return total == 0 ? 0f : (float) correct / total;
		}

		@Override
		public String toString() {
			return String.format(Locale.US,
					"%2d people   correct %2d   reject %d   accept %d   %5.1f%%",
					total, correct, falseReject, falseAccept, accuracy() * 100f);
		}
	}

	@Before
	public void setUp() throws Exception {
		appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
		testContext = InstrumentationRegistry.getInstrumentation().getContext();

		FileUtils.init(appContext);
		FileUtils.deleteAll();
		Recognizer.release();
		recognizer = Recognizer.getInstance(appContext);

		FaceDatabase.minSimilarity = 0.70f;
		FaceDatabase.minMargin = 0.05f;
	}

	@After
	public void tearDown() {
		FileUtils.deleteAll();
		Recognizer.release();
	}

	// ---------------- Identification ----------------

	@Test
	public void everyoneIsRecognisedFromASeparatePhoto() {
		List<Person> people = loadManifest();
		Assume.assumeTrue("needs androidTest/assets/faces/manifest.csv", !people.isEmpty());

		List<Person> enrolled = enrolEveryone(people);
		Assume.assumeTrue("no usable training images", enrolled.size() >= 2);

		Tally overall = new Tally();
		Map<String, Tally> byAge = new LinkedHashMap<>();
		Map<String, Tally> byGender = new LinkedHashMap<>();
		Map<String, Tally> byGlasses = new LinkedHashMap<>();
		Map<String, Tally> byBeard = new LinkedHashMap<>();
		Map<String, Tally> bySkin = new LinkedHashMap<>();

		List<String> failures = new ArrayList<>();

		for (Person person : enrolled) {
			Bitmap test = image(person.id + "_test.jpg");
			if (test == null) {
				continue;
			}

			String got = recognise(test);
			test.recycle();

			int outcome;
			if (person.name.equals(got)) {
				outcome = 0;
			} else if ("Unknown".equals(got)) {
				outcome = 1;
				failures.add(person.name + " (" + person.skin + ", " + person.gender
						+ ") came back Unknown");
			} else {
				outcome = 2;
				failures.add(person.name + " (" + person.skin + ", " + person.gender
						+ ") came back as " + got);
			}

			record(overall, outcome);
			record(byAge.computeIfAbsent(person.age, k -> new Tally()), outcome);
			record(byGender.computeIfAbsent(person.gender, k -> new Tally()), outcome);
			record(byGlasses.computeIfAbsent("glasses " + person.glasses,
					k -> new Tally()), outcome);
			record(byBeard.computeIfAbsent("beard " + person.beard, k -> new Tally()), outcome);
			record(bySkin.computeIfAbsent(person.skin, k -> new Tally()), outcome);
		}

		report("OVERALL", overall, null);
		report("BY AGE", null, byAge);
		report("BY GENDER", null, byGender);
		report("BY GLASSES", null, byGlasses);
		report("BY BEARD", null, byBeard);
		report("BY SKIN", null, bySkin);

		if (!failures.isEmpty()) {
			Log.w(TAG, "Failures:");
			for (String failure : failures) {
				Log.w(TAG, "  " + failure);
			}
		}

		float falseAcceptRate = (float) overall.falseAccept / overall.total;
		assertTrue(String.format(Locale.US,
				"wrong name returned for %.1f%% of people, limit %.1f%%. Being told you "
						+ "are somebody else is worse than being told nothing.",
				falseAcceptRate * 100f, MAX_FALSE_ACCEPT_RATE * 100f),
				falseAcceptRate <= MAX_FALSE_ACCEPT_RATE);

		assertTrue(String.format(Locale.US, "overall accuracy %.1f%%, minimum %.1f%%",
				overall.accuracy() * 100f, MIN_OVERALL_ACCURACY * 100f),
				overall.accuracy() >= MIN_OVERALL_ACCURACY);

		checkGroupGap("skin tone", bySkin, overall);
		checkGroupGap("gender", byGender, overall);
		checkGroupGap("age", byAge, overall);
	}

	// ---------------- Strangers ----------------

	@Test
	public void anUnenrolledPersonIsNotGivenSomebodyElsesName() {
		List<Person> people = loadManifest();
		Assume.assumeTrue("needs the faces dataset", people.size() >= 3);

		int tested = 0;
		int wrongly = 0;
		List<String> failures = new ArrayList<>();

		for (Person absent : people) {
			Bitmap test = image(absent.id + "_test.jpg");
			if (test == null) {
				continue;
			}

			// Rebuild the database with everyone except this person.
			FileUtils.deleteAll();
			Recognizer.release();
			try {
				recognizer = Recognizer.getInstance(appContext);
			} catch (Exception e) {
				throw new AssertionError(e);
			}

			int enrolledCount = 0;
			for (Person other : people) {
				if (!other.id.equals(absent.id) && enrol(other)) {
					enrolledCount++;
				}
			}
			if (enrolledCount < 2) {
				test.recycle();
				continue;
			}

			String got = recognise(test);
			test.recycle();
			tested++;

			if (!"Unknown".equals(got)) {
				wrongly++;
				failures.add(absent.name + " is not enrolled but came back as " + got);
			}
		}

		Assume.assumeTrue("not enough people to test strangers", tested >= 3);

		float rate = (float) wrongly / tested;
		Log.i(TAG, String.format(Locale.US,
				"STRANGERS   %d tested, %d given a name, %.1f%%", tested, wrongly, rate * 100f));
		for (String failure : failures) {
			Log.w(TAG, "  " + failure);
		}

		assertTrue(String.format(Locale.US,
				"%.1f%% of strangers were given a name, limit %.1f%%. Raise "
						+ "FaceDatabase.minSimilarity if this fails.",
				rate * 100f, MAX_FALSE_ACCEPT_RATE * 100f),
				rate <= MAX_FALSE_ACCEPT_RATE);
	}

	// ---------------- Score distribution ----------------

	@Test
	public void reportTheScoreDistribution() {
		// Not an assertion. This prints the numbers you need to choose a
		// threshold, which no test can decide for you.
		List<Person> people = loadManifest();
		Assume.assumeTrue("needs the faces dataset", people.size() >= 2);

		List<Person> enrolled = enrolEveryone(people);
		Assume.assumeTrue(enrolled.size() >= 2);

		float lowestCorrect = 1f;
		float highestWrong = 0f;

		for (Person person : enrolled) {
			Bitmap test = image(person.id + "_test.jpg");
			if (test == null) {
				continue;
			}
			Recognizer.Session session = recognizer.newSession();
			recognizer.addFrame(session, test, false);
			Recognizer.Result result = recognizer.finishSession(session);
			test.recycle();

			if (result == null) {
				continue;
			}
			if (person.name.equals(result.name)) {
				lowestCorrect = Math.min(lowestCorrect, result.confidence);
			} else {
				highestWrong = Math.max(highestWrong, result.confidence);
			}
		}

		Log.i(TAG, String.format(Locale.US,
				"THRESHOLD   lowest correct %.3f, highest wrong %.3f, currently %.2f",
				lowestCorrect, highestWrong, FaceDatabase.minSimilarity));
		if (highestWrong < lowestCorrect) {
			Log.i(TAG, String.format(Locale.US,
					"A threshold anywhere between %.3f and %.3f separates them cleanly. "
							+ "Midpoint %.3f.",
					highestWrong, lowestCorrect, (highestWrong + lowestCorrect) / 2f));
		} else {
			Log.w(TAG, "Correct and wrong scores overlap. No threshold separates them, "
					+ "so better enrolment photos matter more than tuning.");
		}
	}

	// ---------------- Helpers ----------------

	private List<Person> loadManifest() {
		List<Person> people = new ArrayList<>();
		try (InputStream in = testContext.getAssets().open(DIR + "manifest.csv");
				BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			String line = reader.readLine();  // header
			while ((line = reader.readLine()) != null) {
				String[] parts = line.trim().split(",");
				if (parts.length < 7) {
					continue;
				}
				Person person = new Person();
				person.id = parts[0].trim();
				person.name = parts[1].trim();
				person.age = parts[2].trim();
				person.gender = parts[3].trim();
				person.glasses = parts[4].trim();
				person.beard = parts[5].trim();
				person.skin = parts[6].trim();
				people.add(person);
			}
		} catch (Exception e) {
			Log.w(TAG, "No manifest, skipping: " + e.getMessage());
		}
		return people;
	}

	private List<Person> enrolEveryone(List<Person> people) {
		List<Person> enrolled = new ArrayList<>();
		for (Person person : people) {
			if (enrol(person)) {
				enrolled.add(person);
			}
		}
		Log.i(TAG, "Enrolled " + enrolled.size() + " of " + people.size());
		return enrolled;
	}

	/** Adds the four training images. Returns false if none produced a face. */
	private boolean enrol(Person person) {
		List<float[]> all = new ArrayList<>();
		for (int i = 1; i <= TRAIN_IMAGES; i++) {
			Bitmap bitmap = image(person.id + "_train" + i + ".jpg");
			if (bitmap == null) {
				continue;
			}
			all.addAll(recognizer.embedFrame(bitmap));
			bitmap.recycle();
		}
		if (all.isEmpty()) {
			return false;
		}
		recognizer.addEmbeddings(recognizer.addPerson(person.name), all);
		return true;
	}

	private String recognise(Bitmap bitmap) {
		Recognizer.Session session = recognizer.newSession();
		recognizer.addFrame(session, bitmap, false);
		Recognizer.Result result = recognizer.finishSession(session);
		return result == null ? "Unknown" : result.name;
	}

	private Bitmap image(String name) {
		try (InputStream in = testContext.getAssets().open(DIR + name)) {
			return BitmapFactory.decodeStream(in);
		} catch (Exception e) {
			return null;
		}
	}

	/** 0 correct, 1 false reject, 2 false accept. */
	private static void record(Tally tally, int outcome) {
		tally.total++;
		if (outcome == 0) {
			tally.correct++;
		} else if (outcome == 1) {
			tally.falseReject++;
		} else {
			tally.falseAccept++;
		}
	}

	private static void report(String title, Tally single, Map<String, Tally> groups) {
		if (single != null) {
			Log.i(TAG, String.format(Locale.US, "%-16s %s", title, single));
			return;
		}
		Log.i(TAG, title);
		for (Map.Entry<String, Tally> entry : groups.entrySet()) {
			Log.i(TAG, String.format(Locale.US, "  %-14s %s", entry.getKey(), entry.getValue()));
		}
	}

	/**
	 * A group far below the overall rate is the finding that matters, so it fails
	 * rather than just printing. A group of one or two people is too small to
	 * judge and is skipped.
	 */
	private static void checkGroupGap(String label, Map<String, Tally> groups, Tally overall) {
		for (Map.Entry<String, Tally> entry : groups.entrySet()) {
			Tally tally = entry.getValue();
			if (tally.total < 3) {
				continue;
			}
			float gap = overall.accuracy() - tally.accuracy();
			assertTrue(String.format(Locale.US,
					"%s '%s' is %.1f%% accurate against %.1f%% overall, a gap of %.1f "
							+ "points. The system is not equally good for these users.",
					label, entry.getKey(), tally.accuracy() * 100f,
					overall.accuracy() * 100f, gap * 100f),
					gap <= MAX_GROUP_GAP);
		}
	}
}
