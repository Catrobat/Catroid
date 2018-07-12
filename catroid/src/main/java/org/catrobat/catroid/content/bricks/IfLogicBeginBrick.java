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
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class IfLogicBeginBrick extends FormulaBrick implements NestingBrick {

	private static final long serialVersionUID = 1L;

	protected transient IfLogicElseBrick ifElseBrick;
	protected transient IfLogicEndBrick ifEndBrick;

	public IfLogicBeginBrick() {
		addAllowedBrickField(BrickField.IF_CONDITION);
	}

	public IfLogicBeginBrick(int condition) {
		initializeBrickFields(new Formula(condition));
	}

	public IfLogicBeginBrick(Formula condition) {
		initializeBrickFields(condition);
	}

	protected void initializeBrickFields(Formula ifCondition) {
		addAllowedBrickField(BrickField.IF_CONDITION);
		setFormulaWithBrickField(BrickField.IF_CONDITION, ifCondition);
	}

	public IfLogicElseBrick getIfElseBrick() {
		return ifElseBrick;
	}

	public void setIfElseBrick(IfLogicElseBrick elseBrick) {
		this.ifElseBrick = elseBrick;
	}

	public IfLogicEndBrick getIfEndBrick() {
		return ifEndBrick;
	}

	public void setIfEndBrick(IfLogicEndBrick ifEndBrick) {
		this.ifEndBrick = ifEndBrick;
	}

	@Override
	public Brick clone() throws CloneNotSupportedException {
		IfLogicBeginBrick clone = (IfLogicBeginBrick) super.clone();
		clone.ifElseBrick = null;
		clone.ifEndBrick = null;
		return clone;
	}

	@Override
	public int getRequiredResources() {
		return getFormulaWithBrickField(BrickField.IF_CONDITION).getRequiredResources();
	}

	@Override
	protected int getLayoutRes() {
		return R.layout.brick_if_begin_if;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = super.getPrototypeView(context);
		((TextView) prototypeView.findViewById(R.id.brick_if_begin_edit_text)).setText(BrickValues.IF_CONDITION);
		return prototypeView;
	}

	@Override
	public View onCreateView(Context context) {
		super.onCreateView(context);

		TextView ifBeginTextView = view.findViewById(R.id.brick_if_begin_edit_text);
		getFormulaWithBrickField(BrickField.IF_CONDITION).setTextFieldId(R.id.brick_if_begin_edit_text);
		getFormulaWithBrickField(BrickField.IF_CONDITION).refreshTextField(view);

		ifBeginTextView.setOnClickListener(this);
		removePrototypeElseTextViews(view);

		return view;
	}

	protected void removePrototypeElseTextViews(View view) {
		view.findViewById(R.id.if_else_prototype_punctuation).setVisibility(View.GONE);
		view.findViewById(R.id.if_prototype_else).setVisibility(View.GONE);
		view.findViewById(R.id.if_else_prototype_punctuation2).setVisibility(View.GONE);
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		FormulaEditorFragment.showFragment(view, this, BrickField.IF_CONDITION);
	}

	@Override
	public void initialize() {
		ifElseBrick = new IfLogicElseBrick(this);
		ifEndBrick = new IfLogicEndBrick(ifElseBrick, this);
	}

	@Override
	public boolean isInitialized() {
		return ifElseBrick != null;
	}

	@Override
	public boolean isDraggableOver(Brick brick) {
		return brick != ifElseBrick;
	}

	@Override
	public List<NestingBrick> getAllNestingBrickParts() {
		List<NestingBrick> nestingBrickList = new ArrayList<>();
		nestingBrickList.add(this);
		nestingBrickList.add(ifElseBrick);
		nestingBrickList.add(ifEndBrick);

		return nestingBrickList;
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		ScriptSequenceAction ifAction = (ScriptSequenceAction) sprite.getActionFactory().eventSequence(sequence.getScript());
		ScriptSequenceAction elseAction = (ScriptSequenceAction) sprite.getActionFactory().eventSequence(sequence.getScript());
		Action action = sprite.getActionFactory().createIfLogicAction(sprite,
				getFormulaWithBrickField(BrickField.IF_CONDITION), ifAction, elseAction);
		sequence.addAction(action);

		LinkedList<ScriptSequenceAction> returnActionList = new LinkedList<>();
		returnActionList.add(elseAction);
		returnActionList.add(ifAction);

		return returnActionList;
	}
}
