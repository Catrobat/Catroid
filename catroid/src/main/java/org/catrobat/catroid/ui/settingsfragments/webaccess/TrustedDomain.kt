/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

package org.catrobat.catroid.ui.settingsfragments.webaccess

import org.catrobat.catroid.common.Nameable

class TrustedDomain(private var domain: String) : Nameable {
    override fun getName(): String {
        return domain
    }

    override fun setName(name: String) {
        this.domain = name
    }

    override fun equals(other: Any?): Boolean {
        return if (other is TrustedDomain) {
            domain == other.domain
        } else false
    }

    override fun hashCode(): Int {
        return domain.hashCode()
    }
}
