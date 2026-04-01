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

package org.catrobat.catroid.uiespresso.util;

import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.uiespresso.util.actions.CustomActions;

import static junit.framework.Assert.assertEquals;

import static org.junit.Assert.assertNotEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;

public final class UserVariableAssertions {

	private static final double EPSILON = 0.001;

	private UserVariableAssertions() {
		throw new AssertionError();
	}

	public static void assertUserVariableEqualsWithTimeout(UserVariable userVariable, double expectedValue,
			int timeoutMillis) {
		for (int intervalMillis = 10; timeoutMillis > 0; timeoutMillis -= intervalMillis) {
			if (areEqualWithinEpsilon(expectedValue, (Double) userVariable.getValue())) {
				assertEquals(expectedValue, (Double) userVariable.getValue(), EPSILON);
				return;
			}
			onView(isRoot())
					.perform(CustomActions.wait(intervalMillis));
		}
		assertEquals(expectedValue, (Double) userVariable.getValue(), EPSILON);
	}

	public static void assertUserVariableNotEqualsForTimeMs(UserVariable userVariable, double expectedValue,
			int timeoutMillis) {
		for (int intervalMillis = 10; timeoutMillis > 0; timeoutMillis -= intervalMillis) {
			if (areEqualWithinEpsilon(expectedValue, (Double) userVariable.getValue())) {
				assertNotEquals(expectedValue, (Double) userVariable.getValue());
			}
			onView(isRoot())
					.perform(CustomActions.wait(intervalMillis));
		}
		assertNotEquals(expectedValue, (Double) userVariable.getValue());
	}

	public static void assertUserVariableEqualsWithTimeout(UserVariable userVariable, String expectedValue,
			int timeoutMillis) {
		for (int intervalMillis = 10; timeoutMillis > 0; timeoutMillis -= intervalMillis) {
			if (expectedValue.equals(userVariable.getValue().toString())) {
				assertEquals(expectedValue, userVariable.getValue().toString());
				return;
			}
			onView(isRoot())
					.perform(CustomActions.wait(intervalMillis));
		}
		assertEquals(expectedValue, userVariable.getValue().toString());
	}

	public static void assertUserVariableContainsStringWithTimeout(UserVariable userVariable, String expectedValue,
			int timeoutMillis) {
		for (int intervalMillis = 10; timeoutMillis > 0; timeoutMillis -= intervalMillis) {
			if (userVariable.getValue().toString().contains(expectedValue)) {
				assertThat(userVariable.getValue().toString(), containsString(expectedValue));
				return;
			}
			onView(isRoot())
					.perform(CustomActions.wait(intervalMillis));
		}
		assertThat(userVariable.getValue().toString(), containsString(expectedValue));
	}

	public static void assertUserVariableIsGreaterThanWithTimeout(UserVariable userVariable, double expectedValue,
			int timeoutMillis) {
		for (int intervalMillis = 10; timeoutMillis > 0; timeoutMillis -= intervalMillis) {
			if ((double) userVariable.getValue() > (expectedValue + EPSILON)) {
				assertThat((double) userVariable.getValue(), is(greaterThan(expectedValue + EPSILON)));
				return;
			}
			onView(isRoot()).perform(CustomActions.wait(intervalMillis));
		}
		assertThat((double) userVariable.getValue(), is(greaterThan(expectedValue + EPSILON)));
	}

	private static boolean areEqualWithinEpsilon(double expected, double actual) {
		return actual >= expected - EPSILON && actual <= expected + EPSILON;
	}
}
