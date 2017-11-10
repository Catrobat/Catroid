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

import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.uiespresso.util.actions.CustomActions;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;

public final class UserVariableTestUtils {

	private static final double EPSILON = 0.001;

	private UserVariableTestUtils() {
		throw new AssertionError();
	}

	public static boolean userVariableEqualsWithinTimeout(UserVariable userVariable, double expectedValue,
			int timeoutMillis) {
		int intervalMillis = 10;
		for (; timeoutMillis > 0; timeoutMillis -= intervalMillis) {
			if (areEqualWithinEpsilon(expectedValue, (Double) userVariable.getValue())) {
				return true;
			}
			onView(isRoot())
					.perform(CustomActions.wait(intervalMillis));
		}
		return false;
	}

	public static boolean userVariableDoesDifferWithinTimeout(UserVariable userVariable, double expectedValue,
			int timeoutMillis) {
		int intervalMillis = 10;
		for (; timeoutMillis > 0; timeoutMillis -= intervalMillis) {
			if (areEqualWithinEpsilon(expectedValue, (Double) userVariable.getValue())) {
				return false;
			}
			onView(isRoot())
					.perform(CustomActions.wait(intervalMillis));
		}
		return true;
	}

	public static boolean userVariableEqualsWithinTimeout(UserVariable userVariable, String expectedValue,
			int timeoutMillis) {
		int intervalMillis = 10;
		for (; timeoutMillis > 0; timeoutMillis -= intervalMillis) {
			if (expectedValue.equals(userVariable.getValue().toString())) {
				return true;
			}
			onView(isRoot())
					.perform(CustomActions.wait(intervalMillis));
		}
		return false;
	}

	public static boolean userVariableContainsWithinTimeout(UserVariable userVariable, String expectedValue,
			int timeoutMillis) {
		int intervalMillis = 10;
		for (; timeoutMillis > 0; timeoutMillis -= intervalMillis) {
			if (userVariable.getValue().toString().contains(expectedValue)) {
				return true;
			}
			onView(isRoot())
					.perform(CustomActions.wait(intervalMillis));
		}
		return false;
	}

	public static boolean userVariableGreaterThanWithinTimeout(UserVariable userVariable, double expectedValue,
			int timeoutMillis) {
		int step = 10;
		for (; timeoutMillis > 0; timeoutMillis -= step) {
			if ((double) userVariable.getValue() > (expectedValue + EPSILON)) {
				return true;
			}
			onView(isRoot()).perform(CustomActions.wait(step));
		}
		return false;
	}

	private static boolean areEqualWithinEpsilon(double expected, double actual) {
		return actual >= expected - EPSILON && actual <= expected + EPSILON;
	}
}
