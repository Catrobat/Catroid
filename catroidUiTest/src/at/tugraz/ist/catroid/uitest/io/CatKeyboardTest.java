package at.tugraz.ist.catroid.uitest.io;

import java.util.HashMap;
import java.util.Vector;

import android.graphics.Point;
import android.test.suitebuilder.annotation.Smoke;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
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
	//	private Script testScript;
	//	private Script testScript2;
	//	private Script testScript3;
	private Sprite firstSprite;
	private Brick changeBrick;
	private Vector<String> keyString;
	private HashMap<String, Point> keyMap;

	private float amountOfDisplayspaceUsedForKeyboard;
	private float keyboardHeight;
	private int displayWidth;
	private int displayHeight;
	private int buttonsEachColumns;
	private int buttonsEachRow;
	private int buttonWidth;
	private float buttonHeight;

	public CatKeyboardTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject("testProjectCatKeyboard");
		this.solo = new Solo(getInstrumentation(), getActivity());
		this.keyString = new Vector<String>();
		this.keyMap = new HashMap<String, Point>();

		DisplayMetrics currentDisplayMetrics = new DisplayMetrics();
		solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getMetrics(currentDisplayMetrics);

		Log.i("info", "DisplayMetrics" + "width:" + currentDisplayMetrics.widthPixels + " height:"
				+ currentDisplayMetrics.heightPixels);

		// 800 * 480 Nexus S, px = 4.26
		// 1184 * 720 nexus, px = 3.19
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, currentDisplayMetrics);
		Log.i("info", "pixel: " + px);

		this.displayWidth = currentDisplayMetrics.widthPixels;
		this.displayHeight = currentDisplayMetrics.heightPixels;

		this.buttonsEachColumns = 5;
		this.buttonsEachRow = 4;
		this.keyboardHeight = this.buttonsEachRow * 50.0f * px;
		Log.i("info", "keyboardHeight: " + this.keyboardHeight);

		this.amountOfDisplayspaceUsedForKeyboard = this.displayHeight / this.keyboardHeight;
		Log.i("info", "amountOfDisplayspaceUsedForKeyboard: " + this.amountOfDisplayspaceUsedForKeyboard);

		this.buttonWidth = displayWidth / buttonsEachColumns;
		float divisor = this.amountOfDisplayspaceUsedForKeyboard * this.buttonsEachRow;
		Log.i("info", "divisor: " + divisor);
		this.buttonHeight = displayHeight / divisor;
		Log.i("info", "buttonHeight: " + this.buttonHeight);
		// Clicking keys on screen in this order:
		//0,7,4,1,
		//.,8,5,2,
		//space,9,6,3,
		//space2,del,*,+,
		//shift,enter,/,-
		keyString.add("0");
		keyString.add("7");
		keyString.add("4");
		keyString.add("1");
		keyString.add(".");
		keyString.add("8");
		keyString.add("5");
		keyString.add("2");
		keyString.add("space");
		keyString.add("9");
		keyString.add("6");
		keyString.add("3");
		keyString.add("space2");
		keyString.add("del");
		keyString.add("*");
		keyString.add("+");
		keyString.add("shift");
		keyString.add("enter");
		keyString.add("/");
		keyString.add("-");

		//Setting x,y coordinates for each point
		int z = 0;
		for (int i = 0; i < buttonsEachColumns; i++) {
			for (int j = 0; j < buttonsEachRow; j++) {

				Log.i("info", "setUp()" + " i:" + i + " j:" + j + " z:" + z);
				int x = i * buttonWidth + buttonWidth / 2;
				int y = displayHeight - (j * (int) buttonHeight + (int) buttonHeight / 2);
				this.keyMap.put(this.keyString.get(z), new Point(x, y));
				z++;

			}
		}
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

		this.clickOnKey("9");
		this.clickOnKey("8");
		this.clickOnKey("7");
		this.clickOnKey("6");
		this.clickOnKey("5");
		this.clickOnKey("4");
		this.clickOnKey("3");
		this.clickOnKey("2");
		this.clickOnKey("1");
		this.clickOnKey("0");
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

	private void clickOnKey(String key) {

		Point keyOnScreen = this.keyMap.get(key);
		Log.i("info", "clickOnKey(" + key + ")" + "x:" + keyOnScreen.x + "y:" + keyOnScreen.y);
		solo.clickOnScreen(keyOnScreen.x, keyOnScreen.y);
		solo.sleep(250);

	}

}
