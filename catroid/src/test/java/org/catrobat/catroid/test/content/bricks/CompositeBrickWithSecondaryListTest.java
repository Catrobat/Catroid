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
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.PhiroIfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.RaspiIfLogicBeginBrick;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.catrobat.catroid.test.StaticSingletonInitializer.initializeStaticSingletonMethods;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
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
public class CompositeBrickWithSecondaryListTest {

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{IfLogicBeginBrick.class.getSimpleName(), IfLogicBeginBrick.class},
				{PhiroIfLogicBeginBrick.class.getSimpleName(), PhiroIfLogicBeginBrick.class},
				{RaspiIfLogicBeginBrick.class.getSimpleName(), RaspiIfLogicBeginBrick.class},
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public Class<CompositeBrick> compositeBrickClass;

	private CompositeBrick compositeBrick;
	private Brick compositeMiddleBrick;
	private Brick compositeEndBrick;

	@Before
	public void setUp() throws IllegalAccessException, InstantiationException {
		initializeStaticSingletonMethods();
		compositeBrick = compositeBrickClass.newInstance();
		List<Brick> compositeBrickParts = compositeBrick.getAllParts();
		compositeMiddleBrick = compositeBrickParts.get(1);
		compositeEndBrick = compositeBrickParts.get(2);
	}

