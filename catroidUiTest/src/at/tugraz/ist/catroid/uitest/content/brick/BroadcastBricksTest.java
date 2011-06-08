package at.tugraz.ist.catroid.uitest.content.brick;

import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.BroadcastScript;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.BroadcastBrick;
import at.tugraz.ist.catroid.content.bricks.BroadcastWaitBrick;
import at.tugraz.ist.catroid.ui.ScriptActivity;

import com.jayway.android.robotium.solo.Solo;

public class BroadcastBricksTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private Solo solo;
	private Project project;

	public BroadcastBricksTest() {
		super("at.tugraz.ist.catroid", ScriptActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		getActivity().finish();
		super.tearDown();
	}

	@Smoke
	public void testBroadcastBricks() {
		int childrenCount = getActivity().getAdapter().getChildCountFromLastGroup();
		int groupCount = getActivity().getAdapter().getGroupCount();
		assertEquals("Incorrect number of bricks.", 3, solo.getCurrentListViews().get(0).getChildCount());
		assertEquals("Incorrect number of bricks.", 2, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScriptList().get(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 2, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0),
				getActivity().getAdapter().getChild(groupCount - 1, 0));

		String testString = "test";
		String testString2 = "test2";
		String testString3 = "test3";

		solo.clickOnButton(2);

		solo.enterText(0, testString);
		solo.clickOnButton(getActivity().getString(R.string.ok));

		assertEquals("Wrong selection", testString, (String) solo.getCurrentSpinners().get(0).getSelectedItem());
		assertNotSame("Wrong selection", testString, solo.getCurrentSpinners().get(1).getSelectedItem());

		solo.pressSpinnerItem(1, 2);
		assertEquals("Wrong selection", testString, (String) solo.getCurrentSpinners().get(1).getSelectedItem());

		solo.pressSpinnerItem(2, 2);
		assertEquals("Wrong selection", testString, (String) solo.getCurrentSpinners().get(2).getSelectedItem());

		solo.clickOnButton(3);
		solo.enterText(0, testString2);
		solo.clickOnButton(getActivity().getString(R.string.ok));
		assertEquals("Wrong selection", testString, (String) solo.getCurrentSpinners().get(0).getSelectedItem());
		assertEquals("Wrong selection", testString2, (String) solo.getCurrentSpinners().get(1).getSelectedItem());
		assertEquals("Wrong selection", testString, (String) solo.getCurrentSpinners().get(2).getSelectedItem());

		solo.clickOnButton(4);
		solo.enterText(0, testString3);
		solo.clickOnButton(getActivity().getString(R.string.ok));
		assertEquals("Wrong selection", testString, (String) solo.getCurrentSpinners().get(0).getSelectedItem());
		assertEquals("Wrong selection", testString2, (String) solo.getCurrentSpinners().get(1).getSelectedItem());
		assertEquals("Wrong selection", testString3, (String) solo.getCurrentSpinners().get(2).getSelectedItem());

		solo.pressSpinnerItem(1, 4);
		assertEquals("Wrong selection", testString3, (String) solo.getCurrentSpinners().get(1).getSelectedItem());

	}

	private void createProject() {
		project = new Project(null, "testProject");
		Sprite sprite = new Sprite("cat");
		Script script = new BroadcastScript("script", sprite);
		BroadcastBrick broadcastBrick = new BroadcastBrick(sprite);
		BroadcastWaitBrick broadcastWaitBrick = new BroadcastWaitBrick(sprite);
		script.addBrick(broadcastBrick);
		script.addBrick(broadcastWaitBrick);

		sprite.getScriptList().add(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
