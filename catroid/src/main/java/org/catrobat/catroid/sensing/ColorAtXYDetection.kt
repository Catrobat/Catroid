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

import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Matrix4
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Look
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.common.Conversions.convertArgumentToDouble
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.StageListener
import org.koin.java.KoinJavaComponent.inject
import kotlin.math.roundToInt

private const val COLOR_HEX_PREFIX = "#"
private const val RGBA_START_INDEX = 0
private const val RGBA_END_INDEX = 6
private const val ARGB_START_INDEX = 2
private const val ARGB_END_INDEX = 8
private const val HEX_COLOR_BLACK = "#000000"
private const val TAG = "COLORATXY"

class ColorAtXYDetection(
    scope: Scope,
    stageListener: StageListener?
) : ColorDetection(scope, stageListener) {
    private var xPosition: Int = 0
    private var yPosition: Int = 0

    private val projectManager: ProjectManager by inject(ProjectManager::class.java)

    @Suppress("TooGenericExceptionCaught")
    fun tryInterpretFunctionColorAtXY(x: Any?, y: Any?): String {
        setBufferParameters()

        val xPositionUnchecked = convertArgumentToDouble(x) ?: return "NaN"
        val yPositionUnchecked = convertArgumentToDouble(y) ?: return "NaN"

        if (xPositionUnchecked.isNaN() || yPositionUnchecked.isNaN() || stageListener == null) {
            return "NaN"
        }

        xPosition = xPositionUnchecked.roundToInt()
        yPosition = yPositionUnchecked.roundToInt()

        // the camera feature cannot be tested automatically yet
        // in the future, tests can be added with the ongoing sensor-robot-test project
        if (
            StageActivity.getActiveCameraManager() != null &&
            StageActivity.getActiveCameraManager().isCameraActive
        ) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                return Double.NaN.toString()
            }
            if (isXPositionOutsideOfScreen() || isYPositionOutsideOfScreen()) {
                return rgbaColorToRGBHexString(Color.WHITE)
            }
            adjustBorderCoordinatesToPreventInvalidBitmapAccessForUsability()
            callPixelCopyWithSurfaceView(receiveBitmapFromPixelCopy)
            return getHexColorStringFromBitmapAtPosition(cameraBitmap, xPosition, yPosition)
        }

        return try {
            getHexColorStringFromStagePixmap()
        } catch (_: Exception) {
            Double.NaN.toString()
        }
    }

    private fun adjustBorderCoordinatesToPreventInvalidBitmapAccessForUsability() {
        if (isXCoordinateOnRightBorder()) {
            xPosition--
        }
        if (isYCoordinateOnTopBorderLandscape()) {
            yPosition--
        } else if (isYCoordinateOnBottomBorderPortrait()) {
            yPosition++
        }
    }

    private fun isXCoordinateOnRightBorder() = xPosition == virtualWidth / 2

    private fun isYCoordinateOnTopBorderLandscape() =
        projectManager.isCurrentProjectLandscapeMode &&
            yPosition == virtualHeight / 2

    private fun isYCoordinateOnBottomBorderPortrait() =
        !projectManager.isCurrentProjectLandscapeMode &&
            yPosition == -virtualHeight / 2

    private fun getHexColorStringFromStagePixmap(): String {
        val lookList: MutableList<Look> = getLooksOfRelevantSprites() ?: return HEX_COLOR_BLACK

        val batch = SpriteBatch()
        val projectionMatrix = scope.project?.let { createProjectionMatrix(it) }
        val stagePixmap = projectionMatrix?.let { createPicture(lookList, it, batch) }
        val color = stagePixmap?.let { Color(stagePixmap.getPixel(0, 0)) } ?: return HEX_COLOR_BLACK
        return tryRgbaColorToRGBHexString(color, batch, stagePixmap)
    }

    private fun tryRgbaColorToRGBHexString(color: Color, batch: SpriteBatch, stagePixmap: Pixmap):
        String {
        return try {
            rgbaColorToRGBHexString(color)
        } catch (e: StringIndexOutOfBoundsException) {
            Log.e(TAG, "String index is out of bounds when converting rgba color to hex string ")
            return HEX_COLOR_BLACK
        } finally {
            stagePixmap.dispose()
            batch.dispose()
        }
    }

    private fun getHexColorStringFromBitmapAtPosition(
        bitmap: Bitmap?,
        xPosition: Int,
        yPosition: Int
    ): String {
        bitmap ?: return Double.NaN.toString()

        val surfaceViewScaleX = StageActivity.getActiveCameraManager().previewView.surfaceView
            .scaleX
        val surfaceViewScaleY = StageActivity.getActiveCameraManager().previewView.surfaceView
            .scaleY

        val bitmapXCoordinateLandscape = convertStageToBitmapCoordinate(
            yPosition,
            surfaceViewScaleY,
            bitmap.width.toFloat() / virtualHeight.toFloat(),
            bitmap.width / 2
        )

        val bitmapYCoordinateLandscape = convertStageToBitmapCoordinate(
            xPosition,
            surfaceViewScaleX,
            bitmap.height.toFloat() / virtualWidth.toFloat(),
            bitmap.height / 2
        )

        val bitmapXCoordinatePortrait = convertStageToBitmapCoordinate(
            xPosition,
            surfaceViewScaleX,
            bitmap.width.toFloat() / virtualWidth.toFloat(),
            bitmap.width / 2
        )

        val bitmapYCoordinatePortrait = convertStageToBitmapCoordinate(
            -yPosition,
            surfaceViewScaleY,
            bitmap.height.toFloat() / virtualHeight.toFloat(),
            bitmap.height / 2
        )

        val bitmapPixel = if (projectManager.isCurrentProjectLandscapeMode) {
            bitmap.getPixel(bitmapXCoordinateLandscape, bitmapYCoordinateLandscape)
        } else {
            bitmap.getPixel(bitmapXCoordinatePortrait, bitmapYCoordinatePortrait)
        }
        return argbColorToRGBHexString(Color(bitmapPixel))
    }

    private fun rgbaColorToRGBHexString(color: Color): String =
        COLOR_HEX_PREFIX + color.toString().substring(RGBA_START_INDEX, RGBA_END_INDEX)

    private fun argbColorToRGBHexString(color: Color): String =
        COLOR_HEX_PREFIX + color.toString().substring(ARGB_START_INDEX, ARGB_END_INDEX)

    private fun convertStageToBitmapCoordinate(
        position: Int,
        surfaceViewScale: Float,
        bitmapToScreenRatio: Float,
        centerBitmapOffset: Int
    ): Int {
        val scaledPosition = position.toFloat() / surfaceViewScale
        val bitmapPosition = scaledPosition * bitmapToScreenRatio
        val centeredBitmapPosition = bitmapPosition + centerBitmapOffset
        return centeredBitmapPosition.toInt()
    }

    private fun isXPositionOutsideOfScreen(): Boolean = xPosition > virtualWidth / 2 ||
        xPosition < -virtualWidth / 2

    private fun isYPositionOutsideOfScreen(): Boolean = yPosition > virtualHeight / 2 ||
        yPosition < -virtualHeight / 2

    override fun setBufferParameters() {
        bufferHeight = 1
        bufferWidth = 1
    }

    override fun getLooksOfRelevantSprites(): MutableList<Look>? =
        stageListener?.let {
            ArrayList<Sprite>(it.spritesFromStage)
                .filter { s -> s.look.isLookVisible }
                .map { s -> s.look }
                .toMutableList()
        }

    override fun isParameterInvalid(parameter: Any?): Boolean =
        convertArgumentToDouble(parameter) == null

    override fun createProjectionMatrix(project: Project): Matrix4 {
        val camera = OrthographicCamera(bufferWidth.toFloat(), bufferHeight.toFloat())
        val viewPort = createViewport(
            project,
            bufferWidth.toFloat(),
            bufferHeight.toFloat(),
            camera
        )
        viewPort.apply()
        camera.position.set(xPosition.toFloat(), yPosition.toFloat(), 0f)
        camera.rotate(-look.rotation)
        camera.update()
        return camera.combined
    }
}
