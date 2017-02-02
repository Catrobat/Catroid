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

package org.catrobat.catroid.test.merge;

import android.test.AndroidTestCase;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.XmlHeader;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.merge.MergeTask;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.TestUtils;

import java.util.List;

public class MergeTaskTest extends AndroidTestCase {

	private Project firstProject;

	private Project secondProject;

	private Project result;

	public void tearDown() {
		if (firstProject != null) {
			StorageHandler.getInstance().deleteProject(firstProject.getName());
		}

		if (secondProject != null) {
			StorageHandler.getInstance().deleteProject(secondProject.getName());
		}

		if (result != null) {
			StorageHandler.getInstance().deleteProject(result.getName());
		}
	}

	public void testMergeConflict() {
		createProjectForMergeConflict();

		MergeTask merge = new MergeTask(firstProject, secondProject, getContext());
		assertTrue("Error while merging projects", merge.mergeProjects("merge"));

		result = StorageHandler.getInstance().loadProject("merge", getContext());
		List<UserVariable> variables = result.getDefaultScene().getDataContainer().getProjectVariables();
		List<UserList> lists = result.getDefaultScene().getDataContainer().getProjectLists();

		assertEquals("There have to be one global user variable in the list", 2, variables.size());
		assertEquals("There have to be one global user list in the list", 2, lists.size());

		assertTrue("ConflictHelper hasn't renamed global variables correctly", variables.get(0).getName().equals("test_global"));
		assertTrue("ConflictHelper hasn't renamed global lists correctly", lists.get(0).getName().equals("test_global"));
		assertTrue("ConflictHelper hasn't renamed global variables correctly", variables.get(1).getName().equals("test_1_global"));
		assertTrue("ConflictHelper hasn't renamed global lists correctly", lists.get(1).getName().equals("test_1_global"));
	}

	public void testSuccessWithSameScriptsAndGlobalValues() {
		createProjectsWithSameScriptsAndGlobalValues();
		MergeTask merge = new MergeTask(firstProject, secondProject, getContext());
		assertTrue("Error while merging projects", merge.mergeProjects("merge"));

		result = StorageHandler.getInstance().loadProject("merge", getContext());
		Scene scene = result.getDefaultScene();
		List<UserVariable> variables = scene.getDataContainer().getProjectVariables();
		List<UserList> lists = scene.getDataContainer().getProjectLists();

		assertEquals("There have to be one global user variable in the list", 2, variables.size());
		assertEquals("There have to be one global user list in the list", 2, lists.size());
		assertEquals("There have to be 3 sprites in the default scene", 3, scene.getSpriteList().size());

		for (Sprite sprite : result.getDefaultScene().getSpriteList()) {
			assertEquals("Wrong size of UserLists", 0, scene.getDataContainer().getUserListListForSprite(sprite).size());
			assertEquals("Wrong size of UserVariables", 0, scene.getDataContainer().getVariableListForSprite(sprite).size());
			assertEquals("Wrong size of scripts", 1, sprite.getScriptList().size());
			assertEquals("Wrong size of bricks", 4, sprite.getScript(0).getBrickList().size());
		}
	}

	public void testSuccessWithSameScriptsAndSpriteValues() {
		createProjectsWithSameScriptsAndSpriteValues();
		MergeTask merge = new MergeTask(firstProject, secondProject, getContext());
		assertTrue("Error while merging projects", merge.mergeProjects("merge"));

		result = StorageHandler.getInstance().loadProject("merge", getContext());
		Scene scene = result.getDefaultScene();
		List<UserVariable> variables = scene.getDataContainer().getProjectVariables();
		List<UserList> lists = scene.getDataContainer().getProjectLists();

		assertEquals("There have to be no global user variable in the list", 0, variables.size());
		assertEquals("There have to be no global user list in the list", 0, lists.size());
		assertEquals("There have to be 3 sprites in the default scene", 3, scene.getSpriteList().size());

		for (Sprite sprite : scene.getSpriteList()) {
			assertEquals("Wrong size of UserLists", 2, scene.getDataContainer().getUserListListForSprite(sprite).size());
			assertEquals("Wrong size of UserVariables", 2, scene.getDataContainer().getVariableListForSprite(sprite).size());
			assertEquals("Wrong size of scripts", 2, sprite.getScriptList().size());
			assertEquals("Wrong size of bricks", 2, sprite.getScript(0).getBrickList().size());
		}
	}

