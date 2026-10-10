package org.catrobat.catroid.FaceRecognizer.ml

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.os.Trace
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.channels.FileChannel

/**
 * MobileFaceNet: turns an aligned 112x112 face into a 128 value embedding.
 *
 * The model (mobile_facenet.tflite, 4 MB) is the Qualcomm AI Hub release
 * v0.63.0 of foamliu/MobileFaceNet (Apache 2.0), trained with the ArcFace loss
 * on MS-Celeb-1M. Microsoft withdrew that dataset in 2019.
 *
 * The model has two inputs, img1 and img2, each float32 [1, 3, 112, 112]: RGB,
 * channel planes first (NCHW), values 0..1. Its output, embeddings, is float32
 * [2, 128], one row per input. Every run therefore embeds two faces, e.g. a
 * face and its mirror image, for the cost of about one and a half.
 *
 * Embeddings are returned as the model produces them, not normalised.
 */
class MobileFaceNet private constructor(private val interpreter: Interpreter) {
    private val pixels = IntArray(INPUT_SIZE * INPUT_SIZE)
    private val firstInput = newInputBuffer()
    private val secondInput = newInputBuffer()
    private val output: ByteBuffer =
        ByteBuffer.allocateDirect(FACES_PER_RUN * EMBEDDING_SIZE * BYTE_SIZE_OF_FLOAT)
            .order(ByteOrder.nativeOrder())

    private val inputs = arrayOfNulls<Any>(FACES_PER_RUN).apply {
        this[interpreter.getInputIndex(FIRST_INPUT)] = firstInput
        this[interpreter.getInputIndex(SECOND_INPUT)] = secondInput
    }
    private val outputs = mapOf<Int, Any>(interpreter.getOutputIndex(OUTPUT) to output)

    /**
     * Embeds faces two per run. Every bitmap must be [INPUT_SIZE] x [INPUT_SIZE].
     * Returns one embedding per face, in order.
     */
    fun embed(faces: List<Bitmap>): List<FloatArray> {
        val embeddings = ArrayList<FloatArray>(faces.size)
        var index = 0
        while (index < faces.size) {
            val first = faces[index]
            val second = faces.getOrNull(index + 1)
            val pair = embedPair(first, second ?: first)
            embeddings.add(pair[0])
            if (second != null) {
                embeddings.add(pair[1])
            }
            index += FACES_PER_RUN
        }
        return embeddings
    }

    private fun embedPair(first: Bitmap, second: Bitmap): Array<FloatArray> {
        Trace.beginSection("MobileFaceNet.embedPair")
        try {
            load(first, firstInput)
            load(second, secondInput)

            output.clear()
            interpreter.runForMultipleInputsOutputs(inputs, outputs)

            val values = output.duplicate().order(ByteOrder.nativeOrder()).apply { clear() }.asFloatBuffer()
            return Array(FACES_PER_RUN) { row ->
                FloatArray(EMBEDDING_SIZE) { column -> values[row * EMBEDDING_SIZE + column] }
            }
        } finally {
            Trace.endSection()
        }
    }

    private fun load(face: Bitmap, buffer: ByteBuffer) {
        require(face.width == INPUT_SIZE && face.height == INPUT_SIZE) {
            "Face must be ${INPUT_SIZE}x$INPUT_SIZE, was ${face.width}x${face.height}"
        }
        face.getPixels(pixels, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE)
        buffer.clear()
        fillInput(pixels, buffer.asFloatBuffer())
    }

    fun close() {
        interpreter.close()
    }

    companion object {
        private const val MODEL_FILE = "mobile_facenet.tflite"
        private const val FIRST_INPUT = "img1"
        private const val SECOND_INPUT = "img2"
        private const val OUTPUT = "embeddings"

        const val EMBEDDING_SIZE: Int = 128
        const val INPUT_SIZE: Int = 112

        private const val FACES_PER_RUN = 2
        private const val CHANNELS = 3
        private const val BYTE_SIZE_OF_FLOAT = 4
        private const val MAX_INTENSITY = 255f

        private fun newInputBuffer(): ByteBuffer =
            ByteBuffer.allocateDirect(CHANNELS * INPUT_SIZE * INPUT_SIZE * BYTE_SIZE_OF_FLOAT)
                .order(ByteOrder.nativeOrder())

        /**
         * Writes ARGB pixels as the model input: three planes, red, green, blue,
         * each 0..1. No other normalisation; MobileFaceNet was trained on that.
         */
        @JvmStatic
        fun fillInput(argb: IntArray, input: FloatBuffer) {
            val planeSize = argb.size
            for (i in argb.indices) {
                val pixel = argb[i]
                input.put(i, ((pixel shr 16) and 0xFF) / MAX_INTENSITY)
                input.put(planeSize + i, ((pixel shr 8) and 0xFF) / MAX_INTENSITY)
                input.put(2 * planeSize + i, (pixel and 0xFF) / MAX_INTENSITY)
            }
        }

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

        @JvmStatic
        fun create(assetManager: AssetManager): MobileFaceNet {
            val interpreter = try {
                Interpreter(loadModelFile(assetManager))
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
            try {
                return MobileFaceNet(interpreter)
            } catch (e: IllegalArgumentException) {
                // Not the expected img1/img2 -> embeddings signature.
                interpreter.close()
                throw RuntimeException(e)
            }
        }
    }
}
