/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

public class GlideToBrick extends FormulaBrick {
	private static final long serialVersionUID = 1L;

	public GlideToBrick() {
		addAllowedBrickField(BrickField.X_DESTINATION);
		addAllowedBrickField(BrickField.Y_DESTINATION);
		addAllowedBrickField(BrickField.DURATION_IN_SECONDS);
	}

	public GlideToBrick(int xDestinationValue, int yDestinationValue, int durationInMilliSecondsValue) {
		initializeBrickFields(new Formula(xDestinationValue), new Formula(yDestinationValue), new Formula(
				durationInMilliSecondsValue / 1000.0));
	}

	public GlideToBrick(Formula xDestination, Formula yDestination, Formula durationInSeconds) {
		initializeBrickFields(xDestination, yDestination, durationInSeconds);
	}

	private void initializeBrickFields(Formula xDestination, Formula yDestination, Formula durationInSeconds) {
		addAllowedBrickField(BrickField.X_DESTINATION);
		addAllowedBrickField(BrickField.Y_DESTINATION);
		addAllowedBrickField(BrickField.DURATION_IN_SECONDS);
		setFormulaWithBrickField(BrickField.X_DESTINATION, xDestination);
		setFormulaWithBrickField(BrickField.Y_DESTINATION, yDestination);
		setFormulaWithBrickField(BrickField.DURATION_IN_SECONDS, durationInSeconds);
	}

	public void setXDestination(Formula xDestination) {
		setFormulaWithBrickField(BrickField.X_DESTINATION, xDestination);
	}

	public void setYDestination(Formula yDestination) {
		setFormulaWithBrickField(BrickField.Y_DESTINATION, yDestination);
	}

	@Override
	public int getRequiredResources() {
		return getFormulaWithBrickField(BrickField.X_DESTINATION).getRequiredResources() | getFormulaWithBrickField(BrickField.Y_DESTINATION).getRequiredResources()
				| getFormulaWithBrickField(BrickField.DURATION_IN_SECONDS).getRequiredResources();
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.glideTo(sprite, getFormulaWithBrickField(BrickField.X_DESTINATION),
				getFormulaWithBrickField(BrickField.Y_DESTINATION),
				getFormulaWithBrickField(BrickField.DURATION_IN_SECONDS)));
		return null;
	}
}
