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
package org.catrobat.catroid.uitest.stage;

import android.widget.EditText;
import android.widget.ListView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick;
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.UserVariableBrick;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.dialogs.NewDataDialog;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class BroadCastReceiverRegressionTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	public BroadCastReceiverRegressionTest() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		UiTestUtils.prepareStageForTest();
	}

	public void testReceiversWorkMoreThanOnce() {
		UiTestUtils.createEmptyProject();
		Sprite sprite = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0);
		Script script = sprite.getScript(0);

		final String testMessage = "RegressionTest#105";
		BroadcastBrick broadcastBrick = new BroadcastBrick(testMessage);
		script.addBrick(broadcastBrick);

		BroadcastScript broadcastScript = new BroadcastScript(testMessage);
		final int xMovement = 100;
		ChangeXByNBrick changeXByNBrick = new ChangeXByNBrick(xMovement);
		broadcastScript.addBrick(changeXByNBrick);
		sprite.addScript(broadcastScript);

		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(2000);

		assertEquals("Broadcast was not executed!", xMovement, (int) sprite.look.getXInUserInterfaceDimensionUnit());

		solo.goBack();
		solo.goBack();
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		// This is where the magic happens. We try to execute the program again, Broadcasts should still work.
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(2000);

		assertEquals("Broadcast didn't work a second time!", xMovement,
				(int) sprite.look.getXInUserInterfaceDimensionUnit());
	}

	public void testWhenScriptRestartingItself() {
		UiTestUtils.createEmptyProject();
		Sprite sprite = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0);
		Script script = sprite.getScript(0);

		final String testMessage = "RegressionTest#875";
		BroadcastBrick broadcastBrick = new BroadcastBrick(testMessage);
		script.addBrick(broadcastBrick);

		BroadcastScript broadcastScript = new BroadcastScript(testMessage);

		final int xMovement = 1;
		ChangeXByNBrick changeXByNBrick = new ChangeXByNBrick(xMovement);
		broadcastScript.addBrick(changeXByNBrick);

		BroadcastBrick broadcastBrickLoop = new BroadcastBrick(testMessage);
		broadcastScript.addBrick(broadcastBrickLoop);

		sprite.addScript(broadcastScript);

		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(2000);

		assertTrue("When script does not restart itself!",
				(int) sprite.look.getXInUserInterfaceDimensionUnit() > xMovement);
	}

	public void testRestartingOfWhenScriptWithBroadcastWaitBrick() {
		UiTestUtils.createEmptyProject();
		String messageOne = "messageOne";
		String messageTwo = "messageTwo";
		final int xMovement = 1;

		Sprite sprite = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0);
		Script startScript = sprite.getScript(0);
		BroadcastBrick startBroadcastBrick = new BroadcastBrick(messageOne);
		startScript.addBrick(startBroadcastBrick);

		BroadcastScript broadcastScriptMessageOne = new BroadcastScript(messageOne);
		ChangeXByNBrick changeXByNBrickOne = new ChangeXByNBrick(xMovement);
		BroadcastWaitBrick broadcastWaitBrickOne = new BroadcastWaitBrick(messageTwo);
		broadcastScriptMessageOne.addBrick(changeXByNBrickOne);
		broadcastScriptMessageOne.addBrick(broadcastWaitBrickOne);
		sprite.addScript(broadcastScriptMessageOne);

		BroadcastScript broadcastScriptMessageTwo = new BroadcastScript(messageTwo);
		ChangeXByNBrick changeXByNBrickTwo = new ChangeXByNBrick(xMovement);
		BroadcastWaitBrick broadcastWaitBrickTwo = new BroadcastWaitBrick(messageOne);
		broadcastScriptMessageTwo.addBrick(changeXByNBrickTwo);
		broadcastScriptMessageTwo.addBrick(broadcastWaitBrickTwo);
		sprite.addScript(broadcastScriptMessageTwo);

		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(3000);

		assertTrue("When script does not restart itself when a BroadcastWait is sent!",
				(int) sprite.look.getXInUserInterfaceDimensionUnit() > 5 * xMovement);
	}

	public void testRestartingSendBroadcastAfterBroadcastAndWait() {
		String message = "increase variable value";
		String variableName = "test variable";
		SetVariableBrick setVariableBrick = UiTestUtils.createSendBroadcastAfterBroadcastAndWaitProject(message);

		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		createUserVariable(variableName);

		switchToScriptFragmentOfAnotherSprite("sprite1");
		switchToScriptFragmentOfAnotherSprite("sprite2");
		switchToScriptFragmentOfAnotherSprite("sprite3");

		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(3000);

		UserVariable userVariable = (UserVariable) Reflection.getPrivateField(UserVariableBrick.class, setVariableBrick, "userVariable");
		assertNotNull("UserVariable is null", userVariable);

		double expectedValue = 2111.0f;
		assertEquals("Broadcast script of sprite 3 does not restart itself when a BroadcastWait is sent!", expectedValue, userVariable.getValue());
	}

	public void testRestartingSendBroadcastInBroadcastAndWait() {
		String message1 = "message1";
		String message2 = "message2";
		double degreesToTurn = 15.0f;
		Sprite secondSprite = new Sprite("sprite2");
		Sprite thirdSprite = new Sprite("sprite3");
		int initialRotation = UiTestUtils.createSendBroadcastInBroadcastAndWaitProject(message1, message2, degreesToTurn, secondSprite, thirdSprite);

		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(3000);

		assertEquals("Second Broadcast Script does not restart itself!", (int) ((initialRotation - 3 * degreesToTurn) % 360),
				(int) secondSprite.look.getRotation());

		assertEquals("Third Broadcast Script does not restart itself!", (int) ((initialRotation + 3 * degreesToTurn) % 360),
				(int) thirdSprite.look.getRotation());
	}

	public void testCorrectRestartingOfBroadcastsWithSameActionStringsWithinOneSprite() {
		String message = "message";
		String variableName = "test variable";
		SetVariableBrick setVariableBrick = UiTestUtils.createSameActionsBroadcastProject(message);

		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		createUserVariable(variableName);

		switchToScriptFragmentOfAnotherSprite("sprite1");
		switchToScriptFragmentOfAnotherSprite("sprite2");
		switchToScriptFragmentOfAnotherSprite("sprite3");

		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(3000);

		UserVariable userVariable = (UserVariable) Reflection.getPrivateField(UserVariableBrick.class, setVariableBrick, "userVariable");
		assertNotNull("UserVariable is null", userVariable);

		double expectedValue = 20.0f;
		assertEquals("Actions of identical action strings were not restarted!", expectedValue, userVariable.getValue());
	}

	public void switchToScriptFragmentOfAnotherSprite(String spriteName) {
		solo.goBack();
		solo.goBack();
		solo.clickOnText(spriteName);
		solo.clickOnText(solo.getString(R.string.scripts));
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.waitForView(ListView.class);
		solo.sleep(200);
	}

	private void createUserVariable(String variableName) {
		solo.clickOnText(getInstrumentation().getTargetContext().getString(
				R.string.brick_variable_spinner_create_new_variable));
		assertTrue("NewVariableDialog not visible", solo.waitForFragmentByTag(NewDataDialog.DIALOG_FRAGMENT_TAG));

		EditText editText = (EditText) solo.getView(R.id.dialog_formula_editor_data_name_edit_text);
		solo.enterText(editText, variableName);
		solo.clickOnButton(solo.getString(R.string.ok));
		assertTrue("ScriptFragment not visible", solo.waitForText(solo.getString(R.string.brick_set_variable)));
		assertTrue("Created ProjectVariable not set on first position in spinner", solo.searchText(variableName));
	}
}
