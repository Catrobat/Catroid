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
package org.catrobat.catroid.camera

import android.content.Context
import android.os.Build
import android.view.SurfaceView
import android.widget.FrameLayout
import androidx.camera.core.Preview.SurfaceProvider
import androidx.core.util.Consumer
import java.util.concurrent.Executors
import kotlin.math.roundToInt

class PreviewView(context: Context) : FrameLayout(context) {
    val surfaceView = SurfaceView(context)

    init {
        addView(surfaceView)
    }

    fun createSurfaceProvider() = SurfaceProvider { request ->
        with(request.resolution) {
            surfaceView.holder.setFixedSize(width, height)
            scaleView(width, height)
        }
        request.provideSurface(
            surfaceView.holder.surface,
            Executors.newSingleThreadExecutor(),
            Consumer { }
        )
    }

    private fun scaleView(imageWidth: Int, imageHeight: Int) {
        val imageAspectRatio = imageHeight.toFloat() / imageWidth
        val screenAspectRatio = this.width.toFloat() / this.height

        if (screenAspectRatio < 1) { // portrait mode
            val scalingFactor = imageAspectRatio / screenAspectRatio
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
                surfaceView.layoutParams.width = (this.width * scalingFactor).roundToInt()
            }
            surfaceView.scaleX = scalingFactor
        } else { // landscape mode
            val scalingFactor = imageAspectRatio * screenAspectRatio
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
                surfaceView.layoutParams.height = (this.height * scalingFactor).roundToInt()
            }
            surfaceView.scaleY = scalingFactor
        }
    }
}
