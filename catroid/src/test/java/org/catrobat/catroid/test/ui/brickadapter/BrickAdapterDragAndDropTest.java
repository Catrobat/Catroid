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

package org.catrobat.catroid.test.ui.brickadapter;

import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenConditionScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.WhenTouchDownScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.IfElseLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.SetYBrick;
import org.catrobat.catroid.ui.recyclerview.adapter.BrickAdapter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(JUnit4.class)
public class BrickAdapterDragAndDropTest {

	private BrickAdapter adapter;
	private Sprite sprite;

	@Before
	public void setUp() {
		sprite = new Sprite("Test");

		StartScript startScript = new StartScript();

		IfElseLogicBeginBrick ifBeginBrick = new IfLogicBeginBrick();
		IfLogicElseBrick ifElseBrick = new IfLogicElseBrick(ifBeginBrick);
		IfLogicEndBrick ifEndBrick = new IfLogicEndBrick(ifBeginBrick, ifElseBrick);
		ifBeginBrick.setIfElseBrick(ifElseBrick);
		ifBeginBrick.setIfEndBrick(ifEndBrick);
		ifElseBrick.setIfEndBrick(ifEndBrick);

		startScript.addBrick(ifBeginBrick);
		startScript.addBrick(new SetXBrick());
		startScript.addBrick(ifElseBrick);
		startScript.addBrick(new SetYBrick());
		startScript.addBrick(ifEndBrick);

		ForeverBrick foreverBrick = new ForeverBrick();
		LoopEndBrick loopEndBrick = new LoopEndBrick(foreverBrick);
		foreverBrick.setLoopEndBrick(loopEndBrick);

		startScript.addBrick(foreverBrick);
		startScript.addBrick(new PlaceAtBrick());
		startScript.addBrick(loopEndBrick);

		WhenTouchDownScript touchDownScript = new WhenTouchDownScript();

		touchDownScript.addBrick(new SetLookBrick());
		touchDownScript.addBrick(new PlaySoundBrick());

		WhenScript whenScript = new WhenScript();

		whenScript.addBrick(new SetXBrick());
		whenScript.addBrick(new SetYBrick());

		WhenConditionScript whenConditionScript = new WhenConditionScript();

		sprite.addScript(startScript);
		sprite.addScript(touchDownScript);
		sprite.addScript(whenScript);
		sprite.addScript(whenConditionScript);

		adapter = new BrickAdapter(sprite);
	}

	@Test
	public void testDragFromOneScriptToAnother() {
		Script firstScript = sprite.getScript(0);
		Script secondScript = sprite.getScript(1);

		int firstPositionInSecondScript = 9;

		BrickBaseType brickToMove = (BrickBaseType) firstScript.getBrick(1);
		assertTrue(adapter.onItemMove(2, 3));
		assertTrue(adapter.onItemMove(3, 4));
		assertTrue(adapter.onItemMove(4, 5));
		assertTrue(adapter.onItemMove(5, 6));
		assertTrue(adapter.onItemMove(6, 7));
		assertTrue(adapter.onItemMove(7, 8));
		assertTrue(adapter.onItemMove(8, 9));
		adapter.moveItemsTo(firstPositionInSecondScript, Collections.singletonList(brickToMove));

		assertFalse(firstScript.containsBrick(brickToMove));
		assertTrue(secondScript.containsBrick(brickToMove));
		assertEquals(adapter.getItem(9), brickToMove);
	}

	@Test
	public void testDragScriptDown() {
		Script scriptToMove = sprite.getScript(0);

		BrickBaseType brickToMove = (BrickBaseType) scriptToMove.getScriptBrick();

		List<Brick> bricksInScript = scriptToMove.getBrickList();
		List<BrickBaseType> bricksToMove = new ArrayList<>();
		bricksToMove.add(brickToMove);
		for (Brick brick : bricksInScript) {
			bricksToMove.add((BrickBaseType) brick);
		}
		adapter.removeItems(bricksToMove.subList(1, bricksToMove.size()));

		assertTrue(adapter.onItemMove(0, 1));
		assertTrue(adapter.onItemMove(1, 2));
		assertTrue(adapter.onItemMove(2, 3));
		assertTrue(adapter.onItemMove(3, 4));

		adapter.moveItemsTo(4, bricksToMove);
		assertEquals(2, sprite.getScriptIndex(scriptToMove));
	}

	@Test
	public void testDragScriptUp() {
		Script scriptToMove = sprite.getScript(2);

		BrickBaseType brickToMove = (BrickBaseType) scriptToMove.getScriptBrick();

		List<Brick> bricksInScript = scriptToMove.getBrickList();
		List<BrickBaseType> bricksToMove = new ArrayList<>();
		bricksToMove.add(brickToMove);
		for (Brick brick : bricksInScript) {
			bricksToMove.add((BrickBaseType) brick);
		}
		adapter.removeItems(bricksToMove.subList(1, bricksToMove.size()));

		assertTrue(adapter.onItemMove(12, 11));
		assertTrue(adapter.onItemMove(11, 10));
		assertTrue(adapter.onItemMove(10, 9));
		assertTrue(adapter.onItemMove(9, 8));

		adapter.moveItemsTo(8, bricksToMove);
		assertEquals(1, sprite.getScriptIndex(scriptToMove));
	}

