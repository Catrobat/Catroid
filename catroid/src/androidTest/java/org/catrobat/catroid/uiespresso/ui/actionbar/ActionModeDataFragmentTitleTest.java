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

package org.catrobat.catroid.uiespresso.ui.actionbar;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openContextualActionModeOverflowMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorDataListWrapper.onDataList;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;
import static org.catrobat.catroid.uiespresso.ui.actionbar.utils.ActionModeWrapper.onActionMode;

public class ActionModeDataFragmentTitleTest {
	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<SpriteActivity>(SpriteActivity.class,
			SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
		createProject();
		baseActivityTestRule.launchActivity();

		onView(withId(R.id.brick_change_size_by_edit_text)).perform(click());
		onFormulaEditor().performOpenDataFragment();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void actionModeDataFragmentTitleTest() {
		openContextualActionModeOverflowMenu();
		onView(withText(R.string.delete))
				.perform(click());

		onDataList().onVariableAtPosition(0)
				.performCheckItem();
		onDataList().onVariableAtPosition(1)
				.performCheckItem();
		onDataList().onListAtPosition(2)
				.performCheckItem();
		onDataList().onListAtPosition(2)
				.performCheckItem();

		onActionMode()
				.checkTitleMatches(String.format(UiTestUtils.getQuantitiyString(R.plurals
						.am_delete_user_data_items_title, 2), 2));
	}

	private void createProject() {
		Script script = BrickTestUtils.createProjectAndGetStartScript("ActionModeDataFragmentTitleTest");
		script.addBrick(new ChangeSizeByNBrick(0));

		DataContainer dataContainer = ProjectManager.getInstance().getCurrentScene().getDataContainer();
		dataContainer.addProjectUserVariable("var1");
		dataContainer.addProjectUserVariable("var2");
		dataContainer.addProjectUserList("list1");
		dataContainer.addProjectUserList("list2");
	}
}
