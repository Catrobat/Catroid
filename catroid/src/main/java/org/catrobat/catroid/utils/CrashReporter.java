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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.google.gson.Gson;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ui.BaseSettingsActivity;

public final class CrashReporter {

	private static final String TAG = CrashReporter.class.getSimpleName();

	public static final String EXCEPTION_FOR_REPORT = "EXCEPTION_FOR_REPORT";

	protected static SharedPreferences preferences;
	private static boolean isCrashReportEnabled = BuildConfig.CRASHLYTICS_CRASH_REPORT_ENABLED;
	private static CrashReporterInterface reporter = new CrashlyticsCrashReporter();

	private CrashReporter() {
	}

	private static boolean isReportingEnabled() {
		return preferences != null && preferences.getBoolean(BaseSettingsActivity.SETTINGS_CRASH_REPORTS, false) && isCrashReportEnabled;
	}

	@VisibleForTesting
	public static void setCrashReporterInterface(CrashReporterInterface crashReporterInterface) {
		reporter = crashReporterInterface;
	}

	public static boolean initialize(Context context) {

		preferences = PreferenceManager.getDefaultSharedPreferences(context);

		if (isReportingEnabled()) {
			reporter.initialize(context);
			Log.d(TAG, "INITIALIZED!");
			return true;
		}

		Log.d(TAG, "INITIALIZATION FAILED! [ Report : " + isReportingEnabled() + "]");
		return false;
	}

	public static boolean logException(Throwable exception) {

		if (isReportingEnabled()) {
			reporter.logException(exception);
			return true;
		}
		return false;
	}

	@VisibleForTesting
	public static void setIsCrashReportEnabled(boolean isEnabled) {
		isCrashReportEnabled = isEnabled;
	}

	public static void sendUnhandledCaughtException() {
		String json = preferences.getString(EXCEPTION_FOR_REPORT, "");
		if (isReportingEnabled() && !json.isEmpty()) {
			Log.d(TAG, "AFTER_EXCEPTION : sendCaughtException()");
			Gson gson = new Gson();
			Throwable exception = gson.fromJson(json, Throwable.class);
			logException(exception);
			preferences.edit().remove(EXCEPTION_FOR_REPORT).commit();
		}
	}

	public static void storeUnhandledException(Throwable exception) {
		SharedPreferences.Editor prefsEditor = preferences.edit();
		if (isReportingEnabled()) {
			Gson gson = new Gson();
			String check = preferences.getString(EXCEPTION_FOR_REPORT, "");
			if (check.isEmpty()) {
				String json = gson.toJson(exception);
				prefsEditor.putString(EXCEPTION_FOR_REPORT, json);
				prefsEditor.commit();
			}
		}
	}
}
