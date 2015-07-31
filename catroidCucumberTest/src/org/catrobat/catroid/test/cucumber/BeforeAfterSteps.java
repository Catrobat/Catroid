/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.test.cucumber;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.robotium.solo.Solo;

import cucumber.api.android.CucumberInstrumentation;
import cucumber.api.java.After;
import cucumber.api.java.Before;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;

// CHECKSTYLE DISABLE MethodNameCheck FOR 1000 LINES
public class BeforeAfterSteps extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;

	public BeforeAfterSteps() {
		super(MainMenuActivity.class);
	}

	@Before
	public void before() {
		Log.d(CucumberInstrumentation.TAG, "before step");
		Context context = getInstrumentation().getTargetContext();
		solo = new Solo(getInstrumentation(), getActivity());
		String defaultBackgroundName = context.getString(R.string.background);
		String defaultProjectName = context.getString(R.string.default_project_name);
		String defaultSpriteName = context.getString(R.string.default_project_sprites_mole_name);
		Cucumber.put(Cucumber.KEY_DEFAULT_BACKGROUND_NAME, defaultBackgroundName);
		Cucumber.put(Cucumber.KEY_DEFAULT_PROJECT_NAME, defaultProjectName);
		Cucumber.put(Cucumber.KEY_DEFAULT_SPRITE_NAME, defaultSpriteName);
		Cucumber.put(Cucumber.KEY_SOLO, solo);
		Cucumber.put(Cucumber.KEY_START_TIME_NANO, 0);
		Cucumber.put(Cucumber.KEY_STOP_TIME_NANO, 0);
	}

	@After
	public void after() {
		Log.d(CucumberInstrumentation.TAG, "after step");
		solo.finishOpenedActivities();
		ProjectManager.getInstance().deleteCurrentProject(null);
	}
}
