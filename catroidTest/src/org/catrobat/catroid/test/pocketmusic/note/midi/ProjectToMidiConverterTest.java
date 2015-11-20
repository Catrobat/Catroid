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
package org.catrobat.catroid.test.pocketmusic.note.midi;

import android.test.AndroidTestCase;

import org.catrobat.catroid.pocketmusic.note.Project;
import org.catrobat.catroid.pocketmusic.note.midi.MidiException;
import org.catrobat.catroid.pocketmusic.note.midi.ProjectToMidiConverter;
import org.catrobat.catroid.test.pocketmusic.note.ProjectTestDataFactory;

import java.io.File;
import java.io.IOException;

public class ProjectToMidiConverterTest extends AndroidTestCase {

	private static final String FILE_NAME = "ProjectToMidiConverterTest.midi";
	private File file;

	@Override
	protected void setUp() {
		file = new File(getContext().getCacheDir(), FILE_NAME);
	}

	@Override
	protected void tearDown() {
		file.delete();
	}

	public void testWriteProjectAsMidi() throws IOException, MidiException {
		Project project = ProjectTestDataFactory.createProject();
		ProjectToMidiConverter converter = new ProjectToMidiConverter();

		converter.writeProjectAsMidi(project, file);

		assertTrue("File not successfully written", file.exists());
	}

	public void testGetMidiFileFromProjectName() throws IOException, MidiException {
		Project project = ProjectTestDataFactory.createProject();
		ProjectToMidiConverter converter = new ProjectToMidiConverter();

		converter.writeProjectAsMidi(project, file);
		File newFile = ProjectToMidiConverter.getMidiFileFromProjectName(ProjectToMidiConverter.removeMidiExtensionFromString(file.getName()));

		assertEquals("Error while reading midi file from project name", file.getName(), newFile.getName());
	}

	public void testDeleteMidiByName() throws IOException, MidiException {
		Project project = ProjectTestDataFactory.createProject();
		ProjectToMidiConverter converter = new ProjectToMidiConverter();
		converter.writeProjectAsMidi(project);
		if (converter.deleteMidiByName(ProjectToMidiConverter.removeMidiExtensionFromString(project.getName()))) {
			assertFalse("Midi is in storage", ProjectTestDataFactory.checkIfProjectInStorage(project.getName()));
		} else {
			assertTrue("Midi is in storage", ProjectTestDataFactory.checkIfProjectInStorage(project.getName()));
		}
	}
}
