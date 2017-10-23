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

package org.catrobat.catroid.test.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;

import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.utils.CrashReporter;
import org.catrobat.catroid.utils.CrashReporterInterface;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CrashReporterTest {

	private Context context;
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;
	private Exception exception;

	@Mock
	CrashReporterInterface reporter;

	@Before
	public void setUp() {
		context = InstrumentationRegistry.getTargetContext();
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		editor = sharedPreferences.edit();
		editor.clear();
		editor.putBoolean(SettingsActivity.SETTINGS_CRASH_REPORTS, true);
		editor.commit();
		exception = new RuntimeException("Error");
		CrashReporter.setIsCrashReportEnabled(true);
		CrashReporter.setCrashReporterInterface(reporter);
	}

	@After
	public void tearDown() {
		CrashReporter.setIsCrashReportEnabled(false);
	}

	@Test
	public void testCrashlyticsUninitializedOnAnonymousReportDisabled() {
		editor.putBoolean(SettingsActivity.SETTINGS_CRASH_REPORTS, false);
		editor.commit();

		CrashReporter.initialize(context);

		verify(reporter, times(0)).initialize(context);
	}

	@Test
	public void testCrashlyticsInitializedOnAnonymousReportEnabled() {
		CrashReporter.initialize(context);
		verify(reporter, times(1)).initialize(context);
	}

	@Test
	public void testCrashlyticsUninitializedOnCrashReportDisabled() {
		CrashReporter.setIsCrashReportEnabled(false);

		CrashReporter.initialize(context);

		verify(reporter, times(0)).initialize(context);
	}

	@Test
	public void testUnhandledExceptionStoredOnCrashReportEnabled() {
		CrashReporter.storeUnhandledException(exception);

		assertFalse(sharedPreferences.getString(CrashReporter.EXCEPTION_FOR_REPORT, "").isEmpty());
	}

	@Test
	public void testUnhandledExceptionNotStoredOnCrashReportDisabled() {
		CrashReporter.setIsCrashReportEnabled(false);
		CrashReporter.storeUnhandledException(exception);

		assertTrue(sharedPreferences.getString(CrashReporter.EXCEPTION_FOR_REPORT, "").isEmpty());
	}

	@Test
	public void testUnhandledExceptionStoredOnNoPreviousExceptionStored() {
		assertTrue(sharedPreferences.getString(CrashReporter.EXCEPTION_FOR_REPORT, "").isEmpty());

		CrashReporter.storeUnhandledException(exception);

		assertFalse(sharedPreferences.getString(CrashReporter.EXCEPTION_FOR_REPORT, "").isEmpty());
	}

	@Test
	public void testUnhandledExceptionNotStoredOnPreviousExceptionStored() {
		assertTrue(sharedPreferences.getString(CrashReporter.EXCEPTION_FOR_REPORT, "").isEmpty());

		exception = new RuntimeException("Error 1");
		CrashReporter.storeUnhandledException(exception);
		assertFalse(sharedPreferences.getString(CrashReporter.EXCEPTION_FOR_REPORT, "").isEmpty());

		String error1Data = sharedPreferences.getString(CrashReporter.EXCEPTION_FOR_REPORT, "");

		exception = new RuntimeException("Error 2");
		CrashReporter.storeUnhandledException(exception);
		assertFalse(sharedPreferences.getString(CrashReporter.EXCEPTION_FOR_REPORT, "").isEmpty());

		assertTrue(sharedPreferences.getString(CrashReporter.EXCEPTION_FOR_REPORT, "").equals(error1Data));
	}

	@Test
	public void testSharedPreferencesClearedAfterLoggingException() {
		CrashReporter.storeUnhandledException(exception);

		assertFalse(sharedPreferences.getString(CrashReporter.EXCEPTION_FOR_REPORT, "").isEmpty());

		CrashReporter.sendUnhandledCaughtException();

		assertTrue(sharedPreferences.getString(CrashReporter.EXCEPTION_FOR_REPORT, "").isEmpty());
	}

	@Test
	public void testSharedPreferencesClearedOnLoggingFailed() {
		CrashReporter.storeUnhandledException(exception);

		assertFalse(sharedPreferences.getString(CrashReporter.EXCEPTION_FOR_REPORT, "").isEmpty());

		CrashReporter.sendUnhandledCaughtException();

		assertTrue(sharedPreferences.getString(CrashReporter.EXCEPTION_FOR_REPORT, "").isEmpty());
	}

	@Test
	public void testUnhandledCaughtExceptionSentOnCrashReportEnabled() {
		CrashReporter.storeUnhandledException(exception);

		CrashReporter.sendUnhandledCaughtException();

		verify(reporter, times(1)).logException(any(Throwable.class));
	}

	@Test
	public void testUnhandledCaughtExceptionSentOnCrashReportDisabled() {
		CrashReporter.storeUnhandledException(exception);
		editor.putBoolean(SettingsActivity.SETTINGS_CRASH_REPORTS, false);
		editor.commit();

		CrashReporter.sendUnhandledCaughtException();

		verify(reporter, times(0)).logException(any(Exception.class));
	}

	@Test
	public void testLogExceptionGenerateLogsOnReportsEnabled() {
		CrashReporter.logException(exception);

		verify(reporter, times(1)).logException(exception);
	}

	@Test
	public void testLogExceptionGenerateNoLogsOnReportsDisabled() {
		editor.putBoolean(SettingsActivity.SETTINGS_CRASH_REPORTS, false);
		editor.commit();

		CrashReporter.setIsCrashReportEnabled(false);
		CrashReporter.logException(exception);

		verify(reporter, times(0)).logException(exception);
	}
}
