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
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public class SetTextBrick extends FormulaBrick implements View.OnClickListener {
	private static final long serialVersionUID = 1L;

	private transient View prototypeView;

	public SetTextBrick() {
		addAllowedBrickField(BrickField.X_DESTINATION);
		addAllowedBrickField(BrickField.Y_DESTINATION);
		addAllowedBrickField(BrickField.STRING);
	}

	public SetTextBrick(int xDestinationValue, int yDestinationValue, String text) {
		initializeBrickFields(new Formula(xDestinationValue), new Formula(yDestinationValue), new Formula(text));
	}

	public SetTextBrick(Formula xDestination, Formula yDestination, Formula text) {
		initializeBrickFields(xDestination, yDestination, text);
	}

	private void initializeBrickFields(Formula xDestination, Formula yDestination, Formula text) {
		addAllowedBrickField(BrickField.X_DESTINATION);
		addAllowedBrickField(BrickField.Y_DESTINATION);
		addAllowedBrickField(BrickField.STRING);

		setFormulaWithBrickField(BrickField.X_DESTINATION, xDestination);
		setFormulaWithBrickField(BrickField.Y_DESTINATION, yDestination);
		setFormulaWithBrickField(BrickField.STRING, text);
	}

	public void setXDestination(Formula xDestination) {
		setFormulaWithBrickField(BrickField.X_DESTINATION, xDestination);
	}

	public void setYDestination(Formula yDestination) {
		setFormulaWithBrickField(BrickField.Y_DESTINATION, yDestination);
	}

	public void setText(Formula text){
		setFormulaWithBrickField(BrickField.STRING, text);
	}

	@Override
	public int getRequiredResources() {
		return getFormulaWithBrickField(BrickField.X_DESTINATION).getRequiredResources() |
				getFormulaWithBrickField(BrickField.Y_DESTINATION).getRequiredResources()
				| getFormulaWithBrickField(BrickField.STRING).getRequiredResources();
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		view = View.inflate(context, R.layout.brick_drone_set_text, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_set_text_checkbox);
		final Brick brickInstance = this;

		checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView textX = (TextView) view.findViewById(R.id.brick_set_text_prototype_text_view_x);
		TextView textY = (TextView) view.findViewById(R.id.brick_set_text_prototype_text_view_y);

		TextView editX = (TextView) view.findViewById(R.id.brick_set_text_edit_text_x);
		TextView editY = (TextView) view.findViewById(R.id.brick_set_text_edit_text_y);

		getFormulaWithBrickField(BrickField.X_DESTINATION).setTextFieldId(R.id.brick_set_text_edit_text_x);
		getFormulaWithBrickField(BrickField.X_DESTINATION).refreshTextField(view);
		editX.setOnClickListener(this);

		getFormulaWithBrickField(BrickField.Y_DESTINATION).setTextFieldId(R.id.brick_set_text_edit_text_y);
		getFormulaWithBrickField(BrickField.Y_DESTINATION).refreshTextField(view);
		editY.setOnClickListener(this);

		TextView text = (TextView) view.findViewById(R.id.brick_set_text_prototype_view);
		TextView editText = (TextView) view.findViewById(R.id.brick_set_text_edit_text);

		getFormulaWithBrickField(BrickField.STRING).setTextFieldId(R.id.brick_set_text_edit_text);
		getFormulaWithBrickField(BrickField.STRING).refreshTextField(view);

		text.setVisibility(View.GONE);
		editText.setVisibility(View.VISIBLE);
		textX.setVisibility(View.GONE);
		editX.setVisibility(View.VISIBLE);
		textY.setVisibility(View.GONE);
		editY.setVisibility(View.VISIBLE);

		editText.setOnClickListener(this);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_drone_set_text, null);

		TextView posX = (TextView) prototypeView.findViewById(R.id.brick_set_text_prototype_text_view_x);
		TextView posY = (TextView) prototypeView.findViewById(R.id.brick_set_text_prototype_text_view_y);

		TextView text = (TextView) prototypeView.findViewById(R.id.brick_set_text_prototype_view);
		TextView second_text = (TextView) prototypeView.findViewById(R.id.brick_set_text_seconds_text_view);

		posX.setText(String.valueOf(BrickValues.X_POSITION));
		posY.setText(String.valueOf(BrickValues.Y_POSITION));
		text.setText(BrickValues.STRING_VALUE);
		second_text.setText(BrickValues.STRING_VALUE);

		return prototypeView;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_set_text_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView glideToLabel = (TextView) view.findViewById(R.id.brick_set_text_label);
			TextView glideToSeconds = (TextView) view.findViewById(R.id.brick_set_text_seconds_text_view);
			TextView glideToXTextView = (TextView) view.findViewById(R.id.brick_set_text_x);
			TextView glideToYTextView = (TextView) view.findViewById(R.id.brick_set_text_y);
			TextView editDuration = (TextView) view.findViewById(R.id.brick_set_text_edit_text);

			TextView editX = (TextView) view.findViewById(R.id.brick_set_text_edit_text_x);
			TextView editY = (TextView) view.findViewById(R.id.brick_set_text_edit_text_y);

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
			case R.id.brick_set_text_edit_text_x:
				FormulaEditorFragment.showFragment(view, this, getFormulaWithBrickField(BrickField.X_DESTINATION));
				break;

			case R.id.brick_set_text_edit_text_y:
				FormulaEditorFragment.showFragment(view, this, getFormulaWithBrickField(BrickField.Y_DESTINATION));
				break;

			case R.id.brick_set_text_edit_text:
				FormulaEditorFragment
						.showFragment(view, this, getFormulaWithBrickField(BrickField.STRING));
				break;
		}

	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.setText(sprite, getFormulaWithBrickField(BrickField.X_DESTINATION),
				getFormulaWithBrickField(BrickField.Y_DESTINATION),
				getFormulaWithBrickField(BrickField.STRING)));
		return null;
	}
}
