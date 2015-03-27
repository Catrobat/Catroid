/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.ui.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;

import com.robotium.solo.Solo;

import junit.framework.TestSuite;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.MyProjectsActivity;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.ui.dialogs.LoginRegisterDialog;
import org.catrobat.catroid.ui.dialogs.NewProjectDialog;
import org.catrobat.catroid.ui.dialogs.NewSpriteDialog;
import org.catrobat.catroid.ui.fragment.BrickCategoryFragment;
import org.catrobat.catroid.ui.fragment.LookFragment;
import org.catrobat.catroid.ui.fragment.ScriptFragment;
import org.catrobat.catroid.ui.fragment.SoundFragment;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.io.File;
import java.util.ArrayList;

public class DoubleClickOpensViewOnceTest extends TestSuite {
	private static final int SOLO_WAIT_FOR_VIEW_TIMEOUT = 3000;

	public static TestSuite suite() {
		TestSuite suite = new TestSuite(DoubleClickOpensViewOnceTest.class.getName());
		suite.addTestSuite(MainMenuDoubleClickOpensViewOnceTest.class);
		suite.addTestSuite(MyProjectsDoubleClickOpensViewOnceTest.class);
		suite.addTestSuite(ProgramMenuActivityDoubleClickOpensViewOnceTest.class);
		suite.addTestSuite(ProjectActivityDoubleClickOpensViewOnceTest.class);
		suite.addTestSuite(ScriptActivityDoubleClickOpensViewOnceTest.class);
		suite.addTestSuite(ScriptFragmentDoubleClickOpensViewOnceTest.class);
		suite.addTestSuite(LookFragmentDoubleClickOpensViewOnceTest.class);

		return suite;
	}

	private abstract static class OnClickCommand {
		public void runOnUiThread(Activity activity) {
			activity.runOnUiThread(new Runnable() {
				public void run() {
					OnClickCommand.this.execute();
				}
			});
		}

		protected abstract void execute();
	}

	private static class ActivityInstrumentationTestBase<T extends Activity> extends
			ActivityInstrumentationTestCase2<T> {
		public Solo solo;

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
			checkDoubleClickOpensViewOnce(clickCommand, buttonId, openedViewId, false);
		}

		public void checkDoubleClickOpensViewOnce(OnClickCommand clickCommand, int buttonId, int openedViewId,
				boolean isKeyboardVisible) {
			View button = findView(buttonId);
			simulateDoubleClick(clickCommand);
			waitForView(openedViewId);
			goBack(isKeyboardVisible);
			waitForView(button);
		}

		public void checkDoubleClickOpensViewOnceFragment(OnClickCommand clickCommand, int buttonId,
				String openedFragmentTag) {
			checkDoubleClickOpensViewOnceFragment(clickCommand, buttonId, openedFragmentTag, false);
		}

		public void checkDoubleClickOpensViewOnceFragment(OnClickCommand clickCommand, int buttonId,
				String openedFragmentTag, boolean isKeyboardVisible) {
			View button = findView(buttonId);
			simulateDoubleClick(clickCommand);
			waitForFragment(openedFragmentTag);
			goBack(isKeyboardVisible);
			waitForView(button);
		}

		public void checkDoubleClickOpensViewOnceActivity(OnClickCommand clickCommand, int buttonId,
				String openedActivityTag) {
			checkDoubleClickOpensViewOnceActivity(clickCommand, buttonId, openedActivityTag, false);
		}

		public void checkDoubleClickOpensViewOnceActivity(OnClickCommand clickCommand, int buttonId,
				String openedActivityTag, boolean isKeyboardVisible) {
			View button = findView(buttonId);
			simulateDoubleClick(clickCommand);
			waitForActivity(openedActivityTag);
			goBack(isKeyboardVisible);
			waitForView(button);
		}

		public void checkDoubleClickOpensViewOnce(OnClickCommand clickCommand, int buttonId, String text) {
			checkDoubleClickOpensViewOnce(clickCommand, buttonId, text, false);
		}

