/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.stage;

import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.BroadcastHandler;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class BroadcastReceiverRegressionTest {

	private static final String BROADCAST_MESSAGE_1 = "message1";
	private static final String BROADCAST_MESSAGE_2 = "message2";
	private static final String VARIABLE_NAME = "var1";

	private UserVariable userVariable;
	private Project project;
	private Sprite sprite1;
	private Script sprite1StartScript;

	@Rule
	public BaseActivityInstrumentationRule<StageActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(StageActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		BroadcastHandler.clearActionMaps();
		createProject();
	}

	private void createProject() {
		project = UiTestUtils.createEmptyProject("test");
		DataContainer dataContainer = project.getDefaultScene().getDataContainer();
		userVariable = dataContainer.addProjectUserVariable(VARIABLE_NAME);
		sprite1 = project.getDefaultScene().getSpriteList().get(0);
		sprite1StartScript = StageTestUtils.createStartScriptAndAddToSprite(sprite1);
	}

	@Test
	public void broadcastScriptNotExecutedWithWrongMessage() {
		sprite1StartScript.addBrick(new SetVariableBrick(new Formula(1.0), userVariable));
		sprite1StartScript.addBrick(new BroadcastBrick(BROADCAST_MESSAGE_1));
		StageTestUtils.addBroadcastScriptSettingUserVariableToSprite(sprite1, BROADCAST_MESSAGE_2, userVariable, 3.0);

		baseActivityTestRule.launchActivity(null);

		Assert.assertTrue(StageTestUtils.userVariableEqualsWithinTimeout(userVariable, 1, 2000));
	}

	@Test
	public void testBroadcastReceiverOnce() {
		sprite1StartScript.addBrick(new SetVariableBrick(new Formula(1.0), userVariable));
		sprite1StartScript.addBrick(new BroadcastBrick(BROADCAST_MESSAGE_1));
		StageTestUtils.addBroadcastScriptSettingUserVariableToSprite(sprite1, BROADCAST_MESSAGE_1, userVariable, 3.0);

		baseActivityTestRule.launchActivity(null);

		Assert.assertTrue(StageTestUtils.userVariableEqualsWithinTimeout(userVariable, 3, 2000));
	}

	@Test
	public void testReceiversWorkMoreThanOnce() {
		final double initialValue = 1.0;
		sprite1StartScript.addBrick(new SetVariableBrick(new Formula(initialValue), userVariable));
		sprite1StartScript.addBrick(new BroadcastBrick(BROADCAST_MESSAGE_1));
		StageTestUtils.addBroadcastScriptSettingUserVariableToSprite(sprite1, BROADCAST_MESSAGE_1, userVariable, 3.0);

		baseActivityTestRule.launchActivity(null);
		StageTestUtils.userVariableEqualsWithinTimeout(userVariable, 3, 2000);
		pressBack();
		userVariable.setValue(initialValue);
		onView(withId(R.id.stage_dialog_button_restart)).perform(click());

		Assert.assertTrue(StageTestUtils.userVariableEqualsWithinTimeout(userVariable, 3, 2000));
	}

	@Test
	public void testScriptRestartingItself() {
		sprite1StartScript.addBrick(new SetVariableBrick(new Formula(0.0), userVariable));
		sprite1StartScript.addBrick(new BroadcastBrick(BROADCAST_MESSAGE_1));

		Script broadcastScript = StageTestUtils.createBroadcastScriptAndAddToSprite(BROADCAST_MESSAGE_1, sprite1);
		broadcastScript.addBrick(new ChangeVariableBrick(new Formula(1.0), userVariable));
		broadcastScript.addBrick(new BroadcastBrick(BROADCAST_MESSAGE_1));

		baseActivityTestRule.launchActivity(null);

		Assert.assertTrue(StageTestUtils.userVariableGreaterThanWithinTimeout(userVariable, 1.0, 2000));
	}

	@Test
	public void testRestartingOfWhenScriptWithBroadcastWaitBrick() {
		sprite1StartScript.addBrick(new SetVariableBrick(new Formula(0.0), userVariable));
		sprite1StartScript.addBrick(new BroadcastBrick(BROADCAST_MESSAGE_1));

		Script broadcastScriptMessageOne = StageTestUtils.createBroadcastScriptAndAddToSprite(BROADCAST_MESSAGE_1, sprite1);
		broadcastScriptMessageOne.addBrick(new ChangeVariableBrick(new Formula(1.0), userVariable));
		broadcastScriptMessageOne.addBrick(new BroadcastWaitBrick(BROADCAST_MESSAGE_2));
		Script broadcastScriptMessageTwo = StageTestUtils.createBroadcastScriptAndAddToSprite(BROADCAST_MESSAGE_2, sprite1);
		broadcastScriptMessageTwo.addBrick(new ChangeVariableBrick(new Formula(1.0), userVariable));
		broadcastScriptMessageTwo.addBrick(new BroadcastWaitBrick(BROADCAST_MESSAGE_1));

		baseActivityTestRule.launchActivity(null);

		Assert.assertTrue(StageTestUtils.userVariableGreaterThanWithinTimeout(userVariable, 5, 1000));
	}

	@Test
	public void testRestartingSendBroadcastAfterBroadcastAndWait() {
		sprite1StartScript.addBrick(new SetVariableBrick(new Formula(1.0f), userVariable));
		sprite1StartScript.addBrick(new BroadcastWaitBrick(BROADCAST_MESSAGE_1));
		sprite1StartScript.addBrick(new BroadcastBrick(BROADCAST_MESSAGE_1));

		Sprite sprite2 = StageTestUtils.createSpriteAndAddToProject("sprite2", project);
		Script script2StartScript = StageTestUtils.createStartScriptAndAddToSprite(sprite2);
		script2StartScript.addBrick(new ChangeVariableBrick(new Formula(100.0f), userVariable));
		script2StartScript.addBrick(new WaitBrick(400));
		script2StartScript.addBrick(new BroadcastWaitBrick(BROADCAST_MESSAGE_1));

		Sprite sprite3 = StageTestUtils.createSpriteAndAddToProject("sprite3", project);
		Script script3BroadcastScript = StageTestUtils.createBroadcastScriptAndAddToSprite(BROADCAST_MESSAGE_1, sprite3);
		script3BroadcastScript.addBrick(new ChangeVariableBrick(new Formula(1000.0f), userVariable));

		baseActivityTestRule.launchActivity(null);

		Assert.assertTrue(StageTestUtils.userVariableEqualsWithinTimeout(userVariable, 3101, 1000));
	}

	@Test
	public void testRestartingSendBroadcastInBroadcastAndWait() {
		sprite1StartScript.addBrick(new SetVariableBrick(new Formula(0.0), userVariable));
		sprite1StartScript.addBrick(new BroadcastBrick(BROADCAST_MESSAGE_1));

		Sprite sprite2 = StageTestUtils.createSpriteAndAddToProject("sprite2", project);
		Script sprite2BroadcastScript = StageTestUtils.createBroadcastScriptAndAddToSprite(BROADCAST_MESSAGE_1, sprite2);
		sprite2BroadcastScript.addBrick(new ChangeVariableBrick(new Formula(1.0f), userVariable));
		sprite2BroadcastScript.addBrick(new WaitBrick(50));
		sprite2BroadcastScript.addBrick(new BroadcastWaitBrick(BROADCAST_MESSAGE_2));

		Sprite sprite3 = StageTestUtils.createSpriteAndAddToProject("sprite3", project);
		Script sprite3BroadcastScript = StageTestUtils.createBroadcastScriptAndAddToSprite(BROADCAST_MESSAGE_2, sprite3);
		sprite3.addScript(sprite3BroadcastScript);
		sprite3BroadcastScript.addBrick(new ChangeVariableBrick(new Formula(1000.0f), userVariable));
		sprite3BroadcastScript.addBrick(new WaitBrick(50));
		sprite3BroadcastScript.addBrick(new BroadcastBrick(BROADCAST_MESSAGE_1));

		baseActivityTestRule.launchActivity(null);

		Assert.assertTrue(StageTestUtils.userVariableGreaterThanWithinTimeout(userVariable, 2002, 1000));
	}

	@Test
	public void testCorrectRestartingOfBroadcastsWithSameActionStringsWithinOneSprite() throws InterruptedException {
		RepeatBrick repeatBrick = new RepeatBrick(10);
		LoopEndBrick endBrick = new LoopEndBrick(repeatBrick);
		repeatBrick.setLoopEndBrick(endBrick);

		sprite1StartScript.addBrick(new SetVariableBrick(new Formula(0.0f), userVariable));
		sprite1StartScript.addBrick(repeatBrick);
		sprite1StartScript.addBrick(new BroadcastWaitBrick(BROADCAST_MESSAGE_1));
		sprite1StartScript.addBrick(endBrick);

		Sprite sprite2 = StageTestUtils.createSpriteAndAddToProject("sprite2", project);
		Script sprite2BroadcastScript = StageTestUtils.createBroadcastScriptAndAddToSprite(BROADCAST_MESSAGE_1, sprite2);
		sprite2BroadcastScript.addBrick(new ChangeVariableBrick(new Formula(1.0f), userVariable));

		Sprite sprite3 = StageTestUtils.createSpriteAndAddToProject("sprite3", project);
		Script sprite3BroadcastScript = StageTestUtils.createBroadcastScriptAndAddToSprite(BROADCAST_MESSAGE_1, sprite3);
		sprite3BroadcastScript.addBrick(new ChangeVariableBrick(new Formula(1.0f), userVariable));

		baseActivityTestRule.launchActivity(null);

		Assert.assertTrue(StageTestUtils.userVariableEqualsWithinTimeout(userVariable, 20, 1000));
	}

	@Test
	public void testBroadcastReceiverWithMoreThanOneReceiverScript() {
		DataContainer dataContainer = project.getDefaultScene().getDataContainer();
		UserVariable userVariable2 = dataContainer.addProjectUserVariable(VARIABLE_NAME + "2");

		sprite1StartScript.addBrick(new SetVariableBrick(new Formula(1.0), userVariable));
		sprite1StartScript.addBrick(new SetVariableBrick(new Formula(1.0), userVariable2));
		sprite1StartScript.addBrick(new BroadcastBrick(BROADCAST_MESSAGE_1));
		StageTestUtils.addBroadcastScriptSettingUserVariableToSprite(sprite1, BROADCAST_MESSAGE_1, userVariable, 3.0);
		StageTestUtils.addBroadcastScriptSettingUserVariableToSprite(sprite1, BROADCAST_MESSAGE_1, userVariable2, 4.0);

		baseActivityTestRule.launchActivity(null);

		Assert.assertTrue(StageTestUtils.userVariableEqualsWithinTimeout(userVariable, 3, 2000));
		Assert.assertTrue(StageTestUtils.userVariableEqualsWithinTimeout(userVariable2, 4, 2000));
	}
}
