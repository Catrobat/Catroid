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
package org.catrobat.catroid.test.pocketmusic.note;

import android.test.AndroidTestCase;

import org.catrobat.catroid.pocketmusic.note.MusicalBeat;
import org.catrobat.catroid.pocketmusic.note.MusicalInstrument;
import org.catrobat.catroid.pocketmusic.note.Project;
import org.catrobat.catroid.pocketmusic.note.Track;

public class ProjectTest extends AndroidTestCase {

	public void testGetBeatsPerMinute() {
		int beatsPerMinute = 60;
		Project project = ProjectTestDataFactory.createProject(beatsPerMinute);

		assertEquals(beatsPerMinute, project.getBeatsPerMinute());
	}

	public void testGetName() {
		String name = "TestName";
		Project project = ProjectTestDataFactory.createProject(name);

		assertEquals(name, project.getName());
	}

	public void testSetName() {
		String name = "SomeNewName";
		Project project = new Project(ProjectTestDataFactory.createProject(), name);

		assertEquals(name, project.getName());
	}

	public void testAddTrack() {
		Project project = ProjectTestDataFactory.createProject();
		Track track = TrackTestDataFactory.createTrack();
		String trackName = "trackName";
		project.putTrack(trackName, track);

		assertEquals(1, project.size());
	}

	public void testGetTrack() {
		Project project = ProjectTestDataFactory.createProject();
		Track track = TrackTestDataFactory.createTrack();
		String trackName = "trackName";
		project.putTrack(trackName, track);

		assertEquals(track, project.getTrack(trackName));
	}

	public void testGetTrackNames() {
		Project project = ProjectTestDataFactory.createProject();
		Track track = TrackTestDataFactory.createTrack();
		project.putTrack("trackName", track);

		assertEquals(1, project.getTrackNames().size());
	}

	public void testGetTotalTimeInMilliseconds() {
		Project project = ProjectTestDataFactory.createProject();
		Track track = TrackTestDataFactory.createSimpleTrack();
		project.putTrack("trackName", track);

		assertEquals(track.getTotalTimeInMilliseconds(), project.getTotalTimeInMilliseconds());
	}

	public void testEquals1() {
		Project project1 = ProjectTestDataFactory.createProject();
		Project project2 = ProjectTestDataFactory.createProject();

		assertTrue(project1.equals(project2));
	}

	public void testEquals2() {
		Project project1 = ProjectTestDataFactory.createProjectWithTrack();
		Project project2 = ProjectTestDataFactory.createProjectWithTrack();

		assertTrue(project1.equals(project2));
	}

	public void testEquals3() {
		Project project1 = ProjectTestDataFactory.createProjectWithTrack();
		Project project2 = ProjectTestDataFactory.createProjectWithTrack(MusicalInstrument.APPLAUSE);

		assertFalse(project1.equals(project2));
	}

	public void testEquals4() {
		Project project1 = ProjectTestDataFactory.createProjectWithTrack();
		Project project2 = ProjectTestDataFactory.createProject();

		assertFalse(project1.equals(project2));
	}

	public void testEquals5() {
		Project project1 = ProjectTestDataFactory.createProject("Some name");
		Project project2 = ProjectTestDataFactory.createProject("Another name");

		assertFalse(project1.equals(project2));
	}

	public void testEquals6() {
		Project project1 = ProjectTestDataFactory.createProject(60);
		Project project2 = ProjectTestDataFactory.createProject(90);

		assertFalse(project1.equals(project2));
	}

	public void testEquals7() {
		Project project = ProjectTestDataFactory.createProject();

		assertFalse(project.equals(null));
	}

	public void testEquals8() {
		Project project = ProjectTestDataFactory.createProject();

		assertFalse(project.equals(""));
	}

	public void testEquals9() {
		Project project1 = ProjectTestDataFactory.createProjectWithMusicalBeat(MusicalBeat.BEAT_4_4);
		Project project2 = ProjectTestDataFactory.createProjectWithMusicalBeat(MusicalBeat.BEAT_16_16);

		assertFalse(project1.equals(project2));
	}

	public void testToString() {
		Project project = ProjectTestDataFactory.createProject();
		String expectedString = "[Project] name=" + project.getName() + " beatsPerMinute="
				+ project.getBeatsPerMinute() + " trackCount=" + project.size();

		assertEquals(expectedString, project.toString());
	}

	public void testCopyProject() {
		Project project = ProjectTestDataFactory.createProjectWithTrack();
		Project copyProject = new Project(project);

		assertTrue(project != copyProject);
		assertTrue(project.equals(copyProject));
	}
}
