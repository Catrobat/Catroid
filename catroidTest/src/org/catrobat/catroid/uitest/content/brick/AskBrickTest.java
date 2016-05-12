/**
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2013 The Catrobat Team
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

import android.app.Activity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.app.Instrumentation.ActivityResult;
import android.content.Intent;
import android.content.IntentFilter;
import android.speech.RecognizerIntent;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.AskBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;

public class AskBrickTest extends ActivityInstrumentationTestCase2<StageActivity> {

	private Instrumentation instrument;
	private Project project;
	private DataContainer dataContainer;
	private Sprite sprite;

	public AskBrickTest() {
		super(StageActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		ScreenValues.SCREEN_HEIGHT = 16;
		ScreenValues.SCREEN_WIDTH = 16;
		UiTestUtils.prepareStageForTest();
		instrument = getInstrumentation();
		createProject();
	}

	@Override
	public void tearDown() throws Exception {
		dataContainer.deleteUserVariableByName("answer");
		getActivity().finish();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	@Device
	public void testAskBrickWithSingleResult() {
		Intent mockResultIntent = new Intent();
		ArrayList<String> mockRecognizedWords = new ArrayList<String>();
		mockRecognizedWords.add("fun");
		mockRecognizedWords.add("sun");
		mockRecognizedWords.add("run");
		mockResultIntent.putStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS, mockRecognizedWords);

		ActivityResult mockResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, mockResultIntent);
		IntentFilter recognizeFilter = new IntentFilter(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		ActivityMonitor recognizeMonitor = instrument.addMonitor(recognizeFilter, mockResult, true);

		getActivity();

		instrument.waitForIdleSync();
		instrument.waitForMonitorWithTimeout(recognizeMonitor, 4000);
		assertEquals("Recognize intent wasn't fired as expected (no internet?), monitor hit", 1, recognizeMonitor
				.getHits());

		instrument.waitForIdleSync();
		assertEquals("Last Answer was not stored as expected.", mockRecognizedWords.get(0),
				dataContainer.getUserVariable("answer", sprite).getValue());

		instrument.waitForIdleSync();
		assertTrue("Stage didn't finish after AskAnswer returned.",
				ProjectManager.getInstance().getCurrentSprite().look.getAllActionsAreFinished());
	}

	@Device
	public void testCancelAndResetAskAnswer() {

		//Variable should be set to "" when user cancels
		dataContainer.getUserVariable("answer", sprite).setValue("preset");
		assertEquals("Uservariable was not saved via set", "preset", dataContainer.getUserVariable
				("answer", sprite).getValue());

		ArrayList<String> mockRecognizedWords = new ArrayList<String>();
		mockRecognizedWords.add("mock");

		ActivityResult mockResultCanceled = new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null);
		ActivityMonitor recognizeMonitor2 = instrument.addMonitor(new IntentFilter(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), mockResultCanceled, true);

		getActivity();

		instrument.waitForIdleSync();
		instrument.waitForMonitorWithTimeout(recognizeMonitor2, 4000);

		assertEquals("Recognize intent wasn't fired as expected (no internet?), monitor hit", 1, recognizeMonitor2.getHits());

		instrument.removeMonitor(recognizeMonitor2);
		instrument.waitForIdleSync();

		assertEquals("Canceld speech recognition did not reset variabel.", "", dataContainer.getUserVariable
				("answer", sprite).getValue());
		assertTrue("Stage didn't finish after AskBrick returned.",
				ProjectManager.getInstance().getCurrentSprite().look.getAllActionsAreFinished());
	}

	private void createProject() {
		project = new Project(instrument.getTargetContext(), UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		dataContainer = project.getDataContainer();
		dataContainer.addProjectUserVariable("answer");
		sprite = new Sprite("cat");
		Script startscript = new StartScript();
		AskBrick askBrick = new AskBrick("Wanna test?");
		askBrick.setUserVariable(dataContainer.getUserVariable("answer", sprite));
		Log.d("AskBrickTest", "setVariable to " + dataContainer.getUserVariable("answer", sprite).getValue());
		WaitBrick waitBrick = new WaitBrick(2000);

		startscript.addBrick(askBrick);
		startscript.addBrick(waitBrick);
		sprite.addScript(startscript);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(startscript);
	}
}
