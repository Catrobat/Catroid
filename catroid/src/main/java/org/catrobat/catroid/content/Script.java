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
package org.catrobat.catroid.content;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.CompositeBrick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.ListSelectorBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.UserDefinedBrick;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.formulaeditor.UserData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Script implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	protected List<Brick> brickList = new ArrayList<>();
	protected boolean commentedOut = false;

	@XStreamAsAttribute
	protected float posX;
	@XStreamAsAttribute
	protected float posY;

	protected UUID scriptId = UUID.randomUUID();

	protected transient ScriptBrick scriptBrick;

	public List<Brick> getBrickList() {
		return brickList;
	}

	public abstract EventId createEventId(Sprite sprite);

	@Override
	public Script clone() throws CloneNotSupportedException {
		Script clone = (Script) super.clone();
		clone.brickList = new ArrayList<>();

		for (Brick brick : brickList) {
			clone.brickList.add(brick.clone());
		}

		clone.commentedOut = commentedOut;
		clone.scriptBrick = null;
		clone.posX = posX;
		clone.posY = posY;
		clone.scriptId = UUID.randomUUID();
		return clone;
	}

	public float getPosX() {
		return posX;
	}

	public void setPosX(float posX) {
		this.posX = posX;
	}

	public float getPosY() {
		return posY;
	}

	public void setPosY(float posY) {
		this.posY = posY;
	}

	public UUID getScriptId() {
		return scriptId;
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

	public void setParents() {
		ScriptBrick scriptBrick = getScriptBrick();
		scriptBrick.setParent(null);
		for (Brick brick : getBrickList()) {
			brick.setParent(scriptBrick);
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

		for (Brick brick : brickList) {
			if (!brick.isCommentedOut()) {
				brick.addActionToSequence(sprite, sequence);
			}
		}
	}

	public boolean addBrick(Brick brick) {
		return brickList.add(brick);
	}

	public void addBrick(int position, Brick brick) {
		brickList.add(position, brick);
	}

	public void addToFlatList(List<Brick> bricks) {
		bricks.add(getScriptBrick());
		for (Brick brick : brickList) {
			brick.addToFlatList(bricks);
		}
	}

	public Brick getBrick(int index) {
		return brickList.get(index);
	}

	public boolean removeBrick(Brick brick) {
		if (brickList.remove(brick)) {
			return true;
		}
		for (Brick brickInList : brickList) {
			if (brickInList.removeChild(brick)) {
				return true;
			}
		}
		return false;
	}

	public void removeAllOccurrencesOfUserDefinedBrick(List<Brick> brickList, UserDefinedBrick userDefinedBrick) {
		for (int brickIndex = 0; brickIndex < brickList.size(); brickIndex++) {
			Brick currentBrick = brickList.get(brickIndex);
			if (currentBrick instanceof CompositeBrick) {
				CompositeBrick currentCompositeBrick = (CompositeBrick) currentBrick;
				removeAllOccurrencesOfUserDefinedBrick(currentCompositeBrick.getNestedBricks(), userDefinedBrick);
				if (currentCompositeBrick.hasSecondaryList()) {
					removeAllOccurrencesOfUserDefinedBrick(currentCompositeBrick.getSecondaryNestedBricks(), userDefinedBrick);
				}
			}
			if (currentBrick instanceof UserDefinedBrick && userDefinedBrick.isUserDefinedBrickDataEqual(currentBrick)) {
				brickList.remove(brickIndex--);
			}
		}
	}

	public void addRequiredResources(final Brick.ResourcesSet resourcesSet) {
		for (Brick brick : brickList) {
			if (!brick.isCommentedOut()) {
				brick.addRequiredResources(resourcesSet);
			}
		}
	}

	public void updateUserDataReferences(String oldName, String newName, UserData<?> item) {
		List<Brick> flatList = new ArrayList<>();
		addToFlatList(flatList);
		boolean containedInListSelector = false;

		for (Brick brick : flatList) {
			if (brick instanceof ListSelectorBrick) {
				containedInListSelector = true;
				break;
			}
		}

		for (Brick brick : flatList) {
			if (brick instanceof FormulaBrick) {
				((FormulaBrick) brick).updateUserDataReference(oldName, newName, item,
						containedInListSelector);
			}
		}
	}

	public void deselectElements(List<UserData<?>> elements) {
		List<Brick> flatList = new ArrayList<>();
		addToFlatList(flatList);
		for (Brick brick : flatList) {
			if (brick instanceof ListSelectorBrick) {
				((ListSelectorBrick) brick).deselectElements(elements);
			}
		}
	}

	public List<Brick> findBricksInScript(List<UUID> brickIds) {
		List<Brick> bricks = new ArrayList<>();

		for (Brick brick : brickList) {
			if (brickIds.contains(brick.getBrickID())) {
				bricks.add(brick);
			} else if (brick instanceof CompositeBrick) {
				List<Brick> tmpBricks = brick.findBricksInNestedBricks(brickIds);
				if (tmpBricks != null) {
					return tmpBricks;
				}
			}

			if (bricks.size() == brickIds.size()) {
				return bricks;
			}
		}

		if (bricks.size() > 0) {
			return bricks;
		}
		return null;
	}

	public List<Brick> removeBricksFromScript(List<UUID> brickIds) {
		List<Brick> bricks = findBricksInScript(brickIds);

		if (bricks != null) {
			for (Brick brick : bricks) {
				removeBrick(brick);
			}
		}

		return bricks;
	}

	public boolean insertBrickAfter(UUID parentId, int subStackIndex, List<Brick> bricksToAdd) {

		if (subStackIndex == -1) {
			if (scriptId.equals(parentId) || getScriptBrick().getBrickID().equals(parentId)) {
				brickList.addAll(0, bricksToAdd);
				return true;
			}

			boolean found = false;
			int index = 0;
			for (Brick brick : brickList) {
				++index;
				if (brick.getBrickID().equals(parentId)) {
					found = true;
					break;
				}
			}
			if (found) {
				brickList.addAll(index, bricksToAdd);
				return true;
			}
		}

		for (Brick brick : brickList) {
			if (brick.addBrickInNestedBrick(parentId, subStackIndex, bricksToAdd)) {
				return true;
			}
		}
		return false;
	}
}
