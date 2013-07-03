package org.catrobat.catroid.test;

import android.test.AndroidTestCase;
import cucumber.api.java.en.Then;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.UserVariable;

public class VariableSteps extends AndroidTestCase {
    @Then("^the variable '(\\w+)' is (-?\\d+.?\\d*)$")
    public void variable_x_is(String name, float expected) {
        Project project = (Project) Cucumber.get(Cucumber.KEY_PROJECT);
        Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
        UserVariable variable = project.getUserVariables().getUserVariable(name, object);
        double actual = variable.getValue();
        assertEquals(expected, actual, 0f);
    }
}
