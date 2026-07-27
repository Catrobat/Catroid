package org.catrobat.catroid.FaceRecognizer.env;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Single source of truth for where face data lives.
 *
 * Everything is stored in  filesDir/facerecog/
 *   label : one person name per line, line number = person index
 *   data  : one embedding per line,  "<personIndex> v0 v1 ... v511"
 *
 * Call init(context) once before anything else touches these files.
 */
public class FileUtils {
    public static final String TAG = "FileUtils";

    public static final String DATA_FILE = "data";
    public static final String LABEL_FILE = "label";
    public static final String MODEL_FILE = "model"; // legacy, no longer written

    private static final String SUB_DIR = "facerecog";

    /** Kept only so legacy classes still compile. Do not build paths from this by hand. */
    public static String ROOT = "";

    private static File rootDir;

    private FileUtils() { }

    public static synchronized void init(Context context) {
        if (context == null) {
            return;
        }

        File desired = new File(context.getFilesDir(), SUB_DIR);

        // Compare against where this context says the data should live, not just
        // "is something already set". Caching the first answer forever meant the
        // folder could never be corrected once it was wrong.
        if (rootDir != null && rootDir.equals(desired) && rootDir.isDirectory()) {
            return;
        }

        if (!desired.exists() && !desired.mkdirs()) {
            Log.e(TAG, "Could not create face data directory: " + desired.getAbsolutePath());
        }
        rootDir = desired;
        ROOT = desired.getAbsolutePath();
        Log.i(TAG, "Face data root = " + ROOT);
    }

    /** Legacy name, forwards to init. */
    public static synchronized void initializeRoot(Context context) {
        init(context);
    }

    public static synchronized boolean isReady() {
        return rootDir != null && rootDir.isDirectory();
    }

    public static synchronized File file(String fileName) {
        if (rootDir == null) {
            throw new IllegalStateException("FileUtils.init(context) was never called");
        }
        return new File(rootDir, fileName);
    }

    public static synchronized String getAbsolutePath(String fileName) {
        return file(fileName).getAbsolutePath();
    }

    public static synchronized boolean fileExists(Context context, String fileName) {
        init(context);
        File f = file(fileName);
        return f.exists() && f.length() > 0;
    }

    public static synchronized List<String> readLines(String fileName) {
        List<String> out = new ArrayList<>();
        if (rootDir == null) {
            Log.e(TAG, "readLines before init");
            return out;
        }
        File f = file(fileName);
        if (!f.exists()) {
            return out;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    out.add(line);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error reading " + fileName, e);
        }
        return out;
    }

    /** Writes the whole file. Temp file plus rename so a crash cannot leave a half file. */
    public static synchronized boolean writeLines(String fileName, List<String> lines) {
        if (rootDir == null) {
            Log.e(TAG, "writeLines before init");
            return false;
        }
        File target = file(fileName);
        File temp = file(fileName + ".tmp");

        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(temp, false)))) {
            for (String line : lines) {
                writer.println(line);
            }
            writer.flush();
        } catch (IOException e) {
            Log.e(TAG, "Error writing " + fileName, e);
            return false;
        }

        if (target.exists() && !target.delete()) {
            Log.e(TAG, "Could not delete old " + fileName);
            return false;
        }
        if (!temp.renameTo(target)) {
            Log.e(TAG, "Could not rename temp file for " + fileName);
            return false;
        }
        return true;
    }

    public static synchronized void deleteAll() {
        if (rootDir == null) {
            return;
        }
        File[] files = rootDir.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            if (!f.delete()) {
                Log.w(TAG, "Could not delete " + f.getName());
            }
        }
    }

    public static synchronized void saveBitmap(Bitmap bitmap, String fileName) {
        if (bitmap == null || rootDir == null) {
            return;
        }
        try (FileOutputStream out = new FileOutputStream(file(fileName))) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
        } catch (IOException e) {
            Log.e(TAG, "Error saving bitmap " + fileName, e);
        }
    }

    public static synchronized void copyAsset(AssetManager assetManager, String fileName) {
        if (rootDir == null) {
            return;
        }
        File outFile = file(fileName);
        if (outFile.exists() && outFile.length() > 0) {
            return;
        }
        try (InputStream in = assetManager.open(fileName);
                OutputStream out = new FileOutputStream(outFile)) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
        } catch (IOException e) {
            Log.e(TAG, "Failed to copy asset " + fileName, e);
        }
    }

    public static synchronized void copyAsset(Context context, AssetManager assetManager, String fileName) {
        init(context);
        copyAsset(assetManager, fileName);
    }
}