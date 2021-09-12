/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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
package org.catrobat.catroid.utils

import android.graphics.Point
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.vision.text.Text
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants.COORDINATE_TRANSFORMATION_OFFSET
import org.catrobat.catroid.common.ScreenValues.SCREEN_HEIGHT
import org.catrobat.catroid.common.ScreenValues.SCREEN_WIDTH
import org.catrobat.catroid.stage.StageActivity
import kotlin.math.roundToInt

object TextBlockUtil {
    private var textBlocks: List<Text.TextBlock>? = null
    private var textBlockLanguages = mutableListOf<String>()
    private var imageWidth = 0
    private var imageHeight = 0
    private const val MAX_TEXT_SIZE = 100
    private var identifier = LanguageIdentification.getClient()

    fun setTextBlocks(text: List<Text.TextBlock>, width: Int, height: Int) {
        textBlocks = text
        imageWidth = width
        imageHeight = height

        textBlockLanguages.clear()

        textBlocks?.forEachIndexed { index, textBlock ->
            identifier.identifyLanguage(textBlock.text).addOnSuccessListener { languageCode ->
                textBlockLanguages.add(index, languageCode)
            }
        }
    }

    fun getTextBlock(index: Int): String = textBlocks?.getOrNull(index - 1)?.text ?: "0"

    fun getTextBlockLanguage(index: Int): String = textBlockLanguages.getOrNull(index - 1) ?: "0"

    // TODO: check - use resolution here?
    fun getCenterCoordinates(index: Int): Point {
        val textBlockBounds = textBlocks?.getOrNull(index - 1)?.boundingBox ?: return Point(0, 0)
        val isCameraFacingFront = StageActivity.getActiveCameraManager()?.isCameraFacingFront ?: return Point(0, 0)
        val aspectRatio = imageWidth.toFloat() / imageHeight

        return if (ProjectManager.getInstance().isCurrentProjectLandscapeMode) {
            var relativeX = textBlockBounds.exactCenterX() / imageWidth
            relativeX = if (isCameraFacingFront) 1 - relativeX else relativeX
            coordinatesFromRelativePosition(
                relativeX,
                SCREEN_WIDTH.toFloat(),
                1 - textBlockBounds.exactCenterY() / imageHeight,
                SCREEN_WIDTH.toFloat() / aspectRatio
            )
        } else {
            var relativeX = textBlockBounds.exactCenterX() / imageHeight
            relativeX = if (isCameraFacingFront) 1 - relativeX else relativeX
            coordinatesFromRelativePosition(
                relativeX,
                SCREEN_HEIGHT / aspectRatio,
                1 - textBlockBounds.exactCenterY() / imageWidth,
                SCREEN_HEIGHT.toFloat()
            )
        }
    }

    private fun coordinatesFromRelativePosition(
        relativeX: Float,
        width: Float,
        relativeY: Float,
        height: Float
    ): Point {
        return Point(
            (width * (relativeX - COORDINATE_TRANSFORMATION_OFFSET)).roundToInt(),
            (height * (relativeY - COORDINATE_TRANSFORMATION_OFFSET)).roundToInt()
        )
    }

    fun getSize(index: Int): Double {
        val textBlockBounds = textBlocks?.getOrNull(index - 1)?.boundingBox ?: return 0.0
        var relativeTextBlockSize = textBlockBounds.width().toFloat() / imageWidth
        if (relativeTextBlockSize > 1f) {
            relativeTextBlockSize = 1f
        }
        return (MAX_TEXT_SIZE * relativeTextBlockSize).roundToInt().toDouble()
    }
}
