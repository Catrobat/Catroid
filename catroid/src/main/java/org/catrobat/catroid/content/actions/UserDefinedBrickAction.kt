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

package org.catrobat.catroid.content.actions

import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.eventids.UserDefinedBrickEventId
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.userbrick.UserDefinedBrickInput
import java.util.UUID

class UserDefinedBrickAction : SingleSpriteEventAction() {
    var scope: Scope? = null
        set(scope) {
            field = scope
            super.sprite = scope?.sprite
        }

    var userDefinedBrickID: UUID? = null
    var userDefinedBrickInputs: MutableList<UserDefinedBrickInput>? = null

    fun setInputs(userDefinedBrickInputs: MutableList<UserDefinedBrickInput>) {
        this.userDefinedBrickInputs = mutableListOf()
        userDefinedBrickInputs?.forEach {
            this.userDefinedBrickInputs?.add(UserDefinedBrickInput(it))
        }
    }

    private fun getInterpretedInputs(): MutableList<Any> {
        val interpretedInputs = mutableListOf<Any>()

        userDefinedBrickInputs?.forEach {
            val parameter = it.value.interpretObject(scope)
            interpretedInputs?.add(UserVariable(it.name, parameter))
        }
        return interpretedInputs
    }

    override fun getEventId() =
        userDefinedBrickID?.let { id ->
            UserDefinedBrickEventId(id, getInterpretedInputs())
        }
}
