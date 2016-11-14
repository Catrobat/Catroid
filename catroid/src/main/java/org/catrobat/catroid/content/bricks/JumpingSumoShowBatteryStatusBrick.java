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
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.drone.jumpingsumo.JumpingSumoDataContainer;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.adapter.DataAdapter;
import org.catrobat.catroid.ui.adapter.UserVariableAdapterWrapper;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public class JumpingSumoShowBatteryStatusBrick extends UserVariableBrick {

	private static final long serialVersionUID = 1L;

	private transient View prototypeView;

	public static final String TAG = JumpingSumoShowBatteryStatusBrick.class.getSimpleName();
	JumpingSumoDataContainer batteryStatus = JumpingSumoDataContainer.getInstance();

	public JumpingSumoShowBatteryStatusBrick() {
		addAllowedBrickField(BrickField.X_POSITION);
		addAllowedBrickField(BrickField.Y_POSITION);
	}

	public JumpingSumoShowBatteryStatusBrick(int xPosition, int yPosition) {
		userVariable = batteryStatus.getBatteryVariable();
		initializeBrickFields(new Formula(xPosition), new Formula(yPosition));
	}

	public JumpingSumoShowBatteryStatusBrick(Formula xPosition, Formula yPosition) {
		userVariable = batteryStatus.getBatteryVariable();
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
		FormulaEditorFragment.showFragment(view, this, BrickField.X_POSITION);
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

		view = View.inflate(context, R.layout.brick_jumping_sumo_show_batterystatus, null);

		setCheckboxView(R.id.brick_show_variable_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView textViewX = (TextView) view.findViewById(R.id.brick_show_variable_prototype_text_view_x);
		TextView editTextX = (TextView) view.findViewById(R.id.brick_show_variable_edit_text_x);
		getFormulaWithBrickField(BrickField.X_POSITION).setTextFieldId(R.id.brick_show_variable_edit_text_x);
		getFormulaWithBrickField(BrickField.X_POSITION).refreshTextField(view);
		textViewX.setVisibility(View.GONE);
		editTextX.setVisibility(View.VISIBLE);
		editTextX.setOnClickListener(this);

		TextView textViewY = (TextView) view.findViewById(R.id.brick_show_variable_prototype_text_view_y);
		TextView editTextY = (TextView) view.findViewById(R.id.brick_show_variable_edit_text_y);
		getFormulaWithBrickField(BrickField.Y_POSITION).setTextFieldId(R.id.brick_show_variable_edit_text_y);
		getFormulaWithBrickField(BrickField.Y_POSITION).refreshTextField(view);
		textViewY.setVisibility(View.GONE);
		editTextY.setVisibility(View.VISIBLE);
		editTextY.setOnClickListener(this);

		UserBrick currentBrick = ProjectManager.getInstance().getCurrentUserBrick();

		DataAdapter dataAdapter = ProjectManager.getInstance().getCurrentScene().getDataContainer()
				.createDataAdapter(context, currentBrick, ProjectManager.getInstance().getCurrentSprite());
		UserVariableAdapterWrapper userVariableAdapterWrapper = new UserVariableAdapterWrapper(context,
				dataAdapter);
		userVariableAdapterWrapper.setItemLayout(android.R.layout.simple_spinner_item, android.R.id.text1);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_jumping_sumo_show_batterystatus, null);
		TextView textViewX = (TextView) prototypeView.findViewById(R.id.brick_show_variable_prototype_text_view_x);
		textViewX.setText(Utils.getNumberStringForBricks(BrickValues.X_POSITION));
		TextView textViewY = (TextView) prototypeView.findViewById(R.id.brick_show_variable_prototype_text_view_y);
		textViewY.setText(Utils.getNumberStringForBricks(BrickValues.Y_POSITION));
		return prototypeView;
	}

	@Override
	public void onClick(View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}

		switch (view.getId()) {
			case R.id.brick_show_variable_edit_text_x:
				FormulaEditorFragment.showFragment(view, this, BrickField.X_POSITION);
				break;

			case R.id.brick_show_variable_edit_text_y:
				FormulaEditorFragment.showFragment(view, this, BrickField.Y_POSITION);
				break;
		}
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createJumpingSumoShowBatteryStatusAction(sprite, getFormulaWithBrickField(BrickField.X_POSITION),
				getFormulaWithBrickField(BrickField.Y_POSITION), batteryStatus.getBatteryVariable()));
		return null;
	}

	@Override
	public void updateReferenceAfterMerge(Scene into, Scene from) {
		super.updateUserVariableReference(into, from);
	}
}
