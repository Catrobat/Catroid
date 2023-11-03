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


import org.catrobat.catroid.pocketmusic.note.Project;
import org.catrobat.catroid.pocketmusic.note.midi.MidiException;
import org.catrobat.catroid.pocketmusic.note.midi.ProjectToMidiConverter;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ProjectToMidiConverterTest {

	@Rule
	public TemporaryFolder temporaryMidiFolder = new TemporaryFolder();

	private static final String FILE_NAME = "ProjectToMidiConverterTest.midi";
	private File file;

	@Before
	public void setUp() {
		file = new File(ApplicationProvider.getApplicationContext().getCacheDir(), FILE_NAME);
	}

	@After
	public void tearDown() {
		file.delete();
	}

	@Test
	public void testWriteProjectAsMidi() throws IOException, MidiException {
		Project project = new Project("testWriteProjectAsMidi", Project.DEFAULT_BEAT, Project.DEFAULT_BEATS_PER_MINUTE);
		ProjectToMidiConverter converter = new ProjectToMidiConverter(temporaryMidiFolder.getRoot());

		converter.writeProjectAsMidi(project, file);

		assertTrue(file.exists());
	}

	@Test
	public void testGetMidiFileFromProjectName() throws IOException, MidiException {
		Project project = new Project("testGetMidiFileFromProjectName", Project.DEFAULT_BEAT, Project.DEFAULT_BEATS_PER_MINUTE);
		ProjectToMidiConverter converter = new ProjectToMidiConverter(temporaryMidiFolder.getRoot());

		converter.writeProjectAsMidi(project, file);
		File newFile = converter.getMidiFileFromProjectName(
				ProjectToMidiConverter.removeMidiExtensionFromString(file.getName()));

		assertEquals(file.getName(), newFile.getName());
	}
}
