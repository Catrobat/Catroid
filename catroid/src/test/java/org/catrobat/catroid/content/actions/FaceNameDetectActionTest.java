package org.catrobat.catroid.content.actions;

import org.catrobat.catroid.FaceRecognizer.FaceDetector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * The detect brick's action.
 *
 * There is no camera in a unit test, so every detection is refused at the
 * permission check and the callback fires immediately. That makes the state
 * machine fully deterministic here, which is the part worth testing.
 *
 * The case that matters most is secondCallStartsAFreshDetection. The Restart
 * button re-runs the script, and if the action remembers that it already
 * finished, it returns true straight away and the camera never opens. That is
 * the bug this suite exists to catch.
 */
@RunWith(RobolectricTestRunner.class)
public class FaceNameDetectActionTest {

	private FaceNameDetectAction action;

	@Before
	public void setUp() {
		FaceDetector.resetForNewRun();
		action = new FaceNameDetectAction();
	}

	@After
	public void tearDown() {
		FaceDetector.resetForNewRun();
	}

	// ---------------- One pass ----------------

	@Test
	public void theActionFinishes() {
		// With no camera the detection resolves at once, so act returns true.
		assertTrue(action.act(0.016f));
	}

	@Test
	public void aFinishedActionKeepsReturningTrueWithinTheSamePass() {
		assertTrue(action.act(0.016f));
		// The sequence has moved on, but a stray call must not restart anything
		// halfway through. It reports done, then the next call is a new pass.
		assertTrue(action.act(0.016f));
	}

	@Test
	public void theActionAlwaysHasAName() {
		action.act(0.016f);
		assertNotNull(action.getDetectedName());
		assertFalse(action.getDetectedName().isEmpty());
	}

	@Test
	public void withNoCameraTheNameIsUnknownRatherThanNull() {
		action.act(0.016f);
		assertEquals(FaceDetector.UNKNOWN, action.getDetectedName());
	}

	// ---------------- Restart, the bug ----------------

	/**
	 * Press Restart, the script runs again, the brick is reached again. The
	 * camera must open again.
	 *
	 * Proof that begin() ran a second time: it calls resetForNewRun, which wipes
	 * the stored name. If the action had simply returned true without doing any
	 * work, the planted name would still be there.
	 */
	@Test
	public void secondCallStartsAFreshDetection() {
		assertTrue(action.act(0.016f));

		// Pretend a real detection had stored a name.
		setDetectorName("salah");
		setDetectorDone(true);

		assertTrue("the brick must complete again", action.act(0.016f));

		assertEquals("the second pass did not run a fresh detection",
				FaceDetector.UNKNOWN, FaceDetector.getLastName());
	}

	@Test
	public void manyPassesEachRunAFreshDetection() {
		for (int pass = 1; pass <= 5; pass++) {
			setDetectorName("stale" + pass);
			setDetectorDone(true);

			assertTrue("pass " + pass + " did not complete", action.act(0.016f));
			assertEquals("pass " + pass + " reused the old name",
					FaceDetector.UNKNOWN, FaceDetector.getLastName());
		}
	}

	@Test
	public void restartClearsTheAction() {
		action.act(0.016f);
		action.restart();
		assertEquals(FaceDetector.UNKNOWN, action.getDetectedName());
		assertTrue(action.act(0.016f));
	}

	@Test
	public void resetClearsTheAction() {
		action.act(0.016f);
		action.reset();
		assertEquals(FaceDetector.UNKNOWN, action.getDetectedName());
		assertTrue(action.act(0.016f));
	}

	@Test
	public void aReusedActionObjectBehavesLikeAFreshOne() {
		// Catroid may reuse the action object rather than building a new one.
		action.act(0.016f);
		action.act(0.016f);
		action.restart();

		setDetectorName("salah");
		setDetectorDone(true);

		assertTrue(action.act(0.016f));
		assertEquals(FaceDetector.UNKNOWN, FaceDetector.getLastName());
	}

	// ---------------- Independence ----------------

	@Test
	public void twoActionsDoNotShareState() {
		FaceNameDetectAction other = new FaceNameDetectAction();

		assertTrue(action.act(0.016f));
		assertTrue(other.act(0.016f));

		assertNotNull(action.getDetectedName());
		assertNotNull(other.getDetectedName());
	}

	@Test
	public void theActionNeverThrows() {
		for (int i = 0; i < 50; i++) {
			action.act(0.016f);
		}
		action.restart();
		action.reset();
		action.act(0f);
		action.act(-1f);
		action.act(1000f);
	}

	// ---------------- Helpers ----------------

	private static void setDetectorName(String name) {
		set("lastName", name);
	}

	private static void setDetectorDone(boolean done) {
		try {
			Field f = FaceDetector.class.getDeclaredField("detectionDone");
			f.setAccessible(true);
			f.setBoolean(null, done);
		} catch (Exception e) {
			throw new AssertionError(e);
		}
	}

	private static void set(String name, Object value) {
		try {
			Field f = FaceDetector.class.getDeclaredField(name);
			f.setAccessible(true);
			f.set(null, value);
		} catch (Exception e) {
			throw new AssertionError(e);
		}
	}
}
