package org.catrobat.catroid.test.cucumber;

import android.test.AndroidTestCase;
import android.util.Log;
import com.jayway.android.robotium.solo.Solo;
import cucumber.api.android.CucumberInstrumentation;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import org.catrobat.catroid.content.*;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.test.cucumber.util.CallbackBrick;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;

import java.util.HashMap;
import java.util.Map;

public class ScriptSteps extends AndroidTestCase {
	////////////////////////////////////////////////////////////////////////////
	///// LEGACY STEP DEFINTIONS ///////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////
	private final Object mScriptEndWaitLock = new Object();
	private boolean mScriptHasEnded = false;

	@Deprecated
	@Given("^a StartScript$")
	public void start_script() {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		StartScript script = new StartScript(object);
		object.addScript(script);
		Cucumber.put(Cucumber.KEY_CURRENT_SCRIPT, script);

		script.addBrick(new CallbackBrick(object, new CallbackBrick.BrickCallback() {
			@Override
			public void onCallback() {
				Log.w(CucumberInstrumentation.TAG, String.format("Start time (start script): %d", System.nanoTime()));
				Cucumber.put(Cucumber.KEY_START_TIME_NANO, System.nanoTime());
			}
		}));
	}

	@Deprecated
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

	@Deprecated
	@Given("^a BroadcastScript '(\\w+)'$")
	public void broadcast_script(String message) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		BroadcastScript script = new BroadcastScript(object, message);
		object.addScript(script);
		Cucumber.put(Cucumber.KEY_CURRENT_SCRIPT, script);
	}

	@Deprecated
	@When("^the script terminates$")
	public void script_terminates() {
		try {
			synchronized (mScriptEndWaitLock) {
				if (!mScriptHasEnded)
					mScriptEndWaitLock.wait(30000);
			}
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}
	}

	@Deprecated
	@When("^the script is executed$")
	public void script_is_executed() {
		Solo solo = (Solo) Cucumber.get(Cucumber.KEY_SOLO);
		assertEquals(MainMenuActivity.class, solo.getCurrentActivity().getClass());

		Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);
		Sprite object = script.getScriptBrick().getSprite();
		script.addBrick(new CallbackBrick(object, new CallbackBrick.BrickCallback() {
			@Override
			public void onCallback() {
				synchronized (mScriptEndWaitLock) {
					mScriptHasEnded = true;
					Log.w(CucumberInstrumentation.TAG, String.format("Stop time: %d", System.nanoTime()));
					Cucumber.put(Cucumber.KEY_STOP_TIME_NANO, System.nanoTime());
					mScriptEndWaitLock.notify();
				}
			}
		}));

		solo.clickOnView(solo.getView(org.catrobat.catroid.R.id.main_menu_button_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName(), 5000);
		assertEquals(ProjectActivity.class, solo.getCurrentActivity().getClass());

		solo.clickOnView(solo.getView(org.catrobat.catroid.R.id.button_play));
		solo.waitForActivity(StageActivity.class.getSimpleName(), 5000);
//		GdxListener gdxListener = new GdxListener();
//		StageActivity.stageListener.setStageListenerDelegate(gdxListener);
//		try {
//			gdxListener.waitForStageToRender(10000);
//		} catch (InterruptedException e) {
//			fail(e.getMessage());
//		}
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
	}

	private void executeBroadcastScript(BroadcastScript script) {
		fail("BroadcastScript is not yet supported.");
	}
}
