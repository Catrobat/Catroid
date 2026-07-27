package org.catrobat.catroid.uiespresso.facerecognizer;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.catrobat.catroid.FaceRecognizer.Recognizer;
import org.catrobat.catroid.FaceRecognizer.env.FileUtils;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.actions.FaceNameTrainAction;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.endsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Espresso tests for the training dialogs.
 *
 * These run against a plain host activity instead of StageActivity, using the
 * testActivity hook in FaceNameTrainAction. The dialogs only need a window, so
 * this exercises the real dialog code without loading a Catroid project or
 * starting libGDX.
 *
 * The photo picker is stubbed with Espresso Intents and answered with a real
 * image copied out of the test assets, so the full path runs: pick, extract
 * embeddings, save, show the result.
 *
 * Needs face_a_1.jpg in app/src/androidTest/assets/. Tests that use it are
 * skipped, not failed, when it is missing.
 */
@RunWith(AndroidJUnit4.class)
public class FaceTrainingUiTest {

	private Context appContext;
	private Context testContext;
	private ActivityScenario<TestHostActivity> scenario;
	private FaceNameTrainAction action;

	@Before
	public void setUp() throws Exception {
		appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
		testContext = InstrumentationRegistry.getInstrumentation().getContext();

		FileUtils.init(appContext);
		FileUtils.deleteAll();
		Recognizer.release();

		FaceNameTrainAction.resetStateForTest(appContext);

		scenario = ActivityScenario.launch(TestHostActivity.class);
		scenario.onActivity(activity -> {
			FaceNameTrainAction.setTestActivity(activity);
			action = new FaceNameTrainAction();
			action.openMenuForTest(activity);
		});
		waitForIdle();
	}

	@After
	public void tearDown() {
		FaceNameTrainAction.resetStateForTest(null);
		if (scenario != null) {
			scenario.close();
		}
		FileUtils.deleteAll();
		Recognizer.release();
	}

	// ---------------- Add Photos menu ----------------

	@Test
	public void theMenuShowsItsTitle() {
		onView(withText(string(R.string.face_train_title)))
				.inRoot(isDialog())
				.check(matches(isDisplayed()));
	}

	@Test
	public void anEmptyMenuSaysThereAreNoNames() {
		onView(withText(string(R.string.face_train_no_names)))
				.inRoot(isDialog())
				.check(matches(isDisplayed()));
	}

	@Test
	public void theMenuShowsTheAddNewNameButton() {
		onView(withText(string(R.string.face_train_add_new_name)))
				.inRoot(isDialog())
				.check(matches(isDisplayed()));
	}

	@Test
	public void theMenuHasNoDeveloperButtons() {
		onView(withText("Run self test")).check(doesNotExist());
		onView(withText("Tune matching")).check(doesNotExist());
		onView(withText("Clean photos")).check(doesNotExist());
		onView(withText("Check saved files")).check(doesNotExist());
	}

	// ---------------- Add new name popup ----------------

	@Test
	public void tappingAddNewNameOpensThePopup() {
		openNewNamePopup();

		onView(withText(string(R.string.face_train_new_name_title)))
				.inRoot(isDialog())
				.check(matches(isDisplayed()));
		onView(withText(string(R.string.face_train_name_subtitle)))
				.inRoot(isDialog())
				.check(matches(isDisplayed()));
	}

	@Test
	public void thePopupHasCancelAndNext() {
		openNewNamePopup();

		onView(withText(string(R.string.face_train_cancel)))
				.inRoot(isDialog())
				.check(matches(isDisplayed()));
		onView(withText(string(R.string.face_train_next)))
				.inRoot(isDialog())
				.check(matches(isDisplayed()));
	}

	@Test
	public void nextWithAnEmptyNameKeepsThePopupOpen() {
		openNewNamePopup();

		onView(withText(string(R.string.face_train_next))).inRoot(isDialog()).perform(click());
		waitForIdle();

		// Still on the popup, nothing was added.
		onView(withText(string(R.string.face_train_new_name_title)))
				.inRoot(isDialog())
				.check(matches(isDisplayed()));
		assertEquals(0, namesCount());
	}

	@Test
	public void cancelReturnsToTheMenuWithoutAddingAName() {
		openNewNamePopup();

		onView(withText(string(R.string.face_train_cancel))).inRoot(isDialog()).perform(click());
		waitForIdle();

		onView(withText(string(R.string.face_train_title)))
				.inRoot(isDialog())
				.check(matches(isDisplayed()));
		assertEquals(0, namesCount());
	}

