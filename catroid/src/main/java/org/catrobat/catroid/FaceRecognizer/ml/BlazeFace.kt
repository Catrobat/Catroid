package org.catrobat.catroid.FaceRecognizer.ml

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.RectF
import android.os.Build
import android.os.Trace
import androidx.annotation.RequiresApi
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.channels.FileChannel
import kotlin.collections.ArrayList
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.indices
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.sqrt

class BlazeFace private constructor() {
    // Pre-allocated buffers.
    private val intValues = IntArray(INPUT_SIZE_WIDTH * INPUT_SIZE_HEIGHT)
    private val floatValues = Array(1) {
        Array(INPUT_SIZE_HEIGHT) {
            Array(INPUT_SIZE_WIDTH) { FloatArray(3) }
        }
    }
    private val inputArray: Array<Any> = arrayOf(floatValues)
    private lateinit var outputScores: FloatBuffer
    private lateinit var outputBoxes: FloatBuffer
    private lateinit var outputMap: MutableMap<Int, Any>
    private lateinit var interpreter: Interpreter
    private lateinit var anchors: MutableList<Anchor>

    private class Anchor {
        var x_center = 0f
        var y_center = 0f
        var h = 0f
        var w = 0f
    }

    /** A detected face: box plus the six landmarks, all in INPUT_SIZE pixel space.  */
    class FaceBox(
        @JvmField val location: RectF,
        /** 12 floats: x,y for right eye, left eye, nose, mouth, right ear, left ear.  */
        @JvmField val keypoints: FloatArray
    ) {
        /** Midpoint of the two eyes, or null if the landmarks look unusable.  */
        fun eyeMidpoint(): FloatArray? {
            if (keypoints.size < 4) {
                return null
            }
            return floatArrayOf(
                (keypoints[0] + keypoints[2]) / 2f,
                (keypoints[1] + keypoints[3]) / 2f
            )
        }
    }

    private inner class Detection(
        val location: RectF,
        val score: Float,
        val keypoints: FloatArray
    )

    private inner class IndexedScore(val index: Int, val score: Float)

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun detect(bitmap: Bitmap): MutableList<RectF> {
        // Log this method so that it can be analyzed with systrace.
        Trace.beginSection("detect")

        Trace.beginSection("preprocessBitmap")
        // Preprocess the image data from 0-255 int to normalized float based
        // on the provided parameters.
        bitmap.getPixels(intValues, 0, INPUT_SIZE_WIDTH, 0, 0, INPUT_SIZE_WIDTH, INPUT_SIZE_HEIGHT)

        for (i in 0..<INPUT_SIZE_HEIGHT) {
            for (j in 0..<INPUT_SIZE_WIDTH) {
                val p = intValues[i * INPUT_SIZE_WIDTH + j]

                floatValues[0][i][j][2] = (p and 0xFF) / 127.5f - 1
                floatValues[0][i][j][1] = ((p shr 8) and 0xFF) / 127.5f - 1
                floatValues[0][i][j][0] = ((p shr 16) and 0xFF) / 127.5f - 1
            }
        }
        Trace.endSection() // preprocessBitmap

        // Run the inference call.
        Trace.beginSection("run")
        interpreter.runForMultipleInputsOutputs(inputArray, outputMap)
        Trace.endSection()

        outputScores.flip()
        outputBoxes.flip()

        val detections: MutableList<Detection> = ArrayList<Detection>()
        for (i in 0..<NUM_BOXES) {
            var score = outputScores[i]
            score = if (score < -100.0f) -100.0f else score
            score = if (score > 100.0f) 100.0f else score
            score = 1.0f / (1.0f + exp(-score.toDouble()).toFloat())

            if (score <= MIN_SCORE_THRESH) continue

            var x_center: Float = outputBoxes[i * NUM_COORDS]
            var y_center: Float = outputBoxes[i * NUM_COORDS + 1]
            var w: Float = outputBoxes[i * NUM_COORDS + 2]
            var h: Float = outputBoxes[i * NUM_COORDS + 3]

            x_center =
                x_center / X_SCALE * anchors[i].w + anchors[i].x_center
            y_center =
                y_center / Y_SCALE * anchors[i].h + anchors[i].y_center

            h = h / H_SCALE * anchors[i].h
            w = w / W_SCALE * anchors[i].w

            val ymin = y_center - h / 2f
            val xmin = x_center - w / 2f
            val ymax = y_center + h / 2f
            val xmax = x_center + w / 2f

            // The remaining 12 coordinates are the six landmarks. The original code
            // decoded only the box and discarded these, which is why faces were never
            // aligned before being handed to FaceNet.
            val keypoints = FloatArray(12)
            for (k in 0..5) {
                val kx: Float = outputBoxes[i * NUM_COORDS + 4 + k * 2]
                val ky: Float = outputBoxes[i * NUM_COORDS + 5 + k * 2]
                keypoints[k * 2] =
                    ((kx / X_SCALE * anchors[i].w + anchors[i].x_center)
                        * INPUT_SIZE_WIDTH)
                keypoints[k * 2 + 1] =
                    ((ky / Y_SCALE * anchors[i].h + anchors[i].y_center)
                        * INPUT_SIZE_HEIGHT)
            }

            detections.add(Detection(RectF(xmin, ymin, xmax, ymax), score, keypoints))
        }

        outputScores.clear()
        outputBoxes.clear()

        // Check if there are any detections at all.
        if (detections.isEmpty()) {
            return ArrayList<RectF>()
        }

        val indexed_scores: MutableList<IndexedScore> = ArrayList<IndexedScore>()
        for (index in detections.indices) {
            indexed_scores.add(
                IndexedScore(index, detections[index].score)
            )
        }
        indexed_scores.sortWith(
            Comparator { o1, o2 ->
                o2.score.compareTo(o1.score)
            }
        )

        val retained = WeightedNonMaxSuppression(indexed_scores, detections)

        Trace.endSection() // "detect"

        val boxes: MutableList<RectF> = ArrayList<RectF>()
        for (f in retained) {
            boxes.add(f.location)
        }
        lastFaces = retained
        return boxes
    }

