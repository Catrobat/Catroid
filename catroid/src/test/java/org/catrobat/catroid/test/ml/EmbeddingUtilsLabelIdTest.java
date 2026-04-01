package org.catrobat.catroid.test.ml;

import org.catrobat.catroid.ObjectTrainAndRecognition.ml.EmbeddingUtils;
import org.junit.Test;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.junit.Assert.*;

public class EmbeddingUtilsLabelIdTest {
    @Test
    public void resolveOrCreateLabelId_reusesExistingAndCreatesNewIds() {
        Map<Integer, String> labelMap = new LinkedHashMap<>();
        labelMap.put(1, "Cat");
        labelMap.put(2, "Dog");

        int id1 = EmbeddingUtils.resolveOrCreateLabelId(labelMap, "Cat");
        int id2 = EmbeddingUtils.resolveOrCreateLabelId(labelMap, "Dog");
        int id3 = EmbeddingUtils.resolveOrCreateLabelId(labelMap, "Bird");

        assertEquals(1, id1);
        assertEquals(2, id2);
        assertEquals(3, id3);
    }
}