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
import android.graphics.drawable.Drawable;
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
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public abstract class JumpingSumoMoveBrick extends FormulaBrick {
	private static final String TAG = JumpingSumoMoveBrick.class.getSimpleName();

	protected transient View prototypeView;
	private static final long serialVersionUID = 1L;

	public JumpingSumoMoveBrick() {
		addAllowedBrickField(BrickField.JUMPING_SUMO_TIME_TO_DRIVE_IN_SECONDS);
		addAllowedBrickField(BrickField.JUMPING_SUMO_SPEED);
	}

	public JumpingSumoMoveBrick(int durationInMilliseconds, int powerInPercent) {
		initializeBrickFields(new Formula(durationInMilliseconds / 1000.0), new Formula(powerInPercent));
	}

	public JumpingSumoMoveBrick(Formula durationInSeconds, Formula powerInPercent) {
		initializeBrickFields(durationInSeconds, powerInPercent);
	}

	private void initializeBrickFields(Formula durationInSeconds, Formula powerInPercent) {
		addAllowedBrickField(BrickField.JUMPING_SUMO_TIME_TO_DRIVE_IN_SECONDS);
		addAllowedBrickField(BrickField.JUMPING_SUMO_SPEED);
		setFormulaWithBrickField(BrickField.JUMPING_SUMO_TIME_TO_DRIVE_IN_SECONDS, durationInSeconds);
		setFormulaWithBrickField(BrickField.JUMPING_SUMO_SPEED, powerInPercent);
	}

	public void setPower(Formula powerInPercent) {
		setFormulaWithBrickField(BrickField.JUMPING_SUMO_SPEED, powerInPercent);
	}

	public void setTimeToWait(Formula timeToWaitInSeconds) {
		setFormulaWithBrickField(BrickField.JUMPING_SUMO_TIME_TO_DRIVE_IN_SECONDS, timeToWaitInSeconds);
	}

	@Override
	public abstract List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence);

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_jumping_sumo_move, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_jumping_sumo_move_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView textTime = (TextView) view.findViewById(R.id.brick_jumping_sumo_move_prototype_text_view_second);
		TextView editTime = (TextView) view.findViewById(R.id.brick_jumping_sumo_move_edit_text_second);
		getFormulaWithBrickField(BrickField.JUMPING_SUMO_TIME_TO_DRIVE_IN_SECONDS)
				.setTextFieldId(R.id.brick_jumping_sumo_move_edit_text_second);
		getFormulaWithBrickField(BrickField.JUMPING_SUMO_TIME_TO_DRIVE_IN_SECONDS).refreshTextField(view);

		TextView times = (TextView) view.findViewById(R.id.brick_jumping_sumo_move_text_view_second);

		if (getFormulaWithBrickField(BrickField.JUMPING_SUMO_TIME_TO_DRIVE_IN_SECONDS).isSingleNumberFormula()) {
			try {
				times.setText(view.getResources().getQuantityString(R.plurals.second_plural,
						Utils.convertDoubleToPluralInteger(getFormulaWithBrickField(BrickField.JUMPING_SUMO_TIME_TO_DRIVE_IN_SECONDS)
								.interpretDouble(ProjectManager.getInstance().getCurrentSprite()))));
			} catch (InterpretationException interpretationException) {
				Log.d(TAG, "Formula interpretation for this specific Brick failed.", interpretationException);
			}
		} else {
			// Random Number to get into the "other" keyword for values like 0.99 or 2.001 seconds or degrees
			// in hopefully all possible languages
			times.setText(view.getResources().getQuantityString(R.plurals.second_plural,
					Utils.TRANSLATION_PLURAL_OTHER_INTEGER));
		}

		TextView label = (TextView) view.findViewById(R.id.brick_jumping_sumo_move_label);
		label.setText(getBrickLabel(view));

		textTime.setVisibility(View.GONE);
		editTime.setVisibility(View.VISIBLE);
		editTime.setOnClickListener(this);

		TextView textPower = (TextView) view.findViewById(R.id.brick_jumping_sumo_move_prototype_text_view_power);
		TextView editPower = (TextView) view.findViewById(R.id.brick_jumping_sumo_move_edit_text_power);
		getFormulaWithBrickField(BrickField.JUMPING_SUMO_SPEED)
				.setTextFieldId(R.id.brick_jumping_sumo_move_edit_text_power);
		getFormulaWithBrickField(BrickField.JUMPING_SUMO_SPEED).refreshTextField(view);

		textPower.setVisibility(View.GONE);
		editPower.setVisibility(View.VISIBLE);
		editPower.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_jumping_sumo_move, null);
		TextView label = (TextView) prototypeView.findViewById(R.id.brick_jumping_sumo_move_label);
		label.setText(getBrickLabel(prototypeView));
		TextView textTime = (TextView) prototypeView.findViewById(R.id.brick_jumping_sumo_move_prototype_text_view_second);

		TextView times = (TextView) prototypeView.findViewById(R.id.brick_jumping_sumo_move_text_view_second);
		TextView textPower = (TextView) prototypeView.findViewById(R.id.brick_jumping_sumo_move_prototype_text_view_power);
		textTime.setText(String.valueOf(BrickValues.JUMPING_SUMO_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS / 1000));

		textPower.setText(String.valueOf(BrickValues.JUMPING_SUMO_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT));
		times.setText(context.getResources().getQuantityString(R.plurals.second_plural,
				Utils.convertDoubleToPluralInteger(BrickValues.JUMPING_SUMO_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS / 1000)));
		return prototypeView;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		if (view != null) {
			View layout = view.findViewById(R.id.brick_jumping_sumo_move_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textTimeLabel = (TextView) view.findViewById(R.id.brick_jumping_sumo_move_label);
			TextView textPercent = (TextView) view.findViewById(R.id.brick_jumping_sumo_set_power_to_percent);

			TextView textTimeSeconds = (TextView) view.findViewById(R.id.brick_jumping_sumo_move_text_view_second);
			TextView editTime = (TextView) view.findViewById(R.id.brick_jumping_sumo_move_edit_text_second);

			TextView textPower = (TextView) view.findViewById(R.id.brick_jumping_sumo_move_text_view_power);
			TextView editPower = (TextView) view.findViewById(R.id.brick_jumping_sumo_move_edit_text_power);

			textTimeLabel.setTextColor(textTimeLabel.getTextColors().withAlpha(alphaValue));

			textTimeSeconds.setTextColor(textTimeSeconds.getTextColors().withAlpha(alphaValue));
			editTime.setTextColor(editTime.getTextColors().withAlpha(alphaValue));
			editTime.getBackground().setAlpha(alphaValue);

			textPower.setTextColor(textPower.getTextColors().withAlpha(alphaValue));
			editPower.setTextColor(editPower.getTextColors().withAlpha(alphaValue));

			textPercent.setTextColor(textPercent.getTextColors().withAlpha(alphaValue));
			editPower.getBackground().setAlpha(alphaValue);

			this.alphaValue = alphaValue;
		}
		return view;
	}

	@Override
	public void onClick(View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
		switch (view.getId()) {
			case R.id.brick_jumping_sumo_move_edit_text_second:
				FormulaEditorFragment.showFragment(view, this, BrickField.JUMPING_SUMO_TIME_TO_DRIVE_IN_SECONDS);
				break;

			case R.id.brick_jumping_sumo_move_edit_text_power:
				FormulaEditorFragment.showFragment(view, this, BrickField.JUMPING_SUMO_SPEED);
				break;

			default:
				return;
		}
	}

	@Override
	public int getRequiredResources() {
		Log.i(TAG, "getRequiredResources"); //not called
		return super.getRequiredResources();
		//return super.getRequiredResources() | Brick.JUMPING_SUMO;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		FormulaEditorFragment.showFragment(view, this, BrickField.JUMPING_SUMO_TIME_TO_DRIVE_IN_SECONDS);
	}

	protected abstract String getBrickLabel(View view);
}
