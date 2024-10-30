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

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.ui.recyclerview.fragment.CategoryListFragment;
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Locale;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.CatroidApplication.defaultSystemLanguage;
import static org.catrobat.catroid.common.SharedPreferenceKeys.DEVICE_LANGUAGE;
import static org.catrobat.catroid.common.SharedPreferenceKeys.LANGUAGE_TAGS;
import static org.catrobat.catroid.common.SharedPreferenceKeys.LANGUAGE_TAG_KEY;
import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;
import static org.junit.Assert.assertEquals;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@Category({Cat.AppUi.class, Level.Smoke.class})
@RunWith(AndroidJUnit4.class)
public class FormulaEditorFragmentHelpUrlTest {

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	static String language = getLanguage();
	static String functionsHelpUrl = "https://wiki.catrobat"
			+ ".org/bin/view/Documentation/FormulaEditor/Functions/" + language;
	static String logicHelpUrl = "https://wiki.catrobat.org/bin/view/Documentation/FormulaEditor"
			+ "/Logic/" + language;
	static String sensorsHelpUrl = "https://wiki.catrobat"
			+ ".org/bin/view/Documentation/FormulaEditor/Sensors/" + language;
	static String objectHelpUrl = "https://wiki.catrobat.org/bin/view/Documentation/FormulaEditor"
			+ "/Properties/" + language;

	@Before
	public void setUp() throws Exception {
		Project project = UiTestUtils.createDefaultTestProject("FormulaEditorFragmentHelpUrlTest");
		Sprite sprite = project.getDefaultScene().getSprite(TestUtils.DEFAULT_TEST_SPRITE_NAME);
		Script script = sprite.getScript(TestUtils.DEFAULT_TEST_SCRIPT_INDEX);

		SetVariableBrick setVariableBrick = new SetVariableBrick(new Formula(1), new UserVariable("var"));
		UserVariable userVariable = new UserVariable("Global1");
		project.addUserVariable(userVariable);
		setVariableBrick.setUserVariable(userVariable);

		script.addBrick(setVariableBrick);
		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);

		baseActivityTestRule.launchActivity();
	}

	@Test
	public void testFunctionsHelpUrl() {
		onBrickAtPosition(1).onChildView(withId(R.id.brick_set_variable_edit_text))
				.perform(click());
		String helpUrl = onFormulaEditor()
				.performOpenCategory(FormulaEditorWrapper.Category.FUNCTIONS)
				.getHelpUrl(CategoryListFragment.FUNCTION_TAG, baseActivityTestRule.getActivity());
		assertEquals(functionsHelpUrl, helpUrl);
	}

	@Test
	public void testLogicHelpUrl() {
		onBrickAtPosition(1).onChildView(withId(R.id.brick_set_variable_edit_text))
				.perform(click());
		String helpUrl = onFormulaEditor()
				.performOpenCategory(FormulaEditorWrapper.Category.LOGIC)
				.getHelpUrl(CategoryListFragment.LOGIC_TAG, baseActivityTestRule.getActivity());
		assertEquals(logicHelpUrl, helpUrl);
	}

	@Test
	public void testSensorsHelpUrl() {
		onBrickAtPosition(1).onChildView(withId(R.id.brick_set_variable_edit_text))
				.perform(click());
		String helpUrl = onFormulaEditor()
				.performOpenCategory(FormulaEditorWrapper.Category.DEVICE)
				.getHelpUrl(CategoryListFragment.SENSOR_TAG, baseActivityTestRule.getActivity());
		assertEquals(sensorsHelpUrl, helpUrl);
	}

	@Test
	public void testObjectHelpUrl() {
		onBrickAtPosition(1).onChildView(withId(R.id.brick_set_variable_edit_text))
				.perform(click());
		String helpUrl = onFormulaEditor()
				.performOpenCategory(FormulaEditorWrapper.Category.OBJECT)
				.getHelpUrl(CategoryListFragment.OBJECT_TAG, baseActivityTestRule.getActivity());
		assertEquals(objectHelpUrl, helpUrl);
	}

	public static String getLanguage() {
		String language = "?language=";
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext());
		String languageTag = sharedPreferences.getString(LANGUAGE_TAG_KEY, "");
		Locale locale;

		if (languageTag.equals(DEVICE_LANGUAGE)) {
			locale = Locale.forLanguageTag(defaultSystemLanguage);
		} else {
			locale = Arrays.asList(LANGUAGE_TAGS).contains(languageTag)
					? Locale.forLanguageTag(languageTag)
					: Locale.forLanguageTag(defaultSystemLanguage);
		}
		return language + locale.getLanguage();
	}
}
