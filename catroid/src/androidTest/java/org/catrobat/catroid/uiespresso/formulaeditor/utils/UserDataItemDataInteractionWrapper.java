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

package org.catrobat.catroid.uiespresso.formulaeditor.utils;

import android.support.test.espresso.DataInteraction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.uiespresso.util.wrappers.DataInteractionWrapper;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;

public abstract class UserDataItemDataInteractionWrapper<T extends UserDataItemDataInteractionWrapper<T>>
		extends DataInteractionWrapper{

	protected UserDataItemDataInteractionWrapper(DataInteraction dataInteraction) {
		super(dataInteraction);
	}

	public void performSelect() {
		dataInteraction.onChildView(withId(R.id.fragment_formula_editor_datalist_item_name_text_view))
				.perform(click());
	}

	public void performDelete() {
		dataInteraction.onChildView(withId(R.id.fragment_formula_editor_datalist_item_name_text_view))
				.perform(longClick());
		onView(withText(R.string.delete))
				.perform(click());
		onView(withText(R.string.deletion_alert_yes))
				.perform(click());
	}

	public void performRename(String newName) {
		dataInteraction.onChildView(withId(R.id.fragment_formula_editor_datalist_item_name_text_view))
				.perform(longClick());
		onView(withText(R.string.rename))
				.perform(click());
		onView(withId(R.id.dialog_formula_rename_variable_name_edit_text))
				.perform(replaceText(newName), closeSoftKeyboard());
		onView(withText(R.string.ok))
				.perform(click());
	}

	public T checkHasName(String name) {
		dataInteraction.onChildView(
				allOf(withId(R.id.fragment_formula_editor_datalist_item_name_text_view), withText(name)))
				.check(matches(isDisplayed()));
		return (T) this;
	}
}
