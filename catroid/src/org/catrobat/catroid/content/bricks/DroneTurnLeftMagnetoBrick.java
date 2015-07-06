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
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;

import java.util.List;

public class DroneTurnLeftMagnetoBrick extends DroneMoveBrick {

	private static final long serialVersionUID = 1L;

	public DroneTurnLeftMagnetoBrick(int durationInMilliseconds, int powerInPercent) {
		super(durationInMilliseconds, powerInPercent);
	}

	public DroneTurnLeftMagnetoBrick(Formula durationInMilliseconds, Formula powerInPercent) {
		super(durationInMilliseconds, powerInPercent);
	}

	public DroneTurnLeftMagnetoBrick() {
		super();
	}

	@Override
	protected String getBrickLabel(View view) {
		return view.getResources().getString(R.string.brick_drone_turn_left_magneto);
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.droneTurnLeftMagneto(sprite,
				getFormulaWithBrickField(BrickField.DRONE_TIME_TO_FLY_IN_SECONDS),
				getFormulaWithBrickField(BrickField.DRONE_POWER_IN_PERCENT)));
		return null;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		View brickView = super.getView(context, brickId, baseAdapter);
		TextView editTextView = (TextView) brickView.findViewById(R.id.brick_drone_move_text_view_power);
		editTextView.setText(R.string.brick_drone_angle);
		return brickView;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = super.getPrototypeView(context);
		TextView textView = (TextView) prototypeView.findViewById(R.id.brick_drone_move_text_view_power);
		textView.setText(R.string.brick_drone_angle);
		return prototypeView;
	}
}
