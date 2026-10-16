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
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.io.Reader
import java.io.Writer
import java.nio.channels.FileChannel
import java.nio.file.StandardOpenOption

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
    private lateinit var savedOpenForReading: (File) -> Reader
    private lateinit var savedSyncDirectory: (File) -> Unit
    private lateinit var savedDeleteFile: (File) -> Boolean
    private lateinit var savedOpenDirectory: (File) -> FileChannel

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        savedOpenForWriting = FileUtils.openForWriting
        savedReplaceFile = FileUtils.replaceFile
        savedSyncFile = FileUtils.syncFile
        savedOpenForReading = FileUtils.openForReading
        savedSyncDirectory = FileUtils.syncDirectory
        savedDeleteFile = FileUtils.deleteFile
        savedOpenDirectory = FileUtils.openDirectory
        FileUtils.init(context)
        FileUtils.deleteAll()
        assertTrue(FileUtils.writeFiles(OLD))
    }

    @After
    fun tearDown() {
        FileUtils.openForWriting = savedOpenForWriting
        FileUtils.replaceFile = savedReplaceFile
        FileUtils.syncFile = savedSyncFile
        FileUtils.openForReading = savedOpenForReading
        FileUtils.syncDirectory = savedSyncDirectory
        FileUtils.deleteFile = savedDeleteFile
        FileUtils.openDirectory = savedOpenDirectory
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

    /**
     * Found in review: a journal that could not be read was taken for an empty
     * one, so recovery restored nothing, reported success and deleted the
     * backups. After an interrupted delete of Alice, her photos then matched
     * Bob. A journal that cannot be read keeps every recovery file, and the
     * next start restores them.
     */
    @Test
    fun aJournalThatCannotBeReadKeepsEveryBackupForTheNextStart() {
        FileUtils.writeLines(JOURNAL, OLD.keys.map { "$it 1" })
        for (name in OLD.keys) {
            FileUtils.file(name).renameTo(FileUtils.file("$name.bak"))
        }
        FileUtils.writeLines(FileUtils.LABEL_FILE, NEW.getValue(FileUtils.LABEL_FILE))
        FileUtils.openForReading = { file ->
            if (file.name == JOURNAL) throw IOException("I/O error")
            FileReader(file)
        }

        assertFalse("Recovery must not report success", FileUtils.recoverInterruptedWrite())

        assertTrue("The journal stays", FileUtils.file(JOURNAL).exists())
        for (name in OLD.keys) {
            assertTrue("$name.bak stays", FileUtils.file("$name.bak").exists())
        }

        FileUtils.openForReading = savedOpenForReading
        assertTrue(FileUtils.recoverInterruptedWrite())
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

    // ---------------- Directory syncs around the journal ----------------

    /**
     * Found in review: the journal's new name was not synced before the files
     * moved. A power cut could then keep the moves but lose the journal, and the
     * next start deleted the backups as leftovers instead of restoring them.
     */
    @Test
    fun theJournalsNameIsOnStorageBeforeAnyFileMoves() {
        val events = mutableListOf<String>()
        FileUtils.syncDirectory = { folder ->
            events.add("folder synced, journal ${if (FileUtils.file(JOURNAL).exists()) "there" else "gone"}")
            savedSyncDirectory(folder)
        }
        FileUtils.replaceFile = { source, target ->
            assertTrue(
                "The folder is synced with the journal in it before ${source.name} moves: $events",
                "folder synced, journal there" in events
            )
            savedReplaceFile(source, target)
        }

        assertTrue(FileUtils.writeFiles(NEW))

        assertFiles(NEW)
        assertNoHelperFileLeft()
    }

    @Test
    fun aFolderSyncThatFailsBeforeTheMovesKeepsTheOldFiles() {
        FileUtils.syncDirectory = { throw IOException("Could not sync the folder") }

        assertFalse(FileUtils.writeFiles(NEW))

        assertFiles(OLD)
        assertNoHelperFileLeft()
    }

    /**
     * Found in the same review: the journal and the backups were deleted before
     * the folder was synced, and a journal that could not be deleted did not stop
     * the backups from going. A power cut could leave the journal with only some
     * backups, pairing old photos with new names. The backups only go once the
     * journal is deleted and that is on storage.
     */
    @Test
    fun theBackupsStayUntilTheJournalIsGoneForGood() {
        var journalGoneForGood = false
        FileUtils.syncDirectory = { folder ->
            savedSyncDirectory(folder)
            if (!FileUtils.file(JOURNAL).exists()) {
                journalGoneForGood = true
            }
        }
        FileUtils.deleteFile = { file ->
            if (file.name.endsWith(".bak")) {
                assertTrue("${file.name} is deleted only after the journal is gone for good", journalGoneForGood)
            }
            savedDeleteFile(file)
        }

        assertTrue(FileUtils.writeFiles(NEW))

        assertFiles(NEW)
        assertNoHelperFileLeft()
    }

    @Test
    fun aFolderSyncThatFailsAfterTheMovesPutsTheOldFilesBack() {
        var moves = 0
        FileUtils.replaceFile = { source, target ->
            moves++
            savedReplaceFile(source, target)
        }
        FileUtils.syncDirectory = { folder ->
            if (moves > 0) throw IOException("Could not sync the folder")
            savedSyncDirectory(folder)
        }

        assertFalse(FileUtils.writeFiles(NEW))

        assertFiles(OLD)
    }

    @Test
    fun aJournalThatCannotBeDeletedPutsTheOldFilesBackWithTheirBackups() {
        var journalDeletable = false
        FileUtils.deleteFile = { file ->
            if (file.name == JOURNAL && !journalDeletable) false else savedDeleteFile(file)
        }

        assertFalse(FileUtils.writeFiles(NEW))

        assertFiles(OLD)
        journalDeletable = true
        FileUtils.recoverInterruptedWrite()
        assertFiles(OLD)
        assertNoHelperFileLeft()
    }

    @Test
    fun aFolderSyncThatFailsAfterTheJournalIsDeletedKeepsTheBackupsAndTheNewFiles() {
        FileUtils.syncDirectory = { folder ->
            if (!FileUtils.file(JOURNAL).exists() && FileUtils.file(FileUtils.LABEL_FILE + ".bak").exists()) {
                throw IOException("Could not sync the folder")
            }
            savedSyncDirectory(folder)
        }

        assertTrue(FileUtils.writeFiles(NEW))

        assertFiles(NEW)
        assertTrue("The backups stay", FileUtils.file(FileUtils.LABEL_FILE + ".bak").exists())
        FileUtils.syncDirectory = savedSyncDirectory
        FileUtils.recoverInterruptedWrite()
        assertFiles(NEW)
        assertNoHelperFileLeft()
    }

    @Test
    fun recoveryKeepsTheJournalWhenTheRestoredFilesCannotBeSynced() {
        FileUtils.writeLines(JOURNAL, OLD.keys.map { "$it 1" })
        for (name in OLD.keys) {
            FileUtils.file(name).renameTo(FileUtils.file("$name.bak"))
        }
        FileUtils.writeLines(FileUtils.LABEL_FILE, NEW.getValue(FileUtils.LABEL_FILE))
        FileUtils.syncDirectory = { throw IOException("Could not sync the folder") }

        assertFalse(FileUtils.recoverInterruptedWrite())

        assertTrue("The journal stays for the next start", FileUtils.file(JOURNAL).exists())
        FileUtils.syncDirectory = savedSyncDirectory
        assertTrue(FileUtils.recoverInterruptedWrite())
        assertFiles(OLD)
        assertNoHelperFileLeft()
    }

    // ---------------- Backups left by a completed save ----------------

    /**
     * Found in review: when the folder sync after the journal's deletion failed,
     * the save reported success and kept the backups of the files before it. The
     * next save then backed up only some files over them before failing, and
     * recovery paired the newer names with the older photos: Alice's photos
     * matched Bob. Leftover backups are cleared, with the folder synced, before a
     * new journal is written.
     */
    @Test
    fun aSaveAfterLeftoverBackupsDoesNotMixTheirFilesWithNewerOnes() {
        leaveBackupsOfTheOldFiles()
        FileUtils.replaceFile = { source, target ->
            if (target.name == FileUtils.DATA_FILE + ".bak") throw IOException("Could not back up data")
            savedReplaceFile(source, target)
        }

        assertFalse(FileUtils.writeFiles(NEWER))

        assertFiles(NEW)
        FileUtils.replaceFile = savedReplaceFile
        FileUtils.recoverInterruptedWrite()
        assertFiles(NEW)
    }

    @Test
    fun leftoverBackupsThatCannotBeSyncedStopTheNextSaveBeforeItsJournal() {
        leaveBackupsOfTheOldFiles()
        FileUtils.syncDirectory = { throw IOException("Could not sync the folder") }

        assertFalse(FileUtils.writeFiles(NEWER))

        assertFiles(NEW)
        assertFalse("No journal was written", FileUtils.file(JOURNAL).exists())
    }

    @Test
    fun leftoverBackupsThatCannotBeDeletedStopTheNextSave() {
        leaveBackupsOfTheOldFiles()
        FileUtils.deleteFile = { file -> if (file.name.endsWith(".bak")) false else savedDeleteFile(file) }

        assertFalse(FileUtils.writeFiles(NEWER))

        assertFiles(NEW)
        assertFalse("No journal was written", FileUtils.file(JOURNAL).exists())
    }

    @Test
    fun recoveryKeepsLeftoverBackupsUntilTheFolderIsSynced() {
        leaveBackupsOfTheOldFiles()
        FileUtils.syncDirectory = { throw IOException("Could not sync the folder") }

        assertFalse(FileUtils.recoverInterruptedWrite())

        assertTrue("The backups stay", FileUtils.file(FileUtils.LABEL_FILE + ".bak").exists())
        FileUtils.syncDirectory = savedSyncDirectory
        assertTrue(FileUtils.recoverInterruptedWrite())
        assertFiles(NEW)
        assertNoHelperFileLeft()
    }

    /** Saves NEW while the folder sync after the journal's deletion fails: the OLD backups stay. */
    private fun leaveBackupsOfTheOldFiles() {
        FileUtils.syncDirectory = { folder ->
            if (!FileUtils.file(JOURNAL).exists() && FileUtils.file(FileUtils.LABEL_FILE + ".bak").exists()) {
                throw IOException("Could not sync the folder")
            }
            savedSyncDirectory(folder)
        }
        assertTrue(FileUtils.writeFiles(NEW))
        assertTrue("The backups of the old files stay", FileUtils.file(FileUtils.DATA_FILE + ".bak").exists())
        FileUtils.syncDirectory = savedSyncDirectory
    }

    // ---------------- Rolling back a first save ----------------

    /**
     * Found in review: rolling back a save removes the files that did not exist
     * before it, but a removal that failed was only logged. Recovery then
     * reported success and deleted the journal, so the half-saved names stayed,
     * and nothing could remove them later: Alice remained with no photos. A file
     * that is already gone counts as removed; one that cannot be removed keeps
     * the journal for the next attempt.
     */
    @Test
    fun aFirstSaveWhoseNewFileCannotBeRemovedKeepsTheJournalUntilItIs() {
        FileUtils.deleteAll()
        var labelDeletable = false
        FileUtils.replaceFile = { source, target ->
            if (target.name == FileUtils.DATA_FILE) throw IOException("Could not replace data")
            savedReplaceFile(source, target)
        }
        FileUtils.deleteFile = { file ->
            if (file.name == FileUtils.LABEL_FILE && !labelDeletable) false else savedDeleteFile(file)
        }

        assertFalse(FileUtils.writeFiles(NEW))

        assertTrue("The journal stays for the next attempt", FileUtils.file(JOURNAL).exists())
        assertFalse("Recovery reports that it could not complete", FileUtils.recoverInterruptedWrite())
        assertTrue("The journal still stays", FileUtils.file(JOURNAL).exists())

        labelDeletable = true
        FileUtils.replaceFile = savedReplaceFile
        assertTrue(FileUtils.recoverInterruptedWrite())
        for (name in NEW.keys) {
            assertFalse("$name of the failed first save is removed", FileUtils.file(name).exists())
        }
        assertNoHelperFileLeft()

        assertTrue(FileUtils.writeFiles(NEW))
        assertFiles(NEW)
    }

    // ---------------- A folder that cannot be opened to sync it ----------------

    /**
     * Found in review: the folder sync took a folder that could not be opened
     * (no file descriptors left, an I/O error) for one that needs no sync, so the
     * files moved and the backups went without the folder being on storage. The
     * real sync now reports such a failure, here with a failing open.
     */
    @Test
    fun aFolderThatCannotBeOpenedStopsTheSaveBeforeAnyFileMoves() {
        FileUtils.syncDirectory = FileUtils.syncDirectoryOnStorage
        FileUtils.openDirectory = { throw IOException("Too many open files") }
        FileUtils.replaceFile = { source, _ -> fail("${source.name} moved although the folder could not be synced") }

        assertFalse(FileUtils.writeFiles(NEW))

        assertFiles(OLD)
        assertNoHelperFileLeft()
    }

    @Test
    fun aFileSystemThatCannotOpenFoldersStopsTheSaveBeforeAnyFileMoves() {
        FileUtils.syncDirectory = FileUtils.syncDirectoryOnStorage
        FileUtils.openDirectory = { throw UnsupportedOperationException("Folders cannot be opened") }
        FileUtils.replaceFile = { source, _ -> fail("${source.name} moved although the folder could not be synced") }

        assertFalse(FileUtils.writeFiles(NEW))

        assertFiles(OLD)
        assertNoHelperFileLeft()
    }

    @Test
    fun aFolderThatCannotBeOpenedAtCleanupKeepsTheBackupsAndStopsTheNextSave() {
        FileUtils.syncDirectory = FileUtils.syncDirectoryOnStorage
        var openFails = false
        FileUtils.openDirectory = { folder ->
            if (openFails || (!FileUtils.file(JOURNAL).exists() && FileUtils.file(FileUtils.LABEL_FILE + ".bak").exists())) {
                throw IOException("Too many open files")
            }
            syncableChannel(folder)
        }

        assertTrue(FileUtils.writeFiles(NEW))

        assertFiles(NEW)
        assertTrue("The backups stay", FileUtils.file(FileUtils.LABEL_FILE + ".bak").exists())

        openFails = true
        assertFalse("A save cannot start before the backups are settled", FileUtils.writeFiles(NEWER))
        assertFiles(NEW)
        assertFalse("No journal was written", FileUtils.file(JOURNAL).exists())

        openFails = false
        FileUtils.openDirectory = { folder -> syncableChannel(folder) }
        assertTrue(FileUtils.writeFiles(NEWER))
        assertFiles(NEWER)
        assertNoHelperFileLeft()
    }

    /**
     * A channel the real sync can force. Some test hosts (Windows) cannot open a
     * folder at all; a file next to it stands in for it there.
     */
    private fun syncableChannel(folder: File): FileChannel {
        val stand = File(context.cacheDir, "sync-probe").apply { if (!exists()) createNewFile() }
        return try {
            FileChannel.open(folder.toPath(), StandardOpenOption.READ)
        } catch (e: IOException) {
            FileChannel.open(stand.toPath(), StandardOpenOption.READ)
        }
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
        val NEWER = mapOf(
            FileUtils.LABEL_FILE to listOf("Bob", "Charlie"),
            FileUtils.DATA_FILE to listOf("0 0.0 1.0", "1 0.6 0.8"),
            FileUtils.MODEL_FILE to listOf("v1 2 2")
        )
    }
}
