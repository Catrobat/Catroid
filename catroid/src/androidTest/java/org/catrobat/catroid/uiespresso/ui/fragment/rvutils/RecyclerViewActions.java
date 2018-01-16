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

package org.catrobat.catroid.uiespresso.ui.fragment.rvutils;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.util.TreeIterables;
import android.view.View;

import org.catrobat.catroid.R;
import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.allOf;

public final class RecyclerViewActions {

	private RecyclerViewActions() {
	}

	public static void openOverflowMenu() {
		onView(isRoot())
				.perform(new WaitForActionBarReady());
		onView(allOf(isDisplayed(), withContentDescription("More options")))
				.perform(click());
	}

	private static class WaitForActionBarReady implements ViewAction {
		@Override
		public void perform(UiController controller, View view) {
			int loops = 0;
			while (!isMenuButtonReady(view) && loops < 100) {
				loops++;
				controller.loopMainThreadForAtLeast(50);
			}
		}

		@Override
		public String getDescription() {
			return "Takes care that click does not happen to early when coming from "
					+ "Action Mode";
		}

		@Override
		public Matcher<View> getConstraints() {
			return isRoot();
		}

		private boolean isMenuButtonReady(View view) {
			int actionButtonCount = 0;
			boolean confirmDisplayed = false;
			for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
				if (allOf(isDisplayed(), withContentDescription("More options")).matches(child)) {
					actionButtonCount++;
				}
				if (allOf(isDisplayed(), withId(R.id.confirm)).matches(child)) {
					confirmDisplayed = true;
				}
			}
			return actionButtonCount == 1 && !confirmDisplayed;
		}
	}
}
