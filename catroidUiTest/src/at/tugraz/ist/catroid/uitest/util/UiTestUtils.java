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
package at.tugraz.ist.catroid.uitest.util;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.common.FileChecksumContainer;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.bricks.GoNStepsBackBrick;
import at.tugraz.ist.catroid.content.bricks.HideBrick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;
import at.tugraz.ist.catroid.content.bricks.ShowBrick;
import at.tugraz.ist.catroid.formulaeditor.Formula;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.utils.UtilFile;
import at.tugraz.ist.catroid.utils.UtilToken;
import at.tugraz.ist.catroid.utils.Utils;
import at.tugraz.ist.catroid.web.ServerCalls;
import at.tugraz.ist.catroid.web.WebconnectionException;

import com.jayway.android.robotium.solo.Solo;

public class UiTestUtils {
	private static final String TAG = UiTestUtils.class.getSimpleName();

	private static ProjectManager projectManager = ProjectManager.getInstance();
	private static SparseIntArray brickCategoryMap;

	public static final String DEFAULT_TEST_PROJECT_NAME = "testProject";
	public static final String PROJECTNAME1 = "testproject1";
	public static final String PROJECTNAME2 = "testproject2";
	public static final String PROJECTNAME3 = "testproject3";
	public static final String DEFAULT_TEST_PROJECT_NAME_MIXED_CASE = "TeStPROjeCt";

	public static enum FileTypes {
		IMAGE, SOUND, ROOT
	};

	private UiTestUtils() {
	};

	public static void enterText(Solo solo, int editTextIndex, String text) {
		solo.sleep(50);
		solo.getEditText(editTextIndex).setInputType(InputType.TYPE_NULL);
		solo.clearEditText(editTextIndex);
		solo.enterText(editTextIndex, text);
		solo.sleep(50);
	}

	/**
	 * Clicks on the EditText given by editTextId, inserts the integer value and closes the Dialog
	 * 
	 * @param editTextId
	 *            The ID of the EditText to click on
	 * @param value
	 *            The value you want to put into the EditText
	 */
	public static void insertIntegerIntoEditText(Solo solo, int editTextId, int editorEditFieldId, int value) {
		insertValue(solo, editTextId, editorEditFieldId, value + "");
	}

	/**
	 * Clicks on the EditText given by editTextId, inserts the double value and closes the Dialog
	 * 
	 * @param editTextId
	 *            The ID of the EditText to click on
	 * @param value
	 *            The value you want to put into the EditText
	 */
	public static void insertDoubleIntoEditText(Solo solo, int editTextId, int editorEditFieldId, double value) {
		insertValue(solo, editTextId, editorEditFieldId, value + "");
	}

	private static void insertValue(Solo solo, int editTextId, int editorEditFieldId, String value) {
		solo.clickOnEditText(editTextId);
		solo.sleep(50);
		solo.clearEditText(editorEditFieldId);
		solo.enterText(editorEditFieldId, value);
	}

	public static void clickEnterClose(Solo solo, int editTextIndex, String value) {
		solo.clickOnEditText(editTextIndex);
		enterText(solo, 0, value);
		solo.clickOnButton(0);
		solo.sleep(50);
	}

