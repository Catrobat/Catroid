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
package org.catrobat.catroid.content.actions

import android.util.Log
import okhttp3.Response
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.R
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.utils.Utils
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.ArrayList

open class LookRequestAction : WebAction() {
    var response: InputStream? = null
    var errorCode: String? = null
    private var lookName: String? = null
    private var fileExtension: String? = null

    override fun act(delta: Float): Boolean {
        return if (scope?.sprite == null || formula == null || scope?.sequence == null) {
            true
        } else super.act(delta)
    }

    override fun handleResponse() {
        val lookData = getLookFromResponse()
        lookData?.apply {
            updateLookListIndex()
            scope?.sprite?.look?.lookData = this
            collisionInformation?.collisionPolygonCalculationThread?.join()
            file?.delete()
            isWebRequest = true
        }
    }

    private fun updateLookListIndex() {
        val currentLook = scope?.sprite?.look
        if (!(currentLook != null && currentLook.lookListIndexBeforeLookRequest > -1)) {
            scope?.sprite?.look?.lookListIndexBeforeLookRequest =
                scope?.sprite?.lookList?.indexOf(scope?.sprite?.look?.lookData) ?: -1
        }
    }

    fun getLookFromResponse(): LookData? {
        when {
            errorCode != null -> handleError(errorCode!!)
            response == null -> handleInvalidFormat()
            else -> try {
                val lookFile = File.createTempFile(lookName, fileExtension)
                StorageOperations.copyStreamToFile(response, lookFile)
                LookData(lookName, lookFile).apply {
                    collisionInformation.calculate()
                    return this
                }
            } catch (exception: IOException) {
                Log.e(javaClass.simpleName, "Couldn't interpret InputStream as image", exception)
                handleInvalidFormat()
            }
        }
        return null
    }

    private fun handleInvalidFormat() {
        CatroidApplication.getAppContext()?.let {
            showToastMessage(it.getString(R.string.look_request_type_error_message, url))
        }
    }

    private fun showToastMessage(message: String) {
        val params = ArrayList<Any>(listOf(message))
        StageActivity.messageHandler.obtainMessage(StageActivity.SHOW_TOAST, params).sendToTarget()
    }

    override fun handleError(error: String) {
        errorCode = error
        CatroidApplication.getAppContext()?.let {
            showToastMessage(it.getString(R.string.look_request_http_error_message, url, errorCode))
        }
    }

    override fun restart() {
        response = null
        errorCode = null
        lookName = null
        super.restart()
    }

    override fun onRequestSuccess(httpResponse: Response) {
        response = httpResponse.body()?.byteStream()
        val fileName = Utils.getFileNameFromHttpResponse(httpResponse) ?: Utils.getFileNameFromURL(url)
        fileName.split('.', limit = 2).let { name ->
            lookName = name[0]
            name.getOrNull(1)?.let { type ->
                fileExtension = ".$type"
            }
        }
        super.onRequestSuccess(httpResponse)
    }

    override fun onRequestError(httpError: String) {
        errorCode = httpError
        super.onRequestError(httpError)
    }

    override fun onCancelledCall() {
        response = null
        errorCode = null
        lookName = null
        super.onCancelledCall()
    }
}
