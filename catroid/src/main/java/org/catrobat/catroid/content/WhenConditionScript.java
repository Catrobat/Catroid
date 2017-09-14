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
package org.catrobat.catroid.content;

import android.util.Log;

import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ConcurrentFormulaHashMap;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.WhenConditionBrick;
import org.catrobat.catroid.utils.CrashReporter;

import java.util.ArrayList;

public class WhenConditionScript extends Script {

	private static final long serialVersionUID = 1L;

	private ConcurrentFormulaHashMap formulaMap;

	public WhenConditionScript(ScriptBrick brick) {
		this.brick = brick;
	}

	@Override
	public Script copyScriptForSprite(Sprite copySprite) {
		WhenConditionScript cloneScript = new WhenConditionScript(null);

		try {
			cloneScript.formulaMap = this.formulaMap.clone();
		} catch (CloneNotSupportedException e) {
			Log.e(getClass().getSimpleName(), "clone exception should never happen");
			CrashReporter.logException(e);
		}

		doCopy(copySprite, cloneScript);
		return cloneScript;
	}

	@Override
	public ScriptBrick getScriptBrick() {
		if (brick == null) {
			brick = new WhenConditionBrick(this);
		}
		return brick;
	}

	public ConcurrentFormulaHashMap getFormulaMap() {
		if (formulaMap == null) {
			formulaMap = new ConcurrentFormulaHashMap();
		}
		return formulaMap;
	}

	@Override
	public int getRequiredResources() {
		int resources = Brick.NO_RESOURCES;
		resources |= getScriptBrick().getRequiredResources();
		ArrayList<Brick> brickList = getBrickList();
		for (Brick brick : brickList) {
			resources |= brick.getRequiredResources();
		}
		return resources;
	}
}
