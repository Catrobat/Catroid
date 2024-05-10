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
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.UserData
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.io.catlang.parser.project.error.CatrobatLanguageParsingException
import org.catrobat.catroid.io.catlang.CatrobatLanguageUtils
import org.catrobat.catroid.ui.recyclerview.fragment.ListSelectorFragment.Companion.showFragment
import org.catrobat.catroid.ui.recyclerview.fragment.ListSelectorFragment.ListSelectorInterface

abstract class ListSelectorBrick : BrickBaseType(), View.OnClickListener,
    ListSelectorInterface {

    companion object {
        private const val LISTS_CATLANG_PARAMETER_NAME = "lists"
    }

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

    override fun getArgumentByCatlangName(name: String?): MutableMap.MutableEntry<String, String> {
        if (name == LISTS_CATLANG_PARAMETER_NAME) {
            val lists = userLists.joinToString(", ") {
                CatrobatLanguageUtils.formatList(it.name)
            }
            return CatrobatLanguageUtils.getCatlangArgumentTuple(LISTS_CATLANG_PARAMETER_NAME, lists)
        }
        return super.getArgumentByCatlangName(name)
    }

    override fun getRequiredCatlangArgumentNames(): Collection<String>? {
        val requiredArguments = arrayListOf<String>()
        requiredArguments.add(LISTS_CATLANG_PARAMETER_NAME)
        requiredArguments.addAll(super.getRequiredCatlangArgumentNames())
        return requiredArguments
    }

    override fun setParameters(context: Context, project: Project, scene: Scene, sprite: Sprite, arguments: Map<String, String>) {
        if (!arguments.containsKey(LISTS_CATLANG_PARAMETER_NAME)) {
            throw CatrobatLanguageParsingException("Missing required argument $LISTS_CATLANG_PARAMETER_NAME")
        }
        val lists = arguments[LISTS_CATLANG_PARAMETER_NAME]!!.split(",")
        val formattedListNames = lists.map { CatrobatLanguageUtils.getAndValidateListName(it.trim()) }
        formattedListNames.forEach {
            var list = project.getUserList(it)
            if (list == null) {
                list = sprite.getUserList(it)
                if (list == null) {
                    throw CatrobatLanguageParsingException("List $it not found in project or sprite")
                }
                userLists.add(list)
            } else {
                userLists.add(list)
            }
        }
    }
}
