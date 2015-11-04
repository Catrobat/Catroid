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
import android.net.wifi.WifiManager;

import com.robotium.solo.By;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.fragment.SpritesListFragment;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.Utils;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class SpritesListFragmentTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	public SpritesListFragmentTest() {
		super(MainMenuActivity.class);
	}

	private static final String LOCAL_VARIABLE_NAME = "test_local";
	private static final double LOCAL_VARIABLE_VALUE = 0xDEADBEEF;

	private static final String GLOBAL_VARIABLE_NAME = "test_global";
	private static final double GLOBAL_VARIABLE_VALUE = 0xC0FFEE;

	private static final String SPRITE_NAME = "testSprite1";
	private static final String SPRITE_NAME2 = "testSprite2";

	private static final int TIME_TO_WAIT = 200;

	private Sprite sprite;
	private Sprite sprite2;
	private Project project;

	private List<Sprite> spriteList;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		UiTestUtils.createTestProject();

		project = ProjectManager.getInstance().getCurrentProject();
		sprite = new Sprite(SPRITE_NAME);
		sprite2 = new Sprite(SPRITE_NAME2);
		project.addSprite(sprite);
		project.getDataContainer().addSpriteUserVariableToSprite(sprite, LOCAL_VARIABLE_NAME);
		project.getDataContainer().getUserVariable(LOCAL_VARIABLE_NAME, sprite).setValue(LOCAL_VARIABLE_VALUE);

		project.getDataContainer().addProjectUserVariable(GLOBAL_VARIABLE_NAME);
		project.getDataContainer().getUserVariable(GLOBAL_VARIABLE_NAME, null).setValue(GLOBAL_VARIABLE_VALUE);

		ProjectManager.getInstance().setProject(project);
	}

	public void testLocalVariablesWhenSpriteCopiedFromSpritesListFragment() {
		solo.clickOnText(solo.getString(R.string.main_menu_continue));
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
		UiTestUtils.clickOnText(solo, solo.getString(R.string.main_menu_continue));
		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());

		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy, getActivity());
		assertTrue("Select All is not shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), true));

		UiTestUtils.clickOnText(solo, selectAll);
		assertFalse("Select All is still shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), false));

		solo.clickOnCheckBox(0);
		assertTrue("Select All is not shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), true));

		solo.clickOnCheckBox(0);
		assertFalse("Select All is still shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), false));

		solo.goBack();

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete, getActivity());
		assertTrue("Select All is not shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), true));

		UiTestUtils.clickOnText(solo, selectAll);
		assertFalse("Select All is still shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), false));

		solo.clickOnCheckBox(0);
		assertTrue("Select All is not shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), true));

		solo.clickOnCheckBox(0);
		assertFalse("Select All is still shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), false));
	}

	public void testMoveSpriteUp() {
		project.addSprite(sprite2);
		solo.sleep(TIME_TO_WAIT);
		solo.clickOnText(solo.getString(R.string.main_menu_continue));
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
		solo.clickOnText(solo.getString(R.string.main_menu_continue));
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
		solo.clickOnText(solo.getString(R.string.main_menu_continue));
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
		solo.clickOnText(solo.getString(R.string.main_menu_continue));
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
		solo.clickOnText(solo.getString(R.string.main_menu_continue));
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
		solo.clickOnText(solo.getString(R.string.main_menu_continue));
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
		solo.clickOnText(solo.getString(R.string.main_menu_continue));
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
		solo.clickOnText(solo.getString(R.string.main_menu_continue));
		moveSpriteToBottom(SPRITE_NAME2);
		solo.sleep(TIME_TO_WAIT);
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.main_menu_continue));

		assertEquals("Sprite moved (testMoveSpriteToBottomLastEntry 1)", SPRITE_NAME, getSpriteName(1));
		assertEquals("Sprite moved (testMoveSpriteToBottomLastEntry 2)", SPRITE_NAME2, getSpriteName(2));
		project.removeSprite(sprite2);
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

	private void clickOnContextMenuItem(String spriteName, String menuItemName) {
		solo.clickLongOnText(spriteName);
		solo.waitForText(menuItemName);
		solo.clickOnText(menuItemName);
	}

	public void testGetSpriteFromMediaLibrary() {
		solo.clickOnText(solo.getString(R.string.main_menu_continue));
		String mediaLibraryText = solo.getString(R.string.add_look_media_library);
		int numberSpritesBefore = ProjectManager.getInstance().getCurrentProject().getSpriteList().size();
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.waitForText(mediaLibraryText);
		solo.clickOnText(mediaLibraryText);
		solo.waitForWebElement(By.className("program"));
		solo.clickOnWebElement(By.className("program"));
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
		solo.clickOnText(solo.getString(R.string.main_menu_continue));
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

	private String getSpriteName(int spriteIndex) {
		spriteList = project.getSpriteList();
		return spriteList.get(spriteIndex).getName();
	}
}
