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
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.adapter.DataAdapter;
import org.catrobat.catroid.ui.adapter.UserVariableAdapterWrapper;
import org.catrobat.catroid.ui.dialogs.NewDataDialog;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public class ShowTextBrick extends UserVariableBrick {
	private static final long serialVersionUID = 1L;
	private transient View prototypeView;
	private transient AdapterView<?> adapterView;
	public String userVariableName;

	public ShowTextBrick() {
		addAllowedBrickField(BrickField.X_POSITION);
		addAllowedBrickField(BrickField.Y_POSITION);
	}

	public ShowTextBrick(int x, int y) {
		initializeBrickFields(new Formula(x), new Formula(y));
	}

	public ShowTextBrick(Formula xPosition, Formula yPosition) {
		initializeBrickFields(xPosition, yPosition);
	}

	private void initializeBrickFields(Formula xPosition, Formula yPosition) {
		addAllowedBrickField(BrickField.X_POSITION);
		addAllowedBrickField(BrickField.Y_POSITION);
		setFormulaWithBrickField(BrickField.X_POSITION, xPosition);
		setFormulaWithBrickField(BrickField.Y_POSITION, yPosition);
	}

	public void setXPosition(Formula xPosition) {
		setFormulaWithBrickField(BrickField.X_POSITION, xPosition);
	}

	public void setYPosition(Formula yPosition) {
		setFormulaWithBrickField(BrickField.Y_POSITION, yPosition);
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		FormulaEditorFragment.showFragment(view, this, BrickField.SHOWTEXT);
	}

	@Override
	public int getRequiredResources() {
		return getFormulaWithBrickField(BrickField.Y_POSITION).getRequiredResources() | getFormulaWithBrickField(
				BrickField.X_POSITION).getRequiredResources();
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}

		view = View.inflate(context, R.layout.brick_show_text, null);
		view = getViewWithAlpha(alphaValue);
		setCheckboxView(R.id.brick_show_text_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView textX = (TextView) view.findViewById(R.id.brick_show_text_prototype_text_view_x);
		TextView editX = (TextView) view.findViewById(R.id.brick_show_text_edit_text_x);
		getFormulaWithBrickField(BrickField.X_POSITION).setTextFieldId(R.id.brick_show_text_edit_text_x);
		getFormulaWithBrickField(BrickField.X_POSITION).refreshTextField(view);
		textX.setVisibility(View.GONE);
		editX.setVisibility(View.VISIBLE);
		editX.setOnClickListener(this);

		TextView textY = (TextView) view.findViewById(R.id.brick_show_text_prototype_text_view_y);
		TextView editY = (TextView) view.findViewById(R.id.brick_show_text_edit_text_y);
		getFormulaWithBrickField(BrickField.Y_POSITION).setTextFieldId(R.id.brick_show_text_edit_text_y);
		getFormulaWithBrickField(BrickField.Y_POSITION).refreshTextField(view);
		textY.setVisibility(View.GONE);
		editY.setVisibility(View.VISIBLE);
		editY.setOnClickListener(this);

		Spinner variableSpinner = (Spinner) view.findViewById(R.id.show_text_spinner);

		UserBrick currentBrick = ProjectManager.getInstance().getCurrentUserBrick();
		int userBrickId = (currentBrick == null ? -1 : currentBrick.getUserBrickId());

		DataAdapter dataAdapter = ProjectManager.getInstance().getCurrentProject().getDataContainer()
				.createDataAdapter(context, userBrickId, ProjectManager.getInstance().getCurrentSprite(), inUserBrick);
		UserVariableAdapterWrapper userVariableAdapterWrapper = new UserVariableAdapterWrapper(context,
				dataAdapter);
		userVariableAdapterWrapper.setItemLayout(android.R.layout.simple_spinner_item, android.R.id.text1);

		variableSpinner.setAdapter(userVariableAdapterWrapper);

		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			variableSpinner.setClickable(true);
			variableSpinner.setEnabled(true);
		} else {
			variableSpinner.setClickable(false);
			variableSpinner.setFocusable(false);
		}

		setSpinnerSelection(variableSpinner, null);

		variableSpinner.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP
						&& (((Spinner) view).getSelectedItemPosition() == 0
						&& ((Spinner) view).getAdapter().getCount() == 1)) {
					NewDataDialog dialog = new NewDataDialog((Spinner) view, NewDataDialog.DialogType.USER_VARIABLE);
					dialog.addVariableDialogListener(ShowTextBrick.this);
					dialog.show(((SherlockFragmentActivity) view.getContext()).getSupportFragmentManager(),
							NewDataDialog.DIALOG_FRAGMENT_TAG);
					return true;
				}

				return false;
			}
		});
		variableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0 && ((UserVariableAdapterWrapper) parent.getAdapter()).isTouchInDropDownView()) {
					NewDataDialog dialog = new NewDataDialog((Spinner) parent, NewDataDialog.DialogType.USER_VARIABLE);
					dialog.addVariableDialogListener(ShowTextBrick.this);
					dialog.show(((SherlockFragmentActivity) view.getContext()).getSupportFragmentManager(),
							NewDataDialog.DIALOG_FRAGMENT_TAG);
				}
				((UserVariableAdapterWrapper) parent.getAdapter()).resetIsTouchInDropDownView();
				userVariable = (UserVariable) parent.getItemAtPosition(position);
				adapterView = parent;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				userVariable = (UserVariable) adapterView.getItemAtPosition(1);

				userVariableName = "No variable set";
				try {
					userVariableName = userVariable.getName();
				} catch (NullPointerException e) {
					Log.d("ShowTextBrick.java", "NullPointerException");
				}
			}
		});

		userVariableName = "No variable set";
		try {
			userVariableName = userVariable.getName();
		} catch (NullPointerException e) {
			Log.d("ShowTextBrick.java", "NullPointerException");
		}

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_show_text, null);
		TextView textX = (TextView) prototypeView.findViewById(R.id.brick_show_text_prototype_text_view_x);
		textX.setText(String.valueOf(BrickValues.X_POSITION));
		TextView textY = (TextView) prototypeView.findViewById(R.id.brick_show_text_prototype_text_view_y);
		textY.setText(String.valueOf(BrickValues.Y_POSITION));
		return prototypeView;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		if (view != null) {
			View layout = view.findViewById(R.id.brick_show_text_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView placeAtLabel = (TextView) view.findViewById(R.id.brick_show_text_label);
			TextView placeAtX = (TextView) view.findViewById(R.id.brick_show_text_x_textview);
			TextView placeAtY = (TextView) view.findViewById(R.id.brick_show_text_y_textview);
			TextView editX = (TextView) view.findViewById(R.id.brick_show_text_edit_text_x);
			TextView editY = (TextView) view.findViewById(R.id.brick_show_text_edit_text_y);
			placeAtLabel.setTextColor(placeAtLabel.getTextColors().withAlpha(alphaValue));
			placeAtX.setTextColor(placeAtX.getTextColors().withAlpha(alphaValue));
			placeAtY.setTextColor(placeAtY.getTextColors().withAlpha(alphaValue));
			editX.setTextColor(editX.getTextColors().withAlpha(alphaValue));
			editX.getBackground().setAlpha(alphaValue);
			editY.setTextColor(editY.getTextColors().withAlpha(alphaValue));
			editY.getBackground().setAlpha(alphaValue);

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
			case R.id.brick_show_text_edit_text_x:
				FormulaEditorFragment.showFragment(view, this, BrickField.X_POSITION);
				break;

			case R.id.brick_show_text_edit_text_y:
				FormulaEditorFragment.showFragment(view, this, BrickField.Y_POSITION);
				break;
		}
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		if (userVariableName == null) {
			userVariableName = "No variable set";
		}

		sequence.addAction(ExtendedActions.showText(sprite, getFormulaWithBrickField(BrickField.X_POSITION),
				getFormulaWithBrickField(BrickField.Y_POSITION), userVariableName));
		return null;
	}
}
