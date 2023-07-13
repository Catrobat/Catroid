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

package org.catrobat.catroid.content.bricks.brickspinner;

import android.content.Context;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;

import java.io.Serializable;

public enum PickableDrum implements Nameable, Serializable {
	SNARE_DRUM(R.string.snare_drum, 38),
	BASS_DRUM(R.string.bass_drum, 35),
	SIDE_STICK(R.string.side_stick, 37),
	CRASH_CYMBAL(R.string.crash_cymbal, 49),
	OPEN_HI_HAT(R.string.open_hi_hat, 46),
	CLOSED_HI_HAT(R.string.closed_hi_hat, 42),
	TAMBOURINE(R.string.tambourine, 54),
	HAND_CLAP(R.string.hand_clap, 39),
	CLAVES(R.string.claves, 75),
	WOOD_BLOCK(R.string.wood_block, 76),
	COWBELL(R.string.cowbell, 56),
	TRIANGLE(R.string.triangle, 81),
	BONGO(R.string.bongo, 60),
	CONGA(R.string.conga, 63),
	CABASA(R.string.cabasa, 69),
	GUIRO(R.string.guiro, 73),
	VIBRASLAP(R.string.vibraslap, 58),
	OPEN_CUICA(R.string.open_cuica, 79);

	private static final long serialVersionUID = 1L;

	private int nameStringId;
	private int value;
	private String name;

	PickableDrum(int nameStringId, int value) {
		this.nameStringId = nameStringId;
		this.value = value;
	}

	public static int getIndexByValue(int value) {
		int index = 0;
		for (PickableDrum drum : values()) {
			if (drum.getValue() == value) {
				return index;
			}
			index++;
		}
		return -1;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getString(Context context) {
		return context.getString(nameStringId);
	}
}
