/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.retrofittesting

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.asynctask.saveProjectSerial
import org.catrobat.catroid.retrofit.WebService
import org.catrobat.catroid.retrofit.models.RegisterUser
import org.catrobat.catroid.testsuites.annotations.Cat.OutgoingNetworkTests
import org.catrobat.catroid.utils.ProjectZipper
import org.catrobat.catroid.utils.Utils
import org.catrobat.catroid.web.ServerAuthenticationConstants
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import java.io.File
import java.net.HttpURLConnection

@RunWith(MockitoJUnitRunner::class)
@Category(OutgoingNetworkTests::class)
class CatroidWebServerProjectUploadTest : KoinTest {

    companion object {
        private val TAG = CatroidWebServerProjectUploadTest::class.java.simpleName
        private val PROJECT_NAME: String = CatroidWebServerProjectUploadTest::class.java.simpleName
        private val PASSWORD = "sEcR3tPassw0rD"
    }

    val projectManager: ProjectManager by KoinJavaComponent.inject(ProjectManager::class.java)
    private var project: Project? = null

    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var newEmail: String
    private lateinit var newUserName: String

    private val webServer: WebService by inject()

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().context
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        MockitoAnnotations.initMocks(this)
        project = Project(
            ApplicationProvider.getApplicationContext(),
            PROJECT_NAME
        )
        saveProjectSerial(project, ApplicationProvider.getApplicationContext())
        projectManager.currentProject = project

        // Register Test User
        newUserName = "APIUser" + System.currentTimeMillis()
        newEmail = "$newUserName@api.at"

        val response =
            webServer.register("", RegisterUser(true, newEmail, newUserName, PASSWORD)).execute()
        val responseBody = response.body()
        assertNotNull(responseBody)
        assertNotNull(responseBody!!.token)

        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putString(Constants.TOKEN, responseBody.token)
        sharedPreferencesEditor.apply()
        Log.d(TAG, "Login Token Set")

        // Start Mock Server
    }

    @After
    fun tearDown() {
        deleteUser()
        StorageOperations.deleteDir(File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, PROJECT_NAME))
    }

    @Test
    @Throws(Exception::class)
    fun testUploadProjectAndCheckResponseCode201Returned() {
        assertNotNull(project)

        val projectZip = ProjectZipper.zipProjectToArchive(
            File(project?.directory!!.absolutePath),
            File(Constants.CACHE_DIR, "upload${Constants.CATROBAT_EXTENSION}")
        )

        assertNotNull(projectZip!!)

        val checksum = Utils.md5Checksum(projectZip)

        val map: HashMap<String, RequestBody> = HashMap()
        map["checksum"] = RequestBody.create(MultipartBody.FORM, checksum)

        val requestBody = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            projectZip
        )
        val body = MultipartBody.Part.createFormData("file", projectZip.name, requestBody)

        val token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN)
        val response = webServer.uploadProject("Bearer $token", map, body).execute()

        assertEquals(
            response.code(),
            HttpURLConnection.HTTP_CREATED
        )
    }

    @Test
    @Throws(Exception::class)
    fun testUploadProjectReturnsProjectName() {
        assertNotNull(project)

        val projectZip = ProjectZipper.zipProjectToArchive(
            File(project?.directory!!.absolutePath),
            File(Constants.CACHE_DIR, "upload${Constants.CATROBAT_EXTENSION}")
        )

        assertNotNull(projectZip!!)

        val checksum = Utils.md5Checksum(projectZip)

        val map: HashMap<String, RequestBody> = HashMap()
        map["checksum"] = RequestBody.create(MultipartBody.FORM, checksum)

        val requestBody = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            projectZip
        )
        val body = MultipartBody.Part.createFormData("file", projectZip.name, requestBody)

        val token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN)
        val response = webServer.uploadProject("Bearer $token", map, body).execute().body()

        assertNotNull(response!!)

        Log.d(TAG, "Response project name is " + response.name)

        assertEquals(
            response.name,
            PROJECT_NAME
        )
    }

    private fun deleteUser() {
        val token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN)
        val response = webServer.deleteUser("Bearer $token").execute()
        assertEquals(response.code(), ServerAuthenticationConstants.SERVER_RESPONSE_USER_DELETED)
        Log.d(TAG, "Deleted test user")
    }
}
