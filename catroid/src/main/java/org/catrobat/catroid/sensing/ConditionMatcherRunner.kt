/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

package org.catrobat.catroid.sensing

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Polygon
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.catrobat.catroid.BuildConfig
import kotlin.math.absoluteValue

private const val EPSILON = 8 / 256F

private fun Int.isOdd(): Boolean = this % 2 == 1

private fun Color.equalsColor(color: Color): Boolean {
    return this.r.absDiff(color.r) < EPSILON &&
        this.g.absDiff(color.g) < EPSILON &&
        this.b.absDiff(color.b) < EPSILON
}

@Suppress("MagicNumber")
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

private fun Float.absDiff(f: Float): Float = (this - f).absoluteValue

fun Array<Polygon>.toBoundingRectangle() = BoundingRectangle(this.flatMap { polygon -> polygon.vertices.asIterable() })

class BoundingRectangle(vertices: List<Float>) {
    val top = vertices.getYVertices().min()?.toInt() ?: 0
    val bottom = vertices.getYVertices().max()?.toInt() ?: 0
    val left = vertices.getXVertices().min()?.toInt() ?: 0
    val right = vertices.getXVertices().max()?.toInt() ?: 0

    private fun List<Float>.getYVertices() = this.filterIndexed { i, _ -> i.isOdd() }
    private fun List<Float>.getXVertices() = this.filterIndexed { i, _ -> !i.isOdd() }

    private fun Pixmap.validXIndex(x: Int) = x >= 0 && x <= this.width
    private fun Pixmap.validYIndex(y: Int) = y >= 0 && y <= this.height

    fun hasValidBoundaries(pixmap: Pixmap?) = pixmap?.run {
        validXIndex(left) && validXIndex(right) && validYIndex(top) && validYIndex(bottom)
    } ?: false
}

interface ConditionMatcher {
    fun matches(x: Int, y: Int): Boolean
    fun validRange(boundingRectangle: BoundingRectangle): Boolean
    fun setStagePixmap(pixmap: Pixmap)
}

class TouchesColorMatcher(color: String, private val polygons: Array<Polygon>) : ConditionMatcher {
    private var pixmap: Pixmap? = null
    private val color = Color.valueOf(color)
    override fun matches(x: Int, y: Int): Boolean {
        val pixel = pixmap?.getPixel(x, y) ?: return false
        val pixmapColor = Color(pixel)

        return pixmapColor.equalsColor(color) && isPointInSprite(polygons, x, y)
    }

    override fun validRange(boundingRectangle: BoundingRectangle) = boundingRectangle.hasValidBoundaries(pixmap)
    override fun setStagePixmap(pixmap: Pixmap) {
        this.pixmap = pixmap
    }
}

class ColorTouchesColorMatcher(
    private val spritePixmap: Pixmap,
    spriteColor: String,
    stageColor: String
) : ConditionMatcher {
    private var stagePixmap: Pixmap? = null
    private val spriteColor = Color.valueOf(spriteColor)
    private val stageColor = Color.valueOf(stageColor)
    override fun matches(x: Int, y: Int): Boolean {
        val stagePixel = Color(stagePixmap?.getPixel(x, y) ?: return false)
        val spritePixel = Color(spritePixmap.getPixel(x, spritePixmap.height - y))
        if (stagePixel.a == 0f || spritePixel.a == 0f) {
            return false
        }
        return stagePixel.equalsColor(stageColor) && spritePixel.equalsColor(spriteColor)
    }

    override fun validRange(boundingRectangle: BoundingRectangle) = boundingRectangle.hasValidBoundaries(stagePixmap)
    override fun setStagePixmap(pixmap: Pixmap) {
        stagePixmap = pixmap
    }
}

class ConditionMatcherRunner(val matcher: ConditionMatcher) {
    fun matchAsync(boundingRectangle: BoundingRectangle): Boolean = runBlocking {
        if (BuildConfig.DEBUG && !matcher.validRange(boundingRectangle)) {
            error("Wrong projection matrix or rotation")
        }
        val matchesColorCoroutine = GlobalScope.async {
            matchInBoundingRectangle(boundingRectangle)
        }
        return@runBlocking matchesColorCoroutine.await()
    }

    private fun matchInBoundingRectangle(boundingRectangle: BoundingRectangle): Boolean {
        for (x in boundingRectangle.left until boundingRectangle.right) {
            for (y in boundingRectangle.top until boundingRectangle.bottom) {
                if (matcher.matches(x, y)) {
                    return true
                }
            }
        }
        return false
    }
}
