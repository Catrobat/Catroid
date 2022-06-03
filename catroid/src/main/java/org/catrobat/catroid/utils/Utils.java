/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.common.base.Splitter;
//import com.huawei.hms.mlsdk.asr.MLAsrConstants;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.DefaultProjectHandler;
import org.catrobat.catroid.common.ScratchProgramData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.XmlHeader;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.transfers.GoogleLoginHandler;
import org.catrobat.catroid.ui.WebViewActivity;
import org.catrobat.catroid.web.WebconnectionException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;
import okhttp3.Response;

import static android.speech.RecognizerIntent.ACTION_GET_LANGUAGE_DETAILS;
import static android.speech.RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE;
import static android.speech.RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES;

import static org.catrobat.catroid.common.Constants.EXIFTAGS_FOR_EXIFREMOVER;
import static org.catrobat.catroid.common.Constants.MAX_FILE_NAME_LENGTH;
import static org.catrobat.catroid.common.Constants.PREF_PROJECTNAME_KEY;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;
import static org.catrobat.catroid.io.asynctask.ProjectSaverKt.saveProjectSerial;
import static org.catrobat.catroid.web.ServerAuthenticationConstants.TOKEN_CODE_INVALID;
import static org.catrobat.catroid.web.ServerAuthenticationConstants.TOKEN_LENGTH;
import static org.koin.java.KoinJavaComponent.get;

public final class Utils {

	private static final String TAG = Utils.class.getSimpleName();

	private enum RemixUrlParsingState {
		STARTING, TOKEN, BETWEEN
	}

	public static final int TRANSLATION_PLURAL_OTHER_INTEGER = 767676;
	// IETF representation like "en-US".
	// if you need it in ISO representation, just replace '-' with '_'
	public static final ArrayList<String> SPEECH_RECOGNITION_SUPPORTED_LANGUAGES = new ArrayList<>();

