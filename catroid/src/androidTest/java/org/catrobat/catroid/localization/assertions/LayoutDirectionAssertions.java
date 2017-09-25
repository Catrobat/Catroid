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
package org.catrobat.catroid.localization.assertions;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.view.View;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import static org.hamcrest.MatcherAssert.assertThat;

public class LayoutDirectionAssertions {
	public static ViewAssertion isLayoutDirectionRTL() {
		return new ViewAssertion() {
			public void check(View view, NoMatchingViewException noView) {
				assertThat(view, new LayoutDirectionMatcher(View.LAYOUT_DIRECTION_RTL));
			}
		};
	}

	private static class LayoutDirectionMatcher extends BaseMatcher<View> {
		private int layoutDirection;

		public LayoutDirectionMatcher(int layoutDirection) {
			this.layoutDirection = layoutDirection;
		}

		@Override
		public void describeTo(Description description) {
			String layoutDirectionStr;
			if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
				layoutDirectionStr = "Right to Left";
			} else {
				layoutDirectionStr = "Left to Right";
			}
			description.appendText("View LayoutDirection must has equals " + layoutDirectionStr);
		}

		@Override
		public boolean matches(Object object) {
			if (object == null) {
				if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
					return true;
				} else if (layoutDirection == View.LAYOUT_DIRECTION_LTR) {
					return false;
				}
			}

			if (!(object instanceof View)) {
				throw new IllegalArgumentException("Object must be instance of View. Object is instance of " + object);
			}
			return ((View) object).getLayoutDirection() == layoutDirection;
		}
	}
}