		public void checkDoubleClickOpensViewOnce(OnClickCommand clickCommand, int buttonId, String text,
				boolean isKeyboardVisible) {
			View button = findView(buttonId);
			simulateDoubleClick(clickCommand);
			waitForText(text);
			goBack(isKeyboardVisible);
			waitForView(button);
		}

		private View findView(int id) {
			View button = solo.getCurrentActivity().findViewById(id);
			assertNotNull("Button not found", button);
			return button;
		}

		private void simulateDoubleClick(OnClickCommand clickCommand) {
			Activity activity = solo.getCurrentActivity();
			clickCommand.runOnUiThread(activity);
			clickCommand.runOnUiThread(activity);
		}

		private void goBack(boolean isKeyboardVisible) {
			solo.goBack();
			if (isKeyboardVisible) {
				solo.goBack();
			}
		}

		private void waitForView(int id) {
			if (!solo.waitForFragmentById(id, SOLO_WAIT_FOR_VIEW_TIMEOUT)) {
				fail("Couldn't find opened view after clicking on button");
			}
		}

		private void waitForFragment(String tag) {
			if (!solo.waitForFragmentByTag(tag, SOLO_WAIT_FOR_VIEW_TIMEOUT)) {
				fail("Couldn't find opened view after clicking on button");
			}
		}

		private void waitForActivity(String tag) {
			if (!solo.waitForActivity(tag, SOLO_WAIT_FOR_VIEW_TIMEOUT)) {
				fail("Couldn't find opened view after clicking on button");
			}
		}

		private void waitForView(View view) {
			if (!solo.waitForView(view, SOLO_WAIT_FOR_VIEW_TIMEOUT, true)) {
				fail("Didn't return to view which contains the button (" + solo.getCurrentViews() + ")");
			}

			if (!solo.getCurrentActivity().hasWindowFocus()) {
				fail("Activity didn't gain focus");
			}
		}

