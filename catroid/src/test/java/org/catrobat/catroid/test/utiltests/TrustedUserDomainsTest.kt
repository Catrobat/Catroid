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
package org.catrobat.catroid.test.utiltests

import android.content.Context
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.TrustedDomainManager.addToUserTrustList
import org.catrobat.catroid.TrustedDomainManager.getUserTrustList
import org.catrobat.catroid.TrustedDomainManager.isURLTrusted
import org.catrobat.catroid.TrustedDomainManager.reset
import org.catrobat.catroid.TrustedDomainManager.setUserTrustList
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.Constants.TRUSTED_USER_DOMAINS_FILE
import org.catrobat.catroid.common.Constants.TRUST_LIST_JSON_ARRAY_NAME
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.utils.Utils
import org.json.JSONArray
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.rules.TemporaryFolder
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

@RunWith(PowerMockRunner::class)
@PrepareForTest(Utils::class, CatroidApplication::class, Constants::class, FlavoredConstants::class)
class TrustedUserDomainsTest {
    @Before
    fun setUp() {
        mockStatic(Utils::class.java)
        val stream = mock(InputStream::class.java)
        given(Utils.getInputStreamFromAsset(any(), anyString())).willReturn(stream)
        given(Utils.getJsonObjectFromInputStream(stream)).willReturn(JSONObject())

        mockStatic(CatroidApplication::class.java)
        val context = mock(Context::class.java)
        val directory = TemporaryFolder()
        directory.create()
        given(CatroidApplication.getAppContext()).willReturn(context)
        doReturn(directory.newFolder("filesDir")).`when`(context).filesDir
        doReturn(directory.newFolder("cacheDir")).`when`(context).cacheDir
    }

    @Test
    fun testEmptyTrustList() {
        assertFalse(isURLTrusted("https://www.tugraz.at"))
        assertFalse(isURLTrusted("https://www.wikipedia.net/blabla"))
        assertFalse(isURLTrusted("https://www.darknet.com/"))
    }

    @Test
    fun testSetUserTrustList() {
        assertTrue(setUserTrustList("tugraz.at\nwikipedia.net"))
        assertTrue(isURLTrusted("https://www.tugraz.at"))
        assertTrue(isURLTrusted("https://www.wikipedia.net/blabla"))
        assertFalse(isURLTrusted("https://www.darknet.com/"))
    }

    @Test
    fun testWhitespaces() {
        assertTrue(setUserTrustList(" t ugra z.a t   \n wik iped ia.net   "))
        assertTrue(isURLTrusted("https://www.tugraz.at"))
        assertTrue(isURLTrusted("https://www.wikipedia.net/blabla"))
        assertFalse(isURLTrusted("https://www.darknet.com/"))
    }

    @Test
    fun testAddToUserTrustList() {
        TRUSTED_USER_DOMAINS_FILE.createNewFile()
        given(Utils.getJsonObjectFromInputStream(any())).willReturn(
            constructTrustList(listOf("tugraz.at"))
        )
        addToUserTrustList("wikipedia.net")
        assertTrue(isURLTrusted("https://www.tugraz.at"))
        assertTrue(isURLTrusted("https://www.wikipedia.net/blabla"))
        assertFalse(isURLTrusted("https://www.darknet.com/"))
    }

    @Test
    fun testGetUserTrustList() {
        TRUSTED_USER_DOMAINS_FILE.createNewFile()
        given(Utils.getJsonObjectFromInputStream(any())).willReturn(
            constructTrustList(listOf("tugraz.at", "wikipedia.net"))
        )
        assertEquals("tugraz.at\nwikipedia.net", getUserTrustList())
    }

    @After
    fun tearDown() {
        reset()
    }

    private fun constructTrustList(domains: List<String>): JSONObject =
        JSONObject(mapOf(TRUST_LIST_JSON_ARRAY_NAME to JSONArray(domains)))
}
