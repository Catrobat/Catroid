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

package org.catrobat.catroid.test.content.bricks;

import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.content.bricks.CompositeBrick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.RepeatUntilBrick;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertNull;

import static org.catrobat.catroid.test.StaticSingletonInitializer.initializeStaticSingletonMethods;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class CompositeBrickTest {

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{IfThenLogicBeginBrick.class.getSimpleName(), IfThenLogicBeginBrick.class},
				{ForeverBrick.class.getSimpleName(), ForeverBrick.class},
				{RepeatBrick.class.getSimpleName(), RepeatBrick.class},
				{RepeatUntilBrick.class.getSimpleName(), RepeatUntilBrick.class},
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public Class<CompositeBrick> compositeBrickClass;

	private CompositeBrick compositeBrick;
	private Brick compositeEndBrick;

	@Before
	public void setUp() throws IllegalAccessException, InstantiationException {
		initializeStaticSingletonMethods();
		compositeBrick = compositeBrickClass.newInstance();
		List<Brick> compositeBrickParts = compositeBrick.getAllParts();
		compositeEndBrick = compositeBrickParts.get(compositeBrickParts.size() - 1);
	}

	@Test
	public void testGetPartsOfCompositeBrick() {
		assertTrue(compositeBrick.consistsOfMultipleParts());

		assertFalse(compositeBrick.hasSecondaryList());

		assertNull(compositeBrick.getSecondaryNestedBricks());

		assertEquals(2, compositeBrick.getAllParts().size());

		assertSame(compositeBrick, compositeBrick.getAllParts().get(0));

		assertSame(compositeEndBrick, compositeBrick.getAllParts().get(compositeBrick.getAllParts().size() - 1));
	}

	@Test
	public void testSetParents() {
		Brick mockParent = mock(Brick.class);

		Brick nestedBrick = mock(Brick.class);
		compositeBrick.getNestedBricks().add(nestedBrick);

		compositeBrick.setParent(mockParent);

		verify(compositeBrick.getNestedBricks().get(0)).setParent(compositeBrick);
		verifyZeroInteractions(mockParent);

		assertSame(compositeBrick, compositeEndBrick.getParent());
	}

	@Test
	public void testGetParentListFromCompositeStartBrick() {
		Brick mockParent = mock(Brick.class);
		compositeBrick.setParent(mockParent);

		List<Brick> parentList = compositeBrick.getDragAndDropTargetList();
		verifyZeroInteractions(mockParent);

		assertSame(compositeBrick.getNestedBricks(), parentList);
	}

	@Test
	public void testGetPositionInParentListFromCompositeStartBrick() {
		Brick mockParent = mock(Brick.class);

		compositeBrick.getNestedBricks().add(mock(Brick.class));

		compositeBrick.setParent(mockParent);

		assertEquals(-1, compositeBrick.getPositionInDragAndDropTargetList());
	}

	@Test
	public void testGetParentListFromCompositeEndBrick() {
		Brick mockParent = mock(Brick.class);

		List<Brick> mockedParentList = new ArrayList<>();
		mockedParentList.add(compositeBrick);
		when(mockParent.getDragAndDropTargetList()).thenReturn(mockedParentList);

		compositeBrick.setParent(mockParent);

		List<Brick> parentList = compositeEndBrick.getDragAndDropTargetList();
		verify(mockParent).getDragAndDropTargetList();

		assertSame(mockedParentList, parentList);

		assertTrue(parentList.contains(compositeBrick));
	}

	@Test
	public void testGetPositionInParentListFromCompositeEndBrick() {
		Brick mockParent = mock(Brick.class);

		List<Brick> mockedParentList = new ArrayList<>();
		mockedParentList.add(mock(Brick.class));
		mockedParentList.add(compositeBrick);
		when(mockParent.getDragAndDropTargetList()).thenReturn(mockedParentList);

		compositeBrick.setParent(mockParent);

		assertEquals(1, compositeEndBrick.getPositionInDragAndDropTargetList());
	}

	@Test
	public void testCloneCompositeBrick() throws CloneNotSupportedException {
		Brick nestedBrick = mock(Brick.class);
		compositeBrick.getNestedBricks().add(nestedBrick);

		CompositeBrick clone = (CompositeBrick) compositeBrick.clone();

		verify(nestedBrick).clone();

		when(nestedBrick.clone()).thenReturn(mock(Brick.class));

		assertNotSame(compositeBrick, clone);

		assertEquals(1, clone.getNestedBricks().size());

		assertNotSame(nestedBrick, clone.getNestedBricks().get(0));

		assertNotSame(compositeEndBrick, clone.getAllParts().get(1));
	}

	@Test
	public void testCommentOutBrickInCompositeBrick() {
		Sprite sprite = new Sprite();

		ScriptSequenceAction sequence = (ScriptSequenceAction) ActionFactory.createScriptSequenceAction(mock(Script.class));

		Brick brickToCommentOut = spy(new BrickStub());
		Brick otherNestedBrick = spy(new BrickStub());

		compositeBrick.getNestedBricks().add(brickToCommentOut);
		compositeBrick.getNestedBricks().add(otherNestedBrick);

		brickToCommentOut.setCommentedOut(true);

		compositeBrick.addActionToSequence(sprite, sequence);

		verify(brickToCommentOut, never())
				.addActionToSequence(any(Sprite.class), any(ScriptSequenceAction.class));

		verify(otherNestedBrick, times(1))
				.addActionToSequence(any(Sprite.class), any(ScriptSequenceAction.class));
	}

	private static class BrickStub extends BrickBaseType {

		@Override
		public int getViewResource() {
			return 0;
		}

		@Override
		public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		}
	}
}
