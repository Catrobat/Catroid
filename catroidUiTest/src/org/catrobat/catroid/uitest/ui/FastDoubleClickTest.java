package org.catrobat.catroid.uitest.ui;

import junit.framework.TestSuite;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.dialogs.LoginRegisterDialog;
import org.catrobat.catroid.ui.dialogs.NewProjectDialog;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.jayway.android.robotium.solo.Solo;

public class FastDoubleClickTest extends TestSuite {
	private static final int SOLO_WAIT_TIMEOUT = 2000;

	public static TestSuite suite() {
		TestSuite suite = new TestSuite(FastDoubleClickTest.class.getName());
		suite.addTestSuite(MainMenuFastDoubleClickTest.class);
		return suite;
	}

	private static class Base<T extends Activity> extends ActivityInstrumentationTestCase2<T> {
		protected Solo solo;

		public Base(Class<T> clazz) {
			super(clazz);
		}

		@Override
		public void setUp() throws Exception {
			super.setUp();
			solo = new Solo(getInstrumentation(), getActivity());
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
			prefs.edit().putString(Constants.TOKEN, "0").commit();
		}

		@Override
		public void tearDown() throws Exception {
			solo.finishOpenedActivities();
			super.tearDown();
			solo = null;
		}

		public void a(ClickCommand clickCommand, int buttonId, int openedViewId) {
			View button = getButton(buttonId);
			simulateFastDoubleClick(clickCommand);
			waitForViewById(openedViewId);
			goBack();
			waitForView(button);
		}

		public void a(ClickCommand clickCommand, int buttonId, String openedViewTag, boolean isKeyboardVisible) {
			View button = getButton(buttonId);
			simulateFastDoubleClick(clickCommand);
			waitForViewByTag(openedViewTag);
			goBack(isKeyboardVisible);
			waitForView(button);
		}

		public void a(ClickCommand clickCommand, int buttonId, String openedDialogFragmentTag) {
			a(clickCommand, buttonId, openedDialogFragmentTag, false);
		}

		private View getButton(int id) {
			View button = solo.getCurrentActivity().findViewById(id);
			assertNotNull("Button not found", button);
			return button;
		}

		private void simulateFastDoubleClick(ClickCommand clickCommand) {
			clickCommand.execute();
			clickCommand.execute();
		}

		private void goBack() {
			goBack(false);
		}

		private void goBack(boolean isKeyboardVisible) {
			solo.goBack();
			if (isKeyboardVisible) {
				solo.goBack();
			}
		}

		private void waitForViewById(int id) {
			if (!solo.waitForFragmentById(id, SOLO_WAIT_TIMEOUT)) {
				fail("Couldn't find opened view after clicking on button");
			}
		}

		private void waitForViewByTag(String tag) {
			if (!solo.waitForFragmentByTag(tag, SOLO_WAIT_TIMEOUT)) {
				fail("Couldn't find opened view after clicking on button");
			}
		}

		private void waitForView(View view) {
			waitForView(view, false);
		}

		private void waitForView(View view, boolean activityNeedsFocus) {
			if (!solo.waitForView(view, SOLO_WAIT_TIMEOUT, true)) {
				fail("Didn't return to view which contains the button (" + solo.getCurrentViews() + ")");
			}

			if (activityNeedsFocus && !solo.getCurrentActivity().hasWindowFocus()) {
				fail("Activity didn't gain focus");
			}
		}
	}

	public static class MainMenuFastDoubleClickTest extends Base<MainMenuActivity> {
		private MainMenuActivity activity;

		public MainMenuFastDoubleClickTest() {
			super(MainMenuActivity.class);
		}

		@Override
		public void setUp() throws Exception {
			super.setUp();
			activity = (MainMenuActivity) solo.getCurrentActivity();
		}

		@Override
		public void tearDown() throws Exception {
			super.tearDown();
			activity = null;
		}

		public void testMainMenuButtonContinue() {
			a(new ClickCommand() {
				@Override
				public void execute() {
					activity.handleContinueButton(null);
				}
			}, R.id.main_menu_button_continue, R.id.fragment_sprites_list);
		}

		public void testMainMenuButtonNew() {
			a(new ClickCommand() {
				@Override
				public void execute() {
					activity.handleNewButton(null);
				}
			}, R.id.main_menu_button_new, NewProjectDialog.DIALOG_FRAGMENT_TAG, true);
		}

		public void testMainMenuButtonMyProjects() {
			a(new ClickCommand() {
				@Override
				public void execute() {
					activity.handleProgramsButton(null);
				}
			}, R.id.main_menu_button_programs, R.id.fragment_projects_list);
		}

		public void testMainMenuButtonUpload() {
			a(new ClickCommand() {
				@Override
				public void execute() {
					activity.handleUploadButton(null);
				}
			}, R.id.main_menu_button_upload, LoginRegisterDialog.DIALOG_FRAGMENT_TAG, true);
		}
	}

	private abstract static class ClickCommand {
		public abstract void execute();
	}
}
