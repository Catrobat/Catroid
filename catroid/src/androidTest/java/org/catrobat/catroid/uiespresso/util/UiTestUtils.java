/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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
import android.util.Log;
import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.hamcrest.Matcher;

import java.util.Collection;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.ViewInteraction;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;

import static org.catrobat.catroid.uiespresso.util.matchers.SuperToastMatchers.isToast;
import static org.hamcrest.CoreMatchers.instanceOf;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.runner.lifecycle.Stage.RESUMED;

public final class UiTestUtils {

	private UiTestUtils() {
		throw new AssertionError();
	}

	public static Resources getResources() {
		return ApplicationProvider.getApplicationContext().getResources();
	}

	public static String getResourcesString(int stringId) {
		return ApplicationProvider.getApplicationContext().getResources().getString(stringId);
	}

	public static String getResourcesString(int stringId, Object... formatArgs) {
		return ApplicationProvider.getApplicationContext().getResources().getString(stringId, formatArgs);
	}

	public static String getQuantitiyString(int stringId, int quantity) {
		return ApplicationProvider.getApplicationContext().getResources().getQuantityString(stringId, quantity);
	}

	public static void assertCurrentActivityIsInstanceOf(Class activityClass) {
		final Activity[] currentActivity = {null};
		getInstrumentation().runOnMainSync(new Runnable() {
			public void run() {
				Collection<Activity> resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED);
				if (resumedActivities.iterator().hasNext()) {
					currentActivity[0] = resumedActivities.iterator().next();
				}
			}
		});
		assertThat(currentActivity[0], instanceOf(activityClass));
	}

	public static Project createEmptyProject(String projectName) {
		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript();
		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		return project;
	}

	public static ViewInteraction onToast(Matcher<View> viewMatcher) {
		return onView(viewMatcher).inRoot(isToast());
	}

	public static void openActionBar() {
		try {
			Thread.sleep(100);
			openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
			Thread.sleep(100);
		} catch (InterruptedException e) {
			Log.e(UiTestUtils.class.getName(), e.getMessage());
		}
	}
}
