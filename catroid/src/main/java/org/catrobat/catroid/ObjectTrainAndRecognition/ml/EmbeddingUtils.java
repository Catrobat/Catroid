package org.catrobat.catroid.ObjectTrainAndRecognition.ml;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.util.LinkedHashMap;
import java.util.Map;

public final class EmbeddingUtils {
    public static final int WIDTH = 32;
    public static final int HEIGHT = 32;
    public static final int EMBEDDING_DIM = WIDTH * HEIGHT;

    private EmbeddingUtils() {}

    public static int resolveOrCreateLabelId(Map<Integer, String> existing, String name) {
        if (existing != null) {
            for (Map.Entry<Integer, String> e : existing.entrySet()) {
                if (e.getValue() != null && e.getValue().equalsIgnoreCase(name)) {
                    return e.getKey();
                }
            }
            if (!existing.isEmpty()) {
                int max = -1;
                for (Integer k : existing.keySet()) if (k != null && k > max) max = k;
                return max + 1;
            }
        }
        return 0;
    }

    public static Map<Integer, String> mergeLabelMaps(Map<Integer, String> base, Map<Integer, String> addition) {
        LinkedHashMap<Integer, String> out = new LinkedHashMap<>();
        if (base != null) out.putAll(base);
        if (addition != null) out.putAll(addition);
        return out;
    }

    public static float[] bitmapBytesToEmbedding(byte[] imageBytes) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, opts);
        if (bmp == null) throw new IllegalArgumentException("decode bitmap failed");

        Bitmap scaled = Bitmap.createScaledBitmap(bmp, WIDTH, HEIGHT, true);
        if (scaled != bmp) bmp.recycle();

        float[] emb = new float[EMBEDDING_DIM];
        int idx = 0;
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                int c = scaled.getPixel(x, y);
                int r = (c >> 16) & 0xFF;
                int g = (c >> 8) & 0xFF;
                int b = (c) & 0xFF;
                float gray = (0.299f * r + 0.587f * g + 0.114f * b) / 255f;
                emb[idx++] = gray;
            }
        }
        l2NormalizeInPlace(emb);
        scaled.recycle();
        return emb;
    }

    private static void l2NormalizeInPlace(float[] vec) {
        double s = 0.0;
        for (float v : vec) s += (double)v * (double)v;
        if (s < 1e-12) s = 1e-12;
        float inv = (float)(1.0 / Math.sqrt(s));
        for (int i = 0; i < vec.length; i++) vec[i] *= inv;
    }
}
