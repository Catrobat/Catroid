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

package org.catrobat.catroid.test.web

import com.squareup.moshi.JsonDataException
import org.catrobat.catroid.R
import org.catrobat.catroid.web.LoginHelper
import org.catrobat.catroid.web.LoginRepository.LoginRefused
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException

@RunWith(JUnit4::class)
class LoginHelperTest {

    @Test
    fun `offline asks to check the connection`() {
        assertEquals(R.string.error_internet_connection, LoginHelper.failureMessage(IOException("Unable to resolve host")))
    }

    @Test
    fun `a 401 means wrong username or password`() {
        assertEquals(R.string.sign_in_wrong_credentials, LoginHelper.failureMessage(LoginRefused(401, null)))
    }

    @Test
    fun `a suspended account says so instead of blaming the password`() {
        assertEquals(R.string.sign_in_account_suspended, LoginHelper.failureMessage(LoginRefused(401, "account_suspended")))
    }

    @Test
    fun `server errors and parser errors show the generic sign-in error, never their own text`() {
        assertEquals(R.string.sign_in_error, LoginHelper.failureMessage(LoginRefused(502, null)))
        assertEquals(R.string.sign_in_error, LoginHelper.failureMessage(JsonDataException("Required value 'refreshToken' missing at \$")))
    }
}
