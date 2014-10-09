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
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;

import com.robotium.solo.Solo;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class ScriptActivityTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	public ScriptActivityTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.enableNfcBricks(getActivity().getApplicationContext());
		UiTestUtils.createTestProject();
		UiTestUtils.prepareStageForTest();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	public void testOrientation() throws NameNotFoundException {
		Activity currentActivity = solo.getCurrentActivity();

		/// Method 1: Assert it is currently in portrait mode.
		assertEquals("ScriptActivity not in Portrait mode!", Configuration.ORIENTATION_PORTRAIT, currentActivity
				.getResources().getConfiguration().orientation);

		/// Method 2: Retrieve info about Activity as collected from AndroidManifest.xml
		// https://developer.android.com/reference/android/content/pm/ActivityInfo.html
		PackageManager packageManager = currentActivity.getPackageManager();
		ActivityInfo activityInfo = packageManager.getActivityInfo(currentActivity.getComponentName(),
				PackageManager.GET_ACTIVITIES);

		// Note that the activity is _indeed_ rotated on your device/emulator!
		// Robotium can _force_ the activity to be in landscape mode (and so could we, programmatically)
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(200);

		assertEquals(ScriptActivity.class.getSimpleName() + " not set to be in portrait mode in AndroidManifest.xml!",
				ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, activityInfo.screenOrientation);
	}

	public void testMainMenuButton() {
		UiTestUtils.waitForFragment(solo, R.id.fragment_script);

		checkMainMenuButton();

		UiTestUtils.getIntoLooksFromMainMenu(solo, true);
		UiTestUtils.waitForFragment(solo, R.id.fragment_look);

		checkMainMenuButton();

		UiTestUtils.getIntoSoundsFromMainMenu(solo);
		UiTestUtils.waitForFragment(solo, R.id.fragment_sound);

		checkMainMenuButton();

        UiTestUtils.getIntoNfcTagsFromMainMenu(solo);
        UiTestUtils.waitForFragment(solo, R.id.fragment_nfctags);

        checkMainMenuButton();
	}

	public void testPlayProgramButton() {
		UiTestUtils.waitForFragment(solo, R.id.fragment_script);

		String currentSprite = ProjectManager.getInstance().getCurrentSprite().getName();
		assertEquals("Current sprite name is not shown as actionbar title or is wrong", "cat", currentSprite);

		checkplayProgramButton();

		UiTestUtils.switchToFragmentInScriptActivity(solo, UiTestUtils.LOOKS_INDEX);
		assertEquals("Current sprite name is not shown as actionbar title or is wrong", "cat", currentSprite);

		checkplayProgramButton();

		UiTestUtils.switchToFragmentInScriptActivity(solo, UiTestUtils.SOUNDS_INDEX);
		assertEquals("Current sprite name is not shown as actionbar title or is wrong", "cat", currentSprite);

		checkplayProgramButton();

        UiTestUtils.switchToFragmentInScriptActivity(solo, UiTestUtils.NFCTAGS_INDEX);
        assertEquals("Current sprite name is not shown as actionbar title or is wrong", "cat", currentSprite);

        checkplayProgramButton();
	}

	public void testOverflowMenuItemSettings() {
		UiTestUtils.waitForFragment(solo, R.id.fragment_script);

		String currentSprite = ProjectManager.getInstance().getCurrentSprite().getName();
		assertEquals("Current sprite name is not shown as actionbar title or is wrong", "cat", currentSprite);

		checkSettingsAndGoBack();

		solo.goBack();
		solo.waitForActivity(ProgramMenuActivity.class);
		solo.clickOnText(solo.getString(R.string.background));
		UiTestUtils.waitForFragment(solo, R.id.fragment_look);
		assertEquals("Current sprite name is not shown as actionbar title or is wrong", "cat", currentSprite);

		checkSettingsAndGoBack();

		solo.goBack();
		solo.waitForActivity(ProgramMenuActivity.class);
		solo.clickOnText(solo.getString(R.string.sounds));
		UiTestUtils.waitForFragment(solo, R.id.fragment_sound);
		assertEquals("Current sprite name is not shown as actionbar title or is wrong", "cat", currentSprite);

		checkSettingsAndGoBack();

        solo.goBack();
        solo.waitForActivity(ProgramMenuActivity.class);
        solo.clickOnText(solo.getString(R.string.nfctags));
        UiTestUtils.waitForFragment(solo, R.id.fragment_nfctags);
        assertEquals("Current sprite name is not shown as actionbar title or is wrong", "cat", currentSprite);

        checkSettingsAndGoBack();
	}

	//regression test for issue#626; Android version < 4.2
	@Device
	public void testActionBarTitle() {
		assertTrue("Sprite name not found", solo.waitForText("cat"));
		solo.waitForView(solo.getView(R.id.brick_set_size_to_edit_text));
		solo.clickOnView(solo.getView(R.id.brick_set_size_to_edit_text));
		assertTrue("FormulaEditor title not found", solo.waitForText(solo.getString(R.string.formula_editor_title)));

		solo.goBack();
		// workaround for testdevice - Bug in Catroid-multi-job
		// for some reason the discard changes dialog appears without changing anything
		if (solo.searchText(solo.getString(R.string.formula_editor_discard_changes_dialog_title))) {
			solo.clickOnText(solo.getString(R.string.no));
		}
		solo.sleep(200);
		assertTrue("Sprite name not found", solo.waitForText("cat"));
	}

	private void checkplayProgramButton() {
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.assertCurrentActivity("Not in StageActivity", StageActivity.class);

        solo.sleep(500); //StageActivity doesn't seem to handle fast use of goBack
		solo.goBack();
        solo.sleep(500);
		solo.goBack();

		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.assertCurrentActivity("Not in ScriptActivity", ScriptActivity.class);
	}

	private void checkMainMenuButton() {
		UiTestUtils.clickOnHomeActionBarButton(solo);
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		solo.assertCurrentActivity("Main menu is not displayed", MainMenuActivity.class);
	}

	private void checkSettingsAndGoBack() {
		solo.clickOnMenuItem(solo.getString(R.string.settings), true);
		solo.assertCurrentActivity("Not in " + SettingsActivity.class.getSimpleName(), SettingsActivity.class);
		solo.goBack();
		solo.assertCurrentActivity("Not in " + ScriptActivity.class.getSimpleName(), ScriptActivity.class);
	}
}
