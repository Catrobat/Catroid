package org.catrobat.catroid.test;

import android.test.AndroidTestCase;
import com.jayway.android.robotium.solo.Solo;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;

import java.io.IOException;

public class ProgramSteps extends AndroidTestCase {
	@Given("^I have a default program$")
	public void I_have_default_program() {
		ProjectManager pm = ProjectManager.getInstance();
		pm.deleteCurrentProject();
		pm.initializeDefaultProject(getContext());
		Project project = pm.getCurrentProject();
		Cucumber.put(Cucumber.KEY_PROJECT, project);
	}

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

	@When("^I start the program$")
	public void I_start_the_program() {
		Solo solo = (Solo) Cucumber.get(Cucumber.KEY_SOLO);
		assertEquals(MainMenuActivity.class, solo.getCurrentActivity().getClass());
		solo.clickOnView(solo.getView(org.catrobat.catroid.R.id.main_menu_button_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName(), 3000);
		assertEquals(ProjectActivity.class, solo.getCurrentActivity().getClass());
		solo.clickOnView(solo.getView(org.catrobat.catroid.R.id.button_play));
		solo.waitForActivity(StageActivity.class.getSimpleName(), 3000);
		assertEquals(StageActivity.class, solo.getCurrentActivity().getClass());
	}

	@Then("^I wait (\\d+) milliseconds$")
	public void I_wait_d_milliseconds(int time) {
		Solo solo = (Solo) Cucumber.get(Cucumber.KEY_SOLO);
		solo.sleep(time);
	}

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
