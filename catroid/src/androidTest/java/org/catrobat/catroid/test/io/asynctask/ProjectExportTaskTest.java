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

package org.catrobat.catroid.test.io.asynctask;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.asynctask.ProjectExportTask;
import org.catrobat.catroid.io.asynctask.ProjectSaveTask;
import org.catrobat.catroid.utils.notifications.NotificationData;
import org.catrobat.catroid.utils.notifications.StatusBarNotificationManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.io.File;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import static org.catrobat.catroid.common.Constants.CATROBAT_EXTENSION;
import static org.catrobat.catroid.common.Constants.EXTERNAL_STORAGE_ROOT_EXPORT_DIRECTORY;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(AndroidJUnit4.class)
public class ProjectExportTaskTest {

	private Project project;
	private Context contextMock;

	@Rule
	public GrantPermissionRule runtimePermissionRule = GrantPermissionRule.grant(
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE);

	@Before
	public void setUp() {
		project = new Project(ApplicationProvider.getApplicationContext(),
				ProjectExportTaskTest.class.getSimpleName());

		ProjectManager.getInstance().setCurrentProject(project);
		ProjectSaveTask
				.task(project, ApplicationProvider.getApplicationContext());

		NotificationManager notificationManagerMock = Mockito.mock(NotificationManager.class);
		contextMock = Mockito.mock(Context.class);
		Mockito.when(contextMock.getResources()).thenReturn(ApplicationProvider.getApplicationContext().getResources());
		Mockito.when(contextMock.getSystemService(anyString())).thenReturn(notificationManagerMock);
	}

	@Test
	public void exportProjectTest() {
		StatusBarNotificationManager notificationManager = new StatusBarNotificationManager(contextMock);
		NotificationData notificationData = notificationManager
				.createSaveProjectToExternalMemoryNotification(ApplicationProvider.getApplicationContext(), project.getName());

		new ProjectExportTask(project.getDirectory(), notificationData,
				ApplicationProvider.getApplicationContext()).exportProjectToExternalStorage();

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
	}
}
