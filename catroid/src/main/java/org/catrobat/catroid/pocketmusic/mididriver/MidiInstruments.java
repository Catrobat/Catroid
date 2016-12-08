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
package org.catrobat.catroid.pocketmusic.mididriver;

public enum MidiInstruments {

	ACOUSTIC_GRAND_PIANO((byte) 0),
	BRIGHT_ACOUSTIC_PIANO((byte) 1),
	ELECTRIC_GRAND_PIANO((byte) 2),
	HONKY_TONK_PIANO((byte) 3),
	ELECTRIC_PIANO_0((byte) 4),
	ELECTRIC_PIANO_1((byte) 5),
	HARPSICHORD((byte) 6),
	CLAVI((byte) 7),
	CELESTA((byte) 8),
	GLOCKENSPIEL((byte) 9),
	MUSIC_BOX((byte) 10),
	VIBRAPHONE((byte) 11),
	MARIMBA((byte) 12),
	XYLOPHONE((byte) 13),
	TUBULAR_BELLS((byte) 14),
	DULCIMER((byte) 15),
	DRAWBAR_ORGAN((byte) 16),
	PERCUSSIVE_ORGAN((byte) 17),
	ROCK_ORGAN((byte) 18),
	CHURCH_ORGAN((byte) 19),
	REED_ORGAN((byte) 20),
	ACCORDION((byte) 21),
	HARMONICA((byte) 22),
	TANGO_ACCORDION((byte) 23),
	ACOUSTIC_GUITAR_NYLON((byte) 24),
	ACOUSTIC_GUITAR_STEEL((byte) 25),
	ELECTRIC_GUITAR_JAZZ((byte) 26),
	ELECTRIC_GUITAR_CLEAN((byte) 27),
	ELECTRIC_GUITAR_MUTED((byte) 28),
	OVERDRIVEN_GUITAR((byte) 29),
	DISTORTION_GUITAR((byte) 30),
	GUITAR_HARMONICS((byte) 31),
	ACOUSTIC_BASS((byte) 32),
	ELECTRIC_BASS_FINGER((byte) 33),
	ELECTRIC_BASS_PICK((byte) 34),
	FRETLESS_BASS((byte) 35),
	SLAP_BASS_0((byte) 36),
	SLAP_BASS_1((byte) 37),
	SYNTH_BASS_0((byte) 38),
	SYNTH_BASS_1((byte) 39),
	VIOLIN((byte) 40),
	VIOLA((byte) 41),
	CELLO((byte) 42),
	CONTRABASS((byte) 43),
	TREMOLO_STRINGS((byte) 44),
	PIZZICATO_STRINGS((byte) 45),
	ORCHESTRAL_HARP((byte) 46),
	TIMPANI((byte) 47),
	STRING_ENSEMBLE_0((byte) 48),
	STRING_ENSEMBLE_1((byte) 49),
	SYNTHSTRINGS_0((byte) 50),
	SYNTHSTRINGS_1((byte) 51),
	CHOIR_AAHS((byte) 52),
	VOICE_OOHS((byte) 53),
	SYNTH_VOICE((byte) 54),
	ORCHESTRA_HIT((byte) 55),
	TRUMPET((byte) 56),
	TROMBONE((byte) 57),
	TUBA((byte) 58),
	MUTED_TRUMPET((byte) 59),
	FRENCH_HORN((byte) 60),
	BRASS_SECTION((byte) 61),
	SYNTHBRASS_0((byte) 62),
	SYNTHBRASS_1((byte) 63),
	SOPRANO((byte) 64),
	ALTO_SAX((byte) 65),
	TENOR_SAX((byte) 66),
	BARITONE_SAX((byte) 67),
	OBOE((byte) 68),
	ENGLISH_HORN((byte) 69),
	BASSOON((byte) 70),
	CLARINET((byte) 71),
	PICCOLO((byte) 72),
	FLUTE((byte) 73),
	RECORDER((byte) 74),
	PAN_FLUTE((byte) 75),
	BLOWN_BOTTLE((byte) 76),
	SHAKUHACHI((byte) 77),
	WHISTLE((byte) 78),
	OCARINA((byte) 79),
	LEAD_0_SQUARE((byte) 80),
	LEAD_1_SAWTOOTH((byte) 81),
	LEAD_2_CALLIOPE((byte) 82),
	LEAD_3_CHIFF((byte) 83),
	LEAD_4_CHARANG((byte) 84),
	LEAD_5_VOICE((byte) 85),
	LEAD_6_FIFTHS((byte) 86),
	LEAD_7_BASS_LEAD((byte) 87),
	PAD_0_NEW_AGE((byte) 88),
	PAD_1_WARM((byte) 89),
	PAD_2_POLYSYNTH((byte) 90),
	PAD_3_CHOIR((byte) 91),
	PAD_4_BOWED((byte) 92),
	PAD_5_METALLIC((byte) 93),
	PAD_6_HALO((byte) 94),
	PAD_7_SWEEP((byte) 95),
	FX_0_RAIN((byte) 96),
	FX_1_SOUNDTRACK((byte) 97),
	FX_2_CRYSTAL((byte) 98),
	FX_3_ATMOSPHERE((byte) 99),
	FX_4_BRIGHTNESS((byte) 100),
	FX_5_GOBLINS((byte) 101),
	FX_6_ECHOES((byte) 102),
	FX_7_SCI_FI((byte) 103),
	SIT_R((byte) 104),
	BANJO((byte) 105),
	SHAMISEN((byte) 106),
	KOTO((byte) 107),
	KALIMBA((byte) 108),
	BAG_PIPE((byte) 109),
	FIDDLE((byte) 110),
	SHANAI((byte) 111),
	TINKLE_BELL((byte) 112),
	AGOGO((byte) 113),
	STEEL_DRUMS((byte) 114),
	WOODBLOCK((byte) 115),
	TAIKO_DRUM((byte) 116),
	MELODIC_TOM((byte) 117),
	SYNTH_DRUM((byte) 118),
	REVERSE_CYMBAL((byte) 119),
	GUITAR_FRET_NOISE((byte) 120),
	BREATH_NOISE((byte) 121),
	SEASHORE((byte) 122),
	BIRD_TWEET((byte) 123),
	TELEPHONE_RING((byte) 124),
	HELICOPTER((byte) 125),
	APPLAUSE((byte) 126),
	GUNSHOT((byte) 127);

	private byte instrumentByte;

	MidiInstruments(byte instrumentByte) {
		this.instrumentByte = instrumentByte;
	}

	public byte getInstrumentByte() {
		return instrumentByte;
	}
}
