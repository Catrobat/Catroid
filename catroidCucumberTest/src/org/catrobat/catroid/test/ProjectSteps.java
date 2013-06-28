package org.catrobat.catroid.test;

import android.test.AndroidTestCase;
import android.util.Log;
import com.jayway.android.robotium.solo.Solo;
import cucumber.api.android.CucumberInstrumentation;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;

import java.io.IOException;


public class ProjectSteps extends AndroidTestCase {
    private Project mProject;

    @Given("^I have a default program$")
    public void I_have_default_program() {
        ProjectManager pm = ProjectManager.getInstance();
        pm.deleteCurrentProject();
        pm.initializeDefaultProject(getContext());
        mProject = pm.getCurrentProject();
        RunCukes.put(RunCukes.KEY_PROJECT, mProject);
    }

    @Given("^I have a program with the name '(\\w+)'$")
    public void I_have_program_with_name(String name) {
        try {
            ProjectManager pm = ProjectManager.getInstance();
            pm.initializeNewProject(name, getContext());
            mProject = pm.getCurrentProject();
            mProject.getSpriteList().clear();
            RunCukes.put(RunCukes.KEY_PROJECT, mProject);
        } catch (IOException e) {
            Log.e(CucumberInstrumentation.TAG, e.toString());
            fail(e.getMessage());
        }
    }

    @When("^I start the program$")
    public void I_start_the_program() {
        Solo solo = (Solo) RunCukes.get(RunCukes.KEY_SOLO);
        assertEquals(MainMenuActivity.class, solo.getCurrentActivity().getClass());
        solo.clickOnView(solo.getView(org.catrobat.catroid.R.id.main_menu_button_continue));
        solo.waitForActivity(ProjectActivity.class.getSimpleName(), 3000);
        assertEquals(ProjectActivity.class, solo.getCurrentActivity().getClass());
        solo.clickOnView(solo.getView(org.catrobat.catroid.R.id.button_play));
        solo.waitForActivity(StageActivity.class.getSimpleName(), 3000);
        assertEquals(StageActivity.class, solo.getCurrentActivity().getClass());
        solo.sleep(4000);
    }

    @Then("^I wait (\\d+) milliseconds$")
    public void I_wait_d_milliseconds(int time) {
        Solo solo = (Solo) RunCukes.get(RunCukes.KEY_SOLO);
        solo.sleep(time);
    }

    @Then("^the default program is being executed$")
    public void default_project_being_executed() {
        Solo solo = (Solo) RunCukes.get(RunCukes.KEY_SOLO);
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
