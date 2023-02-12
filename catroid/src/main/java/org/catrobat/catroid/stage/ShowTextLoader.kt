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
package org.catrobat.catroid.stage

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.opengl.GLES20
import android.opengl.GLUtils
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.Array
import org.catrobat.catroid.common.ScreenValues
import org.catrobat.catroid.stage.ShowTextLoader.ShowTextParameter
import org.catrobat.catroid.utils.ShowTextUtils
import java.util.Locale

class ShowTextLoader(resolver: FileHandleResolver?) :
    SynchronousAssetLoader<ShowText, ShowTextParameter>(resolver) {
    override fun load(
        am: AssetManager,
        fileName: String,
        file: FileHandle,
        param: ShowTextParameter
    ): ShowText {
        // Convert to bitmap
        val paint = Paint()
        val textSizeInPx = ShowTextUtils.sanitizeTextSize(param.textSize)
        paint.textSize = textSizeInPx
        if (ShowTextUtils.isValidColorString(param.color)) {
            param.color = param.color!!.toUpperCase(Locale.getDefault())
            val rgb: IntArray
            rgb = ShowTextUtils.calculateColorRGBs(param.color)
            paint.color = -0x1000000 or (rgb[0] shl BitCount16) or (rgb[1] shl BitCount8) or rgb[2]
        } else {
            paint.color = Color.BLACK
        }
        val baseline = -paint.ascent()
        paint.isAntiAlias = true

        val availableWidth = ScreenValues.SCREEN_WIDTH + 2 * Math.abs(param.xPosition)
        val bitmapWidth = availableWidth.coerceAtMost(paint.measureText(param.text).toInt())
        val canvasWidth =
            ShowTextUtils.calculateAlignmentValuesForText(paint, bitmapWidth, param.alignment)
        val height = (baseline + paint.descent()).toInt()
        val bitmap = Bitmap.createBitmap(bitmapWidth, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawText(param.text!!, canvasWidth.toFloat(), baseline, paint)
        // Convert to texture
        val tex = Texture(
            bitmap.width, bitmap.height,
            Pixmap.Format.RGBA8888
        )
        GLES20.glBindTexture(
            GLES20.GL_TEXTURE_2D,
            tex.textureObjectHandle
        )
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        bitmap.recycle()
        return ShowText(tex, canvasWidth, textSizeInPx)
    }

    override fun getDependencies(
        fileName: String,
        file: FileHandle,
        param: ShowTextParameter
    ): Array<AssetDescriptor<*>>? = null

    class ShowTextParameter : AssetLoaderParameters<ShowText?>() {
        @JvmField var xPosition = 0
        @JvmField var yPosition = 0
        @JvmField var color: String? = null
        @JvmField var text: String? = null
        @JvmField var textSize = 0f
        @JvmField var alignment = 0
    }

    companion object {
        private const val BitCount16: Int = 16
        private const val BitCount8: Int = 8
        const val ID_POSTFIX = "ShowTextLoaderId"
    }
}
