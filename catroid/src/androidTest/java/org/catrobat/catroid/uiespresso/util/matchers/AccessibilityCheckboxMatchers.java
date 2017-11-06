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

package org.catrobat.catroid.uiespresso.util.matchers;

import android.support.test.espresso.matcher.BoundedMatcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static org.catrobat.catroid.ui.adapter.AccessibilityCheckboxAdapter.AccessibilityCheckbox;

public final class AccessibilityCheckboxMatchers {
	private AccessibilityCheckboxMatchers() {
		throw new AssertionError();
	}

	public static Matcher<Object> withTitle(final String expectedTitle) {
		return new BoundedMatcher<Object, AccessibilityCheckbox>(AccessibilityCheckbox.class) {
			@Override
			protected boolean matchesSafely(AccessibilityCheckbox checkbox) {
				return checkbox.title.equals(expectedTitle);
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("Expected title : \"" + expectedTitle + "\"");
			}
		};
	}
}
