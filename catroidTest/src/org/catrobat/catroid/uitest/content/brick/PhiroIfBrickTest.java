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
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.PhiroIfLogicBeginBrick;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;

public class PhiroIfBrickTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private Project project;
	private PhiroIfLogicBeginBrick ifBrick;

	public PhiroIfBrickTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	public void testIfBrick() {
		ListView view = UiTestUtils.getScriptListView(solo);
		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) view.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();

		String[] motors = getActivity().getResources().getStringArray(R.array.brick_phiro_select_sensor_spinner);
		assertTrue("Spinner items list too short!", motors.length == 6);

		int phiroSpinnerIndex = 0;

		Spinner currentSpinner = solo.getCurrentViews(Spinner.class).get(phiroSpinnerIndex);
		solo.pressSpinnerItem(phiroSpinnerIndex, 6);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		assertEquals("Wrong item in spinner!", motors[5], currentSpinner.getSelectedItem());
		solo.pressSpinnerItem(phiroSpinnerIndex, -1);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		assertEquals("Wrong item in spinner!", motors[4], currentSpinner.getSelectedItem());
		solo.pressSpinnerItem(phiroSpinnerIndex, -1);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		assertEquals("Wrong item in spinner!", motors[3], currentSpinner.getSelectedItem());
		solo.pressSpinnerItem(phiroSpinnerIndex, -1);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		assertEquals("Wrong item in spinner!", motors[2], currentSpinner.getSelectedItem());
		solo.pressSpinnerItem(phiroSpinnerIndex, -1);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		assertEquals("Wrong item in spinner!", motors[1], currentSpinner.getSelectedItem());
		solo.pressSpinnerItem(phiroSpinnerIndex, -1);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		assertEquals("Wrong item in spinner!", motors[0], currentSpinner.getSelectedItem());

		assertEquals("Incorrect number of bricks.", 6, dragDropListView.getChildCount()); // don't forget the footer
		assertEquals("Incorrect number of bricks.", 0, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 4, projectBrickList.size());

		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof PhiroIfLogicBeginBrick);
		assertNotNull("TextView does not exist", solo.getText(getActivity().getString(R.string.brick_phiro_sensor_begin)));
	}

	public void testStrings() {
		solo.sleep(100);

		boolean isFound = solo.searchText(solo.getString(R.string.brick_phiro_sensor_second_part));
		assertTrue("String: " + getActivity().getString(R.string.brick_phiro_sensor_second_part) + " not found!", isFound);

		isFound = solo.searchText(solo.getString(R.string.brick_phiro_sensor_begin));
		assertTrue("String: " + getActivity().getString(R.string.brick_phiro_sensor_begin) + " not found!", isFound);
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript();
		ifBrick = new PhiroIfLogicBeginBrick();
		IfLogicElseBrick ifElseBrick = new IfLogicElseBrick(ifBrick);
		IfLogicEndBrick ifEndBrick = new IfLogicEndBrick(ifElseBrick, ifBrick);
		ifBrick.setIfElseBrick(ifElseBrick);
		ifBrick.setIfEndBrick(ifEndBrick);

		script.addBrick(ifBrick);
		script.addBrick(new ChangeYByNBrick(-10));
		script.addBrick(ifElseBrick);
		script.addBrick(ifEndBrick);

		sprite.addScript(script);
		sprite.addScript(new StartScript());
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
