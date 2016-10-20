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

import android.content.res.Resources;
import android.view.View;
import android.widget.CheckBox;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.BackPackActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.SceneAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.fragment.BackPackSceneFragment;
import org.catrobat.catroid.ui.fragment.ScenesListFragment;
import org.catrobat.catroid.ui.fragment.SpritesListFragment;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.catrobat.catroid.common.Constants.BACKPACK_DIRECTORY;
import static org.catrobat.catroid.common.Constants.DEFAULT_ROOT;
import static org.catrobat.catroid.utils.Utils.buildPath;

public class ScenesListFragmentTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	public ScenesListFragmentTest() {
		super(MainMenuActivity.class);
	}

	private static final String LOCAL_VARIABLE_NAME = "test_local";
	private static final double LOCAL_VARIABLE_VALUE = 0xDEADBEEF;

	private static final String GLOBAL_VARIABLE_NAME = "test_global";
	private static final double GLOBAL_VARIABLE_VALUE = 0xC0FFEE;

	private static final String SCENE_NAME = "testScene1";
	private static final String SCENE_NAME2 = "testScene2";
	private static final String SCENE_NAME3 = "testScene3";
	private static final String SPRITE_NAME = "testSprite1";
	private static final String SPRITE_NAME2 = "testSprite2";
	private static final String SCENE_NAME_UNPACKED = "testScene11";
	private static final String SCENE_NAME2_UNPACKED = "testScene21";

	private static final int TIME_TO_WAIT_BACKPACK = 1000;

	private static final int TIME_TO_WAIT = 400;

	private Sprite sprite;
	private Project project;

	private String delete;
	private String unpack;
	private String backpack;
	private String backpackTitle;
	private String backpackReplaceDialogSingle;
	private String backpackReplaceDialogMultiple;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		UiTestUtils.createTestProject(UiTestUtils.PROJECTNAME1);
		UiTestUtils.createTestProject();

		project = ProjectManager.getInstance().getCurrentProject();
		sprite = new Sprite(SPRITE_NAME);
		Sprite sprite2 = new Sprite(SPRITE_NAME2);
		project.getDefaultScene().rename(SCENE_NAME, getActivity(), false);
		project.getDefaultScene().addSprite(sprite);
		project.getDefaultScene().addSprite(sprite2);
		project.getDefaultScene().getDataContainer().addSpriteUserVariableToSprite(sprite, LOCAL_VARIABLE_NAME);
		project.getDefaultScene().getDataContainer().getUserVariable(LOCAL_VARIABLE_NAME, sprite).setValue(LOCAL_VARIABLE_VALUE);

		project.getDefaultScene().getDataContainer().addProjectUserVariable(GLOBAL_VARIABLE_NAME);
		project.getDefaultScene().getDataContainer().getUserVariable(GLOBAL_VARIABLE_NAME, null).setValue(GLOBAL_VARIABLE_VALUE);

		Scene scene2 = new Scene(getActivity(), SCENE_NAME2, project);
		Scene scene3 = new Scene(getActivity(), SCENE_NAME3, project);
		project.addScene(scene2);
		project.addScene(scene3);

		ProjectManager.getInstance().setProject(project);

		Resources resources = getActivity().getBaseContext().getResources();
		backpackTitle = solo.getString(R.string.backpack_title);
		delete = solo.getString(R.string.delete);
		unpack = solo.getString(R.string.unpack);
		backpack = solo.getString(R.string.backpack);
		backpackReplaceDialogSingle = resources.getString(R.string.backpack_replace_scene, SCENE_NAME);
		backpackReplaceDialogMultiple = solo.getString(R.string.backpack_replace_scene_multiple);

		UiTestUtils.clearBackPack(true);
		solo.clickOnText(solo.getString(R.string.main_menu_continue));
	}

	@Override
	protected void tearDown() throws Exception {
		UiTestUtils.clearBackPack(true);
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	public void testDragAndDropDown() {
		List<Scene> sceneList = ProjectManager.getInstance().getCurrentProject().getSceneList();
		int offset = sceneList.size();
		for (int i = 1; i < 3; i++) {
			addSceneWithName("TestScene" + (i + offset));
		}

		solo.goBack();
		solo.clickOnText(solo.getString(R.string.main_menu_programs));
		solo.clickOnText(ProjectManager.getInstance().getCurrentProject().getName());

		assertEquals("Wrong List before DragAndDropTest", sceneList.get(0).getName(), SCENE_NAME);
		assertEquals("Wrong List before DragAndDropTest", sceneList.get(1).getName(), SCENE_NAME2);
		assertEquals("Wrong List before DragAndDropTest", sceneList.get(2).getName(), SCENE_NAME3);
		assertEquals("Wrong List before DragAndDropTest", sceneList.get(3).getName(), "TestScene4");
		assertEquals("Wrong List before DragAndDropTest", sceneList.get(4).getName(), "TestScene5");

		ArrayList<Integer> yPositionList = UiTestUtils.getListItemYPositions(solo, 1);
		UiTestUtils.longClickAndDrag(solo, 10, yPositionList.get(1), 10, yPositionList.get(4) + 100, 20);

		assertEquals("Wrong List after DragAndDropTest", sceneList.get(0).getName(), SCENE_NAME);
		assertEquals("Wrong List after DragAndDropTest", sceneList.get(1).getName(), SCENE_NAME3);
		assertEquals("Wrong List after DragAndDropTest", sceneList.get(2).getName(), "TestScene4");
		assertEquals("Wrong List after DragAndDropTest", sceneList.get(3).getName(), SCENE_NAME2);
		assertEquals("Wrong List after DragAndDropTest", sceneList.get(4).getName(), "TestScene5");
	}

	public void testDragAndDropUp() {
		List<Scene> sceneList = ProjectManager.getInstance().getCurrentProject().getSceneList();
		int offset = sceneList.size();
		for (int i = 1; i < 3; i++) {
			addSceneWithName("TestScene" + (i + offset));
		}

		solo.goBack();
		solo.clickOnText(solo.getString(R.string.main_menu_programs));
		solo.clickOnText(ProjectManager.getInstance().getCurrentProject().getName());

		assertEquals("Wrong List before DragAndDropTest", sceneList.get(0).getName(), SCENE_NAME);
		assertEquals("Wrong List before DragAndDropTest", sceneList.get(1).getName(), SCENE_NAME2);
		assertEquals("Wrong List before DragAndDropTest", sceneList.get(2).getName(), SCENE_NAME3);
		assertEquals("Wrong List before DragAndDropTest", sceneList.get(3).getName(), "TestScene4");
		assertEquals("Wrong List before DragAndDropTest", sceneList.get(4).getName(), "TestScene5");

		ArrayList<Integer> yPositionList = UiTestUtils.getListItemYPositions(solo, 1);
		UiTestUtils.longClickAndDrag(solo, 10, yPositionList.get(4), 10, yPositionList.get(1) - 100, 20);

		assertEquals("Wrong List after DragAndDropTest", sceneList.get(0).getName(), SCENE_NAME);
		assertEquals("Wrong List after DragAndDropTest", sceneList.get(1).getName(), SCENE_NAME2);
		assertEquals("Wrong List after DragAndDropTest", sceneList.get(2).getName(), SCENE_NAME3);
		assertEquals("Wrong List after DragAndDropTest", sceneList.get(3).getName(), "TestScene5");
		assertEquals("Wrong List after DragAndDropTest", sceneList.get(4).getName(), "TestScene4");
	}

	public void testLocalVariablesWhenSceneCopiedFromScenesListFragment() {
		clickOnSingleActionModeItem(SCENE_NAME, solo.getString(R.string.copy), R.id.copy);

		solo.sleep(1000);

		String copiedSceneName = SCENE_NAME + solo.getString(R.string.copy_sprite_name_suffix);
		solo.waitForText(copiedSceneName);
		assertTrue(copiedSceneName + " not found!", solo.searchText(copiedSceneName));

		Scene clonedScene = null;
		for (Scene tempScene : project.getSceneList()) {
			if (tempScene.getName().equals(copiedSceneName)) {
				clonedScene = tempScene;
			}
		}

		if (clonedScene == null) {
			fail("no cloned scene in project");
		}

		List<UserVariable> userVariableList = project.getDefaultScene().getDataContainer()
				.getOrCreateVariableListForSprite(clonedScene.getSpriteBySpriteName(sprite.getName()));
		Set<String> hashSet = new HashSet<String>();
		for (UserVariable userVariable : userVariableList) {
			assertTrue("Variable already exists", hashSet.add(userVariable.getName()));
		}
	}

	public void testDeleteAllButOneScenesActionMode() {
		UiTestUtils.openActionMode(solo, delete, R.id.delete);
		solo.clickOnText(SCENE_NAME);
		solo.clickOnText(SCENE_NAME2);
		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.clickOnText(solo.getString(R.string.yes));
		solo.sleep(TIME_TO_WAIT);
		assertFalse("Scene " + SCENE_NAME + " was not deleted", ProjectManager.getInstance().getCurrentProject()
				.getSceneByName(SCENE_NAME) != null);
		assertFalse("Scene " + SCENE_NAME2 + " was not deleted", ProjectManager.getInstance().getCurrentProject()
				.getSceneByName(SCENE_NAME2) != null);

		assertTrue("Sprite List Fragment did not show up", solo.waitForFragmentByTag(SpritesListFragment.TAG));
	}

	public void testDeleteAllScenesActionMode() {
		UiTestUtils.openActionMode(solo, delete, R.id.delete);
		solo.clickOnText(SCENE_NAME);
		solo.clickOnText(SCENE_NAME2);
		solo.clickOnText(SCENE_NAME3);
		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.clickOnText(solo.getString(R.string.yes));
		solo.sleep(TIME_TO_WAIT);
		assertFalse("Scene " + SCENE_NAME + " was not deleted", ProjectManager.getInstance().getCurrentProject()
				.getSceneByName(SCENE_NAME) != null);
		assertFalse("Scene " + SCENE_NAME2 + " was not deleted", ProjectManager.getInstance().getCurrentProject()
				.getSceneByName(SCENE_NAME2) != null);
		assertFalse("Scene " + SCENE_NAME3 + " was not deleted", ProjectManager.getInstance().getCurrentProject()
				.getSceneByName(SCENE_NAME2) != null);

		assertTrue("Sprite List Fragment did not show up", solo.waitForFragmentByTag(SpritesListFragment.TAG));

		Scene scene = ProjectManager.getInstance().getCurrentScene();

		assertTrue("Scene is not empty", scene.getSpriteList().size() == 1 && scene.getSpriteList().get(0)
				.getListWithAllBricks().size() == 0 && scene.getSpriteList().get(0).getSoundList().size() == 0
				&& scene.getSpriteList().get(0).getLookDataList().size() == 0);
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
		assertTrue("Deselect All is not shown", solo.searchText(deselectAll, 1, false, true));

		solo.clickOnCheckBox(0);
		assertTrue("Deselect All is not shown", solo.searchText(deselectAll, 1, false, true));

		solo.goBack();

		UiTestUtils.openActionMode(solo, solo.getString(R.string.backpack), R.id.backpack);
		assertTrue("Select All is not shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), true));

		UiTestUtils.clickOnText(solo, selectAll);
		assertTrue("Deselect All is not shown", solo.searchText(deselectAll, 1, false, true));

		solo.clickOnCheckBox(0);
		assertTrue("Deselect All is not shown", solo.searchText(deselectAll, 1, false, true));

		solo.clickOnCheckBox(0);
		assertTrue("Deselect All is not shown", solo.searchText(deselectAll, 1, false, true));

		solo.goBack();

		UiTestUtils.openActionMode(solo, solo.getString(R.string.rename), R.id.rename);
		assertFalse("Select All is shown", solo.searchText(selectAll, 1, false, true));

		solo.clickOnCheckBox(0);
		assertFalse("Deselect All is shown", solo.searchText(deselectAll, 1, false, true));
	}

	public void testEmptyActionModeDialogsInBackPack() {
		UiTestUtils.backPackAllItems(solo, getActivity(), SCENE_NAME, SCENE_NAME2);
		UiTestUtils.deleteAllItems(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete);
		solo.waitForDialogToOpen();
		assertTrue("Nothing to delete dialog not shown", solo.waitForText(solo.getString(R.string
				.nothing_to_delete)));
		solo.clickOnButton(0);
		solo.waitForDialogToClose();

		UiTestUtils.openActionMode(solo, unpack, R.id.unpacking);
		solo.waitForDialogToOpen();
		assertTrue("Nothing to unpack dialog not shown", solo.waitForText(solo.getString(R.string
				.nothing_to_unpack)));
	}

	public void testBackpackSceneSingle() {
		backPackItem(SCENE_NAME);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		assertTrue("BackPack title didn't show up",
				solo.waitForText(backpackTitle, 0, TIME_TO_WAIT_BACKPACK));
		assertTrue("Scene wasn't backpacked!", solo.waitForText(SCENE_NAME, 0, TIME_TO_WAIT));
	}

	public void testBackpackSceneDouble() {
		backPackItem(SCENE_NAME);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);
		solo.goBack();
		backPackItem(SCENE_NAME2);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		assertTrue("BackPack title didn't show up",
				solo.waitForText(backpackTitle, 0, TIME_TO_WAIT_BACKPACK));
		assertTrue("Scene wasn't backpacked!", solo.waitForText(SCENE_NAME, 0, TIME_TO_WAIT));
		assertTrue("Scene wasn't backpacked!", solo.waitForText(SCENE_NAME2, 0, TIME_TO_WAIT));
	}

	public void testBackPackSceneSimpleUnpacking() {
		backPackItem(SCENE_NAME);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		assertTrue("Scene wasn't backpacked!", solo.waitForText(SCENE_NAME, 0, TIME_TO_WAIT));

		clickOnSingleActionModeItem(SCENE_NAME, unpack, R.id.unpacking);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		assertTrue("Scene wasn't unpacked!", solo.waitForText(SCENE_NAME_UNPACKED, 0, TIME_TO_WAIT));
		deleteSprite(SCENE_NAME2);
		solo.sleep(TIME_TO_WAIT);
		UiTestUtils.openBackPack(solo);

		assertTrue("Backpack is empty!", solo.searchText(backpackTitle));
		assertTrue("Scene wasn't kept in backpack!", solo.waitForText(SCENE_NAME, 0, TIME_TO_WAIT));
	}

	public void testBackPackSceneSimpleUnpackingAndDelete() {
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		SceneAdapter adapter = getSceneAdapter(false);
		assertNotNull("Could not get Adapter", adapter);
		int oldCount = adapter.getCount();

		backPackItem(SCENE_NAME2);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		solo.goBack();
		deleteSprite(SCENE_NAME2);

		solo.sleep(TIME_TO_WAIT);
		UiTestUtils.openBackPack(solo);

		clickOnSingleActionModeItem(SCENE_NAME2, unpack, R.id.unpacking);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		assertTrue("Scene wasn't unpacked!", solo.waitForText(SCENE_NAME2, 0, TIME_TO_WAIT));

		int newCount = adapter.getCount();
		assertEquals("Counts have to be equal", oldCount, newCount);
	}

	public void testBackPackSceneMultipleUnpacking() {
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		SceneAdapter adapter = getSceneAdapter(false);
		int oldCount = adapter.getCount();

		assertNotNull("Could not get Adapter", adapter);
		backPackItem(SCENE_NAME);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		clickOnSingleActionModeItem(SCENE_NAME, unpack, R.id.unpacking);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		solo.sleep(TIME_TO_WAIT_BACKPACK);
		assertTrue("Scene wasn't unpacked!", solo.waitForText(SCENE_NAME_UNPACKED, 0, TIME_TO_WAIT));
		backPackItem(SCENE_NAME2);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		clickOnSingleActionModeItem(SCENE_NAME2, unpack, R.id.unpacking);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		solo.sleep(TIME_TO_WAIT_BACKPACK);
		assertTrue("Scene wasn't unpacked!", solo.waitForText(SCENE_NAME2_UNPACKED, 0, TIME_TO_WAIT));
		int newCount = adapter.getCount();
		assertEquals("There are scenes missing", oldCount + 2, newCount);
	}

	public void testBackPackAndUnPackFromDifferentProgrammes() {
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		SceneAdapter adapter = getSceneAdapter(false);
		assertNotNull("Could not get Adapter", adapter);
		backPackItem(SCENE_NAME);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		solo.goBack();
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.main_menu_programs));
		solo.clickOnText(UiTestUtils.PROJECTNAME1);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		ProjectManager.getInstance().getCurrentProject().addScene(new Scene(getActivity(), "testSceneOther",
				ProjectManager.getInstance().getCurrentProject()));
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		solo.goBack();
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		solo.clickOnText(UiTestUtils.PROJECTNAME1);

		UiTestUtils.openBackPack(solo);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		clickOnSingleActionModeItem(SCENE_NAME, unpack, R.id.unpacking);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		assertTrue("Scene wasn't unpacked!", solo.waitForText(SCENE_NAME, 1, 3000));
	}

	public void testBackPackActionModeCheckingAndTitle() {
		UiTestUtils.openBackPackActionModeWhenEmpty(solo);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		int timeToWaitForTitle = 300;

		String scene = solo.getString(R.string.scene);
		String scenes = solo.getString(R.string.scenes);

		assertFalse("Scene should not be displayed in title", solo.waitForText(scene, 4, 300, false, true));

		checkIfCheckboxesAreCorrectlyChecked(false, false, false);

		int expectedNumberOfSelectedScenes = 1;
		String expectedTitle = backpack + " " + expectedNumberOfSelectedScenes + " " + scene;

		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, true, false);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedNumberOfSelectedScenes = 2;
		expectedTitle = backpack + " " + expectedNumberOfSelectedScenes + " " + scenes;

		// Check if multiple-selection is possible
		solo.clickOnCheckBox(0);
		checkIfCheckboxesAreCorrectlyChecked(true, true, false);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedNumberOfSelectedScenes = 1;
		expectedTitle = backpack + " " + expectedNumberOfSelectedScenes + " " + scene;

		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(true, false, false);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0,
				timeToWaitForTitle, false,
				true));

		expectedTitle = backpack;

		solo.clickOnCheckBox(0);
		checkIfCheckboxesAreCorrectlyChecked(false, false, false);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));
	}

	public void testBackPackActionModeIfNothingSelected() {
		UiTestUtils.openBackPackActionModeWhenEmpty(solo);
		int expectedNumberOfScenes = ProjectManager.getInstance().getCurrentProject().getSceneList().size();
		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);
		checkIfCheckboxesAreCorrectlyChecked(false, false, false);
		UiTestUtils.acceptAndCloseActionMode(solo);
		assertFalse("ActionMode didn't disappear", solo.waitForText(backpack, 1, TIME_TO_WAIT, false, true));
		checkIfNumberOfScenesIsEqual(expectedNumberOfScenes);

		UiTestUtils.openBackPackActionModeWhenEmpty(solo);
		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);
		checkIfCheckboxesAreCorrectlyChecked(false, false, false);
		solo.goBack();
		assertFalse("ActionMode didn't disappear", solo.waitForText(backpack, 1, TIME_TO_WAIT, false, true));
		checkIfNumberOfScenesIsEqual(expectedNumberOfScenes);
	}

	public void testBackPackActionModeIfSomethingSelectedAndPressingBack() {
		UiTestUtils.openBackPackActionModeWhenEmpty(solo);
		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(0);
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(true, true, false);
		solo.goBack();
		solo.sleep(TIME_TO_WAIT_BACKPACK);

		assertFalse("ActionMode didn't disappear", solo.waitForText(backpack, 1, TIME_TO_WAIT, false, true));
		assertFalse("Backpack was opened, but shouldn't be!", solo.waitForText(backpackTitle, 1, TIME_TO_WAIT, false,
				true));
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
		assertTrue("Scene wasn't backpacked!", solo.waitForText(SCENE_NAME, 0, TIME_TO_WAIT));
		assertTrue("Scene wasn't backpacked!", solo.waitForText(SCENE_NAME2, 0, TIME_TO_WAIT));
		assertTrue("Scene wasn't backpacked!", solo.waitForText(SCENE_NAME3, 0, TIME_TO_WAIT));
	}

	public void testBackPackSceneDeleteContextMenu() {
		UiTestUtils.backPackAllItems(solo, getActivity(), SCENE_NAME, SCENE_NAME2);

		SceneAdapter adapter = getSceneAdapter(true);
		int oldCount = adapter.getCount();
		List<Scene> backPackSceneList = BackPackListManager.getInstance().getBackPackedScenes();

		clickOnContextMenuItem(SCENE_NAME, delete);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		int newCount = adapter.getCount();
		solo.sleep(500);

		assertEquals("Not all scenes were backpacked", 3, oldCount);
		assertEquals("Scene wasn't deleted in backpack", 2, newCount);
		assertEquals("Count of the backpack sceneList is not correct", newCount, backPackSceneList.size());
	}

	public void testBackPackSceneDeleteActionMode() {
		UiTestUtils.backPackAllItems(solo, getActivity(), SCENE_NAME, SCENE_NAME2);

		SceneAdapter adapter = getSceneAdapter(true);
		int oldCount = adapter.getCount();
		List<Scene> backPackSceneList = BackPackListManager.getInstance().getBackPackedScenes();

		UiTestUtils.deleteAllItems(solo);

		int newCount = adapter.getCount();
		solo.sleep(500);
		assertTrue("No backpack is emtpy text appeared", solo.searchText(backpack));
		assertTrue("No backpack is emtpy text appeared", solo.searchText(solo.getString(R.string.is_empty)));

		assertEquals("Not all scenes were backpacked", 3, oldCount);
		assertEquals("Scene wasn't deleted in backpack", 0, newCount);
		assertEquals("Count of the backpack sceneList is not correct", newCount, backPackSceneList.size());
	}

	public void testBackPackSceneActionModeDifferentProgrammes() {
		UiTestUtils.backPackAllItems(solo, getActivity(), SCENE_NAME, SCENE_NAME2);
		solo.goBack();
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.main_menu_programs));
		solo.clickOnText(UiTestUtils.PROJECTNAME1);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		ProjectManager.getInstance().getCurrentProject().addScene(new Scene(getActivity(), "testSceneOther",
				ProjectManager.getInstance().getCurrentProject()));
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		solo.goBack();
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		solo.clickOnText(UiTestUtils.PROJECTNAME1);
		UiTestUtils.openBackPack(solo);

		UiTestUtils.openActionMode(solo, unpack, R.id.unpacking);
		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());
		UiTestUtils.clickOnText(solo, selectAll);
		UiTestUtils.acceptAndCloseActionMode(solo);

		solo.waitForActivity(ProjectActivity.class);
		assertTrue("scene wasn't unpacked, but should be!", solo.waitForText(SCENE_NAME, 1,
				1000));
		assertTrue("Scene wasn't unpacked!", solo.waitForText(SCENE_NAME2, 1, 1000));
		UiTestUtils.deleteAllItems(solo);
		assertFalse("Scene wasn't deleted!", solo.waitForText(SCENE_NAME, 1, 1000));
		assertFalse("Scene wasn't deleted!", solo.waitForText(SCENE_NAME2, 1, 1000));
	}

	public void testBackPackDeleteActionModeCheckingAndTitle() {
		solo.sleep(TIME_TO_WAIT_BACKPACK);

		UiTestUtils.backPackAllItems(solo, getActivity(), SCENE_NAME, SCENE_NAME2);
		UiTestUtils.openActionMode(solo, delete, R.id.delete);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		int timeToWaitForTitle = 300;

		String scene = solo.getString(R.string.scene);
		String scenes = solo.getString(R.string.scenes);

		assertFalse("Scene should not be displayed in title", solo.waitForText(scene, 4, 300, false, true));

		checkIfCheckboxesAreCorrectlyChecked(false, false, false);

		int expectedNumberOfSelectedScenes = 1;
		String expectedTitle = delete + " " + expectedNumberOfSelectedScenes + " " + scene;

		solo.sleep(TIME_TO_WAIT_BACKPACK);
		solo.clickOnCheckBox(0);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		checkIfCheckboxesAreCorrectlyChecked(true, false, false);
		assertTrue("Title not as expected" + expectedTitle, solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedNumberOfSelectedScenes = 2;
		expectedTitle = delete + " " + expectedNumberOfSelectedScenes + " " + scenes;

		// Check if multiple-selection is possible
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(true, true, false);
		assertTrue("Title not as aspected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedNumberOfSelectedScenes = 1;
		expectedTitle = delete + " " + expectedNumberOfSelectedScenes + " " + scene;

		solo.clickOnCheckBox(0);
		checkIfCheckboxesAreCorrectlyChecked(false, true, false);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedTitle = delete;

		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, false, false);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));
	}

	public void testBackPackDeleteActionModeIfNothingSelected() {
		UiTestUtils.backPackAllItems(solo, getActivity(), SCENE_NAME, SCENE_NAME2);
		UiTestUtils.openActionMode(solo, delete, R.id.delete);

		int expectedNumberOfScenes = BackPackListManager.getInstance().getBackPackedScenes().size();
		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);
		checkIfCheckboxesAreCorrectlyChecked(false, false, false);
		UiTestUtils.acceptAndCloseActionMode(solo);
		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 1, TIME_TO_WAIT, false, true));
		checkIfNumberOfScenesIsEqual(expectedNumberOfScenes);

		UiTestUtils.openActionMode(solo, delete, R.id.delete);
		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);
		checkIfCheckboxesAreCorrectlyChecked(false, false, false);
		solo.goBack();
		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 1, TIME_TO_WAIT, false, true));
		checkIfNumberOfScenesIsEqual(expectedNumberOfScenes);
	}

	public void testBackPackDeleteActionModeIfSomethingSelectedAndPressingBack() {
		UiTestUtils.backPackAllItems(solo, getActivity(), SCENE_NAME, SCENE_NAME2);
		UiTestUtils.openActionMode(solo, delete, R.id.delete);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(0);
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(true, true, false);
		solo.goBack();

		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 1, TIME_TO_WAIT, false, true));
	}

	public void testBackPackDeleteSelectAll() {
		UiTestUtils.backPackAllItems(solo, getActivity(), SCENE_NAME, SCENE_NAME2);
		UiTestUtils.openActionMode(solo, delete, R.id.delete);

		solo.waitForActivity("BackPackActivity");
		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());
		UiTestUtils.clickOnText(solo, selectAll);

		checkIfCheckboxesAreCorrectlyChecked(true, true, true);

		assertFalse("Select All is still shown", solo.waitForText(selectAll, 1, 200, false, true));

		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.sleep(TIME_TO_WAIT_BACKPACK);

		assertFalse("Scene wasn't deleted!", solo.waitForText(SCENE_NAME, 0, TIME_TO_WAIT, false, true));
		assertFalse("Scene wasn't deleted!", solo.waitForText(SCENE_NAME2, 0, TIME_TO_WAIT, false, true));
		assertTrue("No empty bg found!", solo.waitForText(solo.getString(R.string.is_empty), 0, TIME_TO_WAIT));
	}

	public void testBackPackAlreadyPackedDialogSingleItem() {
		backPackItem(SCENE_NAME);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		assertTrue("Scene wasn't backpacked!", solo.waitForText(SCENE_NAME, 0, TIME_TO_WAIT));
		solo.goBack();
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		backPackItem(SCENE_NAME);
		solo.waitForDialogToOpen();
		assertTrue("Scene already exists backpack dialog not shown!", solo.waitForText(backpackReplaceDialogSingle, 0,
				TIME_TO_WAIT));
		solo.clickOnButton(solo.getString(R.string.yes));
		solo.waitForDialogToClose();
		solo.waitForActivity(BackPackActivity.class);
		solo.waitForFragmentByTag(ScenesListFragment.TAG);
		solo.sleep(200);

		assertTrue("Should be in backpack!", solo.waitForText(backpackTitle, 0, TIME_TO_WAIT));
		assertTrue("Scene wasn't backpacked!", solo.waitForText(SCENE_NAME, 0, TIME_TO_WAIT));
		assertTrue("Scene was not replaced!", BackPackListManager.getInstance().getBackPackedScenes().size() == 1);
	}

	public void testBackPackAlreadyPackedDialogMultipleItems() {
		UiTestUtils.backPackAllItems(solo, getActivity(), SCENE_NAME, SCENE_NAME2);
		assertTrue("Scene wasn't backpacked!", solo.waitForText(SCENE_NAME, 0, TIME_TO_WAIT));
		assertTrue("Scene wasn't backpacked!", solo.waitForText(SCENE_NAME2, 0, TIME_TO_WAIT));
		solo.goBack();
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		UiTestUtils.openBackPackActionMode(solo);
		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());
		UiTestUtils.clickOnText(solo, selectAll);
		UiTestUtils.acceptAndCloseActionMode(solo);

		solo.waitForDialogToOpen();
		assertTrue("Scene already exists backpack dialog not shown!", solo.waitForText(backpackReplaceDialogMultiple, 0,
				TIME_TO_WAIT));
		solo.clickOnButton(solo.getString(R.string.yes));
		solo.waitForDialogToClose();
		solo.waitForActivity(BackPackActivity.class);
		solo.waitForFragmentByTag(ScenesListFragment.TAG);
		solo.sleep(200);

		assertTrue("Should be in backpack!", solo.waitForText(backpackTitle, 0, TIME_TO_WAIT));
		assertTrue("Scene wasn't backpacked!", solo.waitForText(SCENE_NAME, 0, TIME_TO_WAIT));
		assertTrue("Scene wasn't backpacked!", solo.waitForText(SCENE_NAME2, 0, TIME_TO_WAIT));
		assertTrue("Scene was not replaced!", BackPackListManager.getInstance().getAllBackpackedScenes().size() == 3);
	}

	public void testBackPackSerializationAndDeserialization() {
		File backPackFile = new File(buildPath(DEFAULT_ROOT, BACKPACK_DIRECTORY, StorageHandler.BACKPACK_FILENAME));
		assertFalse("Backpack.json should not exist!", backPackFile.exists());
		UiTestUtils.backPackAllItems(solo, getActivity(), SCENE_NAME, SCENE_NAME2);
		solo.goBack();
		solo.sleep(TIME_TO_WAIT_BACKPACK);

		assertFalse("No items have been backpacked!", BackPackListManager.getInstance().getBackpack()
				.backpackedScenes.isEmpty());
		assertTrue("Backpack.json has not been saved!", backPackFile.exists());

		UiTestUtils.clearBackPack(false);
		solo.sleep(TIME_TO_WAIT);
		assertTrue("Backpacked items not deleted!", BackPackListManager.getInstance().getBackpack()
				.backpackedScenes.isEmpty());

		BackPackListManager.getInstance().loadBackpack();
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		assertFalse("Backpacked items haven't been restored from backpack.json!", BackPackListManager.getInstance()
				.getBackpack().backpackedScenes.isEmpty());
	}

	private void clickOnSingleActionModeItem(String spriteName, String menuItemName, int menuId) {
		UiTestUtils.openActionMode(solo, menuItemName, menuId);
		solo.clickOnText(spriteName);
		UiTestUtils.acceptAndCloseActionMode(solo);
	}

	private void backPackItem(String sceneName) {
		if (BackPackListManager.getInstance().getBackPackedScenes().size() == 0) {
			UiTestUtils.openBackPackActionModeWhenEmpty(solo);
		} else {
			UiTestUtils.openBackPackActionMode(solo);
		}
		solo.clickOnText(sceneName);
		UiTestUtils.acceptAndCloseActionMode(solo);
	}

	private void clickOnContextMenuItem(String spriteName, String menuItemName) {
		solo.clickLongOnText(spriteName);
		solo.waitForText(menuItemName);
		solo.clickOnText(menuItemName);
	}

	private BackPackSceneFragment getBackPackSceneFragment() {
		BackPackActivity activity = (BackPackActivity) solo.getCurrentActivity();
		return (BackPackSceneFragment) activity.getFragment(BackPackActivity.FRAGMENT_BACKPACK_SCENES);
	}

	private ScenesListFragment getScenesListFragment() {
		ProjectActivity activity = (ProjectActivity) solo.getCurrentActivity();
		return activity.getScenesListFragment();
	}

	private SceneAdapter getSceneAdapter(boolean forBackpack) {
		solo.waitForActivity(ProjectActivity.class);
		solo.waitForFragmentByTag(ScenesListFragment.TAG);
		SceneAdapter adapter = (SceneAdapter) (forBackpack ? getBackPackSceneFragment().getListAdapter() : getScenesListFragment().getListAdapter());
		return adapter;
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
		clickOnSingleActionModeItem(spriteName, delete, R.id.delete);
		solo.waitForDialogToOpen();
		solo.waitForText(solo.getString(R.string.yes));
		solo.clickOnText(solo.getString(R.string.yes));
	}

	private void checkIfNumberOfScenesIsEqual(int expectedNumber) {
		List<Scene> sceneList = ProjectManager.getInstance().getCurrentProject().getSceneList();
		assertEquals("Number of scenes is not as expected", expectedNumber, sceneList.size());
	}

	private void addSceneWithName(String sceneName) {
		Scene sceneToAdd = new Scene(getActivity(), sceneName, ProjectManager.getInstance().getCurrentProject());
		ProjectManager.getInstance().addScene(sceneToAdd);
	}
}
