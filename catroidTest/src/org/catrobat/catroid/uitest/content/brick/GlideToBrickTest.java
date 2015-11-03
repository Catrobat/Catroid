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
package org.catrobat.catroid.uitest.content.brick;

import android.os.Build;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.List;

public class GlideToBrickTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private Project project;
	private Sprite sprite;

	public GlideToBrickTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	public void testNumberInput() {
		double duration = 1.5;
		int xPosition = 123;
		int yPosition = 567;

		// This is a hack. On my device and on Jenkins, the click on the first EditText could not be completed.
		// Doing it manually here
		// #2 Improved hack to have it working on newer devices too, still no solution or anything
		if (Build.VERSION.SDK_INT < 15) {
			solo.clickOnView(solo.getView(R.id.brick_glide_to_edit_text_duration));
		}

		UiTestUtils.insertValueViaFormulaEditor(solo, R.id.brick_glide_to_edit_text_duration, duration);
		UiTestUtils.insertValueViaFormulaEditor(solo, R.id.brick_glide_to_edit_text_x, xPosition);
		UiTestUtils.insertValueViaFormulaEditor(solo, R.id.brick_glide_to_edit_text_y, yPosition);

		TextView textViewDuration = ((TextView) solo.getView(R.id.brick_glide_to_edit_text_duration));
		TextView textViewX = ((TextView) solo.getView(R.id.brick_glide_to_edit_text_x));
		TextView textViewY = ((TextView) solo.getView(R.id.brick_glide_to_edit_text_y));

		assertEquals("Text not updated within FormulaEditor", duration,
				Double.parseDouble(textViewDuration.getText().toString().replace(',', '.')));
		assertEquals("Text not updated within FormulaEditor", xPosition,
				Integer.parseInt(textViewX.getText().toString().substring(0, 3)));
		assertEquals("Text not updated within FormulaEditor", yPosition,
				Integer.parseInt(textViewY.getText().toString().substring(0, 3)));

		ProjectManager manager = ProjectManager.getInstance();
		List<Brick> brickList = manager.getCurrentSprite().getScript(0).getBrickList();
		GlideToBrick glideToBrick = (GlideToBrick) brickList.get(0);

		Formula formula = glideToBrick.getFormulaWithBrickField(Brick.BrickField.DURATION_IN_SECONDS);
		try {
			float temp = formula.interpretFloat(sprite);
			assertEquals("Wrong duration input in Glide to brick", Math.round(duration * 1000), Math.round(temp * 1000));
		} catch (InterpretationException interpretationException) {
			fail("Wrong duration input in Glide to brick");
		}

		formula = glideToBrick.getFormulaWithBrickField(Brick.BrickField.X_DESTINATION);
		try {
			int temp = formula.interpretInteger(sprite);
			assertEquals("Wrong x input in Glide to brick", xPosition, temp);
		} catch (InterpretationException interpretationException) {
			fail("Wrong xDestination input in Glide to brick");
		}

		formula = glideToBrick.getFormulaWithBrickField(Brick.BrickField.Y_DESTINATION);
		try {
			int temp = formula.interpretInteger(sprite);
			assertEquals("Wrong y input in Glide to brick", yPosition, temp);
		} catch (InterpretationException interpretationException) {
			fail("Wrong yDestination input in Glide to brick");
		}

		UiTestUtils.insertValueViaFormulaEditor(solo, R.id.brick_glide_to_edit_text_duration, 1);
		TextView secondsTextView = (TextView) solo.getView(R.id.brick_glide_to_seconds_text_view);
		assertTrue(
				"Specifier hasn't changed from plural to singular",
				secondsTextView.getText().equals(
						secondsTextView.getResources().getQuantityString(R.plurals.second_plural, 1))
		);

		UiTestUtils.insertValueViaFormulaEditor(solo, R.id.brick_glide_to_edit_text_duration, 5);
		secondsTextView = (TextView) solo.getView(R.id.brick_glide_to_seconds_text_view);
		assertTrue(
				"Specifier hasn't changed from singular to plural",
				secondsTextView.getText().equals(
						secondsTextView.getResources().getQuantityString(R.plurals.second_plural, 5))
		);
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		sprite = new Sprite("cat");
		Script script = new StartScript();
		script.addBrick(new GlideToBrick(0, 0, 0));

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
