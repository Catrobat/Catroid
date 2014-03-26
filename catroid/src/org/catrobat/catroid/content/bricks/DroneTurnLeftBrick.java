/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public class DroneTurnLeftBrick extends BrickBaseType implements OnClickListener, FormulaBrick {
	private static final long serialVersionUID = 1L;
	private Formula timeToFlyInSeconds;
	private Formula powerInPercent;
	private transient View prototypeView;

	public DroneTurnLeftBrick(Sprite sprite, int timeInMillisecondsValue, int powerInPercent) {
		this.sprite = sprite;
		this.timeToFlyInSeconds = new Formula(timeInMillisecondsValue / 1000.0);
		this.powerInPercent = new Formula(powerInPercent);
	}

	public DroneTurnLeftBrick(Sprite sprite, Formula time, Formula powerInPercent) {
		this.sprite = sprite;
		this.timeToFlyInSeconds = time;
		this.powerInPercent = powerInPercent;
	}

	public DroneTurnLeftBrick() {

	}

	@Override
	public Formula getFormula() {
		return timeToFlyInSeconds;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	//	public Formula getTimeToWait() {
	//		return timeToFlyInSeconds;
	//	}

	public void setPower(Formula powerInPercent) {
		this.powerInPercent = powerInPercent;
	}

	public void setTimeToWait(Formula timeToWaitInSeconds) {
		this.timeToFlyInSeconds = timeToWaitInSeconds;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		DroneTurnLeftBrick copyBrick = (DroneTurnLeftBrick) clone();
		copyBrick.sprite = sprite;
		return copyBrick;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_drone_turn_left, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_drone_turn_left_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView textTime = (TextView) view.findViewById(R.id.brick_drone_turn_left_prototype_text_view_second);
		TextView editTime = (TextView) view.findViewById(R.id.brick_drone_turn_left_edit_text_second);
		timeToFlyInSeconds.setTextFieldId(R.id.brick_drone_turn_left_edit_text_second);
		timeToFlyInSeconds.refreshTextField(view);

		TextView times = (TextView) view.findViewById(R.id.brick_drone_turn_left_text_view_second);

		if (timeToFlyInSeconds.isSingleNumberFormula()) {
			times.setText(view.getResources().getQuantityString(R.plurals.second_plural,
					Utils.convertDoubleToPluralInteger(timeToFlyInSeconds.interpretDouble(sprite))));
		} else {

			// Random Number to get into the "other" keyword for values like 0.99 or 2.001 seconds or degrees
			// in hopefully all possible languages
			times.setText(view.getResources().getQuantityString(R.plurals.second_plural,
					Utils.TRANSLATION_PLURAL_OTHER_INTEGER));
		}

		textTime.setVisibility(View.GONE);
		editTime.setVisibility(View.VISIBLE);
		editTime.setOnClickListener(this);

		TextView textPower = (TextView) view.findViewById(R.id.brick_drone_turn_left_prototype_text_view_power);
		TextView editPower = (TextView) view.findViewById(R.id.brick_drone_turn_left_edit_text_power);
		powerInPercent.setTextFieldId(R.id.brick_drone_turn_left_edit_text_power);
		powerInPercent.refreshTextField(view);

		//TextView power = (TextView) view.findViewById(R.id.brick_drone_turn_left_text_view_power);

		textPower.setVisibility(View.GONE);
		editPower.setVisibility(View.VISIBLE);
		editPower.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_drone_turn_left, null);
		TextView textTime = (TextView) prototypeView
				.findViewById(R.id.brick_drone_turn_left_prototype_text_view_second);
		textTime.setText(String.valueOf(timeToFlyInSeconds.interpretInteger(sprite)));
		TextView times = (TextView) prototypeView.findViewById(R.id.brick_drone_turn_left_text_view_second);
		times.setText(context.getResources().getQuantityString(R.plurals.second_plural,
				Utils.convertDoubleToPluralInteger(timeToFlyInSeconds.interpretDouble(sprite))));

		TextView textPower = (TextView) prototypeView
				.findViewById(R.id.brick_drone_turn_left_prototype_text_view_power);
		textPower.setText(String.valueOf(powerInPercent.interpretFloat(sprite)));

		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new DroneTurnLeftBrick(getSprite(), timeToFlyInSeconds.clone(), powerInPercent.clone());
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_drone_turn_left_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textTimeLabel = (TextView) view.findViewById(R.id.brick_drone_turn_left_label);
			TextView textPercent = (TextView) view.findViewById(R.id.brick_set_size_to_percent);

			TextView textTimeSeconds = (TextView) view.findViewById(R.id.brick_drone_turn_left_text_view_second);
			TextView editTime = (TextView) view.findViewById(R.id.brick_drone_turn_left_edit_text_second);

			TextView textPower = (TextView) view.findViewById(R.id.brick_drone_turn_left_text_view_power);
			TextView editPower = (TextView) view.findViewById(R.id.brick_drone_turn_left_edit_text_power);

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
			case R.id.brick_drone_turn_left_edit_text_second:
				FormulaEditorFragment.showFragment(view, this, timeToFlyInSeconds);
				break;

			case R.id.brick_drone_turn_left_edit_text_power:
				FormulaEditorFragment.showFragment(view, this, powerInPercent);
				break;
		}
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		sequence.addAction(ExtendedActions.droneTurnLeft(sprite, timeToFlyInSeconds, powerInPercent));
		return null;
	}
}
