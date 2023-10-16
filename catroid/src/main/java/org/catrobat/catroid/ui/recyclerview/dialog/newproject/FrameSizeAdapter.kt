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

package org.catrobat.catroid.ui.recyclerview.dialog.newproject

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import org.catrobat.catroid.R
import org.catrobat.catroid.common.ScreenValues
import org.catrobat.catroid.utils.ScreenValueHandler

class FrameSizeAdapter(
    private var context: Context,
    private var landscape: Boolean,
    private var cm: Boolean
) : BaseAdapter() {

    companion object {
        private const val PADDING_VALUE = 25
    }

    private val currentScreen: FrameSize

    init {
        ScreenValueHandler.updateScreenWidthAndHeight(context)
        currentScreen = FrameSize(
            ScreenValues.SCREEN_HEIGHT, ScreenValues.SCREEN_WIDTH
        )
    }

    fun update(landscape: Boolean, cm: Boolean) {
        this.landscape = landscape
        this.cm = cm

        notifyDataSetChanged()
    }

    override fun getCount(): Int = FrameSize.FrameSizes.size + 1

    override fun getItem(position: Int): FrameSize {
        if (position == 0) {
            return currentScreen
        }
        return FrameSize.FrameSizes[position - 1]
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = TextView(context)
        val frameSize = getItem(position)

        var resourceId = R.string.frame_size_in_cm
        var unit = FrameSizeUnit.CM

        if (!cm) {
            resourceId = R.string.frame_size_in_inches
            unit = FrameSizeUnit.INCH
        }

        val height = frameSize.getHeight(landscape, unit)
        val width = frameSize.getWidth(landscape, unit)

        view.text = context.getString(resourceId, height, width)
        view.setPadding(PADDING_VALUE, PADDING_VALUE, PADDING_VALUE, PADDING_VALUE)
        return view
    }
}
