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
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.XmlHeader;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.merge.MergeTask;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.TestUtils;

public class MergeTaskTest extends AndroidTestCase {

	private Project firstProject;

	private Project secondProject;

	public void tearDown() {
		if (firstProject != null) {
			StorageHandler.getInstance().deleteProject(firstProject.getName());
		}

		if (secondProject != null) {
			StorageHandler.getInstance().deleteProject(secondProject.getName());
		}
	}

	public void testSuccessWithSameScriptsAndGlobalValues() {
		createProjectsWithSameScriptsAndGlobalValues();
		MergeTask merge = new MergeTask(firstProject, secondProject, getContext(), null, false);
		assertTrue("Error!", merge.mergeProjects("merge"));
		Project mergeResult = StorageHandler.getInstance().loadProject("merge", getContext());

		assertTrue("Error!", mergeResult.getDefaultScene().getDataContainer().getProjectVariables().size() == 1);
		assertTrue("Error!", mergeResult.getDefaultScene().getDataContainer().getProjectLists().size() == 1);

		assertTrue("Error!", mergeResult.getDefaultScene().getSpriteList().size() == 3);
		for (Sprite sprite : mergeResult.getDefaultScene().getSpriteList()) {
			assertTrue("Error!", mergeResult.getDefaultScene().getDataContainer().getOrCreateUserListListForSprite(sprite).size() == 0);
			assertTrue("Error!", mergeResult.getDefaultScene().getDataContainer().getOrCreateVariableListForSprite(sprite).size() == 0);
			assertTrue("Error!", sprite.getScriptList().size() == 1);
			assertTrue("Error!", sprite.getScript(0).getBrickList().size() == 2);
		}
		assertTrue("Error!", StorageHandler.getInstance().deleteProject("merge"));
	}

	public void testSuccessWithSameScriptsAndSpriteValues() {
		createProjectsWithSameScriptsAndSpriteValues();
		MergeTask merge = new MergeTask(firstProject, secondProject, getContext(), null, false);
		assertTrue("Error!", merge.mergeProjects("merge"));
		Project mergeResult = StorageHandler.getInstance().loadProject("merge", getContext());

		assertTrue("Error!", mergeResult.getDefaultScene().getDataContainer().getProjectVariables().size() == 0);
		assertTrue("Error!", mergeResult.getDefaultScene().getDataContainer().getProjectLists().size() == 0);

		assertTrue("Error!", mergeResult.getDefaultScene().getSpriteList().size() == 3);
		for (Sprite sprite : mergeResult.getDefaultScene().getSpriteList()) {
			assertTrue("Error!", mergeResult.getDefaultScene().getDataContainer().getOrCreateUserListListForSprite(sprite).size() == 1);
			assertTrue("Error!", mergeResult.getDefaultScene().getDataContainer().getOrCreateVariableListForSprite(sprite).size() == 1);
			assertTrue("Error!", sprite.getScriptList().size() == 1);
			assertTrue("Error!", sprite.getScript(0).getBrickList().size() == 2);
		}
		assertTrue("Error!", StorageHandler.getInstance().deleteProject("merge"));
	}

	public void testVerifyRemixUrlsOfMergedProgramConsistingOfTwoDownloadedPrograms() {
		createProjectWithDifferentScripts();
		String expectedUrlOfFirstProgram = "/pocketcode/program/12345";
		String expectedUrlOfSecondProgram = "https://scratch.mit.edu/projects/10205819";

		firstProject.getXmlHeader().setRemixParentsUrlString(expectedUrlOfFirstProgram);
		Reflection.setPrivateField(XmlHeader.class, firstProject.getXmlHeader(),
				"remixGrandparentsUrlString", "/pocketcode/program/82341");
		secondProject.getXmlHeader().setRemixParentsUrlString(expectedUrlOfSecondProgram);

		MergeTask merge = new MergeTask(firstProject, secondProject, getContext(), null, false);
		assertTrue("Error!", merge.mergeProjects("merge"));
		Project mergeResult = StorageHandler.getInstance().loadProject("merge", getContext());

		String expectedUrlFieldValue = String.format("%s [%s], %s [%s]", firstProject.getName(),
				expectedUrlOfFirstProgram, secondProject.getName(), expectedUrlOfSecondProgram);

		String mergedRemixOfString = (String) Reflection.getPrivateField(XmlHeader.class,
				mergeResult.getXmlHeader(), "remixGrandparentsUrlString");

		assertTrue("Expecting remixOf header-field to be empty!", mergedRemixOfString.equals(""));
		assertEquals("Unexpected remix-url header-field!",
				expectedUrlFieldValue, mergeResult.getXmlHeader().getRemixParentsUrlString());
	}

