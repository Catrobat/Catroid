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
package org.catrobat.catroid.uitest.stage;

import java.util.ArrayList;
import java.util.HashMap;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.actions.SpeakAction;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.SpeakBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.MyProjectsActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.widget.ListView;

public class SpeakStageTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private TextToSpeechMock textToSpeechMock;
	private Sprite spriteNormal, spriteNull, spriteInterrupt;
	private String textMessageTest = "This is a test text.";
	private String textMessageHello = "Hello World!";
	private String textMessageInterrupt = "Interrupt!";
	private String textMessageLong = "This is very very long long test text.";

	public SpeakStageTest() throws InterruptedException {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createProjectToInitializeTextToSpeech();
		UiTestUtils.prepareStageForTest();
		textToSpeechMock = new TextToSpeechMock(getActivity().getApplicationContext());
		Reflection.setPrivateField(SpeakAction.class, "utteranceIdPool", 0);
	}

	@Override
	public void tearDown() throws Exception {
		textToSpeechMock = null;
		super.tearDown();
	}

	public void testNullText() throws InterruptedException {
		ProjectManager.INSTANCE
				.loadProject(UiTestUtils.PROJECTNAME2, getActivity().getApplicationContext(), false);
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		String programString = solo.getString(R.string.main_menu_programs);
		solo.waitForText(programString);
		solo.clickOnButton(programString);
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForView(ListView.class);
		UiTestUtils.clickOnTextInList(solo, UiTestUtils.PROJECTNAME2);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.waitForView(ListView.class);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.sleep(1000);
		TextToSpeech textToSpeech = (TextToSpeech) Reflection.getPrivateField(PreStageActivity.class, "textToSpeech");
		textToSpeechMock.setTextToSpeech(textToSpeech);
		Reflection.setPrivateField(PreStageActivity.class, "textToSpeech", textToSpeechMock);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(3000);
		assertEquals("TextToSpeech executed with wrong parameter", TextToSpeech.QUEUE_FLUSH, textToSpeechMock.queueMode);
		assertEquals("TextToSpeech exectuted with wrong utterance id", "0",
				textToSpeechMock.parameters.get(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID));
		assertEquals("TextToSpeech executed with wrong text", "", textToSpeechMock.text);
	}

	public void testSuccessiveBehaviour() throws InterruptedException {
		ProjectManager.INSTANCE
				.loadProject(UiTestUtils.PROJECTNAME1, getActivity().getApplicationContext(), false);
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		String programString = solo.getString(R.string.main_menu_programs);
		solo.waitForText(programString);
		solo.clickOnButton(programString);
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForView(ListView.class);
		UiTestUtils.clickOnTextInList(solo, UiTestUtils.PROJECTNAME1);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.waitForView(ListView.class);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.sleep(1000);
		TextToSpeech textToSpeech = (TextToSpeech) Reflection.getPrivateField(PreStageActivity.class, "textToSpeech");
		textToSpeechMock.setTextToSpeech(textToSpeech);
		Reflection.setPrivateField(PreStageActivity.class, "textToSpeech", textToSpeechMock);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(2000);
		assertEquals("TextToSpeech executed with wrong parameter", TextToSpeech.QUEUE_FLUSH, textToSpeechMock.queueMode);
		assertEquals("TextToSpeech exectuted with wrong utterance id", "0",
				textToSpeechMock.parameters.get(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID));
		assertEquals("TextToSpeech executed with wrong text", textMessageTest, textToSpeechMock.text);
		solo.sleep(2000);
		assertEquals("TextToSpeech executed with wrong parameter", TextToSpeech.QUEUE_FLUSH, textToSpeechMock.queueMode);
		assertEquals("TextToSpeech exectuted with wrong utterance id", "1",
				textToSpeechMock.parameters.get(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID));
		assertEquals("TextToSpeech executed with wrong text", textMessageHello, textToSpeechMock.text);
	}

	public void testSimultaneousTextToSpeech() throws InterruptedException {
		ProjectManager.INSTANCE
				.loadProject(UiTestUtils.PROJECTNAME3, getActivity().getApplicationContext(), false);
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		String programString = solo.getString(R.string.main_menu_programs);
		solo.waitForText(programString);
		solo.clickOnButton(programString);
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForView(ListView.class);
		UiTestUtils.clickOnTextInList(solo, UiTestUtils.PROJECTNAME3);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.waitForView(ListView.class);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.sleep(1000);
		TextToSpeech textToSpeech = (TextToSpeech) Reflection.getPrivateField(PreStageActivity.class, "textToSpeech");
		textToSpeechMock.setTextToSpeech(textToSpeech);
		Reflection.setPrivateField(PreStageActivity.class, "textToSpeech", textToSpeechMock);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(2000);
		assertEquals("TextToSpeech executed with wrong parameter", TextToSpeech.QUEUE_FLUSH, textToSpeechMock.queueMode);
		assertEquals("TextToSpeech exectuted with wrong utterance id", "0",
				textToSpeechMock.parameters.get(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID));
		assertEquals("TextToSpeech executed with wrong text", textMessageLong, textToSpeechMock.text);
		solo.sleep(2000);
		assertEquals("TextToSpeech executed with wrong parameter", TextToSpeech.QUEUE_FLUSH, textToSpeechMock.queueMode);
		assertEquals("TextToSpeech exectuted with wrong utterance id", "1",
				textToSpeechMock.parameters.get(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID));
		assertEquals("TextToSpeech executed with wrong text", textMessageInterrupt, textToSpeechMock.text);
	}

	private void createProjectToInitializeTextToSpeech() {
		spriteNormal = new Sprite("testNormalBehaviour");

		Script startScriptNormal = new StartScript(spriteNormal);
		WaitBrick waitBrickNormal = new WaitBrick(spriteNormal, 1000);
		SpeakBrick speakBrickNormal = new SpeakBrick(spriteNormal, textMessageTest);
		BroadcastBrick broadcastBrickNormal = new BroadcastBrick(spriteNormal, "normal");
		startScriptNormal.addBrick(waitBrickNormal);
		startScriptNormal.addBrick(speakBrickNormal);
		startScriptNormal.addBrick(broadcastBrickNormal);
		WaitBrick waitBrickNormal2 = new WaitBrick(spriteNormal, 1000);
		startScriptNormal.addBrick(waitBrickNormal2);
		SpeakBrick speakBrickNormal2 = new SpeakBrick(spriteNormal, textMessageHello);
		startScriptNormal.addBrick(speakBrickNormal2);

		spriteNormal.addScript(startScriptNormal);

		ArrayList<Sprite> spriteListNormal = new ArrayList<Sprite>();
		spriteListNormal.add(spriteNormal);

		UiTestUtils.createProject(UiTestUtils.PROJECTNAME1, spriteListNormal, getActivity().getApplicationContext());

		spriteNull = new Sprite("testNullText");
		Script startScriptNull = new StartScript(spriteNull);
		WaitBrick waitBrickNull = new WaitBrick(spriteNull, 1000);
		SpeakBrick speakBrickNull = new SpeakBrick(spriteNull, null);
		startScriptNull.addBrick(waitBrickNull);
		startScriptNull.addBrick(speakBrickNull);

		spriteNull.addScript(startScriptNull);

		ArrayList<Sprite> spriteListNull = new ArrayList<Sprite>();
		spriteListNull.add(spriteNull);

		UiTestUtils.createProject(UiTestUtils.PROJECTNAME2, spriteListNull, getActivity().getApplicationContext());

		spriteInterrupt = new Sprite("testInterrupt");

		Script startScriptInterrupt = new StartScript(spriteInterrupt);
		WaitBrick waitBrickInterrupt = new WaitBrick(spriteNull, 1000);
		BroadcastBrick broadcastBrick = new BroadcastBrick(spriteInterrupt, "double");
		SpeakBrick speakBrickInterrupt = new SpeakBrick(spriteInterrupt, textMessageLong);
		startScriptInterrupt.addBrick(waitBrickInterrupt);
		startScriptInterrupt.addBrick(broadcastBrick);
		startScriptInterrupt.addBrick(speakBrickInterrupt);

		spriteInterrupt.addScript(startScriptInterrupt);

		BroadcastScript broadcastScriptInterrupt = new BroadcastScript(spriteInterrupt, "double");
		WaitBrick waitBrickInterrupt2 = new WaitBrick(spriteInterrupt, 2000);
		broadcastScriptInterrupt.addBrick(waitBrickInterrupt2);
		SpeakBrick speakBrickInterrupt2 = new SpeakBrick(spriteInterrupt, textMessageInterrupt);
		broadcastScriptInterrupt.addBrick(speakBrickInterrupt2);

		spriteInterrupt.addScript(broadcastScriptInterrupt);

		ArrayList<Sprite> spriteListInterrupt = new ArrayList<Sprite>();
		spriteListInterrupt.add(spriteInterrupt);

		UiTestUtils.createProject(UiTestUtils.PROJECTNAME3, spriteListInterrupt, getActivity().getApplicationContext());
	}

	private class TextToSpeechMock extends TextToSpeech {
		private TextToSpeech textToSpeech = null;

		protected String text;
		protected int queueMode = -1;
		protected HashMap<String, String> parameters;

		public TextToSpeechMock(Context context) {
			super(context, new OnInitListener() {
				public void onInit(int status) {
					if (status != SUCCESS) {
						fail("TextToSpeech couldn't be initialized");
					}
				}
			});
		}

		public void setTextToSpeech(TextToSpeech textToSpeech) {
			this.textToSpeech = textToSpeech;
		}

		@Override
		public int speak(String text, int queueMode, HashMap<String, String> parameters) {
			this.text = text;
			this.queueMode = queueMode;
			this.parameters = parameters;
			int returnValue = textToSpeech.speak(text, queueMode, parameters);
			return returnValue;
		}
	}
}
