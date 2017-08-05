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

import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public final class ViewMatchers {
	private ViewMatchers() {
		throw new AssertionError();
	}

	//usage: in MyProjectActivity or other Activity which has (ListView) multiViews with same Id
	// first parameter is a matcher like withId or withText and the second is the index of the view, index start with 0
	//example: onView(withIndex(withText(R.string.last_used), 0))
	//		.check(isLeftOf(withIndex(withId(R.id.list_item_image_view), 0)));
	public static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
		return new TypeSafeMatcher<View>() {
			int currentIndex = 0;

			@Override
			public void describeTo(Description description) {
				description.appendText("with index: ");
				description.appendValue(index);
				matcher.describeTo(description);
			}

			@Override
			public boolean matchesSafely(View view) {
				return matcher.matches(view) && currentIndex++ == index;
			}
		};
	}
}
