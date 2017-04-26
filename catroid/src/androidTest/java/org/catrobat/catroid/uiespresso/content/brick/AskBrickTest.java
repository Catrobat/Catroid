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

import android.app.Activity;
import android.app.Instrumentation;
import android.content.IntentFilter;
import android.speech.RecognizerIntent;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.AskBrick;
import org.catrobat.catroid.content.bricks.AskSpeechBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.checkIfBrickAtPositionShowsString;
import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.clickSelectCheckSpinnerValueOnBrick;
import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.createNewVariableOnSpinner;
import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.createNewVariableOnSpinnerInitial;
import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.enterStringInFormulaTextFieldOnBrickAtPosition;
import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.onScriptList;
import static org.catrobat.catroid.uiespresso.util.UiTestUtils.getResourcesString;
import static org.catrobat.catroid.utils.Utils.getActivity;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;

public class AskBrickTest {
	private int brickPosition;
	private Instrumentation instrument;
	private DataContainer dataContainer;
	private Sprite sprite;
	private Project project;



	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		brickPosition = 1;
		Script script = BrickTestUtils.createProjectAndGetStartScript("askBricksTest");
		script.addBrick(new AskBrick(getResourcesString(R.string.brick_ask_default_question)));

		instrument = getInstrumentation();
		baseActivityTestRule.launchActivity(null);
		project = ProjectManager.getInstance().getCurrentProject();

	}

	@Test
	public void testAskBrick() {
		String question = "Will it work?";
		checkIfBrickAtPositionShowsString(0, R.string.brick_when_started);
		checkIfBrickAtPositionShowsString(brickPosition, R.string.brick_ask_label);
		checkIfBrickAtPositionShowsString(brickPosition, R.string.brick_ask_store);
		enterStringInFormulaTextFieldOnBrickAtPosition(question, R.id.brick_ask_question_edit_text, brickPosition);
	}

	@Test
	public void testCreatingNewVariable() {
		final String firstVariableName = "firstVariable";
		final String secondVariableName = "secondVariable";
		checkIfBrickAtPositionShowsString(0, R.string.brick_when_started);
		checkIfBrickAtPositionShowsString(brickPosition, R.string.brick_ask_label);
		checkIfBrickAtPositionShowsString(brickPosition, R.string.brick_ask_store);
		createNewVariableOnSpinnerInitial(R.id.brick_ask_spinner, brickPosition, firstVariableName);
		createNewVariableOnSpinner(R.id.brick_ask_spinner, brickPosition, secondVariableName);
		/*onView(withId(R.id.brick_ask_spinner)).perform(click());
		onView(withId(R.id.dialog_formula_editor_data_name_edit_text)).perform(typeText(variableName));
		onView(withText(R.string.ok)).perform(click());*/
	}

	@Test
	public void testNewVariableCanceling() {
		onView(withId(R.id.brick_ask_spinner)).perform(click());
		onView(withText(R.string.cancel)).perform(click());
		checkIfBrickAtPositionShowsString(0, R.string.brick_when_started);
		checkIfBrickAtPositionShowsString(brickPosition, R.string.brick_ask_label);
		checkIfBrickAtPositionShowsString(brickPosition, R.string.brick_ask_store);
	}
	@Test
	public void testCancelAndResetAskAnswer() throws ClassNotFoundException, NoSuchMethodException, NoSuchFieldException, InvocationTargetException, IllegalAccessException {
		final String question = "Will it work?";
		final String firstVariableName = "firstVariable";
		final String secondVariableName = "secondVariable";

		createNewVariableOnSpinnerInitial(R.id.brick_ask_spinner, brickPosition, firstVariableName);
		createNewVariableOnSpinner(R.id.brick_ask_spinner, brickPosition, secondVariableName);

		clickSelectCheckSpinnerValueOnBrick(R.id.brick_ask_spinner, brickPosition, secondVariableName);


	/*
		//Variable should be set to "" when user cancels
		dataContainer.getUserVariable("answer", sprite).setValue("preset");
		assertEquals("Uservariable was not saved via set", "preset",
				dataContainer.getUserVariable("answer", sprite).getValue());

		ArrayList<String> mockRecognizedWords = new ArrayList<String>();
		mockRecognizedWords.add("mock");

		Instrumentation.ActivityResult mockResultCanceled = new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null);
		Instrumentation.ActivityMonitor recognizeMonitor2 = instrument.addMonitor(new IntentFilter(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), mockResultCanceled, true);

		getActivity();

		instrument.waitForIdleSync();
		instrument.waitForMonitorWithTimeout(recognizeMonitor2, 4000);

		assertEquals("Recognize intent wasn't fired as expected (no internet?), monitor hit", 1, recognizeMonitor2.getHits());

		instrument.removeMonitor(recognizeMonitor2);
		instrument.waitForIdleSync();

		assertEquals("Canceld speech recognition did not reset variabel.", "",
				dataContainer.getUserVariable("answer", sprite).getValue());
		assertTrue("Stage didn't finish after AskBrick returned.",
				ProjectManager.getInstance().getCurrentSprite().look.getAllActionsAreFinished());*/
	}

/*	private void createProject() {
		project = new Project(instrument.getTargetContext(), UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Scene startScene = new Scene(null, "AskBrickTest", project);
		project.addScene(startScene);
		dataContainer = startScene.getDataContainer();
		dataContainer.addProjectUserVariable("answer");
		sprite = new Sprite("cat");
		Script startscript = new StartScript();
		AskSpeechBrick askBrick = new AskSpeechBrick("Wanna test?");
		askBrick.setUserVariable(dataContainer.getUserVariable("answer", sprite));
		Log.d("AskSpeechBrickTest", "setVariable to " + dataContainer.getUserVariable("answer", sprite).getValue());
		WaitBrick waitBrick = new WaitBrick(2000);

		startscript.addBrick(askBrick);
		startscript.addBrick(waitBrick);
		sprite.addScript(startscript);
		startScene.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentScene(startScene);
		ProjectManager.getInstance().setCurrentSprite(sprite);
	}*/
}
