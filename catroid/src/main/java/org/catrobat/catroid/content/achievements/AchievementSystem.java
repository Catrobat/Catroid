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

package org.catrobat.catroid.content.achievements;

import android.content.SharedPreferences;

import android.preference.PreferenceManager;

import org.catrobat.catroid.R;

import java.util.ArrayList;

public class AchievementSystem {
	private static AchievementSystem instance = null;
	private ArrayList<Achievement> AchievementList = new ArrayList<>();
	private ArrayList<AchievementCondition> ConditionList = new ArrayList<>();
	private SharedPreferences preferences = null;

	private SharedPreferences.Editor editor = null;

	private AchievementSystem(){

		Achievement Start = new Achievement("Start" , R.drawable.ic_main_menu_achievements_button);
		Achievement End = new Achievement("End",  R.drawable.ic_main_menu_achievements_button);
		AchievementList.add(Start);
		for (int i = 0; i < 40; i++) {
			AchievementList.add(new Achievement("Progress "+ Integer.toString(i), R.drawable.ic_main_menu_achievements_button));
		}
		AchievementList.add(End);
	}

	public static synchronized AchievementSystem getInstance()
	{
		if(instance == null)
			instance = new AchievementSystem();

		return instance;
	}

	public ArrayList<Achievement> getAchievementList() {
		return AchievementList;
	}



	public ArrayList<AchievementCondition> getConditionList() {
		return ConditionList;
	}

	public void setPreferences(SharedPreferences preferences) {
		this.preferences = preferences;
		this.editor = this.preferences.edit();
	}

	public SharedPreferences getPreferences() {
		return preferences;
	}

	public SharedPreferences.Editor getEditor() {
		return editor;
	}
}
