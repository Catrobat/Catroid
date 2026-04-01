package org.catrobat.catroid.FaceRecognizer.env;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class FileUtils {
    private static final Logger LOGGER = new Logger();

    // Public ROOT path to be accessed by other classes
    public static String ROOT;

    // File names for saving data
    public static final String DATA_FILE = "data";
    public static final String MODEL_FILE = "model";
    public static final String LABEL_FILE = "label";

    // Initialize ROOT directory based on SDK version
    public static void initializeRoot(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // SDK 29 and above
            ROOT = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + File.separator + "CatroidApplication";
        } else {
            ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CatroidApplication";
        }
        File dir = new File(ROOT);
        if (!dir.exists()) dir.mkdirs();
        LOGGER.i("ROOT=" + ROOT);
    }

    // Save bitmap to file
    public static void saveBitmap(final Bitmap bitmap, final String filename) {
        LOGGER.i("Saving %dx%d bitmap to %s.", bitmap.getWidth(), bitmap.getHeight(), ROOT);
        final File myDir = new File(ROOT);
        if (!myDir.exists() && !myDir.mkdirs()) {
            LOGGER.i("Make dir failed: " + myDir.getAbsolutePath());
        }
        final File file = new File(myDir, filename);
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 99, out);
            out.flush();
            FileDescriptor fd = out.getFD();
            if (fd != null) fd.sync();
        } catch (final Exception e) {
            LOGGER.e(e, "Exception!");
        }
    }

    // ---- LABEL helpers -------------------------------------------------------

    // Rewrite label file with updated classNames (UTF-8 + fsync)
    public static void rewriteLabelFile(ArrayList<String> names) {
        File file = new File(ROOT + File.separator + LABEL_FILE);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();

        try (FileOutputStream fos = new FileOutputStream(file, false);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(osw)) {

            for (int i = 0; i < names.size(); i++) {
                String line = (names.get(i) == null ? "" : names.get(i).trim());
                writer.write(line);
                if (i < names.size() - 1) writer.newLine();
            }
            writer.flush();
            fos.getFD().sync();
            LOGGER.i("rewriteLabelFile wrote %d names → %s", names.size(), file.getAbsolutePath());
        } catch (IOException e) {
            LOGGER.e(e, "Failed to rewrite label file");
        }
    }

    // Append text to a file (UTF-8 + fsync)
    public static void appendText(String text, String filename) {
        File file = new File(ROOT + File.separator + filename);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();

        try (FileOutputStream fos = new FileOutputStream(file, true);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(osw);
             PrintWriter out = new PrintWriter(bw)) {

            out.println(text == null ? "" : text.trim());
            out.flush();
            fos.getFD().sync();
            LOGGER.i("appendText → %s: %s", file.getAbsolutePath(), text);
        } catch (IOException e) {
            LOGGER.e(e, "IOException!");
        }
    }

    // Read labels from a file (accepts "id name" or "name", UTF-8)
    public static ArrayList<String> readLabel(String filename) {
        File file = new File(ROOT + File.separator + filename);
        ArrayList<String> list = new ArrayList<>();
        if (!file.exists()) {
            LOGGER.i("readLabel: not found: " + file.getAbsolutePath());
            return list;
        }
        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                int sp = line.indexOf(' ');
                if (sp > 0) {
                    String left = line.substring(0, sp).trim();
                    String right = line.substring(sp + 1).trim();
                    try {
                        Integer.parseInt(left); // "id name"
                        list.add(right);
                    } catch (NumberFormatException nfe) {
                        list.add(line); // plain name with spaces
                    }
                } else {
                    list.add(line);
                }
            }
            LOGGER.i("readLabel: %d names from %s", list.size(), file.getAbsolutePath());
        } catch (IOException e) {
            LOGGER.e(e, "readLabel IOException!");
        }
        return list;
    }

    // ---- Asset copy helpers --------------------------------------------------

    // Copy asset file to storage — SAFE: does NOT overwrite existing non-empty files.
    public static void copyAssetIfMissing(AssetManager mgr, String filename) {
        File out = new File(ROOT + File.separator + filename);
        if (out.exists() && out.length() > 0) {
            LOGGER.i("copyAssetIfMissing: exists, skip: " + out.getAbsolutePath());
            return;
        }
        copyAsset(mgr, filename);
    }

    // Copy asset file to storage; DO NOT overwrite label if present
    public static void copyAsset(AssetManager mgr, String filename) {
        InputStream in = null;
        OutputStream out = null;

        try {
            File file = new File(ROOT + File.separator + filename);

            // Guard: never overwrite an existing label file
            if (LABEL_FILE.equals(filename) && file.exists() && file.length() > 0) {
                LOGGER.i("copyAsset: NOT overwriting existing label: " + file.getAbsolutePath());
                return;
            }

            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
            if (!file.exists()) file.createNewFile();

            in = mgr.open(filename);
            out = new FileOutputStream(file, false);

            byte[] buffer = new byte[8192];
            int read;
            while((read = in.read(buffer)) != -1){
                out.write(buffer, 0, read);
            }
            out.flush();
            if (out instanceof FileOutputStream) {
                FileDescriptor fd = ((FileOutputStream) out).getFD();
                if (fd != null) fd.sync();
            }
            LOGGER.i("copyAsset wrote: " + file.getAbsolutePath());
        } catch (Exception e) {
            LOGGER.e(e, "Exception!");
        } finally {
            if (in != null) {
                try { in.close(); } catch (IOException e) { LOGGER.e(e, "IOException!"); }
            }
            if (out != null) {
                try { out.close(); } catch (IOException e) { LOGGER.e(e, "IOException!"); }
            }
        }
    }
}
