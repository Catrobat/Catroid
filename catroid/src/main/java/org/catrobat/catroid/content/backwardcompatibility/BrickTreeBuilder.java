/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.content.backwardcompatibility;

import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.CompositeBrick;
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicEndBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class BrickTreeBuilder {

	private List<Brick> convertedBricks = new ArrayList<>();
	private Deque<List<Brick>> parentListDeque = new ArrayDeque<>();

	public BrickTreeBuilder() {
		parentListDeque.push(convertedBricks);
	}

	public void convertBricks(List<Brick> bricks) {
		for (Brick brick : bricks) {
			if (brick instanceof CompositeBrick) {
				addCompositeBrick((CompositeBrick) brick);
			} else if (brick instanceof IfLogicElseBrick) {
				addElseBrick();
			} else if (brick instanceof IfLogicEndBrick) {
				addEndBrick();
			} else if (brick instanceof IfThenLogicEndBrick) {
				addEndBrick();
			} else if (brick instanceof LoopEndBrick) {
				addEndBrick();
			} else {
				addBrick(brick);
			}
		}
	}

	private void addCompositeBrick(CompositeBrick brick) {
		parentListDeque.getFirst().add(brick);
		parentListDeque.push(brick.getNestedBricks());
	}

	private void addElseBrick() {
		if (parentListDeque.size() > 1) {
			parentListDeque.pop();
			List<Brick> parentList = parentListDeque.getFirst();

			if (parentList.size() > 0) {
				Brick lastBrickInParentList = parentListDeque.getFirst().get(parentList.size() - 1);
				if (lastBrickInParentList instanceof CompositeBrick && ((CompositeBrick) lastBrickInParentList).hasSecondaryList()) {
					parentListDeque.push(((CompositeBrick) lastBrickInParentList).getSecondaryNestedBricks());
				}
			}
		}
	}

	private void addEndBrick() {
		if (parentListDeque.size() > 1) {
			parentListDeque.pop();
		}
	}

	private void addBrick(Brick brick) {
		parentListDeque.getFirst().add(brick);
	}

	public List<Brick> toList() {
		return convertedBricks;
	}
}
