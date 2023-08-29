/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.uiespresso.stage.utils.StageTestUtils;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.uiespresso.util.UserVariableAssertions.assertUserVariableEqualsWithTimeout;
import static org.catrobat.catroid.uiespresso.util.UserVariableAssertions.assertUserVariableIsGreaterThanWithTimeout;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

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
	public BaseActivityTestRule<StageActivity> baseActivityTestRule = new
			BaseActivityTestRule<>(StageActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProject();
	}

	@After
	public void tearDown() {
		IdlingRegistry.getInstance().unregister(baseActivityTestRule.getActivity().idlingResource);
	}

	private void createProject() {
		project = UiTestUtils.createDefaultTestProject("test");
		userVariable = new UserVariable(VARIABLE_NAME);
		project.addUserVariable(userVariable);
		sprite1 = project.getDefaultScene().getBackgroundSprite();
		sprite1StartScript = new StartScript();
		sprite1.addScript(sprite1StartScript);
	}

	@Category({Level.Functional.class, Cat.CatrobatLanguage.class})
	@Test
	public void broadcastScriptNotExecutedWithWrongMessage() {
		sprite1StartScript.addBrick(new SetVariableBrick(new Formula(1.0), userVariable));
		sprite1StartScript.addBrick(new BroadcastBrick(BROADCAST_MESSAGE_1));
		StageTestUtils.addBroadcastScriptSettingUserVariableToSprite(sprite1, BROADCAST_MESSAGE_2, userVariable, 3.0);

		baseActivityTestRule.launchActivity(null);

		assertUserVariableEqualsWithTimeout(userVariable, 1, 2000);
	}

	@Category({Level.Functional.class, Cat.CatrobatLanguage.class})
	@Test
	public void testBroadcastReceiverOnce() {
		sprite1StartScript.addBrick(new SetVariableBrick(new Formula(1.0), userVariable));
		sprite1StartScript.addBrick(new BroadcastBrick(BROADCAST_MESSAGE_1));
		StageTestUtils.addBroadcastScriptSettingUserVariableToSprite(sprite1, BROADCAST_MESSAGE_1, userVariable, 3.0);

		baseActivityTestRule.launchActivity(null);

		assertUserVariableEqualsWithTimeout(userVariable, 3, 2000);
	}

	@Category({Level.Functional.class, Cat.CatrobatLanguage.class})
	@Test
	public void testReceiversWorkMoreThanOnce() {
		final double initialValue = 1.0;
		sprite1StartScript.addBrick(new SetVariableBrick(new Formula(initialValue), userVariable));
		sprite1StartScript.addBrick(new BroadcastBrick(BROADCAST_MESSAGE_1));
		StageTestUtils.addBroadcastScriptSettingUserVariableToSprite(sprite1, BROADCAST_MESSAGE_1, userVariable, 3.0);

		baseActivityTestRule.launchActivity(null);
		IdlingRegistry.getInstance().register(baseActivityTestRule.getActivity().idlingResource);
		assertUserVariableEqualsWithTimeout(userVariable, 3, 2000);
		pressBack();
		userVariable.setValue(initialValue);
		onView(withId(R.id.stage_dialog_button_restart)).inRoot(isDialog()).perform(click());

		assertUserVariableEqualsWithTimeout(userVariable, 3, 2000);
	}

	@Category({Level.Functional.class, Cat.CatrobatLanguage.class})
	@Test
	public void testScriptRestartingItself() {
		sprite1StartScript.addBrick(new SetVariableBrick(new Formula(0.0), userVariable));
		sprite1StartScript.addBrick(new BroadcastBrick(BROADCAST_MESSAGE_1));

		Script broadcastScript = createBroadcastScriptAndAddToSprite(BROADCAST_MESSAGE_1, sprite1);
		broadcastScript.addBrick(new ChangeVariableBrick(new Formula(1.0), userVariable));
		broadcastScript.addBrick(new BroadcastBrick(BROADCAST_MESSAGE_1));

		baseActivityTestRule.launchActivity(null);

		assertUserVariableIsGreaterThanWithTimeout(userVariable, 1.0, 2000);
	}

	@Category({Level.Functional.class, Cat.CatrobatLanguage.class})
	@Test
	public void testRestartingOfWhenScriptWithBroadcastWaitBrick() {
		sprite1StartScript.addBrick(new SetVariableBrick(new Formula(0.0), userVariable));
		sprite1StartScript.addBrick(new BroadcastBrick(BROADCAST_MESSAGE_1));

		Script broadcastScriptMessageOne = createBroadcastScriptAndAddToSprite(BROADCAST_MESSAGE_1, sprite1);
		broadcastScriptMessageOne.addBrick(new ChangeVariableBrick(new Formula(1.0), userVariable));
		broadcastScriptMessageOne.addBrick(new BroadcastWaitBrick(BROADCAST_MESSAGE_2));
		Script broadcastScriptMessageTwo = createBroadcastScriptAndAddToSprite(BROADCAST_MESSAGE_2, sprite1);
		broadcastScriptMessageTwo.addBrick(new ChangeVariableBrick(new Formula(1.0), userVariable));
		broadcastScriptMessageTwo.addBrick(new BroadcastWaitBrick(BROADCAST_MESSAGE_1));

		baseActivityTestRule.launchActivity(null);

		assertUserVariableIsGreaterThanWithTimeout(userVariable, 5, 1000);
	}

	@Category({Level.Functional.class, Cat.CatrobatLanguage.class})
	@Test
	public void testRestartingSendBroadcastAfterBroadcastAndWait() {
		sprite1StartScript.addBrick(new SetVariableBrick(new Formula(1.0f), userVariable));
		sprite1StartScript.addBrick(new BroadcastWaitBrick(BROADCAST_MESSAGE_1));
		sprite1StartScript.addBrick(new BroadcastBrick(BROADCAST_MESSAGE_1));

		Sprite sprite2 = createSpriteAndAddToProject("sprite2", project);

		Script script2StartScript = new StartScript();
		sprite2.addScript(script2StartScript);

		script2StartScript.addBrick(new ChangeVariableBrick(new Formula(100.0f), userVariable));
		script2StartScript.addBrick(new WaitBrick(400));
		script2StartScript.addBrick(new BroadcastWaitBrick(BROADCAST_MESSAGE_1));

		Sprite sprite3 = createSpriteAndAddToProject("sprite3", project);
		Script script3BroadcastScript = createBroadcastScriptAndAddToSprite(BROADCAST_MESSAGE_1, sprite3);
		script3BroadcastScript.addBrick(new ChangeVariableBrick(new Formula(1000.0f), userVariable));

		baseActivityTestRule.launchActivity(null);

		assertUserVariableEqualsWithTimeout(userVariable, 3101, 1000);
	}

	@Category({Level.Functional.class, Cat.CatrobatLanguage.class})
	@Test
	public void testRestartingSendBroadcastInBroadcastAndWait() {
		sprite1StartScript.addBrick(new SetVariableBrick(new Formula(0.0), userVariable));
		sprite1StartScript.addBrick(new BroadcastBrick(BROADCAST_MESSAGE_1));

		Sprite sprite2 = createSpriteAndAddToProject("sprite2", project);
		Script sprite2BroadcastScript = createBroadcastScriptAndAddToSprite(BROADCAST_MESSAGE_1, sprite2);
		sprite2BroadcastScript.addBrick(new ChangeVariableBrick(new Formula(1.0f), userVariable));
		sprite2BroadcastScript.addBrick(new WaitBrick(50));
		sprite2BroadcastScript.addBrick(new BroadcastWaitBrick(BROADCAST_MESSAGE_2));

		Sprite sprite3 = createSpriteAndAddToProject("sprite3", project);
		Script sprite3BroadcastScript = createBroadcastScriptAndAddToSprite(BROADCAST_MESSAGE_2, sprite3);
		sprite3.addScript(sprite3BroadcastScript);
		sprite3BroadcastScript.addBrick(new ChangeVariableBrick(new Formula(1000.0f), userVariable));
		sprite3BroadcastScript.addBrick(new WaitBrick(50));
		sprite3BroadcastScript.addBrick(new BroadcastBrick(BROADCAST_MESSAGE_1));

		baseActivityTestRule.launchActivity(null);

		assertUserVariableIsGreaterThanWithTimeout(userVariable, 2002, 1000);
	}

	@Category({Level.Functional.class, Cat.CatrobatLanguage.class})
	@Test
	public void testCorrectRestartingOfBroadcastsWithSameActionStringsWithinOneSprite() {
		sprite1StartScript.addBrick(new SetVariableBrick(new Formula(0.0f), userVariable));
		RepeatBrick repeatBrick = new RepeatBrick(new Formula(10));
		repeatBrick.addBrick(new BroadcastWaitBrick(BROADCAST_MESSAGE_1));
		sprite1StartScript.addBrick(repeatBrick);

		Sprite sprite2 = createSpriteAndAddToProject("sprite2", project);
		Script sprite2BroadcastScript = createBroadcastScriptAndAddToSprite(BROADCAST_MESSAGE_1, sprite2);
		sprite2BroadcastScript.addBrick(new ChangeVariableBrick(new Formula(1.0f), userVariable));

		Sprite sprite3 = createSpriteAndAddToProject("sprite3", project);
		Script sprite3BroadcastScript = createBroadcastScriptAndAddToSprite(BROADCAST_MESSAGE_1, sprite3);
		sprite3BroadcastScript.addBrick(new ChangeVariableBrick(new Formula(1.0f), userVariable));

		baseActivityTestRule.launchActivity(null);

		assertUserVariableEqualsWithTimeout(userVariable, 20, 1000);
	}

	public Sprite createSpriteAndAddToProject(String name, Project project) {
		Sprite sprite = new Sprite(name);
		project.getDefaultScene().addSprite(sprite);
		return sprite;
	}

	public Script createBroadcastScriptAndAddToSprite(String broadcastMessage, Sprite sprite) {
		Script broadcastScript = new BroadcastScript(broadcastMessage);
		sprite.addScript(broadcastScript);
		return broadcastScript;
	}

	@Test
	public void testBroadcastReceiverWithMoreThanOneReceiverScript() {
		UserVariable userVariable2 = new UserVariable(VARIABLE_NAME + "2");
		project.addUserVariable(userVariable2);

		sprite1StartScript.addBrick(new SetVariableBrick(new Formula(1.0), userVariable));
		sprite1StartScript.addBrick(new SetVariableBrick(new Formula(1.0), userVariable2));
		sprite1StartScript.addBrick(new BroadcastBrick(BROADCAST_MESSAGE_1));
		StageTestUtils.addBroadcastScriptSettingUserVariableToSprite(sprite1, BROADCAST_MESSAGE_1, userVariable, 3.0);
		StageTestUtils.addBroadcastScriptSettingUserVariableToSprite(sprite1, BROADCAST_MESSAGE_1, userVariable2, 4.0);

		baseActivityTestRule.launchActivity(null);

		assertUserVariableEqualsWithTimeout(userVariable, 3, 2000);
		assertUserVariableEqualsWithTimeout(userVariable2, 4, 2000);
	}
}
