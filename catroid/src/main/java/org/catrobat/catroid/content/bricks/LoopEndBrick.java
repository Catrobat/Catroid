/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LoopEndBrick extends BrickBaseType implements NestingBrick, AllowedAfterDeadEndBrick {

	private static final long serialVersionUID = 1L;
	private static final String TAG = LoopEndBrick.class.getSimpleName();

	private transient LoopBeginBrick loopBeginBrick;

	public LoopEndBrick() {
	}

	public LoopEndBrick(LoopBeginBrick loopBeginBrick) {
		this.loopBeginBrick = loopBeginBrick;
	}

	public LoopBeginBrick getLoopBeginBrick() {
		return loopBeginBrick;
	}

	public void setLoopBeginBrick(LoopBeginBrick loopBeginBrick) {
		this.loopBeginBrick = loopBeginBrick;
	}

	@Override
	public Brick clone() throws CloneNotSupportedException {
		LoopEndBrick clone = (LoopEndBrick) super.clone();
		clone.loopBeginBrick = null;
		return clone;
	}

	@Override
	protected int getLayoutRes() {
		return R.layout.brick_loop_end;
	}

	@Override
	public void initialize() {
		loopBeginBrick = new ForeverBrick();
		Log.w(TAG, "Not supposed to create the LoopBeginBrick!");
	}

	@Override
	public boolean isDraggableOver(Brick brick) {
		return (loopBeginBrick != null);
	}

	@Override
	public boolean isInitialized() {
		return (loopBeginBrick != null);
	}

	@Override
	public List<NestingBrick> getAllNestingBrickParts(boolean sorted) {
		List<NestingBrick> nestingBrickList = new ArrayList<>();
		if (sorted) {
			nestingBrickList.add(loopBeginBrick);
			nestingBrickList.add(this);
		} else {
			nestingBrickList.add(this);
			nestingBrickList.add(loopBeginBrick);
		}
		return nestingBrickList;
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		LinkedList<ScriptSequenceAction> returnActionList = new LinkedList<>();
		returnActionList.add(sequence);
		return returnActionList;
	}
}
