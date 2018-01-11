/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class LegacyFileUtils {
	private static ProjectManager projectManager = ProjectManager.getInstance();
	private static final String TAG = LegacyFileUtils.class.getName();

	public static final String DEFAULT_TEST_PROJECT_NAME = "testProject";
	public static final String PROJECTNAME1 = "testingproject1";
	public static final String PROJECTNAME2 = "testingproject2";
	public static final String PROJECTNAME3 = "testingproject3";
	public static final String DEFAULT_TEST_PROJECT_NAME_MIXED_CASE = "TeStPROjeCt";
	public static final String COPIED_PROJECT_NAME = "copiedProject";
	public static final String JAPANESE_PROJECT_NAME = "これは例の説明です。";
	private static final List<Object> INITIALIZED_LIST_VALUES = new ArrayList<>();
	static {
		INITIALIZED_LIST_VALUES.add(1.0);
		INITIALIZED_LIST_VALUES.add(2.0);
	}

	public enum FileTypes {
		IMAGE, SOUND, ROOT, SCREENSHOT
	}

	private LegacyFileUtils() {
		throw new AssertionError();
	}

	public static void createEmptyProject() {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), DEFAULT_TEST_PROJECT_NAME);
		Sprite firstSprite = new SingleSprite("cat");
		Script testScript = new StartScript();

		firstSprite.addScript(testScript);
		project.getDefaultScene().addSprite(firstSprite);

		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);
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
	public static File saveFileToProject(String project, String sceneName, String name, int fileID, Context context, FileTypes type) {

		boolean withChecksum = true;
		String filePath;
		if (project == null || project.equalsIgnoreCase("")) {
			filePath = Constants.DEFAULT_ROOT + "/";
		} else {
			switch (type) {
				case IMAGE:
					filePath = Constants.DEFAULT_ROOT + "/" + project + "/" + sceneName + "/" + Constants.IMAGE_DIRECTORY + "/";
					break;
				case SOUND:
					filePath = Constants.DEFAULT_ROOT + "/" + project + "/" + sceneName + "/" + Constants.SOUND_DIRECTORY + "/";
					break;
				case SCREENSHOT:
					filePath = Constants.DEFAULT_ROOT + "/" + project + "/" + sceneName + "/";
					withChecksum = false;
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
			Log.e(TAG, "File handling error", e);
			return null;
		}
	}

	public static Project createProject(String projectName, ArrayList<Sprite> spriteList, Context context) {
		Project project = new Project(context, projectName);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		for (Sprite sprite : spriteList) {
			ProjectManager.getInstance().getCurrentScene().addSprite(sprite);
		}

		StorageHandler.getInstance().saveProject(project);
		return project;
	}

	public static void clearAllUtilTestProjects() {
		Log.v(TAG, "clearAllUtilTestProjects");
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

		directory = new File(Constants.DEFAULT_ROOT + "/" + "My first program");
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + "Mein erstes Programm");
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + "Project");
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + DEFAULT_TEST_PROJECT_NAME_MIXED_CASE);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + COPIED_PROJECT_NAME);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + JAPANESE_PROJECT_NAME);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}
	}
}
