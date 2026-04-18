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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object LoginHelper {

    @JvmStatic
    fun performLogin(
        loginRepository: LoginRepository,
        username: String,
        password: String,
        onSuccess: Runnable,
        onError: java.util.function.Consumer<String>
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = loginRepository.login(username, password)
            withContext(Dispatchers.Main) {
                result.fold(
                    onSuccess = { onSuccess.run() },
                    onFailure = { e -> onError.accept(e.message ?: "Login failed") }
                )
            }
        }
    }

    @JvmStatic
    fun performGoogleLogin(
        loginRepository: LoginRepository,
        idToken: String,
        onSuccess: Runnable,
        onError: java.util.function.Consumer<String>
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = loginRepository.loginWithGoogle(idToken)
            withContext(Dispatchers.Main) {
                result.fold(
                    onSuccess = { onSuccess.run() },
                    onFailure = { e -> onError.accept(e.message ?: "Google login failed") }
                )
            }
        }
    }

    @JvmStatic
    fun performLogout(loginRepository: LoginRepository, onComplete: Runnable) {
        CoroutineScope(Dispatchers.IO).launch {
            loginRepository.logout()
            withContext(Dispatchers.Main) {
                onComplete.run()
            }
        }
    }
}
