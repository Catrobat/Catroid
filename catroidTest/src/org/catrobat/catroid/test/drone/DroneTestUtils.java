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
package org.catrobat.catroid.test.drone;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.content.bricks.DroneFlipBrick;
import org.catrobat.catroid.drone.DroneBrickFactory;
import org.catrobat.catroid.drone.DroneBrickFactory.DroneBricks;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.lang.reflect.Field;
import java.util.Locale;

public abstract class DroneTestUtils {

	private static final int DEFAULT_MOVE_TIME_IN_MILLISECONDS = 2000;
	private static final int DEFAULT_MOVE_POWER_IN_PERCENT = 20;
	private static final String TAG = DroneTestUtils.class.getSimpleName();

	public static Project createDroneProjectWithScriptAndAllDroneMoveBricks() {
		Project project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("DoneMoveBricksTest");
		Script script = new StartScript();

		for (DroneBricks brick : DroneBrickFactory.DroneBricks.values()) {
			String brickName = brick.name().toLowerCase(Locale.getDefault());
			if (brickName.contains("move") || brickName.contains("turn")) {
				BrickBaseType moveBrick = DroneBrickFactory.getInstanceOfDroneBrick(brick, sprite,
						DEFAULT_MOVE_TIME_IN_MILLISECONDS, DEFAULT_MOVE_POWER_IN_PERCENT);
				script.addBrick(moveBrick);
				sprite.addScript(script);
			}
		}
		project.addSprite(sprite);
		setProjectAsCurrentProject(project, sprite, script);
		return project;
	}

	public static Project createBasicDroneProject() {
		Project project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("DoneMoveBricksTest");
		Script script = new StartScript();

		script.addBrick(new DroneFlipBrick());

		project.addSprite(sprite);
		sprite.addScript(script);

		setProjectAsCurrentProject(project, sprite, script);
		return project;
	}

	private static void setProjectAsCurrentProject(Project project, Sprite sprite, Script script) {
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

	public static void fakex86ArchWithReflection() {
		fakexOsArchWithReflection("i686");
	}

	public static void fakexOsArchWithReflection(String newOsArch) {
		Field field;
		//http://stackoverflow.com/questions/11185453/android-changing-private-static-final-field-using-java-reflection
		try {
			field = CatroidApplication.class.getField("OS_ARCH");
			field.setAccessible(true);
			field.set(null, newOsArch);
		} catch (Exception e) {
			Log.e(TAG, "Reflection went", e);
		}
	}

	public static void setDroneTermsOfUseAcceptedPermanently(Context context) {
		setDroneTermsOfUseAcceptedValue(context, true);
	}

	private static void setDroneTermsOfUseAcceptedValue(Context context, boolean accepted) {
		getSharedPreferences(context)
				.edit()
				.putBoolean(SettingsActivity.SETTINGS_PARROT_AR_DRONE_CATROBAT_TERMS_OF_SERVICE_ACCEPTED_PERMANENTLY,
						accepted).commit();
	}

	private static SharedPreferences getSharedPreferences(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	public static void disableARDroneBricks(Context context) {
		getSharedPreferences(context).edit().putBoolean(SettingsActivity.SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS, false).commit();
	}
}
