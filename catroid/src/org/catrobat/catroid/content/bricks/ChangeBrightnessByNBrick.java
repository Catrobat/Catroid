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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

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
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_change_brightness_checkbox);
		final Brick brickInstance = this;

		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});
		TextView textX = (TextView) view.findViewById(R.id.brick_change_brightness_prototype_text_view);
		TextView editX = (TextView) view.findViewById(R.id.brick_change_brightness_edit_text);
		getFormulaWithBrickField(BrickField.BRIGHTNESS_CHANGE).setTextFieldId(R.id.brick_change_brightness_edit_text);
		getFormulaWithBrickField(BrickField.BRIGHTNESS_CHANGE).refreshTextField(view);

		textX.setVisibility(View.GONE);
		editX.setVisibility(View.VISIBLE);

		editX.setOnClickListener(this);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_change_brightness, null);
		TextView textChangeBrightness = (TextView) prototypeView
				.findViewById(R.id.brick_change_brightness_prototype_text_view);
		textChangeBrightness.setText(String.valueOf(BrickValues.CHANGE_BRITHNESS_BY));
		return prototypeView;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_change_brightness_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textBrightness = (TextView) view.findViewById(R.id.brick_change_brightness_label);
			TextView textBy = (TextView) view.findViewById(R.id.brick_change_brightness_by_textview);
			TextView editBrightness = (TextView) view.findViewById(R.id.brick_change_brightness_edit_text);
			textBrightness.setTextColor(textBrightness.getTextColors().withAlpha(alphaValue));
			textBy.setTextColor(textBy.getTextColors().withAlpha(alphaValue));
			editBrightness.setTextColor(editBrightness.getTextColors().withAlpha(alphaValue));
			editBrightness.getBackground().setAlpha(alphaValue);

			this.alphaValue = alphaValue;
		}

		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.changeBrightnessByN(sprite,
				getFormulaWithBrickField(BrickField.BRIGHTNESS_CHANGE)));
		return null;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		FormulaEditorFragment.showFragment(view, this, BrickField.BRIGHTNESS_CHANGE);
	}
}
