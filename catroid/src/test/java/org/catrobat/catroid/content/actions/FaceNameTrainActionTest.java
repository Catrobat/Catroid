package org.catrobat.catroid.content.actions;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * State machine tests for the training action.
 *
 * The state that matters lives in private static fields of the companion object,
 * because that is what lets training survive the stage being destroyed and the
 * action being recreated. These tests read and write those fields by reflection,
 * so no test-only methods are needed in production code.
 *
 * Kotlin puts companion object backing fields on the outer class as private
 * statics, which is why FaceNameTrainAction.class.getDeclaredField works here.
 * If a field is ever renamed, fieldNamesHaveNotChanged fails first and says so,
 * rather than the rest failing for a confusing reason.
 *
 * The dialogs need a window and are covered by FaceTrainingUiTest and the manual
 * checklist. Without the tflite models, recognizerOrInit() returns null and
 * handleResult stops there, so only the guards before that line are exercised.
 */
@RunWith(RobolectricTestRunner.class)
public class FaceNameTrainActionTest {

	private static final int RESULT_OK = -1;
	private static final int RESULT_CANCELED = 0;

	private static final String F_APP_CONTEXT = "appContext";
	private static final String F_PENDING_NAME = "pendingName";
	private static final String F_TRAINING = "trainingInProgress";
	private static final String F_TRAINING_NAME = "trainingName";
	private static final String F_PENDING_OUTCOME = "pendingOutcome";
	private static final String F_PROGRESS_DONE = "progressDone";
	private static final String F_PROGRESS_TOTAL = "progressTotal";

	private Context context;

	@Before
	public void setUp() {
		context = ApplicationProvider.getApplicationContext();
		resetState(context);
	}

	@After
	public void tearDown() {
		resetState(null);
	}

	// ---------------- Guard test ----------------

	@Test
	public void fieldNamesHaveNotChanged() {
		String[] required = {
				F_APP_CONTEXT, F_PENDING_NAME, F_TRAINING, F_TRAINING_NAME,
				F_PENDING_OUTCOME, F_PROGRESS_DONE, F_PROGRESS_TOTAL
		};
		for (String name : required) {
			try {
				FaceNameTrainAction.class.getDeclaredField(name);
			} catch (NoSuchFieldException e) {
				fail("FaceNameTrainAction no longer has a static field called '" + name
						+ "'. Update the constants at the top of this test.");
			}
		}
	}

	// ---------------- Lifecycle ----------------



	@Test
	public void resetDoesNotWipeTheTrainingState() {
		// libGDX calls reset() as soon as act() returns true, long before the user
		// has finished picking photos. Clearing state there killed the callback and
		// was the reason nothing was ever saved.
		setString(F_PENDING_NAME, "salah");
		setBoolean(F_TRAINING, true);

		new FaceNameTrainAction().reset();

		assertEquals("salah", getString(F_PENDING_NAME));
		assertTrue(getBoolean(F_TRAINING));
	}

	@Test
	public void stateIsSharedAcrossActionInstances() {
		// ActionFactory builds a new FaceNameTrainAction on every run. The picker
		// callback must still find what the previous instance left behind.
		new FaceNameTrainAction().reset();
		setString(F_PENDING_NAME, "karim");
		new FaceNameTrainAction().reset();

		assertEquals("karim", getString(F_PENDING_NAME));
	}

	@Test
	public void currentInstanceCanBeSetAndRead() {
		FaceNameTrainAction action = new FaceNameTrainAction();
		FaceNameTrainAction.setCurrentInstance(action);
		assertEquals(action, FaceNameTrainAction.getCurrentInstance());
	}

	@Test
	public void freshStateHasNothingPending() {
		assertNull(getString(F_PENDING_NAME));
		assertFalse(getBoolean(F_TRAINING));
		assertNull(getString(F_PENDING_OUTCOME));
	}

	@Test
	public void progressTotalIsNeverZero() {
		// It divides the progress bar, so zero would be a crash or a blank bar.
		assertTrue(getInt(F_PROGRESS_TOTAL) >= 1);
		assertEquals(0, getInt(F_PROGRESS_DONE));
	}

	// ---------------- handleResult guards ----------------

	@Test
	public void aSecondResultIsIgnoredWhileTrainingRuns() {
		// StageActivity and StageResourceHolder may both forward the result.
		// Training must not run twice on the same photos.
		setBoolean(F_TRAINING, true);
		setString(F_PENDING_NAME, "salah");

		new FaceNameTrainAction().handleResult(1000, RESULT_OK, intentWithOnePhoto());

		assertTrue(getBoolean(F_TRAINING));
		assertEquals("nothing should have been consumed", "salah", getString(F_PENDING_NAME));
	}

