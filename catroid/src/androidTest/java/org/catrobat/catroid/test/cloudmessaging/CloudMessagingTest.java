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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.cloudmessaging.CloudMessaging;
import org.catrobat.catroid.cloudmessaging.CloudMessagingInterface;
import org.catrobat.catroid.cloudmessaging.FirebaseCloudMessaging;
import org.catrobat.catroid.ui.SettingsActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class CloudMessagingTest {

	private Context context;
	private SharedPreferences sharedPreferences;
	private CloudMessagingInterface messagingService;

	@Before
	public void setUp() {
		sharedPreferences = mock(SharedPreferences.class);
		context = mock(Context.class);
		messagingService = mock(FirebaseCloudMessaging.class);
		when(PreferenceManager.getDefaultSharedPreferences(context)).thenReturn(sharedPreferences);
		when(sharedPreferences.getBoolean(SettingsActivity.SETTINGS_CLOUD_NOTIFICATIONS, false)).thenReturn(true);
		CloudMessaging.setIsCloudNotificationsEnabled(true);
		CloudMessaging.setCloudMessagingInterface(messagingService);
	}

	@Test
	public void testCloudMessagingUninitializedOnNotificationsDisabled() {
		CloudMessaging.setIsCloudNotificationsEnabled(false);

		assertFalse(CloudMessaging.initialize(context));
	}

	@Test
	public void testUserSubscribedToTopicsOnCloudMessagingInitialized() {
		assertTrue(CloudMessaging.initialize(context));
		verify(messagingService, times(1)).subscribe();
	}

	@Test
	public void testUserUnsubscribedToTopicsOnCloudMessagingInitializationFailed() {
		when(sharedPreferences.getBoolean(SettingsActivity.SETTINGS_CLOUD_NOTIFICATIONS, false)).thenReturn(false);

		assertFalse(CloudMessaging.initialize(context));
		verify(messagingService, times(1)).unsubscribe();
	}

	@After
	public void tearDown() {
		CloudMessaging.setIsCloudNotificationsEnabled(false);
	}
}