	public void testSuccessWithDifferentScripts() {
		createProjectWithDifferentScripts();

		MergeTask merge = new MergeTask(firstProject, secondProject, getContext());
		assertTrue("Error while merging projects", merge.mergeProjects("merge"));

		result = StorageHandler.getInstance().loadProject("merge", getContext());
		Scene scene = result.getDefaultScene();
		List<UserVariable> variables = scene.getDataContainer().getProjectVariables();
		List<UserList> lists = scene.getDataContainer().getProjectLists();

		assertEquals("There have to be one global user variable in the list", 2, variables.size());
		assertEquals("There have to be one global user list in the list", 2, lists.size());
		assertEquals("There have to be 3 sprites in the default scene", 3, scene.getSpriteList().size());

		Sprite sprite = scene.getSpriteList().get(0);
		assertEquals("Wrong size of UserLists", 2, scene.getDataContainer().getUserListListForSprite(sprite).size());
		assertEquals("Wrong size of UserVariables", 2, scene.getDataContainer().getVariableListForSprite(sprite).size());
		assertEquals("Wrong size of scripts", 3, sprite.getScriptList().size());
		assertEquals("Wrong size of bricks in the first script", 4, sprite.getScript(0).getBrickList().size());
		assertEquals("Wrong size of bricks in the second script", 2, sprite.getScript(1).getBrickList().size());

		sprite = scene.getSpriteList().get(1);
		assertEquals("Wrong size of UserLists", 2, scene.getDataContainer().getUserListListForSprite(sprite).size());
		assertEquals("Wrong size of UserVariables", 2, scene.getDataContainer().getVariableListForSprite(sprite).size());
		assertEquals("Wrong size of scripts", 3, sprite.getScriptList().size());
		assertEquals("Wrong size of bricks in the first script", 4, sprite.getScript(0).getBrickList().size());
		assertEquals("Wrong size of bricks in the second script", 2, sprite.getScript(1).getBrickList().size());

		sprite = scene.getSpriteList().get(2);
		assertEquals("Wrong size of UserLists", 0, scene.getDataContainer().getOrCreateUserListListForSprite(sprite).size());
		assertEquals("Wrong size of UserVariables", 0, scene.getDataContainer().getOrCreateVariableListForSprite(sprite).size());
		assertEquals("Wrong size of scripts", 0, sprite.getScriptList().size());
	}

	public void testVerifyRemixUrlsOfMergedProgramConsistingOfTwoDownloadedPrograms() {
		createProjectWithDifferentScripts();
		String expectedUrlOfFirstProgram = "/pocketcode/program/12345";
		String expectedUrlOfSecondProgram = "https://scratch.mit.edu/projects/10205819";

		firstProject.getXmlHeader().setRemixParentsUrlString(expectedUrlOfFirstProgram);
		Reflection.setPrivateField(XmlHeader.class, firstProject.getXmlHeader(),
				"remixGrandparentsUrlString", "/pocketcode/program/82341");
		secondProject.getXmlHeader().setRemixParentsUrlString(expectedUrlOfSecondProgram);

		MergeTask merge = new MergeTask(firstProject, secondProject, getContext());
		assertTrue("Error while merging projects", merge.mergeProjects("merge"));
		result = StorageHandler.getInstance().loadProject("merge", getContext());

		String expectedUrlFieldValue = String.format("%s [%s], %s [%s]", firstProject.getName(),
				expectedUrlOfFirstProgram, secondProject.getName(), expectedUrlOfSecondProgram);

		String mergedRemixOfString = (String) Reflection.getPrivateField(XmlHeader.class,
				result.getXmlHeader(), "remixGrandparentsUrlString");

		assertTrue("Expecting remixOf header-field to be empty!", mergedRemixOfString.equals(""));
		assertEquals("Unexpected remix-url header-field!",
				expectedUrlFieldValue, result.getXmlHeader().getRemixParentsUrlString());
	}