	private Utils() {
		throw new AssertionError();
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = null;
		if (connectivityManager != null) {
			activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		}
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public static boolean checkIsNetworkAvailableAndShowErrorMessage(Context context) {
		boolean networkAvailable = isNetworkAvailable(context);
		if (!networkAvailable) {
			ToastUtil.showError(context, R.string.error_internet_connection);
		}
		return networkAvailable;
	}

	public static boolean checkForNetworkError(WebconnectionException exception) {
		return exception != null && exception.getStatusCode() == WebconnectionException.ERROR_NETWORK;
	}

	public static String generateRemixUrlsStringForMergedProgram(XmlHeader headerOfFirstProgram, XmlHeader headerOfSecondProgram) {
		String escapedFirstProgramName = headerOfFirstProgram.getProjectName();
		escapedFirstProgramName = escapedFirstProgramName.replace(Constants.REMIX_URL_PREFIX_INDICATOR,
				Constants.REMIX_URL_PREFIX_REPLACE_INDICATOR);
		escapedFirstProgramName = escapedFirstProgramName.replace(Constants.REMIX_URL_SUFIX_INDICATOR,
				Constants.REMIX_URL_SUFIX_REPLACE_INDICATOR);
		escapedFirstProgramName = escapedFirstProgramName.replace(Constants.REMIX_URL_SEPARATOR,
				Constants.REMIX_URL_REPLACE_SEPARATOR);

		String escapedSecondProgramName = headerOfSecondProgram.getProjectName();
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

	public static String getFileNameFromURL(String url) {
		int fileExtensionIndex = url.lastIndexOf('.');
		int fileNameIndex = Math.max(url.substring(0, fileExtensionIndex).lastIndexOf('/'), 0) + 1;
		String fileName = url.substring(fileNameIndex);
		fileName = fileName.split("[?&#]")[0];
		return fileName.contains(":") ? fileName.split(":")[1] : fileName;
	}

	public static String getFileNameFromHttpResponse(Response httpResponse) {
		String contentDisposition = httpResponse.header("Content-Disposition");
		if (contentDisposition != null) {
			Pattern fileNamePattern = Pattern.compile("filename\\*?=[\'\"]?(?:UTF-\\d[\'\"]*)?([^;\r\n\"\']*)[\'\"]?;?");
			Matcher matcher = fileNamePattern.matcher(contentDisposition);
			if (matcher.find()) {
				return matcher.group(1);
			}
		}
		return null;
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
		} else {
			return programData.getCreatedDate() != null && programData.getCreatedDate().before(releasePublishedDate);
		}
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

	public static String sanitizeFileName(String fileName) {
		fileName = fileName.replaceAll("[\\\\ /:*?\"^<>|]", "");
		return fileName.substring(0, Math.min(fileName.length(), MAX_FILE_NAME_LENGTH));
	}

	public static String md5Checksum(File file) {

		if (!file.isFile()) {
			Log.e(TAG, String.format("md5Checksum() Error with file %s isFile: %s isDirectory: %s exists: %s",
					file.getName(),
					file.isFile(),
					file.isDirectory(),
					file.exists()));
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

	public static InputStream getInputStreamFromAsset(Context context, String filename) throws IOException, NullPointerException {
		return context.getAssets().open(filename, AssetManager.ACCESS_BUFFER);
	}

	public static JSONObject getJsonObjectFromInputStream(InputStream stream) throws JSONException {
		return new JSONObject(new Scanner(stream).useDelimiter("\\A").next());
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

	public static String getCurrentProjectName(Context context) {
		if (ProjectManager.getInstance().getCurrentProject() == null) {

			if (FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY).size() == 0) {
				ProjectManager.getInstance().initializeDefaultProject(context);
			}

			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
			String currentProjectName = sharedPreferences.getString(Constants.PREF_PROJECTNAME_KEY, null);
			if (currentProjectName == null
					|| !FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY).contains(currentProjectName)) {
				currentProjectName = FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY).get(0);
			}
			return currentProjectName;
		}
		return ProjectManager.getInstance().getCurrentProject().getName();
	}

	public static void setLastUsedProjectName(Context context, String projectName) {
		PreferenceManager.getDefaultSharedPreferences(context)
				.edit()
				.putString(PREF_PROJECTNAME_KEY, projectName)
				.apply();
	}

	public static boolean isDefaultProject(Project projectToCheck, Context context) {
		try {
			String uniqueProjectName = "project_" + System.currentTimeMillis();

			while (FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY).contains(uniqueProjectName)) {
				uniqueProjectName = "project_" + System.currentTimeMillis();
			}

			Project defaultProject = DefaultProjectHandler.createAndSaveDefaultProject(uniqueProjectName, context, false);

			String defaultProjectXml = XstreamSerializer.getInstance().getXmlAsStringFromProject(defaultProject);

			StorageOperations.deleteDir(defaultProject.getDirectory());

			StringFinder stringFinder = new StringFinder();

			if (!stringFinder.findBetween(defaultProjectXml, "<scenes>", "</scenes>")) {
				return false;
			}

			String defaultProjectSpriteList = stringFinder.getResult();

			saveProjectSerial(projectToCheck, context);

			String projectToCheckXML = XstreamSerializer.getInstance().getXmlAsStringFromProject(projectToCheck);

			if (!stringFinder.findBetween(projectToCheckXML, "<scenes>", "</scenes")) {
				return false;
			}

			String projectToCheckSpriteList = stringFinder.getResult();

			String scriptIdRegex = "((?s)<scriptId>.*?</scriptId>)";
			String brickIdRegex = "(?s)<brickId>.*?</brickId>";
			String scriptIdReplacement = "<scriptId></scriptId>";
			String brickIdIdReplacement = "<bricktId></brickId>";
			projectToCheckSpriteList = projectToCheckSpriteList.replaceAll(scriptIdRegex, scriptIdReplacement);
			projectToCheckSpriteList = projectToCheckSpriteList.replaceAll(brickIdRegex, brickIdIdReplacement);
			defaultProjectSpriteList = defaultProjectSpriteList.replaceAll(scriptIdRegex, scriptIdReplacement);
			defaultProjectSpriteList = defaultProjectSpriteList.replaceAll(brickIdRegex, brickIdIdReplacement);

			return defaultProjectSpriteList.contentEquals(projectToCheckSpriteList);
		} catch (IllegalArgumentException | IOException illegalArgumentException) {
			Log.e(TAG, Log.getStackTraceString(illegalArgumentException));
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
				return TRANSLATION_PLURAL_OTHER_INTEGER;
			}
		}
	}

	public static void invalidateLoginTokenIfUserRestricted(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		if (sharedPreferences.getBoolean(Constants.RESTRICTED_USER, false)) {
			logoutUser(context);
			ToastUtil.showSuccess(context, R.string.logout_successful);
		}
	}