	public void testVerifyRemixUrlsOfMergedProgramConsistingOfTwoLocallyCreatedPrograms() {
		createProjectWithDifferentScripts();

		MergeTask merge = new MergeTask(firstProject, secondProject, getContext(), null, false);
		assertTrue("Error!", merge.mergeProjects("merge"));
		Project mergeResult = StorageHandler.getInstance().loadProject("merge", getContext());

		String expectedUrlFieldValue = String.format("%s, %s", firstProject.getName(), secondProject.getName());

		String mergedRemixOfString = (String) Reflection.getPrivateField(XmlHeader.class,
				mergeResult.getXmlHeader(), "remixGrandparentsUrlString");

		assertTrue("Expecting remixOf header-field to be empty!", mergedRemixOfString.equals(""));
		assertEquals("Unexpected remix-url header-field!",
				expectedUrlFieldValue, mergeResult.getXmlHeader().getRemixParentsUrlString());
	}

	public void testVerifyRemixUrlsOfMergedProgramWhereFirstProgramHasBeenDownloadedAndSecondProgramHasBeenLocallyCreated() {
		createProjectWithDifferentScripts();
		String expectedUrlOfFirstProgram = "http://pocketcode.org/details/3218";
		firstProject.getXmlHeader().setRemixParentsUrlString(expectedUrlOfFirstProgram);

		MergeTask merge = new MergeTask(firstProject, secondProject, getContext(), null, false);
		assertTrue("Error!", merge.mergeProjects("merge"));
		Project mergeResult = StorageHandler.getInstance().loadProject("merge", getContext());

		String expectedUrlFieldValue = String.format("%s [%s], %s",
				firstProject.getName(), expectedUrlOfFirstProgram, secondProject.getName());

		String mergedRemixOfString = (String) Reflection.getPrivateField(XmlHeader.class,
				mergeResult.getXmlHeader(), "remixGrandparentsUrlString");

		assertTrue("Expecting remixOf header-field to be empty!", mergedRemixOfString.equals(""));
		assertEquals("Unexpected remix-url header-field!",
				expectedUrlFieldValue, mergeResult.getXmlHeader().getRemixParentsUrlString());
	}

	public void testVerifyRemixUrlsOfMergedProgramWhereFirstProgramHasBeenLocallyCreatedAndSecondProgramHasBeenDownloaded() {
		createProjectWithDifferentScripts();
		String expectedUrlOfSecondProgram = "http://pocketcode.org/details/3218";
		secondProject.getXmlHeader().setRemixParentsUrlString(expectedUrlOfSecondProgram);

		MergeTask merge = new MergeTask(firstProject, secondProject, getContext(), null, false);
		assertTrue("Error!", merge.mergeProjects("merge"));
		Project mergeResult = StorageHandler.getInstance().loadProject("merge", getContext());

		String expectedUrlFieldValue = String.format("%s, %s [%s]", firstProject.getName(),
				secondProject.getName(), expectedUrlOfSecondProgram);

		String mergedRemixOfString = (String) Reflection.getPrivateField(XmlHeader.class,
				mergeResult.getXmlHeader(), "remixGrandparentsUrlString");

		assertTrue("Expecting remixOf header-field to be empty!", mergedRemixOfString.equals(""));
		assertEquals("Unexpected remix-url header-field!",
				expectedUrlFieldValue, mergeResult.getXmlHeader().getRemixParentsUrlString());
	}

