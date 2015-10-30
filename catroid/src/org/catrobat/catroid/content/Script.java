/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.LoopBeginBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.NestingBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrick;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class Script implements Serializable {

	private static final long serialVersionUID = 1L;
	protected ArrayList<Brick> brickList;

	protected transient ScriptBrick brick;

	private transient volatile boolean paused;

	public Script() {
		brickList = new ArrayList<Brick>();
		init();
	}

	public abstract Script copyScriptForSprite(Sprite copySprite, List<UserBrick> preCopiedUserBricks);

	public void doCopy(Sprite copySprite, Script cloneScript, List<UserBrick> preCopiedUserBricks) {
		ArrayList<Brick> cloneBrickList = cloneScript.getBrickList();
		for (Brick brick : getBrickList()) {
			Brick copiedBrick = null;
			if (brick instanceof UserBrick) {
				UserBrick original = ((UserBrick) brick);
				UserBrick precopiedRootBrick = findBrickWithId(preCopiedUserBricks, original.getUserBrickId());
				ProjectManager.getInstance().setCurrentUserBrick(precopiedRootBrick);
				UserBrick copiedUserBrick = precopiedRootBrick.copyBrickForSprite(copySprite);
				copiedUserBrick
						.copyFormulasMatchingNames(original.getUserBrickParameters(), copiedUserBrick.getUserBrickParameters());

				copiedBrick = precopiedRootBrick;
			} else if (brick instanceof UserScriptDefinitionBrick) {
				UserScriptDefinitionBrick preCopiedDefinitionBrick = findBrickWithId(preCopiedUserBricks, ((UserScriptDefinitionBrick) brick).getUserBrickId()).getDefinitionBrick();
				cloneScript.addBrick(preCopiedDefinitionBrick);
			} else {
				copiedBrick = brick.copyBrickForSprite(copySprite);
			}

			if (copiedBrick instanceof IfLogicEndBrick) {
				setIfBrickReferences((IfLogicEndBrick) copiedBrick, (IfLogicEndBrick) brick);
			} else if (copiedBrick instanceof LoopEndBrick) {
				setLoopBrickReferences((LoopEndBrick) copiedBrick, (LoopEndBrick) brick);
			}
			cloneBrickList.add(copiedBrick);
		}
	}

	protected UserBrick findBrickWithId(List<UserBrick> list, int id) {
		for (UserBrick brick : list) {
			if (brick.getUserBrickId() == id) {
				return brick;
			}
		}
		return null;
	}

	protected Object readResolve() {
		init();
		return this;
	}

	public abstract ScriptBrick getScriptBrick();

	private void init() {
		paused = false;
	}

	public void run(Sprite sprite, SequenceAction sequence) {
		ArrayList<SequenceAction> sequenceList = new ArrayList<SequenceAction>();
		sequenceList.add(sequence);
		for (int i = 0; i < brickList.size(); i++) {
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
		}
	}

	public void addBrick(int position, Brick brick) {
		if (brick != null) {
			brickList.add(position, brick);
		}
	}

	public void removeInstancesOfUserBrick(UserBrick userBrickToRemove) {

		LinkedList<Brick> toRemove = new LinkedList<Brick>();

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

	public void removeBrick(Brick brick) {
		brickList.remove(brick);
	}

	public ArrayList<Brick> getBrickList() {
		return brickList;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public boolean isPaused() {
		return paused;
	}

	public int getRequiredResources() {
		int resources = Brick.NO_RESOURCES;

		for (Brick brick : brickList) {
			resources |= brick.getRequiredResources();
		}
		return resources;
	}

	public boolean containsBrickOfType(Class<?> type) {
		for (Brick brick : brickList) {
			//Log.i("bt", brick.REQUIRED_RESSOURCES + "");
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

	protected void setLoopBrickReferences(LoopEndBrick copiedBrick, LoopEndBrick originalBrick) {
		List<NestingBrick> loopBrickList = originalBrick.getAllNestingBrickParts(true);
		LoopBeginBrick copiedLoopBeginBrick = ((LoopBeginBrick) loopBrickList.get(0)).getCopy();

		copiedLoopBeginBrick.setLoopEndBrick(copiedBrick);
		copiedBrick.setLoopBeginBrick(copiedLoopBeginBrick);
	}
}
