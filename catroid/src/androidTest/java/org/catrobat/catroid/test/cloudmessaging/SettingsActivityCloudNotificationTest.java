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

package org.catrobat.catroid.test.cloudmessaging;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.cloudmessaging.CloudMessaging;
import org.catrobat.catroid.ui.SettingsActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SettingsActivityCloudNotificationTest {
	@Before
	public void setUp() {
		CloudMessaging.setIsCloudNotificationsEnabled(true);
	}

	@Test
	public void testCloudNotificationsEnabling() {
		Context context = InstrumentationRegistry.getTargetContext();
		SettingsActivity.setCloudNotificationsEnabled(context, false);

		assertFalse(CloudMessaging.initialize(context));

		SettingsActivity.setCloudNotificationsEnabled(context, true);

		assertTrue(CloudMessaging.initialize(context));
	}

	@After
	public void tearDown() {
		CloudMessaging.setIsCloudNotificationsEnabled(false);
	}
}
