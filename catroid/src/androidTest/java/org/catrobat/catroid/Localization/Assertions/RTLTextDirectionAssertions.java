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
package org.catrobat.catroid.Localization.Assertions;
        import android.support.test.espresso.NoMatchingViewException;
        import android.support.test.espresso.ViewAssertion;
        import android.view.View;
        import org.hamcrest.BaseMatcher;
        import org.hamcrest.Description;
        import static org.hamcrest.MatcherAssert.assertThat;

public class RTLTextDirectionAssertions {
    public static ViewAssertion isRTLTextDirection() {
        return new ViewAssertion() {
            public void check(View view, NoMatchingViewException noView) {
                assertThat(view, new TextDirectionMatcher(View.TEXT_DIRECTION_RTL));
            }
        };
    }

    private static class TextDirectionMatcher extends BaseMatcher<View> {
        private int textDirection;

        public TextDirectionMatcher(int textDirection) {
            this.textDirection = textDirection;
        }
        @Override public void describeTo(Description description) {
            String TextDirection;
            if (textDirection == View.TEXT_DIRECTION_FIRST_STRONG) TextDirection = "Right to Left";
            else TextDirection = "Left to Right";
            description.appendText("View TextDirection must has equals " + TextDirection);
        }

        @Override public boolean matches(Object o) {
            if (o == null) {
                if (textDirection == View.TEXT_DIRECTION_FIRST_STRONG) return true;
                else if (textDirection == View.TEXT_DIRECTION_LTR) return false;
            }
            if (!(o instanceof View))
                throw new IllegalArgumentException("Object must be instance of View. Object is instance of " + o);
            return ((View) o).getTextDirection() == textDirection;
        }
    }
}