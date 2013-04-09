package org.catrobat.catroid.uitest.stage;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class MultipleBroadcastsTest extends ActivityInstrumentationTestCase2<StageActivity> {

	private Solo solo;

	public MultipleBroadcastsTest() {
		super(StageActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.prepareStageForTest();
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	// If no Exception is thrown, this test is fine!
	public void testSendMultipleBroadcastsWhenProjectStart() {
		solo.waitForActivity(StageActivity.class.getSimpleName());
		Reflection.setPrivateField(StageActivity.stageListener, "makeAutomaticScreenshot", false);
		solo.sleep(1000);
	}

	private void createProject() {
		Project project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);

		Sprite sprite1 = new Sprite("sprite1");
		StartScript startScript1 = new StartScript(sprite1);
		BroadcastBrick broadcastBrick1 = new BroadcastBrick(sprite1);
		broadcastBrick1.setSelectedMessage("run");
		startScript1.addBrick(broadcastBrick1);
		BroadcastScript broadcastScript1 = new BroadcastScript(sprite1);
		broadcastScript1.setBroadcastMessage("run");
		WaitBrick waitBrick1 = new WaitBrick(sprite1, 100);
		broadcastScript1.addBrick(waitBrick1);
		sprite1.addScript(startScript1);
		sprite1.addScript(broadcastScript1);
		project.addSprite(sprite1);

		Sprite sprite2 = new Sprite("sprite2");
		StartScript startScript2 = new StartScript(sprite2);
		BroadcastBrick broadcastBrick2 = new BroadcastBrick(sprite2);
		broadcastBrick2.setSelectedMessage("run");
		startScript2.addBrick(broadcastBrick2);
		BroadcastScript broadcastScript2 = new BroadcastScript(sprite2);
		broadcastScript2.setBroadcastMessage("run");
		WaitBrick waitBrick2 = new WaitBrick(sprite2, 100);
		broadcastScript2.addBrick(waitBrick2);
		sprite2.addScript(startScript2);
		sprite2.addScript(broadcastScript2);
		project.addSprite(sprite2);

		Sprite sprite3 = new Sprite("sprite3");
		StartScript startScript3 = new StartScript(sprite3);
		BroadcastBrick broadcastBrick3 = new BroadcastBrick(sprite3);
		broadcastBrick3.setSelectedMessage("run");
		startScript3.addBrick(broadcastBrick3);
		BroadcastScript broadcastScript3 = new BroadcastScript(sprite3);
		broadcastScript3.setBroadcastMessage("run");
		WaitBrick waitBrick3 = new WaitBrick(sprite3, 100);
		broadcastScript3.addBrick(waitBrick3);
		sprite3.addScript(startScript3);
		sprite3.addScript(broadcastScript3);
		project.addSprite(sprite3);

		Sprite sprite4 = new Sprite("sprite4");
		StartScript startScript4 = new StartScript(sprite4);
		BroadcastBrick broadcastBrick4 = new BroadcastBrick(sprite4);
		broadcastBrick4.setSelectedMessage("run");
		startScript4.addBrick(broadcastBrick4);
		BroadcastScript broadcastScript4 = new BroadcastScript(sprite4);
		broadcastScript4.setBroadcastMessage("run");
		WaitBrick waitBrick4 = new WaitBrick(sprite4, 100);
		broadcastScript4.addBrick(waitBrick4);
		sprite4.addScript(startScript4);
		sprite4.addScript(broadcastScript4);
		project.addSprite(sprite4);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().saveProject();
	}

}
