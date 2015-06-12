/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

import android.test.AndroidTestCase;
import android.util.SparseArray;

import org.catrobat.catroid.utils.NotificationData;
import org.catrobat.catroid.utils.StatusBarNotificationManager;

public class StatusBarNotificationManagerTest extends AndroidTestCase {

	private final StatusBarNotificationManager notificationManager = StatusBarNotificationManager.getInstance();

	@Override
	protected void tearDown() throws Exception {
		TestUtils.cancelAllNotifications(getContext());
		super.tearDown();
	}

	public void testCreateCopyNotification() {
		int id = notificationManager.createCopyNotification(getContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);
		checkNotificationData(id);

		assertEquals("-1 as return parameter expected", -1,
				notificationManager.createCopyNotification(null, TestUtils.DEFAULT_TEST_PROJECT_NAME));
		assertEquals("-1 as return parameter expected", -1,
				notificationManager.createCopyNotification(getContext(), null));
		assertEquals("-1 as return parameter expected", -1, notificationManager.createCopyNotification(null, null));
	}

	public void testCreateDownloadNotification() {
		int id = notificationManager.createDownloadNotification(getContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);
		checkNotificationData(id);

		assertEquals("-1 as return parameter expected", -1,
				notificationManager.createDownloadNotification(null, TestUtils.DEFAULT_TEST_PROJECT_NAME));
		assertEquals("-1 as return parameter expected", -1,
				notificationManager.createDownloadNotification(getContext(), null));
		assertEquals("-1 as return parameter expected", -1, notificationManager.createDownloadNotification(null, null));
	}

	public void testCreateUploadNotification() {
		int id = notificationManager.createUploadNotification(getContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);
		checkNotificationData(id);

		assertEquals("-1 as return parameter expected", -1,
				notificationManager.createUploadNotification(null, TestUtils.DEFAULT_TEST_PROJECT_NAME));
		assertEquals("-1 as return parameter expected", -1,
				notificationManager.createUploadNotification(getContext(), null));
		assertEquals("-1 as return parameter expected", -1, notificationManager.createUploadNotification(null, null));
	}

	public void testShowOrUpdateNotification() {
		try {
			notificationManager.showOrUpdateNotification(-1, 0);
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("there shouldn't be any exception thrown");
		}
	}

	private void checkNotificationData(int id) {
		assertTrue("id must not me negative", id >= 0);

		@SuppressWarnings("unchecked")
		SparseArray<NotificationData> notificationDataMap = (SparseArray<NotificationData>) Reflection.getPrivateField(
				StatusBarNotificationManager.class, notificationManager, "notificationDataMap");

		NotificationData data = notificationDataMap.get(id);
		assertNotNull("there should be an entry in the sparse array", data);
		assertNotNull("there should be a pending intent defined", data.getPendingIntent());
		assertNotNull("there should be a builder defined", data.getNotificationBuilder());
		assertEquals("program names should match", TestUtils.DEFAULT_TEST_PROJECT_NAME, data.getProgramName());
	}
}
