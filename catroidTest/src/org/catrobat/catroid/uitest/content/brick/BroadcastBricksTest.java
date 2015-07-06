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

import android.util.SparseArray;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.List;

public class BroadcastBricksTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private Project project;
	private Sprite sprite;

	private final SparseArray<String> expectedSpinnerText = new SparseArray<String>();
	private String defaultBroadcastMessage = "";

	private final int broadcastReceiverSpinnerId = R.id.brick_broadcast_receive_spinner;
	private final int broadcastSpinnerId = R.id.brick_broadcast_spinner;
	private final int broadcastWaitSpinnerId = R.id.brick_broadcast_wait_spinner;

	public BroadcastBricksTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		defaultBroadcastMessage = solo.getString(R.string.brick_broadcast_default_value);
		expectedSpinnerText.put(broadcastReceiverSpinnerId, defaultBroadcastMessage);
		expectedSpinnerText.put(broadcastSpinnerId, defaultBroadcastMessage);
		expectedSpinnerText.put(broadcastWaitSpinnerId, defaultBroadcastMessage);
		createProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	@Device
	public void testBroadcastBricks() {
		checkSetupBricks();

		final String firstBroadcastMessage = "First";
		final String secondBroadcastMessage = "Second";
		final String thirdBroadcastMessage = "Third";

		enterNewTextIntoSpinner(broadcastReceiverSpinnerId, firstBroadcastMessage);
		pressSpinnerItem(broadcastSpinnerId, firstBroadcastMessage);
		pressSpinnerItem(broadcastWaitSpinnerId, firstBroadcastMessage);
		enterNewTextIntoSpinner(broadcastSpinnerId, secondBroadcastMessage);
		enterNewTextIntoSpinner(broadcastWaitSpinnerId, thirdBroadcastMessage);

		pressSpinnerItem(broadcastSpinnerId, thirdBroadcastMessage);

		dismissEnterNewTextIntoSpinner(broadcastReceiverSpinnerId);
		dismissEnterNewTextIntoSpinner(broadcastSpinnerId);
		dismissEnterNewTextIntoSpinner(broadcastWaitSpinnerId);

		solo.waitForText(solo.getString(R.string.brick_broadcast_receive));
		solo.clickOnText(solo.getString(R.string.brick_broadcast_receive));
		solo.waitForText(solo.getString(R.string.brick_context_dialog_delete_script));
		solo.clickOnText(solo.getString(R.string.brick_context_dialog_delete_script));
		solo.waitForText(solo.getString(R.string.yes));
		solo.clickOnButton(solo.getString(R.string.yes));

		solo.sleep(200);

		UiTestUtils.addNewBrick(solo, R.string.category_control, R.string.brick_broadcast);
		//dont need to place it because there are 0 bricks, places automatically.

		//to gain focus
		solo.clickOnScreen(200, 200);
		if (solo.searchText(solo.getString(R.string.brick_context_dialog_move_brick), true)) {
			solo.goBack();
		}

		Spinner broadcastSpinner = (Spinner) solo.getView(R.id.brick_broadcast_spinner);

		assertEquals("Wrong selection", defaultBroadcastMessage, broadcastSpinner.getSelectedItem().toString());
	}

	@Device
	public void testRemoveUnusedMessages() {
		checkSetupBricks();

		final String broadcastMessage = "Broadcast message";
		enterNewTextIntoSpinner(broadcastReceiverSpinnerId, broadcastMessage);
		pressSpinnerItem(broadcastSpinnerId, broadcastMessage);
		pressSpinnerItem(broadcastWaitSpinnerId, broadcastMessage);
		solo.sleep(500);

		UiTestUtils.clickOnHomeActionBarButton(solo);
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		checkCorrectSpinnerSelections();
		checkIfUnusedBroadcastMessagesHaveBeenRemoved(broadcastReceiverSpinnerId, broadcastMessage);
		checkIfUnusedBroadcastMessagesHaveBeenRemoved(broadcastSpinnerId, broadcastMessage);
		checkIfUnusedBroadcastMessagesHaveBeenRemoved(broadcastWaitSpinnerId, broadcastMessage);
	}

	private void checkIfUnusedBroadcastMessagesHaveBeenRemoved(int spinnerId, String broadcastMessage) {
		Spinner spinner = (Spinner) solo.getView(spinnerId);
		assertEquals("broadcastWaitSpinner has not the correct number of elements", 2, spinner.getCount());
		assertEquals("First spinner element isn't " + solo.getString(R.string.new_broadcast_message),
				solo.getString(R.string.new_broadcast_message), spinner.getItemAtPosition(0));
		assertEquals("First broadcast message isn't" + broadcastMessage, broadcastMessage, spinner.getItemAtPosition(1));
	}

	private void checkCorrectSpinnerSelections() {
		assertEquals("Wrong broadcast message in broadcast receiver.",
				expectedSpinnerText.get(broadcastReceiverSpinnerId),
				((Spinner) solo.getCurrentActivity().findViewById(broadcastReceiverSpinnerId)).getSelectedItem().toString());
		assertEquals("Wrong broadcast message in broadcast.", expectedSpinnerText.get(broadcastSpinnerId),
				((Spinner) solo.getCurrentActivity().findViewById(broadcastSpinnerId)).getSelectedItem().toString());
		assertEquals("Wrong broadcast message in broadcastWait.", expectedSpinnerText.get(broadcastWaitSpinnerId),
				((Spinner) solo.getCurrentActivity().findViewById(broadcastWaitSpinnerId)).getSelectedItem().toString());
	}

	private void enterNewTextIntoSpinner(int spinnerId, String text) {
		solo.clickOnView(solo.getView(spinnerId));
		solo.waitForText(solo.getString(R.string.new_broadcast_message));
		solo.clickInList(0);
		solo.waitForView(EditText.class);
		solo.enterText(0, text);
		solo.clickOnText(solo.getString(R.string.ok));
		solo.waitForView(solo.getView(spinnerId));
		gainFocus();
		expectedSpinnerText.put(spinnerId, text);
		checkCorrectSpinnerSelections();
	}

	private void pressSpinnerItem(int spinnerId, String text) {
		solo.clickOnView(solo.getView(spinnerId));
		solo.clickOnText(text);
		solo.waitForView(solo.getView(spinnerId));
		gainFocus();
		expectedSpinnerText.put(spinnerId, text);
		checkCorrectSpinnerSelections();
	}

	private void dismissEnterNewTextIntoSpinner(int spinnerId) {
		solo.clickOnView(solo.getView(spinnerId));
		solo.waitForText(solo.getString(R.string.new_broadcast_message));
		solo.clickInList(0);
		solo.waitForView(EditText.class);
		solo.goBack();
		solo.getCurrentActivity().findViewById(spinnerId);
		checkCorrectSpinnerSelections();
	}

	private void checkSetupBricks() {
		ListView view = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) view.getAdapter();

		assertEquals("String has changed", solo.getString(R.string.brick_broadcast_default_value),
				defaultBroadcastMessage);

		assertEquals("Wrong number of scripts.", 1, sprite.getNumberOfScripts());
		assertTrue("Wrong script instance.", sprite.getScript(0) instanceof BroadcastScript);

		int childrenCount = sprite.getScript(adapter.getScriptCount() - 1).getBrickList().size();
		assertEquals("Incorrect number of bricks in sprite.", 3, UiTestUtils.getScriptListView(solo).getChildCount());
		assertEquals("Incorrect number of bricks in broadcast script.", 2, childrenCount);

		List<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 2, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof BroadcastBrick);
		assertTrue("Wrong Brick instance.", adapter.getItem(1) instanceof BroadcastBrick);
	}

	private void gainFocus() {
		solo.clickOnText(solo.getString(R.string.brick_broadcast_receive));
		if (solo.searchText(solo.getString(R.string.brick_context_dialog_delete_script), true)) {
			solo.goBack();
		}
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		sprite = new Sprite("cat");
		Script script = new BroadcastScript(defaultBroadcastMessage);
		BroadcastBrick broadcastBrick = new BroadcastBrick(defaultBroadcastMessage);
		BroadcastWaitBrick broadcastWaitBrick = new BroadcastWaitBrick(defaultBroadcastMessage);
		script.addBrick(broadcastBrick);
		script.addBrick(broadcastWaitBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
