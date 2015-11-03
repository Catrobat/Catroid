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
package org.catrobat.catroid.uitest.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.robotium.solo.By;
import com.robotium.solo.Solo;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.LookDataHistory;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.LookAdapter;
import org.catrobat.catroid.ui.controller.LookController;
import org.catrobat.catroid.ui.fragment.LookFragment;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class LookFragmentTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private static final String TAG = LookFragmentTest.class.getSimpleName();

	private static final int RESOURCE_IMAGE = org.catrobat.catroid.test.R.drawable.catroid_sunglasses;
	private static final int RESOURCE_IMAGE2 = org.catrobat.catroid.test.R.drawable.catroid_banzai;
	private static final int RESOURCE_IMAGE3 = org.catrobat.catroid.test.R.drawable.catroid_sunglasses_jpg;
	private static final int VISIBLE = View.VISIBLE;
	private static final int GONE = View.GONE;
	private static final int ACTION_MODE_COPY = 0;
	private static final int ACTION_MODE_DELETE = 1;
	private static final int ACTION_MODE_RENAME = 2;

	private static final int TIME_TO_WAIT = 400;

	private static final String FIRST_TEST_LOOK_NAME = "lookNameTest";
	private static final String SECOND_TEST_LOOK_NAME = "lookNameTest2";
	private static final String THIRD_TEST_LOOK_NAME = "lookNameTest3";

	private String copy;
	private String rename;
	private String renameDialogTitle;
	private String delete;

	private LookData lookData;
	private LookData lookData2;
	private LookData lookData3;

	private File imageFile;
	private File imageFile2;
	private File imageFileJpg;
	private File paintroidImageFile;

	private ArrayList<LookData> lookDataList;

	private CheckBox firstCheckBox;
	private CheckBox secondCheckBox;

	private ProjectManager projectManager;

	public LookFragmentTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.createTestProject();
		UiTestUtils.prepareStageForTest();

		projectManager = ProjectManager.getInstance();
		lookDataList = projectManager.getCurrentSprite().getLookDataList();

		imageFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "catroid_sunglasses.png",
				RESOURCE_IMAGE, getActivity(), UiTestUtils.FileTypes.IMAGE);
		imageFile2 = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "catroid_banzai.png",
				RESOURCE_IMAGE2, getActivity(), UiTestUtils.FileTypes.IMAGE);
		imageFileJpg = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "catroid_sunglasses.jpg",
				RESOURCE_IMAGE3, getActivity(), UiTestUtils.FileTypes.IMAGE);

		paintroidImageFile = UiTestUtils.createTestMediaFile(Constants.DEFAULT_ROOT + "/testFile.png",
				org.catrobat.catroid.test.R.drawable.catroid_banzai, getActivity());

		lookData = new LookData();
		lookData.setLookFilename(imageFile.getName());
		lookData.setLookName(FIRST_TEST_LOOK_NAME);
		lookDataList.add(lookData);

		projectManager.getFileChecksumContainer().addChecksum(lookData.getChecksum(), lookData.getAbsolutePath());

		lookData2 = new LookData();
		lookData2.setLookFilename(imageFile2.getName());
		lookData2.setLookName(SECOND_TEST_LOOK_NAME);
		lookDataList.add(lookData2);

		projectManager.getFileChecksumContainer().addChecksum(lookData2.getChecksum(), lookData2.getAbsolutePath());

		lookData3 = new LookData();
		lookData3.setLookFilename(imageFileJpg.getName());
		lookData3.setLookName(THIRD_TEST_LOOK_NAME);

		Utils.updateScreenWidthAndHeight(solo.getCurrentActivity());
		projectManager.getCurrentProject().getXmlHeader().virtualScreenWidth = ScreenValues.SCREEN_WIDTH;
		projectManager.getCurrentProject().getXmlHeader().virtualScreenHeight = ScreenValues.SCREEN_HEIGHT;
		LookDataHistory.applyChanges(projectManager.getCurrentProject().getName());
		UiTestUtils.getIntoLooksFromMainMenu(solo, true);

		copy = solo.getString(R.string.copy);
		rename = solo.getString(R.string.rename);
		renameDialogTitle = solo.getString(R.string.rename_look_dialog);
		delete = solo.getString(R.string.delete);

		if (getLookAdapter().getShowDetails()) {
			solo.clickOnMenuItem(solo.getString(R.string.hide_details), true);
			solo.sleep(TIME_TO_WAIT);
		}
	}

	@Override
	public void tearDown() throws Exception {
		paintroidImageFile.delete();
		super.tearDown();
	}

	public void testInitialLayout() {
		assertFalse("Initially showing details", getLookAdapter().getShowDetails());
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, GONE);
	}

	public void testEmptyView() {
		TextView emptyViewHeading = (TextView) solo.getCurrentActivity().findViewById(R.id.fragment_look_text_heading);
		TextView emptyViewDescription = (TextView) solo.getCurrentActivity().findViewById(
				R.id.fragment_look_text_description);

		// The Views are gone, we can still make assumptions about them
		assertEquals("Empty View heading is not correct", solo.getString(R.string.backgrounds), emptyViewHeading
				.getText().toString());
		assertEquals("Empty View description is not correct",
				solo.getString(R.string.fragment_background_text_description), emptyViewDescription.getText()
						.toString());

		assertEquals("Empty View shown although there are items in the list!", View.GONE,
				solo.getView(android.R.id.empty).getVisibility());

		projectManager.addSprite(new Sprite("test"));
		solo.goBack();
		solo.goBack();
		solo.clickInList(2);
		solo.clickOnText(solo.getString(R.string.look));
		solo.sleep(400);

		emptyViewHeading = (TextView) solo.getCurrentActivity().findViewById(R.id.fragment_look_text_heading);
		emptyViewDescription = (TextView) solo.getCurrentActivity().findViewById(R.id.fragment_look_text_description);

		assertEquals("Empty View heading is not correct", solo.getString(R.string.looks), emptyViewHeading.getText()
				.toString());
		assertEquals("Empty View description is not correct", solo.getString(R.string.fragment_look_text_description),
				emptyViewDescription.getText().toString());

		assertEquals("Empty View not shown although there are items in the list!", View.VISIBLE,
				solo.getView(android.R.id.empty).getVisibility());
	}

	public void testAddNewLookDialog() {
		String addLookFromCameraText = solo.getString(R.string.add_look_draw_new_image);
		String addLookFromGalleryText = solo.getString(R.string.add_look_choose_image);
		String addLookFromPaintroidText = solo.getString(R.string.add_look_draw_new_image);
		String addLookFromMediaLibraryText = solo.getString(R.string.add_look_media_library);

		assertFalse("Entry to add look from camera should not be visible", solo.searchText(addLookFromCameraText));
		assertFalse("Entry to add look from gallery should not be visible", solo.searchText(addLookFromGalleryText));
		assertFalse("Entry to add look from paintroid should not be visible", solo.searchText(addLookFromPaintroidText));
		assertFalse("Entry to add look from library should not be visible", solo.searchText(addLookFromMediaLibraryText));

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);

		assertTrue("Entry to add look from camera not visible", solo.searchText(addLookFromCameraText));
		assertTrue("Entry to add look from gallery not visible", solo.searchText(addLookFromGalleryText));
		assertTrue("Entry to add look from paintroid not visible", solo.searchText(addLookFromPaintroidText));
		assertTrue("Entry to add look from library not visible", solo.searchText(addLookFromMediaLibraryText));
	}

	public void testUndoRedoSequenceDelete() {
		deleteLook(FIRST_TEST_LOOK_NAME);
		assertEquals("Look was not deleted!", 1, getCurrentLookCount());
		undo();
		assertTrue("Look was not restored!", solo.waitForText(FIRST_TEST_LOOK_NAME));
		redo();
		assertEquals("Look was not deleted again!", 1, getCurrentLookCount());

		deleteLook(SECOND_TEST_LOOK_NAME);
		assertEquals("Second Look was not deleted!", 0, getCurrentLookCount());
		undo();
		assertTrue("Look was not restored!", solo.waitForText(SECOND_TEST_LOOK_NAME));
		undo();
		assertTrue("Look was not restored!", solo.waitForText(FIRST_TEST_LOOK_NAME));
		redo();
		assertEquals("First Look was not deleted again!", 1, getCurrentLookCount());
		deleteLook(SECOND_TEST_LOOK_NAME);
		assertEquals("First Look was not deleted again!", 0, getCurrentLookCount());
		assertFalse("Redo should not be visible!", solo.getView(R.id.menu_redo).isEnabled());
	}

	public void testUndoRedoSequenceCopy() {
		copyLook(FIRST_TEST_LOOK_NAME);
		assertEquals("Look was not copied!", 3, getCurrentLookCount());
		undo();
		assertEquals("Copied Look has not been undone!", 2, getCurrentLookCount());
		redo();
		assertEquals("Look was not copied again!", 3, getCurrentLookCount());

		copyLook(SECOND_TEST_LOOK_NAME);
		assertEquals("Second Look was not copied!", 4, getCurrentLookCount());
		undo();
		assertEquals("Second Look copy was not undone!", 3, getCurrentLookCount());
		undo();
		assertEquals("First Look copy was not undone!", 2, getCurrentLookCount());
		redo();
		assertEquals("First Look was not copied again!", 3, getCurrentLookCount());
		copyLook(SECOND_TEST_LOOK_NAME);
		assertEquals("Second Look was not copied!", 4, getCurrentLookCount());
		assertFalse("Redo should not be visible!", solo.getView(R.id.menu_redo).isEnabled());
	}

	public void testUndoRedoSequenceNew() {
		String mediaLibraryText = solo.getString(R.string.add_look_media_library);
		int numberLooksBefore = ProjectManager.getInstance().getCurrentSprite().getLookDataList().size();

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.waitForText(mediaLibraryText);
		solo.clickOnText(mediaLibraryText);
		solo.waitForWebElement(By.className("program"));
		solo.clickOnWebElement(By.className("program"));
		solo.waitForFragmentByTag(LookFragment.TAG);
		solo.sleep(TIME_TO_WAIT);
		int numberLooksAfter = ProjectManager.getInstance().getCurrentSprite().getLookDataList().size();
		assertEquals("No Look was added!", numberLooksBefore + 1, numberLooksAfter);

		undo();
		assertEquals("New Look was not undone!", 2, getCurrentLookCount());
		redo();
		assertEquals("Look not added in LookDataList after redo!", 3, getCurrentLookCount());
	}

	public void testUndoRedoSequenceRename() {
		String renameNameFirst = "test1";
		String renameNameSecond = "test2";
		renameLook(FIRST_TEST_LOOK_NAME, renameNameFirst);
		assertTrue("Look was not renamed!", searchForLook(renameNameFirst));
		assertFalse("Look " + FIRST_TEST_LOOK_NAME + " should not be in list!", searchForLook(FIRST_TEST_LOOK_NAME));

		undo();
		assertTrue("Look " + FIRST_TEST_LOOK_NAME + " should be in list after undo!", searchForLook(FIRST_TEST_LOOK_NAME));
		assertFalse("Look " + renameNameFirst + " should not be in list after undo!", searchForLook(renameNameFirst));

		redo();
		assertTrue("Look was not renamed after redo!", searchForLook(renameNameFirst));
		assertFalse("Look " + FIRST_TEST_LOOK_NAME + " should not be in list after redo!", searchForLook(FIRST_TEST_LOOK_NAME));

		renameLook(SECOND_TEST_LOOK_NAME, renameNameSecond);
		assertTrue("Second Look was not renamed!", searchForLook(renameNameSecond));
		assertFalse("Look " + SECOND_TEST_LOOK_NAME + " should not be in list!", searchForLook(SECOND_TEST_LOOK_NAME));

		undo();
		assertTrue("Second Look was not undone!", searchForLook(SECOND_TEST_LOOK_NAME));
		assertFalse("Look " + renameNameSecond + " should not be in list!", searchForLook(renameNameSecond));

		undo();
		assertTrue("Look " + FIRST_TEST_LOOK_NAME + " should be in list after undo!", searchForLook(FIRST_TEST_LOOK_NAME));
		assertFalse("Look " + renameNameFirst + " should not be in list after undo!", searchForLook(renameNameFirst));

		redo();
		assertTrue("Look was not renamed after redo!", searchForLook(renameNameFirst));
		assertFalse("Look " + FIRST_TEST_LOOK_NAME + " should not be in list after redo!", searchForLook(FIRST_TEST_LOOK_NAME));

		renameLook(SECOND_TEST_LOOK_NAME, renameNameSecond);
		assertTrue("Second Look was not renamed!", searchForLook(renameNameSecond));
		assertFalse("Look " + SECOND_TEST_LOOK_NAME + " should not be in list!", searchForLook(SECOND_TEST_LOOK_NAME));
		assertFalse("Redo should not be visible!", solo.getView(R.id.menu_redo).isEnabled());
	}

	public void testUndoRedoSequenceMixedCase() {
		String copyLookNameFirst = FIRST_TEST_LOOK_NAME + "_" + solo.getString(R.string.copy_addition);
		copyLook(FIRST_TEST_LOOK_NAME);
		assertEquals("look was not copied!", 3, getCurrentLookCount());

		deleteLook(copyLookNameFirst);
		assertEquals("copied look was not deleted!", 2, getCurrentLookCount());

		undo();
		assertEquals("undo of delete copied look was not done!", 3, getCurrentLookCount());

		undo();
		assertEquals("undo of copy look was not done!", 2, getCurrentLookCount());

		redo();
		assertEquals("redo of copy look was not done!", 3, getCurrentLookCount());

		redo();
		assertEquals("redo of delete copied look was not done!", 2, getCurrentLookCount());
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.backgrounds));
	}

	public void testUndoRedoSequenceMoveDown() {
		moveLookDown(FIRST_TEST_LOOK_NAME);

		assertEquals("testUndoRedoSequenceMoveDown 1", SECOND_TEST_LOOK_NAME, getLookName(0));
		assertEquals("testUndoRedoSequenceMoveDown 2", FIRST_TEST_LOOK_NAME, getLookName(1));

		undo();

		assertEquals("testUndoRedoSequenceMoveDown 3", FIRST_TEST_LOOK_NAME, getLookName(0));
		assertEquals("testUndoRedoSequenceMoveDown 4", SECOND_TEST_LOOK_NAME, getLookName(1));

		redo();

		assertEquals("testUndoRedoSequenceMoveDown 5", SECOND_TEST_LOOK_NAME, getLookName(0));
		assertEquals("testUndoRedoSequenceMoveDown 6", FIRST_TEST_LOOK_NAME, getLookName(1));
	}

	public void testUndoRedoSequenceMoveUp() {
		moveLookUp(SECOND_TEST_LOOK_NAME);

		assertEquals("testUndoRedoSequenceMoveUp 1", SECOND_TEST_LOOK_NAME, getLookName(0));
		assertEquals("testUndoRedoSequenceMoveUp 2", FIRST_TEST_LOOK_NAME, getLookName(1));

		undo();

		assertEquals("testUndoRedoSequenceMoveUp 3", FIRST_TEST_LOOK_NAME, getLookName(0));
		assertEquals("testUndoRedoSequenceMoveUp 4", SECOND_TEST_LOOK_NAME, getLookName(1));

		redo();

		assertEquals("testUndoRedoSequenceMoveUp 5", SECOND_TEST_LOOK_NAME, getLookName(0));
		assertEquals("testUndoRedoSequenceMoveUp 6", FIRST_TEST_LOOK_NAME, getLookName(1));
	}

	public void testUndoRedoSequenceMoveToBottom() {
		moveLookToBottom(FIRST_TEST_LOOK_NAME);

		assertEquals("testUndoRedoSequenceMoveToBottom 1", SECOND_TEST_LOOK_NAME, getLookName(0));
		assertEquals("testUndoRedoSequenceMoveToBottom 2", FIRST_TEST_LOOK_NAME, getLookName(1));

		undo();

		assertEquals("testUndoRedoSequenceMoveToBottom 3", FIRST_TEST_LOOK_NAME, getLookName(0));
		assertEquals("testUndoRedoSequenceMoveToBottom 4", SECOND_TEST_LOOK_NAME, getLookName(1));

		redo();

		assertEquals("testUndoRedoSequenceMoveToBottom 5", SECOND_TEST_LOOK_NAME, getLookName(0));
		assertEquals("testUndoRedoSequenceMoveToBottom 6", FIRST_TEST_LOOK_NAME, getLookName(1));
	}

	public void testUndoRedoSequenceMoveToTop() {
		moveLookToTop(SECOND_TEST_LOOK_NAME);

		assertEquals("testUndoRedoSequenceMoveToTop 1", SECOND_TEST_LOOK_NAME, getLookName(0));
		assertEquals("testUndoRedoSequenceMoveToTop 2", FIRST_TEST_LOOK_NAME, getLookName(1));

		undo();

		assertEquals("testUndoRedoSequenceMoveToTop 3", FIRST_TEST_LOOK_NAME, getLookName(0));
		assertEquals("testUndoRedoSequenceMoveToTop 4", SECOND_TEST_LOOK_NAME, getLookName(1));

		redo();

		assertEquals("testUndoRedoSequenceMoveToTop 5", SECOND_TEST_LOOK_NAME, getLookName(0));
		assertEquals("testUndoRedoSequenceMoveToTop 6", FIRST_TEST_LOOK_NAME, getLookName(1));
	}

	public void testCorrectUpdateOfSetLookBrickOnRedoUndo() {
		SetLookBrick setLookBrick = new SetLookBrick();
		setLookBrick.setLook(lookData2);
		Script script = projectManager.getCurrentProject().getSpriteList().get(0).getScript(0);
		script.addBrick(setLookBrick);
		deleteLook(lookData2.getLookName());
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.scripts));
		solo.sleep(TIME_TO_WAIT);
		assertFalse("SetLookBrick should not set to " + lookData2.getLookName(), solo.waitForText(lookData2.getLookName()));
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.backgrounds));

		undo();
		assertTrue("SetLookBrick should set to " + lookData2.getLookName(), setLookBrick.getLook().equals(lookData2));
	}

	public void testCopyLookContextMenu() {
		String testLookName = SECOND_TEST_LOOK_NAME;

		LookAdapter adapter = getLookAdapter();
		assertNotNull("Could not get Adapter", adapter);

		int oldCount = adapter.getCount();

		clickOnContextMenuItem(testLookName, copy);
		solo.sleep(300);

		int newCount = adapter.getCount();

		if (solo.searchText(testLookName + "_" + solo.getString(R.string.copy_addition), 1, true)) {
			assertEquals("Old count was not correct", 2, oldCount);
			assertEquals("New count is not correct - copy should be added", 3, newCount);
			assertEquals("Count of the lookDataList is not correct", newCount, lookDataList.size());
		} else {
			fail("Copy look didn't work");
		}
	}

	public void testDeleteLookContextMenu() {
		Sprite firstSprite = projectManager.getCurrentProject().getSpriteList().get(0);
		LookData lookToDelete = firstSprite.getLookDataList().get(1);

		Log.d("TEST", "Look to delete: " + lookToDelete.getLookName());

		String testLookName = SECOND_TEST_LOOK_NAME;
		assertEquals("The two names should be equal", testLookName, lookToDelete.getLookName());

		LookAdapter adapter = getLookAdapter();
		assertNotNull("Could not get Adapter", adapter);

		int oldCount = adapter.getCount();

		clickOnContextMenuItem(testLookName, solo.getString(R.string.delete));
		solo.sleep(200);

		int newCount = adapter.getCount();

		assertEquals("Old count was not correct", 2, oldCount);
		assertEquals("New count is not correct - one look should be deleted", 1, newCount);
		assertEquals("Count of the lookDataList is not correct", newCount, lookDataList.size());
	}

	public void testRenameLookContextMenu() {
		String newLookName = "loOKNamEtESt1";

		renameLook(FIRST_TEST_LOOK_NAME, newLookName);
		solo.sleep(50);

		assertEquals("Look not renamed in LookDataList", newLookName, getLookName(0));
		assertTrue("Look not renamed in actual view", solo.searchText(newLookName));
	}

	public void testMoveLookUp() {
		moveLookUp(SECOND_TEST_LOOK_NAME);

		assertEquals("Look didn't move up (testMoveLookUp 1)", SECOND_TEST_LOOK_NAME, getLookName(0));
		assertEquals("Look didn't move up (testMoveLookUp 2)", FIRST_TEST_LOOK_NAME, getLookName(1));
	}

	public void testMoveLookDown() {
		moveLookDown(FIRST_TEST_LOOK_NAME);

		assertEquals("Look didn't move down (testMoveLookDown 1)", SECOND_TEST_LOOK_NAME, getLookName(0));
		assertEquals("Look didn't move down (testMoveLookDown 2)", FIRST_TEST_LOOK_NAME, getLookName(1));
	}

	public void testMoveLookToBottom() {
		moveLookToBottom(FIRST_TEST_LOOK_NAME);

		assertEquals("Look didn't move bottom (testMoveLookToBottom 1)", SECOND_TEST_LOOK_NAME, getLookName(0));
		assertEquals("Look didn't move bottom (testMoveLookToBottom 2)", FIRST_TEST_LOOK_NAME, getLookName(1));
	}

	public void testMoveLookToTop() {
		moveLookToTop(SECOND_TEST_LOOK_NAME);

		assertEquals("Look didn't move top (testMoveLookToTop 1)", SECOND_TEST_LOOK_NAME, getLookName(0));
		assertEquals("Look didn't move top (testMoveLookToTop 2)", FIRST_TEST_LOOK_NAME, getLookName(1));
	}

	public void testMoveLookUpFirstEntry() {
		moveLookUp(FIRST_TEST_LOOK_NAME);

		assertEquals("Look moved (testMoveLookUpFirstEntry 1)", FIRST_TEST_LOOK_NAME, getLookName(0));
		assertEquals("Look moved (testMoveLookUpFirstEntry 2)", SECOND_TEST_LOOK_NAME, getLookName(1));
	}

	public void testMoveLookDownLastEntry() {
		moveLookDown(SECOND_TEST_LOOK_NAME);

		assertEquals("Look moved (testMoveLookDownLastEntry 1)", FIRST_TEST_LOOK_NAME, getLookName(0));
		assertEquals("Look moved (testMoveLookDownLastEntry 2)", SECOND_TEST_LOOK_NAME, getLookName(1));
	}

	public void testMoveLookToTopFirstEntry() {
		moveLookToTop(FIRST_TEST_LOOK_NAME);

		assertEquals("Look moved (testMoveLookToTopFirstEntry 1)", FIRST_TEST_LOOK_NAME, getLookName(0));
		assertEquals("Look moved (testMoveLookToTopFirstEntry 2)", SECOND_TEST_LOOK_NAME, getLookName(1));
	}

	public void testMoveLookToBottomLastEntry() {
		moveLookToBottom(SECOND_TEST_LOOK_NAME);

		assertEquals("Look moved (testMoveLookToBottomLastEntry 1)", FIRST_TEST_LOOK_NAME, getLookName(0));
		assertEquals("Look moved (testMoveLookToBottomLastEntry 2)", SECOND_TEST_LOOK_NAME, getLookName(1));
	}

	public void testShowAndHideDetails() {
		int timeToWait = 300;

		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, GONE);
		solo.clickOnMenuItem(solo.getString(R.string.show_details));
		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, VISIBLE, VISIBLE, GONE);

		// Test if showDetails is remembered after pressing back
		solo.goBack();
		solo.waitForActivity(ProgramMenuActivity.class.getSimpleName());
		solo.clickOnText(solo.getString(R.string.backgrounds));
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, VISIBLE, VISIBLE, GONE);

		solo.clickOnMenuItem(solo.getString(R.string.hide_details));
		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, GONE);
	}

	public void testGetImageFromMediaLibrary() {
		String mediaLibraryText = solo.getString(R.string.add_look_media_library);
		int numberLooksBefore = ProjectManager.getInstance().getCurrentSprite().getLookDataList().size();

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.waitForText(mediaLibraryText);
		solo.clickOnText(mediaLibraryText);
		solo.waitForWebElement(By.className("program"));
		solo.clickOnWebElement(By.className("program"));
		solo.waitForFragmentByTag(LookFragment.TAG);
		solo.sleep(TIME_TO_WAIT);
		int numberLooksAfter = ProjectManager.getInstance().getCurrentSprite().getLookDataList().size();
		assertEquals("No Look was added from Media Library!", numberLooksBefore + 1, numberLooksAfter);
		String newLookName = ProjectManager.getInstance().getCurrentSprite().getLookDataList().get(numberLooksBefore).getLookName();
		assertEquals("Temp File was not deleted!", false, UiTestUtils.checkTempFileFromMediaLibrary(Constants
				.TMP_LOOKS_PATH, newLookName));
		solo.sleep(TIME_TO_WAIT);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.waitForText(mediaLibraryText);
		solo.clickOnText(mediaLibraryText);
		solo.sleep(10000);
		solo.waitForWebElement(By.className("program"));
		solo.clickOnWebElement(By.className("program"));
		solo.sleep(TIME_TO_WAIT);
		solo.clickOnText(solo.getString(R.string.ok));
		solo.waitForFragmentByTag(LookFragment.TAG);
		solo.sleep(TIME_TO_WAIT);
		numberLooksAfter = ProjectManager.getInstance().getCurrentSprite().getLookDataList().size();
		assertEquals("Look was added from Media Library!", numberLooksBefore + 1, numberLooksAfter);
		newLookName = ProjectManager.getInstance().getCurrentSprite().getLookDataList().get(numberLooksBefore)
				.getLookName();
		assertEquals("Temp File was not deleted!", false, UiTestUtils.checkTempFileFromMediaLibrary(Constants
				.TMP_LOOKS_PATH, newLookName));
		solo.sleep(TIME_TO_WAIT);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.waitForText(mediaLibraryText);
		solo.clickOnText(mediaLibraryText);
		solo.waitForWebElement(By.className("program"));
		solo.clickOnWebElement(By.className("program"));
		solo.waitForDialogToOpen();
		solo.clickOnView(solo.getView(R.id.dialog_overwrite_media_radio_rename));
		UiTestUtils.enterText(solo, 0, "testMedia");
		solo.sleep(TIME_TO_WAIT);
		solo.clickOnView(solo.getView(Button.class, 3));
		solo.waitForFragmentByTag(LookFragment.TAG);
		solo.sleep(TIME_TO_WAIT);
		numberLooksAfter = ProjectManager.getInstance().getCurrentSprite().getLookDataList().size();
		assertEquals("Second Look was not added from Media Library!", numberLooksBefore + 2, numberLooksAfter);
		newLookName = ProjectManager.getInstance().getCurrentSprite().getLookDataList().get(numberLooksBefore).getLookName();
		assertEquals("Temp File was not deleted!", false, UiTestUtils.checkTempFileFromMediaLibrary(Constants.TMP_LOOKS_PATH, newLookName));
		newLookName = ProjectManager.getInstance().getCurrentSprite().getLookDataList().get(numberLooksBefore + 1).getLookName();
		assertEquals("Temp File was not deleted!", false, UiTestUtils.checkTempFileFromMediaLibrary(Constants.TMP_LOOKS_PATH, newLookName));
	}

	@Device
	public void testAddLookFromMediaLibraryWithNoInternet() {
		String mediaLibraryText = solo.getString(R.string.add_look_media_library);
		int retryCounter = 0;
		WifiManager wifiManager = (WifiManager) this.getActivity().getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(false);
		while (Utils.isNetworkAvailable(getActivity())) {
			solo.sleep(2000);
			if (retryCounter > 30) {
				break;
			}
			retryCounter++;
		}
		retryCounter = 0;
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.waitForText(mediaLibraryText);
		solo.clickOnText(mediaLibraryText);
		assertTrue("Should be in Look Fragment", solo.waitForText(FIRST_TEST_LOOK_NAME));
		wifiManager.setWifiEnabled(true);
		while (!Utils.isNetworkAvailable(getActivity())) {
			solo.sleep(2000);
			if (retryCounter > 30) {
				break;
			}
			retryCounter++;
		}
	}

	public void testGetImageFromGallery() {
		Bundle bundleForGallery = new Bundle();
		bundleForGallery.putString("filePath", paintroidImageFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockGalleryActivity.class);
		intent.putExtras(bundleForGallery);

		getLookFragment().startActivityForResult(intent, LookController.REQUEST_SELECT_OR_DRAW_IMAGE);

		solo.sleep(200);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		assertTrue("Testfile not added from mockActivity", solo.searchText("testFile"));

		String checksumPaintroidImageFile = Utils.md5Checksum(paintroidImageFile);
		assertTrue("Checksum not in checksumcontainer",
				projectManager.getFileChecksumContainer().containsChecksum(checksumPaintroidImageFile));

		lookDataList = projectManager.getCurrentSprite().getLookDataList();

		boolean isInLookDataList = false;
		for (LookData lookData : lookDataList) {
			if (lookData.getChecksum().equalsIgnoreCase(checksumPaintroidImageFile)) {
				isInLookDataList = true;
			}
		}
		assertTrue("File not added in LookDataList", isInLookDataList);
	}

	public void testGetImageFromGalleryNullData() {
		int numberOfLookDatasBeforeIntent = lookDataList.size();

		Bundle bundleForGallery = new Bundle();
		bundleForGallery.putString("filePath", paintroidImageFile.getAbsolutePath());
		bundleForGallery.putBoolean("returnNullData", true);
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockGalleryActivity.class);
		intent.putExtras(bundleForGallery);

		getLookFragment().startActivityForResult(intent, LookController.REQUEST_SELECT_OR_DRAW_IMAGE);

		solo.sleep(2000);
		solo.waitForActivity(ScriptActivity.class.getSimpleName(), 2000);
		solo.assertCurrentActivity("Should be in ScriptActivity", ScriptActivity.class.getSimpleName());

		lookDataList = projectManager.getCurrentSprite().getLookDataList();
		int numberOfLookDatasAfterReturning = lookDataList.size();
		assertEquals("Wrong size of lookDataList", numberOfLookDatasBeforeIntent, numberOfLookDatasAfterReturning);
	}

	public void testGetImageFromPaintroid() {
		String md5ChecksumPaintroidImageFile = Utils.md5Checksum(paintroidImageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT, paintroidImageFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);

		getLookFragment().startActivityForResult(intent, LookController.REQUEST_SELECT_OR_DRAW_IMAGE);
		solo.sleep(200);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		assertTrue("Testfile not added from mockActivity", solo.searchText("testFile"));
		assertTrue("Checksum not in checksumcontainer",
				projectManager.getFileChecksumContainer().containsChecksum(md5ChecksumPaintroidImageFile));

		lookDataList = projectManager.getCurrentSprite().getLookDataList();

		boolean isInLookDataList = false;
		for (LookData lookData : lookDataList) {
			if (lookData.getChecksum().equalsIgnoreCase(md5ChecksumPaintroidImageFile)) {
				isInLookDataList = true;
			}
		}
		assertTrue("File not added in LookDataList", isInLookDataList);
	}

	public void testGetImageFromPaintroidNoPath() {
		String md5ChecksumImageFileBeforeIntent = Utils.md5Checksum(imageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString("thirdExtra", "doesn't matter");
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);

		getLookFragment().startActivityForResult(intent, LookController.REQUEST_SELECT_OR_DRAW_IMAGE);
		solo.sleep(200);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		lookDataList = projectManager.getCurrentSprite().getLookDataList();
		LookData lookData = lookDataList.get(0);
		String md5ChecksumImageFileAfterIntent = Utils.md5Checksum(new File(lookData.getAbsolutePath()));

		assertEquals("Wrong size of lookDataList", 2, lookDataList.size());
		assertEquals("Picture changed", md5ChecksumImageFileBeforeIntent, md5ChecksumImageFileAfterIntent);
	}

	public void testPaintroidNotInstalledDialog() {

		Reflection.setPrivateField(Constants.class, "POCKET_PAINT_PACKAGE_NAME", "destroy.intent");
		Reflection.setPrivateField(Constants.class, "POCKET_PAINT_INTENT_ACTIVITY_NAME", "for.science");

		solo.clickOnView(solo.getView(R.id.look_main_layout));
		assertTrue("Paintroid not installed dialog missing after click on look", solo.searchText(solo.getString(R.string.pocket_paint_not_installed)));
		solo.clickOnButton(solo.getString(R.string.no));
	}

	public void testEditInPaintroidNotInContextMenu() {
		solo.clickLongOnText(FIRST_TEST_LOOK_NAME);
		assertFalse("\'Edit in Pocket Paint\' is still visible in context menu",
				solo.searchText("Edit in Pocket Paint"));
	}

	public void testEditInPaintroidNotInOverflowMenu() {
		solo.sendKey(Solo.MENU);
		assertFalse("\'Edit in Pocket Paint\' is still visible in overflow menu",
				solo.searchText("Edit in Pocket Paint"));
	}

	public void testEditImageWithPaintroid() {
		LookData lookData = lookDataList.get(0);
		getLookFragment().setSelectedLookData(lookData);

		String md5ChecksumPaintroidImageFile = Utils.md5Checksum(paintroidImageFile);
		String md5ChecksumImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT, imageFile.getAbsolutePath());
		bundleForPaintroid.putString("secondExtra", paintroidImageFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);

		getLookFragment().startActivityForResult(intent, LookController.REQUEST_POCKET_PAINT_EDIT_IMAGE);

		solo.sleep(5000);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		lookDataList = projectManager.getCurrentSprite().getLookDataList();
		lookData = lookDataList.get(0);

		assertNotSame("Picture did not change", Utils.md5Checksum(new File(lookData.getAbsolutePath())),
				md5ChecksumPaintroidImageFile);

		boolean isInLookDataListPaintroidImage = false;
		boolean isInLookDataListSunnglasses = false;
		for (LookData lookDatas : lookDataList) {
			if (lookDatas.getChecksum().equalsIgnoreCase(md5ChecksumPaintroidImageFile)) {
				isInLookDataListPaintroidImage = true;
			}
			if (lookDatas.getChecksum().equalsIgnoreCase(md5ChecksumImageFile)) {
				isInLookDataListSunnglasses = true;
			}
		}
		assertTrue("File not added in LookDataList", isInLookDataListPaintroidImage);
		assertFalse("File not deleted from LookDataList", isInLookDataListSunnglasses);
	}

	public void testEditCopiedImageInPaintroid() {

		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		getLookFragment().sendPocketPaintIntent(0, intent);

		solo.sleep(200);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.sleep(200);
		assertNotNull("there must be an Intent", getLookFragment().lastRecivedIntent);
		Bundle bundle = getLookFragment().lastRecivedIntent.getExtras();
		String pathOfPocketPaintImage = bundle.getString(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT);
		assertEquals("Image must by a temp copy", Constants.TMP_IMAGE_PATH, pathOfPocketPaintImage);
	}

	public void testPaintroidImagefileExtension() {
		String lookDataModifiedHash = lookData3.getLookFileName();
		lookDataModifiedHash = "THIS_IS_A_MODIFIED_HASH_AND_HERE_ARE_SOME_DUMMIE_CHARS";
		lookData3.setLookFilename(lookDataModifiedHash);

		getLookFragment().setSelectedLookData(lookData3);

		//Is needed because the image is stored in project files but has no corresponding look so it gets deleted
		//by LookDataHistory.applyChanges
		imageFileJpg = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "catroid_sunglasses.jpg",
				RESOURCE_IMAGE3, getActivity(), UiTestUtils.FileTypes.IMAGE);
		solo.sleep(TIME_TO_WAIT);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT, imageFileJpg.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);

		getLookFragment().startActivityForResult(intent, LookController.REQUEST_POCKET_PAINT_EDIT_IMAGE);

		solo.sleep(500);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		assertTrue("Copied file does not have correct fileextension", lookData3.getAbsolutePath().endsWith(".png"));
	}

	public void testEditImageWithPaintroidNoChanges() {
		int oldNumberOfLookDatas = lookDataList.size();

		LookData lookData = lookDataList.get(0);
		getLookFragment().setSelectedLookData(lookData);
		String md5ChecksumImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT, imageFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);

		getLookFragment().startActivityForResult(intent, LookController.REQUEST_POCKET_PAINT_EDIT_IMAGE);

		solo.sleep(200);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		lookDataList = projectManager.getCurrentSprite().getLookDataList();
		lookData = lookDataList.get(0);

		assertEquals("Picture did change, although it shouldn't change",
				Utils.md5Checksum(new File(lookData.getAbsolutePath())), md5ChecksumImageFile);

		int newNumberOfLookDatas = lookDataList.size();
		assertEquals("Size of lookDataList has changed", oldNumberOfLookDatas, newNumberOfLookDatas);
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.backgrounds));
		assertEquals("Too many references for checksum", 1,
				projectManager.getFileChecksumContainer().getUsage(md5ChecksumImageFile));
	}

	public void testEditImageWithPaintroidNoPath() {
		int oldNumberOfLookDatas = lookDataList.size();

		LookData lookData = lookDataList.get(0);
		getLookFragment().setSelectedLookData(lookData);
		String md5ChecksumImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString("thirdExtra", "doesn't matter");
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);

		getLookFragment().startActivityForResult(intent, LookController.REQUEST_POCKET_PAINT_EDIT_IMAGE);

		solo.sleep(200);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		lookDataList = projectManager.getCurrentSprite().getLookDataList();
		lookData = lookDataList.get(0);

		assertEquals("Picture changed, although it shouldn't change",
				Utils.md5Checksum(new File(lookData.getAbsolutePath())), md5ChecksumImageFile);

		int newNumberOfLookDatas = lookDataList.size();
		assertEquals("LookData was added", oldNumberOfLookDatas, newNumberOfLookDatas);
		assertEquals("Too many references for checksum", 1,
				projectManager.getFileChecksumContainer().getUsage(md5ChecksumImageFile));
	}

	public void testEditImageWithPaintroidToSomethingAlreadyUsed() throws IOException {
		int oldNumberOfLookDatas = lookDataList.size();

		LookData lookData = lookDataList.get(0);
		getLookFragment().setSelectedLookData(lookData);

		String md5ChecksumImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT, imageFile.getAbsolutePath());
		bundleForPaintroid.putString("secondExtra", imageFile2.getAbsolutePath());

		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);

		getLookFragment().startActivityForResult(intent, LookController.REQUEST_POCKET_PAINT_EDIT_IMAGE);
		solo.sleep(4000);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		lookDataList = projectManager.getCurrentSprite().getLookDataList();
		lookData = lookDataList.get(0);

		int newNumberOfLookDatas = lookDataList.size();

		assertNotSame("Picture did not change", Utils.md5Checksum(new File(lookData.getAbsolutePath())),
				md5ChecksumImageFile);
		assertEquals("LookData was added, although this shouldn't be possible", oldNumberOfLookDatas,
				newNumberOfLookDatas);
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.backgrounds));
		solo.sleep(TIME_TO_WAIT);
	}

	public void testEditImageWhichIsAlreadyUsed() {
		File tempImageFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "catroid_sunglasses2.png", RESOURCE_IMAGE, getActivity(), UiTestUtils.FileTypes.IMAGE);

		LookData lookDataToAdd = new LookData();
		lookDataToAdd.setLookFilename(tempImageFile.getName());
		lookDataToAdd.setLookName("justforthistest");

		lookDataList.add(lookDataToAdd);
		projectManager.getFileChecksumContainer().addChecksum(lookDataToAdd.getChecksum(),
				lookDataToAdd.getAbsolutePath());
		solo.sleep(200);
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.backgrounds));
		solo.sleep(TIME_TO_WAIT);
		LookData lookData = lookDataList.get(0);
		getLookFragment().setSelectedLookData(lookData);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT, imageFile.getAbsolutePath());
		bundleForPaintroid.putString("secondExtra", imageFile2.getAbsolutePath());

		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);

		getLookFragment().startActivityForResult(intent, LookController.REQUEST_POCKET_PAINT_EDIT_IMAGE);
		solo.sleep(4000);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.backgrounds));
		solo.sleep(TIME_TO_WAIT);
		assertEquals("Wrong number of lookDatas", 3, lookDataList.size());
		assertTrue("New added image has been deleted", tempImageFile.exists());
	}

	public void testEqualLookNames() {
		final String assertMessageText = "Look not renamed correctly";

		String defaultLookName = solo.getString(R.string.default_look_name);
		String newLookName = defaultLookName;
		String copyAdditionString = solo.getString(R.string.copy_addition);

		clickOnContextMenuItem(FIRST_TEST_LOOK_NAME, copy);

		renameLook(FIRST_TEST_LOOK_NAME, defaultLookName);
		solo.sleep(200);
		renameLook(SECOND_TEST_LOOK_NAME, defaultLookName);
		solo.sleep(200);

		String expectedLookName = defaultLookName + "1";
		assertEquals(assertMessageText, expectedLookName, getLookName(1));

		String copiedLookName = FIRST_TEST_LOOK_NAME + "_" + copyAdditionString;
		renameLook(copiedLookName, defaultLookName);
		solo.sleep(200);

		expectedLookName = defaultLookName + "2";
		assertEquals(assertMessageText, expectedLookName, getLookName(2));

		expectedLookName = defaultLookName + "1";
		newLookName = "x";
		renameLook(expectedLookName, newLookName);
		solo.sleep(200);

		solo.scrollToTop();
		clickOnContextMenuItem(newLookName, copy);

		copiedLookName = newLookName + "_" + copyAdditionString;
		renameLook(copiedLookName, defaultLookName);
		solo.sleep(200);

		assertEquals(assertMessageText, expectedLookName, getLookName(3));

		// Test that Image from Paintroid is correctly renamed
		String fileName = defaultLookName;
		try {
			imageFile = UiTestUtils.createTestMediaFile(Utils.buildPath(Constants.DEFAULT_ROOT, fileName + ".png"),
					RESOURCE_IMAGE2, getActivity());
		} catch (IOException e) {
			Log.e(TAG, "Image was not created", e);
			fail("Image was not created");
		}
		String md5ChecksumImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT, imageFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);

		getLookFragment().startActivityForResult(intent, LookController.REQUEST_SELECT_OR_DRAW_IMAGE);

		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.sleep(5000);

		expectedLookName = defaultLookName + "3";
		assertEquals(assertMessageText, expectedLookName, getLookName(4));
		assertTrue("Checksum not in checksumcontainer",
				projectManager.getFileChecksumContainer().containsChecksum(md5ChecksumImageFile));

		// Test that Image from gallery is correctly renamed
		fileName = defaultLookName;
		try {
			imageFile = UiTestUtils.createTestMediaFile(Utils.buildPath(Constants.DEFAULT_ROOT, fileName + ".png"),
					RESOURCE_IMAGE, getActivity());
		} catch (IOException e) {
			Log.e(TAG, "Image was not created", e);
			fail("Image was not created");
		}
		md5ChecksumImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForGallery = new Bundle();
		bundleForGallery.putString("filePath", imageFile.getAbsolutePath());
		intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockGalleryActivity.class);
		intent.putExtras(bundleForGallery);

		getLookFragment().startActivityForResult(intent, LookController.REQUEST_SELECT_OR_DRAW_IMAGE);

		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.sleep(5000);

		expectedLookName = defaultLookName + "4";
		assertEquals(assertMessageText, expectedLookName, getLookName(5));
		assertTrue("Checksum not in checksumcontainer",
				projectManager.getFileChecksumContainer().containsChecksum(md5ChecksumImageFile));
	}

	public void testBottomBarAndContextMenuOnActionModes() {
		if (!getLookAdapter().getShowDetails()) {
			solo.clickOnMenuItem(solo.getString(R.string.show_details), true);
			solo.sleep(TIME_TO_WAIT);
		}

		LinearLayout bottomBarLayout = (LinearLayout) solo.getView(R.id.bottom_bar);
		ImageButton addButton = (ImageButton) bottomBarLayout.findViewById(R.id.button_add);
		ImageButton playButton = (ImageButton) bottomBarLayout.findViewById(R.id.button_play);

		int timeToWait = 300;
		String addDialogTitle = solo.getString(R.string.new_look_dialog_title);
		String lookResoltionPrefixText = solo.getString(R.string.look_measure);

		assertTrue("Measures prefix not visible", solo.searchText(lookResoltionPrefixText, true));

		checkIfContextMenuAppears(true, ACTION_MODE_RENAME);

		// Test on rename ActionMode
		UiTestUtils.openActionMode(solo, rename, 0, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.waitForText(rename, 1, timeToWait, false, true);

		checkIfContextMenuAppears(false, ACTION_MODE_RENAME);

		solo.clickOnView(addButton);
		assertFalse("Add dialog should not appear", solo.waitForText(addDialogTitle, 0, timeToWait, false, true));

		solo.clickOnView(playButton);
		assertFalse("Should not start playing program",
				solo.waitForActivity(StageActivity.class.getSimpleName(), timeToWait));

		solo.goBack();
		solo.sleep(500);

		checkIfContextMenuAppears(true, ACTION_MODE_RENAME);

		assertTrue("Resolution prefix not visible after ActionMode", solo.searchText(lookResoltionPrefixText, true));

		// Test on delete ActionMode
		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.waitForText(delete, 1, timeToWait, false, true);

		checkIfContextMenuAppears(false, ACTION_MODE_DELETE);

		solo.clickOnView(addButton);
		assertFalse("Add dialog should not appear", solo.waitForText(addDialogTitle, 0, timeToWait, false, true));

		solo.clickOnView(playButton);
		assertFalse("Should not start playing program",
				solo.waitForActivity(StageActivity.class.getSimpleName(), timeToWait));

		solo.goBack();
		solo.sleep(500);

		checkIfContextMenuAppears(true, ACTION_MODE_DELETE);

		assertTrue("Resolution prefix not visible after ActionMode", solo.searchText(lookResoltionPrefixText, true));

		// Test on copy ActionMode
		UiTestUtils.openActionMode(solo, copy, R.id.copy, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.waitForText(copy, 1, timeToWait, false, true);

		checkIfContextMenuAppears(false, ACTION_MODE_COPY);

		solo.clickOnView(addButton);
		assertFalse("Add dialog should not appear", solo.waitForText(addDialogTitle, 0, timeToWait, false, true));

		solo.clickOnView(playButton);
		assertFalse("Should not start playing program",
				solo.waitForActivity(StageActivity.class.getSimpleName(), timeToWait));

		solo.goBack();
		solo.sleep(500);

		checkIfContextMenuAppears(true, ACTION_MODE_COPY);

		assertTrue("Resolution prefix not visible after ActionMode", solo.searchText(lookResoltionPrefixText, true));
	}

	public void testRenameActionModeChecking() {
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, GONE);
		UiTestUtils.openActionMode(solo, rename, 0, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		// Check if checkboxes are visible
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, VISIBLE);

		checkIfCheckboxesAreCorrectlyChecked(false, false);
		solo.clickOnCheckBox(0);
		checkIfCheckboxesAreCorrectlyChecked(true, false);

		// Check if only single-selection is possible
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, true);

		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, false);
	}

	public void testRenameActionModeIfNothingSelected() {
		UiTestUtils.openActionMode(solo, rename, 0, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		// Check if rename ActionMode disappears if nothing was selected
		checkIfCheckboxesAreCorrectlyChecked(false, false);
		UiTestUtils.acceptAndCloseActionMode(solo);
		assertFalse("Rename dialog showed up", solo.waitForText(renameDialogTitle, 0, TIME_TO_WAIT));
		assertFalse("ActionMode didn't disappear", solo.waitForText(rename, 0, TIME_TO_WAIT));
	}

	public void testRenameActionModeIfSomethingSelectedAndPressingBack() {
		UiTestUtils.openActionMode(solo, rename, 0, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, true);
		solo.goBack();

		// Check if rename ActionMode disappears if back was pressed
		assertFalse("Rename dialog showed up", solo.waitForText(renameDialogTitle, 0, TIME_TO_WAIT));
		assertFalse("ActionMode didn't disappear", solo.waitForText(rename, 0, TIME_TO_WAIT));
	}

	public void testRenameActionModeEqualLookNames() {
		UiTestUtils.openActionMode(solo, rename, 0, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		int checkboxIndex = 1;

		// Rename second look to the name of the first
		String newLookName = FIRST_TEST_LOOK_NAME;

		solo.clickOnCheckBox(checkboxIndex);
		checkIfCheckboxesAreCorrectlyChecked(false, true);
		UiTestUtils.acceptAndCloseActionMode(solo);

		solo.waitForText(renameDialogTitle);
		assertTrue("Rename dialog didn't show up", solo.searchText(renameDialogTitle, true));
		assertTrue("No EditText with actual look name", solo.searchEditText(SECOND_TEST_LOOK_NAME));

		UiTestUtils.enterText(solo, 0, newLookName);
		solo.sendKey(Solo.ENTER);

		// If an already existing name was entered a counter should be appended
		String expectedNewLookName = newLookName + "1";
		solo.sleep(300);
		lookDataList = projectManager.getCurrentSprite().getLookDataList();
		assertEquals("Look is not correctly renamed in lookDataList (1 should be appended)", expectedNewLookName,
				lookDataList.get(checkboxIndex).getLookName());
		assertTrue("Look not renamed in actual view", solo.searchText(expectedNewLookName, true));
	}

	public void testDeleteActionModeCheckingAndTitle() {
		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		int timeToWaitForTitle = 300;

		String look = solo.getString(R.string.look);
		String looks = solo.getString(R.string.looks);

		assertFalse("Look should not be displayed in title", solo.waitForText(look, 3, 300, false, true));

		// Check if checkboxes are visible
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, VISIBLE);

		checkIfCheckboxesAreCorrectlyChecked(false, false);

		int expectedNumberOfSelectedLooks = 1;
		String expectedTitle = delete + " " + expectedNumberOfSelectedLooks + " " + look;

		solo.clickOnCheckBox(0);
		checkIfCheckboxesAreCorrectlyChecked(true, false);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedNumberOfSelectedLooks = 2;
		expectedTitle = delete + " " + expectedNumberOfSelectedLooks + " " + looks;

		// Check if multiple-selection is possible
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(true, true);
		assertTrue("Title not as aspected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedNumberOfSelectedLooks = 1;
		expectedTitle = delete + " " + expectedNumberOfSelectedLooks + " " + look;

		solo.clickOnCheckBox(0);
		checkIfCheckboxesAreCorrectlyChecked(false, true);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedTitle = delete;

		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, false);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));
	}

	public void testDeleteActionModeIfNothingSelected() {
		int expectedNumberOfLooks = lookDataList.size();

		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		// Check if delete ActionMode disappears if nothing was selected
		checkIfCheckboxesAreCorrectlyChecked(false, false);
		UiTestUtils.acceptAndCloseActionMode(solo);
		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 0, TIME_TO_WAIT));

		checkIfNumberOfLooksIsEqual(expectedNumberOfLooks);
	}

	public void testDeleteActionModeIfSomethingSelectedAndPressingBack() {
		int expectedNumberOfLooks = lookDataList.size();

		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(0);
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(true, true);
		solo.goBack();

		// Check if delete ActionMode disappears if back was pressed
		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 0, TIME_TO_WAIT));

		checkIfNumberOfLooksIsEqual(expectedNumberOfLooks);
	}

	public void testDeleteActionMode() {
		int currentNumberOfLooks = lookDataList.size();
		int expectedNumberOfLooks = currentNumberOfLooks - 1;

		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, true);

		UiTestUtils.acceptAndCloseActionMode(solo);
		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 0, TIME_TO_WAIT));

		checkIfNumberOfLooksIsEqual(expectedNumberOfLooks);

		assertTrue("Unselected look '" + FIRST_TEST_LOOK_NAME + "' has been deleted!", lookDataList.contains(lookData));

		assertFalse("Selected look '" + SECOND_TEST_LOOK_NAME + "' was not deleted!", lookDataList.contains(lookData2));

		assertFalse("Look '" + SECOND_TEST_LOOK_NAME + "' has been deleted but is still showing!",
				solo.waitForText(SECOND_TEST_LOOK_NAME, 0, 200, false, false));
	}

	public void testDeleteSelectAll() {
		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());
		solo.waitForActivity("ScriptActivity");
		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());
		UiTestUtils.clickOnText(solo, selectAll);

		for (CheckBox checkBox : solo.getCurrentViews(CheckBox.class)) {
			assertTrue("CheckBox is not Checked!", checkBox.isChecked());
		}
		assertFalse("Select All is still shown", solo.waitForText(selectAll, 1, 200, false, true));

		UiTestUtils.acceptAndCloseActionMode(solo);

		assertFalse("Look was not Deleted!", solo.waitForText(FIRST_TEST_LOOK_NAME, 1, 200));
		assertFalse("Look was not Deleted!", solo.waitForText(SECOND_TEST_LOOK_NAME, 1, 200));
	}

	public void testItemClick() {
		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());
		solo.waitForActivity("ScriptActivity");
		solo.clickInList(2);

		solo.waitForView(CheckBox.class);
		ArrayList<CheckBox> checkBoxList = solo.getCurrentViews(CheckBox.class);
		assertTrue("CheckBox not checked", checkBoxList.get(1).isChecked());

		UiTestUtils.acceptAndCloseActionMode(solo);

		assertFalse("Look not deleted", solo.waitForText(SECOND_TEST_LOOK_NAME, 0, 200));
	}

	public void testDeleteAndCopyActionMode() {
		UiTestUtils.openActionMode(solo, copy, R.id.copy, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, VISIBLE);

		solo.clickOnCheckBox(0);
		solo.clickOnCheckBox(1);

		UiTestUtils.acceptAndCloseActionMode(solo);
		assertFalse("ActionMode didn't disappear", solo.waitForText(copy, 0, TIME_TO_WAIT));

		solo.sleep(300);
		clickOnContextMenuItem(FIRST_TEST_LOOK_NAME, copy);
		solo.sleep(300);

		lookDataList = projectManager.getCurrentSprite().getLookDataList();

		int currentNumberOfLooks = lookDataList.size();
		assertEquals("Wrong number of looks", 5, currentNumberOfLooks);

		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		int[] checkboxIndicesToCheck = { solo.getCurrentViews(CheckBox.class).size() - 1, 0, 2 };
		int expectedNumberOfLooks = currentNumberOfLooks - checkboxIndicesToCheck.length;

		solo.scrollDown();
		solo.clickOnCheckBox(checkboxIndicesToCheck[0]);
		// Note: We don't actually click the first checkbox on lower resolution devices because
		//       solo won't perform, any sort of scrolling after a checkBox-click at the moment.
		//       But we delete 3 sounds anyways, so the test succeeds.
		solo.scrollToTop();
		solo.clickOnCheckBox(checkboxIndicesToCheck[1]);
		solo.clickOnCheckBox(checkboxIndicesToCheck[2]);

		UiTestUtils.acceptAndCloseActionMode(solo);
		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 0, TIME_TO_WAIT));

		checkIfNumberOfLooksIsEqual(expectedNumberOfLooks);
	}

	public void testLongClickCancelDeleteAndCopy() {
		assertFalse("Look is selected!", UiTestUtils.getContextMenuAndGoBackToCheckIfSelected(solo, getActivity(),
				R.id.delete, delete, FIRST_TEST_LOOK_NAME));
		solo.goBack();
		assertFalse("Look is selected!", UiTestUtils.getContextMenuAndGoBackToCheckIfSelected(solo, getActivity(),
				R.id.copy, copy, FIRST_TEST_LOOK_NAME));
	}

	public void testCopyActionModeCheckingAndTitle() {
		UiTestUtils.openActionMode(solo, copy, R.id.copy, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		int timeToWaitForTitle = 300;

		String look = solo.getString(R.string.look);
		String looks = solo.getString(R.string.looks);

		assertFalse("Look should not be displayed in title", solo.waitForText(look, 3, 300, false, true));

		// Check if checkboxes are visible
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, VISIBLE);

		checkIfCheckboxesAreCorrectlyChecked(false, false);

		int expectedNumberOfSelectedLooks = 1;
		String expectedTitle = copy + " " + expectedNumberOfSelectedLooks + " " + look;

		solo.clickOnCheckBox(0);
		checkIfCheckboxesAreCorrectlyChecked(true, false);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedNumberOfSelectedLooks = 2;
		expectedTitle = copy + " " + expectedNumberOfSelectedLooks + " " + looks;

		// Check if multiple-selection is possible
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(true, true);
		assertTrue("Title not as aspected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedNumberOfSelectedLooks = 1;
		expectedTitle = copy + " " + expectedNumberOfSelectedLooks + " " + look;

		solo.clickOnCheckBox(0);
		checkIfCheckboxesAreCorrectlyChecked(false, true);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedTitle = copy;

		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, false);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));
	}

	public void testCopyActionModeIfNothingSelected() {
		int expectedNumberOfLooks = lookDataList.size();

		UiTestUtils.openActionMode(solo, copy, R.id.copy, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		// Check if copy ActionMode disappears if nothing was selected
		checkIfCheckboxesAreCorrectlyChecked(false, false);
		UiTestUtils.acceptAndCloseActionMode(solo);
		assertFalse("ActionMode didn't disappear", solo.waitForText(copy, 0, TIME_TO_WAIT));

		checkIfNumberOfLooksIsEqual(expectedNumberOfLooks);
	}

	public void testCopyActionModeIfSomethingSelectedAndPressingBack() {
		int expectedNumberOfLooks = lookDataList.size();

		UiTestUtils.openActionMode(solo, copy, R.id.copy, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(0);
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(true, true);
		solo.goBack();

		// Check if copy ActionMode disappears if back was pressed
		assertFalse("ActionMode didn't disappear", solo.waitForText(copy, 0, TIME_TO_WAIT));

		checkIfNumberOfLooksIsEqual(expectedNumberOfLooks);
	}

	public void testCopyActionMode() {
		int currentNumberOfLooks = lookDataList.size();
		int expectedNumberOfLooks = currentNumberOfLooks + 2;

		String copiedLookAddition = "_" + solo.getString(R.string.copy_addition);
		solo.sleep(500);

		UiTestUtils.openActionMode(solo, copy, R.id.copy, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(0);
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(true, true);

		UiTestUtils.acceptAndCloseActionMode(solo);
		assertFalse("ActionMode didn't disappear", solo.waitForText(copy, 0, TIME_TO_WAIT));

		solo.sleep(TIME_TO_WAIT);

		checkIfNumberOfLooksIsEqual(expectedNumberOfLooks);

		assertTrue("Selected look '" + FIRST_TEST_LOOK_NAME + "' was not copied!",
				solo.searchText(FIRST_TEST_LOOK_NAME, 4) && solo.searchText(FIRST_TEST_LOOK_NAME + copiedLookAddition));

		assertTrue(
				"Selected look '" + SECOND_TEST_LOOK_NAME + "' was not copied!",
				solo.searchText(SECOND_TEST_LOOK_NAME, 2)
						&& solo.searchText(SECOND_TEST_LOOK_NAME + copiedLookAddition)
		);
	}

	public void testCopySelectAll() {
		int currentNumberOfLooks = lookDataList.size();
		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy, getActivity());
		solo.waitForActivity("ScriptActivity");
		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());
		UiTestUtils.clickOnText(solo, selectAll);

		for (CheckBox checkBox : solo.getCurrentViews(CheckBox.class)) {
			assertTrue("CheckBox is not Checked!", checkBox.isChecked());
		}
		assertFalse("Select All is still shown", solo.waitForText(selectAll, 1, 200, false, true));

		UiTestUtils.acceptAndCloseActionMode(solo);

		checkIfNumberOfLooksIsEqual(currentNumberOfLooks * 2);
	}

	public void testResolutionWhenCroppedWithPaintroid() {
		solo.clickOnMenuItem(solo.getString(R.string.show_details));
		solo.sleep(200);
		checkVisibilityOfViews(VISIBLE, VISIBLE, VISIBLE, GONE);

		LookData lookData = lookDataList.get(0);
		getLookFragment().setSelectedLookData(lookData);

		String pathToImageFile = imageFile.getAbsolutePath();
		int[] fileResolutionBeforeCrop = lookData.getMeasure();
		int[] displayedResolutionBeforeCrop = getDisplayedMeasure();

		int sampleSize = 2;

		solo.sleep(1000);
		try {
			UiTestUtils.cropImage(pathToImageFile, sampleSize);
		} catch (FileNotFoundException e) {
			Log.e(TAG, "Image was not found", e);
			fail("Test failed because file was not found");
		}

		UiTestUtils.clickOnHomeActionBarButton(solo);
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		UiTestUtils.getIntoLooksFromMainMenu(solo, true);

		int[] fileResolutionAfterCrop = lookData.getMeasure();
		int[] displayedResolutionAfterCrop = getDisplayedMeasure();

		assertTrue("Bitmap resolution in file was not cropped",
				fileResolutionAfterCrop[0] < fileResolutionBeforeCrop[0]
						&& fileResolutionAfterCrop[1] < fileResolutionBeforeCrop[1]
		);
		assertTrue("Image resolution was not updated in look fragment",
				displayedResolutionAfterCrop[0] < displayedResolutionBeforeCrop[0]
						&& fileResolutionAfterCrop[1] < displayedResolutionBeforeCrop[1]
		);
	}

	public void testBottombarElementsVisibilty() {
		assertTrue("Bottombar is not visible", solo.getView(R.id.button_play).getVisibility() == View.VISIBLE);
		assertTrue("Add button is not visible", solo.getView(R.id.button_add).getVisibility() == View.VISIBLE);
		assertTrue("Play button is not visible", solo.getView(R.id.button_play).getVisibility() == View.VISIBLE);
		assertTrue("Bottombar separator is not visible",
				solo.getView(R.id.bottom_bar_separator).getVisibility() == View.VISIBLE);
	}

	public void testSelectAllActionModeButton() {
		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());

		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy, getActivity());
		solo.waitForActivity("ScriptActivity");
		assertTrue("Select All is not shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnText(solo, selectAll);
		assertFalse("Select All is still shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnCheckBox(solo, 0);
		assertTrue("Select All is not shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnCheckBox(solo, 1);
		assertTrue("Select All is not shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnCheckBox(solo, 0);
		UiTestUtils.clickOnCheckBox(solo, 1);
		assertFalse("Select All is still shown", solo.getView(R.id.select_all).isShown());

		solo.goBack();

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete, getActivity());
		solo.waitForActivity("ScriptActivity");
		assertTrue("Select All is not shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnText(solo, selectAll);
		assertFalse("Select All is still shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnCheckBox(solo, 0);
		assertTrue("Select All is not shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnCheckBox(solo, 1);
		assertTrue("Select All is not shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnCheckBox(solo, 0);
		UiTestUtils.clickOnCheckBox(solo, 1);
		assertFalse("Select All is still shown", solo.getView(R.id.select_all).isShown());
	}

	private int[] getDisplayedMeasure() {
		TextView measureTextView = (TextView) solo.getView(R.id.fragment_look_item_measure_text_view);
		String measureString = measureTextView.getText().toString();

		// Resolution string has form "Resolution: width x height"
		int dividingPosition = measureString.indexOf(' ', 0);

		String widthString = measureString.substring(0, dividingPosition);
		String heightString = measureString.substring(dividingPosition + 3, measureString.length());

		int width = Integer.parseInt(widthString);
		int heigth = Integer.parseInt(heightString);

		int[] measure = new int[2];
		measure[0] = width;
		measure[1] = heigth;

		return measure;
	}

	private void renameLook(String lookToRename, String newLookName) {
		clickOnContextMenuItem(lookToRename, solo.getString(R.string.rename));
		assertTrue("Wrong title of dialog", solo.searchText(renameDialogTitle));
		assertTrue("No EditText with actual look name", solo.searchEditText(lookToRename));

		UiTestUtils.enterText(solo, 0, newLookName);
		solo.sendKey(Solo.ENTER);
		solo.sleep(TIME_TO_WAIT);
	}

	private void moveLookDown(String lookToMove) {
		clickOnContextMenuItem(lookToMove, solo.getString(R.string.menu_item_move_down));
		solo.sleep(TIME_TO_WAIT);
	}

	private void moveLookUp(String lookToMove) {
		clickOnContextMenuItem(lookToMove, solo.getString(R.string.menu_item_move_up));
		solo.sleep(TIME_TO_WAIT);
	}

	private void moveLookToBottom(String lookToMove) {
		clickOnContextMenuItem(lookToMove, solo.getString(R.string.menu_item_move_to_bottom));
		solo.sleep(TIME_TO_WAIT);
	}

	private void moveLookToTop(String lookToMove) {
		clickOnContextMenuItem(lookToMove, solo.getString(R.string.menu_item_move_to_top));
		solo.sleep(TIME_TO_WAIT);
	}

	private LookFragment getLookFragment() {
		ScriptActivity activity = (ScriptActivity) solo.getCurrentActivity();
		return (LookFragment) activity.getFragment(ScriptActivity.FRAGMENT_LOOKS);
	}

	private LookAdapter getLookAdapter() {
		return (LookAdapter) getLookFragment().getListAdapter();
	}

	private void checkVisibilityOfViews(int imageVisibility, int lookNameVisibility, int lookDetailsVisibility,
			int checkBoxVisibility) {
		solo.sleep(200);
		assertTrue("Look image " + getAssertMessageAffix(imageVisibility),
				solo.getView(R.id.fragment_look_item_image_view).getVisibility() == imageVisibility);
		assertTrue("Look name " + getAssertMessageAffix(lookNameVisibility),
				solo.getView(R.id.fragment_look_item_name_text_view).getVisibility() == lookNameVisibility);
		assertTrue("Look details " + getAssertMessageAffix(lookDetailsVisibility),
				solo.getView(R.id.fragment_look_item_detail_linear_layout).getVisibility() == lookDetailsVisibility);
		assertTrue("Checkboxes " + getAssertMessageAffix(checkBoxVisibility),
				solo.getView(R.id.fragment_look_item_checkbox).getVisibility() == checkBoxVisibility);
	}

	private String getAssertMessageAffix(int visibility) {
		String assertMessageAffix = "";
		switch (visibility) {
			case View.VISIBLE:
				assertMessageAffix = "not visible";
				break;
			case View.GONE:
				assertMessageAffix = "not gone";
				break;
			default:
				break;
		}
		return assertMessageAffix;
	}

	private void clickOnContextMenuItem(String lookName, String menuItemName) {
		solo.clickLongOnText(lookName);
		solo.waitForText(menuItemName);
		solo.clickOnText(menuItemName);
	}

	private String getLookName(int lookIndex) {
		lookDataList = projectManager.getCurrentSprite().getLookDataList();
		return lookDataList.get(lookIndex).getLookName();
	}

	private void checkIfContextMenuAppears(boolean contextMenuShouldAppear, int actionModeType) {
		solo.clickLongOnText(FIRST_TEST_LOOK_NAME);

		int timeToWait = 200;
		String assertMessageAffix = "";

		if (contextMenuShouldAppear) {
			assertMessageAffix = "should appear";

			assertTrue("Context menu with title '" + FIRST_TEST_LOOK_NAME + "' " + assertMessageAffix,
					solo.waitForText(FIRST_TEST_LOOK_NAME, 1, timeToWait, false, true));
			assertTrue("Context menu item '" + copy + "' " + assertMessageAffix,
					solo.waitForText(copy, 1, timeToWait, false, true));
			assertTrue("Context menu item '" + delete + "' " + assertMessageAffix,
					solo.waitForText(delete, 1, timeToWait, false, true));
			assertTrue("Context menu item '" + rename + "' " + assertMessageAffix,
					solo.waitForText(rename, 1, timeToWait, false, true));

			solo.goBack();
		} else {
			assertMessageAffix = "should not appear";

			int minimumMatchesCopy = 1;
			int minimumMatchesDelete = 1;
			int minimumMatchesRename = 1;

			switch (actionModeType) {
				case ACTION_MODE_COPY:
					minimumMatchesCopy = 2;
					break;
				case ACTION_MODE_DELETE:
					minimumMatchesDelete = 2;
					break;
				case ACTION_MODE_RENAME:
					minimumMatchesRename = 2;
					break;
			}
			assertFalse("Context menu with title '" + FIRST_TEST_LOOK_NAME + "' " + assertMessageAffix,
					solo.waitForText(FIRST_TEST_LOOK_NAME, 3, timeToWait, false, true));
			assertFalse("Context menu item '" + copy + "' " + assertMessageAffix,
					solo.waitForText(copy, minimumMatchesCopy, timeToWait, false, true));
			assertFalse("Context menu item '" + delete + "' " + assertMessageAffix,
					solo.waitForText(delete, minimumMatchesDelete, timeToWait, false, true));
			assertFalse("Context menu item '" + rename + "' " + assertMessageAffix,
					solo.waitForText(rename, minimumMatchesRename, timeToWait, false, true));
		}
	}

	private void checkIfCheckboxesAreCorrectlyChecked(boolean firstCheckboxExpectedChecked,
			boolean secondCheckboxExpectedChecked) {
		solo.sleep(300);
		firstCheckBox = solo.getCurrentViews(CheckBox.class).get(0);
		secondCheckBox = solo.getCurrentViews(CheckBox.class).get(1);
		assertEquals("First checkbox not correctly checked", firstCheckboxExpectedChecked, firstCheckBox.isChecked());
		assertEquals("Second checkbox not correctly checked", secondCheckboxExpectedChecked, secondCheckBox.isChecked());
	}

	private void checkIfNumberOfLooksIsEqual(int expectedNumber) {
		lookDataList = projectManager.getCurrentSprite().getLookDataList();
		assertEquals("Number of looks is not as expected", expectedNumber, lookDataList.size());
	}

	private void undo() {
		solo.clickOnActionBarItem(R.id.menu_undo);
		solo.sleep(TIME_TO_WAIT);
	}

	private void redo() {
		solo.clickOnActionBarItem(R.id.menu_redo);
		solo.sleep(TIME_TO_WAIT);
	}

	private void deleteLook(String lookName) {
		clickOnContextMenuItem(lookName, solo.getString(R.string.delete));
		solo.sleep(TIME_TO_WAIT);
	}

	private void copyLook(String lookName) {
		clickOnContextMenuItem(lookName, solo.getString(R.string.copy));
		solo.sleep(TIME_TO_WAIT);
	}

	private boolean searchForLook(String lookName) {
		for (LookData lookData : ProjectManager.getInstance().getCurrentSprite().getLookDataList()) {
			if (lookData.getLookName().compareTo(lookName) == 0) {
				return true;
			}
		}
		return false;
	}

	private int getCurrentLookCount() {
		return ProjectManager.getInstance().getCurrentSprite().getLookDataList().size();
	}
}
