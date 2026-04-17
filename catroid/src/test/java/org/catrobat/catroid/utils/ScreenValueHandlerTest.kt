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

package org.catrobat.catroid.utils

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.view.Display
import android.view.WindowManager
import android.view.WindowMetrics
import org.catrobat.catroid.common.ScreenValues
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
class ScreenValueHandlerTest {

    @After
    fun tearDown() {
        ScreenValues.setToDefaultScreenSize()
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.R])
    fun updateScreenWidthAndHeightUsesCurrentWindowMetricsOnAndroidRAndAbove() {
        val context = mock(Context::class.java)
        val windowManager = mock(WindowManager::class.java)
        val windowMetrics = mock(WindowMetrics::class.java)
        val display = mock(Display::class.java)

        stubGetMetrics(display, width = 1080, height = 2280)
        `when`(windowManager.defaultDisplay).thenReturn(display)
        `when`(windowMetrics.bounds).thenReturn(Rect(0, 0, 1080, 2400))
        `when`(windowManager.currentWindowMetrics).thenReturn(windowMetrics)
        `when`(context.getSystemService(Context.WINDOW_SERVICE)).thenReturn(windowManager)

        ScreenValueHandler.updateScreenWidthAndHeight(context)

        assertEquals(1080, ScreenValues.currentScreenResolution.width)
        assertEquals(2400, ScreenValues.currentScreenResolution.height)
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.Q])
    fun updateScreenWidthAndHeightUsesRealMetricsBeforeAndroidR() {
        val context = mock(Context::class.java)
        val windowManager = mock(WindowManager::class.java)
        val display = mock(Display::class.java)

        stubGetMetrics(display, width = 1080, height = 2280)
        stubGetRealMetrics(display, width = 1080, height = 2400)
        `when`(windowManager.defaultDisplay).thenReturn(display)
        `when`(context.getSystemService(Context.WINDOW_SERVICE)).thenReturn(windowManager)

        ScreenValueHandler.updateScreenWidthAndHeight(context)

        assertEquals(1080, ScreenValues.currentScreenResolution.width)
        assertEquals(2400, ScreenValues.currentScreenResolution.height)
    }

    private fun stubGetMetrics(display: Display, width: Int, height: Int) {
        doAnswer { invocation ->
            val metrics = invocation.getArgument<DisplayMetrics>(0)
            metrics.widthPixels = width
            metrics.heightPixels = height
            null
        }.`when`(display).getMetrics(any(DisplayMetrics::class.java))
    }

    private fun stubGetRealMetrics(display: Display, width: Int, height: Int) {
        doAnswer { invocation ->
            val metrics = invocation.getArgument<DisplayMetrics>(0)
            metrics.widthPixels = width
            metrics.heightPixels = height
            null
        }.`when`(display).getRealMetrics(any(DisplayMetrics::class.java))
    }
}
