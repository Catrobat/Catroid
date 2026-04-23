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
import org.catrobat.catroid.formulaeditor.UserVariable
import java.io.IOException

class WebRequestAction : WebAction() {
    var userVariable: UserVariable? = null
    var response: String? = null

    override fun act(delta: Float): Boolean {
        val scopeInitialized = scope?.sprite != null && scope?.sequence != null
        return if (userVariable == null || formula == null || !scopeInitialized) {
            true
        } else super.act(delta)
    }

    override fun handleResponse() {
        userVariable!!.value = response
    }

    override fun handleError(error: String) {
        userVariable!!.value = error
    }

    override fun restart() {
        response = null
        super.restart()
    }

    override fun onRequestSuccess(httpResponse: Response) {
        response = try {
            httpResponse.body()?.string() ?: ""
        } catch (exception: IOException) {
            Log.d(javaClass.simpleName, "HTTP reponse body is empty", exception)
            ""
        }
        super.onRequestSuccess(httpResponse)
    }

    override fun onRequestError(httpError: String) {
        response = httpError
        super.onRequestError(httpError)
    }

    override fun onCancelledCall() {
        response = null
        super.onCancelledCall()
    }
}
