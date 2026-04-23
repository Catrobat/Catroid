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

import com.google.common.collect.Ordering;

import org.catrobat.catroid.common.ProjectData;
import org.catrobat.catroid.ui.recyclerview.adapter.RVAdapter;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewMatcher {
	private final int recyclerViewId;

	public RecyclerViewMatcher(int recyclerViewId) {
		this.recyclerViewId = recyclerViewId;
	}

	public Matcher<View> withNumberOfItems(final int numberOfItems) {
		return new TypeSafeMatcher<View>() {
			@Override
			public void describeTo(Description description) {
				description.appendText("RecyclerViewItemMatcher with number of items:" + numberOfItems + " does not match the view");
			}

			@Override
			protected boolean matchesSafely(View view) {
				RecyclerView recyclerView =
						(RecyclerView) view.getRootView().findViewById(recyclerViewId);
				return recyclerView != null
						&& recyclerView.getId() == recyclerViewId
						&& recyclerView.getAdapter() != null
						&& recyclerView.getAdapter().getItemCount() == numberOfItems;
			}
		};
	}

	public Matcher<View> listIsSorted() {
		return new TypeSafeMatcher<View>() {
			@Override
			public void describeTo(Description description) {
				description.appendText("Sorted order of projects does not match");
			}

			@Override
			protected boolean matchesSafely(View view) {
				RecyclerView recyclerView = view.getRootView().findViewById(recyclerViewId);

				if (recyclerView != null) {
					RVAdapter<ProjectData> rvAdapter = (RVAdapter<ProjectData>) recyclerView.getAdapter();

					if (rvAdapter == null) {
						return false;
					}
					List<ProjectData> items = rvAdapter.getItems();
					return Ordering.natural().isOrdered(extractProjectNames(items));
				}
				return false;
			}

			private List<String> extractProjectNames(List<ProjectData> items) {
				List<String> projectNames = new ArrayList<>();
				for (ProjectData project : items) {
					projectNames.add(project.getName());
				}
				return projectNames;
			}
		};
	}
}
