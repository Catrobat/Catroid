/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
import android.content.Intent;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.uiespresso.annotations.Flaky;
import org.catrobat.catroid.uiespresso.util.SystemAnimations;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.File;
import java.io.IOException;

import static org.catrobat.catroid.common.Constants.DEFAULT_ROOT_DIRECTORY;

public class BaseActivityInstrumentationRule<T extends Activity> extends ActivityTestRule<T> {

	private SystemAnimations systemAnimations;
	private static final String TAG = BaseActivityInstrumentationRule.class.getSimpleName();
	private Intent launchIntent = null;

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

	public BaseActivityInstrumentationRule(Class<T> activityClass, String extraFragementPosition, int fragment) {
		super(activityClass, true, false);
		launchIntent = new Intent();
		launchIntent.putExtra(extraFragementPosition, fragment);
	}

	public void launchActivity() {
		super.launchActivity(launchIntent);
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

	void setUpTestProjectFolder() {
		Reflection.setPrivateField(StageListener.class, "checkIfAutomaticScreenshotShouldBeTaken", false);
		Reflection.setPrivateField(Constants.class, "DEFAULT_ROOT_DIRECTORY",
				new File(Environment.getExternalStorageDirectory(), "Pocket Code UiTest"));

		if (DEFAULT_ROOT_DIRECTORY.exists()) {
			try {
				StorageOperations.deleteDir(DEFAULT_ROOT_DIRECTORY);
			} catch (IOException e) {
				Log.e(TAG, "Error deleting root directory:", e);
			}
		}

		try {
			StorageOperations.createDir(DEFAULT_ROOT_DIRECTORY);
		} catch (IOException e) {
			throw new RuntimeException("What a terrible failure! Cannot create root directory: "
					+ DEFAULT_ROOT_DIRECTORY.getAbsolutePath());
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
						Log.d(TAG, description.getDisplayName() + ": succeeded");
						return;
					} catch (Throwable t) {
						caughtThrowable = t;
						Log.e(TAG, description.getDisplayName() + ": run " + (i + 1) + " failed", t);
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
