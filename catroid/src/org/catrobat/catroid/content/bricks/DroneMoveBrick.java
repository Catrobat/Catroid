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
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public abstract class DroneMoveBrick extends DroneBrick implements OnClickListener, FormulaBrick {

	protected transient View prototypeView;
	private static final long serialVersionUID = 1L;
	protected Formula timeToFlyInSeconds;
	protected Formula powerInPercent;

	public DroneMoveBrick(Sprite sprite, int durationInMilliseconds, int powerInPercent) {
		this.sprite = sprite;
		this.timeToFlyInSeconds = new Formula(durationInMilliseconds / 1000.0);
		this.powerInPercent = new Formula(powerInPercent);
	}

	public DroneMoveBrick(Sprite sprite, Formula durationInSeconds, Formula powerInPercent) {
		this.sprite = sprite;
		this.timeToFlyInSeconds = durationInSeconds;
		this.powerInPercent = powerInPercent;
	}

	public DroneMoveBrick() {
	}

	@Override
	public Formula getFormula() {
		return timeToFlyInSeconds;
	}

	public void setPower(Formula powerInPercent) {
		this.powerInPercent = powerInPercent;
	}

	public void setTimeToWait(Formula timeToWaitInSeconds) {
		this.timeToFlyInSeconds = timeToWaitInSeconds;
	}

	protected abstract String getBrickLabel(View view);

	@Override
	public abstract Brick clone();

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		DroneMoveBrick copyBrick = (DroneMoveBrick) clone();
		copyBrick.sprite = sprite;
		return copyBrick;
	}

	@Override
	public abstract List<SequenceAction> addActionToSequence(SequenceAction sequence);

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_drone_move, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_drone_move_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView textTime = (TextView) view.findViewById(R.id.brick_drone_move_prototype_text_view_second);
		TextView editTime = (TextView) view.findViewById(R.id.brick_drone_move_edit_text_second);
		timeToFlyInSeconds.setTextFieldId(R.id.brick_drone_move_edit_text_second);
		timeToFlyInSeconds.refreshTextField(view);

		TextView times = (TextView) view.findViewById(R.id.brick_drone_move_text_view_second);

		if (timeToFlyInSeconds.isSingleNumberFormula()) {
			times.setText(view.getResources().getQuantityString(R.plurals.second_plural,
					Utils.convertDoubleToPluralInteger(timeToFlyInSeconds.interpretDouble(sprite))));
		} else {
			// Random Number to get into the "other" keyword for values like 0.99 or 2.001 seconds or degrees
			// in hopefully all possible languages
			times.setText(view.getResources().getQuantityString(R.plurals.second_plural,
					Utils.TRANSLATION_PLURAL_OTHER_INTEGER));
		}

		TextView label = (TextView) view.findViewById(R.id.brick_drone_move_label);
		label.setText(getBrickLabel(view));

		textTime.setVisibility(View.GONE);
		editTime.setVisibility(View.VISIBLE);
		editTime.setOnClickListener(this);

		TextView textPower = (TextView) view.findViewById(R.id.brick_drone_move_prototype_text_view_power);
		TextView editPower = (TextView) view.findViewById(R.id.brick_drone_move_edit_text_power);
		powerInPercent.setTextFieldId(R.id.brick_drone_move_edit_text_power);
		powerInPercent.refreshTextField(view);

		textPower.setVisibility(View.GONE);
		editPower.setVisibility(View.VISIBLE);
		editPower.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_drone_move, null);
		TextView label = (TextView) prototypeView.findViewById(R.id.brick_drone_move_label);
		label.setText(getBrickLabel(prototypeView));
		TextView textTime = (TextView) prototypeView.findViewById(R.id.brick_drone_move_prototype_text_view_second);
		textTime.setText(String.valueOf(timeToFlyInSeconds.interpretInteger(sprite)));
		TextView times = (TextView) prototypeView.findViewById(R.id.brick_drone_move_text_view_second);
		times.setText(context.getResources().getQuantityString(R.plurals.second_plural,
				Utils.convertDoubleToPluralInteger(timeToFlyInSeconds.interpretDouble(sprite))));

		TextView textPower = (TextView) prototypeView.findViewById(R.id.brick_drone_move_prototype_text_view_power);
		textPower.setText(String.valueOf(powerInPercent.interpretFloat(sprite)));

		return prototypeView;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		if (view != null) {
			View layout = view.findViewById(R.id.brick_drone_move_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textTimeLabel = (TextView) view.findViewById(R.id.brick_drone_move_label);
			TextView textPercent = (TextView) view.findViewById(R.id.brick_set_power_to_percent);

			TextView textTimeSeconds = (TextView) view.findViewById(R.id.brick_drone_move_text_view_second);
			TextView editTime = (TextView) view.findViewById(R.id.brick_drone_move_edit_text_second);

			TextView textPower = (TextView) view.findViewById(R.id.brick_drone_move_text_view_power);
			TextView editPower = (TextView) view.findViewById(R.id.brick_drone_move_edit_text_power);

			textTimeLabel.setTextColor(textTimeLabel.getTextColors().withAlpha(alphaValue));

			textTimeSeconds.setTextColor(textTimeSeconds.getTextColors().withAlpha(alphaValue));
			editTime.setTextColor(editTime.getTextColors().withAlpha(alphaValue));
			editTime.getBackground().setAlpha(alphaValue);

			textPower.setTextColor(textPower.getTextColors().withAlpha(alphaValue));
			editPower.setTextColor(editPower.getTextColors().withAlpha(alphaValue));

			textPercent.setTextColor(textPercent.getTextColors().withAlpha(alphaValue));
			editPower.getBackground().setAlpha(alphaValue);

			this.alphaValue = (alphaValue);
		}
		return view;
	}

	@Override
	public void onClick(View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}

		switch (view.getId()) {
			case R.id.brick_drone_move_edit_text_second:
				FormulaEditorFragment.showFragment(view, this, timeToFlyInSeconds);
				break;

			case R.id.brick_drone_move_edit_text_power:
				FormulaEditorFragment.showFragment(view, this, powerInPercent);
				break;

			default:
				return;
		}
	}
}
