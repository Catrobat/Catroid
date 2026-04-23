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

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.embroidery.TripleRunningStitch;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;

public class TripleStitchAction extends TemporalAction {

	private Scope scope;
	private Formula steps;

	@Override
	protected void update(float delta) {
		int stepsInterpretation = 0;
		try {
			if (steps != null) {
				stepsInterpretation = steps.interpretInteger(scope);
			}
		} catch (InterpretationException interpretationException) {
			stepsInterpretation = 0;
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", interpretationException);
		}
		this.scope.getSprite().runningStitch.activateStitching(scope.getSprite(), new TripleRunningStitch(scope.getSprite(),
				stepsInterpretation));
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	public void setSteps(Formula steps) {
		this.steps = steps;
	}
}
