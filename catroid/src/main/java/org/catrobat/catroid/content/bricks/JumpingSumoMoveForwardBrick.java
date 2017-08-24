/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;

import java.util.List;

public class JumpingSumoMoveForwardBrick extends JumpingSumoMoveBrick {

	private static final long serialVersionUID = 1L;
	private static final String TAG = JumpingSumoMoveForwardBrick.class.getSimpleName();

	public JumpingSumoMoveForwardBrick(int durationInMilliseconds, int powerInPercent) {
		super(durationInMilliseconds, powerInPercent);
	}

	@Override
	protected String getBrickLabel(View view) {
		return view.getResources().getString(R.string.brick_jumping_sumo_move_forward);
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		Log.i(TAG, "add time in ms: " + getFormulaWithBrickField(BrickField.JUMPING_SUMO_TIME_TO_DRIVE_IN_SECONDS) + " power: " + getFormulaWithBrickField(BrickField.JUMPING_SUMO_SPEED));
		sequence.addAction(sprite.getActionFactory().createJumpingSumoMoveForwardAction(sprite,
				getFormulaWithBrickField(BrickField.JUMPING_SUMO_TIME_TO_DRIVE_IN_SECONDS),
				getFormulaWithBrickField(BrickField.JUMPING_SUMO_SPEED)));
		return null;
	}

	@Override
	public void updateReferenceAfterMerge(Scene into, Scene from) {
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_jumping_sumo_move, null);

		setCheckboxView(R.id.brick_jumping_sumo_move_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView editTime = (TextView) view.findViewById(R.id.brick_jumping_sumo_move_edit_text_second);
		getFormulaWithBrickField(BrickField.JUMPING_SUMO_TIME_TO_DRIVE_IN_SECONDS)
				.setTextFieldId(R.id.brick_jumping_sumo_move_edit_text_second);
		getFormulaWithBrickField(BrickField.JUMPING_SUMO_TIME_TO_DRIVE_IN_SECONDS).refreshTextField(view);

		TextView label = (TextView) view.findViewById(R.id.brick_jumping_sumo_move_label);
		label.setText(getBrickLabel(view));

		editTime.setVisibility(View.VISIBLE);
		editTime.setOnClickListener(this);

		TextView editPower = (TextView) view.findViewById(R.id.brick_jumping_sumo_move_edit_text_power);
		getFormulaWithBrickField(BrickField.JUMPING_SUMO_SPEED)
				.setTextFieldId(R.id.brick_jumping_sumo_move_edit_text_power);
		getFormulaWithBrickField(BrickField.JUMPING_SUMO_SPEED).refreshTextField(view);
		TextView textPower = (TextView) view.findViewById(R.id.brick_jumping_sumo_move_text_view_power);

		textPower.setVisibility(View.VISIBLE);
		editPower.setVisibility(View.VISIBLE);
		editPower.setOnClickListener(this);

		return view;
	}
}
