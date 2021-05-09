/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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
package org.catrobat.catroid.content.actions

import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable

class ForItemInUserListAction : LoopAction() {
    private var userList: UserList? = null
    private var currentItemVariable: UserVariable? = null
    private var isCurrentLoopInitialized = false
    private var index = 0
    public override fun delegate(delta: Float): Boolean {
        if (!isCurrentLoopInitialized) {
            currentTime = 0f
            isCurrentLoopInitialized = true
        }
        if (userList == null) {
            return true
        }
        val list = userList!!.value
        if (list == null || index >= list.size) {
            return true
        }
        setCurrentItemVariable(list[index])
        currentTime = currentTime + delta
        if (action != null && action.act(delta) && !isLoopDelayNeeded()) {
            index++
            isCurrentLoopInitialized = false
            action.restart()
        }
        return false
    }

    override fun restart() {
        isCurrentLoopInitialized = false
        index = 0
        super.restart()
    }

    fun setUserList(userList: UserList?) {
        this.userList = userList
    }

    fun setCurrentItemVariable(variable: UserVariable?) {
        currentItemVariable = variable
    }

    private fun setCurrentItemVariable(listItem: Any) {
        currentItemVariable!!.value = listItem
    }
}