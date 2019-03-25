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
package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenTouchDownScript;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;

import java.util.List;

public class WhenTouchDownBrick extends BrickBaseType implements ScriptBrick {

	private WhenTouchDownScript whenTouchDownScript;
	private static final long serialVersionUID = 1L;

	public WhenTouchDownBrick() {
		this(new WhenTouchDownScript());
	}

	public WhenTouchDownBrick(WhenTouchDownScript whenTouchDownScript) {
		whenTouchDownScript.setScriptBrick(this);
		commentedOut = whenTouchDownScript.isCommentedOut();
		this.whenTouchDownScript = whenTouchDownScript;
	}

	@Override
	public Script getScript() {
		return whenTouchDownScript;
	}

	@Override
	public BrickBaseType clone() throws CloneNotSupportedException {
		WhenTouchDownBrick clone = (WhenTouchDownBrick) super.clone();
		clone.whenTouchDownScript = (WhenTouchDownScript) whenTouchDownScript.clone();
		clone.whenTouchDownScript.setScriptBrick(clone);
		return clone;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_screen_touched;
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		return null;
	}

	@Override
	public void setCommentedOut(boolean commentedOut) {
		super.setCommentedOut(commentedOut);
		getScript().setCommentedOut(commentedOut);
	}
}
