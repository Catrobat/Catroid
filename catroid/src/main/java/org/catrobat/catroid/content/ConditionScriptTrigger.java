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

package org.catrobat.catroid.content;

import android.util.Log;

import org.catrobat.catroid.content.eventids.WhenConditionEventId;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

public class ConditionScriptTrigger {
	@Retention(RetentionPolicy.SOURCE)
	@IntDef({TRIGGER_NOW, ALREADY_TRIGGERED})
	@interface TriggerStatus {
	}

	static final int TRIGGER_NOW = 0;
	static final int ALREADY_TRIGGERED = 1;
	private static final String TAG = ConditionScriptTrigger.class.getSimpleName();

	@TriggerStatus
	private int status = TRIGGER_NOW;
	private final Formula formula;

	ConditionScriptTrigger(Formula formula) {
		this.formula = formula;
	}

	void evaluateAndTriggerActions(Sprite sprite) {
		try {
			boolean conditionValue = formula.interpretBoolean(sprite);
			if (conditionValue) {
				triggerScript(sprite);
			} else {
				status = TRIGGER_NOW;
			}
		} catch (InterpretationException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	private void triggerScript(Sprite sprite) {
		if (status == TRIGGER_NOW) {
			EventWrapper eventWrapper = new EventWrapper(new WhenConditionEventId(formula), false);
			sprite.look.fire(eventWrapper);
			status = ALREADY_TRIGGERED;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ConditionScriptTrigger)) {
			return false;
		}
		ConditionScriptTrigger that = (ConditionScriptTrigger) o;
		return formula.equals(that.formula);
	}

	@Override
	public int hashCode() {
		return formula.hashCode();
	}
}
