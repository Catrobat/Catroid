/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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
import android.util.Patterns
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.retrofit.WebService
import org.catrobat.catroid.retrofit.models.getCardUrl
import org.catrobat.catroid.retrofit.models.getDetailUrl
import org.catrobat.catroid.testsuites.annotations.Cat.OutgoingNetworkTests
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import java.net.HttpURLConnection

@RunWith(MockitoJUnitRunner::class)
@Category(OutgoingNetworkTests::class)
class CatroidWebServerTest : KoinTest {

    companion object {
        private const val SUCCESS_RESPONSE_FILE_NAME = "featured_projects_success_response.json"
    }

    private lateinit var mockWebServer: MockWebServer
    private lateinit var context: Context

    private val webServer: WebService by inject()

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().context
        MockitoAnnotations.initMocks(this)
        mockWebServer = MockWebServer()
        mockWebServer.start()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testReadSampleSuccessJsonFile() {
        val reader = MockResponseFileReader(context, SUCCESS_RESPONSE_FILE_NAME)
        assertNotNull(reader.content)
    }

    @Test
    @Throws(Exception::class)
    fun testFetchFeaturedProjectsAndCheckResponseCode200Returned() {
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(MockResponseFileReader(context, SUCCESS_RESPONSE_FILE_NAME).content)
        mockWebServer.enqueue(response)

        val actualResponse = webServer.getFeaturedProjects().execute()

        assertEquals(
            response.toString().containsOkHttpCode(),
            actualResponse.code().toString().containsOkHttpCode()
        )
    }

    @Test
    @Throws(Exception::class)
    fun testFeaturedProjectsCallUsesHTTPS() {
        assertTrue(webServer.getFeaturedProjects().request().isHttps)
    }

    @Test
    @Throws(Exception::class)
    fun testFeaturedProjectsResponseHasCorrectStructure() {
        val response = webServer.getFeaturedProjects().execute().body()
        assertNotNull(response)
        response?.data?.forEach {
            assertNotNull(it)
            assertNotNull(it.id)
            assertNotNull(it.author)
            assertNotNull(it.featuredImage)
            assertNotNull(it.name)
            assertNotNull(it.projectId)
            assertNotNull(it.projectUrl)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testFeaturedProjectsResponseHasValidProjectUrls() {
        val response = webServer.getFeaturedProjects().execute().body()
        assertNotNull(response)
        response?.data?.forEach {
            assertTrue(it.name.isNotEmpty())
            assertTrue(Patterns.WEB_URL.matcher(it.projectUrl).matches())
        }
    }

    @Test
    @Throws(Exception::class)
    fun testFeaturedProjectsResponseHasValidImages() {
        val response = webServer.getFeaturedProjects().execute().body()
        assertNotNull(response)
        response?.data?.forEach {
            assertTrue(it.name.isNotEmpty())
            val imageUrl = it.featuredImage?.getDetailUrl() ?: it.featuredImage?.getCardUrl()
            assertNotNull(imageUrl)
            assertTrue(Patterns.WEB_URL.matcher(imageUrl!!).matches())
        }
    }

    @Test
    @Throws(Exception::class)
    fun testFeaturedProjectsEmptyFlavorNameReturnsEmptyList() {
        val response = webServer.getFeaturedProjects(flavor = "")
            .execute()
            .body()
        assertNotNull(response)
        assertTrue(response!!.data.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun testFeaturedProjectsCallWithDefaultValues() {
        val expectedRawResponse =
            "Response{protocol=h2, code=200, message=," +
                " url=${Constants.API_BASE_URL}projects/featured?" +
                "max_version=${Constants.CURRENT_CATROBAT_LANGUAGE_VERSION}" +
                "&flavor=${FlavoredConstants.FLAVOR_NAME}&platform=android&limit=20}"
        val rawResponse = webServer.getFeaturedProjects().execute().raw().toString()
        assertEquals(expectedRawResponse, rawResponse)
    }

    @Test
    @Throws(Exception::class)
    fun testProjectCategoriesCallUsesHTTPS() {
        assertTrue(webServer.getProjectCategories().request().isHttps)
    }

    @Test
    @Throws(Exception::class)
    fun testProjectCategoriesCallWithDefaultValues() {
        val expectedRawResponse =
            "Response{protocol=h2, code=200, message=," +
                " url=${Constants.API_BASE_URL}projects/categories?" +
                "max_version=${Constants.CURRENT_CATROBAT_LANGUAGE_VERSION}" +
                "&flavor=${FlavoredConstants.FLAVOR_NAME}}"
        val rawResponse = webServer.getProjectCategories().execute().raw().toString()
        assertEquals(expectedRawResponse, rawResponse)
    }

    @Test
    @Throws(Exception::class)
    fun testProjectCategoriesResponseHasCorrectStructure() {
        val response = webServer.getProjectCategories().execute().body()
        assertNotNull(response)
        response?.data?.forEach {
            assertNotNull(it)
            assertNotNull(it.name)
            assertNotNull(it.type)
            assertNotNull(it.projectsList)
            it.projectsList.forEach { projectResponse ->
                assertNotNull(projectResponse.id)
                assertNotNull(projectResponse.name)
                assertNotNull(projectResponse.author)
                assertNotNull(projectResponse.description)
                assertNotNull(projectResponse.version)
                assertNotNull(projectResponse.views)
                assertNotNull(projectResponse.downloads)
                assertNotNull(projectResponse.flavor)
                assertNotNull(projectResponse.uploadedString)
                assertNotNull(projectResponse.screenshot)
                assertNotNull(projectResponse.projectUrl)
                assertTrue(Patterns.WEB_URL.matcher(projectResponse.projectUrl).matches())
                assertNotNull(projectResponse.downloadUrl)
                assertTrue(Patterns.WEB_URL.matcher(projectResponse.downloadUrl).matches())
                assertNotNull(projectResponse.filesize)
            }
        }
    }

    private fun String.containsOkHttpCode() = contains(200.toString())
}
