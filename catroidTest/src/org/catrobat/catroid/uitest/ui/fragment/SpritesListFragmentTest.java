/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.uitest.ui.fragment;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class SpritesListFragmentTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	public SpritesListFragmentTest() {
		super(MainMenuActivity.class);
	}

	private static final String LOCAL_VARIABLE_NAME = "test_local";
	private static final double LOCAL_VARIABLE_VALUE = 0xDEADBEEF;

	private static final String GLOBAL_VARIABLE_NAME = "test_global";
	private static final double GLOBAL_VARIABLE_VALUE = 0xC0FFEE;

	private static final String SPRITE_NAME = "testSprite";

	private Sprite sprite;
	private Project project;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		UiTestUtils.createTestProject();

		project = ProjectManager.getInstance().getCurrentProject();
		sprite = new Sprite(SPRITE_NAME);
		project.addSprite(sprite);
		project.getUserVariables().addSpriteUserVariableToSprite(sprite, LOCAL_VARIABLE_NAME);
		project.getUserVariables().getUserVariable(LOCAL_VARIABLE_NAME, sprite).setValue(LOCAL_VARIABLE_VALUE);

		project.getUserVariables().addProjectUserVariable(GLOBAL_VARIABLE_NAME);
		project.getUserVariables().getUserVariable(GLOBAL_VARIABLE_NAME, null).setValue(GLOBAL_VARIABLE_VALUE);

		ProjectManager.getInstance().setProject(project);
	}

	public void testLocalVariablesWhenSpriteCopiedFromSpritesListFragment() {
		solo.clickOnText(solo.getString(R.string.main_menu_continue));
		solo.clickLongOnText(SPRITE_NAME);
		solo.clickOnText(solo.getString(R.string.copy));

		String copiedSpriteName = SPRITE_NAME + solo.getString(R.string.copy_sprite_name_suffix);
		solo.waitForText(copiedSpriteName);
		assertTrue(copiedSpriteName + " not found!", solo.searchText(copiedSpriteName));

		Sprite clonedSprite = null;
		for (Sprite tempSprite : project.getSpriteList()) {
			if (tempSprite.getName().equals(copiedSpriteName)) {
				clonedSprite = tempSprite;
			}
		}

		if (clonedSprite == null) {
			fail("no cloned sprite in project");
		}

		List<UserVariable> userVariableList = project.getUserVariables().getOrCreateVariableListForSprite(clonedSprite);
		Set<String> hashSet = new HashSet<String>();
		for (UserVariable userVariable : userVariableList) {
			assertTrue("Variable already exists", hashSet.add(userVariable.getName()));
		}
	}

	public void testSelectAllActionModeButton() {
		UiTestUtils.clickOnText(solo, solo.getString(R.string.main_menu_continue));
		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());

		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy, getActivity());
		assertTrue("Select All is not shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), true));

		UiTestUtils.clickOnText(solo, selectAll);
		assertFalse("Select All is still shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), false));

		solo.clickOnCheckBox(0);
		assertTrue("Select All is not shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), true));

		solo.clickOnCheckBox(0);
		assertFalse("Select All is still shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), false));

		solo.goBack();

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete, getActivity());
		assertTrue("Select All is not shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), true));

		UiTestUtils.clickOnText(solo, selectAll);
		assertFalse("Select All is still shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), false));

		solo.clickOnCheckBox(0);
		assertTrue("Select All is not shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), true));

		solo.clickOnCheckBox(0);
		assertFalse("Select All is still shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), false));
	}
}
