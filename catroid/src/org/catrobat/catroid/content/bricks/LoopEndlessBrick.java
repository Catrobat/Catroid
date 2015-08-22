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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import org.catrobat.catroid.R;

public class LoopEndlessBrick extends LoopEndBrick implements DeadEndBrick {

	private static final long serialVersionUID = 1L;
	private transient boolean isPuzzleView = true;

	public LoopEndlessBrick() {
	}

	public LoopEndlessBrick(LoopBeginBrick loopStartingBrick) {
		super(loopStartingBrick);
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null || !isPuzzleView) {
			isPuzzleView = true;
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.brick_loop_endless, null);
			view = getViewWithAlpha(alphaValue);
			checkbox = (CheckBox) view.findViewById(R.id.brick_loop_endless_checkbox);

			setCheckboxView(R.id.brick_loop_endless_checkbox);
			final Brick brickInstance = this;

			checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					checked = isChecked;
					adapter.handleCheck(brickInstance, isChecked);
				}
			});
		}
		return view;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = null;
			if (isPuzzleView) {
				layout = view.findViewById(R.id.brick_loop_endless_layout);
				TextView endlessLabel = (TextView) view.findViewById(R.id.brick_loop_endless_label);
				endlessLabel.setTextColor(endlessLabel.getTextColors().withAlpha(alphaValue));
			} else {
				layout = view.findViewById(R.id.brick_loop_endless_nopuzzle_layout);
				TextView endlessLabel = (TextView) view.findViewById(R.id.brick_loop_endless_nopuzzle_label);
				endlessLabel.setTextColor(endlessLabel.getTextColors().withAlpha(alphaValue));
			}
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);
			this.alphaValue = alphaValue;
		}

		return view;
	}

	@Override
	public Brick clone() {
		return new LoopEndlessBrick(getLoopBeginBrick());
	}

	@Override
	public View getNoPuzzleView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (view == null || isPuzzleView) {
			isPuzzleView = false;
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.brick_loop_endless_no_puzzle, null);
			view = getViewWithAlpha(alphaValue);
			checkbox = (CheckBox) view.findViewById(R.id.brick_loop_endless_no_puzzle_checkbox);

			final Brick brickInstance = this;

			checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					checked = isChecked;
					adapter.handleCheck(brickInstance, isChecked);
				}
			});
		}
		return view;
	}
}
