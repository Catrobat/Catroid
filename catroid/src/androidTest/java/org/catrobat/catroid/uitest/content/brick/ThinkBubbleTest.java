/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
package org.catrobat.catroid.uitest.content.brick;

import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ThinkBubbleBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.List;

public class ThinkBubbleTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private Project project;
	private Sprite sprite;
	private ThinkBubbleBrick thinkBubbleBrick;

	public ThinkBubbleTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	public void testBrick() {
		String text = "think about something";

		insertText(text);

		TextView textViewText = ((TextView) solo.getView(R.id.brick_think_bubble_edit_text));

		assertEquals("Text not updated within FormulaEditor", "\'" + text + "\' ", textViewText.getText().toString());

		ProjectManager manager = ProjectManager.getInstance();
		List<Brick> brickList = manager.getCurrentSprite().getScript(0).getBrickList();
		ThinkBubbleBrick thinkBrick = (ThinkBubbleBrick) brickList.get(0);

		Formula formula = thinkBrick.getFormulaWithBrickField(Brick.BrickField.STRING);
		try {
			String temp = formula.interpretString(sprite);
			assertEquals("Wrong text input in Think for brick", text, temp);
		} catch (InterpretationException interpretationException) {
			fail("Wrong text input in Think for brick");
		}

		UiTestUtils.clickOnPlayButton(solo);
		solo.waitForActivity(StageActivity.class);
		solo.sleep(2000);
		assertNotNull("Bubble was not shown", StageActivity.stageListener.getBubbleActorForSprite(sprite));
	}

	private void insertText(String text) {
		solo.clickOnView(solo.getView(R.id.brick_think_bubble_edit_text));
		solo.sleep(200);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_string));
		solo.sleep(200);
		solo.clickOnEditText(2);
		solo.clearEditText(2);
		solo.enterText(2, text);
		solo.sleep(200);
		solo.clickOnText(solo.getString(R.string.ok), 2);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));
		solo.sleep(200);
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		sprite = new Sprite("cat");
		Script script = new StartScript();
		thinkBubbleBrick = new ThinkBubbleBrick();
		script.addBrick(thinkBubbleBrick);

		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
