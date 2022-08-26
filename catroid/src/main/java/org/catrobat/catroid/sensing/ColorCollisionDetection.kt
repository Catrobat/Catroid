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

import androidx.annotation.VisibleForTesting
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Matrix4
import org.catrobat.catroid.content.Look
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.common.Conversions.isValidHexColor
import org.catrobat.catroid.stage.StageListener
import kotlin.math.sqrt

private const val MAX_PIXELS = 10_000f

class ColorCollisionDetection(
    scope: Scope,
    stageListener: StageListener?
) : ColorDetection(scope, stageListener) {
    private val polygons = scope.sprite.look.currentCollisionPolygon
    private val boundingRectangle = polygons.toBoundingRectangle()
    private val scale = calculateBufferScale()

    @Suppress("TooGenericExceptionCaught")
    fun tryInterpretFunctionTouchesColor(color: Any?): Boolean {
        setBufferParameters()
        if (isParameterInvalid(color) || isLookInvalid() || stageListener == null) {
            return false
        }
        val matcher = TouchesColorMatcher(color as String)
        return try {
            interpretMatcherOnStage(matcher)
        } catch (_: Exception) {
            false
        }
    }

    private fun interpretMatcherOnStage(matcher: ConditionMatcher): Boolean {
        val lookList: MutableList<Look> = getLooksOfRelevantSprites() ?: return false
        val batch = SpriteBatch()
        val spriteBatch = SpriteBatch()
        val projectionMatrix = scope.project?.let { createProjectionMatrix(it) }
        matcher.stagePixmap = projectionMatrix?.let { createPicture(lookList, it, batch) }
        val wasLookVisible = look.isLookVisible
        look.isLookVisible = true
        matcher.spritePixmap = projectionMatrix?.let {
            createPicture(listOf(look), it, spriteBatch)
        }
        look.isLookVisible = wasLookVisible

        return tryConditionMatcherRunnerMatch(matcher, batch, spriteBatch)
    }

    private fun tryConditionMatcherRunnerMatch(
        matcher: ConditionMatcher,
        batch: SpriteBatch,
        spriteBatch: SpriteBatch
    ): Boolean {
        return try {
            ConditionMatcherRunner(matcher).match(bufferWidth, bufferHeight)
        } finally {
            matcher.stagePixmap?.dispose()
            matcher.spritePixmap?.dispose()
            batch.dispose()
            spriteBatch.dispose()
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun tryInterpretFunctionColorTouchesColor(spriteColor: Any?, stageColor: Any?): Boolean {
        setBufferParameters()
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

    fun calculateBufferScale(): Float {
        val scale = sqrt(MAX_PIXELS / (boundingRectangle.width * boundingRectangle.height))
        return if (scale < 1) {
            scale
        } else {
            1f
        }
    }

    override fun setBufferParameters() {
        bufferWidth = (boundingRectangle.width * scale).toInt()
        bufferHeight = (boundingRectangle.height * scale).toInt()
    }

    @VisibleForTesting
    override fun isParameterInvalid(parameter: Any?): Boolean =
        parameter !is String || !parameter.isValidHexColor()

    override fun getLooksOfRelevantSprites(): MutableList<Look>? =
        stageListener?.let {
            ArrayList<Sprite>(it.spritesFromStage)
                .filter { s -> (s != scope.sprite || s.isClone) && s.look.isLookVisible }
                .map { s -> s.look }
                .toMutableList()
        }

    override fun createProjectionMatrix(project: Project): Matrix4 {
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
}
