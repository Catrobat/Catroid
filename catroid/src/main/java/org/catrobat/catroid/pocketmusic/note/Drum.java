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
package org.catrobat.catroid.pocketmusic.note;

public enum Drum {
	ACOUSTIC_BASS_DRUM(35),
	BASS_DRUM_1(36),
	SIDE_STICK(37),
	ACOUSTIC_SNARE(38),
	HAND_CLAP(39),
	ELECTRIC_SNARE(40),
	LOW_FLOOR_TOM(41),
	CLOSED_HI_HAT(42),
	HIGH_FLOOR_TOM(43),
	PEDAL_HI_HAT(44),
	LOW_TOM(45),
	OPEN_HI_HAT(46),
	LOW_MID_TOM(47),
	HI_MID_TOM(48),
	CRASH_CYMBAL_1(49),
	HIGH_TOM(50),
	RIDE_CYMBAL_1(51),
	CHINESE_CYMBAL(52),
	RIDE_BELL(53),
	TAMBOURINE(54),
	SPLASH_CYMBAL(55),
	COWBELL(56),
	CRASH_CYMBAL_2(57),
	VIBRASLAP(58),
	RIDE_CYMBAL_2(59),
	HI_BONGO(60),
	LOW_BONGO(61),
	MUTE_HI_CONGA(62),
	OPEN_HI_CONGA(63),
	LOW_CONGA(64),
	HIGH_TIMBALE(65),
	LOW_TIMBALE(66),
	HIGH_AGOGO(67),
	LOW_AGOGO(68),
	CABASA(69),
	MARACAS(70),
	SHORT_WHISTLE(71),
	LONG_WHISTLE(72),
	SHORT_GUIRO(73),
	LONG_GUIRO(74),
	CLAVES(75),
	HI_WOOD_BLOCK(76),
	LOW_WOOD_BLOCK(77),
	MUTE_CUICA(78),
	OPEN_CUICA(79),
	MUTE_TRIANGLE(80),
	OPEN_TRIANGLE(81);

	private int program;

	Drum(int program) {
		this.program = program;
	}

	public static Drum getDrumFromProgram(int program) {
		Drum[] drum = Drum.values();

		for (int i = 0; i < drum.length; i++) {
			if (drum[i].getProgram() == program) {
				return drum[i];
			}
		}

		return ACOUSTIC_BASS_DRUM;
	}

	public int getProgram() {
		return program;
	}
}
