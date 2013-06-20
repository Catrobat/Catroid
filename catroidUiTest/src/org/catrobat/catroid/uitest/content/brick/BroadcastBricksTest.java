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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.jayway.android.robotium.solo.Solo;

public class BroadcastBricksTest extends ActivityInstrumentationTestCase2<ScriptActivity> {

	private Solo solo;
	private Project project;

	private static final int SECOND_BRICK_SPINNER_INDEX = 1;
	private static final int THIRD_BRICK_SPINNER_INDEX = 2;

	private static final int BROADCAST_RECEIVE_SPINNER_ID = R.id.brick_broadcast_receive_spinner;
	private static final int BROADCAST_SPINNER_ID = R.id.brick_broadcast_spinner;
	private static final int BROADCAST_WAIT_SPINNER_ID = R.id.brick_broadcast_wait_spinner;

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
		assertEquals("Incorrect number of bricks.", 3, UiTestUtils.getScriptListView(solo).getChildCount());
		assertEquals("Incorrect number of bricks.", 2, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 2, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof BroadcastBrick);
		assertTrue("Wrong Brick instance.", adapter.getItem(1) instanceof BroadcastBrick);

		String testString = "test";
		String testString2 = "test2";
		String testString3 = "test3";

		enterNewTextIntoSpinner(BROADCAST_RECEIVE_SPINNER_ID, testString);
		// just to get focus
		String brickBroadcastString = solo.getString(R.string.brick_broadcast);
		solo.clickOnText(brickBroadcastString);
		if (solo.searchText(solo.getString(R.string.brick_context_dialog_move_brick), true)) {
			solo.goBack();
		}

		assertEquals("Wrong selection", testString, ((Spinner) solo.getView(BROADCAST_RECEIVE_SPINNER_ID))
				.getSelectedItem().toString());
		assertNotSame("Wrong selection", testString, ((Spinner) solo.getView(BROADCAST_SPINNER_ID)).getSelectedItem()
				.toString());

		solo.pressSpinnerItem(SECOND_BRICK_SPINNER_INDEX, 1);
		solo.sleep(200);
		assertEquals("Wrong selection", testString, ((Spinner) solo.getView(BROADCAST_SPINNER_ID)).getSelectedItem()
				.toString());

		solo.pressSpinnerItem(THIRD_BRICK_SPINNER_INDEX, 1);
		solo.sleep(200);
		assertEquals("Wrong selection", testString, ((Spinner) solo.getView(BROADCAST_WAIT_SPINNER_ID))
				.getSelectedItem().toString());

		enterNewTextIntoSpinner(BROADCAST_SPINNER_ID, testString2);
		// just to get focus
		solo.clickOnText(brickBroadcastString);
		if (solo.searchText(solo.getString(R.string.brick_context_dialog_move_brick), true)) {
			solo.goBack();
		}

		checkIfSpinnerTextsCorrect(testString, testString2, testString);

		enterNewTextIntoSpinner(BROADCAST_WAIT_SPINNER_ID, testString3);
		// just to get focus
		solo.clickOnText(brickBroadcastString);
		if (solo.searchText(solo.getString(R.string.brick_context_dialog_move_brick), true)) {
			solo.goBack();
		}

		checkIfSpinnerTextsCorrect(testString, testString2, testString3);

		solo.pressSpinnerItem(SECOND_BRICK_SPINNER_INDEX, 1);
		solo.sleep(200);
		assertEquals("Wrong selection", testString3, ((Spinner) solo.getView(BROADCAST_SPINNER_ID)).getSelectedItem()
				.toString());

		solo.clickOnView(solo.getView(BROADCAST_RECEIVE_SPINNER_ID));
		solo.waitForText(solo.getString(R.string.new_broadcast_message));
		solo.clickInList(0);
		solo.waitForView(EditText.class);
		solo.goBack();
		solo.goBack();

		solo.clickOnView(solo.getView(BROADCAST_SPINNER_ID));
		solo.waitForText(solo.getString(R.string.new_broadcast_message));
		solo.clickInList(0);
		solo.waitForView(EditText.class);
		solo.goBack();
		solo.goBack();

		solo.clickOnView(solo.getView(BROADCAST_WAIT_SPINNER_ID));
		solo.waitForText(solo.getString(R.string.new_broadcast_message));
		solo.clickInList(0);
		solo.waitForView(EditText.class);
		solo.goBack();
		solo.goBack();

		checkIfSpinnerTextsCorrect(testString, testString3, testString3);

		solo.clickLongOnText(solo.getString(R.string.brick_broadcast_receive));
		solo.clickOnText(solo.getString(R.string.delete));

