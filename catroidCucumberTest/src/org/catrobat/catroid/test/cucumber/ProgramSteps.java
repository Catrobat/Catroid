package org.catrobat.catroid.test.cucumber;

import android.test.AndroidTestCase;
import android.util.Log;
import com.jayway.android.robotium.solo.Solo;
import cucumber.api.android.CucumberInstrumentation;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.*;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.test.cucumber.util.CallbackBrick;
import org.catrobat.catroid.test.cucumber.util.Util;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;

import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

public class ProgramSteps extends AndroidTestCase {
	private final Object mProgramStartWaitLock = new Object();
	private boolean mProgramHasStarted = false;
	// Decrement once for every new script.
	private int mProgramWaitLockPermits = 1;
	// Release once for each script that ends.
	// Should be == 1 after every script ended.
	private Semaphore mProgramWaitLock;

	@Given("^I have a Program$")
	public void I_have_a_program() throws IOException {
		ProjectManager pm = ProjectManager.getInstance();
		pm.initializeNewProject("Cucumber", getContext(), /*empty*/ true);
		Project project = pm.getCurrentProject();
		Cucumber.put(Cucumber.KEY_PROJECT, project);
	}

	@Given("^this program has an Object '(\\w+)'$")
	public void program_has_object(String name) {
		int lookId = org.catrobat.catroid.R.drawable.default_project_mole_1;
		ProjectManager pm = ProjectManager.getInstance();
		Project project = pm.getCurrentProject();
		Sprite sprite = Util.addNewObjectWithLook(getContext(), project, name, lookId);
		Cucumber.put(Cucumber.KEY_CURRENT_OBJECT, sprite);
	}

	@Given("^'(\\w+)' has a Start script$")
	public void object_has_start_script(String object) {
		mProgramWaitLockPermits -= 1;
		Project project = ProjectManager.getInstance().getCurrentProject();
		Sprite sprite = Util.findSprite(project, object);
		StartScript script = new StartScript(sprite);

		script.addBrick(new CallbackBrick(sprite, new CallbackBrick.BrickCallback() {
			@Override
			public void onCallback() {
				synchronized (mProgramStartWaitLock) {
					if (!mProgramHasStarted) {
						mProgramHasStarted = true;
						mProgramStartWaitLock.notify();
					}
				}
			}
		}));

		sprite.addScript(script);
		Cucumber.put(Cucumber.KEY_CURRENT_SCRIPT, script);
	}

