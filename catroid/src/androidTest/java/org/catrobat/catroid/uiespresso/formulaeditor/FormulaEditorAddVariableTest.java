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
package org.catrobat.catroid.uiespresso.formulaeditor;

import android.content.Intent;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorDataListWrapper;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorDataListWrapper.onDataList;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;

@RunWith(AndroidJUnit4.class)
public class FormulaEditorAddVariableTest {

	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<SpriteActivity>(SpriteActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		Script script = BrickTestUtils.createProjectAndGetStartScript("FormulaEditorAddVariableTest");
		script.addBrick(new ChangeSizeByNBrick(0));
		Intent intent = new Intent();
		intent.putExtra(SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);
		baseActivityTestRule.launchActivity(intent);

		onView(withId(R.id.brick_change_size_by_edit_text)).perform(click());
		onFormulaEditor().performOpenDataFragment();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void addVariableTest() {
		final String varName = "variable";
		onDataList().performAdd(varName);

		onDataList().onVariableAtPosition(0).checkHasName(varName);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void addListTest() {
		final String listName = "list";
		onDataList().performAdd(listName, FormulaEditorDataListWrapper.ItemType.LIST);

		onDataList().onListAtPosition(0).checkHasName(listName);
	}
}
