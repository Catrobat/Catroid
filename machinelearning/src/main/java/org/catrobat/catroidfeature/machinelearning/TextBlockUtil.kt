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
package org.catrobat.catroidfeature.machinelearning

import android.graphics.Point
import android.graphics.Rect
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.vision.text.Text
import com.huawei.hms.mlsdk.langdetect.MLLangDetectorFactory
import com.huawei.hms.mlsdk.langdetect.local.MLLocalLangDetector
import com.huawei.hms.mlsdk.langdetect.local.MLLocalLangDetectorSetting
import com.huawei.hms.mlsdk.text.MLText
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.ScreenValues
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroidfeature.machinelearning.VisualDetectionHandler.coordinatesFromRelativePosition
import kotlin.math.roundToInt
import org.catrobat.catroid.utils.TextBlockUtil as TextBlockUtilInterface

object TextBlockUtil : TextBlockUtilInterface {
    private const val TRUSTED_THRESHOLD = 0.01f
    private var textBlocks = mutableListOf<String>()
    private var textBlockBoundingBoxes = mutableListOf<Rect>()
    private var textBlockLanguages = mutableMapOf<Int, String>()
    private var imageWidth = 0
    private var imageHeight = 0
    private const val MAX_TEXT_SIZE = 100
    private var languageIdentifierGoogle = LanguageIdentification.getClient()

    fun setTextBlocksGoogle(text: List<Text.TextBlock>, width: Int, height: Int) {
        imageWidth = width
        imageHeight = height

        textBlockLanguages.clear()
        textBlockBoundingBoxes.clear()

        text.forEachIndexed { index, textBlock ->
            textBlock.text.let { textBlocks.add(index, it) }
            textBlock.boundingBox?.let { textBlockBoundingBoxes.add(index, it) }
            languageIdentifierGoogle.identifyLanguage(textBlock.text).addOnSuccessListener { languageCode ->
                textBlockLanguages[index] = languageCode
            }
        }
    }

    fun setTextBlocksHuawei(text: List<MLText.Block>, width: Int, height: Int) {
        imageWidth = width
        imageHeight = height

        textBlockLanguages.clear()
        textBlockBoundingBoxes.clear()

        text.forEachIndexed { index, textBlock ->
            textBlock.stringValue?.let { textBlocks.add(index, it) }
            textBlock.border?.let { textBlockBoundingBoxes.add(index, it) }
            val firstBestDetectTask = MLLangDetectorFactory.getInstance()
                .getLocalLangDetector(MLLocalLangDetectorSetting.Factory()
                                          .setTrustedThreshold(TRUSTED_THRESHOLD)
                                          .create())
                .firstBestDetect(textBlock.stringValue)
            firstBestDetectTask.addOnSuccessListener { languageCode ->
                textBlockLanguages[index] = languageCode
            }
        }
    }

    override fun getTextBlock(index: Int): String = textBlocks.getOrNull(index - 1) ?: "0"

    override fun getTextBlockLanguage(index: Int): String = textBlockLanguages[index - 1] ?: "0"

    override fun getCenterCoordinates(index: Int): Point {
        val textBlockBounds = textBlockBoundingBoxes.getOrNull(index - 1) ?: return Point(0, 0)
        val isCameraFacingFront = StageActivity.getActiveCameraManager()?.isCameraFacingFront ?: return Point(0, 0)
        val aspectRatio = imageWidth.toDouble() / imageHeight

        return if (ProjectManager.getInstance().isCurrentProjectLandscapeMode) {
            var relativeX = textBlockBounds.exactCenterX().toDouble() / imageWidth
            relativeX = if (isCameraFacingFront) 1 - relativeX else relativeX
            coordinatesFromRelativePosition(
                relativeX,
                ScreenValues.SCREEN_WIDTH.toDouble(),
                1 - textBlockBounds.exactCenterY().toDouble() / imageHeight,
                ScreenValues.SCREEN_WIDTH.toFloat() / aspectRatio
            )
        } else {
            var relativeX = textBlockBounds.exactCenterX().toDouble() / imageHeight
            relativeX = if (isCameraFacingFront) 1 - relativeX else relativeX
            coordinatesFromRelativePosition(
                relativeX,
                ScreenValues.SCREEN_HEIGHT / aspectRatio,
                1 - textBlockBounds.exactCenterY().toDouble() / imageWidth,
                ScreenValues.SCREEN_HEIGHT.toDouble()
            )
        }
    }

    override fun getSize(index: Int): Double {
        val textBlockBounds = textBlockBoundingBoxes.getOrNull(index - 1) ?: return 0.0
        var relativeTextBlockSize = textBlockBounds.width().toFloat() / imageWidth
        if (relativeTextBlockSize > 1f) {
            relativeTextBlockSize = 1f
        }
        return (MAX_TEXT_SIZE * relativeTextBlockSize).roundToInt().toDouble()
    }
}
