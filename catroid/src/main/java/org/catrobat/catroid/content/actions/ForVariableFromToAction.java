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

package org.catrobat.catroid.content.actions;

import android.util.Log;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.formulaeditor.UserVariable;

public class ForVariableFromToAction extends LoopAction {

	private UserVariable controlVariable;
	private Formula from;
	private Formula to;
	private Scope scope;
	private boolean isCurrentLoopInitialized = false;
	private boolean isRepeatActionInitialized = false;
	private int fromValue;
	private int toValue;
	private int step = 1;

	@Override
	public boolean delegate(float delta) {

		if (!isRepeatActionInitialized && !interpretParameters()) {
			return true;
		}

		if (!isCurrentLoopInitialized) {
			setCurrentTime(0.f);
			isCurrentLoopInitialized = true;
		}

		setCurrentTime(getCurrentTime() + delta);

		if (action != null && action.act(delta) && !isLoopDelayNeeded()) {
			if (!(controlVariable.getValue() instanceof Double)
					|| (step > 0 && (double) controlVariable.getValue() >= toValue)
					|| (step < 0 && (double) controlVariable.getValue() <= toValue)) {
				return true;
			}
			changeControlVariable(step);
			isCurrentLoopInitialized = false;
			action.restart();
		}
		return false;
	}

	@Override
	public void restart() {
		isCurrentLoopInitialized = false;
		isRepeatActionInitialized = false;
		super.restart();
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	public void setRange(Formula from, Formula to) {
		this.from = from;
		this.to = to;
	}

	public void setControlVariable(UserVariable variable) {
		controlVariable = variable;
	}

	private boolean interpretParameters() {
		isRepeatActionInitialized = true;
		try {
			Double fromInterpretation = from == null ? Double.valueOf(0d)
					: from.interpretDouble(scope);
			fromValue = fromInterpretation.intValue();
			Double toInterpretation = to == null ? Double.valueOf(0d) : to.interpretDouble(scope);
			toValue = toInterpretation.intValue();
			setStepValue();
			setControlVariable(fromValue);
			return true;
		} catch (InterpretationException interpretationException) {
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", interpretationException);
			return false;
		}
	}

	private void setStepValue() {
		step = (fromValue <= toValue) ? 1 : -1;
	}

	private void setControlVariable(int value) {
		controlVariable.setValue((double) value);
	}

	private void changeControlVariable(int value) {
		controlVariable.setValue((double) controlVariable.getValue() + (double) value);
	}
}
