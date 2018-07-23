/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.util.SparseArray;

import org.catrobat.catroid.R;
import org.catrobat.catroid.utils.NotificationData;
import org.catrobat.catroid.utils.StatusBarNotificationManager;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class StatusBarNotificationManagerTest {
	private static final String TAG = StatusBarNotificationManagerTest.class.getSimpleName();

	private final StatusBarNotificationManager notificationManager = StatusBarNotificationManager.getInstance();

	@After
	public void tearDown() throws Exception {
		TestUtils.cancelAllNotifications(InstrumentationRegistry.getTargetContext());
	}

	@Test
	public void testCreateCopyNotification() {
		int id = notificationManager.createCopyNotification(InstrumentationRegistry.getTargetContext(), TestUtils
				.DEFAULT_TEST_PROJECT_NAME);
		checkNotificationData(id);

		assertEquals(-1, notificationManager.createCopyNotification(null, TestUtils.DEFAULT_TEST_PROJECT_NAME));
		assertEquals(-1, notificationManager.createCopyNotification(InstrumentationRegistry.getTargetContext(), null));
		assertEquals(-1, notificationManager.createCopyNotification(null, null));
	}

	@Test
	public void testCreateDownloadNotification() {
		int id = notificationManager.createDownloadNotification(InstrumentationRegistry.getTargetContext(), TestUtils
				.DEFAULT_TEST_PROJECT_NAME);
		checkNotificationData(id);

		assertEquals(-1, notificationManager.createDownloadNotification(null, TestUtils.DEFAULT_TEST_PROJECT_NAME));
		assertEquals(-1, notificationManager.createDownloadNotification(InstrumentationRegistry.getTargetContext(), null));
		assertEquals(-1, notificationManager.createDownloadNotification(null, null));
	}

	@Test
	public void testCreateUploadNotification() {
		int id = notificationManager.createUploadNotification(InstrumentationRegistry.getTargetContext(), TestUtils
				.DEFAULT_TEST_PROJECT_NAME);
		checkNotificationData(id);

		assertEquals(-1, notificationManager.createUploadNotification(null, TestUtils.DEFAULT_TEST_PROJECT_NAME));
		assertEquals(-1, notificationManager.createUploadNotification(InstrumentationRegistry.getTargetContext(),
				null));
		assertEquals(-1, notificationManager.createUploadNotification(null, null));
	}

	@Test
	public void testShowOrUpdateNotification() {
		try {
			notificationManager.showOrUpdateNotification(-1, 0);
		} catch (Exception ex) {
			Log.e(TAG, "showOrUpdateNotification() failed", ex);
			fail("there shouldn't be any exception thrown");
		}
	}

	@Test
	public void testUploadRejectedNotification() {
		int id = notificationManager.createUploadNotification(InstrumentationRegistry.getTargetContext(), TestUtils
				.DEFAULT_TEST_PROJECT_NAME);
		checkNotificationData(id);
		notificationManager.showUploadRejectedNotification(id, 507, InstrumentationRegistry.getTargetContext()
				.getResources().getString(R.string.error_project_upload), new Bundle());
		@SuppressWarnings("unchecked")
		SparseArray<NotificationData> notificationDataMap = (SparseArray<NotificationData>) Reflection.getPrivateField(
				StatusBarNotificationManager.class, notificationManager, "notificationDataMap");

		NotificationData data = notificationDataMap.get(id);
		assertEquals(data.getNotificationTextDone(), InstrumentationRegistry.getTargetContext().getResources()
				.getString(R.string.error_project_upload));
	}

	private void checkNotificationData(int id) {
		assertTrue(id >= 0);

		@SuppressWarnings("unchecked")
		SparseArray<NotificationData> notificationDataMap = (SparseArray<NotificationData>) Reflection.getPrivateField(
				StatusBarNotificationManager.class, notificationManager, "notificationDataMap");

		NotificationData data = notificationDataMap.get(id);
		assertNotNull(data);
		assertNotNull(data.getPendingIntent());
		assertNotNull(data.getNotificationBuilder());
		assertEquals(TestUtils.DEFAULT_TEST_PROJECT_NAME, data.getProgramName());
	}
}
