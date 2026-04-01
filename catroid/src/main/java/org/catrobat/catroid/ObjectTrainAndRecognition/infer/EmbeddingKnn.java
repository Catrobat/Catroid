package org.catrobat.catroid.ObjectTrainAndRecognition.infer;

public class EmbeddingKnn {
    private final float[] embeddings; // [N*D]
    private final int[] labels;       // [N]
    private final int dim;
    private final boolean l2Normalized;
    private final int n;

    public static class Hit {
        public final int label;
        public final float score;
        public Hit(int label, float score) { this.label = label; this.score = score; }
    }

    public EmbeddingKnn(float[] embeddings, int[] labels, int dim, boolean l2Normalized) {
        this.embeddings = embeddings;
        this.labels = labels;
        this.dim = dim;
        this.l2Normalized = l2Normalized;
        this.n = labels != null ? labels.length : 0;
    }

    private static float cosine(float[] a, int ai, float[] b, int bi, int d) {
        float dot = 0f, na = 0f, nb = 0f;
        for (int i = 0; i < d; i++) {
            float va = a[ai + i], vb = b[bi + i];
            dot += va * vb; na += va * va; nb += vb * vb;
        }
        if (na <= 1e-12f || nb <= 1e-12f) return 0f;
        return (float)(dot / (Math.sqrt(na) * Math.sqrt(nb)));
    }

    private static float l2(float[] a, int ai, float[] b, int bi, int d) {
        float s = 0f;
        for (int i = 0; i < d; i++) {
            float diff = a[ai + i] - b[bi + i];
            s += diff * diff;
        }
        return (float)Math.sqrt(s);
    }

    public Hit predict(float[] query) {
        if (query == null || query.length != dim) return null;
        boolean useCosine = l2Normalized;
        int bestIdx = -1;
        float bestScore = useCosine ? -Float.MAX_VALUE : Float.MAX_VALUE;

        for (int i = 0; i < n; i++) {
            float score = useCosine ? cosine(query, 0, embeddings, i * dim, dim)
                    : l2(query, 0, embeddings, i * dim, dim);
            if (useCosine) {
                if (score > bestScore) { bestScore = score; bestIdx = i; }
            } else {
                if (score < bestScore) { bestScore = score; bestIdx = i; }
            }
        }
        if (bestIdx < 0) return null;
        float sim = useCosine ? bestScore : (1f / (1f + bestScore));
        return new Hit(labels[bestIdx], sim);
    }
}

