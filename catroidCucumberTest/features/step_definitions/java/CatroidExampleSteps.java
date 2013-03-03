// CatroidExampleSteps.java

public final class CatroidExampleSteps {
    private CatroidExampleSteps() {
    }

    @Given("there is one sprite")
    public void there_is_one_sprite() {
        System.out.println("Hello, World!");
    }

    @And("the sprite has one script")
    public void the_sprite_has_one_script() {
    }

    @And("the script has one (.*) with (.*)")
    public void there_is_one_sprite(String brickName, int arg) {
    }

    @When("I run the script")
    public void run_the_script() {
    }

    @And("I wait (.*) ms")
    public void wait_milliseconds(int millis) {
    }

    @Then("the sprite has a (.*) position of (*.)")
    public void sprite_has_position(String axis, int value) {
    }
}
