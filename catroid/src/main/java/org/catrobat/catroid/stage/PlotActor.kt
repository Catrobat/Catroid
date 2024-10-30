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
package org.catrobat.catroid.stage

import android.content.res.Resources
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import org.catrobat.catroid.ProjectManager
import kotlin.math.pow
import kotlin.math.sqrt

class PlotActor : Actor() {
    private var buffer: FrameBuffer?
    private var bufferBatch: Batch?
    private val camera: OrthographicCamera
    private val screenRatio: Float

    init {
        val header = ProjectManager.getInstance().currentProject.xmlHeader
        buffer = FrameBuffer(
            Pixmap.Format.RGBA8888,
            header.virtualScreenWidth,
            header.virtualScreenHeight,
            false
        )
        bufferBatch = SpriteBatch()
        camera = OrthographicCamera(
            header.virtualScreenWidth.toFloat(),
            header.virtualScreenHeight.toFloat()
        )
        (bufferBatch as SpriteBatch).setProjectionMatrix(camera.combined)
        screenRatio = calculateScreenRatio()
        reset()
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        buffer!!.begin()
        for (sprite in StageActivity.stageListener.spritesFromStage) {
            val plot = sprite.plot
            plot.drawLinesForSprite(screenRatio, stage.viewport.camera)
        }
        buffer!!.end()

        batch.end()
        val region = TextureRegion(buffer!!.colorBufferTexture)
        region.flip(false, true)
        val image = Image(region)
        image.setPosition((-buffer!!.width / 2).toFloat(), (-buffer!!.height / 2).toFloat())
        batch.begin()
        image.draw(batch, parentAlpha)
    }

    fun reset() {
        buffer!!.begin()
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT)
        buffer!!.end()
    }

    fun dispose() {
        if (buffer != null) {
            buffer!!.dispose()
            buffer = null
        }
        if (bufferBatch != null) {
            bufferBatch!!.dispose()
            bufferBatch = null
        }
    }

    private fun calculateScreenRatio(): Float {
        val metrics = Resources.getSystem().displayMetrics
        val deviceDiagonalPixel =
            sqrt(metrics.widthPixels.toFloat().pow(2) + metrics.heightPixels .toFloat().pow(2))

        val header = ProjectManager.getInstance().currentProject.xmlHeader
        val creatorDiagonalPixel =
            sqrt(header.getVirtualScreenWidth().toFloat().pow(2) + header.getVirtualScreenHeight
                ().toFloat().pow(2))
        return creatorDiagonalPixel / deviceDiagonalPixel
    }
}