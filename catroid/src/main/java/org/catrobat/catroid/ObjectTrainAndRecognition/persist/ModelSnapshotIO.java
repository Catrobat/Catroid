package org.catrobat.catroid.ObjectTrainAndRecognition.persist;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public final class ModelSnapshotIO {
    private static final String TAG = "ModelSnapshotIO";
    private static final String DIR = "otr";
    private static final String SUB = "snapshots";
    private static final SimpleDateFormat TS = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);

    private ModelSnapshotIO() {}

    public static class LoadedSnapshot {
        public final ModelMeta meta;
        public final float[] embeddings;
        public final int[] labels;
        public LoadedSnapshot(ModelMeta meta, float[] embeddings, int[] labels) {
            this.meta = meta; this.embeddings = embeddings; this.labels = labels;
        }
    }

    public static File snapshotsDir(Context context) {
        File root = context.getDir(DIR, Context.MODE_PRIVATE);
        File dir = new File(root, SUB);
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    public static String generateFilename() {
        return "model_v1_" + TS.format(new Date()) + ".otr.zip";
    }

    public static File save(Context context, ModelMeta meta, float[] embeddings, int[] labels) throws Exception {
        if (meta == null) throw new IllegalArgumentException("meta == null");
        if (meta.numItems < 1) throw new IllegalArgumentException("No training data to save.");
        if (embeddings == null || labels == null) throw new IllegalArgumentException("null arrays");
        if (embeddings.length != meta.numItems * meta.embeddingDim) {
            throw new IllegalArgumentException("Embeddings size mismatch: got " + embeddings.length +
                    ", expected " + (meta.numItems * meta.embeddingDim));
        }
        if (labels.length != meta.numItems) {
            throw new IllegalArgumentException("Labels count mismatch: got " + labels.length +
                    ", expected " + meta.numItems);
        }

        File outFile = new File(snapshotsDir(context), generateFilename());
        ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)));
        try {
            // meta.json (force String keys+values for labelMap — matches Kotlin)
            zos.putNextEntry(new ZipEntry("meta.json"));
            JSONObject lmObj = new JSONObject();
            if (meta.labelMap != null) {
                for (Map.Entry<Integer,String> e : meta.labelMap.entrySet()) {
                    lmObj.put(String.valueOf(e.getKey()), e.getValue() == null ? "" : e.getValue());
                }
            }
            JSONObject metaJson = new JSONObject();
            metaJson.put("schemaVersion", meta.schemaVersion);
            metaJson.put("createdAtEpochMs", meta.createdAtEpochMs);
            metaJson.put("embeddingDim", meta.embeddingDim);
            metaJson.put("numItems", meta.numItems);
            metaJson.put("l2Normalized", meta.l2Normalized);
            metaJson.put("trainerAppVersion", meta.trainerAppVersion);
            metaJson.put("notes", meta.notes);
            metaJson.put("labelMap", lmObj);
            byte[] metaBytes = metaJson.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
            zos.write(metaBytes);
            zos.closeEntry();

            // embeddings.bin float32 LE
            zos.putNextEntry(new ZipEntry("embeddings.bin"));
            ByteBuffer eb = ByteBuffer.allocate(4 * embeddings.length).order(ByteOrder.LITTLE_ENDIAN);
            for (float v : embeddings) eb.putFloat(v);
            zos.write(eb.array());
            zos.closeEntry();

            // labels.int32 LE
            zos.putNextEntry(new ZipEntry("labels.int32"));
            ByteBuffer lb = ByteBuffer.allocate(4 * labels.length).order(ByteOrder.LITTLE_ENDIAN);
            for (int v : labels) lb.putInt(v);
            zos.write(lb.array());
            zos.closeEntry();
        } finally {
            try { zos.close(); } catch (Throwable ignore) {}
        }
        return outFile;
    }

    public static boolean removeLabelAndResave(Context context, int labelId) throws Exception {
        LoadedSnapshot snap = loadLatest(context);
        if (snap == null) return false;

        int d = snap.meta.embeddingDim;
        int n = snap.labels.length;

        int keep = 0;
        for (int lbl : snap.labels) if (lbl != labelId) keep++;

        if (keep == n) return true; // nothing to delete
        if (keep == 0) {
            File[] files = snapshotsDir(context).listFiles((f, name) -> name.endsWith(".otr.zip"));
            if (files != null) for (File f : files) try { f.delete(); } catch (Throwable ignore) {}
            return true;
        }

        float[] newEmb = new float[keep * d];
        int[] newLab = new int[keep];
        int w = 0;
        for (int i = 0; i < n; i++) {
            if (snap.labels[i] == labelId) continue;
            System.arraycopy(snap.embeddings, i * d, newEmb, w * d, d);
            newLab[w] = snap.labels[i];
            w++;
        }

        Map<Integer,String> newMap = new LinkedHashMap<>(snap.meta.labelMap);
        newMap.remove(labelId);

        ModelMeta newMeta = new ModelMeta(
                1,
                System.currentTimeMillis(),
                d,
                newLab.length,
                snap.meta.l2Normalized,
                newMap,
                "1.0",
                "Deleted label " + labelId
        );
        save(context, newMeta, newEmb, newLab);
        return true;
    }

    public static LoadedSnapshot loadLatest(Context context) {
        File dir = snapshotsDir(context);
        File[] arr = dir.listFiles((f, name) -> name.endsWith(".otr.zip"));
        if (arr == null || arr.length == 0) return null;
        Arrays.sort(arr, (a,b) -> Long.compare(b.lastModified(), a.lastModified()));
        for (File f : arr) {
            try {
                LoadedSnapshot s = loadFromFile(f);
                if (s != null) return s;
            } catch (Exception e) {
                Log.w(TAG, "Failed to load " + f.getName() + ": " + e.getMessage());
            }
        }
        return null;
    }

    public static LoadedSnapshot loadFromFile(File file) throws Exception {
        ModelMeta meta = null;
        float[] embeddings = null;
        int[] labels = null;

        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(file)));
        try {
            ZipEntry entry = zis.getNextEntry();
            while (entry != null) {
                String name = entry.getName();
                if ("meta.json".equals(name)) {
                    byte[] txtBytes = readAllBytes(zis);
                    String txt = new String(txtBytes, java.nio.charset.StandardCharsets.UTF_8);
                    JSONObject o = new JSONObject(txt);

                    // labelMap (Android-safe keys())
                    Map<Integer,String> lm = new LinkedHashMap<>();
                    JSONObject lmJson = o.optJSONObject("labelMap");
                    if (lmJson != null) {
                        java.util.Iterator<String> it = lmJson.keys();
                        while (it.hasNext()) {
                            String k = it.next();
                            Object vAny = lmJson.opt(k);
                            try {
                                int id = Integer.parseInt(k);
                                lm.put(id, vAny == null ? "" : vAny.toString());
                            } catch (NumberFormatException nfe) {
                                // Skip bad key (defensive)
                            }
                        }
                    }

                    int schema = o.optInt("schemaVersion", 1);
                    if (schema != 1) throw new IOException("Unsupported schema v" + schema);

                    meta = new ModelMeta(
                            schema,
                            o.optLong("createdAtEpochMs", System.currentTimeMillis()),
                            o.optInt("embeddingDim", 0),
                            o.optInt("numItems", 0),
                            o.optBoolean("l2Normalized", true),
                            lm,
                            o.optString("trainerAppVersion", null),
                            o.optString("notes", null)
                    );

                } else if ("embeddings.bin".equals(name)) {
                    byte[] bytes = readAllBytes(zis);
                    ByteBuffer bb = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
                    int cnt = bytes.length / 4;
                    float[] arr = new float[cnt];
                    for (int i = 0; i < cnt; i++) arr[i] = bb.getFloat();
                    embeddings = arr;

                } else if ("labels.int32".equals(name)) {
                    byte[] bytes = readAllBytes(zis);
                    ByteBuffer bb = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
                    int cnt = bytes.length / 4;
                    int[] arr = new int[cnt];
                    for (int i = 0; i < cnt; i++) arr[i] = bb.getInt();
                    labels = arr;
                }
                zis.closeEntry();
                entry = zis.getNextEntry();
            }
        } finally {
            try { zis.close(); } catch (Throwable ignore) {}
        }

        if (meta == null || embeddings == null || labels == null) return null;

        // Integrity checks (match Kotlin behavior, but guard instead of hard crash)
        if (meta.embeddingDim <= 0 || meta.numItems <= 0) {
            throw new IOException("Invalid meta: dim=" + meta.embeddingDim + " numItems=" + meta.numItems);
        }
        if (embeddings.length != meta.numItems * meta.embeddingDim) {
            throw new IOException("Embedding size mismatch: got " + embeddings.length +
                    ", expected " + (meta.numItems * meta.embeddingDim));
        }
        if (labels.length != meta.numItems) {
            throw new IOException("Label count mismatch: got " + labels.length + ", expected " + meta.numItems);
        }
        return new LoadedSnapshot(meta, embeddings, labels);
    }

    private static byte[] readAllBytes(InputStream in) throws IOException {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int n;
        while ((n = in.read(buf)) != -1) {
            baos.write(buf, 0, n);
        }
        return baos.toByteArray();
    }
}
