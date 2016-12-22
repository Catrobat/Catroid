/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
import org.catrobat.catroid.content.bricks.NestingBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.UserBrick;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class Script implements Serializable {

	private static final long serialVersionUID = 1L;
	protected ArrayList<Brick> brickList;

	protected transient ScriptBrick brick;

	protected boolean commentedOut = false;

	public Script() {
		brickList = new ArrayList<>();
	}

	public abstract Script copyScriptForSprite(Sprite copySprite);

	public void doCopy(Sprite copySprite, Script cloneScript) {
		ArrayList<Brick> cloneBrickList = cloneScript.getBrickList();
		cloneScript.commentedOut = commentedOut;
		for (Brick brick : getBrickList()) {
			Brick copiedBrick = brick.copyBrickForSprite(copySprite);
			if (!(copiedBrick instanceof ScriptBrick)) {
				copiedBrick.setCommentedOut(brick.isCommentedOut());
			}

			if (copiedBrick instanceof IfLogicEndBrick) {
				setIfBrickReferences((IfLogicEndBrick) copiedBrick, (IfLogicEndBrick) brick);
			} else if (copiedBrick instanceof IfThenLogicEndBrick) {
				setIfThenBrickReferences((IfThenLogicEndBrick) copiedBrick, (IfThenLogicEndBrick) brick);
			} else if (copiedBrick instanceof LoopEndBrick) {
				setLoopBrickReferences((LoopEndBrick) copiedBrick, (LoopEndBrick) brick);
			}
			cloneBrickList.add(copiedBrick);
		}
	}

	protected Object readResolve() {
		return this;
	}

	public abstract ScriptBrick getScriptBrick();

	public void run(Sprite sprite, SequenceAction sequence) {
		if (this.isCommentedOut()) {
			return;
		}

		ArrayList<SequenceAction> sequenceList = new ArrayList<SequenceAction>();
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
		if (brick != null) {
			brickList.add(brick);
			updateUserBricksIfNecessary(brick);
		}
	}

	public void addBrick(int position, Brick brick) {
		if (brick != null) {
			brickList.add(position, brick);
			updateUserBricksIfNecessary(brick);
		}
	}

	private void updateUserBricksIfNecessary(Brick brick) {
		if (brick instanceof UserBrick) {
			UserBrick userBrick = (UserBrick) brick;
			userBrick.updateUserBrickParametersAndVariables();
		}
	}

	public void removeInstancesOfUserBrick(UserBrick userBrickToRemove) {

		LinkedList<Brick> toRemove = new LinkedList<>();

		for (Brick brick : brickList) {
			if (brick instanceof UserBrick) {
				UserBrick userBrick = (UserBrick) brick;
				if (userBrick.getDefinitionBrick() == userBrickToRemove.getDefinitionBrick()) {
					toRemove.add(brick);
				}
			}
		}

		for (Brick brick : toRemove) {
			brickList.remove(brick);
		}
	}

	public void removeBricks(List<Brick> bricksToRemove) {
		for (Brick brick : bricksToRemove) {
			removeBrick(brick);
		}
	}

	public void removeBrick(Brick brick) {
		brickList.remove(brick);
	}

	public ArrayList<Brick> getBrickList() {
		return brickList;
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

	public boolean containsBrickOfType(Class<?> type) {
		for (Brick brick : brickList) {
			//Log.i(TAG, brick.REQUIRED_RESSOURCES + "");
			if (brick.getClass() == type) {
				return true;
			}
		}
		return false;
	}

	public int containsBrickOfTypeReturnsFirstIndex(Class<?> type) {
		int i = 0;
		for (Brick brick : brickList) {

			if (brick.getClass() == type) {
				return i;
			}
			i++;
		}
		return -1;
	}

	//
	//	public boolean containsBluetoothBrick() {
	//		for (Brick brick : brickList) {
	//			if ((brick instanceof NXTMotorActionBrick) || (brick instanceof NXTMotorTurnAngleBrick)
	//					|| (brick instanceof NXTMotorStopBrick) || (brick instanceof NXTPlayToneBrick)) {
	//				return true;
	//			}
	//		}
	//		return false;
	//	}

	public Brick getBrick(int index) {
		if (index < 0 || index >= brickList.size()) {
			return null;
		}

		return brickList.get(index);
	}

	public void setBrick(ScriptBrick brick) {
		this.brick = brick;
	}

	protected void setIfBrickReferences(IfLogicEndBrick copiedIfEndBrick, IfLogicEndBrick originalIfEndBrick) {
		List<NestingBrick> ifBrickList = originalIfEndBrick.getAllNestingBrickParts(true);
		IfLogicBeginBrick copiedIfBeginBrick = ((IfLogicBeginBrick) ifBrickList.get(0)).getCopy();
		IfLogicElseBrick copiedIfElseBrick = ((IfLogicElseBrick) ifBrickList.get(1)).getCopy();

		copiedIfBeginBrick.setIfElseBrick(copiedIfElseBrick);
		copiedIfBeginBrick.setIfEndBrick(copiedIfEndBrick);
		copiedIfElseBrick.setIfBeginBrick(copiedIfBeginBrick);
		copiedIfElseBrick.setIfEndBrick(copiedIfEndBrick);
		copiedIfEndBrick.setIfBeginBrick(copiedIfBeginBrick);
		copiedIfEndBrick.setIfElseBrick(copiedIfElseBrick);
	}

	protected void setIfThenBrickReferences(IfThenLogicEndBrick copiedIfEndBrick, IfThenLogicEndBrick
			originalIfEndBrick) {
		List<NestingBrick> ifBrickList = originalIfEndBrick.getAllNestingBrickParts(true);
		IfThenLogicBeginBrick copiedIfBeginBrick = (IfThenLogicBeginBrick) ((IfThenLogicBeginBrick) ifBrickList.get(0)).getCopy();

		copiedIfBeginBrick.setIfThenEndBrick(copiedIfEndBrick);
		copiedIfEndBrick.setIfThenBeginBrick(copiedIfBeginBrick);
	}

	protected void setLoopBrickReferences(LoopEndBrick copiedBrick, LoopEndBrick originalBrick) {
		List<NestingBrick> loopBrickList = originalBrick.getAllNestingBrickParts(true);
		LoopBeginBrick copiedLoopBeginBrick = ((LoopBeginBrick) loopBrickList.get(0)).getCopy();

		copiedLoopBeginBrick.setLoopEndBrick(copiedBrick);
		copiedBrick.setLoopBeginBrick(copiedLoopBeginBrick);
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
}
