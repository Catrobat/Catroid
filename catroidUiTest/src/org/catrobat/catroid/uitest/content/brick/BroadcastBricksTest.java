/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.content.brick;

import java.util.ArrayList;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.widget.ListView;

import com.jayway.android.robotium.solo.Solo;

public class BroadcastBricksTest extends ActivityInstrumentationTestCase2<ScriptActivity> {

	private Solo solo;
	private Project project;

	private static final int FIRST_BRICK_SPINNER_INDEX = 0;
	private static final int SECOND_BRICK_SPINNER_INDEX = 1;
	private static final int THIRD_BRICK_SPINNER_INDEX = 2;

	public BroadcastBricksTest() {
		super(ScriptActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	@Smoke
	public void testBroadcastBricks() {
		ListView view = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) view.getAdapter();

		int childrenCount = ProjectManager.getInstance().getCurrentSprite().getScript(adapter.getScriptCount() - 1)
				.getBrickList().size();
		assertEquals("Incorrect number of bricks.", 3 + 1, UiTestUtils.getScriptListView(solo).getChildCount()); // don't forget the footer
		assertEquals("Incorrect number of bricks.", 2, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 2, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof BroadcastBrick);
		assertTrue("Wrong Brick instance.", adapter.getItem(1) instanceof BroadcastBrick);

		String testString = "test";
		String testString2 = "test2";
		String testString3 = "test3";

		enterNewTextIntoSpinner(1, testString);
		// just to get focus
		String brickBroadcastString = solo.getString(R.string.brick_broadcast);
		solo.clickOnText(brickBroadcastString);
		if (solo.searchText(solo.getString(R.string.brick_context_dialog_move_brick), true)) {
			solo.goBack();
		}

		assertEquals("Wrong selection", testString, (String) solo.getCurrentSpinners().get(FIRST_BRICK_SPINNER_INDEX)
				.getSelectedItem());
		assertNotSame("Wrong selection", testString, solo.getCurrentSpinners().get(SECOND_BRICK_SPINNER_INDEX)
				.getSelectedItem());

		solo.pressSpinnerItem(SECOND_BRICK_SPINNER_INDEX, 2);
		solo.sleep(200);
		assertEquals("Wrong selection", testString, (String) solo.getCurrentSpinners().get(SECOND_BRICK_SPINNER_INDEX)
				.getSelectedItem());

		solo.pressSpinnerItem(THIRD_BRICK_SPINNER_INDEX, 2);
		solo.sleep(200);
		assertEquals("Wrong selection", testString, (String) solo.getCurrentSpinners().get(THIRD_BRICK_SPINNER_INDEX)
				.getSelectedItem());

		enterNewTextIntoSpinner(2, testString2);
		// just to get focus
		solo.clickOnText(brickBroadcastString);
		if (solo.searchText(solo.getString(R.string.brick_context_dialog_move_brick), true)) {
			solo.goBack();
		}

		checkIfSpinnerTextsCorrect(testString, testString2, testString);

		enterNewTextIntoSpinner(3, testString3);
		// just to get focus
		solo.clickOnText(brickBroadcastString);
		if (solo.searchText(solo.getString(R.string.brick_context_dialog_move_brick), true)) {
			solo.goBack();
		}

		checkIfSpinnerTextsCorrect(testString, testString2, testString3);

		solo.pressSpinnerItem(SECOND_BRICK_SPINNER_INDEX, 4);
		solo.sleep(200);
		assertEquals("Wrong selection", testString3, (String) solo.getCurrentSpinners().get(SECOND_BRICK_SPINNER_INDEX)
				.getSelectedItem());
	}

	private void checkIfSpinnerTextsCorrect(String firstTextSpinner, String secondTextSpinner, String thirdTextSpinner) {
		assertEquals("Wrong selection", firstTextSpinner,
				(String) solo.getCurrentSpinners().get(FIRST_BRICK_SPINNER_INDEX).getSelectedItem());
		assertEquals("Wrong selection", secondTextSpinner,
				(String) solo.getCurrentSpinners().get(SECOND_BRICK_SPINNER_INDEX).getSelectedItem());
		assertEquals("Wrong selection", thirdTextSpinner,
				(String) solo.getCurrentSpinners().get(THIRD_BRICK_SPINNER_INDEX).getSelectedItem());
	}

	private void enterNewTextIntoSpinner(int spinnerItemIndex, String text) {
		solo.clickOnText(solo.getString(R.string.new_broadcast_message), spinnerItemIndex);
		solo.clearEditText(0);
		solo.enterText(0, text);
		solo.sleep(200);
		solo.sendKey(Solo.ENTER);
		solo.sleep(300);
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new BroadcastScript(sprite);
		BroadcastBrick broadcastBrick = new BroadcastBrick(sprite);
		BroadcastWaitBrick broadcastWaitBrick = new BroadcastWaitBrick(sprite);
		script.addBrick(broadcastBrick);
		script.addBrick(broadcastWaitBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
