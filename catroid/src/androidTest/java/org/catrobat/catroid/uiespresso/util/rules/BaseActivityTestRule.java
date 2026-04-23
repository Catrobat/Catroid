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
package org.catrobat.catroid.uiespresso.util.rules;

import android.app.Activity;
import android.util.Log;

import org.catrobat.catroid.common.FlavoredConstants;

import java.io.File;
import java.io.IOException;

import androidx.test.rule.ActivityTestRule;

import static org.catrobat.catroid.io.StorageOperations.deleteDir;

public class BaseActivityTestRule<T extends Activity> extends ActivityTestRule<T> {

	private static final String TAG = BaseActivityTestRule.class.getSimpleName();

	public BaseActivityTestRule(Class<T> activityClass, boolean initialTouchMode, boolean launchActivity) {
		super(activityClass, initialTouchMode, launchActivity);
		deleteAllProjects();
	}

	public BaseActivityTestRule(Class<T> activityClass, boolean initialTouchMode) {
		super(activityClass, initialTouchMode);
		deleteAllProjects();
	}

	public BaseActivityTestRule(Class<T> activityClass) {
		super(activityClass);
		deleteAllProjects();
	}

	public void deleteAllProjects() {
		if (FlavoredConstants.DEFAULT_ROOT_DIRECTORY.exists() && FlavoredConstants.DEFAULT_ROOT_DIRECTORY.isDirectory()) {
			for (File file : FlavoredConstants.DEFAULT_ROOT_DIRECTORY.listFiles()) {
				if (file.isDirectory()) {
					try {
						deleteDir(file);
					} catch (IOException ioException) {
						Log.d(TAG, "unable to delete project " + file.getName());
					}
				}
			}
		}
	}
}
