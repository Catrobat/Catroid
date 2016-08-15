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
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class IfThenLogicBeginBrick extends IfLogicBeginBrick implements NestingBrick {
	private static final long serialVersionUID = 1L;
	protected transient IfThenLogicEndBrick ifEndBrick;

	public IfThenLogicBeginBrick(int condition) {
		initializeBrickFields(new Formula(condition));
	}

	public IfThenLogicBeginBrick(Formula condition) {
		initializeBrickFields(condition);
	}

	public void setIfThenEndBrick(IfThenLogicEndBrick ifEndBrick) {
		this.ifEndBrick = ifEndBrick;
	}

	@Override
	public Brick clone() {
		return new IfThenLogicBeginBrick(getFormulaWithBrickField(BrickField.IF_CONDITION).clone());
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = super.getPrototypeView(context);
		removePrototypeElseTextViews(prototypeView);
		return prototypeView;
	}

	@Override
	public void initialize() {
		ifEndBrick = new IfThenLogicEndBrick(this);
	}

	@Override
	public List<NestingBrick> getAllNestingBrickParts(boolean sorted) {
		List<NestingBrick> nestingBrickList = new ArrayList<>();
		nestingBrickList.add(this);
		nestingBrickList.add(ifEndBrick);
		return nestingBrickList;
	}

	@Override
	public boolean isDraggableOver(Brick brick) {
		return brick != ifEndBrick;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		IfThenLogicBeginBrick copyBrick = (IfThenLogicBeginBrick) clone();
		copyBrick.ifEndBrick = null; // will be set in copyBrickForSprite method of IfThenLogicEndBrick

		this.copy = copyBrick;
		return copyBrick;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		SequenceAction ifAction = (SequenceAction) sprite.getActionFactory().createSequence();
		Action action = sprite.getActionFactory().createIfLogicAction(sprite,
				getFormulaWithBrickField(BrickField.IF_CONDITION), ifAction, null);
		sequence.addAction(action);

		LinkedList<SequenceAction> returnActionList = new LinkedList<>();
		returnActionList.add(ifAction);

		return returnActionList;
	}
}
