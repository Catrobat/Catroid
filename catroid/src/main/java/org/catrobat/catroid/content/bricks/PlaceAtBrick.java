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

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.ui.UiUtils;
import org.catrobat.catroid.ui.recyclerview.fragment.ScriptFragment;
import org.catrobat.catroid.visualplacement.VisualPlacementActivity;

import static org.catrobat.catroid.content.bricks.Brick.BrickField.X_POSITION;
import static org.catrobat.catroid.content.bricks.Brick.BrickField.Y_POSITION;
import static org.catrobat.catroid.ui.SpriteActivity.EXTRA_BRICK_HASH;
import static org.catrobat.catroid.ui.SpriteActivity.REQUEST_CODE_VISUAL_PLACEMENT;

public class PlaceAtBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	public PlaceAtBrick() {
		addAllowedBrickField(X_POSITION, R.id.brick_place_at_edit_text_x);
		addAllowedBrickField(Y_POSITION, R.id.brick_place_at_edit_text_y);
	}

	public PlaceAtBrick(int xPositionValue, int yPositionValue) {
		this(new Formula(xPositionValue), new Formula(yPositionValue));
	}

	public PlaceAtBrick(Formula xPosition, Formula yPosition) {
		this();
		setFormulaWithBrickField(X_POSITION, xPosition);
		setFormulaWithBrickField(Y_POSITION, yPosition);
	}

	@Override
	public void showFormulaEditorToEditFormula(final View view) {
		Context context = view.getContext();
		Fragment currentFragment = UiUtils.getActivityFromView(view).getSupportFragmentManager()
				.findFragmentById(R.id.fragment_container);

		boolean showVisualPlacementDialog = currentFragment instanceof ScriptFragment
				&& (view.getId() == R.id.brick_place_at_edit_text_x || view.getId() == R.id.brick_place_at_edit_text_y)
				&& areAllBrickFieldsNumbers();

		if (showVisualPlacementDialog) {
			String[] optionStrings = {
					context.getString(R.string.brick_place_at_option_place_visually),
					context.getString(R.string.brick_context_dialog_formula_edit_brick)};

			new AlertDialog.Builder(context).setItems(optionStrings, (dialog, which) -> {
				switch (which) {
					case 0:
						placeVisually();
						break;
					case 1:
						super.showFormulaEditorToEditFormula(view);
						break;
				}
			}).show();
		} else {
			super.showFormulaEditorToEditFormula(view);
		}
	}

	private boolean areAllBrickFieldsNumbers() {
		return isBrickFieldANumber(X_POSITION)
				&& isBrickFieldANumber(Y_POSITION);
	}

	public void placeVisually() {
		AppCompatActivity activity = UiUtils.getActivityFromView(view);
		if (!(activity instanceof SpriteActivity)) {
			return;
		}

		Intent intent = new Intent(view.getContext(), VisualPlacementActivity.class);
		intent.putExtra(EXTRA_BRICK_HASH, hashCode());
		activity.startActivityForResult(intent, REQUEST_CODE_VISUAL_PLACEMENT);
	}

	public void setCoordinates(int x, int y) {
		setFormulaWithBrickField(X_POSITION, new Formula(x));
		setFormulaWithBrickField(Y_POSITION, new Formula(y));
	}

	@Override
	public BrickField getDefaultBrickField() {
		return X_POSITION;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_place_at;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createPlaceAtAction(sprite,
				getFormulaWithBrickField(X_POSITION),
				getFormulaWithBrickField(Y_POSITION)));
	}
}
