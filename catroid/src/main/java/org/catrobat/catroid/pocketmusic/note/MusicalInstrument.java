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

public enum MusicalInstrument {
	ACOUSTIC_GRAND_PIANO(0), BRIGHT_ACOUSTIC_PIANO(1), ELECTRIC_GRAND_PIANO(2), HONKY_TONK_PIANO(3), ELECTRIC_PIANO_1(4), ELECTRIC_PIANO_2(
			5), HARPSICHORD(6), CLAVI(7), CELESTA(8), GLOCKENSPIEL(9), MUSIC_BOX(10), VIBRAPHONE(11), MARIMBA(12), XYLOPHONE(
			13), TUBULAR_BELLS(14), DULCIMER(15), DRAWBAR_ORGAN(16), PERCUSSIVE_ORGAN(17), ROCK_ORGAN(18), CHURCH_ORGAN(
			19), REED_ORGAN(20), ACCORDION(21), HARMONICA(22), TANGO_ACCORDION(23), ACOUSTIC_GUITAR_NYLON(24), ACOUSTIC_GUITAR_STEEL(
			25), ELECTRIC_GUITAR_JAZZ(26), ELECTRIC_GUITAR_CLEAN(27), ELECTRIC_GUITAR_MUTED(28), OVERDRIVEN_GUITAR(29), DISTORTION_GUITAR(
			30), GUITAR_HARMONICS(31), ACOUSTIC_BASS(32), ELECTRIC_BASS_FINGER(33), ELECTRIC_BASS_PICK(34), FRETLESS_BASS(
			35), SLAP_BASS_1(36), SLAP_BASS_2(37), SYNTH_BASS_1(38), SYNTH_BASS_2(39), VIOLIN(40), VIOLA(41), CELLO(42), CONTRABASS(
			43), TREMOLO_STRINGS(44), PIZZICATO_STRINGS(45), ORCHESTRAL_HARP(46), TIMPANI(47), STRING_ENSEMBLE_1(48), STRING_ENSEMBLE_2(
			49), SYNTH_STRINGS_1(50), SYNTH_STRINGS_2(51), VOICE_AAHS(52), VOICE_OOHS(53), SYNTH_VOICE(54), ORCHESTRA_HIT(
			55), TRUMPET(56), TROMBONE(57), TUBA(58), MUTED_TRUMPET(59), FRENCH_HORN(60), BRASS_SECTION(61), SYNTH_BRASS_1(
			62), SYNTH_BRASS_2(63), SOPRANO_SAX(64), ALTO_SAX(65), TENOR_SAX(66), BARITONE_SAX(67), OBOE(68), ENGLISH_HORN(
			69), BASSOON(70), CLARINET(71), PICCOLO(72), FLUTE(73), RECORDER(74), PAN_FLUTE(75), BLOWN_BOTTLE(76), SHAKUHACHI(
			77), WHISTLE(78), OCARINA(79), LEAD_1_SQUARE(80), LEAD_2_SAWTOOTH(81), LEAD_3_CALLIOPE(82), LEAD_4_CHIFF(83), LEAD_5_CHARANG(
			84), LEAD_6_VOICE(85), LEAD_7_FIFTHS(86), LEAD_8_BASS_AND_LEAD(87), PAD_1_NEW_AGE(88), PAD_2_WARM(89), PAD_3_POLYSYNTH(
			90), PAD_4_CHOIR(91), PAD_5_BOWED(92), PAD_6_METALLIC(93), PAD_7_HALO(94), PAD_8_SWEEP(95), FX_1_RAIN(96), FX_2_SOUNDTRACK(
			97), FX_3_CRYSTAL(98), FX_4_ATMOSPHERE(99), FX_5_BRIGHTNESS(100), FX_6_GOBLINS(101), FX_7_ECHOES(102), FX_8_SCI_FI(
			103), SITAR(104), BANJO(105), SHAMISEN(106), KOTO(107), KALIMBA(108), BAGPIPE(109), FIDDLE(110), SHANAI(111), TINKLE_BELL(
			112), AGOGO_BELLS(113), STEEL_DRUMS(114), WOODBLOCK(115), TAIKO_DRUM(116), MELODIC_TOM(117), SYNTH_DRUM(118), REVERSE_CYMBAL(
			119), GUITAR_FRET_NOISE(120), BREATH_NOISE(121), SEASHORE(122), BIRD_TWEET(123), TELEPHONE_RING(124), HELICOPTER(
			125), APPLAUSE(126), GUNSHOT(127);

	private int program;

	private MusicalInstrument(int program) {
		this.program = program;
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
}
