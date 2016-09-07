/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public class ThinkForBubbleBrick extends FormulaBrick {
	private static final long serialVersionUID = 1L;
	protected int type = Constants.THINK_BRICK;
	private transient View prototypeView;

	public ThinkForBubbleBrick() {
		addAllowedBrickField(BrickField.STRING);
		initializeBrickFields(new Formula(2));
	}

	public ThinkForBubbleBrick(String text, float durationInSecondsValue) {
		initializeBrickFields(new Formula(text), new Formula(durationInSecondsValue));
	}

	public ThinkForBubbleBrick(Formula text, Formula durationInSeconds) {
		initializeBrickFields(text, durationInSeconds);
	}

	protected void initializeBrickFields(Formula text, Formula durationInSeconds) {
		addAllowedBrickField(BrickField.STRING);
		addAllowedBrickField(BrickField.DURATION_IN_SECONDS);
		setFormulaWithBrickField(BrickField.STRING, text);
		setFormulaWithBrickField(BrickField.DURATION_IN_SECONDS, durationInSeconds);
	}

	protected void initializeBrickFields(Formula durationInSeconds) {
		addAllowedBrickField(BrickField.STRING);
		addAllowedBrickField(BrickField.DURATION_IN_SECONDS);
		setFormulaWithBrickField(BrickField.DURATION_IN_SECONDS, durationInSeconds);
	}

	@Override
	public int getRequiredResources() {
		return getFormulaWithBrickField(BrickField.STRING).getRequiredResources()
				| getFormulaWithBrickField(BrickField.DURATION_IN_SECONDS).getRequiredResources();
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		int layoutId = type == Constants.SAY_BRICK ? R.layout.brick_say_for_bubble : R.layout.brick_think_for_bubble;
		int checkboxId = type == Constants.SAY_BRICK ? R.id.brick_say_for_bubble_checkbox : R.id
				.brick_think_for_bubble_checkbox;
		int textTextId = type == Constants.SAY_BRICK ? R.id.brick_say_for_bubble_prototype_text_view_text : R.id
				.brick_think_for_bubble_prototype_text_view_text;
		int editTextId = type == Constants.SAY_BRICK ? R.id.brick_say_for_bubble_edit_text_text : R.id
				.brick_think_for_bubble_edit_text_text;
		int textDurationId = type == Constants.SAY_BRICK ? R.id.brick_say_for_bubble_prototype_text_view_duration : R
				.id.brick_think_for_bubble_prototype_text_view_duration;
		int editDurationId = type == Constants.SAY_BRICK ? R.id.brick_say_for_bubble_edit_text_duration : R.id
				.brick_think_for_bubble_edit_text_duration;
		int thinkSaySecondsLabelId = type == Constants.SAY_BRICK ? R.id.brick_say_for_bubble_seconds_label : R.id
				.brick_think_for_bubble_seconds_label;

		view = View.inflate(context, layoutId, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		setCheckboxView(checkboxId);

		TextView textText = (TextView) view.findViewById(textTextId);
		TextView editText = (TextView) view.findViewById(editTextId);
		getFormulaWithBrickField(BrickField.STRING).setTextFieldId(editTextId);
		getFormulaWithBrickField(BrickField.STRING).refreshTextField(view);
		editText.setOnClickListener(this);

		TextView textDuration = (TextView) view.findViewById(textDurationId);
		TextView editDuration = (TextView) view.findViewById(editDurationId);
		getFormulaWithBrickField(BrickField.DURATION_IN_SECONDS).setTextFieldId(editDurationId);
		getFormulaWithBrickField(BrickField.DURATION_IN_SECONDS).refreshTextField(view);

		TextView seconds = (TextView) view.findViewById(thinkSaySecondsLabelId);

		if (getFormulaWithBrickField(BrickField.DURATION_IN_SECONDS).isSingleNumberFormula()) {
			try {
				seconds.setText(view.getResources().getQuantityString(
						R.plurals.second_plural,
						Utils.convertDoubleToPluralInteger(getFormulaWithBrickField(BrickField.DURATION_IN_SECONDS)
								.interpretDouble(ProjectManager.getInstance().getCurrentSprite()))));
			} catch (InterpretationException interpretationException) {
				Log.d(getClass().getSimpleName(), "Couldn't interpret Formula.", interpretationException);
			}
		} else {

			// Random Number to get into the "other" keyword for values like 0.99 or 2.001 seconds or degrees
			// in hopefully all possible languages
			seconds.setText(view.getResources().getQuantityString(R.plurals.second_plural,
					Utils.TRANSLATION_PLURAL_OTHER_INTEGER));
		}

		textDuration.setVisibility(View.GONE);
		editDuration.setVisibility(View.VISIBLE);
		textText.setVisibility(View.GONE);
		editText.setVisibility(View.VISIBLE);

		editDuration.setOnClickListener(this);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		int layoutId = type == Constants.SAY_BRICK ? R.layout.brick_say_for_bubble : R.layout.brick_think_for_bubble;
		int textTextId = type == Constants.SAY_BRICK ? R.id.brick_say_for_bubble_prototype_text_view_text : R.id
				.brick_think_for_bubble_prototype_text_view_text;
		int textDurationId = type == Constants.SAY_BRICK ? R.id.brick_say_for_bubble_prototype_text_view_duration : R
				.id.brick_think_for_bubble_prototype_text_view_duration;
		int defaultStringId = type == Constants.SAY_BRICK ? R.string.brick_say_bubble_default_value : R.string
				.brick_think_bubble_default_value;
		int thinkSaySecondsLabelId = type == Constants.SAY_BRICK ? R.id.brick_say_for_bubble_seconds_label : R.id
				.brick_think_for_bubble_seconds_label;
		prototypeView = View.inflate(context, layoutId, null);
		TextView textText = (TextView) prototypeView.findViewById(textTextId);
		TextView textDuration = (TextView) prototypeView.findViewById(textDurationId);
		TextView seconds = (TextView) prototypeView.findViewById(thinkSaySecondsLabelId);
		textText.setText(context.getString(defaultStringId));
		textDuration.setText(Utils.getNumberStringForBricks(BrickValues.DURATION));
		seconds.setText(context.getResources().getQuantityString(R.plurals.second_plural,
				Utils.convertDoubleToPluralInteger(BrickValues.DURATION)));
		return prototypeView;
	}

	@Override
	public void onClick(View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}

		int editTextId = type == Constants.SAY_BRICK ? R.id.brick_say_for_bubble_edit_text_text : R.id
				.brick_think_for_bubble_edit_text_text;

		if (view.getId() == editTextId) {
			FormulaEditorFragment.showFragment(view, this, BrickField.STRING);
		} else {
			FormulaEditorFragment.showFragment(view, this, BrickField.DURATION_IN_SECONDS);
		}
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		if (type == Constants.SAY_BRICK) {
			sequence.addAction(sprite.getActionFactory().createSayForBubbleAction(sprite, getFormulaWithBrickField(BrickField.STRING)));
			sequence.addAction(sprite.getActionFactory().createWaitForBubbleBrickAction(sprite, getFormulaWithBrickField(BrickField.DURATION_IN_SECONDS)));
		} else {
			sequence.addAction(sprite.getActionFactory().createThinkForBubbleAction(sprite, getFormulaWithBrickField(BrickField.STRING)));
			sequence.addAction(sprite.getActionFactory().createWaitForBubbleBrickAction(sprite, getFormulaWithBrickField(BrickField.DURATION_IN_SECONDS)));
		}
		return null;
	}

	public void showFormulaEditorToEditFormula(View view) {
		FormulaEditorFragment.showFragment(view, this, BrickField.STRING);
	}

	@Override
	public void updateReferenceAfterMerge(Scene into, Scene from) {
	}
}