	@And("^this script has a Wait (\\d+.?\\d*) seconds brick$")
	public void script_has_wait_ms_brick(float seconds) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);

		int millis = Math.round(seconds * 1000f);
		Brick brick = new WaitBrick(object, millis);
		script.addBrick(brick);
	}

	@And("^this script has a set '(\\w+)' to '(\\w+)' brick$")
	public void script_has_set_var_to_var_brick(String a, String b) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);
		Project project = ProjectManager.getInstance().getCurrentProject();

		UserVariable varA = project.getUserVariables().getUserVariable(a, object);
		if (varA == null) {
			varA = project.getUserVariables().addSpriteUserVariableToSprite(object, a);
		}

		FormulaElement elemB = new FormulaElement(FormulaElement.ElementType.USER_VARIABLE, b, null);

		Brick brick = new SetVariableBrick(object, new Formula(elemB), varA);
		script.addBrick(brick);
	}

	@And("^this script has a change '(\\w+)' by (\\d+.?\\d*) brick$")
	public void script_has_change_var_by_val_brick(String name, String value) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);
		Project project = ProjectManager.getInstance().getCurrentProject();

		UserVariable variable = project.getUserVariables().getUserVariable(name, object);
		if (variable == null) {
			variable = project.getUserVariables().addSpriteUserVariableToSprite(object, name);
		}

		FormulaElement elemValue = new FormulaElement(FormulaElement.ElementType.NUMBER, value, null);

		Brick brick = new ChangeVariableBrick(object, new Formula(elemValue), variable);
		script.addBrick(brick);
	}

	@And("^this script has a Repeat (\\d+) times brick$")
	public void script_has_repeat_times_brick(int iterations) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);

		Brick brick = new RepeatBrick(object, new Formula(iterations));
		Cucumber.put(Cucumber.KEY_LOOP_BEGIN_BRICK, brick);
		script.addBrick(brick);
	}

	@And("^this script has a Repeat end brick$")
	public void script_has_repeat_end_brick() {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);

		LoopBeginBrick loopBeginBrick = (LoopBeginBrick) Cucumber.get(Cucumber.KEY_LOOP_BEGIN_BRICK);
		Brick brick = new LoopEndBrick(object, loopBeginBrick);
		script.addBrick(brick);
	}

	@When("^I start the program$")
	public void I_start_the_program() throws InterruptedException {
		mProgramWaitLock = new Semaphore(mProgramWaitLockPermits);
		addScriptEndCallbacks();

		Solo solo = (Solo) Cucumber.get(Cucumber.KEY_SOLO);
		assertEquals(MainMenuActivity.class, solo.getCurrentActivity().getClass());
		solo.clickOnView(solo.getView(org.catrobat.catroid.R.id.main_menu_button_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName(), 3000);
		assertEquals(ProjectActivity.class, solo.getCurrentActivity().getClass());
		solo.clickOnView(solo.getView(org.catrobat.catroid.R.id.button_play));
		solo.waitForActivity(StageActivity.class.getSimpleName(), 3000);
		assertEquals(StageActivity.class, solo.getCurrentActivity().getClass());

		synchronized (mProgramStartWaitLock) {
			if (!mProgramHasStarted) {
				mProgramStartWaitLock.wait(10000);
			}
		}
	}

	private void addScriptEndCallbacks() {
		Project project = ProjectManager.getInstance().getCurrentProject();
		for (Sprite sprite : project.getSpriteList()) {
			for (int i = 0; i < sprite.getNumberOfScripts(); i++) {
				sprite.getScript(i).addBrick(new CallbackBrick(sprite, new CallbackBrick.BrickCallback() {
					@Override
					public void onCallback() {
						mProgramWaitLock.release();
					}
				}));
			}
		}
	}

	@And("^I wait until the program has stopped$")
	public void wait_until_program_has_stopped() throws InterruptedException {
		// While there are still script running, the available permits should be < 1.
		Log.d(CucumberInstrumentation.TAG, "> waiting...");
		mProgramWaitLock.tryAcquire(1, 60, TimeUnit.SECONDS);
		Log.d(CucumberInstrumentation.TAG, "> done!");
	}

	@Then("^the variable '(\\w+)' should be greater than or equal (\\d+.?\\d*)$")
	public void var_should_greater_than_equal_float(String name, float expected) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Project project = ProjectManager.getInstance().getCurrentProject();

		UserVariable variable = project.getUserVariables().getUserVariable(name, object);
		assertNotNull(variable);

		float actual = variable.getValue().floatValue();
		assertThat(actual, greaterThanOrEqualTo(expected));
	}

	@Then("^the variable '(\\w+)' should be be less than or equal (\\d+.?\\d*)$")
	public void var_should_less_than_equal_float(String name, float expected) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Project project = ProjectManager.getInstance().getCurrentProject();

		UserVariable variable = project.getUserVariables().getUserVariable(name, object);
		assertNotNull(variable);

		float actual = variable.getValue().floatValue();
		assertThat(actual, lessThanOrEqualTo(expected));
	}

	////////////////////////////////////////////////////////////////////////////
	///// LEGACY STEP DEFINTIONS ///////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////
	@Deprecated
	@Given("^I have a default program$")
	public void I_have_default_program() {
		ProjectManager pm = ProjectManager.getInstance();
		pm.deleteCurrentProject();
		pm.initializeDefaultProject(getContext());
		Project project = pm.getCurrentProject();
		Cucumber.put(Cucumber.KEY_PROJECT, project);
	}

	@Deprecated
	@Given("^I have a program with the name '(\\w+)'$")
	public void I_have_program_with_name(String name) {
		try {
			ProjectManager pm = ProjectManager.getInstance();
			pm.initializeNewProject(name, getContext(), /*empty*/ true);
			Project project = pm.getCurrentProject();
			project.getSpriteList().clear();
			Cucumber.put(Cucumber.KEY_PROJECT, project);
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@Deprecated
	@Given("^an empty program$")
	public void empty_program() {
		try {
			String name = System.currentTimeMillis() + "_cucumber";
			ProjectManager pm = ProjectManager.getInstance();
			pm.initializeNewProject(name, getContext(), /*empty*/ true);
			Project project = pm.getCurrentProject();
			Cucumber.put(Cucumber.KEY_PROJECT, project);
			project.getSpriteList().clear();
			project.addSprite(newBackgroundObject());
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	private Sprite newBackgroundObject() {
		Sprite background = new Sprite("background");
		background.look.setZIndex(0);

		StartScript startScript = new StartScript(background);
		SetLookBrick setLookBrick = new SetLookBrick(background);
		startScript.addBrick(setLookBrick);
		background.addScript(startScript);

		LookData lookData = Util.newLookData("background", Util.createBackgroundImage(getContext(), "background"));
		background.getLookDataList().add(lookData);
		setLookBrick.setLook(lookData);
		return background;
	}

	@Deprecated
	@Then("^I wait (\\d+) milliseconds$")
	public void I_wait_d_milliseconds(int time) {
		Solo solo = (Solo) Cucumber.get(Cucumber.KEY_SOLO);
		solo.sleep(time);
	}

	@Deprecated
	@Then("^the program is being executed$")
	public void program_being_executed() {
		ProjectManager pm = ProjectManager.getInstance();
		for (Sprite sprite : pm.getCurrentProject().getSpriteList()) {
			assertFalse("Sprite shouldn't be paused.", sprite.isPaused);
			for (int i = 0; i < sprite.getNumberOfScripts(); i++) {
				Script script = sprite.getScript(i);
				assertFalse("Script shouldn't be paused.", script.isPaused());
			}
		}
	}
}