	@Test
	public void aCancelledPickerDoesNotStartTraining() {
		setString(F_PENDING_NAME, "salah");
		new FaceNameTrainAction().handleResult(1000, RESULT_CANCELED, null);
		assertFalse(getBoolean(F_TRAINING));
	}

	@Test
	public void aResultWithNoDataDoesNotStartTraining() {
		setString(F_PENDING_NAME, "salah");
		new FaceNameTrainAction().handleResult(1000, RESULT_OK, null);
		assertFalse(getBoolean(F_TRAINING));
	}

	@Test
	public void aResultWithNoPendingNameDoesNotStartTraining() {
		setString(F_PENDING_NAME, null);
		new FaceNameTrainAction().handleResult(1000, RESULT_OK, intentWithOnePhoto());
		assertFalse(getBoolean(F_TRAINING));
	}

	@Test
	public void anEmptyPickerSelectionDoesNotStartTraining() {
		setString(F_PENDING_NAME, "salah");
		new FaceNameTrainAction().handleResult(1000, RESULT_OK, new Intent());
		assertFalse(getBoolean(F_TRAINING));
	}

	@Test
	public void handleResultNeverThrowsOnBadInput() {
		FaceNameTrainAction action = new FaceNameTrainAction();

		// Whatever arrives, the program must keep running.
		action.handleResult(0, 0, null);
		action.handleResult(-1, RESULT_OK, null);
		action.handleResult(Integer.MAX_VALUE, RESULT_OK, new Intent());
		action.handleResult(1000, 12345, new Intent());
	}

	// ---------------- Request codes ----------------

	@Test
	public void thePickerRangeIsClaimed() {
		assertTrue(FaceNameTrainAction.ownsRequestCode(FaceNameTrainAction.REQUEST_FIRST));
		assertTrue(FaceNameTrainAction.ownsRequestCode(FaceNameTrainAction.REQUEST_LAST));
		assertFalse(FaceNameTrainAction.ownsRequestCode(
				FaceNameTrainAction.REQUEST_FIRST - 1));
		assertFalse(FaceNameTrainAction.ownsRequestCode(
				FaceNameTrainAction.REQUEST_LAST + 1));
	}

	// ---------------- Reflection helpers ----------------

	private static void resetState(Context context) {
		setObject(F_APP_CONTEXT, context == null ? null : context.getApplicationContext());
		setObject(F_PENDING_NAME, null);
		setObject(F_TRAINING_NAME, null);
		setObject(F_PENDING_OUTCOME, null);
		setBoolean(F_TRAINING, false);
		setInt(F_PROGRESS_DONE, 0);
		setInt(F_PROGRESS_TOTAL, 1);
		FaceNameTrainAction.setCurrentInstance(null);
	}

	private static Field field(String name) {
		try {
			Field f = FaceNameTrainAction.class.getDeclaredField(name);
			f.setAccessible(true);
			return f;
		} catch (NoSuchFieldException e) {
			throw new AssertionError("missing static field: " + name, e);
		}
	}

	private static void setObject(String name, Object value) {
		try {
			field(name).set(null, value);
		} catch (IllegalAccessException e) {
			throw new AssertionError(e);
		}
	}

	private static void setString(String name, String value) {
		setObject(name, value);
	}

	private static String getString(String name) {
		try {
			return (String) field(name).get(null);
		} catch (IllegalAccessException e) {
			throw new AssertionError(e);
		}
	}

	private static void setBoolean(String name, boolean value) {
		try {
			field(name).setBoolean(null, value);
		} catch (IllegalAccessException e) {
			throw new AssertionError(e);
		}
	}

	private static boolean getBoolean(String name) {
		try {
			return field(name).getBoolean(null);
		} catch (IllegalAccessException e) {
			throw new AssertionError(e);
		}
	}

	private static void setInt(String name, int value) {
		try {
			field(name).setInt(null, value);
		} catch (IllegalAccessException e) {
			throw new AssertionError(e);
		}
	}

	private static int getInt(String name) {
		try {
			return field(name).getInt(null);
		} catch (IllegalAccessException e) {
			throw new AssertionError(e);
		}
	}

	private static Intent intentWithOnePhoto() {
		Intent intent = new Intent();
		intent.setData(Uri.parse("content://media/external/images/media/1"));
		return intent;
	}
}