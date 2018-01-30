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

import android.support.test.espresso.ViewInteraction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.uiespresso.util.wrappers.ViewInteractionWrapper;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;

public class RecyclerViewInteractionWrapper extends ViewInteractionWrapper {
	protected static int recyclerViewId = R.id.recycler_view;
	protected int position;

	protected RecyclerViewInteractionWrapper(ViewInteraction viewInteraction, int position) {
		super(viewInteraction);
		this.position = position;
	}

	public static RecyclerViewInteractionWrapper onRVAtPosition(int position) {
		return new RecyclerViewInteractionWrapper(
				onView(new RecyclerViewMatcher(recyclerViewId).withPosition(position)), position);
	}

	public ViewInteraction onChildView(int childViewId) {
		return onView(new RecyclerViewMatcher(recyclerViewId).withIdInsidePosition(childViewId, position));
	}

	public RecyclerViewInteractionWrapper performCheckItem() {
		onChildView(R.id.checkbox)
				.perform(click());
		return this;
	}
}
