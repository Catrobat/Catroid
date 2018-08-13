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
import org.catrobat.catroid.content.bricks.IfElseLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicEndBrick;
import org.catrobat.catroid.content.bricks.LoopBeginBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.content.eventids.EventId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Script implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	protected ArrayList<Brick> brickList = new ArrayList<>();
	protected boolean commentedOut = false;

	protected transient ScriptBrick scriptBrick;

	public ArrayList<Brick> getBrickList() {
		return brickList;
	}

	public abstract EventId createEventId(Sprite sprite);

	@Override
	public Script clone() throws CloneNotSupportedException {
		Script clone = (Script) super.clone();
		clone.commentedOut = commentedOut;
		clone.scriptBrick = null;
		clone.brickList = cloneBrickList();
		return clone;
	}

	private ArrayList<Brick> cloneBrickList() throws CloneNotSupportedException {
		ArrayList<Brick> clones = new ArrayList<>();

		for (Brick brick : brickList) {
			clones.add(brick.clone());
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

				LoopBeginBrick beginBrick = (LoopBeginBrick) clones.get(begin);
				LoopEndBrick endBrick = (LoopEndBrick) clones.get(end);

				beginBrick.setLoopEndBrick(endBrick);
				endBrick.setLoopBeginBrick(beginBrick);
			}
			if (brick instanceof IfThenLogicBeginBrick) {
				int begin = brickList.indexOf(brick);
				int end = brickList.indexOf(((IfThenLogicBeginBrick) brick).getIfThenEndBrick());

				IfThenLogicBeginBrick beginBrick = (IfThenLogicBeginBrick) clones.get(begin);
				IfThenLogicEndBrick endBrick = (IfThenLogicEndBrick) clones.get(end);

				beginBrick.setIfThenEndBrick(endBrick);
				endBrick.setIfThenBeginBrick(beginBrick);
			} else if (brick instanceof IfElseLogicBeginBrick) {
				int begin = brickList.indexOf(brick);
				int middle = brickList.indexOf(((IfElseLogicBeginBrick) brick).getIfElseBrick());
				int end = brickList.indexOf(((IfElseLogicBeginBrick) brick).getIfEndBrick());

				// The structure of the nested bricks should be reworked -> having to update references in all bricks
				// is error prone and has no benefit whatsoever. This workaround should not be necessary:
				if (middle == -1 || end == -1) {
					continue;
				}

				IfElseLogicBeginBrick beginBrick = (IfElseLogicBeginBrick) clones.get(begin);
				IfLogicElseBrick elseBrick = (IfLogicElseBrick) clones.get(middle);
				IfLogicEndBrick endBrick = (IfLogicEndBrick) clones.get(end);

				beginBrick.setIfElseBrick(elseBrick);
				beginBrick.setIfEndBrick(endBrick);
				elseBrick.setIfBeginBrick(beginBrick);
				elseBrick.setIfEndBrick(endBrick);
				endBrick.setIfBeginBrick(beginBrick);
				endBrick.setIfElseBrick(elseBrick);
			}
		}

		return clones;
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

	public void addBrick(Brick brick) {
		brickList.add(brick);
		updateUserBricksIfNecessary(brick);
	}

	public void addBrick(int position, Brick brick) {
		brickList.add(position, brick);
		updateUserBricksIfNecessary(brick);
	}

	public Brick getBrick(int index) {
		return brickList.get(index);
	}

	public void removeBrick(Brick brick) {
		brickList.remove(brick);
	}

	private void updateUserBricksIfNecessary(Brick brick) {
		if (brick instanceof UserBrick) {
			UserBrick userBrick = (UserBrick) brick;
			userBrick.updateUserBrickParametersAndVariables();
		}
	}

	public void addRequiredResources(final Brick.ResourcesSet resourcesSet) {
		for (Brick brick : brickList) {
			if (!brick.isCommentedOut()) {
				brick.addRequiredResources(resourcesSet);
			}
		}
	}
}
