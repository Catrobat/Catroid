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
package org.catrobat.catroid.test.content

import android.speech.tts.UtteranceProgressListener
import org.catrobat.catroid.stage.UtteranceProgressListenerContainer
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import java.io.File

import junit.framework.Assert.assertTrue
import junit.framework.Assert.assertFalse

@RunWith(JUnit4::class)
class UtteranceProgressListenerContainerTest {

    private lateinit var container: UtteranceProgressListenerContainer
    private lateinit var existingFile: File
    private lateinit var nonExistingFile: File
    private val utteranceId1 = "hash1"
    private val utteranceId2 = "hash2"

    @Before
    fun setUp() {
        container = UtteranceProgressListenerContainer()

        nonExistingFile = PowerMockito.mock(File::class.java)
        existingFile = PowerMockito.mock(File::class.java)

        Mockito.`when`(nonExistingFile.exists()).thenReturn(false)
        Mockito.`when`(existingFile.exists()).thenReturn(true)
    }

    @Test
    fun testExistingSpeechFile() {
        val listener = Mockito.mock(UtteranceProgressListener::class.java)
        val returnValue = container.addUtteranceProgressListener(existingFile, listener, utteranceId1)
        assertFalse(returnValue)
        Mockito.verify(listener, Mockito.times(1)).onDone(utteranceId1)
    }

    @Test
    fun testNonExistingSpeechFile() {
        val listener = Mockito.mock(UtteranceProgressListener::class.java)
        val returnValue = container.addUtteranceProgressListener(nonExistingFile, listener, utteranceId1)
        assertTrue(returnValue)
        container.onDone(utteranceId1)
        Mockito.verify(listener, Mockito.times(1)).onDone(utteranceId1)
    }

    @Test
    fun testSpeechFilesWithSameHashValue() {
        val listener1 = Mockito.mock(UtteranceProgressListener::class.java)
        var returnValue = container.addUtteranceProgressListener(nonExistingFile, listener1, utteranceId1)
        assertTrue(returnValue)
        val listener2 = Mockito.mock(UtteranceProgressListener::class.java)
        returnValue = container.addUtteranceProgressListener(nonExistingFile, listener2, utteranceId1)
        assertFalse(returnValue)
        container.onDone(utteranceId1)
        Mockito.verify(listener1, Mockito.times(1)).onDone(utteranceId1)
        Mockito.verify(listener2, Mockito.times(1)).onDone(utteranceId1)
    }

    @Test
    fun testNormalBehavior() {
        val listener1 = Mockito.mock(UtteranceProgressListener::class.java)
        val listener2 = Mockito.mock(UtteranceProgressListener::class.java)
        val listener3 = Mockito.mock(UtteranceProgressListener::class.java)
        var returnValue = container.addUtteranceProgressListener(nonExistingFile, listener1, utteranceId1)
        assertTrue(returnValue)
        returnValue = container.addUtteranceProgressListener(nonExistingFile, listener2, utteranceId2)
        assertTrue(returnValue)
        container.onDone(utteranceId1)
        Mockito.verify(listener1, Mockito.times(1)).onDone(utteranceId1)
        returnValue = container.addUtteranceProgressListener(existingFile, listener3, utteranceId1)
        assertFalse(returnValue)
        Mockito.verify(listener3, Mockito.times(1)).onDone(utteranceId1)
        container.onDone(utteranceId2)
        Mockito.verify(listener2, Mockito.times(1)).onDone(utteranceId2)
    }
}
