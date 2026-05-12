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

package org.catrobat.catroid.stage

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Actor
import org.catrobat.catroid.common.BrickValues
import org.catrobat.catroid.embroidery.EmbroideryPatternManager
import org.catrobat.catroid.embroidery.StitchPoint
import androidx.annotation.VisibleForTesting

open class EmbroideryActor(
    screenRatio: Float,
    private val embroideryPatternManager: EmbroideryPatternManager,
    private val shapeRenderer: ShapeRenderer
) : Actor() {
    private val stitchSize = BrickValues.STITCH_SIZE * screenRatio

    override fun draw(batch: Batch, parentAlpha: Float) {
        val stitchPoints = embroideryPatternManager.embroideryPatternList
        if (stitchPoints.size < 2) {
            return
        }

        val iterator = stitchPoints.listIterator()
        batch.end()
        shapeRenderer.projectionMatrix = batch.projectionMatrix
        shapeRenderer.transformMatrix = batch.transformMatrix
        shapeRenderer.updateMatrices()

        var stitchPoint = iterator.next()
        shapeRenderer.color = stitchPoint.color
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        drawCircle(stitchPoint)
        var colorChange = false

        while (iterator.hasNext()) {
            val previousStitchPoint = stitchPoint
            stitchPoint = iterator.next()

            colorChange = colorChange || stitchPoint.isColorChangePoint

            if (!colorChange) {
                shapeRenderer.color = previousStitchPoint.color
                drawLine(previousStitchPoint, stitchPoint)
            }
            if (stitchPoint.isConnectingPoint) {
                shapeRenderer.color = stitchPoint.color
                drawCircle(stitchPoint)
                colorChange = false
            }
        }

        shapeRenderer.end()
        batch.begin()
    }

    @VisibleForTesting
    open fun drawCircle(stitchPoint: StitchPoint) {
        shapeRenderer.circle(stitchPoint.x, stitchPoint.y, stitchSize)
    }

    @VisibleForTesting
    open fun drawLine(stitchPoint1: StitchPoint, stitchPoint2: StitchPoint) {
        shapeRenderer.rectLine(
            stitchPoint1.x,
            stitchPoint1.y,
            stitchPoint2.x,
            stitchPoint2.y,
            stitchSize
        )
    }

    @VisibleForTesting
    fun getStitchSize(): Float = stitchSize
}
