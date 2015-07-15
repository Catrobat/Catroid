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

import android.widget.ListView;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.GoNStepsBackBrick;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;

public class GoNStepsBackTest extends BaseActivityInstrumentationTestCase<ScriptActivity> {
	private static final int STEPS_TO_GO_BACK = 17;

	private Project project;
	private GoNStepsBackBrick goNStepsBackBrick;

	public GoNStepsBackTest() {
		super(ScriptActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		// normally super.setUp should be called first
		// but kept the test failing due to view is null
		// when starting in ScriptActivity
		createProject();
		super.setUp();
	}

	public void testGoNStepsBackBrick() {
		solo.sleep(300);
		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) dragDropListView.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();
		int groupCount = adapter.getScriptCount();

		assertEquals("Incorrect number of bricks.", 2, dragDropListView.getChildCount());
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), adapter.getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist.", solo.getText(solo.getString(R.string.brick_go_back)));

		UiTestUtils.insertValueViaFormulaEditor(solo, R.id.brick_go_back_edit_text, STEPS_TO_GO_BACK);

		try {
			assertEquals("Wrong text in field.", STEPS_TO_GO_BACK,
					goNStepsBackBrick.getFormulaWithBrickField(Brick.BrickField.STEPS).interpretDouble(null).intValue());
		} catch (InterpretationException interpretationException) {
			fail("Wrong text in field.");
		}

		assertEquals(
				"Value in Brick is not updated.",
				(double) STEPS_TO_GO_BACK,
				Double.valueOf(((TextView) solo.getView(R.id.brick_go_back_edit_text)).getText().toString()
						.replace(',', '.'))
		);

		UiTestUtils.insertValueViaFormulaEditor(solo, R.id.brick_go_back_edit_text, 1);
		TextView secondsTextView = (TextView) solo.getView(R.id.brick_go_back_layers_text_view);
		assertTrue(
				"Specifier hasn't changed from plural to singular",
				secondsTextView.getText().equals(
						dragDropListView.getResources().getQuantityString(R.plurals.brick_go_back_layer_plural, 1))
		);

		UiTestUtils.insertValueViaFormulaEditor(solo, R.id.brick_go_back_edit_text, 2);
		secondsTextView = (TextView) solo.getView(R.id.brick_go_back_layers_text_view);
		assertTrue(
				"Specifier hasn't changed from singular to plural",
				secondsTextView.getText().equals(
						dragDropListView.getResources().getQuantityString(R.plurals.brick_go_back_layer_plural, 2))
		);
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript();
		goNStepsBackBrick = new GoNStepsBackBrick(0);
		script.addBrick(goNStepsBackBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
