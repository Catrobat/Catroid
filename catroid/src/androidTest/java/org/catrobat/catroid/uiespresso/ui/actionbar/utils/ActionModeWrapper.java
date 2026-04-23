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

import org.catrobat.catroid.R;
import org.hamcrest.Matcher;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public final class ActionModeWrapper {
	public static final Matcher<View> TITLE_MATCHER = withId(R.id.action_bar_title);
	public static final Matcher<View> NAVIGATE_UP_MATCHER = withId(R.id.action_mode_close_button);
	public static final Matcher<View> CONFIRM_MATCHER = withId(R.id.confirm);

	private ActionModeWrapper() {
		onView(TITLE_MATCHER)
				.check(matches(isDisplayed()));
		onView(NAVIGATE_UP_MATCHER)
				.check(matches(isDisplayed()));
		onView(CONFIRM_MATCHER)
				.check(matches(isDisplayed()));
	}

	public static ActionModeWrapper onActionMode() {
		return new ActionModeWrapper();
	}

	public ActionModeWrapper checkTitleMatches(String text) {
		onView(TITLE_MATCHER).check(matches(withText(text)));
		return this;
	}

	public void performGoBack() {
		onView(NAVIGATE_UP_MATCHER).perform(click());
	}

	public ActionModeWrapper performConfirm() {
		onView(CONFIRM_MATCHER).perform(click());
		return this;
	}
}
