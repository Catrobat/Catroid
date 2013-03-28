/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

public class DoubleClickOpensViewOnceTest extends TestSuite {
	private static final int SOLO_WAIT_TIMEOUT = 2000;

	public static TestSuite suite() {
		TestSuite suite = new TestSuite(DoubleClickOpensViewOnceTest.class.getName());
		suite.addTestSuite(MainMenuDoubleClickOpensViewOnceTest.class);
		return suite;
	}

	private static class ActivityInstrumentationTestBase<T extends Activity> extends
			ActivityInstrumentationTestCase2<T> {
		protected Solo solo;

		public ActivityInstrumentationTestBase(Class<T> clazz) {
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

		public void checkDoubleClickOpensViewOnce(OnClickCommand clickCommand, int buttonId, int openedViewId) {
			View button = getButton(buttonId);
			simulateDoubleClick(clickCommand);
			waitForViewById(openedViewId);
			goBack();
			waitForView(button);
		}

		public void checkDoubleClickOpensViewOnce(OnClickCommand clickCommand, int buttonId,
				String openedDialogFragmentTag) {
			checkDoubleClickOpensViewOnce(clickCommand, buttonId, openedDialogFragmentTag, false);
		}

		public void checkDoubleClickOpensViewOnce(OnClickCommand clickCommand, int buttonId, String openedViewTag,
				boolean isKeyboardVisible) {
			View button = getButton(buttonId);
			simulateDoubleClick(clickCommand);
			waitForViewByTag(openedViewTag);
			goBack(isKeyboardVisible);
			waitForView(button);
		}

		private View getButton(int id) {
			View button = solo.getCurrentActivity().findViewById(id);
			assertNotNull("Button not found", button);
			return button;
		}

		private void simulateDoubleClick(OnClickCommand clickCommand) {
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
			if (!solo.waitForView(view, SOLO_WAIT_TIMEOUT, true)) {
				fail("Didn't return to view which contains the button (" + solo.getCurrentViews() + ")");
			}

			if (!solo.getCurrentActivity().hasWindowFocus()) {
				fail("Activity didn't gain focus");
			}
		}
	}

	public static class MainMenuDoubleClickOpensViewOnceTest extends ActivityInstrumentationTestBase<MainMenuActivity> {
		private MainMenuActivity activity;

		public MainMenuDoubleClickOpensViewOnceTest() {
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
			checkDoubleClickOpensViewOnce(new OnClickCommand() {
				@Override
				public void execute() {
					activity.handleContinueButton(null);
				}
			}, R.id.main_menu_button_continue, R.id.fragment_sprites_list);
		}

		public void testMainMenuButtonNew() {
			checkDoubleClickOpensViewOnce(new OnClickCommand() {
				@Override
				public void execute() {
					activity.handleNewButton(null);
				}
			}, R.id.main_menu_button_new, NewProjectDialog.DIALOG_FRAGMENT_TAG, true);
		}

		public void testMainMenuButtonMyProjects() {
			checkDoubleClickOpensViewOnce(new OnClickCommand() {
				@Override
				public void execute() {
					activity.handleProgramsButton(null);
				}
			}, R.id.main_menu_button_programs, R.id.fragment_projects_list);
		}

		public void testMainMenuButtonUpload() {
			checkDoubleClickOpensViewOnce(new OnClickCommand() {
				@Override
				public void execute() {
					activity.handleUploadButton(null);
				}
			}, R.id.main_menu_button_upload, LoginRegisterDialog.DIALOG_FRAGMENT_TAG, true);
		}
	}

	private abstract static class OnClickCommand {
		public abstract void execute();
	}
}
