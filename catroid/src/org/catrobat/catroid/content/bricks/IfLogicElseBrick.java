/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.bricks;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class IfLogicElseBrick extends NestingBrick implements AllowedAfterDeadEndBrick {

	private static final long serialVersionUID = 1L;
	private static final String TAG = IfLogicElseBrick.class.getSimpleName();
	private IfLogicBeginBrick ifBeginBrick;
	private IfLogicEndBrick ifEndBrick;

	public IfLogicElseBrick(Sprite sprite, IfLogicBeginBrick ifBeginBrick) {
		this.sprite = sprite;
		this.ifBeginBrick = ifBeginBrick;
		ifBeginBrick.setElseBrick(this);
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public Sprite getSprite() {
		return sprite;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (view == null) {
			view = inflater.inflate(R.layout.brick_if_else, null);
		}

		checkbox = (CheckBox) view.findViewById(R.id.brick_if_else_checkbox);
		final Brick brickInstance = this;

		checkbox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				checked = !checked;
				if (!checked) {
					for (Brick currentBrick : adapter.getCheckedBricks()) {
						currentBrick.setCheckedBoolean(false);
					}
				}
				adapter.handleCheck(brickInstance, checked);
			}
		});

		return view;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_if_else_layout);
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);
		this.alphaValue = (alphaValue);
		return view;
	}

	@Override
	public Brick clone() {
		return new IfLogicElseBrick(getSprite(), ifBeginBrick);
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_if_else, null);
	}

	public void setIfEndBrick(IfLogicEndBrick ifEndBrick) {
		this.ifEndBrick = ifEndBrick;
	}

	@Override
	public boolean isDraggableOver(Brick brick) {
		if (brick == ifBeginBrick || brick == ifEndBrick) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean isInitialized() {
		if (ifBeginBrick == null || ifEndBrick == null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void initialize() {
		//ifBeginBrick = new IfLogicBeginBrick(sprite, 0);
		//ifEndBrick = new IfLogicEndBrick(sprite, this);
		Log.w(TAG, "Cannot create the IfLogic Bricks from here!");
	}

	@Override
	public List<NestingBrick> getAllNestingBrickParts(boolean sorted) {
		//TODO: handle sorting
		List<NestingBrick> nestingBrickList = new ArrayList<NestingBrick>();
		if (sorted) {
			nestingBrickList.add(ifBeginBrick);
			nestingBrickList.add(this);
			nestingBrickList.add(ifEndBrick);
		} else {
			//nestingBrickList.add(this);
			nestingBrickList.add(ifBeginBrick);
			nestingBrickList.add(ifEndBrick);
		}

		return nestingBrickList;
	}

	@Override
	public View getNoPuzzleView(Context context, int brickId, BaseAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(R.layout.brick_if_else, null);
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		LinkedList<SequenceAction> returnActionList = new LinkedList<SequenceAction>();
		returnActionList.add(sequence);
		return returnActionList;
	}

}
