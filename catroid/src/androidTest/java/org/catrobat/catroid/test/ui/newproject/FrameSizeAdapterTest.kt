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

package org.catrobat.catroid.test.ui.newproject

import android.content.Context
import android.widget.LinearLayout
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import org.catrobat.catroid.R
import org.catrobat.catroid.common.ScreenValues
import org.catrobat.catroid.ui.recyclerview.dialog.newproject.FrameSize
import org.catrobat.catroid.ui.recyclerview.dialog.newproject.FrameSizeAdapter
import org.catrobat.catroid.ui.recyclerview.dialog.newproject.FrameSizeUnit
import org.junit.Assert.assertEquals
import org.junit.Test

class FrameSizeAdapterTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test

    fun testInitialization() {
        val adapter = FrameSizeAdapter(context, landscape = false, cm = false)

        assertEquals(FrameSize.FrameSizes.size + 1, adapter.count)

        for (i in 0..FrameSize.FrameSizes.size) {
            val frame = getFrameSize(i)
            assertEquals(frame, adapter.getItem(i))
            assertEquals(i.toLong(), adapter.getItemId(i))
        }
    }

    @Test
    fun testLandscapeModeInCm() {
        val adapter = FrameSizeAdapter(context, landscape = true, cm = true)
        testAdapter(true, R.string.frame_size_in_cm, FrameSizeUnit.CM, adapter)
    }

    @Test
    fun testLandscapeModeInInch() {
        val adapter = FrameSizeAdapter(context, landscape = true, cm = false)
        testAdapter(true, R.string.frame_size_in_inches, FrameSizeUnit.INCH, adapter)
    }

    @Test
    fun testPortraitModeInCm() {
        val adapter = FrameSizeAdapter(context, landscape = false, cm = true)
        testAdapter(false, R.string.frame_size_in_cm, FrameSizeUnit.CM, adapter)
    }

    @Test
    fun testPortraitModeInInch() {
        val adapter = FrameSizeAdapter(context, landscape = false, cm = false)
        testAdapter(false, R.string.frame_size_in_inches, FrameSizeUnit.INCH, adapter)
    }

    @Test
    fun testPortraitModeInInchChangeToLandscapeModeInCm() {
        val adapter = FrameSizeAdapter(context, landscape = false, cm = false)
        adapter.update(landscape = true, cm = true)
        testAdapter(true, R.string.frame_size_in_cm, FrameSizeUnit.CM, adapter)
    }

    private fun testAdapter(
        landscape: Boolean,
        resourceId: Int,
        unit: FrameSizeUnit,
        adapter: FrameSizeAdapter
    ) {
        for (i in 0..FrameSize.FrameSizes.size) {
            val view = adapter.getView(i, null, LinearLayout(context)) as TextView

            val frame = getFrameSize(i)
            val height = frame.getHeight(landscape, unit)
            val width = frame.getWidth(landscape, unit)
            val text = context.getString(resourceId, height, width)

            assertEquals(text, view.text)
        }
    }

    private fun getFrameSize(index: Int): FrameSize {
        if (index == 0) {
            return FrameSize(ScreenValues.SCREEN_HEIGHT, ScreenValues.SCREEN_WIDTH)
        }
        return FrameSize.FrameSizes[index - 1]
    }
}
