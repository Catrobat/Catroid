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
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.ScalingViewport
import com.badlogic.gdx.utils.viewport.Viewport
import org.catrobat.catroid.common.ScreenModes
import org.catrobat.catroid.content.Look
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.common.Conversions.matchesColor
import org.catrobat.catroid.stage.StageListener
import java.util.regex.Pattern

class ColorCollisionDetection(
    private val sprite: Sprite,
    private val currentProject: Project,
    private val stageListener: StageListener
) {

    private val colorPattern = Pattern.compile("#\\p{XDigit}{6}")

    @Suppress("TooGenericExceptionCaught")
    fun tryInterpretFunctionTouchesColor(parameter: Any?): Boolean {
        return try {
            interpretFunctionTouchesColor(parameter)
        } catch (_: Exception) {
            false
        }
    }

    fun interpretFunctionTouchesColor(parameter: Any?): Boolean {
        val look = sprite.look
        if (areParametersInvalid(parameter, look)) {
            return false
        }
        val color = parameter as String
        val lookList: MutableList<Look> = getLooksOfOtherSprites()
        val batch = SpriteBatch()
        val projectionMatrix = createProjectionMatrix(currentProject, look)
        val pixmap = recreateStageOnCameraView(lookList, projectionMatrix, look, batch)

        try {
            return matchesColor(pixmap, look.currentCollisionPolygon, color)
        } finally {
            pixmap.dispose()
            batch.dispose()
        }
    }

    private fun getLooksOfOtherSprites(): MutableList<Look> =
        ArrayList<Sprite>(stageListener.spritesFromStage)
            .filter { s -> (s != sprite || s.isClone) && s.look.isLookVisible }
            .map { s -> s.look }
            .toMutableList()

    @VisibleForTesting
    fun areParametersInvalid(parameter: Any?, look: Look): Boolean =
        parameter == null || parameter !is String || !colorPattern.matcher(parameter).matches() ||
            look.width <= Float.MIN_VALUE || look.height <= Float.MIN_VALUE

    private fun recreateStageOnCameraView(lookList: List<Look>, projectionMatrix: Matrix4, actor: Actor, batch: SpriteBatch): Pixmap {
        val buffer = FrameBuffer(Pixmap.Format.RGBA8888, actor.width.toInt(), actor.height.toInt(), false)

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
        for (look in lookList) {
            look.draw(batch, 1f)
        }
    }

    private fun createProjectionMatrix(project: Project, actor: Actor): Matrix4 {
        val scaledWidth = actor.width * actor.scaleX
        val scaledHeight = actor.height * actor.scaleY
        val camera = OrthographicCamera(scaledWidth, scaledHeight)
        val viewPort = createViewport(project, scaledWidth, scaledHeight, camera)
        viewPort.apply()
        camera.position.set(actor.x + actor.width / 2, actor.y + actor.height / 2, 0f)
        camera.rotate(actor.rotation)
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
