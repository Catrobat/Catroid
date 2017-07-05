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
import android.test.AndroidTestCase;

import org.catrobat.catroid.ui.BaseExceptionHandler;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.utils.CrashReporter;
import org.junit.Before;

import static org.catrobat.catroid.utils.CrashReporter.EXCEPTION_FOR_REPORT;

public class BaseExceptionHandlerTest extends AndroidTestCase {

	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		Context context = InstrumentationRegistry.getTargetContext();
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		editor = sharedPreferences.edit();
		editor.clear();
		editor.putBoolean(SettingsActivity.SETTINGS_CRASH_REPORTS, true);
		editor.commit();
		CrashReporter.setIsCrashReportEnabled(true);
		CrashReporter.initialize(context);
	}

	@Override
	protected void tearDown() throws Exception {
		CrashReporter.setIsCrashReportEnabled(false);
		editor.clear();
		editor.commit();
		super.tearDown();
	}

	public void testExceptionStoredOnCallingUncaughtException() {
		assertTrue(sharedPreferences.getString(EXCEPTION_FOR_REPORT, "").isEmpty());

		BaseExceptionHandler baseExceptionHandler = new BaseExceptionHandler() {
			@Override
			protected void exit() {
			}
		};

		baseExceptionHandler.uncaughtException(null, new RuntimeException("Test Error"));

		assertFalse(sharedPreferences.getString(EXCEPTION_FOR_REPORT, "").isEmpty());
	}
}
