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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.SpeakBrick;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;

public class SpeakBrickTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private Project project;
	private SpeakBrick speakBrick;
	private Sprite sprite;

	private String testString = "test";

	public SpeakBrickTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	public void testSpeakBrick() {
		solo.sleep(200);    // mal schaun
		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) dragDropListView.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();
		int groupCount = adapter.getScriptCount();

		assertEquals("Incorrect number of bricks.", 2, dragDropListView.getChildCount());
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), adapter.getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist.", solo.getText(solo.getString(R.string.brick_speak)));

		UiTestUtils.testBrickWithFormulaEditor(sprite, solo, R.id.brick_speak_edit_text, testString, Brick.BrickField.SPEAK, speakBrick);
		try {
			String brickText = speakBrick.getFormulaWithBrickField(Brick.BrickField.SPEAK).interpretString(sprite);
			assertEquals("Wrong text in field.", testString, brickText);
		} catch (InterpretationException interpretationException) {
			fail("Wrong text in field.");
		}

		UiTestUtils.testBrickWithFormulaEditor(sprite, solo, R.id.brick_speak_edit_text, "", Brick.BrickField.SPEAK, speakBrick);

		try {
			String brickText = speakBrick.getFormulaWithBrickField(Brick.BrickField.SPEAK).interpretString(sprite);
			assertEquals("Wrong text in field.", "", brickText);
		} catch (InterpretationException interpretationException) {
			fail("Wrong text in field.");
		}
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		sprite = new Sprite("cat");
		Script script = new StartScript();
		speakBrick = new SpeakBrick("");

		script.addBrick(speakBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
