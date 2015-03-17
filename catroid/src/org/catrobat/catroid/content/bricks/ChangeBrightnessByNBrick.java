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

public class ChangeBrightnessByNBrick extends FormulaBrick {
	private static final long serialVersionUID = 1L;

	public ChangeBrightnessByNBrick() {
		addAllowedBrickField(BrickField.BRIGHTNESS_CHANGE);
	}

	public ChangeBrightnessByNBrick(double changeBrightnessValue) {
		initializeBrickFields(new Formula(changeBrightnessValue));
	}

	public ChangeBrightnessByNBrick(Formula changeBrightness) {
		initializeBrickFields(changeBrightness);
	}

	private void initializeBrickFields(Formula changeBrightness) {
		addAllowedBrickField(BrickField.BRIGHTNESS_CHANGE);
		setFormulaWithBrickField(BrickField.BRIGHTNESS_CHANGE, changeBrightness);
	}

	@Override
	public int getRequiredResources() {
		return getFormulaWithBrickField(BrickField.BRIGHTNESS_CHANGE).getRequiredResources();
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.changeBrightnessByN(sprite,
				getFormulaWithBrickField(BrickField.BRIGHTNESS_CHANGE)));
		return null;
	}
}
