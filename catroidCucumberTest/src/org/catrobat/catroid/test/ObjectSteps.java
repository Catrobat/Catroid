package org.catrobat.catroid.test;

import android.test.AndroidTestCase;
import cucumber.api.java.en.Given;
import org.catrobat.catroid.content.*;

public class ObjectSteps extends AndroidTestCase {
    @Given("^an object '(\\w+)'$")
    public void object(String name) {
        Project project = (Project) Cucumber.get(Cucumber.KEY_PROJECT);
        Sprite object = new Sprite(name);
        project.addSprite(object);
        Cucumber.put(Cucumber.KEY_CURRENT_OBJECT, object);
    }

    @Given("^a (\\w+)Script$")
    public void script(String name) {
        Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
        Script script = null;

        if ("StartScript".equals(name)) {
            script = new StartScript(object);
        } else if ("WhenTappedScript".equals(name)) {
            WhenScript whenScript = new WhenScript(object);
            whenScript.setAction(0);
            script = whenScript;
        } else {
            fail(String.format("No script for this name: '%s'", name));
        }

        object.addScript(script);
        Cucumber.put(Cucumber.KEY_CURRENT_SCRIPT, script);
    }
}
