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
import org.catrobat.catroid.formulaeditor.InterpretationException
import org.catrobat.catroid.formulaeditor.UserList
import java.util.ArrayList

class ReplaceItemInUserListAction : TemporalAction() {
    private var scope: Scope? = null
    private var formulaIndexToReplace: Formula? = null
    private var formulaItemToInsert: Formula? = null
    private var userList: UserList? = null
    override fun update(percent: Float) {
        if (userList == null) {
            return
        }
        val value =
            if (formulaItemToInsert == null) java.lang.Double.valueOf(0.0) else formulaItemToInsert!!.interpretObject(
                scope
            )
        var indexToReplace: Int
        indexToReplace = try {
            if (formulaIndexToReplace == null) 1 else formulaIndexToReplace!!.interpretInteger(scope)
        } catch (interpretationException: InterpretationException) {
            1
        }
        indexToReplace--
        if (indexToReplace >= userList!!.value.size || indexToReplace < 0) {
            return
        }
        (userList!!.value as ArrayList<Any?>)[indexToReplace] = value
    }

    fun setUserList(userVariable: UserList?) {
        userList = userVariable
    }

    fun setFormulaIndexToReplace(formulaIndexToReplace: Formula?) {
        this.formulaIndexToReplace = formulaIndexToReplace
    }

    fun setFormulaItemToInsert(formulaItemToInsert: Formula?) {
        this.formulaItemToInsert = formulaItemToInsert
    }

    fun setScope(scope: Scope?) {
        this.scope = scope
    }
}