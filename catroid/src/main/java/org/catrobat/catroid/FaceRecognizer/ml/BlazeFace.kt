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
        var xaCenter = 0f
        var yaCenter = 0f
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

            var xaCenter: Float = outputBoxes[i * NUM_COORDS]
            var yaCenter: Float = outputBoxes[i * NUM_COORDS + 1]
            var w: Float = outputBoxes[i * NUM_COORDS + 2]
            var h: Float = outputBoxes[i * NUM_COORDS + 3]

            xaCenter =
                xaCenter / X_SCALE * anchors[i].w + anchors[i].xaCenter
            yaCenter =
                yaCenter / Y_SCALE * anchors[i].h + anchors[i].yaCenter

            h = h / H_SCALE * anchors[i].h
            w = w / W_SCALE * anchors[i].w

            val ymin = yaCenter - h / 2f
            val xmin = xaCenter - w / 2f
            val ymax = yaCenter + h / 2f
            val xmax = xaCenter + w / 2f

            // The remaining 12 coordinates are the six landmarks. The original code
            // decoded only the box and discarded these, which is why faces were never
            // aligned before being handed to FaceNet.
            val keypoints = FloatArray(12)
            for (k in 0..5) {
                val kx: Float = outputBoxes[i * NUM_COORDS + 4 + k * 2]
                val ky: Float = outputBoxes[i * NUM_COORDS + 5 + k * 2]
                keypoints[k * 2] =
                    ((kx / X_SCALE * anchors[i].w + anchors[i].xaCenter)
                        * INPUT_SIZE_WIDTH)
                keypoints[k * 2 + 1] =
                    ((ky / Y_SCALE * anchors[i].h + anchors[i].yaCenter)
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

        val indexedScores: MutableList<IndexedScore> = ArrayList<IndexedScore>()
        for (index in detections.indices) {
            indexedScores.add(
                IndexedScore(index, detections[index].score)
            )
        }
        indexedScores.sortWith(
            Comparator { o1, o2 ->
                o2.score.compareTo(o1.score)
            }
        )

        val retained: List<FaceBox> =
            WeightedNonMaxSuppression(
                indexedScores,
                detections
            ).toList()

        Trace.endSection() // "detect"

        val boxes: MutableList<RectF> = ArrayList<RectF>()
        for (f in retained) {
            boxes.add(f.location)
        }
        lastFaces = retained
        return boxes
    }

    /** Faces from the most recent detect() call, with landmarks.  */
    private var lastFaces: List<FaceBox> = ArrayList<FaceBox>()

    /** Runs detection and returns the boxes together with their landmarks.  */
    @RequiresApi(api = Build.VERSION_CODES.N)
    fun detectWithLandmarks(bitmap: Bitmap): List<FaceBox> {
        detect(bitmap)
        return lastFaces
    }

    private fun WeightedNonMaxSuppression(
        indexedScores: MutableList<IndexedScore>,
        detections: List<Detection>
    ): MutableList<FaceBox> {
        val remainedIndexedScores: MutableList<IndexedScore> =
            ArrayList<IndexedScore>(indexedScores)

        val remained: MutableList<IndexedScore> = ArrayList<IndexedScore>()
        val candidates: MutableList<IndexedScore> = ArrayList<IndexedScore>()
        val outputLocations: MutableList<FaceBox> = ArrayList<FaceBox>()

        while (remainedIndexedScores.isNotEmpty()) {
            val detection = detections[remainedIndexedScores[0].index]
            if (detection.score.toInt() < -1f) {
                break
            }

            remained.clear()
            candidates.clear()
            val location = RectF(detection.location)
            // This includes the first box.
            for (indexed_score in remainedIndexedScores) {
                val restLocation = RectF(detections[indexed_score.index].location)
                val similarity =
                    OverlapSimilarity(restLocation, location)
                if (similarity > MIN_SUPPRESSION_THRESHOLD) {
                    candidates.add(indexed_score)
                } else {
                    remained.add(indexed_score)
                }
            }
            val weightedLocation = RectF(detection.location)
            if (candidates.isNotEmpty()) {
                var wxmin = 0.0f
                var wymin = 0.0f
                var wxmax = 0.0f
                var wymax = 0.0f
                var totalScore = 0.0f
                for (candidate in candidates) {
                    totalScore += candidate.score
                    val bbox =
                        detections[candidate.index].location
                    wxmin += bbox.left * candidate.score
                    wymin += bbox.top * candidate.score
                    wxmax += bbox.right * candidate.score
                    wymax += bbox.bottom * candidate.score
                }
                weightedLocation.left = wxmin / totalScore * INPUT_SIZE_WIDTH
                weightedLocation.top = wymin / totalScore * INPUT_SIZE_HEIGHT
                weightedLocation.right = wxmax / totalScore * INPUT_SIZE_WIDTH
                weightedLocation.bottom = wymax / totalScore * INPUT_SIZE_HEIGHT
            }
            remainedIndexedScores.clear()
            remainedIndexedScores.addAll(remained)
            // Landmarks come from the seed, which is the highest scoring detection
            // in this cluster. Averaging them adds nothing and blurs the eyes.
            outputLocations.add(FaceBox(weightedLocation, detection.keypoints))
        }

        return outputLocations
    }

    // Computes an overlap similarity between two rectangles. Similarity measure is
    // defined by overlap_type parameter.
    private fun OverlapSimilarity(rect1: RectF, rect2: RectF): Float {
        if (!RectF.intersects(rect1, rect2)) return 0.0f
        val intersection = RectF()
        intersection.setIntersect(rect1, rect2)

        val intersectionArea = intersection.height() * intersection.width()
        val normalization = (rect1.height() * rect1.width()
            + rect2.height() * rect2.width() - intersectionArea)

        return if (normalization > 0.0f) intersectionArea / normalization else 0.0f
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
                        xaCenter = xCenter
                        yaCenter = yCenter
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