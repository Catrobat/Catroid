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
package org.catrobat.catroid.FaceRecognizer.env

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.Writer

/**
 * The face database is written with writeLines(). When the storage fills up
 * while a file is written, the old file must stay as it was and the write must
 * report failure; a partial file must never replace good training data.
 */
@RunWith(RobolectricTestRunner::class)
class FileUtilsWriteLinesTest {

    private lateinit var savedOpenForWriting: (File) -> Writer

    @Before
    fun setUp() {
        savedOpenForWriting = FileUtils.openForWriting
        FileUtils.init(ApplicationProvider.getApplicationContext<Context>())
        FileUtils.deleteAll()
    }

    @After
    fun tearDown() {
        FileUtils.openForWriting = savedOpenForWriting
        FileUtils.deleteAll()
    }

    @Test
    fun aSuccessfulWriteReplacesTheFile() {
        assertTrue(FileUtils.writeLines(FILE, OLD_LINES))

        assertTrue(FileUtils.writeLines(FILE, NEW_LINES))

        assertEquals(NEW_LINES, FileUtils.readLines(FILE))
        assertNoTemporaryFileLeft()
    }

    @Test
    fun aWriteThatFailsPartWayKeepsTheOldFileAndReportsFailure() {
        assertTrue(FileUtils.writeLines(FILE, OLD_LINES))
        FileUtils.openForWriting = { file -> FullDiskWriter(FileWriter(file), roomForChars = 10) }

        val written = FileUtils.writeLines(FILE, NEW_LINES)

        assertFalse("A write that ran out of space must report failure", written)
        assertEquals("The old file must be untouched", OLD_LINES, FileUtils.readLines(FILE))
        assertNoTemporaryFileLeft()
    }

    @Test
    fun aWriteThatFailsWhenFlushedKeepsTheOldFileAndReportsFailure() {
        assertTrue(FileUtils.writeLines(FILE, OLD_LINES))
        FileUtils.openForWriting = { file -> FullDiskWriter(FileWriter(file), roomForChars = Int.MAX_VALUE, failOnFlush = true) }

        val written = FileUtils.writeLines(FILE, NEW_LINES)

        assertFalse("A write whose flush failed must report failure", written)
        assertEquals("The old file must be untouched", OLD_LINES, FileUtils.readLines(FILE))
        assertNoTemporaryFileLeft()
    }

    private fun assertNoTemporaryFileLeft() {
        assertFalse("No temporary file may be left behind", FileUtils.file("$FILE.tmp").exists())
    }

    /** Takes [roomForChars] characters, then fails like a full disk. */
    private class FullDiskWriter(
        private val target: Writer,
        private var roomForChars: Int,
        private val failOnFlush: Boolean = false
    ) : Writer() {
        override fun write(cbuf: CharArray, off: Int, len: Int) {
            val fits = minOf(len, roomForChars)
            target.write(cbuf, off, fits)
            roomForChars -= fits
            if (fits < len) {
                throw IOException("No space left on device")
            }
        }

        override fun flush() {
            target.flush()
            if (failOnFlush) {
                throw IOException("No space left on device")
            }
        }

        override fun close() {
            target.close()
        }
    }

    private companion object {
        const val FILE = "label"
        val OLD_LINES = listOf("Person A", "Person B")
        val NEW_LINES = listOf("Person A", "Person B", "Person C with a long name")
    }
}
