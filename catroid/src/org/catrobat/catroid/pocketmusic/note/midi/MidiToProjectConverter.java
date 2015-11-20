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
package org.catrobat.catroid.pocketmusic.note.midi;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.ProgramChange;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.meta.Text;
import com.leff.midi.event.meta.TimeSignature;
import com.leff.midi.event.meta.TrackName;

import org.catrobat.catroid.pocketmusic.note.MusicalBeat;
import org.catrobat.catroid.pocketmusic.note.MusicalInstrument;
import org.catrobat.catroid.pocketmusic.note.MusicalKey;
import org.catrobat.catroid.pocketmusic.note.NoteEvent;
import org.catrobat.catroid.pocketmusic.note.NoteName;
import org.catrobat.catroid.pocketmusic.note.Project;
import org.catrobat.catroid.pocketmusic.note.Track;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MidiToProjectConverter {

	private static final MusicalInstrument DEFAULT_INSTRUMENT = MusicalInstrument.ACOUSTIC_GRAND_PIANO;

	private int beatsPerMinute;
	private MusicalBeat beat;
	private List<Track> tracks;
	private List<String> trackNames;

	public MidiToProjectConverter() {
		beatsPerMinute = Project.DEFAULT_BEATS_PER_MINUTE;
		beat = Project.DEFAULT_BEAT;
		tracks = new ArrayList<>();
		trackNames = new ArrayList<>();
	}

	public Project convertMidiFileToProject(File file) throws MidiException, IOException {
		MidiFile midi = new MidiFile(file);

		validateMidiFile(midi);

		String projectName = file.getName().split(ProjectToMidiConverter.MIDI_FILE_EXTENSION)[0];

		return convertMidi(projectName, midi);
	}

	private void validateMidiFile(MidiFile midiFile) throws MidiException {
		if (midiFile.getTrackCount() > 0) {
			MidiTrack tempoTrack = midiFile.getTracks().get(0);

			Iterator<MidiEvent> it = tempoTrack.getEvents().iterator();

			if (it.hasNext()) {
				MidiEvent event = it.next();

				if (event instanceof Text) {
					Text text = (Text) event;

					if (text.getText().equals(ProjectToMidiConverter.MIDI_FILE_IDENTIFIER)) {
						return;
					}
				}
			}
		}

		throw new MidiException("Unsupported MIDI!");
	}

	private Project convertMidi(String name, MidiFile midi) {
		for (MidiTrack midiTrack : midi.getTracks()) {
			createTrack(midiTrack);
		}

		Project project = new Project(name, beat, beatsPerMinute);

		int i = 0;

		for (Track track : tracks) {
			if (track.size() > 0) {
				String trackName = trackNames.get(i);
				i++;
				project.addTrack(trackName, track);
			}
		}

		return project;
	}

	private void createTrack(MidiTrack midiTrack) {
		MusicalInstrument instrument = getInstrumentFromMidiTrack(midiTrack);
		Track track = new Track(MusicalKey.VIOLIN, instrument);
		Iterator<MidiEvent> it = midiTrack.getEvents().iterator();

		while (it.hasNext()) {
			MidiEvent midiEvent = it.next();

			if (midiEvent instanceof TrackName) {
				TrackName trackNameEvent = (TrackName) midiEvent;
				trackNames.add(trackNameEvent.getTrackName());
			}
			if (midiEvent instanceof NoteOn) {
				NoteOn noteOn = (NoteOn) midiEvent;
				long tick = noteOn.getTick();
				NoteName noteName = NoteName.getNoteNameFromMidiValue(noteOn.getNoteValue());
				NoteEvent noteEvent = new NoteEvent(noteName, true);

				track.addNoteEvent(tick, noteEvent);
			} else if (midiEvent instanceof NoteOff) {
				NoteOff noteOff = (NoteOff) midiEvent;
				long tick = noteOff.getTick();
				NoteName noteName = NoteName.getNoteNameFromMidiValue(noteOff.getNoteValue());
				NoteEvent noteEvent = new NoteEvent(noteName, false);

				track.addNoteEvent(tick, noteEvent);
			} else if (midiEvent instanceof Tempo) {
				Tempo tempo = (Tempo) midiEvent;

				beatsPerMinute = (int) tempo.getBpm();
			} else if (midiEvent instanceof TimeSignature) {
				TimeSignature timeSignature = (TimeSignature) midiEvent;

				beat = MusicalBeat.convertToMusicalBeat(timeSignature.getNumerator(), timeSignature
						.getRealDenominator());
			}
		}

		tracks.add(track);
	}

	private MusicalInstrument getInstrumentFromMidiTrack(MidiTrack midiTrack) {
		Iterator<MidiEvent> it = midiTrack.getEvents().iterator();
		MusicalInstrument instrument = DEFAULT_INSTRUMENT;

		while (it.hasNext()) {
			MidiEvent midiEvent = it.next();

			if (midiEvent instanceof ProgramChange) {
				ProgramChange program = (ProgramChange) midiEvent;

				instrument = MusicalInstrument.getInstrumentFromProgram(program.getProgramNumber());
				break;
			}
		}

		return instrument;
	}
}
