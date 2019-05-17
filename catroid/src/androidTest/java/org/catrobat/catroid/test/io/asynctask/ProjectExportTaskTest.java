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

package org.catrobat.catroid.test.io.asynctask;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.SparseArray;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.asynctask.ProjectExportTask;
import org.catrobat.catroid.io.asynctask.ProjectSaveTask;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.utils.notifications.NotificationData;
import org.catrobat.catroid.utils.notifications.StatusBarNotificationManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.catrobat.catroid.common.Constants.CATROBAT_EXTENSION;
import static org.catrobat.catroid.common.Constants.EXTERNAL_STORAGE_ROOT_EXPORT_DIRECTORY;

@RunWith(AndroidJUnit4.class)
public class ProjectExportTaskTest {

	private Project project;

	@Rule
	public GrantPermissionRule runtimePermissionRule = GrantPermissionRule.grant(
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE);

	@Before
	public void setUp() {
		project = new Project(InstrumentationRegistry.getTargetContext(),
				ProjectExportTaskTest.class.getSimpleName());

		ProjectManager.getInstance().setCurrentProject(project);
		ProjectSaveTask
				.task(project, InstrumentationRegistry.getTargetContext());
	}

	@Test
	public void exportProjectTest() {
		StatusBarNotificationManager notificationManager = StatusBarNotificationManager.getInstance();
		int notificationId = notificationManager
				.createSaveProjectToExternalMemoryNotification(InstrumentationRegistry.getTargetContext(), project.getName());
		ProjectExportTask
				.task(project.getDirectory(), notificationId);

		File externalProjectZip = new File(EXTERNAL_STORAGE_ROOT_EXPORT_DIRECTORY,
				project.getDirectory().getName() + CATROBAT_EXTENSION);
		Assert.assertTrue(externalProjectZip.exists());
	}

	@After
	public void tearDown() throws Exception {
		if (project.getDirectory().isDirectory()) {
			StorageOperations.deleteDir(project.getDirectory());
		}
		if (EXTERNAL_STORAGE_ROOT_EXPORT_DIRECTORY.exists()) {
			StorageOperations.deleteDir(EXTERNAL_STORAGE_ROOT_EXPORT_DIRECTORY);
		}
		cancelAllNotifications(InstrumentationRegistry.getTargetContext());
	}

	private void cancelAllNotifications(Context context) throws Exception {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		@SuppressWarnings("unchecked")
		SparseArray<NotificationData> notificationMap = (SparseArray<NotificationData>) Reflection.getPrivateField(
				StatusBarNotificationManager.class, StatusBarNotificationManager.getInstance(), "notificationDataMap");
		if (notificationMap == null) {
			return;
		}

		for (int i = 0; i < notificationMap.size(); i++) {
			notificationManager.cancel(notificationMap.keyAt(i));
		}
		notificationMap.clear();
	}
}
