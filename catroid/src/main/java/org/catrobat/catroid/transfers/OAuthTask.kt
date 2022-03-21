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
package org.catrobat.catroid.transfers

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.catrobat.catroid.retrofit.WebService
import org.catrobat.catroid.retrofit.models.LoginResponse
import org.catrobat.catroid.retrofit.models.OAuthLogin
import org.catrobat.catroid.web.ServerAuthenticationConstants.SERVER_RESPONSE_TOKEN_OK
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OAuthTask(private val webServer: WebService) {
    private val oAuthResponse = MutableLiveData<LoginResponse>()
    fun getOAuthResponse(): LiveData<LoginResponse> = oAuthResponse

    private val isOAuthLoggingIn = MutableLiveData<Boolean>(false)
    fun isOAuthLoggingIn(): LiveData<Boolean> = isOAuthLoggingIn
    fun setIsOAuthLoggingIn(isOAuthLoggingIn: Boolean = true) {
        this.isOAuthLoggingIn.postValue(isOAuthLoggingIn)
    }

    private var message: String = String()
    fun getMessage(): String = message
    fun setMessage(message: String) {
        this.message = message
    }

    fun oAuthLogin(idToken: String, resourceOwner: String) {
        val oAuthLoginCall: Call<LoginResponse> = webServer.oAuthLogin(OAuthLogin(idToken, resourceOwner))

        oAuthLoginCall.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val statusCode = response.code()
                val serverAnswer = response.message()

                when (statusCode) {
                    SERVER_RESPONSE_TOKEN_OK -> {
                        val tokenReceived = response.body()?.token
                        val refreshToken = response.body()?.refresh_token

                        tokenReceived?.let {
                            Log.d(TAG, "$tokenReceived  $refreshToken")
                            oAuthResponse.postValue(response.body())
                        } ?: run {
                            oAuthResponse.postValue(null)
                        }
                    }
                    else -> {
                        Log.e(TAG, "Not accepted StatusCode: $statusCode; Server Answer: $serverAnswer")
                        oAuthResponse.postValue(null)
                    }
                }
                isOAuthLoggingIn.postValue(false)
            }

            override fun onFailure(call: Call<LoginResponse>, throwable: Throwable) {
                Log.e(TAG, "onFailure $throwable.message")
                message = throwable.message.orEmpty()
                oAuthResponse.postValue(null)
                isOAuthLoggingIn.postValue(false)
            }
        })
    }

    fun clear() {
        oAuthResponse.value = null
        isOAuthLoggingIn.value = false
    }

    companion object {
        private val TAG = OAuthTask::class.java.simpleName
    }
}
