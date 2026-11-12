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

package org.catrobat.catroid.test.transfers

import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import okhttp3.mockwebserver.SocketPolicy
import org.catrobat.catroid.retrofit.CatroidWebServer
import org.catrobat.catroid.retrofit.WebService
import org.catrobat.catroid.retrofit.models.ProjectUploadSessionRequest
import org.catrobat.catroid.transfers.project.ResumableProjectUpload
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.io.IOException
import java.math.BigInteger
import java.net.InetAddress
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

@RunWith(JUnit4::class)
class ResumableProjectUploadTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var server: MockWebServer
    private lateinit var upload: ResumableProjectUpload
    private lateinit var archive: File
    private val sleeps = mutableListOf<Long>()
    private var clock = 0L

    @Before
    fun setUp() {
        server = MockWebServer()
        // One address only: without OkHttp's retry a dual-stack "localhost" may fail on the first one.
        server.start(InetAddress.getByName("127.0.0.1"), 0)
        val webService = Retrofit.Builder()
            .baseUrl(server.url("/api/").newBuilder().host("127.0.0.1").build())
            // Dropped connections reach the code under test instead of OkHttp's own retry, and no
            // request lands on a pooled connection the server has already closed.
            .client(
                OkHttpClient.Builder()
                    .retryOnConnectionFailure(false)
                    .connectionPool(ConnectionPool(0, 1, TimeUnit.SECONDS))
                    .build()
            )
            .addConverterFactory(MoshiConverterFactory.create(CatroidWebServer.moshi))
            .build()
            .create(WebService::class.java)
        upload = ResumableProjectUpload(webService, sleep = { sleeps += it; clock += it }, now = { clock })
        archive = tempFolder.newFile("upload.catrobat").apply { writeText(CONTENT) }
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun sendsEveryChunkWithItsChecksumThenCompletes() {
        server.enqueue(session(received = ""))
        repeat(3) { server.enqueue(MockResponse().setResponseCode(204)) }
        server.enqueue(MockResponse().setResponseCode(201).setBody("""{"id":"project-1"}"""))
        val progress = mutableListOf<Int>()

        val response = upload.upload(archive, request(), progress::add)

        assertTrue(response.isSuccessful)
        assertEquals("project-1", response.body()?.id)
        val start = server.takeRequest()
        assertEquals("/api/projects/uploads", start.path)
        assertTrue(start.body.readUtf8().contains("\"file_size\":25"))
        assertChunk(server.takeRequest(), 0, "AAAAAAAAAA")
        assertChunk(server.takeRequest(), 1, "BBBBBBBBBB")
        assertChunk(server.takeRequest(), 2, "CCCCC")
        assertEquals("/api/projects/uploads/up1/complete", server.takeRequest().path)
        assertEquals(100, progress.last())
        assertTrue(progress.dropLast(1).all { it <= 99 })
        assertEquals(progress.sorted().distinct(), progress)
    }

    @Test
    fun sendsOnlyTheChunksTheServerIsMissing() {
        server.enqueue(session(received = "0,2"))
        server.enqueue(MockResponse().setResponseCode(204))
        server.enqueue(MockResponse().setResponseCode(201).setBody("""{"id":"project-1"}"""))

        assertTrue(upload.upload(archive, request()).isSuccessful)

        server.takeRequest()
        assertChunk(server.takeRequest(), 1, "BBBBBBBBBB")
        assertEquals("/api/projects/uploads/up1/complete", server.takeRequest().path)
    }

    @Test
    fun retriesAServerErrorAfterAskingWhatArrived() {
        server.enqueue(session(received = ""))
        server.enqueue(MockResponse().setResponseCode(204))
        server.enqueue(MockResponse().setResponseCode(503))
        server.enqueue(session(received = "0"))
        server.enqueue(MockResponse().setResponseCode(204))
        server.enqueue(MockResponse().setResponseCode(204))
        server.enqueue(MockResponse().setResponseCode(201).setBody("""{"id":"project-1"}"""))

        assertTrue(upload.upload(archive, request()).isSuccessful)

        server.takeRequest()
        assertChunk(server.takeRequest(), 0, "AAAAAAAAAA")
        assertChunk(server.takeRequest(), 1, "BBBBBBBBBB")
        assertEquals("GET /api/projects/uploads/up1", server.takeRequest().let { "${it.method} ${it.path}" })
        assertChunk(server.takeRequest(), 1, "BBBBBBBBBB")
        assertChunk(server.takeRequest(), 2, "CCCCC")
        assertEquals(listOf(1000L), sleeps)
    }

    @Test
    fun resumesFromTheServerStateAfterALostConnection() {
        server.enqueue(session(received = ""))
        server.enqueue(MockResponse().setResponseCode(204))
        server.enqueue(MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST))
        server.enqueue(session(received = "0,1"))
        server.enqueue(MockResponse().setResponseCode(204))
        server.enqueue(MockResponse().setResponseCode(201).setBody("""{"id":"project-1"}"""))

        assertTrue(upload.upload(archive, request()).isSuccessful)

        server.takeRequest()
        assertChunk(server.takeRequest(), 0, "AAAAAAAAAA")
        assertChunk(server.takeRequest(), 1, "BBBBBBBBBB")
        assertEquals("/api/projects/uploads/up1", server.takeRequest().path)
        assertChunk(server.takeRequest(), 2, "CCCCC")
        assertEquals("/api/projects/uploads/up1/complete", server.takeRequest().path)
    }

    @Test
    fun refusedChunkEndsTheUploadWithTheServerErrorAndAbortsIt() {
        val error = """{"error":{"code":422,"type":"validation_error","message":"Chunk damaged"}}"""
        server.enqueue(session(received = ""))
        server.enqueue(MockResponse().setResponseCode(422).setBody(error))
        server.enqueue(MockResponse().setResponseCode(204))

        val response = upload.upload(archive, request())

        assertEquals(422, response.code())
        assertEquals(error, response.errorBody()?.string())
        server.takeRequest()
        server.takeRequest()
        assertEquals("DELETE /api/projects/uploads/up1", server.takeRequest().let { "${it.method} ${it.path}" })
        assertEquals(3, server.requestCount)
        assertTrue(sleeps.isEmpty())
    }

    @Test
    fun refusedStartSendsNothingElse() {
        server.enqueue(MockResponse().setResponseCode(422).setBody("""{"error":{"code":422,"type":"file_too_large","message":"Too large"}}"""))

        val response = upload.upload(archive, request())

        assertFalse(response.isSuccessful)
        assertEquals(422, response.code())
        assertEquals(1, server.requestCount)
    }

    @Test
    fun givesUpWhenTheNetworkStaysDown() {
        server.enqueue(session(received = ""))
        // six tries of chunk 0 and five status checks in between
        repeat(11) { server.enqueue(MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST)) }
        server.enqueue(MockResponse().setResponseCode(204))

        val error = runCatching { upload.upload(archive, request()) }.exceptionOrNull()

        assertTrue(error.toString(), error is IOException)
        assertEquals(listOf(1000L, 2000L, 4000L, 8000L, 16_000L), sleeps)
        assertEquals("DELETE /api/projects/uploads/up1", requests().last())
    }

    @Test
    fun retriesCompletionWhileItIsStillRunningOrTimedOut() {
        server.enqueue(session(received = "0,1,2"))
        server.enqueue(MockResponse().setResponseCode(409))
        server.enqueue(MockResponse().setResponseCode(524))
        server.enqueue(MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST))
        server.enqueue(MockResponse().setResponseCode(201).setBody("""{"id":"project-1"}"""))

        val response = upload.upload(archive, request())

        assertEquals("project-1", response.body()?.id)
        assertEquals(listOf(5000L, 5000L, 5000L), sleeps)
        server.takeRequest()
        repeat(4) { assertEquals("/api/projects/uploads/up1/complete", server.takeRequest().path) }
    }

    @Test
    fun completionNotFoundIsFinalAndNotAborted() {
        server.enqueue(session(received = "0,1,2"))
        server.enqueue(MockResponse().setResponseCode(404))
        server.enqueue(MockResponse().setResponseCode(204))

        val response = upload.upload(archive, request())

        assertEquals(404, response.code())
        assertTrue(sleeps.isEmpty())
        assertEquals(2, server.requestCount)
    }

    @Test
    fun stopsWaitingForCompletionAfterTenMinutesWithoutAborting() {
        server.enqueue(session(received = "0,1,2"))
        repeat(200) { server.enqueue(MockResponse().setResponseCode(409)) }

        val response = upload.upload(archive, request())

        assertEquals(409, response.code())
        assertEquals(600_000L, sleeps.sum())
        server.takeRequest()
        repeat(121) { assertEquals("POST", server.takeRequest().method) }
        assertEquals(122, server.requestCount)
    }

    @Test
    fun busyChunkIsResentAndTheUploadKept() {
        server.enqueue(session(received = ""))
        server.enqueue(MockResponse().setResponseCode(409))
        server.enqueue(session(received = ""))
        repeat(3) { server.enqueue(MockResponse().setResponseCode(204)) }
        server.enqueue(MockResponse().setResponseCode(201).setBody("""{"id":"project-1"}"""))

        assertTrue(upload.upload(archive, request()).isSuccessful)

        val requests = requests()
        assertEquals(2, requests.count { it == "POST /api/projects/uploads/up1/chunks/0" })
        assertFalse(requests.any { it.startsWith("DELETE") })
        assertEquals(listOf(1000L), sleeps)
    }

    @Test
    fun rateLimitedChunkWaitsForRetryAfterAndResumes() {
        server.enqueue(session(received = ""))
        server.enqueue(MockResponse().setResponseCode(204))
        server.enqueue(MockResponse().setResponseCode(429).setHeader("Retry-After", "7"))
        server.enqueue(session(received = "0"))
        repeat(2) { server.enqueue(MockResponse().setResponseCode(204)) }
        server.enqueue(MockResponse().setResponseCode(201).setBody("""{"id":"project-1"}"""))

        assertTrue(upload.upload(archive, request()).isSuccessful)

        assertEquals(listOf(7000L), sleeps)
        assertEquals(2, requests().count { it == "POST /api/projects/uploads/up1/chunks/1" })
    }

    @Test
    fun failedCompletionIsFinalAndKeepsTheChunks() {
        server.enqueue(session(received = "0,1,2"))
        server.enqueue(
            MockResponse().setResponseCode(500)
                .setBody("""{"error":{"code":500,"type":"upload_failed","message":"Upload failed"}}""")
        )

        val response = upload.upload(archive, request())

        assertEquals(500, response.code())
        assertTrue(sleeps.isEmpty())
        assertEquals(listOf("POST /api/projects/uploads", "POST /api/projects/uploads/up1/complete"), requests())
    }

    @Test
    fun archiveShorterThanTheSessionIsRefusedAndTheUploadDeleted() {
        archive.writeText(CONTENT.take(20))
        server.enqueue(session(received = ""))
        server.enqueue(MockResponse().setResponseCode(204))

        val error = runCatching { upload.upload(archive, request()) }.exceptionOrNull()

        assertTrue(error.toString(), error is IOException)
        assertEquals(listOf("POST /api/projects/uploads", "DELETE /api/projects/uploads/up1"), requests())
    }

    @Test
    fun incompleteSessionIsRefusedAndTheUploadDeleted() {
        server.enqueue(MockResponse().setResponseCode(201).setBody("""{"id":"up1","file_size":25}"""))
        server.enqueue(MockResponse().setResponseCode(204))

        val error = runCatching { upload.upload(archive, request()) }.exceptionOrNull()

        assertTrue(error.toString(), error is IOException)
        assertEquals(listOf("POST /api/projects/uploads", "DELETE /api/projects/uploads/up1"), requests())
    }

    @Test
    fun chunkAndCompletionGetTheLongUploadTimeout() {
        fun post(path: String) = Request.Builder().url("https://share.catrobat.org/api/$path")
            .post(RequestBody.create(null, ByteArray(0))).build()

        assertTrue(CatroidWebServer.isProjectUpload(post("projects/uploads/up1/chunks/0")))
        assertTrue(CatroidWebServer.isProjectUpload(post("projects/uploads/up1/complete")))
        assertFalse(CatroidWebServer.isProjectUpload(post("projects/uploads")))
        assertFalse(
            CatroidWebServer.isProjectUpload(
                Request.Builder().url("https://share.catrobat.org/api/projects/uploads/up1").build()
            )
        )
    }

    @Test
    fun answersLostOnDifferentChunksDoNotAddUp() {
        // 13 chunks of 2 bytes; the answers for chunks 0 to 6 get lost although the chunks arrive.
        server.enqueue(session(received = "", chunkSize = 2, totalChunks = 13))
        for (index in 0..6) {
            server.enqueue(MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST))
            server.enqueue(session(received = (0..index).joinToString(","), chunkSize = 2, totalChunks = 13))
        }
        repeat(6) { server.enqueue(MockResponse().setResponseCode(204)) }
        server.enqueue(MockResponse().setResponseCode(201).setBody("""{"id":"project-1"}"""))

        val response = upload.upload(archive, request())

        assertEquals("project-1", response.body()?.id)
        assertEquals(List(7) { 1000L }, sleeps)
        assertFalse(requests().any { it.startsWith("DELETE") })
    }

    @Test
    fun ignoresReceivedChunksOutsideTheUpload() {
        server.enqueue(session(received = "-1,0,7"))
        repeat(2) { server.enqueue(MockResponse().setResponseCode(204)) }
        server.enqueue(MockResponse().setResponseCode(201).setBody("""{"id":"project-1"}"""))

        assertTrue(upload.upload(archive, request()).isSuccessful)

        server.takeRequest()
        assertChunk(server.takeRequest(), 1, "BBBBBBBBBB")
        assertChunk(server.takeRequest(), 2, "CCCCC")
    }

    @Test
    fun chunkSizeAboveTheCapIsRefusedAndTheUploadDeleted() {
        server.enqueue(session(received = "", chunkSize = 32 * 1024 * 1024, totalChunks = 1))
        server.enqueue(MockResponse().setResponseCode(204))

        val error = runCatching { upload.upload(archive, request()) }.exceptionOrNull()

        assertTrue(error.toString(), error is IOException)
        assertEquals(listOf("POST /api/projects/uploads", "DELETE /api/projects/uploads/up1"), requests())
    }

    @Test
    fun chunkCountMismatchIsRefusedAndTheUploadDeleted() {
        server.enqueue(session(received = "", totalChunks = 4))
        server.enqueue(MockResponse().setResponseCode(204))

        val error = runCatching { upload.upload(archive, request()) }.exceptionOrNull()

        assertTrue(error.toString(), error is IOException)
        assertEquals(listOf("POST /api/projects/uploads", "DELETE /api/projects/uploads/up1"), requests())
    }

    @Test
    fun unreadableCompletionIsFinalAndKeepsTheUpload() {
        server.enqueue(session(received = "0,1,2"))
        server.enqueue(MockResponse().setResponseCode(201).setBody("<html>proxy page</html>"))
        server.enqueue(MockResponse().setResponseCode(201).setBody("""{"id":"project-1"}"""))

        val error = runCatching { upload.upload(archive, request()) }.exceptionOrNull()

        assertTrue(error.toString(), error is IOException)
        assertTrue(sleeps.isEmpty())
        assertEquals(listOf("POST /api/projects/uploads", "POST /api/projects/uploads/up1/complete"), requests())
    }

    private fun requests(): List<String> =
        generateSequence { server.takeRequest(100, TimeUnit.MILLISECONDS) }.map { "${it.method} ${it.path}" }.toList()

    private fun request() = ProjectUploadSessionRequest(fileSize = archive.length(), flavor = "pocketcode")

    private fun session(received: String, chunkSize: Int = 10, totalChunks: Int = 3) =
        MockResponse().setResponseCode(201).setBody(
            """{"id":"up1","file_size":25,"chunk_size":$chunkSize,"total_chunks":$totalChunks,""" +
                """"received_chunks":[$received],"max_file_size":314572800,""" +
                """"expires_at":"2026-11-11T00:00:00+00:00"}"""
        )

    private fun assertChunk(request: RecordedRequest, index: Int, content: String) {
        assertEquals("POST /api/projects/uploads/up1/chunks/$index", "${request.method} ${request.path}")
        val body = request.body.readUtf8()
        assertTrue(body.contains("\r\n\r\n$content\r\n"))
        assertTrue(body.contains(md5(content)))
    }

    private fun md5(text: String) =
        String.format("%032x", BigInteger(1, MessageDigest.getInstance("MD5").digest(text.toByteArray())))

    companion object {
        private const val CONTENT = "AAAAAAAAAABBBBBBBBBBCCCCC"
    }
}
