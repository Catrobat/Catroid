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
package org.catrobat.catroid.uiespresso.ui.dialog;

import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.NoteBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class FormulaEditorComputeDialogTest {

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class);

	private Integer someIntegerVaule = 911;

	@Before
	public void setUp() throws Exception {
		createProject("formulaEditorComputeDialogTest");
		baseActivityTestRule.launchActivity(null);
	}

	private void openComputeDialog() {
		onView(withId(R.id.brick_note_edit_text))
				.perform(click());
		onView(withId(R.id.formula_editor_keyboard_compute))
				.perform(click());
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void dialogCreationTest() {
		openComputeDialog();

		String computedValueText = Integer.toString(someIntegerVaule)
				+ UiTestUtils.getResourcesString(R.string.formula_editor_decimal_mark) + "0";

		onView(withId(R.id.formula_editor_compute_dialog_textview))
				.check(matches(withText(computedValueText)));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void cancelOnTouchInsideOfDialogTest() {
		openComputeDialog();

		onView(withId(R.id.formula_editor_compute_dialog_textview))
				.perform(click());

		onView(withId(R.id.formula_editor_compute_dialog_textview))
				.check(doesNotExist());
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void cancelOnBackDialogTest() {
		openComputeDialog();

		pressBack();

		onView(withId(R.id.formula_editor_compute_dialog_textview))
				.check(doesNotExist());
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void cancelOnTouchOutsideOfDialogTest() {
		openComputeDialog();

		onView(withId(R.id.formula_editor_compute_dialog_textview))
				.perform(clickWithOffset(-50, -50));

		onView(withId(R.id.formula_editor_compute_dialog_textview))
				.check(doesNotExist());
	}

	public Project createProject(String projectName) {
		Project project = new Project(null, projectName);
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript();

		Formula noteFormula = new Formula(someIntegerVaule);
		script.addBrick(new NoteBrick(noteFormula));

		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);

		return project;
	}

	public static ViewAction clickWithOffset(final int xOffset, final int yOffset) {
		return new GeneralClickAction(
			Tap.SINGLE,
			new CoordinatesProvider() {
				@Override
				public float[] calculateCoordinates(View view) {
					final int[] viewsCoordinates = new int[2];
					view.getLocationOnScreen(viewsCoordinates);

					float[] clickCoordinates = {viewsCoordinates[0] + xOffset, viewsCoordinates[1] + yOffset};
					return clickCoordinates;
				}
			},
			Press.FINGER);
	}
}
