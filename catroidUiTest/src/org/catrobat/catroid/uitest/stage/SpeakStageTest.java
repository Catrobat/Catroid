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
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
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
		PreStageActivity.textToSpeech(null, null, null);
		assertEquals("Null-text isn't converted into empty string", "", textToSpeechMock.text);
	}

	public void testNormalBehavior() throws InterruptedException {
		String text = "Hello world!";
		SpeakBrick speakBrick = new SpeakBrick(null, text);

		NonBlockingSpeakBrickExecutionThread speakBrickThread = new NonBlockingSpeakBrickExecutionThread(speakBrick);
		assertFalse("SpeakBrick already executed", speakBrickThread.isFinished());

		speakBrickThread.start();
		speakBrickThread.join(2000);

		String utteranceId = String.valueOf(speakBrick.hashCode());
		assertEquals("TextToSpeech executed with wrong parameter", TextToSpeech.QUEUE_FLUSH, textToSpeechMock.queueMode);
		assertEquals("TextToSpeech complete listener was called with wrong utteranceId", utteranceId,
				textToSpeechMock.mockListener.utteranceId);

		HashMap<String, String> speakParameter = new HashMap<String, String>();
		speakParameter.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId);
		assertEquals("TextToSpeech executed with wrong parameter", speakParameter, textToSpeechMock.parameters);

		assertTrue("SpeakBrick not finished yet", speakBrickThread.isFinished());
		assertEquals("TextToSpeech executed with wrong text", text, textToSpeechMock.text);
	}

	public void testSimultaneousTextToSpeech() throws InterruptedException {
		SpeakBrick longTextSpeakBrick = new SpeakBrick(null, "This very long text will be interrupted.");
		NonBlockingSpeakBrickExecutionThread longTextSpeakBrickThread = new NonBlockingSpeakBrickExecutionThread(
				longTextSpeakBrick);

		SpeakBrick shortTextSpeakBrick = new SpeakBrick(null, "Interrupt!");
		NonBlockingSpeakBrickExecutionThread shortTextSpeakBrickThread = new NonBlockingSpeakBrickExecutionThread(
				shortTextSpeakBrick);

		longTextSpeakBrickThread.start();
		solo.sleep(100);

		assertFalse("SpeakBrick with long text already executed", longTextSpeakBrickThread.isFinished());
		assertFalse("SpeakBrick with short text already executed", shortTextSpeakBrickThread.isFinished());

		shortTextSpeakBrickThread.start();
		shortTextSpeakBrickThread.join(5000);

		assertTrue("SpeakBrick with long text not finished yet", longTextSpeakBrickThread.isFinished());
		assertTrue("SpeakBrick with short text not finished yet", shortTextSpeakBrickThread.isFinished());
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

		public NonBlockingSpeakBrickExecutionThread(Brick speakBrick) {
			this.speakBrick = speakBrick;
		}

		public boolean isFinished() {
			return finished;
		}

		@Override
		public void run() {
			speakBrick.execute();
			finished = true;
		}
	}

	private class TextToSpeechMock extends TextToSpeech {
		private final TextToSpeech textToSpeech;
		protected OnUtteranceCompletedListenerMock mockListener;

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
		@Deprecated
		public int setOnUtteranceCompletedListener(OnUtteranceCompletedListener listener) {
			mockListener = new OnUtteranceCompletedListenerMock(listener);
			return textToSpeech.setOnUtteranceCompletedListener(mockListener);
		}

		@Override
		public int speak(String text, int queueMode, HashMap<String, String> parameters) {
			this.text = text;
			this.queueMode = queueMode;
			this.parameters = parameters;

			return textToSpeech.speak(text, queueMode, parameters);
		}
	}

	private class OnUtteranceCompletedListenerMock implements OnUtteranceCompletedListener {
		private final OnUtteranceCompletedListener listener;
		protected String utteranceId;

		public OnUtteranceCompletedListenerMock(OnUtteranceCompletedListener listener) {
			this.listener = listener;
		}

		public void onUtteranceCompleted(String utteranceId) {
			this.utteranceId = utteranceId;
			listener.onUtteranceCompleted(utteranceId);
		}
	}
}
