/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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

package org.catrobat.catroid.test.transfers

import android.content.SharedPreferences
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.io.ProjectAndSceneScreenshotLoader
import org.catrobat.catroid.io.ZipArchiver
import org.catrobat.catroid.transfers.project.ProjectUpload
import org.catrobat.catroid.transfers.project.ProjectUploadData
import org.catrobat.catroid.web.ServerCalls
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.verification.VerificationMode
import org.powermock.api.mockito.PowerMockito.mock
import org.powermock.core.classloader.annotations.PowerMockIgnore
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.io.File
import java.io.IOException
import java.util.Locale

private const val defaultToken = "TOKEN_1234"
private const val defaultUsername = "USER MC_USER"
private const val userEmail = "user@catrobat.com"
private const val projectName = "testproject"
private const val projectDescription = "testproject description"

@PowerMockIgnore("javax.net.ssl.*")
@RunWith(PowerMockRunner::class)
@PrepareForTest(ServerCalls::class)
class ProjectUploadTest {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPrefsEditor: SharedPreferences.Editor
    private lateinit var serverCalls: ServerCalls
    private lateinit var screenshotLoader: ProjectAndSceneScreenshotLoader
    private lateinit var projectDirectory: File
    private lateinit var projectDirectoryFiles: Array<File>
    private lateinit var projectDirectoryFilesFiltered: Array<File>
    private lateinit var archiveDirectory: File
    private lateinit var zipArchiver: ZipArchiver

