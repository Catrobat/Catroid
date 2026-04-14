/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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

package org.catrobat.catroid.embroidery

import org.catrobat.catroid.ProjectManager
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.Locale
import kotlin.math.abs

private const val PROJECT_LABEL_MAX_LENGTH = 15

class DSTHeader : EmbroideryHeader {
    private var minX = 0
    private var maxX = 0
    private var minY = 0
    private var maxY = 0
    private var firstX = 0
    private var firstY = 0
    private var lastX = 0
    private var lastY = 0
    private var colorChangeCount = 1
    private var pointAmount = 0

    override fun initialize(currentX: Float, currentY: Float) {
        val x = DSTFileConstants.toEmbroideryUnit(currentX)
        val y = DSTFileConstants.toEmbroideryUnit(currentY)
        minX = x
        maxX = x
        minY = y
        maxY = y
        firstX = x
        firstY = y
        lastX = x
        lastY = y
        colorChangeCount = 1
        pointAmount = 1
    }

    override fun update(currentX: Float, currentY: Float) {
        val x = DSTFileConstants.toEmbroideryUnit(currentX)
        val y = DSTFileConstants.toEmbroideryUnit(currentY)
        minX = minOf(minX, x)
        maxX = maxOf(maxX, x)
        minY = minOf(minY, y)
        maxY = maxOf(maxY, y)
        lastX = x
        lastY = y
        pointAmount++
    }

    override fun addColorChange() {
        colorChangeCount++
    }

    @Throws(IOException::class)
    override fun appendToStream(fileStream: FileOutputStream) {
        val label = ProjectManager.getInstance()?.currentProject?.name?.take(PROJECT_LABEL_MAX_LENGTH).orEmpty()
        // DST stores +X/-X/+Y/-Y extents as magnitudes in fixed-width fields.
        val positiveXExtent = maxX.coerceAtLeast(0)
        val negativeXExtent = abs(minX.coerceAtMost(0))
        val positiveYExtent = maxY.coerceAtLeast(0)
        val negativeYExtent = abs(minY.coerceAtMost(0))
        val deltaX = lastX - firstX
        val deltaY = lastY - firstY

        val header = buildString {
            append(String.format(DSTFileConstants.DST_HEADER_LABEL, label))
            append(
                String.format(
                    Locale.getDefault(),
                    DSTFileConstants.DST_HEADER,
                    pointAmount,
                    colorChangeCount,
                    positiveXExtent,
                    negativeXExtent,
                    positiveYExtent,
                    negativeYExtent,
                    deltaX,
                    deltaY,
                    0,
                    0,
                    "*****"
                ).replace(' ', '\u0000')
            )
            append(DSTFileConstants.HEADER_FILL)
        }

        fileStream.write(header.toByteArray(StandardCharsets.US_ASCII))
    }

    override fun reset() {
        colorChangeCount = 1
    }
}
