package org.catrobat.catroid.FaceRecognizer.ml

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Trace
import org.catrobat.catroid.FaceRecognizer.env.ImageUtils
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.channels.FileChannel

class FaceNet private constructor() {
    // Pre-allocated buffers.
    private val intValues = IntArray(INPUT_SIZE_HEIGHT * INPUT_SIZE_WIDTH)
    private val rgbValues = FloatArray(INPUT_SIZE_HEIGHT * INPUT_SIZE_WIDTH * 3)
    private lateinit var inputBuffer: FloatBuffer
    private lateinit var outputBuffer: FloatBuffer
    private lateinit var bitmap: Bitmap
    private lateinit var interpreter: Interpreter

    fun getEmbeddings(originalBitmap: Bitmap, rect: Rect?): FloatBuffer {
        // Log this method so that it can be analyzed with systrace.
        Trace.beginSection("getEmbeddings")

        Trace.beginSection("preprocessBitmap")
        val canvas = Canvas(bitmap)
        canvas.drawBitmap(
            originalBitmap, rect,
            Rect(0, 0, INPUT_SIZE_WIDTH, INPUT_SIZE_HEIGHT), SCALE_PAINT
        )

        bitmap.getPixels(
            intValues, 0, INPUT_SIZE_WIDTH, 0, 0,
            INPUT_SIZE_WIDTH, INPUT_SIZE_HEIGHT
        )

        for (i in intValues.indices) {
            val p = intValues[i]

            rgbValues[i * 3 + 2] = (p and 0xFF).toFloat()
            rgbValues[i * 3 + 1] = ((p shr 8) and 0xFF).toFloat()
            rgbValues[i * 3 + 0] = ((p shr 16) and 0xFF).toFloat()
        }

        normalizeIllumination(rgbValues)
        ImageUtils.prewhiten(rgbValues, inputBuffer)

        Trace.endSection() // preprocessBitmap

        // Run the inference call.
        Trace.beginSection("run")
        outputBuffer.rewind()
        interpreter.run(inputBuffer, outputBuffer)
        outputBuffer.flip()
        Trace.endSection()

        Trace.endSection() // "getEmbeddings"
        return outputBuffer
    }

    fun close() {
        interpreter.close()
    }

    companion object {
        private const val MODEL_FILE = "facenet.tflite"

        const val EMBEDDING_SIZE: Int = 512

        private const val INPUT_SIZE_HEIGHT = 160
        private const val INPUT_SIZE_WIDTH = 160

        private const val BYTE_SIZE_OF_FLOAT = 4

        /**
         * Bilinear filtering when the face crop is scaled into the 160x160 input.
         * Without this the draw is nearest neighbour, which aliases badly on any
         * downscale and produces embeddings FaceNet was never trained to handle.
         */
        private val SCALE_PAINT =
            Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)

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
        fun create(assetManager: AssetManager): FaceNet {
            val f = FaceNet()

            try {
                f.interpreter = Interpreter(loadModelFile(assetManager))
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

            // Pre-allocate buffers.
            f.inputBuffer =
                ByteBuffer.allocateDirect(INPUT_SIZE_HEIGHT * INPUT_SIZE_WIDTH * 3 * BYTE_SIZE_OF_FLOAT)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer()
            f.outputBuffer = ByteBuffer.allocateDirect(EMBEDDING_SIZE * BYTE_SIZE_OF_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()

            f.bitmap =
                Bitmap.createBitmap(INPUT_SIZE_WIDTH, INPUT_SIZE_HEIGHT, Bitmap.Config.ARGB_8888)
            return f
        }

        /**
         * Removes colour cast and exposure differences before prewhitening.
         * 
         * A gallery photo and a live camera capture of the same face differ mostly in
         * white balance, exposure and contrast, not in identity. Two passes fix most
         * of that, and both the training side and the detection side run through here,
         * so the correction is identical on both.
         * 
         * Pass 1, grey world: scale each channel so all three have the same mean.
         * Pass 2, contrast stretch: map the 2nd and 98th percentile to 0 and 255.
         */
        private fun normalizeIllumination(rgb: FloatArray) {
            val pixels = rgb.size / 3
            if (pixels <= 0) {
                return
            }

            var sumR = 0.0
            var sumG = 0.0
            var sumB = 0.0
            for (i in 0..<pixels) {
                sumR += rgb[i * 3].toDouble()
                sumG += rgb[i * 3 + 1].toDouble()
                sumB += rgb[i * 3 + 2].toDouble()
            }
            val meanR = (sumR / pixels).toFloat()
            val meanG = (sumG / pixels).toFloat()
            val meanB = (sumB / pixels).toFloat()
            val grey = (meanR + meanG + meanB) / 3f

            if (meanR > 1f && meanG > 1f && meanB > 1f) {
                val gainR = grey / meanR
                val gainG = grey / meanG
                val gainB = grey / meanB
                for (i in 0..<pixels) {
                    rgb[i * 3] *= gainR
                    rgb[i * 3 + 1] *= gainG
                    rgb[i * 3 + 2] *= gainB
                }
            }

            // Percentiles from a 256 bin histogram. Cheap and accurate enough here.
            val histogram = IntArray(256)
            for (v in rgb) {
                var bin = v.toInt()
                if (bin < 0) {
                    bin = 0
                } else if (bin > 255) {
                    bin = 255
                }
                histogram[bin]++
            }

            val lowTarget = (rgb.size * 0.02f).toInt()
            val highTarget = (rgb.size * 0.98f).toInt()
            var low = 0
            var high = 255
            var running = 0
            for (i in 0..255) {
                running += histogram[i]
                if (running >= lowTarget) {
                    low = i
                    break
                }
            }
            running = 0
            for (i in 0..255) {
                running += histogram[i]
                if (running >= highTarget) {
                    high = i
                    break
                }
            }

            if (high - low < 16) {
                return
            }
            val scale = 255f / (high - low)
            for (i in rgb.indices) {
                val v = (rgb[i] - low) * scale
                rgb[i] = if (v < 0f) 0f else (if (v > 255f) 255f else v)
            }
        }
    }
}