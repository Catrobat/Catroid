/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
package org.catrobat.catroid.pocketmusic.note;

public enum MusicalBeat {
	BEAT_3_4(3, 4, NoteLength.QUARTER), BEAT_4_4(4, 4, NoteLength.QUARTER), BEAT_16_16(16, 16, NoteLength.SIXTEENTH);

	private final int topNumber;
	private final int bottomNumber;
	private final NoteLength noteLength;

	private MusicalBeat(int topNumnber, int bottomNumber, NoteLength noteLength) {
		this.topNumber = topNumnber;
		this.bottomNumber = bottomNumber;
		this.noteLength = noteLength;
	}

	public int getTopNumber() {
		return topNumber;
	}

	public int getBottomNumber() {
		return bottomNumber;
	}

	public NoteLength getNoteLength() {
		return noteLength;
	}

	public static MusicalBeat convertToMusicalBeat(int topNumber, int bottomNumber) {
		for (MusicalBeat beat : MusicalBeat.values()) {
			if (beat.getTopNumber() == topNumber && beat.getBottomNumber() == bottomNumber) {
				return beat;
			}
		}

		return MusicalBeat.BEAT_4_4;
	}
}
