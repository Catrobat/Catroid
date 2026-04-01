package org.catrobat.catroid.ObjectTrainAndRecognition.persist;

import java.util.Map;

public class ModelMeta {
    public final int schemaVersion;
    public final long createdAtEpochMs;
    public final int embeddingDim;
    public final int numItems;
    public final boolean l2Normalized;
    public final Map<Integer, String> labelMap;
    public final String trainerAppVersion;
    public final String notes;

    public ModelMeta(int schemaVersion, long createdAtEpochMs, int embeddingDim, int numItems,
                     boolean l2Normalized, Map<Integer, String> labelMap,
                     String trainerAppVersion, String notes) {
        this.schemaVersion = schemaVersion;
        this.createdAtEpochMs = createdAtEpochMs;
        this.embeddingDim = embeddingDim;
        this.numItems = numItems;
        this.l2Normalized = l2Normalized;
        this.labelMap = labelMap;
        this.trainerAppVersion = trainerAppVersion;
        this.notes = notes;
    }

    public static ModelMeta createDefault(long now, int dim, int numItems, boolean l2Normalized,
                                          Map<Integer,String> labelMap, String trainerAppVersion, String notes) {
        return new ModelMeta(1, now, dim, numItems, l2Normalized, labelMap, trainerAppVersion, notes);
    }
}
