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

import java.util.HashMap;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.FileChecksumContainer;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.SpeakBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class SpeakStageTest extends ActivityInstrumentationTestCase2<PreStageActivity> {
	private Solo solo;
	private TextToSpeechMock textToSpeechMock;

	public SpeakStageTest() {
		super(PreStageActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());

		TextToSpeech textToSpeech = (TextToSpeech) Reflection.getPrivateField(PreStageActivity.class, "textToSpeech");
		textToSpeechMock = new TextToSpeechMock(getActivity().getApplicationContext(), textToSpeech, this);
		synchronized (this) {
			wait(2000);
		}

		Reflection.setPrivateField(PreStageActivity.class, "textToSpeech", textToSpeechMock);
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	public void testNullText() {
		assertEquals("Initialized TextToSpeechMock with wrong text value", null, textToSpeechMock.text);
		PreStageActivity.textToSpeech(null, null, new HashMap<String, String>());
		assertEquals("Null-text isn't converted into empty string", "", textToSpeechMock.text);
	}

	public void testNormalBehavior() throws InterruptedException {
		String text = "Hello.";
		SpeakBrick speakBrick = new SpeakBrick(null, text);
		String utteranceId = String.valueOf(speakBrick.hashCode());
		NonBlockingSpeakBrickExecutionThread speakBrickThread = new NonBlockingSpeakBrickExecutionThread(speakBrick);

		assertFalse("SpeakBrick already executed", speakBrickThread.isFinished());

		speakBrickThread.start();
		speakBrickThread.join(2000);

		assertEquals("TextToSpeech executed with wrong parameter", TextToSpeech.QUEUE_FLUSH, textToSpeechMock.queueMode);

		HashMap<String, String> speakParameter = new HashMap<String, String>();
		speakParameter.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId);
		assertEquals("TextToSpeech executed with wrong parameter", speakParameter, textToSpeechMock.parameters);

		assertTrue("SpeakBrick not finished yet", speakBrickThread.isFinished());
		assertEquals("TextToSpeech executed with wrong text", text, textToSpeechMock.text);
	}

	public void testSuccessiveTextToSpeech() throws InterruptedException {
		SpeakBrick firstSpeakBrick = new SpeakBrick(null, "Hello");
		NonBlockingSpeakBrickExecutionThread firstSpeakBrickThread = new NonBlockingSpeakBrickExecutionThread(
				firstSpeakBrick);

		SpeakBrick secondSpeakBrick = new SpeakBrick(null, "World");
		NonBlockingSpeakBrickExecutionThread secondSpeakBrickThread = new NonBlockingSpeakBrickExecutionThread(
				secondSpeakBrick);

		firstSpeakBrickThread.start();
		firstSpeakBrickThread.join(1500);
		assertTrue("First SpeakBrick not finished yet", firstSpeakBrickThread.finished);

		secondSpeakBrickThread.start();
		secondSpeakBrickThread.join(1500);
		assertTrue("First SpeakBrick not finished yet", secondSpeakBrickThread.finished);
	}

	public void testSimultaneousTextToSpeech() throws InterruptedException {
		SpeakBrick firstSpeakBrick = new SpeakBrick(null, "This very long text will be interrupted.");
		NonBlockingSpeakBrickExecutionThread firstSpeakBrickThread = new NonBlockingSpeakBrickExecutionThread(
				firstSpeakBrick);

		SpeakBrick interruptSpeakBrick = new SpeakBrick(null, "Interrupt.");
		NonBlockingSpeakBrickExecutionThread interruptSpeakBrickThread = new NonBlockingSpeakBrickExecutionThread(
				interruptSpeakBrick);

		firstSpeakBrickThread.start();
		synchronized (this) {
			wait(250);
		}
		assertFalse("First SpeakBrick already executed", firstSpeakBrickThread.isFinished());
		assertFalse("Interrupted SpeakBrick already executed", interruptSpeakBrickThread.isFinished());

		interruptSpeakBrickThread.start();
		synchronized (this) {
			wait(100);
		}
		assertTrue("First SpeakBrick not finished yet", firstSpeakBrickThread.isFinished());
		assertFalse("Interrupted SpeakBrick not finished yet", interruptSpeakBrickThread.isFinished());

		interruptSpeakBrickThread.join(1500);
		assertTrue("Interrupted SpeakBrick not finished yet", interruptSpeakBrickThread.isFinished());
	}

	private void createProject() {
		Project project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");

		Script startScript = new StartScript(sprite);
		SpeakBrick speakBrick = new SpeakBrick(sprite, "");
		startScript.addBrick(speakBrick);

		sprite.addScript(startScript);
		project.addSprite(sprite);

		ProjectManager projectManager = ProjectManager.getInstance();
		projectManager.setFileChecksumContainer(new FileChecksumContainer());
		projectManager.setProject(project);
		projectManager.setCurrentSprite(sprite);
		projectManager.setCurrentScript(startScript);

		projectManager.setProject(project);
		StorageHandler.getInstance().saveProject(project);
	}

	private class NonBlockingSpeakBrickExecutionThread extends Thread {
		private final Brick speakBrick;
		private boolean finished = false;

		private long elapsedTime = -1;

		public NonBlockingSpeakBrickExecutionThread(Brick speakBrick) {
			this.speakBrick = speakBrick;
		}

		public boolean isFinished() {
			return finished;
		}

		@SuppressWarnings("unused")
		// The method estimates the wait and join timeouts used in this test.
		public long getElapsedTime() {
			return elapsedTime;
		}

		@Override
		public void run() {
			long start = System.currentTimeMillis();
			speakBrick.execute();
			elapsedTime = System.currentTimeMillis() - start;
			finished = true;
		}
	}

	private class TextToSpeechMock extends TextToSpeech {
		private final TextToSpeech textToSpeech;

		protected String text;
		protected int queueMode = -1;
		protected HashMap<String, String> parameters;

		public TextToSpeechMock(Context context, TextToSpeech textToSpeech, final SpeakStageTest speakStageTest) {
			super(context, new OnInitListener() {
				public void onInit(int status) {
					synchronized (speakStageTest) {
						speakStageTest.notifyAll();
					}
				}
			});

			this.textToSpeech = textToSpeech;
		}

		@Override
		public int speak(String text, int queueMode, HashMap<String, String> parameters) {
			this.text = text;
			this.queueMode = queueMode;
			this.parameters = parameters;

			return textToSpeech.speak(text, queueMode, parameters);
		}
	}
}
