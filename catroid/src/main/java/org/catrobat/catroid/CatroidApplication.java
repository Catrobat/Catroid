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
package org.catrobat.catroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.parrot.freeflight.settings.ApplicationSettings;

import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.utils.CrashReporter;

import java.util.Arrays;
import java.util.Locale;

import static org.catrobat.catroid.common.Constants.DEVICE_LANGUAGE;
import static org.catrobat.catroid.common.Constants.LANGUAGE_CODE;
import static org.catrobat.catroid.common.Constants.LANGUAGE_TAG_KEY;

public class CatroidApplication extends MultiDexApplication {

	private static final String TAG = CatroidApplication.class.getSimpleName();

	private ApplicationSettings settings;
	private static Context context;

	public static final String OS_ARCH = System.getProperty("os.arch");
	public static boolean parrotLibrariesLoaded = false;
	public static String defaultSystemLanguage;
	public static boolean parrotJSLibrariesLoaded = false;

	@Override
	public void onCreate() {
		super.onCreate();
		CrashReporter.initialize(this);
		Log.d(TAG, "CatroidApplication onCreate");
		settings = new ApplicationSettings(this);
		CatroidApplication.context = getApplicationContext();
		setAppLanguage();
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	public ApplicationSettings getParrotApplicationSettings() {
		return settings;
	}

	public static synchronized boolean loadNativeLibs() {
		if (parrotLibrariesLoaded) {
			return true;
		}

		try {
			System.loadLibrary("avutil");
			System.loadLibrary("swscale");
			System.loadLibrary("avcodec");
			System.loadLibrary("avfilter");
			System.loadLibrary("avformat");
			System.loadLibrary("avdevice");
			System.loadLibrary("adfreeflight");
			parrotLibrariesLoaded = true;
		} catch (UnsatisfiedLinkError e) {
			Log.e(TAG, Log.getStackTraceString(e));
			parrotLibrariesLoaded = false;
		}
		return parrotLibrariesLoaded;
	}

	private void setAppLanguage() {
		defaultSystemLanguage = Locale.getDefault().getLanguage();
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String languageTag = sharedPreferences.getString(LANGUAGE_TAG_KEY, "");

		if (languageTag.equals(DEVICE_LANGUAGE)) {
			SettingsActivity.updateLocale(getAppContext(), defaultSystemLanguage, "");
		} else if (Arrays.asList(LANGUAGE_CODE).contains(languageTag) && languageTag.length() == 2) {
			SettingsActivity.updateLocale(getAppContext(), languageTag, "");
		} else if (Arrays.asList(LANGUAGE_CODE).contains(languageTag) && languageTag.length() == 6) {
			String language = languageTag.substring(0, 2);
			String country = languageTag.substring(4);
			SettingsActivity.updateLocale(getAppContext(), language, country);
		}
	}

	public static synchronized boolean loadSDKLib() {
		if (parrotJSLibrariesLoaded) {
			return true;
		}

		try {
			System.loadLibrary("curl");
			System.loadLibrary("json-c");
			System.loadLibrary("arsal");
			System.loadLibrary("arsal_android");
			System.loadLibrary("arnetworkal");
			System.loadLibrary("arnetworkal_android");
			System.loadLibrary("arnetwork");
			System.loadLibrary("arnetwork_android");
			System.loadLibrary("arcommands");
			System.loadLibrary("arcommands_android");
			System.loadLibrary("arstream");
			System.loadLibrary("arstream_android");
			System.loadLibrary("arstream2");
			System.loadLibrary("arstream2_android");
			System.loadLibrary("ardiscovery");
			System.loadLibrary("ardiscovery_android");
			System.loadLibrary("arutils");
			System.loadLibrary("arutils_android");
			System.loadLibrary("ardatatransfer");
			System.loadLibrary("ardatatransfer_android");
			System.loadLibrary("armedia");
			System.loadLibrary("armedia_android");
			System.loadLibrary("arupdater");
			System.loadLibrary("arupdater_android");
			System.loadLibrary("armavlink");
			System.loadLibrary("armavlink_android");
			System.loadLibrary("arcontroller");
			System.loadLibrary("arcontroller_android");
			parrotJSLibrariesLoaded = true;
		} catch (UnsatisfiedLinkError e) {
			Log.e(TAG, Log.getStackTraceString(e));
			parrotJSLibrariesLoaded = false;
		}
		return parrotJSLibrariesLoaded;
	}

	public static Context getAppContext() {
		return CatroidApplication.context;
	}
}
