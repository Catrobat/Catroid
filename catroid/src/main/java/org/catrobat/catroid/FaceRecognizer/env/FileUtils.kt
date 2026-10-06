package org.catrobat.catroid.FaceRecognizer.env

import android.content.Context
import android.util.Log
import androidx.annotation.VisibleForTesting
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.io.RandomAccessFile
import java.io.Reader
import java.io.Writer
import java.nio.channels.FileChannel
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption

/**
 * Single source of truth for where face data lives.
 * 
 * Everything is stored in  filesDir/facerecog/
 * label : one person name per line, line number = person index
 * data  : one embedding per line,  "<personIndex> v0 v1 ... v127"
 * model : thresholds and one centroid per person, rebuilt from data
 * 
 * Call init(context) once before anything else touches these files.
 */
object FileUtils {
    const val TAG: String = "FileUtils"

    const val DATA_FILE: String = "data"
    const val LABEL_FILE: String = "label"
    const val MODEL_FILE: String = "model"

    private const val SUB_DIR = "facerecog"

    /** Lists the files a [writeFiles] is replacing, until it has completed. */
    private const val SAVE_JOURNAL = "save.journal"

    private var rootDir: File? = null

    @JvmStatic
    @Synchronized
    fun init(context: Context?) {
        if (context == null) {
            return
        }

        val desired = File(context.getFilesDir(), SUB_DIR)

        // Compare against where this context says the data should live, not just
        // "is something already set". Caching the first answer forever meant the
        // folder could never be corrected once it was wrong.
        if (rootDir == desired && desired.isDirectory) {
            return
        }

        if (!desired.exists() && !desired.mkdirs()) {
            Log.e(TAG, "Could not create face data directory: " + desired.getAbsolutePath())
        }
        rootDir = desired
        Log.i(TAG, "Face data root = " + desired.getAbsolutePath())
        recoverInterruptedWrite()
    }

    @JvmStatic
    @get:Synchronized
    val isReady: Boolean
        get() = rootDir?.isDirectory == true

    @JvmStatic
    @Synchronized
    fun file(fileName: String): File {
        checkNotNull(rootDir) { "FileUtils.init(context) was never called" }
        return File(rootDir, fileName)
    }

    /** All non-empty lines of the file; an empty list if it is missing or cannot be read. */
    @JvmStatic
    @Synchronized
    fun readLines(fileName: String): MutableList<String> {
        if (rootDir == null) {
            Log.e(TAG, "readLines before init")
            return mutableListOf()
        }
        return try {
            readAllLines(fileName)
        } catch (e: IOException) {
            Log.e(TAG, "Error reading " + fileName, e)
            mutableListOf()
        }
    }

    /**
     * All non-empty lines of the file; an empty list if it does not exist. A
     * file that exists but cannot be read throws, so that a caller never takes
     * it for an empty one and saves that over it.
     */
    @Synchronized
    @Throws(IOException::class)
    fun readAllLines(fileName: String): MutableList<String> {
        val out = mutableListOf<String>()
        val f = file(fileName)
        if (!f.exists()) {
            return out
        }
        BufferedReader(openForReading(f)).use { reader ->
            while (true) {
                val line = reader.readLine()?.trim() ?: break
                if (line.isNotEmpty()) {
                    out.add(line)
                }
            }
        }
        return out
    }

    /** Opens a file for [readAllLines]; the tests replace it to make a read fail. */
    @VisibleForTesting
    internal var openForReading: (File) -> Reader = { FileReader(it) }

    /** Opens a file for [writeLines]; the tests replace it to make a write fail. */
    @VisibleForTesting
    internal var openForWriting: (File) -> Writer = { FileWriter(it, false) }

    /**
     * Writes the whole file: into a temporary file first, which then replaces the
     * old one in a single atomic move. A failed write (a full disk, say) reports
     * false and leaves the old file as it was; it never replaces it with part of
     * the new one.
     */
    @Synchronized
    fun writeLines(fileName: String, lines: List<String>): Boolean {
        if (rootDir == null) {
            Log.e(TAG, "writeLines before init")
            return false
        }
        if (!writeTemporary(fileName, lines)) {
            return false
        }
        val temp = file(fileName + ".tmp")
        return try {
            replace(temp, file(fileName))
            true
        } catch (e: IOException) {
            Log.e(TAG, "Could not replace " + fileName + "; the old file is kept", e)
            deleteTemporary(temp)
            false
        }
    }

