import CucumberAnnotation.Given;
import CucumberAnnotation.When;
import CucumberAnnotation.Then;
import CucumberAnnotation.And;

import static sh.calaba.instrumentationbackend.InstrumentationBackend.solo;

public final class CatroidExampleSteps {
    private CatroidExampleSteps() {
    }

    @Given("there is one sprite")
    public void there_is_one_sprite() {
        assert (true);
    }

    @And("the sprite has one script")
    public void the_sprite_has_one_script() {
        assert (true);
    }

    @And("the script has one (\\w+Brick) with (-?\\d+)")
    public void the_script_has_one_brick_with_int(String name, int arg) {
        assert (true);
    }

    @And("the script has one (\\w+Brick)")
    public void the_script_has_one_brick(String name) {
        assert (true);
    }

    @When("I run the script")
    public void run_the_script() {
        assert (true);
    }

    @And("I wait (\\d+) ms")
    public void wait_milliseconds(int millis) {
        solo.sleep(millis);
    }

    @Then("the sprite has a ([x,y,z]) position of (-?\\d+)")
    public void sprite_has_position(String axis, int value) {
        assert (true);
    }
}
