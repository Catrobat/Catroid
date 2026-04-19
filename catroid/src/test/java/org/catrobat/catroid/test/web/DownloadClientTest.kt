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

package org.catrobat.catroid.test.web

import io.mockk.every
import io.mockk.mockk
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import org.catrobat.catroid.web.DownloadClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DownloadClientTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var okHttpClient: OkHttpClient
    private lateinit var client: DownloadClient

    @Before
    fun setUp() {
        okHttpClient = mockk(relaxed = true)
        client = DownloadClient(okHttpClient)
    }

    private fun buildResponse(code: Int, body: String): Response {
        val request = Request.Builder().url("https://share.catrobat.org/test").build()
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message(if (code == 200) "OK" else "Error")
            .body(ResponseBody.create(null, body))
            .build()
    }

    private fun mockCall(response: Response) {
        val call = mockk<Call>()
        every { call.execute() } returns response
        every { okHttpClient.newBuilder() } returns OkHttpClient.Builder()
    }

    @Test
    fun `downloadProject writes file and calls success on 200`() {
        val content = "fake-project-zip"
        val call = mockk<Call>()
        every { call.execute() } returns buildResponse(200, content)

        val realClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                buildResponse(200, content)
            }
            .build()

        val downloadClient = DownloadClient(realClient)
        val destination = tempFolder.newFile("test.catrobat")
        var successCalled = false

        downloadClient.downloadProject(
            "https://share.catrobat.org/api/projects/123/catrobat",
            destination,
            successCallback = { successCalled = true },
            errorCallback = { _, _ -> throw AssertionError("Should not fail") },
            progressCallback = { }
        )

        assertTrue(successCalled)
        assertEquals(content, destination.readText())
    }

    @Test
    fun `downloadProject calls error on non-200`() {
        val realClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                buildResponse(404, "Not Found")
            }
            .build()

        val downloadClient = DownloadClient(realClient)
        val destination = tempFolder.newFile("test.catrobat")
        var errorCode = -1

        downloadClient.downloadProject(
            "https://share.catrobat.org/api/projects/999/catrobat",
            destination,
            successCallback = { throw AssertionError("Should not succeed") },
            errorCallback = { code, _ -> errorCode = code },
            progressCallback = { }
        )

        assertEquals(404, errorCode)
    }
}
