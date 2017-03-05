/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public class ChangeBrightnessByNBrick extends FormulaBrick {
	private static final long serialVersionUID = 1L;

	private transient View prototypeView;

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
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_change_brightness, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		setCheckboxView(R.id.brick_change_brightness_checkbox);
		TextView editX = (TextView) view.findViewById(R.id.brick_change_brightness_edit_text);
		getFormulaWithBrickField(BrickField.BRIGHTNESS_CHANGE).setTextFieldId(R.id.brick_change_brightness_edit_text);
		getFormulaWithBrickField(BrickField.BRIGHTNESS_CHANGE).refreshTextField(view);

		editX.setOnClickListener(this);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_change_brightness, null);
		TextView textChangeBrightness = (TextView) prototypeView
				.findViewById(R.id.brick_change_brightness_edit_text);
		textChangeBrightness.setText(Utils.getNumberStringForBricks(BrickValues.CHANGE_BRITHNESS_BY));
		return prototypeView;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createChangeBrightnessByNAction(sprite,
				getFormulaWithBrickField(BrickField.BRIGHTNESS_CHANGE)));
		return null;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		FormulaEditorFragment.showFragment(view, this, BrickField.BRIGHTNESS_CHANGE);
	}

	@Override
	public void updateReferenceAfterMerge(Scene into, Scene from) {
	}
}
