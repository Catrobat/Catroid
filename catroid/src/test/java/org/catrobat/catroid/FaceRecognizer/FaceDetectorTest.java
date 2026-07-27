package org.catrobat.catroid.FaceRecognizer;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * The gate in front of the camera.
 *
 * There is no camera in a unit test, so every request is refused at the
 * permission check. That is exactly what makes these deterministic: the callback
 * always fires immediately, so the contract can be checked precisely.
 *
 * The contract that matters: the callback fires exactly once, always, whatever
 * happens. Anything waiting on it must never be left waiting.
 */
@RunWith(RobolectricTestRunner.class)
public class FaceDetectorTest {

	private static final String F_RUNNING = "detectionRunning";
	private static final String F_DONE = "detectionDone";
	private static final String F_LAST_START = "lastDetectionStart";
	private static final String F_SCRIPT = "scriptRunning";
	private static final String F_LAST_NAME = "lastName";

	private Context context;

	@Before
	public void setUp() {
		context = ApplicationProvider.getApplicationContext();
		FaceDetector.resetForNewRun();
	}

	@After
	public void tearDown() {
		FaceDetector.resetForNewRun();
	}

	// ---------------- Guard ----------------

	@Test
	public void fieldNamesHaveNotChanged() {
		for (String name : new String[]{F_RUNNING, F_DONE, F_LAST_START, F_SCRIPT, F_LAST_NAME}) {
			try {
				FaceDetector.class.getDeclaredField(name);
			} catch (NoSuchFieldException e) {
				fail("FaceDetector no longer has a static field '" + name
						+ "'. Update the constants at the top of this test.");
			}
		}
	}

	// ---------------- Starting state ----------------

	@Test
	public void aFreshRunHasNoNameAndIsNotDone() {
		assertEquals(FaceDetector.UNKNOWN, FaceDetector.getLastName());
		assertEquals(0f, FaceDetector.getLastConfidence(), 0.001f);
		assertFalse(FaceDetector.isDetectionDone());
		assertFalse(FaceDetector.isRunning());
	}

	@Test
	public void resetClearsEverything() {
		setString(F_LAST_NAME, "salah");
		setBoolean(F_DONE, true);
		setBoolean(F_RUNNING, true);
		setLong(F_LAST_START, System.currentTimeMillis());

		FaceDetector.resetForNewRun();

		assertEquals(FaceDetector.UNKNOWN, FaceDetector.getLastName());
		assertFalse(FaceDetector.isDetectionDone());
		assertFalse(FaceDetector.isRunning());
	}

	@Test
	public void resetAlsoClearsAStuckRunningFlag() {
		// A capture interrupted by a teardown leaves this set. If reset did not
		// clear it, no later run would ever detect.
		setBoolean(F_RUNNING, true);
		FaceDetector.resetForNewRun();
		assertFalse(FaceDetector.isRunning());
	}

	// ---------------- The callback contract ----------------

	@Test
	public void theCallbackAlwaysFires() {
		AtomicInteger calls = new AtomicInteger();
		FaceDetector.requestDetection(context, (name, confidence) -> calls.incrementAndGet());
		assertEquals("a waiting script must never be left waiting", 1, calls.get());
	}

	@Test
	public void theCallbackFiresExactlyOnce() {
		AtomicInteger calls = new AtomicInteger();
		FaceDetector.requestDetection(context, (name, confidence) -> calls.incrementAndGet());
		assertEquals(1, calls.get());
	}

	@Test
	public void theCallbackNeverReceivesANullName() {
		AtomicReference<String> received = new AtomicReference<>();
		FaceDetector.requestDetection(context, (name, confidence) -> received.set(name));
		assertNotNull(received.get());
		assertFalse(received.get().isEmpty());
	}

	@Test
	public void aNullContextStillFiresTheCallback() {
		AtomicInteger calls = new AtomicInteger();
		FaceDetector.requestDetection(null, (name, confidence) -> calls.incrementAndGet());
		assertEquals(1, calls.get());
	}

	@Test
	public void aNullCallbackIsNotACrash() {
		FaceDetector.requestDetection(context, null);
	}

	// ---------------- The one per run latch ----------------