	@Test
	public void testGetPartsOfCompositeBrick() {
		assertTrue(compositeBrick.consistsOfMultipleParts());

		assertTrue(compositeBrick.hasSecondaryList());

		assertNotNull(compositeBrick.getSecondaryNestedBricks());

		assertEquals(3, compositeBrick.getAllParts().size());

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

		assertSame(compositeBrick, compositeMiddleBrick.getParent());

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
	public void testGetParentListFromCompositeMiddleBrick() {
		Brick mockParent = mock(Brick.class);

		compositeBrick.setParent(mockParent);

		List<Brick> parentList = compositeMiddleBrick.getDragAndDropTargetList();
		verifyZeroInteractions(mockParent);

		assertSame(compositeBrick.getSecondaryNestedBricks(), parentList);
	}

	@Test
	public void testGetPositionInParentListFromCompositeMiddleBrick() {
		Brick mockParent = mock(Brick.class);

		compositeBrick.getNestedBricks().add(mock(Brick.class));

		compositeBrick.setParent(mockParent);

		assertEquals(-1, compositeMiddleBrick.getPositionInDragAndDropTargetList());
	}

	@Test
	public void testGetParentListFromCompositeEndBrick() {
		Brick mockParent = mock(Brick.class);

		List<Brick> mockedParentList = new ArrayList<>();
		when(mockParent.getDragAndDropTargetList()).thenReturn(mockedParentList);

		compositeBrick.setParent(mockParent);

		List<Brick> parentList = compositeEndBrick.getDragAndDropTargetList();
		verify(mockParent).getDragAndDropTargetList();

		assertThat(mockedParentList, is(empty()));

		assertSame(mockedParentList, parentList);
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
		Brick secondaryNestedBrick = mock(Brick.class);

		compositeBrick.getNestedBricks().add(nestedBrick);
		compositeBrick.getSecondaryNestedBricks().add(secondaryNestedBrick);

		CompositeBrick clone = (CompositeBrick) compositeBrick.clone();

		verify(nestedBrick).clone();

		when(nestedBrick.clone()).thenReturn(mock(Brick.class));

		assertNotSame(compositeBrick, clone);

		Brick clonedCompositeMiddleBrick = clone.getAllParts().get(1);
		assertNotSame(compositeMiddleBrick, clonedCompositeMiddleBrick);

		Brick clonedCompositeEndBrick = clone.getAllParts().get(2);
		assertNotSame(compositeEndBrick, clonedCompositeEndBrick);

		assertEquals(1, clone.getNestedBricks().size());

		assertEquals(1, clone.getSecondaryNestedBricks().size());

		assertNotSame(nestedBrick, clone.getNestedBricks().get(0));

		assertNotSame(secondaryNestedBrick, clone.getSecondaryNestedBricks().get(0));
	}

	@Test
	public void testCommentOutBrickInPrimaryList() {
		Sprite sprite = new Sprite();

		ScriptSequenceAction sequence = (ScriptSequenceAction) ActionFactory.createScriptSequenceAction(mock(Script.class));

		Brick brickToCommentOutInPrimaryList = spy(new BrickStub());
		Brick otherNestedBrickInPrimaryList = spy(new BrickStub());
		Brick otherNestedBrickInSecondaryList = spy(new BrickStub());

		compositeBrick.getNestedBricks().add(brickToCommentOutInPrimaryList);
		compositeBrick.getNestedBricks().add(otherNestedBrickInPrimaryList);

		compositeBrick.getSecondaryNestedBricks().add(otherNestedBrickInSecondaryList);

		brickToCommentOutInPrimaryList.setCommentedOut(true);

		compositeBrick.addActionToSequence(sprite, sequence);

		verify(brickToCommentOutInPrimaryList, never())
				.addActionToSequence(any(Sprite.class), any(ScriptSequenceAction.class));

		verify(otherNestedBrickInPrimaryList, times(1))
				.addActionToSequence(any(Sprite.class), any(ScriptSequenceAction.class));

		verify(otherNestedBrickInSecondaryList, times(1))
				.addActionToSequence(any(Sprite.class), any(ScriptSequenceAction.class));
	}

	@Test
	public void testCommentOutBrickInSecondaryList() {
		Sprite sprite = new Sprite();

		ScriptSequenceAction sequence = (ScriptSequenceAction) ActionFactory.createScriptSequenceAction(mock(Script.class));

		Brick brickToCommentOutInSecondaryList = spy(new BrickStub());
		Brick otherNestedBrickInPrimaryList = spy(new BrickStub());
		Brick otherNestedBrickInSecondaryList = spy(new BrickStub());

		compositeBrick.getNestedBricks().add(otherNestedBrickInPrimaryList);

		compositeBrick.getSecondaryNestedBricks().add(brickToCommentOutInSecondaryList);
		compositeBrick.getSecondaryNestedBricks().add(otherNestedBrickInSecondaryList);

		brickToCommentOutInSecondaryList.setCommentedOut(true);

		compositeBrick.addActionToSequence(sprite, sequence);

		verify(brickToCommentOutInSecondaryList, never())
				.addActionToSequence(any(Sprite.class), any(ScriptSequenceAction.class));

		verify(otherNestedBrickInPrimaryList, times(1))
				.addActionToSequence(any(Sprite.class), any(ScriptSequenceAction.class));

		verify(otherNestedBrickInSecondaryList, times(1))
				.addActionToSequence(any(Sprite.class), any(ScriptSequenceAction.class));
	}

	@Test
	public void testCommentOutBricksInBothLists() {
		Sprite sprite = new Sprite();

		ScriptSequenceAction sequence = (ScriptSequenceAction) ActionFactory.createScriptSequenceAction(mock(Script.class));

		Brick brickToCommentOutInPrimaryList = spy(new BrickStub());
		Brick brickToCommentOutInSecondaryList = spy(new BrickStub());
		Brick otherNestedBrickInPrimaryList = spy(new BrickStub());
		Brick otherNestedBrickInSecondaryList = spy(new BrickStub());

		compositeBrick.getNestedBricks().add(brickToCommentOutInPrimaryList);
		compositeBrick.getNestedBricks().add(otherNestedBrickInPrimaryList);

		compositeBrick.getSecondaryNestedBricks().add(brickToCommentOutInSecondaryList);
		compositeBrick.getSecondaryNestedBricks().add(otherNestedBrickInSecondaryList);

		brickToCommentOutInPrimaryList.setCommentedOut(true);
		brickToCommentOutInSecondaryList.setCommentedOut(true);

		compositeBrick.addActionToSequence(sprite, sequence);

		verify(brickToCommentOutInPrimaryList, never())
				.addActionToSequence(any(Sprite.class), any(ScriptSequenceAction.class));

		verify(brickToCommentOutInSecondaryList, never())
				.addActionToSequence(any(Sprite.class), any(ScriptSequenceAction.class));

		verify(otherNestedBrickInPrimaryList, times(1))
				.addActionToSequence(any(Sprite.class), any(ScriptSequenceAction.class));

		verify(otherNestedBrickInSecondaryList, times(1))
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
