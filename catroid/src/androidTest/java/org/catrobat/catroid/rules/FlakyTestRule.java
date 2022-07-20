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
package org.catrobat.catroid.rules;

import android.util.Log;

import org.catrobat.catroid.runner.Flaky;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class FlakyTestRule implements TestRule {

	private static final String TAG = FlakyTestRule.class.getSimpleName();

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
					}
				}
				Log.e(TAG, description.getDisplayName() + ": giving up after " + flakyTestRetryCount + " failures");
				throw caughtThrowable;
			}
		};
	}
}
