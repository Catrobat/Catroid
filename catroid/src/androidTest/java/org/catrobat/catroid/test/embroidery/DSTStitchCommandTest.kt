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

import com.badlogic.gdx.graphics.Color;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.embroidery.DSTStitchCommand;
import org.catrobat.catroid.embroidery.DSTStream;
import org.catrobat.catroid.embroidery.DSTWorkSpace;
import org.catrobat.catroid.embroidery.EmbroideryStream;
import org.catrobat.catroid.embroidery.EmbroideryWorkSpace;
import org.catrobat.catroid.embroidery.StitchCommand;
import org.catrobat.catroid.embroidery.StitchPoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.ArrayList;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class DSTStitchCommandTest {
	private Sprite sprite;
	private EmbroideryStream stream;
	private EmbroideryWorkSpace workSpace;
	private final float xCoord = 1.5f;
	private final float yCoord = 2.5f;
	private final int layer = 3;

	@Before
	public void setUp() {
		sprite = mock(Sprite.class);
		stream = mock(DSTStream.class);
		workSpace = mock(DSTWorkSpace.class);
	}

	@Test
	public void testDSTStitchCommand() {
		StitchCommand command = new DSTStitchCommand(xCoord, yCoord, layer, sprite, Color.BLACK);

		assertEquals(xCoord, command.getX(), 0);
		assertEquals(yCoord, command.getY(), 0);
		assertEquals(layer, command.getLayer());
		assertEquals(sprite, command.getSprite());
	}

	@Test
	public void testAddSimpleStitchCommand() {
		StitchCommand command = new DSTStitchCommand(xCoord, yCoord, layer, sprite, Color.BLACK);
		command.act(workSpace, stream, null);

		verify(stream, times(1)).addStitchPoint(eq(xCoord), eq(yCoord), eq(Color.BLACK));
	}

	@Test
	public void testActDuplicateStitchCommand() {
		when(workSpace.getCurrentX()).thenReturn(xCoord);
		when(workSpace.getCurrentY()).thenReturn(yCoord);
		when(workSpace.getLastSprite()).thenReturn(sprite);

		StitchCommand command = new DSTStitchCommand(xCoord, yCoord, layer, sprite, Color.BLACK);
		command.act(workSpace, stream, null);

		verify(stream, Mockito.never()).addStitchPoint(anyFloat(), anyFloat(), eq(Color.BLACK));
	}

	@Test
	public void testSpriteTriggeredColorChange() {
		when(workSpace.getCurrentX()).thenReturn(0.0f);
		when(workSpace.getCurrentY()).thenReturn(0.0f);
		when(workSpace.getLastSprite()).thenReturn(sprite);

		StitchCommand command = new DSTStitchCommand(xCoord, yCoord, layer, mock(Sprite.class), Color.BLACK);
		command.act(workSpace, stream, null);

		verify(stream, times(1)).addColorChange();
		verify(stream, times(2)).addStitchPoint(eq(0.0f), eq(0.0f), eq(null));
		verify(stream, times(1)).addStitchPoint(eq(xCoord), eq(yCoord), eq(Color.BLACK));
	}

	@Test
	public void testLayerSwitchTriggeredColorChange() {
		ArrayList<StitchPoint> pointList = new ArrayList<>();
		pointList.add(mock(StitchPoint.class));
		when(stream.getPointList()).thenReturn(pointList);

		StitchCommand previousCommand = new DSTStitchCommand(1, 1, layer - 1, mock(Sprite.class), Color.BLACK);
		StitchCommand command = new DSTStitchCommand(xCoord, yCoord, layer, mock(Sprite.class), Color.BLACK);
		command.act(workSpace, stream, previousCommand);

		InOrder inOrder = inOrder(stream);
		inOrder.verify(stream, times(1)).addColorChange();
		inOrder.verify(stream, times(1)).addStitchPoint(eq(1.0f), eq(1.0f), eq(Color.BLACK));
		inOrder.verify(stream, times(1)).addJump();
		inOrder.verify(stream, times(2)).addStitchPoint(eq(1.0f), eq(1.0f), eq(Color.BLACK));
		inOrder.verify(stream, times(1)).addStitchPoint(eq(xCoord), eq(yCoord), eq(Color.BLACK));
	}

	@Test
	public void testAddPreviousCommandOfSpriteOfOtherLayer() {
		ArrayList<StitchPoint> pointList = new ArrayList<>();
		when(stream.getPointList()).thenReturn(pointList);

		StitchCommand previousCommand = new DSTStitchCommand(1, 1, layer - 1, mock(Sprite.class), Color.BLACK);
		StitchCommand command = new DSTStitchCommand(xCoord, yCoord, layer, mock(Sprite.class), Color.BLACK);
		command.act(workSpace, stream, previousCommand);

		verify(stream, times(1)).addStitchPoint(eq(1.0f), eq(1.0f), eq(Color.BLACK));
		verify(stream, times(1)).addStitchPoint(eq(xCoord), eq(yCoord), eq(Color.BLACK));
	}

	@Test
	public void testStitchCommandEquals() {
		Sprite sprite = new Sprite("firstSprite");
		StitchCommand command1 = new DSTStitchCommand(0, 0, 0, sprite, Color.BLACK);
		StitchCommand command2 = new DSTStitchCommand(0, 1, 0, sprite, Color.BLACK);
		StitchCommand command3 = new DSTStitchCommand(0, 0, 1, sprite, Color.BLACK);
		StitchCommand command4 = new DSTStitchCommand(0, 0, 0, new Sprite("secondSprite"), Color.BLACK);
		StitchCommand command5 = new DSTStitchCommand(0, 0, 0, sprite, Color.BLACK);

		assertEquals(command1, command5);
		assertNotEquals(command1, command2);
		assertNotEquals(command1, command3);
		assertNotEquals(command1, command4);
	}

	@Test
	public void testStitchCommandHashCode() {
		Sprite sprite = new Sprite("firstSprite");
		StitchCommand command1 = new DSTStitchCommand(0, 0, 0, sprite, Color.BLACK);
		StitchCommand command2 = new DSTStitchCommand(0, 1, 0, sprite, Color.BLACK);
		StitchCommand command3 = new DSTStitchCommand(0, 0, 1, sprite, Color.BLACK);
		StitchCommand command4 = new DSTStitchCommand(0, 0, 0, new Sprite("secondSprite"), Color.BLACK);
		StitchCommand command5 = new DSTStitchCommand(0, 0, 0, sprite, Color.BLACK);

		assertEquals(command1.hashCode(), command5.hashCode());
		assertNotEquals(command1.hashCode(), command2.hashCode());
		assertNotEquals(command1.hashCode(), command3.hashCode());
		assertNotEquals(command1.hashCode(), command4.hashCode());
	}
}

