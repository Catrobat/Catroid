/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

package org.catrobat.catroid.common;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public enum ScratchVisibilityState implements Parcelable {
	// NOTE: do not change values!
	UNKNOWN(0),
	PRIVATE(1),
	PUBLIC(2);

	private int visibilityState;

	private static Map<Integer, ScratchVisibilityState> map = new HashMap<>();
	static {
		for (ScratchVisibilityState legEnum : ScratchVisibilityState.values()) {
			map.put(legEnum.visibilityState, legEnum);
		}
	}
	ScratchVisibilityState(final int visibilityState) {
		this.visibilityState = visibilityState;
	}

	public static ScratchVisibilityState valueOf(int visibilityState) {
		return map.get(visibilityState);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeInt(ordinal());
	}

	public static final Creator<ScratchVisibilityState> CREATOR = new Creator<ScratchVisibilityState>() {
		@Override
		public ScratchVisibilityState createFromParcel(final Parcel source) {
			return ScratchVisibilityState.values()[source.readInt()];
		}

		@Override
		public ScratchVisibilityState[] newArray(final int size) {
			return new ScratchVisibilityState[size];
		}
	};
}
