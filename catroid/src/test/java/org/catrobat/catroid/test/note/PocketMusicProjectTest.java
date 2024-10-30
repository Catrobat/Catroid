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
package org.catrobat.catroid.test.note;

import org.catrobat.catroid.pocketmusic.note.MusicalBeat;
import org.catrobat.catroid.pocketmusic.note.MusicalInstrument;
import org.catrobat.catroid.pocketmusic.note.MusicalKey;
import org.catrobat.catroid.pocketmusic.note.Project;
import org.catrobat.catroid.pocketmusic.note.Track;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertNotNull;

import static org.junit.Assert.assertNotEquals;

@RunWith(JUnit4.class)
public class PocketMusicProjectTest {

	@Test
	public void testGetBeatsPerMinute() {
		int beatsPerMinute = 60;
		Project project = new Project("testGetBeatsPerMinute", Project.DEFAULT_BEAT, 60);

		assertEquals(beatsPerMinute, project.getBeatsPerMinute());
	}

	@Test
	public void testGetName() {
		String name = "testGetName";
		Project project = new Project("testGetName", Project.DEFAULT_BEAT, Project.DEFAULT_BEATS_PER_MINUTE);

		assertEquals(name, project.getName());
	}

	@Test
	public void testSetName() {
		String name = "SomeNewName";
		Project otherProject = new Project("testSetName", Project.DEFAULT_BEAT, Project.DEFAULT_BEATS_PER_MINUTE);
		Project project = new Project(otherProject, name);

		assertEquals(name, project.getName());
	}

	@Test
	public void testAddTrack() {
		Project project = new Project("testAddTrack", Project.DEFAULT_BEAT, Project.DEFAULT_BEATS_PER_MINUTE);
		Track track = new Track(MusicalKey.VIOLIN, MusicalInstrument.ACOUSTIC_GRAND_PIANO);
		String trackName = "trackName";
		project.putTrack(trackName, track);

		assertEquals(1, project.size());
	}

	@Test
	public void testGetTrack() {
		Project project = new Project("testGetTrack", Project.DEFAULT_BEAT, Project.DEFAULT_BEATS_PER_MINUTE);
		Track track = new Track(MusicalKey.VIOLIN, MusicalInstrument.ACOUSTIC_GRAND_PIANO);
		String trackName = "trackName";
		project.putTrack(trackName, track);

		assertEquals(track, project.getTrack(trackName));
	}

	@Test
	public void testGetTrackNames() {
		Project project = new Project("testGetTrackNames", Project.DEFAULT_BEAT, Project.DEFAULT_BEATS_PER_MINUTE);
		Track track = new Track(MusicalKey.VIOLIN, MusicalInstrument.ACOUSTIC_GRAND_PIANO);
		project.putTrack("trackName", track);

		assertEquals(1, project.getTrackNames().size());
	}

	@Test
	public void testGetTotalTimeInMilliseconds() {
		Project project = new Project("testGetTotalTimeInMilliseconds", Project.DEFAULT_BEAT, Project.DEFAULT_BEATS_PER_MINUTE);
		Track track = TrackTestDataFactory.createSimpleTrack();
		project.putTrack("trackName", track);

		assertEquals(track.getTotalTimeInMilliseconds(), project.getTotalTimeInMilliseconds());
	}

	@Test
	public void testEquals1() {
		Project project1 = new Project("testEquals1Project", Project.DEFAULT_BEAT, Project.DEFAULT_BEATS_PER_MINUTE);
		Project project2 = new Project("testEquals1Project", Project.DEFAULT_BEAT, Project.DEFAULT_BEATS_PER_MINUTE);

		assertEquals(project1, project2);
	}

	@Test
	public void testEquals2() {
		Project project1 = createProjectWithTrack(MusicalInstrument.ACOUSTIC_GRAND_PIANO);
		Project project2 = createProjectWithTrack(MusicalInstrument.ACOUSTIC_GRAND_PIANO);

		assertEquals(project1, project2);
	}

	@Test
	public void testEquals3() {
		Project project1 = createProjectWithTrack(MusicalInstrument.ACOUSTIC_GRAND_PIANO);
		Project project2 = createProjectWithTrack(MusicalInstrument.APPLAUSE);

		assertNotEquals(project2, project1);
	}

	@Test
	public void testEquals4() {
		Project project1 = createProjectWithTrack(MusicalInstrument.ACOUSTIC_GRAND_PIANO);
		Project project2 = new Project("testEquals4Project2", Project.DEFAULT_BEAT, Project.DEFAULT_BEATS_PER_MINUTE);

		assertNotEquals(project2, project1);
	}

	@Test
	public void testEquals5() {
		Project project1 = new Project("testEquals5Project1", Project.DEFAULT_BEAT, Project.DEFAULT_BEATS_PER_MINUTE);
		Project project2 = new Project("testEquals5Project2", Project.DEFAULT_BEAT, Project.DEFAULT_BEATS_PER_MINUTE);

		assertNotEquals(project2, project1);
	}

	@Test
	public void testEquals6() {
		Project project1 = new Project("testEquals6", Project.DEFAULT_BEAT, 60);
		Project project2 = new Project("testEquals6", Project.DEFAULT_BEAT, 90);

		assertNotEquals(project2, project1);
	}

	@Test
	public void testEquals7() {
		Project project = new Project("testEquals7", Project.DEFAULT_BEAT, Project.DEFAULT_BEATS_PER_MINUTE);

		assertNotNull(project);
	}

	@Test
	public void testEquals8() {
		Project project = new Project("testEquals8", Project.DEFAULT_BEAT, Project.DEFAULT_BEATS_PER_MINUTE);

		assertNotEquals("", project);
	}

	@Test
	public void testEquals9() {
		Project project1 = new Project("testEquals9Project1", MusicalBeat.BEAT_4_4, Project.DEFAULT_BEATS_PER_MINUTE);
		Project project2 = new Project("testEquals9Project2", MusicalBeat.BEAT_16_16, Project.DEFAULT_BEATS_PER_MINUTE);

		assertNotEquals(project2, project1);
	}

	@Test
	public void testToString() {
		Project project = new Project("testToString", Project.DEFAULT_BEAT, Project.DEFAULT_BEATS_PER_MINUTE);
		String expectedString = "[Project] name=" + project.getName() + " beatsPerMinute="
				+ project.getBeatsPerMinute() + " trackCount=" + project.size();

		assertEquals(expectedString, project.toString());
	}

	@Test
	public void testCopyProject() {
		Project project = createProjectWithTrack(MusicalInstrument.ACOUSTIC_GRAND_PIANO);
		Project copyProject = new Project(project);

		assertNotSame(project, copyProject);
		assertEquals(project, copyProject);
	}

	public Project createProjectWithTrack(MusicalInstrument instrument) {
		Project project = new Project("TestProjectWithTrack", Project.DEFAULT_BEAT, Project.DEFAULT_BEATS_PER_MINUTE);
		Track track = new Track(MusicalKey.VIOLIN, instrument);
		project.putTrack("someRandomTrackName1", track);

		return project;
	}
}
