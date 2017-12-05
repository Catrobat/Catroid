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

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.matchers.FormulaEditorFunctionListMatchers;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class FormulaEditorObjectListTouchesObjectTest {

	private String nameSprite2 = "testSprite2";
	private Project project;
	private Sprite sprite;
	private Sprite sprite2;

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		project = UiTestUtils.createEmptyProject("FormulaEditorObjectTest");
		sprite = project.getDefaultScene().getSpriteBySpriteName("testSprite");
		sprite.getScript(0).addBrick(new ChangeSizeByNBrick(0));
		sprite2 = new Sprite(nameSprite2);
		project.getDefaultScene().addSprite(sprite2);
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void testObjectListTouchesObject() {
		onBrickAtPosition(1)
				.onChildView(withId(R.id.brick_change_size_by_edit_text))
				.perform(click());

		onView(withText(R.string.formula_editor_object))
				.perform(click());

		String editorFunction = UiTestUtils.getResourcesString(R.string.formula_editor_function_collision);

		onData(allOf(is(instanceOf(String.class)), is(editorFunction)))
				.inAdapterView(FormulaEditorFunctionListMatchers.isFunctionListView())
				.onChildView(withId(R.id.fragment_formula_editor_list_item))
				.perform(click());

		onView(withText(nameSprite2))
				.check(matches(isDisplayed()));
		onView(withText(R.string.ok))
				.perform(click());
		onView(withId(R.id.formula_editor_edit_field))
				.check(matches(withText(editorFunction + "(" + nameSprite2 + ") ")));

		onView(withId(R.id.formula_editor_keyboard_delete))
				.perform(click());
		onView(withId(R.id.formula_editor_edit_field))
				.check(matches(withText(" ")));
	}
}
