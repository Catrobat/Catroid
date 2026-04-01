package org.catrobat.catroid.FaceRecognizer.ml;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Build;
import android.os.Trace;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.RequiresApi;

public class BlazeFace {
    private static final String MODEL_FILE = "face_detection_front.tflite";

    public static final int INPUT_SIZE_HEIGHT = 128;
    public static final int INPUT_SIZE_WIDTH = 128;

    // Only return this many results.
    private static final int NUM_BOXES = 896;
    private static final int NUM_COORDS = 16;
    private static final int BYTE_SIZE_OF_FLOAT = 4;

    private static final float MIN_SCORE_THRESH = 0.95f;

    private static final int[] strides = {8, 16, 16, 16};

    private static final int ASPECT_RATIOS_SIZE = 1;

    private static final float MIN_SCALE = 0.1484375f;
    private static final float MAX_SCALE = 0.75f;

    private static final float ANCHOR_OFFSET_X = 0.5f;
    private static final float ANCHOR_OFFSET_Y = 0.5f;

    private static final float X_SCALE = 128f;
    private static final float Y_SCALE = 128f;
    private static final float H_SCALE = 128f;
    private static final float W_SCALE = 128f;

    private static final float MIN_SUPPRESSION_THRESHOLD = 0.3f;

    // Pre-allocated buffers.
    private int[] intValues;
    private float[][][][] floatValues;
    private Object[] inputArray;

    private FloatBuffer outputScores;
    private FloatBuffer outputBoxes;
    private Map<Integer, Object> outputMap;

    private Interpreter interpreter;

    private List<Anchor> anchors;

    private static class Anchor {
        private float x_center;
        private float y_center;
        private float h;
        private float w;
    }

    private class Detection {
        private RectF location;
        private float score;

        Detection(RectF location, float score) {
            this.location = location;
            this.score = score;
        }
    }

    private class IndexedScore {
        private int index;
        private float score;

        IndexedScore(int index, float score) {
            this.index = index;
            this.score = score;
        }
    }

