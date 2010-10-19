package at.tugraz.ist.catroid.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.utils.filesystem.FileCopyThread;

public class Utils {
	private static boolean hasSdCard() {
		return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}

	/**
	 * Checks whether the current device has an SD card. If it has none an error
	 * message is displayed and the calling activity is finished. TODO: A
	 * RuntimeException is thrown after the call to Activity.finish; find out
	 * why!
	 * 
	 * @param parentActivity
	 *            the activity that calls this method
	 */
	public static boolean checkForSdCard(final Activity parentActivity) {
		if (!hasSdCard()) {
			Builder builder = new AlertDialog.Builder(parentActivity);

			builder.setTitle(parentActivity.getString(R.string.error));
			builder.setMessage(parentActivity.getString(R.string.error_no_sd_card));
			builder.setNeutralButton(parentActivity.getString(R.string.close), new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// finish parent activity
					parentActivity.finish();
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

		if (fileTo.exists())
			deleteFile(fileTo.getAbsolutePath());
		try {
			fileTo.createNewFile();
		} catch (IOException e1) {
			return false;
		}

		if (!fileFrom.exists() || !fileTo.exists())
			return false;

		ProgressDialog progressDialog = null;
		if (showProgressDialog && context != null) {
			progressDialog = ProgressDialog.show(context, context.getString(R.string.please_wait), context.getString(R.string.loading));
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
	 * @param projectName project name to check for existence
	 * @return whether the project exists
	 */
	public static boolean projectExists(String projectName) {
		File projectFolder = new File(concatPaths(ConstructionSiteActivity.DEFAULT_ROOT, projectName));
		return projectFolder.exists();
	}

	public static boolean deleteFolder(String path) {
		File fileFrom = new File(path);
		if (fileFrom.isDirectory()) {
			for (File c : fileFrom.listFiles()) {
				if (c.isDirectory())
					deleteFolder(c.getAbsolutePath());
				else
					c.delete();
			}
		} else
			fileFrom.delete();

		return true;
	}

	public static boolean deleteFolder(String path, String ignoreFile) {
		File fileFrom = new File(path);
		if (fileFrom.isDirectory()) {
			for (File c : fileFrom.listFiles()) {
				if (c.isDirectory())
					deleteFolder(c.getAbsolutePath(), ignoreFile);
				else {
					if (!c.getName().equals(ignoreFile))
						c.delete();
				}

			}
		} else {
			if (!fileFrom.getName().equals(ignoreFile))
				fileFrom.delete();
		}

		return true;
	}

	public static String concatPaths(String first, String second) {
		if (first == null && second == null)
			return null;
		if (first == null)
			return second;
		if (second == null)
			return first;
		if (first.endsWith("/"))
			if (second.startsWith("/"))
				return first + second.replaceFirst("/", "");
			else
				return first + second;
		else if (second.startsWith("/"))
			return first + second;
		else
			return first + "/" + second;
	}

	public static String addDefaultFileEnding(String filename) {
		if (!filename.endsWith(ConstructionSiteActivity.DEFAULT_FILE_ENDING))
			return filename + ConstructionSiteActivity.DEFAULT_FILE_ENDING;
		return filename;
	}

	public static void saveBitmapOnSDCardAsPNG(String full_path, Bitmap bitmap) {
		File file = new File(full_path);
		try {
			FileOutputStream os = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
			os.close();
		} catch (FileNotFoundException e) {
			Log.e("UTILS", e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("UTILS", e.getMessage());
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
	 * @param context
	 * @param uri
	 */
	public static void displayWebsite(Context context, Uri uri) {
		Intent websiteIntent = new Intent(Intent.ACTION_VIEW);
		websiteIntent.setData(uri);
		context.startActivity(websiteIntent);
	}
}
