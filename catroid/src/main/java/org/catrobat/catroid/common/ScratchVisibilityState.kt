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

import android.os.Parcel
import android.os.Parcelable
import android.util.SparseArray

enum class ScratchVisibilityState(private val visibilityState: Int) : Parcelable {
    // NOTE: do not change values!
    UNKNOWN(0), PRIVATE(1), PUBLIC(2);

    companion object {
        private val visibilityStates = SparseArray<ScratchVisibilityState>()
        @JvmStatic
		fun valueOf(visibilityState: Int): ScratchVisibilityState {
            return visibilityStates[visibilityState]
        }

        @JvmField val CREATOR: Parcelable.Creator<ScratchVisibilityState?> =
            object : Parcelable.Creator<ScratchVisibilityState?> {
                override fun createFromParcel(source: Parcel): ScratchVisibilityState? {
                    return values()[source.readInt()]
                }

                override fun newArray(size: Int): Array<ScratchVisibilityState?> {
                    return arrayOfNulls(size)
                }
            }

        init {
            for (legEnum in values()) {
                visibilityStates.put(
                    legEnum.visibilityState,
                    legEnum
                )
            }
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(ordinal)
    }
}