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

public class ChangeColorByNBrick extends FormulaBrick {
	private static final long serialVersionUID = 1L;

	private transient View prototypeView;

	public ChangeColorByNBrick() {
		addAllowedBrickField(BrickField.COLOR_CHANGE);
	}

	public ChangeColorByNBrick(Integer changeColorValue) {
		initializeBrickFields(new Formula(changeColorValue));
	}

	public ChangeColorByNBrick(Formula changeColor) {
		initializeBrickFields(changeColor);
	}

	private void initializeBrickFields(Formula changeColor) {
		addAllowedBrickField(BrickField.COLOR_CHANGE);
		setFormulaWithBrickField(BrickField.COLOR_CHANGE, changeColor);
	}

	@Override
	public int getRequiredResources() {
		return getFormulaWithBrickField(BrickField.COLOR_CHANGE).getRequiredResources();
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_change_color_by, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_change_color_by_checkbox);
		final Brick brickInstance = this;

		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});
		TextView textX = (TextView) view.findViewById(R.id.brick_change_color_by_prototype_text_view);
		TextView editX = (TextView) view.findViewById(R.id.brick_change_color_by_edit_text);
		getFormulaWithBrickField(BrickField.COLOR_CHANGE).setTextFieldId(R.id.brick_change_color_by_edit_text);
		getFormulaWithBrickField(BrickField.COLOR_CHANGE).refreshTextField(view);

		textX.setVisibility(View.GONE);
		editX.setVisibility(View.VISIBLE);

		editX.setOnClickListener(this);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_change_color_by, null);
		TextView textChangeColor = (TextView) prototypeView
				.findViewById(R.id.brick_change_color_by_prototype_text_view);
		textChangeColor.setText(String.valueOf(BrickValues.CHANGE_COLOR_BY));
		return prototypeView;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_change_color_by_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textColor = (TextView) view.findViewById(R.id.brick_change_color_by_label);
			TextView textBy = (TextView) view.findViewById(R.id.brick_change_color_by_by_textview);
			TextView editColor = (TextView) view.findViewById(R.id.brick_change_color_by_edit_text);
			textColor.setTextColor(textColor.getTextColors().withAlpha(alphaValue));
			textBy.setTextColor(textBy.getTextColors().withAlpha(alphaValue));
			editColor.setTextColor(editColor.getTextColors().withAlpha(alphaValue));
			editColor.getBackground().setAlpha(alphaValue);

			this.alphaValue = alphaValue;
		}

		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.changeColorByN(sprite,
				getFormulaWithBrickField(BrickField.COLOR_CHANGE)));
		return null;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		FormulaEditorFragment.showFragment(view, this, BrickField.COLOR_CHANGE);
	}
}
