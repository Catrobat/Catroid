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

package org.catrobat.catroid.uiespresso.formulaeditor;

import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.UserDefinedBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.catrobat.catroid.userbrick.UserDefinedBrickInput;
import org.catrobat.catroid.userbrick.UserDefinedBrickLabel;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.TestCase.assertEquals;

import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;

import static java.util.Arrays.asList;

import static androidx.test.espresso.Espresso.pressBack;

@RunWith(AndroidJUnit4.class)
public class FormulaEditorUserDefinedBrickTest {

	private UserDefinedBrick userDefinedBrick;
	private static UserDefinedBrickLabel label = new UserDefinedBrickLabel("Label");
	private static UserDefinedBrickInput input = new UserDefinedBrickInput("Input");
	private static UserDefinedBrickInput secondInput = new UserDefinedBrickInput("SecondInput");

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION,
			SpriteActivity.FRAGMENT_SCRIPTS);

	@After
	public void tearDown() throws Exception {
		TestUtils.deleteProjects(FormulaEditorUserDefinedBrickTest.class.getSimpleName());
	}

	@Before
	public void setUp() throws Exception {
		Script script = UiTestUtils.createProjectAndGetStartScript(FormulaEditorUserDefinedBrickTest.class.getSimpleName());
		userDefinedBrick = new UserDefinedBrick(asList(input, label, secondInput));
		userDefinedBrick.setCallingBrick(true);
		userDefinedBrick.getFormulaMap().putIfAbsent(input.getInputFormulaField(), new Formula(100));
		userDefinedBrick.getFormulaMap().putIfAbsent(secondInput.getInputFormulaField(), new Formula(200));
		script.addBrick(userDefinedBrick);
		baseActivityTestRule.launchActivity();
	}

	@Test
	public void testChangeFormula() throws Throwable {
		clickOnFormulaField(input.getInputFormulaField());
		onFormulaEditor().performEnterFormula("1234");
		pressBack();
		assertEquals("200 ", getValueOfFormulaField(secondInput.getInputFormulaField()));
		assertEquals("1234 ", getValueOfFormulaField(input.getInputFormulaField()));
	}

	@Test
	public void testSwitchBetweenFormulaFields() throws Throwable {
		clickOnFormulaField(input.getInputFormulaField());
		onFormulaEditor()
				.checkShows(userDefinedBrick.getFormulaWithBrickField(input.getInputFormulaField())
						.getTrimmedFormulaString(ApplicationProvider.getApplicationContext()));
		clickOnFormulaField(secondInput.getInputFormulaField());
		onFormulaEditor()
				.checkShows(userDefinedBrick.getFormulaWithBrickField(secondInput.getInputFormulaField())
						.getTrimmedFormulaString(ApplicationProvider.getApplicationContext()));
	}

	private void clickOnFormulaField(Brick.FormulaField formulaField) throws Throwable {
		baseActivityTestRule.runOnUiThread(() -> userDefinedBrick.getTextView(formulaField).callOnClick());
	}

	private String getValueOfFormulaField(Brick.FormulaField formulaField) {
		return userDefinedBrick.getFormulaWithBrickField(formulaField).getTrimmedFormulaString(baseActivityTestRule.getActivity());
	}
}
