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
package org.catrobat.catroid.test.pocketmusic.note.midi;

import android.test.AndroidTestCase;

import org.catrobat.catroid.pocketmusic.note.Project;
import org.catrobat.catroid.pocketmusic.note.midi.MidiException;
import org.catrobat.catroid.pocketmusic.note.midi.MidiToProjectConverter;
import org.catrobat.catroid.pocketmusic.note.midi.ProjectToMidiConverter;
import org.catrobat.catroid.test.pocketmusic.note.ProjectTestDataFactory;

import java.io.File;
import java.io.IOException;

public class MidiToProjectConverterTest extends AndroidTestCase {

	private Project project;
	private File file;

	@Override
	protected void setUp() {
		project = ProjectTestDataFactory.createProjectWithSemiComplexTracks();
		file = new File(ProjectToMidiConverter.MIDI_FOLDER + File.separator + project.getName() + ProjectToMidiConverter.MIDI_FILE_EXTENSION);
	}

	@Override
	protected void tearDown() {
		file.delete();
	}

	public void testConvertToMidiToProject() throws MidiException, IOException {
		ProjectToMidiConverter projectConverter = new ProjectToMidiConverter();
		MidiToProjectConverter midiConverter = new MidiToProjectConverter();

		projectConverter.writeProjectAsMidi(project);
		Project actualProject = midiConverter.convertMidiFileToProject(file);

		assertEquals("Failed to convert midi to project", project, actualProject);
	}
}
