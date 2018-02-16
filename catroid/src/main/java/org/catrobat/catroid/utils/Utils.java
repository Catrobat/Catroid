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
import com.google.common.base.Splitter;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.DefaultProjectHandler;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScratchProgramData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.XmlHeader;
import org.catrobat.catroid.exceptions.ProjectException;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.transfers.LogoutTask;
import org.catrobat.catroid.ui.WebViewActivity;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.web.ServerCalls;
import org.catrobat.catroid.web.WebconnectionException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
		return !success && exception != null && exception.getStatusCode() == WebconnectionException.ERROR_NETWORK;
	}

	public static boolean checkForSignInError(boolean success, WebconnectionException exception, Context context,
			boolean userSignedIn) {
		return (!success && exception != null) || context == null || !userSignedIn;
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

	public static String changeSizeOfScratchImageURL(final String url, int newHeight) {
		// example: https://cdn2.scratch.mit.edu/get_image/project/10205819_480x360.png
		//    ->    https://cdn2.scratch.mit.edu/get_image/project/10205819_240x180.png
		final int width = Constants.SCRATCH_IMAGE_DEFAULT_WIDTH;
		final int height = Constants.SCRATCH_IMAGE_DEFAULT_HEIGHT;
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
		Dialog dialog = builder.create();
		showDialog(context, dialog);
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
		Dialog dialog = builder.create();
		showDialog(context, dialog);
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

	private static void showDialog(Context context, final Dialog dialog) {
		if (context instanceof Activity) {
			((Activity) context).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					dialog.show();
				}
			});
		} else {
			dialog.show();
		}
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

	public static Pixmap getPixmapFromFile(File imageFile) {
		Pixmap pixmap;
		try {
			GdxNativesLoader.load();
			pixmap = new Pixmap(new FileHandle(imageFile));
		} catch (GdxRuntimeException gdxRuntimeException) {
			return null;
		} catch (Exception e) {
			return null;
		}
		return pixmap;
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
			Project standardProject = DefaultProjectHandler.createAndSaveDefaultProject(getUniqueProjectName(),
					context);
			String standardProjectXMLString = StorageHandler.getInstance().getXMLStringOfAProject(standardProject);
			int start = standardProjectXMLString.indexOf("<scenes>");
			int end = standardProjectXMLString.indexOf("</scenes>");
			String standardProjectSpriteList = standardProjectXMLString.substring(start, end);

			ProjectManager.getInstance().setProject(projectToCheck);
			ProjectManager.getInstance().saveProject(context);

			projectToCheck.updateMessageContainer(context);

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
		for (LookData lookData : ProjectManager.getInstance().getCurrentSprite().getLookList()) {
			if (lookData.getName().compareTo(name) == 0) {
				return true;
			}
		}
		return false;
	}

	public static boolean checkIfSoundExists(String name) {
		for (SoundInfo soundInfo : ProjectManager.getInstance().getCurrentSprite().getSoundList()) {
			if (soundInfo.getName().compareTo(name) == 0) {
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
		logoutUser(context, true);
	}

	public static void logoutUser(Context context, boolean showToast) {
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

		if (showToast) {
			ToastUtil.showSuccess(context, R.string.logout_successful);
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

	public static <T> List<T> distinctListByClassOfObjects(List<T> listToDistinct) {
		Map<Class, T> uniqueMap = new HashMap<>();
		for (T objectInstance : listToDistinct) {
			uniqueMap.put(objectInstance.getClass(), objectInstance);
		}
		return new ArrayList<>(uniqueMap.values());
	}

	public static int setBit(int number, int index, int value) {
		if ((index >= 0) && (index < 32)) {
			if (value == 0) {
				return number & ~(1 << index);
			} else {
				return number | (1 << index);
			}
		}
		return number;
	}

	public static int getBit(int number, int index) {
		if ((index >= 0) && (index < 32)) {
			return (number >> index) & 0x1;
		}
		return 0;
	}
}
