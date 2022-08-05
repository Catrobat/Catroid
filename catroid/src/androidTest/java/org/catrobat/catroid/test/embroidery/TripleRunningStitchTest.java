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

package org.catrobat.catroid.test.embroidery;

import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.embroidery.EmbroideryPatternManager;
import org.catrobat.catroid.embroidery.TripleRunningStitch;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.stage.StageListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class TripleRunningStitchTest {
	private Sprite sprite;
	private TripleRunningStitch tripleRunningStitch;
	private EmbroideryPatternManager embroideryPatternManager;

	@Before
	public void setUp() {
		sprite = Mockito.mock(Sprite.class);
		sprite.look = Mockito.mock(Look.class);
		embroideryPatternManager = Mockito.mock(EmbroideryPatternManager.class);
		StageActivity.stageListener = Mockito.mock(StageListener.class);
		StageActivity.stageListener.embroideryPatternManager = embroideryPatternManager;
	}

	@After
	public void tearDown() {
		StageActivity.stageListener = null;
	}

	@Test
	public void testNoMoveOfRunningStitch() {
		final int steps = 10;
		tripleRunningStitch = new TripleRunningStitch(sprite, steps);
		tripleRunningStitch.update(0, 0);

		verify(embroideryPatternManager, times(0)).addStitchCommand(any());
	}

	@Test
	public void testSimpleMoveOfRunningStitch() {
		final int steps = 10;
		tripleRunningStitch = new TripleRunningStitch(sprite, steps);
		tripleRunningStitch.update(10, 10);

		verify(embroideryPatternManager, times(4)).addStitchCommand(any());
	}

	@Test
	public void testSetStartCoordinates() {
		final int steps = 10;
		tripleRunningStitch = new TripleRunningStitch(sprite, steps);
		tripleRunningStitch.setStartCoordinates(20, 20);
		tripleRunningStitch.update(0, 0);

		verify(embroideryPatternManager, times(7)).addStitchCommand(any());
	}
}
