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

		TextToSpeech textToSpeech = (TextToSpeech) UiTestUtils.getPrivateField("textToSpeech", PreStageActivity.class);
		textToSpeechMock = new TextToSpeechMock(getActivity().getApplicationContext(), textToSpeech, this);
		synchronized (this) {
			wait(500);
		}

		UiTestUtils.setPrivateField2(PreStageActivity.class, null, "textToSpeech", textToSpeechMock);
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
		assertEquals("TextToSpeech has already been called", null, textToSpeechMock.text);
		PreStageActivity.textToSpeech(null, null, null);
		assertEquals("Called TextToSpeech with wrong text", "", textToSpeechMock.text);
	}

	public void testTextToSpeechParameter() throws InterruptedException {
		String text = "A";
		SpeakBrick speakBrick = new SpeakBrick(null, text);
		assertEquals("TextToSpeech executed with wrong parameter", TextToSpeech.QUEUE_FLUSH, textToSpeechMock.queueMode);

		Timeout timeout = new Timeout(speakBrick);
		Thread timeoutThread = new Thread(timeout);
		assertFalse("SpeakBrick already executed", timeout.isFinished());

		timeoutThread.start();
		timeoutThread.join(2000);

		String utteranceId = String.valueOf(speakBrick.hashCode());
		assertEquals("TextToSpeech executed with wrong utterance id", utteranceId,
				String.valueOf(textToSpeechMock.mockListener.utteranceId));

		HashMap<String, String> speakParameter = new HashMap<String, String>();
		speakParameter.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId);
		assertTrue("TextToSpeech executed with wrong parameter", speakParameter.equals(textToSpeechMock.params));
	}

	public void testNormalBehavior() throws InterruptedException {
		String text = "Hello";
		SpeakBrick speakBrick = new SpeakBrick(null, text);

		Timeout timeout = new Timeout(speakBrick);
		Thread timeoutThread = new Thread(timeout);
		timeoutThread.start();
		timeoutThread.join(2000);

		assertTrue("SpeakBrick not finished yet", timeout.isFinished());
		assertEquals("TextToSpeech executed with wrong text", text, textToSpeechMock.text);

		text = "World";
		speakBrick = new SpeakBrick(null, text);

		timeout = new Timeout(speakBrick);
		timeoutThread = new Thread(timeout);
		timeoutThread.start();
		timeoutThread.join(2000);

		assertTrue("SpeakBrick not finished yet", timeout.isFinished());
		assertEquals("TextToSpeech executed with wrong text", text, textToSpeechMock.text);
	}

	public void testSimultaneousTextToSpeech() throws InterruptedException {
		SpeakBrick longTextSpeakBrick = new SpeakBrick(null, "A very long text will be interrupted.");
		Timeout longTextTimeout = new Timeout(longTextSpeakBrick);
		Thread longTextTimeoutThread = new Thread(longTextTimeout);

		SpeakBrick shortTextSpeakBrick = new SpeakBrick(null, "Interrupt!");
		Timeout shortTextTimeout = new Timeout(shortTextSpeakBrick);
		Thread shortTextTimeoutThread = new Thread(shortTextTimeout);

		longTextTimeoutThread.start();
		solo.sleep(100);

		assertFalse("SpeakBrick with long text already executed", longTextTimeout.isFinished());
		assertFalse("SpeakBrick with short text already executed", shortTextTimeout.isFinished());

		shortTextTimeoutThread.start();
		shortTextTimeoutThread.join(5000);

		assertTrue("SpeakBrick with long text not finished yet", longTextTimeout.isFinished());
		assertTrue("SpeakBrick with short text not finished yet", shortTextTimeout.isFinished());
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

	private class Timeout implements Runnable {
		private final Brick speakBrick;
		private boolean finished = false;

		public Timeout(Brick speakBrick) {
			this.speakBrick = speakBrick;
		}

		public boolean isFinished() {
			return finished;
		}

		public void run() {
			speakBrick.execute();
			finished = true;
		}
	}

	private class TextToSpeechMock extends TextToSpeech {
		private final TextToSpeech textToSpeech;
		protected OnUtteranceCompletedListenerMock mockListener;

		protected String text;
		protected int queueMode;
		protected HashMap<String, String> params;

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
		public int speak(String text, int queueMode, HashMap<String, String> params) {
			this.text = text;
			this.queueMode = queueMode;
			this.params = params;

			return textToSpeech.speak(text, queueMode, params);
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
