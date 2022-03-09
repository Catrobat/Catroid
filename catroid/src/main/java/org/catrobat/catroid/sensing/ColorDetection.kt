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
import android.os.Handler
import android.os.HandlerThread
import android.view.PixelCopy
import androidx.annotation.RequiresApi
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.ScalingViewport
import com.badlogic.gdx.utils.viewport.Viewport
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.ScreenModes
import org.catrobat.catroid.content.Look
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.StageListener
import org.koin.java.KoinJavaComponent

abstract class ColorDetection(
    protected open val scope: Scope,
    protected open val stageListener: StageListener?
) {
    protected open val look = scope.sprite.look
    protected var bufferWidth = 0
    protected var bufferHeight = 0
    protected var cameraBitmap: Bitmap? = null
    private val projectManager: ProjectManager by KoinJavaComponent.inject(ProjectManager::class.java)
    protected val virtualHeight = projectManager.currentProject.xmlHeader
        .virtualScreenHeight
    protected val virtualWidth = projectManager.currentProject.xmlHeader
        .virtualScreenWidth

    protected fun isLookInvalid(): Boolean =
        look.width <= Float.MIN_VALUE || look.height <= Float.MIN_VALUE

    protected fun createPicture(
        lookList: List<Look>,
        projectionMatrix: Matrix4,
        batch: SpriteBatch
    ):
        Pixmap {
        val buffer = FrameBuffer(Pixmap.Format.RGBA8888, bufferWidth, bufferHeight, false)

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

    protected fun createViewport(
        project: Project,
        virtualWidth: Float,
        virtualHeight: Float,
        camera: Camera
    ): Viewport {
        return if (project.screenMode == ScreenModes.STRETCH) {
            ScalingViewport(Scaling.stretch, virtualWidth, virtualHeight, camera)
        } else {
            ExtendViewport(virtualWidth, virtualHeight, camera)
        }
    }

    var receiveBitmapFromPixelCopy = fun(bitmap: Bitmap?) {
        cameraBitmap = bitmap
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun callPixelCopyWithSurfaceView(callback: (Bitmap?) -> Unit) {
        val surfaceView = StageActivity.getActiveCameraManager().previewView.surfaceView
        val bitmap: Bitmap = if (projectManager.isCurrentProjectLandscapeMode) {
            createNewBitmap(surfaceView.height, surfaceView.width)
        } else {
            createNewBitmap(surfaceView.width, surfaceView.height)
        }
        val pixelCopyHandlerThread = HandlerThread("PixelCopier")
        pixelCopyHandlerThread.start()
        PixelCopy.request(
            surfaceView,
            bitmap,
            PixelCopy.OnPixelCopyFinishedListener { copyResult ->
                if (copyResult == PixelCopy.SUCCESS) {
                    callback(bitmap)
                } else {
                    callback(null)
                }
                pixelCopyHandlerThread.quitSafely()
            },
            Handler(pixelCopyHandlerThread.looper)
        )
        pixelCopyHandlerThread.join()
    }

    private fun createNewBitmap(width: Int, height: Int): Bitmap {
        return Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.ARGB_8888
        )
    }

    abstract fun setBufferParameters()
    abstract fun isParameterInvalid(parameter: Any?): Boolean
    abstract fun getLooksOfRelevantSprites(): MutableList<Look>?
    abstract fun createProjectionMatrix(project: Project): Matrix4
}
