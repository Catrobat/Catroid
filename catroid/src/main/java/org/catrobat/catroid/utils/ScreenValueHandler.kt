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
import android.util.DisplayMetrics
import android.view.WindowManager
import org.catrobat.catroid.common.ScreenValues

object ScreenValueHandler {

    @JvmStatic
    fun updateScreenWidthAndHeight(context: Context?) {
        val windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        val screenResolution = windowManager?.let(::screenResolutionFromWindowManager)

        if (screenResolution != null) {
            ScreenValues.currentScreenResolution = screenResolution
        } else {
            ScreenValues.setToDefaultScreenSize()
        }
    }

    @Suppress("DEPRECATION")
    private fun screenResolutionFromWindowManager(windowManager: WindowManager): Resolution {
        val displayMetrics = displayMetricsFor(windowManager)
        return Resolution(displayMetrics.widthPixels, displayMetrics.heightPixels)
    }

    @Suppress("DEPRECATION")
    private fun displayMetricsFor(windowManager: WindowManager): DisplayMetrics =
        DisplayMetrics().apply {
            // Project coordinates must match the drawable app window instead of the
            // full physical display, otherwise a hidden system-bar strip becomes
            // part of the virtual screen and STRETCH projects render squashed.
            windowManager.defaultDisplay.getMetrics(this)
        }
}
