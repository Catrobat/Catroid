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

import org.catrobat.catroid.content.EventWrapper
import org.catrobat.catroid.content.Sprite

class ScriptSequenceActionWithWaiter(
    thread: ScriptSequenceAction,
    event: EventWrapper,
    sprite: Sprite
) : ScriptSequenceAction(thread.script) {

    private data class Waiter(val event: EventWrapper, val sprite: Sprite)
    private val waiter = Waiter(event, sprite)

    init {
        thread.actions.forEach { action ->
            addAction(action)
        }
    }

    fun notifyWaiter() = waiter.event.notify(waiter.sprite)

    override fun act(delta: Float) = super.act(delta).also { finished ->
        if (finished) notifyWaiter()
    }
}
