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

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class RecyclerViewMatcher {
	private final int recyclerViewId;

	public RecyclerViewMatcher(int recyclerViewId) {
		this.recyclerViewId = recyclerViewId;
	}

	public Matcher<View> atPosition(final int position) {
		return atPositionOnView(position, -1);
	}

	public Matcher<View> atPositionOnView(final int position, final int targetViewId) {

		return new TypeSafeMatcher<View>() {
			Resources resources = null;
			View childView;

			public void describeTo(Description description) {
				String idDescription = Integer.toString(recyclerViewId);
				if (this.resources != null) {
					try {
						idDescription = this.resources.getResourceName(recyclerViewId);
					} catch (Resources.NotFoundException var4) {
						idDescription = String.format("%s (resource name not found)",
								recyclerViewId);
					}
				}

				description.appendText("with id: " + idDescription);
			}

			public boolean matchesSafely(View view) {

				this.resources = view.getResources();

				if (childView == null) {
					RecyclerView recyclerView =
							(RecyclerView) view.getRootView().findViewById(recyclerViewId);
					if (recyclerView != null && recyclerView.getId() == recyclerViewId) {
						childView = recyclerView.findViewHolderForAdapterPosition(position).itemView;
					} else {
						return false;
					}
				}

				if (targetViewId == -1) {
					return view == childView;
				} else {
					View targetView = childView.findViewById(targetViewId);
					return view == targetView;
				}
			}
		};
	}
}
