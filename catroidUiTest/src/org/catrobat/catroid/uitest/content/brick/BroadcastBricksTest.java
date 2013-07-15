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

import java.util.List;

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
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.suitebuilder.annotation.Smoke;
import android.util.SparseArray;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

public class BroadcastBricksTest extends BaseActivityInstrumentationTestCase<ScriptActivity> {

	private Project project;
	private Sprite sprite;

	private final SparseArray<String> expected = new SparseArray<String>();
	private final String defaultBroadcastMessage = "Default message";

	private final int broadcastReceiverSpinnerId = R.id.brick_broadcast_receive_spinner;
	private final int broadcastSpinnerId = R.id.brick_broadcast_spinner;
	private final int broadcastWaitSpinnerId = R.id.brick_broadcast_wait_spinner;

	public BroadcastBricksTest() {
		super(ScriptActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		// normally super.setUp should be called first
		// but kept the test failing due to view is null
		// when starting in ScriptActivity
		createProject();
		super.setUp();

		expected.put(broadcastReceiverSpinnerId, defaultBroadcastMessage);
		expected.put(broadcastSpinnerId, defaultBroadcastMessage);
		expected.put(broadcastWaitSpinnerId, defaultBroadcastMessage);
	}

	@Smoke
	public void testBroadcastBricks() {
		checkSetupBricks();

		final String broadcastMessage1 = "Apple";
		final String broadcastMessage2 = "Banana";
		final String broadcastMessage3 = "Cherry";

		enterNewTextIntoSpinner(broadcastReceiverSpinnerId, broadcastMessage1);
		pressSpinnerItem(broadcastSpinnerId, broadcastMessage1);
		pressSpinnerItem(broadcastWaitSpinnerId, broadcastMessage1);

		enterNewTextIntoSpinner(broadcastSpinnerId, broadcastMessage2);
		enterNewTextIntoSpinner(broadcastWaitSpinnerId, broadcastMessage3);

		pressSpinnerItem(broadcastSpinnerId, broadcastMessage3);

		dismissEnterNewTextIntoSpinner(broadcastReceiverSpinnerId);
		dismissEnterNewTextIntoSpinner(broadcastSpinnerId);
		dismissEnterNewTextIntoSpinner(broadcastWaitSpinnerId);

		solo.clickLongOnText(solo.getString(R.string.brick_broadcast_receive));
		solo.clickOnText(solo.getString(R.string.delete));
		solo.clickOnButton(solo.getString(R.string.yes));

		UiTestUtils.addNewBrick(solo, R.string.brick_broadcast);
		solo.clickOnScreen(200, 200);
		if (solo.searchText(solo.getString(R.string.brick_context_dialog_move_brick), true)) {
			solo.goBack();
		}
		Spinner broadcastSpinner = (Spinner) solo.getView(R.id.brick_broadcast_spinner);

		assertEquals("Wrong selection", defaultBroadcastMessage, broadcastSpinner.getSelectedItem().toString());
	}

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
		assertEquals("Wrong broadcast message in broadcast receiver.", expected.get(broadcastReceiverSpinnerId),
				((Spinner) solo.getView(broadcastReceiverSpinnerId)).getSelectedItem().toString());
		assertEquals("Wrong broadcast message in broadcast.", expected.get(broadcastSpinnerId),
				((Spinner) solo.getView(broadcastSpinnerId)).getSelectedItem().toString());
		assertEquals("Wrong broadcast message in broadcastWait.", expected.get(broadcastWaitSpinnerId),
				((Spinner) solo.getView(broadcastWaitSpinnerId)).getSelectedItem().toString());
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
		solo.waitForView(solo.getView(spinnerId));
		gainFocus();
		expected.put(spinnerId, text);
		checkCorrectSpinnerSelections();
	}

	private void pressSpinnerItem(int spinnerId, String text) {
		solo.clickOnView(solo.getView(spinnerId));
		solo.clickOnText(text);
		solo.waitForView(solo.getView(spinnerId));
		gainFocus();
		expected.put(spinnerId, text);
		checkCorrectSpinnerSelections();
	}

	private void dismissEnterNewTextIntoSpinner(int spinnerId) {
		solo.clickOnView(solo.getView(spinnerId));
		solo.waitForText(solo.getString(R.string.new_broadcast_message));
		solo.clickInList(0);
		solo.waitForView(EditText.class);
		solo.goBack();
		solo.goBack();
		solo.waitForView(solo.getView(spinnerId));
		checkCorrectSpinnerSelections();
	}

	private void checkSetupBricks() {
		ListView view = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) view.getAdapter();

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
		if (solo.searchText(solo.getString(R.string.brick_context_dialog_delete_brick), true)) {
			solo.goBack();
		}
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		sprite = new Sprite("cat");
		Script script = new BroadcastScript(sprite, defaultBroadcastMessage);
		BroadcastBrick broadcastBrick = new BroadcastBrick(sprite, defaultBroadcastMessage);
		BroadcastWaitBrick broadcastWaitBrick = new BroadcastWaitBrick(sprite, defaultBroadcastMessage);
		script.addBrick(broadcastBrick);
		script.addBrick(broadcastWaitBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
