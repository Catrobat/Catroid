/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
import com.robotium.solo.WebElement;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.ui.BackPackActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BackPackLookAdapter;
import org.catrobat.catroid.ui.adapter.LookAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.LookController;
import org.catrobat.catroid.ui.fragment.BackPackLookFragment;
import org.catrobat.catroid.ui.fragment.LookFragment;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LookFragmentTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private static final String TAG = LookFragmentTest.class.getSimpleName();

	private static final int RESOURCE_IMAGE = org.catrobat.catroid.test.R.drawable.catroid_sunglasses;
	private static final int RESOURCE_IMAGE2 = org.catrobat.catroid.test.R.drawable.catroid_banzai;
	private static final int RESOURCE_IMAGE3 = org.catrobat.catroid.test.R.drawable.catroid_sunglasses_jpg;
	private static final int VISIBLE = View.VISIBLE;
	private static final int GONE = View.GONE;

	private static final int TIME_TO_WAIT = 400;
	private static final int TIME_TO_WAIT_BACKPACK = 1000;

	private static final String FIRST_TEST_LOOK_NAME = "catroid_sunglasses";
	private static final String SECOND_TEST_LOOK_NAME = "lookNameTest2";
	private static final String THIRD_TEST_LOOK_NAME = "lookNameTest3";
	private static final String SPRITE_NAME = "cat";
	private static final String SECOND_SPRITE_NAME = "second_sprite";
	private static String firstTestLookNamePacked;
	private static String secondTestLookNamePacked;
	private String firstTestLookNamePackedAndUnpacked;
	private String secondTestLookNamePackedAndUnpacked;
	private String copy;
	private String rename;
	private String renameDialogTitle;
	private String delete;
	private String deleteDialogTitle;

	private LookData lookData;
	private LookData lookData2;
	private LookData lookData3;

	private File imageFile;
	private File imageFile2;
	private File imageFileJpg;
	private File paintroidImageFile;

	private List<LookData> lookDataList;

	private CheckBox firstCheckBox;
	private CheckBox secondCheckBox;

	private ProjectManager projectManager;

	private String unpack;
	private String backpack;
	private String backpackAdd;
	private String backpackTitle;
	private String backpackReplaceDialogMultiple;

	public LookFragmentTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.createTestProject(UiTestUtils.PROJECTNAME1);
		UiTestUtils.createTestProject();
		UiTestUtils.prepareStageForTest();

		projectManager = ProjectManager.getInstance();

		firstTestLookNamePacked = FIRST_TEST_LOOK_NAME;
		firstTestLookNamePackedAndUnpacked = FIRST_TEST_LOOK_NAME + "1";
		secondTestLookNamePacked = SECOND_TEST_LOOK_NAME;
		secondTestLookNamePackedAndUnpacked = SECOND_TEST_LOOK_NAME + "1";

		lookDataList = projectManager.getCurrentSprite().getLookDataList();

		//Bitmap bm = BitmapFactory.decodeResource(getInstrumentation().getContext().getResources(), RESOURCE_IMAGE);

		imageFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "catroid_sunglasses.png",
				RESOURCE_IMAGE, getInstrumentation().getContext(), UiTestUtils.FileTypes.IMAGE);
		imageFile2 = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "catroid_banzai.png",
				RESOURCE_IMAGE2, getInstrumentation().getContext(), UiTestUtils.FileTypes.IMAGE);
		imageFileJpg = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "catroid_sunglasses.jpg",
				RESOURCE_IMAGE3, getInstrumentation().getContext(), UiTestUtils.FileTypes.IMAGE);

		paintroidImageFile = UiTestUtils.createTestMediaFile(Constants.DEFAULT_ROOT + "/testFile.png",
				org.catrobat.catroid.test.R.drawable.catroid_banzai, getInstrumentation().getContext());

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

		UiTestUtils.clearBackPackJson();
		UiTestUtils.getIntoLooksFromMainMenu(solo, true);

		copy = solo.getString(R.string.copy);
		rename = solo.getString(R.string.rename);
		renameDialogTitle = solo.getString(R.string.rename_look_dialog);
		delete = solo.getString(R.string.delete);
		unpack = solo.getString(R.string.unpack);
		backpack = solo.getString(R.string.backpack);
		backpackAdd = solo.getString(R.string.packing);
		backpackTitle = solo.getString(R.string.backpack_title);
		deleteDialogTitle = solo.getString(R.string.dialog_confirm_delete_look_title);
		backpackReplaceDialogMultiple = solo.getString(R.string.backpack_replace_look_multiple);

		if (getLookAdapter().getShowDetails()) {
			solo.clickOnMenuItem(solo.getString(R.string.hide_details), true);
			solo.sleep(TIME_TO_WAIT);
		}

		BackPackListManager.getInstance().clearBackPackLooks();
		StorageHandler.getInstance().clearBackPackLookDirectory();
	}

	@Override
	public void tearDown() throws Exception {
		if (paintroidImageFile != null && paintroidImageFile.exists()) {
			paintroidImageFile.delete();
		}

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

	public void testUndoRedoActionModesNoItemsSelected() {
		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());
		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.sleep(TIME_TO_WAIT);
		assertFalse("Undo should not be visible! (Delete)", solo.getView(R.id.menu_undo).isEnabled());
		UiTestUtils.openActionMode(solo, copy, R.id.copy, getActivity());
		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.sleep(TIME_TO_WAIT);
		assertFalse("Undo should not be visible! (Copy)", solo.getView(R.id.menu_undo).isEnabled());

		UiTestUtils.openActionMode(solo, rename, R.id.rename, getActivity());
		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.sleep(TIME_TO_WAIT);
		assertFalse("Undo should not be visible! (Rename)", solo.getView(R.id.menu_undo).isEnabled());
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

	public void testBackpackLookContextMenu() {
		LookAdapter adapter = getLookAdapter();
		assertNotNull("Could not get Adapter", adapter);

		packSingleItem(SECOND_TEST_LOOK_NAME, true);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);
		solo.waitForActivity(BackPackActivity.class);
		solo.waitForFragmentByTag(BackPackLookFragment.TAG);

		assertTrue("BackPack title didn't show up",
				solo.waitForText(backpackTitle, 0, TIME_TO_WAIT_BACKPACK));
		assertTrue("Look wasn't backpacked!", solo.waitForText(secondTestLookNamePacked, 0, TIME_TO_WAIT));
	}

	public void testBackpackLookDoubleContextMenu() {
		LookAdapter adapter = getLookAdapter();
		assertNotNull("Could not get Adapter", adapter);
		packSingleItem(SECOND_TEST_LOOK_NAME, true);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);
		solo.waitForActivity(BackPackActivity.class);
		solo.waitForFragmentByTag(BackPackLookFragment.TAG);

		solo.goBack();
		packSingleItem(FIRST_TEST_LOOK_NAME, false);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		assertTrue("BackPack title didn't show up",
				solo.waitForText(backpackTitle, 0, TIME_TO_WAIT_BACKPACK));
		assertTrue("Look wasn't backpacked!", solo.waitForText(firstTestLookNamePacked, 0, TIME_TO_WAIT));
		assertTrue("Look wasn't backpacked!", solo.waitForText(secondTestLookNamePacked, 0, TIME_TO_WAIT));
	}

	public void testBackPackLookSimpleUnpackingContextMenu() {
		LookAdapter adapter = getLookAdapter();
		assertNotNull("Could not get Adapter", adapter);
		packSingleItem(FIRST_TEST_LOOK_NAME, true);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		assertTrue("Look wasn't backpacked!", solo.waitForText(firstTestLookNamePacked, 0, TIME_TO_WAIT));

		clickOnContextMenuItem(firstTestLookNamePacked, unpack);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		assertTrue("Look wasn't unpacked!", solo.waitForText(firstTestLookNamePackedAndUnpacked, 0, TIME_TO_WAIT));
	}

	public void testBackPackLookSimpleUnpackingAndDelete() {
		LookAdapter adapter = getLookAdapter();
		int oldCount = adapter.getCount();

		assertNotNull("Could not get Adapter", adapter);
		packSingleItem(FIRST_TEST_LOOK_NAME, true);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		solo.goBack();
		deleteLook(FIRST_TEST_LOOK_NAME);
		solo.sleep(50);
		UiTestUtils.openBackPack(solo, getActivity());

		clickOnContextMenuItem(firstTestLookNamePacked, unpack);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		assertTrue("Look wasn't unpacked!", solo.waitForText(FIRST_TEST_LOOK_NAME, 0, TIME_TO_WAIT));

		int newCount = adapter.getCount();
		assertEquals("Counts have to be equal", oldCount, newCount);
	}

	public void testBackPackLookMultipleUnpacking() {
		LookAdapter adapter = getLookAdapter();
		int oldCount = adapter.getCount();

		assertNotNull("Could not get Adapter", adapter);
		packSingleItem(FIRST_TEST_LOOK_NAME, true);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		clickOnContextMenuItem(firstTestLookNamePacked, solo.getString(R.string.unpack));
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		solo.sleep(TIME_TO_WAIT_BACKPACK);
		assertTrue("Look wasn't unpacked!", solo.waitForText(firstTestLookNamePackedAndUnpacked, 0, TIME_TO_WAIT));
		packSingleItem(SECOND_TEST_LOOK_NAME, false);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		clickOnContextMenuItem(secondTestLookNamePacked, solo.getString(R.string.unpack));
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		solo.sleep(TIME_TO_WAIT_BACKPACK);
		assertTrue("Look wasn't unpacked!", solo.waitForText(secondTestLookNamePackedAndUnpacked, 0, TIME_TO_WAIT));
		int newCount = adapter.getCount();
		assertEquals("There are looks missing", oldCount + 2, newCount);
	}

	public void testBackPackAndUnPackFromDifferentProgrammes() {
		LookAdapter adapter = getLookAdapter();
		assertNotNull("Could not get Adapter", adapter);
		packSingleItem(FIRST_TEST_LOOK_NAME, true);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		UiTestUtils.switchToProgrammBackground(solo, UiTestUtils.PROJECTNAME1, SPRITE_NAME);
		solo.clickOnText(solo.getString(R.string.backgrounds));

		UiTestUtils.openBackPackActionModeWhenEmtpy(solo, getActivity());
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		clickOnContextMenuItem(firstTestLookNamePacked, unpack);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		assertTrue("Look wasn't unpacked!", solo.waitForText(FIRST_TEST_LOOK_NAME, 1, 3000));
	}

	public void testBackPackAndUnPackFromDifferentSprites() {
		UiTestUtils.createTestProjectWithTwoSprites(UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		lookDataList = projectManager.getCurrentSprite().getLookDataList();
		lookDataList.add(lookData);
		projectManager.getFileChecksumContainer().addChecksum(lookData.getChecksum(), lookData.getAbsolutePath());

		LookAdapter adapter = getLookAdapter();

		assertNotNull("Could not get Adapter", adapter);
		packSingleItem(FIRST_TEST_LOOK_NAME, true);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		solo.goBack();
		solo.sleep(200);
		solo.goBack();
		solo.sleep(200);
		solo.goBack();
		solo.sleep(200);

		solo.waitForText(SECOND_SPRITE_NAME, 1, 1000);
		solo.clickOnText(SECOND_SPRITE_NAME);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		solo.clickOnText(solo.getString(R.string.look));
		UiTestUtils.openBackPackActionModeWhenEmtpy(solo, getActivity());
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		clickOnContextMenuItem(firstTestLookNamePacked, unpack);
		solo.waitForDialogToClose(1000);

		assertTrue("Look wasn't unpacked!", solo.waitForText(FIRST_TEST_LOOK_NAME, 0, TIME_TO_WAIT));
	}

	public void testBackPackActionModeCheckingAndTitle() {
		UiTestUtils.openBackPackActionModeWhenEmtpy(solo, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		int timeToWaitForTitle = 300;

		String look = solo.getString(R.string.look);
		String looks = solo.getString(R.string.looks);

		assertFalse("Look should not be displayed in title", solo.waitForText(look, 3, 300, false, true));

		// Check if checkboxes are visible
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, VISIBLE);

		checkIfCheckboxesAreCorrectlyChecked(false, false);

		int expectedNumberOfSelectedLooks = 1;
		String expectedTitle = backpack + " " + expectedNumberOfSelectedLooks + " " + look;

		solo.clickOnCheckBox(0);
		checkIfCheckboxesAreCorrectlyChecked(true, false);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedNumberOfSelectedLooks = 2;
		expectedTitle = backpack + " " + expectedNumberOfSelectedLooks + " " + looks;

		// Check if multiple-selection is possible
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(true, true);
		assertTrue("Title not as aspected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedNumberOfSelectedLooks = 1;
		expectedTitle = backpack + " " + expectedNumberOfSelectedLooks + " " + look;

		solo.clickOnCheckBox(0);
		checkIfCheckboxesAreCorrectlyChecked(false, true);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedTitle = backpack;

		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, false);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));
	}

	public void testBackPackActionModeIfNothingSelected() {
		UiTestUtils.openBackPackActionModeWhenEmtpy(solo, getActivity());
		int expectedNumberOfLooks = lookDataList.size();
		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);
		checkIfCheckboxesAreCorrectlyChecked(false, false);
		UiTestUtils.acceptAndCloseActionMode(solo);
		assertFalse("ActionMode didn't disappear", solo.waitForText(backpack, 0, TIME_TO_WAIT));
		checkIfNumberOfLooksIsEqual(expectedNumberOfLooks);

		UiTestUtils.openBackPackActionModeWhenEmtpy(solo, getActivity());
		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);
		checkIfCheckboxesAreCorrectlyChecked(false, false);
		solo.goBack();
		assertFalse("ActionMode didn't disappear", solo.waitForText(backpack, 0, TIME_TO_WAIT));
		checkIfNumberOfLooksIsEqual(expectedNumberOfLooks);
	}

	public void testBackPackActionModeIfSomethingSelectedAndPressingBack() {
		UiTestUtils.openBackPackActionModeWhenEmtpy(solo, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(0);
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(true, true);
		solo.goBack();

		assertFalse("ActionMode didn't disappear", solo.waitForText(backpack, 0, TIME_TO_WAIT));
		assertFalse("Backpack was opened, but shouldn't be!", solo.waitForText(backpackTitle, 0, TIME_TO_WAIT));
	}

	public void testBackPackSelectAll() {
		UiTestUtils.openBackPackActionModeWhenEmtpy(solo, getActivity());
		solo.waitForActivity("ScriptActivity");
		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());
		UiTestUtils.clickOnText(solo, selectAll);

		checkAllCheckboxes();
		assertFalse("Select All is still shown", solo.waitForText(selectAll, 1, 200, false, true));

		UiTestUtils.acceptAndCloseActionMode(solo);
		assertTrue("Backpack didn't appear", solo.waitForText(backpackTitle));
		assertTrue("Look wasn't backpacked!", solo.waitForText(firstTestLookNamePacked, 0, TIME_TO_WAIT));
		assertTrue("Look wasn't backpacked!", solo.waitForText(secondTestLookNamePacked, 0, TIME_TO_WAIT));
	}

	public void testBackPackLookDeleteContextMenu() {
		UiTestUtils.backPackAllItems(solo, getActivity(), firstTestLookNamePacked, secondTestLookNamePacked);

		BackPackLookAdapter adapter = getBackPackLookAdapter();
		int oldCount = adapter.getCount();
		List<LookData> backPackLookDataList = BackPackListManager.getInstance().getBackPackedLooks();
		String pathOfFirstBackPackedLook = backPackLookDataList.get(0).getAbsolutePath();
		String pathOfSecondBackPackedLook = backPackLookDataList.get(1).getAbsolutePath();
		assertTrue("Backpack look file doesn't exist", UiTestUtils.fileExists(pathOfFirstBackPackedLook));
		assertTrue("Backpack look file doesn't exist", UiTestUtils.fileExists(pathOfSecondBackPackedLook));

		clickSingleItemActionMode(firstTestLookNamePacked, R.id.delete, delete);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);
		int newCount = adapter.getCount();
		solo.sleep(500);

		assertEquals("Not all looks were backpacked", 2, oldCount);
		assertEquals("Look wasn't deleted in backpack", 1, newCount);
		assertEquals("Count of the backpack lookDataList is not correct", newCount, backPackLookDataList.size());
		assertFalse("Backpack look file exists, but shouldn't", UiTestUtils.fileExists(pathOfFirstBackPackedLook));
		assertTrue("Backpack look file doesn't exist", UiTestUtils.fileExists(pathOfSecondBackPackedLook));
	}

	public void testBackPackLookDeleteActionMode() {
		UiTestUtils.backPackAllItems(solo, getActivity(), firstTestLookNamePacked, secondTestLookNamePacked);

		BackPackLookAdapter adapter = getBackPackLookAdapter();
		int oldCount = adapter.getCount();
		List<LookData> backPackLookDataList = BackPackListManager.getInstance().getBackPackedLooks();
		String pathOfFirstBackPackedLook = backPackLookDataList.get(0).getAbsolutePath();
		String pathOfSecondBackPackedLook = backPackLookDataList.get(1).getAbsolutePath();
		assertTrue("Backpack look file doesn't exist", UiTestUtils.fileExists(pathOfFirstBackPackedLook));
		assertTrue("Backpack look file doesn't exist", UiTestUtils.fileExists(pathOfSecondBackPackedLook));

		UiTestUtils.deleteAllItems(solo, getActivity());

		int newCount = adapter.getCount();
		solo.sleep(500);
		assertTrue("No backpack is emtpy text appeared", solo.searchText(backpack));
		assertTrue("No backpack is emtpy text appeared", solo.searchText(solo.getString(R.string.is_empty)));

		assertEquals("Not all looks were backpacked", 2, oldCount);
		assertEquals("Look wasn't deleted in backpack", 0, newCount);
		assertEquals("Count of the backpack lookDataList is not correct", newCount, backPackLookDataList.size());
		assertFalse("Backpack look file exists, but shouldn't", UiTestUtils.fileExists(pathOfFirstBackPackedLook));
		assertFalse("Backpack look file doesn't exist", UiTestUtils.fileExists(pathOfSecondBackPackedLook));
	}

	public void testBackPackLookActionModeDifferentProgrammes() {
		UiTestUtils.backPackAllItems(solo, getActivity(), firstTestLookNamePacked, secondTestLookNamePacked);
		UiTestUtils.switchToProgrammBackground(solo, UiTestUtils.PROJECTNAME1, SPRITE_NAME);
		solo.clickOnText(solo.getString(R.string.backgrounds));

		UiTestUtils.openBackPackActionModeWhenEmtpy(solo, getActivity());

		UiTestUtils.openActionMode(solo, unpack, R.id.unpacking, getActivity());
		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());
		UiTestUtils.clickOnText(solo, selectAll);
		UiTestUtils.acceptAndCloseActionMode(solo);

		solo.waitForActivity(ScriptActivity.class);
		assertTrue("Look wasn't unpacked!", solo.waitForText(FIRST_TEST_LOOK_NAME, 1, 1000));
		assertTrue("Look wasn't unpacked!", solo.waitForText(SECOND_TEST_LOOK_NAME, 1, 1000));
		UiTestUtils.deleteAllItems(solo, getActivity());
		assertFalse("Look wasn't deleted!", solo.waitForText(FIRST_TEST_LOOK_NAME, 1, 1000));
		assertFalse("Look wasn't deleted!", solo.waitForText(SECOND_TEST_LOOK_NAME, 1, 1000));
	}

	public void testBackPackDeleteActionModeCheckingAndTitle() {
		UiTestUtils.backPackAllItems(solo, getActivity(), firstTestLookNamePacked, secondTestLookNamePacked);
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

	public void testBackPackDeleteActionModeIfNothingSelected() {
		UiTestUtils.backPackAllItems(solo, getActivity(), firstTestLookNamePacked, secondTestLookNamePacked);
		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());

		int expectedNumberOfLooks = BackPackListManager.getInstance().getBackPackedLooks().size();
		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);
		checkIfCheckboxesAreCorrectlyChecked(false, false);
		UiTestUtils.acceptAndCloseActionMode(solo);
		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 0, TIME_TO_WAIT));
		checkIfNumberOfLooksIsEqual(expectedNumberOfLooks);

		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());
		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);
		checkIfCheckboxesAreCorrectlyChecked(false, false);
		solo.goBack();
		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 0, TIME_TO_WAIT));
		checkIfNumberOfLooksIsEqual(expectedNumberOfLooks);
	}

	public void testBackPackDeleteActionModeIfSomethingSelectedAndPressingBack() {
		UiTestUtils.backPackAllItems(solo, getActivity(), firstTestLookNamePacked, secondTestLookNamePacked);
		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(0);
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(true, true);
		solo.goBack();

		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 0, TIME_TO_WAIT));
	}

	public void testBackPackDeleteSelectAll() {
		UiTestUtils.backPackAllItems(solo, getActivity(), firstTestLookNamePacked, secondTestLookNamePacked);
		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());

		solo.waitForActivity("BackPackActivity");
		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());
		UiTestUtils.clickOnText(solo, selectAll);
		checkAllCheckboxes();
		assertFalse("Select All is still shown", solo.waitForText(selectAll, 1, 200, false, true));

		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		assertFalse("Look wasn't deleted!", solo.waitForText(firstTestLookNamePacked, 0, TIME_TO_WAIT));
		assertFalse("Look wasn't deleted!", solo.waitForText(secondTestLookNamePacked, 0, TIME_TO_WAIT));
		assertTrue("No empty bg found!", solo.waitForText(solo.getString(R.string.is_empty), 0, TIME_TO_WAIT));
	}

	public void testBackPackShowAndHideDetails() {
		UiTestUtils.backPackAllItems(solo, getActivity(), firstTestLookNamePacked, secondTestLookNamePacked);
		int timeToWait = 300;

		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, GONE);
		solo.clickOnMenuItem(solo.getString(R.string.show_details));
		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, VISIBLE, VISIBLE, GONE);

		// Test if showDetails is remembered after pressing back
		solo.goBack();
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		UiTestUtils.openBackPack(solo, getActivity());
		solo.waitForActivity(BackPackActivity.class.getSimpleName());
		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, VISIBLE, VISIBLE, GONE);

		solo.clickOnMenuItem(solo.getString(R.string.hide_details));
		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, GONE);
	}

	public void testBackPackAlreadyPackedDialogSingleItem() {
		packSingleItem(FIRST_TEST_LOOK_NAME, true);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		assertTrue("Look wasn't backpacked!", solo.waitForText(firstTestLookNamePacked, 0, TIME_TO_WAIT));
		solo.goBack();
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		packSingleItem(FIRST_TEST_LOOK_NAME, false);
		solo.waitForDialogToOpen();
		assertTrue("Look already exists backpack dialog not shown!", solo.waitForText(backpackReplaceDialogMultiple, 0, TIME_TO_WAIT));
		solo.clickOnButton(solo.getString(R.string.yes));
		solo.sleep(200);

		assertTrue("Should be in backpack!", solo.waitForText(backpackTitle, 0, TIME_TO_WAIT));
		assertTrue("Look wasn't backpacked!", solo.waitForText(firstTestLookNamePacked, 0, TIME_TO_WAIT));
		assertTrue("Look was not replaced!", BackPackListManager.getInstance().getBackPackedLooks().size() == 1);
	}

	public void testBackPackAlreadyPackedDialogMultipleItems() {
		UiTestUtils.backPackAllItems(solo, getActivity(), firstTestLookNamePacked, secondTestLookNamePacked);
		assertTrue("Look wasn't backpacked!", solo.waitForText(firstTestLookNamePacked, 0, TIME_TO_WAIT));
		solo.goBack();
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.waitForFragmentByTag(LookFragment.TAG);

		UiTestUtils.openBackPackActionMode(solo, getActivity());
		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());
		UiTestUtils.clickOnText(solo, selectAll);
		UiTestUtils.acceptAndCloseActionMode(solo);

		solo.waitForDialogToOpen();
		assertTrue("Look already exists backpack dialog not shown!", solo.waitForText(backpackReplaceDialogMultiple, 0,
				TIME_TO_WAIT));
		solo.clickOnButton(solo.getString(R.string.yes));
		solo.waitForDialogToClose();

		solo.waitForActivity(BackPackActivity.class.getSimpleName());
		solo.waitForFragmentByTag(BackPackLookFragment.TAG);
		solo.sleep(200);
		assertTrue("Should be in backpack!", solo.waitForText(backpackTitle, 0, TIME_TO_WAIT));
		assertTrue("Look wasn't backpacked!", solo.waitForText(firstTestLookNamePacked, 0, TIME_TO_WAIT));
		assertTrue("Look wasn't backpacked!", solo.waitForText(secondTestLookNamePacked, 0, TIME_TO_WAIT));
		assertTrue("Look was not replaced!", BackPackListManager.getInstance().getBackPackedLooks().size() == 2);
	}

	public void testCopyLookContextMenu() {
		String testLookName = SECOND_TEST_LOOK_NAME;

		LookAdapter adapter = getLookAdapter();
		assertNotNull("Could not get Adapter", adapter);

		int oldCount = adapter.getCount();

		clickSingleItemActionMode(testLookName, R.id.copy, copy);
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

		Log.d(TAG, "Look to delete: " + lookToDelete.getLookName());

		String testLookName = SECOND_TEST_LOOK_NAME;
		assertEquals("The two names should be equal", testLookName, lookToDelete.getLookName());

		LookAdapter adapter = getLookAdapter();
		assertNotNull("Could not get Adapter", adapter);

		int oldCount = adapter.getCount();

		clickSingleItemActionMode(testLookName, R.id.delete, delete);
		solo.waitForText(deleteDialogTitle);
		solo.clickOnButton(solo.getString(R.string.yes));
		solo.sleep(200);

		int newCount = adapter.getCount();

		assertEquals("Old count was not correct", 2, oldCount);
		assertEquals("New count is not correct - one look should be deleted", 1, newCount);
		assertEquals("Count of the lookDataList is not correct", newCount, lookDataList.size());

		Log.d(TAG, "path: " + lookToDelete.getAbsolutePath());
		File deletedFile = new File(lookToDelete.getAbsolutePath());
		assertFalse("File should be deleted", deletedFile.exists());
	}

	public void testRenameLookContextMenu() {
		String newLookName = "loOKNamEtESt1";

		renameLook(FIRST_TEST_LOOK_NAME, newLookName);
		solo.sleep(50);

		assertEquals("Look not renamed in LookDataList", newLookName, getLookName(0));
		assertTrue("Look not renamed in actual view", solo.searchText(newLookName));
	}

	public void testDragAndDropDownWithUndoRedo() {
		for (int i = 0; i < 3; i++) {
			addLookWithName("TestLook" + i);
		}

		solo.goBack();
		solo.clickOnText(solo.getString(R.string.backgrounds));

		assertEquals("Wrong List before DragAndDropTest", lookDataList.get(0).getLookName(), FIRST_TEST_LOOK_NAME);
		assertEquals("Wrong List before DragAndDropTest", lookDataList.get(1).getLookName(), SECOND_TEST_LOOK_NAME);
		assertEquals("Wrong List before DragAndDropTest", lookDataList.get(2).getLookName(), "TestLook0");
		assertEquals("Wrong List before DragAndDropTest", lookDataList.get(3).getLookName(), "TestLook1");
		assertEquals("Wrong List before DragAndDropTest", lookDataList.get(4).getLookName(), "TestLook2");

		ArrayList<Integer> yPositionList = UiTestUtils.getListItemYPositions(solo, 1);
		UiTestUtils.longClickAndDrag(solo, 10, yPositionList.get(1), 10, yPositionList.get(4) + 100, 20);

		assertEquals("Wrong List after DragAndDropTest", lookDataList.get(0).getLookName(), FIRST_TEST_LOOK_NAME);
		assertEquals("Wrong List after DragAndDropTest", lookDataList.get(1).getLookName(), "TestLook0");
		assertEquals("Wrong List after DragAndDropTest", lookDataList.get(2).getLookName(), "TestLook1");
		assertEquals("Wrong List after DragAndDropTest", lookDataList.get(3).getLookName(), SECOND_TEST_LOOK_NAME);
		assertEquals("Wrong List after DragAndDropTest", lookDataList.get(4).getLookName(), "TestLook2");

		undo();

		assertEquals("Wrong List before DragAndDropTest", lookDataList.get(0).getLookName(), FIRST_TEST_LOOK_NAME);
		assertEquals("Wrong List before DragAndDropTest", lookDataList.get(1).getLookName(), SECOND_TEST_LOOK_NAME);
		assertEquals("Wrong List before DragAndDropTest", lookDataList.get(2).getLookName(), "TestLook0");
		assertEquals("Wrong List before DragAndDropTest", lookDataList.get(3).getLookName(), "TestLook1");
		assertEquals("Wrong List before DragAndDropTest", lookDataList.get(4).getLookName(), "TestLook2");

		redo();

		assertEquals("Wrong List after DragAndDropTest", lookDataList.get(0).getLookName(), FIRST_TEST_LOOK_NAME);
		assertEquals("Wrong List after DragAndDropTest", lookDataList.get(1).getLookName(), "TestLook0");
		assertEquals("Wrong List after DragAndDropTest", lookDataList.get(2).getLookName(), "TestLook1");
		assertEquals("Wrong List after DragAndDropTest", lookDataList.get(3).getLookName(), SECOND_TEST_LOOK_NAME);
		assertEquals("Wrong List after DragAndDropTest", lookDataList.get(4).getLookName(), "TestLook2");
	}

	public void testDragAndDropUpWithUndoRedo() {
		for (int i = 0; i < 3; i++) {
			addLookWithName("TestLook" + i);
		}

		solo.goBack();
		solo.clickOnText(solo.getString(R.string.backgrounds));

		assertEquals("Wrong List before DragAndDropTest", lookDataList.get(0).getLookName(), FIRST_TEST_LOOK_NAME);
		assertEquals("Wrong List before DragAndDropTest", lookDataList.get(1).getLookName(), SECOND_TEST_LOOK_NAME);
		assertEquals("Wrong List before DragAndDropTest", lookDataList.get(2).getLookName(), "TestLook0");
		assertEquals("Wrong List before DragAndDropTest", lookDataList.get(3).getLookName(), "TestLook1");
		assertEquals("Wrong List before DragAndDropTest", lookDataList.get(4).getLookName(), "TestLook2");

		ArrayList<Integer> yPositionList = UiTestUtils.getListItemYPositions(solo, 1);
		UiTestUtils.longClickAndDrag(solo, 10, yPositionList.get(4), 10, yPositionList.get(1) - 100, 20);

		assertEquals("Wrong List after DragAndDropTest", lookDataList.get(0).getLookName(), FIRST_TEST_LOOK_NAME);
		assertEquals("Wrong List after DragAndDropTest", lookDataList.get(1).getLookName(), SECOND_TEST_LOOK_NAME);
		assertEquals("Wrong List after DragAndDropTest", lookDataList.get(2).getLookName(), "TestLook2");
		assertEquals("Wrong List after DragAndDropTest", lookDataList.get(3).getLookName(), "TestLook0");
		assertEquals("Wrong List after DragAndDropTest", lookDataList.get(4).getLookName(), "TestLook1");

		undo();

		assertEquals("Wrong List before DragAndDropTest", lookDataList.get(0).getLookName(), FIRST_TEST_LOOK_NAME);
		assertEquals("Wrong List before DragAndDropTest", lookDataList.get(1).getLookName(), SECOND_TEST_LOOK_NAME);
		assertEquals("Wrong List before DragAndDropTest", lookDataList.get(2).getLookName(), "TestLook0");
		assertEquals("Wrong List before DragAndDropTest", lookDataList.get(3).getLookName(), "TestLook1");
		assertEquals("Wrong List before DragAndDropTest", lookDataList.get(4).getLookName(), "TestLook2");

		redo();

		assertEquals("Wrong List after DragAndDropTest", lookDataList.get(0).getLookName(), FIRST_TEST_LOOK_NAME);
		assertEquals("Wrong List after DragAndDropTest", lookDataList.get(1).getLookName(), SECOND_TEST_LOOK_NAME);
		assertEquals("Wrong List after DragAndDropTest", lookDataList.get(2).getLookName(), "TestLook2");
		assertEquals("Wrong List after DragAndDropTest", lookDataList.get(3).getLookName(), "TestLook0");
		assertEquals("Wrong List after DragAndDropTest", lookDataList.get(4).getLookName(), "TestLook1");
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
		solo.waitForWebElement(By.className("programs"));
		solo.sleep(200);

		ArrayList<WebElement> webElements = solo.getCurrentWebElements();
		for (WebElement webElement : webElements) {
			if (webElement.getClassName().contains("program mediafile-")) {
				solo.clickOnWebElement(webElement);
				break;
			}
		}

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
		solo.waitForWebElement(By.className("programs"));
		solo.sleep(200);

		webElements = solo.getCurrentWebElements();
		for (WebElement webElement : webElements) {
			if (webElement.getClassName().contains("program mediafile-")) {
				solo.clickOnWebElement(webElement);
				break;
			}
		}

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
		solo.waitForWebElement(By.className("programs"));
		solo.sleep(200);

		webElements = solo.getCurrentWebElements();
		for (WebElement webElement : webElements) {
			if (webElement.getClassName().contains("program mediafile-")) {
				solo.clickOnWebElement(webElement);
				break;
			}
		}

		solo.waitForDialogToOpen();
		solo.clickOnView(solo.getView(R.id.dialog_overwrite_media_radio_rename));
		UiTestUtils.enterText(solo, 0, "testMedia");
		solo.sleep(TIME_TO_WAIT);
		solo.clickOnView(solo.getView(Button.class, 3));
		solo.waitForDialogToClose();
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
		assertNotNull("there must be an Intent", getLookFragment().lastReceivedIntent);
		Bundle bundle = getLookFragment().lastReceivedIntent.getExtras();
		String pathOfPocketPaintImage = bundle.getString(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT);
		assertEquals("Image must by a temp copy", Constants.TMP_IMAGE_PATH, pathOfPocketPaintImage);
	}

	public void testPaintroidImagefileExtension() {
		String lookDataModifiedHash = lookData3.getLookFileName();
		lookDataModifiedHash = "THIS_IS_A_MODIFIED_HASH_AND_HERE_ARE_SOME_DUMMIE_CHARS";
		lookData3.setLookFilename(lookDataModifiedHash);

		getLookFragment().setSelectedLookData(lookData3);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT, imageFileJpg.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);

		getLookFragment().startActivityForResult(intent, LookController.REQUEST_POCKET_PAINT_EDIT_IMAGE);

		solo.sleep(500);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		assertTrue("Copied file does not have correct fileextension", lookData3.getLookFileName().endsWith(".png"));
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
		String md5ChecksumPaintroidImageFile = Utils.md5Checksum(paintroidImageFile);

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
		assertEquals("Too many references for checksum", 0,
				projectManager.getFileChecksumContainer().getUsage(md5ChecksumImageFile));
		assertEquals("Incorrect number of checksum references", 2,
				projectManager.getFileChecksumContainer().getUsage(md5ChecksumPaintroidImageFile));
	}

	public void testEditImageWhichIsAlreadyUsed() {
		File tempImageFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME,
				"catroid_sunglasses2.png", RESOURCE_IMAGE, getInstrumentation().getContext(), UiTestUtils.FileTypes.IMAGE);

		LookData lookDataToAdd = new LookData();
		lookDataToAdd.setLookFilename(tempImageFile.getName());
		lookDataToAdd.setLookName("justforthistest");

		lookDataList.add(lookDataToAdd);
		projectManager.getFileChecksumContainer().addChecksum(lookDataToAdd.getChecksum(),
				lookDataToAdd.getAbsolutePath());
		getLookAdapter().hardSetIdMapForTesting();
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.backgrounds));

		solo.sleep(200);

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

		assertEquals("Wrong number of lookDatas", 3, lookDataList.size());
		assertTrue("New added image has been deleted", tempImageFile.exists());
		assertEquals("Wrong number of checksum references of sunnglasses picture", 1, projectManager
				.getFileChecksumContainer().getUsage(md5ChecksumImageFile));
	}

	public void testEqualLookNames() {
		final String assertMessageText = "Look not renamed correctly";

		String defaultLookName = solo.getString(R.string.default_look_name);
		String newLookName;
		String copyAdditionString = solo.getString(R.string.copy_addition);

		clickSingleItemActionMode(FIRST_TEST_LOOK_NAME, R.id.copy, copy);

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
		clickSingleItemActionMode(newLookName, R.id.copy, copy);

		copiedLookName = newLookName + "_" + copyAdditionString;
		renameLook(copiedLookName, defaultLookName);
		solo.sleep(200);

		assertEquals(assertMessageText, expectedLookName, getLookName(3));

		// Test that Image from Paintroid is correctly renamed
		String fileName = defaultLookName;
		try {
			imageFile = UiTestUtils.createTestMediaFile(Utils.buildPath(Constants.DEFAULT_ROOT, fileName + ".png"),
					RESOURCE_IMAGE2, getInstrumentation().getContext());
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
					RESOURCE_IMAGE, getInstrumentation().getContext());
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

	public void testBottomBarOnActionModes() {
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

		// Test on rename ActionMode
		UiTestUtils.openActionMode(solo, rename, 0, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.waitForText(rename, 1, timeToWait, false, true);

		solo.clickOnView(addButton);
		assertFalse("Add dialog should not appear", solo.waitForText(addDialogTitle, 0, timeToWait, false, true));

		solo.clickOnView(playButton);
		assertFalse("Should not start playing program",
				solo.waitForActivity(StageActivity.class.getSimpleName(), timeToWait));

		solo.goBack();
		solo.sleep(500);

		assertTrue("Resolution prefix not visible after ActionMode", solo.searchText(lookResoltionPrefixText, true));

		// Test on delete ActionMode
		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.waitForText(delete, 1, timeToWait, false, true);

		solo.clickOnView(addButton);
		assertFalse("Add dialog should not appear", solo.waitForText(addDialogTitle, 0, timeToWait, false, true));

		solo.clickOnView(playButton);
		assertFalse("Should not start playing program",
				solo.waitForActivity(StageActivity.class.getSimpleName(), timeToWait));

		solo.goBack();
		solo.sleep(500);

		assertTrue("Resolution prefix not visible after ActionMode", solo.searchText(lookResoltionPrefixText, true));

		// Test on copy ActionMode
		UiTestUtils.openActionMode(solo, copy, R.id.copy, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.waitForText(copy, 1, timeToWait, false, true);

		solo.clickOnView(addButton);
		assertFalse("Add dialog should not appear", solo.waitForText(addDialogTitle, 0, timeToWait, false, true));

		solo.clickOnView(playButton);
		assertFalse("Should not start playing program",
				solo.waitForActivity(StageActivity.class.getSimpleName(), timeToWait));

		solo.goBack();
		solo.sleep(500);

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
		assertFalse("Delete dialog showed up", solo.waitForText(deleteDialogTitle, 0, TIME_TO_WAIT));
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
		assertFalse("Delete dialog showed up", solo.waitForText(deleteDialogTitle, 0, TIME_TO_WAIT));
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
		solo.clickOnButton(solo.getString(R.string.yes));
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

		checkAllCheckboxes();
		assertFalse("Select All is still shown", solo.waitForText(selectAll, 1, 200, false, true));

		UiTestUtils.acceptAndCloseActionMode(solo);
		String yes = solo.getString(R.string.yes);
		solo.waitForText(yes);
		UiTestUtils.clickOnText(solo, yes);

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
		assertTrue("default project not visible", solo.searchText(solo.getString(R.string.yes)));
		solo.clickOnButton(solo.getString(R.string.yes));

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
		clickSingleItemActionMode(FIRST_TEST_LOOK_NAME, R.id.copy, copy);
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
		solo.clickOnButton(solo.getString(R.string.yes));
		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 0, TIME_TO_WAIT));

		checkIfNumberOfLooksIsEqual(expectedNumberOfLooks);
	}

	public void testOpenDeleteDialogAndGoBack() {
		int viewAmountBeforeDeleteMode = solo.getCurrentViews().size();
		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		int[] checkboxIndicesToCheck = { solo.getCurrentViews(CheckBox.class).size() - 1, 0, 2 };

		solo.scrollDown();
		solo.clickOnCheckBox(checkboxIndicesToCheck[0]);
		solo.scrollToTop();

		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.clickOnButton(solo.getString(R.string.no));

		solo.sleep(300);

		int viewAmountAfterDeleteMode = solo.getCurrentViews().size();

		assertTrue("checkboxes or other delete elements are still visible", viewAmountBeforeDeleteMode == viewAmountAfterDeleteMode);
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

		checkAllCheckboxes();
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

	public void testBottombarElementsVisibility() {
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
		solo.goBack();
	}

	public void testEmptyActionModeDialogs() {
		lookDataList.clear();
		UiTestUtils.createEmptyProject();

		UiTestUtils.openBackPackActionModeWhenEmtpy(solo, getActivity());
		solo.waitForDialogToOpen();
		assertTrue("Nothing to backpack dialog not shown", solo.waitForText(solo.getString(R.string
				.nothing_to_backpack_and_unpack)));
		solo.clickOnButton(0);
		solo.waitForDialogToClose();

		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());
		solo.waitForDialogToOpen();
		assertTrue("Nothing to delete dialog not shown", solo.waitForText(solo.getString(R.string
				.nothing_to_delete)));
		solo.clickOnButton(0);
		solo.waitForDialogToClose();

		UiTestUtils.openActionMode(solo, copy, R.id.copy, getActivity());
		solo.waitForDialogToOpen();
		assertTrue("Nothing to backpack dialog not shown", solo.waitForText(solo.getString(R.string
				.nothing_to_copy)));
		solo.clickOnButton(0);
		solo.waitForDialogToClose();

		UiTestUtils.openActionMode(solo, rename, R.id.rename, getActivity());
		solo.waitForDialogToOpen();
		assertTrue("Nothing to backpack dialog not shown", solo.waitForText(solo.getString(R.string
				.nothing_to_rename)));
	}

	public void testEmptyActionModeDialogsInBackPack() {
		UiTestUtils.backPackAllItems(solo, getActivity(), firstTestLookNamePacked, secondTestLookNamePacked);
		UiTestUtils.deleteAllItems(solo, getActivity());

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete, getActivity());
		solo.waitForDialogToOpen();
		assertTrue("Nothing to delete dialog not shown", solo.waitForText(solo.getString(R.string
				.nothing_to_delete)));
		solo.clickOnButton(0);
		solo.waitForDialogToClose();

		UiTestUtils.openActionMode(solo, unpack, R.id.unpacking, getActivity());
		solo.waitForDialogToOpen();
		assertTrue("Nothing to unpack dialog not shown", solo.waitForText(solo.getString(R.string
				.nothing_to_unpack)));
	}

	public void testOpenBackPackWhenScriptListEmptyButSomethingInBackPack() {
		UiTestUtils.backPackAllItems(solo, getActivity(), firstTestLookNamePacked, secondTestLookNamePacked);

		solo.goBack();
		UiTestUtils.deleteAllItems(solo, getActivity());

		UiTestUtils.openActionMode(solo, backpack, R.id.backpack, getActivity());
		solo.waitForActivity(BackPackActivity.class);
		assertTrue("Backpack wasn't opened", solo.waitForText(backpackTitle));
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
		clickSingleItemActionMode(lookToRename, R.id.rename, rename);
		assertTrue("Wrong title of dialog", solo.searchText(renameDialogTitle));
		assertTrue("No EditText with actual look name", solo.searchEditText(lookToRename));

		UiTestUtils.enterText(solo, 0, newLookName);
		solo.sendKey(Solo.ENTER);
	}

	private BackPackLookFragment getBackPackLookFragment() {
		BackPackActivity activity = (BackPackActivity) solo.getCurrentActivity();
		return (BackPackLookFragment) activity.getFragment(BackPackActivity.FRAGMENT_BACKPACK_LOOKS);
	}

	private LookFragment getLookFragment() {
		ScriptActivity activity = (ScriptActivity) solo.getCurrentActivity();
		return (LookFragment) activity.getFragment(ScriptActivity.FRAGMENT_LOOKS);
	}

	private LookAdapter getLookAdapter() {
		return (LookAdapter) getLookFragment().getListAdapter();
	}

	private BackPackLookAdapter getBackPackLookAdapter() {
		return (BackPackLookAdapter) getBackPackLookFragment().getListAdapter();
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

	private void packSingleItem(String lookName, boolean backPackEmpty) {
		UiTestUtils.openActionMode(solo, backpack, R.id.backpack, getActivity());
		if (!backPackEmpty) {
			solo.waitForDialogToOpen();
			solo.clickOnText(backpackAdd);
			solo.sleep(TIME_TO_WAIT_BACKPACK);
		}
		solo.clickOnText(lookName);
		solo.sleep(TIME_TO_WAIT);
		UiTestUtils.acceptAndCloseActionMode(solo);
	}

	private void clickSingleItemActionMode(String lookName, int menuItem, String itemName) {
		UiTestUtils.openActionMode(solo, itemName, menuItem, getActivity());
		solo.clickOnText(lookName);
		solo.sleep(TIME_TO_WAIT);
		UiTestUtils.acceptAndCloseActionMode(solo);
	}

	private String getLookName(int lookIndex) {
		lookDataList = projectManager.getCurrentSprite().getLookDataList();
		return lookDataList.get(lookIndex).getLookName();
	}

	private void checkIfCheckboxesAreCorrectlyChecked(boolean firstCheckboxExpectedChecked,
			boolean secondCheckboxExpectedChecked) {
		solo.sleep(300);
		int startIndex = 0;
		if (solo.getCurrentViews(CheckBox.class).size() > projectManager.getCurrentSprite().getLookDataList().size()) {
			startIndex = 1;
		}
		firstCheckBox = solo.getCurrentViews(CheckBox.class).get(startIndex);
		secondCheckBox = solo.getCurrentViews(CheckBox.class).get(startIndex + 1);
		assertEquals("First checkbox not correctly checked", firstCheckboxExpectedChecked, firstCheckBox.isChecked());
		assertEquals("Second checkbox not correctly checked", secondCheckboxExpectedChecked, secondCheckBox.isChecked());
	}

	private void checkAllCheckboxes() {
		boolean skipFirst = solo.getCurrentViews(CheckBox.class).size() > projectManager.getCurrentSprite().getLookDataList().size();
		for (CheckBox checkBox : solo.getCurrentViews(CheckBox.class)) {
			if (skipFirst) {
				continue;
			}
			assertTrue("CheckBox is not Checked!", checkBox.isChecked());
		}
	}

	private void checkIfNumberOfLooksIsEqual(int expectedNumber) {
		lookDataList = projectManager.getCurrentSprite().getLookDataList();
		assertEquals("Number of looks is not as expected", expectedNumber, lookDataList.size());
	}

	private void clickOnContextMenuItem(String lookName, String menuItemName) {
		solo.clickLongOnText(lookName);
		solo.waitForText(menuItemName);
		solo.clickOnText(menuItemName);
		solo.waitForActivity(BackPackActivity.class);
		solo.waitForFragmentByTag(BackPackLookFragment.TAG);
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
		clickSingleItemActionMode(lookName, R.id.delete, delete);
		solo.waitForDialogToOpen();
		solo.waitForText(solo.getString(R.string.yes));
		solo.clickOnText(solo.getString(R.string.yes));
		solo.waitForDialogToClose();
	}

	private void addLookWithName(String lookName) {
		LookData lookDataToAdd = lookData.clone();
		lookDataToAdd.setLookName(lookName);
		lookDataList.add(lookDataToAdd);
	}

	private int getCurrentLookCount() {
		return ProjectManager.getInstance().getCurrentSprite().getLookDataList().size();
	}

	private boolean searchForLook(String lookName) {
		for (LookData lookData : ProjectManager.getInstance().getCurrentSprite().getLookDataList()) {
			if (lookData.getLookName().equals(lookName)) {
				return true;
			}
		}

		return false;
	}

	private void copyLook(String lookName) {
		clickSingleItemActionMode(lookName, R.id.copy, copy);
	}
}