	public void testVerifyRemixUrlsOfMergedProgramConsistingOfTwoLocallyCreatedPrograms() {
		createProjectWithDifferentScripts();

		MergeTask merge = new MergeTask(firstProject, secondProject, getContext());
		assertTrue("Error while merging projects", merge.mergeProjects("merge"));
		result = StorageHandler.getInstance().loadProject("merge", getContext());

		String expectedUrlFieldValue = String.format("%s, %s", firstProject.getName(), secondProject.getName());

		String mergedRemixOfString = (String) Reflection.getPrivateField(XmlHeader.class,
				result.getXmlHeader(), "remixGrandparentsUrlString");

		assertTrue("Expecting remixOf header-field to be empty!", mergedRemixOfString.equals(""));
		assertEquals("Unexpected remix-url header-field!",
				expectedUrlFieldValue, result.getXmlHeader().getRemixParentsUrlString());
	}

	public void testVerifyRemixUrlsOfMergedProgramWhereFirstProgramHasBeenDownloadedAndSecondProgramHasBeenLocallyCreated() {
		createProjectWithDifferentScripts();
		String expectedUrlOfFirstProgram = "http://pocketcode.org/details/3218";
		firstProject.getXmlHeader().setRemixParentsUrlString(expectedUrlOfFirstProgram);

		MergeTask merge = new MergeTask(firstProject, secondProject, getContext());
		assertTrue("Error while merging projects", merge.mergeProjects("merge"));
		result = StorageHandler.getInstance().loadProject("merge", getContext());

		String expectedUrlFieldValue = String.format("%s [%s], %s",
				firstProject.getName(), expectedUrlOfFirstProgram, secondProject.getName());

		String mergedRemixOfString = (String) Reflection.getPrivateField(XmlHeader.class,
				result.getXmlHeader(), "remixGrandparentsUrlString");

		assertTrue("Expecting remixOf header-field to be empty!", mergedRemixOfString.equals(""));
		assertEquals("Unexpected remix-url header-field!",
				expectedUrlFieldValue, result.getXmlHeader().getRemixParentsUrlString());
	}

	public void testVerifyRemixUrlsOfMergedProgramWhereFirstProgramHasBeenLocallyCreatedAndSecondProgramHasBeenDownloaded() {
		createProjectWithDifferentScripts();
		String expectedUrlOfSecondProgram = "http://pocketcode.org/details/3218";
		secondProject.getXmlHeader().setRemixParentsUrlString(expectedUrlOfSecondProgram);

		MergeTask merge = new MergeTask(firstProject, secondProject, getContext());
		assertTrue("Error while merging projects", merge.mergeProjects("merge"));
		result = StorageHandler.getInstance().loadProject("merge", getContext());

		String expectedUrlFieldValue = String.format("%s, %s [%s]", firstProject.getName(),
				secondProject.getName(), expectedUrlOfSecondProgram);

		String mergedRemixOfString = (String) Reflection.getPrivateField(XmlHeader.class,
				result.getXmlHeader(), "remixGrandparentsUrlString");

		assertTrue("Expecting remixOf header-field to be empty!", mergedRemixOfString.equals(""));
		assertEquals("Unexpected remix-url header-field!",
				expectedUrlFieldValue, result.getXmlHeader().getRemixParentsUrlString());
	}

