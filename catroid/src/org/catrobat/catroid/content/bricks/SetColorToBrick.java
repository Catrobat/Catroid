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

public class SetColorToBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	private transient View prototypeView;

	public SetColorToBrick() {
		addAllowedBrickField(BrickField.COLOR);
	}
	public SetColorToBrick(Integer color) {
		initializeBrickField(new Formula(color));
	}
	public SetColorToBrick(Formula color) {
		initializeBrickField(color);
	}

	private void initializeBrickField(Formula color) {
		addAllowedBrickField(BrickField.COLOR);
		setFormulaWithBrickField(BrickField.COLOR, color);
	}

	@Override
	public int getRequiredResources() {
		return getFormulaWithBrickField(BrickField.COLOR).getRequiredResources();
	}
	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_set_color_to, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_set_color_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});
		TextView text = (TextView) view.findViewById(R.id.brick_set_color_prototype_text_view);
		TextView edit = (TextView) view.findViewById(R.id.brick_set_color_edit_text);
		getFormulaWithBrickField(BrickField.COLOR).setTextFieldId(R.id.brick_set_color_edit_text);
		getFormulaWithBrickField(BrickField.COLOR).refreshTextField(view);
		text.setVisibility(View.GONE);
		edit.setVisibility(View.VISIBLE);
		edit.setOnClickListener(this);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_set_color_to, null);
		TextView textSetSizeTo = (TextView) prototypeView.findViewById(R.id.brick_set_color_prototype_text_view);
		textSetSizeTo.setText(String.valueOf(BrickValues.SET_COLOR_TO));
		return prototypeView;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_set_color_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textSize = (TextView) view.findViewById(R.id.brick_set_color_label);
			TextView editSize = (TextView) view.findViewById(R.id.brick_set_color_edit_text);
			textSize.setTextColor(textSize.getTextColors().withAlpha(alphaValue));
			editSize.setTextColor(editSize.getTextColors().withAlpha(alphaValue));
			editSize.getBackground().setAlpha(alphaValue);

			this.alphaValue = alphaValue;
		}

		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.setColorTo(sprite, getFormulaWithBrickField(BrickField.COLOR)));
		return null;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		FormulaEditorFragment.showFragment(view, this, BrickField.COLOR);
	}
}