	public static void logoutUser(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		GoogleLoginHandler googleLoginHandler = new GoogleLoginHandler((AppCompatActivity) context);
		googleLoginHandler.getGoogleSignInClient().signOut();
		sharedPreferences.edit()
				.putString(Constants.TOKEN, Constants.NO_TOKEN)
				.putString(Constants.USERNAME, Constants.NO_USERNAME)
				.putString(Constants.GOOGLE_EXCHANGE_CODE, Constants.NO_GOOGLE_EXCHANGE_CODE)
				.putString(Constants.GOOGLE_EMAIL, Constants.NO_GOOGLE_EMAIL)
				.putString(Constants.GOOGLE_USERNAME, Constants.NO_GOOGLE_USERNAME)
				.putString(Constants.GOOGLE_ID, Constants.NO_GOOGLE_ID)
				.putString(Constants.GOOGLE_LOCALE, Constants.NO_GOOGLE_LOCALE)
				.putString(Constants.GOOGLE_ID_TOKEN, Constants.NO_GOOGLE_ID_TOKEN)
				.apply();
		WebViewActivity.clearCookies();
	}

	public static boolean isUserLoggedIn(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String token = preferences.getString(Constants.TOKEN, Constants.NO_TOKEN);

		boolean tokenValid = !(token.equals(Constants.NO_TOKEN) || token.length() != TOKEN_LENGTH
				|| token.equals(TOKEN_CODE_INVALID));
		return tokenValid;
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

	public static void fetchSpeechRecognitionSupportedLanguages(Context context) {
		if (!SPEECH_RECOGNITION_SUPPORTED_LANGUAGES.isEmpty()) {
			return;
		}

		MobileServiceAvailability mobileServiceAvailability = get(MobileServiceAvailability.class);

		if (mobileServiceAvailability.isGmsAvailable(context)) {
			final Intent srIntent = new Intent(ACTION_GET_LANGUAGE_DETAILS);
			srIntent.setPackage("com.google.android.googlequicksearchbox");

			context.sendOrderedBroadcast(srIntent, null, new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					final Bundle bundle = getResultExtras(true);

					if (bundle != null) {
						String defaultLanguage = bundle.getString(EXTRA_LANGUAGE_PREFERENCE);
						SensorHandler.setListeningLanguageSensor(defaultLanguage);
						List<String> supportedLanguages = bundle
								.getStringArrayList(EXTRA_SUPPORTED_LANGUAGES);
						if (supportedLanguages != null) {
							SPEECH_RECOGNITION_SUPPORTED_LANGUAGES.clear();
							SPEECH_RECOGNITION_SUPPORTED_LANGUAGES.addAll(supportedLanguages);
							SPEECH_RECOGNITION_SUPPORTED_LANGUAGES.remove(defaultLanguage);
							SPEECH_RECOGNITION_SUPPORTED_LANGUAGES.add(0, defaultLanguage);
						} else {
							Log.w(TAG, "onReceive: EXTRA_SUPPORTED_LANGUAGES is null");
						}
					} else {
						Log.w(TAG, "onReceive: Bundle is null");
					}
				}
			}, null, Activity.RESULT_OK, null, null);
		} else if (mobileServiceAvailability.isHmsAvailable(context)) {
			/*SensorHandler.setListeningLanguageSensor(MLAsrConstants.LAN_EN_US);
			SPEECH_RECOGNITION_SUPPORTED_LANGUAGES.clear();
			SPEECH_RECOGNITION_SUPPORTED_LANGUAGES.add(MLAsrConstants.LAN_EN_US);
			SPEECH_RECOGNITION_SUPPORTED_LANGUAGES.add(MLAsrConstants.LAN_DE_DE);
			SPEECH_RECOGNITION_SUPPORTED_LANGUAGES.add(MLAsrConstants.LAN_EN_IN);
			SPEECH_RECOGNITION_SUPPORTED_LANGUAGES.add(MLAsrConstants.LAN_ES_ES);
			SPEECH_RECOGNITION_SUPPORTED_LANGUAGES.add(MLAsrConstants.LAN_FR_FR);
			SPEECH_RECOGNITION_SUPPORTED_LANGUAGES.add(MLAsrConstants.LAN_ZH_CN);*/
		}
	}

	public static void removeExifData(File directory, String fileName) {
		boolean isJPG = fileName.toLowerCase(Locale.US).endsWith(".jpeg") || fileName.toLowerCase(Locale.US).endsWith(".jpg");

		if (!isJPG) {
			return;
		}

		File file = new File(directory, fileName);
		try {
			ExifInterface exif = new ExifInterface(file.getAbsolutePath());
			for (String exifTag: EXIFTAGS_FOR_EXIFREMOVER) {
				exif.setAttribute(exifTag, "");
			}
			exif.saveAttributes();
		} catch (IOException e) {
			Log.e(TAG, "removeExifData: Failed to remove exif data");
		}
	}

	public static boolean checkForDuplicates(List<Object> anyList) {
		Object prev = null;
		for (Object it: anyList) {
			if (it == prev) {
				return true;
			}
			prev = it;
		}
		return false;
	}
}
