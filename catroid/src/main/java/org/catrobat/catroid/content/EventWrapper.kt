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
import org.catrobat.catroid.content.eventids.EventId

class EventWrapper(internal val eventId: EventId, private val wait: Boolean) : Event() {
    private val spriteWaitList = mutableMapOf<Sprite, Int?>()

    fun notify(sprite: Sprite) {
        if (spriteWaitList.containsKey(sprite)) {
            spriteWaitList[sprite] = spriteWaitList[sprite]?.minus(1)
        }

        if (spriteWaitList[sprite] == 0) {
            spriteWaitList.remove(sprite)
        }
    }

    internal fun addSpriteToWaitList(sprite: Sprite) = wait.also {
        if (it) {
            if (!spriteWaitList.containsKey(sprite)) {
                spriteWaitList[sprite] = 1
            } else {
                spriteWaitList[sprite] = spriteWaitList[sprite]?.plus(1)
            }
        }
    }

    fun isWaitingForSprite() = spriteWaitList.isNotEmpty()
}
