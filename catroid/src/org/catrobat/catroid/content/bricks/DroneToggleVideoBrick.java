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

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import java.util.List;

/**
 * Created by Lukas on 24.03.2015.
 */



public class DroneToggleVideoBrick extends BrickBaseType {
	private static final long serialVersionUID = 1L;

	public DroneToggleVideoBrick() {

	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		DroneToggleVideoBrick copyBrick = (DroneToggleVideoBrick) clone();
		return copyBrick;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_drone_toggle_video, null);

		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new DroneToggleVideoBrick();
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}
		view = View.inflate(context, R.layout.brick_drone_toggle_video, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_drone_toggle_video_checkbox);
		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		return view;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		if (view != null) {
			View layout = view.findViewById(R.id.brick_drone_toggle_video);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);
			this.alphaValue = (alphaValue);
		}
		return view;
	}


	@Override
	public int getRequiredResources() {
		return super.getRequiredResources() | Brick.ARDRONE_SUPPORT;
	}


	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.droneStartVideo());
		return null;
	}

}

