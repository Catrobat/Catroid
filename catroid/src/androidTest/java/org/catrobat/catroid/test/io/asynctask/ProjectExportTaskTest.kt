/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.catroid.test.io.asynctask

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.asynctask.ProjectExportTask
import org.catrobat.catroid.io.asynctask.ProjectExportTask.ProjectExportCallback
import org.catrobat.catroid.io.asynctask.saveProjectSerial
import org.catrobat.catroid.test.io.asynctask.ProjectExportTaskTest
import org.catrobat.catroid.utils.notifications.StatusBarNotificationManager
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import java.io.File
import java.io.IOException
import java.util.Enumeration
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

@RunWith(AndroidJUnit4::class)
class ProjectExportTaskTest {
    private var project: Project? = null
    private var contextMock: Context? = null
    private var projectZip: File? = null

    @get:Rule
    var runtimePermissionRule = GrantPermissionRule.grant(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @Before
    fun setUp() {
        project = Project(
            ApplicationProvider.getApplicationContext(),
            ProjectExportTaskTest::class.java.simpleName
        )
        ProjectManager.getInstance().currentProject = project
        saveProjectSerial(project, ApplicationProvider.getApplicationContext())
        val notificationManagerMock = Mockito.mock(
            NotificationManager::class.java
        )
        contextMock = Mockito.mock(Context::class.java)
        Mockito.`when`(contextMock?.getResources())
            .thenReturn(ApplicationProvider.getApplicationContext<Context>().resources)
        Mockito.`when`(contextMock?.getSystemService(ArgumentMatchers.anyString()))
            .thenReturn(notificationManagerMock)
    }

    @Test
    fun exportProjectTest() {
        createUndoCodeXmlFile()
        val notificationManager = StatusBarNotificationManager(contextMock)
        val fileName = project!!.directory.name + "_destination" + Constants.CATROBAT_EXTENSION
        projectZip = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            File(Constants.CACHE_DIRECTORY, fileName)
        } else {
            File(Constants.DOWNLOAD_DIRECTORY, fileName)
        }
        val projectUri = Uri.fromFile(projectZip)
        val notificationData = notificationManager
            .createSaveProjectToExternalMemoryNotification(
                ApplicationProvider.getApplicationContext(),
                projectUri, project!!.name
            )
        val task = ProjectExportTask(
            project!!.directory, projectUri, notificationData,
            ApplicationProvider.getApplicationContext()
        )
        task.registerCallback(object : ProjectExportCallback {
            override fun onProjectExportFinished() {
                Assert.assertTrue(projectZip!!.exists())
                checkUndoCodeXmlFileIsDeleted(projectZip!!)
            }
        })
        task.exportProjectToExternalStorage()
    }

    private fun createUndoCodeXmlFile() {
        val currentCodeFile = File(project!!.directory, Constants.CODE_XML_FILE_NAME)
        val undoCodeFile = File(project!!.directory, Constants.UNDO_CODE_XML_FILE_NAME)
        try {
            StorageOperations.transferData(currentCodeFile, undoCodeFile)
        } catch (exception: IOException) {
            Log.e(TAG, "Copying project " + project!!.name + " failed.", exception)
        }
    }

    private fun checkUndoCodeXmlFileIsDeleted(externalProjectZip: File) {
        val zipFileName =
            externalProjectZip.absolutePath.replace(Constants.CATROBAT_EXTENSION, ".zip")
        externalProjectZip.renameTo(File(zipFileName))
        try {
            val zipFile = ZipFile(zipFileName)
            val zipEntries: Enumeration<*> = zipFile.entries()
            var fileName: String?
            while (zipEntries.hasMoreElements()) {
                fileName = (zipEntries.nextElement() as ZipEntry).name
                Assert.assertNotEquals(Constants.UNDO_CODE_XML_FILE_NAME, fileName)
            }
        } catch (exception: IOException) {
            Log.e(TAG, "Creating zip folder failed.", exception)
        }
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        if (project!!.directory.isDirectory) {
            StorageOperations.deleteDir(project!!.directory)
        }
        projectZip!!.delete()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
            && Constants.DOWNLOAD_DIRECTORY.exists()
        ) {
            StorageOperations.deleteDir(Constants.DOWNLOAD_DIRECTORY)
        }
    }

    companion object {
        val TAG = ProjectExportTaskTest::class.java.simpleName
    }
}
