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
package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;

import java.util.List;

public class WhenBrick extends BrickBaseType implements ScriptBrick {

	protected WhenScript whenScript;
	private static final long serialVersionUID = 1L;

	public WhenBrick() {
	}

	public WhenBrick(WhenScript whenScript) {
		this.whenScript = whenScript;

		if (whenScript != null && whenScript.isCommentedOut()) {
			setCommentedOut(true);
		}
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_when;
	}

	@Override
	public Brick clone() {
		return new WhenBrick(null);
	}

	@Override
	public Script getScriptSafe() {
		if (whenScript == null) {
			whenScript = new WhenScript();
		}

		return whenScript;
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		return null;
	}

	@Override
	public void setCommentedOut(boolean commentedOut) {
		super.setCommentedOut(commentedOut);
		getScriptSafe().setCommentedOut(commentedOut);
	}
}
