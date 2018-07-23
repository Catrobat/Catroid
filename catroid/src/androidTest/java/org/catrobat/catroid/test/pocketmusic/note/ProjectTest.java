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

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.pocketmusic.note.MusicalBeat;
import org.catrobat.catroid.pocketmusic.note.MusicalInstrument;
import org.catrobat.catroid.pocketmusic.note.Project;
import org.catrobat.catroid.pocketmusic.note.Track;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotSame;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class ProjectTest {

	@Test
	public void testGetBeatsPerMinute() {
		int beatsPerMinute = 60;
		Project project = ProjectTestDataFactory.createProject(beatsPerMinute);

		assertEquals(beatsPerMinute, project.getBeatsPerMinute());
	}

	@Test
	public void testGetName() {
		String name = "TestName";
		Project project = ProjectTestDataFactory.createProject(name);

		assertEquals(name, project.getName());
	}

	@Test
	public void testSetName() {
		String name = "SomeNewName";
		Project project = new Project(ProjectTestDataFactory.createProject(), name);

		assertEquals(name, project.getName());
	}

	@Test
	public void testAddTrack() {
		Project project = ProjectTestDataFactory.createProject();
		Track track = TrackTestDataFactory.createTrack();
		String trackName = "trackName";
		project.putTrack(trackName, track);

		assertEquals(1, project.size());
	}

	@Test
	public void testGetTrack() {
		Project project = ProjectTestDataFactory.createProject();
		Track track = TrackTestDataFactory.createTrack();
		String trackName = "trackName";
		project.putTrack(trackName, track);

		assertEquals(track, project.getTrack(trackName));
	}

	@Test
	public void testGetTrackNames() {
		Project project = ProjectTestDataFactory.createProject();
		Track track = TrackTestDataFactory.createTrack();
		project.putTrack("trackName", track);

		assertEquals(1, project.getTrackNames().size());
	}

	@Test
	public void testGetTotalTimeInMilliseconds() {
		Project project = ProjectTestDataFactory.createProject();
		Track track = TrackTestDataFactory.createSimpleTrack();
		project.putTrack("trackName", track);

		assertEquals(track.getTotalTimeInMilliseconds(), project.getTotalTimeInMilliseconds());
	}

	@Test
	public void testEquals1() {
		Project project1 = ProjectTestDataFactory.createProject();
		Project project2 = ProjectTestDataFactory.createProject();

		assertEquals(project1, project2);
	}

	@Test
	public void testEquals2() {
		Project project1 = ProjectTestDataFactory.createProjectWithTrack();
		Project project2 = ProjectTestDataFactory.createProjectWithTrack();

		assertEquals(project1, project2);
	}

	@Test
	public void testEquals3() {
		Project project1 = ProjectTestDataFactory.createProjectWithTrack();
		Project project2 = ProjectTestDataFactory.createProjectWithTrack(MusicalInstrument.APPLAUSE);

		assertThat(project1, is(not(equalTo(project2))));
	}

	@Test
	public void testEquals4() {
		Project project1 = ProjectTestDataFactory.createProjectWithTrack();
		Project project2 = ProjectTestDataFactory.createProject();

		assertThat(project1, is(not(equalTo(project2))));
	}

	@Test
	public void testEquals5() {
		Project project1 = ProjectTestDataFactory.createProject("Some name");
		Project project2 = ProjectTestDataFactory.createProject("Another name");

		assertThat(project1, is(not(equalTo(project2))));
	}

	@Test
	public void testEquals6() {
		Project project1 = ProjectTestDataFactory.createProject(60);
		Project project2 = ProjectTestDataFactory.createProject(90);

		assertThat(project1, is(not(equalTo(project2))));
	}

	@Test
	public void testEquals7() {
		Project project = ProjectTestDataFactory.createProject();

		assertThat(project, is(not(equalTo(null))));
	}

	@Test
	public void testEquals8() {
		Project project = ProjectTestDataFactory.createProject();

		assertFalse(project.equals(""));
	}

	@Test
	public void testEquals9() {
		Project project1 = ProjectTestDataFactory.createProjectWithMusicalBeat(MusicalBeat.BEAT_4_4);
		Project project2 = ProjectTestDataFactory.createProjectWithMusicalBeat(MusicalBeat.BEAT_16_16);

		assertThat(project1, is(not(equalTo(project2))));
	}

	@Test
	public void testToString() {
		Project project = ProjectTestDataFactory.createProject();
		String expectedString = "[Project] name=" + project.getName() + " beatsPerMinute="
				+ project.getBeatsPerMinute() + " trackCount=" + project.size();

		assertEquals(expectedString, project.toString());
	}

	@Test
	public void testCopyProject() {
		Project project = ProjectTestDataFactory.createProjectWithTrack();
		Project copyProject = new Project(project);

		assertNotSame(project, copyProject);
		assertEquals(project, copyProject);
	}
}