    /** Faces from the most recent detect() call, with landmarks.  */
    private var lastFaces: MutableList<FaceBox> = ArrayList<FaceBox>()

    /** Runs detection and returns the boxes together with their landmarks.  */
    @RequiresApi(api = Build.VERSION_CODES.N)
    fun detectWithLandmarks(bitmap: Bitmap): MutableList<FaceBox> {
        detect(bitmap)
        return lastFaces
    }

    private fun WeightedNonMaxSuppression(
        indexed_scores: MutableList<IndexedScore>,
        detections: MutableList<Detection>
    ): MutableList<FaceBox> {
        val remained_indexed_scores: MutableList<IndexedScore> =
            ArrayList<IndexedScore>(indexed_scores)

        val remained: MutableList<IndexedScore> = ArrayList<IndexedScore>()
        val candidates: MutableList<IndexedScore> = ArrayList<IndexedScore>()
        val output_locations: MutableList<FaceBox> = ArrayList<FaceBox>()

        while (remained_indexed_scores.isNotEmpty()) {
            val detection = detections[remained_indexed_scores[0].index]
            if (detection.score.toInt() < -1f) {
                break
            }

            remained.clear()
            candidates.clear()
            val location = RectF(detection.location)
            // This includes the first box.
            for (indexed_score in remained_indexed_scores) {
                val rest_location = RectF(detections[indexed_score.index].location)
                val similarity =
                    OverlapSimilarity(rest_location, location)
                if (similarity > MIN_SUPPRESSION_THRESHOLD) {
                    candidates.add(indexed_score)
                } else {
                    remained.add(indexed_score)
                }
            }
            val weighted_location = RectF(detection.location)
            if (candidates.isNotEmpty()) {
                var w_xmin = 0.0f
                var w_ymin = 0.0f
                var w_xmax = 0.0f
                var w_ymax = 0.0f
                var total_score = 0.0f
                for (candidate in candidates) {
                    total_score += candidate.score
                    val bbox =
                        detections[candidate.index].location
                    w_xmin += bbox.left * candidate.score
                    w_ymin += bbox.top * candidate.score
                    w_xmax += bbox.right * candidate.score
                    w_ymax += bbox.bottom * candidate.score
                }
                weighted_location.left = w_xmin / total_score * INPUT_SIZE_WIDTH
                weighted_location.top = w_ymin / total_score * INPUT_SIZE_HEIGHT
                weighted_location.right = w_xmax / total_score * INPUT_SIZE_WIDTH
                weighted_location.bottom = w_ymax / total_score * INPUT_SIZE_HEIGHT
            }
            remained_indexed_scores.clear()
            remained_indexed_scores.addAll(remained)
            // Landmarks come from the seed, which is the highest scoring detection
            // in this cluster. Averaging them adds nothing and blurs the eyes.
            output_locations.add(FaceBox(weighted_location, detection.keypoints))
        }

        return output_locations
    }

    // Computes an overlap similarity between two rectangles. Similarity measure is
    // defined by overlap_type parameter.
    private fun OverlapSimilarity(rect1: RectF, rect2: RectF): Float {
        if (!RectF.intersects(rect1, rect2)) return 0.0f
        val intersection = RectF()
        intersection.setIntersect(rect1, rect2)

        val intersection_area = intersection.height() * intersection.width()
        val normalization = (rect1.height() * rect1.width()
            + rect2.height() * rect2.width() - intersection_area)

        return if (normalization > 0.0f) intersection_area / normalization else 0.0f
    }

    fun close() {
        interpreter.close()
    }

