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

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewItemMatcher {
	private final int recyclerViewId;

	public RecyclerViewItemMatcher(int recyclerViewId) {
		this.recyclerViewId = recyclerViewId;
	}

	public Matcher<View> withPosition(final int position) {

		return new TypeSafeMatcher<View>() {
			public void describeTo(Description description) {
				description.appendText("RecyclerViewItemMatcher at position: " + Integer.toString(position)
						+ "does not match the view");
			}

			public boolean matchesSafely(View view) {
				RecyclerView recyclerView =
						(RecyclerView) view.getRootView().findViewById(recyclerViewId);
				return recyclerView != null
						&& recyclerView.getId() == recyclerViewId
						&& recyclerView.findViewHolderForAdapterPosition(position) != null
						&& view == recyclerView.findViewHolderForAdapterPosition(position).itemView;
			}
		};
	}

	public Matcher<View> withIdInsidePosition(final int viewId, final int position) {

		return new TypeSafeMatcher<View>() {
			public void describeTo(Description description) {
				description.appendText("RecyclerViewItemMatcher with Id: " + viewId + "inside position: "
						+ Integer.toString(position) + "does not match the view");
			}

			public boolean matchesSafely(View view) {
				RecyclerView recyclerView =
						(RecyclerView) view.getRootView().findViewById(recyclerViewId);
				return recyclerView != null
						&& recyclerView.getId() == recyclerViewId
						&& recyclerView.findViewHolderForAdapterPosition(position) != null
						&& view == recyclerView.findViewHolderForAdapterPosition(position).itemView
								.findViewById(viewId);
			}
		};
	}
}
