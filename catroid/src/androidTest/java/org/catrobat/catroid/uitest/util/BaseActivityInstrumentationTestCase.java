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
package org.catrobat.catroid.uitest.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.robotium.solo.Solo;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.UtilZip;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public abstract class BaseActivityInstrumentationTestCase<T extends Activity> extends
		ActivityInstrumentationTestCase2<T> {
	protected Solo solo;

	private static final String TAG = "BaseActivityTestCase";
	private Class clazz;
	private SystemAnimations systemAnimations;
	private static final String ZIPFILE_NAME = "testzip";

	private boolean createSoloInSetUp;

	public BaseActivityInstrumentationTestCase(Class<T> clazz) {
		super(clazz);
		this.clazz = clazz;

		createSoloInSetUp = true;
	}

	/**
	 * Methods like setActivityIntent(Intent) in child classes supposes that
	 * setUp() of ActivityInstrumentationTestCase2 was called. But also it must be
	 * called after getActivity() of ActivityInstrumentationTestCase2 otherwise
	 * it has no effect. So a flag is needed to initialize solo later in child class
	 * to use setActivityIntent.
	 */
	public BaseActivityInstrumentationTestCase(Class<T> clazz, boolean createSoloInSetUp) {
		super(clazz);
		this.clazz = clazz;
		this.createSoloInSetUp = createSoloInSetUp;
	}

	private boolean unzip;

	@Override
	protected void setUp() throws Exception {
		if (createSoloInSetUp) {
			solo = new Solo(getInstrumentation(), getActivity());
		}
		Log.v(TAG, "setUp");

		systemAnimations = new SystemAnimations(getInstrumentation().getTargetContext());
		systemAnimations.disableAll();

		unzip = false;
		saveProjectsToZip();

		if (clazz.getSimpleName().equalsIgnoreCase(MainMenuActivity.class.getSimpleName())) {
			UiTestUtils.createEmptyProject();
		}
		Reflection.setPrivateField(StageListener.class, "checkIfAutomaticScreenshotShouldBeTaken", false);

		if (solo != null) {
			solo.unlockScreen();
		}
		super.setUp();
		Log.v(TAG, "setUp end");
	}

	@Override
	protected void tearDown() throws Exception {

		Log.v(TAG, "tearDown");
		Log.v(TAG, "remove Projectname from SharedPreferences");
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getInstrumentation().getTargetContext());
		SharedPreferences.Editor edit = preferences.edit();
		edit.remove(Constants.PREF_PROJECTNAME_KEY);
		edit.commit();

		if (solo != null) {
			solo.finishOpenedActivities();
		}

		systemAnimations.enableAll();
		solo = null;
		Log.i(TAG, "tearDown end 1");

		unzipProjects();

		Log.i(TAG, "tearDown end 2");
		super.tearDown();

		Log.i(TAG, "tearDown end 3");
	}

	@Override
	public void runBare() throws Throwable {
		try {
			setUp();
			runTest();
		} finally {
			tearDown();
		}
	}

	public void saveProjectsToZip() {
		File rootDirectory = new File(Constants.DEFAULT_ROOT);

		rootDirectory.mkdirs();

		String[] paths = rootDirectory.list(new FilenameFilter() {
			@Override
			public boolean accept(File file, String s) {
				return !s.equals(ZIPFILE_NAME);
			}
		});

		if (paths == null) {
			fail("could not determine catroid directory");
		} else if (paths.length > 0) {
			for (int i = 0; i < paths.length; i++) {
				paths[i] = Utils.buildPath(rootDirectory.getAbsolutePath(), paths[i]);
			}
			try {
				String zipFileString = Utils.buildPath(Constants.DEFAULT_ROOT, ZIPFILE_NAME);
				Log.d(TAG, "i am the zipfile: " + zipFileString);
				File zipFile = new File(zipFileString);
				if (zipFile.exists()) {
					zipFile.delete();
				}
				zipFile.getParentFile().mkdirs();
				zipFile.createNewFile();
				if (!UtilZip.writeToZipFile(paths, zipFileString)) {
					zipFile.delete();
					throw new IOException("Cannot write data to Zip File!");
				}

				for (String projectName : UtilFile.getProjectNames(rootDirectory)) {
					Log.d(TAG, projectName + "will be deleted");
					ProjectManager.getInstance().deleteProject(projectName, this.getInstrumentation().getTargetContext());
				}

				for (int i = 0; i < paths.length; i++) {
					Log.d(TAG, "Path to delete: " + paths[i]);
					StorageHandler.getInstance().deleteAllFile(paths[i]);
				}
				unzip = true;
			} catch (IOException e) {
				Log.d(TAG, "Zipping failed!", e);
				fail("IOException while zipping projects");
			}
		}
	}

	public void unzipProjects() {

		try {

			File rootDirectory = new File(Constants.DEFAULT_ROOT);

			for (String projectName : UtilFile.getProjectNames(rootDirectory)) {
				Log.d(TAG, projectName + "will be deleted");
				ProjectManager.getInstance().deleteProject(projectName, this.getInstrumentation().getTargetContext());
			}

			String[] paths = rootDirectory.list();
			for (int i = 0; i < paths.length; i++) {
				paths[i] = Utils.buildPath(rootDirectory.getAbsolutePath(), paths[i]);
			}

			String zipFileString = Utils.buildPath(Constants.DEFAULT_ROOT, ZIPFILE_NAME);

			for (int i = 0; i < paths.length; i++) {
				if (!paths[i].equals(zipFileString)) {
					Log.d(TAG, "Path to delete: " + paths[i]);
					StorageHandler.getInstance().deleteAllFile(paths[i]);
				}
			}

			if (unzip) {
				Log.d(TAG, "i am the unzipfile: " + zipFileString);
				File zipFile = new File(zipFileString);
				UtilZip.unZipFile(zipFileString, Constants.DEFAULT_ROOT);
				zipFile.delete();
			}
		} catch (IOException e) {
			Log.d(TAG, "Something wet wrong while unzip files in tear down", e);
		}
	}
}
