package org.catrobat.catroid.test;

import android.graphics.Point;
import android.test.AndroidTestCase;
import android.util.Log;
import com.jayway.android.robotium.solo.Solo;
import cucumber.api.android.CucumberInstrumentation;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;

import java.util.ArrayList;
import java.util.List;

public class SpriteSteps extends AndroidTestCase {
    @And("^I (\\w+) the default object$")
    public void I_s_the_default_object(String action) {
        String defaultSpriteName = (String) RunCukes.get(RunCukes.KEY_DEFAULT_SPRITE_NAME);
        I_s_the_object_s(action, defaultSpriteName);
    }

    @And("^I (\\w+) the object '(\\w+)'$")
    public void I_s_the_object_s(String action, String name) {
        Solo solo = (Solo) RunCukes.get(RunCukes.KEY_SOLO);
        Project project = (Project) RunCukes.get(RunCukes.KEY_PROJECT);
        if ("tap".equals(action)) {
            Sprite sprite = Util.findSprite(project, name);
            if ("background".equals(name)) {
                solo.clickOnScreen(10, 10);
            } else {
                Point p = Util.libgdxToScreenCoordinates(getContext(), sprite.look.getX(), sprite.look.getY());
                p.x += Math.round(sprite.look.getWidth() / 2f);
                p.y += Math.round(sprite.look.getHeight() / 2f);
//                Log.d(CucumberInstrumentation.TAG, String.format("click: [%d/%d]", p.x, p.y));
                solo.clickOnScreen(p.x, p.y);
            }
            // wait for actions to start
            int timeout = 4;
            while (sprite.look.getAllActionsAreFinished() && timeout-- > 1) {
                solo.sleep(500);
            }
        } else {
            fail(String.format("Unsupported action '%s'", action));
        }
    }

    @Then("^the object '(\\w+)' is \\b(visible|invisible)$")
    public void object_is_visible_or_not(String name, String visibility) {
        Solo solo = (Solo) RunCukes.get(RunCukes.KEY_SOLO);
        Project project = (Project) RunCukes.get(RunCukes.KEY_PROJECT);
        Sprite sprite = Util.findSprite(project, name);
        while (!sprite.look.getAllActionsAreFinished()) {
            solo.sleep(1000);
        }
        if ("visible".equals(visibility)) {
            assertTrue(sprite.look.show);
        } else {
            assertFalse(sprite.look.show);
        }
    }

    @Then("^the default object is changing its costumes$")
    public void default_object_changing_costumes() {
        Project project = (Project) RunCukes.get(RunCukes.KEY_PROJECT);
        String defaultSpriteName = (String) RunCukes.get(RunCukes.KEY_DEFAULT_SPRITE_NAME);
        Sprite sprite = Util.findSprite(project, defaultSpriteName);
        List<String> names = new ArrayList<String>();
        Log.d(CucumberInstrumentation.TAG, "go!");
        while (!sprite.look.getAllActionsAreFinished()) {
            String lookName = sprite.look.getLookData().getLookName();
            if (!names.contains(lookName)) {
                names.add(lookName);
                Log.d(CucumberInstrumentation.TAG, "look: " + lookName);
            }
        }
        assertTrue("Looks did not change!", names.size() > 1);
    }
}
