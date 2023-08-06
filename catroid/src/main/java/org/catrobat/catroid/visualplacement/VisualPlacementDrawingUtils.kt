/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.visualplacement

import android.graphics.Bitmap
import android.graphics.Matrix
import org.catrobat.catroid.common.defaultprojectcreators.BitmapWithRotationInfo
import org.catrobat.catroid.content.Look
import org.catrobat.catroid.visualplacement.model.Size

class VisualPlacementDrawingUtils {

    fun rotateAndScaleBitmap(sprite: BitmapWithRotationInfo, layoutRatio: Size): Bitmap {
        val rotationMatrix = createMatrix(
            sprite.bitmap.getSize(), sprite.rotation, sprite
                .rotationMode
        )
        val rotatedBitmap = Bitmap.createBitmap(
            sprite.bitmap, 0, 0, sprite.bitmap.width, sprite.bitmap.height, rotationMatrix, true
        )
        return Bitmap.createScaledBitmap(
            rotatedBitmap,
            (rotatedBitmap.width * layoutRatio.width).toInt(),
            (rotatedBitmap.height * layoutRatio.height).toInt(),
            true
        )
    }

    @Suppress("MagicNumber")
    private fun createMatrix(spriteSize: Size, rotation: Int, rotationMode: Int): Matrix {
        val matrix = Matrix()

        when (rotationMode) {
            Look.ROTATION_STYLE_NONE -> matrix.postRotate(0f)
            Look.ROTATION_STYLE_ALL_AROUND -> if (rotation != 90) matrix.postRotate(
                rotation - Look.DEGREE_UI_OFFSET
            )

            Look.ROTATION_STYLE_LEFT_RIGHT_ONLY -> if (rotation < 0) matrix.postScale(
                -1f,
                1f,
                spriteSize.width / 2,
                spriteSize.height / 2
            )
        }

        return matrix
    }

    private fun Bitmap.getSize() = Size(
        width.toFloat(),
        height.toFloat(),
    )
}
