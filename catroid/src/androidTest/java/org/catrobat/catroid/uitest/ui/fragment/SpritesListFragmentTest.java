/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;

import com.robotium.solo.By;
import com.robotium.solo.WebElement;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.GroupItemSprite;
import org.catrobat.catroid.content.GroupSprite;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.AddItemToUserListBrick;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.BackPackActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.WebViewActivity;
import org.catrobat.catroid.ui.adapter.SpriteAdapter;
import org.catrobat.catroid.ui.adapter.SpriteListAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.fragment.BackPackSpriteListFragment;
import org.catrobat.catroid.ui.fragment.ScriptFragment;
import org.catrobat.catroid.ui.fragment.SpritesListFragment;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.web.UtilWebConnection;
import org.catrobat.catroid.web.ServerCalls;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.catrobat.catroid.common.Constants.BACKPACK_DIRECTORY;
import static org.catrobat.catroid.common.Constants.DEFAULT_ROOT;
import static org.catrobat.catroid.utils.Utils.buildPath;

public class SpritesListFragmentTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private static final String OLD_SPRITE_PROJECT = "OldSpriteProject";

	public SpritesListFragmentTest() {
		super(MainMenuActivity.class);
	}

	private static final int RESOURCE_IMAGE = org.catrobat.catroid.test.R.drawable.catroid_sunglasses;
	private static final int RESOURCE_SOUND = org.catrobat.catroid.test.R.raw.longsound;
	private static final String TEST_LOOK_NAME = "testLook";
	private static final String TEST_SOUND_NAME = "testSound";

	private static final int VISIBLE = View.VISIBLE;
	private static final int GONE = View.GONE;

	private static final String LOCAL_VARIABLE_NAME = "test_local";
	private static final double LOCAL_VARIABLE_VALUE = 0xDEADBEEF;

	private static final String GLOBAL_VARIABLE_NAME = "test_global";
	private static final double GLOBAL_VARIABLE_VALUE = 0xC0FFEE;

	private static final String SPRITE_NAME = "testSprite1";
	private static final String SPRITE_NAME2 = "testSprite2";
	private static final String SPRITE_NAME_UNPACKED = "testSprite11";
	private static final String SPRITE_NAME2_UNPACKED = "testSprite21";
	private static final String SPRITE_NAME_BACKGROUND = "cat";

	private static final int TIME_TO_WAIT_BACKPACK = 1000;
	private static final int TIME_TO_WAIT = 400;
	private static final int DRAG_AND_DROP_Y_OFFSET = 100;

	private Sprite sprite;
	private Sprite sprite2;
	private Project project;

	private String continueMenu;
	private String rename;
	private String delete;
	private String copy;

	private String unpack;
	private String unpackAsObject;
	private String unpackAsBackGround;
	private String backpack;
	private String backpackAdd;
	private String backpackTitle;
	private String backpackReplaceDialogMultiple;
	private String upload;
	private String next;
	private String showProgram;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		UiTestUtils.createOldTestProjectWithSprites(OLD_SPRITE_PROJECT);
		UiTestUtils.createTestProject(UiTestUtils.PROJECTNAME1);
		UiTestUtils.createTestProject();

		project = ProjectManager.getInstance().getCurrentProject();
		sprite = new SingleSprite(SPRITE_NAME);
		sprite2 = new SingleSprite(SPRITE_NAME2);
		project.getDefaultScene().addSprite(sprite);
		project.getDefaultScene().addSprite(sprite2);
		project.getDefaultScene().getDataContainer().addSpriteUserVariableToSprite(sprite, LOCAL_VARIABLE_NAME);
		project.getDefaultScene().getDataContainer().getUserVariable(sprite, LOCAL_VARIABLE_NAME).setValue(LOCAL_VARIABLE_VALUE);

		project.getDefaultScene().getDataContainer().addProjectUserVariable(GLOBAL_VARIABLE_NAME);
		project.getDefaultScene().getDataContainer().getUserVariable(null, GLOBAL_VARIABLE_NAME).setValue(GLOBAL_VARIABLE_VALUE);

		ProjectManager.getInstance().setProject(project);

		continueMenu = solo.getString(R.string.main_menu_continue);
		rename = solo.getString(R.string.rename);
		backpackTitle = solo.getString(R.string.backpack_title);
		delete = solo.getString(R.string.delete);
		copy = solo.getString(R.string.copy);
		unpack = solo.getString(R.string.unpack);
		unpackAsObject = solo.getString(R.string.unpack_object);
		unpackAsBackGround = solo.getString(R.string.unpack_bg);
		backpack = solo.getString(R.string.backpack);
		backpackAdd = solo.getString(R.string.packing);
		backpackReplaceDialogMultiple = solo.getString(R.string.backpack_replace_object_multiple);
		upload = solo.getString(R.string.upload_button);
		next = solo.getString(R.string.next);
		showProgram = solo.getString(R.string.progress_upload_dialog_show_program);

		UiTestUtils.clearBackPack(true);
		solo.clickOnText(solo.getString(R.string.main_menu_continue));

		SpriteAdapter adapter = getSpriteAdapter();
		if (adapter != null && adapter.getShowDetails()) {
			solo.clickOnMenuItem(solo.getString(R.string.hide_details), true);
			solo.sleep(TIME_TO_WAIT);
		}
	}

	public void testLocalVariablesWhenSpriteCopiedFromSpritesListFragment() {
		clickOnActionModeSingleItem(SPRITE_NAME, R.string.copy, R.id.copy);

		String copiedSpriteName = SPRITE_NAME + solo.getString(R.string.copy_sprite_name_suffix);
		solo.waitForText(copiedSpriteName);
		assertTrue(copiedSpriteName + " not found!", solo.searchText(copiedSpriteName));

		Sprite clonedSprite = null;
		for (Sprite tempSprite : project.getDefaultScene().getSpriteList()) {
			if (tempSprite.getName().equals(copiedSpriteName)) {
				clonedSprite = tempSprite;
			}
		}

		if (clonedSprite == null) {
			fail("no cloned sprite in project");
		}

		List<UserVariable> userVariableList = project.getDefaultScene().getDataContainer().getOrCreateVariableListForSprite(clonedSprite);
		Set<String> hashSet = new HashSet<>();
		for (UserVariable userVariable : userVariableList) {
			assertTrue("Variable already exists", hashSet.add(userVariable.getName()));
		}
	}

	public void testSelectAllActionModeButton() {
		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());
		String deselectAll = solo.getString(R.string.deselect_all).toUpperCase(Locale.getDefault());
		solo.sleep(TIME_TO_WAIT);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy);
		assertTrue("Select All is not shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), true));

		UiTestUtils.clickOnText(solo, selectAll);
		for (CheckBox checkBox : solo.getCurrentViews(CheckBox.class)) {
			if (checkBox.isShown()) {
				assertTrue("CheckBox is not checked!", checkBox.isChecked());
			}
		}

		assertTrue("Deselect All is not shown", solo.searchText(deselectAll, 1, false, true));
		UiTestUtils.clickOnText(solo, deselectAll);
		for (CheckBox checkBox : solo.getCurrentViews(CheckBox.class)) {
			if (checkBox.isShown()) {
				assertFalse("CheckBox is checked!", checkBox.isChecked());
			}
		}
		assertFalse("Deselect All is still shown", solo.searchText(deselectAll, 1, false, true));

		solo.clickOnCheckBox(0);
		assertTrue("Select All is not shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), true));

		solo.clickOnCheckBox(0);
		assertTrue("Select All is not shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), true));

		solo.goBack();

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete);
		assertTrue("Select All is not shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), true));

		UiTestUtils.clickOnText(solo, selectAll);
		assertTrue("Deselect All is not shown", solo.searchText(deselectAll, 1, false, true));

		solo.clickOnCheckBox(0);
		assertTrue("Deselect All is not shown", solo.searchText(selectAll, 1, false, true));

		solo.clickOnCheckBox(0);
		assertTrue("Deselect All is not shown", solo.searchText(deselectAll, 1, false, true));
	}

	public void testDragAndDropUp() {
		for (int i = 0; i < 2; i++) {
			addSpriteWithName("TestSprite" + i);
		}

		solo.goBack();
		solo.clickOnText(solo.getString(R.string.main_menu_continue));

		List<Sprite> list = ProjectManager.getInstance().getCurrentScene().getSpriteList();

		assertEquals("Wrong List before DragAndDropTest", list.get(1).getName(), SPRITE_NAME);
		assertEquals("Wrong List before DragAndDropTest", list.get(2).getName(), SPRITE_NAME2);
		assertEquals("Wrong List before DragAndDropTest", list.get(3).getName(), "TestSprite0");
		assertEquals("Wrong List before DragAndDropTest", list.get(4).getName(), "TestSprite1");

		solo.sleep(200);
		ArrayList<Integer> yPositionList = UiTestUtils.getListItemYPositions(solo, 0);
		UiTestUtils.longClickAndDrag(solo, 10, yPositionList.get(4), 10, yPositionList.get(3) - DRAG_AND_DROP_Y_OFFSET, 20);

		list = ProjectManager.getInstance().getCurrentScene().getSpriteList();

		assertEquals("Wrong List after DragAndDropTest", list.get(1).getName(), SPRITE_NAME);
		assertEquals("Wrong List after DragAndDropTest", list.get(2).getName(), SPRITE_NAME2);
		assertEquals("Wrong List after DragAndDropTest", list.get(3).getName(), "TestSprite1");
		assertEquals("Wrong List after DragAndDropTest", list.get(4).getName(), "TestSprite0");
	}

	public void testDragAndDropDown() {
		for (int i = 0; i < 2; i++) {
			addSpriteWithName("TestSprite" + i);
		}

		solo.goBack();
		solo.clickOnText(solo.getString(R.string.main_menu_continue));

		List<Sprite> list = ProjectManager.getInstance().getCurrentScene().getSpriteList();

		assertEquals("Wrong List before DragAndDropTest", list.get(1).getName(), SPRITE_NAME);
		assertEquals("Wrong List before DragAndDropTest", list.get(2).getName(), SPRITE_NAME2);
		assertEquals("Wrong List before DragAndDropTest", list.get(3).getName(), "TestSprite0");
		assertEquals("Wrong List before DragAndDropTest", list.get(4).getName(), "TestSprite1");

		ArrayList<Integer> yPositionList = UiTestUtils.getListItemYPositions(solo, 0);
		UiTestUtils.longClickAndDrag(solo, 10, yPositionList.get(1), 10, yPositionList.get(2) + DRAG_AND_DROP_Y_OFFSET, 20);

		list = ProjectManager.getInstance().getCurrentScene().getSpriteList();

		assertEquals("Wrong List after DragAndDropTest", list.get(1).getName(), SPRITE_NAME2);
		assertEquals("Wrong List after DragAndDropTest", list.get(2).getName(), SPRITE_NAME);
		assertEquals("Wrong List after DragAndDropTest", list.get(3).getName(), "TestSprite0");
		assertEquals("Wrong List after DragAndDropTest", list.get(4).getName(), "TestSprite1");
	}

	public void testDragAndDropWithBackground() {
		List<Sprite> list = ProjectManager.getInstance().getCurrentScene().getSpriteList();

		assertEquals("Wrong List before DragAndDropTest", list.get(0).getName(), SPRITE_NAME_BACKGROUND);
		assertEquals("Wrong List before DragAndDropTest", list.get(1).getName(), SPRITE_NAME);
		assertEquals("Wrong List before DragAndDropTest", list.get(2).getName(), SPRITE_NAME2);

		ArrayList<Integer> yPositionList = UiTestUtils.getListItemYPositions(solo, 0);
		UiTestUtils.longClickAndDrag(solo, 10, yPositionList.get(0), 10, yPositionList.get(2) + DRAG_AND_DROP_Y_OFFSET, 20);

		solo.waitForText(solo.getString(R.string.backpack_add));

		solo.clickInList(0);

		UiTestUtils.longClickAndDrag(solo, 10, yPositionList.get(2), 10, 20, 20);

		assertEquals("Wrong List before DragAndDropTest", list.get(0).getName(), SPRITE_NAME_BACKGROUND);
		assertEquals("Wrong List before DragAndDropTest", list.get(1).getName(), SPRITE_NAME2);
		assertEquals("Wrong List before DragAndDropTest", list.get(2).getName(), SPRITE_NAME);
	}

	public void testEmptyActionModeDialogs() {
		solo.goBack();
		UiTestUtils.createEmptyProject();
		solo.clickOnText(continueMenu);
		UiTestUtils.openActionMode(solo, delete, R.id.delete);
		solo.waitForDialogToOpen();
		assertTrue("Nothing to delete dialog not shown", solo.waitForText(solo.getString(R.string
				.nothing_to_delete)));
		solo.clickOnButton(0);
		solo.waitForDialogToClose();
		UiTestUtils.openActionMode(solo, copy, R.id.copy);
		solo.waitForDialogToOpen();
		assertTrue("Nothing to backpack dialog not shown", solo.waitForText(solo.getString(R.string
				.nothing_to_copy)));
		solo.clickOnButton(0);
		solo.waitForDialogToClose();
		UiTestUtils.openActionMode(solo, rename, R.id.rename);
		solo.waitForDialogToOpen();
		assertTrue("Nothing to backpack dialog not shown", solo.waitForText(solo.getString(R.string
				.nothing_to_rename)));
		solo.clickOnButton(0);
		solo.waitForDialogToClose();

		UiTestUtils.openBackPackActionModeWhenEmpty(solo);
		solo.waitForDialogToOpen();
		assertFalse("Nothing to backpack dialog shown, but it should be possible to backpack the background", solo
				.waitForText(solo.getString(R.string.nothing_to_backpack_and_unpack), 1, TIME_TO_WAIT_BACKPACK));
	}

	public void testEmptyActionModeDialogsInBackPack() {
		UiTestUtils.backPackAllItems(solo, getActivity(), "cat", null);
		UiTestUtils.deleteAllItems(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete);
		solo.waitForDialogToOpen();
		assertTrue("Nothing to delete dialog not shown", solo.waitForText(solo.getString(R.string
				.nothing_to_delete)));
		solo.clickOnButton(0);
		solo.waitForDialogToClose();

		UiTestUtils.openActionMode(solo, unpackAsObject, R.id.unpacking_object);
		solo.waitForDialogToOpen();
		assertTrue("Nothing to unpack dialog not shown", solo.waitForText(solo.getString(R.string
				.nothing_to_unpack)));
	}

	public void testGetSpriteFromMediaLibrary() {
		String mediaLibraryText = solo.getString(R.string.add_look_media_library);
		int numberSpritesBefore = ProjectManager.getInstance().getCurrentProject().getDefaultScene().getSpriteList().size();
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

		solo.waitForFragmentByTag(SpritesListFragment.TAG);
		UiTestUtils.enterText(solo, 0, "testSpriteMediaLibrary");
		solo.waitForText(solo.getString(R.string.ok));
		solo.clickOnText(solo.getString(R.string.ok));
		solo.waitForDialogToClose();
		solo.sleep(TIME_TO_WAIT);
		int numberSpritesAfter = ProjectManager.getInstance().getCurrentProject().getDefaultScene().getSpriteList().size();
		assertEquals("No Sprite was added!", numberSpritesBefore + 1, numberSpritesAfter);
	}

	@Device
	public void testAddSpriteFromMediaLibraryWithNoInternet() {
		String mediaLibraryText = solo.getString(R.string.add_look_media_library);
		int retryCounter = 0;
		WifiManager wifiManager = (WifiManager) this.getActivity().getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(false);
		while (UtilWebConnection.isNetworkAvailable(getActivity())) {
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
		assertTrue("Should be in Sprites Fragment", solo.waitForText(SPRITE_NAME));
		wifiManager.setWifiEnabled(true);
		while (!UtilWebConnection.isNetworkAvailable(getActivity())) {
			solo.sleep(2000);
			if (retryCounter > 30) {
				break;
			}
			retryCounter++;
		}
	}

	public void testBackPackSpriteContextMenu() {
		packSingleItem(SPRITE_NAME2, true);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		assertTrue("BackPack title didn't show up",
				solo.waitForText(backpackTitle, 0, TIME_TO_WAIT_BACKPACK));
		assertTrue("Sprite wasn't backpacked!", solo.waitForText(SPRITE_NAME2, 0, TIME_TO_WAIT));
	}

	public void testBackPackSpriteDoubleContextMenu() {
		packSingleItem(SPRITE_NAME, true);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);
		solo.goBack();
		packSingleItem(SPRITE_NAME2, false);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		assertTrue("BackPack title didn't show up",
				solo.waitForText(backpackTitle, 0, TIME_TO_WAIT_BACKPACK));
		assertTrue("Sprite wasn't backpacked!", solo.waitForText(SPRITE_NAME, 0, TIME_TO_WAIT));
		assertTrue("Sprite wasn't backpacked!", solo.waitForText(SPRITE_NAME2, 0, TIME_TO_WAIT));
	}

	public void testBackPackSpriteSimpleUnpackingContextMenu() {
		packSingleItem(SPRITE_NAME, true);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		assertTrue("Sprite wasn't backpacked!", solo.waitForText(SPRITE_NAME, 0, TIME_TO_WAIT));

		clickOnBackPackItem(SPRITE_NAME, unpackAsObject);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		assertTrue("Sprite wasn't unpacked!", solo.waitForText(SPRITE_NAME_UNPACKED, 0, TIME_TO_WAIT));
	}

	public void testBackPackSpriteSimpleUnpackingAndDelete() {
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		SpriteAdapter adapter = getSpriteAdapter();
		assertNotNull("Could not get Adapter", adapter);
		int oldCount = adapter.getGroupCount();

		packSingleItem(SPRITE_NAME2, true);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		solo.goBack();
		deleteSprite(SPRITE_NAME2);

		solo.sleep(TIME_TO_WAIT);
		UiTestUtils.openBackPack(solo);

		clickOnBackPackItem(SPRITE_NAME2, unpackAsObject);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		assertTrue("Sprite wasn't unpacked!", solo.waitForText(SPRITE_NAME2, 0, TIME_TO_WAIT));

		int newCount = adapter.getGroupCount();
		assertEquals("Counts have to be equal", oldCount, newCount);
	}

	public void testBackPackSpriteMultipleUnpacking() {
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		SpriteAdapter adapter = getSpriteAdapter();
		int oldCount = adapter.getGroupCount();

		assertNotNull("Could not get Adapter", adapter);
		packSingleItem(SPRITE_NAME, true);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		clickOnBackPackItem(SPRITE_NAME, unpackAsObject);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		solo.sleep(TIME_TO_WAIT_BACKPACK);
		assertTrue("Sprite wasn't unpacked!", solo.waitForText(SPRITE_NAME_UNPACKED, 0, TIME_TO_WAIT));
		packSingleItem(SPRITE_NAME2, false);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		clickOnBackPackItem(SPRITE_NAME2, unpackAsObject);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		solo.sleep(TIME_TO_WAIT_BACKPACK);
		solo.scrollDown();
		solo.sleep(TIME_TO_WAIT);
		assertTrue("Sprite wasn't unpacked!", solo.waitForText(SPRITE_NAME2_UNPACKED, 0, TIME_TO_WAIT));
		int newCount = adapter.getGroupCount();
		assertEquals("There are sprites missing", oldCount + 2, newCount);
	}

	public void testBackPackAndUnPackFromDifferentProgrammes() {
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		SpriteAdapter adapter = getSpriteAdapter();
		assertNotNull("Could not get Adapter", adapter);
		packSingleItem(SPRITE_NAME, true);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		switchToProgrammeBackgroundFromBackpack(UiTestUtils.PROJECTNAME1);

		UiTestUtils.openBackPack(solo);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		clickOnBackPackItem(SPRITE_NAME, unpackAsObject);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		assertTrue("Sprite wasn't unpacked!", solo.waitForText(SPRITE_NAME, 1, 3000));
	}

	public void testBackPackBackgroundSprite() {
		solo.sleep(TIME_TO_WAIT_BACKPACK);

		UiTestUtils.backPackAllItems(solo, getActivity(), SPRITE_NAME_BACKGROUND, SPRITE_NAME);

		clickOnBackPackItem(SPRITE_NAME_BACKGROUND, unpackAsBackGround);
		solo.waitForDialogToOpen(TIME_TO_WAIT_BACKPACK);
		assertTrue("No replace background dialog was shown", solo.waitForText(solo.getString(R.string.unpack_background)));
		solo.clickOnText(solo.getString(R.string.ok));
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		assertTrue("Background sprite was not unpacked or renamed to background",
				solo.waitForText(solo.getString(R.string.background)));
	}

	public void testBackPackActionModeCheckingAndTitle() {
		UiTestUtils.openBackPackActionModeWhenEmpty(solo);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		int timeToWaitForTitle = 300;

		String sprite = solo.getString(R.string.sprite);
		String sprites = solo.getString(R.string.sprites);

		assertFalse("Sprite should not be displayed in title", solo.waitForText(sprite, 3, 300, false, true));

		// Check if checkboxes are visible
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, VISIBLE);

		checkIfCheckboxesAreCorrectlyChecked(false, false, false);

		int expectedNumberOfSelectedSprites = 1;
		String expectedTitle = backpack + " " + expectedNumberOfSelectedSprites + " " + sprite;

		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, true, false);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedNumberOfSelectedSprites = 2;
		expectedTitle = backpack + " " + expectedNumberOfSelectedSprites + " " + sprites;

		// Check if multiple-selection is possible
		solo.clickOnCheckBox(2);
		checkIfCheckboxesAreCorrectlyChecked(false, true, true);
		assertTrue("Title not as aspected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedNumberOfSelectedSprites = 1;
		expectedTitle = backpack + " " + expectedNumberOfSelectedSprites + " " + sprite;

		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, false, true);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedTitle = backpack;

		solo.clickOnCheckBox(2);
		checkIfCheckboxesAreCorrectlyChecked(false, false, false);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));
	}

	public void testBackPackActionModeIfNothingSelected() {
		UiTestUtils.openBackPackActionModeWhenEmpty(solo);
		int expectedNumberOfSprites = ProjectManager.getInstance().getCurrentProject().getDefaultScene().getSpriteList().size();
		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);
		checkIfCheckboxesAreCorrectlyChecked(false, false, false);
		UiTestUtils.acceptAndCloseActionMode(solo);
		assertFalse("ActionMode didn't disappear", solo.waitForText(backpack, 1, TIME_TO_WAIT, false, true));
		checkIfNumberOfSpritesIsEqual(expectedNumberOfSprites);

		UiTestUtils.openBackPackActionModeWhenEmpty(solo);
		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);
		checkIfCheckboxesAreCorrectlyChecked(false, false, false);
		solo.goBack();
		assertFalse("ActionMode didn't disappear", solo.waitForText(backpack, 1, TIME_TO_WAIT, false, true));
		checkIfNumberOfSpritesIsEqual(expectedNumberOfSprites);
	}

	public void testBackPackActionModeIfSomethingSelectedAndPressingBack() {
		UiTestUtils.openBackPackActionModeWhenEmpty(solo);
		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(1);
		solo.clickOnCheckBox(2);
		checkIfCheckboxesAreCorrectlyChecked(false, true, true);
		solo.goBack();

		assertFalse("ActionMode didn't disappear", solo.waitForText(backpack, 1, TIME_TO_WAIT, false, true));
		solo.sleep(TIME_TO_WAIT);
		assertFalse("Backpack was opened, but shouldn't be!", solo.waitForText(backpackTitle, 1, TIME_TO_WAIT, false, true));
	}

	public void testBackPackSelectAll() {
		UiTestUtils.openBackPackActionModeWhenEmpty(solo);
		solo.waitForActivity("ProjectActivity");
		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());
		String deselectAll = solo.getString(R.string.deselect_all).toUpperCase(Locale.getDefault());
		UiTestUtils.clickOnText(solo, selectAll);

		for (CheckBox checkBox : solo.getCurrentViews(CheckBox.class)) {
			assertTrue("CheckBox is not Checked!", checkBox.isChecked());
		}
		assertTrue("Deselect All is not shown", solo.waitForText(deselectAll, 1, 200, false, true));

		UiTestUtils.acceptAndCloseActionMode(solo);
		assertTrue("Backpack didn't appear", solo.waitForText(backpackTitle));
		assertTrue("Sprite wasn't backpacked!", solo.waitForText(SPRITE_NAME_BACKGROUND, 0, TIME_TO_WAIT));
		assertTrue("Sprite wasn't backpacked!", solo.waitForText(SPRITE_NAME, 0, TIME_TO_WAIT));
	}

	public void testBackPackSpriteDeleteContextMenu() {
		UiTestUtils.backPackAllItems(solo, getActivity(), SPRITE_NAME_BACKGROUND, SPRITE_NAME);

		SpriteListAdapter adapter = getSpriteListAdapter();
		int oldCount = adapter.getCount();
		List<Sprite> backPackSpriteList = BackPackListManager.getInstance().getBackPackedSprites();

		clickOnBackPackItem(SPRITE_NAME, delete);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		int newCount = adapter.getCount();
		solo.sleep(500);

		assertEquals("Not all sprites were backpacked", 3, oldCount);
		assertEquals("Sprite wasn't deleted in backpack", 2, newCount);
		assertEquals("Count of the backpack spriteList is not correct", newCount, backPackSpriteList.size());
	}

	public void testBackPackSpriteDeleteActionMode() {
		UiTestUtils.backPackAllItems(solo, getActivity(), SPRITE_NAME_BACKGROUND, SPRITE_NAME);

		SpriteListAdapter adapter = getSpriteListAdapter();
		int oldCount = adapter.getCount();
		List<Sprite> backPackSpriteList = BackPackListManager.getInstance().getBackPackedSprites();

		UiTestUtils.deleteAllItems(solo);

		int newCount = adapter.getCount();
		solo.sleep(500);
		assertTrue("No backpack is emtpy text appeared", solo.searchText(backpack));
		assertTrue("No backpack is emtpy text appeared", solo.searchText(solo.getString(R.string.is_empty)));

		assertEquals("Not all sprites were backpacked", 3, oldCount);
		assertEquals("Sprite wasn't deleted in backpack", 0, newCount);
		assertEquals("Count of the backpack spritlist is not correct", newCount, backPackSpriteList.size());
	}

	public void testBackPackSpriteActionModeDifferentProgrammes() {
		UiTestUtils.backPackAllItems(solo, getActivity(), SPRITE_NAME_BACKGROUND, SPRITE_NAME);
		switchToProgrammeBackgroundFromBackpack(UiTestUtils.PROJECTNAME1);

		UiTestUtils.openBackPack(solo);

		UiTestUtils.openActionMode(solo, unpackAsObject, R.id.unpacking_object);
		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());
		UiTestUtils.clickOnText(solo, selectAll);
		UiTestUtils.acceptAndCloseActionMode(solo);

		solo.waitForActivity(ProjectActivity.class);
		assertTrue("Background sprite wasn't unpacked, but should be!", solo.waitForText(SPRITE_NAME_BACKGROUND, 1,
				1000));
		assertTrue("Sprite wasn't unpacked!", solo.waitForText(SPRITE_NAME, 1, 1000));
		UiTestUtils.deleteAllItems(solo);
		assertFalse("Sprite wasn't deleted!", solo.waitForText(SPRITE_NAME, 1, 1000));
		assertFalse("Sprite wasn't deleted!", solo.waitForText(SPRITE_NAME2, 1, 1000));
	}

	public void testBackPackDeleteActionModeCheckingAndTitle() {
		solo.sleep(TIME_TO_WAIT_BACKPACK);

		UiTestUtils.backPackAllItems(solo, getActivity(), SPRITE_NAME_BACKGROUND, SPRITE_NAME);
		UiTestUtils.openActionMode(solo, delete, R.id.delete);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		int timeToWaitForTitle = 300;

		String sprite = solo.getString(R.string.sprite);
		String sprites = solo.getString(R.string.sprites);

		assertFalse("Sprite should not be displayed in title", solo.waitForText(sprite, 3, 300, false, true));

		// Check if checkboxes are visible
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, VISIBLE);

		checkIfCheckboxesAreCorrectlyChecked(false, false, false);

		int expectedNumberOfSelectedSprites = 1;
		String expectedTitle = delete + " " + expectedNumberOfSelectedSprites + " " + sprite;

		solo.sleep(TIME_TO_WAIT_BACKPACK);
		solo.clickOnCheckBox(0);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		checkIfCheckboxesAreCorrectlyChecked(true, false, false);
		assertTrue("Title not as expected" + expectedTitle, solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedNumberOfSelectedSprites = 2;
		expectedTitle = delete + " " + expectedNumberOfSelectedSprites + " " + sprites;

		// Check if multiple-selection is possible
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(true, true, false);
		assertTrue("Title not as aspected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedNumberOfSelectedSprites = 1;
		expectedTitle = delete + " " + expectedNumberOfSelectedSprites + " " + sprite;

		solo.clickOnCheckBox(0);
		checkIfCheckboxesAreCorrectlyChecked(false, true, false);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedTitle = delete;

		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, false, false);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));
	}

	public void testBackPackDeleteActionModeIfNothingSelected() {
		UiTestUtils.backPackAllItems(solo, getActivity(), SPRITE_NAME_BACKGROUND, SPRITE_NAME);
		UiTestUtils.openActionMode(solo, delete, R.id.delete);

		int expectedNumberOfSprites = BackPackListManager.getInstance().getBackPackedSprites().size();
		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);
		checkIfCheckboxesAreCorrectlyChecked(false, false, false);
		UiTestUtils.acceptAndCloseActionMode(solo);
		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 1, TIME_TO_WAIT, false, true));
		checkIfNumberOfSpritesIsEqual(expectedNumberOfSprites);

		UiTestUtils.openActionMode(solo, delete, R.id.delete);
		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);
		checkIfCheckboxesAreCorrectlyChecked(false, false, false);
		solo.goBack();
		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 1, TIME_TO_WAIT, false, true));
		checkIfNumberOfSpritesIsEqual(expectedNumberOfSprites);
	}

	public void testBackPackDeleteActionModeIfSomethingSelectedAndPressingBack() {
		UiTestUtils.backPackAllItems(solo, getActivity(), SPRITE_NAME_BACKGROUND, SPRITE_NAME);
		UiTestUtils.openActionMode(solo, delete, R.id.delete);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(1);
		solo.clickOnCheckBox(2);
		checkIfCheckboxesAreCorrectlyChecked(false, true, true);
		solo.goBack();

		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 1, TIME_TO_WAIT, false, true));
	}

	public void testBackPackDeleteSelectAll() {
		UiTestUtils.backPackAllItems(solo, getActivity(), SPRITE_NAME_BACKGROUND, SPRITE_NAME);
		UiTestUtils.openActionMode(solo, delete, R.id.delete);

		solo.waitForActivity("BackPackActivity");
		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());
		UiTestUtils.clickOnText(solo, selectAll);

		checkIfCheckboxesAreCorrectlyChecked(true, true, true);

		assertFalse("Select All is still shown", solo.waitForText(selectAll, 1, 200, false, true));

		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.sleep(TIME_TO_WAIT_BACKPACK);

		assertFalse("Sprite wasn't deleted!", solo.waitForText(SPRITE_NAME_BACKGROUND, 0, TIME_TO_WAIT, false, true));
		assertFalse("Sprite wasn't deleted!", solo.waitForText(SPRITE_NAME, 0, TIME_TO_WAIT, false, true));
		assertFalse("Sprite wasn't deleted!", solo.waitForText(SPRITE_NAME2, 0, TIME_TO_WAIT, false, true));
		assertTrue("No empty bg found!", solo.waitForText(solo.getString(R.string.is_empty), 0, TIME_TO_WAIT));
	}

	public void testBackPackShowAndHideDetails() {
		UiTestUtils.backPackAllItems(solo, getActivity(), SPRITE_NAME_BACKGROUND, SPRITE_NAME);
		int timeToWait = 500;

		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, GONE);
		solo.clickOnMenuItem(solo.getString(R.string.show_details));
		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, VISIBLE, VISIBLE, GONE);

		// Test if showDetails is remembered after pressing back
		solo.goBack();
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		UiTestUtils.openBackPack(solo);
		solo.waitForActivity(BackPackActivity.class.getSimpleName());
		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, VISIBLE, VISIBLE, GONE);

		solo.sleep(timeToWait);
		solo.clickOnMenuItem(solo.getString(R.string.hide_details));
		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, GONE);
	}

	public void testBackPackSpriteWithLooksSoundsAndScripts() {
		solo.goBack();
		UiTestUtils.createEmptyProjectWithoutScript();
		UiTestUtils.createTestProjectWithSpecialBricksForBackPack(UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		UiTestUtils.prepareForSpecialBricksTest(getInstrumentation().getContext(), RESOURCE_IMAGE,
				RESOURCE_SOUND, TEST_LOOK_NAME, TEST_SOUND_NAME);
		solo.clickOnText(continueMenu);
		solo.sleep(TIME_TO_WAIT);

		packSingleItem(SPRITE_NAME_BACKGROUND, true);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		assertTrue("BackPack title didn't show up",
				solo.waitForText(backpackTitle, 0, TIME_TO_WAIT_BACKPACK));
		assertTrue("Sprite wasn't backpacked!", solo.waitForText(SPRITE_NAME_BACKGROUND, 0, TIME_TO_WAIT));
		solo.goBack();

		solo.sleep(TIME_TO_WAIT);
		solo.clickOnText(SPRITE_NAME_BACKGROUND);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		solo.clickOnText(solo.getString(R.string.backgrounds));
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		UiTestUtils.openBackPackActionModeWhenEmpty(solo);
		assertFalse("Visible Backpack was opened despite look should be in hidden backpack", solo.waitForText(unpack, 1, TIME_TO_WAIT_BACKPACK));
		assertFalse("Visible Backpack was opened despite look should be in hidden backpack", solo.waitForText(TEST_LOOK_NAME + "1", 1, TIME_TO_WAIT_BACKPACK));
		assertTrue("Look is not in hidden backpack!", BackPackListManager.getInstance().getHiddenBackpackedLooks().size() == 1);
		solo.goBack();
		solo.goBack();

		solo.clickOnText(solo.getString(R.string.sounds));
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		UiTestUtils.openBackPackActionModeWhenEmpty(solo);
		assertFalse("Visible Backpack was opened despite sound should be in hidden backpack", solo.waitForText(unpack, 1, TIME_TO_WAIT_BACKPACK));
		assertFalse("Visible Backpack was opened despite sound should be in hidden backpack", solo.waitForText(TEST_SOUND_NAME + "1", 1, TIME_TO_WAIT_BACKPACK));
		assertTrue("Sound is not in hidden backpack!", BackPackListManager.getInstance().getHiddenBackpackedSounds().size() == 1);
		solo.goBack();
		solo.goBack();

		solo.clickOnText(solo.getString(R.string.scripts));
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		UiTestUtils.openBackPackActionModeWhenEmpty(solo);
		assertFalse("Visible Backpack was opened despite script should be in hidden backpack", solo.waitForText(unpack, 1, TIME_TO_WAIT_BACKPACK));
		assertTrue("Scripts are not in hidden backpack!", BackPackListManager.getInstance().getHiddenBackpackedScripts().size() == 2);
		solo.goBack();

		switchToProgrammeBackgroundFromSpritesList(UiTestUtils.PROJECTNAME3);
		solo.waitForText(solo.getString(R.string.background));
		solo.clickOnText(solo.getString(R.string.background));
		solo.waitForText(solo.getString(R.string.scripts));
		solo.clickOnText(solo.getString(R.string.scripts));
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		ListView listView = solo.getCurrentViews(ListView.class).get(solo.getCurrentViews(ListView.class).size() - 1);
		int brickCountInView = listView.getCount();
		int numberOfBricksInBrickList = ProjectManager.getInstance().getCurrentSprite().getNumberOfBricks();

		solo.goBack();
		solo.goBack();

		UiTestUtils.openBackPack(solo);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		clickOnBackPackItem(SPRITE_NAME_BACKGROUND, unpackAsBackGround);
		solo.waitForDialogToOpen();
		solo.waitForText(solo.getString(R.string.unpack_background));
		solo.clickOnText(solo.getString(R.string.ok));
		solo.waitForDialogToClose();
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);
		solo.sleep(TIME_TO_WAIT_BACKPACK);

		solo.clickOnText(solo.getString(R.string.background));
		solo.clickOnText(solo.getString(R.string.scripts));
		solo.sleep(TIME_TO_WAIT_BACKPACK);

		assertEquals("Brick count in current sprite not correct", numberOfBricksInBrickList + 8,
				ProjectManager.getInstance().getCurrentSprite().getNumberOfBricks());
		listView = solo.getCurrentViews(ListView.class).get(solo.getCurrentViews(ListView.class).size() - 1);
		assertEquals("Brick count in list view not correct", brickCountInView + 9, listView.getCount());

		ProjectManager projectManager = ProjectManager.getInstance();
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		DataContainer dataContainer = projectManager.getCurrentProject().getDefaultScene().getDataContainer();
		UserVariable spriteUserVariable = dataContainer.getUserVariable(projectManager.getCurrentSprite(), "sprite_var");
		UserVariable projectUserVariable = dataContainer.getProjectVariables().get(0);
		UserList projectUserList = dataContainer.getUserList(null, "global_list");
		UserList spriteUserList = dataContainer.getSpriteListOfLists(projectManager.getCurrentSprite()).get(0);
		assertTrue("Project user list was not unpacked", projectUserList.getName().equals("global_list"));
		assertTrue("Sprite user list was not unpacked", spriteUserList.getName().equals("sprite_list"));
		assertTrue("Project user variable was not unpacked", projectUserVariable.getName().equals("global_var"));
		assertTrue("Project user variable was not unpacked", spriteUserVariable.getName().equals("sprite_var"));

		List<Brick> unpackedBricks = projectManager.getCurrentSprite().getListWithAllBricks();

		assertTrue("Brick does not contain sprite user list", ((AddItemToUserListBrick) unpackedBricks.get(4))
				.getUserList().getName().equals("sprite_list"));
		assertTrue("Brick does not contain project user list", ((AddItemToUserListBrick) unpackedBricks.get(5))
				.getUserList().getName().equals("global_list"));
		assertTrue("Brick does not contain sprite user variable", ((SetVariableBrick) unpackedBricks.get(6))
				.getUserVariable().getName().equals("sprite_var"));
		assertTrue("Brick does not contain project user variable", ((ChangeVariableBrick) unpackedBricks.get(7))
				.getUserVariable().getName().equals("global_var"));

		solo.goBack();
		solo.clickOnText(solo.getString(R.string.backgrounds));
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		assertTrue("Look was not unpacked!", solo.waitForText(TEST_LOOK_NAME, 1, TIME_TO_WAIT_BACKPACK));
		solo.goBack();

		solo.clickOnText(solo.getString(R.string.sounds));
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		assertTrue("Sound was not unpacked!", solo.waitForText(TEST_SOUND_NAME, 1, TIME_TO_WAIT_BACKPACK));
		solo.goBack();
		solo.goBack();

		assertTrue("Sprite from PointToBrick was not unpacked!", solo.waitForText("dog", 1, TIME_TO_WAIT_BACKPACK));
	}

	public void testBackPackSpriteWithUserBrick() {
		solo.goBack();
		UiTestUtils.createTestProjectWithUserBrick();
		solo.clickOnText(continueMenu);
		solo.waitForActivity(ProjectActivity.class);
		solo.waitForFragmentByTag(SpritesListFragment.TAG);

		SpriteAdapter adapter = getSpriteAdapter();
		assertNotNull("Could not get Adapter", adapter);
		clickOnActionModeSingleItem(SPRITE_NAME_BACKGROUND, R.string.backpack, R.id.backpack);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		switchToProgrammeBackgroundFromBackpack(UiTestUtils.PROJECTNAME1);

		UiTestUtils.openBackPack(solo);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		clickOnBackPackItem(SPRITE_NAME_BACKGROUND, unpackAsObject);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);
		assertTrue("Sprite wasn't unpacked!", solo.waitForText(SPRITE_NAME_BACKGROUND, 0,
				TIME_TO_WAIT_BACKPACK, false, true));

		solo.clickOnText(SPRITE_NAME_BACKGROUND);
		solo.clickOnText(solo.getString(R.string.scripts));
		solo.waitForActivity(ScriptActivity.class);
		solo.waitForFragmentByTag(ScriptFragment.TAG);
		UiTestUtils.getIntoUserBrickOverView(solo);
		assertTrue("No UserBrick was unpacked!", solo.waitForText(UiTestUtils.TEST_USER_BRICK_NAME, 0,
				TIME_TO_WAIT_BACKPACK, false, true));
	}

	public void testBackPackAlreadyPackedDialogSingleItem() {
		packSingleItem(SPRITE_NAME, true);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		assertTrue("Sprite wasn't backpacked!", solo.waitForText(SPRITE_NAME, 0, TIME_TO_WAIT, false, true));
		solo.goBack();
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		packSingleItem(SPRITE_NAME, false);
		solo.waitForDialogToOpen();
		assertTrue("Sprite already exists backpack dialog not shown!", solo.waitForText(backpackReplaceDialogMultiple, 0,
				TIME_TO_WAIT, false, true));
		solo.clickOnButton(solo.getString(R.string.yes));
		solo.waitForDialogToClose();
		solo.waitForActivity(BackPackActivity.class);
		solo.waitForFragmentByTag(SpritesListFragment.TAG);
		solo.sleep(200);

		assertTrue("Should be in backpack!", solo.waitForText(backpackTitle, 0, TIME_TO_WAIT, false, true));
		assertTrue("Sprite wasn't backpacked!", solo.waitForText(SPRITE_NAME, 0, TIME_TO_WAIT, false, true));
		assertTrue("Sprite was not replaced!", BackPackListManager.getInstance().getBackPackedSprites().size() == 1);
	}

	public void testBackPackAlreadyPackedDialogMultipleItems() {
		UiTestUtils.backPackAllItems(solo, getActivity(), SPRITE_NAME, SPRITE_NAME2);
		assertTrue("Sprite wasn't backpacked!", solo.waitForText(SPRITE_NAME, 0, TIME_TO_WAIT));
		solo.goBack();
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		UiTestUtils.openBackPackActionMode(solo);
		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());
		UiTestUtils.clickOnText(solo, selectAll);
		UiTestUtils.acceptAndCloseActionMode(solo);

		solo.waitForDialogToOpen();
		assertTrue("Sprite already exists backpack dialog not shown!", solo.waitForText(backpackReplaceDialogMultiple, 0,
				TIME_TO_WAIT));
		solo.clickOnButton(solo.getString(R.string.yes));
		solo.waitForDialogToClose();
		solo.waitForActivity(BackPackActivity.class);
		solo.waitForFragmentByTag(SpritesListFragment.TAG);
		solo.sleep(200);

		assertTrue("Should be in backpack!", solo.waitForText(backpackTitle, 0, TIME_TO_WAIT));
		assertTrue("Sprite wasn't backpacked!", solo.waitForText(SPRITE_NAME_BACKGROUND, 0, TIME_TO_WAIT));
		assertTrue("Sprite wasn't backpacked!", solo.waitForText(SPRITE_NAME, 0, TIME_TO_WAIT));
		assertTrue("Sprite wasn't backpacked!", solo.waitForText(SPRITE_NAME2, 0, TIME_TO_WAIT));
		assertTrue("Sprite was not replaced!", BackPackListManager.getInstance().getBackPackedSprites().size() == 3);
	}

	public void testBackPackSerializationAndDeserialization() {
		File backPackFile = new File(buildPath(DEFAULT_ROOT, BACKPACK_DIRECTORY, StorageHandler.BACKPACK_FILENAME));
		assertFalse("Backpack.json should not exist!", backPackFile.exists());
		UiTestUtils.backPackAllItems(solo, getActivity(), SPRITE_NAME_BACKGROUND, SPRITE_NAME);
		solo.goBack();
		solo.sleep(TIME_TO_WAIT_BACKPACK);

		assertTrue("No items have been backpacked!", !BackPackListManager.getInstance().getBackpack()
				.backpackedSprites.isEmpty());
		backPackFile = new File(buildPath(DEFAULT_ROOT, BACKPACK_DIRECTORY, StorageHandler.BACKPACK_FILENAME));
		assertTrue("Backpack.json has not been saved!", backPackFile.exists());

		UiTestUtils.clearBackPack(false);
		solo.sleep(TIME_TO_WAIT);
		assertTrue("Backpacked items not deleted!", BackPackListManager.getInstance().getBackpack()
				.backpackedSprites.isEmpty());

		BackPackListManager.getInstance().loadBackpack();
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		assertTrue("Backpacked items haven't been restored from backpack.json!", !BackPackListManager.getInstance()
				.getBackpack().backpackedSprites.isEmpty());
	}

	public void testBackPackWithGroups() {
		String firstTestItemNamePacked = SPRITE_NAME_BACKGROUND;
		String secondTestItemNamePacked = "third_sprite";

		prepareGroupingTest(true);

		SpriteAdapter adapter = getSpriteAdapter();
		assertNotNull("Could not get Adapter", adapter);

		UiTestUtils.checkAllItemsForBackpack(solo, getActivity());
		assertEquals("It should not be possible to backpack group items, but more or less items are checked", 6, adapter
				.getAmountOfCheckedItems());
		UiTestUtils.backpackAllCheckedItems(solo, firstTestItemNamePacked, secondTestItemNamePacked);

		SpriteListAdapter spriteListAdapter = getSpriteListAdapter();
		assertNotNull("Could not get Adapter", spriteListAdapter);

		assertEquals("Wrong number of items in backpack", 6, spriteListAdapter.getCount());
		clickOnBackPackItem(firstTestItemNamePacked, unpackAsObject);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);
		assertEquals("Item was not unpacked from backpack", 5, adapter.getGroupCount());
		assertEquals("Item was unpacked as GroupItem instead of as a SingleSprite", 3, adapter.getChildrenCount(2));
	}

	public void testCopyWithGroups() {
		prepareGroupingTest(true);

		SpriteAdapter adapter = getSpriteAdapter();
		assertNotNull("Could not get Adapter", adapter);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy);
		UiTestUtils.selectAllItems(solo);

		assertEquals("It should not be possible to copy GroupSprites and the background sprite, but more or less "
				+ "items are checked", 5, adapter.getAmountOfCheckedItems());
		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.sleep(TIME_TO_WAIT_BACKPACK);

		assertEquals("Items were not copied as groups", 9, adapter.getGroupCount());
		for (int copiedGroupPosition = 4; copiedGroupPosition < 9; copiedGroupPosition++) {
			assertTrue("Item not copied as SingleSprite at position " + copiedGroupPosition,
					adapter.getGroup(copiedGroupPosition) instanceof SingleSprite);
		}
	}

	public void testDeleteWithGroups() {
		prepareGroupingTest(true);

		SpriteAdapter adapter = getSpriteAdapter();
		assertNotNull("Could not get Adapter", adapter);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete);
		UiTestUtils.selectAllItems(solo);
		solo.sleep(TIME_TO_WAIT);

		assertEquals("It should not be possible to delete the background sprite, but it seems to be checked", 7, adapter
				.getAmountOfCheckedItems());
		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.waitForDialogToOpen();
		if (solo.waitForText(solo.getString(R.string.yes), 1, 800)) {
			solo.clickOnButton(solo.getString(R.string.yes));
		}
		solo.sleep(TIME_TO_WAIT);

		assertEquals("Probably group items have been deleted", 1, adapter.getGroupCount());
		assertEquals("The wrong number of items is in the spritelist", 1, ProjectManager.getInstance()
				.getCurrentScene().getSpriteList().size());
	}

	public void testShowAndHideDetails() {
		prepareGroupingTest(true);
		int timeToWait = 500;

		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, GONE);
		solo.clickOnMenuItem(solo.getString(R.string.show_details));
		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, VISIBLE, VISIBLE, GONE);
		assertFalse("Object detail text is shown, but shouldn't", solo.searchText(solo.getString(R.string
				.number_of_objects), 0, false, true));

		// Test if showDetails is remembered after pressing back
		solo.goBack();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		solo.waitForText(continueMenu);
		solo.clickOnText(continueMenu);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, VISIBLE, VISIBLE, GONE);

		solo.sleep(timeToWait);
		solo.clickOnMenuItem(solo.getString(R.string.hide_details));
		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, GONE);
	}

	public void testGroupAddRenameAndDeleteActions() {
		prepareGroupingTest(true);
		String defaultGroupName = solo.getString(R.string.group) + " 3";
		String renamedGroupName = solo.getString(R.string.group) + " 4";

		String firstGroupName = "second_sprite";
		String secondGroupName = "fourth_sprite";

		String create = solo.getString(R.string.groups_create);

		SpriteAdapter adapter = getSpriteAdapter();
		assertNotNull("Could not get Adapter", adapter);

		UiTestUtils.openActionMode(solo, create, R.id.groups_create);
		solo.waitForDialogToOpen();
		assertTrue("Wrong or no default text appeared for new group", solo.searchText(defaultGroupName, 0, false, true));
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.waitForDialogToClose();
		solo.sleep(TIME_TO_WAIT);

		assertTrue("Group has not been added", solo.searchText(defaultGroupName, 0, false, true));
		assertEquals("Wrong group count", 5, adapter.getGroupCount());

		UiTestUtils.openActionMode(solo, rename, R.id.rename);
		solo.clickOnText(defaultGroupName);
		UiTestUtils.acceptAndCloseActionMode(solo);
		renameGroup(renamedGroupName, true, secondGroupName);

		UiTestUtils.openActionMode(solo, rename, R.id.rename);
		solo.clickOnText(renamedGroupName);
		UiTestUtils.acceptAndCloseActionMode(solo);
		renameGroup(defaultGroupName, false, null);

		UiTestUtils.openActionMode(solo, delete, R.id.delete);
		solo.scrollUp();
		solo.clickOnText(firstGroupName);
		UiTestUtils.acceptAndCloseActionMode(solo);
		deleteGroup(true);

		assertEquals("Group has not been deleted or GroupItemSprite not been converted to SingleSprite", 5, adapter.getGroupCount());
		assertEquals("Group has not been deleted", 2, adapter.getGroupNames().size());
		assertTrue("Group item has not been converted to SingleSprite", ProjectManager.getInstance().getCurrentScene().getSpriteList()
				.get(1) instanceof SingleSprite);

		UiTestUtils.openActionMode(solo, delete, R.id.delete);
		solo.clickOnText(secondGroupName);
		UiTestUtils.acceptAndCloseActionMode(solo);
		deleteGroup(false);

		assertEquals("Group and GroupItemSprites have not been deleted", 4, adapter.getGroupCount());
		assertEquals("Group has not been deleted", 1, adapter.getGroupNames().size());
		assertEquals("Number of spriteList items is wrong", 4, ProjectManager.getInstance().getCurrentScene().getSpriteList().size());
	}

	public void testLoadOldProjectAndConvertSpritesForGrouping() {
		prepareGroupingTest(false);

		assertTrue("Sprite has not been converted to SingleSprite and is therefore not visible",
				solo.searchText(SPRITE_NAME, 0, false, true));

		for (int spritePosition = 0; spritePosition < ProjectManager.getInstance().getCurrentScene().getSpriteList().size();
				spritePosition++) {
			assertTrue("Group item has not been converted to SingleSprite",
					ProjectManager.getInstance().getCurrentScene().getSpriteList().get(spritePosition) instanceof SingleSprite);
		}
	}

	public void testExpandAndCollapseGroup() {
		prepareGroupingTest(true);

		String firstGroupName = "second_sprite";
		String groupItemNameOfFirstGroup = "third_sprite";

		checkGroupIndicatorVisibility(false);
		assertFalse("Group is expanded", solo.searchText(groupItemNameOfFirstGroup, 0, false, true));
		solo.clickOnText(firstGroupName);

		checkGroupIndicatorVisibility(true);
		assertTrue("Group is not expanded", solo.searchText(groupItemNameOfFirstGroup, 0, false, true));

		solo.clickOnText(firstGroupName);
		checkGroupIndicatorVisibility(false);
		assertFalse("Group is expanded", solo.searchText(groupItemNameOfFirstGroup, 0, false, true));
	}

	public void testDragAndDropSingleSpritesAndGroupItemSprites() {
		prepareGroupingTest(true);

		String secondSpriteName = "second_sprite";
		String thirdSpriteName = "third_sprite";
		String fourthSpriteName = "fourth_sprite";
		String fifthSpriteName = "fifth_sprite";
		String sixthSpriteName = "sixth_sprite";
		String seventhSpriteName = "seventh_sprite";
		String eightSpriteName = "eight_sprite";

		solo.clickOnText(secondSpriteName);

		List<Sprite> spriteList = ProjectManager.getInstance().getCurrentScene().getSpriteList();
		ArrayList<Integer> yPositionList = UiTestUtils.getListItemYPositions(solo, 0);

		UiTestUtils.longClickAndDrag(solo, 10, yPositionList.get(2), 10, yPositionList.get(1) - DRAG_AND_DROP_Y_OFFSET, 20);
		assertEquals("Wrong list order after DragAndDrop", thirdSpriteName, spriteList.get(1).getName());
		assertEquals("Wrong list order after DragAndDrop", secondSpriteName, spriteList.get(2).getName());
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(1) instanceof SingleSprite);
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(2) instanceof GroupSprite);

		yPositionList = UiTestUtils.getListItemYPositions(solo, 0);
		UiTestUtils.longClickAndDrag(solo, 10, yPositionList.get(1), 10, yPositionList.get(2) + DRAG_AND_DROP_Y_OFFSET, 20);
		assertEquals("Wrong list order after DragAndDrop", secondSpriteName, spriteList.get(1).getName());
		assertEquals("Wrong list order after DragAndDrop", thirdSpriteName, spriteList.get(2).getName());
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(1) instanceof GroupSprite);
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(2) instanceof GroupItemSprite);

		solo.clickOnText(fourthSpriteName);
		yPositionList = UiTestUtils.getListItemYPositions(solo, 0);
		UiTestUtils.longClickAndDrag(solo, 10, yPositionList.get(4), 10, yPositionList.get(5) + DRAG_AND_DROP_Y_OFFSET, 20);
		assertEquals("Wrong list order after DragAndDrop", sixthSpriteName, spriteList.get(4).getName());
		assertEquals("Wrong list order after DragAndDrop", fifthSpriteName, spriteList.get(5).getName());
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(4) instanceof GroupItemSprite);
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(5) instanceof GroupItemSprite);

		yPositionList = UiTestUtils.getListItemYPositions(solo, 0);
		UiTestUtils.longClickAndDrag(solo, 10, yPositionList.get(4), 10, yPositionList.get(3) - DRAG_AND_DROP_Y_OFFSET, 20);
		assertEquals("Wrong list order after DragAndDrop", sixthSpriteName, spriteList.get(3).getName());
		assertEquals("Wrong list order after DragAndDrop", fourthSpriteName, spriteList.get(4).getName());
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(3) instanceof SingleSprite);
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(4) instanceof GroupSprite);

		solo.clickOnText(fourthSpriteName);
		yPositionList = UiTestUtils.getListItemYPositions(solo, 0);
		UiTestUtils.longClickAndDrag(solo, 10, yPositionList.get(3), 10, yPositionList.get(4) + DRAG_AND_DROP_Y_OFFSET, 20);
		solo.clickOnText(fourthSpriteName);
		assertEquals("Wrong list order after DragAndDrop", fourthSpriteName, spriteList.get(3).getName());
		assertEquals("Wrong list order after DragAndDrop", fifthSpriteName, spriteList.get(4).getName());
		assertEquals("Wrong list order after DragAndDrop", seventhSpriteName, spriteList.get(5).getName());
		assertEquals("Wrong list order after DragAndDrop", sixthSpriteName, spriteList.get(6).getName());
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(3) instanceof GroupSprite);
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(4) instanceof GroupItemSprite);
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(5) instanceof GroupItemSprite);
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(6) instanceof GroupItemSprite);
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(7) instanceof SingleSprite);

		solo.clickOnText(fourthSpriteName);
		yPositionList = UiTestUtils.getListItemYPositions(solo, 0);
		UiTestUtils.longClickAndDrag(solo, 10, yPositionList.get(4), 10, yPositionList.get(3) - DRAG_AND_DROP_Y_OFFSET, 20);
		assertEquals("Wrong list order after DragAndDrop", fourthSpriteName, spriteList.get(3).getName());
		assertEquals("Wrong list order after DragAndDrop", fifthSpriteName, spriteList.get(4).getName());
		assertEquals("Wrong list order after DragAndDrop", seventhSpriteName, spriteList.get(5).getName());
		assertEquals("Wrong list order after DragAndDrop", sixthSpriteName, spriteList.get(6).getName());
		assertEquals("Wrong list order after DragAndDrop", eightSpriteName, spriteList.get(7).getName());
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(3) instanceof GroupSprite);
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(4) instanceof GroupItemSprite);
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(5) instanceof GroupItemSprite);
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(6) instanceof GroupItemSprite);
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(7) instanceof GroupItemSprite);
	}

	public void testDragAndDropGroupSprites() {
		prepareGroupingTest(true);

		String secondSpriteName = "second_sprite";
		String thirdSpriteName = "third_sprite";
		String fourthSpriteName = "fourth_sprite";
		String fifthSpriteName = "fifth_sprite";
		String sixthSpriteName = "sixth_sprite";
		String seventhSpriteName = "seventh_sprite";
		String eightSpriteName = "eight_sprite";

		List<Sprite> spriteList = ProjectManager.getInstance().getCurrentScene().getSpriteList();
		ArrayList<Integer> yPositionList = UiTestUtils.getListItemYPositions(solo, 0);
		UiTestUtils.longClickAndDrag(solo, 10, yPositionList.get(1), 10, yPositionList.get(2) + DRAG_AND_DROP_Y_OFFSET, 20);

		assertEquals("Wrong list order after DragAndDrop", fourthSpriteName, spriteList.get(1).getName());
		assertEquals("Wrong list order after DragAndDrop", fifthSpriteName, spriteList.get(2).getName());
		assertEquals("Wrong list order after DragAndDrop", sixthSpriteName, spriteList.get(3).getName());
		assertEquals("Wrong list order after DragAndDrop", seventhSpriteName, spriteList.get(4).getName());
		assertEquals("Wrong list order after DragAndDrop", secondSpriteName, spriteList.get(5).getName());
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(1) instanceof GroupSprite);
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(2) instanceof GroupItemSprite);
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(3) instanceof GroupItemSprite);
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(4) instanceof GroupItemSprite);
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(5) instanceof GroupSprite);

		yPositionList = UiTestUtils.getListItemYPositions(solo, 0);
		UiTestUtils.longClickAndDrag(solo, 10, yPositionList.get(2), 10, yPositionList.get(1) - DRAG_AND_DROP_Y_OFFSET, 20);

		assertEquals("Wrong list order after DragAndDrop", secondSpriteName, spriteList.get(1).getName());
		assertEquals("Wrong list order after DragAndDrop", thirdSpriteName, spriteList.get(2).getName());
		assertEquals("Wrong list order after DragAndDrop", fourthSpriteName, spriteList.get(3).getName());
		assertEquals("Wrong list order after DragAndDrop", fifthSpriteName, spriteList.get(4).getName());
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(1) instanceof GroupSprite);
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(2) instanceof GroupItemSprite);
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(3) instanceof GroupSprite);
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(4) instanceof GroupItemSprite);

		yPositionList = UiTestUtils.getListItemYPositions(solo, 0);
		UiTestUtils.longClickAndDrag(solo, 10, yPositionList.get(2), 10, yPositionList.get(3) + DRAG_AND_DROP_Y_OFFSET, 20);

		assertEquals("Wrong list order after DragAndDrop", eightSpriteName, spriteList.get(3).getName());
		assertEquals("Wrong list order after DragAndDrop", fourthSpriteName, spriteList.get(4).getName());
		assertEquals("Wrong list order after DragAndDrop", fifthSpriteName, spriteList.get(5).getName());
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(3) instanceof SingleSprite);
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(4) instanceof GroupSprite);
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(5) instanceof GroupItemSprite);

		yPositionList = UiTestUtils.getListItemYPositions(solo, 0);
		UiTestUtils.longClickAndDrag(solo, 10, yPositionList.get(3), 10, yPositionList.get(2) - DRAG_AND_DROP_Y_OFFSET, 20);

		assertEquals("Wrong list order after DragAndDrop", fourthSpriteName, spriteList.get(3).getName());
		assertEquals("Wrong list order after DragAndDrop", fifthSpriteName, spriteList.get(4).getName());
		assertEquals("Wrong list order after DragAndDrop", sixthSpriteName, spriteList.get(5).getName());
		assertEquals("Wrong list order after DragAndDrop", seventhSpriteName, spriteList.get(6).getName());
		assertEquals("Wrong list order after DragAndDrop", eightSpriteName, spriteList.get(7).getName());
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(3) instanceof GroupSprite);
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(4) instanceof GroupItemSprite);
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(5) instanceof GroupItemSprite);
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(6) instanceof GroupItemSprite);
		assertTrue("Sprite has incorrect instance after DragAndDrop", spriteList.get(7) instanceof SingleSprite);
	}

	public void testUploadProjectGoToWebViewActivityAndReturnToSpritesListFragment() throws Throwable {
		String newProjectName = "newName";

		setServerURLToTestUrl();
		UiTestUtils.createValidUser(getActivity());

		UiTestUtils.openActionMode(solo, upload, R.id.upload);
		solo.waitForDialogToOpen();

		solo.clearEditText(0);
		solo.enterText(0, newProjectName);
		solo.clickOnText(next);
		solo.waitForText(solo.getString(R.string.upload_tag_dialog_title));
		solo.clickOnText(next);
		if (solo.searchText(solo.getString(R.string.rating_dialog_title))) {
			solo.clickOnText(solo.getString(R.string.rating_dialog_rate_later));
		}
		solo.waitForText(showProgram);
		solo.clickOnText(showProgram);

		solo.waitForActivity(WebViewActivity.class);
		solo.sleep(1000);
		solo.goBack();
		solo.waitForActivity(ScriptActivity.class);
		solo.waitForFragmentByTag(SpritesListFragment.TAG);
		assertTrue("Project was renamed correctly after return from WebView!", solo.searchText(newProjectName, 0, false, true));
	}

	private void clickOnActionModeSingleItem(String spriteName, int menuItem, int menuItemId) {
		String menuItemName = solo.getString(menuItem);
		UiTestUtils.openActionMode(solo, menuItemName, menuItemId);
		solo.clickOnText(spriteName);
		solo.sleep(TIME_TO_WAIT);
		UiTestUtils.acceptAndCloseActionMode(solo);
	}

	private void packSingleItem(String spriteName, boolean backPackEmpty) {
		UiTestUtils.openActionMode(solo, backpack, R.string.backpack);
		if (!backPackEmpty) {
			solo.waitForDialogToOpen();
			solo.clickOnText(backpackAdd);
			solo.sleep(TIME_TO_WAIT_BACKPACK);
		}
		solo.clickOnText(spriteName);
		solo.sleep(TIME_TO_WAIT);
		UiTestUtils.acceptAndCloseActionMode(solo);
	}

	private void clickOnBackPackItem(String spriteName, String menuItemName) {
		solo.clickOnText(spriteName);
		solo.sleep(TIME_TO_WAIT);
		solo.clickOnText(menuItemName);
		solo.sleep(TIME_TO_WAIT);
	}

	private BackPackSpriteListFragment getBackPackSpriteFragment() {
		BackPackActivity activity = (BackPackActivity) solo.getCurrentActivity();
		return (BackPackSpriteListFragment) activity.getFragment(BackPackActivity.FRAGMENT_BACKPACK_SPRITES);
	}

	private SpritesListFragment getSpritesListFragment() {
		ProjectActivity activity = (ProjectActivity) solo.getCurrentActivity();
		return activity.getSpritesListFragment();
	}

	private SpriteAdapter getSpriteAdapter() {
		solo.waitForActivity(ProjectActivity.class);
		solo.waitForFragmentByTag(SpritesListFragment.TAG);
		return getSpritesListFragment().getSpriteAdapter();
	}

	private SpriteListAdapter getSpriteListAdapter() {
		return (SpriteListAdapter) getBackPackSpriteFragment().getListAdapter();
	}

	private void checkVisibilityOfViews(int imageVisibility, int lookNameVisibility, int lookDetailsVisibility,
			int checkBoxVisibility) {
		solo.sleep(200);
		assertTrue("Sprite image " + getAssertMessageAffix(imageVisibility),
				solo.getView(R.id.sprite_img).getVisibility() == imageVisibility);
		assertTrue("Sprite name " + getAssertMessageAffix(lookNameVisibility),
				solo.getView(R.id.project_activity_sprite_title).getVisibility() == lookNameVisibility);
		assertTrue("Sprite details " + getAssertMessageAffix(lookDetailsVisibility),
				solo.getView(R.id.project_activity_sprite_details).getVisibility() == lookDetailsVisibility);
		assertTrue("Checkboxes " + getAssertMessageAffix(checkBoxVisibility),
				solo.getView(R.id.sprite_checkbox).getVisibility() == checkBoxVisibility);
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

	private void checkIfCheckboxesAreCorrectlyChecked(boolean firstCheckboxExpectedChecked,
			boolean secondCheckboxExpectedChecked, boolean thirdCheckboxExpectedChecked) {
		solo.sleep(300);
		CheckBox firstCheckBox = solo.getCurrentViews(CheckBox.class).get(0);
		CheckBox secondCheckBox = solo.getCurrentViews(CheckBox.class).get(1);
		CheckBox thirdCheckBox = solo.getCurrentViews(CheckBox.class).get(2);
		if (solo.getCurrentViews(CheckBox.class).size() > 3) {
			firstCheckBox = solo.getCurrentViews(CheckBox.class).get(3);
			secondCheckBox = solo.getCurrentViews(CheckBox.class).get(4);
			thirdCheckBox = solo.getCurrentViews(CheckBox.class).get(5);
		}
		assertEquals("First checkbox not correctly checked", firstCheckboxExpectedChecked, firstCheckBox.isChecked());
		assertEquals("Second checkbox not correctly checked", secondCheckboxExpectedChecked, secondCheckBox.isChecked());
		assertEquals("Third checkbox not correctly checked", thirdCheckboxExpectedChecked, thirdCheckBox.isChecked());
	}

	private void deleteSprite(String spriteName) {
		clickOnActionModeSingleItem(spriteName, R.string.delete, R.id.delete);
		solo.waitForDialogToOpen();
		solo.waitForText(solo.getString(R.string.yes));
		solo.clickOnText(solo.getString(R.string.yes));
	}

	private void checkIfNumberOfSpritesIsEqual(int expectedNumber) {
		List<Sprite> spriteList = ProjectManager.getInstance().getCurrentProject().getDefaultScene().getSpriteList();
		assertEquals("Number of sprites is not as expected", expectedNumber, spriteList.size());
	}

	private void addSpriteWithName(String spriteName) {
		Sprite spriteToAdd = sprite.clone();
		spriteToAdd.setName(spriteName);
		ProjectManager.getInstance().getCurrentScene().addSprite(spriteToAdd);
	}

	private void switchToProgrammeBackgroundFromBackpack(String programmeName) {
		solo.waitForText(solo.getString(R.string.backpack_title));
		solo.goBack();
		solo.goBack();
		solo.waitForText(solo.getString(R.string.programs));
		solo.clickOnText(solo.getString(R.string.programs));
		solo.waitForText(programmeName);
		solo.clickOnText(programmeName);
		solo.waitForText(solo.getString(R.string.background));
	}

	private void switchToProgrammeBackgroundFromSpritesList(String programmeName) {
		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.waitForText(solo.getString(R.string.programs));
		solo.clickOnText(solo.getString(R.string.programs));
		solo.waitForText(programmeName);
		solo.clickOnText(programmeName);
		solo.waitForText(solo.getString(R.string.background));
	}

	private void prepareGroupingTest(boolean createProjectWithGroups) {
		solo.goBack();
		if (createProjectWithGroups) {
			UiTestUtils.createTestProjectWithGroups();
			solo.clickOnText(continueMenu);
		} else {
			solo.clickOnText(solo.getString(R.string.programs));
			solo.waitForText(OLD_SPRITE_PROJECT);
			solo.clickOnText(OLD_SPRITE_PROJECT);
		}

		project = ProjectManager.getInstance().getCurrentProject();
		solo.waitForActivity(ProjectActivity.class);
		solo.waitForFragmentByTag(SpritesListFragment.TAG);
	}

	private void renameGroup(String newName, boolean checkGivenName, String givenName) {
		solo.waitForDialogToOpen();
		if (checkGivenName) {
			solo.clearEditText(0);
			solo.enterText(0, givenName);
			solo.clickOnButton(solo.getString(R.string.ok));
			assertTrue("Group was not renamed", solo.searchText(solo.getString(R.string.spritename_already_exists), 0, false, true));
			solo.clickOnButton(solo.getString(R.string.close));
			solo.waitForDialogToClose();
		}
		solo.clearEditText(0);
		solo.enterText(0, newName);
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.waitForDialogToClose();
		solo.scrollDown();
		assertTrue("Group was not renamed", solo.searchText(newName, 0, false, true));
	}

	private void deleteGroup(boolean onlyGroup) {
		String deleteGroupOnly = solo.getString(R.string.ungroup);
		String deleteGroupAndObjects = solo.getString(R.string.group_objects_delete);

		solo.waitForDialogToOpen();
		solo.waitForText(solo.getString(R.string.yes));
		solo.clickOnText(solo.getString(R.string.yes));

		solo.waitForDialogToOpen();
		if (onlyGroup) {
			solo.waitForText(deleteGroupOnly);
			solo.clickOnText(deleteGroupOnly);
		} else {
			solo.waitForText(deleteGroupAndObjects);
			solo.clickOnText(deleteGroupAndObjects);
		}
		solo.waitForDialogToClose();
		solo.sleep(TIME_TO_WAIT);
	}

	private void checkGroupIndicatorVisibility(boolean isExpanded) {
		solo.sleep(200);
		Resources resources = solo.getCurrentActivity().getResources();
		Drawable expandedDrawable = resources.getDrawable(R.drawable.ic_play_down);
		Drawable collapsedDrawable = resources.getDrawable(R.drawable.ic_play);

		if (isExpanded) {
			assertTrue("expandedIndicator not shown", expandedDrawable != null && expandedDrawable.isVisible());
		} else {
			assertTrue("collapsedIndicator not shown", collapsedDrawable != null && collapsedDrawable.isVisible());
		}
	}

	private void setServerURLToTestUrl() throws Throwable {
		runTestOnUiThread(new Runnable() {
			public void run() {
				ServerCalls.useTestUrl = true;
			}
		});
	}
}
