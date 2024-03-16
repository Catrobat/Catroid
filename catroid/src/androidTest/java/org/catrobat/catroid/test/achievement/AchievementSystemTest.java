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
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import androidx.test.core.app.ApplicationProvider;

import static android.content.Context.MODE_PRIVATE;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AchievementSystemTest {

	AchievementSystem achievementSystem = AchievementSystem.getInstance();

	String achievement_key = "achievement_test_key";
	String achievement_title= "achievement_title";
	String key = "test_key";
	String description = "test_description";

	@Before
	public void setUp()
	{
		achievementSystem.reset();

		achievementSystem.setPreferences(ApplicationProvider.getApplicationContext());
	}

	@Test
	public void checkPreferences()
	{
		achievementSystem.reset();
		SharedPreferences preferences =	ApplicationProvider.getApplicationContext().
				getSharedPreferences("Achievement_System_Key",	MODE_PRIVATE);

		achievementSystem.setPreferences(ApplicationProvider.getApplicationContext());
		boolean result = achievementSystem.getPreferences() == preferences;

		assertTrue(result);

	}

	@Test
	public void checkGetInstance(){
		achievementSystem = AchievementSystem.getInstance();
		AchievementSystem system = AchievementSystem.getInstance();

		boolean result = (achievementSystem != null) && achievementSystem == system;

		assertTrue(result);
	}

	@Test
	public void isActive()
	{
		achievementSystem.setActive(true);

		assertTrue(achievementSystem.isActive());
	}
	@Test
	public void isNotActive()
	{
		achievementSystem.setActive(false);

		assertFalse(achievementSystem.isActive());
	}

	@Test
	public void checkConditionList(){
		AchievementCondition condition = new AchievementCondition(key, 1, description);
		achievementSystem.addCondition(condition);
		boolean result = false;
		ArrayList<AchievementCondition> conditionArrayList = achievementSystem.getConditionList();
		if (conditionArrayList.get(0) == condition)
			result = true;

		assertTrue(result);
	}


	@Test
	public void checkAchievementList(){
		Achievement achievement = new Achievement(achievement_title, achievement_key, 1);
		achievementSystem.addAchievement(achievement);
		boolean result = false;
		ArrayList<Achievement> achievementArrayList = achievementSystem.getAchievementList();
		if (achievementArrayList.get(0) == achievement)
			result = true;

		assertTrue(result);
	}


	@Test
	public void achievementAreRemembered()
	{
		Achievement achievement = new Achievement(achievement_title, achievement_key, 1);
		achievementSystem.addAchievement(achievement);

		boolean result = achievement == achievementSystem.getAchievement(achievement_key);

		assertTrue(result);
	}

	@Test
	public void conditionAreRemembered()
	{
		AchievementCondition condition = new AchievementCondition(key, 1, description);
		achievementSystem.addCondition(condition);

		boolean result = condition == achievementSystem.getCondition(key);

		assertTrue(result);
	}

}
