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
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.devices.raspberrypi.RaspberryPiService
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException

class RaspiIfLogicAction : Action() {
    private var scope: Scope? = null
    private var ifAction: Action? = null
    private var elseAction: Action? = null
    private var isInitialized = false
    private var pinNumber: Formula? = null
    private var pin = 0
    fun setPinNumber(pinNumber: Formula?) {
        this.pinNumber = pinNumber
    }

    protected fun begin() {
        var pinNumberInterpretation: Int
        try {
            pinNumberInterpretation =
                if (pinNumber == null) Integer.valueOf(0) else pinNumber!!.interpretInteger(scope)
        } catch (interpretationException: InterpretationException) {
            pinNumberInterpretation = 0
            Log.e(
                TAG, "Formula interpretation for this specific Brick failed.",
                interpretationException
            )
        }
        pin = pinNumberInterpretation
    }

    override fun act(delta: Float): Boolean {
        if (!isInitialized) {
            begin()
            isInitialized = true
        }
        return if (readIfConditionValue()) {
            ifAction!!.act(delta)
        } else {
            elseAction!!.act(delta)
        }
    }

    protected fun readIfConditionValue(): Boolean {
        val connection = RaspberryPiService.getInstance().connection
        try {
            Log.d(TAG, "RPi get $pin")
            return connection.getPin(pin)
        } catch (e: Exception) {
            Log.e(TAG, "RPi: exception during getPin: $e")
        }
        return false
    }

    override fun restart() {
        ifAction!!.restart()
        elseAction!!.restart()
        isInitialized = false
        super.restart()
    }

    fun setScope(scope: Scope?) {
        this.scope = scope
    }

    fun setIfAction(ifAction: Action?) {
        this.ifAction = ifAction
    }

    fun setElseAction(elseAction: Action?) {
        this.elseAction = elseAction
    }

    override fun setActor(actor: Actor) {
        super.setActor(actor)
        ifAction!!.actor = actor
        elseAction!!.actor = actor
    }

    companion object {
        private val TAG = RaspiIfLogicAction::class.java.simpleName
    }
}