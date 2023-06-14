/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid;

import android.content.Context;
import android.content.SharedPreferences;

import org.catrobat.catroid.achievements.AchievementCondition;
import org.catrobat.catroid.achievements.AchievementSystem;
import org.catrobat.catroid.achievements.Observer;
import org.catrobat.catroid.achievements.Subject;

import org.junit.Before;

import org.junit.Test;


import java.util.ArrayList;

import androidx.test.core.app.ApplicationProvider;


import static android.content.Context.MODE_PRIVATE;



import static org.junit.Assert.assertEquals;


public class AchievementConditionTest implements Subject {

	private ArrayList<Observer> observerArrayList = new ArrayList<>();
	AchievementSystem achievementSystem = AchievementSystem.getInstance();
	String key = "test_key";
	String description = "test_description";

	@Before
	public void setUp() {
		initAchievementSystem();
	}
	public void initAchievementSystem()
	{
		Context context = ApplicationProvider.getApplicationContext();
		achievementSystem.setPreferences(context.getSharedPreferences("test_string_key", MODE_PRIVATE));
		achievementSystem.setActive(true);
		SharedPreferences.Editor editor = achievementSystem.getEditor();
		editor.putInt(key+"_Int", 0);
		editor.putBoolean(key+"_Boolean", false);
		editor.commit();
	}
	@Test
	public void notifyObserverIncreamentsByOne()
	{

		AchievementCondition condition = new AchievementCondition(key, 1, description);
		addObserver(condition);
		notifyObserver();
		boolean result = false;
		String test_description = "1/1 "+description+'\n';
		if (condition.getCondition().equals(test_description) && condition.isFinished())
			result = true;

		assertEquals(true, result);
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

		assertEquals(true, result);
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
		boolean result = false;
		if(condition.getCondition().equals(test_description))
		{
			result = true;
		}
		assertEquals(true, result);
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
		boolean result = false;
		if(condition.isFinished() == Finished)
		{
			result = true;
		}
		assertEquals(true, result);
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
