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

package org.catrobat.catroid.uiespresso.util.matchers;

import android.view.View;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.List;

public final class BrickSpinnerMatchers {
	private BrickSpinnerMatchers() {
		throw new AssertionError();
	}

	public static Matcher<View> withNameableValues(final List<String> stringValues) {
		return new TypeSafeMatcher<View>() {
			@Override
			protected boolean matchesSafely(View view) {
				if (!(view instanceof Spinner)) {
					return false;
				}
				SpinnerAdapter spinnerAdapter = ((Spinner) view).getAdapter();
				if (stringValues.size() != spinnerAdapter.getCount()
						|| !(spinnerAdapter instanceof BrickSpinner.BrickSpinnerAdapter)) {
					return false;
				}
				for (int index = 0; index < spinnerAdapter.getCount(); index++) {
					Nameable item = (Nameable) spinnerAdapter.getItem(index);
					if (!item.getName().equals(stringValues.get(index))) {
						return false;
					}
				}
				return true;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("with spinner values");
			}
		};
	}

	public static Matcher<View> withStringValues(final List<String> stringValues) {
		return new TypeSafeMatcher<View>() {
			@Override
			protected boolean matchesSafely(View view) {
				if (!(view instanceof Spinner)) {
					return false;
				}
				SpinnerAdapter spinnerAdapter = ((Spinner) view).getAdapter();
				if (stringValues.size() != spinnerAdapter.getCount()) {
					return false;
				}
				for (int index = 0; index < spinnerAdapter.getCount(); index++) {
					String item = ((Nameable) spinnerAdapter.getItem(index)).getName();
					if (!item.equals(stringValues.get(index))) {
						return false;
					}
				}
				return true;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("with spinner values");
			}
		};
	}
}