	@Test
	public void aFinishedDetectionIsNotRepeated() {
		FaceDetector.requestDetection(context, null);
		assertTrue(FaceDetector.isDetectionDone());

		AtomicInteger calls = new AtomicInteger();
		assertFalse(FaceDetector.requestDetection(context,
				(name, confidence) -> calls.incrementAndGet()));
		assertEquals("a refused request must still answer its caller", 1, calls.get());
	}

	@Test
	public void hammeringTheGateAfterAResultChangesNothing() {
		FaceDetector.requestDetection(context, null);
		String after = FaceDetector.getLastName();

		for (int i = 0; i < 200; i++) {
			FaceDetector.requestDetection(context, null);
		}
		assertEquals(after, FaceDetector.getLastName());
		assertTrue(FaceDetector.isDetectionDone());
	}

	@Test
	public void resetAllowsDetectingAgain() {
		FaceDetector.requestDetection(context, null);
		assertTrue(FaceDetector.isDetectionDone());

		FaceDetector.resetForNewRun();
		assertFalse(FaceDetector.isDetectionDone());

		AtomicInteger calls = new AtomicInteger();
		FaceDetector.requestDetection(context, (name, confidence) -> calls.incrementAndGet());
		assertEquals(1, calls.get());
	}

	// ---------------- detectNow, what a brick uses ----------------

	@Test
	public void detectNowIgnoresTheLatch() {
		FaceDetector.requestDetection(context, null);
		assertTrue(FaceDetector.isDetectionDone());

		AtomicInteger calls = new AtomicInteger();
		FaceDetector.detectNow(context, (name, confidence) -> calls.incrementAndGet());

		assertEquals("a brick means look now, whatever happened before", 1, calls.get());
	}

	@Test
	public void detectNowIgnoresTheInterval() {
		FaceDetector.requestDetection(context, null);
		FaceDetector.resetForNewRun();
		setLong(F_LAST_START, System.currentTimeMillis());

		AtomicInteger calls = new AtomicInteger();
		FaceDetector.detectNow(context, (name, confidence) -> calls.incrementAndGet());
		assertEquals(1, calls.get());
	}

	@Test
	public void detectNowRefusesWhileACaptureIsInFlight() {
		setBoolean(F_RUNNING, true);
		assertFalse(FaceDetector.detectNow(context, null));
	}

	// ---------------- The script gate ----------------

	@Test
	public void aBlockingReadOutsideAScriptDoesNotOpenTheCamera() {
		// The brick view evaluates formulas to draw its label. That must never
		// trigger a camera.
		setBoolean(F_SCRIPT, false);
		setString(F_LAST_NAME, "salah");
		setBoolean(F_DONE, false);

		assertEquals("salah", FaceDetector.detectBlocking(context));
		assertFalse(FaceDetector.isDetectionDone());
	}

	@Test
	public void aBlockingReadAfterAResultReturnsItImmediately() {
		setString(F_LAST_NAME, "salah");
		setBoolean(F_DONE, true);
		assertEquals("salah", FaceDetector.detectBlocking(context));
	}

	@Test
	public void aBlockingReadWithNoContextIsSafe() {
		assertEquals(FaceDetector.UNKNOWN, FaceDetector.detectBlocking(null));
	}

	@Test
	public void setScriptRunningIsReadBack() {
		FaceDetector.setScriptRunning(true);
		assertTrue(FaceDetector.isScriptRunning());
		FaceDetector.setScriptRunning(false);
		assertFalse(FaceDetector.isScriptRunning());
	}

	// ---------------- Reflection helpers ----------------

	private static Field field(String name) {
		try {
			Field f = FaceDetector.class.getDeclaredField(name);
			f.setAccessible(true);
			return f;
		} catch (NoSuchFieldException e) {
			throw new AssertionError("missing static field: " + name, e);
		}
	}

	private static void setBoolean(String name, boolean value) {
		try {
			field(name).setBoolean(null, value);
		} catch (IllegalAccessException e) {
			throw new AssertionError(e);
		}
	}

	private static void setLong(String name, long value) {
		try {
			field(name).setLong(null, value);
		} catch (IllegalAccessException e) {
			throw new AssertionError(e);
		}
	}

	private static void setString(String name, String value) {
		try {
			field(name).set(null, value);
		} catch (IllegalAccessException e) {
			throw new AssertionError(e);
		}
	}
}
