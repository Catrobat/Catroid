/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026  The Catrobat Team
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

import org.catrobat.catroid.web.Cookie
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CookieTest {
    @Test
    fun `test generateCookieString`() {
        val cookie = Cookie("sessionID", "12345")
        assertEquals("sessionID=12345; HttpOnly; Secure; Path=/; SameSite=Strict", cookie.generateCookieString())
    }

    @Test
    fun `cookie string contains HttpOnly flag`() {
        val cookie = Cookie("BEARER", "jwt-token-value")
        val cookieString = cookie.generateCookieString()
        assertTrue(cookieString.contains("HttpOnly"))
        assertTrue(cookieString.contains("Secure"))
        assertTrue(cookieString.contains("SameSite=Strict"))
    }

    @Test
    fun `cookie string starts with name value pair`() {
        val cookie = Cookie("TOKEN", "abc123")
        assertTrue(cookie.generateCookieString().startsWith("TOKEN=abc123"))
    }

    @Test
    fun `cookie string omits Secure flag when secure is false`() {
        val cookie = Cookie("BEARER", "jwt-token-value", secure = false)
        val cookieString = cookie.generateCookieString()
        assertFalse(cookieString.contains("Secure"))
        assertTrue(cookieString.contains("HttpOnly"))
        assertTrue(cookieString.contains("SameSite=Strict"))
    }
}
