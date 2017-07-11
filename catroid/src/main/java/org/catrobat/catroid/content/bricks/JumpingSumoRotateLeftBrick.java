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
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;

import java.util.List;

public class JumpingSumoRotateLeftBrick extends JumpingSumoRotateBrick {

	private static final long serialVersionUID = 1L;
	private static final String TAG = JumpingSumoRotateLeftBrick.class.getSimpleName();

	public JumpingSumoRotateLeftBrick(float degree) {
		super(degree);
	}

	@Override
	protected String getBrickLabel(View view) {
		return view.getResources().getString(R.string.brick_jumping_sumo_rotate_left);
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createJumpingSumoRotateLeftAction(sprite,
				getFormulaWithBrickField(BrickField.JUMPING_SUMO_ROTATE)));
		return null;
	}

	@Override
	public void updateReferenceAfterMerge(Scene into, Scene from) {
	}

	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_jumping_sumo_rotate, null);

		setCheckboxView(R.id.brick_jumping_sumo_rotate_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView editDegree = (TextView) view.findViewById(R.id.brick_jumping_sumo_change_variable_edit_text);
		getFormulaWithBrickField(BrickField.JUMPING_SUMO_ROTATE)
				.setTextFieldId(R.id.brick_jumping_sumo_change_variable_edit_text);
		getFormulaWithBrickField(BrickField.JUMPING_SUMO_ROTATE).refreshTextField(view);

		TextView label = (TextView) view.findViewById(R.id.brick_jumping_sumo_rotate_label);
		label.setText(getBrickLabel(view));

		editDegree.setVisibility(View.VISIBLE);
		editDegree.setOnClickListener(this);

		return view;
	}
}
