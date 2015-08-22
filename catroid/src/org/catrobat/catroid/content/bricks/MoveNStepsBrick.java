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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public class MoveNStepsBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	private transient View prototypeView;

	public MoveNStepsBrick() {
		addAllowedBrickField(BrickField.STEPS);
	}

	public MoveNStepsBrick(double stepsValue) {
		initializeBrickFields(new Formula(stepsValue));
	}

	public MoveNStepsBrick(Formula steps) {
		initializeBrickFields(steps);
	}

	private void initializeBrickFields(Formula steps) {
		addAllowedBrickField(BrickField.STEPS);
		setFormulaWithBrickField(BrickField.STEPS, steps);
	}

	@Override
	public int getRequiredResources() {
		return getFormulaWithBrickField(BrickField.STEPS).getRequiredResources();
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		view = View.inflate(context, R.layout.brick_move_n_steps, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_move_n_steps_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView text = (TextView) view.findViewById(R.id.brick_move_n_steps_prototype_text_view);
		TextView edit = (TextView) view.findViewById(R.id.brick_move_n_steps_edit_text);

		getFormulaWithBrickField(BrickField.STEPS).setTextFieldId(R.id.brick_move_n_steps_edit_text);
		getFormulaWithBrickField(BrickField.STEPS).refreshTextField(view);

		TextView times = (TextView) view.findViewById(R.id.brick_move_n_steps_step_text_view);

		if (getFormulaWithBrickField(BrickField.STEPS).isSingleNumberFormula()) {
			try {
				times.setText(view.getResources().getQuantityString(
						R.plurals.brick_move_n_step_plural,
						Utils.convertDoubleToPluralInteger(getFormulaWithBrickField(BrickField.STEPS).interpretDouble(
								ProjectManager.getInstance().getCurrentSprite()))
				));
			} catch (InterpretationException interpretationException) {
				Log.d(getClass().getSimpleName(), "Couldn't interpret Formula.", interpretationException);
			}
		} else {

			// Random Number to get into the "other" keyword for values like 0.99 or 2.001 seconds or degrees
			// in hopefully all possible languages
			times.setText(view.getResources().getQuantityString(R.plurals.brick_move_n_step_plural,
					Utils.TRANSLATION_PLURAL_OTHER_INTEGER));
		}

		text.setVisibility(View.GONE);
		edit.setVisibility(View.VISIBLE);
		edit.setOnClickListener(this);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		prototypeView = inflater.inflate(R.layout.brick_move_n_steps, null);
		TextView textSteps = (TextView) prototypeView.findViewById(R.id.brick_move_n_steps_prototype_text_view);
		textSteps.setText(String.valueOf(BrickValues.MOVE_STEPS));
		TextView times = (TextView) prototypeView.findViewById(R.id.brick_move_n_steps_step_text_view);
		times.setText(context.getResources().getQuantityString(R.plurals.brick_move_n_step_plural,
				Utils.convertDoubleToPluralInteger(BrickValues.MOVE_STEPS)));
		return prototypeView;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_move_n_steps_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView moveNStepsLabel = (TextView) view.findViewById(R.id.brick_move_n_steps_label);
			TextView times = (TextView) view.findViewById(R.id.brick_move_n_steps_step_text_view);
			TextView moveNStepsEdit = (TextView) view.findViewById(R.id.brick_move_n_steps_edit_text);
			moveNStepsLabel.setTextColor(moveNStepsLabel.getTextColors().withAlpha(alphaValue));
			times.setTextColor(times.getTextColors().withAlpha(alphaValue));
			moveNStepsEdit.setTextColor(moveNStepsEdit.getTextColors().withAlpha(alphaValue));
			moveNStepsEdit.getBackground().setAlpha(alphaValue);

			this.alphaValue = alphaValue;
		}

		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.moveNSteps(sprite, getFormulaWithBrickField(BrickField.STEPS)));
		return null;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		FormulaEditorFragment.showFragment(view, this, BrickField.STEPS);
	}
}
