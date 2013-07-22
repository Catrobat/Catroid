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
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public class GlideToBrick extends BrickBaseType implements OnClickListener, FormulaBrick {
	private static final long serialVersionUID = 1L;

	private Formula xDestination;
	private Formula yDestination;
	private Formula durationInSeconds;

	private transient View prototypeView;

	public GlideToBrick() {

	}

	public GlideToBrick(Sprite sprite, int xDestinationValue, int yDestinationValue, int durationInMilliSecondsValue) {
		this.sprite = sprite;

		xDestination = new Formula(xDestinationValue);
		yDestination = new Formula(yDestinationValue);
		durationInSeconds = new Formula(durationInMilliSecondsValue / 1000.0);
	}

	public GlideToBrick(Sprite sprite, Formula xDestination, Formula yDestination, Formula durationInSeconds) {
		this.sprite = sprite;

		this.xDestination = xDestination;
		this.yDestination = yDestination;
		this.durationInSeconds = durationInSeconds;
	}

	@Override
	public Formula getFormula() {
		return durationInSeconds;
	}

	public void setXDestination(Formula xDestination) {
		this.xDestination = xDestination;
	}

	public void setYDestination(Formula yDestination) {
		this.yDestination = yDestination;
	}

	@Override
	public int getRequiredResources() {
		return xDestination.getRequiredResources() | yDestination.getRequiredResources()
				| durationInSeconds.getRequiredResources();
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		GlideToBrick copyBrick = (GlideToBrick) clone();
		copyBrick.sprite = sprite;
		return copyBrick;
	}

	public int getDurationInMilliSeconds() {
		return durationInSeconds.interpretInteger(sprite);
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		view = View.inflate(context, R.layout.brick_glide_to, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_glide_to_checkbox);
		final Brick brickInstance = this;

		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView textX = (TextView) view.findViewById(R.id.brick_glide_to_prototype_text_view_x);
		TextView editX = (TextView) view.findViewById(R.id.brick_glide_to_edit_text_x);
		xDestination.setTextFieldId(R.id.brick_glide_to_edit_text_x);
		xDestination.refreshTextField(view);
		editX.setOnClickListener(this);

		TextView textY = (TextView) view.findViewById(R.id.brick_glide_to_prototype_text_view_y);
		TextView editY = (TextView) view.findViewById(R.id.brick_glide_to_edit_text_y);
		yDestination.setTextFieldId(R.id.brick_glide_to_edit_text_y);
		yDestination.refreshTextField(view);
		editY.setOnClickListener(this);

		TextView textDuration = (TextView) view.findViewById(R.id.brick_glide_to_prototype_text_view_duration);
		TextView editDuration = (TextView) view.findViewById(R.id.brick_glide_to_edit_text_duration);
		durationInSeconds.setTextFieldId(R.id.brick_glide_to_edit_text_duration);
		durationInSeconds.refreshTextField(view);

		TextView times = (TextView) view.findViewById(R.id.brick_glide_to_seconds_text_view);
		if (durationInSeconds.isSingleNumberFormula()) {
			times.setText(view.getResources().getQuantityString(R.plurals.second_plural,
					Utils.convertDoubleToPluralInteger(durationInSeconds.interpretDouble(sprite))));
		} else {

			// Random Number to get into the "other" keyword for values like 0.99 or 2.001 seconds or degrees
			// in hopefully all possible languages
			times.setText(view.getResources().getQuantityString(R.plurals.second_plural,
					Utils.TRANSLATION_PLURAL_OTHER_INTEGER));
		}

		textDuration.setVisibility(View.GONE);
		editDuration.setVisibility(View.VISIBLE);
		textX.setVisibility(View.GONE);
		editX.setVisibility(View.VISIBLE);
		textY.setVisibility(View.GONE);
		editY.setVisibility(View.VISIBLE);

		editDuration.setOnClickListener(this);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_glide_to, null);
		TextView textX = (TextView) prototypeView.findViewById(R.id.brick_glide_to_prototype_text_view_x);
		textX.setText(String.valueOf(xDestination.interpretInteger(sprite)));
		TextView textY = (TextView) prototypeView.findViewById(R.id.brick_glide_to_prototype_text_view_y);
		textY.setText(String.valueOf(yDestination.interpretInteger(sprite)));
		TextView textDuration = (TextView) prototypeView.findViewById(R.id.brick_glide_to_prototype_text_view_duration);
		textDuration.setText(String.valueOf(durationInSeconds.interpretDouble(sprite)));
		TextView times = (TextView) prototypeView.findViewById(R.id.brick_glide_to_seconds_text_view);
		times.setText(context.getResources().getQuantityString(R.plurals.second_plural,
				Utils.convertDoubleToPluralInteger(durationInSeconds.interpretDouble(sprite))));
		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new GlideToBrick(getSprite(), xDestination.clone(), yDestination.clone(), durationInSeconds.clone());
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_glide_to_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView glideToLabel = (TextView) view.findViewById(R.id.brick_glide_to_label);
			TextView glideToSeconds = (TextView) view.findViewById(R.id.brick_glide_to_seconds_text_view);
			TextView glideToXTextView = (TextView) view.findViewById(R.id.brick_glide_to_x);
			TextView glideToYTextView = (TextView) view.findViewById(R.id.brick_glide_to_y);
			TextView editDuration = (TextView) view.findViewById(R.id.brick_glide_to_edit_text_duration);
			TextView editX = (TextView) view.findViewById(R.id.brick_glide_to_edit_text_x);
			TextView editY = (TextView) view.findViewById(R.id.brick_glide_to_edit_text_y);

			glideToLabel.setTextColor(glideToLabel.getTextColors().withAlpha(alphaValue));
			glideToSeconds.setTextColor(glideToSeconds.getTextColors().withAlpha(alphaValue));
			glideToXTextView.setTextColor(glideToXTextView.getTextColors().withAlpha(alphaValue));
			glideToYTextView.setTextColor(glideToYTextView.getTextColors().withAlpha(alphaValue));
			editDuration.setTextColor(editDuration.getTextColors().withAlpha(alphaValue));
			editDuration.getBackground().setAlpha(alphaValue);
			editX.setTextColor(editX.getTextColors().withAlpha(alphaValue));
			editX.getBackground().setAlpha(alphaValue);
			editY.setTextColor(editY.getTextColors().withAlpha(alphaValue));
			editY.getBackground().setAlpha(alphaValue);

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
			case R.id.brick_glide_to_edit_text_x:
				FormulaEditorFragment.showFragment(view, this, xDestination);
				break;

			case R.id.brick_glide_to_edit_text_y:
				FormulaEditorFragment.showFragment(view, this, yDestination);
				break;

			case R.id.brick_glide_to_edit_text_duration:
				FormulaEditorFragment.showFragment(view, this, durationInSeconds);
				break;
		}

	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		sequence.addAction(ExtendedActions.glideTo(sprite, xDestination, yDestination, durationInSeconds));
		return null;
	}
}
