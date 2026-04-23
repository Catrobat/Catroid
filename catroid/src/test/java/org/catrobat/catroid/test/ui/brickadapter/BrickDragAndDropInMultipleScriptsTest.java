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

package org.catrobat.catroid.test.ui.brickadapter;

import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.ScriptBrickBaseType;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.ui.recyclerview.adapter.BrickAdapter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class BrickDragAndDropInMultipleScriptsTest {

	private BrickAdapter adapter;

	private Script firstScript;
	private Script secondScript;

	private Brick mockedBrickInFirstScript;
	private Brick mockedBrickInSecondScript;

	@Before
	public void setUp() {
		mockedBrickInFirstScript = mock(Brick.class);
		doAnswer(new AddToFlatListAnswer()).when(mockedBrickInFirstScript).addToFlatList(anyList());
		when(mockedBrickInFirstScript.getAllParts())
				.thenReturn(Collections.singletonList(mockedBrickInFirstScript));

		mockedBrickInSecondScript = mock(Brick.class);
		doAnswer(new AddToFlatListAnswer()).when(mockedBrickInSecondScript).addToFlatList(anyList());
		when(mockedBrickInSecondScript.getAllParts())
				.thenReturn(Collections.singletonList(mockedBrickInSecondScript));

		firstScript = new ScriptStub();
		firstScript.addBrick(mockedBrickInFirstScript);

		secondScript = new ScriptStub();
		secondScript.addBrick(mockedBrickInSecondScript);

		List<Script> scripts = new ArrayList<>();
		scripts.add(firstScript);
		scripts.add(secondScript);

		Sprite sprite = mock(Sprite.class);
		when(sprite.getScriptList()).thenReturn(scripts);

		adapter = new BrickAdapter(sprite);
	}

	@Test
	public void testDragFromOneScriptToDownAnother() {
		Brick brickToMove = mockedBrickInFirstScript;
		Brick brickAtTargetPosition = mockedBrickInSecondScript;

		when(brickAtTargetPosition.getDragAndDropTargetList()).thenReturn(secondScript.getBrickList());
		when(brickAtTargetPosition.getPositionInDragAndDropTargetList()).thenReturn(0);

		assertTrue(adapter.onItemMove(1, 2));
		assertTrue(adapter.onItemMove(2, 3));

		adapter.moveItemTo(3, brickToMove);

		assertFalse(firstScript.getBrickList().contains(brickToMove));

		assertTrue(secondScript.getBrickList().contains(brickToMove));
	}

	@Test
	public void testDragFromOneScriptToUpAnother() {
		Brick brickToMove = mockedBrickInSecondScript;
		Brick brickAtTargetPosition = mockedBrickInFirstScript;

		when(brickAtTargetPosition.getDragAndDropTargetList()).thenReturn(firstScript.getBrickList());
		when(brickAtTargetPosition.getPositionInDragAndDropTargetList()).thenReturn(0);

		assertTrue(adapter.onItemMove(3, 2));
		assertTrue(adapter.onItemMove(2, 1));

		adapter.moveItemTo(1, brickToMove);

		assertFalse(secondScript.getBrickList().contains(brickToMove));

		assertTrue(firstScript.getBrickList().contains(brickToMove));
	}

	private static class ScriptStub extends Script {

		private ScriptBrick scriptBrick = new ScriptBrickBaseType() {

			@Override
			public Script getScript() {
				return ScriptStub.this;
			}

			@Override
			public int getViewResource() {
				return 0;
			}

			@Override
			public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
			}
		};

		@Override
		public EventId createEventId(Sprite sprite) {
			return null;
		}

		@Override
		public ScriptBrick getScriptBrick() {
			return scriptBrick;
		}
	}

	private static class AddToFlatListAnswer implements Answer {
		@Override
		public Object answer(InvocationOnMock invocation) {
			((List<Brick>) invocation.getArgument(0)).add((Brick) invocation.getMock());
			return null;
		}
	}
}
