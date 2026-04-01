/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.utils.LoopUtil;

import java.util.ArrayList;
import java.util.List;

public class ForVariableFromToBrick extends UserVariableBrickWithFormula implements CompositeBrick {

	private transient EndBrick endBrick = new EndBrick(this);

	private List<Brick> loopBricks = new ArrayList<>();

	public ForVariableFromToBrick() {
		addAllowedBrickField(BrickField.FOR_LOOP_FROM, R.id.brick_loop_from_edit);
		addAllowedBrickField(BrickField.FOR_LOOP_TO, R.id.brick_loop_to_edit);
	}

	public ForVariableFromToBrick(int from, int to) {
		this(new Formula(from), new Formula(to));
	}

	public ForVariableFromToBrick(Formula from, Formula to) {
		this();
		setFormulaWithBrickField(BrickField.FOR_LOOP_FROM, from);
		setFormulaWithBrickField(BrickField.FOR_LOOP_TO, to);
	}

	@Override
	protected int getSpinnerId() {
		return R.id.brick_for_variable_spinner;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_for_variable_from_to;
	}

	@Override
	public BrickField getDefaultBrickField() {
		return BrickField.FOR_LOOP_FROM;
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
	}

	@Override
	public Brick clone() throws CloneNotSupportedException {
		ForVariableFromToBrick clone = (ForVariableFromToBrick) super.clone();
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
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		if (userVariable == null || userVariable.getName() == null) {
			userVariable = new UserVariable("NoVariableSet",
					CatroidApplication.getAppContext().getString(R.string.no_variable_selected));
			userVariable.setDummy(true);
		}

		ScriptSequenceAction repeatSequence = (ScriptSequenceAction) ActionFactory.createScriptSequenceAction(sequence.getScript());
		boolean isLoopDelay = LoopUtil.checkLoopBrickForLoopDelay(this, sequence.getScript());

		for (Brick brick : loopBricks) {
			if (!brick.isCommentedOut()) {
				brick.addActionToSequence(sprite, repeatSequence);
			}
		}

		Action action = sprite.getActionFactory()
				.createForVariableFromToAction(sprite, sequence, userVariable,
						getFormulaWithBrickField(BrickField.FOR_LOOP_FROM),
						getFormulaWithBrickField(BrickField.FOR_LOOP_TO), repeatSequence, isLoopDelay);

		sequence.addAction(action);
	}
}
