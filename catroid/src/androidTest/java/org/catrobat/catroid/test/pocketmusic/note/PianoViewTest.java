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

package org.catrobat.catroid.test.pocketmusic.note;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.pocketmusic.note.NoteName;
import org.catrobat.catroid.pocketmusic.ui.PianoView;
import org.catrobat.catroid.pocketmusic.ui.TrackRowView;
import org.catrobat.catroid.pocketmusic.ui.TrackView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.test.core.app.ApplicationProvider;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class PianoViewTest {
	private PianoView pianoView;
	private List<TextView> blackKeys;
	private List<TextView> whiteKeys;

	private int activeColor;
	private int disabledwhiteKeyColor;
	private int disabledblackKeyColor;

	@Parameterized.Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{"Set One Key", Arrays.asList(0), Arrays.asList(), Arrays.asList(0)},
				{"Set All Keys", Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12), Arrays.asList(), Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)},
				{"Set And Reset All Keys", Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12), Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12), Arrays.asList()},
				{"Set various Keys", Arrays.asList(1, 5, 10), Arrays.asList(5), Arrays.asList(1, 10)},
				{"Set Invalid Key", Arrays.asList(-1, 13), Arrays.asList(-5, 14), Arrays.asList()}
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public List<Integer> activeKeys;

	@Parameterized.Parameter(2)
	public List<Integer> disabledKeys;

	@Parameterized.Parameter(3)
	public List<Integer> expectedActiveKeys;

	@Before
	public void setUp() {
		Context context = ApplicationProvider.getApplicationContext();
		pianoView = new PianoView(context);
		blackKeys = pianoView.getBlackPianoKeys();
		whiteKeys = pianoView.getWhitePianoKeys();
		activeColor = ContextCompat.getColor(context, R.color.orange);
		disabledwhiteKeyColor = ContextCompat.getColor(context, R.color.solid_white);
		disabledblackKeyColor = ContextCompat.getColor(context, R.color.solid_black);
	}

	@Test
	public void testSetButtonColor() {
		for (int activeKey : activeKeys) {
			NoteName tempNote = NoteName.getNoteNameFromMidiValue(TrackRowView.getMidiValueForRow(activeKey));
			pianoView.setKeyColor(tempNote, true);
		}

		for (int disabledKey : disabledKeys) {
			NoteName tempNote = NoteName.getNoteNameFromMidiValue(TrackRowView.getMidiValueForRow(disabledKey));
			pianoView.setKeyColor(tempNote, false);
		}

		for (int i = 0; i < blackKeys.size() + whiteKeys.size(); ++i) {
			int index = Arrays.binarySearch(TrackView.BLACK_KEY_INDICES, i);
			if (index < 0) {
				index = Arrays.binarySearch(TrackView.WHITE_KEY_INDICES, i);
				if (index < 0) {
					continue;
				}
				boolean keyExpectedActive = Arrays.binarySearch(expectedActiveKeys.toArray(), i) >= 0;
				assertEquals(keyExpectedActive ? activeColor : disabledwhiteKeyColor, getKeyColor(whiteKeys.get(index)));
			} else {
				boolean keyExpectedActive = Arrays.binarySearch(expectedActiveKeys.toArray(), i) >= 0;
				assertEquals(keyExpectedActive ? activeColor : disabledblackKeyColor, getKeyColor(blackKeys.get(index)));
			}
		}
	}

	private int getKeyColor(TextView key) {
		ColorDrawable cd = (ColorDrawable) key.getBackground();
		return cd.getColor();
	}
}
