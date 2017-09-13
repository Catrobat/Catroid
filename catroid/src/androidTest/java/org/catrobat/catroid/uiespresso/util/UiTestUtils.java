/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
package org.catrobat.catroid.uiespresso.util;

import android.app.Activity;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;

import java.util.Collection;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.runner.lifecycle.Stage.RESUMED;

public final class UiTestUtils {
	// Suppress default constructor for noninstantiability
	private UiTestUtils() {
		throw new AssertionError();
	}

	public static Resources getResources() {
		return InstrumentationRegistry.getTargetContext().getResources();
	}

	public static String getResourcesString(int stringId) {
		return InstrumentationRegistry.getTargetContext().getResources().getString(stringId);
	}

	@Nullable
	public static Activity getCurrentActivity() {
		final Activity[] currentActivity = {null};
		getInstrumentation().runOnMainSync(new Runnable() {
			public void run() {
				Collection<Activity> resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED);
				if (resumedActivities.iterator().hasNext()) {
					currentActivity[0] = resumedActivities.iterator().next();
				}
			}
		});
		return currentActivity[0];
	}

	public static Project createEmptyProject(String projectName) {
		Project project = new Project(null, projectName);
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript();
		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		return project;
	}

	public static boolean comparePixelRgbaArrays(byte[] firstArray, byte[] secondArray) {
		if (firstArray == null || secondArray == null || firstArray.length != 4 || secondArray.length != 4) {
			return false;
		}
		for (int i = 0; i < 4; i++) {
			if (Math.abs((firstArray[i] & 0xFF) - (secondArray[i] & 0xFF)) > 10) {
				return false;
			}
		}
		return true;
	}
}
