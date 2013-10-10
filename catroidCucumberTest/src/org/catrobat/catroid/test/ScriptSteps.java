package org.catrobat.catroid.test;

import android.test.AndroidTestCase;
import android.util.Log;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.jayway.android.robotium.solo.Solo;
import cucumber.api.android.CucumberInstrumentation;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import org.catrobat.catroid.content.*;
import org.catrobat.catroid.content.bricks.ShowBrick;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class ScriptSteps extends AndroidTestCase {
	private final Semaphore mScriptEndWaitLock = new Semaphore(0);

	@Given("^a StartScript$")
	public void start_script() {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		StartScript script = new StartScript(object);
		object.addScript(script);
		Cucumber.put(Cucumber.KEY_CURRENT_SCRIPT, script);
	}

	@Given("^a WhenScript '(\\w+)'$")
	public void when_script(String name) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		WhenScript script = new WhenScript(object);
		Map<String, Integer> actions = new HashMap<String, Integer>();
		actions.put("tapped", 0);
		actions.put("doubletapped", 1);
		actions.put("longpressed", 2);
		actions.put("swipeup", 3);
		actions.put("swipedown", 4);
		actions.put("swipeleft", 5);
		actions.put("swiperight", 6);
		script.setAction(actions.get(name));
		object.addScript(script);
		Cucumber.put(Cucumber.KEY_CURRENT_SCRIPT, script);
	}

	@Given("^a BroadcastScript '(\\w+)'$")
	public void broadcast_script(String message) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		BroadcastScript script = new BroadcastScript(object, message);
		object.addScript(script);
		Cucumber.put(Cucumber.KEY_CURRENT_SCRIPT, script);
	}

	@When("^the script terminates$")
	public void script_terminates() {
		try {
			mScriptEndWaitLock.tryAcquire(1, 30, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}
	}

	@When("^the script is executed$")
	public void script_is_executed() {
		Solo solo = (Solo) Cucumber.get(Cucumber.KEY_SOLO);
		assertEquals(MainMenuActivity.class, solo.getCurrentActivity().getClass());

		Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);
		Sprite object = script.getScriptBrick().getSprite();
		script.addBrick(new ScriptEndBrick(object, new ScriptEndCallback() {
			@Override
			public void onScriptEnd() {
				long time = System.nanoTime();
				Log.d(CucumberInstrumentation.TAG, String.format("Stop time: %d", time));
				Cucumber.put(Cucumber.KEY_STOP_TIME_NANO, time);
				mScriptEndWaitLock.release();
			}
		}));

		solo.clickOnView(solo.getView(org.catrobat.catroid.R.id.main_menu_button_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName(), 5000);
		assertEquals(ProjectActivity.class, solo.getCurrentActivity().getClass());

		solo.clickOnView(solo.getView(org.catrobat.catroid.R.id.button_play));
		solo.waitForActivity(StageActivity.class.getSimpleName(), 5000);
		GdxListener gdxListener = new GdxListener();
		StageActivity.stageListener.setStageListenerDelegate(gdxListener);
		gdxListener.waitForStageToRender(10000);
		assertEquals(StageActivity.class, solo.getCurrentActivity().getClass());

		if (script instanceof StartScript) {
			executeStartScript((StartScript) script);
		} else if (script instanceof WhenScript) {
			executeWhenScript((WhenScript) script);
		} else if (script instanceof BroadcastScript) {
			executeBroadcastScript((BroadcastScript) script);
		} else {
			fail("Unsupported script class.");
		}
	}

	private void executeStartScript(StartScript script) {
		// start scripts should start with the stage activity
	}

	private void executeWhenScript(WhenScript script) {
		String action = script.getAction();
		Sprite object = script.getScriptBrick().getSprite();
		if ("Tapped".equals(action)) { // action names should be public in WhenScript
			object.look.doTouchDown(0, 0, 0);
		} else {
			fail(String.format("Unsupported when script action: %s", action));
		}
		Cucumber.put(Cucumber.KEY_START_TIME_NANO, System.nanoTime());
	}

	private void executeBroadcastScript(BroadcastScript script) {
		Cucumber.put(Cucumber.KEY_START_TIME_NANO, System.nanoTime());
		fail("BroadcastScript is not yet supported.");
	}

	public static interface ScriptEndCallback {
		public void onScriptEnd();
	}

	private static final class GdxListener implements ApplicationListener {
		private final Semaphore mLock = new Semaphore(0);

		public void waitForStageToRender(int timeout) {
			try {
				mLock.tryAcquire(1, timeout, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}
			long time = System.nanoTime();
			Log.d(CucumberInstrumentation.TAG, String.format("Start time: %d", time));
			Cucumber.put(Cucumber.KEY_START_TIME_NANO, time);
		}

		@Override
		public void render() {
			mLock.release();
		}

		@Override
		public void create() {
		}

		@Override
		public void resize(int i, int i2) {
		}

		@Override
		public void pause() {
		}

		@Override
		public void resume() {
		}

		@Override
		public void dispose() {
		}
	}

	public static final class ScriptEndBrick extends ShowBrick {
		private final transient ScriptEndCallback mCallback;

		public ScriptEndBrick(Sprite sprite, ScriptEndCallback callback) {
			this.sprite = sprite;
			mCallback = callback;
		}

		@Override
		public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
			sequence.addAction(new ScriptEndAction(mCallback));
			return null;
		}
	}

	public static final class ScriptEndAction extends Action {
		private final transient ScriptEndCallback mCallback;

		public ScriptEndAction(ScriptEndCallback callback) {
			mCallback = callback;
		}

		@Override
		public boolean act(float delta) {
			mCallback.onScriptEnd();
			return true;
		}
	}
}
