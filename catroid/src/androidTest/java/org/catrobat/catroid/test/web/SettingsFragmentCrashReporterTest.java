/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

package org.catrobat.catroid.test.web;

import android.content.Context;

import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.catrobat.catroid.utils.CrashReporter;
import org.catrobat.catroid.utils.CrashReporterInterface;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import androidx.test.core.app.ApplicationProvider;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SettingsFragmentCrashReporterTest {

	@Mock
	CrashReporterInterface reporter;

	@Before
	public void setUp() throws Exception {
		CrashReporter.setIsCrashReportEnabled(true);
		CrashReporter.setCrashReporterInterface(reporter);
	}

	@Test
	public void testAutoCrashReportEnabling() {
		Context context = ApplicationProvider.getApplicationContext();
		SettingsFragment.setAutoCrashReportingEnabled(context, false);

		verify(reporter, times(0)).initialize(context);

		SettingsFragment.setAutoCrashReportingEnabled(context, true);

		verify(reporter, times(1)).initialize(context);
	}

	@After
	public void tearDown() throws Exception {
		CrashReporter.setIsCrashReportEnabled(false);
	}
}
