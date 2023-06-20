/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.test.achievement;


import android.content.SharedPreferences;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.achievements.Achievement;
import org.catrobat.catroid.achievements.AchievementCondition;
import org.catrobat.catroid.achievements.AchievementSystem;
import org.catrobat.catroid.achievements.Query;
import org.catrobat.catroid.common.DefaultProjectHandler;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick;
import org.catrobat.catroid.io.StorageOperations;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import androidx.test.core.app.ApplicationProvider;

import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;

import static org.junit.Assert.assertTrue;

public class RunQueriesTest {

	AchievementSystem achievementSystem = AchievementSystem.getInstance();
	private static final String PROJECT_NAME = RunQueriesTest.class.getSimpleName();
	private final File projectDir = new File(DEFAULT_ROOT_DIRECTORY, PROJECT_NAME);
	ProjectManager projectManager = ProjectManager.getInstance();


	String achievement_key = "achievement_test_key";
	String achievement_title= "achievement_title";
	String key = "test_key";
	String description = "test_description";

	@Before
	public void setUp() throws IOException {
		achievementSystem.reset();
		achievementSystem.setPreferences(ApplicationProvider.getApplicationContext());
		achievementSystem.setActive(true);
		SharedPreferences.Editor editor = achievementSystem.getEditor();
		editor.putBoolean(achievement_key, false);
		editor.putInt(key+"_Int", 0);
		editor.putBoolean(key+"_Boolean", false);
		editor.commit();
		Achievement achievement = new Achievement(achievement_title, achievement_key,
				R.drawable.test_image);
		AchievementCondition condition = new AchievementCondition(key, 1,description);
		achievement.addCondition(condition);
		achievementSystem.addAchievement(achievement);
		achievementSystem.addCondition(condition);


		if (projectDir.isDirectory()) {
				StorageOperations.deleteDir(projectDir);
		}

		Project defaultProject;

		try {
			defaultProject = DefaultProjectHandler
					.createAndSaveDefaultProject(PROJECT_NAME,
							ApplicationProvider.getApplicationContext(), true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		projectManager.setCurrentProject(defaultProject);
	}

	@After
	public void tearDown() throws IOException{
		if (projectDir.isDirectory()) {
				StorageOperations.deleteDir(projectDir);
		}

		achievementSystem.setActive(false);
	}




	@Test
	public void foreverBrickQuery()
	{
		for (Scene scene: projectManager.getCurrentProject().getSceneList()){
			for (Sprite sprite: scene.getSpriteList()) {
				for (Script script: sprite.getScriptList()) {
					for (Brick brick: script.getBrickList()) {
						if (brick instanceof ForeverBrick)
						{
							for (int i = 0; i < 3; i++) {
								((ForeverBrick) brick).addBrick(new ChangeXByNBrick(5));
							}
							break;
						}
					}
				}
			}
		}
		achievementSystem.addQuery(key, new Query() {
			@Override
			protected boolean query(ProjectManager projectManager) {
				for (Scene scene: projectManager.getCurrentProject().getSceneList()) {
					for (Sprite sprite: scene.getSpriteList()) {
						for (Script script: sprite.getScriptList()) {
							for (Brick brick: script.getBrickList()) {
								if(brick instanceof ForeverBrick && ((ForeverBrick) brick).getNestedBricks().size() >=5)
								{
									return true;
								}
							}
						}
					}
				}
				return false;
			}
		});
		achievementSystem.runQueries(projectManager);
		assertTrue(achievementSystem.getAchievement(achievement_key).isUnlocked());
	}

	@Test
	public void ifThenBrickQuery()
	{
		for (Scene scene: projectManager.getCurrentProject().getSceneList()){
			for (Sprite sprite: scene.getSpriteList()) {
				for (Script script: sprite.getScriptList()) {
					List<Brick> brickList = script.getBrickList();
					for (int i = 0; i < brickList.size(); i++) {
						if(brickList.get(i) instanceof ForeverBrick)
						{
							IfThenLogicBeginBrick brick = new IfThenLogicBeginBrick();
							for (int j = 0; j < 5; j++) {
								brick.addBrick(new ChangeXByNBrick(5));
							}
							brickList.add(i, brick);
							break;
						}
					}
				}
			}
		}
		achievementSystem.addQuery(key, new Query() {
			@Override
			protected boolean query(ProjectManager projectManager) {
				for (Scene scene: projectManager.getCurrentProject().getSceneList()) {
					for (Sprite sprite: scene.getSpriteList()) {
						for (Script script: sprite.getScriptList()) {
							for (Brick brick: script.getBrickList()) {
								if(brick instanceof IfThenLogicBeginBrick &&
										((IfThenLogicBeginBrick) brick).getNestedBricks().size() >= 5)
								{
									return true;
								}
							}
						}
					}
				}
				return false;
			}
		});
		achievementSystem.runQueries(projectManager);
		assertTrue(achievementSystem.getAchievement(achievement_key).isUnlocked());
	}
	@Test
	public void projectHasBricks()
	{
		achievementSystem.addQuery(key, new Query() {
			@Override
			protected boolean query(ProjectManager projectManager) {
				for (Scene scene: projectManager.getCurrentProject().getSceneList()) {
					for (Sprite sprite: scene.getSpriteList()) {
						for (Script script: sprite.getScriptList()) {
							if(script.getBrickList().size() > 0)
							{
								return true;
							}
						}
					}
				}
				return false;
			}
		});
		achievementSystem.runQueries(projectManager);
		assertTrue(achievementSystem.getAchievement(achievement_key).isUnlocked());
	}

	@Test
	public void ifElseBrickQueryIfBranch()
	{
		for (Scene scene: projectManager.getCurrentProject().getSceneList()){
			for (Sprite sprite: scene.getSpriteList()) {
				for (Script script: sprite.getScriptList()) {
					List<Brick> brickList = script.getBrickList();
					for (int i = 0; i < brickList.size(); i++) {
						if(brickList.get(i) instanceof ForeverBrick)
						{
							IfLogicBeginBrick brick = new IfLogicBeginBrick();
							for (int j = 0; j < 5; j++) {
								brick.addBrickToIfBranch(new ChangeXByNBrick(5));
							}
							brickList.add(i, brick);
							break;
						}
					}
				}
			}
		}
		addIfElseQuery();

		achievementSystem.runQueries(projectManager);
		assertTrue(achievementSystem.getAchievement(achievement_key).isUnlocked());
	}

	@Test
	public void ifElseBrickQueryElseBranch()
	{
		for (Scene scene: projectManager.getCurrentProject().getSceneList()){
			for (Sprite sprite: scene.getSpriteList()) {
				for (Script script: sprite.getScriptList()) {
					List<Brick> brickList = script.getBrickList();
					for (int i = 0; i < brickList.size(); i++) {
						if(brickList.get(i) instanceof ForeverBrick)
						{
							IfLogicBeginBrick brick = new IfLogicBeginBrick();
							for (int j = 0; j < 5; j++) {
								brick.addBrickToElseBranch(new ChangeXByNBrick(5));
							}
							brickList.add(i, brick);
							break;
						}
					}
				}
			}
		}
		addIfElseQuery();

		achievementSystem.runQueries(projectManager);
		assertTrue(achievementSystem.getAchievement(achievement_key).isUnlocked());
	}

	private void addIfElseQuery()
	{
		achievementSystem.addQuery(key, new Query() {
			@Override
			protected boolean query(ProjectManager projectManager) {
				for (Scene scene: projectManager.getCurrentProject().getSceneList()) {
					for (Sprite sprite: scene.getSpriteList()) {
						for (Script script: sprite.getScriptList()) {
							for (Brick brick: script.getBrickList()) {
								if(brick instanceof IfLogicBeginBrick &&
									(((IfLogicBeginBrick) brick).getNestedBricks().size() >= 5 ||
									((IfLogicBeginBrick) brick).getSecondaryNestedBricks().size() >= 5))
								{
									return true;
								}
							}
						}
					}
				}
				return false;
			}
		});
	}



}
