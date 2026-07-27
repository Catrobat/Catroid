package org.catrobat.catroid.FaceRecognizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.catrobat.catroid.FaceRecognizer.env.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Runs the real pipeline on a device: BlazeFace, the landmarks, the alignment and
 * FaceNet. Needs the two tflite models in the app assets, plus test faces in the
 * androidTest assets folder.
 *
 * Put these in app/src/androidTest/assets/ :
 *   face_a_1.jpg, face_a_2.jpg   two photos of the same person
 *   face_b_1.jpg                 a photo of a different person
 *   no_face.jpg                  anything without a face, a landscape will do
 *
 * Without those files the tests that need them are skipped rather than failed,
 * so the suite still runs on a machine that does not have them.
 */
@RunWith(AndroidJUnit4.class)
public class RecognizerInstrumentedTest {

	private Context appContext;
	private Context testContext;
	private Recognizer recognizer;

	@Before
	public void setUp() throws Exception {
		appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
		testContext = InstrumentationRegistry.getInstrumentation().getContext();

		FileUtils.init(appContext);
		FileUtils.deleteAll();

		Recognizer.release();
		recognizer = Recognizer.getInstance(appContext);
	}

	@After
	public void tearDown() {
		FileUtils.deleteAll();
		Recognizer.release();
	}

	// ---------------- Setup ----------------

	@Test
	public void recognizerStartsWithNobodyTrained() {
		assertTrue(recognizer.getClassNames().isEmpty());
	}

	@Test
	public void statusNamesTheFolderItIsUsing() {
		String status = recognizer.getStatus();
		assertNotNull(status);
		assertTrue(status.contains("facerecog"));
	}

	@Test
	public void addPersonIsVisibleImmediately() {
		int index = recognizer.addPerson("salah");
		assertEquals(0, index);
		assertEquals(1, recognizer.getClassNames().size());
		assertEquals("salah", recognizer.getClassNames().get(0));
	}

	@Test
	public void addingTheSameNameTwiceReusesTheSamePerson() {
		assertEquals(0, recognizer.addPerson("salah"));
		assertEquals(0, recognizer.addPerson("salah"));
		assertEquals(1, recognizer.getClassNames().size());
	}

	// ---------------- Embedding ----------------

	@Test
	public void aRealFaceProducesEmbeddings() {
		Bitmap face = asset("face_a_1.jpg");
		if (face == null) {
			return;
		}
		List<float[]> variants = recognizer.embedFrame(face);
		face.recycle();

		assertFalse("a clear face must produce at least one embedding", variants.isEmpty());
		for (float[] v : variants) {
			assertEquals(512, v.length);
			assertEquals("embeddings must be unit length", 1.0f, norm(v), 0.01f);
		}
	}

	@Test
	public void augmentationProducesSeveralViewsOfOneFace() {
		Bitmap face = asset("face_a_1.jpg");
		if (face == null) {
			return;
		}
		List<float[]> variants = recognizer.embedFrame(face);
		face.recycle();

		// Three crop tightnesses, each mirrored.
		assertTrue("expected multiple views, got " + variants.size(), variants.size() >= 4);
	}

	@Test
	public void anImageWithoutAFaceProducesNothing() {
		Bitmap landscape = asset("no_face.jpg");
		if (landscape == null) {
			return;
		}
		List<float[]> variants = recognizer.embedFrame(landscape);
		landscape.recycle();
		assertTrue(variants.isEmpty());
	}

	@Test
	public void aNullFrameIsHandledSafely() {
		assertTrue(recognizer.embedFrame(null).isEmpty());
	}

	// ---------------- Matching ----------------

	@Test
	public void aFaceMatchesItself() {
		Bitmap face = asset("face_a_1.jpg");
		if (face == null) {
			return;
		}
		recognizer.addEmbeddings(recognizer.addPerson("salah"), recognizer.embedFrame(face));

		Recognizer.Session session = recognizer.newSession();
		assertTrue(recognizer.addFrame(session, face, false));
		Recognizer.Result result = recognizer.finishSession(session);
		face.recycle();

		assertNotNull("the same image must match itself", result);
		assertEquals("salah", result.name);
		assertTrue("self match should be very high, was " + result.confidence,
				result.confidence > 0.9f);
	}

	@Test
	public void twoPhotosOfTheSamePersonMatch() {
		Bitmap first = asset("face_a_1.jpg");
		Bitmap second = asset("face_a_2.jpg");
		if (first == null || second == null) {
			return;
		}
		recognizer.addEmbeddings(recognizer.addPerson("salah"), recognizer.embedFrame(first));

		Recognizer.Session session = recognizer.newSession();
		recognizer.addFrame(session, second, false);
		Recognizer.Result result = recognizer.finishSession(session);

		first.recycle();
		second.recycle();

		assertNotNull("two photos of one person should match", result);
		assertEquals("salah", result.name);
	}

