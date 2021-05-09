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
import org.catrobat.catroid.bluetooth.base.BluetoothDevice
import org.catrobat.catroid.common.CatroidService
import org.catrobat.catroid.common.ServiceProvider
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException

class LegoNxtPlayToneAction : TemporalAction() {
    private var hertz: Formula? = null
    private var durationInSeconds: Formula? = null
    private var scope: Scope? = null
    private val btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE)
    override fun update(percent: Float) {
        var hertzInterpretation: Int
        var durationInterpretation: Float
        try {
            hertzInterpretation = hertz!!.interpretInteger(scope)
        } catch (interpretationException: InterpretationException) {
            hertzInterpretation = 0
            Log.d(
                javaClass.simpleName,
                "Formula interpretation for this specific Brick failed.",
                interpretationException
            )
        }
        try {
            durationInterpretation = durationInSeconds!!.interpretFloat(scope)
        } catch (interpretationException: InterpretationException) {
            durationInterpretation = 0f
            Log.d(
                javaClass.simpleName,
                "Formula interpretation for this specific Brick failed.",
                interpretationException
            )
        }
        val nxt = btService.getDevice(BluetoothDevice.LEGO_NXT)
            ?: return
        val durationInMs = (durationInterpretation * 1000).toInt()
        nxt.playTone(hertzInterpretation * 100, durationInMs)
    }

    fun setHertz(hertz: Formula?) {
        this.hertz = hertz
    }

    fun setDurationInSeconds(durationInSeconds: Formula?) {
        this.durationInSeconds = durationInSeconds
    }

    fun setScope(scope: Scope?) {
        this.scope = scope
    }
}