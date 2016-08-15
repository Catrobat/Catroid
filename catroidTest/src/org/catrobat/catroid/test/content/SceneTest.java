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
package org.catrobat.catroid.test.content;

import android.test.AndroidTestCase;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.AddItemToUserListBrick;
import org.catrobat.catroid.content.bricks.MoveNStepsBrick;
import org.catrobat.catroid.content.bricks.SceneStartBrick;
import org.catrobat.catroid.content.bricks.SceneTransitionBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.BackPackSceneController;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.util.List;

public class SceneTest extends AndroidTestCase {

	private static final String SCENE_NAME = "testScene";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Project project = new Project(getContext(), UiTestUtils.DEFAULT_TEST_PROJECT_NAME, false);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().saveProject(getContext());

		Scene scene = StorageHandler.getInstance().createDefaultScene(SCENE_NAME, false, false, getContext());
		UtilFile.deleteDirectory(new File(Utils.buildScenePath(project.getName(), project.getDefaultScene().getName())));
		project.removeScene(project.getDefaultScene());
		project.addScene(scene);
		ProjectManager.getInstance().setCurrentScene(scene);
		ProjectManager.getInstance().setProject(project);
		StorageHandler.getInstance().saveProject(project);
	}

	@Override
	protected void tearDown() throws Exception {
		ProjectManager.getInstance().deleteCurrentProject(getContext());
		super.tearDown();
	}

	public void testCloneScene() {
		ProjectManager projectManager = ProjectManager.getInstance();
		Scene cloneScene = projectManager.getCurrentScene().clone();
		assertNotNull("cloned scene was null", cloneScene);
		assertTrue("Scene was not cloned correctly", Utils.isStandardScene(projectManager.getCurrentProject(), cloneScene
				.getName(), getContext()));
		assertFalse("Scene was the same instance", cloneScene.equals(projectManager.getCurrentScene()));
	}

	public void testRename() {
		Scene scene = ProjectManager.getInstance().getCurrentScene();
		File originalSceneDir = new File(Utils.buildScenePath(scene.getProject().getName(), scene.getName()));

		assertTrue("Original dir does not exist " + originalSceneDir.getAbsolutePath(), originalSceneDir.exists());
		long originalSize = folderSize(originalSceneDir);

		assertTrue("rename did not work", scene.rename("newName", getContext(), false));

		File renamedSceneDir = new File(Utils.buildScenePath(scene.getProject().getName(), scene.getName()));
		assertTrue("Renamed dir does not exist " + renamedSceneDir.getAbsolutePath(), renamedSceneDir.exists());
		assertFalse("Original dir still exists " + originalSceneDir.getAbsolutePath(), originalSceneDir.exists());
		assertEquals("Directories are not same size", originalSize, folderSize(renamedSceneDir));
	}

	public void testBackpackScene() throws InterruptedException {
		Scene scene = ProjectManager.getInstance().getCurrentScene();
		boolean success = BackPackSceneController.getInstance().backpackScene(scene);
		assertTrue("Scene was not backpacked correctly", success && BackPackListManager.getInstance()
				.backPackedScenesContains(scene));

		long original = folderSize(new File(Utils.buildScenePath(scene.getProject().getName(), scene.getName())));
		long backpackSize = folderSize(new File(Utils.buildBackpackScenePath(scene.getName())));
		assertEquals("Scene Folder was not copied to Backpack correctly", original, backpackSize);

		Scene backPackedScene = BackPackListManager.getInstance().getBackPackedScenes().get(0);
		assertTrue("Backpacked scene not cloned correctly", Utils.isStandardScene(ProjectManager.getInstance()
				.getCurrentProject(), backPackedScene.getName(), getContext()));

		BackPackListManager.getInstance().saveBackpack();
		Thread.sleep(2000);
		BackPackListManager.getInstance().loadBackpack();
		Thread.sleep(2000);
		assertTrue("Scene was not serialized and deserialized correctly", BackPackListManager.getInstance()
				.backPackedScenesContains(scene));

		Scene unpackedScene = BackPackSceneController.getInstance().unpackScene(backPackedScene);
		assertTrue("Scene was not unpacked correctly", unpackedScene != null && Utils.isStandardScene(unpackedScene
				.getProject(), unpackedScene.getName(), getContext()));
		long unpackedSize = folderSize(new File(Utils.buildScenePath(unpackedScene.getProject().getName(),
				unpackedScene.getName())));
		assertEquals("Unpacked Folder has not correct size", original, unpackedSize);
	}

	public void testBackPackScenesWithHiddenScene() throws InterruptedException {
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		String hiddenSceneName1 = "hiddenScene1";
		String hiddenSceneName2 = "hiddenScene2";
		Scene hiddenScene1 = StorageHandler.getInstance().createDefaultScene(hiddenSceneName1, false, false,
				getContext());
		currentProject.addScene(hiddenScene1);
		Scene hiddenScene2 = StorageHandler.getInstance().createDefaultScene(hiddenSceneName2, false, false,
				getContext());
		currentProject.addScene(hiddenScene2);
		SceneTransitionBrick transitionBrick = new SceneTransitionBrick(hiddenSceneName1);
		SceneStartBrick startBrick = new SceneStartBrick(hiddenSceneName2);
		StartScript script = new StartScript();
		script.addBrick(transitionBrick);
		script.addBrick(startBrick);
		currentProject.getDefaultScene().getSpriteList().get(0).addScript(script);
		StorageHandler.getInstance().saveProject(currentProject);

		Scene scene = ProjectManager.getInstance().getCurrentProject().getDefaultScene();
		boolean success = BackPackSceneController.getInstance().backpackScene(scene);
		assertTrue("Scene was not backpacked correctly", success && BackPackListManager.getInstance()
				.backPackedScenesContains(scene) && BackPackListManager.getInstance()
				.hiddenBackPackedScenesContains(hiddenScene1) && BackPackListManager.getInstance()
				.hiddenBackPackedScenesContains(hiddenScene2));

		long original = folderSize(new File(Utils.buildScenePath(scene.getProject().getName(), scene.getName())));
		long backpackSize = folderSize(new File(Utils.buildBackpackScenePath(scene.getName())));
		assertEquals("Scene Folder was not copied to Backpack correctly", original, backpackSize);

		original = folderSize(new File(Utils.buildScenePath(hiddenScene1.getProject().getName(), hiddenSceneName1)));
		backpackSize = folderSize(new File(Utils.buildBackpackScenePath(hiddenSceneName1)));
		assertEquals(hiddenScene1 + " Folder was not copied to Backpack correctly", original, backpackSize);

		original = folderSize(new File(Utils.buildScenePath(hiddenScene2.getProject().getName(), hiddenSceneName2)));
		backpackSize = folderSize(new File(Utils.buildBackpackScenePath(hiddenSceneName2)));
		assertEquals(hiddenScene2 + " Folder was not copied to Backpack correctly", original, backpackSize);

		Scene backPackedScene = BackPackListManager.getInstance().getBackPackedScenes().get(0);
		assertTrue("Backpacked scene not cloned correctly", Utils.isStandardScene(ProjectManager.getInstance()
				.getCurrentProject(), backPackedScene.getName(), getContext()));

		Scene hiddenBackPackedScene1 = BackPackListManager.getInstance().getHiddenSceneByName(hiddenSceneName1);
		assertTrue("Backpacked scene" + hiddenSceneName1 + "not cloned correctly",
				Utils.isStandardScene(ProjectManager.getInstance().getCurrentProject(), hiddenBackPackedScene1.getName(), getContext()));

		Scene hiddenBackPackedScene2 = BackPackListManager.getInstance().getHiddenSceneByName(hiddenSceneName2);
		assertTrue("Backpacked scene" + hiddenSceneName2 + "not cloned correctly",
				Utils.isStandardScene(ProjectManager.getInstance().getCurrentProject(), hiddenBackPackedScene1.getName(), getContext()));

		BackPackListManager.getInstance().saveBackpack();
		Thread.sleep(2000);
		BackPackListManager.getInstance().loadBackpack();
		Thread.sleep(2000);
		assertTrue("Scene was not serialized and deserialized correctly", BackPackListManager.getInstance()
				.backPackedScenesContains(scene));
		assertTrue(hiddenSceneName1 + " was not serialized and deserialized correctly", BackPackListManager
				.getInstance().hiddenBackPackedScenesContains(hiddenBackPackedScene1));
		assertTrue(hiddenSceneName2 + " was not serialized and deserialized correctly", BackPackListManager
				.getInstance().hiddenBackPackedScenesContains(hiddenBackPackedScene1));

		String unpackedHiddenSceneName1 = Utils.getUniqueSceneName(hiddenBackPackedScene1.getName(), false);
		String unpackedHiddenSceneName2 = Utils.getUniqueSceneName(hiddenBackPackedScene2.getName(), false);

		Scene unpackedScene = BackPackSceneController.getInstance().unpackScene(backPackedScene);
		assertNotNull("unpacked scene was null", unpackedScene);
		assertTrue("Scene was not unpacked correctly", Utils.isStandardScene(unpackedScene
				.getProject(), unpackedScene.getName(), getContext()));
		long unpackedSize = folderSize(new File(Utils.buildScenePath(unpackedScene.getProject().getName(),
				unpackedScene.getName())));
		assertEquals("Unpacked Folder has not correct size", original, unpackedSize);

		unpackedScene = ProjectManager.getInstance().getCurrentProject().getSceneByName(unpackedHiddenSceneName1);
		assertTrue(unpackedHiddenSceneName1 + " was not unpacked correctly", unpackedScene != null && Utils
				.isStandardScene(unpackedScene
				.getProject(), unpackedScene.getName(), getContext()));
		unpackedSize = folderSize(new File(Utils.buildScenePath(unpackedScene.getProject().getName(),
				unpackedScene.getName())));
		assertEquals("Unpacked Folder has not correct size", original, unpackedSize);

		unpackedScene = ProjectManager.getInstance().getCurrentProject().getSceneByName(unpackedHiddenSceneName2);
		assertTrue(unpackedHiddenSceneName2 + " was not unpacked correctly", unpackedScene != null && Utils
				.isStandardScene(unpackedScene
				.getProject(), unpackedScene.getName(), getContext()));
		unpackedSize = folderSize(new File(Utils.buildScenePath(unpackedScene.getProject().getName(),
				unpackedScene.getName())));
		assertEquals("Unpacked Folder has not correct size", original, unpackedSize);
	}

	public void testUnpackWithVariables() {
		Project project = new Project(getContext(), "testProjectForVariables", false);
		Scene scene2 = new Scene(getContext(), "scene 2", project);
		Scene scene1 = project.getDefaultScene();

		UserVariable var = scene1.getDataContainer().addProjectUserVariable("one");
		UserList list = scene1.getDataContainer().addProjectUserList("list");
		SetVariableBrick setVariableBrick = new SetVariableBrick(new Formula(1), var);
		AddItemToUserListBrick userListBrick = new AddItemToUserListBrick(new Formula(2), list);
		StartScript start = new StartScript();
		start.addBrick(setVariableBrick);
		start.addBrick(userListBrick);
		scene1.getSpriteList().get(0).addScript(start);

		FormulaElement variableElement = new FormulaElement(FormulaElement.ElementType.USER_VARIABLE, var.getName(), null);
		FormulaElement listElement = new FormulaElement(FormulaElement.ElementType.USER_LIST, list.getName(), null);
		SetSizeToBrick sizeToBrick = new SetSizeToBrick(new Formula(variableElement));
		MoveNStepsBrick moveBrick = new MoveNStepsBrick(new Formula(listElement));
		StartScript start2 = new StartScript();
		start2.addBrick(sizeToBrick);
		start2.addBrick(moveBrick);
		scene2.getSpriteList().get(0).addScript(start2);

		project.addScene(scene2);
		StorageHandler.getInstance().saveProject(project);

		BackPackSceneController.getInstance().backpackScene(scene2);
		UiTestUtils.createEmptyProject();
		Scene sceneToUnpack = null;
		for (Scene scene : BackPackListManager.getInstance().getBackPackedScenes()) {
			if (scene.getName().equals(scene2.getName())) {
				sceneToUnpack = scene;
			}
		}
		BackPackSceneController.getInstance().unpackScene(sceneToUnpack);
		Project current = ProjectManager.getInstance().getCurrentProject();

		List<UserList> userLists = current.getProjectLists();
		List<UserVariable> userVariables = current.getProjectVariables();
		assertEquals("ProjectList Size not correct", 1, userLists.size());
		assertEquals("ProjectVariables Size not correct", 1, userVariables.size());
		assertEquals("Wrong userList", list.getName(), userLists.get(0).getName());
		assertEquals("Wrong userVariable", var.getName(), userVariables.get(0).getName());
	}

	private static long folderSize(File directory) {
		try {
			long length = 0;
			for (File file : directory.listFiles()) {
				if (file.isFile()) {
					length += file.length();
				} else {
					length += folderSize(file);
				}
			}
			return length;
		} catch (Exception e) {
			return 0;
		}
	}
}
