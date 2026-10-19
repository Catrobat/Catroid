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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.IOException

/**
 * Runs login calls for Java callers. Each call returns its [Job]; the caller owns it and cancels
 * it when its screen goes away, so one screen never cancels another screen's login.
 */
object LoginHelper {

    private const val TAG = "LoginHelper"
    private const val SESSION_CHECK_TIMEOUT_MS = 3000L

    private var ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    private var mainDispatcher: CoroutineDispatcher = Dispatchers.Main

    @JvmStatic
    fun performLogin(
        loginRepository: LoginRepository,
        username: String,
        password: String,
        onSuccess: Runnable,
        onError: java.util.function.Consumer<String?>
    ): Job =
        CoroutineScope(SupervisorJob() + ioDispatcher).launch {
            val result = loginRepository.login(username, password)
            withContext(mainDispatcher) {
                coroutineContext.ensureActive()
                result.fold(
                    onSuccess = { onSuccess.run() },
                    onFailure = { e -> onError.accept(userMessage(e, "Login failed")) }
                )
            }
        }

    @JvmStatic
    fun performGoogleLogin(
        loginRepository: LoginRepository,
        idToken: String,
        onSuccess: Runnable,
        onError: java.util.function.Consumer<String?>
    ): Job =
        CoroutineScope(SupervisorJob() + ioDispatcher).launch {
            val result = loginRepository.loginWithGoogle(idToken)
            withContext(mainDispatcher) {
                coroutineContext.ensureActive()
                result.fold(
                    onSuccess = { onSuccess.run() },
                    onFailure = { e -> onError.accept(userMessage(e, "Google login failed")) }
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
        CoroutineScope(SupervisorJob() + ioDispatcher).launch {
            try {
                if (isSharePage && loginRepository.isLoggedIn()) {
                    withTimeoutOrNull(SESSION_CHECK_TIMEOUT_MS) { loginRepository.validateToken() }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w(TAG, "Session check before the web view failed", e)
            }
            withContext(mainDispatcher) {
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
        CoroutineScope(SupervisorJob() + ioDispatcher).launch {
            loginRepository.revoke(accessToken, refreshToken)
        }
    }

    /** Network errors map to null, so the caller shows its own connection message. */
    private fun userMessage(e: Throwable, fallback: String): String? =
        if (e is IOException) null else e.message ?: fallback

    @JvmStatic
    fun cancel(job: Job?) {
        job?.cancel()
    }
}
