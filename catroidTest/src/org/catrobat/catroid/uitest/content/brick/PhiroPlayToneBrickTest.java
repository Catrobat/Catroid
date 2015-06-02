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
import android.widget.Spinner;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.PhiroPlayToneBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;

public class PhiroPlayToneBrickTest extends BaseActivityInstrumentationTestCase<ScriptActivity> {
	private static final int TONE_DURATION = 30;
	private static final int TONE_DURATION_INITIALLY = -70;

	private Project project;
	private PhiroPlayToneBrick toneBrick;

	public PhiroPlayToneBrickTest() {
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

	public void testPhiroMotorActionBrick() {
		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) dragDropListView.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();
		int groupCount = adapter.getScriptCount();

		assertEquals("Incorrect number of bricks.", 2, dragDropListView.getChildCount());
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), adapter.getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist.", solo.getText(solo.getString(R.string.phiro_play_tone)));
		assertNotNull("TextView does not exist.", solo.getText(solo.getString(R.string.phiro_tone_duration)));

		UiTestUtils.testBrickWithFormulaEditor(solo, ProjectManager.getInstance().getCurrentSprite(),
				R.id.brick_phiro_play_tone_duration_edit_text, TONE_DURATION, Brick.BrickField.PHIRO_DURATION_IN_SECONDS, toneBrick);

		String[] tones = getActivity().getResources().getStringArray(R.array.brick_phiro_select_tone_spinner);
		assertTrue("Spinner items list too short!", tones.length == 7);

		int phiroSpinnerIndex = 0;

		Spinner currentSpinner = solo.getCurrentViews(Spinner.class).get(phiroSpinnerIndex);
		solo.pressSpinnerItem(phiroSpinnerIndex, 7);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		assertEquals("Wrong item in spinner!", tones[6], currentSpinner.getSelectedItem());
		solo.pressSpinnerItem(phiroSpinnerIndex, -1);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		assertEquals("Wrong item in spinner!", tones[5], currentSpinner.getSelectedItem());
		solo.pressSpinnerItem(phiroSpinnerIndex, -1);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		assertEquals("Wrong item in spinner!", tones[4], currentSpinner.getSelectedItem());
		solo.pressSpinnerItem(phiroSpinnerIndex, -1);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		assertEquals("Wrong item in spinner!", tones[3], currentSpinner.getSelectedItem());
		solo.pressSpinnerItem(phiroSpinnerIndex, -1);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		assertEquals("Wrong item in spinner!", tones[2], currentSpinner.getSelectedItem());
		solo.pressSpinnerItem(phiroSpinnerIndex, -1);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		assertEquals("Wrong item in spinner!", tones[1], currentSpinner.getSelectedItem());
		solo.pressSpinnerItem(phiroSpinnerIndex, -1);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		assertEquals("Wrong item in spinner!", tones[0], currentSpinner.getSelectedItem());
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript();

		toneBrick = new PhiroPlayToneBrick(PhiroPlayToneBrick.Tone.DO, TONE_DURATION_INITIALLY);

		script.addBrick(toneBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
