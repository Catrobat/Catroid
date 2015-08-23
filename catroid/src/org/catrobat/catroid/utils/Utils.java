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
package org.catrobat.catroid.utils;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.badlogic.gdx.utils.GdxRuntimeException;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.common.StandardProjectHandler;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.exceptions.ProjectException;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class Utils {

	private static final String TAG = Utils.class.getSimpleName();

	public static final int TRANSLATION_PLURAL_OTHER_INTEGER = 767676;

	// Suppress default constructor for noninstantiability
	private Utils() {
		throw new AssertionError();
	}

	public static boolean externalStorageAvailable() {
		String externalStorageState = Environment.getExternalStorageState();
		return externalStorageState.equals(Environment.MEDIA_MOUNTED)
				&& !externalStorageState.equals(Environment.MEDIA_MOUNTED_READ_ONLY);
	}

	public static boolean checkForExternalStorageAvailableAndDisplayErrorIfNot(final Context context) {
		if (!externalStorageAvailable()) {
			Builder builder = new CustomAlertDialogBuilder(context);

			builder.setTitle(R.string.error);
			builder.setMessage(R.string.error_no_writiable_external_storage_available);
			builder.setNeutralButton(R.string.close, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					((Activity) context).moveTaskToBack(true);
				}
			});
			builder.show();
			return false;
		}
		return true;
	}

	public static void updateScreenWidthAndHeight(Context context) {
		if (context != null) {
			WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			DisplayMetrics displayMetrics = new DisplayMetrics();
			windowManager.getDefaultDisplay().getMetrics(displayMetrics);
			ScreenValues.SCREEN_WIDTH = displayMetrics.widthPixels;
			ScreenValues.SCREEN_HEIGHT = displayMetrics.heightPixels;
		} else {
			//a null-context should never be passed. However, an educated guess is needed in that case.
			ScreenValues.setToDefaultSreenSize();
		}
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	/**
	 * Constructs a path out of the pathElements.
	 *
	 * @param pathElements the strings to connect. They can have "/" in them which will be de-duped in the result, if necessary.
	 * @return the path that was constructed.
	 */
	public static String buildPath(String... pathElements) {
		StringBuilder result = new StringBuilder("/");

		for (String pathElement : pathElements) {
			result.append(pathElement).append('/');
		}

		String returnValue = result.toString().replaceAll("/+", "/");

		if (returnValue.endsWith("/")) {
			returnValue = returnValue.substring(0, returnValue.length() - 1);
		}

		return returnValue;
	}

	public static String buildProjectPath(String projectName) {
		return buildPath(Constants.DEFAULT_ROOT, UtilFile.encodeSpecialCharsForFileSystem(projectName));
	}

	public static void showErrorDialog(Context context, int errorMessageId) {
		Builder builder = new CustomAlertDialogBuilder(context);
		builder.setTitle(R.string.error);
		builder.setMessage(errorMessageId);
		builder.setNeutralButton(R.string.close, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		Dialog errorDialog = builder.create();
		errorDialog.show();
	}

	public static void showErrorDialog(Context context, String msg, int errorTitleId) {
		Builder builder = new CustomAlertDialogBuilder(context);
		builder.setTitle(errorTitleId);
		builder.setMessage(msg);
		builder.setNeutralButton(R.string.close, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		Dialog errorDialog = builder.create();
		errorDialog.show();
	}

	public static void showErrorDialog(Context context, int errorTitleId, int errorMessageId) {
		Builder builder = new CustomAlertDialogBuilder(context);
		builder.setTitle(errorTitleId);
		builder.setMessage(errorMessageId);
		builder.setNeutralButton(R.string.close, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		Dialog errorDialog = builder.create();
		errorDialog.show();
	}

	public static View addSelectAllActionModeButton(LayoutInflater inflater, ActionMode mode, Menu menu) {
		mode.getMenuInflater().inflate(R.menu.menu_actionmode, menu);
		MenuItem item = menu.findItem(R.id.select_all);
		View view = item.getActionView();
		if (view.getId() == R.id.select_all) {
			View selectAllView = inflater.inflate(R.layout.action_mode_select_all, null);
			item.setActionView(selectAllView);
			return selectAllView;
		}
		return null;
	}

	public static String md5Checksum(File file) {

		if (!file.isFile()) {
			Log.e(TAG, String.format("md5Checksum() Error with file %s isFile: %s isDirectory: %s exists: %s",
					file.getName(),
					Boolean.valueOf(file.isFile()),
					Boolean.valueOf(file.isDirectory()),
					Boolean.valueOf(file.exists())));
			return null;
		}

		MessageDigest messageDigest = getMD5MessageDigest();

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			byte[] buffer = new byte[Constants.BUFFER_8K];

			int length;
			while ((length = fis.read(buffer)) != -1) {
				messageDigest.update(buffer, 0, length);
			}
		} catch (IOException e) {
			Log.w(TAG, "IOException thrown in md5Checksum()");
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				Log.w(TAG, "IOException thrown in finally block of md5Checksum()");
			}
		}

		return toHex(messageDigest.digest()).toLowerCase(Locale.US);
	}

	public static String md5Checksum(String string) {
		MessageDigest messageDigest = getMD5MessageDigest();

		messageDigest.update(string.getBytes());

		return toHex(messageDigest.digest()).toLowerCase(Locale.US);
	}

	private static String toHex(byte[] messageDigest) {
		final char[] hexChars = "0123456789ABCDEF".toCharArray();

		char[] hexBuffer = new char[messageDigest.length * 2];
		int j = 0;
		for (byte c : messageDigest) {
			hexBuffer[j++] = hexChars[(c & 0xF0) >> 4];
			hexBuffer[j++] = hexChars[c & 0x0F];
		}

		return String.valueOf(hexBuffer);
	}

	private static MessageDigest getMD5MessageDigest() {
		MessageDigest messageDigest = null;

		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			Log.w(TAG, "NoSuchAlgorithmException thrown in getMD5MessageDigest()");
		}

		return messageDigest;
	}

	public static String getVersionName(Context context) {
		String versionName = "unknown";
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),
					PackageManager.GET_META_DATA);
			versionName = packageInfo.versionName;
		} catch (NameNotFoundException nameNotFoundException) {
			Log.e(TAG, "Name not found", nameNotFoundException);
		}
		return versionName;
	}

	public static int getPhysicalPixels(int densityIndependentPixels, Context context) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (densityIndependentPixels * scale + 0.5f);
	}

	public static void saveToPreferences(Context context, String key, String message) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor edit = sharedPreferences.edit();
		edit.putString(key, message);
		edit.commit();
	}

	public static void removeFromPreferences(Context context, String key) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = preferences.edit();
		edit.remove(key);
		edit.commit();
	}

	public static void loadProjectIfNeeded(Context context) {
		if (ProjectManager.getInstance().getCurrentProject() == null) {
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
			String projectName = sharedPreferences.getString(Constants.PREF_PROJECTNAME_KEY, null);

			if (projectName == null || !StorageHandler.getInstance().projectExists(projectName)) {
				projectName = context.getString(R.string.default_project_name);
			}

			try {
				ProjectManager.getInstance().loadProject(projectName, context);
			} catch (ProjectException projectException) {
				Log.e(TAG, "Project cannot load", projectException);
				ProjectManager.getInstance().initializeDefaultProject(context);
			}
		}
	}

	public static String getCurrentProjectName(Context context) {
		if (ProjectManager.getInstance().getCurrentProject() == null) {

			if (UtilFile.getProjectNames(new File(Constants.DEFAULT_ROOT)).size() == 0) {
				Log.i("Utils", "Somebody deleted all projects in the file-system");
				ProjectManager.getInstance().initializeDefaultProject(context);
			}

			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
			String currentProjectName = sharedPreferences.getString(Constants.PREF_PROJECTNAME_KEY, null);
			if (currentProjectName == null || !StorageHandler.getInstance().projectExists(currentProjectName)) {
				currentProjectName = UtilFile.getProjectNames(new File(Constants.DEFAULT_ROOT)).get(0);
			}
			return currentProjectName;
		}
		return ProjectManager.getInstance().getCurrentProject().getName();
	}

	public static String deleteSpecialCharactersInString(String stringToAdapt) {
		return stringToAdapt.replaceAll("[\"*/:<>?\\\\|]", "");
	}

	public static String getUniqueObjectName(String name) {
		return searchForNonExistingObjectNameInCurrentProgram(name, 0);
	}

	private static String searchForNonExistingObjectNameInCurrentProgram(String name, int nextNumber) {
		String newName;

		if (nextNumber == 0) {
			newName = name;
		} else {
			newName = name + nextNumber;
		}

		if (ProjectManager.getInstance().spriteExists(newName)) {
			return searchForNonExistingObjectNameInCurrentProgram(name, ++nextNumber);
		}

		return newName;
	}

	public static String getUniqueLookName(String name) {
		return searchForNonExistingLookName(name, 0);
	}

	private static String searchForNonExistingLookName(String name, int nextNumber) {
		String newName;
		ArrayList<LookData> lookDataList = ProjectManager.getInstance().getCurrentSprite().getLookDataList();
		if (nextNumber == 0) {
			newName = name;
		} else {
			newName = name + nextNumber;
		}
		for (LookData lookData : lookDataList) {
			if (lookData.getLookName().equals(newName)) {
				return searchForNonExistingLookName(name, ++nextNumber);
			}
		}
		return newName;
	}

	public static String getUniqueSoundName(String title) {
		return searchForNonExistingSoundTitle(title, 0);
	}

	public static Project findValidProject() {
		Project loadableProject = null;

		List<String> projectNameList = UtilFile.getProjectNames(new File(Constants.DEFAULT_ROOT));
		for (String projectName : projectNameList) {
			loadableProject = StorageHandler.getInstance().loadProject(projectName);
			if (loadableProject != null) {
				break;
			}
		}
		return loadableProject;
	}

	private static String searchForNonExistingSoundTitle(String title, int nextNumber) {
		// search for sounds with the same title
		String newTitle;
		ArrayList<SoundInfo> soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		if (nextNumber == 0) {
			newTitle = title;
		} else {
			newTitle = title + nextNumber;
		}
		for (SoundInfo soundInfo : soundInfoList) {
			if (soundInfo.getTitle().equals(newTitle)) {
				return searchForNonExistingSoundTitle(title, ++nextNumber);
			}
		}
		return newTitle;
	}

	public static Pixmap getPixmapFromFile(File imageFile) {
		Pixmap pixmap;
		try {
			GdxNativesLoader.load();
			pixmap = new Pixmap(new FileHandle(imageFile));
		} catch (GdxRuntimeException e) {
			return null;
		} catch (Exception e1) {
			return null;
		}
		return pixmap;
	}

	public static void rewriteImageFileForStage(Context context, File lookFile) throws IOException {
		// if pixmap cannot be created, image would throw an Exception in stage
		// so has to be loaded again with other Config
		Pixmap pixmap;
		pixmap = Utils.getPixmapFromFile(lookFile);

		if (pixmap == null) {
			ImageEditing.overwriteImageFileWithNewBitmap(lookFile);
			pixmap = Utils.getPixmapFromFile(lookFile);

			if (pixmap == null) {
				Utils.showErrorDialog(context, R.string.error_load_image);
				StorageHandler.getInstance().deleteFile(lookFile.getAbsolutePath());
				Log.d("testitest", "path: " + lookFile.getAbsolutePath());
				throw new IOException("Pixmap could not be fixed");
			}
		}
	}

	public static String getUniqueProjectName() {
		String projectName = "project_" + System.currentTimeMillis();
		while (StorageHandler.getInstance().projectExists(projectName)) {
			projectName = "project_" + System.currentTimeMillis();
		}
		return projectName;
	}

	public static boolean isStandardProject(Project projectToCheck, Context context) {
		try {
			Project standardProject = StandardProjectHandler.createAndSaveStandardProject(getUniqueProjectName(),
					context);
			String standardProjectXMLString = StorageHandler.getInstance().getXMLStringOfAProject(standardProject);
			int start = standardProjectXMLString.indexOf("<objectList>");
			int end = standardProjectXMLString.indexOf("</objectList>");
			String standardProjectSpriteList = standardProjectXMLString.substring(start, end);
			ProjectManager.getInstance().deleteCurrentProject(null);

			ProjectManager.getInstance().setProject(projectToCheck);
			ProjectManager.getInstance().saveProject(context);

			String projectToCheckXMLString = StorageHandler.getInstance().getXMLStringOfAProject(projectToCheck);
			start = projectToCheckXMLString.indexOf("<objectList>");
			end = projectToCheckXMLString.indexOf("</objectList>");
			String projectToCheckStringList = projectToCheckXMLString.substring(start, end);

			return standardProjectSpriteList.contentEquals(projectToCheckStringList);
		} catch (IllegalArgumentException illegalArgumentException) {
			Log.e(TAG, Log.getStackTraceString(illegalArgumentException));
		} catch (IOException ioException) {
			Log.e(TAG, Log.getStackTraceString(ioException));
		}
		return true;
	}

	public static int convertDoubleToPluralInteger(double value) {
		double absoluteValue = Math.abs(value);
		if (absoluteValue > 2.5) {
			return (int) Math.round(absoluteValue);
		} else {
			if (absoluteValue == 0.0 || absoluteValue == 1.0 || absoluteValue == 2.0) {
				return (int) absoluteValue;
			} else {
				// Random Number to get into the "other" keyword for values like 0.99 or 2.001 seconds or degrees
				// in hopefully all possible languages
				return TRANSLATION_PLURAL_OTHER_INTEGER;
			}
		}
	}

	public static boolean checkIfProjectExistsOrIsDownloadingIgnoreCase(String programName) {
		if (DownloadUtil.getInstance().isProgramNameInDownloadQueueIgnoreCase(programName)) {
			return true;
		}

		File projectDirectory = new File(Utils.buildProjectPath(programName));
		return projectDirectory.exists();
	}

	public static boolean checkIfLookExists(String name) {
		for (LookData lookData : ProjectManager.getInstance().getCurrentSprite().getLookDataList()) {
			if (lookData.getLookName().compareTo(name) == 0) {
				return true;
			}
		}
		return false;
	}

	public static boolean checkIfSoundExists(String name) {
		for (SoundInfo soundInfo : ProjectManager.getInstance().getCurrentSprite().getSoundList()) {
			if (soundInfo.getTitle().compareTo(name) == 0) {
				return true;
			}
		}
		return false;
	}

	public static void setSelectAllActionModeButtonVisibility(View selectAllActionModeButton, boolean setVisible) {
		if (selectAllActionModeButton == null) {
			return;
		}

		if (setVisible) {
			selectAllActionModeButton.setVisibility(View.VISIBLE);
		} else {
			selectAllActionModeButton.setVisibility(View.GONE);
		}
	}
}
