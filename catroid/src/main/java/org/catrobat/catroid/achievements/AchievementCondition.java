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


import android.content.SharedPreferences;

import java.util.ArrayList;


public class AchievementCondition implements Subject, Observer {
	private final ArrayList<Observer> observerArrayList = new ArrayList<>();
	private boolean Finished;
	private int ConditionMetNumerator;
	private final int ConditionMetDenominator;
	private final String Description;
	private String Condition = "";
	private final String Key;

	public AchievementCondition(String key,
			int conditionMetDenominator, String description)
	{

		this.Key = key;
		SharedPreferences preferences = AchievementSystem.getInstance().getPreferences();
		this.ConditionMetNumerator = preferences.getInt(this.Key+"_Int", 0);
		this.Finished = preferences.getBoolean(this.Key+"_Boolean", false);
		this.ConditionMetDenominator = conditionMetDenominator;
		this.Description = description;
		updateCondition();
	}

	@Override
	public void addObserver(Observer observer) {
		observerArrayList.add(observer);
	}

	@Override
	public void removeObserver(Observer observer) {
		observerArrayList.remove(observer);
	}

	@Override
	public void notifyObserver() {
		for (Observer observer: observerArrayList		) {
			observer.update(this);
		}
	}

	@Override
	public void update(Subject subject) {
		AchievementSystem achievementSystem = AchievementSystem.getInstance();
		if (Finished)
		{

			return;
		}
		if(!achievementSystem.isActive())
			return;
		ConditionMetNumerator+=1;
		SharedPreferences.Editor editor = achievementSystem.getEditor();
		editor.putInt(this.Key+"_Int", this.ConditionMetNumerator);
		updateCondition();
		if(ConditionMetNumerator == ConditionMetDenominator)
		{
			this.Finished = true;
			editor.putBoolean(this.Key+"_Boolean", true);

			notifyObserver();
		}
		editor.apply();
	}

	private void updateCondition()
	{
		Condition =
				ConditionMetNumerator + "/" + ConditionMetDenominator;
		Condition += " " + Description + "\n";
	}
	public String getCondition() {
		return Condition;
	}

	public boolean isFinished() {
		return Finished;
	}

	public String getKey() {
		return Key;
	}
}
