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

package org.catrobat.catroid.ui;

import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.achievements.AchievementCondition;
import org.catrobat.catroid.achievements.AchievementSystem;
import org.catrobat.catroid.achievements.Observer;
import org.catrobat.catroid.achievements.Subject;

import java.util.ArrayList;

import androidx.appcompat.widget.Toolbar;

public class TestActivity extends BaseActivity implements Subject {
	private ArrayList<Observer> observerArrayList = new ArrayList<>();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.test_layout);
		AchievementCondition condition =
				AchievementSystem.getInstance().getCondition(getString(R.string.achievement_condition_key_test));
		if (!condition.isFinished())
		{
			addObserver(condition);
		}

		Button test_button = findViewById(R.id.Test_button);

		test_button.setOnClickListener(view -> {
			AchievementSystem.getInstance().setActive(true);
			notifyObserver();
			AchievementSystem.getInstance().setActive(false);
		});


		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setTitle(R.string.main_menu_test);
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

