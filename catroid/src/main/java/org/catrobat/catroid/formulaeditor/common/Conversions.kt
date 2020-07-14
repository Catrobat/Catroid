/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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
package org.catrobat.catroid.formulaeditor.common

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import androidx.annotation.ColorInt
import com.badlogic.gdx.graphics.Pixmap
import java.nio.ByteBuffer
import kotlin.experimental.and

object Conversions {
    const val TRUE = 1.0
    const val FALSE = 0.0

    private fun tryParseDouble(argument: String): Double? {
        return try {
            argument.toDouble()
        } catch (numberFormatException: NumberFormatException) {
            null
        }
    }

    @Suppress("MagicNumber")
    @ColorInt
    @JvmStatic
    @JvmOverloads
    fun tryParseColor(string: String?, defaultValue: Int = Color.BLACK): Int {
        return if (string != null && string.length == 7 && string.matches("^#[0-9a-fA-F]+$".toRegex())) {
            Color.parseColor(string)
        } else {
            defaultValue
        }
    }

    @JvmStatic
    fun convertArgumentToDouble(argument: Any?): Double? {
        return argument?.let {
            when (argument) {
                is String -> tryParseDouble(argument)
                else -> argument as Double?
            }
        }
    }

    @JvmStatic
    fun booleanToDouble(value: Boolean) = if (value) TRUE else FALSE

    @JvmStatic
    fun matchesColor(pixels: ByteBuffer, color: Int): Boolean {
        val bytes = ByteArray(pixels.remaining())
        pixels[bytes]
        var i = 0
        while (i < bytes.size) {
            val pixelColor: Int =
                bytes[i].toInt() and 0xFF shl 16 or (bytes[i + 1].toInt() and 0xFF shl 8) or
                    (bytes[i + 2].toInt() and 0xFF)
            if (compareColors(color, pixelColor)) {
                return true
            }
            i += 4
        }
        return false
    }

    @JvmStatic
    fun compareColors(colorA: Int, colorB: Int): Boolean {
        return Color.red(colorA) and 248 == Color.red(colorB) and 248 && Color.green(colorA) and
            248 == Color.green(colorB) and 248 && Color.blue(colorA) and 240 == Color.blue(colorB) and 240
    }

    @JvmStatic
    fun convertToBitmap(pixmap: Pixmap): Bitmap? {
        val buf = pixmap.pixels
        val byteColors = ByteArray(buf.remaining())
        buf[byteColors]
        buf.rewind()
        return convertToBitmap(byteColors, pixmap.width, pixmap.height)
    }

    @JvmStatic
    fun convertToBitmap(byteColors: ByteArray, pixmapWidth: Int, pixmapHeight: Int): Bitmap? {
        val colors = IntArray(byteColors.size / 4)
        for (i in colors.indices) {
            colors[i] = Color.argb(
                0xFF, byteColors[i * 4].toInt() and 0xFF,
                byteColors[i * 4 + 1].toInt() and 0xFF, byteColors[i * 4 + 2].toInt() and 0xFF
            )
        }
        return Bitmap.createBitmap(
            colors, 0, pixmapWidth,
            pixmapWidth, pixmapHeight, Bitmap.Config.ARGB_8888
        )
    }

    @JvmStatic
    fun flipBitmap(bitmap: Bitmap): Bitmap? {
        val m = Matrix()
        m.preScale(1f, -1f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, m, false)
    }
}
