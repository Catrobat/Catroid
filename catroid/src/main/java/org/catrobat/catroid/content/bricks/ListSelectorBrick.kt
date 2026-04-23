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
package org.catrobat.catroid.content.bricks

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.annotation.IdRes
import org.catrobat.catroid.R
import org.catrobat.catroid.formulaeditor.UserData
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.ui.recyclerview.fragment.ListSelectorFragment.Companion.showFragment
import org.catrobat.catroid.ui.recyclerview.fragment.ListSelectorFragment.ListSelectorInterface

abstract class ListSelectorBrick : BrickBaseType(), View.OnClickListener,
    ListSelectorInterface {
    var userLists = mutableListOf<UserList>()

    @Throws(CloneNotSupportedException::class)
    override fun clone(): Brick {
        val clone = super.clone() as ListSelectorBrick
        clone.userLists = userLists.toMutableList()
        return clone
    }

    override fun getView(context: Context): View {
        super.getView(context)
        updateSelectorText()
        return view
    }

    @get:IdRes
    protected abstract val selectorId: Int
    protected open fun updateSelectorText() {
        val brickFieldView = view.findViewById<TextView>(selectorId)
        brickFieldView.text = view.resources.getQuantityString(
            R.plurals.list_selection_plural,
            userLists.size, userLists.size
        )
    }

    fun setClickListeners() {
        val brickFieldView = view.findViewById<TextView>(selectorId)
        brickFieldView.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        showFragment(view.context, this)
    }

    override fun onUserListSelected(userLists: List<UserList>) {
        this.userLists = userLists.toMutableList()
        updateSelectorText()
    }

    fun deselectElements(deletedElements: List<*>) {
        deletedElements.filterIsInstance<UserData<*>>().forEach { element ->
            userLists.removeAll { list -> list.name == element.name }
        }
    }
}
