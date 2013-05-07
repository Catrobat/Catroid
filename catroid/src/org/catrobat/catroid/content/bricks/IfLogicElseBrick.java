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
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class IfLogicElseBrick extends NestingBrick implements AllowedAfterDeadEndBrick {

	private static final long serialVersionUID = 1L;
	private static final String TAG = IfLogicElseBrick.class.getSimpleName();
	private IfLogicBeginBrick ifBeginBrick;
	private IfLogicEndBrick ifEndBrick;

	private transient IfLogicElseBrick copy;

	public IfLogicElseBrick(Sprite sprite, IfLogicBeginBrick ifBeginBrick) {
		this.sprite = sprite;
		this.ifBeginBrick = ifBeginBrick;
		ifBeginBrick.setIfElseBrick(this);
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	public IfLogicElseBrick getCopy() {
		return copy;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.brick_if_else, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_if_else_checkbox);
		final Brick brickInstance = this;

		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
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
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_if_else_layout);
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);
		this.alphaValue = (alphaValue);
		return view;
	}

	@Override
	public Brick clone() {
		return new IfLogicElseBrick(sprite, ifBeginBrick);
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_if_else, null);
	}

	public void setIfEndBrick(IfLogicEndBrick ifEndBrick) {
		this.ifEndBrick = ifEndBrick;
	}

	public void setIfBeginBrick(IfLogicBeginBrick ifBeginBrick) {
		this.ifBeginBrick = ifBeginBrick;
	}

	public IfLogicBeginBrick getIfBeginBrick() {
		return ifBeginBrick;
	}

	public IfLogicEndBrick getIfEndBrick() {
		return ifEndBrick;
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

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		//ifEndBrick and ifBeginBrick will be set in the copyBrickForSprite method of IfLogicEndBrick
		IfLogicElseBrick copyBrick = (IfLogicElseBrick) clone(); //Using the clone method because of its flexibility if new fields are added
		ifBeginBrick.setIfElseBrick(this);
		ifEndBrick.setIfElseBrick(this);

		copyBrick.ifBeginBrick = null;
		copyBrick.ifEndBrick = null;
		copyBrick.sprite = sprite;
		this.copy = copyBrick;
		return copyBrick;
	}

}
