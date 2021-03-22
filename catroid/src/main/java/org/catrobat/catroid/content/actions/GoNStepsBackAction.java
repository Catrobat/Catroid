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
package org.catrobat.catroid.content.actions;

import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;

public class GoNStepsBackAction extends TemporalAction {

	private Scope scope;
	private Formula steps;

	@Override
	protected void update(float delta) {
		Float stepsValue;
		try {
			stepsValue = steps == null ? Float.valueOf(0f) : steps.interpretFloat(scope);
		} catch (InterpretationException interpretationException) {
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", interpretationException);
			return;
		}

		int zPosition = scope.getSprite().look.getZIndex();
		if (stepsValue.intValue() > 0 && (zPosition - stepsValue.intValue()) < Constants.Z_INDEX_FIRST_SPRITE) {
			scope.getSprite().look.setZIndex(Constants.Z_INDEX_FIRST_SPRITE);
		} else if (stepsValue.intValue() < 0 && (zPosition - stepsValue.intValue()) < zPosition) {
			toFront(delta);
		} else {
			goNStepsBack(stepsValue.intValue());
		}
	}

	private void toFront(float delta) {
		Action comeToFrontAction = scope.getSprite().getActionFactory().createComeToFrontAction(scope.getSprite());
		comeToFrontAction.act(delta);
	}

	private void goNStepsBack(int steps) {
		int zPosition = scope.getSprite().look.getZIndex();
		int newSpriteZIndex = Math.max(zPosition - steps, Constants.Z_INDEX_FIRST_SPRITE);
		scope.getSprite().look.setZIndex(newSpriteZIndex);
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	public void setSteps(Formula steps) {
		this.steps = steps;
	}
}
