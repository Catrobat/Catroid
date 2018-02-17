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
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;

import java.util.List;


public class BroadcastWaitBrick extends BroadcastBrick {
	private static final long serialVersionUID = 1L;

	public BroadcastWaitBrick(String broadcastMessage) {
		super(broadcastMessage);
	}

	@Override
	public Brick clone() {
		return new BroadcastWaitBrick(broadcastMessage);
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}
		view = View.inflate(context, R.layout.brick_broadcast_wait, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);
		setCheckboxView(R.id.brick_broadcast_wait_checkbox);
		final Spinner broadcastSpinner = (Spinner) view.findViewById(R.id.brick_broadcast_wait_spinner);

		broadcastSpinner.setAdapter(getMessageAdapter(context));
		setOnItemSelectedListener(broadcastSpinner, context);

		setSpinnerSelection(broadcastSpinner);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_broadcast_wait, null);
		Spinner broadcastWaitSpinner = (Spinner) prototypeView.findViewById(R.id.brick_broadcast_wait_spinner);

		SpinnerAdapter broadcastWaitSpinnerAdapter = getMessageAdapter(context);
		broadcastWaitSpinner.setAdapter(broadcastWaitSpinnerAdapter);
		setSpinnerSelection(broadcastWaitSpinner);
		return prototypeView;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createBroadcastActionFromWaiter(sprite, broadcastMessage));
		return null;
	}
}
