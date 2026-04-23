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
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class GlideToBrick extends VisualPlacementBrick {

	private static final long serialVersionUID = 1L;

	public GlideToBrick() {
		addAllowedBrickField(BrickField.X_DESTINATION, R.id.brick_glide_to_edit_text_x);
		addAllowedBrickField(BrickField.Y_DESTINATION, R.id.brick_glide_to_edit_text_y);
		addAllowedBrickField(BrickField.DURATION_IN_SECONDS, R.id.brick_glide_to_edit_text_duration);
	}

	public GlideToBrick(int xDestinationValue, int yDestinationValue, int durationInMilliSecondsValue) {
		this(new Formula(xDestinationValue),
				new Formula(yDestinationValue),
				new Formula(durationInMilliSecondsValue / 1000.0));
	}

	public GlideToBrick(Formula xDestination, Formula yDestination, Formula durationInSeconds) {
		this();
		setFormulaWithBrickField(BrickField.X_DESTINATION, xDestination);
		setFormulaWithBrickField(BrickField.Y_DESTINATION, yDestination);
		setFormulaWithBrickField(BrickField.DURATION_IN_SECONDS, durationInSeconds);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_glide_to;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);
		setSecondsLabel(view, BrickField.DURATION_IN_SECONDS);
		return view;
	}

	@Override
	public BrickField getDefaultBrickField() {
		return BrickField.DURATION_IN_SECONDS;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createGlideToAction(sprite, sequence,
				getFormulaWithBrickField(BrickField.X_DESTINATION),
				getFormulaWithBrickField(BrickField.Y_DESTINATION),
				getFormulaWithBrickField(BrickField.DURATION_IN_SECONDS)));
	}

	@Override
	public BrickField getXBrickField() {
		return BrickField.X_DESTINATION;
	}

	@Override
	public BrickField getYBrickField() {
		return BrickField.Y_DESTINATION;
	}

	@Override
	public int getXEditTextId() {
		return R.id.brick_glide_to_edit_text_x;
	}

	@Override
	public int getYEditTextId() {
		return R.id.brick_glide_to_edit_text_y;
	}
}
