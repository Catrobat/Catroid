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

package org.catrobat.catroid.test.merge;

import android.test.AndroidTestCase;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.merge.MergeTask;
import org.catrobat.catroid.test.utils.TestUtils;

public class MergeTaskTest extends AndroidTestCase {

	private Project firstProject;

	private Project secondProject;

	public void testSuccessWithSameScriptsAndGlobalValues() {
		createProjectsWithSameScriptsAndGlobalValues();
		MergeTask merge = new MergeTask(firstProject, secondProject, null, null);
		assertTrue(merge.mergeProjects("merge"));
		Project mergeResult = StorageHandler.getInstance().loadProject("merge");

		assertTrue(mergeResult.getDataContainer().getProjectVariables().size() == 1);
		assertTrue(mergeResult.getDataContainer().getProjectLists().size() == 1);

		assertTrue(mergeResult.getSpriteList().size() == 3);
		for (Sprite sprite : mergeResult.getSpriteList()) {
			assertTrue(mergeResult.getDataContainer().getOrCreateUserListListForSprite(sprite).size() == 0);
			assertTrue(mergeResult.getDataContainer().getOrCreateVariableListForSprite(sprite).size() == 0);
			assertTrue(sprite.getScriptList().size() == 1);
			assertTrue(sprite.getScript(0).getBrickList().size() == 2);
		}
		assertTrue(StorageHandler.getInstance().deleteProject("merge"));
	}

	public void testSuccessWithSameScriptsAndSpriteValues() {
		createProjectsWithSameScriptsAndSpriteValues();
		MergeTask merge = new MergeTask(firstProject, secondProject, null, null);
		assertTrue(merge.mergeProjects("merge"));
		Project mergeResult = StorageHandler.getInstance().loadProject("merge");

		assertTrue(mergeResult.getDataContainer().getProjectVariables().size() == 0);
		assertTrue(mergeResult.getDataContainer().getProjectLists().size() == 0);

		assertTrue(mergeResult.getSpriteList().size() == 3);
		for (Sprite sprite : mergeResult.getSpriteList()) {
			assertTrue(mergeResult.getDataContainer().getOrCreateUserListListForSprite(sprite).size() == 1);
			assertTrue(mergeResult.getDataContainer().getOrCreateVariableListForSprite(sprite).size() == 1);
			assertTrue(sprite.getScriptList().size() == 1);
			assertTrue(sprite.getScript(0).getBrickList().size() == 2);
		}
		assertTrue(StorageHandler.getInstance().deleteProject("merge"));
	}

	public void testSuccessWithDifferentScripts() {
		createProjectWithDifferentScripts();

		MergeTask merge = new MergeTask(firstProject, secondProject, null, null);
		assertTrue(merge.mergeProjects("merge"));
		Project mergeResult = StorageHandler.getInstance().loadProject("merge");

		assertTrue(mergeResult.getDataContainer().getProjectVariables().size() == 1);
		assertTrue(mergeResult.getDataContainer().getProjectLists().size() == 1);
		assertTrue(mergeResult.getSpriteList().size() == 3);

		Sprite sprite = mergeResult.getSpriteList().get(0);
		assertTrue(mergeResult.getDataContainer().getOrCreateUserListListForSprite(sprite).size() == 1);
		assertTrue(mergeResult.getDataContainer().getOrCreateVariableListForSprite(sprite).size() == 1);
		assertTrue(sprite.getScriptList().size() == 2);
		assertTrue(sprite.getScript(0).getBrickList().size() == 2);
		assertTrue(sprite.getScript(1).getBrickList().size() == 2);

		sprite = mergeResult.getSpriteList().get(1);
		assertTrue(mergeResult.getDataContainer().getOrCreateUserListListForSprite(sprite).size() == 1);
		assertTrue(mergeResult.getDataContainer().getOrCreateVariableListForSprite(sprite).size() == 1);
		assertTrue(sprite.getScriptList().size() == 2);
		assertTrue(sprite.getScript(0).getBrickList().size() == 2);
		assertTrue(sprite.getScript(1).getBrickList().size() == 2);

		sprite = mergeResult.getSpriteList().get(2);
		assertTrue(mergeResult.getDataContainer().getOrCreateUserListListForSprite(sprite).size() == 0);
		assertTrue(mergeResult.getDataContainer().getOrCreateVariableListForSprite(sprite).size() == 0);
		assertTrue(sprite.getScriptList().size() == 0);

		assertTrue(StorageHandler.getInstance().deleteProject("merge"));
	}

	public void testMergeConflict() {
		createProjectForMergeConflict();

		MergeTask merge = new MergeTask(firstProject, secondProject, null, null);
		assertFalse(merge.mergeProjects("merge"));
	}

	private void createProjectsWithSameScriptsAndGlobalValues() {
		String firstSpriteName = getContext().getResources().getString(R.string.default_project_cloud_sprite_name_1);
		String secondSpriteName = getContext().getResources().getString(R.string.default_project_cloud_sprite_name_2);

		firstProject = TestUtils.createProjectWithGlobalValues("First Project", firstSpriteName, "test", getContext());
		secondProject = TestUtils.createProjectWithGlobalValues("Second Project", secondSpriteName, "test", getContext());
	}

	private void createProjectsWithSameScriptsAndSpriteValues() {
		String firstSpriteName = getContext().getResources().getString(R.string.default_project_cloud_sprite_name_1);
		String secondSpriteName = getContext().getResources().getString(R.string.default_project_cloud_sprite_name_2);

		firstProject = TestUtils.createProjectWithSpriteValues("First Project", firstSpriteName, "test1", getContext());
		secondProject = TestUtils.createProjectWithSpriteValues("Second Project", secondSpriteName, "test1", getContext());
	}

	private void createProjectWithDifferentScripts() {
		String firstSpriteName = getContext().getResources().getString(R.string.default_project_cloud_sprite_name_1);
		String secondSpriteName = getContext().getResources().getString(R.string.default_project_cloud_sprite_name_2);

		firstProject = TestUtils.createProjectWithGlobalValues("First Project", firstSpriteName, "test1", getContext());
		secondProject = TestUtils.createProjectWithSpriteValues("Second Project", firstSpriteName, "test2", getContext());
		secondProject.addSprite(new Sprite(secondSpriteName));
	}

	private void createProjectForMergeConflict() {
		String firstSpriteName = getContext().getResources().getString(R.string.default_project_cloud_sprite_name_1);
		String secondSpriteName = getContext().getResources().getString(R.string.default_project_cloud_sprite_name_2);

		firstProject = TestUtils.createProjectWithGlobalValues("First Project", firstSpriteName, "test", getContext());
		secondProject = TestUtils.createProjectWithSpriteValues("Second Project", secondSpriteName, "test", getContext());
	}
}
