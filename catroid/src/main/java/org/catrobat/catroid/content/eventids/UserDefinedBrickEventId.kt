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
package org.catrobat.catroid.content.eventids

import java.util.UUID

class UserDefinedBrickEventId : EventId {
    val userDefinedBrickID: UUID?
    var userBrickParameters: List<Any>?

    constructor(
        userDefinedBrickID: UUID?,
        userBrickParameters: List<Any>?
    ) {
        this.userDefinedBrickID = userDefinedBrickID
        this.userBrickParameters = userBrickParameters
    }

    constructor(userDefinedBrickID: UUID?) {
        this.userDefinedBrickID = userDefinedBrickID
        userBrickParameters = null
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is UserDefinedBrickEventId) {
            return false
        }
        return userDefinedBrickID == o.userDefinedBrickID
    }

    override fun hashCode(): Int {
        return userDefinedBrickID?.hashCode() ?: 0
    }
}