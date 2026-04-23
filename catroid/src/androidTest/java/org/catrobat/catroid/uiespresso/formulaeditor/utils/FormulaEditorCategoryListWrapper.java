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

package org.catrobat.catroid.uiespresso.formulaeditor.utils;

import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.ui.recyclerview.fragment.CategoryListFragment;
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.matchers.FormulaEditorCategoryListMatchers;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

public final class FormulaEditorCategoryListWrapper extends RecyclerViewInteractionWrapper {
	private FormulaEditorCategoryListWrapper() {
		super(onView(FormulaEditorCategoryListMatchers.isFunctionListView()));
		onView(FormulaEditorCategoryListMatchers.isFunctionListView())
				.check(matches(isDisplayed()));
	}

	public static FormulaEditorCategoryListWrapper onCategoryList() {
		return new FormulaEditorCategoryListWrapper();
	}

	public void performSelect(String selection) {
		performOnItemWithText(selection, click());
	}

	public void performSelect(int stringResourceId) {
		UiTestUtils.getResourcesString(stringResourceId);
		performSelect(UiTestUtils.getResourcesString(stringResourceId));
	}

	public String getHelpUrl(String tag, SpriteActivity activity) {
		CategoryListFragment fragment = new CategoryListFragment();
		return fragment.getHelpUrl(tag, activity);
	}
}
