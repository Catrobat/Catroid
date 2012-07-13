package at.tugraz.ist.catroid.uitest.io;

import android.test.suitebuilder.annotation.Smoke;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.ChangeSizeByNBrick;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;

import com.jayway.android.robotium.solo.Solo;

public class CatKeyboardTest extends android.test.ActivityInstrumentationTestCase2<ScriptTabActivity> {

	private Solo solo;
	private Script testScript;
	//	private Script testScript2;
	//	private Script testScript3;
	Sprite firstSprite;
	Brick changeBrick;

	public CatKeyboardTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
	}

	//	public CatKeyboardTest(Class<ScriptTabActivity> activityClass) {
	//		super(activityClass);
	//		// TODO Auto-generated constructor stub
	//	}

	@Override
	public void setUp() throws Exception {
		createProject("testProject");
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
	public void testKeyboard() {

		solo.clickOnEditText(0);
		solo.sleep(1000);
		solo.clickOnEditText(0);
		//		solo.sleep(1000);
		//
		//		solo.sendKey(8);
		solo.sleep(1000);
		//		solo.clickOnText("+");
		//		solo.clickOnImage(0);
		solo.sleep(1000);
		solo.clickOnImageButton(2); // "x" - Button 

		//		solo.clickOnImageButton(2);
		//		solo.clickOnImageButton(3);
		//		solo.clickOnImageButton(4);
		//		solo.clickOnImageButton(5);
		//		solo.clickOnImageButton(6);
		//		solo.clickOnImageButton(7);
		//		solo.clickOnImageButton(8);
		//		solo.clickOnImageButton(9);
		//		solo.clickOnImageButton(10);
		//		ArrayList<Integer> yPositionList = getListItemYPositions();
		//		assertTrue("Test project brick list smaller than expected", yPositionList.size() >= 6);
		//
		//		int numberOfBricks = ProjectManager.getInstance().getCurrentScript().getBrickList().size();
		//
		//		longClickAndDrag(10, yPositionList.get(7), 10, yPositionList.get(2), 20);
		//
		//		assertTrue("Number of Bricks inside Script hasn't changed", (numberOfBricks + 1) == ProjectManager
		//				.getInstance().getCurrentScript().getBrickList().size());
		//
		//		Adapter adapter = ((ScriptActivity) getActivity().getCurrentActivity()).getAdapter();
		//
		//		assertEquals("Incorrect Brick after dragging over Script", (Brick) adapter.getItem(2) instanceof WaitBrick,
		//				true);
		solo.sleep(3000);
	}

	private void createProject(String projectName) {
		//		double size = 0.8;
		//
		Project project = new Project(null, projectName);
		firstSprite = new Sprite("nom nom nom");

		Script startScript1 = new StartScript(firstSprite);
		changeBrick = new ChangeSizeByNBrick(firstSprite, 0);

		firstSprite.addScript(startScript1);
		startScript1.addBrick(changeBrick);
		project.addSprite(firstSprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
	}

}
