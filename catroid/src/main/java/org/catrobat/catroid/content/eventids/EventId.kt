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
package org.catrobat.catroid.content.eventids

import androidx.annotation.IntDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

open class EventId {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(TAP, TAP_BACKGROUND, START, START_AS_CLONE, ANY_NFC, OTHER)
    annotation class EventType

    @EventType
    private val type: Int

    constructor(@EventType type: Int) {
        this.type = type
    }

    protected constructor() {
        type = OTHER
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is EventId) {
            return false
        }
        return type == o.type
    }

    override fun hashCode(): Int {
        return type
    }

    companion object {
        const val OTHER = 0
        const val TAP = 1
        const val TAP_BACKGROUND = 2
        const val START = 3
        const val START_AS_CLONE = 4
        const val ANY_NFC = 5
    }
}