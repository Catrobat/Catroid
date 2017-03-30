/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.facebook.AccessToken;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.DefaultProjectHandler;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.NfcTagData;
import org.catrobat.catroid.common.ScratchProgramData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.Translatable;
import org.catrobat.catroid.content.XmlHeader;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.exceptions.ProjectException;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.transfers.LogoutTask;
import org.catrobat.catroid.ui.BaseExceptionHandler;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.ui.WebViewActivity;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.web.ServerCalls;
import org.catrobat.catroid.web.WebconnectionException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class Utils {

	private static final String TAG = Utils.class.getSimpleName();

	private enum RemixUrlParsingState {
		STARTING, TOKEN, BETWEEN
	}

	private static final CharMatcher MATCHER =
			CharMatcher.javaLetterOrDigit()
					.or(CharMatcher.is('.'))
					.or(CharMatcher.is('_'))
					.or(CharMatcher.whitespace())
					.precomputed();

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

	public static boolean checkIfCrashRecoveryAndFinishActivity(final Activity context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		if (preferences.getBoolean(BaseExceptionHandler.RECOVERED_FROM_CRASH, false)) {
			if (BuildConfig.FIREBASE_CRASH_REPORT_ENABLED
					&& preferences.getBoolean(SettingsActivity.SETTINGS_CRASH_REPORTS, false)) {
				sendCaughtException(context);
			}

			if (!(context instanceof MainMenuActivity)) {
				context.finish();
			} else {
				preferences.edit().putBoolean(BaseExceptionHandler.RECOVERED_FROM_CRASH, false).commit();
				return true;
			}
		}
		return false;
	}

	public static void sendCaughtException(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		Gson gson = new Gson();
		String json = preferences.getString(BaseExceptionHandler.EXCEPTION_FOR_REPORT, "");
		Throwable exception = gson.fromJson(json, Throwable.class);

		FirebaseCrash.report(exception);

		preferences.edit().remove(BaseExceptionHandler.EXCEPTION_FOR_REPORT).commit();
	}

	public static boolean isNetworkAvailable(Context context, boolean createDialog) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		boolean isAvailable = activeNetworkInfo != null && activeNetworkInfo.isConnected();
		if (!isAvailable && createDialog) {
			new CustomAlertDialogBuilder(context).setTitle(R.string.no_internet)
					.setMessage(R.string.error_no_internet).setPositiveButton(R.string.ok, null)
					.show();
		}

		return isAvailable;
	}

	public static boolean isNetworkAvailable(Context context) {
		return isNetworkAvailable(context, false);
	}

	public static boolean checkForNetworkError(boolean success, WebconnectionException exception) {
		return !success && exception != null && exception.getStatusCode() == WebconnectionException
				.ERROR_NETWORK;
	}

	public static boolean checkForSignInError(boolean success, WebconnectionException exception, Context context,
			boolean userSignedIn) {
		return (!success && exception != null) || context == null || (!BuildConfig.CREATE_AT_SCHOOL && !userSignedIn);
	}

	public static boolean checkForNetworkError(WebconnectionException exception) {
		return exception != null && exception.getStatusCode() == WebconnectionException.ERROR_NETWORK;
	}

	public static String formatDate(Date date, Locale locale) {
		return DateFormat.getDateInstance(DateFormat.LONG, locale).format(date);
	}

	@Nullable
	public static byte[] convertInputStreamToByteArray(final InputStream inputStream) {
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[4096];
			int len;
			while ((len = inputStream.read(buffer)) > -1) {
				byteArrayOutputStream.write(buffer, 0, len);
			}
			byteArrayOutputStream.flush();
			return byteArrayOutputStream.toByteArray();
		} catch (IOException e) {
			return null;
		}
	}

	public static String generateRemixUrlsStringForMergedProgram(XmlHeader headerOfFirstProgram, XmlHeader headerOfSecondProgram) {
		String escapedFirstProgramName = headerOfFirstProgram.getProgramName();
		escapedFirstProgramName = escapedFirstProgramName.replace(Constants.REMIX_URL_PREFIX_INDICATOR,
				Constants.REMIX_URL_PREFIX_REPLACE_INDICATOR);
		escapedFirstProgramName = escapedFirstProgramName.replace(Constants.REMIX_URL_SUFIX_INDICATOR,
				Constants.REMIX_URL_SUFIX_REPLACE_INDICATOR);
		escapedFirstProgramName = escapedFirstProgramName.replace(Constants.REMIX_URL_SEPARATOR,
				Constants.REMIX_URL_REPLACE_SEPARATOR);

		String escapedSecondProgramName = headerOfSecondProgram.getProgramName();
		escapedSecondProgramName = escapedSecondProgramName.replace(Constants.REMIX_URL_PREFIX_INDICATOR,
				Constants.REMIX_URL_PREFIX_REPLACE_INDICATOR);
		escapedSecondProgramName = escapedSecondProgramName.replace(Constants.REMIX_URL_SUFIX_INDICATOR,
				Constants.REMIX_URL_SUFIX_REPLACE_INDICATOR);
		escapedSecondProgramName = escapedSecondProgramName.replace(Constants.REMIX_URL_SEPARATOR,
				Constants.REMIX_URL_REPLACE_SEPARATOR);

		StringBuilder remixUrlString = new StringBuilder(escapedFirstProgramName);

		if (!headerOfFirstProgram.getRemixParentsUrlString().equals("")) {
			remixUrlString
					.append(' ')
					.append(Constants.REMIX_URL_PREFIX_INDICATOR)
					.append(headerOfFirstProgram.getRemixParentsUrlString())
					.append(Constants.REMIX_URL_SUFIX_INDICATOR);
		}

		remixUrlString
				.append(Constants.REMIX_URL_SEPARATOR)
				.append(' ')
				.append(escapedSecondProgramName);

		if (!headerOfSecondProgram.getRemixParentsUrlString().equals("")) {
			remixUrlString
					.append(' ')
					.append(Constants.REMIX_URL_PREFIX_INDICATOR)
					.append(headerOfSecondProgram.getRemixParentsUrlString())
					.append(Constants.REMIX_URL_SUFIX_INDICATOR);
		}

		return remixUrlString.toString();
	}

	// based on: http://stackoverflow.com/a/27295688
	public static List<String> extractRemixUrlsFromString(String text) {
		RemixUrlParsingState state = RemixUrlParsingState.STARTING;
		ArrayList<String> extractedUrls = new ArrayList<>();
		StringBuffer temp = new StringBuffer("");

		for (int index = 0; index < text.length(); index++) {
			char currentCharacter = text.charAt(index);
			switch (currentCharacter) {
				case Constants.REMIX_URL_PREFIX_INDICATOR:
					if (state == RemixUrlParsingState.STARTING) {
						state = RemixUrlParsingState.BETWEEN;
					} else if (state == RemixUrlParsingState.TOKEN) {
						temp.delete(0, temp.length());
						state = RemixUrlParsingState.BETWEEN;
					}
					break;

				case Constants.REMIX_URL_SUFIX_INDICATOR:
					if (state == RemixUrlParsingState.TOKEN) {
						String extractedUrl = temp.toString().trim();
						if (!extractedUrl.contains(String.valueOf(Constants.REMIX_URL_SEPARATOR))
								&& extractedUrl.length() > 0) {
							extractedUrls.add(extractedUrl);
						}
						temp.delete(0, temp.length());
						state = RemixUrlParsingState.BETWEEN;
					}
					break;

				default:
					state = RemixUrlParsingState.TOKEN;
					temp.append(currentCharacter);
			}
		}

		if (extractedUrls.size() == 0 && !text.contains(String.valueOf(Constants.REMIX_URL_SEPARATOR))) {
			extractedUrls.add(text);
		}

		return extractedUrls;
	}

	public static Date getScratchSecondReleasePublishedDate() {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, Constants.SCRATCH_SECOND_RELEASE_PUBLISHED_DATE_YEAR);
		calendar.set(Calendar.MONTH, Constants.SCRATCH_SECOND_RELEASE_PUBLISHED_DATE_MONTH);
		calendar.set(Calendar.DAY_OF_MONTH, Constants.SCRATCH_SECOND_RELEASE_PUBLISHED_DATE_DAY);
		return calendar.getTime();
	}

	public static boolean isDeprecatedScratchProgram(final ScratchProgramData programData) {
		// NOTE: ignoring old Scratch 1.x programs -> converter only supports version 2.x and later
		//       Scratch 1.x programs are created before May 9, 2013 (see: https://wiki.scratch.mit.edu/wiki/Scratch_2.0)
		final Date releasePublishedDate = getScratchSecondReleasePublishedDate();
		if (programData.getModifiedDate() != null && programData.getModifiedDate().before(releasePublishedDate)) {
			return true;
		} else if (programData.getCreatedDate() != null && programData.getCreatedDate().before(releasePublishedDate)) {
			return true;
		}
		return false;
	}

	public static String extractParameterFromURL(final String url, final String parameterKey) {
		final String query = url.split("\\?")[1];
		return Splitter.on('&').trimResults().withKeyValueSeparator("=").split(query).get(parameterKey);
	}

	public static long extractScratchJobIDFromURL(final String url) {
		if (!url.startsWith(Constants.SCRATCH_CONVERTER_BASE_URL)) {
			return Constants.INVALID_SCRATCH_PROGRAM_ID;
		}

		final String jobIDString = extractParameterFromURL(url, "job_id");
		if (jobIDString == null) {
			return Constants.INVALID_SCRATCH_PROGRAM_ID;
		}

		final long jobID = Long.parseLong(jobIDString);
		return jobID > 0 ? jobID : Constants.INVALID_SCRATCH_PROGRAM_ID;
	}

	public static int[] extractImageSizeFromScratchImageURL(final String url) {
		// example: https://cdn2.scratch.mit.edu/get_image/project/10205819_480x360.png?v=1368470695.0 -> [480, 360]
		int[] defaultSize = new int[] { Constants.SCRATCH_IMAGE_DEFAULT_WIDTH, Constants.SCRATCH_IMAGE_DEFAULT_HEIGHT };

		String urlWithoutQuery = url.split("\\?")[0];
		String[] urlStringParts = urlWithoutQuery.split("_");
		if (urlStringParts.length == 0) {
			return defaultSize;
		}

		final String[] sizeParts = urlStringParts[urlStringParts.length - 1].replace(".png", "").split("x");
		if (sizeParts.length != 2) {
			return defaultSize;
		}

		try {
			int width = Integer.parseInt(sizeParts[0]);
			int height = Integer.parseInt(sizeParts[1]);
			return new int[] { width, height };
		} catch (NumberFormatException ex) {
			return new int[] { Constants.SCRATCH_IMAGE_DEFAULT_WIDTH, Constants.SCRATCH_IMAGE_DEFAULT_HEIGHT };
		}
	}

	public static String changeSizeOfScratchImageURL(final String url, int newHeight) {
		// example: https://cdn2.scratch.mit.edu/get_image/project/10205819_480x360.png
		//    ->    https://cdn2.scratch.mit.edu/get_image/project/10205819_240x180.png
		final int[] imageSize = extractImageSizeFromScratchImageURL(url);
		final int width = imageSize[0];
		final int height = imageSize[1];
		final int newWidth = Math.round(((float) width) / ((float) height) * newHeight);

		return url.replace(width + "x", Integer.toString(newWidth) + "x")
				.replace("x" + height, "x" + Integer.toString(newHeight));
	}

	public static String humanFriendlyFormattedShortNumber(final int number) {
		if (number < 1_000) {
			return Integer.toString(number);
		} else if (number < 10_000) {
			return Integer.toString(number / 1_000) + (number % 1_000 > 100 ? "."
					+ Integer.toString((number % 1_000) / 100) : "") + "k";
		} else if (number < 1_000_000) {
			return Integer.toString(number / 1_000) + "k";
		}
		return Integer.toString(number / 1_000_000) + "M";
	}

	public static boolean setListViewHeightBasedOnItems(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter != null) {
			int numberOfItems = listAdapter.getCount();
			// Get total height of all items.
			int totalItemsHeight = 0;
			for (int itemPos = 0; itemPos < numberOfItems; ++itemPos) {
				View item = listAdapter.getView(itemPos, null, listView);
				item.measure(0, 0);
				totalItemsHeight += item.getMeasuredHeight();
			}

			// Get total height of all item dividers.
			int totalDividersHeight = listView.getDividerHeight() * (numberOfItems - 1);

			// Set list height.
			ViewGroup.LayoutParams params = listView.getLayoutParams();
			params.height = totalItemsHeight + totalDividersHeight;
			listView.setLayoutParams(params);
			listView.requestLayout();
			return true;
		} else {
			return false;
		}
	}

	// Has to be called after listView has already been measured.
	public static boolean setListViewHeightBasedOnItemsAndTheirWidth(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter != null) {
			int numberOfItems = listAdapter.getCount();
			// Get total height of all items.
			int totalItemsHeight = 0;
			final int maxHeight = 1000;
			for (int itemPos = 0; itemPos < numberOfItems; ++itemPos) {
				View item = listAdapter.getView(itemPos, null, listView);
				item.measure(View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.EXACTLY),
						View.MeasureSpec.makeMeasureSpec(maxHeight, View.MeasureSpec.AT_MOST));
				totalItemsHeight += item.getMeasuredHeight();
			}

			// Get total height of all item dividers.
			int totalDividersHeight = listView.getDividerHeight() * (numberOfItems - 1);

			// Set list height.
			ViewGroup.LayoutParams params = listView.getLayoutParams();
			params.height = totalItemsHeight + totalDividersHeight;
			listView.setLayoutParams(params);
			listView.requestLayout();
			return true;
		} else {
			return false;
		}
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

	public static String buildPathForTemplatesZip(String templateName) {
		return Utils.buildPath(Constants.TMP_TEMPLATES_PATH, templateName) + ".zip";
	}

	public static String buildProjectPath(String projectName) {
		return buildPath(Constants.DEFAULT_ROOT, UtilFile.encodeSpecialCharsForFileSystem(projectName));
	}

	public static String buildScenePath(String projectName, String sceneName) {
		return buildPath(buildProjectPath(projectName), UtilFile.encodeSpecialCharsForFileSystem(sceneName));
	}

	public static String buildBackpackScenePath(String sceneName) {
		return buildPath(Constants.DEFAULT_ROOT, Constants.BACKPACK_DIRECTORY, Constants.SCENES_DIRECTORY,
				UtilFile.encodeSpecialCharsForFileSystem(sceneName));
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

	public static double round(double value, int precision) {
		final int scale = (int) Math.pow(10, precision);
		return (double) Math.round(value * scale) / scale;
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

	public static ArrayList<String> formatStringForBubbleBricks(String text) {
		ArrayList<String> lines = new ArrayList<>();

		int cursorPos = 0;
		while (cursorPos + Constants.MAX_STRING_LENGTH_BUBBLES < text.length()) {
			String newLine = text.substring(cursorPos, cursorPos + Constants.MAX_STRING_LENGTH_BUBBLES);
			int lastWhitespace = newLine.lastIndexOf(' ');
			if (lastWhitespace < 0) {
				lastWhitespace = Constants.MAX_STRING_LENGTH_BUBBLES;
			}
			newLine = text.substring(cursorPos, cursorPos + lastWhitespace);
			while (newLine.contains("\n")) {
				String subLine = newLine.substring(0, newLine.indexOf('\n') + 1);
				lines.add(subLine);
				newLine = newLine.replace(subLine, "");
			}
			lines.add(newLine);
			cursorPos += lastWhitespace;
		}
		lines.add(text.substring(cursorPos, text.length()).trim());

		return lines;
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

	public static Activity getActivity() throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException,
			NoSuchMethodException, InvocationTargetException {
		Class activityThreadClass = Class.forName("android.app.ActivityThread");
		Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
		Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
		activitiesField.setAccessible(true);
		HashMap activities = (HashMap) activitiesField.get(activityThread);
		for (Object activityRecord : activities.values()) {
			Class activityRecordClass = activityRecord.getClass();
			Field pausedField = activityRecordClass.getDeclaredField("paused");
			pausedField.setAccessible(true);
			if (!pausedField.getBoolean(activityRecord)) {
				Field activityField = activityRecordClass.getDeclaredField("activity");
				activityField.setAccessible(true);
				Activity activity = (Activity) activityField.get(activityRecord);
				return activity;
			}
		}
		return null;
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
		String projectName;
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		projectName = sharedPreferences.getString(Constants.PREF_PROJECTNAME_KEY, null);
		if (ProjectManager.getInstance().getCurrentProject() == null) {
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
				Log.i(TAG, "Somebody deleted all projects in the file-system");
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
			newName = name + "_" + nextNumber;
		}

		if (ProjectManager.getInstance().getCurrentScene().containsSpriteBySpriteName(newName)) {
			return searchForNonExistingObjectNameInCurrentProgram(name, ++nextNumber);
		}

		return newName;
	}

	public static String getUniqueNfcTagName(String name) {
		return searchForNonExistingNfcTagName(name, 0);
	}

	private static String searchForNonExistingNfcTagName(String name, int nextNumber) {
		String newName;
		List<NfcTagData> nfcTagDataList = ProjectManager.getInstance().getCurrentSprite().getNfcTagList();
		if (nextNumber == 0) {
			newName = name;
		} else {
			newName = name + "_" + nextNumber;
		}
		for (NfcTagData nfcTagData : nfcTagDataList) {
			if (nfcTagData.getNfcTagName().equals(newName)) {
				return searchForNonExistingNfcTagName(name, ++nextNumber);
			}
		}
		return newName;
	}

	public static String getUniqueLookName(LookData lookData, boolean forBackPack) {
		return searchForNonExistingLookName(lookData, 0, forBackPack);
	}

	private static String searchForNonExistingLookName(LookData originalLookData,
			int nextNumber, boolean forBackPack) {
		String newName;
		List<LookData> lookDataList;
		if (forBackPack) {
			lookDataList = BackPackListManager.getInstance().getAllBackPackedLooks();
		} else {
			lookDataList = ProjectManager.getInstance().getCurrentSprite().getLookDataList();
		}

		if (nextNumber == 0) {
			newName = originalLookData.getLookName();
		} else {
			newName = originalLookData.getLookName() + "_" + nextNumber;
		}
		for (LookData lookData : lookDataList) {
			if (lookData.getLookName().equals(newName)) {
				return searchForNonExistingLookName(originalLookData, ++nextNumber, forBackPack);
			}
		}
		return newName;
	}

	public static String getUniqueSpriteName(Sprite sprite) {
		return searchForNonExistingSpriteName(sprite, 0);
	}

	private static String searchForNonExistingSpriteName(Sprite sprite, int nextNumber) {
		String newName;
		List<Sprite> spriteList;
		if (!sprite.isBackpackObject) {
			spriteList = BackPackListManager.getInstance().getAllBackPackedSprites();
		} else {
			spriteList = ProjectManager.getInstance().getCurrentScene().getSpriteList();
		}

		if (nextNumber == 0) {
			newName = sprite.getName();
		} else {
			newName = sprite.getName() + "_" + nextNumber;
		}
		for (Sprite spriteListItem : spriteList) {
			if (spriteListItem.getName().equals(newName)) {
				return searchForNonExistingSpriteName(sprite, ++nextNumber);
			}
		}
		return newName;
	}

	public static String getUniqueSceneName(String sceneName, Project firstProject, Project secondProject) {
		Project backup = ProjectManager.getInstance().getCurrentProject();
		ProjectManager.getInstance().setCurrentProject(firstProject);
		String result = getUniqueSceneName(sceneName, false);
		ProjectManager.getInstance().setCurrentProject(secondProject);
		sceneName = getUniqueSceneName(result, false);
		ProjectManager.getInstance().setCurrentProject(backup);
		return sceneName;
	}

	public static String getUniqueSceneName(String sceneName, boolean forBackPack) {
		List<Scene> sceneList;

		if (forBackPack) {
			sceneList = BackPackListManager.getInstance().getAllBackpackedScenes();
		} else {
			sceneList = ProjectManager.getInstance().getCurrentProject().getSceneList();
		}

		String possibleNewName = sceneName;
		Boolean check = true;
		int nextNumber = 1;
		while (check) {

			check = false;
			possibleNewName = sceneName + "_" + nextNumber;
			for (Scene sceneListItem : sceneList) {
				if (sceneListItem.getName().equals(possibleNewName)) {
					check = true;
					break;
				}
			}
			nextNumber += 1;
		}

		return possibleNewName;
	}

	public static String searchForNonExistingSceneName(String sceneName, int nextNumber, boolean forBackPack) {
		List<Scene> sceneList;

		if (forBackPack) {
			sceneList = BackPackListManager.getInstance().getAllBackpackedScenes();
		} else {
			sceneList = ProjectManager.getInstance().getCurrentProject().getSceneList();
		}

		String possibleNewName = String.format(sceneName, nextNumber);
		for (Scene sceneListItem : sceneList) {
			if (sceneListItem.getName().equals(possibleNewName)) {
				return searchForNonExistingSceneName(sceneName, ++nextNumber, forBackPack);
			}
		}

		return possibleNewName;
	}

	public static String getUniqueSoundName(SoundInfo soundInfo, boolean forBackPack) {
		return searchForNonExistingSoundTitle(soundInfo, 0, forBackPack);
	}

	public static Project findValidProject(Context context) {
		Project loadableProject = null;

		List<String> projectNameList = UtilFile.getProjectNames(new File(Constants.DEFAULT_ROOT));
		for (String projectName : projectNameList) {
			loadableProject = StorageHandler.getInstance().loadProject(projectName, context);
			if (loadableProject != null) {
				break;
			}
		}
		return loadableProject;
	}

	private static String searchForNonExistingSoundTitle(SoundInfo soundInfo, int nextNumber, boolean forBackPack) {
		// search for sounds with the same title
		String newTitle = "";
		List<SoundInfo> soundInfoList;
		if (forBackPack) {
			soundInfoList = BackPackListManager.getInstance().getAllBackPackedSounds();
		} else {
			soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		}

		if (nextNumber == 0) {
			if (soundInfo != null) {
				newTitle = soundInfo.getTitle();
			}
		} else {
			if (soundInfo != null) {
				newTitle = soundInfo.getTitle() + "_" + nextNumber;
			}
		}
		for (SoundInfo soundInfoFromList : soundInfoList) {
			if (soundInfoFromList.getTitle().equals(newTitle)) {
				return searchForNonExistingSoundTitle(soundInfo, ++nextNumber, forBackPack);
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
				Log.e(TAG, "error_load_image rewriteImageFileForStage");
				Utils.showErrorDialog(context, R.string.error_load_image);
				StorageHandler.getInstance().deleteFile(lookFile.getAbsolutePath(), false);
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

	public static boolean isStandardScene(Project project, String sceneName, Context context) {
		try {
			Project standardProject = DefaultProjectHandler.createAndSaveDefaultProject(getUniqueProjectName(),
					context);
			Scene standardScene = standardProject.getDefaultScene();
			ProjectManager.getInstance().deleteCurrentProject(null);

			ProjectManager.getInstance().setProject(project);
			ProjectManager.getInstance().saveProject(context);
			Scene sceneToCheck = ProjectManager.getInstance().getCurrentProject().getSceneByName(sceneName);

			if (sceneToCheck == null) {
				Log.e(TAG, "isStandardScene: scene not found");
				return false;
			}

			boolean result = true;

			for (int i = 0; i < standardScene.getSpriteList().size(); i++) {
				Sprite standardSprite = standardScene.getSpriteList().get(i);
				Sprite spriteToCheck = sceneToCheck.getSpriteList().get(i);

				for (int t = 0; t < standardSprite.getLookDataList().size(); t++) {
					LookData standardLook = standardSprite.getLookDataList().get(t);
					LookData lookToCheck = spriteToCheck.getLookDataList().get(t);

					result &= standardLook.equals(lookToCheck);
					if (!result) {
						Log.e(TAG, "isStandardScene: " + standardLook.getLookName() + " was not the same as "
								+ lookToCheck.getLookName());
						return false;
					}
				}

				for (int t = 0; t < standardSprite.getSoundList().size(); t++) {
					SoundInfo standardSound = standardSprite.getSoundList().get(t);
					SoundInfo soundToCheck = spriteToCheck.getSoundList().get(t);

					result &= standardSound.equals(soundToCheck);
					if (!result) {
						Log.e(TAG, "isStandardScene: " + standardSound.getTitle() + " was not the same as "
								+ standardSound.getTitle());
						return false;
					}
				}

				for (int t = 0; t < standardSprite.getListWithAllBricks().size(); t++) {
					Brick standardBrick = standardSprite.getListWithAllBricks().get(t);
					Brick brickToCheck = spriteToCheck.getListWithAllBricks().get(t);

					result &= standardBrick.getClass().toString().equals(brickToCheck.getClass().toString());
					if (!result) {
						Log.e(TAG, "isStandardScene: " + standardBrick.getClass().toString() + " was not the same as "
								+ brickToCheck.getClass().toString());
						return false;
					}
				}

				result &= standardSprite.equals(spriteToCheck);
				if (!result) {
					Log.e(TAG, "isStandardScene: " + standardSprite.getName() + " was not the same as "
							+ spriteToCheck.getName());
					return false;
				}
			}

			return result;
		} catch (Exception e) {
			Log.e(TAG, "Exception: isStandardScene: ", e);
			return false;
		}
	}

	public static boolean isStandardProject(Project projectToCheck, Context context) {
		try {
			Project standardProject = DefaultProjectHandler.createAndSaveDefaultProject(getUniqueProjectName(),
					context);
			String standardProjectXMLString = StorageHandler.getInstance().getXMLStringOfAProject(standardProject);
			int start = standardProjectXMLString.indexOf("<scenes>");
			int end = standardProjectXMLString.indexOf("</scenes>");
			String standardProjectSpriteList = standardProjectXMLString.substring(start, end);
			ProjectManager.getInstance().deleteCurrentProject(null);

			ProjectManager.getInstance().setProject(projectToCheck);
			ProjectManager.getInstance().saveProject(context);

			String projectToCheckXMLString = StorageHandler.getInstance().getXMLStringOfAProject(projectToCheck);
			start = projectToCheckXMLString.indexOf("<scenes>");
			end = projectToCheckXMLString.indexOf("</scenes>");
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

	public static void invalidateLoginTokenIfUserRestricted(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		if (sharedPreferences.getBoolean(Constants.RESTRICTED_USER, false)) {
			logoutUser(context);
		}
	}

	@SuppressWarnings("unused")
	public static void logoutUser(Context context) {

		TrackingUtil.trackLogoutEndSessionEvent(context);

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		String userName = sharedPreferences.getString(Constants.USERNAME, Constants.NO_USERNAME);
		LogoutTask logoutTask = new LogoutTask(context, userName);
		logoutTask.execute();

		sharedPreferences.edit().putString(Constants.TOKEN, Constants.NO_TOKEN).commit();
		sharedPreferences.edit().putString(Constants.USERNAME, Constants.NO_USERNAME).commit();

		sharedPreferences.edit().putBoolean(Constants.FACEBOOK_TOKEN_REFRESH_NEEDED, false).commit();
		sharedPreferences.edit().putString(Constants.FACEBOOK_EMAIL, Constants.NO_FACEBOOK_EMAIL).commit();
		sharedPreferences.edit().putString(Constants.FACEBOOK_USERNAME, Constants.NO_FACEBOOK_USERNAME).commit();
		sharedPreferences.edit().putString(Constants.FACEBOOK_ID, Constants.NO_FACEBOOK_ID).commit();
		sharedPreferences.edit().putString(Constants.FACEBOOK_LOCALE, Constants.NO_FACEBOOK_LOCALE).commit();
		AccessToken.setCurrentAccessToken(null);

		sharedPreferences.edit().putString(Constants.GOOGLE_EXCHANGE_CODE, Constants.NO_GOOGLE_EXCHANGE_CODE).commit();
		sharedPreferences.edit().putString(Constants.GOOGLE_EMAIL, Constants.NO_GOOGLE_EMAIL).commit();
		sharedPreferences.edit().putString(Constants.GOOGLE_USERNAME, Constants.NO_GOOGLE_USERNAME).commit();
		sharedPreferences.edit().putString(Constants.GOOGLE_ID, Constants.NO_GOOGLE_ID).commit();
		sharedPreferences.edit().putString(Constants.GOOGLE_LOCALE, Constants.NO_GOOGLE_LOCALE).commit();
		sharedPreferences.edit().putString(Constants.GOOGLE_ID_TOKEN, Constants.NO_GOOGLE_ID_TOKEN).commit();

		WebViewActivity.clearCookies(context);

		ToastUtil.showSuccess(context, R.string.logout_successful);

		if (BuildConfig.CREATE_AT_SCHOOL) {
			ProjectManager.getInstance().showLogInDialog((Activity) context, false);
		}
	}

	public static boolean isUserLoggedIn(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String token = preferences.getString(Constants.TOKEN, Constants.NO_TOKEN);

		boolean tokenValid = !(token.equals(Constants.NO_TOKEN) || token.length() != ServerCalls.TOKEN_LENGTH
				|| token.equals(ServerCalls.TOKEN_CODE_INVALID));
		return tokenValid;
	}

	public static String getNumberStringForBricks(float value) {
		return (int) value == value ? "" + (int) value : "" + value;
	}

	public static void replaceTranslatableStringsInProject(String templateName, Context context) {
		Project project = ProjectManager.getInstance().getCurrentProject();
		List<String> stringEntriesList = new ArrayList<>();

		List<String> spriteVariablesToReplaceInFormulas = new ArrayList<>();
		List<String> spriteListsToReplaceInFormulas = new ArrayList<>();
		List<String> projectVariablesToReplaceInFormulas = new ArrayList<>();
		List<String> projectListsToReplaceInFormulas = new ArrayList<>();
		List<String> spritesToReplaceInFormulas = new ArrayList<>();

		locateAndReplaceGlobalVariableAndListStrings(project, templateName, stringEntriesList, projectListsToReplaceInFormulas,
				projectVariablesToReplaceInFormulas, context);

		buildSpriteMap(project, spritesToReplaceInFormulas);

		for (Scene scene : project.getSceneList()) {
			locateAndReplaceSceneStrings(scene, templateName, stringEntriesList, context);

			DataContainer dataContainer = scene.getDataContainer();

			for (Sprite sprite : scene.getSpriteList()) {
				locateAndReplaceSpriteStrings(sprite, templateName, stringEntriesList, context);

				locateAndReplaceLookStrings(sprite, templateName, stringEntriesList, context);
				locateAndReplaceSoundStrings(sprite, templateName, stringEntriesList, context);

				locateLocalVariableAndListStrings(sprite, templateName, stringEntriesList,
						spriteVariablesToReplaceInFormulas, spriteListsToReplaceInFormulas, dataContainer);

				locateAndReplaceBricksAndBrickFormulas(sprite, templateName, stringEntriesList,
						projectVariablesToReplaceInFormulas, spriteVariablesToReplaceInFormulas,
						projectListsToReplaceInFormulas, spriteListsToReplaceInFormulas, spritesToReplaceInFormulas,
						scene, context);

				replaceLocalVariableAndListStrings(sprite, templateName, dataContainer, context);

				spriteListsToReplaceInFormulas.clear();
				spriteVariablesToReplaceInFormulas.clear();
			}
		}

		String entries = "";
		for (String entry : stringEntriesList) {
			entries += entry;
		}

		logLargeString(entries);
	}

	private static void buildSpriteMap(Project project, List<String> spritesToReplaceInFormulas) {
		for (Scene scene : project.getSceneList()) {
			for (Sprite sprite : scene.getSpriteList()) {
				spritesToReplaceInFormulas.add(sprite.getName());
			}
		}
	}

	private static void locateAndReplaceGlobalVariableAndListStrings(Project project, String templateName,
			List<String> stringEntriesList, List<String> projectListsToReplaceInFormulas,
			List<String> projectVariablesToReplaceInFormulas, Context context) {

		String key = templateName + Constants.TRANSLATION_PROJECT_VARIABLE;
		for (UserVariable variable : project.getProjectVariables()) {
			String variableName = variable.getName();

			addIfNotInList(stringEntriesList, createStringEntry(key, variableName));
			projectVariablesToReplaceInFormulas.add(variableName);

			variable.setName(getStringResourceByName(getStringResourceName(key, variableName), variableName, context));
		}

		key = templateName + Constants.TRANSLATION_PROJECT_LIST;
		for (UserList list : project.getProjectLists()) {
			String listName = list.getName();

			addIfNotInList(stringEntriesList, createStringEntry(key, listName));
			projectListsToReplaceInFormulas.add(listName);

			list.setName(getStringResourceByName(getStringResourceName(key, listName), listName, context));
		}
	}

	private static void locateLocalVariableAndListStrings(Sprite sprite, String templateName,
			List<String> stringEntriesList, List<String> spriteVariablesToReplaceInFormulas,
			List<String> spriteListsToReplaceInFormulas, DataContainer dataContainer) {

		String key = templateName + Constants.TRANSLATION_SPRITE_VARIABLE;
		for (UserVariable variable : dataContainer.getVariableListForSprite(sprite)) {

			String variableName = variable.getName();
			addIfNotInList(stringEntriesList, createStringEntry(key, variableName));
			spriteVariablesToReplaceInFormulas.add(variableName);
		}

		key = templateName + Constants.TRANSLATION_SPRITE_LIST;
		for (UserList list : dataContainer.getUserListListForSprite(sprite)) {

			addIfNotInList(stringEntriesList, createStringEntry(key, list.getName()));
			spriteListsToReplaceInFormulas.add(list.getName());
		}
	}

	private static void replaceLocalVariableAndListStrings(Sprite sprite, String templateName,
			DataContainer dataContainer, Context context) {

		String key = templateName + Constants.TRANSLATION_SPRITE_VARIABLE;
		for (UserVariable variable : dataContainer.getVariableListForSprite(sprite)) {
			variable.setName(getStringResourceByName(getStringResourceName(key, variable.getName()), variable.getName(), context));
		}

		key = templateName + Constants.TRANSLATION_SPRITE_LIST;
		for (UserList list : dataContainer.getUserListListForSprite(sprite)) {
			list.setName(getStringResourceByName(getStringResourceName(key, list.getName()), list.getName(), context));
		}
	}

	private static void locateAndReplaceSceneStrings(Scene scene, String templateName, List<String>
			stringEntriesList, Context context) {
		String key = templateName + Constants.TRANSLATION_SCENE;
		String oldSceneName = scene.getName();
		addIfNotInList(stringEntriesList, createStringEntry(key, oldSceneName));

		scene.setSceneName(getStringResourceByName(getStringResourceName(key, oldSceneName), oldSceneName, context));
		UtilFile.renameSceneDirectory(oldSceneName, scene.getName());
	}

	private static void locateAndReplaceSpriteStrings(Sprite sprite, String templateName,
			List<String> stringEntriesList, Context context) {
		String key = templateName + Constants.TRANSLATION_SPRITE;
		String spriteName = sprite.getName();

		addIfNotInList(stringEntriesList, createStringEntry(key, spriteName));

		sprite.setName(getStringResourceByName(getStringResourceName(key, spriteName), spriteName, context));
	}

	private static void locateAndReplaceSoundStrings(Sprite sprite, String templateName, List<String>
			stringEntriesList, Context context) {
		String key = templateName + Constants.TRANSLATION_SOUND;

		for (SoundInfo soundInfo : sprite.getSoundList()) {
			addIfNotInList(stringEntriesList, createStringEntry(key, soundInfo.getTitle()));

			String soundName = soundInfo.getTitle();
			soundInfo.setTitle(getStringResourceByName(getStringResourceName(key, soundName), soundName, context));
		}
	}

	private static void locateAndReplaceLookStrings(Sprite sprite, String templateName, List<String>
			stringEntriesList, Context context) {
		String key = templateName + Constants.TRANSLATION_LOOK;

		for (LookData lookData : sprite.getLookDataList()) {
			addIfNotInList(stringEntriesList, createStringEntry(key, lookData.getLookName()));

			String lookName = lookData.getLookName();
			lookData.setLookName(getStringResourceByName(getStringResourceName(key, lookName), lookName, context));
		}
	}

	private static void locateAndReplaceBricksAndBrickFormulas(Sprite sprite, String templateName,
			List<String> stringEntriesList, List<String> projectVariablesToReplaceInFormulas,
			List<String> spriteVariablesToReplaceInFormulas,
			List<String> projectListsToReplaceInFormulas,
			List<String> spriteListsToReplaceInFormulas, List<String> spritesToReplaceInFormulas,
			Scene scene, Context context) {
		for (Brick brick : sprite.getListWithAllBricks()) {

			replaceFormulaDataStrings(projectVariablesToReplaceInFormulas, brick,
					templateName + Constants.TRANSLATION_PROJECT_VARIABLE, context);
			replaceFormulaDataStrings(spriteVariablesToReplaceInFormulas, brick,
					templateName + Constants.TRANSLATION_SPRITE_VARIABLE, context);
			replaceFormulaDataStrings(projectListsToReplaceInFormulas, brick,
					templateName + Constants.TRANSLATION_PROJECT_LIST, context);
			replaceFormulaDataStrings(spriteListsToReplaceInFormulas, brick,
					templateName + Constants.TRANSLATION_SPRITE_LIST, context);

			replaceFormulaSpriteStrings(spritesToReplaceInFormulas, brick, templateName + Constants
					.TRANSLATION_SPRITE, context);

			if (brick instanceof Translatable) {
				String stringEntry = ((Translatable) brick).translate(templateName, scene, sprite, context);
				if (stringEntry != null) {
					addIfNotInList(stringEntriesList, stringEntry);
				}
			}
		}
	}

	private static void replaceFormulaSpriteStrings(List<String> spritesToReplaceInFormulas, Brick brick,
			String key, Context context) {
		if (!(brick instanceof FormulaBrick)) {
			return;
		}

		for (String spriteName : spritesToReplaceInFormulas) {
			String newName = getStringResourceByName(getStringResourceName(key, spriteName), spriteName, context);
			for (Formula formula : ((FormulaBrick) brick).getFormulas()) {
				formula.updateCollisionFormulas(spriteName, newName, context);
			}
		}
	}

	private static void replaceFormulaDataStrings(List<String> dataToReplaceInFormulas, Brick brick, String key,
			Context context) {
		if (!(brick instanceof FormulaBrick)) {
			return;
		}

		for (String variable : dataToReplaceInFormulas) {
			String newName = getStringResourceByName(getStringResourceName(key, variable), variable, context);
			for (Formula formula : ((FormulaBrick) brick).getFormulas()) {
				formula.updateVariableReferences(variable, newName, context);
			}
		}
	}

	public static String getStringResourceByName(String key, String originalName, Context context) {
		String packageName = context.getPackageName();
		int resId = context.getResources().getIdentifier(key, "string", packageName);
		Log.d(TAG, "Loading resId:" + resId + " for key: " + key);
		if (resId != 0) {
			return context.getString(resId);
		} else {
			Log.d(TAG, "No resId for key: " + key);
		}
		return originalName;
	}

	private static void addIfNotInList(List<String> stringEntriesList, String stringEntry) {
		if (!stringEntriesList.contains(stringEntry)) {
			stringEntriesList.add(stringEntry);
		}
	}

	//This method prints valid strings.xml output as log message.
	//Intended to use when new templates are added - simply copy and paste the auto-generated xml content.
	private static void logLargeString(String stringEntries) {
		if (stringEntries.length() > Constants.MAX_LOGCAT_OUTPUT_CHARS) {
			Log.i(TAG, stringEntries.substring(0, Constants.MAX_LOGCAT_OUTPUT_CHARS));
			logLargeString(stringEntries.substring(Constants.MAX_LOGCAT_OUTPUT_CHARS));
		} else {
			Log.i(TAG, stringEntries);
		}
	}

	public static String createStringEntry(String key, String value) {
		return "<string name=\"" + getStringResourceName(key, value) + "\">" + value + "</string>\n";
	}

	public static String getStringResourceName(String key, String value) {
		return removeInvalidCharacters(key.concat(value)).toLowerCase(Locale.US);
	}

	private static String removeInvalidCharacters(String string) {
		return MATCHER.retainFrom(string).replace(" ", "_");
	}

	// http://stackoverflow.com/questions/2711858/is-it-possible-to-set-font-for-entire-application/16883281#16883281
	public static void setDefaultFont(Context context,
			String staticTypefaceFieldName, String fontAssetName) {
		final Typeface regular = Typeface.createFromAsset(context.getAssets(),
				fontAssetName);
		replaceFont(staticTypefaceFieldName, regular);
	}

	private static void replaceFont(String staticTypefaceFieldName,
			final Typeface newTypeface) {
		try {
			final Field staticField = Typeface.class
					.getDeclaredField(staticTypefaceFieldName);
			staticField.setAccessible(true);
			staticField.set(null, newTypeface);
		} catch (NoSuchFieldException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (IllegalAccessException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	public static <T> List<T> distinctListByClassOfObjects(List<T> listToDistinct) {
		Map<Class, T> uniqueMap = new HashMap<>();
		for (T objectInstance : listToDistinct) {
			uniqueMap.put(objectInstance.getClass(), objectInstance);
		}
		return new ArrayList<>(uniqueMap.values());
	}

	public static boolean isCreateAtSchoolUser(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPreferences.getBoolean(Constants.CREATE_AT_SCHOOL_USER, false);
	}
}
