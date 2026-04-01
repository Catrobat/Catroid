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

public enum MusicalInstrument {
	ACOUSTIC_GRAND_PIANO(0, 21, 108),
	BRIGHT_ACOUSTIC_PIANO(1, 21, 108),
	ELECTRIC_GRAND_PIANO(2, 21, 108),
	HONKY_TONK_PIANO(3, 21, 108),
	ELECTRIC_PIANO_1(4, 21, 108),
	ELECTRIC_PIANO_2(5, 21, 108),
	HARPSICHORD(6, 21, 108),
	CLAVI(7, 21, 68),
	CELESTA(8, 21, 108),
	GLOCKENSPIEL(9, 36, 108),
	MUSIC_BOX(10, 36, 108),
	VIBRAPHONE(11, 36, 108),
	MARIMBA(12, 36, 108),
	XYLOPHONE(13, 36, 108),
	TUBULAR_BELLS(14, 55, 108),
	DULCIMER(15, 33, 108),
	DRAWBAR_ORGAN(16, 21, 89),
	PERCUSSIVE_ORGAN(17, 21, 108),
	ROCK_ORGAN(18, 21, 108),
	CHURCH_ORGAN(19, 21, 108),
	REED_ORGAN(20, 21, 89),
	ACCORDION(21, 48, 96),
	HARMONICA(22, 60, 78),
	TANGO_ACCORDION(23, 48, 96),
	ACOUSTIC_GUITAR_NYLON(24, 21, 108),
	ACOUSTIC_GUITAR_STEEL(25, 21, 108),
	ELECTRIC_GUITAR_JAZZ(26, 21, 108),
	ELECTRIC_GUITAR_CLEAN(27, 21, 108),
	ELECTRIC_GUITAR_MUTED(28, 21, 96),
	OVERDRIVEN_GUITAR(29, 21, 108),
	DISTORTION_GUITAR(30, 21, 108),
	GUITAR_HARMONICS(31, 21, 96),
	ACOUSTIC_BASS(32, 21, 96),
	ELECTRIC_BASS_FINGER(33, 21, 96),
	ELECTRIC_BASS_PICK(34, 21, 96),
	FRETLESS_BASS(35, 21, 96),
	SLAP_BASS_1(36, 21, 96),
	SLAP_BASS_2(37, 21, 96),
	SYNTH_BASS_1(38, 21, 96),
	SYNTH_BASS_2(39, 21, 96),
	VIOLIN(40, 55, 108),
	VIOLA(41, 48, 96),
	CELLO(42, 36, 72),
	CONTRABASS(43, 21, 60),
	TREMOLO_STRINGS(44, 21, 108),
	PIZZICATO_STRINGS(45, 21, 108),
	ORCHESTRAL_HARP(46, 21, 108),
	TIMPANI(47, 21, 72),
	STRING_ENSEMBLE_1(48, 21, 108),
	STRING_ENSEMBLE_2(49, 21, 108),
	SYNTH_STRINGS_1(50, 21, 108),
	SYNTH_STRINGS_2(51, 21, 108),
	VOICE_AAHS(52, 21, 108),
	VOICE_OOHS(53, 21, 108),
	SYNTH_VOICE(54, 21, 108),
	ORCHESTRA_HIT(55, 21, 96),
	TRUMPET(56, 24, 108),
	TROMBONE(57, 21, 96),
	TUBA(58, 21, 84),
	MUTED_TRUMPET(59, 36, 96),
	FRENCH_HORN(60, 21, 96),
	BRASS_SECTION(61, 24, 79),
	SYNTH_BRASS_1(62, 24, 83),
	SYNTH_BRASS_2(63, 24, 83),
	SOPRANO_SAX(64, 24, 108),
	ALTO_SAX(65, 24, 96),
	TENOR_SAX(66, 24, 96),
	BARITONE_SAX(67, 24, 84),
	OBOE(68, 24, 108),
	ENGLISH_HORN(69, 24, 96),
	BASSOON(70, 21, 84),
	CLARINET(71, 24, 108),
	PICCOLO(72, 36, 108),
	FLUTE(73, 36, 108),
	RECORDER(74, 36, 108),
	PAN_FLUTE(75, 36, 108),
	BLOWN_BOTTLE(76, 36, 96),
	SHAKUHACHI(77, 36, 96),
	WHISTLE(78, 36, 96),
	OCARINA(79, 36, 108),
	LEAD_1_SQUARE(80, 21, 108),
	LEAD_2_SAWTOOTH(81, 21, 83),
	LEAD_3_CALLIOPE(82, 21, 108),
	LEAD_4_CHIFF(83, 21, 108),
	LEAD_5_CHARANG(84, 21, 108),
	LEAD_6_VOICE(85, 21, 108),
	LEAD_7_FIFTHS(86, 21, 108),
	LEAD_8_BASS_AND_LEAD(87, 21, 83),
	PAD_1_NEW_AGE(88, 21, 108),
	PAD_2_WARM(89, 21, 108),
	PAD_3_POLYSYNTH(90, 21, 83),
	PAD_4_CHOIR(91, 21, 108),
	PAD_5_BOWED(92, 21, 108),
	PAD_6_METALLIC(93, 21, 108),
	PAD_7_HALO(94, 21, 108),
	PAD_8_SWEEP(95, 21, 83),
	FX_1_RAIN(96, 21, 108),
	FX_2_SOUNDTRACK(97, 21, 108),
	FX_3_CRYSTAL(98, 21, 108),
	FX_4_ATMOSPHERE(99, 21, 108),
	FX_5_BRIGHTNESS(100, 21, 108),
	FX_6_GOBLINS(101, 21, 108),
	FX_7_ECHOES(102, 21, 108),
	FX_8_SCI_FI(103, 21, 108),
	SITAR(104, 24, 108),
	BANJO(105, 24, 108),
	SHAMISEN(106, 21, 108),
	KOTO(107, 24, 108),
	KALIMBA(108, 24, 108),
	BAGPIPE(109, 24, 96),
	FIDDLE(110, 55, 108),
	SHANAI(111, 24, 96),
	TINKLE_BELL(112, 24, 108),
	AGOGO_BELLS(113, 36, 84),
	STEEL_DRUMS(114, 36, 96),
	WOODBLOCK(115, 36, 84),
	TAIKO_DRUM(116, 36, 84),
	MELODIC_TOM(117, 36, 84),
	SYNTH_DRUM(118, 36, 84),
	REVERSE_CYMBAL(119, 36, 84),
	GUITAR_FRET_NOISE(120, 36, 84),
	BREATH_NOISE(121, 36, 96),
	SEASHORE(122, 36, 84),
	BIRD_TWEET(123, 36, 84),
	TELEPHONE_RING(124, 36, 84),
	HELICOPTER(125, 36, 84),
	APPLAUSE(126, 36, 84),
	GUNSHOT(127, 36, 84);

	private int program;
	private int lowEnd;
	private int highEnd;

	MusicalInstrument(int program, int lowEnd, int highEnd) {
		this.program = program;
		this.lowEnd = lowEnd;
		this.highEnd = highEnd;
	}

	public static MusicalInstrument getInstrumentFromProgram(int program) {
		MusicalInstrument[] instruments = MusicalInstrument.values();

		for (int i = 0; i < instruments.length; i++) {
			if (instruments[i].getProgram() == program) {
				return instruments[i];
			}
		}

		return ACOUSTIC_GRAND_PIANO;
	}

	public int getProgram() {
		return program;
	}
	public int getLowEnd() {
		return lowEnd;
	}
	public int getHighEnd() {
		return highEnd;
	}
}
