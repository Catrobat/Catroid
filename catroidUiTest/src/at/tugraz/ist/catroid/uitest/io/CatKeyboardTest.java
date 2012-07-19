package at.tugraz.ist.catroid.uitest.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import android.graphics.Point;
import android.test.suitebuilder.annotation.Smoke;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.EditText;
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
	private Vector<Vector<String>> numberKeyString;
	//	private Vector<String> functionKeyString;
	//	private Vector<String> sensorKeyString;
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
		this.numberKeyString = new Vector<Vector<String>>();
		this.numberKeyString.add(new Vector<String>());
		this.numberKeyString.add(new Vector<String>());
		this.numberKeyString.add(new Vector<String>());

		this.keyMap = new HashMap<String, Point>();
		this.buttonsEachColumns = 5;
		this.buttonsEachRow = 4;

		this.calculateCoordinatesOnScreen();
		this.createKeyStrings();
		this.setCoordinatesForKeys();

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
	public void testKeysFromNumbersKeyboard() {

		solo.sleep(1000);
		solo.clickOnEditText(0);
		solo.clickOnEditText(0);
		solo.clickOnEditText(1);// View.performclick()
		//		solo.clickOnEditText(1);
		//		int size = solo.getViews().size();
		//		Log.i("info", "solo.getViews().size():" + size);
		//		solo.getViews().get(1).performClick();
		//		solo.getViews().get(1).performClick();

		//Didnt worked to test the keyboard:
		//				solo.sendKey(8); 
		//		solo.sleep(1000);
		//		solo.clickOnText("+");
		//		solo.clickOnImage(0);

		this.clickOnKey("del");
		this.clickOnKey("del");
		this.clickOnKey("del");
		this.clickOnKey("del");

		this.clickOnKey("9");
		ArrayList<EditText> textList = solo.getCurrentEditTexts();
		//		Log.i("info", "text.size()" + textList.size());
		EditText text = textList.get(textList.size() - 1);
		//		Log.i("info", "textstring" + text.getText().toString());
		assertEquals("9", text.getText().toString().substring(0, 1));
		this.clickOnKey("del");

		this.clickOnKey("8");
		assertEquals("8", text.getText().toString().substring(0, 1));
		this.clickOnKey("del");

		this.clickOnKey("7");
		assertEquals("7", text.getText().toString().substring(0, 1));
		this.clickOnKey("del");

		this.clickOnKey("6");
		assertEquals("6", text.getText().toString().substring(0, 1));
		this.clickOnKey("del");

		this.clickOnKey("5");
		assertEquals("5", text.getText().toString().substring(0, 1));
		this.clickOnKey("del");

		this.clickOnKey("4");
		assertEquals("4", text.getText().toString().substring(0, 1));
		this.clickOnKey("del");

		this.clickOnKey("3");
		assertEquals("3", text.getText().toString().substring(0, 1));
		this.clickOnKey("del");

		this.clickOnKey("2");
		assertEquals("2", text.getText().toString().substring(0, 1));
		this.clickOnKey("del");

		this.clickOnKey("1");
		assertEquals("1", text.getText().toString().substring(0, 1));
		this.clickOnKey("del");

		this.clickOnKey("space");
		assertEquals(" ", text.getText().toString().substring(0, 1));
		this.clickOnKey("del");

		this.clickOnKey("+");
		assertEquals("+", text.getText().toString().substring(0, 1));
		this.clickOnKey("del");

		this.clickOnKey("-");
		assertEquals("-", text.getText().toString().substring(0, 1));
		this.clickOnKey("del");

		this.clickOnKey("*");
		assertEquals("*", text.getText().toString().substring(0, 1));
		this.clickOnKey("del");

		this.clickOnKey("/");
		assertEquals("/", text.getText().toString().substring(0, 1));
		this.clickOnKey("del");

		this.clickOnKey("^");
		assertEquals("^", text.getText().toString().substring(0, 1));
		this.clickOnKey("del");
		//LinearLayout ll = new LinearLayout(solo.getViews().get(0).getContext());

		//		for (int i = 0; i < solo.getViews().size(); i++) {
		//			solo.clickOnView(solo.getViews().get(i));
		//			Log.i("info", "solo.getViews().get(" + i + "):" + solo.getViews().get(i).getId());
		//			solo.sleep(1000);
		//		}

		//Test the 3 Buttons with this methods:
		//		solo.clickOnImageButton(0); // Ok-Button
		//		solo.clickOnImageButton(1); // UNDO - Button
		//		solo.clickOnImageButton(2); // Cancel - Button 

	}

	public void testKeysFromFunctionKeyboard() {

		solo.sleep(1000);
		solo.clickOnEditText(0);
		solo.clickOnEditText(0);
		solo.clickOnEditText(1);

		this.clickOnKey("del");
		this.clickOnKey("del");
		this.clickOnKey("del");
		this.clickOnKey("del");

		ArrayList<EditText> textList = solo.getCurrentEditTexts();
		EditText text = textList.get(textList.size() - 1);

		this.clickOnKey("keyboardswitch");
		this.clickOnKey("2");// cos()
		assertEquals("cos( 0 )", text.getText().toString().substring(0, "cos( 0 )".length()));
		this.clickOnKey("del");
		// TODO: test function keys

	}

	public void testKeysSensorKeyboard() {

		solo.sleep(1000);
		solo.clickOnEditText(0);
		solo.clickOnEditText(0);
		solo.clickOnEditText(1);

		this.clickOnKey("del");
		this.clickOnKey("del");
		this.clickOnKey("del");
		this.clickOnKey("del");

		ArrayList<EditText> textList = solo.getCurrentEditTexts();
		EditText text = textList.get(textList.size() - 1);

		this.clickOnKey("keyboardswitch");
		this.clickOnKey("keyboardswitch");
		this.clickOnKey("1");// x-Accelerometer
		assertEquals("X_Accelerometer", text.getText().toString().substring(0, "X_Accelerometer".length()));
		this.clickOnKey("del");
		// TODO: test other sensor keys

	}

	//	public void testFormulas() {
	//		assert false;
	//	}

	private void createProject(String projectName) {

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

	}

	private void createKeyStrings() {
		// Clicking keys on screen in this order:
		//0,7,4,1,
		//.,8,5,2,
		//space,9,6,3,
		//space2,del,*,+,
		//shift,enter,/,-
		numberKeyString.get(0).add("0");
		numberKeyString.get(0).add("7");
		numberKeyString.get(0).add("4");
		numberKeyString.get(0).add("1");
		numberKeyString.get(0).add(".");
		numberKeyString.get(0).add("8");
		numberKeyString.get(0).add("5");
		numberKeyString.get(0).add("2");
		numberKeyString.get(0).add("space");
		numberKeyString.get(0).add("9");
		numberKeyString.get(0).add("6");
		numberKeyString.get(0).add("3");
		numberKeyString.get(0).add("space2");
		numberKeyString.get(0).add("del");
		numberKeyString.get(0).add("*");
		numberKeyString.get(0).add("+");
		numberKeyString.get(0).add("keyboardswitch");
		numberKeyString.get(0).add("^");
		numberKeyString.get(0).add("/");
		numberKeyString.get(0).add("-");

		numberKeyString.get(1).add("pi");
		numberKeyString.get(1).add("rand1");
		numberKeyString.get(1).add("ln");
		numberKeyString.get(1).add("sin");
		numberKeyString.get(1).add("e");
		numberKeyString.get(1).add("rand2");
		numberKeyString.get(1).add("log");
		numberKeyString.get(1).add("cos");
		numberKeyString.get(1).add("space");
		numberKeyString.get(1).add("rand3");
		numberKeyString.get(1).add("sqrt");
		numberKeyString.get(1).add("tan");
		numberKeyString.get(1).add("space2");
		numberKeyString.get(1).add("del");
		numberKeyString.get(1).add("*");
		numberKeyString.get(1).add("+");
		numberKeyString.get(1).add("keyboardswitch");
		numberKeyString.get(1).add("^");
		numberKeyString.get(1).add("/");
		numberKeyString.get(1).add("-");

		numberKeyString.get(2).add("freecookies");
		numberKeyString.get(2).add("pitch");
		numberKeyString.get(2).add("z-accel");
		numberKeyString.get(2).add("x-accel");
		numberKeyString.get(2).add("freecookies2");
		numberKeyString.get(2).add(null);
		numberKeyString.get(2).add(null);
		numberKeyString.get(2).add(null);
		numberKeyString.get(2).add("space");
		numberKeyString.get(2).add("roll");
		numberKeyString.get(2).add("azimuth");
		numberKeyString.get(2).add("y-accel");
		numberKeyString.get(2).add("space2");
		numberKeyString.get(2).add("del");
		numberKeyString.get(2).add("*");
		numberKeyString.get(2).add("+");
		numberKeyString.get(2).add("keyboardswitch");
		numberKeyString.get(2).add("^");
		numberKeyString.get(2).add("/");
		numberKeyString.get(2).add("-");

	}

	private void setCoordinatesForKeys() {
		//Setting x,y coordinates for each key
		int z = 0;
		for (int h = 0; h < this.numberKeyString.size(); h++) {
			for (int i = 0; i < buttonsEachColumns; i++) {
				for (int j = 0; j < buttonsEachRow; j++) {

					Log.i("info", "setUp()" + " i:" + i + " j:" + j + " z:" + z);
					int x = i * buttonWidth + buttonWidth / 2;
					int y = displayHeight - (j * (int) buttonHeight + (int) buttonHeight / 2);
					this.keyMap.put(this.numberKeyString.get(h).get(z), new Point(x, y));
					z++;

				}
			}
		}
		return;
	}

	private void calculateCoordinatesOnScreen() {

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

		this.keyboardHeight = this.buttonsEachRow * 50.0f * px;
		Log.i("info", "keyboardHeight: " + this.keyboardHeight);

		this.amountOfDisplayspaceUsedForKeyboard = this.displayHeight / this.keyboardHeight;
		Log.i("info", "amountOfDisplayspaceUsedForKeyboard: " + this.amountOfDisplayspaceUsedForKeyboard);

		this.buttonWidth = displayWidth / buttonsEachColumns;
		float divisor = this.amountOfDisplayspaceUsedForKeyboard * this.buttonsEachRow;
		Log.i("info", "divisor: " + divisor);
		this.buttonHeight = displayHeight / divisor;
		Log.i("info", "buttonHeight: " + this.buttonHeight);

	}
}
