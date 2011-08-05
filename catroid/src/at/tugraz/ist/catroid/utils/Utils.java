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

package at.tugraz.ist.catroid.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.Values;

public class Utils {

	private static final String TAG = "Utils";

	public static boolean hasSdCard() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	public static void updateScreenWidthAndHeight(Activity currentActivity) {
		DisplayMetrics dm = new DisplayMetrics();
		currentActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);

		Values.SCREEN_WIDTH = dm.widthPixels;
		Values.SCREEN_HEIGHT = dm.heightPixels;
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

	/**
	 * Copies a file from the source to the destination. Can optionally show a
	 * progress dialog until copying is finished.
	 * 
	 * @param from
	 *            path to the source file
	 * @param to
	 *            path to the destination file
	 * @param context
	 *            the Context, can be null if no progress dialog is wanted
	 * @param showProgressDialog
	 *            whether or not to display a progress dialog until copying is
	 *            finished
	 * @return whether the file was copied successfully
	 */
	public static boolean copyFile(String from, String to, Context context, boolean showProgressDialog) {
		File fileFrom = new File(from);
		File fileTo = new File(to);

		if (fileTo.exists()) {
			deleteFile(fileTo.getAbsolutePath());
		}
		try {
			fileTo.createNewFile();
		} catch (IOException e1) {
			return false;
		}

		if (!fileFrom.exists() || !fileTo.exists()) {
			return false;
		}

		ProgressDialog progressDialog = null;
		if (showProgressDialog && context != null) {
			progressDialog = ProgressDialog.show(context, context.getString(R.string.please_wait),
					context.getString(R.string.loading));
		}

		Thread t = new FileCopyThread(fileTo, fileFrom, progressDialog);
		t.start();
		return true;
	}

	public static boolean deleteFile(String path) {
		File fileFrom = new File(path);
		return fileFrom.delete();
	}

	/**
	 * Returns whether a project with the given name already exists
	 * 
	 * @param projectName
	 *            project name to check for existence
	 * @return whether the project exists
	 */
	public static boolean projectExists(String projectName) {
		File projectFolder = new File(concatPaths(Consts.DEFAULT_ROOT, projectName));
		return projectFolder.exists();
	}

	public static String getTimestamp() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		return simpleDateFormat.format(new Date());
	}

	public static boolean deleteFolder(String path) {
		File fileFrom = new File(path);
		if (fileFrom.isDirectory()) {
			for (File file : fileFrom.listFiles()) {
				if (file.isDirectory()) {
					deleteFolder(file.getAbsolutePath());
				} else {
					file.delete();
				}
			}
		} else {
			fileFrom.delete();
		}

		return true;
	}

	public static boolean deleteFolder(String path, String ignoreFile) {
		File fileFrom = new File(path);
		if (fileFrom.isDirectory()) {
			for (File file : fileFrom.listFiles()) {
				if (file.isDirectory()) {
					deleteFolder(file.getAbsolutePath(), ignoreFile);
				} else {
					if (!file.getName().equals(ignoreFile)) {
						file.delete();
					}
				}

			}
		} else {
			if (!fileFrom.getName().equals(ignoreFile)) {
				fileFrom.delete();
			}
		}

		return true;
	}

	public static String concatPaths(String first, String second) {
		if (first == null && second == null) {
			return null;
		}
		if (first == null) {
			return second;
		}
		if (second == null) {
			return first;
		}
		if (first.endsWith("/")) {
			if (second.startsWith("/")) {
				return first + second.replaceFirst("/", "");
			} else {
				return first + second;
			}
		} else if (second.startsWith("/")) {
			return first + second;
		} else {
			return first + "/" + second;
		}
	}

	public static String addDefaultFileEnding(String filename) {
		if (!filename.endsWith(Consts.PROJECT_EXTENTION)) {
			return filename + Consts.PROJECT_EXTENTION;
		}
		return filename;
	}

	/**
	 * 
	 * @param projectFileName
	 * @return the project name without the default file extension, else returns unchanged string
	 */
	public static String getProjectName(String projectFileName) {
		if (projectFileName.endsWith(Consts.PROJECT_EXTENTION)) {
			return projectFileName.substring(0, projectFileName.length() - Consts.PROJECT_EXTENTION.length());
		}
		return projectFileName;
	}

	public static void saveBitmapOnSDCardAsPNG(String full_path, Bitmap bitmap) {
		File file = new File(full_path);
		try {
			FileOutputStream os = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
			os.close();
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
	}

	public static String changeFileEndingToPng(String filename) {
		String newFileName;

		int beginOfFileEnding = filename.lastIndexOf(".");
		newFileName = filename.replace(filename.substring(beginOfFileEnding), "");

		newFileName = newFileName + ".png";
		return newFileName;
	}

	/**
	 * Displays a website with the given URI using an Intent
	 * 
	 * @param context
	 * @param uri
	 */
	public static void displayWebsite(Context context, Uri uri) {
		Intent websiteIntent = new Intent(Intent.ACTION_VIEW);
		websiteIntent.setData(uri);
		context.startActivity(websiteIntent);
	}

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

	private static String toHex(byte[] messageDigest) {
		StringBuilder md5StringBuilder = new StringBuilder(2 * messageDigest.length);

		for (byte b : messageDigest) {
			md5StringBuilder.append("0123456789ABCDEF".charAt((b & 0xF0) >> 4));
			md5StringBuilder.append("0123456789ABCDEF".charAt((b & 0x0F)));
		}

		Log.v(TAG, md5StringBuilder.toString());

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
}
