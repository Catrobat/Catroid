/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2024 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.stage

import android.graphics.Bitmap
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.utils.ScreenUtils
import java.io.ByteArrayOutputStream

object ScreenshotUtils {

    fun convertPixmapToBitmap(pixmap: Pixmap): Bitmap {
        var y: Int
        var yl: Int
        val bmp = Bitmap.createBitmap(pixmap.width, pixmap.height, Bitmap.Config.ARGB_8888)
        var x = 0
        val xl: Int = pixmap.width
        while (x < xl) {
            y = 0
            yl = pixmap.height
            while (y < yl) {
                val color = pixmap.getPixel(x, y)
                // RGBA => ARGB
                val rgb = color shr 8
                val a = (color and 0x000000ff) shl 24
                val argb = a or rgb
                bmp.setPixel(x, y, argb)
                y++
            }
            x++
        }
        return bmp
    }

    fun convertBitmapToPixmap(bitmap: Bitmap): Pixmap {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        return Pixmap(byteArray, 0, byteArray.size)
    }

    fun getFrameBufferPixmapFlip(x: Int, y: Int, w: Int, h: Int, flipY: Boolean): Pixmap {
        val pixmap = ScreenUtils.getFrameBufferPixmap(x, y, w, h)
        val pixels = pixmap.pixels
        val numBytes: Int = w * h * 4
        val lines = ByteArray(numBytes)
        if (flipY) {
            val numBytesPerLine: Int = w * 4
            for (i in 0 until h) {
                pixels.position((h - i - 1) * numBytesPerLine)
                pixels.get(lines, i * numBytesPerLine, numBytesPerLine)
            }
            pixels.clear()
            pixels.put(lines)
        } else {
            pixels.clear()
            pixels.get(lines)
        }
        return pixmap
    }

    fun merge2Pixmaps(mainPixmap: Pixmap, overlayedPixmap: Pixmap) {
        mainPixmap.filter = Pixmap.Filter.BiLinear
        val mainPixmapAR = mainPixmap.width.toFloat() / mainPixmap.height
        val overlayedPixmapAR = overlayedPixmap.width.toFloat() / overlayedPixmap.height
        if (overlayedPixmapAR < mainPixmapAR) {
            val overlayNewWidth =
                ((mainPixmap.height.toFloat() / overlayedPixmap.height) * overlayedPixmap.width).toInt()
            val overlayStartX = (mainPixmap.width - overlayNewWidth) / 2
            mainPixmap.drawPixmap(
                overlayedPixmap,
                0,
                0,
                overlayedPixmap.width,
                overlayedPixmap.height,
                overlayStartX,
                0,
                overlayNewWidth,
                mainPixmap.height
            )
        } else {
            val overlayNewHeight =
                ((mainPixmap.width.toFloat() / overlayedPixmap.width) * overlayedPixmap.height).toInt()
            val overlayStartY = (mainPixmap.height - overlayNewHeight) / 2
            mainPixmap.drawPixmap(
                overlayedPixmap,
                0,
                0,
                overlayedPixmap.width,
                overlayedPixmap.height,
                0,
                overlayStartY,
                mainPixmap.width,
                overlayNewHeight
            )
        }
    }
}