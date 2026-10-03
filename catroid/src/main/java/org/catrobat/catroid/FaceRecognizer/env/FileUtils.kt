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
import java.io.Writer
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

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

    @JvmStatic
    @Synchronized
    fun readLines(fileName: String): MutableList<String> {
        val out = mutableListOf<String>()
        if (rootDir == null) {
            Log.e(TAG, "readLines before init")
            return out
        }
        val f = file(fileName)
        if (!f.exists()) {
            return out
        }
        try {
            BufferedReader(FileReader(f)).use { reader ->
                while (true) {
                    val line = reader.readLine()?.trim() ?: break
                    if (line.isNotEmpty()) {
                        out.add(line)
                    }
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error reading " + fileName, e)
        }
        return out
    }

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
        val target = file(fileName)
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
        } catch (e: IOException) {
            Log.e(TAG, "Error writing " + fileName + "; the old file is kept", e)
            deleteTemporary(temp)
            return false
        }

        return try {
            replace(temp, target)
            true
        } catch (e: IOException) {
            Log.e(TAG, "Could not replace " + fileName + "; the old file is kept", e)
            deleteTemporary(temp)
            false
        }
    }

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
