/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.uitest.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;
import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
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
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.utils.UtilFile;
import at.tugraz.ist.catroid.utils.Utils;

import com.jayway.android.robotium.solo.Solo;

public class UiTestUtils {
	private static ProjectManager projectManager = ProjectManager.getInstance();
	private static HashMap<Integer, Integer> brickCategoryMap;

	public static final String DEFAULT_TEST_PROJECT_NAME = "testProject";
	public static final String PROJECTNAME1 = "testproject1";
	public static final String PROJECTNAME2 = "testproject2";
	public static final String PROJECTNAME3 = "testproject3";
	public static final String PROJECTNAME4 = "testproject4";
	public static final int TYPE_IMAGE_FILE = 0;
	public static final int TYPE_SOUND_FILE = 1;

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
	public static void insertIntegerIntoEditText(Solo solo, int editTextId, int value) {
		insertValue(solo, editTextId, value + "");
	}

	/**
	 * Clicks on the EditText given by editTextId, inserts the double value and closes the Dialog
	 * 
	 * @param editTextId
	 *            The ID of the EditText to click on
	 * @param value
	 *            The value you want to put into the EditText
	 */
	public static void insertDoubleIntoEditText(Solo solo, int editTextId, double value) {
		insertValue(solo, editTextId, value + "");
	}

	private static void insertValue(Solo solo, int editTextId, String value) {
		solo.clickOnEditText(editTextId);
		solo.sleep(50);
		solo.clearEditText(0);
		solo.enterText(0, value);
	}

	public static void clickEnterClose(Solo solo, int editTextIndex, String value) {
		solo.clickOnEditText(editTextIndex);
		enterText(solo, 0, value);
		solo.clickOnButton(0);
		solo.sleep(50);
	}

	private static void initBrickCategoryMap() {
		brickCategoryMap = new HashMap<Integer, Integer>();

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

	public static void addNewBrickAndScrollDown(Solo solo, int brickStringId) {
		int categoryStringId = getBrickCategory(solo, brickStringId);
		addNewBrickAndScrollDown(solo, categoryStringId, brickStringId);
	}

	/**
	 * Ugly workaround because Robotium is acting flaky with clickOnText
	 */

	private static View getCategoryViewFromCategoryStringId(Solo solo, int categoryStringId) {
		String text = solo.getString(categoryStringId);
		ListView listView = solo.getCurrentListViews().get(0);

		switch (categoryStringId) {
			case R.string.category_motion:
				return listView.getChildAt(0);
			case R.string.category_looks:
				return listView.getChildAt(1);
			case R.string.category_sound:
				return listView.getChildAt(2);
			case R.string.category_control:
				return listView.getChildAt(3);

		}
		/*
		 * for (int i = 0; i < listView.getChildCount(); i++) {
		 * LinearLayout childView = (LinearLayout) listView.getChildAt(i);
		 * TextView textView = (TextView) childView.getChildAt(0);
		 * Log.d("catroid", "Looking for: " + text + ", current: " + textView.getText());
		 * if (textView.getText().equals(text)) {
		 * return textView;
		 * }
		 * }
		 */
		throw new RuntimeException("Unknown category: " + text);
	}

	public static void addNewBrickAndScrollDown(Solo solo, int categoryStringId, int brickStringId) {
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_add_sprite);

		Log.d("catroid", "---------- addNewBrickAndScrollDown ----------");

		ListView listView = solo.getCurrentListViews().get(0);
		Log.d("catroid", "ListView has " + listView.getChildCount() + " entries");
		for (int i = 0; i < listView.getChildCount(); i++) {
			LinearLayout child = (LinearLayout) listView.getChildAt(i);
			TextView textView = (TextView) child.getChildAt(0);
			Log.d("catroid", "Found text view " + textView.getText() + " inside ListView");
		}

		List<TextView> textViews = solo.getCurrentTextViews(null);
		Log.d("catroid", textViews.size() + " TextViews were found");
		for (TextView textView : textViews) {
			Log.d("catroid", "Found text view " + textView.getText());
		}

		solo.sleep(500);
		solo.clickOnText(solo.getCurrentActivity().getString(categoryStringId));
		//solo.clickOnView(getCategoryViewFromCategoryStringId(solo, categoryStringId));
		solo.sleep(500);

		solo.clickOnText(solo.getCurrentActivity().getString(brickStringId));

		while (solo.scrollDown()) {
			;
		}
	}

	public static List<Brick> createTestProject() {
		int xPosition = 457;
		int yPosition = 598;
		double size = 0.8;

		Project project = new Project(null, DEFAULT_TEST_PROJECT_NAME);
		Sprite firstSprite = new Sprite("cat");

		Script testScript = new StartScript("testscript", firstSprite);

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

		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);

		return brickList;
	}

	public static void createEmptyProject() {
		Project project = new Project(null, DEFAULT_TEST_PROJECT_NAME);
		Sprite firstSprite = new Sprite("cat");
		Script testScript = new StartScript("testscript", firstSprite);

		firstSprite.addScript(testScript);
		project.addSprite(firstSprite);

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
	public static File saveFileToProject(String project, String name, int fileID, Context context, int type) {

		String filePath;
		if (project == null || project.equalsIgnoreCase("")) {
			filePath = Consts.DEFAULT_ROOT + "/";
		} else {
			switch (type) {
				case TYPE_IMAGE_FILE:
					filePath = Consts.DEFAULT_ROOT + "/" + project + Consts.IMAGE_DIRECTORY + "/";
					break;
				case TYPE_SOUND_FILE:
					filePath = Consts.DEFAULT_ROOT + "/" + project + Consts.SOUND_DIRECTORY + "/";
					break;
				default:
					filePath = Consts.DEFAULT_ROOT + "/";
					break;
			}
		}
		BufferedInputStream in = new BufferedInputStream(context.getResources().openRawResource(fileID),
				Consts.BUFFER_8K);

		try {
			File file = new File(filePath + name);
			file.getParentFile().mkdirs();
			file.createNewFile();

			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file), Consts.BUFFER_8K);
			byte[] buffer = new byte[Consts.BUFFER_8K];
			int length = 0;
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

			in.close();
			out.flush();
			out.close();

			String checksum = Utils.md5Checksum(file);
			File tempFile = new File(filePath + checksum + "_" + name);
			file.renameTo(tempFile);

			return tempFile;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean clearProject(String projectname) {
		File directory = new File(Consts.DEFAULT_ROOT + "/" + projectname);
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
		File directory = new File(Consts.DEFAULT_ROOT + "/" + PROJECTNAME1);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Consts.DEFAULT_ROOT + "/" + PROJECTNAME2);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Consts.DEFAULT_ROOT + "/" + PROJECTNAME3);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Consts.DEFAULT_ROOT + "/" + PROJECTNAME4);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Consts.DEFAULT_ROOT + "/" + DEFAULT_TEST_PROJECT_NAME);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Consts.DEFAULT_ROOT + "/" + "defaultProject");
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Consts.DEFAULT_ROOT + "/" + "standardProjekt");
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

	public static void clickOnImageButton(Solo solo, int imageButtonId) {
		solo.waitForView(ImageButton.class);
		ImageButton imageButton = (ImageButton) solo.getView(imageButtonId);
		solo.clickOnView(imageButton);
	}
}
