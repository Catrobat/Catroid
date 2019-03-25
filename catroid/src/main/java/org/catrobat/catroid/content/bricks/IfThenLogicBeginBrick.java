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

import android.view.View;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class IfThenLogicBeginBrick extends FormulaBrick implements ControlStructureBrick {

	private static final long serialVersionUID = 1L;

	private transient IfThenLogicEndBrick ifEndBrick;

	public IfThenLogicBeginBrick() {
		addAllowedBrickField(BrickField.IF_CONDITION, R.id.brick_if_begin_edit_text);
	}

	public IfThenLogicBeginBrick(int condition) {
		this(new Formula(condition));
	}

	public IfThenLogicBeginBrick(Formula formula) {
		this();
		setFormulaWithBrickField(BrickField.IF_CONDITION, formula);
	}

	public IfThenLogicEndBrick getIfThenEndBrick() {
		return ifEndBrick;
	}

	public void setIfThenEndBrick(IfThenLogicEndBrick ifEndBrick) {
		this.ifEndBrick = ifEndBrick;
	}

	@Override
	public BrickBaseType clone() throws CloneNotSupportedException {
		IfThenLogicBeginBrick clone = (IfThenLogicBeginBrick) super.clone();
		clone.ifEndBrick = null;
		return clone;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_if_then_begin_if;
	}

	@Override
	public void onViewCreated() {
		super.onViewCreated();
		TextView prototypeTextPunctuation = view.findViewById(R.id.if_prototype_punctuation);
		prototypeTextPunctuation.setVisibility(View.GONE);
	}

	@Override
	public Brick getFirstBrick() {
		return this;
	}

	@Override
	public Brick getLastBrick() {
		return ifEndBrick;
	}

	@Override
	public List<Brick> getAllParts() {
		List<Brick> parts = new ArrayList<>();
		parts.add(this);
		parts.add(ifEndBrick);
		return parts;
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		ScriptSequenceAction ifAction = (ScriptSequenceAction) ActionFactory.eventSequence(sequence.getScript());

		Action action = sprite.getActionFactory()
				.createIfLogicAction(sprite, getFormulaWithBrickField(BrickField.IF_CONDITION), ifAction, null);
		sequence.addAction(action);

		LinkedList<ScriptSequenceAction> returnActionList = new LinkedList<>();
		returnActionList.add(ifAction);

		return returnActionList;
	}
}
