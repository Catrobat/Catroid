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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
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

public class TurnRightBrick extends FormulaBrick implements OnClickListener {

	private static final long serialVersionUID = 1L;
	private transient View prototypeView;

	public TurnRightBrick() {
		addAllowedBrickField(BrickField.TURN_RIGHT_DEGREES);
	}

	public TurnRightBrick(double degreesValue) {
		initializeBrickFields(new Formula(degreesValue));
	}

	public TurnRightBrick(Formula degreesFormula) {
		initializeBrickFields(degreesFormula);
	}

	private void initializeBrickFields(Formula degrees) {
		addAllowedBrickField(BrickField.TURN_RIGHT_DEGREES);
		setFormulaWithBrickField(BrickField.TURN_RIGHT_DEGREES, degrees);
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_turn_right, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_turn_right_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});
		TextView textDegrees = (TextView) view.findViewById(R.id.brick_turn_right_prototype_text_view);
		TextView editDegrees = (TextView) view.findViewById(R.id.brick_turn_right_edit_text);
		getFormulaWithBrickField(BrickField.TURN_RIGHT_DEGREES).setTextFieldId(R.id.brick_turn_right_edit_text);
		getFormulaWithBrickField(BrickField.TURN_RIGHT_DEGREES).refreshTextField(view);

		textDegrees.setVisibility(View.GONE);
		editDegrees.setVisibility(View.VISIBLE);
		editDegrees.setOnClickListener(this);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_turn_right, null);
		TextView textDegrees = (TextView) prototypeView.findViewById(R.id.brick_turn_right_prototype_text_view);
        textDegrees.setText(String.valueOf(BrickValues.TURN_DEGREES));
		return prototypeView;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_turn_right_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView turnRightLabel = (TextView) view.findViewById(R.id.brick_turn_right_label);
			TextView textDegrees = (TextView) view.findViewById(R.id.brick_turn_right_prototype_text_view);
			TextView degreeSymbol = (TextView) view.findViewById(R.id.brick_turn_right_degree_text_view);
			TextView editDegrees = (TextView) view.findViewById(R.id.brick_turn_right_edit_text);

			turnRightLabel.setTextColor(turnRightLabel.getTextColors().withAlpha(alphaValue));
			textDegrees.setTextColor(textDegrees.getTextColors().withAlpha(alphaValue));
			degreeSymbol.setTextColor(degreeSymbol.getTextColors().withAlpha(alphaValue));
			editDegrees.setTextColor(editDegrees.getTextColors().withAlpha(alphaValue));
			editDegrees.getBackground().setAlpha(alphaValue);

			this.alphaValue = (alphaValue);

		}

		return view;
	}

	@Override
	public void onClick(View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
		FormulaEditorFragment.showFragment(view, this, getFormulaWithBrickField(BrickField.TURN_RIGHT_DEGREES));
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.turnRight(sprite, getFormulaWithBrickField(BrickField.TURN_RIGHT_DEGREES)));
		return null;
	}
}
