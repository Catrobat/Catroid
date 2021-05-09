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

import android.util.Log
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.formulaeditor.common.Conversions.convertArgumentToDouble

class SetVariableAction : TemporalAction() {
    private var scope: Scope? = null
    private var changeVariable: Formula? = null
    private var userVariable: UserVariable? = null
    override fun update(percent: Float) {
        if (userVariable == null) {
            return
        }
        var value =
            if (changeVariable == null) java.lang.Double.valueOf(0.0) else changeVariable!!.interpretObject(
                scope
            )
        var isFirstLevelStringTree = false
        if (changeVariable != null && changeVariable!!.root.elementType == FormulaElement.ElementType.STRING) {
            isFirstLevelStringTree = true
        }
        try {
            if (!isFirstLevelStringTree && value is String && userVariable.hashCode() != Constants.TEXT_FROM_CAMERA_SENSOR_HASHCODE && convertArgumentToDouble(
                    value
                ) != null
            ) {
                value = convertArgumentToDouble(value)
            }
        } catch (numberFormatException: NumberFormatException) {
            Log.d(javaClass.simpleName, "Couldn't parse String", numberFormatException)
        }
        userVariable!!.value = value
    }

    fun setUserVariable(userVariable: UserVariable?) {
        if (userVariable == null) {
            return
        }
        this.userVariable = userVariable
    }

    fun setChangeVariable(changeVariable: Formula?) {
        this.changeVariable = changeVariable
    }

    fun setScope(scope: Scope?) {
        this.scope = scope
    }
}