/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
import androidx.lifecycle.ViewModel
import org.catrobat.catroid.retrofit.WebService
import org.catrobat.catroid.retrofit.models.LoginResponse
import org.catrobat.catroid.retrofit.models.User
import org.catrobat.catroid.web.ServerAuthenticationConstants
import org.catrobat.catroid.web.ServerAuthenticationConstants.SERVER_RESPONSE_TOKEN_OK
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val webServer: WebService) : ViewModel() {
    private val loginResponse = MutableLiveData<LoginResponse>()
    fun getLoginResponse(): LiveData<LoginResponse> = loginResponse

    private val isLoggingIn = MutableLiveData<Boolean>(false)
    fun isLoggingIn(): LiveData<Boolean> = isLoggingIn
    fun setIsLoggingIn(isLoggingIn: Boolean = true) {
        this.isLoggingIn.postValue(isLoggingIn)
    }

    private var message: String = String()
    fun getMessage(): String = message
    fun setMessage(message: String) {
        this.message = message
    }

    fun login(username: String, password: String) {

        val loginCall: Call<LoginResponse> = webServer.login(User(username, password))

        loginCall.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val tokenReceived = response.body()?.token
                val refreshToken = response.body()?.refresh_token

                tokenReceived?.let {
                    if (isInvalidResponse(response.code(), response.message(), tokenReceived)) {
                        message = response.message()
                        Log.e(TAG, "StatusCode: $response.code() $message")
                        loginResponse.postValue(response.body())
                        isLoggingIn.postValue(false)
                        return
                    } else {
                        // TODO: no refresh token
                        Log.d(TAG, "$tokenReceived  $refreshToken")
                        loginResponse.postValue(response.body())
                        isLoggingIn.postValue(false)
                        return
                    }
                } ?: run {
                    message = response.message()
                    Log.e(TAG, "StatusCode: $response.code() $message")
                    loginResponse.postValue(response.body())
                    isLoggingIn.postValue(false)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e(TAG, "onFailure $t.message")
                message = t.message.orEmpty()
                loginResponse.postValue(null)
                isLoggingIn.postValue(false)
            }
        })
    }

    fun isInvalidResponse(statusCode: Int, serverAnswer: String, tokenReceived: String = ""): Boolean {
        if (SERVER_RESPONSE_TOKEN_OK != statusCode) {
            Log.e(TAG, "Not accepted StatusCode: $statusCode; Server Answer: $serverAnswer")
            return true
        }
        if (tokenReceived.length != ServerAuthenticationConstants.NEW_TOKEN_LENGTH) {
            Log.e(TAG, "Invalid TokenError: $tokenReceived; StatusCode: $statusCode Server " +
                "Answer: $serverAnswer")
            return true
        }
        return false
    }

    companion object {
        private val TAG = LoginViewModel::class.java.simpleName
    }
}
