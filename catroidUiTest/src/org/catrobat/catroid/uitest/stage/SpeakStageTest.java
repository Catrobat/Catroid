package org.catrobat.catroid.uitest.stage;

import java.util.HashMap;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.FileChecksumContainer;
import org.catrobat.catroid.common.Values;
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
	private Brick speakBrick;
	private String text = "Hello world!";

	public SpeakStageTest() {
		super(PreStageActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());

		TextToSpeech textToSpeech = (TextToSpeech) UiTestUtils.getPrivateField("textToSpeech", PreStageActivity.class);
		textToSpeechMock = new TextToSpeechMock(getActivity().getApplicationContext(), textToSpeech);
		UiTestUtils.setPrivateField2(PreStageActivity.class, null, "textToSpeech", textToSpeechMock);
		solo.sleep(1000);
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	public void testNormalBehavior() throws InterruptedException {
		Timeout timeout = new Timeout(speakBrick);
		Thread timeoutThread = new Thread(timeout);
		timeoutThread.start();
		timeoutThread.join(5000);

		assertTrue(timeout.isFinished());
		assertEquals(text, textToSpeechMock.text);
		assertEquals(TextToSpeech.QUEUE_FLUSH, textToSpeechMock.queueMode);

		String utteranceId = String.valueOf(speakBrick.hashCode());
		assertEquals(utteranceId, String.valueOf(textToSpeechMock.mockListener.utteranceId));

		HashMap<String, String> speakParameter = new HashMap<String, String>();
		speakParameter.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId);
		assertTrue(speakParameter.equals(textToSpeechMock.params));
	}

	//	public void testNullText() {
	//		TextToSpeechMock textToSpeechMock = new TextToSpeechMock(getActivity().getApplicationContext(),
	//				PreStageActivity.textToSpeech);
	//		PreStageActivity.textToSpeech = textToSpeechMock;
	//
	//		PreStageActivity.textToSpeech(null, null, null);
	//		assertEquals("", textToSpeechMock.text);
	//	}

	private void createProject() {
		Values.SCREEN_WIDTH = 20;
		Values.SCREEN_HEIGHT = 20;

		Project project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");

		Script startScript = new StartScript(sprite);
		speakBrick = new SpeakBrick(sprite, text);
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
			System.out.println("LOG: isFinished" + finished);
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

		public TextToSpeechMock(Context context, TextToSpeech textToSpeech) {
			super(context, new OnInitListener() {
				public void onInit(int status) {
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
