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
/**
 * Copyright for original "String buildPath" held by:
 * 	Copyright (C) 2008 Rob Manning
 * 	manningr@users.sourceforge.net
 * Source: http://www.java2s.com/Code/Java/File-Input-Output/Autilityclassformanipulatingpaths.htm
 */
package at.tugraz.ist.catroid.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.common.Values;

public class Utils {

	private static final String TAG = Utils.class.getSimpleName();
	private static long uniqueLong = 0;
	private static Semaphore uniqueNameLock = new Semaphore(1);

	public static boolean hasSdCard() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * Checks whether the current device has an SD card. If it has none an error
	 * message is displayed and the calling activity is finished. A
	 * RuntimeException is thrown after the call to Activity.finish; find out
	 * why!
	 * 
	 * @param context
	 */
	public static boolean checkForSdCard(final Context context) {
		if (!hasSdCard()) {
			Builder builder = new AlertDialog.Builder(context);

			builder.setTitle(context.getString(R.string.error));
			builder.setMessage(context.getString(R.string.error_no_sd_card));
			builder.setNeutralButton(context.getString(R.string.close), new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// finish parent activity
					// parentActivity.finish();
					System.exit(0);
				}
			});
			builder.show();
			return false;
		}
		return true;
	}

	public static void updateScreenWidthAndHeight(Activity currentActivity) {
		DisplayMetrics dm = new DisplayMetrics();
		currentActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);

		Values.SCREEN_WIDTH = dm.widthPixels;
		Values.SCREEN_HEIGHT = dm.heightPixels;
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}

	/**
	 * Constructs a path out of the pathElements.
	 * 
	 * @param pathElements
	 *            the strings to connect. They can have "/" in them which will be de-duped in the result, if necessary.
	 * @return
	 *         the path that was constructed.
	 */
	static public String buildPath(String... pathElements) {
		StringBuilder result = new StringBuilder("/");

		for (String pathElement : pathElements) {
			result.append(pathElement).append("/");
		}

		String returnValue = result.toString().replaceAll("/+", "/");

		if (returnValue.endsWith("/")) {
			returnValue = returnValue.substring(0, returnValue.length() - 1);
		}

		return returnValue;
	}

	static public String buildProjectPath(String projectName) {
		return Consts.DEFAULT_ROOT + "/" + deleteSpecialCharactersInString(projectName);
	}

	/**
	 * @param projectFileName
	 * @return the project name without the default file extension, else returns unchanged string
	 */
	//	public static String getProjectName(String projectFileName) {
	//		if (projectFileName.endsWith(Consts.PROJECT_EXTENTION)) {
	//			return projectFileName.substring(0, projectFileName.length() - Consts.PROJECT_EXTENTION.length());
	//		}
	//		return projectFileName;
	//	}

	/**
	 * Displays an AlertDialog with the given error message and just a close
	 * button
	 * 
	 * @param context
	 * @param errorMessage
	 */
	public static void displayErrorMessage(Context context, String errorMessage) {
		Builder builder = new AlertDialog.Builder(context);

		builder.setTitle(context.getString(R.string.error));
		builder.setMessage(errorMessage);
		builder.setNeutralButton(context.getString(R.string.close), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.show();
	}

	public static void displayToast(Activity activity, String message/* , int duration */) {
		LayoutInflater inflater = activity.getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast_settings,
				(ViewGroup) activity.findViewById(R.id.toast_layout_root));

		TextView text = (TextView) layout.findViewById(R.id.text);
		text.setText(message);

		Toast toast = new Toast(activity.getApplicationContext());
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
	}

	public static String md5Checksum(File file) {
		if (!file.isFile()) {
			return null;
		}

		MessageDigest messageDigest = getMD5MessageDigest();

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			byte[] buffer = new byte[Consts.BUFFER_8K];

			int length = 0;

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

		return toHex(messageDigest.digest());
	}

	public static String md5Checksum(String string) {
		MessageDigest messageDigest = getMD5MessageDigest();

		messageDigest.update(string.getBytes());

		return toHex(messageDigest.digest());
	}

	public static String getUniqueName() {
		uniqueNameLock.acquireUninterruptibly();
		String uniqueName = String.valueOf(uniqueLong++);
		uniqueNameLock.release();
		return uniqueName;
	}

	private static String toHex(byte[] messageDigest) {
		StringBuilder md5StringBuilder = new StringBuilder(2 * messageDigest.length);

		for (byte b : messageDigest) {
			md5StringBuilder.append("0123456789ABCDEF".charAt((b & 0xF0) >> 4));
			md5StringBuilder.append("0123456789ABCDEF".charAt((b & 0x0F)));
		}

		return md5StringBuilder.toString();
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

	public static int getVersionCode(Context context) {
		int versionCode = -1;
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),
					PackageManager.GET_META_DATA);
			versionCode = packageInfo.versionCode;
		} catch (NameNotFoundException nameNotFoundException) {
			Log.e(TAG, "Name not found", nameNotFoundException);
		}
		return versionCode;
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

	public static OnShowListener getBrickDialogOnClickListener(final Context context, final EditText input) {
		return new OnShowListener() {
			public void onShow(DialogInterface dialog) {
				InputMethodManager inputManager = (InputMethodManager) context
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
			}
		};
	}

	public static int getPhysicalPixels(int densityIndependentPixels, Context context) {
		final float scale = context.getResources().getDisplayMetrics().density;
		int physicalPixels = (int) (densityIndependentPixels * scale + 0.5f);
		return physicalPixels;
	}

	public static void saveToPreferences(Context context, String key, String message) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor edit = prefs.edit();
		edit.putString(key, message);
		edit.commit();
	}

	public static void loadProjectIfNeeded(Context context) {
		if (ProjectManager.getInstance().getCurrentProject() == null) {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			String projectName = prefs.getString(Consts.PREF_PROJECTNAME_KEY, null);

			if (projectName != null) {
				ProjectManager.getInstance().loadProject(projectName, context, false);
			} else {
				ProjectManager.getInstance().initializeDefaultProject(context);
			}
		}
	}

	public static String deleteSpecialCharactersInString(String stringToAdapt) {
		return stringToAdapt.replaceAll("[\"*/:<>?\\\\|]", "");
	}

	public static String getUniqueCostumeName(String name) {
		return searchForNonExistingCostumeName(name, 0);
	}

	private static String searchForNonExistingCostumeName(String name, int nextNumber) {
		String newName;
		ArrayList<CostumeData> costumeDataList = ProjectManager.getInstance().getCurrentSprite().getCostumeDataList();
		if (nextNumber == 0) {
			newName = name;
		} else {
			newName = name + nextNumber;
		}
		for (CostumeData costumeData : costumeDataList) {
			if (costumeData.getCostumeName().equals(newName)) {
				return searchForNonExistingCostumeName(name, ++nextNumber);
			}
		}
		return newName;
	}

	public static String getUniqueSoundName(String title) {
		return searchForNonExistingSoundTitle(title, 0);
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
}
