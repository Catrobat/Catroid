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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class MusicalBeatTest {

	@Test
	public void testConvertToMusicalBeat1() {
		MusicalBeat musicalBeat = MusicalBeat.convertToMusicalBeat(16, 16);

		assertEquals(MusicalBeat.BEAT_16_16, musicalBeat);
	}

	@Test
	public void testConvertToMusicalBeat2() {
		MusicalBeat musicalBeat = MusicalBeat.convertToMusicalBeat(7, 7);

		assertEquals(MusicalBeat.BEAT_4_4, musicalBeat);
	}
}
