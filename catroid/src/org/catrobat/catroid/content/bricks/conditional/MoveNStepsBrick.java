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
package org.catrobat.catroid.content.bricks.conditional;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public class MoveNStepsBrick extends BrickBaseType implements OnClickListener, FormulaBrick {

	private static final long serialVersionUID = 1L;
	private Formula steps;

	private transient View prototypeView;

	public MoveNStepsBrick() {

	}

	public MoveNStepsBrick(Sprite sprite, double stepsValue) {
		this.sprite = sprite;
		steps = new Formula(stepsValue);
	}

	public MoveNStepsBrick(Sprite sprite, Formula steps) {
		this.sprite = sprite;

		this.steps = steps;
	}

	@Override
	public Formula getFormula() {
		return steps;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		MoveNStepsBrick copyBrick = (MoveNStepsBrick) clone();
		copyBrick.sprite = sprite;
		return copyBrick;
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

		steps.setTextFieldId(R.id.brick_move_n_steps_edit_text);
		steps.refreshTextField(view);

		TextView times = (TextView) view.findViewById(R.id.brick_move_n_steps_step_text_view);

		if (steps.isSingleNumberFormula()) {
			times.setText(view.getResources().getQuantityString(R.plurals.brick_move_n_step_plural,
					Utils.convertDoubleToPluralInteger(steps.interpretDouble(sprite))));
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
		textSteps.setText(String.valueOf(steps.interpretDouble(sprite)));
		TextView times = (TextView) prototypeView.findViewById(R.id.brick_move_n_steps_step_text_view);
		times.setText(context.getResources().getQuantityString(R.plurals.brick_move_n_step_plural,
				Utils.convertDoubleToPluralInteger(steps.interpretDouble(sprite))));
		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new MoveNStepsBrick(getSprite(), steps.clone());
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

			this.alphaValue = (alphaValue);

		}

		return view;
	}

	@Override
	public void onClick(View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
		FormulaEditorFragment.showFragment(view, this, steps);
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		//sequence.addAction(ExtendedActions.moveNSteps(sprite, steps));
		sequence.addAction(sprite.getActionFactory().createMoveNStepsAction(sprite, steps)); // TODO[physics]
		return null;
	}
}
