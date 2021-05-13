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

package org.catrobat.catroid.uiespresso.formulaeditor;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorDataListWrapper.onDataList;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class FormulaEditorShowVariableTest {
	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
		Script script = BrickTestUtils.createProjectAndGetStartScript("FormulaEditorShowVariableTest");
		script.addBrick(new ChangeSizeByNBrick(10));
		baseActivityTestRule.launchActivity();
	}

	private static String variableNameNew = "variableNew";
	private static Integer whenBrickPosition = 0;
	private static Integer changeSizeBrickPosition = 1;
	private static String variableName = "variable";
	private static String variableValue = "20";



	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void showVariable() {
		onBrickAtPosition(whenBrickPosition).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(changeSizeBrickPosition).checkShowsText(R.string.brick_change_size_by);

		onBrickAtPosition(changeSizeBrickPosition).onChildView(withId(R.id.brick_change_size_by_edit_text))
				.perform(click());

		onFormulaEditor()
				.performOpenDataFragment();

		onDataList()
				.performAdd(variableName);

		onDataList().onVariableAtPosition(0).performEdit(variableValue);

		onDataList().onVariableAtPosition(0)
				.checkHasName(variableName);
		onDataList().onVariableAtPosition(0).checkHasValue(variableValue);

		onDataList().onVariableAtPosition(0).performArrowClick(variableName,variableValue);

		onDataList()
				.performClose();

	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void addAndShowVariable() {
		onBrickAtPosition(whenBrickPosition).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(changeSizeBrickPosition).checkShowsText(R.string.brick_change_size_by);

		onBrickAtPosition(changeSizeBrickPosition).onChildView(withId(R.id.brick_change_size_by_edit_text))
				.perform(click());

		onFormulaEditor()
				.performOpenDataFragment();

		onDataList()
				.performAdd(variableName);

		onDataList().onVariableAtPosition(0).performEdit(variableValue);

		onDataList().onVariableAtPosition(0)
				.checkHasName(variableName);
		onDataList().onVariableAtPosition(0).checkHasValue(variableValue);

		onDataList()
				.performAdd(variableNameNew);

		onDataList().onVariableAtPosition(1)
				.performSelect();

		onFormulaEditor()
				.checkShows(getUserVariableEditText(variableNameNew));

		onFormulaEditor()
				.performOpenDataFragment();

		onDataList().onVariableAtPosition(0).performArrowClick(variableName,variableValue);

		onDataList().onVariableAtPosition(1).performArrowClick(variableNameNew,"0");

		onDataList()
				.performClose();


	}


	private String getUserVariableEditText(String variableName) {
		return "\"" + variableName + "\" ";
	}

}
