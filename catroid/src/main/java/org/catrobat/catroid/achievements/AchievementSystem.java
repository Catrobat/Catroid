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

import org.catrobat.catroid.R;

import java.util.ArrayList;

public class AchievementSystem {
	private  boolean Active = false;
	private Context context = null;
	private static AchievementSystem Instance = null;
	private ArrayList<Achievement> AchievementList = new ArrayList<>();
	private ArrayList<AchievementCondition> ConditionList = new ArrayList<>();
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

	public void setUpConditionList(Context context)
	{
		this.context = context.getApplicationContext();
		addCondition(new AchievementCondition(context.getString(R.string.achievement_condition_key_test), 4 ,
				"test_string_condition"));
	}
	public void setUpAchievementList()
	{


		Achievement Start = new Achievement(context.getString(R.string.achievement_title_when),
				context.getString(R.string.achievement_key_test),
			R.drawable.test_image);
		Start.addCondition(getCondition(context.getString(R.string.achievement_condition_key_test)));
		Achievement End = new Achievement("End", context.getString(R.string.achievement_key_test),
				R.drawable.test_image);
		AchievementList.add(Start);
		for (int i = 0; i < 40; i++) {
			AchievementList.add(new Achievement("Progress "+ Integer.toString(i), context.getString(R.string.achievement_key_test),
					R.drawable.test_image));
		}
		AchievementList.add(End);
	}
	public ArrayList<Achievement> getAchievementList() {
		return AchievementList;
	}
	public void addAchievement(Achievement achievement) {
		AchievementList.add(achievement);
	}
	public Achievement getAchievement(String title)
	{
		for (Achievement achievement:AchievementList) {
			if(achievement.getTitle() == title){
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
			if (condition.getKey() == key)
			{
				return condition;
			}
		}
		return null;
	}


	public void setPreferences(SharedPreferences preferences) {
		this.Preferences = preferences;
		this.Editor = this.Preferences.edit();
	}

	public SharedPreferences getPreferences() {
		return Preferences;
	}

	public SharedPreferences.Editor getEditor() {
		return Editor;
	}
}
