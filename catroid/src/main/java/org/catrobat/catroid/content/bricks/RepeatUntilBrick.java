/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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
import org.catrobat.catroid.formulaeditor.Formula;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RepeatUntilBrick extends FormulaBrick implements LoopBeginBrick {

	private static final long serialVersionUID = 1L;

	private transient LoopEndBrick loopEndBrick;

	public RepeatUntilBrick() {
		addAllowedBrickField(BrickField.REPEAT_UNTIL_CONDITION, R.id.brick_repeat_until_edit_text);
	}

	public RepeatUntilBrick(int condition) {
		this(new Formula(condition));
	}

	public RepeatUntilBrick(Formula condition) {
		this();
		setFormulaWithBrickField(BrickField.REPEAT_UNTIL_CONDITION, condition);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_repeat_until;
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		ScriptSequenceAction repeatSequence = (ScriptSequenceAction) ActionFactory.eventSequence(sequence.getScript());

		Action action = sprite.getActionFactory()
				.createRepeatUntilAction(sprite, getFormulaWithBrickField(BrickField.REPEAT_UNTIL_CONDITION), repeatSequence);

		sequence.addAction(action);
		LinkedList<ScriptSequenceAction> returnActionList = new LinkedList<>();
		returnActionList.add(repeatSequence);
		return returnActionList;
	}

	@Override
	public LoopEndBrick getLoopEndBrick() {
		return loopEndBrick;
	}

	@Override
	public void setLoopEndBrick(LoopEndBrick loopEndBrick) {
		this.loopEndBrick = loopEndBrick;
	}

	@Override
	public Brick getFirstBrick() {
		return this;
	}

	@Override
	public Brick getLastBrick() {
		return loopEndBrick;
	}

	@Override
	public List<Brick> getAllParts() {
		List<Brick> parts = new ArrayList<>();
		parts.add(this);
		parts.add(loopEndBrick);
		return parts;
	}
}
