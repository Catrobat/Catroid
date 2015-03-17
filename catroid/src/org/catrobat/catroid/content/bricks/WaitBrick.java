/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;

import java.util.List;

public class WaitBrick extends FormulaBrick {
	private static final long serialVersionUID = 1L;

	public WaitBrick() {
		addAllowedBrickField(BrickField.TIME_TO_WAIT_IN_SECONDS);
	}

	public WaitBrick(int timeToWaitInMillisecondsValue) {
		initializeBrickFields(new Formula(timeToWaitInMillisecondsValue / 1000.0));
	}

	public WaitBrick(Formula timeToWaitInSecondsFormula) {
		initializeBrickFields(timeToWaitInSecondsFormula);
	}

	private void initializeBrickFields(Formula timeToWaitInSeconds) {
		addAllowedBrickField(BrickField.TIME_TO_WAIT_IN_SECONDS);
		setFormulaWithBrickField(BrickField.TIME_TO_WAIT_IN_SECONDS, timeToWaitInSeconds);
	}

	@Override
	public int getRequiredResources() {
		return getFormulaWithBrickField(BrickField.TIME_TO_WAIT_IN_SECONDS).getRequiredResources();
	}

	public Formula getTimeToWait() {
		return getFormulaWithBrickField(BrickField.TIME_TO_WAIT_IN_SECONDS);
	}

	public void setTimeToWait(Formula timeToWaitInSeconds) {
		setFormulaWithBrickField(BrickField.TIME_TO_WAIT_IN_SECONDS, timeToWaitInSeconds);
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.delay(sprite, getFormulaWithBrickField(BrickField.TIME_TO_WAIT_IN_SECONDS)));
		return null;
	}
}
