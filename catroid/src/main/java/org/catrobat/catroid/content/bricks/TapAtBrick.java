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
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class TapAtBrick extends VisualPlacementBrick {

	private static final long serialVersionUID = 1L;

	public TapAtBrick() {
		addAllowedBrickField(BrickField.X_POSITION, R.id.brick_tap_at_edit_text_x);
		addAllowedBrickField(BrickField.Y_POSITION, R.id.brick_tap_at_edit_text_y);
	}

	public TapAtBrick(int xPositionValue, int yPositionValue) {
		this(new Formula(xPositionValue), new Formula(yPositionValue));
	}

	public TapAtBrick(Formula xPosition, Formula yPosition) {
		this();
		setFormulaWithBrickField(BrickField.X_POSITION, xPosition);
		setFormulaWithBrickField(BrickField.Y_POSITION, yPosition);
	}

	@Override
	public BrickField getDefaultBrickField() {
		return BrickField.X_POSITION;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_tap_at;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createTapAtAction(sprite,
				getFormulaWithBrickField(BrickField.X_POSITION),
				getFormulaWithBrickField(BrickField.Y_POSITION)));
	}

	@Override
	public BrickField getXBrickField() {
		return BrickField.X_POSITION;
	}

	@Override
	public BrickField getYBrickField() {
		return BrickField.Y_POSITION;
	}

	@Override
	public int getXEditTextId() {
		return R.id.brick_tap_at_edit_text_x;
	}

	@Override
	public int getYEditTextId() {
		return R.id.brick_tap_at_edit_text_y;
	}
}
