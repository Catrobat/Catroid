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

import android.support.test.espresso.matcher.BoundedMatcher;
import android.view.View;
import android.widget.ListView;

import org.catrobat.catroid.common.ProjectData;
import org.catrobat.catroid.ui.adapter.ProjectListAdapter;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static org.hamcrest.Matchers.equalTo;

public final class ProjectListMatchers {
	private ProjectListMatchers() {
		throw new AssertionError();
	}

	public static Matcher<View> isProjectListView() {
		return new TypeSafeMatcher<View>() {

			@Override
			protected boolean matchesSafely(View view) {
				return view instanceof ListView && ((ListView) view).getAdapter()
						instanceof ProjectListAdapter;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("ProjectListView");
			}
		};
	}

	public static Matcher<Object> withProjectName(String expectedProjectName) {
		return withProjectName(equalTo(expectedProjectName));
	}
	private static Matcher<Object> withProjectName(final Matcher<String> expectedName) {
		return new BoundedMatcher<Object, ProjectData>(ProjectData.class) {

			@Override
			public boolean matchesSafely(final ProjectData actualObject) {
				return expectedName.matches(actualObject.projectName);
			}

			@Override
			public void describeTo(final Description description) {
				description.appendText("project name matches");
			}
		};
	}
}
