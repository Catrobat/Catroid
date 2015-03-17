/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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

import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.content.Sprite;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class IfLogicElseBrick extends BrickBaseType implements NestingBrick, AllowedAfterDeadEndBrick {

	private static final long serialVersionUID = 1L;
	private static final String TAG = IfLogicElseBrick.class.getSimpleName();
	private transient IfLogicBeginBrick ifBeginBrick;
	private transient IfLogicEndBrick ifEndBrick;

	private transient IfLogicElseBrick copy;

	public IfLogicElseBrick(IfLogicBeginBrick ifBeginBrick) {
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
	public Brick clone() {
		return new IfLogicElseBrick(ifBeginBrick);
	}

	public IfLogicBeginBrick getIfBeginBrick() {
		return ifBeginBrick;
	}

	public void setIfBeginBrick(IfLogicBeginBrick ifBeginBrick) {
		this.ifBeginBrick = ifBeginBrick;
	}

	public IfLogicEndBrick getIfEndBrick() {
		return ifEndBrick;
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
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		LinkedList<SequenceAction> returnActionList = new LinkedList<SequenceAction>();
		returnActionList.add(sequence);
		return returnActionList;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		//ifEndBrick and ifBeginBrick will be set in the copyBrickForSprite method of IfLogicEndBrick
		IfLogicElseBrick copyBrick = (IfLogicElseBrick) clone(); //Using the clone method because of its flexibility if new fields are added
		ifBeginBrick.setIfElseBrick(this);
		ifEndBrick.setIfElseBrick(this);

		copyBrick.ifBeginBrick = null;
		copyBrick.ifEndBrick = null;
		this.copy = copyBrick;
		return copyBrick;
	}

}
