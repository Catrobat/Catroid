/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserList

class AddItemToUserListAction : TemporalAction() {
    private var scope: Scope? = null
    private var formulaItemToAdd: Formula? = null
    private var userList: UserList? = null
    override fun update(percent: Float) {
        if (userList == null) {
            return
        }
        val value =
            if (formulaItemToAdd == null) java.lang.Double.valueOf(0.0) else formulaItemToAdd!!.interpretObject(
                scope
            )
        userList!!.addListItem(value)
    }

    fun setUserList(userVariable: UserList?) {
        userList = userVariable
    }

    fun setFormulaItemToAdd(changeVariable: Formula?) {
        formulaItemToAdd = changeVariable
    }

    fun setScope(scope: Scope?) {
        this.scope = scope
    }
}