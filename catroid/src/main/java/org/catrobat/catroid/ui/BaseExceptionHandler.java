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

package org.catrobat.catroid.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class BaseExceptionHandler implements
		java.lang.Thread.UncaughtExceptionHandler {

	private static final String TAG = BaseExceptionHandler.class.getSimpleName();

	public static final int EXIT_CODE = 10;
	public static final String RECOVERED_FROM_CRASH = "RECOVERED_FROM_CRASH";

	private final SharedPreferences preferences;

	public BaseExceptionHandler(Activity context) {
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public void uncaughtException(Thread thread, Throwable exception) {
		Log.e(TAG, "unhandled exception", exception);
		preferences.edit().putBoolean(RECOVERED_FROM_CRASH, true).commit();
		System.exit(EXIT_CODE);
		android.os.Process.killProcess(android.os.Process.myPid());
	}
}
