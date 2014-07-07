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

import java.util.List;

public abstract class NestingBrick extends BrickBaseType {

	private static final long serialVersionUID = 1L;

	public boolean containsDeadEnd() {
		for (Brick brick : getAllNestingBrickParts(false)) {
			if (brick instanceof DeadEndBrick) {
				return true;
			}
		}

		return false;
	}

	@Override
	public abstract Brick clone();

	public abstract boolean isInitialized();

	public abstract void initialize();

	public abstract boolean isDraggableOver(Brick brick);

	/**
	 * @return List of NestingBricks in order of their appearance
	 */
	public abstract List<NestingBrick> getAllNestingBrickParts(boolean sorted);

}
