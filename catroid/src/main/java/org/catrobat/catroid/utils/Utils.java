/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;

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
import org.catrobat.catroid.common.ScratchProgramData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.XmlHeader;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.transfers.LogoutTask;
import org.catrobat.catroid.ui.WebViewActivity;
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

import static org.catrobat.catroid.common.Constants.DEFAULT_ROOT_DIRECTORY;

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
			new AlertDialog.Builder(context)
					.setTitle(R.string.error)
					.setMessage(R.string.error_no_writiable_external_storage_available)
					.setNeutralButton(R.string.close, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							((Activity) context).moveTaskToBack(true);
						}
					})
					.show();
			return false;
		}
		return true;
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		return connectivityManager != null && connectivityManager.getActiveNetworkInfo().isConnected();
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

	public static long extractScratchJobIDFromURL(final String url) {
		if (!url.startsWith(Constants.SCRATCH_CONVERTER_BASE_URL)) {
			return Constants.INVALID_SCRATCH_PROGRAM_ID;
		}

		final String query = url.split("\\?")[1];
		final String jobIDString = Splitter.on('&').trimResults().withKeyValueSeparator("=").split(query).get("job_id");
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

	public static String getCurrentProjectName(Context context) {
		if (ProjectManager.getInstance().getCurrentProject() == null) {

			if (FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY).size() == 0) {
				ProjectManager.getInstance().initializeDefaultProject(context);
			}

			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
			String currentProjectName = sharedPreferences.getString(Constants.PREF_PROJECTNAME_KEY, null);
			if (currentProjectName == null || !XstreamSerializer.getInstance().projectExists(currentProjectName)) {
				currentProjectName = FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY).get(0);
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
		while (XstreamSerializer.getInstance().projectExists(projectName)) {
			projectName = "project_" + System.currentTimeMillis();
		}
		return projectName;
	}

	public static boolean isDefaultProject(Project projectToCheck, Context context) {
		try {
			Project defaultProject = DefaultProjectHandler
					.createAndSaveDefaultProject(getUniqueProjectName(), context);

			String defaultProjectXml = XstreamSerializer.getInstance().getXmlAsStringFromProject(defaultProject);

			StorageOperations.deleteDir(new File(PathBuilder.buildProjectPath(defaultProject.getName())));

			StringFinder stringFinder = new StringFinder();

			if (!stringFinder.findBetween(defaultProjectXml, "<scenes>", "</scenes>")) {
				return false;
			}

			String defaultProjectSpriteList = stringFinder.getResult();

			ProjectManager.getInstance().setProject(projectToCheck);
			ProjectManager.getInstance().saveProject(context);

			String projectToCheckXML = XstreamSerializer.getInstance().getXmlAsStringFromProject(projectToCheck);

			if (!stringFinder.findBetween(projectToCheckXML, "<scenes>", "</scenes")) {
				return false;
			}

			String projectToCheckSpriteList = stringFinder.getResult();
			return defaultProjectSpriteList.contentEquals(projectToCheckSpriteList);
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

		File projectDirectory = new File(PathBuilder.buildProjectPath(programName));
		return projectDirectory.exists();
	}

	public static void invalidateLoginTokenIfUserRestricted(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		if (sharedPreferences.getBoolean(Constants.RESTRICTED_USER, false)) {
			logoutUser(context);
		}
	}

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
