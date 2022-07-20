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

package org.catrobat.catroid.formulaeditor;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.UserDefinedScript;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;

public final class UserDataWrapper {

	public static UserVariable getUserVariable(String name, Scope scope) {
		UserVariable userVariable = null;
		if (scope.getSprite() != null) {
			userVariable = scope.getSprite().getUserVariable(name);
		}
		if (scope.getProject() != null && userVariable == null) {
			userVariable = scope.getProject().getUserVariable(name);
			if (userVariable == null) {
				userVariable = scope.getProject().getMultiplayerVariable(name);
			}
		}
		return userVariable;
	}

	public static UserList getUserList(String name, Scope scope) {
		UserList userList = null;
		if (scope != null) {
			userList = scope.getSprite().getUserList(name);
		}
		if (scope.getProject() != null && userList == null) {
			return scope.getProject().getUserList(name);
		}
		return userList;
	}

	public static UserData getUserDefinedBrickInput(String value, SequenceAction sequence) {
		if (sequence instanceof ScriptSequenceAction
				&& ((ScriptSequenceAction) sequence).getScript() instanceof UserDefinedScript) {
			Script userDefinedScript = ((ScriptSequenceAction) sequence).getScript();
			return ((UserDefinedScript) userDefinedScript).getUserDefinedBrickInput(value);
		}
		return null;
	}

	public static void resetAllUserData(Project project) {
		project.resetUserData();
		for (Scene scene : project.getSceneList()) {
			for (Sprite sprite : scene.getSpriteList()) {
				sprite.resetUserData();
			}
		}
	}

	private UserDataWrapper() {
		throw new AssertionError("No.");
	}
}