	private static void initBrickCategoryMap() {
		brickCategoryMap = new SparseIntArray();

		brickCategoryMap.put(R.string.brick_place_at, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_set_x, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_set_y, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_change_x_by, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_change_y_by, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_go_back, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_come_to_front, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_if_on_edge_bounce, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_move_n_steps, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_turn_left, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_turn_right, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_point_in_direction, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_point_to, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_glide, R.string.category_motion);

		brickCategoryMap.put(R.string.brick_set_costume, R.string.category_looks);
		brickCategoryMap.put(R.string.brick_set_size_to, R.string.category_looks);
		brickCategoryMap.put(R.string.brick_change_size_by, R.string.category_looks);
		brickCategoryMap.put(R.string.brick_hide, R.string.category_looks);
		brickCategoryMap.put(R.string.brick_show, R.string.category_looks);
		brickCategoryMap.put(R.string.brick_set_ghost_effect, R.string.category_looks);
		brickCategoryMap.put(R.string.brick_set_brightness, R.string.category_looks);
		brickCategoryMap.put(R.string.brick_change_brightness, R.string.category_looks);
		brickCategoryMap.put(R.string.brick_clear_graphic_effect, R.string.category_looks);
		brickCategoryMap.put(R.string.brick_say, R.string.category_looks);
		brickCategoryMap.put(R.string.brick_think, R.string.category_looks);

		brickCategoryMap.put(R.string.brick_play_sound, R.string.category_sound);
		brickCategoryMap.put(R.string.brick_stop_all_sounds, R.string.category_sound);
		brickCategoryMap.put(R.string.brick_set_volume_to, R.string.category_sound);
		brickCategoryMap.put(R.string.brick_change_volume_by, R.string.category_sound);
		brickCategoryMap.put(R.string.brick_speak, R.string.category_sound);

		brickCategoryMap.put(R.string.brick_when_started, R.string.category_control);
		brickCategoryMap.put(R.string.brick_when, R.string.category_control);
		brickCategoryMap.put(R.string.brick_wait, R.string.category_control);
		brickCategoryMap.put(R.string.brick_broadcast_receive, R.string.category_control);
		brickCategoryMap.put(R.string.brick_broadcast, R.string.category_control);
		brickCategoryMap.put(R.string.brick_broadcast_wait, R.string.category_control);
		brickCategoryMap.put(R.string.brick_note, R.string.category_control);
		brickCategoryMap.put(R.string.brick_forever, R.string.category_control);
		brickCategoryMap.put(R.string.brick_repeat, R.string.category_control);

		brickCategoryMap.put(R.string.brick_motor_action, R.string.category_lego_nxt);
	}

	public static int getBrickCategory(Solo solo, int brickStringId) {
		if (brickCategoryMap == null) {
			initBrickCategoryMap();
		}

		Integer brickCategoryid = brickCategoryMap.get(brickStringId);
		if (brickCategoryid == null) {
			String brickString = solo.getCurrentActivity().getString(brickStringId);
			throw new RuntimeException("No category was found for brick string \"" + brickString + "\".\n"
					+ "Please check brick string or add brick string to category map");
		}

		return brickCategoryMap.get(brickStringId);
	}

	public static void addNewBrick(Solo solo, int brickStringId) {
		int categoryStringId = getBrickCategory(solo, brickStringId);
		addNewBrick(solo, categoryStringId, brickStringId);
	}

