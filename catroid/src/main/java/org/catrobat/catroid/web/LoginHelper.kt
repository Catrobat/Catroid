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

package org.catrobat.catroid.web

import android.util.Log
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.catrobat.catroid.R
import java.io.IOException
import java.util.function.IntConsumer

/**
 * Runs login calls for Java callers. Each call returns its [Job]; the caller owns it and cancels
 * it when its screen goes away, so one screen never cancels another screen's login.
 */
object LoginHelper {

    private const val TAG = "LoginHelper"
    private const val SESSION_CHECK_TIMEOUT_MS = 3000L

    private const val HTTP_UNAUTHORIZED = 401
    private const val ERROR_CODE_SUSPENDED = "account_suspended"

    @JvmStatic
    fun performLogin(
        loginRepository: LoginRepository,
        username: String,
        password: String,
        onSuccess: Runnable,
        onError: IntConsumer
    ): Job =
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            val result = loginRepository.login(username, password)
            withContext(Dispatchers.Main) {
                coroutineContext.ensureActive()
                result.fold(
                    onSuccess = { onSuccess.run() },
                    onFailure = { e -> onError.accept(failureMessage(e)) }
                )
            }
        }

    @JvmStatic
    fun performGoogleLogin(
        loginRepository: LoginRepository,
        idToken: String,
        onSuccess: Runnable,
        onError: IntConsumer
    ): Job =
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            val result = loginRepository.loginWithGoogle(idToken)
            withContext(Dispatchers.Main) {
                coroutineContext.ensureActive()
                result.fold(
                    onSuccess = { onSuccess.run() },
                    onFailure = { e -> onError.accept(failureMessage(e)) }
                )
            }
        }

    /**
     * The WebView signs in with the stored access token, which expires after an hour. For a share
     * page, check it (refreshing it when expired) before the page loads. The page loads in any
     * case, and a slow or failing check only costs [SESSION_CHECK_TIMEOUT_MS].
     */
    @JvmStatic
    @Suppress("TooGenericExceptionCaught") // any failure of the check must still load the page
    fun prepareWebSession(loginRepository: LoginRepository, isSharePage: Boolean, onReady: Runnable): Job =
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                if (isSharePage && loginRepository.isLoggedIn()) {
                    withTimeoutOrNull(SESSION_CHECK_TIMEOUT_MS) { loginRepository.validateToken() }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w(TAG, "Session check before the web view failed", e)
            }
            withContext(Dispatchers.Main) {
                coroutineContext.ensureActive()
                onReady.run()
            }
        }

    /**
     * Revokes the session on the server. Deliberately not tied to a screen: the logout page closes
     * the WebView right away, and cancelling would leave the refresh token valid on the server.
     */
    @JvmStatic
    fun revokeInBackground(loginRepository: LoginRepository, accessToken: String?, refreshToken: String?) {
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            loginRepository.revoke(accessToken, refreshToken)
        }
    }

    /**
     * The message for a failed login, as a string resource. Server texts are English and parser
     * errors are technical, so neither is shown.
     */
    @StringRes
    @VisibleForTesting
    fun failureMessage(e: Throwable): Int = when {
        e is IOException -> R.string.error_internet_connection
        e is LoginRepository.LoginRefused && e.errorCode == ERROR_CODE_SUSPENDED -> R.string.sign_in_account_suspended
        e is LoginRepository.LoginRefused && e.status == HTTP_UNAUTHORIZED -> R.string.sign_in_wrong_credentials
        else -> R.string.sign_in_error
    }

    @JvmStatic
    fun cancel(job: Job?) {
        job?.cancel()
    }
}
