package at.tugraz.ist.catroid.uitest.io;

import android.test.suitebuilder.annotation.Smoke;
import android.util.Log;
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

		//Didnt worked to test the keyboard:
		//				solo.sendKey(8); 
		//		solo.sleep(1000);
		//		solo.clickOnText("+");
		//		solo.clickOnImage(0);
		solo.sleep(1000);

		int displayWidth = 720;
		int displayHigh = 1200;
		int buttonsEachColumns = 5;
		int buttonsEachRow = 4;
		int buttonWidth = displayWidth / buttonsEachColumns;
		int buttonHigh = 1200 / (3 * buttonsEachRow);

		for (int i = 0; i < buttonsEachColumns; i++) {
			for (int j = 0; j < buttonsEachRow; j++) {
				Log.i("testKeyBoard()", "i:" + i + "j:" + j);
				int x = i * buttonWidth + buttonWidth / 2;
				int y = displayHigh - (j * buttonHigh + buttonHigh / 2);
				solo.clickOnScreen(x, y);
				solo.sleep(100);
				// Clicking keys on screen in this order:
				//0,7,4,1,
				//.,8,5,2,
				//space,9,
				//6,3,space,
				//del,*,+,
				//shift,enter,/,-
			}
		}

		//Test the 3 Buttons with this methods:
		//		solo.clickOnImageButton(0); // Ok-Button
		//		solo.clickOnImageButton(1); // UNDO - Button
		//		solo.clickOnImageButton(2); // Cancel - Button 

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