		UiTestUtils.addNewBrick(solo, R.string.brick_broadcast);
		assertEquals("Wrong selection", solo.getString(R.string.brick_broadcast_default_value),
				((Spinner) solo.getView(R.id.brick_broadcast_spinner)).getSelectedItem().toString());
	}

	public void testDeleteUnusedMessages() {
		ListView view = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) view.getAdapter();

		int childrenCount = ProjectManager.getInstance().getCurrentSprite().getScript(adapter.getScriptCount() - 1)
				.getBrickList().size();
		assertEquals("Incorrect number of bricks.", 3, UiTestUtils.getScriptListView(solo).getChildCount());
		assertEquals("Incorrect number of bricks.", 2, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 2, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof BroadcastBrick);
		assertTrue("Wrong Brick instance.", adapter.getItem(1) instanceof BroadcastBrick);

		String testString = "test";

		enterNewTextIntoSpinner(BROADCAST_RECEIVE_SPINNER_ID, testString);
		// just to get focus
		String brickBroadcastString = solo.getString(R.string.brick_broadcast);
		solo.clickOnText(brickBroadcastString);
		if (solo.searchText(solo.getString(R.string.brick_context_dialog_move_brick), true)) {
			solo.goBack();
		}

		assertEquals("Wrong selection", testString, ((Spinner) solo.getView(BROADCAST_RECEIVE_SPINNER_ID))
				.getSelectedItem().toString());

		solo.pressSpinnerItem(SECOND_BRICK_SPINNER_INDEX, 2);
		solo.pressSpinnerItem(THIRD_BRICK_SPINNER_INDEX, 2);

		assertEquals("Wrong selection", testString, ((Spinner) solo.getView(BROADCAST_SPINNER_ID)).getSelectedItem()
				.toString());
		assertEquals("Wrong selection", testString, ((Spinner) solo.getView(BROADCAST_WAIT_SPINNER_ID))
				.getSelectedItem().toString());
		solo.sleep(500);

		UiTestUtils.clickOnHomeActionBarButton(solo);
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		checkIfSpinnerTextsCorrect(testString, testString, testString);

		Spinner broadcastReceiveSpinner = (Spinner) solo.getView(BROADCAST_RECEIVE_SPINNER_ID);
		assertEquals("broadcastReceiveSpinner has not the correct number of elements", 2,
				broadcastReceiveSpinner.getCount());
		for (int itemIndex = 0; itemIndex < broadcastReceiveSpinner.getCount(); ++itemIndex) {
			assertNotSame(solo.getString(R.string.brick_broadcast_default_value) + " is still in adapter",
					solo.getString(R.string.brick_broadcast_default_value),
					broadcastReceiveSpinner.getItemAtPosition(itemIndex));
		}

		Spinner broadcastSpinner = (Spinner) solo.getView(BROADCAST_SPINNER_ID);
		assertEquals("broadcastSpinner has not the correct number of elements", 2, broadcastSpinner.getCount());
		for (int itemIndex = 0; itemIndex < broadcastSpinner.getCount(); ++itemIndex) {
			assertNotSame(solo.getString(R.string.brick_broadcast_default_value) + " is still in adapter",
					solo.getString(R.string.brick_broadcast_default_value),
					broadcastSpinner.getItemAtPosition(itemIndex));
		}

		Spinner broadcastWaitSpinner = (Spinner) solo.getView(BROADCAST_WAIT_SPINNER_ID);
		assertEquals("broadcastWaitSpinner has not the correct number of elements", 2, broadcastWaitSpinner.getCount());
		for (int itemIndex = 0; itemIndex < broadcastWaitSpinner.getCount(); ++itemIndex) {
			assertNotSame(solo.getString(R.string.brick_broadcast_default_value) + " is still in adapter",
					solo.getString(R.string.brick_broadcast_default_value),
					broadcastWaitSpinner.getItemAtPosition(itemIndex));
		}
	}

	private void checkIfSpinnerTextsCorrect(String firstTextSpinner, String secondTextSpinner, String thirdTextSpinner) {
		assertEquals("Wrong selection", firstTextSpinner, ((Spinner) solo.getView(BROADCAST_RECEIVE_SPINNER_ID))
				.getSelectedItem().toString());
		assertEquals("Wrong selection", secondTextSpinner, ((Spinner) solo.getView(BROADCAST_SPINNER_ID))
				.getSelectedItem().toString());
		assertEquals("Wrong selection", thirdTextSpinner, ((Spinner) solo.getView(BROADCAST_WAIT_SPINNER_ID))
				.getSelectedItem().toString());
	}

	private void enterNewTextIntoSpinner(int spinnerId, String text) {
		solo.clickOnView(solo.getView(spinnerId));
		solo.waitForText(solo.getString(R.string.new_broadcast_message));
		solo.clickInList(0);
		solo.waitForView(EditText.class);
		solo.enterText(0, text);
		solo.goBack();
		solo.sleep(200);
		solo.clickOnText(solo.getString(R.string.ok));
		solo.sleep(300);
		solo.waitForView(solo.getView(spinnerId));
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		String broadcastMessage = "message 1";
		Script script = new BroadcastScript(sprite, broadcastMessage);
		BroadcastBrick broadcastBrick = new BroadcastBrick(sprite, broadcastMessage);
		BroadcastWaitBrick broadcastWaitBrick = new BroadcastWaitBrick(sprite, broadcastMessage);
		script.addBrick(broadcastBrick);
		script.addBrick(broadcastWaitBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
