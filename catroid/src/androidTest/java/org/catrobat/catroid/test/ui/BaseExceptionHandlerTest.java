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

package org.catrobat.catroid.test.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ui.BaseExceptionHandler;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.utils.CrashReporter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.ui.BaseActivity.RECOVERED_FROM_CRASH;
import static org.catrobat.catroid.utils.CrashReporter.EXCEPTION_FOR_REPORT;

@RunWith(AndroidJUnit4.class)
public class BaseExceptionHandlerTest {

	private Context context;
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;

	@Before
	public void setUp() throws Exception {
		context = InstrumentationRegistry.getTargetContext();
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		editor = sharedPreferences.edit();
		editor.clear();
		editor.putBoolean(SettingsActivity.SETTINGS_CRASH_REPORTS, true);
		editor.commit();
		CrashReporter.setIsCrashReportEnabled(true);
		CrashReporter.initialize(context);
	}

	@After
	public void tearDown() throws Exception {
		CrashReporter.setIsCrashReportEnabled(false);
		editor.clear();
		editor.commit();
	}

	@Test
	public void testExceptionStoredOnCallingUncaughtException() {
		assertTrue(sharedPreferences.getString(EXCEPTION_FOR_REPORT, "").isEmpty());

		BaseExceptionHandler baseExceptionHandler = new BaseExceptionHandler(context) {
			@Override
			protected void exit() {
			}
		};

		baseExceptionHandler.uncaughtException(null, new RuntimeException("Test Error"));

		assertFalse(sharedPreferences.getString(EXCEPTION_FOR_REPORT, "").isEmpty());
	}

	@Test
	public void testSetRecoveredFromCrashFlag() {
		assertFalse(sharedPreferences.getBoolean(RECOVERED_FROM_CRASH, false));

		BaseExceptionHandler baseExceptionHandler = new BaseExceptionHandler(context) {
			@Override
			protected void exit() {
			}
		};
		baseExceptionHandler.uncaughtException(null, new RuntimeException("Test Error"));

		assertTrue(sharedPreferences.getBoolean(RECOVERED_FROM_CRASH, false));
	}
}
