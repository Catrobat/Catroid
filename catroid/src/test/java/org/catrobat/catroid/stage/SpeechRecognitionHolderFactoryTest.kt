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

package org.catrobat.catroid.stage

import android.content.Context
import org.catrobat.catroid.utils.MobileServiceAvailability
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class SpeechRecognitionHolderFactoryTest {

    @Mock
    private lateinit var googleRecognitionHolder: SpeechRecognitionHolderInterface
    @Mock
    private lateinit var huaweiRecognitionHolder: SpeechRecognitionHolderInterface
    @Mock
    private lateinit var mobileServiceAvailability: MobileServiceAvailability
    @Mock
    private lateinit var context: Context

    private lateinit var factory: SpeechRecognitionHolderFactory

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        factory = SpeechRecognitionHolderFactory(
            googleRecognitionHolder,
            huaweiRecognitionHolder,
            mobileServiceAvailability
        )
    }

    @Test
    fun testGmsRecognizerIsReturnedWhenAvailable() {
        Mockito.`when`(mobileServiceAvailability.isGmsAvailable(context)).thenReturn(true)

        assertTrue(factory.isRecognitionAvailable(context))
        assertEquals(googleRecognitionHolder, factory.instance)
    }

    @Test
    fun testHmsRecognizerIsReturnedWhenGmsIsNotAvailable() {
        Mockito.`when`(mobileServiceAvailability.isGmsAvailable(context)).thenReturn(false)
        Mockito.`when`(mobileServiceAvailability.isHmsAvailable(context)).thenReturn(true)

        assertTrue(factory.isRecognitionAvailable(context))
        assertEquals(huaweiRecognitionHolder, factory.instance)
    }

    @Test
    fun testNoneIsReturnedWhenNeitherIsAvailable() {
        Mockito.`when`(mobileServiceAvailability.isGmsAvailable(context)).thenReturn(false)
        Mockito.`when`(mobileServiceAvailability.isHmsAvailable(context)).thenReturn(false)

        assertFalse(factory.isRecognitionAvailable(context))
        assertNotEquals(googleRecognitionHolder, factory.instance)
        assertNotEquals(huaweiRecognitionHolder, factory.instance)
    }
}
