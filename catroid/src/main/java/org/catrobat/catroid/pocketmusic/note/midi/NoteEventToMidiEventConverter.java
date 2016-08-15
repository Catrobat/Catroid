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
package org.catrobat.catroid.pocketmusic.note.midi;

import com.leff.midi.event.ChannelEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;

import org.catrobat.catroid.pocketmusic.note.NoteEvent;

public class NoteEventToMidiEventConverter {

	private static final int DEFAULT_NOISE = 64;
	private static final int DEFAULT_SILENT = 0;

	public ChannelEvent convertNoteEvent(long tick, NoteEvent noteEvent, int channel) {
		if (noteEvent.isNoteOn()) {
			return new NoteOn(tick, channel, noteEvent.getNoteName().getMidi(), DEFAULT_NOISE);
		} else {
			return new NoteOff(tick, channel, noteEvent.getNoteName().getMidi(), DEFAULT_SILENT);
		}
	}
}
