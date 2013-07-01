package org.catrobat.catroid.test;

import android.test.AndroidTestCase;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;

public class BrickSteps extends AndroidTestCase {
    @Given("^a RepeatBrick with (\\d+) iterations?$")
    public void repeat_brick_with_iterations(int iterations) {
        Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
        Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);
        Brick brick = new RepeatBrick(object, iterations);
        script.addBrick(brick);
    }

    @Given("^a ChangeXByNBrick with (\\d+)$")
    public void change_x_by_n_brick(int amount) {
        Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
        Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);
        Brick brick = new ChangeXByNBrick(object, amount);
        script.addBrick(brick);
    }

    @Given("^a ChangeYByNBrick with (\\d+)$")
    public void change_y_by_n_brick(int amount) {
        Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
        Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);
        Brick brick = new ChangeYByNBrick(object, amount);
        script.addBrick(brick);
    }

    @Then("^the elapsed time is at least (\\d+) ms$")
    public void elapsed_time_is_at_least(int assumed) {
        long startTime = (Long) Cucumber.get(Cucumber.KEY_START_TIME);
        int elapsed = (int) (System.currentTimeMillis() - startTime);
        assertTrue("The elapsed time is too short!", elapsed >= assumed);
    }
}
