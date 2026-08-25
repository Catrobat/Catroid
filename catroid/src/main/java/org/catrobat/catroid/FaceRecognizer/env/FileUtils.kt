package org.catrobat.catroid.FaceRecognizer.env

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.util.Log
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter

/**
 * Single source of truth for where face data lives.
 * 
 * Everything is stored in  filesDir/facerecog/
 * label : one person name per line, line number = person index
 * data  : one embedding per line,  "<personIndex> v0 v1 ... v511"
 * 
 * Call init(context) once before anything else touches these files.
 */
object FileUtils {
    const val TAG: String = "FileUtils"

    const val DATA_FILE: String = "data"
    const val LABEL_FILE: String = "label"
    const val MODEL_FILE: String = "model" // legacy, no longer written

    private const val SUB_DIR = "facerecog"

    /** Kept only so legacy classes still compile. Do not build paths from this by hand.  */
    var ROOT: String = ""

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
        ROOT = desired.getAbsolutePath()
        Log.i(TAG, "Face data root = " + ROOT)
    }

    /** Legacy name, forwards to init.  */
    @Synchronized
    fun initializeRoot(context: Context?) {
        init(context)
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
    fun getAbsolutePath(fileName: String): String {
        return file(fileName).getAbsolutePath()
    }

    @JvmStatic
    @Synchronized
    fun fileExists(context: Context?, fileName: String): Boolean {
        init(context)
        val f = file(fileName)
        return f.exists() && f.length() > 0
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

    /** Writes the whole file. Temp file plus rename so a crash cannot leave a half file.  */
    @Synchronized
    fun writeLines(fileName: String, lines: List<String>): Boolean {
        if (rootDir == null) {
            Log.e(TAG, "writeLines before init")
            return false
        }
        val target = file(fileName)
        val temp = file(fileName + ".tmp")

        try {
            PrintWriter(BufferedWriter(FileWriter(temp, false))).use { writer ->
                for (line in lines) {
                    writer.println(line)
                }
                writer.flush()
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error writing " + fileName, e)
            return false
        }

        if (target.exists() && !target.delete()) {
            Log.e(TAG, "Could not delete old " + fileName)
            return false
        }
        if (!temp.renameTo(target)) {
            Log.e(TAG, "Could not rename temp file for " + fileName)
            return false
        }
        return true
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

    @JvmStatic
    @Synchronized
    fun saveBitmap(bitmap: Bitmap?, fileName: String) {
        if (bitmap == null || rootDir == null) {
            return
        }
        try {
            FileOutputStream(file(fileName)).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                out.flush()
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error saving bitmap " + fileName, e)
        }
    }

    @Synchronized
    fun copyAsset(assetManager: AssetManager, fileName: String) {
        if (rootDir == null) {
            return
        }
        val outFile = file(fileName)
        if (outFile.exists() && outFile.length() > 0) {
            return
        }
        try {
            assetManager.open(fileName).use { `in` ->
                FileOutputStream(outFile).use { out ->
                    val buffer = ByteArray(4096)
                    var read: Int
                    while ((`in`.read(buffer).also { read = it }) != -1) {
                        out.write(buffer, 0, read)
                    }
                    out.flush()
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to copy asset " + fileName, e)
        }
    }

    @Synchronized
    fun copyAsset(context: Context?, assetManager: AssetManager, fileName: String) {
        init(context)
        copyAsset(assetManager, fileName)
    }
}