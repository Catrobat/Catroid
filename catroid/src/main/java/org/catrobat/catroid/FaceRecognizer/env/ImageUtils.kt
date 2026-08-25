package org.catrobat.catroid.FaceRecognizer.env

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import org.catrobat.catroid.FaceRecognizer.env.FileUtils.saveBitmap
import java.nio.FloatBuffer
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

object ImageUtils {
    init {
        try {
            System.loadLibrary("tensorflow_demo")
        } catch (e: UnsatisfiedLinkError) {
            Log.w(
                "ImageUtils",
                String.format("Native library not found, native RGB -> YUV conversion may be unavailable.")
            )
        }
    }

    fun getYUVByteSize(width: Int, height: Int): Int {
        // The luminance plane requires 1 byte per pixel.
        val ySize = width * height

        // The UV plane works on 2x2 blocks, so dimensions with odd size must be rounded up.
        // Each 2x2 block takes 2 bytes to encode, one each for U and V.
        val uvSize = ((width + 1) / 2) * ((height + 1) / 2) * 2

        return ySize + uvSize
    }

    fun saveBitmap(bitmap: Bitmap?) {
        saveBitmap(bitmap, "preview.png")
    }

    // This value is 2 ^ 18 - 1, and is used to clamp the RGB values before their ranges
    // are normalized to eight bits.
    const val kMaxChannelValue: Int = 262143

    // Always prefer the native implementation if available.
    private var useNativeConversion = true

    fun convertYUV420SPToARGB8888(
        input: ByteArray,
        width: Int,
        height: Int,
        output: IntArray
    ) {
        if (useNativeConversion) {
            try {
                convertYUV420SPToARGB8888(input, output, width, height, false)
                return
            } catch (e: UnsatisfiedLinkError) {
                Log.w(
                    "ImageUtils",
                    String.format("Native YUV420SP -> RGB implementation not found, falling back to Java implementation")
                )
                useNativeConversion = false
            }
        }

        // Java implementation of YUV420SP to ARGB8888 converting
        val frameSize = width * height
        var j = 0
        var yp = 0
        while (j < height) {
            var uvp = frameSize + (j shr 1) * width
            var u = 0
            var v = 0

            var i = 0
            while (i < width) {
                val y = 0xff and input[yp].toInt()
                if ((i and 1) == 0) {
                    v = 0xff and input[uvp++].toInt()
                    u = 0xff and input[uvp++].toInt()
                }

                output[yp] = YUV2RGB(y, u, v)
                i++
                yp++
            }
            j++
        }
    }

    private fun YUV2RGB(y: Int, u: Int, v: Int): Int {
        // Adjust and check YUV values
        var y = y
        var u = u
        var v = v
        y = if ((y - 16) < 0) 0 else (y - 16)
        u -= 128
        v -= 128

        // This is the floating point equivalent. We do the conversion in integer
        // because some Android devices do not have floating point in hardware.
        // nR = (int)(1.164 * nY + 2.018 * nU);
        // nG = (int)(1.164 * nY - 0.813 * nV - 0.391 * nU);
        // nB = (int)(1.164 * nY + 1.596 * nV);
        val y1192 = 1192 * y
        var r = (y1192 + 1634 * v)
        var g = (y1192 - 833 * v - 400 * u)
        var b = (y1192 + 2066 * u)

        // Clipping RGB values to be inside boundaries [ 0 , kMaxChannelValue ]
        r = if (r > kMaxChannelValue) kMaxChannelValue else (if (r < 0) 0 else r)
        g = if (g > kMaxChannelValue) kMaxChannelValue else (if (g < 0) 0 else g)
        b = if (b > kMaxChannelValue) kMaxChannelValue else (if (b < 0) 0 else b)

        return -0x1000000 or ((r shl 6) and 0xff0000) or ((g shr 2) and 0xff00) or ((b shr 10) and 0xff)
    }

//    fun convertYUV420ToARGB8888(
//        yData: ByteArray,
//        uData: ByteArray,
//        vData: ByteArray,
//        width: Int,
//        height: Int,
//        yRowStride: Int,
//        uvRowStride: Int,
//        uvPixelStride: Int,
//        out: IntArray
//    ) {
//        if (useNativeConversion) {
//            try {
//                convertYUV420ToARGB8888(
//                    yData,
//                    uData,
//                    vData,
//                    out,
//                    width,
//                    height,
//                    yRowStride,
//                    uvRowStride,
//                    uvPixelStride,
//                    false
//                )
//                return
//            } catch (e: UnsatisfiedLinkError) {
//                Log.w(
//                    "ImageUtils",
//                    String.format("Native YUV420 -> RGB implementation not found, falling back to Java implementation")
//                )
//                useNativeConversion = false
//            }
//        }
//
//        var yp = 0
//        for (j in 0..<height) {
//            val pY = yRowStride * j
//            val pUV = uvRowStride * (j shr 1)
//
//            for (i in 0..<width) {
//                val uv_offset = pUV + (i shr 1) * uvPixelStride
//
//                out[yp++] = YUV2RGB(
//                    0xff and yData[pY + i].toInt(),
//                    0xff and uData[uv_offset].toInt(),
//                    0xff and vData[uv_offset].toInt()
//                )
//            }
//        }
//    }

