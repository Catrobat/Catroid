package org.catrobat.catroid.test;

import android.test.AndroidTestCase;
import com.jayway.android.robotium.solo.Solo;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;

import java.io.IOException;


public class ProjectSteps extends AndroidTestCase {
    private Project mProject;

    @Given("^I have a default program$")
    public void I_have_default_program() {
        ProjectManager pm = ProjectManager.getInstance();
//        Project currentProject = pm.getCurrentProject();
//        String defaultProjectName = (String) RunCukes.get(RunCukes.KEY_DEFAULT_PROJECT_NAME);
//        if (currentProject != null && currentProject.getName().equals(defaultProjectName)) {
//            pm.saveProject();
//            mProject = currentProject;
//            RunCukes.put(RunCukes.KEY_PROJECT, mProject);
//        } else {
        pm.deleteCurrentProject();
        pm.initializeDefaultProject(getContext());
        mProject = pm.getCurrentProject();
        RunCukes.put(RunCukes.KEY_PROJECT, mProject);
//        }
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
}