    /**
     * Replaces several files together: either all of them get the new lines, or
     * all of them keep the old ones. The face database needs that, because its
     * files refer to each other by person index.
     *
     * 1. Every file is written to "<name>.tmp". A failure here touches nothing.
     * 2. A journal lists the files and whether each existed; then every existing
     *    file is moved to "<name>.bak" and every temporary file into place. A
     *    failure here moves the backups back.
     * 3. Deleting the journal completes the write; the backups go after it.
     * Every new file and the journal are synced to storage before anything is
     * moved, and the moves before the journal is deleted, so a power cut at any
     * point leaves either the old files or the new ones.
     * If the app stops during step 2, [recoverInterruptedWrite] undoes it at the
     * next start.
     */
    @Synchronized
    fun writeFiles(files: Map<String, List<String>>): Boolean {
        if (rootDir == null) {
            Log.e(TAG, "writeFiles before init")
            return false
        }
        for ((name, lines) in files) {
            if (!writeTemporary(name, lines)) {
                files.keys.forEach { deleteTemporary(file("$it.tmp")) }
                return false
            }
        }
        val existed = files.keys.associateWith { file(it).exists() }
        if (!writeLines(SAVE_JOURNAL, existed.map { (name, exists) -> "$name ${if (exists) 1 else 0}" })) {
            files.keys.forEach { deleteTemporary(file("$it.tmp")) }
            return false
        }
        try {
            for (name in files.keys.filter { existed.getValue(it) }) {
                replaceFile(file(name), file("$name.bak"))
            }
            for (name in files.keys) {
                replaceFile(file("$name.tmp"), file(name))
            }
        } catch (e: IOException) {
            Log.e(TAG, "Could not replace the face files; the old ones are put back", e)
            recoverInterruptedWrite()
            return false
        }
        // The moves must be on storage before the journal, which could undo them, goes.
        syncDirectory()
        deleteTemporary(file(SAVE_JOURNAL))
        files.keys.forEach { deleteTemporary(file("$it.bak")) }
        syncDirectory()
        return true
    }

    /**
     * Undoes a [writeFiles] that did not complete: every file listed in the
     * journal gets its backup back, and a file that did not exist before is
     * removed. Without a journal it only clears leftovers.
     */
    @Synchronized
    fun recoverInterruptedWrite(): Boolean {
        val root = rootDir ?: return false
        val journal = File(root, SAVE_JOURNAL)
        if (journal.exists()) {
            Log.w(TAG, "The last save of the face files did not complete; restoring the files before it")
            val entries = try {
                readAllLines(SAVE_JOURNAL)
            } catch (e: IOException) {
                // Without the journal nothing can be restored; deleting the
                // backups now would lose the only good copy. Keep everything.
                Log.e(TAG, "Could not read the save journal; keeping every recovery file", e)
                return false
            }
            var restored = true
            for (entry in entries) {
                val parts = entry.split(" ")
                val name = parts.getOrNull(0) ?: continue
                val existed = parts.getOrNull(1) == "1"
                val backup = File(root, "$name.bak")
                when {
                    backup.exists() -> restored = restore(backup, File(root, name)) && restored
                    !existed -> deleteTemporary(File(root, name))
                }
            }
            if (!restored) {
                // Keep the journal and the backups: the next start tries again.
                Log.e(TAG, "Could not put every face file back; trying again at the next start")
                root.listFiles()?.filter { it.name.endsWith(".tmp") }?.forEach { deleteTemporary(it) }
                return false
            }
            syncDirectory()
            deleteTemporary(journal)
        }
        root.listFiles()?.filter { it.name.endsWith(".tmp") || it.name.endsWith(".bak") }
            ?.forEach { deleteTemporary(it) }
        return true
    }

    private fun restore(backup: File, target: File): Boolean {
        return try {
            replaceFile(backup, target)
            true
        } catch (e: IOException) {
            Log.e(TAG, "Could not restore " + target.getName(), e)
            false
        }
    }

    /** Writes a file's data through to storage; the tests replace it to watch the order. */
    @VisibleForTesting
    internal var syncFile: (File) -> Unit = { file ->
        RandomAccessFile(file, "rw").use { it.fd.sync() }
    }

    /** Writes the folder's entries (the moves and deletes) through to storage, where the system allows it. */
    private fun syncDirectory() {
        val root = rootDir ?: return
        try {
            FileChannel.open(root.toPath(), StandardOpenOption.READ).use { it.force(true) }
        } catch (e: Exception) {
            // Not every file system lets a folder be opened; the file syncs still hold.
            Log.w(TAG, "Could not sync the face data folder", e)
        }
    }

    /** Writes [lines] to "<fileName>.tmp"; false, and no temporary file, if that fails. */
    private fun writeTemporary(fileName: String, lines: List<String>): Boolean {
        val temp = file(fileName + ".tmp")
        try {
            // Unlike PrintWriter, BufferedWriter passes write, flush and close errors on.
            BufferedWriter(openForWriting(temp)).use { writer ->
                for (line in lines) {
                    writer.write(line)
                    writer.newLine()
                }
                // close() does not flush the writer underneath; a failed flush must show.
                writer.flush()
            }
            // flush() only hands the data to the system; it must be on storage
            // before this file replaces the old one.
            syncFile(temp)
            return true
        } catch (e: IOException) {
            Log.e(TAG, "Error writing " + fileName + "; the old file is kept", e)
            deleteTemporary(temp)
            return false
        }
    }

    /** Moves a file over another; the tests replace it to make a move fail. */
    @VisibleForTesting
    internal var replaceFile: (File, File) -> Unit = { source, target -> replace(source, target) }

    /** A temporary file left behind is overwritten by the next write, so a failed delete is only logged. */
    private fun deleteTemporary(temp: File) {
        if (temp.exists() && !temp.delete()) {
            Log.w(TAG, "Could not delete " + temp.getName())
        }
    }

    private fun replace(source: File, target: File) {
        try {
            Files.move(
                source.toPath(), target.toPath(),
                StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE
            )
        } catch (e: AtomicMoveNotSupportedException) {
            Files.move(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
    }

    @JvmStatic
    @Synchronized
    fun deleteAll() {
        if (rootDir == null) {
            return
        }
        val files = rootDir?.listFiles() ?: return
        for (f in files) {
            if (!f.delete()) {
                Log.w(TAG, "Could not delete " + f.getName())
            }
        }
    }
}
