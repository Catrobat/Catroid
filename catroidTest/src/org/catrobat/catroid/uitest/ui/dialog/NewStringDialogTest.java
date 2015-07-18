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
package org.catrobat.catroid.uitest.ui.dialog;

import android.widget.EditText;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.NoteBrick;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.dialogs.NewStringDialog;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class NewStringDialogTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private static final int FORMULA_EDITOR_EDIT_TEXT_ID = 0;
	private static final String NOTE = "some note about yummy cookies ;)";
	private static final String EDIT_TEXT_STRING = "my edit text string";

	public NewStringDialogTest() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		solo.clickOnView(solo.getView(R.id.brick_note_edit_text));
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_string));
	}

	public void testDialogCreation() {
		assertTrue(NewStringDialog.class.getSimpleName() + " did not load under 5 seconds!",
				solo.waitForFragmentByTag(NewStringDialog.DIALOG_FRAGMENT_TAG, 5000));
	}

	public void testPositiveButtonAndCreationOfNewString() {
		EditText stringEditText = (EditText) solo.getView(R.id.formula_editor_string_name_edit_text);
		solo.enterText(stringEditText, EDIT_TEXT_STRING);
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		assertEquals("Wrong string in Formula Editor edit text!", "\'" + EDIT_TEXT_STRING + "\' ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText().toString());
	}

	public void testNegativeButton() {
		solo.clickOnButton(solo.getString(R.string.cancel_button));
		assertTrue("New " + NewStringDialog.class.getSimpleName() + " should be closed!", solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG));
		assertEquals("Wrong string in Formula Editor edit text!", "\'" + NOTE + "\' ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText().toString());
	}

	private void createProject() {
		Project project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript();
		script.addBrick(new NoteBrick(NOTE));

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
