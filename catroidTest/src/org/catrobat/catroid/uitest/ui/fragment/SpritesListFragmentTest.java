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
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;

import com.robotium.solo.By;
import com.robotium.solo.WebElement;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.AddItemToUserListBrick;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.BackPackActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BackPackSpriteAdapter;
import org.catrobat.catroid.ui.adapter.SpriteAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.fragment.BackPackSpriteFragment;
import org.catrobat.catroid.ui.fragment.SpritesListFragment;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.Utils;

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
	private String backpackReplaceDialogSingle;
	private String backpackReplaceDialogMultiple;

	private List<Sprite> spriteList;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		UiTestUtils.createTestProject(UiTestUtils.PROJECTNAME1);
		UiTestUtils.createTestProject();

		project = ProjectManager.getInstance().getCurrentProject();
		sprite = new Sprite(SPRITE_NAME);
		sprite2 = new Sprite(SPRITE_NAME2);
		project.addSprite(sprite);
		project.addSprite(sprite2);
		project.getDataContainer().addSpriteUserVariableToSprite(sprite, LOCAL_VARIABLE_NAME);
		project.getDataContainer().getUserVariable(LOCAL_VARIABLE_NAME, sprite).setValue(LOCAL_VARIABLE_VALUE);

		project.getDataContainer().addProjectUserVariable(GLOBAL_VARIABLE_NAME);
		project.getDataContainer().getUserVariable(GLOBAL_VARIABLE_NAME, null).setValue(GLOBAL_VARIABLE_VALUE);

		ProjectManager.getInstance().setProject(project);

		Resources resources = getActivity().getBaseContext().getResources();
		continueMenu = solo.getString(R.string.main_menu_continue);
		rename = solo.getString(R.string.rename);
		backpackTitle = solo.getString(R.string.backpack_title);
		delete = solo.getString(R.string.delete);
		copy = solo.getString(R.string.copy);
		unpack = solo.getString(R.string.unpack);
		unpackAsObject = solo.getString(R.string.unpack_object);
		unpackAsBackGround = solo.getString(R.string.unpack_bg);
		backpack = solo.getString(R.string.backpack);
		backpackAdd = solo.getString(R.string.backpack_add);
		backpackReplaceDialogSingle = resources.getString(R.string.backpack_replace_object, SPRITE_NAME);
		backpackReplaceDialogMultiple = solo.getString(R.string.backpack_replace_object_multiple);

		UiTestUtils.clearBackPack(true);
		solo.clickOnText(solo.getString(R.string.main_menu_continue));

		SpriteAdapter adapter = getSpriteAdapter();
		if (adapter != null && adapter.getShowDetails()) {
			solo.clickOnMenuItem(solo.getString(R.string.hide_details), true);
			solo.sleep(TIME_TO_WAIT);
		}
	}

	public void testLocalVariablesWhenSpriteCopiedFromSpritesListFragment() {
		solo.clickLongOnText(SPRITE_NAME);
		solo.clickOnText(solo.getString(R.string.copy));

		String copiedSpriteName = SPRITE_NAME + solo.getString(R.string.copy_sprite_name_suffix);
		solo.waitForText(copiedSpriteName);
		assertTrue(copiedSpriteName + " not found!", solo.searchText(copiedSpriteName));

		Sprite clonedSprite = null;
		for (Sprite tempSprite : project.getSpriteList()) {
			if (tempSprite.getName().equals(copiedSpriteName)) {
				clonedSprite = tempSprite;
			}
		}

		if (clonedSprite == null) {
			fail("no cloned sprite in project");
		}

		List<UserVariable> userVariableList = project.getDataContainer().getOrCreateVariableListForSprite(clonedSprite);
		Set<String> hashSet = new HashSet<String>();
		for (UserVariable userVariable : userVariableList) {
			assertTrue("Variable already exists", hashSet.add(userVariable.getName()));
		}
	}

	public void testSelectAllActionModeButton() {
		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());
		String deselectAll = solo.getString(R.string.deselect_all).toUpperCase(Locale.getDefault());
		solo.sleep(TIME_TO_WAIT);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy, getActivity());
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

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete, getActivity());
		assertTrue("Select All is not shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), true));

		UiTestUtils.clickOnText(solo, selectAll);
		assertTrue("Deselect All is not shown", solo.searchText(deselectAll, 1, false, true));

		solo.clickOnCheckBox(0);
		assertTrue("Deselect All is not shown", solo.searchText(deselectAll, 1, false, true));

		solo.clickOnCheckBox(0);
		assertTrue("Deselect All is not shown", solo.searchText(deselectAll, 1, false, true));
	}

	public void testMoveSpriteUp() {
		project.addSprite(sprite2);
		solo.sleep(TIME_TO_WAIT);
		moveSpriteUp(SPRITE_NAME2);
		solo.sleep(TIME_TO_WAIT);
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.main_menu_continue));

		assertEquals("Sprite didn't move up (testMoveSpriteUp 1)", SPRITE_NAME2, getSpriteName(1));
		assertEquals("Sprite didn't move up (testMoveSpriteUp 2)", SPRITE_NAME, getSpriteName(2));
		project.removeSprite(sprite2);
	}

	public void testMoveSpriteDown() {
		project.addSprite(sprite2);
		solo.sleep(TIME_TO_WAIT);
		moveSpriteDown(SPRITE_NAME);
		solo.sleep(TIME_TO_WAIT);
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.main_menu_continue));

		assertEquals("Sprite didn't move down (testMoveSpriteDown 1)", SPRITE_NAME2, getSpriteName(1));
		assertEquals("Sprite didn't move down (testMoveSpriteDown 2)", SPRITE_NAME, getSpriteName(2));
		project.removeSprite(sprite2);
	}

	public void testMoveSpriteToBottom() {
		project.addSprite(sprite2);
		solo.sleep(TIME_TO_WAIT);
		moveSpriteToBottom(SPRITE_NAME);
		solo.sleep(TIME_TO_WAIT);
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.main_menu_continue));

		assertEquals("Sprite didn't move bottom (testMoveSpriteToBottom 1)", SPRITE_NAME2, getSpriteName(1));
		assertEquals("Sprite didn't move bottom (testMoveSpriteToBottom 2)", SPRITE_NAME, getSpriteName(2));
		project.removeSprite(sprite2);
	}

	public void testMoveSpriteToTop() {
		project.addSprite(sprite2);
		solo.sleep(TIME_TO_WAIT);
		moveSpriteToTop(SPRITE_NAME2);
		solo.sleep(TIME_TO_WAIT);
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.main_menu_continue));

		assertEquals("Sprite didn't move top (testMoveSpriteToTop 1)", SPRITE_NAME2, getSpriteName(1));
		assertEquals("Sprite didn't move top (testMoveSpriteToTop 2)", SPRITE_NAME, getSpriteName(2));
		project.removeSprite(sprite2);
	}

	public void testMoveSpriteUpFirstEntry() {
		project.addSprite(sprite2);
		solo.sleep(TIME_TO_WAIT);
		moveSpriteUp(SPRITE_NAME);
		solo.sleep(TIME_TO_WAIT);
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.main_menu_continue));

		assertEquals("Sprite moved (testMoveSpriteUpFirstEntry 1)", SPRITE_NAME, getSpriteName(1));
		assertEquals("Sprite moved (testMoveSpriteUpFirstEntry 2)", SPRITE_NAME2, getSpriteName(2));
		project.removeSprite(sprite2);
	}

	public void testMoveSpriteDownLastEntry() {
		project.addSprite(sprite2);
		solo.sleep(TIME_TO_WAIT);
		moveSpriteDown(SPRITE_NAME2);
		solo.sleep(TIME_TO_WAIT);
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.main_menu_continue));

		assertEquals("Sprite moved (testMoveSpriteDownLastEntry 1)", SPRITE_NAME, getSpriteName(1));
		assertEquals("Sprite moved (testMoveSpriteDownLastEntry 2)", SPRITE_NAME2, getSpriteName(2));
		project.removeSprite(sprite2);
	}

	public void testMoveSpriteToTopFirstEntry() {
		project.addSprite(sprite2);
		solo.sleep(TIME_TO_WAIT);
		moveSpriteToTop(SPRITE_NAME);
		solo.sleep(TIME_TO_WAIT);
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.main_menu_continue));

		assertEquals("Sprite moved (testMoveSpriteToTopFirstEntry 1)", SPRITE_NAME, getSpriteName(1));
		assertEquals("Sprite moved (testMoveSpriteToTopFirstEntry 2)", SPRITE_NAME2, getSpriteName(2));
		project.removeSprite(sprite2);
	}

	public void testMoveSpriteToBottomLastEntry() {
		project.addSprite(sprite2);
		solo.sleep(TIME_TO_WAIT);
		moveSpriteToBottom(SPRITE_NAME2);
		solo.sleep(TIME_TO_WAIT);
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.main_menu_continue));

		assertEquals("Sprite moved (testMoveSpriteToBottomLastEntry 1)", SPRITE_NAME, getSpriteName(1));
		assertEquals("Sprite moved (testMoveSpriteToBottomLastEntry 2)", SPRITE_NAME2, getSpriteName(2));
		project.removeSprite(sprite2);
	}

	public void testEmptyActionModeDialogs() {
		solo.goBack();
		UiTestUtils.createEmptyProject();
		solo.clickOnText(continueMenu);
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
		solo.clickOnButton(0);
		solo.waitForDialogToClose();

		UiTestUtils.openBackPackActionModeWhenEmtpy(solo, getActivity());
		solo.waitForDialogToOpen();
		assertFalse("Nothing to backpack dialog shown, but it should be possible to backpack the background", solo
				.waitForText(solo.getString(R.string.nothing_to_backpack_and_unpack), 1, TIME_TO_WAIT_BACKPACK));
	}

	public void testEmptyActionModeDialogsInBackPack() {
		UiTestUtils.backPackAllItems(solo, getActivity(), "cat", null);
		UiTestUtils.deleteAllItems(solo, getActivity());

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete, getActivity());
		solo.waitForDialogToOpen();
		assertTrue("Nothing to delete dialog not shown", solo.waitForText(solo.getString(R.string
				.nothing_to_delete)));
		solo.clickOnButton(0);
		solo.waitForDialogToClose();

		UiTestUtils.openActionMode(solo, unpackAsObject, R.id.unpacking_object, getActivity());
		solo.waitForDialogToOpen();
		assertTrue("Nothing to unpack dialog not shown", solo.waitForText(solo.getString(R.string
				.nothing_to_unpack)));
	}

	public void testGetSpriteFromMediaLibrary() {
		String mediaLibraryText = solo.getString(R.string.add_look_media_library);
		int numberSpritesBefore = ProjectManager.getInstance().getCurrentProject().getSpriteList().size();
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
		int numberSpritesAfter = ProjectManager.getInstance().getCurrentProject().getSpriteList().size();
		assertEquals("No Sprite was added!", numberSpritesBefore + 1, numberSpritesAfter);
	}

	@Device
	public void testAddSpriteFromMediaLibraryWithNoInternet() {
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
		assertTrue("Should be in Sprites Fragment", solo.waitForText(SPRITE_NAME));
		wifiManager.setWifiEnabled(true);
		while (!Utils.isNetworkAvailable(getActivity())) {
			solo.sleep(2000);
			if (retryCounter > 30) {
				break;
			}
			retryCounter++;
		}
	}

	public void testBackpackSpriteContextMenu() {
		clickOnContextMenuItem(SPRITE_NAME2, backpackAdd);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		assertTrue("BackPack title didn't show up",
				solo.waitForText(backpackTitle, 0, TIME_TO_WAIT_BACKPACK));
		assertTrue("Sprite wasn't backpacked!", solo.waitForText(SPRITE_NAME2, 0, TIME_TO_WAIT));
	}

	public void testBackpackSpriteDoubleContextMenu() {
		clickOnContextMenuItem(SPRITE_NAME, backpackAdd);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);
		solo.goBack();
		clickOnContextMenuItem(SPRITE_NAME2, backpackAdd);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		assertTrue("BackPack title didn't show up",
				solo.waitForText(backpackTitle, 0, TIME_TO_WAIT_BACKPACK));
		assertTrue("Sprite wasn't backpacked!", solo.waitForText(SPRITE_NAME, 0, TIME_TO_WAIT));
		assertTrue("Sprite wasn't backpacked!", solo.waitForText(SPRITE_NAME2, 0, TIME_TO_WAIT));
	}

	public void testBackPackSpriteSimpleUnpackingContextMenu() {
		clickOnContextMenuItem(SPRITE_NAME, backpackAdd);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		assertTrue("Sprite wasn't backpacked!", solo.waitForText(SPRITE_NAME, 0, TIME_TO_WAIT));

		clickOnContextMenuItem(SPRITE_NAME, unpackAsObject);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		assertTrue("Sprite wasn't unpacked!", solo.waitForText(SPRITE_NAME_UNPACKED, 0, TIME_TO_WAIT));
		deleteSprite(SPRITE_NAME2);
		solo.sleep(TIME_TO_WAIT);
		UiTestUtils.openBackPack(solo, getActivity());

		assertTrue("Backpack is empty!", solo.searchText(backpackTitle));
		assertTrue("Sprite wasn't kept in backpack!", solo.waitForText(SPRITE_NAME, 0, TIME_TO_WAIT));
	}

	public void testBackPackSpriteSimpleUnpackingAndDelete() {
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		SpriteAdapter adapter = getSpriteAdapter();
		assertNotNull("Could not get Adapter", adapter);
		int oldCount = adapter.getCount();

		clickOnContextMenuItem(SPRITE_NAME2, backpackAdd);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		solo.goBack();
		deleteSprite(SPRITE_NAME2);

		solo.sleep(TIME_TO_WAIT);
		UiTestUtils.openBackPack(solo, getActivity());

		clickOnContextMenuItem(SPRITE_NAME2, unpackAsObject);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		assertTrue("Sprite wasn't unpacked!", solo.waitForText(SPRITE_NAME2, 0, TIME_TO_WAIT));

		int newCount = adapter.getCount();
		assertEquals("Counts have to be equal", oldCount, newCount);
	}

	public void testBackPackSpriteMultipleUnpacking() {
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		SpriteAdapter adapter = getSpriteAdapter();
		int oldCount = adapter.getCount();

		assertNotNull("Could not get Adapter", adapter);
		clickOnContextMenuItem(SPRITE_NAME, backpackAdd);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		clickOnContextMenuItem(SPRITE_NAME, unpackAsObject);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		solo.sleep(TIME_TO_WAIT_BACKPACK);
		assertTrue("Sprite wasn't unpacked!", solo.waitForText(SPRITE_NAME_UNPACKED, 0, TIME_TO_WAIT));
		clickOnContextMenuItem(SPRITE_NAME2, backpackAdd);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		clickOnContextMenuItem(SPRITE_NAME2, unpackAsObject);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		solo.sleep(TIME_TO_WAIT_BACKPACK);
		solo.scrollDown();
		solo.sleep(TIME_TO_WAIT);
		assertTrue("Sprite wasn't unpacked!", solo.waitForText(SPRITE_NAME2_UNPACKED, 0, TIME_TO_WAIT));
		int newCount = adapter.getCount();
		assertEquals("There are sprites missing", oldCount + 2, newCount);
	}

	public void testBackPackAndUnPackFromDifferentProgrammes() {
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		SpriteAdapter adapter = getSpriteAdapter();
		assertNotNull("Could not get Adapter", adapter);
		clickOnContextMenuItem(SPRITE_NAME, backpackAdd);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		UiTestUtils.switchToProgrammBackground(solo, UiTestUtils.PROJECTNAME1, SPRITE_NAME_BACKGROUND);
		solo.goBack();

		UiTestUtils.openBackPack(solo, getActivity());
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		clickOnContextMenuItem(SPRITE_NAME, unpackAsObject);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		assertTrue("Sprite wasn't unpacked!", solo.waitForText(SPRITE_NAME, 1, 3000));
	}

	public void testBackPackBackgroundSprite() {
		solo.sleep(TIME_TO_WAIT_BACKPACK);

		UiTestUtils.backPackAllItems(solo, getActivity(), SPRITE_NAME_BACKGROUND, SPRITE_NAME);

		clickOnContextMenuItem(SPRITE_NAME_BACKGROUND, unpackAsBackGround);
		solo.waitForDialogToOpen(TIME_TO_WAIT_BACKPACK);
		assertTrue("No replace background dialog was shown", solo.waitForText(solo.getString(R.string.unpack_background)));
		solo.clickOnText(solo.getString(R.string.ok));
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		assertTrue("Background sprite was not unpacked or renamed to background",
				solo.waitForText(solo.getString(R.string.background)));
	}

	public void testBackPackActionModeCheckingAndTitle() {
		UiTestUtils.openBackPackActionModeWhenEmtpy(solo, getActivity());

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
		UiTestUtils.openBackPackActionModeWhenEmtpy(solo, getActivity());
		int expectedNumberOfSprites = ProjectManager.getInstance().getCurrentProject().getSpriteList().size();
		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);
		checkIfCheckboxesAreCorrectlyChecked(false, false, false);
		UiTestUtils.acceptAndCloseActionMode(solo);
		assertFalse("ActionMode didn't disappear", solo.waitForText(backpack, 1, TIME_TO_WAIT, false, true));
		checkIfNumberOfSpritesIsEqual(expectedNumberOfSprites);

		UiTestUtils.openBackPackActionModeWhenEmtpy(solo, getActivity());
		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);
		checkIfCheckboxesAreCorrectlyChecked(false, false, false);
		solo.goBack();
		assertFalse("ActionMode didn't disappear", solo.waitForText(backpack, 1, TIME_TO_WAIT, false, true));
		checkIfNumberOfSpritesIsEqual(expectedNumberOfSprites);
	}

	public void testBackPackActionModeIfSomethingSelectedAndPressingBack() {
		UiTestUtils.openBackPackActionModeWhenEmtpy(solo, getActivity());
		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(1);
		solo.clickOnCheckBox(2);
		checkIfCheckboxesAreCorrectlyChecked(false, true, true);
		solo.goBack();

		assertFalse("ActionMode didn't disappear", solo.waitForText(backpack, 1, TIME_TO_WAIT, false, true));
		assertFalse("Backpack was opened, but shouldn't be!", solo.waitForText(backpackTitle, 0, TIME_TO_WAIT));
	}

	public void testBackPackSelectAll() {
		UiTestUtils.openBackPackActionModeWhenEmtpy(solo, getActivity());
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

		BackPackSpriteAdapter adapter = getBackPackSpriteAdapter();
		int oldCount = adapter.getCount();
		List<Sprite> backPackSpriteList = BackPackListManager.getInstance().getBackPackedSprites();

		clickOnContextMenuItem(SPRITE_NAME, delete);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		int newCount = adapter.getCount();
		solo.sleep(500);

		assertEquals("Not all sprites were backpacked", 3, oldCount);
		assertEquals("Sprite wasn't deleted in backpack", 2, newCount);
		assertEquals("Count of the backpack spriteList is not correct", newCount, backPackSpriteList.size());
	}

	public void testBackPackSpriteDeleteActionMode() {
		UiTestUtils.backPackAllItems(solo, getActivity(), SPRITE_NAME_BACKGROUND, SPRITE_NAME);

		BackPackSpriteAdapter adapter = getBackPackSpriteAdapter();
		int oldCount = adapter.getCount();
		List<Sprite> backPackSpriteList = BackPackListManager.getInstance().getBackPackedSprites();

		UiTestUtils.deleteAllItems(solo, getActivity());

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
		UiTestUtils.switchToProgrammBackground(solo, UiTestUtils.PROJECTNAME1, SPRITE_NAME);
		solo.goBack();

		UiTestUtils.openBackPack(solo, getActivity());

		UiTestUtils.openActionMode(solo, unpackAsObject, R.id.unpacking_object, getActivity());
		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());
		UiTestUtils.clickOnText(solo, selectAll);
		UiTestUtils.acceptAndCloseActionMode(solo);

		solo.waitForActivity(ProjectActivity.class);
		assertFalse("Background sprite was unpacked, but shouldn't be!", solo.waitForText(SPRITE_NAME_BACKGROUND, 1,
				1000));
		assertTrue("Sprite wasn't unpacked!", solo.waitForText(SPRITE_NAME, 1, 1000));
		UiTestUtils.deleteAllItems(solo, getActivity());
		assertFalse("Sprite wasn't deleted!", solo.waitForText(SPRITE_NAME, 1, 1000));
		assertFalse("Sprite wasn't deleted!", solo.waitForText(SPRITE_NAME2, 1, 1000));
	}

	public void testBackPackDeleteActionModeCheckingAndTitle() {
		solo.sleep(TIME_TO_WAIT_BACKPACK);

		UiTestUtils.backPackAllItems(solo, getActivity(), SPRITE_NAME_BACKGROUND, SPRITE_NAME);
		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());

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
		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());

		int expectedNumberOfSprites = BackPackListManager.getInstance().getBackPackedSprites().size();
		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);
		checkIfCheckboxesAreCorrectlyChecked(false, false, false);
		UiTestUtils.acceptAndCloseActionMode(solo);
		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 1, TIME_TO_WAIT, false, true));
		checkIfNumberOfSpritesIsEqual(expectedNumberOfSprites);

		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());
		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);
		checkIfCheckboxesAreCorrectlyChecked(false, false, false);
		solo.goBack();
		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 1, TIME_TO_WAIT, false, true));
		checkIfNumberOfSpritesIsEqual(expectedNumberOfSprites);
	}

	public void testBackPackDeleteActionModeIfSomethingSelectedAndPressingBack() {
		UiTestUtils.backPackAllItems(solo, getActivity(), SPRITE_NAME_BACKGROUND, SPRITE_NAME);
		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(1);
		solo.clickOnCheckBox(2);
		checkIfCheckboxesAreCorrectlyChecked(false, true, true);
		solo.goBack();

		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 1, TIME_TO_WAIT, false, true));
	}

	public void testBackPackDeleteSelectAll() {
		UiTestUtils.backPackAllItems(solo, getActivity(), SPRITE_NAME_BACKGROUND, SPRITE_NAME);
		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());

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
		UiTestUtils.openBackPack(solo, getActivity());
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

		clickOnContextMenuItem(SPRITE_NAME_BACKGROUND, backpackAdd);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		assertTrue("BackPack title didn't show up",
				solo.waitForText(backpackTitle, 0, TIME_TO_WAIT_BACKPACK));
		assertTrue("Sprite wasn't backpacked!", solo.waitForText(SPRITE_NAME_BACKGROUND, 0, TIME_TO_WAIT));
		solo.goBack();

		solo.clickOnText(SPRITE_NAME_BACKGROUND);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		solo.clickOnText(solo.getString(R.string.backgrounds));
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		UiTestUtils.openBackPackActionModeWhenEmtpy(solo, getActivity());
		assertFalse("Visible Backpack was opened despite look should be in hidden backpack", solo.waitForText(unpack, 1, TIME_TO_WAIT_BACKPACK));
		assertFalse("Visible Backpack was opened despite look should be in hidden backpack", solo.waitForText(TEST_LOOK_NAME + "1", 1, TIME_TO_WAIT_BACKPACK));
		assertTrue("Look is not in hidden backpack!", BackPackListManager.getInstance().getHiddenBackpackedLooks().size() == 1);
		solo.goBack();
		solo.goBack();

		solo.clickOnText(solo.getString(R.string.sounds));
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		UiTestUtils.openBackPackActionModeWhenEmtpy(solo, getActivity());
		assertFalse("Visible Backpack was opened despite sound should be in hidden backpack", solo.waitForText(unpack, 1, TIME_TO_WAIT_BACKPACK));
		assertFalse("Visible Backpack was opened despite sound should be in hidden backpack", solo.waitForText(TEST_SOUND_NAME + "1", 1, TIME_TO_WAIT_BACKPACK));
		assertTrue("Sound is not in hidden backpack!", BackPackListManager.getInstance().getHiddenBackpackedSounds().size() == 1);
		solo.goBack();
		solo.goBack();

		solo.clickOnText(solo.getString(R.string.scripts));
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		UiTestUtils.openBackPackActionModeWhenEmtpy(solo, getActivity());
		assertFalse("Visible Backpack was opened despite script should be in hidden backpack", solo.waitForText(unpack, 1, TIME_TO_WAIT_BACKPACK));
		assertTrue("Scripts are not in hidden backpack!", BackPackListManager.getInstance().getHiddenBackpackedScripts().size() == 2);

		UiTestUtils.switchToProgrammBackground(solo, UiTestUtils.PROJECTNAME3, "cat");
		solo.clickOnText(solo.getString(R.string.scripts));
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		ListView listView = solo.getCurrentViews(ListView.class).get(solo.getCurrentViews(ListView.class).size() - 1);
		int brickCountInView = listView.getCount();
		int numberOfBricksInBrickList = ProjectManager.getInstance().getCurrentSprite().getNumberOfBricks();

		solo.goBack();
		solo.goBack();

		UiTestUtils.openBackPack(solo, getActivity());
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		clickOnContextMenuItem(SPRITE_NAME_BACKGROUND, unpackAsObject);
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
		DataContainer dataContainer = projectManager.getCurrentProject().getDataContainer();
		UserVariable spriteUserVariable = dataContainer.getUserVariable("sprite_var", projectManager.getCurrentSprite());
		UserVariable projectUserVariable = dataContainer.getProjectVariables().get(0);
		UserList projectUserList = dataContainer.getUserList("global_list", null);
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

	public void testBackPackAlreadyPackedDialogSingleItem() {
		clickOnContextMenuItem(SPRITE_NAME, backpackAdd);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		assertTrue("Sprite wasn't backpacked!", solo.waitForText(SPRITE_NAME, 0, TIME_TO_WAIT));
		solo.goBack();
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		clickOnContextMenuItem(SPRITE_NAME, backpackAdd);
		solo.waitForDialogToOpen();
		assertTrue("Sprite already exists backpack dialog not shown!", solo.waitForText(backpackReplaceDialogSingle, 0, TIME_TO_WAIT));
		solo.clickOnButton(solo.getString(R.string.yes));
		solo.waitForDialogToClose();
		solo.waitForActivity(BackPackActivity.class);
		solo.waitForFragmentByTag(SpritesListFragment.TAG);
		solo.sleep(200);

		assertTrue("Should be in backpack!", solo.waitForText(backpackTitle, 0, TIME_TO_WAIT));
		assertTrue("Sprite wasn't backpacked!", solo.waitForText(SPRITE_NAME, 0, TIME_TO_WAIT));
		assertTrue("Sprite was not replaced!", BackPackListManager.getInstance().getBackPackedSprites().size() == 1);
	}

	public void testBackPackAlreadyPackedDialogMultipleItems() {
		UiTestUtils.backPackAllItems(solo, getActivity(), SPRITE_NAME, SPRITE_NAME2);
		assertTrue("Sprite wasn't backpacked!", solo.waitForText(SPRITE_NAME, 0, TIME_TO_WAIT));
		solo.goBack();
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		UiTestUtils.openBackPackActionMode(solo, getActivity());
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

	private String getSpriteName(int spriteIndex) {
		spriteList = project.getSpriteList();
		return spriteList.get(spriteIndex).getName();
	}

	private void clickOnContextMenuItem(String spriteName, String menuItemName) {
		solo.clickLongOnText(spriteName);
		solo.waitForText(menuItemName);
		solo.clickOnText(menuItemName);
	}

	private BackPackSpriteFragment getBackPackSpriteFragment() {
		BackPackActivity activity = (BackPackActivity) solo.getCurrentActivity();
		return (BackPackSpriteFragment) activity.getFragment(BackPackActivity.FRAGMENT_BACKPACK_SPRITES);
	}

	private SpritesListFragment getSpritesListFragment() {
		ProjectActivity activity = (ProjectActivity) solo.getCurrentActivity();
		return activity.getSpritesListFragment();
	}

	private SpriteAdapter getSpriteAdapter() {
		solo.waitForActivity(ProjectActivity.class);
		solo.waitForFragmentByTag(SpritesListFragment.TAG);
		return (SpriteAdapter) getSpritesListFragment().getListAdapter();
	}

	private BackPackSpriteAdapter getBackPackSpriteAdapter() {
		return (BackPackSpriteAdapter) getBackPackSpriteFragment().getListAdapter();
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
		clickOnContextMenuItem(spriteName, delete);
		solo.waitForDialogToOpen();
		solo.waitForText(solo.getString(R.string.yes));
		solo.clickOnText(solo.getString(R.string.yes));
	}

	private void checkIfNumberOfSpritesIsEqual(int expectedNumber) {
		List<Sprite> spriteList = ProjectManager.getInstance().getCurrentProject().getSpriteList();
		assertEquals("Number of sprites is not as expected", expectedNumber, spriteList.size());
	}

	private void moveSpriteDown(String spriteToMove) {
		clickOnContextMenuItem(spriteToMove, solo.getString(R.string.menu_item_move_down));
	}

	private void moveSpriteUp(String spriteToMove) {
		clickOnContextMenuItem(spriteToMove, solo.getString(R.string.menu_item_move_up));
	}

	private void moveSpriteToBottom(String spriteToMove) {
		clickOnContextMenuItem(spriteToMove, solo.getString(R.string.menu_item_move_to_bottom));
	}

	private void moveSpriteToTop(String spriteToMove) {
		clickOnContextMenuItem(spriteToMove, solo.getString(R.string.menu_item_move_to_top));
	}
}
