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
package org.catrobat.catroid.pocketmusic.note;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import androidx.collection.LongSparseArray;

public class Track implements Serializable {

	private static final long serialVersionUID = 7483021689872527955L;

	private MusicalInstrument instrument;
	private LongSparseArray<List<NoteEvent>> events;
	private MusicalKey key;
	private long lastTick;

	public Track(MusicalKey key, MusicalInstrument instrument) {
		this.events = new LongSparseArray<>();
		this.instrument = instrument;
		this.key = key;
		this.lastTick = 0;
	}

	public Track(Track track) {
		this.events = new LongSparseArray<>();
		this.instrument = track.getInstrument();
		this.key = track.getKey();
		this.lastTick = track.getLastTick();

		for (long tick : track.getSortedTicks()) {
			List<NoteEvent> noteEventList = new LinkedList<>();
			this.events.put(tick, noteEventList);

			for (NoteEvent noteEvent : track.getNoteEventsForTick(tick)) {
				noteEventList.add(new NoteEvent(noteEvent));
			}
		}
	}

	public MusicalInstrument getInstrument() {
		return instrument;
	}

	public MusicalKey getKey() {
		return key;
	}

	public void addNoteEvent(long tick, NoteEvent noteEvent) {
		List<NoteEvent> noteEventList;

		if (events.get(tick) != null) {
			noteEventList = events.get(tick);
		} else {
			noteEventList = new LinkedList<>();
			events.put(tick, noteEventList);
		}

		if (!noteEvent.isNoteOn()) {
			lastTick = tick;
		}

		if (!eventListAlreadyContainsNoteEventWithTick(tick, noteEvent)) {
			noteEventList.add(noteEvent);
		}
	}

	private boolean eventListAlreadyContainsNoteEventWithTick(long tick, NoteEvent noteEvent) {
		for (int i = 0; i < events.size(); i++) {
			long key = events.keyAt(i);
			if (key == tick && events.get(key).contains(noteEvent)) {
				return true;
			}
		}
		return false;
	}

	public List<NoteEvent> getNoteEventsForTick(long tick) {
		List<NoteEvent> noteEvents = events.get(tick);

		Collections.sort(noteEvents, new Comparator<NoteEvent>() {
			@Override
			public int compare(NoteEvent noteEvent1, NoteEvent noteEvent2) {
				if (noteEvent1.isNoteOn() == noteEvent2.isNoteOn()) {
					return 0;
				} else if (noteEvent1.isNoteOn()) {
					return 1;
				} else {
					return -1;
				}
			}
		});

		return noteEvents;
	}

	public Set<Long> getSortedTicks() {
		Set<Long> treeSet = new TreeSet<>();
		for (int i = 0; i < events.size(); i++) {
			treeSet.add(events.keyAt(i));
		}
		return treeSet;
	}

	public int size() {
		int size = 0;

		for (Long sortedTick : getSortedTicks()) {
			size += events.get(sortedTick).size();
		}

		return size;
	}

	public long getLastTick() {
		return lastTick;
	}

	public long getTotalTimeInMilliseconds() {
		return NoteLength.tickToMilliseconds(lastTick);
	}

	public boolean empty() {
		return (0 == size());
	}

	@Override
	public int hashCode() {
		int primeWithGoodCollisionPrevention = 31;
		int hashCode = 18;
		hashCode = primeWithGoodCollisionPrevention * hashCode + instrument.hashCode();
		hashCode = primeWithGoodCollisionPrevention * hashCode + events.hashCode();
		hashCode = primeWithGoodCollisionPrevention * hashCode + key.hashCode();
		hashCode = primeWithGoodCollisionPrevention * hashCode + (int) lastTick;
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj == null) || !(obj instanceof Track)) {
			return false;
		}

		Track track = (Track) obj;

		if (track.getInstrument() != getInstrument()) {
			return false;
		}

		if (track.getKey() != getKey()) {
			return false;
		}

		Set<Long> ownTrackTicks = getSortedTicks();
		Set<Long> otherTrackTicks = track.getSortedTicks();

		if (otherTrackTicks.equals(ownTrackTicks)) {
			for (long tick : ownTrackTicks) {
				List<NoteEvent> ownNoteEventList = getNoteEventsForTick(tick);
				List<NoteEvent> otherNoteEventList = track.getNoteEventsForTick(tick);

				if (!ownNoteEventList.equals(otherNoteEventList)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "[Track] instrument=" + instrument + " key=" + key + " size=" + size();
	}

	public boolean isEmpty() {
		return size() == 0;
	}
}
