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
 * The face database is three files whose lines refer to each other by person
 * index: label, data and model. Found in review: they were replaced one by one,
 * so deleting Alice from [Alice, Bob] could write the new label file ([Bob])
 * and then fail on data; after a restart Alice's photos belonged to Bob.
 * writeFiles() replaces all of them, or none.
 */
@RunWith(RobolectricTestRunner::class)
class FileUtilsWriteFilesTest {

    private lateinit var context: Context
    private lateinit var savedOpenForWriting: (File) -> Writer
    private lateinit var savedReplaceFile: (File, File) -> Unit
    private lateinit var savedSyncFile: (File) -> Unit

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        savedOpenForWriting = FileUtils.openForWriting
        savedReplaceFile = FileUtils.replaceFile
        savedSyncFile = FileUtils.syncFile
        FileUtils.init(context)
        FileUtils.deleteAll()
        assertTrue(FileUtils.writeFiles(OLD))
    }

    @After
    fun tearDown() {
        FileUtils.openForWriting = savedOpenForWriting
        FileUtils.replaceFile = savedReplaceFile
        FileUtils.syncFile = savedSyncFile
        FileUtils.deleteAll()
    }

    @Test
    fun allFilesAreReplacedTogether() {
        assertTrue(FileUtils.writeFiles(NEW))

        assertFiles(NEW)
        assertNoHelperFileLeft()
    }

    @Test
    fun aFileThatCannotBeWrittenKeepsEveryOldFile() {
        FileUtils.openForWriting = { file ->
            if (file.name.startsWith(FileUtils.DATA_FILE)) throw IOException("No space left on device")
            FileWriter(file, false)
        }

        assertFalse(FileUtils.writeFiles(NEW))

        assertFiles(OLD)
        assertNoHelperFileLeft()
    }

    /** The review case: the label file is already replaced when data fails. */
    @Test
    fun aFileThatCannotBeReplacedPutsBackTheFilesReplacedBeforeIt() {
        FileUtils.replaceFile = { source, target ->
            if (target.name == FileUtils.DATA_FILE && source.name.endsWith(".tmp")) {
                throw IOException("Could not replace data")
            }
            savedReplaceFile(source, target)
        }

        assertFalse(FileUtils.writeFiles(NEW))

        assertFiles(OLD)
        assertNoHelperFileLeft()
    }

    /** The app stopped half way through replacing: the next start puts the old files back. */
    @Test
    fun aSaveInterruptedByTheAppStoppingIsUndoneAtTheNextStart() {
        // As writeFiles() leaves it after backing up and replacing label, before data and model.
        FileUtils.writeLines(JOURNAL, OLD.keys.map { "$it 1" })
        FileUtils.file(FileUtils.LABEL_FILE).renameTo(FileUtils.file(FileUtils.LABEL_FILE + ".bak"))
        FileUtils.file(FileUtils.DATA_FILE).renameTo(FileUtils.file(FileUtils.DATA_FILE + ".bak"))
        FileUtils.file(FileUtils.MODEL_FILE).renameTo(FileUtils.file(FileUtils.MODEL_FILE + ".bak"))
        FileUtils.writeLines(FileUtils.LABEL_FILE, NEW.getValue(FileUtils.LABEL_FILE))

        FileUtils.recoverInterruptedWrite()

        assertFiles(OLD)
        assertNoHelperFileLeft()
    }

    /** A file that did not exist before the interrupted save is removed again. */
    @Test
    fun aFileCreatedByAnInterruptedSaveIsRemoved() {
        FileUtils.deleteAll()
        FileUtils.writeLines(FileUtils.LABEL_FILE, OLD.getValue(FileUtils.LABEL_FILE))
        FileUtils.writeLines(JOURNAL, listOf("${FileUtils.LABEL_FILE} 1", "${FileUtils.DATA_FILE} 0"))
        FileUtils.file(FileUtils.LABEL_FILE).renameTo(FileUtils.file(FileUtils.LABEL_FILE + ".bak"))
        FileUtils.writeLines(FileUtils.LABEL_FILE, NEW.getValue(FileUtils.LABEL_FILE))
        FileUtils.writeLines(FileUtils.DATA_FILE, NEW.getValue(FileUtils.DATA_FILE))

        FileUtils.recoverInterruptedWrite()

        assertEquals(OLD.getValue(FileUtils.LABEL_FILE), FileUtils.readLines(FileUtils.LABEL_FILE))
        assertFalse(FileUtils.file(FileUtils.DATA_FILE).exists())
        assertNoHelperFileLeft()
    }

    /**
     * Found in a review of the save path: the new files were only flushed to
     * the system, not written to storage, before they replaced the old ones and
     * the backups were deleted. A power cut a few seconds after a save could
     * leave empty or cut-off files and no backup. Every new file, and the
     * journal, is on storage before anything is replaced.
     */
    @Test
    fun everyNewFileIsOnStorageBeforeItReplacesAnOldOne() {
        val synced = mutableListOf<String>()
        FileUtils.syncFile = { file ->
            synced.add(file.name)
            savedSyncFile(file)
        }
        val moved = mutableListOf<String>()
        FileUtils.replaceFile = { source, target ->
            assertTrue("The journal is on storage before ${source.name} moves: $synced", "$JOURNAL.tmp" in synced)
            if (source.name.endsWith(".tmp")) {
                assertTrue("${source.name} is on storage before it moves: $synced", source.name in synced)
            }
            moved.add(target.name)
            savedReplaceFile(source, target)
        }

        assertTrue(FileUtils.writeFiles(NEW))

        assertFiles(NEW)
        assertTrue("Every file was moved: $moved", NEW.keys.all { it in moved })
    }

    /**
     * Found in the same review: when putting a backup back failed too, the
     * journal was deleted anyway. The next start could not finish the job, and
     * label and data no longer matched. The journal and the backups stay until
     * every file is back.
     */
    @Test
    fun aBackupThatCannotBePutBackIsRetriedAtTheNextStart() {
        var restoreFailures = 1
        FileUtils.replaceFile = { source, target ->
            when {
                target.name == FileUtils.DATA_FILE && source.name.endsWith(".tmp") ->
                    throw IOException("Could not replace data")
                source.name == FileUtils.LABEL_FILE + ".bak" && restoreFailures-- > 0 ->
                    throw IOException("Could not put label back")
                else -> savedReplaceFile(source, target)
            }
        }

        assertFalse(FileUtils.writeFiles(NEW))

        assertTrue("The journal stays for the next start", FileUtils.file(JOURNAL).exists())
        assertTrue("The label backup stays", FileUtils.file(FileUtils.LABEL_FILE + ".bak").exists())

        FileUtils.recoverInterruptedWrite()

        assertFiles(OLD)
        assertNoHelperFileLeft()
    }

    private fun assertFiles(expected: Map<String, List<String>>) {
        for ((name, lines) in expected) {
            assertEquals(name, lines, FileUtils.readLines(name))
        }
    }

    private fun assertNoHelperFileLeft() {
        val left = FileUtils.file(FileUtils.LABEL_FILE).parentFile!!.list()!!
            .filter { it.endsWith(".tmp") || it.endsWith(".bak") || it == JOURNAL }
        assertEquals("No temporary, backup or journal file may be left", emptyList<String>(), left)
    }

    private companion object {
        const val JOURNAL = "save.journal"
        val OLD = mapOf(
            FileUtils.LABEL_FILE to listOf("Alice", "Bob"),
            FileUtils.DATA_FILE to listOf("0 1.0 0.0", "1 0.0 1.0"),
            FileUtils.MODEL_FILE to listOf("v1 2 2")
        )
        val NEW = mapOf(
            FileUtils.LABEL_FILE to listOf("Bob"),
            FileUtils.DATA_FILE to listOf("0 0.0 1.0"),
            FileUtils.MODEL_FILE to listOf("v1 2 1")
        )
    }
}
