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

    fun login(username: String, password: String, token: String) {
        val loginCall: Call<LoginResponse> = webServer.login("Bearer $token", User(username, password))

        loginCall.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val statusCode = response.code()
                val serverAnswer = response.message()

                when (statusCode) {
                    SERVER_RESPONSE_TOKEN_OK -> {
                        val tokenReceived = response.body()?.token
                        val refreshToken = response.body()?.refresh_token

                        tokenReceived?.let {
                            Log.d(TAG, "$tokenReceived  $refreshToken")
                            loginResponse.postValue(response.body())
                        } ?: run {
                            loginResponse.postValue(null)
                        }
                    }
                    else -> {
                        Log.e(TAG, "Not accepted StatusCode: $statusCode; Server Answer: $serverAnswer")
                        loginResponse.postValue(null)
                    }
                }
                isLoggingIn.postValue(false)
            }

            override fun onFailure(call: Call<LoginResponse>, throwable: Throwable) {
                Log.e(TAG, "onFailure $throwable.message")
                message = throwable.message.orEmpty()
                loginResponse.postValue(null)
                isLoggingIn.postValue(false)
            }
        })
    }

    companion object {
        private val TAG = LoginViewModel::class.java.simpleName
    }
}
