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

package org.catrobat.catroid.ui.controller

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import org.catrobat.catroid.R
import org.catrobat.catroid.ui.BottomBar

class ActionBarStateManager(private val title: String?) {
    private var previousActionBarTitle: CharSequence? = null
    private var hasTitle: Boolean = false
    private var hasBottomBar: Boolean = false
    private var hasAddButton: Boolean = false
    private var hasPlayButton: Boolean = false
    private var savedBottomBar: Boolean = false
    private var savedAddButton: Boolean = false
    private var savedPlayButton: Boolean = false

    init {
        hasTitle = !title.isNullOrEmpty()
    }

    private fun savePreviousActionBarState(activity: AppCompatActivity?) {
        if (!previousActionBarTitle.isNullOrEmpty()) {
            return
        }
        val actionBar = activity?.supportActionBar
        previousActionBarTitle = actionBar?.title
        savedBottomBar = activity?.findViewById<View?>(R.id.bottom_bar)?.visibility == View.VISIBLE
        if (savedBottomBar) {
            savedAddButton = activity?.findViewById<View?>(R.id.button_add)?.visibility == View
                .VISIBLE
            savedPlayButton = activity?.findViewById<View?>(R.id.button_play)?.visibility == View
                .VISIBLE
        }
    }

    private fun setUpBottomBar(activity: AppCompatActivity?) {
        if (hasBottomBar) {
            BottomBar.showBottomBar(activity)
        } else {
            BottomBar.hideBottomBar(activity)
            return
        }
        if (hasPlayButton) {
            BottomBar.showPlayButton(activity)
        } else {
            BottomBar.hidePlayButton(activity)
        }
        if (hasAddButton) {
            BottomBar.showAddButton(activity)
        } else {
            BottomBar.hideAddButton(activity)
        }
    }

    private fun setUpActionBar(activity: AppCompatActivity?) {
        savePreviousActionBarState(activity)
        val actionBar = activity?.supportActionBar
        if (hasTitle) {
            actionBar?.setDisplayShowTitleEnabled(true)
            actionBar?.title = title
        } else {
            actionBar?.setDisplayShowTitleEnabled(false)
            actionBar?.title = ""
        }
    }

    fun restoreOldState(activity: AppCompatActivity?) {
        val actionBar = activity?.supportActionBar
        val isRestoringPreviouslyDestroyedActivity = actionBar == null
        if (!isRestoringPreviouslyDestroyedActivity) {
            actionBar?.setDisplayShowTitleEnabled(!previousActionBarTitle.isNullOrEmpty())
            actionBar?.title = previousActionBarTitle
        }

        if (savedBottomBar) {
            BottomBar.showBottomBar(activity)
        } else {
            BottomBar.hideBottomBar(activity)
            return
        }
        if (savedPlayButton) {
            BottomBar.showPlayButton(activity)
        } else {
            BottomBar.showPlayButton(activity)
        }
        if (savedAddButton) {
            BottomBar.showAddButton(activity)
        } else {
            BottomBar.showAddButton(activity)
        }
    }

    fun setUpActionAndBottomBar(activity: AppCompatActivity?) {
        setUpActionBar(activity)
        setUpBottomBar(activity)
    }
}