    @Before
    fun setup() {
        sharedPreferences = mock(SharedPreferences::class.java)
        sharedPrefsEditor = mock(SharedPreferences.Editor::class.java)
        serverCalls = mock(ServerCalls::class.java)
        screenshotLoader = mock(ProjectAndSceneScreenshotLoader::class.java)
        projectDirectory = mock(File::class.java)
        val deviceVariableFile = mock(File::class.java)
        `when`(deviceVariableFile.getName()).thenReturn(Constants.DEVICE_VARIABLE_JSON_FILENAME)
        projectDirectoryFilesFiltered = arrayOf(mock(File::class.java), mock(File::class.java), mock(File::class.java))
        projectDirectoryFiles = projectDirectoryFilesFiltered + arrayOf(deviceVariableFile)
        archiveDirectory = mock(File::class.java)
        zipArchiver = mock(ZipArchiver::class.java)

        `when`(projectDirectory.listFiles()).thenReturn(projectDirectoryFiles)

        `when`(sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN)).thenReturn(defaultToken)
        `when`(sharedPreferences.getString(Constants.USERNAME, Constants.NO_USERNAME)).thenReturn(defaultUsername)
        `when`(sharedPreferences.edit()).thenReturn(sharedPrefsEditor)
    }

    @Test
    fun testProjectUploadDataPassedToServerCalls() {
        `when`(zipArchiver.zip(archiveDirectory, projectDirectoryFiles)).then {}

        `when`(
            serverCalls.uploadProject(
                any(ProjectUploadData::class.java),
                any(ServerCalls.UploadSuccessCallback::class.java),
                any(ServerCalls.UploadErrorCallback::class.java)
            )
        ).then {
            val projectUploadData = it.arguments[0] as? ProjectUploadData
            assertEquals(
                ProjectUploadData(
                    projectName = projectName,
                    projectDescription = projectDescription,
                    projectArchive = archiveDirectory,
                    userEmail = userEmail,
                    language = Locale.getDefault().language,
                    token = defaultToken,
                    username = defaultUsername
                ),
                projectUploadData
            )
        }

        createProjectUpload().start(
            successCallback = {},
            errorCallback = { _, _ -> }
        )

        didCallUploadProject()
    }

    @Test
    fun testProjectUploadSuccess() {
        val projectId = "1234"
        `when`(zipArchiver.zip(archiveDirectory, projectDirectoryFiles)).then {}
        `when`(sharedPrefsEditor.putString(ArgumentMatchers.eq(Constants.TOKEN), any()))
            .thenReturn(sharedPrefsEditor)
        `when`(sharedPrefsEditor.putString(ArgumentMatchers.eq(Constants.USERNAME), any()))
            .thenReturn(sharedPrefsEditor)

        `when`(
            serverCalls.uploadProject(
                any(ProjectUploadData::class.java),
                any(ServerCalls.UploadSuccessCallback::class.java),
                any(ServerCalls.UploadErrorCallback::class.java)
            )
        ).then {
            val successCallback = it.arguments[1] as? ServerCalls.UploadSuccessCallback
            successCallback?.onSuccess(projectId, "username", "token")
        }

        var returnedProjectId = "-1"
        createProjectUpload().start(
            successCallback = { returnedProjectId = it },
            errorCallback = { _, _ -> fail("Error callback must not be invoked in this test") }
        )

        assertEquals(projectId, returnedProjectId)
    }

    @Test
    fun testSetSharedPreferencesOnSuccess() {
        var token: String? = null
        var username: String? = null
        val callbackToken = "catroid_testtoken"
        val callbackUsername = "catroid_testuser"
        var successCallbackCalled = false

        `when`(sharedPrefsEditor.putString(ArgumentMatchers.eq(Constants.TOKEN), any()))
            .then {
                token = it.arguments[1] as? String
                return@then sharedPrefsEditor
            }

        `when`(sharedPrefsEditor.putString(ArgumentMatchers.eq(Constants.USERNAME), any()))
            .then {
                username = it.arguments[1] as? String
                return@then sharedPrefsEditor
            }

        `when`(
            serverCalls.uploadProject(
                any(ProjectUploadData::class.java),
                any(ServerCalls.UploadSuccessCallback::class.java),
                any(ServerCalls.UploadErrorCallback::class.java)
            )
        ).then {
            val successCallback = it.arguments[1] as? ServerCalls.UploadSuccessCallback
            successCallback?.onSuccess("123", callbackUsername, callbackToken)
        }

        createProjectUpload().start(
            successCallback = { successCallbackCalled = true },
            errorCallback = { _, _ -> fail("Error callback must not be invoked in this test") }
        )

        assertTrue(successCallbackCalled)
        assertEquals(callbackToken, token)
        assertEquals(callbackUsername, username)
    }

    @Test
    fun testProjectUploadError() {
        val errorCode = 32_202
        val errorMessage = "An error occured during the project Upload"
        var receivedErrorMessage = ""
        var receivedErrorCode = -1

        `when`(zipArchiver.zip(archiveDirectory, projectDirectoryFiles)).then {}

        `when`(
            serverCalls.uploadProject(
                any(ProjectUploadData::class.java),
                any(ServerCalls.UploadSuccessCallback::class.java),
                any(ServerCalls.UploadErrorCallback::class.java)
            )
        ).then {
            val errorCallback = it.arguments[2] as? ServerCalls.UploadErrorCallback
            errorCallback?.onError(errorCode, errorMessage)
        }

        createProjectUpload().start(
            successCallback = { fail("Success callback must not be invoked in this test") },
            errorCallback = { eCode, eMessage ->
                receivedErrorCode = eCode
                receivedErrorMessage = eMessage
            }
        )

        assertEquals(errorCode, receivedErrorCode)
        assertEquals(errorMessage, receivedErrorMessage)
    }

    @Test
    fun testNoUploadOnZipError() {
        var receivedErrorCode = -1
        var receivedErrorMessage = ""

        `when`(zipArchiver.zip(archiveDirectory, projectDirectoryFilesFiltered))
            .thenThrow(IOException("Failed to zip project"))

        createProjectUpload().start(
            successCallback = { fail("Success callback must not be invoked in this test") },
            errorCallback = { errorCode, errorMessage ->
                receivedErrorCode = errorCode
                receivedErrorMessage = errorMessage
            }
        )

        assertEquals(ProjectUpload.UPLOAD_ZIP_ERROR, receivedErrorCode)
        assertEquals(ProjectUpload.UPLOAD_ZIP_ERROR_MESSAGE, receivedErrorMessage)

        didCallUploadProject(never())
    }

    @Test
    fun testOneUploadCallPerUploadStart() {
        `when`(
            serverCalls.uploadProject(
                any(ProjectUploadData::class.java),
                any(ServerCalls.UploadSuccessCallback::class.java),
                any(ServerCalls.UploadErrorCallback::class.java)
            )
        ).then {}

        val upload = createProjectUpload()
        val timesUploadStartCalled = 3
        repeat(timesUploadStartCalled) { upload.start({}, { _, _ -> }) }
        didCallUploadProject(times(timesUploadStartCalled))
    }

    @Test
    fun testDeviceVariableFileRemoved() {
        createProjectUpload().start(
            successCallback = { },
            errorCallback = { _, _ -> }
        )
        verify(zipArchiver).zip(archiveDirectory, projectDirectoryFilesFiltered)
    }

    private fun createProjectUpload(): ProjectUpload {
        return ProjectUpload(
            projectDirectory = projectDirectory,
            projectName = projectName,
            projectDescription = projectDescription,
            userEmail = userEmail,
            sceneNames = arrayOf("scene1", "scene2", "scene3"),
            archiveDirectory = archiveDirectory,
            zipArchiver = zipArchiver,
            screenshotLoader = screenshotLoader,
            sharedPreferences = sharedPreferences,
            serverCalls = serverCalls
        )
    }

    private fun didCallUploadProject(mode: VerificationMode = times(1)) {
        verify(serverCalls, mode).uploadProject(
            any(ProjectUploadData::class.java),
            any(ServerCalls.UploadSuccessCallback::class.java),
            any(ServerCalls.UploadErrorCallback::class.java)
        )
    }
}
