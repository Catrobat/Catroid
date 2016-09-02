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
package org.catrobat.catroid.content.actions;

import android.util.Log;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;

public class RepeatUntilAction extends com.badlogic.gdx.scenes.scene2d.actions.RepeatAction {

	private int executedCount = 0;
	private Sprite sprite;
	private boolean isCurrentLoopInitialized = false;
	private static final float LOOP_DELAY = 0.02f;
	private float currentTime = 0f;
	private Formula repeatCondition;

	boolean isValidConditionFormula() {
		try {
			if (repeatCondition == null) {
				return false;
			}

			repeatCondition.interpretDouble(sprite);
		} catch (InterpretationException interpretationException) {
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.",
					interpretationException);
			return false;
		}

		return true;
	}

	boolean isConditionTrue() {
		try {
			return repeatCondition.interpretDouble(sprite).intValue() != 0;
		} catch (InterpretationException interpretationException) {
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.",
					interpretationException);
			return true;
		}
	}

	@Override
	public boolean delegate(float delta) {

		if (!isValidConditionFormula()) {
			return true;
		}

		if (!isCurrentLoopInitialized) {
			if (isConditionTrue()) {
				return true;
			}
			currentTime = 0f;
			isCurrentLoopInitialized = true;
		}

		currentTime += delta;

		if (action.act(delta) && currentTime >= LOOP_DELAY) {

			executedCount++;
			if (isConditionTrue()) {
				return true;
			}

			isCurrentLoopInitialized = false;

			if (action != null) {
				action.restart();
			}
		}

		return false;
	}

	@Override
	public void restart() {
		isCurrentLoopInitialized = false;
		executedCount = 0;
		super.restart();
	}

	public void setRepeatCondition(Formula repeatCondition) {
		this.repeatCondition = repeatCondition;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public int getExecutedCount() {
		return executedCount;
	}
}