	public void testVerifyRemixUrlsOfMergedMergedProgram() {
		createProjectWithDifferentScripts();
		String expectedUrlOfSecondProgram = "http://pocketcode.org/details/3218";
		secondProject.getXmlHeader().setRemixParentsUrlString(expectedUrlOfSecondProgram);

		MergeTask firstMerge = new MergeTask(firstProject, secondProject, getContext());
		assertTrue("Error!", firstMerge.mergeProjects("firstMerge"));
		result = StorageHandler.getInstance().loadProject("firstMerge", getContext());

		MergeTask secondMerge = new MergeTask(result, secondProject, getContext());
		assertTrue("Error!", secondMerge.mergeProjects("secondMerge"));
		StorageHandler.getInstance().deleteProject(result.getName());
		result = StorageHandler.getInstance().loadProject("secondMerge", getContext());

		String expectedUrlFieldValue = String.format("%s [%s, %s [%s]], %s [%s]",
				"firstMerge", firstProject.getName(), secondProject.getName(), expectedUrlOfSecondProgram,
				secondProject.getName(), expectedUrlOfSecondProgram);

		String mergedRemixOfString = (String) Reflection.getPrivateField(XmlHeader.class,
				result.getXmlHeader(), "remixGrandparentsUrlString");

		assertTrue("Expecting remixOf header-field to be empty!", mergedRemixOfString.equals(""));
		assertEquals("Unexpected remix-url header-field!",
				expectedUrlFieldValue, result.getXmlHeader().getRemixParentsUrlString());
	}

	private void createProjectForMergeConflict() {
		String firstSpriteName = getContext().getResources().getString(R.string.default_project_cloud_sprite_name_1);
		String secondSpriteName = getContext().getResources().getString(R.string.default_project_cloud_sprite_name_2);

		firstProject = TestUtils.createProjectWithGlobalValues("First Project", firstSpriteName, "test", getContext());
		secondProject = TestUtils.createProjectWithSpriteValues("Second Project", secondSpriteName, "test", getContext());
		StorageHandler.getInstance().saveProject(firstProject);
		StorageHandler.getInstance().saveProject(secondProject);
	}

	private void createProjectsWithSameScriptsAndGlobalValues() {
		String firstSpriteName = getContext().getResources().getString(R.string.default_project_cloud_sprite_name_1);
		String secondSpriteName = getContext().getResources().getString(R.string.default_project_cloud_sprite_name_2);

		firstProject = TestUtils.createProjectWithGlobalValues("First Project", firstSpriteName, "test", getContext());
		secondProject = TestUtils.createProjectWithGlobalValues("Second Project", secondSpriteName, "test", getContext());
		StorageHandler.getInstance().saveProject(firstProject);
		StorageHandler.getInstance().saveProject(secondProject);
	}

	private void createProjectsWithSameScriptsAndSpriteValues() {
		String firstSpriteName = getContext().getResources().getString(R.string.default_project_cloud_sprite_name_1);
		String secondSpriteName = getContext().getResources().getString(R.string.default_project_cloud_sprite_name_2);

		firstProject = TestUtils.createProjectWithSpriteValues("First Project", firstSpriteName, "test1", getContext());
		secondProject = TestUtils.createProjectWithSpriteValues("Second Project", secondSpriteName, "test1", getContext());
		StorageHandler.getInstance().saveProject(firstProject);
		StorageHandler.getInstance().saveProject(secondProject);
	}

	private void createProjectWithDifferentScripts() {
		String firstSpriteName = getContext().getResources().getString(R.string.default_project_cloud_sprite_name_1);
		String secondSpriteName = getContext().getResources().getString(R.string.default_project_cloud_sprite_name_2);

		firstProject = TestUtils.createProjectWithGlobalValues("First Project", firstSpriteName, "test1", getContext());
		secondProject = TestUtils.createProjectWithSpriteValues("Second Project", firstSpriteName, "test2", getContext());
		secondProject.getDefaultScene().addSprite(new SingleSprite(secondSpriteName));
		StorageHandler.getInstance().saveProject(firstProject);
		StorageHandler.getInstance().saveProject(secondProject);
	}
}