		private void waitForText(String text) {
			if (!solo.waitForText(text)) {
				fail("Didn't find text " + text);
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
					activity.handleContinueButton();
				}
			}, R.id.main_menu_button_continue, R.id.fragment_sprites_list);
		}

		public void testMainMenuButtonNew() {
			checkDoubleClickOpensViewOnceFragment(new OnClickCommand() {
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
			checkDoubleClickOpensViewOnceFragment(new OnClickCommand() {
				@Override
				public void execute() {
					activity.handleUploadButton(null);
				}
			}, R.id.main_menu_button_upload, LoginRegisterDialog.DIALOG_FRAGMENT_TAG, true);
		}
	}

	public static class MyProjectsDoubleClickOpensViewOnceTest extends
			ActivityInstrumentationTestBase<MyProjectsActivity> {
		private MyProjectsActivity activity;

		public MyProjectsDoubleClickOpensViewOnceTest() {
			super(MyProjectsActivity.class);
		}

		@Override
		public void setUp() throws Exception {
			super.setUp();
			activity = (MyProjectsActivity) solo.getCurrentActivity();
		}

		@Override
		public void tearDown() throws Exception {
			super.tearDown();
			activity = null;
		}

		public void testMyProjectsAddButton() {
			checkDoubleClickOpensViewOnceFragment(new OnClickCommand() {
				@Override
				public void execute() {
					activity.handleAddButton(null);
				}
			}, R.id.button_add, NewProjectDialog.DIALOG_FRAGMENT_TAG, true);
		}
	}

	public static class ProgramMenuActivityDoubleClickOpensViewOnceTest extends
			ActivityInstrumentationTestBase<ProgramMenuActivity> {
		private ProgramMenuActivity activity;

		public ProgramMenuActivityDoubleClickOpensViewOnceTest() {
			super(ProgramMenuActivity.class);
		}

		@Override
		public void setUp() throws Exception {
			UiTestUtils.createTestProject();
			super.setUp();
			activity = (ProgramMenuActivity) solo.getCurrentActivity();
		}

		@Override
		public void tearDown() throws Exception {
			super.tearDown();
			UiTestUtils.clearAllUtilTestProjects();
			activity = null;
		}

		public void testProgramMenuPlayButton() {
			checkDoubleClickOpensViewOnceActivity(new OnClickCommand() {
				@Override
				public void execute() {
					activity.handlePlayButton(null);
				}
			}, R.id.button_play, StageActivity.TAG, true);
		}

		public void testProgramMenuScriptsButton() {
			checkDoubleClickOpensViewOnceFragment(new OnClickCommand() {
				@Override
				public void execute() {
					activity.handleScriptsButton(null);
				}
			}, R.id.program_menu_button_scripts, ScriptFragment.TAG);
		}

		public void testProgramMenuLooksButton() {
			checkDoubleClickOpensViewOnceFragment(new OnClickCommand() {
				@Override
				public void execute() {
					activity.handleLooksButton(null);
				}
			}, R.id.program_menu_button_looks, LookFragment.TAG);
		}

		public void testProgramMenuSoundsButton() {
			checkDoubleClickOpensViewOnceFragment(new OnClickCommand() {
				@Override
				public void execute() {
					activity.handleSoundsButton(null);
				}
			}, R.id.program_menu_button_sounds, SoundFragment.TAG);
		}
	}

	public static class ProjectActivityDoubleClickOpensViewOnceTest extends
			ActivityInstrumentationTestBase<ProjectActivity> {
		private ProjectActivity activity;

		public ProjectActivityDoubleClickOpensViewOnceTest() {
			super(ProjectActivity.class);
		}

		@Override
		public void setUp() throws Exception {
			UiTestUtils.createTestProject();
			super.setUp();
			activity = (ProjectActivity) solo.getCurrentActivity();
		}

		@Override
		public void tearDown() throws Exception {
			super.tearDown();
			UiTestUtils.clearAllUtilTestProjects();
			activity = null;
		}

		public void testProjectAddButton() {
			checkDoubleClickOpensViewOnceFragment(new OnClickCommand() {
				@Override
				public void execute() {
					activity.handleAddButton(null);
				}
			}, R.id.button_add, NewSpriteDialog.DIALOG_FRAGMENT_TAG, true);
		}

		public void testProjectPlayButton() {
			checkDoubleClickOpensViewOnceActivity(new OnClickCommand() {
				@Override
				public void execute() {
					activity.handlePlayButton(null);
				}
			}, R.id.button_play, StageActivity.TAG, true);
		}
	}

	public static class ScriptActivityDoubleClickOpensViewOnceTest extends
			ActivityInstrumentationTestBase<ScriptActivity> {
		private ScriptActivity activity;

		public ScriptActivityDoubleClickOpensViewOnceTest() {
			super(ScriptActivity.class);
		}

		@Override
		public void setUp() throws Exception {
			UiTestUtils.createTestProject();
			super.setUp();
			activity = (ScriptActivity) solo.getCurrentActivity();
		}

		@Override
		public void tearDown() throws Exception {
			super.tearDown();
			UiTestUtils.clearAllUtilTestProjects();
			activity = null;
		}

		public void testProjectAddButton() {
			checkDoubleClickOpensViewOnceFragment(new OnClickCommand() {
				@Override
				public void execute() {
					activity.handleAddButton(null);
				}
			}, R.id.button_add, ScriptFragment.TAG);
		}

		public void testProjectPlayButton() {
			checkDoubleClickOpensViewOnceActivity(new OnClickCommand() {
				@Override
				public void execute() {
					activity.handlePlayButton(null);
				}
			}, R.id.button_play, StageActivity.TAG, true);
		}
	}

	public static class ScriptFragmentDoubleClickOpensViewOnceTest extends
			ActivityInstrumentationTestBase<MainMenuActivity> {
		private ScriptFragment fragment;

		public ScriptFragmentDoubleClickOpensViewOnceTest() {
			super(MainMenuActivity.class);
		}

		@Override
		public void setUp() throws Exception {
			UiTestUtils.createTestProject();
			super.setUp();
			UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
			fragment = (ScriptFragment) Reflection.getPrivateField(solo.getCurrentActivity(), "currentFragment");
		}

		@Override
		public void tearDown() throws Exception {
			super.tearDown();
			UiTestUtils.clearAllUtilTestProjects();
			fragment = null;
		}

		public void testScriptFragmentAddButton() {
			checkDoubleClickOpensViewOnceFragment(new OnClickCommand() {
				@Override
				public void execute() {
					fragment.handleAddButton();
				}
			}, R.id.script_fragment_container, ScriptFragment.TAG);
		}

		public void testBrickAdapterOnItemClick() {
			final View brickView = solo.getView(R.id.brick_hide_layout);
			ScriptActivity activity = (ScriptActivity) solo.getCurrentActivity();
			ScriptFragment scriptFragment = (ScriptFragment) activity.getFragment(ScriptActivity.FRAGMENT_SCRIPTS);
			final BrickAdapter brickAdapter = scriptFragment.getAdapter();

			checkDoubleClickOpensViewOnce(new OnClickCommand() {
				@Override
				protected void execute() {
					brickAdapter.onClick(brickView);
				}
			}, R.id.brick_hide_layout, getActivity().getApplicationContext().getText(R.string.brick_context_dialog_delete_brick).toString());
		}

		public void testBrickCategoryFragmentOnItemClick() {
			View addButton = solo.getView(R.id.button_add);
			solo.clickOnView(addButton);
			solo.waitForFragmentByTag(BrickCategoryFragment.BRICK_CATEGORY_FRAGMENT_TAG);

			ScriptActivity activity = (ScriptActivity) solo.getCurrentActivity();
			BrickCategoryFragment brickCategoryFragment = (BrickCategoryFragment) activity.getSupportFragmentManager()
					.findFragmentByTag(BrickCategoryFragment.BRICK_CATEGORY_FRAGMENT_TAG);
			solo.sleep(250);
			final OnItemClickListener onItemClickListener = brickCategoryFragment.getListView()
					.getOnItemClickListener();

			checkDoubleClickOpensViewOnceFragment(new OnClickCommand() {
				@Override
				protected void execute() {
					onItemClickListener.onItemClick(null, null, 0, 0);
				}
			}, brickCategoryFragment.getId(), BrickCategoryFragment.BRICK_CATEGORY_FRAGMENT_TAG);
		}
	}

	public static class LookFragmentDoubleClickOpensViewOnceTest extends
			ActivityInstrumentationTestBase<MainMenuActivity> {
		private static final int RESOURCE_IMAGE = org.catrobat.catroid.test.R.drawable.catroid_sunglasses;
		private static final String FIRST_TEST_LOOK_NAME = "lookNameTest";

		private LookFragment fragment;

		public LookFragmentDoubleClickOpensViewOnceTest() {
			super(MainMenuActivity.class);
		}

		@Override
		public void setUp() throws Exception {
			UiTestUtils.createTestProject();

			File imageFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME,
					"catroid_sunglasses.png", RESOURCE_IMAGE, getActivity(), UiTestUtils.FileTypes.IMAGE);

			ArrayList<LookData> lookDataList = ProjectManager.getInstance().getCurrentSprite().getLookDataList();
			LookData lookData = new LookData();
			lookData.setLookFilename(imageFile.getName());
			lookData.setLookName(FIRST_TEST_LOOK_NAME);
			lookDataList.add(lookData);

			super.setUp();
			UiTestUtils.getIntoLooksFromMainMenu(solo, true);
			fragment = (LookFragment) Reflection.getPrivateField(solo.getCurrentActivity(), "currentFragment");
		}

		@Override
		public void tearDown() throws Exception {
			super.tearDown();
			UiTestUtils.clearAllUtilTestProjects();
			fragment = null;
		}

		public void testLookFragmentAddButton() {
			checkDoubleClickOpensViewOnceFragment(new OnClickCommand() {
				@Override
				public void execute() {
					fragment.handleAddButton();
				}
			}, R.id.script_fragment_container, LookFragment.TAG);
		}
	}
}
