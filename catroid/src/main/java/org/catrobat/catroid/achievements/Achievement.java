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


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.ListView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.BaseActivity;
import org.catrobat.catroid.ui.adapter.AchievementListAdapter;

import java.util.ArrayList;

import androidx.appcompat.widget.Toolbar;

public class Achievement implements Observer {
	private String Title;
	private String Key;
	private int Drawable;
	private boolean Unlocked;
	private final ArrayList<AchievementCondition> ConditionList = new ArrayList<>();
	private String Description = "";

	public Achievement(String title,String key, int drawable) {
		Title = title;
		Key = key;
		Drawable = drawable;
		Unlocked = AchievementSystem.getInstance().getPreferences().getBoolean(Key, false);

	}

	/*public Achievement(String title, int drawable) {
		Title = title;

		Drawable = drawable;
		Unlocked = false;
	}*/

	public String getKey() {
		return Key;
	}

	@Override
	public void update(Subject subject) {
		if (Unlocked)
			return;

		for (AchievementCondition condition:ConditionList) {
			if(!condition.isFinished())
			{
				return;
			}
		}
		updateDescription();
		setUnlocked(true);
	}
	public void addCondition(AchievementCondition condition)
	{
		ConditionList.add(condition);
		condition.addObserver(this);
		updateDescription();
	}

	public String getTitle() {
		return Title;
	}


	public int getDrawable() {
		return Drawable;
	}

	public void setDrawable(int drawable) {
		Drawable = drawable;
	}

	private void updateDescription()
	{
		StringBuilder builder = new StringBuilder();
		for (AchievementCondition condition:ConditionList) {
			builder.append(condition.getCondition());
		}
		Description = builder.toString();
	}
	public String getDescription() {
		if(!Unlocked)
			updateDescription();
		return Description;
	}

	public boolean isUnlocked() {
		return Unlocked;
	}

	private void setUnlocked(boolean unlocked) {
		Unlocked = unlocked;
		updateDescription();
		SharedPreferences.Editor editor = AchievementSystem.getInstance().getEditor();
		editor.putBoolean(Key, Unlocked);
		editor.apply();
	}


}
