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

package org.catrobat.catroid.test.content.script;

import org.catrobat.catroid.common.NfcTagData;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.CollisionScript;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenBackgroundChangesScript;
import org.catrobat.catroid.content.WhenClonedScript;
import org.catrobat.catroid.content.WhenConditionScript;
import org.catrobat.catroid.content.WhenGamepadButtonScript;
import org.catrobat.catroid.content.WhenNfcScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.WhenTouchDownScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.SetYBrick;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertSame;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class CloneScriptTest {

	@Test
	public void testCloneBroadcastScript() throws CloneNotSupportedException {
		BroadcastScript script = new BroadcastScript("broadCastMessage");
		script.getScriptBrick(); // side magic
		addBricksToScript(script);
		assertIsClone(script.clone(), script);
	}

	@Test
	public void testCloneCollisionScript() throws CloneNotSupportedException {
		CollisionScript script = new CollisionScript(null);
		script.getScriptBrick(); // side magic
		addBricksToScript(script);
		assertIsClone(script.clone(), script);
	}

	@Test
	public void testCloneStartScript() throws CloneNotSupportedException {
		StartScript script = new StartScript();
		script.getScriptBrick(); // side magic
		addBricksToScript(script);
		assertIsClone(script.clone(), script);
	}

	@Test
	public void testCloneWhenBackgroundChangesScript() throws CloneNotSupportedException {
		WhenBackgroundChangesScript script = new WhenBackgroundChangesScript();
		script.getScriptBrick(); // side magic
		addBricksToScript(script);
		assertIsClone(script.clone(), script);
	}

	@Test
	public void testCloneWhenClonedScript() throws CloneNotSupportedException {
		WhenClonedScript script = new WhenClonedScript();
		script.getScriptBrick(); // side magic
		addBricksToScript(script);
		assertIsClone(script.clone(), script);
	}

	@Test
	public void testCloneWhenConditionScript() throws CloneNotSupportedException {
		WhenConditionScript script = new WhenConditionScript();
		script.getScriptBrick(); // side magic
		addBricksToScript(script);
		assertIsClone(script.clone(), script);
	}

	@Test
	public void testCloneWhenGamepadButtonScript() throws CloneNotSupportedException {
		WhenGamepadButtonScript script = new WhenGamepadButtonScript("cast_gamepad_A");
		script.getScriptBrick(); // side magic
		addBricksToScript(script);
		assertIsClone(script.clone(), script);
	}

	@Test
	public void testCloneWhenNfcScript() throws CloneNotSupportedException {
		WhenNfcScript script = new WhenNfcScript(new NfcTagData());
		script.getScriptBrick(); // side magic
		addBricksToScript(script);
		assertIsClone(script.clone(), script);
	}

	@Test
	public void testCloneWhenScript() throws CloneNotSupportedException {
		WhenScript script = new WhenScript();
		script.getScriptBrick(); // side magic
		addBricksToScript(script);
		assertIsClone(script.clone(), script);
	}

	@Test
	public void testCloneWhenTouchDownScript() throws CloneNotSupportedException {
		WhenTouchDownScript script = new WhenTouchDownScript();
		script.getScriptBrick(); // side magic
		addBricksToScript(script);
		assertIsClone(script.clone(), script);
	}

	private void addBricksToScript(Script script) {
		script.addBrick(new SetXBrick(100));
		script.addBrick(new SetYBrick(100));
	}

	private void assertIsClone(Script clone, Script script) {
		assertNotSame(clone, script);
		assertNotSame(clone.getScriptBrick(), script.getScriptBrick());

		assertNotSame(clone.getScriptBrick().getScript(), script.getScriptBrick().getScript());

		assertSame(clone.getScriptBrick().getScript(), clone);
		assertSame(script.getScriptBrick().getScript(), script);

		List<Brick> cloneBrickList = clone.getBrickList();
		List<Brick> scriptBrickList = script.getBrickList();

		assertEquals(cloneBrickList.size(), scriptBrickList.size());

		for (int i = 0; i < cloneBrickList.size(); i++) {
			Brick clonedBrick = cloneBrickList.get(i);
			Brick originalBrick = scriptBrickList.get(i);
			assertThat(clonedBrick, is(instanceOf(originalBrick.getClass())));
			assertNotSame(clonedBrick, originalBrick);
		}
	}
}
