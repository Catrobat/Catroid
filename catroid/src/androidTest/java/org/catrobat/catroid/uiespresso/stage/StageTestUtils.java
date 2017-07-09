/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.stage;

import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.uiespresso.util.actions.CustomActions;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;

public final class StageTestUtils {

	private static final double EPSILON = 0.001;

	// Suppress default constructor for noninstantiability
	private StageTestUtils() {
		throw new AssertionError();
	}

	public static void addBroadcastScriptSettingUserVariableToSprite(Sprite sprite, String message, UserVariable
			userVariable, double value) {
		Script broadcastScript = new BroadcastScript(message);
		SetVariableBrick setVariableBrickAfterBroadcast = new SetVariableBrick(new Formula(value), userVariable);
		broadcastScript.addBrick(setVariableBrickAfterBroadcast);
		sprite.addScript(broadcastScript);
	}

	public static Sprite createSpriteAndAddToProject(String name, Project project) {
		Sprite sprite = new Sprite(name);
		project.getDefaultScene().addSprite(sprite);
		return sprite;
	}

	public static Script createStartScriptAndAddToSprite(Sprite sprite) {
		Script startScript = new StartScript();
		sprite.addScript(startScript);
		return startScript;
	}

	public static Script createBroadcastScriptAndAddToSprite(String broadcastMessage, Sprite sprite) {
		Script broadcastScript = new BroadcastScript(broadcastMessage);
		sprite.addScript(broadcastScript);
		return broadcastScript;
	}

	public static boolean userVariableEqualsWithinTimeout(UserVariable userVariable, double expectedValue, int timeout) {
		int step = 10;
		for (; timeout > 0; timeout -= step) {
			if (Math.abs((double) userVariable.getValue() - expectedValue) < EPSILON) {
				return true;
			}
			onView(isRoot()).perform(CustomActions.wait(step));
		}
		return false;
	}

	public static boolean userVariableGreaterThanWithinTimeout(UserVariable userVariable, double expectedValue, int timeout) {
		int step = 10;
		for (; timeout > 0; timeout -= step) {
			if ((double) userVariable.getValue() > (expectedValue + EPSILON)) {
				return true;
			}
			onView(isRoot()).perform(CustomActions.wait(step));
		}
		return false;
	}
}
