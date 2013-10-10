package org.catrobat.catroid.test.cucumber;

import android.test.AndroidTestCase;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.*;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.test.cucumber.util.Util;

import java.util.concurrent.TimeUnit;

public class BrickSteps extends AndroidTestCase {
	////////////////////////////////////////////////////////////////////////////
	///// LEGACY STEP DEFINTIONS ///////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////
	@Deprecated
	@Given("^a RepeatBrick with (-?\\d+.?\\d*) iterations?$")
	public void repeat_brick_with_n_iterations(float iterations) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);
		Brick brick = new RepeatBrick(object, new Formula(iterations));
		Cucumber.put(Cucumber.KEY_LOOP_BEGIN_BRICK, brick);
		script.addBrick(brick);
	}

	@Deprecated
	@Given("^a RepeatBrick with '(\\w+)' iterations?$")
	public void repeat_brick_with_x_iterations(String name) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);
		Brick brick = new RepeatBrick(object, Util.newUserVariableFormula(name));
		Cucumber.put(Cucumber.KEY_LOOP_BEGIN_BRICK, brick);
		script.addBrick(brick);
	}

	@Deprecated
	@Given("^a RepeatEndBrick$")
	public void repeat_end_brick() {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);
		LoopBeginBrick loopBeginBrick = (LoopBeginBrick) Cucumber.get(Cucumber.KEY_LOOP_BEGIN_BRICK);
		Brick brick = new LoopEndBrick(object, loopBeginBrick);
		script.addBrick(brick);
	}

	@Deprecated
	@Given("^a ChangeXByNBrick with (\\d+)$")
	public void change_x_by_n_brick(int amount) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);
		Brick brick = new ChangeXByNBrick(object, amount);
		script.addBrick(brick);
	}

	@Deprecated
	@Given("^a ChangeYByNBrick with (\\d+)$")
	public void change_y_by_n_brick(int amount) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);
		Brick brick = new ChangeYByNBrick(object, amount);
		script.addBrick(brick);
	}

	@Deprecated
	@Given("^a SetVariableBrick with '(\\w+)' and (-?\\d+.?\\d*)$")
	public void set_variable_brick(String name, float value) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);
		Project project = (Project) Cucumber.get(Cucumber.KEY_PROJECT);
		UserVariable variable = project.getUserVariables().addSpriteUserVariableToSprite(object, name);
		Brick brick = new SetVariableBrick(object, new Formula((value)), variable);
		script.addBrick(brick);
	}

	@Deprecated
	@Given("^a ChangeVariableBrick with '(\\w+)' and (-?\\d+.?\\d*)$")
	public void change_variable_brick(String name, float value) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);
		Brick brick = new ChangeVariableBrick(object, new Formula((value)), new UserVariable(name));
		script.addBrick(brick);
	}

	@Deprecated
	@Given("^a WaitBrick with (-?\\d+.?\\d*) seconds?$")
	public void wait_brick(float seconds) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);
		Brick brick = new WaitBrick(object, Math.round(seconds * 1000f));
		script.addBrick(brick);
	}

	@Deprecated
	@Then("^the elapsed time is at least (\\d+) ms$")
	public void elapsed_time_is_at_least(int expected) {
		long startTime = (Long) Cucumber.get(Cucumber.KEY_START_TIME_NANO);
		long stopTime = (Long) Cucumber.get(Cucumber.KEY_STOP_TIME_NANO);
		long elapsed = TimeUnit.NANOSECONDS.toMillis(stopTime - startTime);
		assertTrue(String.format("elapsed time expected: %d ms, was: %d ms", expected, elapsed), elapsed >= expected);
	}

	@Deprecated
	@Then("^the elapsed time is at most (\\d+) ms$")
	public void elapsed_time_is_at_most(int expected) {
		long startTime = (Long) Cucumber.get(Cucumber.KEY_START_TIME_NANO);
		long stopTime = (Long) Cucumber.get(Cucumber.KEY_STOP_TIME_NANO);
		long elapsed = TimeUnit.NANOSECONDS.toMillis(stopTime - startTime);
		assertTrue(String.format("elapsed time expected: %d ms, was: %d ms", expected, elapsed), elapsed <= expected);
	}
}