	public void testVerifyRemixUrlsOfMergedMergedProgram() {
		createProjectWithDifferentScripts();
		String expectedUrlOfSecondProgram = "http://pocketcode.org/details/3218";
		secondProject.getXmlHeader().setRemixParentsUrlString(expectedUrlOfSecondProgram);

		MergeTask firstMerge = new MergeTask(firstProject, secondProject, getContext(), null, false);
		assertTrue("Error!", firstMerge.mergeProjects("firstMerge"));
		Project mergeResult = StorageHandler.getInstance().loadProject("firstMerge", getContext());

		MergeTask secondMerge = new MergeTask(mergeResult, secondProject, getContext(), null, false);
		assertTrue("Error!", secondMerge.mergeProjects("secondMerge"));
		Project finalMergeResult = StorageHandler.getInstance().loadProject("secondMerge", getContext());

		String expectedUrlFieldValue = String.format("%s [%s, %s [%s]], %s [%s]",
				"firstMerge", firstProject.getName(), secondProject.getName(), expectedUrlOfSecondProgram,
				secondProject.getName(), expectedUrlOfSecondProgram);

		String mergedRemixOfString = (String) Reflection.getPrivateField(XmlHeader.class,
				finalMergeResult.getXmlHeader(), "remixGrandparentsUrlString");

		assertTrue("Expecting remixOf header-field to be empty!", mergedRemixOfString.equals(""));
		assertEquals("Unexpected remix-url header-field!",
				expectedUrlFieldValue, finalMergeResult.getXmlHeader().getRemixParentsUrlString());
	}

	public void testSuccessWithDifferentScripts() {
		createProjectWithDifferentScripts();

		MergeTask merge = new MergeTask(firstProject, secondProject, getContext(), null, false);
		assertTrue("Error!", merge.mergeProjects("merge"));
		Project mergeResult = StorageHandler.getInstance().loadProject("merge", getContext());

		assertTrue("Error!", mergeResult.getDefaultScene().getDataContainer().getProjectVariables().size() == 1);
		assertTrue("Error!", mergeResult.getDefaultScene().getDataContainer().getProjectLists().size() == 1);
		assertTrue("Error!", mergeResult.getDefaultScene().getSpriteList().size() == 3);

		Sprite sprite = mergeResult.getDefaultScene().getSpriteList().get(0);
		assertTrue("Error!", mergeResult.getDefaultScene().getDataContainer().getOrCreateUserListListForSprite(sprite).size() == 1);
		assertTrue("Error!", mergeResult.getDefaultScene().getDataContainer().getOrCreateVariableListForSprite(sprite).size() == 1);
		assertTrue("Error!", sprite.getScriptList().size() == 2);
		assertTrue("Error!", sprite.getScript(0).getBrickList().size() == 2);
		assertTrue("Error!", sprite.getScript(1).getBrickList().size() == 2);

		sprite = mergeResult.getDefaultScene().getSpriteList().get(1);
		assertTrue("Error!", mergeResult.getDefaultScene().getDataContainer().getOrCreateUserListListForSprite(sprite).size() == 1);
		assertTrue("Error!", mergeResult.getDefaultScene().getDataContainer().getOrCreateVariableListForSprite(sprite).size() == 1);
		assertTrue("Error!", sprite.getScriptList().size() == 2);
		assertTrue("Error!", sprite.getScript(0).getBrickList().size() == 2);
		assertTrue("Error!", sprite.getScript(1).getBrickList().size() == 2);

		sprite = mergeResult.getDefaultScene().getSpriteList().get(2);
		assertTrue("Error!", mergeResult.getDefaultScene().getDataContainer().getOrCreateUserListListForSprite(sprite).size() == 0);
		assertTrue("Error!", mergeResult.getDefaultScene().getDataContainer().getOrCreateVariableListForSprite(sprite).size() == 0);
		assertTrue("Error!", sprite.getScriptList().size() == 0);

		assertTrue("Error!", StorageHandler.getInstance().deleteProject("merge"));
	}

	public void testMergeConflict() {
		createProjectForMergeConflict();

		MergeTask merge = new MergeTask(firstProject, secondProject, getContext(), null, false);
		assertFalse("Error!", merge.mergeProjects("merge"));
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

	private void createProjectForMergeConflict() {
		String firstSpriteName = getContext().getResources().getString(R.string.default_project_cloud_sprite_name_1);
		String secondSpriteName = getContext().getResources().getString(R.string.default_project_cloud_sprite_name_2);

		firstProject = TestUtils.createProjectWithGlobalValues("First Project", firstSpriteName, "test", getContext());
		secondProject = TestUtils.createProjectWithSpriteValues("Second Project", secondSpriteName, "test", getContext());
		StorageHandler.getInstance().saveProject(firstProject);
		StorageHandler.getInstance().saveProject(secondProject);
	}
}
