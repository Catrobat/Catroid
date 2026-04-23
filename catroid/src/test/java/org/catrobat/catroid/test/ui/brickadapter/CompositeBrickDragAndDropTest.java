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
import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.content.bricks.CompositeBrick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.RepeatUntilBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.ScriptBrickBaseType;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.ui.recyclerview.adapter.BrickAdapter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class CompositeBrickDragAndDropTest {

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

	private BrickAdapter adapter;

	private Sprite sprite;
	private Script script;

	private Brick firstBrickInScript;
	private Brick lastBrickInScript;

	private CompositeBrick compositeBrick;
	private Brick compositeEndBrick;

	@Before
	public void setUp() throws IllegalAccessException, InstantiationException {
		script = new ScriptStub();

		List<Script> scripts = new ArrayList<>();
		scripts.add(script);

		sprite = mock(Sprite.class);
		when(sprite.getScriptList()).thenReturn(scripts);

		firstBrickInScript = new BrickStub();

		compositeBrick = compositeBrickClass.newInstance();
		compositeEndBrick = compositeBrick.getAllParts().get(1);

		lastBrickInScript = new BrickStub();

		script.addBrick(firstBrickInScript);
		script.addBrick(compositeBrick);
		script.addBrick(lastBrickInScript);

		adapter = new BrickAdapter(sprite);
	}

	@Test
	public void testDragBrickBelowCompositeBrick() {
		assertCorrectInitialBrickSetup();

		startMovingMethodOfBrickListViewStub(firstBrickInScript);

		assertTrue(adapter.onItemMove(1, 2));
		assertTrue(adapter.onItemMove(2, 3));

		adapter.moveItemTo(3, firstBrickInScript);

		assertSame(script.getScriptBrick(), adapter.getItem(0));
		assertSame(compositeBrick, adapter.getItem(1));
		assertSame(compositeEndBrick, adapter.getItem(2));
		assertSame(firstBrickInScript, adapter.getItem(3));
		assertSame(lastBrickInScript, adapter.getItem(4));
	}

	@Test
	public void testDragBrickAboveCompositeBrick() {
		assertCorrectInitialBrickSetup();

		startMovingMethodOfBrickListViewStub(lastBrickInScript);

		assertTrue(adapter.onItemMove(4, 3));
		assertTrue(adapter.onItemMove(3, 2));
		assertTrue(adapter.onItemMove(2, 1));

		adapter.moveItemTo(1, lastBrickInScript);

		assertSame(script.getScriptBrick(), adapter.getItem(0));
		assertSame(lastBrickInScript, adapter.getItem(1));
		assertSame(firstBrickInScript, adapter.getItem(2));
		assertSame(compositeBrick, adapter.getItem(3));
		assertSame(compositeEndBrick, adapter.getItem(4));
	}

	@Test
	public void testDragBrickIntoEmptyCompositeBrickFromAbove() {
		assertCorrectInitialBrickSetup();

		startMovingMethodOfBrickListViewStub(firstBrickInScript);

		assertTrue(adapter.onItemMove(1, 2));

		adapter.moveItemTo(2, firstBrickInScript);

		assertSame(script.getScriptBrick(), adapter.getItem(0));
		assertSame(compositeBrick, adapter.getItem(1));
		assertSame(firstBrickInScript, adapter.getItem(2));
		assertSame(compositeEndBrick, adapter.getItem(3));
		assertSame(lastBrickInScript, adapter.getItem(4));
	}

	@Test
	public void testDragBrickIntoEmptyCompositeBrickFromBelow() {
		assertCorrectInitialBrickSetup();

		startMovingMethodOfBrickListViewStub(lastBrickInScript);

		assertTrue(adapter.onItemMove(4, 3));

		adapter.moveItemTo(3, lastBrickInScript);

		assertSame(script.getScriptBrick(), adapter.getItem(0));
		assertSame(firstBrickInScript, adapter.getItem(1));
		assertSame(compositeBrick, adapter.getItem(2));
		assertSame(lastBrickInScript, adapter.getItem(3));
		assertSame(compositeEndBrick, adapter.getItem(4));
	}

	@Test
	public void testDragBrickIntoNonEmptyCompositeBrickFromAbove() {
		assertCorrectInitialBrickSetup();
		Brick nestedBrick = new BrickStub();

		compositeBrick.getNestedBricks().add(nestedBrick);

		adapter.updateItems(sprite);

		assertSame(script.getScriptBrick(), adapter.getItem(0));
		assertSame(firstBrickInScript, adapter.getItem(1));
		assertSame(compositeBrick, adapter.getItem(2));
		assertSame(nestedBrick, adapter.getItem(3));
		assertSame(compositeEndBrick, adapter.getItem(4));
		assertSame(lastBrickInScript, adapter.getItem(5));

		startMovingMethodOfBrickListViewStub(firstBrickInScript);

		assertTrue(adapter.onItemMove(1, 2));
		assertTrue(adapter.onItemMove(2, 3));

		adapter.moveItemTo(3, firstBrickInScript);

		assertSame(script.getScriptBrick(), adapter.getItem(0));
		assertSame(compositeBrick, adapter.getItem(1));
		assertSame(nestedBrick, adapter.getItem(2));
		assertSame(firstBrickInScript, adapter.getItem(3));
		assertSame(compositeEndBrick, adapter.getItem(4));
		assertSame(lastBrickInScript, adapter.getItem(5));
	}

	@Test
	public void testDragBrickIntoNonEmptyCompositeBrickFromBelow() {
		assertCorrectInitialBrickSetup();
		Brick nestedBrick = new BrickStub();

		compositeBrick.getNestedBricks().add(nestedBrick);

		adapter.updateItems(sprite);

		assertSame(script.getScriptBrick(), adapter.getItem(0));
		assertSame(firstBrickInScript, adapter.getItem(1));
		assertSame(compositeBrick, adapter.getItem(2));
		assertSame(nestedBrick, adapter.getItem(3));
		assertSame(compositeEndBrick, adapter.getItem(4));
		assertSame(lastBrickInScript, adapter.getItem(5));

		startMovingMethodOfBrickListViewStub(lastBrickInScript);

		assertTrue(adapter.onItemMove(5, 4));

		adapter.moveItemTo(4, lastBrickInScript);

		assertSame(script.getScriptBrick(), adapter.getItem(0));
		assertSame(firstBrickInScript, adapter.getItem(1));
		assertSame(compositeBrick, adapter.getItem(2));
		assertSame(nestedBrick, adapter.getItem(3));
		assertSame(lastBrickInScript, adapter.getItem(4));
		assertSame(compositeEndBrick, adapter.getItem(5));
	}

	@Test
	public void testDragEmptyCompositeBrickDownToLastPosition() {
		assertCorrectInitialBrickSetup();

		startMovingMethodOfBrickListViewStub(compositeBrick);

		assertTrue(adapter.onItemMove(2, 3));

		adapter.moveItemTo(3, compositeBrick);

		assertSame(script.getScriptBrick(), adapter.getItem(0));
		assertSame(firstBrickInScript, adapter.getItem(1));
		assertSame(lastBrickInScript, adapter.getItem(2));
		assertSame(compositeBrick, adapter.getItem(3));
		assertSame(compositeEndBrick, adapter.getItem(4));
	}

	@Test
	public void testDragEmptyCompositeBrickUpToFirstPosition() {
		assertCorrectInitialBrickSetup();

		startMovingMethodOfBrickListViewStub(compositeBrick);

		assertTrue(adapter.onItemMove(2, 1));

		adapter.moveItemTo(1, compositeBrick);

		assertSame(script.getScriptBrick(), adapter.getItem(0));
		assertSame(compositeBrick, adapter.getItem(1));
		assertSame(compositeEndBrick, adapter.getItem(2));
		assertSame(firstBrickInScript, adapter.getItem(3));
		assertSame(lastBrickInScript, adapter.getItem(4));
	}

	@Test
	public void testDragNonEmptyCompositeBrickDownToLastPosition() {
		assertCorrectInitialBrickSetup();
		Brick nestedBrick = new BrickStub();

		compositeBrick.getNestedBricks().add(nestedBrick);

		adapter.updateItems(sprite);

		assertSame(script.getScriptBrick(), adapter.getItem(0));
		assertSame(firstBrickInScript, adapter.getItem(1));
		assertSame(compositeBrick, adapter.getItem(2));
		assertSame(nestedBrick, adapter.getItem(3));
		assertSame(compositeEndBrick, adapter.getItem(4));
		assertSame(lastBrickInScript, adapter.getItem(5));

		startMovingMethodOfBrickListViewStub(compositeBrick);

		assertTrue(adapter.onItemMove(2, 3));

		adapter.moveItemTo(3, compositeBrick);

		assertSame(script.getScriptBrick(), adapter.getItem(0));
		assertSame(firstBrickInScript, adapter.getItem(1));
		assertSame(lastBrickInScript, adapter.getItem(2));
		assertSame(compositeBrick, adapter.getItem(3));
		assertSame(nestedBrick, adapter.getItem(4));
		assertSame(compositeEndBrick, adapter.getItem(5));
	}

	@Test
	public void testDragNonEmptyCompositeBrickUpToFirstPosition() {
		assertCorrectInitialBrickSetup();
		Brick nestedBrick = new BrickStub();

		compositeBrick.getNestedBricks().add(nestedBrick);

		adapter.updateItems(sprite);

		assertSame(script.getScriptBrick(), adapter.getItem(0));
		assertSame(firstBrickInScript, adapter.getItem(1));
		assertSame(compositeBrick, adapter.getItem(2));
		assertSame(nestedBrick, adapter.getItem(3));
		assertSame(compositeEndBrick, adapter.getItem(4));
		assertSame(lastBrickInScript, adapter.getItem(5));

		startMovingMethodOfBrickListViewStub(compositeBrick);

		assertTrue(adapter.onItemMove(2, 1));

		adapter.moveItemTo(1, compositeBrick);

		assertSame(script.getScriptBrick(), adapter.getItem(0));
		assertSame(compositeBrick, adapter.getItem(1));
		assertSame(nestedBrick, adapter.getItem(2));
		assertSame(compositeEndBrick, adapter.getItem(3));
		assertSame(firstBrickInScript, adapter.getItem(4));
		assertSame(lastBrickInScript, adapter.getItem(5));
	}

	private void assertCorrectInitialBrickSetup() {
		assertSame(script.getScriptBrick(), adapter.getItem(0));
		assertSame(firstBrickInScript, adapter.getItem(1));
		assertSame(compositeBrick, adapter.getItem(2));
		assertSame(compositeEndBrick, adapter.getItem(3));
		assertSame(lastBrickInScript, adapter.getItem(4));
	}

	private void startMovingMethodOfBrickListViewStub(Brick brickToMove) {
		List<Brick> flatList = new ArrayList<>();
		brickToMove.addToFlatList(flatList);
		flatList.remove(0);
		adapter.removeItems(flatList);
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
