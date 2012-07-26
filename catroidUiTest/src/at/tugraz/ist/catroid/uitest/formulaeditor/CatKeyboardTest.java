/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.uitest.formulaeditor;

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
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class CatKeyboardTest extends android.test.ActivityInstrumentationTestCase2<ScriptTabActivity> {

	private Project project;
	private Solo solo;
	private Sprite firstSprite;
	private Brick changeBrick;
	private Vector<Vector<String>> keyString;
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
		this.keyString = new Vector<Vector<String>>();
		this.keyString.add(new Vector<String>());
		this.keyString.add(new Vector<String>());
		this.keyString.add(new Vector<String>());

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
		UiTestUtils.clearAllUtilTestProjects();
		this.project = null;
		super.tearDown();
		Thread.sleep(1000);
	}

	@Smoke
	public void testKeysFromNumbersKeyboard() {

		solo.clickOnEditText(0);
		solo.clickOnEditText(0);
		solo.clickOnEditText(1);// View.performclick()

		this.clickOnKey("del");
		this.clickOnKey("del");
		this.clickOnKey("del");
		this.clickOnKey("del");

		this.clickOnKey("9");
		ArrayList<EditText> textList = solo.getCurrentEditTexts();
		//		Log.i("info", "text.size()" + textList.size());
		EditText text = textList.get(textList.size() - 1);
		//		Log.i("info", "textstring" + text.getText().toString());
		assertEquals("Wrong button clicked", "9", text.getText().toString().substring(0, 1));
		this.clickOnKey("del");

		this.clickOnKey("8");
		assertEquals("Wrong button clicked", "8", text.getText().toString().substring(0, 1));
		this.clickOnKey("del");

		this.clickOnKey("7");
		assertEquals("Wrong button clicked", "7", text.getText().toString().substring(0, 1));
		this.clickOnKey("del");

		this.clickOnKey("6");
		assertEquals("Wrong button clicked", "6", text.getText().toString().substring(0, 1));
		this.clickOnKey("del");

		this.clickOnKey("5");
		assertEquals("Wrong button clicked", "5", text.getText().toString().substring(0, 1));
		this.clickOnKey("del");

		this.clickOnKey("4");
		assertEquals("Wrong button clicked", "4", text.getText().toString().substring(0, 1));
		this.clickOnKey("del");

		this.clickOnKey("3");
		assertEquals("Wrong button clicked", "3", text.getText().toString().substring(0, 1));
		this.clickOnKey("del");

		this.clickOnKey("2");
		assertEquals("Wrong button clicked", "2", text.getText().toString().substring(0, 1));
		this.clickOnKey("del");

		this.clickOnKey("1");
		assertEquals("Wrong button clicked", "1", text.getText().toString().substring(0, 1));
		this.clickOnKey("del");

		this.clickOnKey("space");
		assertEquals("Wrong button clicked", " ", text.getText().toString().substring(0, 1));
		this.clickOnKey("del");

		this.clickOnKey("+");
		assertEquals("Wrong button clicked", "+", text.getText().toString().substring(0, 1));
		this.clickOnKey("del");

		this.clickOnKey("-");
		assertEquals("Wrong button clicked", "-", text.getText().toString().substring(0, 1));
		this.clickOnKey("del");

		this.clickOnKey("*");
		assertEquals("Wrong button clicked", "*", text.getText().toString().substring(0, 1));
		this.clickOnKey("del");

		this.clickOnKey("/");
		assertEquals("Wrong button clicked", "/", text.getText().toString().substring(0, 1));
		this.clickOnKey("del");

		this.clickOnKey("^");
		assertEquals("Wrong button clicked", "^", text.getText().toString().substring(0, 1));
		this.clickOnKey("del");

		solo.clickOnButton(2);

	}

	public void testKeysFromFunctionKeyboard() {

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
		this.clickOnKey("cos");
		assertEquals("Wrong button clicked", "cos( 0 )", text.getText().toString().substring(0, "cos( 0 )".length()));
		this.clickOnKey("del");

		this.clickOnKey("sin");
		assertEquals("Wrong button clicked", "sin( 0 )", text.getText().toString().substring(0, "sin( 0 )".length()));
		this.clickOnKey("del");

		this.clickOnKey("tan");
		assertEquals("Wrong button clicked", "tan( 0 )", text.getText().toString().substring(0, "tan( 0 )".length()));
		this.clickOnKey("del");

		this.clickOnKey("ln");
		assertEquals("Wrong button clicked", "ln( 0 )", text.getText().toString().substring(0, "ln( 0 )".length()));
		this.clickOnKey("del");

		this.clickOnKey("log");
		assertEquals("Wrong button clicked", "log( 0 )", text.getText().toString().substring(0, "log( 0 )".length()));
		this.clickOnKey("del");

		this.clickOnKey("sqrt");
		assertEquals("Wrong button clicked", "sqrt( 0 )", text.getText().toString().substring(0, "sqrt( 0 )".length()));
		this.clickOnKey("del");

		this.clickOnKey("rand");
		assertEquals("Wrong button clicked", "rand( 0 , 1 )",
				text.getText().toString().substring(0, "rand( 0 , 1 )".length()));
		this.clickOnKey("del");

		this.clickOnKey("pi");
		assertEquals("Wrong button clicked", "pi", text.getText().toString().substring(0, "pi".length()));
		this.clickOnKey("del");
		this.clickOnKey("del");

		this.clickOnKey("e");
		assertEquals("Wrong button clicked", "e", text.getText().toString().substring(0, "e".length()));
		this.clickOnKey("del");

		solo.clickOnButton(2);
	}

	public void testKeysFromSensorKeyboard() {

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
		this.clickOnKey("x-accel");
		assertEquals("Wrong button clicked", "XACC_", text.getText().toString().substring(0, "XACC_".length()));
		this.clickOnKey("del");

		this.clickOnKey("y-accel");
		assertEquals("Wrong button clicked", "YACC_", text.getText().toString().substring(0, "YACC_".length()));
		this.clickOnKey("del");

		this.clickOnKey("z-accel");
		assertEquals("Wrong button clicked", "ZACC_", text.getText().toString().substring(0, "ZACC_".length()));
		this.clickOnKey("del");

		this.clickOnKey("pitch");
		assertEquals("Wrong button clicked", "PITCH_", text.getText().toString().substring(0, "PITCH_".length()));
		this.clickOnKey("del");

		this.clickOnKey("roll");
		assertEquals("Wrong button clicked", "ROLL_", text.getText().toString().substring(0, "ROLL_".length()));
		this.clickOnKey("del");

		this.clickOnKey("azimuth");
		assertEquals("Wrong button clicked", "AZIM_", text.getText().toString().substring(0, "AZIM_".length()));
		this.clickOnKey("del");

		solo.clickOnButton(2);
	}

	//	public void testLanguageKeys() {
	//
	//		solo.clickOnEditText(0);
	//		solo.clickOnEditText(0);
	//		solo.clickOnEditText(1);
	//
	//		this.clickOnKey("del");
	//		this.clickOnKey("del");
	//		this.clickOnKey("del");
	//		this.clickOnKey("del");
	//
	//		Log.i("info", "searchText for ',' " + solo.searchText(","));
	//		Log.i("info", "searchButton for ',' " + solo.searchButton(","));
	//		Log.i("info", "imageButton size: " + solo.getCurrentImageButtons().size());
	//		Log.i("info", "imageViews size: " + solo.getCurrentImageViews().size());
	//		Log.i("info", "listViews size: " + solo.getCurrentListViews().size());
	//		Log.i("info", "allOpenedActivities size: " + solo.getAllOpenedActivities().size());
	//		Log.i("info", "currentActivity: " + solo.getCurrentActivity());
	//		Log.i("info", "allCurrentButtons size: " + solo.getCurrentButtons().size());
	//
	//		assertEquals(solo.searchText(","), true);
	//	}

	private void createProject(String projectName) throws InterruptedException {

		this.project = new Project(null, projectName);
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
		keyString.get(0).add("0");
		keyString.get(0).add("7");
		keyString.get(0).add("4");
		keyString.get(0).add("1");
		keyString.get(0).add(".");
		keyString.get(0).add("8");
		keyString.get(0).add("5");
		keyString.get(0).add("2");
		keyString.get(0).add("space");
		keyString.get(0).add("9");
		keyString.get(0).add("6");
		keyString.get(0).add("3");
		keyString.get(0).add("space2");
		keyString.get(0).add("del");
		keyString.get(0).add("*");
		keyString.get(0).add("+");
		keyString.get(0).add("keyboardswitch");
		keyString.get(0).add("^");
		keyString.get(0).add("/");
		keyString.get(0).add("-");

		keyString.get(1).add("pi");
		keyString.get(1).add("rand");
		keyString.get(1).add("ln");
		keyString.get(1).add("sin");
		keyString.get(1).add("e");
		keyString.get(1).add("rand2");
		keyString.get(1).add("log");
		keyString.get(1).add("cos");
		keyString.get(1).add("space");
		keyString.get(1).add("rand3");
		keyString.get(1).add("sqrt");
		keyString.get(1).add("tan");
		keyString.get(1).add("space2");
		keyString.get(1).add("del");
		keyString.get(1).add("*");
		keyString.get(1).add("+");
		keyString.get(1).add("keyboardswitch");
		keyString.get(1).add("^");
		keyString.get(1).add("/");
		keyString.get(1).add("-");

		keyString.get(2).add("freecookies");
		keyString.get(2).add("pitch");
		keyString.get(2).add("z-accel");
		keyString.get(2).add("x-accel");
		keyString.get(2).add("freecookies2");
		keyString.get(2).add(null);
		keyString.get(2).add(null);
		keyString.get(2).add(null);
		keyString.get(2).add("space");
		keyString.get(2).add("roll");
		keyString.get(2).add("azimuth");
		keyString.get(2).add("y-accel");
		keyString.get(2).add("space2");
		keyString.get(2).add("del");
		keyString.get(2).add("*");
		keyString.get(2).add("+");
		keyString.get(2).add("keyboardswitch");
		keyString.get(2).add("^");
		keyString.get(2).add("/");
		keyString.get(2).add("-");

	}

	private void setCoordinatesForKeys() {
		//Setting x,y coordinates for each key
		int z = 0;
		for (int h = 0; h < this.keyString.size(); h++) {
			for (int i = 0; i < buttonsEachColumns; i++) {
				for (int j = 0; j < buttonsEachRow; j++) {

					Log.i("info", "setUp()" + " i:" + i + " j:" + j + " z:" + z + " h:" + h);
					int x = i * buttonWidth + buttonWidth / 2;
					int y = displayHeight - (j * (int) buttonHeight + (int) buttonHeight / 2);
					this.keyMap.put(this.keyString.get(h).get(z), new Point(x, y));
					++z;
					z = z % keyString.get(h).size();

				}
			}
		}
	}

	private void calculateCoordinatesOnScreen() {

		DisplayMetrics currentDisplayMetrics = new DisplayMetrics();
		solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getMetrics(currentDisplayMetrics);

		Log.i("info", "DisplayMetrics" + "width:" + currentDisplayMetrics.widthPixels + " height:"
				+ currentDisplayMetrics.heightPixels);

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