    private external fun convertYUV420SPToARGB8888(
        input: ByteArray?, output: IntArray?, width: Int, height: Int, halfSize: Boolean
    )

//    private external fun convertYUV420ToARGB8888(
//        y: ByteArray?,
//        u: ByteArray?,
//        v: ByteArray?,
//        output: IntArray?,
//        width: Int,
//        height: Int,
//        yRowStride: Int,
//        uvRowStride: Int,
//        uvPixelStride: Int,
//        halfSize: Boolean
//    )

    private external fun convertYUV420SPToRGB565(
        input: ByteArray?, output: ByteArray?, width: Int, height: Int
    )

    private external fun convertARGB8888ToYUV420SP(
        input: IntArray?, output: ByteArray?, width: Int, height: Int
    )

    private external fun convertRGB565ToYUV420SP(
        input: ByteArray?, output: ByteArray?, width: Int, height: Int
    )

    fun getTransformationMatrix(
        srcWidth: Int,
        srcHeight: Int,
        dstWidth: Int,
        dstHeight: Int,
        applyRotation: Int,
        maintainAspectRatio: Boolean
    ): Matrix {
        val matrix = Matrix()

        if (applyRotation != 0) {
            if (applyRotation % 90 != 0) {
                Log.w("ImageUtils", String.format("Rotation of %d %% 90 != 0", applyRotation))
            }

            // Translate so center of image is at origin.
            matrix.postTranslate(-srcWidth / 2.0f, -srcHeight / 2.0f)

            // Rotate around origin.
            matrix.postRotate(applyRotation.toFloat())
        }

        // Account for the already applied rotation, if any, and then determine how
        // much scaling is needed for each axis.
        val transpose = (abs(applyRotation) + 90) % 180 == 0

        val inWidth = if (transpose) srcHeight else srcWidth
        val inHeight = if (transpose) srcWidth else srcHeight

        // Apply scaling if necessary.
        if (inWidth != dstWidth || inHeight != dstHeight) {
            val scaleFactorX = dstWidth / inWidth.toFloat()
            val scaleFactorY = dstHeight / inHeight.toFloat()

            if (maintainAspectRatio) {
                // Scale by minimum factor so that dst is filled completely while
                // maintaining the aspect ratio. Some image may fall off the edge.
                val scaleFactor = max(scaleFactorX, scaleFactorY)
                matrix.postScale(scaleFactor, scaleFactor)
            } else {
                // Scale exactly to fill dst from src.
                matrix.postScale(scaleFactorX, scaleFactorY)
            }
        }

        if (applyRotation != 0) {
            // Translate back from origin centered reference to destination frame.
            matrix.postTranslate(dstWidth / 2.0f, dstHeight / 2.0f)
        }

        return matrix
    }

    @JvmStatic
    fun prewhiten(input: FloatArray, output: FloatBuffer) {
        if (useNativeConversion) {
            try {
                prewhiten(input, input.size, output)
                return
            } catch (e: UnsatisfiedLinkError) {
                Log.w(
                    "ImageUtils",
                    String.format("Native prewhiten implementation not found, falling back to Java implementation")
                )
                useNativeConversion = false
            }
        }

        var sum = 0.0
        for (value in input) {
            sum += value.toDouble()
        }
        val mean = sum / input.size
        sum = 0.0

        for (i in input.indices) {
            input[i] -= mean.toFloat()
            sum += input[i].toDouble().pow(2.0)
        }
        val std = sqrt(sum / input.size)
        val std_adj = max(std, 1.0 / sqrt(input.size.toDouble()))

        output.clear()
        for (value in input) {
            output.put((value / std_adj).toFloat())
        }
        output.rewind()
    }

    private external fun prewhiten(input: FloatArray?, length: Int, output: FloatBuffer?): Float
}