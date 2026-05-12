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
package org.catrobat.catroid.test.soundrecorder

import android.media.MediaRecorder
import org.catrobat.catroid.soundrecorder.SoundRecorder
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.clearInvocations
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.io.File
import java.io.IOException

@RunWith(JUnit4::class)
class SoundRecorderTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var soundRecorder: SoundRecorder
    private lateinit var testFile: File
    private lateinit var mockRecorder: MediaRecorder

    @Before
    fun setUp() {
        mockRecorder = mock(MediaRecorder::class.java)

        // Setup default mock behavior for MediaRecorder
        doNothing().`when`(mockRecorder).reset()
        doNothing().`when`(mockRecorder).setAudioSource(MediaRecorder.AudioSource.MIC)
        doNothing().`when`(mockRecorder).setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        doNothing().`when`(mockRecorder).setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        doNothing().`when`(mockRecorder).setOutputFile(org.mockito.ArgumentMatchers.anyString())
        doNothing().`when`(mockRecorder).prepare()
        doNothing().`when`(mockRecorder).start()
        doNothing().`when`(mockRecorder).stop()
        doNothing().`when`(mockRecorder).release()

        testFile = File(tempFolder.root, "test_recording.m4a")
        soundRecorder = SoundRecorder(testFile.absolutePath, mockRecorder)
    }

    @After
    fun tearDown() {
        if (soundRecorder.isRecording) {
            soundRecorder.stop()
        }
    }

    @Test
    fun testConstructorInitializesCorrectly() {
        assertEquals(
            "Path should match constructor argument",
            testFile.absolutePath,
            soundRecorder.path
        )
        assertFalse("SoundRecorder should not be recording initially", soundRecorder.isRecording)
    }

    @Test
    fun testMaxAmplitudeDelegatesToMediaRecorder() {
        val expectedAmplitude = 12345
        `when`(mockRecorder.maxAmplitude).thenReturn(expectedAmplitude)

        val actualAmplitude = soundRecorder.maxAmplitude

        assertEquals(
            "maxAmplitude should return value from MediaRecorder",
            expectedAmplitude,
            actualAmplitude
        )
        verify(mockRecorder).maxAmplitude
    }

    @Test
    fun testStartDeletesExistingFile() {
        // Create an existing file with content
        testFile.createNewFile()
        testFile.writeText("old content")
        assertTrue("File should exist before start", testFile.exists())

        soundRecorder.start()

        // Verify that the file was deleted by checking:
        // In our test with mock, the file should NOT exist after delete() was called
        assertFalse("File should have been deleted by start()", testFile.exists())

        // Verify setOutputFile was called (MediaRecorder would recreate the file)
        verify(mockRecorder).setOutputFile(testFile.absolutePath)
    }

    @Test
    fun testStartCreatesDirectoryIfNotExists() {
        val fileInSubDir = File(tempFolder.root, "subdir/test.m4a")
        val subDirectory = fileInSubDir.parentFile
        assertFalse("Subdirectory should not exist initially", subDirectory?.exists() ?: true)

        val recorder = SoundRecorder(fileInSubDir.absolutePath, mockRecorder)

        recorder.start()

        assertTrue("Directory should be created", subDirectory?.exists() ?: false)
        assertTrue("Recording should be active", recorder.isRecording)
        verify(mockRecorder).setOutputFile(fileInSubDir.absolutePath)

        recorder.stop()
    }

    @Test(expected = IOException::class)
    fun testStartThrowsIOExceptionIfDirectoryCannotBeCreated() {
        // Create a file (not directory) at the path where we need a directory
        // Then try to create a subdirectory inside it - mkdirs() will fail
        val blockingFile = File(tempFolder.root, "blocker")
        blockingFile.createNewFile()  // Create a FILE named "blocker"
        assertTrue("Blocking file should exist", blockingFile.exists())
        assertTrue("Should be a file, not a directory", blockingFile.isFile)

        // Now try to create a recording in "blocker/subdir/test.m4a"
        // When mkdirs() tries to create "blocker/subdir", it will fail because
        // "blocker" is a file, not a directory
        val invalidPath = File(File(blockingFile, "subdir"), "test.m4a").absolutePath
        val invalidRecorder = SoundRecorder(invalidPath, mockRecorder)
        invalidRecorder.start()  // Should throw IOException: "Path to file could not be created."
    }

    @Test
    fun testStartCallsMediaRecorderMethodsInCorrectOrder() {
        soundRecorder.start()

        val inOrder = org.mockito.Mockito.inOrder(mockRecorder)
        inOrder.verify(mockRecorder).reset()
        inOrder.verify(mockRecorder).setAudioSource(MediaRecorder.AudioSource.MIC)
        inOrder.verify(mockRecorder).setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        inOrder.verify(mockRecorder).setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        inOrder.verify(mockRecorder).setOutputFile(testFile.absolutePath)
        inOrder.verify(mockRecorder).prepare()
        inOrder.verify(mockRecorder).start()

        assertTrue("isRecording should be true after start", soundRecorder.isRecording)
    }

    @Test
    fun testDoubleStartDoesNotThrow() {
        soundRecorder.start()
        assertTrue("Should be recording after first start", soundRecorder.isRecording)

        // Second start should not throw
        soundRecorder.start()

        // Verify that MediaRecorder methods were called twice
        verify(mockRecorder, times(2)).reset()
        verify(mockRecorder, times(2)).setAudioSource(MediaRecorder.AudioSource.MIC)
        verify(mockRecorder, times(2)).setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        verify(mockRecorder, times(2)).setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        verify(mockRecorder, times(2)).setOutputFile(testFile.absolutePath)
        verify(mockRecorder, times(2)).prepare()
        verify(mockRecorder, times(2)).start()

        assertTrue("Should still be recording after second start", soundRecorder.isRecording)
    }

    @Test
    fun testStopCallsMediaRecorderMethodsInCorrectOrder() {
        soundRecorder.start()
        assertTrue("Should be recording before stop", soundRecorder.isRecording)
        clearInvocations(mockRecorder)

        soundRecorder.stop()

        val inOrder = org.mockito.Mockito.inOrder(mockRecorder)
        inOrder.verify(mockRecorder).stop()
        inOrder.verify(mockRecorder).reset()
        inOrder.verify(mockRecorder).release()

        assertFalse("isRecording should be false after stop", soundRecorder.isRecording)
    }

    @Test
    fun testStopWithoutStartDoesNotThrow() {
        // Should not propagate exception
        soundRecorder.stop()

        // But should still cleanup
        verify(mockRecorder).reset()
        verify(mockRecorder).release()
        assertFalse("isRecording should be false after stop", soundRecorder.isRecording)
    }

    @Test
    fun testStopHandlesRuntimeExceptionGracefully() {
        soundRecorder.start()
        clearInvocations(mockRecorder)

        org.mockito.Mockito.doThrow(RuntimeException("No valid audio data"))
            .`when`(mockRecorder).stop()

        // Should not propagate exception
        soundRecorder.stop()

        // But should still cleanup
        verify(mockRecorder).reset()
        verify(mockRecorder).release()
        assertFalse("isRecording should be false after stop", soundRecorder.isRecording)
    }

    @Test
    fun testDoubleStopDoesNotThrow() {
        soundRecorder.start()
        clearInvocations(mockRecorder)
        soundRecorder.stop()

        assertFalse("Should not be recording after stop", soundRecorder.isRecording)

        // Second stop should not throw
        soundRecorder.stop()

        // Verify methods were called twice
        verify(mockRecorder, times(2)).stop()
        verify(mockRecorder, times(2)).reset()
        verify(mockRecorder, times(2)).release()

        assertFalse("Should still not be recording after second stop", soundRecorder.isRecording)
    }
}

