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
import org.catrobat.catroid.ui.SettingsActivity;

public final class CrashReporter {

	private static final String TAG = CrashReporter.class.getSimpleName();

	public static final String EXCEPTION_FOR_REPORT = "EXCEPTION_FOR_REPORT";

	protected static SharedPreferences preferences;
	private static boolean isCrashReportEnabled = BuildConfig.CRASHLYTICS_CRASH_REPORT_ENABLED;
	private static CrashReporterInterface reporter = new CrashlyticsCrashReporter();

	private CrashReporter() {
	}

	private static boolean isReportingEnabled() {
		return preferences != null && preferences.getBoolean(SettingsActivity.SETTINGS_CRASH_REPORTS, true)
				&& isCrashReportEnabled;
	}

	public static void initialize(Context context) {
		preferences = PreferenceManager.getDefaultSharedPreferences(context);

		if (isReportingEnabled()) {
			Log.d(TAG, "Initializing Crash Reporter");
			reporter.initialize(context);
		} else {
			Log.d(TAG, "Crash reporting is disabled. Skipping initializing");
		}
	}

	public static void logException(Throwable exception) {
		if (isReportingEnabled()) {
			reporter.logException(exception);
		}
	}

	public static void logUnhandledException() {
		if (isReportingEnabled() && hasStoredException()) {
			Log.d(TAG, "Reporting stored exception");
			logException(getStoredException());
			removeStoredException();
		}
	}

	public static void storeUnhandledException(Throwable exception) {
		if (isReportingEnabled() && !hasStoredException()) {
			Log.d(TAG, "Storing unhandled exception");
			storeException(exception);
		}
	}

	private static void storeException(Throwable exception) {
		preferences.edit()
				.putString(EXCEPTION_FOR_REPORT, serializeException(exception))
				.commit();
	}

	private static Throwable getStoredException() {
		return deserializeException(preferences.getString(EXCEPTION_FOR_REPORT, ""));
	}

	private static boolean hasStoredException() {
		return getStoredException() != null;
	}

	private static void removeStoredException() {
		preferences.edit()
				.remove(EXCEPTION_FOR_REPORT)
				.commit();
	}

	private static String serializeException(Throwable exception) {
		return new Gson().toJson(exception);
	}

	private static Throwable deserializeException(String exception) {
		return new Gson().fromJson(exception, Throwable.class);
	}

	@VisibleForTesting
	public static void setCrashReporterInterface(CrashReporterInterface crashReporterInterface) {
		reporter = crashReporterInterface;
	}

	@VisibleForTesting
	public static void setIsCrashReportEnabled(boolean isEnabled) {
		isCrashReportEnabled = isEnabled;
	}
}
