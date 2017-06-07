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

package org.catrobat.catroid.uiespresso.content.brick;

import android.support.test.InstrumentationRegistry;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick;
import org.catrobat.catroid.content.bricks.NextLookBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.WhenConditionBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.FileTestUtils;
import org.catrobat.catroid.uiespresso.util.matchers.StageMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isFocusable;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils.checkIfBrickAtPositionShowsString;
import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils.onScriptList;
import static org.catrobat.catroid.uiespresso.content.brick.utils.SpinnerUtils.checkIfSpinnerOnBrickAtPositionShowsString;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;

public class BroadcastBricksTest {
	private String defaultMessage = "defaultMessage";
	private int broadcastSendPosition = 4;
	private int broadcastReceivePosition = 5;

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProjectAndGetStartScriptWithImages("BroadcastBrickTest");
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void checkBroadcastBricksStartUp() {
		checkIfBrickAtPositionShowsString(0, R.string.brick_when_started);
		checkIfBrickAtPositionShowsString(broadcastSendPosition, R.string.brick_broadcast);
		checkIfBrickAtPositionShowsString(broadcastReceivePosition, R.string.brick_broadcast_receive);
	}

	@Test
	public void checkBroadcastBricks() {
		byte[] red = {(byte) 237, (byte) 28, (byte) 36, (byte) 255};
		byte[] green = {(byte) 34, (byte) 177, (byte) 76, (byte) 255};
		onView(withId(R.id.button_play))
				.perform(click());

		onView(isFocusable()).check(matches(StageMatchers.isColorAtPx(red, 1, 1)));
		onView(isFocusable())
				.perform(click());
		onView(isFocusable()).check(matches(StageMatchers.isColorAtPx(green, 1, 1)));
	}

	@Test
	public void testRemoveUnusedMessagesBroadcastSend() {
		String uselessMessage = "useless";

		createNewMessageOnSpinner(R.id.brick_broadcast_spinner, broadcastSendPosition, uselessMessage);
		clickSelectCheckSpinnerValueOnBrick(R.id.brick_broadcast_spinner, broadcastSendPosition, defaultMessage);

		onView(withId(R.id.button_play))
				.perform(click());
		pressBack();
		onView(withId(R.id.stage_dialog_button_back))
				.perform(click());
		onScriptList().atPosition(broadcastSendPosition).onChildView(withId(R.id.brick_broadcast_spinner))
				.perform(click());

		onView(withText(uselessMessage)).check(doesNotExist());

		pressBack();

		checkIfSpinnerOnBrickAtPositionShowsString(R.id.brick_broadcast_spinner, broadcastSendPosition, defaultMessage);
	}

	@Test
	public void testRemoveUnusedMessagesBroadcastReceive() {
		String uselessMessage = "useless";
		createNewMessageOnSpinner(R.id.brick_broadcast_receive_spinner, broadcastReceivePosition, uselessMessage);
		clickSelectCheckSpinnerValueOnBrick(R.id.brick_broadcast_receive_spinner, broadcastReceivePosition,
				defaultMessage);

		onView(withId(R.id.button_play))
				.perform(click());
		pressBack();
		onView(withId(R.id.stage_dialog_button_back))
				.perform(click());
		onScriptList().atPosition(broadcastReceivePosition).onChildView(withId(R.id.brick_broadcast_receive_spinner))
				.perform(click());

		onView(withText(uselessMessage)).check(doesNotExist());

		pressBack();

		checkIfSpinnerOnBrickAtPositionShowsString(R.id.brick_broadcast_receive_spinner, broadcastReceivePosition,
				defaultMessage);
	}

	public Script createProjectAndGetStartScriptWithImages(String projectName) {
		Project project = new Project(null, projectName);
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript();

		sprite.addScript(script);

		LookData redLookData = new LookData();
		String redImageName = "red_image.bmp";
		redLookData.setLookName(redImageName);
		sprite.getLookDataList().add(redLookData);

		LookData greenLookData = new LookData();
		String greenImageName = "green_image.bmp";
		greenLookData.setLookName(greenImageName);
		sprite.getLookDataList().add(greenLookData);

		script.addBrick(new PlaceAtBrick(0, 0));
		script.addBrick(new SetSizeToBrick(5000));
		Formula condition = new Formula(new FormulaElement(FormulaElement.ElementType.SENSOR,
				Sensors.COLLIDES_WITH_FINGER.name(), null));
		script.addBrick(new WhenConditionBrick(condition));
		script.addBrick(new BroadcastBrick(defaultMessage));
		script.addBrick(new BroadcastReceiverBrick(defaultMessage));
		script.addBrick(new NextLookBrick());
		sprite.addScript(script);

		project.getDefaultScene().addSprite(sprite);

		StorageHandler.getInstance().saveProject(project);
		File redImageFile = FileTestUtils.saveFileToProject(project.getName(), project.getDefaultScene().getName(),
				redImageName,
				org.catrobat.catroid.test.R.raw.red_image, InstrumentationRegistry.getContext(),
				FileTestUtils.FileTypes.IMAGE);

		File greenImageFile = FileTestUtils.saveFileToProject(project.getName(), project.getDefaultScene().getName(),
				greenImageName,
				org.catrobat.catroid.test.R.raw.green_image, InstrumentationRegistry.getContext(),
				FileTestUtils.FileTypes.IMAGE);

		redLookData.setLookFilename(redImageFile.getName());
		greenLookData.setLookFilename(greenImageFile.getName());

		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		return script;
	}

	public static void createNewMessageOnSpinner(int spinnerResourceId, int position, String massage) {
		onScriptList().atPosition(position).onChildView(withId(spinnerResourceId))
				.perform(click());

		onView(withText(R.string.brick_variable_spinner_create_new_variable))
				.perform(click());

		onView(withId(R.id.edit_text))
				.perform(click());
		onView(withId(R.id.edit_text))
				.perform(clearText());
		onView(withId(R.id.edit_text))
				.perform(typeText(massage));
		onView(withId(android.R.id.button1))
				.perform(click());
		// todo: CAT-2359 to fix this:
		checkIfSpinnerOnBrickAtPositionShowsString(spinnerResourceId, position, massage);
	}

	public static void clickSelectCheckSpinnerValueOnBrick(int spinnerResourceId, int position, String
			stringResource) {
		onScriptList().atPosition(position).onChildView(withId(spinnerResourceId))
				.perform(click());
		onData(allOf(is(instanceOf(String.class)), is(stringResource)))
				.perform(click());
		checkIfSpinnerOnBrickAtPositionShowsString(spinnerResourceId, position, stringResource);
	}
}
