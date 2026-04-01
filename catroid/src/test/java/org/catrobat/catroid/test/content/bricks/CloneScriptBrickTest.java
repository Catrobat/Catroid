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

import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.RaspiInterruptScript;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.UserDefinedScript;
import org.catrobat.catroid.content.WhenBackgroundChangesScript;
import org.catrobat.catroid.content.WhenBounceOffScript;
import org.catrobat.catroid.content.WhenClonedScript;
import org.catrobat.catroid.content.WhenConditionScript;
import org.catrobat.catroid.content.WhenGamepadButtonScript;
import org.catrobat.catroid.content.WhenNfcScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.WhenTouchDownScript;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertSame;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class CloneScriptBrickTest {

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{BroadcastScript.class.getSimpleName(), new BroadcastScript("test")},
				{WhenBounceOffScript.class.getSimpleName(), new WhenBounceOffScript("test")},
				{RaspiInterruptScript.class.getSimpleName(), new RaspiInterruptScript("testPin", "testEvent")},
				{StartScript.class.getSimpleName(), new StartScript()},
				{WhenBackgroundChangesScript.class.getSimpleName(), new WhenBackgroundChangesScript()},
				{WhenClonedScript.class.getSimpleName(), new WhenClonedScript()},
				{WhenConditionScript.class.getSimpleName(), new WhenConditionScript()},
				{WhenGamepadButtonScript.class.getSimpleName(), new WhenGamepadButtonScript("testAction")},
				{WhenNfcScript.class.getSimpleName(), new WhenNfcScript()},
				{WhenScript.class.getSimpleName(), new WhenScript()},
				{WhenTouchDownScript.class.getSimpleName(), new WhenTouchDownScript()},
				{UserDefinedScript.class.getSimpleName(), new UserDefinedScript()},
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public Script script;

	@Test
	public void testScriptIsClonedWithScriptBrick() throws CloneNotSupportedException {
		ScriptBrick brick = script.getScriptBrick();
		ScriptBrick clone = (ScriptBrick) brick.clone();

		assertNotSame(clone, brick);

		Script cloneScript = clone.getScript();

		assertNotNull(cloneScript);

		Script originalScript = brick.getScript();

		assertThat(cloneScript, is(instanceOf(originalScript.getClass())));
		assertNotSame(cloneScript, originalScript);

		assertSame(clone.getScript().getScriptBrick(), clone);
		assertSame(brick.getScript().getScriptBrick(), brick);
	}
}
