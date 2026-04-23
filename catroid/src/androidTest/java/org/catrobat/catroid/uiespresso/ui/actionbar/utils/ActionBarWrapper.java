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

package org.catrobat.catroid.uiespresso.ui.actionbar.utils;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public final class ActionBarWrapper {
	public static final Matcher<View> TOOLBAR_MATCHER = withId(R.id.toolbar);
	public static final Matcher<View> TITLE_MATCHER = allOf(withParent(TOOLBAR_MATCHER), instanceOf(TextView.class));
	public static final Matcher<View> NAVIGATE_UP_MATCHER = allOf(withParent(TOOLBAR_MATCHER),
			instanceOf(ImageButton.class), withContentDescription(R.string.abc_action_bar_up_description));

	private ActionBarWrapper() {
		onView(TITLE_MATCHER)
				.check(matches(isDisplayed()));
		onView(NAVIGATE_UP_MATCHER)
				.check(matches(isDisplayed()));
	}

	public static ActionBarWrapper onActionBar() {
		return new ActionBarWrapper();
	}

	public ActionBarWrapper checkTitleMatches(int resId) {
		return checkTitleMatches(UiTestUtils.getResourcesString(resId));
	}

	public ActionBarWrapper checkTitleMatches(String text) {
		onView(TITLE_MATCHER)
				.check(matches(withText(text)));
		return this;
	}

	public void performGoBack() {
		onView(NAVIGATE_UP_MATCHER)
				.perform(click());
	}
}
