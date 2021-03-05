/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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
import android.content.Intent;
import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.ui.UiUtils;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.ui.recyclerview.fragment.ScriptFragment;
import org.catrobat.catroid.visualplacement.VisualPlacementActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import static org.catrobat.catroid.ui.SpriteActivity.EXTRA_BRICK_HASH;
import static org.catrobat.catroid.ui.SpriteActivity.EXTRA_X_TRANSFORM;
import static org.catrobat.catroid.ui.SpriteActivity.EXTRA_Y_TRANSFORM;
import static org.catrobat.catroid.ui.SpriteActivity.REQUEST_CODE_VISUAL_PLACEMENT;

public abstract class VisualPlacementBrick extends FormulaBrick {

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		AppCompatActivity activity = UiUtils.getActivityFromView(view);
		if (activity == null) {
			return;
		}
		Fragment currentFragment = activity.getSupportFragmentManager().findFragmentById(R.id.fragment_container);

		if (currentFragment instanceof FormulaEditorFragment) {
			super.showFormulaEditorToEditFormula(view);
			if (isVisualPlacement(view)) {
				showDialog(view, currentFragment);
			}
		} else if (currentFragment instanceof ScriptFragment) {
			if (isVisualPlacement(view)) {
				showDialog(view, currentFragment);
			} else {
				super.showFormulaEditorToEditFormula(view);
			}
		}
	}

	private void showDialog(View view, Fragment currentFragment) {
		Context context = view.getContext();

		String[] optionStrings = {
				context.getString(R.string.brick_option_place_visually),
				context.getString(R.string.brick_context_dialog_formula_edit_brick)};

		new AlertDialog.Builder(context).setItems(optionStrings, (dialog, which) -> {
			switch (which) {
				case 0:
					placeVisually(getXBrickField(), getYBrickField());
					break;
				case 1:
					if (currentFragment instanceof ScriptFragment) {
						super.showFormulaEditorToEditFormula(view);
					}
					break;
			}
		}).show();
	}

	public void placeVisually(BrickField brickFieldX, BrickField brickFieldY) {
		Intent intent = generateIntentForVisualPlacement(brickFieldX, brickFieldY);
		startVisualPlacementActivity(intent);
	}

	public Intent generateIntentForVisualPlacement(BrickField brickFieldX, BrickField brickFieldY) {
		Formula formulax = getFormulaWithBrickField(brickFieldX);
		Formula formulay = getFormulaWithBrickField(brickFieldY);
		Intent intent = new Intent(view.getContext(), VisualPlacementActivity.class);
		intent.putExtra(EXTRA_BRICK_HASH, hashCode());
		int xValue;
		int yValue;
		try {
			ProjectManager projectManager = ProjectManager.getInstance();
			Scope scope = new Scope(projectManager.getCurrentProject(),
					projectManager.getCurrentSprite(), null);
			xValue = formulax.interpretInteger(scope);
			yValue = formulay.interpretInteger(scope);
		} catch (InterpretationException interpretationException) {
			xValue = 0;
			yValue = 0;
		}
		intent.putExtra(EXTRA_X_TRANSFORM, xValue);
		intent.putExtra(EXTRA_Y_TRANSFORM, yValue);

		return intent;
	}

	private void startVisualPlacementActivity(Intent intent) {
		AppCompatActivity activity = UiUtils.getActivityFromView(view);
		if (!(activity instanceof SpriteActivity)) {
			return;
		}
		activity.startActivityForResult(intent, REQUEST_CODE_VISUAL_PLACEMENT);
	}

	public void setCoordinates(int x, int y) {
		setFormulaWithBrickField(getXBrickField(), new Formula(x));
		setFormulaWithBrickField(getYBrickField(), new Formula(y));
	}

	public boolean isVisualPlacement(View view) {
		return isCorrectTextField(view) && areAllBrickFieldsNumbers();
	}

	public boolean areAllBrickFieldsNumbers() {
		return isBrickFieldANumber(getXBrickField())
				&& isBrickFieldANumber(getYBrickField());
	}

	private boolean isCorrectTextField(View view) {
		return (view.getId() == getXEditTextId() || view.getId() == getYEditTextId());
	}

	public abstract BrickField getXBrickField();
	public abstract BrickField getYBrickField();
	public abstract int getXEditTextId();
	public abstract int getYEditTextId();
}
