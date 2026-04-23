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

package org.catrobat.catroid.uiespresso.ui.fragment.rvutils;

import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.uiespresso.util.wrappers.ViewInteractionWrapper;
import org.hamcrest.Matcher;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnHolderItem;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class RecyclerViewInteractionWrapper extends ViewInteractionWrapper {
	private static int recyclerViewId = R.id.recycler_view;
	private static final Matcher<View> RV_MATCHER = withId(recyclerViewId);

	protected RecyclerViewInteractionWrapper(ViewInteraction viewInteraction) {
		super(viewInteraction);
	}

	public static RecyclerViewInteractionWrapper onRecyclerView() {
		return new RecyclerViewInteractionWrapper(onView(RV_MATCHER));
	}

	public RecyclerViewItemInteractionWrapper atPosition(int position) {
		return new RecyclerViewItemInteractionWrapper(
				onView(new RecyclerViewItemMatcher(recyclerViewId).withPosition(position)), position);
	}

	public ViewInteraction performOnItemWithText(String text, ViewAction viewAction) {
		return perform(actionOnHolderItem(new RecyclerViewHolderMatcher().withText(text), viewAction));
	}

	public RecyclerViewInteractionWrapper checkHasNumberOfItems(int count) {
		viewInteraction
				.check(matches(new RecyclerViewMatcher(recyclerViewId).withNumberOfItems(count)));
		return this;
	}

	public RecyclerViewInteractionWrapper checkHasSortedOrder() {
		viewInteraction
				.check(matches(new RecyclerViewMatcher(recyclerViewId).listIsSorted()));
		return this;
	}
}
