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

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.VisibleForTesting;

public class ForeverBrick extends BrickBaseType implements CompositeBrick {

	private transient EndBrick endBrick = new EndBrick(this);

	private List<Brick> loopBricks = new ArrayList<>();

	public ForeverBrick() {
	}

	@VisibleForTesting
	public EndBrick getEndBrick() {
		return endBrick;
	}

	@Override
	public boolean hasSecondaryList() {
		return false;
	}

	@Override
	public List<Brick> getNestedBricks() {
		return loopBricks;
	}

	@Override
	public List<Brick> getSecondaryNestedBricks() {
		return null;
	}

	public boolean addBrick(Brick brick) {
		return loopBricks.add(brick);
	}

	@Override
	public void setCommentedOut(boolean commentedOut) {
		super.setCommentedOut(commentedOut);
		for (Brick brick : loopBricks) {
			brick.setCommentedOut(commentedOut);
		}
		endBrick.setCommentedOut(commentedOut);
	}

	@Override
	public Brick clone() throws CloneNotSupportedException {
		ForeverBrick clone = (ForeverBrick) super.clone();
		clone.endBrick = new EndBrick(clone);
		clone.loopBricks = new ArrayList<>();
		for (Brick brick : loopBricks) {
			clone.addBrick(brick.clone());
		}
		return clone;
	}

	@Override
	public boolean consistsOfMultipleParts() {
		return true;
	}

	@Override
	public List<Brick> getAllParts() {
		List<Brick> bricks = new ArrayList<>();
		bricks.add(this);
		bricks.add(endBrick);
		return bricks;
	}

	@Override
	public void addToFlatList(List<Brick> bricks) {
		super.addToFlatList(bricks);
		for (Brick brick : loopBricks) {
			brick.addToFlatList(bricks);
		}
		bricks.add(endBrick);
	}

	@Override
	public void setParent(Brick parent) {
		super.setParent(parent);
		for (Brick brick : loopBricks) {
			brick.setParent(this);
		}
	}

	@Override
	public List<Brick> getDragAndDropTargetList() {
		return loopBricks;
	}

	@Override
	public boolean removeChild(Brick brick) {
		if (loopBricks.remove(brick)) {
			return true;
		}
		for (Brick childBrick : loopBricks) {
			if (childBrick.removeChild(brick)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_forever;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		ScriptSequenceAction foreverSequence = (ScriptSequenceAction) ActionFactory.eventSequence(sequence.getScript());

		for (Brick brick : loopBricks) {
			if (!brick.isCommentedOut()) {
				brick.addActionToSequence(sprite, foreverSequence);
			}
		}

		Action action = sprite.getActionFactory().createForeverAction(sprite, foreverSequence);

		sequence.addAction(action);
	}

	@Override
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
		super.addRequiredResources(requiredResourcesSet);
		for (Brick brick : loopBricks) {
			brick.addRequiredResources(requiredResourcesSet);
		}
	}

	private static class EndBrick extends BrickBaseType {

		EndBrick(ForeverBrick parent) {
			this.parent = parent;
		}

		@Override
		public boolean consistsOfMultipleParts() {
			return true;
		}

		@Override
		public List<Brick> getAllParts() {
			return parent.getAllParts();
		}

		@Override
		public void addToFlatList(List<Brick> bricks) {
			parent.addToFlatList(bricks);
		}

		@Override
		public List<Brick> getDragAndDropTargetList() {
			return parent.getParent().getDragAndDropTargetList();
		}

		@Override
		public int getPositionInDragAndDropTargetList() {
			return parent.getParent().getDragAndDropTargetList().indexOf(parent);
		}

		@Override
		public int getViewResource() {
			return R.layout.brick_loop_end;
		}

		@Override
		public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		}
	}
}
