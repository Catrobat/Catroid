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
package org.catrobat.catroid.content;

import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.ui.recyclerview.controller.BrickController;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Script implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	protected List<Brick> brickList = new ArrayList<>();
	protected boolean commentedOut = false;

	protected transient ScriptBrick scriptBrick;

	public List<Brick> getBrickList() {
		return brickList;
	}

	public abstract EventId createEventId(Sprite sprite);

	@Override
	public Script clone() throws CloneNotSupportedException {
		Script clone = (Script) super.clone();
		clone.commentedOut = commentedOut;
		clone.scriptBrick = null;
		clone.brickList = new BrickController().clone(brickList);
		return clone;
	}

	public boolean isCommentedOut() {
		return commentedOut;
	}

	public void setCommentedOut(boolean commentedOut) {
		this.commentedOut = commentedOut;
		for (Brick brick : brickList) {
			brick.setCommentedOut(commentedOut);
		}
	}

	public abstract ScriptBrick getScriptBrick();

	public void setScriptBrick(ScriptBrick scriptBrick) {
		this.scriptBrick = scriptBrick;
	}

	public void run(Sprite sprite, ScriptSequenceAction sequence) {
		if (commentedOut) {
			return;
		}

		ArrayList<ScriptSequenceAction> sequenceList = new ArrayList<>();
		sequenceList.add(sequence);

		for (Brick brick : brickList) {
			if (brick.isCommentedOut()) {
				continue;
			}

			List<ScriptSequenceAction> actions = brick
					.addActionToSequence(sprite, sequenceList.get(sequenceList.size() - 1));

			if (actions != null) {
				for (ScriptSequenceAction action : actions) {
					if (sequenceList.contains(action)) {
						sequenceList.remove(action);
					} else {
						sequenceList.add(action);
					}
				}
			}
		}
	}

	public boolean addBrick(Brick brick) {
		return brickList.add(brick);
	}

	public void addBrick(int position, Brick brick) {
		brickList.add(position, brick);
	}

	public boolean addBricks(List<Brick> bricks) {
		return brickList.addAll(bricks);
	}

	public boolean addBricks(int position, List<Brick> bricks) {
		return brickList.addAll(position, bricks);
	}

	public boolean containsBrick(Brick brick) {
		return brickList.contains(brick);
	}

	public Brick getBrick(int index) {
		return brickList.get(index);
	}

	public boolean removeBrick(Brick brick) {
		return brickList.remove(brick);
	}

	public boolean removeBricks(List<Brick> bricks) {
		return brickList.removeAll(bricks);
	}

	public void addRequiredResources(final Brick.ResourcesSet resourcesSet) {
		for (Brick brick : brickList) {
			if (!brick.isCommentedOut()) {
				brick.addRequiredResources(resourcesSet);
			}
		}
	}
}
