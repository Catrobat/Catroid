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
package org.catrobat.catroid.ui

import android.app.Activity
import android.view.View
import org.catrobat.catroid.R

object BottomBar {
    @JvmStatic
    fun showBottomBar(activity: Activity?) {
        activity?.findViewById<View?>(R.id.bottom_bar)?.visibility = View.VISIBLE
    }

    @JvmStatic
    fun hideBottomBar(activity: Activity?) {
        activity?.findViewById<View?>(R.id.bottom_bar)?.visibility = View.GONE
    }

    @JvmStatic
    fun showAddButton(activity: Activity?) {
        activity?.findViewById<View?>(R.id.button_add)?.visibility = View.VISIBLE
    }

    @JvmStatic
    fun hideAddButton(activity: Activity?) {
        activity?.findViewById<View?>(R.id.button_add)?.visibility = View.GONE
    }

    @JvmStatic
    fun showPlayButton(activity: Activity?) {
        activity?.findViewById<View?>(R.id.button_play)?.visibility = View.VISIBLE
    }

    @JvmStatic
    fun hidePlayButton(activity: Activity?) {
        activity?.findViewById<View?>(R.id.button_play)?.visibility = View.GONE
    }

    @JvmStatic
    fun showAiAssistButton(activity: Activity?) {
        activity?.findViewById<View?>(R.id.button_ai_assist)?.visibility = View.VISIBLE
    }

    @JvmStatic
    fun hideAiAssistButton(activity: Activity?) {
        activity?.findViewById<View?>(R.id.button_ai_assist)?.visibility = View.GONE
    }
}
