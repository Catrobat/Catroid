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

package org.catrobat.catroid.content;

import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.UserDefinedReceiverBrick;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.content.eventids.UserDefinedBrickEventId;
import org.catrobat.catroid.formulaeditor.UserData;

import java.util.List;
import java.util.UUID;

import androidx.annotation.VisibleForTesting;

public class UserDefinedScript extends Script {

	private static final long serialVersionUID = 1L;
	private UUID userDefinedBrickID;
	private List<Object> userDefinedBrickInputs;
	private Boolean screenRefresh = true;

	public UserDefinedScript(UUID userDefinedBrickID) {
		this.userDefinedBrickID = userDefinedBrickID;
	}

	@VisibleForTesting
	public UserDefinedScript() {
	}

	@Override
	public ScriptBrick getScriptBrick() {
		if (scriptBrick == null) {
			scriptBrick = new UserDefinedReceiverBrick(this);
		}
		return scriptBrick;
	}

	@Override
	public EventId createEventId(Sprite sprite) {
		return new UserDefinedBrickEventId(userDefinedBrickID);
	}

	public void setUserDefinedBrickInputs(List<Object> userDefinedBrickInputs) {
		this.userDefinedBrickInputs = userDefinedBrickInputs;
	}

	public UserData getUserDefinedBrickInput(String name) {
		for (Object variable : userDefinedBrickInputs) {
			if (variable instanceof UserData && ((UserData) (variable)).getName().equals(name)) {
				return (UserData) variable;
			}
		}
		return null;
	}

	public UUID getUserDefinedBrickID() {
		return userDefinedBrickID;
	}

	public void setScreenRefresh(Boolean screenRefresh) {
		this.screenRefresh = screenRefresh;
	}

	public Boolean getScreenRefresh() {
		return screenRefresh;
	}
}
