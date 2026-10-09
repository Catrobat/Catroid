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

package org.catrobat.catroid.retrofit

import android.util.Log
import org.catrobat.catroid.web.WebConnectionException
import retrofit2.Call
import retrofit2.Response
import retrofit2.awaitResponse
import java.io.IOException

@Throws(WebConnectionException::class)
internal suspend fun <T> Call<T>.awaitSuccessfulResponse(tag: String): Response<T> {
    val response = try {
        awaitResponse()
    } catch (ioException: IOException) {
        Log.e(tag, Log.getStackTraceString(ioException))
        throw WebConnectionException(WebConnectionException.ERROR_NETWORK, "I/O Exception")
    }

    if (!response.isSuccessful) {
        throw WebConnectionException(response.code(), response.message())
    }

    return response
}
