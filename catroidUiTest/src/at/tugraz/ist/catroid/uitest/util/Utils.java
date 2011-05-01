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
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.InputType;
import android.util.Log;
import at.tugraz.ist.catroid.Consts;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.bricks.GoNStepsBackBrick;
import at.tugraz.ist.catroid.content.bricks.HideBrick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.ScaleCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.ShowBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.utils.UtilFile;

import com.jayway.android.robotium.solo.Solo;

public class Utils {
	private static final int WAIT_TIME_IN_MILLISECONDS = 50;
	private static final String TAG = "UiTestUtils";
	public static final String DEFAULT_TEST_PROJECT_NAME = "testProject";

	public static void pause() {
		try {
			Thread.sleep(WAIT_TIME_IN_MILLISECONDS);
		} catch (InterruptedException e) {
			Log.e(TAG, "pause() threw an InterruptedException");
			Log.e(TAG, e.getMessage());
		}
	}

	public static void enterText(Solo solo, int editTextIndex, String text) {
		pause();
		solo.getEditText(editTextIndex).setInputType(InputType.TYPE_NULL);
		solo.enterText(editTextIndex, text);
		pause();
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

	public static void insertDoubleIntoEditText(Solo solo, int editTextId, double value) {
		insertValue(solo, editTextId, value + "");
	}

	private static void insertValue(Solo solo, int editTextId, String value) {
		solo.clickOnEditText(editTextId);
		Utils.pause();
		solo.clearEditText(0);
		solo.enterText(0, value);
	}

	public static void addNewBrickAndScrollDown(Solo solo, int brickStringId) {
		solo.clickOnButton(solo.getCurrentActivity().getString(R.string.add_new_brick));
		solo.clickOnText(solo.getCurrentActivity().getString(brickStringId));

		while (solo.scrollDown()) {
			;
		}
	}

	public static List<Brick> createTestProject() {
		int xPosition = 457;
		int yPosition = 598;
		double scaleValue = 0.8;

		Project project = new Project(null, DEFAULT_TEST_PROJECT_NAME);
		Sprite firstSprite = new Sprite("cat");

		Script testScript = new Script("testscript", firstSprite);

		ArrayList<Brick> brickList = new ArrayList<Brick>();
		brickList.add(new HideBrick(firstSprite));
		brickList.add(new ShowBrick(firstSprite));
		brickList.add(new ScaleCostumeBrick(firstSprite, scaleValue));
		brickList.add(new GoNStepsBackBrick(firstSprite, 1));
		brickList.add(new ComeToFrontBrick(firstSprite));
		brickList.add(new PlaceAtBrick(firstSprite, xPosition, yPosition));

		// adding Bricks: ----------------
		for (Brick brick : brickList) {
			testScript.addBrick(brick);
		}
		// -------------------------------

		firstSprite.getScriptList().add(testScript);

		project.addSprite(firstSprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
		ProjectManager.getInstance().setCurrentScript(testScript);

		return brickList;
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
	public static File saveFileToProject(String project, String name, int fileID, Context context, int type)
			throws IOException {

		String filePath;
		if (project == null || project.equalsIgnoreCase("")) {
			filePath = Consts.DEFAULT_ROOT + "/" + name;
		} else {
			switch (type) {
				case 0:
					filePath = Consts.DEFAULT_ROOT + "/" + project + Consts.IMAGE_DIRECTORY + "/" + name;
					break;
				case 1:
					filePath = Consts.DEFAULT_ROOT + "/" + project + Consts.SOUND_DIRECTORY + "/" + name;
					break;
				default:
					filePath = Consts.DEFAULT_ROOT + "/" + name;
					break;
			}
		}
		BufferedInputStream in = new BufferedInputStream(context.getResources().openRawResource(fileID));

		File file = new File(filePath);
		file.createNewFile();

		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file), 1024);
		byte[] buffer = new byte[1024];
		int length = 0;
		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}

		in.close();
		out.flush();
		out.close();

		return file;
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
}
