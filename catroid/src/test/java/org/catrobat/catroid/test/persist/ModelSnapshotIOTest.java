package org.catrobat.catroid.test.persist;

import static org.junit.Assert.*;

import android.content.Context;

import org.catrobat.catroid.ObjectTrainAndRecognition.persist.ModelMeta;
import org.catrobat.catroid.ObjectTrainAndRecognition.persist.ModelSnapshotIO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.RuntimeEnvironment;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ModelSnapshotIOTest {

    @Test
    public void saveAndLoadLatest_roundTripsDataCorrectly() throws Exception {
        Context ctx = RuntimeEnvironment.getApplication();

        // Prepare dummy data
        int dim = 4;
        float[] embeddings = new float[]{1, 2, 3, 4, 5, 6, 7, 8};
        int[] labels = new int[]{11, 22};
        Map<Integer, String> labelMap = new LinkedHashMap<>();
        labelMap.put(11, "Alpha");
        labelMap.put(22, "Beta");

        ModelMeta meta = ModelMeta.createDefault(
                System.currentTimeMillis(),
                dim,
                2,
                true,
                labelMap,
                "1.0-test",
                "unit-test"
        );

        // Save snapshot using your real method
        File file = ModelSnapshotIO.save(ctx, meta, embeddings, labels);
        assertTrue(file.exists());

        // Load snapshot using your actual API
        ModelSnapshotIO.LoadedSnapshot loaded = ModelSnapshotIO.loadLatest(ctx);
        assertNotNull(loaded);
        assertNotNull(loaded.meta);

        assertEquals(meta.embeddingDim, loaded.meta.embeddingDim);
        assertEquals(meta.numItems, loaded.meta.numItems);
        assertEquals(meta.l2Normalized, loaded.meta.l2Normalized);
        assertEquals("Alpha", loaded.meta.labelMap.get(11));
        assertEquals("Beta", loaded.meta.labelMap.get(22));
    }
}
