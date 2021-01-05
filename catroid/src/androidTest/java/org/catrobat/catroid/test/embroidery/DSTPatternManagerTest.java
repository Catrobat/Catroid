/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import com.badlogic.gdx.graphics.Color;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.embroidery.DSTPatternManager;
import org.catrobat.catroid.embroidery.DSTStitchCommand;
import org.catrobat.catroid.embroidery.DSTStitchPoint;
import org.catrobat.catroid.embroidery.EmbroideryPatternManager;
import org.catrobat.catroid.embroidery.EmbroideryStream;
import org.catrobat.catroid.embroidery.EmbroideryWorkSpace;
import org.catrobat.catroid.embroidery.StitchCommand;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DSTPatternManagerTest {

	private EmbroideryPatternManager patternManager;
	private Sprite sprite;
	private StitchCommand stitchCommand1;
	private StitchCommand stitchCommand2;

	@Before
	public void setUp() {
		patternManager = new DSTPatternManager();
		sprite = Mockito.mock(Sprite.class);
		stitchCommand1 = Mockito.mock(StitchCommand.class);
		Mockito.when(stitchCommand1.getLayer()).thenReturn(0);
		Mockito.when(stitchCommand1.getSprite()).thenReturn(sprite);
		stitchCommand2 = Mockito.mock(StitchCommand.class);
		Mockito.when(stitchCommand2.getLayer()).thenReturn(1);
		Mockito.when(stitchCommand2.getSprite()).thenReturn(sprite);
	}

	@Test
	public void testAddSingleStitchCommand() {
		patternManager.addStitchCommand(stitchCommand1);
		Mockito.verify(stitchCommand1, Mockito.times(1)).act(Mockito.any(EmbroideryWorkSpace.class),
				Mockito.any(EmbroideryStream.class), Mockito.eq(null));
	}

	@Test
	public void testAddMultipleIndependentStitchCommands() {
		StitchCommand stitchCommand3 = Mockito.mock(StitchCommand.class);
		Mockito.when(stitchCommand3.getLayer()).thenReturn(1);
		Mockito.when(stitchCommand3.getSprite()).thenReturn(new Sprite());

		patternManager.addStitchCommand(stitchCommand1);
		patternManager.addStitchCommand(stitchCommand3);

		Mockito.verify(stitchCommand3, Mockito.times(1)).act(Mockito.any(EmbroideryWorkSpace.class),
				Mockito.any(EmbroideryStream.class), Mockito.eq(null));
	}

	@Test
	public void testAddMultipleStitchCommands() {
		patternManager.addStitchCommand(stitchCommand1);
		patternManager.addStitchCommand(stitchCommand2);
		Mockito.verify(stitchCommand2, Mockito.times(1)).act(Mockito.any(EmbroideryWorkSpace.class),
				Mockito.any(EmbroideryStream.class), Mockito.eq(stitchCommand1));
	}

	@Test
	public void testClearEmbroideryPattern() {
		patternManager.addStitchCommand(stitchCommand1);
		patternManager.clear();
		patternManager.addStitchCommand(stitchCommand2);
		Mockito.verify(stitchCommand2, Mockito.times(1)).act(Mockito.any(EmbroideryWorkSpace.class),
				Mockito.any(EmbroideryStream.class), Mockito.eq(null));
	}

	@Test
	public void testInvalidPattern() {
		patternManager.addStitchCommand(new DSTStitchCommand(0, 0, 0, sprite, Color.BLACK));
		assertFalse(patternManager.validPatternExists());
	}

	@Test
	public void testValidPattern() {
		patternManager.addStitchCommand(new DSTStitchCommand(0, 0, 0, sprite, Color.BLACK));
		patternManager.addStitchCommand(new DSTStitchCommand(0, 1, 0, sprite, Color.BLACK));
		assertTrue(patternManager.validPatternExists());
	}

	@Test
	public void testEmptyEmbroideryPattern() {
		assertTrue(patternManager.getEmbroideryPatternList().isEmpty());
	}

	@Test
	public void testSingleLayerEmbroideryPatternList() {
		StitchCommand command = new DSTStitchCommand(0, 0, 0, sprite, Color.BLACK);
		patternManager.addStitchCommand(command);

		assertEquals(1, patternManager.getEmbroideryPatternList().size());
		assertEquals(new DSTStitchPoint(command.getX(), command.getY(), Color.BLACK),
				patternManager.getEmbroideryPatternList().get(0));
	}

	@Test
	public void testMultilayerEmbroideryPatternList() {
		patternManager.addStitchCommand(new DSTStitchCommand(0, 0, 0, sprite, Color.BLACK));
		patternManager.addStitchCommand(new DSTStitchCommand(0, 0, 1, sprite, Color.BLACK));

		assertEquals(5, patternManager.getEmbroideryPatternList().size());
		assertTrue(patternManager.getEmbroideryPatternList().get(1).isColorChangePoint());
		assertTrue(patternManager.getEmbroideryPatternList().get(2).isJumpPoint());
	}
}
