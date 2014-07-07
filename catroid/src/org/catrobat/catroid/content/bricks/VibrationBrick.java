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
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public class VibrationBrick extends BrickBaseType implements OnClickListener, FormulaBrick {
	private static final long serialVersionUID = 1L;
	private Formula vibrateDurationInSeconds;

	private transient View prototypeView;

	public VibrationBrick(Sprite sprite, Formula vibrateDurationInSecondsFormula) {
		this.sprite = sprite;
		this.vibrateDurationInSeconds = vibrateDurationInSecondsFormula;
	}

	public VibrationBrick(Sprite sprite, int vibrationDurationInMilliseconds) {
		this.sprite = sprite;
		this.vibrateDurationInSeconds = new Formula(vibrationDurationInMilliseconds / 1000.0);
	}

	private VibrationBrick() {
	}

	@Override
	public Formula getFormula() {
		return vibrateDurationInSeconds;
	}

	@Override
	public int getRequiredResources() {
		return VIBRATOR;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		VibrationBrick copyBrick = (VibrationBrick) clone();
		copyBrick.sprite = sprite;
		return copyBrick;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_vibration, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_vibration_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView textSeconds = (TextView) view.findViewById(R.id.brick_vibration_prototype_text_view_seconds);
		TextView editSeconds = (TextView) view.findViewById(R.id.brick_vibration_edit_seconds_text);
		vibrateDurationInSeconds.setTextFieldId(R.id.brick_vibration_edit_seconds_text);
		vibrateDurationInSeconds.refreshTextField(view);

		TextView times = (TextView) view.findViewById(R.id.brick_vibration_second_text_view);

		if (vibrateDurationInSeconds.isSingleNumberFormula()) {
			times.setText(view.getResources().getQuantityString(R.plurals.second_plural,
					Utils.convertDoubleToPluralInteger(vibrateDurationInSeconds.interpretDouble(sprite))));
		} else {
			times.setText(view.getResources().getQuantityString(R.plurals.second_plural,
					Utils.TRANSLATION_PLURAL_OTHER_INTEGER));
		}

		textSeconds.setVisibility(View.GONE);
		editSeconds.setVisibility(View.VISIBLE);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_vibration, null);
		TextView textSeconds = (TextView) prototypeView.findViewById(R.id.brick_vibration_prototype_text_view_seconds);
		textSeconds.setText(String.valueOf(vibrateDurationInSeconds.interpretInteger(sprite)));
		TextView times = (TextView) prototypeView.findViewById(R.id.brick_vibration_second_text_view);
		times.setText(context.getResources().getQuantityString(R.plurals.second_plural,
				Utils.convertDoubleToPluralInteger(vibrateDurationInSeconds.interpretDouble(sprite))));
		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new VibrationBrick(getSprite(), vibrateDurationInSeconds.clone());
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		if (view != null) {
			View layout = view.findViewById(R.id.brick_vibration_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textVibrationLabel = (TextView) view.findViewById(R.id.brick_vibration_label);
			TextView textVibrationSeconds = (TextView) view.findViewById(R.id.brick_vibration_second_text_view);
			TextView editSeconds = (TextView) view.findViewById(R.id.brick_vibration_edit_seconds_text);

			textVibrationLabel.setTextColor(textVibrationLabel.getTextColors().withAlpha(alphaValue));
			textVibrationSeconds.setTextColor(textVibrationSeconds.getTextColors().withAlpha((alphaValue)));

			editSeconds.setTextColor(editSeconds.getTextColors().withAlpha(alphaValue));
			editSeconds.getBackground().setAlpha(alphaValue);

			this.alphaValue = (alphaValue);
		}
		return view;
	}

	@Override
	public void onClick(View v) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
		FormulaEditorFragment.showFragment(view, this, vibrateDurationInSeconds);
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().vibrate(sprite, vibrateDurationInSeconds));
		return null;
	}
}