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
package org.catrobat.catroid.test.utiltests

import org.catrobat.catroid.WhiteListManager
import org.catrobat.catroid.WhiteListManager.checkIfURLIsWhitelisted
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.utils.Utils
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.BDDMockito.given
import org.powermock.api.mockito.PowerMockito.doReturn
import org.powermock.api.mockito.PowerMockito.mock
import org.powermock.api.mockito.PowerMockito.mockStatic
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.io.InputStream
import java.util.regex.Matcher
import java.util.regex.Pattern

@RunWith(PowerMockRunner::class)
@PrepareForTest(Utils::class, WhiteListManager::class)
class URLWhiteListTest {
    private lateinit var userWhiteListMatcher: Matcher

    @Before
    fun setUp() {
        userWhiteListMatcher = mock(Matcher::class.java)
        WhiteListManager.userWhiteListPattern = mock(Pattern::class.java)
        doReturn(userWhiteListMatcher).`when`(WhiteListManager.userWhiteListPattern)!!
            .matcher(anyString())

        mockStatic(Utils::class.java)
        val stream = mock(InputStream::class.java)
        val urlWhiteList = constructWhitelist(listOf("tugraz.at", "net", "wikipedia.org", "ac.at"))
        given(Utils.getInputStreamFromAsset(any(), anyString())).willReturn(stream)
        given(Utils.getJsonObjectFromInputStream(stream)).willReturn(urlWhiteList)
    }

    @Test
    fun testNoProtocol() {
        doReturn(false).`when`(userWhiteListMatcher).matches()
        assertTrue(checkIfURLIsWhitelisted("https://www.tugraz.at"))
        assertFalse(checkIfURLIsWhitelisted("www.tugraz.at"))
    }

    @Test
    fun testEnding() {
        doReturn(false).`when`(userWhiteListMatcher).matches()
        assertTrue(checkIfURLIsWhitelisted("https://www.wikipedia.net/blabla"))
        assertFalse(checkIfURLIsWhitelisted("https://something.net.com/blabla"))
    }

    @Test
    fun testCommonInternetScheme() {
        doReturn(false).`when`(userWhiteListMatcher).matches()
        assertTrue(checkIfURLIsWhitelisted("http://www.ist.tugraz.at:8080/blablabla"))
        assertTrue(checkIfURLIsWhitelisted("http://www.ist.tugraz.at:8080/"))
        assertTrue(checkIfURLIsWhitelisted("http://connect4.ist.tugraz.at"))
        assertFalse(checkIfURLIsWhitelisted("http://myaccount:mypassword@www.ist.tugraz.at:8080/blablabla"))
        assertFalse(checkIfURLIsWhitelisted("http://myaccount:@www.ist.tugraz.at/blablabla"))
        assertFalse(checkIfURLIsWhitelisted("http://myaccount:mypassword@www.ist.tugraz.at/blablabla"))
        assertFalse(checkIfURLIsWhitelisted("http://www.tugraz.at:/"))
    }

    @Test
    fun testDomainEndsWithEntry() {
        doReturn(false).`when`(userWhiteListMatcher).matches()
        assertTrue(checkIfURLIsWhitelisted("https://www.wikipedia.org/hallo"))
        assertFalse(checkIfURLIsWhitelisted("https://wikipedia.org.darknet.com/trallala"))
        assertFalse(checkIfURLIsWhitelisted("https://wikipedia.orgxxx/trallala"))
        assertFalse(checkIfURLIsWhitelisted("https://www.darknet.com/wikipedia.org/"))
    }

    @Test
    fun testDomainExtension() {
        doReturn(false).`when`(userWhiteListMatcher).matches()
        assertFalse(checkIfURLIsWhitelisted("https://wwwwikipedia.org/hallo"))
    }

    @Test
    fun testEscapedDots() {
        doReturn(false).`when`(userWhiteListMatcher).matches()
        assertTrue(checkIfURLIsWhitelisted("https://www.tugraz.ac.at/hallo"))
        assertFalse(checkIfURLIsWhitelisted("https://www.tugraz.acbat/hallo"))
    }

    @Test
    fun testUserDomain() {
        doReturn(true).`when`(userWhiteListMatcher).matches()
        assertTrue(checkIfURLIsWhitelisted("https://something.net.com/blabla"))
        assertTrue(checkIfURLIsWhitelisted("https://www.darknet.com/wikipedia.org/"))
    }

    private fun constructWhitelist(domains: List<String>): JSONObject =
        JSONObject(mapOf(Constants.WHITELIST_JSON_ARRAY_NAME to JSONArray(domains)))
}
