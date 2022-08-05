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

package org.catrobat.catroid.uiespresso.pocketmusic;

import org.catrobat.catroid.pocketmusic.mididriver.MidiNotePlayer;
import org.catrobat.catroid.pocketmusic.mididriver.MidiSignals;
import org.catrobat.catroid.pocketmusic.note.MusicalInstrument;
import org.catrobat.catroid.pocketmusic.note.NoteName;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.uiespresso.util.hardware.SensorTestArduinoServerConnection;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class MidiNoteTest {

	private int waitingTime = 500;

	@Category({Cat.CatrobatLanguage.class, Level.Functional.class, Cat.Gadgets.class, Cat.SettingsAndPermissions
			.class, Cat.SensorBox.class})
	@Test
	public void testMidiNoteLowPlayed() {
		MidiNotePlayer notePlayer = new MidiNotePlayer();

		SensorTestArduinoServerConnection.checkAudioSensorValue(SensorTestArduinoServerConnection
				.SET_AUDIO_OFF_VALUE, waitingTime);

		notePlayer.setInstrument((byte) 0, MusicalInstrument.ACOUSTIC_GRAND_PIANO);
		notePlayer.sendMidi(MidiSignals.NOTE_ON.getSignalByte(), NoteName.A0.getMidi(), 127);

		SensorTestArduinoServerConnection.checkAudioSensorValue(SensorTestArduinoServerConnection
				.SET_AUDIO_ON_VALUE, waitingTime);
	}

	@Category({Cat.CatrobatLanguage.class, Level.Functional.class, Cat.Gadgets.class, Cat.SettingsAndPermissions
			.class, Cat.SensorBox.class})
	@Test
	public void testMidiNoteHighPlayed() {
		MidiNotePlayer notePlayer = new MidiNotePlayer();

		SensorTestArduinoServerConnection.checkAudioSensorValue(SensorTestArduinoServerConnection
				.SET_AUDIO_OFF_VALUE, waitingTime);

		notePlayer.setInstrument((byte) 0, MusicalInstrument.ACOUSTIC_GRAND_PIANO);
		notePlayer.sendMidi(MidiSignals.NOTE_ON.getSignalByte(), NoteName.C8.getMidi(), 127);

		SensorTestArduinoServerConnection.checkAudioSensorValue(SensorTestArduinoServerConnection
				.SET_AUDIO_ON_VALUE, waitingTime);
	}

	@Category({Cat.CatrobatLanguage.class, Level.Functional.class, Cat.Gadgets.class, Cat.SettingsAndPermissions
			.class, Cat.SensorBox.class})
	@Test
	public void testMidiNoteOff() {
		MidiNotePlayer notePlayer = new MidiNotePlayer();

		SensorTestArduinoServerConnection.checkAudioSensorValue(SensorTestArduinoServerConnection
				.SET_AUDIO_OFF_VALUE, waitingTime);

		notePlayer.setInstrument((byte) 0, MusicalInstrument.ACOUSTIC_GRAND_PIANO);
		notePlayer.sendMidi(MidiSignals.NOTE_ON.getSignalByte(), NoteName.C8.getMidi(), 127);
		notePlayer.sendMidi(MidiSignals.NOTE_OFF.getSignalByte(), NoteName.C8.getMidi(), 127);

		SensorTestArduinoServerConnection.checkAudioSensorValue(SensorTestArduinoServerConnection
				.SET_AUDIO_ON_VALUE, waitingTime);
	}
}
