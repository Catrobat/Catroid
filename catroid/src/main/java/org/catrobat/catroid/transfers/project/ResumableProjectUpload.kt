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

package org.catrobat.catroid.transfers.project

import com.squareup.moshi.JsonDataException
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okio.ByteString.Companion.toByteString
import org.catrobat.catroid.retrofit.CatroidWebServer
import org.catrobat.catroid.retrofit.WebService
import org.catrobat.catroid.retrofit.models.ProjectUploadResponse
import org.catrobat.catroid.retrofit.models.ProjectUploadSession
import org.catrobat.catroid.retrofit.models.ProjectUploadSessionRequest
import org.catrobat.catroid.web.ProgressRequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile

/**
 * Sends a project archive through the resumable upload API: start, send the chunks the server
 * does not have yet, complete. A network error, 409, 429 or 5xx only costs the current chunk:
 * after a backoff the server's list of received chunks decides what is sent next. Completing is
 * retried too: checking a big project outlasts the proxy timeout, and the server answers a
 * repeated call with 409 while the first one runs and with the created project afterwards.
 */
class ResumableProjectUpload(
    private val webService: WebService,
    private val sleep: (millis: Long) -> Unit = { Thread.sleep(it) },
    private val now: () -> Long = { System.currentTimeMillis() }
) {

    /**
     * Returns the response that ended the upload: the completion on success, otherwise the one
     * the server refused with. Throws when the network stays down through every retry, or when
     * the server's answer cannot be used.
     */
    @Throws(IOException::class)
    fun upload(
        archive: File,
        request: ProjectUploadSessionRequest,
        progressCallback: ((percent: Int) -> Unit)? = null
    ): Response<ProjectUploadResponse> {
        val start = webService.startProjectUpload(request).fetch()
        if (!start.isSuccessful) {
            return failure(start)
        }
        val session = usableSession(start.body(), archive)

        val refused = try {
            RandomAccessFile(archive, "r").use { sendChunks(it, session, progressCallback) }
        } catch (e: IOException) {
            abort(session.id)
            throw e
        }
        if (refused != null) {
            abort(session.id)
            return refused
        }
        progressCallback?.invoke(PERCENT.toInt())
        // No abort from here on: a completion may still be running when we stop waiting for it.
        return complete(session.id)
    }

    private fun usableSession(session: ProjectUploadSession?, archive: File): ProjectUploadSession {
        if (session == null || session.id.isBlank()) {
            throw IOException(INVALID_SESSION_MESSAGE)
        }
        val chunkSize = session.chunkSize.toLong()
        val fits = chunkSize in 1..MAX_CHUNK_SIZE && session.fileSize == archive.length() &&
            session.totalChunks.toLong() == (session.fileSize + chunkSize - 1) / chunkSize
        if (!fits) {
            abort(session.id)
            throw IOException(INVALID_SESSION_MESSAGE)
        }
        return session
    }

    private fun complete(id: String): Response<ProjectUploadResponse> {
        val deadline = now() + COMPLETE_WAIT_MILLIS
        while (true) {
            val response = try {
                webService.completeProjectUpload(id).execute()
            } catch (e: IOException) {
                if (now() >= deadline) throw e
                null
            }
            if (response != null && (response.code() !in COMPLETE_RETRY_CODES || now() >= deadline)) {
                return completion(response)
            }
            sleep(COMPLETE_RETRY_MILLIS)
        }
    }

    /**
     * The body is parsed here, not by Retrofit, so an unreadable success is not mistaken for a
     * network error and retried: the server may already have created the project.
     */
    private fun completion(response: Response<ResponseBody>): Response<ProjectUploadResponse> {
        if (!response.isSuccessful) {
            return failure(response)
        }
        val project = try {
            response.body()?.string()?.let { projectAdapter.fromJson(it) }
        } catch (ignored: IOException) {
            null
        } catch (ignored: JsonDataException) {
            null
        }
        return Response.success(project ?: throw IOException(UNREADABLE_COMPLETION_MESSAGE), response.raw())
    }

    /** Returns null once the server has every chunk, otherwise the response it refused with. */
    private fun sendChunks(
        file: RandomAccessFile,
        session: ProjectUploadSession,
        progressCallback: ((percent: Int) -> Unit)?
    ): Response<ProjectUploadResponse>? {
        val received = chunksInRange(session.receivedChunks, session).toMutableSet()
        var failures = 0
        var reportedPercent = -1
        val reportBytes = { bytes: Long ->
            // 100 % means "processing" in the dialog, so it waits until every chunk is confirmed.
            val percent = minOf(bytes * PERCENT / session.fileSize, PERCENT - 1).toInt()
            if (percent > reportedPercent) {
                reportedPercent = percent
                progressCallback?.invoke(percent)
            }
        }

        while (true) {
            val index = (0 until session.totalChunks).firstOrNull { it !in received } ?: return null
            val doneBytes = received.sumOf { chunkLength(session, it) }
            val bytes = readChunk(file, session, index)
            val checksum = bytes.toByteString().md5().hex()
            val response = try {
                sendChunk(session, index, bytes, checksum) { reportBytes(doneBytes + it) }
            } catch (e: IOException) {
                if (++failures > MAX_RETRIES) throw e
                null
            }
            when {
                response == null -> Unit
                response.isSuccessful -> {
                    received += index
                    failures = 0
                    continue
                }
                !isRetryable(response.code()) || ++failures > MAX_RETRIES -> return failure(response)
            }

            // The failed request may still have arrived: the server's list decides what to send next.
            sleep(retryDelay(failures, response))
            val status = try {
                webService.getProjectUpload(session.id).fetch()
            } catch (ignored: IOException) {
                null
            }
            when {
                status == null || isRetryable(status.code()) -> Unit
                !status.isSuccessful -> return failure(status)
                else -> status.body()?.let {
                    received.clear()
                    received += chunksInRange(it.receivedChunks, session)
                }
            }
            // Only the answer got lost: that is no reason to give up on the upload.
            if (index in received) {
                failures = 0
            }
        }
    }

    private fun sendChunk(
        session: ProjectUploadSession,
        index: Int,
        bytes: ByteArray,
        checksum: String,
        reportChunkBytes: (Long) -> Unit
    ): Response<Unit> {
        val body = ProgressRequestBody(RequestBody.create(MEDIA_TYPE_OCTET_STREAM, bytes)) { percent ->
            reportChunkBytes(bytes.size.toLong() * percent / PERCENT)
        }
        return webService.uploadProjectChunk(
            session.id,
            index,
            MultipartBody.Part.createFormData("chunk", CHUNK_FILE_NAME, body),
            RequestBody.create(MEDIA_TYPE_TEXT_PLAIN, checksum)
        ).fetch()
    }

    private fun abort(id: String) {
        try {
            webService.abortProjectUpload(id).fetch()
        } catch (ignored: IOException) {
            // The server drops abandoned uploads after a day anyway.
        }
    }

    companion object {
        private const val MAX_RETRIES = 5
        private const val BACKOFF_MILLIS = 1000L
        private const val MAX_RETRY_AFTER_SECONDS = 60L
        private const val MILLIS_PER_SECOND = 1000L
        private const val HTTP_CONFLICT = 409
        private const val HTTP_TOO_MANY_REQUESTS = 429
        private const val HTTP_SERVER_ERROR = 500
        private const val COMPLETE_RETRY_MILLIS = 5000L
        private const val COMPLETE_WAIT_MILLIS = 10 * 60 * 1000L
        // The server's chunk size; one chunk is held in memory at a time.
        private const val MAX_CHUNK_SIZE = 16L * 1024 * 1024
        private const val INVALID_SESSION_MESSAGE = "The server started the upload with unusable values"
        private const val UNREADABLE_COMPLETION_MESSAGE = "The server's answer to the finished upload was unreadable"
        private val projectAdapter = CatroidWebServer.moshi.adapter(ProjectUploadResponse::class.java)

        // Still running (409), or a proxy gave up waiting for the server (Cloudflare's 52x included).
        private val COMPLETE_RETRY_CODES = setOf(409, 502, 503, 504, 520, 522, 524)
        private const val PERCENT = 100L
        private const val CHUNK_FILE_NAME = "chunk"
        private val MEDIA_TYPE_OCTET_STREAM = MediaType.parse("application/octet-stream")
        private val MEDIA_TYPE_TEXT_PLAIN = MediaType.parse("text/plain")

        /** Busy with the same chunk (409), rate limited (429) or a server or proxy error. */
        private fun isRetryable(code: Int) =
            code == HTTP_CONFLICT || code == HTTP_TOO_MANY_REQUESTS || code >= HTTP_SERVER_ERROR

        private fun retryDelay(failures: Int, response: Response<*>?): Long {
            val backoff = BACKOFF_MILLIS shl (failures - 1)
            val retryAfter = response?.headers()?.get("Retry-After")?.toLongOrNull() ?: return backoff
            return maxOf(backoff, retryAfter.coerceIn(0, MAX_RETRY_AFTER_SECONDS) * MILLIS_PER_SECOND)
        }

        private fun chunkLength(session: ProjectUploadSession, index: Int): Long =
            minOf(session.chunkSize.toLong(), session.fileSize - index.toLong() * session.chunkSize)

        /** A chunk that does not fit in memory ends the upload like any other local read error. */
        private fun readChunk(file: RandomAccessFile, session: ProjectUploadSession, index: Int): ByteArray =
            try {
                ByteArray(chunkLength(session, index).toInt()).also {
                    file.seek(index.toLong() * session.chunkSize)
                    file.readFully(it)
                }
            } catch (e: OutOfMemoryError) {
                throw IOException("Not enough memory for an upload chunk", e)
            }

        private fun chunksInRange(chunks: List<Int>, session: ProjectUploadSession) =
            chunks.filter { it in 0 until session.totalChunks }

        /** An answer that does not parse is reported like a failed request instead of crashing. */
        private fun <T> Call<T>.fetch(): Response<T> = try {
            execute()
        } catch (e: JsonDataException) {
            throw IOException("Unexpected answer from the server", e)
        }

        private fun <T> failure(response: Response<*>): Response<T> =
            Response.error(response.errorBody() ?: ResponseBody.create(null, ""), response.raw())
    }
}
