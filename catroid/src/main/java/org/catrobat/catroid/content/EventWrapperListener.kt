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
package org.catrobat.catroid.content

import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.content.actions.ScriptSequenceActionWithWaiter
import org.catrobat.catroid.content.eventids.UserDefinedBrickEventId

class EventWrapperListener internal constructor(private val look: Look) : EventListener {

    override fun handle(event: Event) =
        if (event is EventWrapper) {
            handleEvent(event)
            true
        } else false

    private fun handleEvent(event: EventWrapper) {
        with(look) {
            sprite.getIdToEventThreadMap(event.eventId).forEach { sequenceAction ->
                if (event.eventId is UserDefinedBrickEventId) {
                    handleUserBrickEvent(sequenceAction, event)
                    return
                }
                stopThreadWithScript(sequenceAction.script)
                if (event.addSpriteToWaitList(sprite)) {
                    startThread(ScriptSequenceActionWithWaiter(sequenceAction, event, sprite))
                } else {
                    startThread(sequenceAction)
                }
            }
        }
    }

    private fun handleUserBrickEvent(sequenceAction: ScriptSequenceAction, event: EventWrapper) {
        with(look) {
            val scriptClone = (sequenceAction.script as UserDefinedScript).clone() as
                UserDefinedScript
            scriptClone.setUserDefinedBrickInputs((event.eventId as
                UserDefinedBrickEventId).userBrickParameters)
            val sequenceClone: ScriptSequenceAction = sequenceAction.cloneAndChangeScript(scriptClone)
            sequenceClone.script.run(sprite, sequenceClone)
            event.addSpriteToWaitList(sprite)
            startThread(ScriptSequenceActionWithWaiter(sequenceClone, event, sprite))
        }
    }
}
