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
package org.catrobat.catroid.sensing

import androidx.annotation.VisibleForTesting
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.ScalingViewport
import com.badlogic.gdx.utils.viewport.Viewport
import org.catrobat.catroid.common.ScreenModes
import org.catrobat.catroid.content.Look
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.common.Conversions.isValidHexColor
import org.catrobat.catroid.stage.StageListener
import kotlin.math.sqrt

private const val MAX_PIXELS = 10_000f

class ColorCollisionDetection(
    private val sprite: Sprite,
    private val currentProject: Project,
    private val stageListener: StageListener
) {
    private val look: Look = sprite.look
    private val polygons = sprite.look.currentCollisionPolygon
    private val boundingRectangle = polygons.toBoundingRectangle()
    private val scale = calculateBufferScale()
    private val bufferWidth = (boundingRectangle.width * scale).toInt()
    private val bufferHeight = (boundingRectangle.height * scale).toInt()

    @Suppress("TooGenericExceptionCaught")
    fun tryInterpretFunctionTouchesColor(color: Any?): Boolean {
        if (isParameterInvalid(color) || isLookInvalid()) {
            return false
        }
        val matcher = TouchesColorMatcher(color as String)
        return try {
            interpretMatcherOnStage(matcher)
        } catch (_: Exception) {
            false
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun tryInterpretFunctionColorTouchesColor(spriteColor: Any?, stageColor: Any?): Boolean {
        if (isParameterInvalid(spriteColor) || isParameterInvalid(stageColor) || isLookInvalid()) {
            return false
        }
        val matcher = ColorTouchesColorMatcher(spriteColor as String, stageColor as String)
        return try {
            interpretMatcherOnStage(matcher)
        } catch (_: Exception) {
            false
        }
    }

    private fun interpretMatcherOnStage(matcher: ConditionMatcher): Boolean {
        val lookList: MutableList<Look> = getLooksOfOtherSprites()
        val batch = SpriteBatch()
        val spriteBatch = SpriteBatch()
        val projectionMatrix = createProjectionMatrix(currentProject)
        matcher.stagePixmap = createPicture(lookList, projectionMatrix, batch)
        val wasLookVisible = look.isLookVisible
        look.isLookVisible = true
        matcher.spritePixmap = createPicture(listOf(look), projectionMatrix, spriteBatch)
        look.isLookVisible = wasLookVisible

        try {
            return ConditionMatcherRunner(matcher).match(bufferWidth, bufferHeight)
        } finally {
            matcher.stagePixmap?.dispose()
            matcher.spritePixmap?.dispose()
            batch.dispose()
            spriteBatch.dispose()
        }
    }

    private fun getLooksOfOtherSprites(): MutableList<Look> =
        ArrayList<Sprite>(stageListener.spritesFromStage)
            .filter { s -> (s != sprite || s.isClone) && s.look.isLookVisible }
            .map { s -> s.look }
            .toMutableList()

    @VisibleForTesting
    fun isParameterInvalid(parameter: Any?): Boolean = parameter !is String || !parameter.isValidHexColor()

    private fun isLookInvalid(): Boolean = look.width <= Float.MIN_VALUE || look.height <= Float.MIN_VALUE

    private fun createPicture(lookList: List<Look>, projectionMatrix: Matrix4, batch: SpriteBatch): Pixmap {
        val buffer = FrameBuffer(RGBA8888, bufferWidth, bufferHeight, false)

        batch.projectionMatrix = projectionMatrix
        buffer.begin()
        batch.begin()
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT)
        drawSprites(lookList, batch)
        batch.end()
        val pixmap = ScreenUtils.getFrameBufferPixmap(0, 0, buffer.width, buffer.height)
        buffer.end()
        buffer.dispose()
        return pixmap
    }

    private fun drawSprites(lookList: List<Look>, batch: SpriteBatch) {
        lookList.forEach { it.draw(batch, 1f) }
    }

    private fun calculateBufferScale(): Float {
        val scale = sqrt(MAX_PIXELS / (boundingRectangle.width * boundingRectangle.height))
        return if (scale < 1) {
            scale
        } else {
            1f
        }
    }

    private fun createProjectionMatrix(project: Project): Matrix4 {
        val scaledWidth = boundingRectangle.width * look.scaleX
        val scaledHeight = boundingRectangle.height * look.scaleY
        val camera = OrthographicCamera(scaledWidth, scaledHeight)
        val viewPort = createViewport(project, scaledWidth, scaledHeight, camera)
        viewPort.apply()
        camera.position.set(look.x + look.width / 2, look.y + look.height / 2, 0f)
        camera.rotate(-look.rotation)
        camera.update()
        return camera.combined
    }

    private fun createViewport(project: Project, virtualWidth: Float, virtualHeight: Float, camera: Camera): Viewport {
        return if (project.screenMode == ScreenModes.STRETCH) {
            ScalingViewport(Scaling.stretch, virtualWidth, virtualHeight, camera)
        } else {
            ExtendViewport(virtualWidth, virtualHeight, camera)
        }
    }
}
