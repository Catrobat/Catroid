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
package at.tugraz.ist.catroid.test.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.Context;
import android.util.Log;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.common.FileChecksumContainer;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.utils.UtilFile;

public class TestUtils {

	private static final String TAG = TestUtils.class.getSimpleName();
	public static final int TYPE_IMAGE_FILE = 0;
	public static final int TYPE_SOUND_FILE = 1;
	public static final String DEFAULT_TEST_PROJECT_NAME = "testProject";

	private TestUtils() {
	};

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
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return contents.toString();
	}

	public static Object getPrivateField(String fieldName, Object object, boolean ofSuperclass) {

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
				return field.get(object);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return field;
	}

	public static void setPrivateField(Class<?> classFromObject, Object object, String fieldName, Object value)
			throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field field = classFromObject.getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(object, value);
	}

	public static Object invokeMethod(Object classObject, String methodName, Class<?>[] methodParams,
			Object[] methodArgs) {
		try {
			Class<?> currentClass = classObject.getClass();
			Method currentMethod = currentClass.getDeclaredMethod(methodName, methodParams);
			currentMethod.setAccessible(true);
			Object returnObject = currentMethod.invoke(classObject, methodArgs);
			return returnObject;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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

	public static void createTestProjectOnLocalStorageWithVersionCodeAndName(int versionCode, String name) {
		Project project = new ProjectWithVersionCode(name, versionCode);
		Sprite firstSprite = new Sprite("cat");
		Script testScript = new StartScript(firstSprite);

		firstSprite.addScript(testScript);
		project.addSprite(firstSprite);

		StorageHandler.getInstance().saveProject(project);
	}

	public static void createTestProjectOnLocalStorageWithVersionCode(int versionCode) {
		createTestProjectOnLocalStorageWithVersionCodeAndName(versionCode, DEFAULT_TEST_PROJECT_NAME);
	}

	public static void deleteTestProjects(String... additionalProjectNames) {
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
}
