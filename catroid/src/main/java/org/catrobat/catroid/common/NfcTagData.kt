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
package org.catrobat.catroid.common

import java.io.Serializable
import java.util.Objects

class NfcTagData : Cloneable, Comparable<NfcTagData>, Nameable, Serializable {
    private var name: String? = null
    var nfcTagUid: String? = null
    public override fun clone(): NfcTagData {
        val cloneNfcTagData = NfcTagData()
        cloneNfcTagData.name = name
        cloneNfcTagData.nfcTagUid = nfcTagUid
        return cloneNfcTagData
    }

    override fun getName(): String {
        return name!!
    }

    override fun setName(name: String) {
        this.name = name
    }

    override fun compareTo(nfcTagData: NfcTagData): Int {
        return nfcTagUid!!.compareTo(nfcTagData.nfcTagUid!!)
    }

    override fun toString(): String {
        return name!!
    }

    override fun equals(obj: Any?): Boolean {
        if (obj is NfcTagData) {
            return nfcTagUid == obj.nfcTagUid
        }
        return false
    }

    override fun hashCode(): Int {
        return Objects.hash(name, nfcTagUid)
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}