    companion object {
        private const val MODEL_FILE = "face_detection_front.tflite"

        const val INPUT_SIZE_HEIGHT: Int = 128
        const val INPUT_SIZE_WIDTH: Int = 128

        // Only return this many results.
        private const val NUM_BOXES = 896
        private const val NUM_COORDS = 16
        private const val BYTE_SIZE_OF_FLOAT = 4

        /* 0.95 rejected valid faces after sunlight/contrast changed detector confidence.
       Identity acceptance is still controlled separately by FaceNet similarity and
       inter-person margin, so this only makes face localisation more tolerant. */
        private const val MIN_SCORE_THRESH = 0.75f

        private val strides = intArrayOf(8, 16, 16, 16)

        private const val ASPECT_RATIOS_SIZE = 1

        private const val MIN_SCALE = 0.1484375f
        private const val MAX_SCALE = 0.75f

        private const val ANCHOR_OFFSET_X = 0.5f
        private const val ANCHOR_OFFSET_Y = 0.5f

        private const val X_SCALE = 128f
        private const val Y_SCALE = 128f
        private const val H_SCALE = 128f
        private const val W_SCALE = 128f

        private const val MIN_SUPPRESSION_THRESHOLD = 0.3f

        /** Memory-map the model file in Assets.  */
        @Throws(IOException::class)
        private fun loadModelFile(assets: AssetManager): ByteBuffer {
            assets.openFd(MODEL_FILE).use { descriptor ->
                FileInputStream(descriptor.fileDescriptor).use { input ->
                    return input.channel.map(
                        FileChannel.MapMode.READ_ONLY,
                        descriptor.startOffset,
                        descriptor.declaredLength
                    )
                }
            }
        }

//        private fun CalculateScale(
//            min_scale: Float, max_scale: Float, stride_index: Int,
//            num_strides: Int
//        ): Float {
//            return min_scale +
//                (max_scale - min_scale) * 1.0f * stride_index / (num_strides - 1.0f)
//        }

        private data class AnchorLayerInfo(
            val nextLayerId: Int,
            val anchorCount: Int
        )

        private fun GenerateAnchors(): MutableList<Anchor> {
            val anchors = mutableListOf<Anchor>()
            var layerId = 0

            while (layerId < strides.size) {
                val layerInfo = calculateAnchorLayerInfo(layerId)

                addLayerAnchors(
                    anchors = anchors,
                    stride = strides[layerId],
                    anchorCount = layerInfo.anchorCount
                )

                layerId = layerInfo.nextLayerId
            }

            return anchors
        }

        private fun calculateAnchorLayerInfo(
            layerId: Int
        ): AnchorLayerInfo {
            var currentLayerId = layerId
            var anchorCount = 0

            while (
                currentLayerId < strides.size &&
                strides[currentLayerId] == strides[layerId]
            ) {
                /*
                 * Each aspect ratio produces one anchor, and an additional
                 * interpolated-scale anchor is generated for every layer.
                 */
                anchorCount += ASPECT_RATIOS_SIZE + 1
                currentLayerId++
            }

            return AnchorLayerInfo(
                nextLayerId = currentLayerId,
                anchorCount = anchorCount
            )
        }

        private fun addLayerAnchors(
            anchors: MutableList<Anchor>,
            stride: Int,
            anchorCount: Int
        ) {
            val featureMapHeight =
                ceil(INPUT_SIZE_HEIGHT.toDouble() / stride).toInt()

            val featureMapWidth =
                ceil(INPUT_SIZE_WIDTH.toDouble() / stride).toInt()

            for (y in 0 until featureMapHeight) {
                val yCenter =
                    (y + ANCHOR_OFFSET_Y) / featureMapHeight.toFloat()

                for (x in 0 until featureMapWidth) {
                    val xCenter =
                        (x + ANCHOR_OFFSET_X) / featureMapWidth.toFloat()

                    addAnchorsAtPosition(
                        anchors = anchors,
                        anchorCount = anchorCount,
                        xCenter = xCenter,
                        yCenter = yCenter
                    )
                }
            }
        }

        private fun addAnchorsAtPosition(
            anchors: MutableList<Anchor>,
            anchorCount: Int,
            xCenter: Float,
            yCenter: Float
        ) {
            repeat(anchorCount) {
                anchors.add(
                    Anchor().apply {
                        x_center = xCenter
                        y_center = yCenter
                        w = 1.0f
                        h = 1.0f
                    }
                )
            }
        }

        @JvmStatic
        fun create(
            assetManager: AssetManager
        ): BlazeFace {
            val b = BlazeFace()

            try {
                val options = Interpreter.Options()
                // Inference is most of the time cost and ran single threaded.
                options.setNumThreads(4)
                b.interpreter = Interpreter(loadModelFile(assetManager), options)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

            // Pre-allocate buffers.
            b.outputScores = ByteBuffer.allocateDirect(NUM_BOXES * BYTE_SIZE_OF_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()

            b.outputBoxes = ByteBuffer.allocateDirect(NUM_BOXES * NUM_COORDS * BYTE_SIZE_OF_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()

            b.outputMap = hashMapOf(
                0 to b.outputBoxes,
                1 to b.outputScores
            )

            b.anchors = GenerateAnchors()

            return b
        }
    }
}