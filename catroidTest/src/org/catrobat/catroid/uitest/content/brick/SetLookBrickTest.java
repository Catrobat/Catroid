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
package org.catrobat.catroid.uitest.content.brick;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Spinner;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.controller.LookController;
import org.catrobat.catroid.ui.fragment.LookFragment;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.io.File;
import java.util.ArrayList;

public class SetLookBrickTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private static final int RESOURCE_LOOK = org.catrobat.catroid.test.R.raw.icon;
	private static final int RESOURCE_LOOK2 = org.catrobat.catroid.test.R.raw.icon2;

	private String lookName = "testLook1";
	private String lookName2 = "testLook2";
	private File lookFile;
	private File lookFile2;
	private ArrayList<LookData> lookDataList;
	private String testFile = "testFile";

	private File paintroidImageFile;

	public SetLookBrickTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		paintroidImageFile = UiTestUtils.createTestMediaFile(Constants.DEFAULT_ROOT + "/" + testFile + ".png",
				org.catrobat.catroid.test.R.drawable.catroid_banzai, getActivity());

		createProject();

		UiTestUtils.prepareStageForTest();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	@Override
	public void tearDown() throws Exception {
		if (lookFile.exists()) {
			lookFile.delete();
		}
		if (lookFile2.exists()) {
			lookFile2.delete();
		}
		paintroidImageFile.delete();
		super.tearDown();
	}

	public void testDismissNewLookDialog() {
		solo.clickOnText(lookName);
		solo.clickOnText(solo.getString(R.string.new_broadcast_message));
		solo.waitForDialogToOpen();
		solo.goBack();
		solo.waitForDialogToClose();

		assertEquals("Not in ScriptActivity", "ui.ScriptActivity", solo.getCurrentActivity().getLocalClassName());
		assertTrue("Spinner not updated", solo.waitForText(lookName));
	}

	public void testSelectLookAndPlay() {
		assertTrue(lookName + " is not selected in Spinner", solo.isSpinnerTextSelected(lookName));

		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);

		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(1000);
		Look look = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0).look;
		assertEquals("look not set", look.getImagePath(), lookDataList.get(0).getAbsolutePath());
		solo.goBack();
		solo.goBack();
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		solo.clickOnText(lookName);
		solo.clickOnText(lookName2);
		assertTrue(lookName2 + " is not selected in Spinner", solo.searchText(lookName2));

		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);

		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(1000);
		look = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0).look;
		assertEquals("look not set", look.getImagePath(), lookDataList.get(1).getAbsolutePath());
	}

	public void testSpinnerUpdatesDelete() {
		solo.clickOnText(lookName);

		assertTrue(lookName + " is not in Spinner", solo.searchText(lookName));
		assertTrue(lookName2 + " is not in Spinner", solo.searchText(lookName2));

		solo.goBack();
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.backgrounds));

		clickOnContextMenuItem(lookName, solo.getString(R.string.delete));
		solo.clickOnButton(solo.getString(R.string.yes));

		UiTestUtils.switchToFragmentInScriptActivity(solo, UiTestUtils.SCRIPTS_INDEX);

		solo.clickOnText(lookName2);

		assertFalse(lookName + " is still in Spinner", solo.searchText(lookName));
		assertTrue(lookName2 + " is not in Spinner", solo.searchText(lookName2));
	}

	public void testSpinnerUpdatesRename() {
		String newName = "nameRenamed";

		solo.clickOnText(lookName);

		assertTrue(lookName + " is not in Spinner", solo.searchText(lookName));
		assertTrue(lookName2 + " is not in Spinner", solo.searchText(lookName2));

		solo.goBack();
		solo.goBack();

		solo.clickOnText(solo.getString(R.string.backgrounds));

		clickOnContextMenuItem(lookName, solo.getString(R.string.rename));

		solo.clearEditText(0);
		solo.enterText(0, newName);
		solo.clickOnButton(solo.getString(R.string.ok));

		UiTestUtils.switchToFragmentInScriptActivity(solo, UiTestUtils.SCRIPTS_INDEX);

		solo.clickOnText(newName);

		assertTrue(newName + " is not in Spinner", solo.searchText(newName));
		assertTrue(lookName2 + " is not in Spinner", solo.searchText(lookName2));
	}

	public void testAdapterUpdateInScriptActivity() {
		String look1ImagePath = lookDataList.get(0).getAbsolutePath();
		String look2ImagePath = lookDataList.get(1).getAbsolutePath();
		assertTrue(lookName + " is not selected in Spinner", solo.isSpinnerTextSelected(lookName));

		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		String lookPath = ProjectManager.getInstance().getCurrentSprite().getLookDataList().get(0).getAbsolutePath();
		assertEquals("Wrong image shown in stage --> Problem with Adapter update in Script", look1ImagePath, lookPath);
		solo.goBack();
		solo.goBack();

		for (int i = 0; i < 5; ++i) {
			selectLook(lookName2, lookName, look2ImagePath);
			selectLook(lookName, lookName2, look1ImagePath);
		}
	}

	public void testAddNewLook() {
		String newText = solo.getString(R.string.new_broadcast_message);

		Bundle bundleForGallery = new Bundle();
		bundleForGallery.putString("filePath", paintroidImageFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockGalleryActivity.class);
		intent.putExtras(bundleForGallery);

		UiTestUtils.switchToFragmentInScriptActivity(solo, UiTestUtils.LOOKS_INDEX);
		UiTestUtils.switchToFragmentInScriptActivity(solo, UiTestUtils.SCRIPTS_INDEX);

		solo.clickOnText(lookName);
		solo.clickOnText(newText);

		ScriptActivity currentActivity = (ScriptActivity) solo.getCurrentActivity();
		solo.waitForFragmentByTag(LookFragment.TAG);

		LookFragment lookFragment = (LookFragment) currentActivity.getFragment(ScriptActivity.FRAGMENT_LOOKS);
		lookFragment.startActivityForResult(intent, LookController.REQUEST_SELECT_OR_DRAW_IMAGE);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.goBack();
		//This is needed, because the spinner is only updated, when you actually click on the dialog
		//and not using the MockActivity. This functionality is tested in testDismissNewLookDialog()
		solo.clickOnView(solo.getView(Spinner.class, 0));

		assertTrue("Testfile not added from mockActivity", solo.searchText(testFile));

		solo.goBack();
		solo.goBack();

		solo.waitForActivity(ProgramMenuActivity.class.getSimpleName());

		String programMenuActivityClass = ProgramMenuActivity.class.getSimpleName();
		assertTrue("Should be in " + programMenuActivityClass, solo.getCurrentActivity().getClass().getSimpleName()
				.equals(programMenuActivityClass));
	}

	public void selectLook(String newLook, String oldName, String lookImagePath) {
		solo.clickOnText(oldName);
		solo.clickOnText(newLook);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.sleep(5000);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(2000);
		String lookPath = ProjectManager.getInstance().getCurrentSprite().look.getImagePath();
		assertEquals("Wrong image shown in stage --> Problem with Adapter update in Script", lookImagePath, lookPath);
		solo.goBack();
		solo.goBack();
	}

	private void createProject() {
		ProjectManager projectManager = ProjectManager.getInstance();
		Project project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite firstSprite = new Sprite("cat");
		Script testScript = new StartScript();

		SetLookBrick setLookBrick = new SetLookBrick();
		testScript.addBrick(setLookBrick);

		firstSprite.addScript(testScript);
		project.addSprite(firstSprite);

		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);
		lookDataList = projectManager.getCurrentSprite().getLookDataList();

		lookFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "image.png", RESOURCE_LOOK,
				getInstrumentation().getContext(), UiTestUtils.FileTypes.IMAGE);
		LookData lookData = new LookData();
		lookData.setLookFilename(lookFile.getName());
		lookData.setLookName(lookName);

		lookFile2 = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "image2.png", RESOURCE_LOOK2,
				getInstrumentation().getContext(), UiTestUtils.FileTypes.IMAGE);
		LookData lookData2 = new LookData();
		lookData2.setLookFilename(lookFile2.getName());
		lookData2.setLookName(lookName2);

		lookDataList.add(lookData);
		lookDataList.add(lookData2);
		ProjectManager.getInstance().getFileChecksumContainer()
				.addChecksum(lookData.getChecksum(), lookData.getAbsolutePath());
		ProjectManager.getInstance().getFileChecksumContainer()
				.addChecksum(lookData2.getChecksum(), lookData2.getAbsolutePath());
	}

	private void clickOnContextMenuItem(String lookName, String menuItemName) {
		solo.clickLongOnText(lookName);
		solo.waitForText(menuItemName);
		solo.clickOnText(menuItemName);
	}
}
