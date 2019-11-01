/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.embroidery.EmbroideryPatternManager;
import org.catrobat.catroid.embroidery.SimpleRunningStitch;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.stage.StageListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class SimpleRunningStitchTest {
	private Sprite sprite;
	private Look spriteLook;
	private SimpleRunningStitch simpleRunningStitch;
	private EmbroideryPatternManager embroideryPatternManager;

	@Before
	public void setUp() {
		sprite = Mockito.mock(Sprite.class);
		spriteLook = Mockito.mock(Look.class);
		sprite.look = spriteLook;
		embroideryPatternManager = Mockito.mock(EmbroideryPatternManager.class);
		StageActivity.stageListener = Mockito.mock(StageListener.class);
		StageActivity.stageListener.embroideryPatternManager = embroideryPatternManager;
	}

	@Test
	public void testNoMoveOfRunningStitch() {
		final int length = 10;
		simpleRunningStitch = new SimpleRunningStitch(sprite, length);
		simpleRunningStitch.update(0, 0);

		verify(embroideryPatternManager, times(0)).addStitchCommand(any());
	}

	@Test
	public void testSimpleMoveOfRunningStitch() {
		final int length = 10;
		simpleRunningStitch = new SimpleRunningStitch(sprite, length);
		simpleRunningStitch.update(10, 10);

		verify(embroideryPatternManager, times(2)).addStitchCommand(any());
	}

	@Test
	public void testSetStartCoordinates() {
		final int length = 10;
		simpleRunningStitch = new SimpleRunningStitch(sprite, length);
		simpleRunningStitch.setStartCoordinates(20, 20);
		simpleRunningStitch.update(0, 0);

		verify(embroideryPatternManager, times(3)).addStitchCommand(any());
	}
}
