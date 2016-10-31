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
package org.catrobat.catroid.uitest.content.brick;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SceneStartBrick;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.dialogs.NewSceneDialog;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class SceneStartBrickTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private String sceneName;
	private String sceneName2 = "testScene2";

	public SceneStartBrickTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		createProject();

		UiTestUtils.prepareStageForTest();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo, sceneName);
	}

	public void testDismissNewSceneDialog() {
		solo.clickOnText(sceneName2);
		solo.clickOnText(solo.getString(R.string.new_broadcast_message));
		solo.waitForDialogToOpen();
		solo.goBack();
		solo.waitForDialogToClose();

		assertEquals("Not in ScriptActivity", "ui.ScriptActivity", solo.getCurrentActivity().getLocalClassName());
		assertTrue("Spinner not updated", solo.waitForText(sceneName2));
	}

	public void testSelectSceneAndPlay() {
		assertTrue(sceneName2 + " is not selected in Spinner", solo.isSpinnerTextSelected(sceneName2));

		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);

		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(2000);
		Scene scene = ProjectManager.getInstance().getSceneToPlay();
		assertEquals("scene not set", scene.getName(), sceneName2);
		solo.goBack();
		solo.goBack();
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		solo.clickOnText(sceneName2);
		solo.clickOnText(sceneName, 2);
		assertTrue(sceneName + " is not selected in Spinner", solo.searchText(sceneName, 2));

		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);

		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(2000);
		scene = ProjectManager.getInstance().getSceneToPlay();
		assertEquals("scene not set", sceneName, scene.getName());
	}

	public void testSpinnerUpdatesDelete() {
		solo.clickOnText(sceneName2);

		assertTrue(sceneName + " is not in Spinner", solo.searchText(sceneName, 2));
		assertTrue(sceneName2 + " is not in Spinner", solo.searchText(sceneName2));

		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.goBack();

		clickOnContextMenuItem(sceneName2, solo.getString(R.string.delete));
		solo.clickOnButton(solo.getString(R.string.yes));

		solo.goBack();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		solo.clickOnText(sceneName, 1);

		assertFalse(sceneName2 + " is still in Spinner", solo.searchText(sceneName2));
		assertTrue(sceneName + " is not in Spinner", solo.searchText(sceneName, 2));
	}

	public void testSpinnerUpdatesRename() {
		String newName = "nameRenamed";

		solo.clickOnText(sceneName2);

		assertTrue(sceneName + " is not in Spinner", solo.searchText(sceneName, 2));
		assertTrue(sceneName2 + " is not in Spinner", solo.searchText(sceneName2));

		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.goBack();

		clickOnContextMenuItem(sceneName2, solo.getString(R.string.rename));

		solo.clearEditText(0);
		solo.enterText(0, newName);
		solo.clickOnButton(solo.getString(R.string.ok));

		solo.goBack();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo, sceneName);

		solo.clickOnText(newName);

		assertTrue(newName + " is not in Spinner", solo.searchText(newName));
		assertTrue(sceneName + " is not in Spinner", solo.searchText(sceneName, 2));
	}

	public void testAddNewScene() {
		String newName = "newScene";
		String newText = solo.getString(R.string.new_broadcast_message);

		solo.clickOnText(sceneName2);
		solo.clickOnText(newText);

		solo.waitForFragmentByTag(NewSceneDialog.TAG);
		solo.enterText(0, newName);

		solo.clickOnText(solo.getString(R.string.ok));
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.goBack();

		assertTrue("No new scene", solo.searchText(newName));
	}

	private void createProject() {
		ProjectManager projectManager = ProjectManager.getInstance();
		Project project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Scene scene = new Scene(null, sceneName2, project);
		project.addScene(scene);
		Sprite firstSprite = new SingleSprite("cat");
		Script testScript = new StartScript();

		SceneStartBrick sceneStartBrick = new SceneStartBrick(sceneName2);
		testScript.addBrick(sceneStartBrick);

		firstSprite.addScript(testScript);
		project.getDefaultScene().addSprite(firstSprite);

		projectManager.setProject(project);
		projectManager.setCurrentScene(project.getDefaultScene());
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);
		sceneName = project.getDefaultScene().getName();
	}

	private void clickOnContextMenuItem(String sceneName, String menuItemName) {
		solo.clickLongOnText(sceneName);
		solo.waitForText(menuItemName);
		solo.clickOnText(menuItemName);
	}
}