	@Test
	public void testDragScriptBrickIntoOtherScriptWithoutControlStructure() {
		Script scriptToMove = sprite.getScript(3);
		Script scriptToSplit = sprite.getScript(2);

		SetYBrick brickToTakeOverFromOtherScript = (SetYBrick) sprite.getScript(2).getBrickList().get(1);

		List<Brick> bricksInScript = scriptToMove.getBrickList();

		assertTrue(bricksInScript.isEmpty());

		List<BrickBaseType> bricksToMove = new ArrayList<>();
		bricksToMove.add((BrickBaseType) scriptToMove.getScriptBrick());

		assertTrue(adapter.onItemMove(15, 14));

		adapter.moveItemsTo(14, bricksToMove);
		assertEquals(3, sprite.getScriptIndex(scriptToMove));
		assertTrue(scriptToMove.containsBrick(brickToTakeOverFromOtherScript));
		assertFalse(scriptToSplit.containsBrick(brickToTakeOverFromOtherScript));
	}

	@Test
	public void testDragScriptBrickIntoOtherScriptWithControlStructure() {
		Script scriptToMove = sprite.getScript(3);
		Script scriptToSplit = sprite.getScript(0);

		List<Brick> bricksInScript = scriptToMove.getBrickList();

		List<Brick> bricksToTakeOverFromOtherScript = new ArrayList<>();
		bricksToTakeOverFromOtherScript.add(scriptToSplit.getBrick(5));
		bricksToTakeOverFromOtherScript.add(scriptToSplit.getBrick(6));
		bricksToTakeOverFromOtherScript.add(scriptToSplit.getBrick(7));

		assertTrue(bricksInScript.isEmpty());

		List<BrickBaseType> bricksToMove = new ArrayList<>();
		bricksToMove.add((BrickBaseType) scriptToMove.getScriptBrick());

		assertTrue(adapter.onItemMove(12, 11));
		assertTrue(adapter.onItemMove(11, 10));
		assertTrue(adapter.onItemMove(10, 9));
		assertTrue(adapter.onItemMove(9, 8));
		assertTrue(adapter.onItemMove(8, 7));
		assertTrue(adapter.onItemMove(7, 6));
		assertTrue(adapter.onItemMove(6, 5));

		adapter.moveItemsTo(5, bricksToMove);
		assertEquals(2, sprite.getScriptIndex(scriptToMove));

		assertTrue(scriptToMove.getBrickList().containsAll(bricksToTakeOverFromOtherScript));
		assertFalse(scriptToSplit.getBrickList().containsAll(bricksToTakeOverFromOtherScript));
	}

	@Test
	public void testDragIfElseBrickOverIfBeginBrick() {
		int positionOfIfElseBrick = 3;
		assertThat(adapter.getItem(positionOfIfElseBrick), instanceOf(IfLogicElseBrick.class));
		int positionOfIfBeginBrick = 1;
		assertThat(adapter.getItem(positionOfIfBeginBrick), instanceOf(IfLogicBeginBrick.class));
		assertTrue(adapter.onItemMove(positionOfIfElseBrick, 2));
		assertFalse(adapter.onItemMove(2, positionOfIfBeginBrick));
	}

	@Test
	public void testDragIfElseBrickOverIfEndBrick() {
		int positionOfIfElseBrick = 3;
		assertThat(adapter.getItem(positionOfIfElseBrick), instanceOf(IfLogicElseBrick.class));
		int positionOfIfEndBrick = 5;
		assertThat(adapter.getItem(positionOfIfEndBrick), instanceOf(IfLogicEndBrick.class));
		assertTrue(adapter.onItemMove(positionOfIfElseBrick, 4));
		assertFalse(adapter.onItemMove(4, positionOfIfEndBrick));
	}

	@Test
	public void testDragIfEndBrickOverIfElseBrick() {
		int positionOfIfEndBrick = 5;
		assertThat(adapter.getItem(positionOfIfEndBrick), instanceOf(IfLogicEndBrick.class));
		int positionOfIfElseBrick = 3;
		assertThat(adapter.getItem(positionOfIfElseBrick), instanceOf(IfLogicElseBrick.class));
		assertTrue(adapter.onItemMove(positionOfIfEndBrick, 4));
		assertFalse(adapter.onItemMove(4, positionOfIfElseBrick));
	}

	@Test
	public void testDragLoopEndBrickOverLoopBeginBrick() {
		int positionOfLoopEndBrick = 8;
		assertThat(adapter.getItem(positionOfLoopEndBrick), instanceOf(LoopEndBrick.class));
		int positionOfForeverBrick = 6;
		assertThat(adapter.getItem(positionOfForeverBrick), instanceOf(ForeverBrick.class));
		assertTrue(adapter.onItemMove(positionOfLoopEndBrick, 7));
		assertFalse(adapter.onItemMove(7, positionOfForeverBrick));
	}

	@Test
	public void testDragLoopEndBrickToOtherScript() {
		int positionOfLoopEndBrick = 8;
		assertThat(adapter.getItem(positionOfLoopEndBrick), instanceOf(LoopEndBrick.class));
		int firstPositionInSecondScript = 9;
		assertThat(adapter.getItem(firstPositionInSecondScript), instanceOf(ScriptBrick.class));
		assertFalse(adapter.onItemMove(positionOfLoopEndBrick, firstPositionInSecondScript));
	}
}