    /** Memory-map the model file in Assets. */
    private static ByteBuffer loadModelFile(AssetManager assets)
            throws IOException {
        AssetFileDescriptor fileDescriptor = assets.openFd(MODEL_FILE);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private static float CalculateScale(float min_scale, float max_scale, int stride_index,
            int num_strides) {
        return min_scale +
                (max_scale - min_scale) * 1.0f * stride_index / (num_strides - 1.0f);
    }

    private static List<Anchor> GenerateAnchors() {
        List<Anchor> anchors = new ArrayList<>();
        int layer_id = 0;

        while (layer_id < strides.length) {
            List<Float> anchor_height = new ArrayList<>();
            List<Float> anchor_width = new ArrayList<>();
            List<Float> aspect_ratios = new ArrayList<>();
            List<Float> scales = new ArrayList<>();

            // For same strides, we merge the anchors in the same order.
            int last_same_stride_layer = layer_id;
            while (last_same_stride_layer < strides.length &&
                    strides[last_same_stride_layer] == strides[layer_id]) {
                float scale = CalculateScale(MIN_SCALE, MAX_SCALE,
                        last_same_stride_layer, strides.length);
                for (int aspect_ratio_id = 0; aspect_ratio_id < ASPECT_RATIOS_SIZE; ++aspect_ratio_id) {
                    aspect_ratios.add(1.0f);
                    scales.add(scale);
                }
                float scale_next =
                        last_same_stride_layer == strides.length - 1
                                ? 1.0f
                                : CalculateScale(MIN_SCALE, MAX_SCALE,
                                        last_same_stride_layer + 1,
                                        strides.length);
                scales.add((float) Math.sqrt(scale * scale_next));
                aspect_ratios.add(1.0f);
                last_same_stride_layer++;
            }

            for (int i = 0; i < aspect_ratios.size(); ++i) {
                float ratio_sqrts = (float) Math.sqrt(aspect_ratios.get(i));
                anchor_height.add(scales.get(i) / ratio_sqrts);
                anchor_width.add(scales.get(i) * ratio_sqrts);
            }

            int stride = strides[layer_id];
            int feature_map_height = (int) Math.ceil(1.0f * INPUT_SIZE_HEIGHT / stride);
            int feature_map_width = (int) Math.ceil(1.0f * INPUT_SIZE_WIDTH / stride);

            for (int y = 0; y < feature_map_height; ++y) {
                for (int x = 0; x < feature_map_width; ++x) {
                    for (int anchor_id = 0; anchor_id < anchor_height.size(); ++anchor_id) {
                        // TODO: Support specifying anchor_offset_x, anchor_offset_y.
                        float x_center = (x + ANCHOR_OFFSET_X) * 1.0f / feature_map_width;
                        float y_center = (y + ANCHOR_OFFSET_Y) * 1.0f / feature_map_height;

                        Anchor new_anchor = new Anchor();
                        new_anchor.x_center = x_center;
                        new_anchor.y_center = y_center;
                        new_anchor.w = 1.0f;
                        new_anchor.h = 1.0f;

                        anchors.add(new_anchor);
                    }
                }
            }
            layer_id = last_same_stride_layer;
        }
        return anchors;
    }

    public static BlazeFace create(
            final AssetManager assetManager) {
        final BlazeFace b = new BlazeFace();

        try {
            b.interpreter = new Interpreter(loadModelFile(assetManager));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Pre-allocate buffers.
        b.intValues = new int[INPUT_SIZE_WIDTH * INPUT_SIZE_HEIGHT];
        b.floatValues = new float[1][INPUT_SIZE_HEIGHT][INPUT_SIZE_WIDTH][3];
        b.inputArray = new Object[]{b.floatValues};

        b.outputScores = ByteBuffer.allocateDirect(NUM_BOXES * BYTE_SIZE_OF_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        b.outputBoxes = ByteBuffer.allocateDirect(NUM_BOXES * NUM_COORDS * BYTE_SIZE_OF_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        b.outputMap = new HashMap<>();
        b.outputMap.put(0, b.outputBoxes);
        b.outputMap.put(1, b.outputScores);

        b.anchors = GenerateAnchors();

        return b;
    }

    private BlazeFace() {}

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<RectF> detect(Bitmap bitmap) {
        // Log this method so that it can be analyzed with systrace.
        Trace.beginSection("detect");

        Trace.beginSection("preprocessBitmap");
        // Preprocess the image data from 0-255 int to normalized float based
        // on the provided parameters.
        bitmap.getPixels(intValues, 0, INPUT_SIZE_WIDTH, 0, 0, INPUT_SIZE_WIDTH, INPUT_SIZE_HEIGHT);

        for (int i = 0; i < INPUT_SIZE_HEIGHT; ++i) {
            for (int j = 0; j < INPUT_SIZE_WIDTH; ++j) {
                int p = intValues[i * INPUT_SIZE_WIDTH + j];

                floatValues[0][i][j][2] = (p & 0xFF) / 127.5f - 1;
                floatValues[0][i][j][1] = ((p >> 8) & 0xFF) / 127.5f - 1;
                floatValues[0][i][j][0] = ((p >> 16) & 0xFF) / 127.5f - 1;
            }
        }
        Trace.endSection(); // preprocessBitmap

        // Run the inference call.
        Trace.beginSection("run");
        interpreter.runForMultipleInputsOutputs(inputArray, outputMap);
        Trace.endSection();

        outputScores.flip();
        outputBoxes.flip();

        List<Detection> detections = new ArrayList<>();
        for (int i = 0; i < NUM_BOXES; i++) {
            float score = outputScores.get(i);
            score = score < -100.0f ? -100.0f : score;
            score = score > 100.0f ? 100.0f : score;
            score = 1.0f / (1.0f + (float) Math.exp(-score));

            if (score <= MIN_SCORE_THRESH)
                continue;

            float x_center = outputBoxes.get(i * NUM_COORDS);
            float y_center = outputBoxes.get(i * NUM_COORDS + 1);
            float w = outputBoxes.get(i * NUM_COORDS + 2);
            float h = outputBoxes.get(i * NUM_COORDS + 3);

            x_center =
                    x_center / X_SCALE * anchors.get(i).w + anchors.get(i).x_center;
            y_center =
                    y_center / Y_SCALE * anchors.get(i).h + anchors.get(i).y_center;

            h = h / H_SCALE * anchors.get(i).h;
            w = w / W_SCALE * anchors.get(i).w;

            float ymin = y_center - h / 2.f;
            float xmin = x_center - w / 2.f;
            float ymax = y_center + h / 2.f;
            float xmax = x_center + w / 2.f;

            detections.add(new Detection(new RectF(xmin, ymin, xmax, ymax), score));
        }

        outputScores.clear();
        outputBoxes.clear();

        // Check if there are any detections at all.
        if (detections.isEmpty()) {
            return new ArrayList<>();
        }

        List<IndexedScore> indexed_scores = new ArrayList<>();
        for (int index = 0; index < detections.size(); ++index) {
            indexed_scores.add(
                    new IndexedScore(index, detections.get(index).score));
        }
        indexed_scores.sort((o1, o2) -> {
            if (o1.score > o2.score) return 1;
            else if (o1.score == o2.score) return 0;
            return -1;
        });

        List<RectF> retained_detections = WeightedNonMaxSuppression(indexed_scores, detections);

        Trace.endSection(); // "detect"
        return retained_detections;
    }

    private List<RectF> WeightedNonMaxSuppression(List<IndexedScore> indexed_scores,
            List<Detection> detections) {
        List<IndexedScore> remained_indexed_scores = new ArrayList<>(indexed_scores);

        List<IndexedScore> remained = new ArrayList<>();
        List<IndexedScore> candidates = new ArrayList<>();
        List<RectF> output_locations = new ArrayList<>();

        while (!remained_indexed_scores.isEmpty()) {
            Detection detection = detections.get(remained_indexed_scores.get(0).index);
            if ((int) detection.score < -1.f) {
                break;
            }

            remained.clear();
            candidates.clear();
            RectF location = new RectF(detection.location);
            // This includes the first box.
            for (IndexedScore indexed_score : remained_indexed_scores) {
                RectF rest_location = new RectF(detections.get(indexed_score.index).location);
                float similarity =
                        OverlapSimilarity(rest_location, location);
                if (similarity > MIN_SUPPRESSION_THRESHOLD) {
                    candidates.add(indexed_score);
                } else {
                    remained.add(indexed_score);
                }
            }
            RectF weighted_location = new RectF(detection.location);
            if (!candidates.isEmpty()) {
                float w_xmin = 0.0f;
                float w_ymin = 0.0f;
                float w_xmax = 0.0f;
                float w_ymax = 0.0f;
                float total_score = 0.0f;
                for (IndexedScore candidate : candidates) {
                    total_score += candidate.score;
                    RectF bbox =
                            detections.get(candidate.index).location;
                    w_xmin += bbox.left * candidate.score;
                    w_ymin += bbox.top * candidate.score;
                    w_xmax += bbox.right * candidate.score;
                    w_ymax += bbox.bottom * candidate.score;

                }
                weighted_location.left = w_xmin / total_score * INPUT_SIZE_WIDTH;
                weighted_location.top = w_ymin / total_score * INPUT_SIZE_HEIGHT;
                weighted_location.right = w_xmax / total_score * INPUT_SIZE_WIDTH;
                weighted_location.bottom = w_ymax / total_score * INPUT_SIZE_HEIGHT;
            }
            remained_indexed_scores.clear();
            remained_indexed_scores.addAll(remained);
            output_locations.add(weighted_location);
        }

        return output_locations;
    }

    // Computes an overlap similarity between two rectangles. Similarity measure is
    // defined by overlap_type parameter.
    private float OverlapSimilarity(RectF rect1, RectF rect2) {
        if (!RectF.intersects(rect1, rect2)) return 0.0f;
        RectF intersection = new RectF();
        intersection.setIntersect(rect1, rect2);

        float intersection_area = intersection.height() * intersection.width();
        float normalization = rect1.height() * rect1.width()
                + rect2.height() * rect2.width() - intersection_area;

        return normalization > 0.0f ? intersection_area / normalization : 0.0f;
    }

    public void close() {
        interpreter.close();
    }
}

