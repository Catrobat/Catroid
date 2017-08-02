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
package org.catrobat.catroid.uitest.formulaeditor;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.ui.fragment.MainFormulaEditorCategoryListFragment;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.regex.Pattern;

public class FormulaEditorListFragmentTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private static final int[] FUNCTIONS_ITEMS = {R.string.formula_editor_function_sin,
			R.string.formula_editor_function_cos, R.string.formula_editor_function_tan,
			R.string.formula_editor_function_ln, R.string.formula_editor_function_log,
			R.string.formula_editor_function_pi, R.string.formula_editor_function_sqrt,
			R.string.formula_editor_function_rand, R.string.formula_editor_function_abs,
			R.string.formula_editor_function_round, R.string.formula_editor_function_mod,
			R.string.formula_editor_function_arcsin, R.string.formula_editor_function_arccos,
			R.string.formula_editor_function_arctan, R.string.formula_editor_function_exp,
			R.string.formula_editor_function_floor, R.string.formula_editor_function_ceil,
			R.string.formula_editor_function_max, R.string.formula_editor_function_min,
			R.string.formula_editor_function_length, R.string.formula_editor_function_letter,
			R.string.formula_editor_function_join, R.string.formula_editor_function_number_of_items,
			R.string.formula_editor_function_list_item, R.string.formula_editor_function_contains};

	private static final int[] FUNCTIONS_PARAMETERS = {R.string.formula_editor_function_sin_parameter,
			R.string.formula_editor_function_cos_parameter, R.string.formula_editor_function_tan_parameter,
			R.string.formula_editor_function_ln_parameter, R.string.formula_editor_function_log_parameter,
			R.string.formula_editor_function_pi_parameter, R.string.formula_editor_function_sqrt_parameter,
			R.string.formula_editor_function_rand_parameter, R.string.formula_editor_function_abs_parameter,
			R.string.formula_editor_function_round_parameter, R.string.formula_editor_function_mod_parameter,
			R.string.formula_editor_function_arcsin_parameter, R.string.formula_editor_function_arccos_parameter,
			R.string.formula_editor_function_arctan_parameter, R.string.formula_editor_function_exp_parameter,
			R.string.formula_editor_function_floor_parameter, R.string.formula_editor_function_ceil_parameter,
			R.string.formula_editor_function_max_parameter, R.string.formula_editor_function_min_parameter,
			R.string.formula_editor_function_length_parameter, R.string.formula_editor_function_letter_parameter,
			R.string.formula_editor_function_join_parameter, R.string.formula_editor_function_number_of_items_parameter,
			R.string.formula_editor_function_list_item_parameter, R.string.formula_editor_function_contains_parameter};

	public FormulaEditorListFragmentTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	private void createProject(String projectName) throws InterruptedException {

		Project project = new Project(null, projectName);

		Sprite firstSprite = new SingleSprite("firstSprite");

		project.getDefaultScene().addSprite(firstSprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);

		Script startScript1 = new StartScript();
		Brick changeBrick = new ChangeSizeByNBrick(0);
		firstSprite.addScript(startScript1);
		startScript1.addBrick(changeBrick);
	}

	public void testFunctionsListElements() {

		solo.clickOnView(solo.getView(R.id.brick_change_size_by_edit_text));
		assertTrue("FormulaEditorFragment not found", solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG, 1000));

		solo.clickOnText(solo.getString(R.string.formula_editor_functions));
		assertTrue("FormulaEditorCategoryListFragment not found", solo.waitForFragmentByTag(MainFormulaEditorCategoryListFragment.FUNCTION_TAG, 1000));

		int numberOfFunctions = FUNCTIONS_ITEMS.length;

		for (int i = 0; i < numberOfFunctions; i++) {
			String listItemText = solo.getString(FUNCTIONS_ITEMS[i]) + solo.getString(FUNCTIONS_PARAMETERS[i]);
			assertTrue("Function element not found or incorrect", solo.waitForText(Pattern.quote(listItemText), 1, 2000));
		}
	}
}
