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

import org.catrobat.catroid.achievements.AchievementCondition;
import org.catrobat.catroid.achievements.AchievementSystem;
import org.catrobat.catroid.achievements.Observer;
import org.catrobat.catroid.achievements.Subject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import androidx.test.core.app.ApplicationProvider;

import static org.junit.Assert.assertTrue;

public class AchievementConditionTest implements Subject {

	private final ArrayList<Observer> observerArrayList = new ArrayList<>();
	AchievementSystem achievementSystem = AchievementSystem.getInstance();
	String key = "test_key";
	String description = "test_description";

	@Before
	public void setUp() {
		initAchievementSystem();
	}
	public void initAchievementSystem()
	{

		achievementSystem.setPreferences(ApplicationProvider.getApplicationContext());
		achievementSystem.setActive(true);
		SharedPreferences.Editor editor = achievementSystem.getEditor();
		editor.putInt(key+"_Int", 0);
		editor.putBoolean(key+"_Boolean", false);
		editor.commit();
	}
	@Test
	public void outputWithoutNotification()
	{
		int denominator = 1;
		AchievementCondition condition = new AchievementCondition(key, denominator,
				description);
		addObserver(condition);
		boolean result = false;
		String test_description = "0/"+ denominator +" "+description + '\n';
		if(!condition.isFinished() && condition.getCondition().equals(test_description))
			result = true;

		assertTrue(result);
	}
	@Test
	public void notifyObserverIncrementByOne()
	{

		AchievementCondition condition = new AchievementCondition(key, 1, description);
		addObserver(condition);
		notifyObserver();
		boolean result = false;
		String test_description = "1/1 "+description+'\n';
		if (condition.getCondition().equals(test_description) && condition.isFinished())
			result = true;

		assertTrue(result);
	}

	@Test
	public void DoesNotIncrementOverDenominator(){
		AchievementCondition condition = new AchievementCondition(key, 4, description);
		addObserver(condition);
		for (int i = 0; i < 5; i++) {
			notifyObserver();
		}

		boolean result = false;
		String test_description = "4/4 "+description+'\n';
		if (condition.getCondition().equals(test_description) && condition.isFinished())
			result = true;

		assertTrue(result);
	}

	@Test
	public void doesRememberNotification()
	{
		AchievementCondition condition = new AchievementCondition(key, 4, description);
		addObserver(condition);
		for (int i = 0; i < 2; i++) {
			notifyObserver();
		}
		String test_description = condition.getCondition();

		condition = new AchievementCondition(key, 4, description);
		boolean result = condition.getCondition().equals(test_description);
		assertTrue(result);
	}
	@Test
	public void doesRememberItIsFinished()
	{
		AchievementCondition condition = new AchievementCondition(key, 4, description);
		addObserver(condition);
		for (int i = 0; i < 4; i++) {
			notifyObserver();
		}
		boolean Finished = condition.isFinished();

		condition = new AchievementCondition(key, 4, description);
		boolean result = condition.isFinished() == Finished;
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
