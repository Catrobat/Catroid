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
package org.catrobat.catroid.ui.recyclerview

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.catrobat.catroid.R
import org.catrobat.catroid.ui.recyclerview.adapter.FeaturedProjectsAdapter
import org.catrobat.catroid.utils.dpToPx

class IndicatorDecoration(context: Context) : RecyclerView.ItemDecoration() {

    companion object {
        private const val DIFF_RADIUS = 1.5f
        private const val UNSELECTED_RADIUS = 4f
        private const val MARGIN = 8f
    }

    private val selectedColor: Int =
        ContextCompat.getColor(context, R.color.indicator_selected_color)
    private val unselectedColor: Int =
        ContextCompat.getColor(context, R.color.indicator_unselected_color)

    private val diffRadius: Float = context.dpToPx(DIFF_RADIUS)
    private val unselectedRadius: Float = context.dpToPx(UNSELECTED_RADIUS)
    private val margin: Float = unselectedRadius + context.dpToPx(MARGIN)

    private val paint by lazy {
        Paint().apply {
            color = unselectedColor
            style = Paint.Style.FILL
        }
    }

    private val evaluator by lazy {
        ArgbEvaluator()
    }

    private val interpolator by lazy { AccelerateDecelerateInterpolator() }

    @SuppressWarnings("NestedBlockDepth")
    override fun onDrawOver(
        canvas: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.onDrawOver(canvas, parent, state)

        parent.adapter
            ?.takeIf { it.itemCount > 0 }
            ?.run {
                val itemCount = (this as FeaturedProjectsAdapter).itemCount

                (parent.layoutManager as? LinearLayoutManager)
                    ?.let { layoutManager ->
                    val firstItemPosition = layoutManager.findFirstVisibleItemPosition()
                    val actualItemPosition = firstItemPosition % itemCount

                    layoutManager.findViewByPosition(firstItemPosition)
                        ?.let { firstView ->
                        val totalWidth = margin * (itemCount - 1)
                        val startX = (parent.width - totalWidth) / 2
                        val y = parent.height - margin

                        val left = firstView.left
                        val width = firstView.width
                        val progress = interpolator.getInterpolation(-1 * left / width.toFloat())

                        for (i in 0 until itemCount) {
                            var radius = unselectedRadius

                            when (i) {
                                actualItemPosition -> {
                                    radius += diffRadius * (1 - progress)
                                    paint.color = evaluator.evaluate(
                                        1 - progress,
                                        unselectedColor,
                                        selectedColor
                                    ) as Int
                                }

                                actualItemPosition + 1 -> {
                                    radius += diffRadius * progress
                                    paint.color =
                                        evaluator.evaluate(
                                            progress,
                                            unselectedColor,
                                            selectedColor
                                        ) as Int
                                }

                                else ->
                                    paint.color = unselectedColor
                            }

                            canvas.drawCircle(startX + i * margin, y, radius, paint)
                        }
                    }
                }
            }
    }
}
