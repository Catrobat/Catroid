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
package org.catrobat.catroid.test.pocketmusic.note.midi;

import org.catrobat.catroid.pocketmusic.note.MusicalInstrument;
import org.catrobat.catroid.pocketmusic.note.Project;
import org.catrobat.catroid.pocketmusic.note.Track;
import org.catrobat.catroid.pocketmusic.note.midi.MidiException;
import org.catrobat.catroid.pocketmusic.note.midi.MidiToProjectConverter;
import org.catrobat.catroid.pocketmusic.note.midi.ProjectToMidiConverter;
import org.catrobat.catroid.test.pocketmusic.note.TrackTestDataFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class MidiToProjectConverterTest {

	@Rule
	public TemporaryFolder temporaryMidiFolder = new TemporaryFolder();
	private Project project;
	private File file;

	@Before
	public void setUp() {
		project = createProjectWithSemiComplexTracks();
		file = new File(temporaryMidiFolder.getRoot() + File.separator + project.getName() + ProjectToMidiConverter.MIDI_FILE_EXTENSION);
	}

	@After
	public void tearDown() {
		file.delete();
	}

	@Test
	public void testConvertToMidiToProject() throws MidiException, IOException {
		ProjectToMidiConverter projectConverter = new ProjectToMidiConverter(temporaryMidiFolder.getRoot());
		MidiToProjectConverter midiConverter = new MidiToProjectConverter();

		projectConverter.writeProjectAsMidi(project);
		Project actualProject = midiConverter.convertMidiFileToProject(file);

		assertEquals(project, actualProject);
	}

	public static Project createProjectWithSemiComplexTracks() {
		Project project = new Project("MidiToProjectConverterTest", Project.DEFAULT_BEAT, Project.DEFAULT_BEATS_PER_MINUTE);
		Track track1 = TrackTestDataFactory.createSemiComplexTrack(MusicalInstrument.GUNSHOT);
		Track track2 = TrackTestDataFactory.createSemiComplexTrack(MusicalInstrument.WHISTLE);

		project.putTrack("someRandomTrackName1", track1);
		project.putTrack("someRandomTrackName2", track2);

		return project;
	}
}
