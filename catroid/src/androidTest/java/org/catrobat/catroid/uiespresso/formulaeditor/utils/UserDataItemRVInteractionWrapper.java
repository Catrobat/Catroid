/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.formulaeditor.utils;

import org.catrobat.catroid.R;
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewItemInteractionWrapper;
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewItemMatcher;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public abstract class UserDataItemRVInteractionWrapper<T extends UserDataItemRVInteractionWrapper<T>>
		extends RecyclerViewItemInteractionWrapper {

	protected UserDataItemRVInteractionWrapper(int position) {
		super(onView(new RecyclerViewItemMatcher(recyclerViewId).withPosition(position)), position);
	}

	public void performSelect() {
		onChildView(R.id.title_view)
				.perform(click());
	}

	public void performDelete() {
		onChildView(R.id.title_view)
				.perform(longClick());
		onView(withText(R.string.delete))
				.perform(click());
		onView(withText(R.string.delete))
				.perform(click());
	}

	public void performRename(String newName) {
		onChildView(R.id.title_view)
				.perform(longClick());
		onView(withText(R.string.rename))
				.perform(click());
		onView(withId(R.id.input_edit_text))
				.perform(replaceText(newName), closeSoftKeyboard());
		onView(withText(R.string.ok))
				.perform(click());
	}

	public void performEdit(String newValue) {
		onChildView(R.id.title_view)
				.perform(longClick());
		onView(withText(R.string.edit))
				.perform(click());
		onView(withId(R.id.input_edit_text))
				.perform(replaceText(newValue), closeSoftKeyboard());
		onView(withText(R.string.save))
				.perform(click());
	}

	public T checkHasName(String name) {
		onChildView(R.id.title_view)
				.check(matches(withText(name)));
		return (T) this;
	}
}
