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
package org.catrobat.catroid.transfers

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.catrobat.catroid.retrofit.WebService
import org.catrobat.catroid.web.ServerAuthenticationConstants.SERVER_RESPONSE_TOKEN_OK
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckTokenTask(private val webServer: WebService) {
    private val isValidToken = MutableLiveData<Boolean>()

    fun isValidToken(): LiveData<Boolean> = isValidToken

    fun checkToken(token: String) {
        val checkTokenCall: Call<Void> = webServer.checkToken("Bearer $token")

        checkTokenCall.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                val statusCode = response.code()
                val serverAnswer = response.message()

                when (statusCode) {
                    SERVER_RESPONSE_TOKEN_OK -> isValidToken.postValue(true)
                    else -> {
                        Log.e(TAG, "Invalid Token StatusCode: $statusCode; ServerAnswer: $serverAnswer")
                        isValidToken.postValue(false)
                    }
                }
            }

            override fun onFailure(call: Call<Void>, throwable: Throwable) {
                Log.e(TAG, "onFailure $throwable.message")
                isValidToken.postValue(false)
            }
        })
    }

    companion object {
        private val TAG = CheckTokenTask::class.java.simpleName
    }
}
