/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
import com.badlogic.gdx.math.Polygon
import kotlin.math.absoluteValue

private const val EPSILON = 8 / 256F
private val clearColor = Color(1f, 1f, 1f, 0f)

private fun Int.isOdd(): Boolean = this % 2 == 1

private fun Color.equalsColor(color: Color): Boolean {
    return this.r.absDiff(color.r) < EPSILON &&
        this.g.absDiff(color.g) < EPSILON &&
        this.b.absDiff(color.b) < EPSILON &&
        this != clearColor
}

private fun Float.absDiff(f: Float): Float = (this - f).absoluteValue

fun Array<Polygon>.toBoundingRectangle() = BoundingRectangle(this.flatMap { polygon -> polygon.vertices.asIterable() })

class BoundingRectangle(vertices: List<Float>) {
    val top = vertices.getYVertices().minOrNull()?.toInt() ?: 0
    val bottom = vertices.getYVertices().maxOrNull()?.toInt() ?: 0
    val left = vertices.getXVertices().minOrNull()?.toInt() ?: 0
    val right = vertices.getXVertices().maxOrNull()?.toInt() ?: 0
    val width = right - left
    val height = bottom - top

    private fun List<Float>.getYVertices() = this.filterIndexed { i, _ -> i.isOdd() }
    private fun List<Float>.getXVertices() = this.filterIndexed { i, _ -> !i.isOdd() }
}

abstract class ConditionMatcher {
    var stagePixmap: Pixmap? = null
    var spritePixmap: Pixmap? = null

    abstract fun matches(x: Int, y: Int): Boolean
}

class TouchesColorMatcher(color: String) : ConditionMatcher() {
    private val color = Color.valueOf(color)
    override fun matches(x: Int, y: Int): Boolean {
        val stagePixel = stagePixmap?.getPixel(x, y) ?: return false
        val spritePixel = spritePixmap?.getPixel(x, y) ?: return false

        return Color(stagePixel).equalsColor(color) && Color(spritePixel) != clearColor
    }
}

class ColorTouchesColorMatcher(spriteColor: String, stageColor: String) : ConditionMatcher() {
    private val spriteColor = Color.valueOf(spriteColor)
    private val stageColor = Color.valueOf(stageColor)
    override fun matches(x: Int, y: Int): Boolean {
        val stagePixel = stagePixmap?.getPixel(x, y) ?: return false
        val spritePixel = spritePixmap?.getPixel(x, y) ?: return false

        return Color(stagePixel).equalsColor(stageColor) && Color(spritePixel).equalsColor(spriteColor)
    }
}

class ConditionMatcherRunner(val matcher: ConditionMatcher) {
    fun match(width: Int, height: Int): Boolean {
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (matcher.matches(x, y)) {
                    return true
                }
            }
        }
        return false
    }
}
