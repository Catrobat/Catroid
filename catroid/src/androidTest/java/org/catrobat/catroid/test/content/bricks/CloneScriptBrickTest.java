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

package org.catrobat.catroid.test.content.bricks;

import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.common.NfcTagData;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.CollisionScript;
import org.catrobat.catroid.content.RaspiInterruptScript;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenBackgroundChangesScript;
import org.catrobat.catroid.content.WhenClonedScript;
import org.catrobat.catroid.content.WhenConditionScript;
import org.catrobat.catroid.content.WhenGamepadButtonScript;
import org.catrobat.catroid.content.WhenNfcScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.WhenTouchDownScript;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.junit.Test;

import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertSame;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CloneScriptBrickTest {

	@Test
	public void testCloneBroadcastReceiverBrick() throws CloneNotSupportedException {
		BroadcastScript script = new BroadcastScript("broadCastMessage");
		ScriptBrick brick = script.getScriptBrick();
		assertIsClone((ScriptBrick) brick.clone(), brick);
	}

	@Test
	public void testCloneCollisionReceiverBrick() throws CloneNotSupportedException {
		CollisionScript script = new CollisionScript(null);
		ScriptBrick brick = script.getScriptBrick();
		assertIsClone((ScriptBrick) brick.clone(), brick);
	}

	@Test
	public void testCloneWhenRaspiPinChangedBrick() throws CloneNotSupportedException {
		RaspiInterruptScript script = new RaspiInterruptScript(
				Integer.toString(BrickValues.RASPI_DIGITAL_INITIAL_PIN_NUMBER), BrickValues.RASPI_EVENTS[0]);
		ScriptBrick brick = script.getScriptBrick();
		assertIsClone((ScriptBrick) brick.clone(), brick);
	}

	@Test
	public void testCloneWhenStartedBrick() throws CloneNotSupportedException {
		StartScript script = new StartScript();
		ScriptBrick brick = script.getScriptBrick();
		assertIsClone((ScriptBrick) brick.clone(), brick);
	}

	@Test
	public void testCloneWhenBackgroundChangesBrick() throws CloneNotSupportedException {
		WhenBackgroundChangesScript script = new WhenBackgroundChangesScript();
		ScriptBrick brick = script.getScriptBrick();
		assertIsClone((ScriptBrick) brick.clone(), brick);
	}

	@Test
	public void testCloneWhenClonedBrick() throws CloneNotSupportedException {
		WhenClonedScript script = new WhenClonedScript();
		ScriptBrick brick = script.getScriptBrick();
		assertIsClone((ScriptBrick) brick.clone(), brick);
	}

	@Test
	public void testCloneWhenConditionBrick() throws CloneNotSupportedException {
		WhenConditionScript script = new WhenConditionScript();
		ScriptBrick brick = script.getScriptBrick();
		assertIsClone((ScriptBrick) brick.clone(), brick);
	}

	@Test
	public void testCloneWhenGamepadButtonBrick() throws CloneNotSupportedException {
		WhenGamepadButtonScript script = new WhenGamepadButtonScript("cast_gamepad_A");
		ScriptBrick brick = script.getScriptBrick();
		assertIsClone((ScriptBrick) brick.clone(), brick);
	}

	@Test
	public void testCloneWhenNfcBrick() throws CloneNotSupportedException {
		WhenNfcScript script = new WhenNfcScript(new NfcTagData());
		ScriptBrick brick = script.getScriptBrick();
		assertIsClone((ScriptBrick) brick.clone(), brick);
	}

	@Test
	public void testCloneWhenBrick() throws CloneNotSupportedException {
		WhenScript script = new WhenScript();
		ScriptBrick brick = script.getScriptBrick();
		assertIsClone((ScriptBrick) brick.clone(), brick);
	}

	@Test
	public void testCloneWhenTouchDownBrick() throws CloneNotSupportedException {
		WhenTouchDownScript script = new WhenTouchDownScript();
		ScriptBrick brick = script.getScriptBrick();
		assertIsClone((ScriptBrick) brick.clone(), brick);
	}

	private void assertIsClone(ScriptBrick clone, ScriptBrick brick) {
		assertNotSame(clone, brick);

		Script cloneScript = clone.getScript();
		Script originalScript = brick.getScript();

		assertThat(cloneScript, is(instanceOf(originalScript.getClass())));
		assertNotSame(cloneScript, originalScript);

		assertSame(clone.getScript().getScriptBrick(), clone);
		assertSame(brick.getScript().getScriptBrick(), brick);
	}
}
