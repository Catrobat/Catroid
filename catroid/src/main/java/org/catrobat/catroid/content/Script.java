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

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicEndBrick;
import org.catrobat.catroid.content.bricks.LoopBeginBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.UserBrick;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Script implements Serializable {

	private static final long serialVersionUID = 1L;

	protected ArrayList<Brick> brickList;
	protected transient ScriptBrick brick;
	protected boolean commentedOut = false;

	protected Script() {
		brickList = new ArrayList<>();
	}

	public ArrayList<Brick> getBrickList() {
		return brickList;
	}

	public abstract Script clone() throws CloneNotSupportedException;

	public List<Brick> cloneBrickList() throws CloneNotSupportedException {
		List<Brick> copies = new ArrayList<>();

		for (Brick brick : brickList) {
			copies.add(brick.clone());
		}

		for (Brick brick : brickList) {
			if (brick instanceof LoopBeginBrick) {
				int begin = brickList.indexOf(brick);
				int end = brickList.indexOf(((LoopBeginBrick) brick).getLoopEndBrick());

				// The structure of the nested bricks should be reworked -> having to update references in all bricks
				// is error prone and has no benefit whatsoever. This workaround should not be necessary:
				if (end == -1) {
					continue;
				}

				LoopBeginBrick beginBrick = (LoopBeginBrick) copies.get(begin);
				LoopEndBrick endBrick = (LoopEndBrick) copies.get(end);

				beginBrick.setLoopEndBrick(endBrick);
				endBrick.setLoopBeginBrick(beginBrick);
			}
			if (brick instanceof IfThenLogicBeginBrick) {
				int begin = brickList.indexOf(brick);
				int end = brickList.indexOf(((IfThenLogicBeginBrick) brick).getIfThenEndBrick());

				IfThenLogicBeginBrick beginBrick = (IfThenLogicBeginBrick) copies.get(begin);
				IfThenLogicEndBrick endBrick = (IfThenLogicEndBrick) copies.get(end);

				beginBrick.setIfThenEndBrick(endBrick);
				endBrick.setIfThenBeginBrick(beginBrick);
			} else if (brick instanceof IfLogicBeginBrick) {
				int begin = brickList.indexOf(brick);
				int middle = brickList.indexOf(((IfLogicBeginBrick) brick).getIfElseBrick());
				int end = brickList.indexOf(((IfLogicBeginBrick) brick).getIfEndBrick());

				// The structure of the nested bricks should be reworked -> having to update references in all bricks
				// is error prone and has no benefit whatsoever. This workaround should not be necessary:
				if (middle == -1 || end == -1) {
					continue;
				}

				IfLogicBeginBrick beginBrick = (IfLogicBeginBrick) copies.get(begin);
				IfLogicElseBrick elseBrick = (IfLogicElseBrick) copies.get(middle);
				IfLogicEndBrick endBrick = (IfLogicEndBrick) copies.get(end);

				beginBrick.setIfElseBrick(elseBrick);
				beginBrick.setIfEndBrick(endBrick);
				elseBrick.setIfBeginBrick(beginBrick);
				elseBrick.setIfEndBrick(endBrick);
				endBrick.setIfBeginBrick(beginBrick);
				endBrick.setIfElseBrick(elseBrick);
			}
		}

		return copies;
	}

	protected Object readResolve() {
		return this;
	}

	public abstract ScriptBrick getScriptBrick();

	public void run(Sprite sprite, SequenceAction sequence) {
		if (this.isCommentedOut()) {
			return;
		}

		ArrayList<SequenceAction> sequenceList = new ArrayList<>();
		sequenceList.add(sequence);
		for (int i = 0; i < brickList.size(); i++) {
			if (brickList.get(i).isCommentedOut()) {
				continue;
			}
			List<SequenceAction> actions = brickList.get(i).addActionToSequence(sprite,
					sequenceList.get(sequenceList.size() - 1));
			if (actions != null) {
				for (SequenceAction action : actions) {
					if (sequenceList.contains(action)) {
						sequenceList.remove(action);
					} else {
						sequenceList.add(action);
					}
				}
			}
		}
	}

	public void addBrick(Brick brick) {
		brickList.add(brick);
		updateUserBricksIfNecessary(brick);
	}

	public void addBrick(int position, Brick brick) {
		brickList.add(position, brick);
		updateUserBricksIfNecessary(brick);
	}

	private void updateUserBricksIfNecessary(Brick brick) {
		if (brick instanceof UserBrick) {
			UserBrick userBrick = (UserBrick) brick;
			userBrick.updateUserBrickParametersAndVariables();
		}
	}

	public void removeBrick(Brick brick) {
		brickList.remove(brick);
	}

	public int getRequiredResources() {
		int resources = Brick.NO_RESOURCES;

		for (Brick brick : brickList) {
			if (!brick.isCommentedOut()) {
				resources |= brick.getRequiredResources();
			}
		}
		return resources;
	}

	public Brick getBrick(int index) {
		return brickList.get(index);
	}

	public void setBrick(ScriptBrick brick) {
		this.brick = brick;
	}

	public boolean isCommentedOut() {
		return commentedOut;
	}

	public void setCommentedOut(boolean commentedOut) {
		this.commentedOut = commentedOut;
		if (commentedOut) {
			for (Brick brick : brickList) {
				brick.setCommentedOut(commentedOut);
			}
		}
	}

	public List<Brick> getBricksRequiringResources(int resource) {
		List<Brick> resourceBrickList = new ArrayList<>();

		for (Brick brick : brickList) {
			if ((brick.getRequiredResources() & resource) != 0) {
				resourceBrickList.add(brick);
			}
		}
		return resourceBrickList;
	}
}
