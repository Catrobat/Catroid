/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.catrobat.catroid.retrofit.WebService
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import java.net.HttpURLConnection

@RunWith(MockitoJUnitRunner::class)
class CatroidWebServerTest : KoinTest {

    companion object {
        private const val SUCCESS_RESPONSE_FILE_NAME = "featured_projects_success_response.json"
    }

    private lateinit var mockWebServer: MockWebServer
    private lateinit var context: Context

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

    private val webServer: WebService by inject()

    @Test
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

    private fun String.containsOkHttpCode() = contains(200.toString())
}
