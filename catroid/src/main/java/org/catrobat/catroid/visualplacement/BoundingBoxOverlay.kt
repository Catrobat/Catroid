/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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

package org.catrobat.catroid.visualplacement

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import org.catrobat.catroid.R

class BoundingBoxOverlay(context: Context) : View(context) {

    private val boundingBoxPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = BOUNDING_BOX_COLOR
        style = Paint.Style.STROKE
        strokeWidth = BOUNDING_BOX_STROKE_WIDTH
    }

    private val cornerHandleDrawable = ContextCompat.getDrawable(context, R.drawable.ic_corner_handle)

    private val handleSizePx: Int =
        (HANDLE_SIZE_DP * context.resources.displayMetrics.density).toInt()

    var trackedImageView: ImageView? = null

    fun updateOverlay() {
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val imageView = trackedImageView ?: return
        val drawable = imageView.drawable ?: return

        val imgWidth = drawable.intrinsicWidth.toFloat()
        val imgHeight = drawable.intrinsicHeight.toFloat()

        val scaleX = imageView.scaleX
        val scaleY = imageView.scaleY

        val scaledWidth = imgWidth * scaleX
        val scaledHeight = imgHeight * scaleY

        val viewCenterX = imageView.x + imageView.width / 2f
        val viewCenterY = imageView.y + imageView.height / 2f

        val left = viewCenterX - scaledWidth / 2f
        val top = viewCenterY - scaledHeight / 2f
        val right = viewCenterX + scaledWidth / 2f
        val bottom = viewCenterY + scaledHeight / 2f

        val rect = RectF(left, top, right, bottom)

        canvas.save()
        canvas.rotate(imageView.rotation, viewCenterX, viewCenterY)

        canvas.drawRect(rect, boundingBoxPaint)

        drawCornerHandle(canvas, left, top, 0f)
        drawCornerHandle(canvas, right, top, 90f)
        drawCornerHandle(canvas, right, bottom, 180f)
        drawCornerHandle(canvas, left, bottom, 270f)

        canvas.restore()
    }

    private fun drawCornerHandle(canvas: Canvas, cx: Float, cy: Float, rotationDegrees: Float) {
        val handle = cornerHandleDrawable ?: return

        canvas.save()
        canvas.translate(cx, cy)
        canvas.rotate(rotationDegrees)

        val halfSize = handleSizePx / 2
        handle.setBounds(-halfSize, -halfSize, halfSize, halfSize)
        handle.draw(canvas)

        canvas.restore()
    }

    companion object {
        private const val BOUNDING_BOX_STROKE_WIDTH = 3f
        private val BOUNDING_BOX_COLOR = Color.parseColor("#4FC3F7")
        private const val HANDLE_SIZE_DP = 32
    }
}
