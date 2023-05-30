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

import java.util.ArrayList;

public class Achievement implements Observer{
	private String Title;
	private int Drawable;
	private boolean Unlocked;
	private ArrayList<AchievementCondition> ConditionList = new ArrayList<>();
	private String Description = "";

	public Achievement(String title, int drawable) {
		Title = title;
		Drawable = drawable;
		Unlocked = false;
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
		Unlocked = true;
	}
	public void addCondition(AchievementCondition condition)
	{
		ConditionList.add(condition);
		condition.addObserver(this);
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public int getDrawable() {
		return Drawable;
	}

	public void setDrawable(int drawable) {
		Drawable = drawable;
	}

	public String getDescription() {
		Description = "";
		for (AchievementCondition condition:ConditionList) {
			Description += condition.getCondition();
		}
		return Description;
	}

	public boolean isUnlocked() {
		return Unlocked;
	}
}
