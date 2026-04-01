package org.catrobat.catroid.test.infer;
import org.catrobat.catroid.ObjectTrainAndRecognition.infer.EmbeddingKnn;
import org.junit.Test;
import static org.junit.Assert.*;

public class EmbeddingKnnTest {

    @Test
    public void predict_withCosine_returnsMostSimilar() {
        float[] embeddings = new float[]{
                1f, 0f, 0f,
                0f, 1f, 0f
        };
        int[] labels = new int[]{10, 20};
        EmbeddingKnn knn = new EmbeddingKnn(embeddings, labels, 3, /*l2Normalized=*/true);

        float[] q = new float[]{0.9f, 0.1f, 0f};
        EmbeddingKnn.Hit hit = knn.predict(q);
        assertNotNull(hit);
        assertEquals(10, hit.label);
        assertTrue(hit.score > 0.5f);
    }

    @Test
    public void predict_withL2_returnsClosest() {
        float[] embeddings = new float[]{
                0f, 0f, 1f,
                0f, 1f, 0f
        };
        int[] labels = new int[]{1, 2};
        EmbeddingKnn knn = new EmbeddingKnn(embeddings, labels, 3, /*l2Normalized=*/false);

        float[] q = new float[]{0f, 0.8f, 0.1f};
        EmbeddingKnn.Hit hit = knn.predict(q);
        assertNotNull(hit);
        assertEquals(2, hit.label);
        assertTrue(hit.score > 0 && hit.score <= 1f);
    }

    @Test
    public void predict_returnsNullForInvalidInput() {
        float[] embeddings = new float[]{1f, 0f, 0f};
        int[] labels = new int[]{1};
        EmbeddingKnn knn = new EmbeddingKnn(embeddings, labels, 3, true);

        float[] badQuery = new float[]{1f, 2f}; // wrong length
        assertNull(knn.predict(badQuery));
    }
}