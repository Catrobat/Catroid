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

import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;

import java.util.ArrayList;
import java.util.List;

public abstract class LoopBeginBrick extends FormulaBrick implements NestingBrick {
	private static final long serialVersionUID = 1L;

	protected LoopEndBrick loopEndBrick;
	private transient long beginLoopTime;

	private transient LoopBeginBrick copy;

	protected LoopBeginBrick() {
	}

	protected void setFirstStartTime() {
		beginLoopTime = System.nanoTime();
	}

	public long getBeginLoopTime() {
		return beginLoopTime;
	}

	public void setBeginLoopTime(long beginLoopTime) {
		this.beginLoopTime = beginLoopTime;
	}

	public LoopEndBrick getLoopEndBrick() {
		return this.loopEndBrick;
	}

	public void setLoopEndBrick(LoopEndBrick loopEndBrick) {
		this.loopEndBrick = loopEndBrick;
	}

	@Override
	public boolean isDraggableOver(Brick brick) {
		return (loopEndBrick != null);
	}

	@Override
	public boolean isInitialized() {
		return (loopEndBrick != null);
	}

	@Override
	public void initialize() {
		loopEndBrick = new LoopEndBrick(sprite, this);
	}

	@Override
	public List<NestingBrick> getAllNestingBrickParts(boolean sorted) {
		List<NestingBrick> nestingBrickList = new ArrayList<NestingBrick>();
		nestingBrickList.add(this);
		nestingBrickList.add(loopEndBrick);

		return nestingBrickList;
	}

	@Override
	public abstract Brick clone();

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		//loopEndBrick will be set in the LoopEndBrick's copyBrickForSprite method
		LoopBeginBrick copyBrick = (LoopBeginBrick) clone();
		copyBrick.sprite = sprite;
		copy = copyBrick;
		return copyBrick;
	}

	public LoopBeginBrick getCopy() {
		return copy;
	}

}
