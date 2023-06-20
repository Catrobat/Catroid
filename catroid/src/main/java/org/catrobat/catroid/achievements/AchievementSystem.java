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

package org.catrobat.catroid.achievements;

import android.content.Context;
import android.content.SharedPreferences;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick;

import java.util.ArrayList;


import java.util.Objects;

public class AchievementSystem {
	private boolean Active = false;

	private static AchievementSystem Instance = null;
	private ArrayList<Achievement> AchievementList = new ArrayList<>();
	private ArrayList<AchievementCondition> ConditionList = new ArrayList<>();
	private ArrayList<Query> QueryList = new ArrayList<>();
	private SharedPreferences Preferences = null;

	private SharedPreferences.Editor Editor = null;

	private AchievementSystem(){
	}

	public static synchronized AchievementSystem getInstance()
	{
		if(Instance == null)
			Instance = new AchievementSystem();

		return Instance;
	}

	public boolean isActive() {
		return Active;
	}

	public void setActive(boolean active) {
		Active = active;
	}

	public void setUpConditionList(Context contextIn)
	{
		Context context = contextIn.getApplicationContext();

		addCondition(new AchievementCondition(context.getString(R.string.achievement_condition_key_run_first_project), 1,
				context.getString(R.string.achievement_condition_description_run_first_project)));
		addCondition(new AchievementCondition(context.getString(R.string.achievement_condition_key_forever_loop), 1,
				context.getString(R.string.achievement_condition_description_forever_loop)));
		addCondition(new AchievementCondition(context.getString(R.string.achievement_condition_key_if_then),1,
				context.getString(R.string.achievement_condition_description_if_then)));
		addCondition(new AchievementCondition(context.getString(R.string.achievement_condition_key_if_else), 1,
				context.getString(R.string.achievement_condition_description_if_else)));
	}
	public void setUpAchievementList(Context contextIn)
	{
		Context context = contextIn.getApplicationContext();
		Achievement runFirstProject =
				new Achievement(context.getString(R.string.achievement_title_run_first_project),
						context.getString(R.string.achievement_key_run_first_project),
						R.drawable.achievement_run_first_project_image);
		runFirstProject.addCondition(getCondition(context.getString(R.string.achievement_condition_key_run_first_project)));
		addAchievement(runFirstProject);

		Achievement foreverLoop =
				new Achievement(context.getString(R.string.achievement_title_forever_loop),
						context.getString(R.string.achievement_key_forever_loop),
						R.drawable.achievement_forever_loop_image);
		foreverLoop.addCondition(getCondition(context.getString(R.string.achievement_condition_key_forever_loop)));
		addAchievement(foreverLoop);

		Achievement ifThen =
				new Achievement(context.getString(R.string.achievement_title_if_then),
						context.getString(R.string.achievement_key_if_then),
						R.drawable.achievement_if_then_image);
		ifThen.addCondition(getCondition(context.getString(R.string.achievement_condition_key_if_then)));
		addAchievement(ifThen);

		Achievement ifElse =
				new Achievement(context.getString(R.string.achievement_title_if_else),
						context.getString(R.string.achievement_key_if_else),
						R.drawable.achievement_if_else_image);
		ifElse.addCondition(getCondition(context.getString(R.string.achievement_condition_key_if_else)));
		addAchievement(ifElse);


	}
	public void setUpQueryList(Context contextIn){
		Context context = contextIn.getApplicationContext();
		addQuery(context.getString(R.string.achievement_condition_key_forever_loop),new Query() {
			@Override
			protected boolean query(ProjectManager projectManager) {
				for (Scene scene: projectManager.getCurrentProject().getSceneList()) {
					for (Sprite sprite: scene.getSpriteList()) {
						for (Script script: sprite.getScriptList()) {
							for (Brick brick: script.getBrickList()) {
								if(brick instanceof ForeverBrick && ((ForeverBrick) brick).getNestedBricks().size() >= 5)
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
		addQuery(context.getString(R.string.achievement_condition_key_if_then), new Query() {
			@Override
			protected boolean query(ProjectManager projectManager) {
				for (Scene scene: projectManager.getCurrentProject().getSceneList()) {
					for (Sprite sprite: scene.getSpriteList()) {
						for (Script script: sprite.getScriptList()) {
							for (Brick brick: script.getBrickList()) {
								if(brick instanceof IfThenLogicBeginBrick && ((IfThenLogicBeginBrick) brick).getNestedBricks().size() >= 5)
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

		addQuery(context.getString(R.string.achievement_condition_key_if_else), new Query() {
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

	public void addQuery(String condition_key, Query query)
	{
		AchievementCondition condition = getCondition(condition_key);
		if(condition.isFinished())
			return;
		QueryList.add(query);
		query.addObserver(condition);
	}
	public void runQueries(ProjectManager projectManager)
	{
		for (Query query: QueryList)
		{
			query.run(projectManager);
		}
	}

	public ArrayList<Achievement> getAchievementList() {
		return AchievementList;
	}
	public void addAchievement(Achievement achievement) {
		AchievementList.add(achievement);
	}
	public Achievement getAchievement(String key)
	{
		for (Achievement achievement:AchievementList) {
			if(Objects.equals(achievement.getKey(), key)){
				return achievement;
			}
		}
		return null;
	}

	public ArrayList<AchievementCondition> getConditionList() {
		return ConditionList;
	}

	public void addCondition(AchievementCondition condition) {
		ConditionList.add(condition);
	}
	public AchievementCondition getCondition(String key){
		for (AchievementCondition condition:ConditionList) {
			if (Objects.equals(condition.getKey(), key))
			{
				return condition;
			}
		}
		return null;
	}


	public void setPreferences(Context context_in)
	{
		Context context_local = context_in.getApplicationContext();
		this.Preferences = context_local.getSharedPreferences("Achievement_System_Key",
				Context.MODE_PRIVATE);
		this.Editor = this.Preferences.edit();
	}

	public SharedPreferences getPreferences() {
		return Preferences;
	}

	public SharedPreferences.Editor getEditor() {
		return Editor;
	}

	public void reset() {
		Active = false;

		AchievementList = new ArrayList<>();
		ConditionList = new ArrayList<>();
		QueryList = new ArrayList<>();
		Preferences = null;
		Editor = null;
	}
}
