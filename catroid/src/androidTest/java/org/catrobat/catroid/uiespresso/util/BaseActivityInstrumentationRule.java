/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.test.utils.Reflection;

import java.io.File;

public class BaseActivityInstrumentationRule<T extends Activity> extends ActivityTestRule<T> {
	private SystemAnimations systemAnimations;

	public BaseActivityInstrumentationRule(Class<T> activityClass, boolean initialTouchMode, boolean launchActivity) {
		super(activityClass, initialTouchMode, launchActivity);
		setUpTestProjectFolder();
	}

	public BaseActivityInstrumentationRule(Class<T> activityClass, boolean initialTouchMode) {
		super(activityClass, initialTouchMode);
		setUpTestProjectFolder();
	}

	public BaseActivityInstrumentationRule(Class<T> activityClass) {
		super(activityClass);
		setUpTestProjectFolder();
	}

	@Override
	protected void afterActivityLaunched() {
		systemAnimations = new SystemAnimations(InstrumentationRegistry.getTargetContext());
		systemAnimations.disableAll();
		super.afterActivityLaunched();
	}

	@Override
	protected void afterActivityFinished() {
		systemAnimations.enableAll();
		super.afterActivityFinished();
	}

	void deleteRecursive(File fileOrDirectory) {
		if (fileOrDirectory.isDirectory()) {
			for (File child : fileOrDirectory.listFiles()) {
				deleteRecursive(child);
			}
		}
		fileOrDirectory.delete();
	}

	void setUpTestProjectFolder() {
		Reflection.setPrivateField(StageListener.class, "checkIfAutomaticScreenshotShouldBeTaken", false);
		Reflection.setPrivateField(Constants.class, "DEFAULT_ROOT", Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Pocket Code uiTest");
		File uiTestFolder = new File(Constants.DEFAULT_ROOT);
		if (uiTestFolder.exists()) {
			deleteRecursive(uiTestFolder);
		}
	}
}
