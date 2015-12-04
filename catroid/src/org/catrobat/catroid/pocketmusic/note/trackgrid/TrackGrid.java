/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.pocketmusic.note.trackgrid;

import org.catrobat.catroid.pocketmusic.note.MusicalBeat;
import org.catrobat.catroid.pocketmusic.note.MusicalInstrument;
import org.catrobat.catroid.pocketmusic.note.MusicalKey;

import java.util.List;

public class TrackGrid {

	private final MusicalKey key;
	private final MusicalInstrument instrument;
	private final MusicalBeat beat;
	private final List<GridRow> gridRows;

	public TrackGrid(MusicalKey key, MusicalInstrument instrument, MusicalBeat beat, List<GridRow> gridRows) {
		this.key = key;
		this.instrument = instrument;
		this.beat = beat;
		this.gridRows = gridRows;
	}

	public MusicalKey getKey() {
		return key;
	}

	public MusicalInstrument getInstrument() {
		return instrument;
	}

	public MusicalBeat getBeat() {
		return beat;
	}

	public List<GridRow> getGridRows() {
		return gridRows;
	}

	@Override
	public int hashCode() {
		int result = 172;
		result = 31 * result + key.hashCode();
		result = 31 * result + instrument.hashCode();
		result = 31 * result + beat.hashCode();
		result = 31 * result + gridRows.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object o) {
		TrackGrid reference = (TrackGrid) o;
		return reference.gridRows.containsAll(gridRows)
				&& gridRows.containsAll(reference.gridRows)
				&& reference.beat.equals(beat)
				&& reference.instrument.equals(instrument)
				&& reference.key.equals(key);
	}
}
