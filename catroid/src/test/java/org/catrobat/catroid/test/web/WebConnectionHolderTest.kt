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
package org.catrobat.catroid.test.web

import org.catrobat.catroid.web.WebConnection
import org.catrobat.catroid.web.WebConnectionHolder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import java.util.ArrayList
import java.util.Collections

@RunWith(JUnit4::class)
class WebConnectionHolderTest {
    private lateinit var webConnection: WebConnection
    private lateinit var webConnectionHolder: WebConnectionHolder

    companion object {
        private const val MAX_CONNECTIONS = 10
    }

    @Before
    fun setUp() {
        webConnection = Mockito.mock(WebConnection::class.java)
        webConnectionHolder = WebConnectionHolder()
    }

    @Test
    fun testAddConnection() {
        assertTrue(webConnectionHolder.addConnection(webConnection))
        assertEquals(webConnectionHolder.connections.size, 1)
    }

    @Test
    fun testAddConnectionOverLimit() {
        webConnectionHolder.connections = ArrayList(
            Collections.nCopies(MAX_CONNECTIONS - 1, webConnection))
        assertTrue(webConnectionHolder.addConnection(webConnection))
        assertFalse(webConnectionHolder.addConnection(webConnection))
        assertEquals(webConnectionHolder.connections.size, MAX_CONNECTIONS)
    }

    @Test
    fun testRemoveConnection() {
        assertTrue(webConnectionHolder.addConnection(webConnection))
        webConnectionHolder.removeConnection(webConnection)
        assertTrue(webConnectionHolder.connections.isEmpty())
    }

    @Test
    fun testWebConnectionHolderOnPause() {
        webConnectionHolder.connections = ArrayList(Collections.nCopies(5, webConnection))
        webConnectionHolder.onPause()
        assertTrue(webConnectionHolder.connections.isEmpty())
        Mockito.verify(webConnection, Mockito.times(5)).cancelCall()
    }
}
