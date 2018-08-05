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

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class IfLogicEndBrick extends BrickBaseType implements NestingBrick, AllowedAfterDeadEndBrick {

	private static final long serialVersionUID = 1L;

	private transient IfLogicBeginBrick ifBeginBrick;
	private transient IfLogicElseBrick ifElseBrick;

	public IfLogicEndBrick(IfLogicBeginBrick ifLogicBeginBrick, IfLogicElseBrick ifElseBrick) {
		this.ifBeginBrick = ifLogicBeginBrick;
		this.ifElseBrick = ifElseBrick;
	}

	public IfLogicBeginBrick getIfBeginBrick() {
		return ifBeginBrick;
	}

	public void setIfBeginBrick(IfLogicBeginBrick ifBeginBrick) {
		this.ifBeginBrick = ifBeginBrick;
	}

	public IfLogicElseBrick getIfElseBrick() {
		return ifElseBrick;
	}

	public void setIfElseBrick(IfLogicElseBrick ifElseBrick) {
		this.ifElseBrick = ifElseBrick;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_if_end_if;
	}

	@Override
	public boolean isInitialized() {
		return ifElseBrick != null;
	}

	@Override
	public void initialize() {
	}

	@Override
	public boolean isDraggableOver(Brick brick) {
		return brick != ifElseBrick;
	}

	@Override
	public List<NestingBrick> getAllNestingBrickParts() {
		List<NestingBrick> nestingBrickList = new ArrayList<>();
		nestingBrickList.add(ifBeginBrick);
		nestingBrickList.add(ifElseBrick);
		nestingBrickList.add(this);
		return nestingBrickList;
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		LinkedList<ScriptSequenceAction> returnActionList = new LinkedList<>();
		returnActionList.add(sequence);
		return returnActionList;
	}
}
