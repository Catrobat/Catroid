/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Project implements Serializable {

	public static final int DEFAULT_BEATS_PER_MINUTE = 60;
	public static final MusicalBeat DEFAULT_BEAT = MusicalBeat.BEAT_4_4;
	public static final MusicalInstrument DEFAULT_INSTRUMENT = MusicalInstrument.ACOUSTIC_GRAND_PIANO;
	private static final long serialVersionUID = 7396763540934053008L;

	private String name;
	private int beatsPerMinute;
	private MusicalBeat beat;
	private Map<String, Track> tracks;
	private String fileName;

	public Project(String name, MusicalBeat beat, int beatsPerMinute) {
		this.name = name;
		this.beatsPerMinute = beatsPerMinute;
		this.beat = beat;
		this.tracks = new HashMap<>();
	}

	public Project(Project project) {
		name = project.getName();
		beatsPerMinute = project.getBeatsPerMinute();
		beat = project.getBeat();
		tracks = new HashMap<>();

		for (String name : project.tracks.keySet()) {
			tracks.put(name, new Track(project.tracks.get(name)));
		}
	}

	public Project(Project project, String name) {
		this(project);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getBeatsPerMinute() {
		return beatsPerMinute;
	}

	public MusicalBeat getBeat() {
		return beat;
	}

	public void putTrack(String trackName, Track track) {
		tracks.put(trackName, track);
	}

	public Set<String> getTrackNames() {
		return tracks.keySet();
	}

	public Track getTrack(String trackName) {
		return tracks.get(trackName);
	}

	public long getTotalTimeInMilliseconds() {
		long totalTime = 0;

		for (Track track : tracks.values()) {
			long trackTime = track.getTotalTimeInMilliseconds();

			if (trackTime > totalTime) {
				totalTime = trackTime;
			}
		}

		return totalTime;
	}

	public int size() {
		return tracks.size();
	}

	@Override
	public int hashCode() {
		int hashCode = 16;
		int primeWithGoodCollisionPrevention = 31;
		hashCode = primeWithGoodCollisionPrevention * hashCode + name.hashCode();
		hashCode = primeWithGoodCollisionPrevention * hashCode + beatsPerMinute;
		hashCode = primeWithGoodCollisionPrevention * hashCode + beat.hashCode();
		hashCode = primeWithGoodCollisionPrevention * hashCode + tracks.hashCode();
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj == null) || !(obj instanceof Project)) {
			return false;
		}

		Project project = (Project) obj;

		if (!getName().equals(project.getName())) {
			return false;
		}

		if (getBeatsPerMinute() != project.getBeatsPerMinute()) {
			return false;
		}

		if (getBeat() != project.getBeat()) {
			return false;
		}

		if (tracks.equals(project.tracks)) {
			return true;
		}

		return false;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String toString() {
		return "[Project] name=" + name + " beatsPerMinute=" + beatsPerMinute + " trackCount=" + size();
	}
}
