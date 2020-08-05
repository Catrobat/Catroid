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

import android.graphics.Color
import androidx.annotation.ColorInt
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Polygon
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.catrobat.catroid.BuildConfig
import kotlin.math.absoluteValue
import com.badlogic.gdx.graphics.Color as LibGDXColor

@Suppress("MagicNumber")
object Conversions {
    const val TRUE = 1.0
    const val FALSE = 0.0
    private const val EPSILON = 8 / 256F

    private fun tryParseDouble(argument: String): Double? {
        return try {
            argument.toDouble()
        } catch (numberFormatException: NumberFormatException) {
            null
        }
    }

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
    fun matchesColor(pixmap: Pixmap, collisionPolygons: Array<Polygon>, color: String): Boolean = runBlocking {
        val vertices = collisionPolygons.flatMap { polygon -> polygon.vertices.asIterable() }
        val boundingRectangle = BoundingRectangle(vertices)

        if (BuildConfig.DEBUG && !boundingRectangle.hasValidBoundaries(pixmap)) {
            error("Wrong projection matrix or rotation")
        }
        val matchesColorCoroutine = GlobalScope.async {
            matchColorInBoundingRectangle(boundingRectangle, pixmap, color, collisionPolygons)
        }
        return@runBlocking matchesColorCoroutine.await()
    }

    private fun matchColorInBoundingRectangle(boundingRectangle: BoundingRectangle, pixmap: Pixmap, color: String, polygons: Array<Polygon>): Boolean {
        for (x in boundingRectangle.left until boundingRectangle.right) {
            for (y in boundingRectangle.top until boundingRectangle.bottom) {
                val pixmapColor = LibGDXColor(pixmap.getPixel(x, y))

                if (!pixmapColor.equalsColor(LibGDXColor.valueOf(color))) {
                    continue
                }

                if (isPointInSprite(polygons, x, y)) {
                    return true
                }
            }
        }
        return false
    }

    private fun isPointInSprite(polygons: Array<Polygon>, x: Int, y: Int): Boolean {
        var inPolygonCounter = 0
        for (polygon in polygons) {
            val vertices = polygon.vertices
            if (Intersector.isPointInPolygon(vertices, 0, vertices.size, x + 0.5F, y + 0.5F)) {
                inPolygonCounter++
            }
        }
        return inPolygonCounter.isOdd()
    }

    private fun LibGDXColor.equalsColor(color: LibGDXColor): Boolean {
        return this.r.absDiff(color.r) < EPSILON &&
            this.g.absDiff(color.g) < EPSILON &&
            this.b.absDiff(color.b) < EPSILON
    }

    private fun Float.absDiff(f: Float): Float = (this - f).absoluteValue

    private fun Int.isOdd(): Boolean = this % 2 == 1

    private class BoundingRectangle(vertices: List<Float>) {
        val top = vertices.getYVertices().min()?.toInt() ?: 0
        val bottom = vertices.getYVertices().max()?.toInt() ?: 0
        val left = vertices.getXVertices().min()?.toInt() ?: 0
        val right = vertices.getXVertices().max()?.toInt() ?: 0

        private fun List<Float>.getYVertices() = this.filterIndexed { i, _ -> i.isOdd() }
        private fun List<Float>.getXVertices() = this.filterIndexed { i, _ -> !i.isOdd() }

        private fun Pixmap.validXIndex(x: Int) = x >= 0 && x <= this.width
        private fun Pixmap.validYIndex(y: Int) = y >= 0 && y <= this.height

        fun hasValidBoundaries(pixmap: Pixmap) = pixmap.run {
            validXIndex(left) && validXIndex(right) && validYIndex(top) && validYIndex(bottom)
        }
    }
}
