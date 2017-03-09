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
package org.catrobat.catroid.uitest.content.brick;

import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.SayForBubbleBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.List;

public class SayForBubbleTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private Project project;
	private Sprite sprite;
	private SayForBubbleBrick sayForBubbleBrick;

	public SayForBubbleTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	public void testBrick() {
		double duration = 1.5;
		String text = "say something";

		UiTestUtils.insertValueViaFormulaEditor(solo, R.id.brick_say_for_bubble_edit_text_duration, duration);

		insertText(text);

		TextView textViewDuration = ((TextView) solo.getView(R.id.brick_say_for_bubble_edit_text_duration));
		TextView textViewText = ((TextView) solo.getView(R.id.brick_say_for_bubble_edit_text_text));

		assertEquals("Text not updated within FormulaEditor", duration,
				Double.parseDouble(textViewDuration.getText().toString().replace(',', '.')));
		assertEquals("Text not updated within FormulaEditor", "\'" + text + "\' ", textViewText.getText().toString());

		ProjectManager manager = ProjectManager.getInstance();
		List<Brick> brickList = manager.getCurrentSprite().getScript(0).getBrickList();
		SayForBubbleBrick sayForBrick = (SayForBubbleBrick) brickList.get(0);

		Formula formula = sayForBrick.getFormulaWithBrickField(Brick.BrickField.DURATION_IN_SECONDS);
		try {
			float temp = formula.interpretFloat(sprite);
			assertEquals("Wrong duration input in Say for brick", Math.round(duration * 1000), Math.round(temp
					* 1000));
		} catch (InterpretationException interpretationException) {
			fail("Wrong duration input in Say for brick");
		}

		formula = sayForBrick.getFormulaWithBrickField(Brick.BrickField.STRING);
		try {
			String temp = formula.interpretString(sprite);
			assertEquals("Wrong text input in Say for brick", text, temp);
		} catch (InterpretationException interpretationException) {
			fail("Wrong text input in Say for brick");
		}

		UiTestUtils.insertValueViaFormulaEditor(solo, R.id.brick_say_for_bubble_edit_text_duration, 1);
		TextView secondsTextView = (TextView) solo.getView(R.id.brick_say_for_bubble_seconds_label);
		assertTrue(
				"Specifier hasn't changed from plural to singular",
				secondsTextView.getText().equals(
						secondsTextView.getResources().getQuantityString(R.plurals.second_plural, 1))
		);

		UiTestUtils.insertValueViaFormulaEditor(solo, R.id.brick_say_for_bubble_edit_text_duration, 5);
		secondsTextView = (TextView) solo.getView(R.id.brick_say_for_bubble_seconds_label);
		assertTrue(
				"Specifier hasn't changed from singular to plural",
				secondsTextView.getText().equals(
						secondsTextView.getResources().getQuantityString(R.plurals.second_plural, 5))
		);

		UiTestUtils.clickOnPlayButton(solo);
		solo.waitForActivity(StageActivity.class);
		solo.sleep(2000);
		assertNotNull("Bubble was not shown", StageActivity.stageListener.getBubbleActorForSprite(sprite));
		solo.sleep(5000);
		assertNull("Bubble was not hidden", StageActivity.stageListener.getBubbleActorForSprite(sprite));
	}

	private void insertText(String text) {
		solo.clickOnView(solo.getView(R.id.brick_say_for_bubble_edit_text_text));
		solo.sleep(200);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_string));
		solo.sleep(200);
		solo.clickOnEditText(1);
		solo.clearEditText(1);
		solo.enterText(1, text);
		solo.sleep(200);
		solo.clickOnText(solo.getString(R.string.ok), 2);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));
		solo.sleep(200);
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		sprite = new SingleSprite("cat");
		Script script = new StartScript();
		sayForBubbleBrick = new SayForBubbleBrick();
		script.addBrick(sayForBubbleBrick);

		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