	public static void addNewBrick(Solo solo, int categoryStringId, int brickStringId) {
		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_add_button);
		solo.clickOnText(solo.getCurrentActivity().getString(categoryStringId));
		solo.clickOnText(solo.getCurrentActivity().getString(brickStringId));
	}

	public static List<Brick> createTestProject() {
		int xPosition = 457;
		int yPosition = 598;
		double size = 0.8;

		Project project = new Project(null, DEFAULT_TEST_PROJECT_NAME);
		Sprite firstSprite = new Sprite("cat");

		Script testScript = new StartScript(firstSprite);

		ArrayList<Brick> brickList = new ArrayList<Brick>();
		brickList.add(new HideBrick(firstSprite));
		brickList.add(new ShowBrick(firstSprite));
		brickList.add(new SetSizeToBrick(firstSprite, size));
		brickList.add(new GoNStepsBackBrick(firstSprite, 1));
		brickList.add(new ComeToFrontBrick(firstSprite));
		brickList.add(new PlaceAtBrick(firstSprite, xPosition, yPosition));

		for (Brick brick : brickList) {
			testScript.addBrick(brick);
		}

		firstSprite.addScript(testScript);

		project.addSprite(firstSprite);

		projectManager.fileChecksumContainer = new FileChecksumContainer();
		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);

		return brickList;
	}

	public static void createEmptyProject() {
		Project project = new Project(null, DEFAULT_TEST_PROJECT_NAME);
		Sprite firstSprite = new Sprite("cat");
		Script testScript = new StartScript(firstSprite);

		firstSprite.addScript(testScript);
		project.addSprite(firstSprite);

		projectManager.fileChecksumContainer = new FileChecksumContainer();
		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);
	}

	/**
	 * saves a file into the project folder
	 * if project == null or "" file will be saved into Catroid folder
	 * 
	 * @param project
	 *            Folder where the file will be saved, this folder should exist
	 * @param name
	 *            Name of the file
	 * @param fileID
	 *            the id of the file --> needs the right context
	 * @param context
	 * @param type
	 *            type of the file: 0 = imagefile, 1 = soundfile
	 * @return the file
	 * @throws IOException
	 */
	public static File saveFileToProject(String project, String name, int fileID, Context context, FileTypes type) {

		boolean withChecksum = true;
		String filePath;
		if (project == null || project.equalsIgnoreCase("")) {
			filePath = Constants.DEFAULT_ROOT + "/";
		} else {
			switch (type) {
				case IMAGE:
					filePath = Constants.DEFAULT_ROOT + "/" + project + "/" + Constants.IMAGE_DIRECTORY + "/";
					break;
				case SOUND:
					filePath = Constants.DEFAULT_ROOT + "/" + project + "/" + Constants.SOUND_DIRECTORY + "/";
					break;
				case ROOT:
					filePath = Constants.DEFAULT_ROOT + "/" + project + "/";
					withChecksum = false;
					break;
				default:
					filePath = Constants.DEFAULT_ROOT + "/";
					break;
			}
		}
		BufferedInputStream in = new BufferedInputStream(context.getResources().openRawResource(fileID),
				Constants.BUFFER_8K);

		try {
			File file = new File(filePath + name);
			file.getParentFile().mkdirs();
			file.createNewFile();

			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file), Constants.BUFFER_8K);
			byte[] buffer = new byte[Constants.BUFFER_8K];
			int length = 0;
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

			in.close();
			out.flush();
			out.close();

			String checksum;
			if (withChecksum) {
				checksum = Utils.md5Checksum(file) + "_";
			} else {
				checksum = "";
			}

			File tempFile = new File(filePath + checksum + name);
			file.renameTo(tempFile);

			return tempFile;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean clearProject(String projectname) {
		File directory = new File(Constants.DEFAULT_ROOT + "/" + projectname);
		if (directory.exists()) {
			return UtilFile.deleteDirectory(directory);
		}
		return false;
	}

	public static Project createProject(String projectName, ArrayList<Sprite> spriteList, Context context) {
		Project project = new Project(context, projectName);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		for (Sprite sprite : spriteList) {
			ProjectManager.getInstance().addSprite(sprite);
		}

		StorageHandler.getInstance().saveProject(project);
		return project;
	}

	public static void clearAllUtilTestProjects() {
		projectManager.fileChecksumContainer = new FileChecksumContainer();
		File directory = new File(Constants.DEFAULT_ROOT + "/" + PROJECTNAME1);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + PROJECTNAME2);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + PROJECTNAME3);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + DEFAULT_TEST_PROJECT_NAME);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + "defaultProject");
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + "standardProjekt");
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + "My first project");
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + "Mein erstes Projekt");
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}
	}

	public static Object getPrivateField(String fieldName, Object object) {
		try {
			Field field = object.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(object);
		} catch (Exception e) {
			Assert.fail(e.getClass().getName() + " when accessing " + fieldName);
		}
		return null;
	}

	public static void setPrivateField(String fieldName, Object object, Object value, boolean ofSuperclass) {

		Field field = null;

		try {
			Class<?> c = object.getClass();
			field = ofSuperclass ? c.getSuperclass().getDeclaredField(fieldName) : c.getDeclaredField(fieldName);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			Log.e(TAG, e.getClass().getName() + ": " + fieldName);
		}

		if (field != null) {
			field.setAccessible(true);

			try {
				field.set(object, value);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public static void clickOnLinearLayout(Solo solo, int imageButtonId) {
		solo.waitForView(LinearLayout.class);
		LinearLayout linearLayout = (LinearLayout) solo.getView(imageButtonId);
		solo.clickOnView(linearLayout);
	}

	public static File createTestMediaFile(String filePath, int fileID, Context context) throws IOException {

		File testImage = new File(filePath);

		if (!testImage.exists()) {
			testImage.createNewFile();
		}

		InputStream in = context.getResources().openRawResource(fileID);
		OutputStream out = new BufferedOutputStream(new FileOutputStream(testImage), Constants.BUFFER_8K);

		byte[] buffer = new byte[Constants.BUFFER_8K];
		int length = 0;

		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}

		in.close();
		out.flush();
		out.close();

		return testImage;
	}

	public static void createValidUser(Context context) {
		try {
			String testUser = "testUser" + System.currentTimeMillis();
			String testPassword = "pwspws";
			String testEmail = testUser + "@gmail.com";

			String token = UtilToken.calculateToken(testUser, testPassword);
			boolean userRegistered = ServerCalls.getInstance().registerOrCheckToken(testUser, testPassword, testEmail,
					"de", "at", token);

			assert (userRegistered);

			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
			sharedPreferences.edit().putString(Constants.TOKEN, token).commit();

		} catch (WebconnectionException e) {
			e.printStackTrace();
			fail("Error creating test user.");
		}
	}

	// Stage methods
	public static void compareByteArrays(byte[] firstArray, byte[] secondArray) {
		assertEquals("Length of byte arrays not equal", firstArray.length, secondArray.length);
		assertEquals("Arrays don't have same content.", firstArray[0], secondArray[0], 10);
		assertEquals("Arrays don't have same content.", firstArray[1], secondArray[1], 10);
		assertEquals("Arrays don't have same content.", firstArray[2], secondArray[2], 10);
		assertEquals("Arrays don't have same content.", firstArray[3], secondArray[3], 10);
	}

	public static void comparePixelArrayWithPixelScreenArray(byte[] pixelArray, byte[] screenArray, int x, int y,
			int screenWidth, int screenHeight) {
		assertEquals("Length of pixel array not 4", 4, pixelArray.length);
		int convertedX = x + (screenWidth / 2);
		int convertedY = y + (screenHeight / 2);
		byte[] screenPixel = new byte[4];
		for (int i = 0; i < 4; i++) {
			screenPixel[i] = screenArray[(convertedX * 3 + convertedX + convertedY * screenWidth * 4) + i];
		}
		assertEquals("Pixels don't have same content.", pixelArray[0], screenPixel[0], 10);
		assertEquals("Pixels don't have same content.", pixelArray[1], screenPixel[1], 10);
		assertEquals("Pixels don't have same content.", pixelArray[2], screenPixel[2], 10);
		assertEquals("Pixels don't have same content.", pixelArray[3], screenPixel[3], 10);
	}

	public static void testIntegerEditText(Solo solo, int editTextIndex, int editorEditFieldId, int value,
			int editTextMinWidth, boolean assertMode) {
		insertIntegerIntoEditText(solo, editTextIndex, editorEditFieldId, value);
		testEditText(solo, editTextIndex, value + "", editTextMinWidth, assertMode);
	}

	public static void testDoubleEditText(Solo solo, int editTextIndex, int editorEditFieldId, double value,
			int editTextMinWidth, boolean assertMode) {
		insertDoubleIntoEditText(solo, editTextIndex, editorEditFieldId, value);
		testEditText(solo, editTextIndex, value + "", editTextMinWidth, assertMode);
	}

	private static void testEditText(Solo solo, int editTextIndex, String value, int editTextMinWidth,
			boolean assertMode) {
		//be aware, negativ numbers are displayed as "- 0.1", not "-0.1" by the FE

		if (value.startsWith("-")) {
			value = "- " + value.substring(1);

		}

		String buttonOKText = solo.getCurrentActivity().getString(R.string.formula_editor_button_save);
		solo.waitForText(buttonOKText);
		solo.clickOnText(buttonOKText);
		solo.sleep(100);

		String buttonBackText = solo.getCurrentActivity().getString(R.string.formula_editor_button_return);
		solo.waitForText(buttonBackText);
		solo.clickOnText(buttonBackText);
		solo.sleep(100);

		int width = 0;
		if (assertMode) {
			assertTrue("EditText not resized - value not (fully) visible", solo.searchText(value));
			width = solo.getEditText(editTextIndex).getWidth();
			assertTrue("Minwidth of EditText should be " + editTextMinWidth + " dpi",
					width >= Utils.getPhysicalPixels(editTextMinWidth, solo.getCurrentActivity().getBaseContext()));
		} else {
			assertFalse("Number too long - should not be resized and fully visible", solo.searchText(value));
		}
	}

	/**
	 * For bricks using the FormulaEditor. Tests starting the FE, entering a new number/formula and
	 * ensures its set correctly to the brick´s edit text field
	 */
	public static void testBrickWithFormulaEditor(Solo solo, int editTextNumber, int numberOfEditTextsInBrick,
			double newValue, String fieldName, Object theBrick) {

		solo.clickOnEditText(editTextNumber);
		solo.clearEditText(numberOfEditTextsInBrick);
		solo.enterText(numberOfEditTextsInBrick, newValue + "");
		solo.clickOnButton(solo.getString(R.string.formula_editor_button_save));
		solo.sleep(300);

		assertEquals("Text not updated within FormulaEditor", newValue,
				Double.parseDouble(solo.getEditText(editTextNumber).getText().toString()));
		solo.clickOnButton(solo.getString(R.string.formula_editor_button_return));
		solo.sleep(300);

		Formula formula = (Formula) UiTestUtils.getPrivateField(fieldName, theBrick);

		assertEquals("Wrong text in field", newValue, formula.interpret());
		assertEquals("Text not updated in the brick list", newValue,
				Double.parseDouble(solo.getEditText(editTextNumber).getText().toString()));

	}

	/**
	 * Returns the absolute pixel y coordinates of the displayed bricks
	 * 
	 * @return a list of the y pixel coordinates of the center of displayed bricks
	 */
	public static ArrayList<Integer> getListItemYPositions(final Solo solo) {
		ArrayList<Integer> yPositionList = new ArrayList<Integer>();
		ListView listView = solo.getCurrentListViews().get(0);

		for (int i = 0; i < listView.getChildCount(); ++i) {
			View currentViewInList = listView.getChildAt(i);

			Rect globalVisibleRect = new Rect();
			currentViewInList.getGlobalVisibleRect(globalVisibleRect);
			int middleYPos = globalVisibleRect.top + globalVisibleRect.height() / 2;
			yPositionList.add(middleYPos);
		}

		return yPositionList;
	}

	private static class ProjectWithVersionCode extends Project {
		static final long serialVersionUID = 1L;
		private final int mCatroidVersionCode;

		public ProjectWithVersionCode(String name, int catroidVersionCode) {
			super(null, name);
			mCatroidVersionCode = catroidVersionCode;
		}

		@Override
		public int getCatroidVersionCode() {
			return mCatroidVersionCode;
		}
	}

	public static boolean createTestProjectOnLocalStorageWithVersionCode(int versionCode) {
		Project project = new ProjectWithVersionCode(DEFAULT_TEST_PROJECT_NAME, versionCode);
		Sprite firstSprite = new Sprite("cat");
		Script testScript = new StartScript(firstSprite);

		firstSprite.addScript(testScript);
		project.addSprite(firstSprite);

		ProjectManager.INSTANCE.fileChecksumContainer = new FileChecksumContainer();
		ProjectManager.INSTANCE.setProject(project);
		ProjectManager.INSTANCE.setCurrentSprite(firstSprite);
		ProjectManager.INSTANCE.setCurrentScript(testScript);
		return ProjectManager.INSTANCE.saveProject();
	}
}
