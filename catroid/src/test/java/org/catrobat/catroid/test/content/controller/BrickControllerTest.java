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

package org.catrobat.catroid.test.content.controller;

import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ControlStructureBrick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicEndBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.LoopEndlessBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.ui.recyclerview.controller.BrickController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static junit.framework.Assert.assertFalse;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class BrickControllerTest {

	private boolean containsControlBricksWithInvalidReferences(List<Brick> bricks) {
		for (Brick brick : bricks) {
			if (brick instanceof ControlStructureBrick && hasInvalidReference((ControlStructureBrick) brick, bricks)) {
				return true;
			}
		}
		return false;
	}

	private boolean hasInvalidReference(ControlStructureBrick brick, List<Brick> bricks) {
		List<Brick> brickParts = brick.getAllParts();
		if (brickParts.contains(null)) {
			return true;
		}
		for (Brick brickPart : brickParts) {
			if (!(bricks.contains(brickPart))) {
				return true;
			}
		}
		return false;
	}

	@Test
	public void testSettingIfElseBrickReferencesInCorrectScript() {
		Script script = new StartScript();

		script.addBrick(new SetXBrick());
		IfLogicBeginBrick outerBeginBrick = new IfLogicBeginBrick();
		script.addBrick(outerBeginBrick);
		script.addBrick(new SetXBrick());
		IfLogicElseBrick outerElseBrick = new IfLogicElseBrick(null);
		script.addBrick(outerElseBrick);
		script.addBrick(new SetXBrick());

		IfLogicBeginBrick innerBeginBrick = new IfLogicBeginBrick();
		script.addBrick(innerBeginBrick);
		script.addBrick(new SetXBrick());
		IfLogicElseBrick innerElseBrick = new IfLogicElseBrick(null);
		script.addBrick(innerElseBrick);
		script.addBrick(new SetXBrick());
		IfLogicEndBrick innerEndBrick = new IfLogicEndBrick(null, null);
		script.addBrick(innerEndBrick);

		IfLogicEndBrick outerEndBrick = new IfLogicEndBrick(null, null);
		script.addBrick(outerEndBrick);

		new BrickController().setControlBrickReferences(script.getBrickList());
		assertFalse(containsControlBricksWithInvalidReferences(script.getBrickList()));

		assertEquals(innerBeginBrick, innerElseBrick.getIfBeginBrick());
		assertEquals(innerBeginBrick, innerEndBrick.getIfBeginBrick());
		assertEquals(innerElseBrick, innerBeginBrick.getIfElseBrick());
		assertEquals(innerElseBrick, innerEndBrick.getIfElseBrick());
		assertEquals(innerEndBrick, innerBeginBrick.getIfEndBrick());
		assertEquals(innerEndBrick, innerElseBrick.getIfEndBrick());

		assertEquals(outerBeginBrick, outerElseBrick.getIfBeginBrick());
		assertEquals(outerBeginBrick, outerEndBrick.getIfBeginBrick());
		assertEquals(outerElseBrick, outerBeginBrick.getIfElseBrick());
		assertEquals(outerElseBrick, outerEndBrick.getIfElseBrick());
		assertEquals(outerEndBrick, outerBeginBrick.getIfEndBrick());
		assertEquals(outerEndBrick, outerElseBrick.getIfEndBrick());
	}

	@Test
	public void testSettingIfElseBrickReferencesInScriptMissingBeginBrick() {
		Script script = new StartScript();

		script.addBrick(new SetXBrick());
		script.addBrick(new SetXBrick());
		IfLogicElseBrick outerElseBrick = new IfLogicElseBrick(null);
		script.addBrick(outerElseBrick);
		script.addBrick(new SetXBrick());

		IfLogicBeginBrick innerBeginBrick = new IfLogicBeginBrick();
		script.addBrick(innerBeginBrick);
		script.addBrick(new SetXBrick());
		IfLogicElseBrick innerElseBrick = new IfLogicElseBrick(null);
		script.addBrick(innerElseBrick);
		script.addBrick(new SetXBrick());
		IfLogicEndBrick innerEndBrick = new IfLogicEndBrick(null, null);
		script.addBrick(innerEndBrick);

		IfLogicEndBrick outerEndBrick = new IfLogicEndBrick(null, null);
		script.addBrick(outerEndBrick);

		new BrickController().setControlBrickReferences(script.getBrickList());
		assertFalse(containsControlBricksWithInvalidReferences(script.getBrickList()));

		assertFalse(script.containsBrick(outerElseBrick));
		assertFalse(script.containsBrick(outerEndBrick));

		assertEquals(innerBeginBrick, innerElseBrick.getIfBeginBrick());
		assertEquals(innerBeginBrick, innerEndBrick.getIfBeginBrick());
		assertEquals(innerElseBrick, innerBeginBrick.getIfElseBrick());
		assertEquals(innerElseBrick, innerEndBrick.getIfElseBrick());
		assertEquals(innerEndBrick, innerBeginBrick.getIfEndBrick());
		assertEquals(innerEndBrick, innerElseBrick.getIfEndBrick());
	}

	@Test
	public void testSettingIfElseBrickReferencesInScriptMissingNestedBeginBrick() {
		Script script = new StartScript();

		script.addBrick(new SetXBrick());
		IfLogicBeginBrick outerBeginBrick = new IfLogicBeginBrick();
		script.addBrick(outerBeginBrick);
		script.addBrick(new SetXBrick());
		IfLogicElseBrick outerElseBrick = new IfLogicElseBrick(null);
		script.addBrick(outerElseBrick);
		script.addBrick(new SetXBrick());

		script.addBrick(new SetXBrick());
		IfLogicElseBrick innerElseBrick = new IfLogicElseBrick(null);
		script.addBrick(innerElseBrick);
		script.addBrick(new SetXBrick());
		IfLogicEndBrick innerEndBrick = new IfLogicEndBrick(null, null);
		script.addBrick(innerEndBrick);

		IfLogicEndBrick outerEndBrick = new IfLogicEndBrick(null, null);
		script.addBrick(outerEndBrick);

		new BrickController().setControlBrickReferences(script.getBrickList());
		assertFalse(containsControlBricksWithInvalidReferences(script.getBrickList()));

		assertFalse(script.containsBrick(innerElseBrick));
		assertFalse(script.containsBrick(outerEndBrick));

		assertEquals(outerBeginBrick, outerElseBrick.getIfBeginBrick());
		assertEquals(outerBeginBrick, innerEndBrick.getIfBeginBrick());
		assertEquals(outerElseBrick, outerBeginBrick.getIfElseBrick());
		assertEquals(outerElseBrick, innerEndBrick.getIfElseBrick());
		assertEquals(innerEndBrick, outerBeginBrick.getIfEndBrick());
		assertEquals(innerEndBrick, outerBeginBrick.getIfEndBrick());
	}

	@Test
	public void testSettingIfElseBrickReferencesInScriptMissingElseBrick() {
		Script script = new StartScript();

		script.addBrick(new SetXBrick());
		IfLogicBeginBrick outerBeginBrick = new IfLogicBeginBrick();
		script.addBrick(outerBeginBrick);
		script.addBrick(new SetXBrick());
		IfLogicElseBrick outerElseBrick = new IfLogicElseBrick(null);
		script.addBrick(outerElseBrick);
		script.addBrick(new SetXBrick());

		IfLogicBeginBrick innerBeginBrick = new IfLogicBeginBrick();
		script.addBrick(innerBeginBrick);
		script.addBrick(new SetXBrick());
		script.addBrick(new SetXBrick());
		IfLogicEndBrick innerEndBrick = new IfLogicEndBrick(null, null);
		script.addBrick(innerEndBrick);

		IfLogicEndBrick outerEndBrick = new IfLogicEndBrick(null, null);
		script.addBrick(outerEndBrick);

		new BrickController().setControlBrickReferences(script.getBrickList());
		assertFalse(containsControlBricksWithInvalidReferences(script.getBrickList()));

		assertFalse(script.containsBrick(innerBeginBrick));
		assertFalse(script.containsBrick(outerEndBrick));

		assertEquals(outerBeginBrick, outerElseBrick.getIfBeginBrick());
		assertEquals(outerBeginBrick, innerEndBrick.getIfBeginBrick());
		assertEquals(outerElseBrick, outerBeginBrick.getIfElseBrick());
		assertEquals(outerElseBrick, innerEndBrick.getIfElseBrick());
		assertEquals(innerEndBrick, outerBeginBrick.getIfEndBrick());
		assertEquals(innerEndBrick, outerElseBrick.getIfEndBrick());
	}

	@Test
	public void testSettingIfElseBrickReferencesInScriptMissingEndBrick() {
		Script script = new StartScript();

		script.addBrick(new SetXBrick());
		IfLogicBeginBrick outerBeginBrick = new IfLogicBeginBrick();
		script.addBrick(outerBeginBrick);
		script.addBrick(new SetXBrick());
		IfLogicElseBrick outerElseBrick = new IfLogicElseBrick(null);
		script.addBrick(outerElseBrick);
		script.addBrick(new SetXBrick());

		IfLogicBeginBrick innerBeginBrick = new IfLogicBeginBrick();
		script.addBrick(innerBeginBrick);
		script.addBrick(new SetXBrick());
		IfLogicElseBrick innerElseBrick = new IfLogicElseBrick(null);
		script.addBrick(innerElseBrick);
		script.addBrick(new SetXBrick());
		IfLogicEndBrick innerEndBrick = new IfLogicEndBrick(null, null);
		script.addBrick(innerEndBrick);

		new BrickController().setControlBrickReferences(script.getBrickList());
		assertFalse(containsControlBricksWithInvalidReferences(script.getBrickList()));

		assertFalse(script.containsBrick(outerBeginBrick));
		assertFalse(script.containsBrick(outerElseBrick));

		assertEquals(innerBeginBrick, innerElseBrick.getIfBeginBrick());
		assertEquals(innerBeginBrick, innerEndBrick.getIfBeginBrick());
		assertEquals(innerElseBrick, innerBeginBrick.getIfElseBrick());
		assertEquals(innerElseBrick, innerEndBrick.getIfElseBrick());
		assertEquals(innerEndBrick, innerBeginBrick.getIfEndBrick());
		assertEquals(innerEndBrick, innerElseBrick.getIfEndBrick());
	}

	@Test
	public void testSettingIfThenBrickReferencesInCorrectScript() {
		Script script = new StartScript();

		script.addBrick(new SetXBrick());
		IfThenLogicBeginBrick outerBeginBrick = new IfThenLogicBeginBrick();
		script.addBrick(outerBeginBrick);
		script.addBrick(new SetXBrick());

		IfThenLogicBeginBrick innerBeginBrick = new IfThenLogicBeginBrick();
		script.addBrick(innerBeginBrick);
		script.addBrick(new SetXBrick());

		script.addBrick(new SetXBrick());
		IfThenLogicEndBrick innerEndBrick = new IfThenLogicEndBrick(null);
		script.addBrick(innerEndBrick);

		IfThenLogicEndBrick outerEndBrick = new IfThenLogicEndBrick(null);
		script.addBrick(outerEndBrick);

		new BrickController().setControlBrickReferences(script.getBrickList());
		assertFalse(containsControlBricksWithInvalidReferences(script.getBrickList()));

		assertEquals(innerBeginBrick, innerEndBrick.getIfBeginBrick());
		assertEquals(innerEndBrick, innerBeginBrick.getIfThenEndBrick());

		assertEquals(outerBeginBrick, outerEndBrick.getIfBeginBrick());
		assertEquals(outerEndBrick, outerBeginBrick.getIfThenEndBrick());
	}

	@Test
	public void testSettingIfThenBrickReferencesInScriptMissingBeginBrick() {
		Script script = new StartScript();

		script.addBrick(new SetXBrick());
		script.addBrick(new SetXBrick());

		IfThenLogicBeginBrick innerBeginBrick = new IfThenLogicBeginBrick();
		script.addBrick(innerBeginBrick);
		script.addBrick(new SetXBrick());

		script.addBrick(new SetXBrick());
		IfThenLogicEndBrick innerEndBrick = new IfThenLogicEndBrick(null);
		script.addBrick(innerEndBrick);

		IfThenLogicEndBrick outerEndBrick = new IfThenLogicEndBrick(null);
		script.addBrick(outerEndBrick);

		new BrickController().setControlBrickReferences(script.getBrickList());
		assertFalse(containsControlBricksWithInvalidReferences(script.getBrickList()));

		assertFalse(script.containsBrick(outerEndBrick));

		assertEquals(innerBeginBrick, innerEndBrick.getIfBeginBrick());
		assertEquals(innerEndBrick, innerBeginBrick.getIfThenEndBrick());
	}

	@Test
	public void testSettingIfThenBrickReferencesInScriptMissingNestedBeginBrick() {
		Script script = new StartScript();

		script.addBrick(new SetXBrick());
		IfThenLogicBeginBrick outerBeginBrick = new IfThenLogicBeginBrick();
		script.addBrick(outerBeginBrick);
		script.addBrick(new SetXBrick());

		script.addBrick(new SetXBrick());

		script.addBrick(new SetXBrick());
		IfThenLogicEndBrick innerEndBrick = new IfThenLogicEndBrick(null);
		script.addBrick(innerEndBrick);

		IfThenLogicEndBrick outerEndBrick = new IfThenLogicEndBrick(null);
		script.addBrick(outerEndBrick);

		new BrickController().setControlBrickReferences(script.getBrickList());
		assertFalse(containsControlBricksWithInvalidReferences(script.getBrickList()));

		assertFalse(script.containsBrick(outerEndBrick));

		assertEquals(outerBeginBrick, innerEndBrick.getIfBeginBrick());
		assertEquals(innerEndBrick, outerBeginBrick.getIfThenEndBrick());
	}

	@Test
	public void testSettingIfThenBrickReferencesInScriptMissingEndBrick() {
		Script script = new StartScript();

		script.addBrick(new SetXBrick());
		IfThenLogicBeginBrick outerBeginBrick = new IfThenLogicBeginBrick();
		script.addBrick(outerBeginBrick);
		script.addBrick(new SetXBrick());

		IfThenLogicBeginBrick innerBeginBrick = new IfThenLogicBeginBrick();
		script.addBrick(innerBeginBrick);
		script.addBrick(new SetXBrick());

		script.addBrick(new SetXBrick());
		IfThenLogicEndBrick innerEndBrick = new IfThenLogicEndBrick(null);
		script.addBrick(innerEndBrick);

		new BrickController().setControlBrickReferences(script.getBrickList());
		assertFalse(containsControlBricksWithInvalidReferences(script.getBrickList()));

		assertFalse(script.containsBrick(outerBeginBrick));

		assertEquals(innerBeginBrick, innerEndBrick.getIfBeginBrick());
		assertEquals(innerEndBrick, innerBeginBrick.getIfThenEndBrick());
	}

	@Test
	public void testSettingLoopBrickReferencesInSimpleCorrectScript() {
		Script script = new StartScript();

		ForeverBrick beginBrick = new ForeverBrick();
		script.addBrick(beginBrick);
		script.addBrick(new SetXBrick());

		LoopEndBrick endBrick = new LoopEndlessBrick(null);
		script.addBrick(endBrick);

		new BrickController().setControlBrickReferences(script.getBrickList());

		assertFalse(containsControlBricksWithInvalidReferences(script.getBrickList()));

		assertEquals(beginBrick, endBrick.getLoopBeginBrick());
		assertEquals(endBrick, beginBrick.getLoopEndBrick());
	}

	@Test
	public void testSettingLoopBrickReferencesInCorrectScript() {
		Script script = new StartScript();

		script.addBrick(new SetXBrick());
		ForeverBrick outerBeginBrick = new ForeverBrick();
		script.addBrick(outerBeginBrick);
		script.addBrick(new SetXBrick());

		RepeatBrick innerBeginBrick = new RepeatBrick();
		script.addBrick(innerBeginBrick);
		script.addBrick(new SetXBrick());

		script.addBrick(new SetXBrick());
		LoopEndBrick innerEndBrick = new LoopEndBrick(null);
		script.addBrick(innerEndBrick);
		script.addBrick(new SetXBrick());

		LoopEndBrick outerEndBrick = new LoopEndlessBrick(null);
		script.addBrick(outerEndBrick);

		new BrickController().setControlBrickReferences(script.getBrickList());
		assertFalse(containsControlBricksWithInvalidReferences(script.getBrickList()));

		assertEquals(innerBeginBrick, innerEndBrick.getLoopBeginBrick());
		assertEquals(innerEndBrick, innerBeginBrick.getLoopEndBrick());

		assertEquals(outerBeginBrick, outerEndBrick.getLoopBeginBrick());
		assertEquals(outerEndBrick, outerBeginBrick.getLoopEndBrick());
	}

	@Test
	public void testSettingLoopBrickReferencesInScriptMissingEndBrick() {
		Script script = new StartScript();

		script.addBrick(new SetXBrick());
		ForeverBrick outerBeginBrick = new ForeverBrick();
		script.addBrick(outerBeginBrick);
		script.addBrick(new SetXBrick());

		RepeatBrick innerBeginBrick = new RepeatBrick();
		script.addBrick(innerBeginBrick);
		script.addBrick(new SetXBrick());

		script.addBrick(new SetXBrick());
		LoopEndBrick innerEndBrick = new LoopEndBrick(null);
		script.addBrick(innerEndBrick);

		new BrickController().setControlBrickReferences(script.getBrickList());
		assertFalse(containsControlBricksWithInvalidReferences(script.getBrickList()));

		assertFalse(script.containsBrick(outerBeginBrick));

		assertEquals(innerBeginBrick, innerEndBrick.getLoopBeginBrick());
		assertEquals(innerEndBrick, innerBeginBrick.getLoopEndBrick());
	}
}
