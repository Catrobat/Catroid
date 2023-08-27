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

public enum NoteName {
	EXT0(0, false), EXT1(1, false), EXT2(2, false), EXT3(3, false), EXT4(4, false), EXT5(5, false),
	EXT6(6, false), EXT7(7, false), EXT8(8, false), EXT9(9, false), EXT10(10, false), EXT11(11, false),
	EXT12(12, false), EXT13(13, false), EXT14(14, false), EXT15(15, false), EXT16(16, false),
	EXT17(17, false), EXT18(17, false), EXT19(17, false), EXT20(20, false),
	A0(21, false, "A₀"), A0S(22, true, "A₀♯/B♭₀"), B0(23, false, "B₀"),
	C1(24, false, "C₁"), C1S(25, true, "C₁♯/D♭₁"), D1(26, false, "D₁"),
	D1S(27, true, "D₁♯/E♭₁"), E1(28, false, "E₁"), F1(29, false, "F₁"),
	F1S(30, true, "F₁♯/G♭₁"), G1(31, false, "G₁"), G1S(32, true, "G₁♯/A♭₁"),
	A1(33, false, "A₁"), A1S(34, true, "A₁♯/B♭₁"), B1(35, false, "B₁"),
	C2(36, false, "C₂"), C2S(37, true, "C₂♯/D♭₂"), D2(38, false, "D₂"),
	D2S(39, true, "D₂♯/E♭₂"), E2(40, false, "E₂"), F2(41, false, "F₂"),
	F2S(42, true, "F₂♯/G♭₂"), G2(43, false, "G₂"), G2S(44, true, "G₂♯/A♭₂"),
	A2(45, false, "A₂"), A2S(46, true, "A₂♯/B♭₂"), B2(47, false, "B₂"),
	C3(48, false, "C₃"), C3S(49, true, "C₃♯/D♭₃"), D3(50, false, "D₃"),
	D3S(51, true, "D₃♯/E♭₃"), E3(52, false, "E₃"), F3(53, false, "F₃"),
	F3S(54, true, "F₃♯/G♭₃"), G3(55, false, "G₃"), G3S(56, true, "G₃♯/A♭₃"),
	A3(57, false, "A₃"), A3S(58, true, "A₃♯/B♭₃"), B3(59, false, "B₃"),
	C4(60, false, "C₄"), C4S(61, true, "C₄♯/D♭₄"), D4(62, false, "D₄"),
	D4S(63, true, "D₄♯/E♭₄"), E4(64, false, "E₄"), F4(65, false, "F₄"),
	F4S(66, true, "F₄♯/G♭₄"), G4(67, false, "G₄"), G4S(68, true, "G₄♯/A♭₄"),
	A4(69, false, "A₄"), A4S(70, true, "A₄♯/B♭₄"), B4(71, false, "B₄"),
	C5(72, false, "C₅"), C5S(73, true, "C₅♯/D♭₅"), D5(74, false, "D₅"),
	D5S(75, true, "D₅♯/E♭₅"), E5(76, false, "E₅"), F5(77, false, "F₅"),
	F5S(78, true, "F₅♯/G♭₅"), G5(79, false, "G₅"), G5S(80, true, "G₅♯/A♭₅"),
	A5(81, false, "A₅"), A5S(82, true, "A₅♯/B♭₅"), B5(83, false, "B₅"),
	C6(84, false, "C₆"), C6S(85, true, "C₆♯/D♭₆"), D6(86, false, "D₆"),
	D6S(87, true, "D₆♯/E♭₆"), E6(88, false, "E₆"), F6(89, false, "F₆"),
	F6S(90, true, "F₆♯/G♭₆"), G6(91, false, "G₆"), G6S(92, true, "G₆♯/A♭₆"),
	A6(93, false, "A₆"), A6S(94, true, "A₆♯/B♭₆"), B6(95, false, "B₆"),
	C7(96, false, "C₇"), C7S(97, true, "C₇♯/D♭₇"), D7(98, false, "D₇"),
	D7S(99, true, "D₇♯/E♭₇"), E7(100, false, "E₇"), F7(101, false, "F₇"),
	F7S(102, true, "F₇♯/G♭₇"), G7(103, false, "G₇"), G7S(104, true, "G₇♯/A♭₇"),
	A7(105, false, "A₇"), A7S(106, true, "A₇♯/B♭₇"), B7(107, false, "B₇"),
	C8(108, false, "C₈"),
	EXT109(109, false), EXT110(110, false), EXT111(111, false), EXT112(112, false), EXT113(113, false),
	EXT114(114, false), EXT115(115, false), EXT116(116, false), EXT117(117, false), EXT118(118, false),
	EXT119(119, false), EXT120(120, false), EXT121(121, false), EXT122(122, false), EXT123(123, false),
	EXT124(124, false), EXT125(125, false), EXT126(126, false), EXT127(127, false), EXT128(128, false),
	EXT129(129, false), EXT130(130, false);

	public static final NoteName DEFAULT_NOTE_NAME = NoteName.C1;

	private int midi;
	private boolean signed;

	private String prettyPrintName;

	NoteName(int midi, boolean signed) {
		this.midi = midi;
		this.signed = signed;
		this.prettyPrintName = "";
	}

	NoteName(int midi, boolean signed, String prettyPrintName) {
		this.midi = midi;
		this.signed = signed;
		this.prettyPrintName = prettyPrintName;
	}

	public static NoteName getNoteNameFromMidiValue(int midiValue) {
		NoteName[] noteNames = NoteName.values();

		for (int i = 0; i < noteNames.length; i++) {
			if (noteNames[i].getMidi() == midiValue) {
				return noteNames[i];
			}
		}

		return DEFAULT_NOTE_NAME;
	}

	public static int calculateDistanceCountingNoneSignedNotesOnly(NoteName referenceNoteName, NoteName noteName) {
		int distance = 0;
		boolean isDownGoing = (noteName.midi - referenceNoteName.midi) > 0;

		NoteName smallNoteName = isDownGoing ? referenceNoteName : noteName;
		NoteName largeNoteName = isDownGoing ? noteName : referenceNoteName;

		if (smallNoteName.isSigned()) {
			distance = 1;
		} else if (largeNoteName.isSigned()) {
			distance = -1;
		}

		while (smallNoteName.getMidi() != largeNoteName.getMidi()) {
			if (!smallNoteName.isSigned()) {
				distance++;
			}

			smallNoteName = smallNoteName.next();
		}

		return (isDownGoing ? distance * (-1) : distance);
	}

	public static int calculateDistanceToMiddleLineCountingSignedNotesOnly(MusicalKey key, NoteName noteName) {
		return calculateDistanceCountingNoneSignedNotesOnly(key.getNoteNameOnMiddleLine(), noteName);
	}

	public int getMidi() {
		return midi;
	}

	public NoteName next() {
		int index = this.ordinal() + 1;

		if (index >= values().length) {
			index--;
		}

		return values()[index];
	}

	public NoteName previous() {
		int index = this.ordinal() - 1;

		if (index < 0) {
			index++;
		}

		return values()[index];
	}

	public boolean isSigned() {
		return signed;
	}

	public String getPrettyPrintName() {
		return prettyPrintName;
	}
}
