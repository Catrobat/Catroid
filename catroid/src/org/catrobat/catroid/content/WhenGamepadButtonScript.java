/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.content.bricks.WhenGampadButtonBrick;

import java.util.List;

public class WhenGamepadButtonScript extends Script {

	private static final long serialVersionUID = 1L;
	private String action;

	public WhenGamepadButtonScript() {
		super();
		this.action = CatroidApplication.getAppContext().getString(R.string.cast_gamepad_A);
	}

	public WhenGamepadButtonScript(WhenGampadButtonBrick brick) {
		this.brick = brick;
	}

	public WhenGamepadButtonScript(String action) {
		super();
		this.action = action;
	}

	@Override
	protected Object readResolve() {
		super.readResolve();
		return this;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}

	@Override
	public ScriptBrick getScriptBrick() {
		if (brick == null) {
			brick = new WhenGampadButtonBrick(this);
		}

		return brick;
	}

	@Override
	public int getRequiredResources() {
		return Brick.CAST_REQUIRED | super.getRequiredResources();
	}

	@Override
	//CAST
	//public Script copyScriptForSprite(Sprite copySprite, List<UserBrick> preCopiedUserBricks) {
	public Script copyScriptForSprite(Sprite copySprite) {
		WhenGamepadButtonScript cloneScript = new WhenGamepadButtonScript();
		//CAST
		//doCopy(copySprite, cloneScript, preCopiedUserBricks);
		doCopy(copySprite, cloneScript);
		return cloneScript;
	}
}
