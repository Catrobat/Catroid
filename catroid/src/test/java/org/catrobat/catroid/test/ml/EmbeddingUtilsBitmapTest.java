package org.catrobat.catroid.test.ml;

import static org.junit.Assert.*;

import android.graphics.Bitmap;
import android.graphics.Color;

import org.catrobat.catroid.ObjectTrainAndRecognition.ml.EmbeddingUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.ByteArrayOutputStream;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class EmbeddingUtilsBitmapTest {

    @Test
    public void bitmapBytesToEmbedding_l2NormalizedHasUnitNorm() {
        Bitmap bmp = Bitmap.createBitmap(
                EmbeddingUtils.WIDTH,
                EmbeddingUtils.HEIGHT,
                Bitmap.Config.ARGB_8888
        );

        for (int y = 0; y < bmp.getHeight(); y++) {
            for (int x = 0; x < bmp.getWidth(); x++) {
                int v = (x + y) & 0xFF;
                bmp.setPixel(x, y, Color.rgb(v, v, v));
            }
        }

        // Convert bitmap to bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();

        // FIX: call the correct available method
        float[] emb = EmbeddingUtils.bitmapBytesToEmbedding(bytes);

        assertEquals(EmbeddingUtils.EMBEDDING_DIM, emb.length);

        double s = 0.0;
        for (float e : emb) s += e * e;
        double norm = Math.sqrt(s);

        assertTrue("L2 norm should be ~1.0 but was " + norm, norm > 0.99 && norm < 1.01);
    }
}
