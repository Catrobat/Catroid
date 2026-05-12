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
package org.catrobat.catroid.ui

import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

object EdgeToEdge {
    @JvmStatic
    fun applyTopPadding(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v: View, insets: WindowInsetsCompat ->
            val consideredUIComponents =
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()

            val topInsets = insets.getInsets(consideredUIComponents)
            v.setPadding(v.paddingLeft, topInsets.top, v.paddingRight, v.paddingBottom)

            WindowInsetsCompat.CONSUMED
        }
    }

    @JvmStatic
    fun applyBottomMargin(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v: View, insets: WindowInsetsCompat ->
            val consideredUIComponents = WindowInsetsCompat.Type.systemBars()

            val bottomInsets = insets.getInsets(consideredUIComponents)
            val params = v.layoutParams as MarginLayoutParams
            params.bottomMargin = bottomInsets.bottom

            WindowInsetsCompat.CONSUMED
        }
    }

    @JvmStatic
    fun applyFloatingActionButtonMargin(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v: View, insets: WindowInsetsCompat ->
            val consideredUIComponents = WindowInsetsCompat.Type.systemBars()

            val bottomInsets = insets.getInsets(consideredUIComponents)
            val params = v.layoutParams as MarginLayoutParams
            params.bottomMargin += bottomInsets.bottom

            WindowInsetsCompat.CONSUMED
        }
    }

    @JvmStatic
    fun applyBottomPadding(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v: View, insets: WindowInsetsCompat ->
            val consideredUIComponents =
                (WindowInsetsCompat.Type.systemBars()
                    or WindowInsetsCompat.Type.displayCutout()
                    or WindowInsetsCompat.Type.ime())

            val allInsets = insets.getInsets(consideredUIComponents)
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, allInsets.bottom)

            WindowInsetsCompat.CONSUMED
        }
    }
}
