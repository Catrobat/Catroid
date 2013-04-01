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

public class IfLogicEndBrick extends NestingBrick implements AllowedAfterDeadEndBrick {

	static final int FOREVER = -1;
	private static final long serialVersionUID = 1L;
	private static final String TAG = IfLogicEndBrick.class.getSimpleName();
	private IfLogicElseBrick elseBrick;
	private IfLogicBeginBrick beginBrick;

	public IfLogicEndBrick(Sprite sprite, IfLogicElseBrick elseBrick, IfLogicBeginBrick beginBrick) {
		this.sprite = sprite;
		this.elseBrick = elseBrick;
		this.beginBrick = beginBrick;
		elseBrick.setIfEndBrick(this);
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
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.brick_if_end_if, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_if_end_if_checkbox);
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
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_if_end_if_layout);
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);
		this.alphaValue = (alphaValue);
		return view;
	}

	@Override
	public Brick clone() {
		return new IfLogicEndBrick(getSprite(), elseBrick, beginBrick);
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_if_end_if, null);
	}

	@Override
	public boolean isDraggableOver(Brick brick) {
		if (brick == elseBrick) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean isInitialized() {
		if (elseBrick == null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void initialize() {
		//elseBrick = new IfLogicElseBrick(sprite);
		Log.w(TAG, "Cannot create the IfLogic Bricks from here!");
	}

	@Override
	public List<NestingBrick> getAllNestingBrickParts(boolean sorted) {
		//TODO: handle sorting
		List<NestingBrick> nestingBrickList = new ArrayList<NestingBrick>();
		if (sorted) {
			nestingBrickList.add(beginBrick);
			nestingBrickList.add(elseBrick);
			nestingBrickList.add(this);
		} else {
			nestingBrickList.add(this);
			nestingBrickList.add(beginBrick);
			//nestingBrickList.add(elseBrick);
		}

		return nestingBrickList;
	}

	@Override
	public View getNoPuzzleView(Context context, int brickId, BaseAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(R.layout.brick_if_end_if, null);
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		LinkedList<SequenceAction> returnActionList = new LinkedList<SequenceAction>();
		returnActionList.add(sequence);
		return returnActionList;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		IfLogicBeginBrick beginBrick = null;
		IfLogicElseBrick elseBrick = null;

		ArrayList<Brick> currentBrickList = script.getBrickList();
		int loopEnds = 0;
		for (int i = currentBrickList.size() - 1; i >= 0; i--) {
			Brick b = currentBrickList.get(i);
			if (b instanceof IfLogicBeginBrick) {
				if (loopEnds > 0) {
					loopEnds--;
				} else {
					beginBrick = (IfLogicBeginBrick) b;
					break;
				}
			} else if (b instanceof IfLogicElseBrick) {
				if (loopEnds != 0) {
					elseBrick = (IfLogicElseBrick) b;
				}
			} else if (b instanceof IfLogicEndBrick) {
				loopEnds++;
			}
		}

		IfLogicEndBrick copyBrick = (IfLogicEndBrick) clone(); //Using the clone method because of its flexibility if new fields are added
		copyBrick.sprite = sprite;
		copyBrick.elseBrick = elseBrick;
		copyBrick.beginBrick = beginBrick;

		beginBrick.setElseBrick(elseBrick);
		beginBrick.setEndBrick(this);
		if (elseBrick != null) {
			elseBrick.setIfEndBrick(this);
		}

		return copyBrick;
	}

}
