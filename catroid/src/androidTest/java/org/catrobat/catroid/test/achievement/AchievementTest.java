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

import org.catrobat.catroid.achievements.Achievement;
import org.catrobat.catroid.achievements.AchievementCondition;
import org.catrobat.catroid.achievements.AchievementSystem;
import org.catrobat.catroid.achievements.Observer;
import org.catrobat.catroid.achievements.Subject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import androidx.test.core.app.ApplicationProvider;

import static org.junit.Assert.assertTrue;

public class AchievementTest implements Subject {

	private ArrayList<Observer> observerArrayList;
	AchievementSystem achievementSystem = AchievementSystem.getInstance();
	int drawable = 1;
	String achievement_key = "achievement_test_key";
	String achievement_title= "achievement_title";
	String condition_key_1 = "condition_test_key_1";
	String condition_key_2 = "condition_test_key_2";
	String description_1 = "test_description_1";
	String description_2 = "test_description_2";

	@Before
	public void setUp() {

		initAchievementSystem();
	}
	public void initAchievementSystem()
	{
		observerArrayList = new ArrayList<>();

		achievementSystem.setPreferences(ApplicationProvider.getApplicationContext());
		achievementSystem.setActive(true);
		SharedPreferences.Editor editor = achievementSystem.getEditor();
		editor.putBoolean(achievement_key, false);
		editor.putInt(condition_key_1+"_Int", 0);
		editor.putBoolean(condition_key_1+"_Boolean", false);
		editor.putInt(condition_key_2+"_Int", 0);
		editor.putBoolean(condition_key_2+"_Boolean", false);
		editor.commit();
	}

	@Test
	public void getKey()
	{
		Achievement achievement = new Achievement(achievement_title, achievement_key, drawable);
		boolean result = achievement_key.equals(achievement.getKey());

		assertTrue(result);
	}
	@Test
	public void achievementWithoutCondition()
	{
		Achievement achievement = new Achievement(achievement_title, achievement_key, drawable);
		boolean result = (!achievement.isUnlocked()) && achievement.getTitle().equals(achievement_title)
				&& achievement.getDrawable() == drawable && achievement.getDescription().equals("");

		assertTrue(result);
	}
	@Test
	public void achievementWithCondition()
	{
		Achievement achievement = new Achievement(achievement_title, achievement_key, drawable);
		AchievementCondition condition1 = new AchievementCondition(condition_key_1, 1,
				description_1);
		AchievementCondition condition2 = new AchievementCondition(condition_key_2, 5,
				description_2);
		achievement.addCondition(condition1);
		achievement.addCondition(condition2);
		

		String test_description = condition1.getCondition()+condition2.getCondition();

		boolean result = (!achievement.isUnlocked()) && achievement.getTitle().equals(achievement_title)
				&& achievement.getDrawable() == drawable && achievement.getDescription().equals(test_description);

		assertTrue(result);
	}

	@Test
	public void remembersUnlock()
	{
		Achievement achievement = new Achievement(achievement_title, achievement_key, drawable);
		AchievementCondition condition1 = new AchievementCondition(condition_key_1, 1,
				description_1);

		achievement.addCondition(condition1);
		addObserver(condition1);
		notifyObserver();

		achievement = new Achievement(achievement_title, achievement_key, drawable);

		boolean result = achievement.isUnlocked();

		assertTrue(result);
	}

	@Test
	public void descriptionIsAccurate()
	{
		Achievement achievement = new Achievement(achievement_title, achievement_key, drawable);
		AchievementCondition condition1 = new AchievementCondition(condition_key_1, 1,
				description_1);
		AchievementCondition condition2 = new AchievementCondition(condition_key_2, 5,
				description_2);
		achievement.addCondition(condition1);
		achievement.addCondition(condition2);
		this.addObserver(condition1);
		this.addObserver(condition2);

		for (int i = 0; i < 3; i++) {
			notifyObserver();
		}

		String test_description = "1/1 " + description_1 + "\n3/5 "+description_2 +'\n';

		boolean result = achievement.getDescription().equals(test_description);

		assertTrue(result);
	}

	@Test
	public void remembersDescription(){
		Achievement achievement = new Achievement(achievement_title, achievement_key, drawable);
		AchievementCondition condition1 = new AchievementCondition(condition_key_1, 1,
				description_1);
		AchievementCondition condition2 = new AchievementCondition(condition_key_2, 5,
				description_2);
		achievement.addCondition(condition1);
		achievement.addCondition(condition2);
		this.addObserver(condition1);
		this.addObserver(condition2);

		for (int i = 0; i < 3; i++) {
			notifyObserver();
		}
		achievement = new Achievement(achievement_title, achievement_key, drawable);
		achievement.addCondition(condition1);
		achievement.addCondition(condition2);

		String test_description = "1/1 " + description_1 + "\n3/5 "+description_2 +'\n';

		boolean result = achievement.getDescription().equals(test_description);

		assertTrue(result);
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
		for (Observer observer: observerArrayList) {
			observer.update(this);
		}
	}
}
