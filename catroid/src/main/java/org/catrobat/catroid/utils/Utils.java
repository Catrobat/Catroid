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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.DefaultProjectHandler;
import org.catrobat.catroid.common.ScratchProgramData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.XmlHeader;
import org.catrobat.catroid.content.bricks.brickspinner.PickableMusicalInstrument;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.io.asynctask.ProjectSaveTask;
import org.catrobat.catroid.transfers.GoogleLoginHandler;
import org.catrobat.catroid.transfers.LogoutTask;
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
import okhttp3.Response;

import static android.speech.RecognizerIntent.ACTION_GET_LANGUAGE_DETAILS;
import static android.speech.RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE;
import static android.speech.RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES;

import static org.catrobat.catroid.common.Constants.MAX_FILE_NAME_LENGTH;
import static org.catrobat.catroid.common.Constants.PREF_PROJECTNAME_KEY;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;
import static org.catrobat.catroid.web.ServerAuthenticationConstants.TOKEN_CODE_INVALID;
import static org.catrobat.catroid.web.ServerAuthenticationConstants.TOKEN_LENGTH;

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

			ProjectSaveTask
					.task(projectToCheck, context);

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
		String userName = sharedPreferences.getString(Constants.USERNAME, Constants.NO_USERNAME);
		GoogleLoginHandler googleLoginHandler = new GoogleLoginHandler((AppCompatActivity) context);
		googleLoginHandler.getGoogleSignInClient().signOut();
		LogoutTask logoutTask = new LogoutTask(context, userName);
		logoutTask.execute();
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

	public static List<PickableMusicalInstrument> getPickableMusicalInstruments() {
		List instruments = new ArrayList<PickableMusicalInstrument>();

		instruments.add(new PickableMusicalInstrument("Acoustic Grand Piano", 0));
		instruments.add(new PickableMusicalInstrument("Bright Acoustic Piano", 1));
		instruments.add(new PickableMusicalInstrument("Electric Grand Piano", 2));
		instruments.add(new PickableMusicalInstrument("Honky-tonk Piano", 3));
		instruments.add(new PickableMusicalInstrument("Rhodes Piano", 4));
		instruments.add(new PickableMusicalInstrument("Chorused Piano", 5));
		instruments.add(new PickableMusicalInstrument("Harpsichord", 6));
		instruments.add(new PickableMusicalInstrument("Clavinet", 7));
		instruments.add(new PickableMusicalInstrument("Celesta", 8));
		instruments.add(new PickableMusicalInstrument("Drawbar Organ", 16));
		instruments.add(new PickableMusicalInstrument("Percussive Organ", 17));
		instruments.add(new PickableMusicalInstrument("Rock Organ", 18));
		instruments.add(new PickableMusicalInstrument("Church Organ", 19));
		instruments.add(new PickableMusicalInstrument("Reed Organ", 20));
		instruments.add(new PickableMusicalInstrument("Acoustic Guitar", 24));
		instruments.add(new PickableMusicalInstrument("Acoustic Guitar Steel", 25));
		instruments.add(new PickableMusicalInstrument("Electric Guitar", 26));
		instruments.add(new PickableMusicalInstrument("Electric Guitar Clean", 27));
		instruments.add(new PickableMusicalInstrument("Electric Guitar Muted", 28));
		instruments.add(new PickableMusicalInstrument("Overdriven Guitar", 29));
		instruments.add(new PickableMusicalInstrument("Distortion Guitar", 30));
		instruments.add(new PickableMusicalInstrument("Guitar Harmonics", 31));
		instruments.add(new PickableMusicalInstrument("Acoustic Bass", 32));
		instruments.add(new PickableMusicalInstrument("Electric Bass Finger", 33));
		instruments.add(new PickableMusicalInstrument("Electric Bass Pick", 34));
		instruments.add(new PickableMusicalInstrument("Fretless Bass", 35));
		instruments.add(new PickableMusicalInstrument("Slap Bass 1", 36));
		instruments.add(new PickableMusicalInstrument("Slap Bass 2", 37));
		instruments.add(new PickableMusicalInstrument("Synth Bass 1", 38));
		instruments.add(new PickableMusicalInstrument("Synth Bass 2", 39));
		instruments.add(new PickableMusicalInstrument("Contrabass", 43));
		instruments.add(new PickableMusicalInstrument("Tremolo Strings", 44));
		instruments.add(new PickableMusicalInstrument("Pizzicato", 45));
		instruments.add(new PickableMusicalInstrument("Orchestral Harp", 46));
		instruments.add(new PickableMusicalInstrument("Timpani", 47));
		instruments.add(new PickableMusicalInstrument("Strings", 48));
		instruments.add(new PickableMusicalInstrument("Slow Strings", 49));
		instruments.add(new PickableMusicalInstrument("Synth Strings 1", 50));
		instruments.add(new PickableMusicalInstrument("Synth Strings 2", 51));
		instruments.add(new PickableMusicalInstrument("Choir Aahs", 52));
		instruments.add(new PickableMusicalInstrument("Voice Oohs", 53));
		instruments.add(new PickableMusicalInstrument("Synth Voice", 54));
		instruments.add(new PickableMusicalInstrument("Orchestra Hit", 55));
		instruments.add(new PickableMusicalInstrument("Trumpet", 56));
		instruments.add(new PickableMusicalInstrument("Trombone", 57));
		instruments.add(new PickableMusicalInstrument("Tuba", 58));
		instruments.add(new PickableMusicalInstrument("French Horn", 60));
		instruments.add(new PickableMusicalInstrument("Brass Section", 61));
		instruments.add(new PickableMusicalInstrument("Synth Brass 1", 62));
		instruments.add(new PickableMusicalInstrument("Synth Brass 2", 63));
		instruments.add(new PickableMusicalInstrument("Soprano Sax", 64));
		instruments.add(new PickableMusicalInstrument("Alto Sax", 65));
		instruments.add(new PickableMusicalInstrument("Tenor Sax", 66));
		instruments.add(new PickableMusicalInstrument("Baritone Sax", 67));
		instruments.add(new PickableMusicalInstrument("Oboe", 68));
		instruments.add(new PickableMusicalInstrument("English Horn", 69));
		instruments.add(new PickableMusicalInstrument("Bassoon", 70));
		instruments.add(new PickableMusicalInstrument("Clarinet", 71));
		instruments.add(new PickableMusicalInstrument("Lead Square Wave", 80));
		instruments.add(new PickableMusicalInstrument("Lead Sawtooth Wave", 81));
		instruments.add(new PickableMusicalInstrument("Lead Calliope", 82));
		instruments.add(new PickableMusicalInstrument("Lead Chiffer", 83));
		instruments.add(new PickableMusicalInstrument("Lead Charang", 84));
		instruments.add(new PickableMusicalInstrument("Lead Voice Solo", 85));
		instruments.add(new PickableMusicalInstrument("Lead Fifths", 86));
		instruments.add(new PickableMusicalInstrument("Lead Bass and Lead", 87));
		instruments.add(new PickableMusicalInstrument("Pad New Age Fantasia", 88));
		instruments.add(new PickableMusicalInstrument("Pad Warm", 89));
		instruments.add(new PickableMusicalInstrument("Pad Polysinth", 90));
		instruments.add(new PickableMusicalInstrument("Pad Choir Space Voice", 91));
		instruments.add(new PickableMusicalInstrument("Pad Bowed Glass", 92));
		instruments.add(new PickableMusicalInstrument("Pad Metallic Pro", 93));
		instruments.add(new PickableMusicalInstrument("Pad Halo", 94));
		instruments.add(new PickableMusicalInstrument("Pad Sweep", 95));
		instruments.add(new PickableMusicalInstrument("FX Rain", 96));
		instruments.add(new PickableMusicalInstrument("FX Soundtrack", 98));
		instruments.add(new PickableMusicalInstrument("FX Crystal", 98));
		instruments.add(new PickableMusicalInstrument("FX Atmosphere", 99));
		instruments.add(new PickableMusicalInstrument("FX Brightness", 100));
		instruments.add(new PickableMusicalInstrument("FX Goblins", 101));
		instruments.add(new PickableMusicalInstrument("FX 7 Echoes, Drops", 102));
		instruments.add(new PickableMusicalInstrument("FX Sci-fi", 103));
		instruments.add(new PickableMusicalInstrument("Sitar", 104));
		instruments.add(new PickableMusicalInstrument("Banjo", 105));
		instruments.add(new PickableMusicalInstrument("Shamisen", 106));
		instruments.add(new PickableMusicalInstrument("Koto", 107));
		instruments.add(new PickableMusicalInstrument("Kalimba", 108));
		instruments.add(new PickableMusicalInstrument("Bagpipe", 109));
		instruments.add(new PickableMusicalInstrument("Shanai", 111));
		instruments.add(new PickableMusicalInstrument("Tinkle bell", 112));

		return instruments;
	}

	public static void fetchSpeechRecognitionSupportedLanguages(Context context) {
		if (!SPEECH_RECOGNITION_SUPPORTED_LANGUAGES.isEmpty()) {
			return;
		}

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
	}
}