	@Test
	public void aDifferentPersonIsNotAccepted() {
		Bitmap known = asset("face_a_1.jpg");
		Bitmap stranger = asset("face_b_1.jpg");
		if (known == null || stranger == null) {
			return;
		}
		recognizer.addEmbeddings(recognizer.addPerson("salah"), recognizer.embedFrame(known));
		recognizer.setThresholds(0.60f, 0.05f);

		Recognizer.Session session = recognizer.newSession();
		recognizer.addFrame(session, stranger, false);
		Recognizer.Result result = recognizer.finishSession(session);

		known.recycle();
		stranger.recycle();

		assertNull("a stranger must not be given a name at a strict threshold", result);
	}

	@Test
	public void recognitionWithNobodyTrainedReturnsNothing() {
		Bitmap face = asset("face_a_1.jpg");
		if (face == null) {
			return;
		}
		Recognizer.Session session = recognizer.newSession();
		recognizer.addFrame(session, face, false);
		assertNull(recognizer.finishSession(session));
		face.recycle();
	}

	@Test
	public void aSessionWithNoUsableFrameReturnsNothing() {
		recognizer.addPerson("salah");
		Recognizer.Session session = recognizer.newSession();
		assertNull(recognizer.finishSession(session));
		assertEquals(0, session.getFramesWithFace());
	}

	@Test
	public void severalFramesAreAveraged() {
		Bitmap face = asset("face_a_1.jpg");
		if (face == null) {
			return;
		}
		recognizer.addEmbeddings(recognizer.addPerson("salah"), recognizer.embedFrame(face));

		Recognizer.Session session = recognizer.newSession();
		recognizer.addFrame(session, face, false);
		recognizer.addFrame(session, face, false);
		recognizer.addFrame(session, face, false);

		assertEquals(3, session.getFramesWithFace());
		assertNotNull(recognizer.finishSession(session));
		face.recycle();
	}

	// ---------------- Persistence ----------------

	@Test
	public void trainingSurvivesARestart() {
		Bitmap face = asset("face_a_1.jpg");
		if (face == null) {
			return;
		}
		recognizer.addEmbeddings(recognizer.addPerson("salah"), recognizer.embedFrame(face));
		int photos = recognizer.getPhotoCount(0);
		assertTrue(photos > 0);

		// Simulate the app being killed and started again.
		Recognizer.release();
		Recognizer restarted;
		try {
			restarted = Recognizer.getInstance(appContext);
		} catch (Exception e) {
			throw new AssertionError(e);
		}

		assertEquals(1, restarted.getClassNames().size());
		assertEquals("salah", restarted.getClassNames().get(0));
		assertEquals(photos, restarted.getPhotoCount(0));
		face.recycle();
	}

	@Test
	public void deletingAPersonAlsoRemovesTheirPhotos() {
		Bitmap face = asset("face_a_1.jpg");
		if (face == null) {
			return;
		}
		recognizer.addEmbeddings(recognizer.addPerson("salah"), recognizer.embedFrame(face));
		recognizer.deletePerson(0);

		assertTrue(recognizer.getClassNames().isEmpty());
		assertEquals(0, recognizer.getPhotoCount(0));

		Recognizer.Session session = recognizer.newSession();
		recognizer.addFrame(session, face, false);
		assertNull("a deleted person must never be returned",
				recognizer.finishSession(session));
		face.recycle();
	}

	@Test
	public void selfTestReportsHealthyEmbeddings() {
		Bitmap first = asset("face_a_1.jpg");
		Bitmap second = asset("face_a_2.jpg");
		if (first == null || second == null) {
			return;
		}
		int index = recognizer.addPerson("salah");
		recognizer.addEmbeddings(index, recognizer.embedFrame(first));
		recognizer.addEmbeddings(index, recognizer.embedFrame(second));

		String report = recognizer.selfTest();
		first.recycle();
		second.recycle();

		assertNotNull(report);
		assertTrue(report.contains("salah"));
		assertFalse("two photos of one person must not report BROKEN",
				report.contains("BROKEN"));
	}

	// ---------------- Helpers ----------------

	/** Loads a test asset, or returns null so the test can skip itself. */
	private Bitmap asset(String name) {
		try (InputStream in = testContext.getAssets().open(name)) {
			return BitmapFactory.decodeStream(in);
		} catch (Exception e) {
			return null;
		}
	}

	private static float norm(float[] v) {
		double sum = 0;
		for (float f : v) {
			sum += f * f;
		}
		return (float) Math.sqrt(sum);
	}
}
