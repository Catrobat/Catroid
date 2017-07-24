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
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.uiespresso.annotations.Flaky;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.File;

public class BaseActivityInstrumentationRule<T extends Activity> extends ActivityTestRule<T> {
	private SystemAnimations systemAnimations;
	private static final String TAG = BaseActivityInstrumentationRule.class.getSimpleName();

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

	//http://stackoverflow.com/questions/8295100/how-to-re-run-failed-junit-tests-immediately
	//http://stackoverflow.com/questions/1492856/easy-way-of-running-the-same-junit-test-over-and-over
	public Statement apply(Statement base, Description description) {
		return statement(base, description);
	}

	private Statement statement(final Statement base, final Description description) {
		Flaky flakyTest = description.getAnnotation(Flaky.class);
		int retryCount = 1;
		if (flakyTest != null) {
			retryCount = flakyTest.value();
		}
		final int flakyTestRetryCount = retryCount;

		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				Throwable caughtThrowable = null;

				for (int i = 0; i < flakyTestRetryCount; i++) {
					try {
						base.evaluate();
						return;
					} catch (Throwable t) {
						caughtThrowable = t;
						Log.e(TAG, description.getDisplayName() + ": run " + (i + 1) + " failed");
						if (getActivity() != null) {
							getActivity().finish();
						}
					}
				}
				Log.e(TAG, description.getDisplayName() + ": giving up after " + flakyTestRetryCount + " failures");
				throw caughtThrowable;
			}
		};
	}
}
