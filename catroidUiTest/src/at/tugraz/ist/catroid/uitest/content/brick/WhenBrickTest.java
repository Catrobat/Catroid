package at.tugraz.ist.catroid.uitest.content.brick;

import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.WhenScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.ui.ScriptActivity;

import com.jayway.android.robotium.solo.Solo;

public class WhenBrickTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private Solo solo;
	private Project project;

	public WhenBrickTest() {
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
	public void testWhenBrick() {
		int groupCount = getActivity().getAdapter().getGroupCount();

		assertEquals("Incorrect number of bricks.", 2, solo.getCurrentListViews().get(0).getCount());

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0),
				getActivity().getAdapter().getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist", solo.getText(getActivity().getString(R.string.brick_when)));

		solo.pressSpinnerItem(0, 1);
		solo.sleep(1000);
		System.out.println(solo.getCurrentSpinners().get(0).getSelectedItemPosition());
		System.out.println(solo.getCurrentSpinners().get(0).getSelectedItem().toString());
		assertEquals("Wrong event selected!", 1, solo.getCurrentSpinners().get(0).getSelectedItemPosition());
	}

	private void createProject() {

		project = new Project(null, "testProject");
		Sprite sprite = new Sprite("cat");
		Script script = new WhenScript("script", sprite);
		Brick placeAtBrick = new PlaceAtBrick(sprite, 100, 100);
		script.addBrick(placeAtBrick);
		sprite.addScript(script);

		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

}
