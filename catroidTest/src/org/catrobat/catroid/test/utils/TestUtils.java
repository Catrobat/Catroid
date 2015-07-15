/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.FileChecksumContainer;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ComeToFrontBrick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.utils.NotificationData;
import org.catrobat.catroid.utils.StatusBarNotificationManager;
import org.catrobat.catroid.utils.UtilFile;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public final class TestUtils {
	public static final int TYPE_IMAGE_FILE = 0;
	public static final int TYPE_SOUND_FILE = 1;
	public static final String DEFAULT_TEST_PROJECT_NAME = "testProject";
	public static final String CORRUPT_PROJECT_NAME = "copiedProject";
	public static final String EMPTY_PROJECT = "emptyProject";

	private static final String TAG = TestUtils.class.getSimpleName();

	// Suppress default constructor for noninstantiability
	private TestUtils() {
		throw new AssertionError();
	}

	/**
	 * saves a file into the project folder
	 * if project == null or "" file will be saved into Catroid folder
	 *
	 * @param project Folder where the file will be saved, this folder should exist
	 * @param name    Name of the file
	 * @param fileID  the id of the file --> needs the right context
	 * @param context
	 * @param type    type of the file: 0 = imagefile, 1 = soundfile
	 * @return the file
	 * @throws IOException
	 */
	public static File saveFileToProject(String project, String name, int fileID, Context context, int type)
			throws IOException {

		String filePath;
		if (project == null || project.equalsIgnoreCase("")) {
			filePath = Constants.DEFAULT_ROOT + "/" + name;
		} else {
			switch (type) {
				case TYPE_IMAGE_FILE:
					filePath = Constants.DEFAULT_ROOT + "/" + project + "/" + Constants.IMAGE_DIRECTORY + "/" + name;
					break;
				case TYPE_SOUND_FILE:
					filePath = Constants.DEFAULT_ROOT + "/" + project + "/" + Constants.SOUND_DIRECTORY + "/" + name;
					break;
				default:
					filePath = Constants.DEFAULT_ROOT + "/" + name;
					break;
			}
		}

		return createTestMediaFile(filePath, fileID, context);
	}

	public static boolean clearProject(String projectname) {
		File directory = new File(Constants.DEFAULT_ROOT + "/" + projectname);
		if (directory.exists()) {
			return UtilFile.deleteDirectory(directory);
		}
		return false;
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

	public static String getProjectfileAsString(String projectName) {
		File projectFile = new File(Constants.DEFAULT_ROOT + "/" + projectName + "/" + Constants.PROJECTCODE_NAME);
		if (!projectFile.exists()) {
			return null;
		}
		StringBuilder contents = new StringBuilder();

		try {
			BufferedReader input = new BufferedReader(new FileReader(projectFile), Constants.BUFFER_8K);
			try {
				String line = null;
				while ((line = input.readLine()) != null) {
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
				}
			} finally {
				input.close();
			}
		} catch (IOException ioException) {
			Log.e(TAG, Log.getStackTraceString(ioException));
		}
		return contents.toString();
	}

	//	private static class ProjectWithCatrobatLanguageVersion extends Project {
	//		static final long serialVersionUID = 1L;
	//		private final float catrobatLanguageVersion;
	//
	//		@SuppressWarnings("unused")
	//		public ProjectWithCatrobatLanguageVersion() {
	//			catrobatLanguageVersion = 0.1f;
	//		}
	//
	//		public ProjectWithCatrobatLanguageVersion(String name, float catrobatLanguageVersion) {
	//			super(null, name);
	//			this.catrobatLanguageVersion = catrobatLanguageVersion;
	//		}
	//
	//		@Override
	//		public float getCatrobatLanguageVersion() {
	//			return catrobatLanguageVersion;
	//		}
	//	}

	public static Project createTestProjectOnLocalStorageWithCatrobatLanguageVersionAndName(
			float catrobatLanguageVersion, String name) {
		//		Project project = new ProjectWithCatrobatLanguageVersion(name, catrobatLanguageVersion);
		Project project = new Project(null, name);
		project.setCatrobatLanguageVersion(catrobatLanguageVersion);

		Sprite firstSprite = new Sprite("cat");
		Script testScript = new StartScript();
		Brick testBrick = new HideBrick();
		testScript.addBrick(testBrick);

		firstSprite.addScript(testScript);
		project.addSprite(firstSprite);

		StorageHandler.getInstance().saveProject(project);
		return project;
	}

	public static List<Brick> createTestProjectWithWrongIfClauseReferences() {
		ProjectManager projectManager = ProjectManager.getInstance();
		Project project = new Project(null, CORRUPT_PROJECT_NAME);
		Sprite firstSprite = new Sprite("corruptReferences");

		Script testScript = new StartScript();

		ArrayList<Brick> brickList = new ArrayList<Brick>();

		IfLogicBeginBrick ifBeginBrick = new IfLogicBeginBrick(0);
		IfLogicElseBrick ifElseBrick = new IfLogicElseBrick(ifBeginBrick);
		ifElseBrick.setIfBeginBrick(null);

		IfLogicBeginBrick ifBeginBrickNested = new IfLogicBeginBrick(0);
		//reference shouldn't be null:
		IfLogicElseBrick ifElseBrickNested = new IfLogicElseBrick(ifBeginBrickNested);
		ifElseBrickNested.setIfBeginBrick(null);
		//reference shouldn't be null + wrong ifElseBrickReference:
		IfLogicEndBrick ifEndBrickNested = new IfLogicEndBrick(ifElseBrick, ifBeginBrickNested);
		ifEndBrickNested.setIfBeginBrick(null);

		//reference to wrong ifBegin and ifEnd-Bricks:
		IfLogicEndBrick ifEndBrick = new IfLogicEndBrick(ifElseBrickNested, ifBeginBrickNested);

		brickList.add(ifBeginBrick);
		brickList.add(new ShowBrick());
		brickList.add(ifElseBrick);
		brickList.add(new ComeToFrontBrick());
		brickList.add(ifBeginBrickNested);
		brickList.add(new ComeToFrontBrick());
		brickList.add(ifElseBrickNested);
		brickList.add(new ShowBrick());
		brickList.add(ifEndBrickNested);
		brickList.add(ifEndBrick);

		for (Brick brick : brickList) {
			testScript.addBrick(brick);
		}

		firstSprite.addScript(testScript);

		project.addSprite(firstSprite);

		projectManager.setFileChecksumContainer(new FileChecksumContainer());
		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);

		return brickList;
	}

	public static Project createTestProjectOnLocalStorageWithCatrobatLanguageVersion(float catrobatLanguageVersion) {
		return createTestProjectOnLocalStorageWithCatrobatLanguageVersionAndName(catrobatLanguageVersion,
				DEFAULT_TEST_PROJECT_NAME);
	}

	public static Project createEmptyProject() {
		Project project = new Project(null, EMPTY_PROJECT);
		StorageHandler.getInstance().saveProject(project);
		return project;
	}

	public static void deleteTestProjects(String... additionalProjectNames) {
		Log.d(TAG, "deleteTestProjects");
		ProjectManager.getInstance().setFileChecksumContainer(new FileChecksumContainer());

		File directory = new File(Constants.DEFAULT_ROOT + "/" + DEFAULT_TEST_PROJECT_NAME);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		for (String name : additionalProjectNames) {
			directory = new File(Constants.DEFAULT_ROOT + "/" + name);
			if (directory.exists()) {
				UtilFile.deleteDirectory(directory);
			}
		}
	}

	public static void cancelAllNotifications(Context context) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		@SuppressWarnings("unchecked")
		SparseArray<NotificationData> notificationMap = (SparseArray<NotificationData>) Reflection.getPrivateField(
				StatusBarNotificationManager.class, StatusBarNotificationManager.getInstance(), "notificationDataMap");
		if (notificationMap == null) {
			return;
		}

		for (int i = 0; i < notificationMap.size(); i++) {
			notificationManager.cancel(notificationMap.keyAt(i));
		}

		notificationMap.clear();
	}

	public static Script addUserBrickToSpriteAndGetUserScript(UserBrick userBrick, Sprite sprite) {
		UserScriptDefinitionBrick definitionBrick = (UserScriptDefinitionBrick) Reflection.getPrivateField(userBrick,
				"definitionBrick");
		sprite.addUserBrick(userBrick);
		return definitionBrick.getScriptSafe();
	}

	public static void removeFromPreferences(Context context, String key) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = preferences.edit();
		edit.remove(key);
		edit.commit();
	}
}
