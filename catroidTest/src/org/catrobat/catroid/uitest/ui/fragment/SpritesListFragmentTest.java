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
import android.util.Log;

import com.robotium.solo.By;
import com.robotium.solo.Solo;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.LookDataHistory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SoundInfoHistory;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.PointToBrick;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.fragment.SpritesListFragment;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.Utils;

import java.io.IOException;
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
		project.addSprite(sprite2);
		project.getDataContainer().addSpriteUserVariableToSprite(sprite, LOCAL_VARIABLE_NAME);
		project.getDataContainer().getUserVariable(LOCAL_VARIABLE_NAME, sprite).setValue(LOCAL_VARIABLE_VALUE);

		project.getDataContainer().addProjectUserVariable(GLOBAL_VARIABLE_NAME);
		project.getDataContainer().getUserVariable(GLOBAL_VARIABLE_NAME, null).setValue(GLOBAL_VARIABLE_VALUE);

		ProjectManager.getInstance().setProject(project);
		LookDataHistory.applyChanges(project.getName());
		SoundInfoHistory.applyChanges(project.getName());
		solo.clickOnText(solo.getString(R.string.main_menu_continue));
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
		moveSpriteUp(SPRITE_NAME2);

		assertEquals("Sprite didn't move up (testMoveSpriteUp 1)", SPRITE_NAME2, getSpriteName(1));
		assertEquals("Sprite didn't move up (testMoveSpriteUp 2)", SPRITE_NAME, getSpriteName(2));
	}

	public void testMoveSpriteDown() {
		moveSpriteDown(SPRITE_NAME);

		assertEquals("Sprite didn't move down (testMoveSpriteDown 1)", SPRITE_NAME2, getSpriteName(1));
		assertEquals("Sprite didn't move down (testMoveSpriteDown 2)", SPRITE_NAME, getSpriteName(2));
	}

	public void testMoveSpriteToBottom() {
		moveSpriteToBottom(SPRITE_NAME);

		assertEquals("Sprite didn't move bottom (testMoveSpriteToBottom 1)", SPRITE_NAME2, getSpriteName(1));
		assertEquals("Sprite didn't move bottom (testMoveSpriteToBottom 2)", SPRITE_NAME, getSpriteName(2));
	}

	public void testMoveSpriteToTop() {
		moveSpriteToTop(SPRITE_NAME2);

		assertEquals("Sprite didn't move top (testMoveSpriteToTop 1)", SPRITE_NAME2, getSpriteName(1));
		assertEquals("Sprite didn't move top (testMoveSpriteToTop 2)", SPRITE_NAME, getSpriteName(2));
	}

	public void testMoveSpriteUpFirstEntry() {
		moveSpriteUp(SPRITE_NAME);

		assertEquals("Sprite moved (testMoveSpriteUpFirstEntry 1)", SPRITE_NAME, getSpriteName(1));
		assertEquals("Sprite moved (testMoveSpriteUpFirstEntry 2)", SPRITE_NAME2, getSpriteName(2));
	}

	public void testMoveSpriteDownLastEntry() {
		moveSpriteDown(SPRITE_NAME2);
		solo.sleep(TIME_TO_WAIT);

		assertEquals("Sprite moved (testMoveSpriteDownLastEntry 1)", SPRITE_NAME, getSpriteName(1));
		assertEquals("Sprite moved (testMoveSpriteDownLastEntry 2)", SPRITE_NAME2, getSpriteName(2));
	}

	public void testMoveSpriteToTopFirstEntry() {
		moveSpriteToTop(SPRITE_NAME);

		assertEquals("Sprite moved (testMoveSpriteToTopFirstEntry 1)", SPRITE_NAME, getSpriteName(1));
		assertEquals("Sprite moved (testMoveSpriteToTopFirstEntry 2)", SPRITE_NAME2, getSpriteName(2));
	}

	public void testMoveSpriteToBottomLastEntry() {
		moveSpriteToBottom(SPRITE_NAME2);

		assertEquals("Sprite moved (testMoveSpriteToBottomLastEntry 1)", SPRITE_NAME, getSpriteName(1));
		assertEquals("Sprite moved (testMoveSpriteToBottomLastEntry 2)", SPRITE_NAME2, getSpriteName(2));
	}

	public void testUndoRedoSequenceDelete() {
		deleteSprite(SPRITE_NAME);
		assertEquals("sprite was not deleted!", 2, getCurrentNumberOfSprites());
		undo();
		assertTrue("sprite was not restored!", solo.waitForText(SPRITE_NAME));
		redo();
		assertEquals("sprite was not deleted again!", 2, getCurrentNumberOfSprites());

		deleteSprite(SPRITE_NAME2);
		assertEquals("Second sprite was not deleted!", 1, getCurrentNumberOfSprites());
		undo();
		assertTrue("sprite was not restored!", solo.waitForText(SPRITE_NAME2));
		undo();
		assertTrue("sprite was not restored!", solo.waitForText(SPRITE_NAME));
		redo();
		assertEquals("First sprite was not deleted again!", 2, getCurrentNumberOfSprites());
		deleteSprite(SPRITE_NAME2);
		assertEquals("First sprite was not deleted again!", 1, getCurrentNumberOfSprites());
		assertFalse("Redo should not be visible!", solo.getView(R.id.menu_redo).isEnabled());
	}

	public void testUndoRedoSequenceCopy() {
		copySprite(SPRITE_NAME);
		assertEquals("sprite was not copied!", 4, getCurrentNumberOfSprites());
		undo();
		assertEquals("Copied sprite has not been undone!", 3, getCurrentNumberOfSprites());
		redo();
		assertEquals("sprite was not copied again!", 4, getCurrentNumberOfSprites());

		copySprite(SPRITE_NAME2);
		assertEquals("Second sprite was not copied!", 5, getCurrentNumberOfSprites());
		undo();
		assertEquals("Second sprite copy was not undone!", 4, getCurrentNumberOfSprites());
		undo();
		assertEquals("First sprite copy was not undone!", 3, getCurrentNumberOfSprites());
		redo();
		assertEquals("First sprite was not copied again!", 4, getCurrentNumberOfSprites());
		copySprite(SPRITE_NAME2);
		assertEquals("Second sprite was not copied!", 5, getCurrentNumberOfSprites());
		assertFalse("Redo should not be visible!", solo.getView(R.id.menu_redo).isEnabled());
	}

	public void testUndoRedoSequenceRename() {
		String renameNameFirst = "test1";
		String renameNameSecond = "test2";
		renameSprite(SPRITE_NAME, renameNameFirst);
		assertTrue("sprite was not renamed!", searchForSprite(renameNameFirst));
		assertFalse("sprite " + SPRITE_NAME + " should not be in list!", searchForSprite(SPRITE_NAME));

		undo();
		assertTrue("sprite " + SPRITE_NAME + " should be in list after undo!", searchForSprite(SPRITE_NAME));
		assertFalse("sprite " + renameNameFirst + " should not be in list after undo!", searchForSprite(renameNameFirst));

		redo();
		assertTrue("sprite was not renamed after redo!", searchForSprite(renameNameFirst));
		assertFalse("sprite " + SPRITE_NAME + " should not be in list after redo!", searchForSprite(SPRITE_NAME));

		renameSprite(SPRITE_NAME2, renameNameSecond);
		assertTrue("Second sprite was not renamed!", searchForSprite(renameNameSecond));
		assertFalse("sprite " + SPRITE_NAME2 + " should not be in list!", searchForSprite(SPRITE_NAME2));

		undo();
		assertTrue("Second sprite was not undone!", searchForSprite(SPRITE_NAME2));
		assertFalse("sprite " + renameNameSecond + " should not be in list!", searchForSprite(renameNameSecond));

		undo();
		assertTrue("sprite " + SPRITE_NAME + " should be in list after undo!", searchForSprite(SPRITE_NAME));
		assertFalse("sprite " + renameNameFirst + " should not be in list after undo!", searchForSprite(renameNameFirst));

		redo();
		assertTrue("sprite was not renamed after redo!", searchForSprite(renameNameFirst));
		assertFalse("sprite " + SPRITE_NAME + " should not be in list after redo!", searchForSprite(SPRITE_NAME));

		renameSprite(SPRITE_NAME2, renameNameSecond);
		assertTrue("Second sprite was not renamed!", searchForSprite(renameNameSecond));
		assertFalse("sprite " + SPRITE_NAME2 + " should not be in list!", searchForSprite(SPRITE_NAME2));
		assertFalse("Redo should not be visible!", solo.getView(R.id.menu_redo).isEnabled());
	}

	public void testUndoRedoSequenceMixedCase() {
		copySprite(SPRITE_NAME);
		List<Sprite> currentList = ProjectManager.getInstance().getCurrentProject().getSpriteList();
		String copySpriteNameFirst = currentList.get(currentList.size() - 1).getName();
		assertEquals("sprite was not copied!", 4, getCurrentNumberOfSprites());

		deleteSprite(copySpriteNameFirst);
		assertEquals("copied sprite was not deleted!", 3, getCurrentNumberOfSprites());

		undo();
		assertEquals("undo of delete copied sprite was not done!", 4, getCurrentNumberOfSprites());

		undo();
		assertEquals("undo of copy sprite was not done!", 3, getCurrentNumberOfSprites());

		redo();
		assertEquals("redo of copy sprite was not done!", 4, getCurrentNumberOfSprites());

		redo();
		assertEquals("redo of delete copied sprite was not done!", 3, getCurrentNumberOfSprites());
	}

	public void testUndoRedoSequenceMoveDown() {
		moveSpriteDown(SPRITE_NAME);

		assertEquals("testUndoRedoSequenceMoveDown 1", SPRITE_NAME2, getSpriteName(1));
		assertEquals("testUndoRedoSequenceMoveDown 2", SPRITE_NAME, getSpriteName(2));

		undo();

		assertEquals("testUndoRedoSequenceMoveDown 3", SPRITE_NAME, getSpriteName(1));
		assertEquals("testUndoRedoSequenceMoveDown 4", SPRITE_NAME2, getSpriteName(2));

		redo();

		assertEquals("testUndoRedoSequenceMoveDown 5", SPRITE_NAME2, getSpriteName(1));
		assertEquals("testUndoRedoSequenceMoveDown 6", SPRITE_NAME, getSpriteName(2));
	}

	public void testUndoRedoSequenceMoveUp() {
		moveSpriteUp(SPRITE_NAME2);

		assertEquals("testUndoRedoSequenceMoveUp 1", SPRITE_NAME2, getSpriteName(1));
		assertEquals("testUndoRedoSequenceMoveUp 2", SPRITE_NAME, getSpriteName(2));

		undo();

		assertEquals("testUndoRedoSequenceMoveUp 3", SPRITE_NAME, getSpriteName(1));
		assertEquals("testUndoRedoSequenceMoveUp 4", SPRITE_NAME2, getSpriteName(2));

		redo();

		assertEquals("testUndoRedoSequenceMoveUp 5", SPRITE_NAME2, getSpriteName(1));
		assertEquals("testUndoRedoSequenceMoveUp 6", SPRITE_NAME, getSpriteName(2));
	}

	public void testUndoRedoSequenceMoveToBottom() {
		moveSpriteToBottom(SPRITE_NAME);

		assertEquals("testUndoRedoSequenceMoveToBottom 1", SPRITE_NAME2, getSpriteName(1));
		assertEquals("testUndoRedoSequenceMoveToBottom 2", SPRITE_NAME, getSpriteName(2));

		undo();

		assertEquals("testUndoRedoSequenceMoveToBottom 3", SPRITE_NAME, getSpriteName(1));
		assertEquals("testUndoRedoSequenceMoveToBottom 4", SPRITE_NAME2, getSpriteName(2));

		redo();

		assertEquals("testUndoRedoSequenceMoveToBottom 5", SPRITE_NAME2, getSpriteName(1));
		assertEquals("testUndoRedoSequenceMoveToBottom 6", SPRITE_NAME, getSpriteName(2));
	}

	public void testUndoRedoSequenceMoveToTop() {
		moveSpriteToTop(SPRITE_NAME2);

		assertEquals("testUndoRedoSequenceMoveToTop 1", SPRITE_NAME2, getSpriteName(1));
		assertEquals("testUndoRedoSequenceMoveToTop 2", SPRITE_NAME, getSpriteName(2));

		undo();

		assertEquals("testUndoRedoSequenceMoveToTop 3", SPRITE_NAME, getSpriteName(1));
		assertEquals("testUndoRedoSequenceMoveToTop 4", SPRITE_NAME2, getSpriteName(2));

		redo();

		assertEquals("testUndoRedoSequenceMoveToTop 5", SPRITE_NAME2, getSpriteName(1));
		assertEquals("testUndoRedoSequenceMoveToTop 6", SPRITE_NAME, getSpriteName(2));
	}

	public void testUndoRedoSequenceWithUndoRedoInSoundsAndLooks() {
		String mole1Name = solo.getString(R.string.default_project_sprites_mole_name) + " 1";
		solo.goBack();
		if (UiTestUtils.deleteOldAndCreateAndSaveCleanStandardProject(getActivity(), getInstrumentation()) == null) {
			fail("StandardProject Not created!");
		}
		Sprite mole1 = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(1);

		solo.clickOnText(solo.getString(R.string.main_menu_continue));
		solo.clickOnText(mole1Name);
		solo.clickOnText(solo.getString(R.string.looks));
		solo.clickLongOnText(solo.getString(R.string.default_project_sprites_mole_name));
		solo.clickOnText(solo.getString(R.string.delete));
		solo.sleep(TIME_TO_WAIT);
		assertEquals("Look was not deleted!", 2, getLookCountFromSprite(mole1));
		undo();
		assertEquals("Look was not restored!", 3, getLookCountFromSprite(mole1));
		redo();
		assertEquals("Look was not deleted again!", 2, getLookCountFromSprite(mole1));
		solo.goBack();
		solo.goBack();
		deleteSprite(mole1Name);
		assertEquals("Sprite was not deleted!", 4, getCurrentNumberOfSprites());
		undo();
		assertEquals("Sprite was not restored!", 5, getCurrentNumberOfSprites());
		solo.clickOnText(mole1Name);
		solo.clickOnText(solo.getString(R.string.looks));
		assertTrue("Undo should be visible!", solo.getView(R.id.menu_undo).isEnabled());
		undo();
		assertEquals("Look was not restored! 2", 3, getLookCountFromSprite(mole1));
		redo();
		assertEquals("Look was not deleted again! 2", 2, getLookCountFromSprite(mole1));
		solo.goBack();

		solo.clickOnText(solo.getString(R.string.sounds));
		solo.clickLongOnText(solo.getString(R.string.default_project_sprites_mole_sound));
		solo.clickOnText(solo.getString(R.string.delete));
		solo.sleep(TIME_TO_WAIT);
		assertEquals("Sound was not deleted!", 0, getSoundCountFromSprite(mole1));
		undo();
		assertEquals("Sound deletion was not undone!", 1, getSoundCountFromSprite(mole1));
		redo();
		assertEquals("Sound deletion was not redone!", 0, getSoundCountFromSprite(mole1));
		solo.goBack();
		solo.goBack();
		redo();
		assertEquals("Mole was not deleted again!", 4, getCurrentNumberOfSprites());
		undo();
		assertEquals("Mole deletion was not undone!", 5, getCurrentNumberOfSprites());
		solo.clickOnText(mole1Name);
		solo.clickOnText(solo.getString(R.string.sounds));
		undo();
		assertEquals("Sound deletion was not undone! 2", 1, getSoundCountFromSprite(mole1));
		redo();
		assertEquals("Sound deletion was not redone! 2", 0, getSoundCountFromSprite(mole1));
		solo.goBack();
		solo.goBack();
		solo.goBack();
		try {
			ProjectManager.getInstance().deleteCurrentProject(getActivity());
		} catch (IOException exception) {
			Log.e("SpritesListFragmentTest", Log.getStackTraceString(exception));
			fail("Could not delete Standard Project!");
		}
	}

	public void testCorrectUpdateOfPointToBrickOnRedoUndo() {
		PointToBrick pointToBrick = new PointToBrick();
		pointToBrick.setSprite(sprite2);
		Script script = project.getSpriteList().get(0).getScript(0);
		script.addBrick(pointToBrick);
		deleteSprite(sprite2.getName());
		solo.clickOnText("cat");
		solo.clickOnText(solo.getString(R.string.scripts));
		solo.sleep(TIME_TO_WAIT);
		assertFalse("PointToBrick should not point to " + sprite2.getName(), solo.waitForText(sprite2.getName()));
		solo.goBack();
		solo.goBack();

		undo();
		assertTrue("PointToBrick should point to " + sprite2.getName(), pointToBrick.getSprite().equals(sprite2));
	}

	private void moveSpriteDown(String spriteToMove) {
		clickOnContextMenuItem(spriteToMove, solo.getString(R.string.menu_item_move_down));
		solo.sleep(TIME_TO_WAIT);
	}

	private void moveSpriteUp(String spriteToMove) {
		clickOnContextMenuItem(spriteToMove, solo.getString(R.string.menu_item_move_up));
		solo.sleep(TIME_TO_WAIT);
	}

	private void moveSpriteToBottom(String spriteToMove) {
		clickOnContextMenuItem(spriteToMove, solo.getString(R.string.menu_item_move_to_bottom));
		solo.sleep(TIME_TO_WAIT);
	}

	private void moveSpriteToTop(String spriteToMove) {
		clickOnContextMenuItem(spriteToMove, solo.getString(R.string.menu_item_move_to_top));
		solo.sleep(TIME_TO_WAIT);
	}

	private void clickOnContextMenuItem(String spriteName, String menuItemName) {
		solo.clickLongOnText(spriteName);
		solo.waitForText(menuItemName);
		solo.clickOnText(menuItemName);
	}

	public void testGetSpriteFromMediaLibrary() {
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

	private void undo() {
		solo.clickOnActionBarItem(R.id.menu_undo);
		solo.sleep(TIME_TO_WAIT);
	}

	private void redo() {
		solo.clickOnActionBarItem(R.id.menu_redo);
		solo.sleep(TIME_TO_WAIT);
	}

	private void deleteSprite(String spriteName) {
		clickOnContextMenuItem(spriteName, solo.getString(R.string.delete));
		solo.sleep(TIME_TO_WAIT);
	}

	private void copySprite(String spriteName) {
		clickOnContextMenuItem(spriteName, solo.getString(R.string.copy));
		solo.sleep(TIME_TO_WAIT);
	}

	private boolean searchForSprite(String spriteName) {
		for (Sprite sprite : ProjectManager.getInstance().getCurrentProject().getSpriteList()) {
			if (sprite.getName().compareTo(spriteName) == 0) {
				return true;
			}
		}
		return false;
	}

	private void renameSprite(String oldName, String newName) {
		solo.clickLongOnText(oldName);
		solo.clickOnText(solo.getString(R.string.rename));
		UiTestUtils.enterText(solo, 0, newName);
		solo.sendKey(Solo.ENTER);
		solo.sleep(TIME_TO_WAIT);
	}

	private int getCurrentNumberOfSprites() {
		return ProjectManager.getInstance().getCurrentProject().getSpriteList().size();
	}

	private int getLookCountFromSprite(Sprite sprite) {
		return sprite.getLookDataList().size();
	}

	private int getSoundCountFromSprite(Sprite sprite) {
		return sprite.getSoundList().size();
	}
}
