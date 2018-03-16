/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.matchers.FormulaEditorCategoryListMatchers;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;

public class FormulaEditorCategoryListWrapper {
	FormulaEditorCategoryListWrapper() {
		onView(FormulaEditorCategoryListMatchers.isFunctionListView())
				.check(matches(isDisplayed()));
	}

	public FormulaEditorWrapper performSelect(String selection) {
		onData(allOf(is(instanceOf(String.class)), is(selection)))
				.inAdapterView(FormulaEditorCategoryListMatchers.isFunctionListView())
				.onChildView(withId(R.id.fragment_formula_editor_list_item))
				.check(matches(isDisplayed()))
				.perform(click());
		return FormulaEditorWrapper.onFormulaEditor();
	}

	public FormulaEditorWrapper performSelect(int stringResourceId) {
		String selection = UiTestUtils.getResourcesString(stringResourceId);
		return performSelect(selection);
	}
}
