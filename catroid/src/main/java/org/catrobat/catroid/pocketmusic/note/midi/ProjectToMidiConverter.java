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
package org.catrobat.catroid.pocketmusic.note.midi;

import android.content.Context;

import com.pdrogfer.mididroid.MidiFile;
import com.pdrogfer.mididroid.MidiTrack;
import com.pdrogfer.mididroid.event.ChannelEvent;
import com.pdrogfer.mididroid.event.ProgramChange;
import com.pdrogfer.mididroid.event.meta.Tempo;
import com.pdrogfer.mididroid.event.meta.Text;
import com.pdrogfer.mididroid.event.meta.TimeSignature;
import com.pdrogfer.mididroid.event.meta.TrackName;

import org.catrobat.catroid.pocketmusic.note.MusicalBeat;
import org.catrobat.catroid.pocketmusic.note.NoteEvent;
import org.catrobat.catroid.pocketmusic.note.Project;
import org.catrobat.catroid.pocketmusic.note.Track;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProjectToMidiConverter {

	public static final String MIDI_FILE_EXTENSION = ".midi";
	public static final String MIDI_FILE_IDENTIFIER = "Musicdroid Midi File";
	private static final int MAX_CHANNEL = 16;
	private final File midiFolder;
	private NoteEventToMidiEventConverter eventConverter;
	private int nextChannel;

	public ProjectToMidiConverter(@NotNull File midiFolder) {
		this.midiFolder = midiFolder;
		eventConverter = new NoteEventToMidiEventConverter();
		nextChannel = 0;
	}

	public void writeProjectAsMidi(Project project) throws IOException, MidiException {
		MidiFile midiFile = convertProject(project);

		checkMidiFolder();

		midiFile.writeToFile(getMidiFileFromProjectName(project.getName()));
	}

	private void checkMidiFolder() throws IOException {
		if (!midiFolder.exists()) {
			boolean success = midiFolder.mkdir();

			if (!success) {
				throw new IOException("Could not create folder: " + midiFolder);
			}
		}
	}

	public File getMidiFileFromProjectName(String name) throws IOException {
		checkMidiFolder();
		return new File(midiFolder + File.separator + name + MIDI_FILE_EXTENSION);
	}

	public static String removeMidiExtensionFromString(String input) {
		return input.split(MIDI_FILE_EXTENSION)[0];
	}

	public void writeProjectAsMidi(Project project, File file) throws IOException, MidiException {
		MidiFile midi = convertProject(project);
		midi.writeToFile(file);
	}

	private MidiFile convertProject(Project project) throws MidiException {
		for (String trackName : project.getTrackNames()) {
			Track track = project.getTrack(trackName);

			if (0 == track.size()) {
				throw new MidiException("Cannot save a project with an empty track!");
			}
		}

		ArrayList<MidiTrack> tracks = new ArrayList<MidiTrack>();

		MidiTrack tempoTrack = createTempoTrackWithMetaInfo(project.getBeat(), project.getBeatsPerMinute());
		tracks.add(tempoTrack);

		for (String trackName : project.getTrackNames()) {
			Track track = project.getTrack(trackName);
			int channel = getNextChannel();

			MidiTrack noteTrack = createNoteTrack(trackName, track, channel);

			tracks.add(noteTrack);
		}

		return new MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks);
	}

	private int getNextChannel() throws MidiException {
		if (nextChannel >= MAX_CHANNEL) {
			throw new MidiException("You cannot have more than " + MAX_CHANNEL + " channels!");
		}

		return nextChannel++;
	}

	private MidiTrack createTempoTrackWithMetaInfo(MusicalBeat beat, int beatsPerMinute) {
		MidiTrack tempoTrack = new MidiTrack();

		Text text = new Text(0, 0, MIDI_FILE_IDENTIFIER);
		tempoTrack.insertEvent(text);

		Tempo tempo = new Tempo();
		tempo.setBpm(beatsPerMinute);
		tempoTrack.insertEvent(tempo);

		TimeSignature timeSignature = new TimeSignature();
		timeSignature.setTimeSignature(beat.getTopNumber(), beat.getBottomNumber(), TimeSignature.DEFAULT_METER,
				TimeSignature
						.DEFAULT_DIVISION);
		tempoTrack.insertEvent(timeSignature);

		return tempoTrack;
	}

	private MidiTrack createNoteTrack(String trackName, Track track, int channel) throws MidiException {
		MidiTrack noteTrack = new MidiTrack();

		TrackName trackNameEvent = new TrackName(0, channel, trackName);
		noteTrack.insertEvent(trackNameEvent);
		ProgramChange program = new ProgramChange(0, channel, track.getInstrument().getProgram());
		noteTrack.insertEvent(program);

		for (long tick : track.getSortedTicks()) {
			List<NoteEvent> noteEventList = track.getNoteEventsForTick(tick);

			for (NoteEvent noteEvent : noteEventList) {
				ChannelEvent channelEvent = eventConverter.convertNoteEvent(tick, noteEvent, channel);
				noteTrack.insertEvent(channelEvent);
			}
		}

		return noteTrack;
	}
}