	@Test
	public void whitespaceOnlyNamesAreRejected() {
		openNewNamePopup();
		typeName("   ");

		onView(withText(string(R.string.face_train_next))).inRoot(isDialog()).perform(click());
		waitForIdle();

		assertEquals(0, namesCount());
	}

	// ---------------- Picker ----------------

	@Test
	public void nextWithANameOpensThePhotoPicker() {
		Intents.init();
		try {
			intending(hasAction(Intent.ACTION_OPEN_DOCUMENT))
					.respondWith(new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null));

			openNewNamePopup();
			typeName("salah");
			onView(withText(string(R.string.face_train_next))).inRoot(isDialog()).perform(click());
			waitForIdle();

			intended(hasAction(Intent.ACTION_OPEN_DOCUMENT));
			// The name is written straight away, so it survives a cancelled picker.
			assertEquals(1, namesCount());
		} finally {
			Intents.release();
		}
	}

	@Test
	public void cancellingThePickerReturnsToTheMenu() {
		Intents.init();
		try {
			intending(hasAction(Intent.ACTION_OPEN_DOCUMENT))
					.respondWith(new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null));

			addName("salah");
			deliverPickerResult(1000, Activity.RESULT_CANCELED, null);
			waitForIdle();

			onView(withText(string(R.string.face_train_title)))
					.inRoot(isDialog())
					.check(matches(isDisplayed()));
		} finally {
			Intents.release();
		}
	}

	// ---------------- Training ----------------

	@Test
	public void aTrainedNameAppearsInTheMenu() {
		Uri photo = copyTestPhoto();
		Assume.assumeTrue("needs face_a_1.jpg in androidTest/assets", photo != null);

		addName("salah");
		deliverPickerResult(1000, Activity.RESULT_OK, intentFor(photo));
		waitForTraining();

		onView(withText("salah")).inRoot(isDialog()).check(matches(isDisplayed()));
		assertTrue("embeddings should have been saved", photoCount(0) > 0);
	}

	@Test
	public void trainingWithAPhotoOfNoFaceReportsIt() {
		Uri photo = copyAsset("no_face.jpg", "no_face.jpg");
		Assume.assumeTrue("needs no_face.jpg in androidTest/assets", photo != null);

		addName("salah");
		deliverPickerResult(1000, Activity.RESULT_OK, intentFor(photo));
		waitForTraining();

		// The name stays, but nothing was learned from it.
		assertEquals(1, namesCount());
		assertEquals(0, photoCount(0));
	}

	@Test
	public void trainingASecondNameBehavesTheSameAsTheFirst() {
		Uri photo = copyTestPhoto();
		Assume.assumeTrue("needs face_a_1.jpg in androidTest/assets", photo != null);

		addName("salah");
		deliverPickerResult(1000, Activity.RESULT_OK, intentFor(photo));
		waitForTraining();

		addName("karim");
		deliverPickerResult(1001, Activity.RESULT_OK, intentFor(photo));
		waitForTraining();

		assertEquals(2, namesCount());
		assertTrue(photoCount(1) > 0);
		onView(withText("karim")).inRoot(isDialog()).check(matches(isDisplayed()));
	}

	@Test
	public void aDuplicateResultDoesNotTrainTwice() {
		Uri photo = copyTestPhoto();
		Assume.assumeTrue("needs face_a_1.jpg in androidTest/assets", photo != null);

		addName("salah");
		deliverPickerResult(1000, Activity.RESULT_OK, intentFor(photo));
		// A second delivery while the first is still running must be ignored.
		deliverPickerResult(1000, Activity.RESULT_OK, intentFor(photo));
		waitForTraining();

		int afterFirst = photoCount(0);
		deliverPickerResult(1000, Activity.RESULT_OK, intentFor(photo));
		waitForTraining();

		assertTrue("a repeat should add photos, not double the first batch",
				photoCount(0) > afterFirst);
	}

	// ---------------- Delete ----------------

	@Test
	public void theDeleteButtonAsksForConfirmation() {
		addName("salah");
		reopenMenu();

		onView(withText("\u2715")).inRoot(isDialog()).perform(click());
		waitForIdle();

		onView(withText(string(R.string.face_train_delete_title)))
				.inRoot(isDialog())
				.check(matches(isDisplayed()));
	}

	@Test
	public void answeringNoKeepsTheName() {
		addName("salah");
		reopenMenu();

		onView(withText("\u2715")).inRoot(isDialog()).perform(click());
		waitForIdle();
		onView(withText(string(R.string.face_train_no))).inRoot(isDialog()).perform(click());
		waitForIdle();

		assertEquals(1, namesCount());
	}

	@Test
	public void answeringYesRemovesTheName() {
		addName("salah");
		reopenMenu();

		onView(withText("\u2715")).inRoot(isDialog()).perform(click());
		waitForIdle();
		onView(withText(string(R.string.face_train_yes))).inRoot(isDialog()).perform(click());
		waitForIdle();

		assertEquals(0, namesCount());
		onView(withText(string(R.string.face_train_no_names)))
				.inRoot(isDialog())
				.check(matches(isDisplayed()));
	}

	@Test
	public void deletingOneOfThreeLeavesTheOtherTwo() {
		addName("a");
		addName("b");
		addName("c");
		reopenMenu();

		// The delete buttons all carry the same label, so take the second row.
		onView(withText("b")).inRoot(isDialog()).check(matches(isDisplayed()));
		recognizer().deletePerson(1);
		reopenMenu();

		assertEquals(2, namesCount());
		onView(withText("a")).inRoot(isDialog()).check(matches(isDisplayed()));
		onView(withText("c")).inRoot(isDialog()).check(matches(isDisplayed()));
		onView(withText("b")).check(doesNotExist());
	}

	// ---------------- Helpers ----------------

	private String string(int resId) {
		return appContext.getString(resId);
	}

	private void openNewNamePopup() {
		onView(withText(string(R.string.face_train_add_new_name)))
				.inRoot(isDialog())
				.perform(click());
		waitForIdle();
	}

	private void typeName(String name) {
		onView(withClassName(endsWith("EditText")))
				.inRoot(isDialog())
				.perform(clearText(), typeText(name), closeSoftKeyboard());
	}

	/** Adds a name straight through the recogniser, for tests about later steps. */
	private void addName(String name) {
		scenario.onActivity(activity -> recognizer().addPerson(name));
		waitForIdle();
	}

	private void reopenMenu() {
		scenario.onActivity(activity -> action.openMenuForTest(activity));
		waitForIdle();
	}

	private void deliverPickerResult(int requestCode, int resultCode, Intent data) {
		scenario.onActivity(activity ->
				action.handleResult(requestCode, resultCode, data));
		waitForIdle();
	}

	private Recognizer recognizer() {
		try {
			return Recognizer.getInstance(appContext);
		} catch (Exception e) {
			throw new AssertionError(e);
		}
	}

	private int namesCount() {
		return recognizer().getClassNames().size();
	}

	private int photoCount(int index) {
		return recognizer().getPhotoCount(index);
	}

	private static Intent intentFor(Uri uri) {
		Intent intent = new Intent();
		intent.setData(uri);
		return intent;
	}

	private Uri copyTestPhoto() {
		return copyAsset("face_a_1.jpg", "face_a_1.jpg");
	}

	/** Copies a test asset into the app cache so it can be read through a Uri. */
	private Uri copyAsset(String assetName, String fileName) {
		File out = new File(appContext.getCacheDir(), fileName);
		try (InputStream in = testContext.getAssets().open(assetName);
				OutputStream os = new FileOutputStream(out)) {
			byte[] buffer = new byte[8192];
			int read;
			while ((read = in.read(buffer)) != -1) {
				os.write(buffer, 0, read);
			}
			os.flush();
			return Uri.fromFile(out);
		} catch (Exception e) {
			return null;
		}
	}

	private void waitForIdle() {
		InstrumentationRegistry.getInstrumentation().waitForIdleSync();
		sleep(250);
		InstrumentationRegistry.getInstrumentation().waitForIdleSync();
	}

	/** Training runs on a background thread, so polling beats a fixed sleep. */
	private void waitForTraining() {
		long deadline = System.currentTimeMillis() + 30000;
		while (System.currentTimeMillis() < deadline) {
			if (!FaceNameTrainAction.isTrainingForTest()) {
				break;
			}
			sleep(100);
		}
		waitForIdle();
		sleep(900);
		waitForIdle();
	}

	private static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}