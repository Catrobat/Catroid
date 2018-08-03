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

public class RepeatUntilBrick extends FormulaBrick implements LoopBeginBrick {

	private static final long serialVersionUID = 1L;

	private transient LoopEndBrick loopEndBrick;

	public RepeatUntilBrick(int condition) {
		initializeBrickFields(new Formula(condition));
	}

	@Override
	public int getRequiredResources() {
		return getFormulaWithBrickField(BrickField.REPEAT_UNTIL_CONDITION).getRequiredResources();
	}

	public RepeatUntilBrick(Formula condition) {
		initializeBrickFields(condition);
	}

	private void initializeBrickFields(Formula condition) {
		addAllowedBrickField(BrickField.REPEAT_UNTIL_CONDITION);
		setFormulaWithBrickField(BrickField.REPEAT_UNTIL_CONDITION, condition);
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		FormulaEditorFragment.showFragment(view, this, BrickField.REPEAT_UNTIL_CONDITION);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_repeat_until;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);
		TextView edit = view.findViewById(R.id.brick_repeat_until_edit_text);
		getFormulaWithBrickField(BrickField.REPEAT_UNTIL_CONDITION).setTextFieldId(R.id.brick_repeat_until_edit_text);
		getFormulaWithBrickField(BrickField.REPEAT_UNTIL_CONDITION).refreshTextField(view);

		edit.setOnClickListener(this);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = super.getPrototypeView(context);
		TextView textRepeat = prototypeView.findViewById(R.id.brick_repeat_until_edit_text);
		textRepeat.setText(BrickValues.IF_CONDITION);
		return prototypeView;
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		ScriptSequenceAction repeatSequence = (ScriptSequenceAction) sprite.getActionFactory().eventSequence(sequence.getScript());

		Action action = sprite.getActionFactory().createRepeatUntilAction(sprite,
				getFormulaWithBrickField(BrickField.REPEAT_UNTIL_CONDITION), repeatSequence);
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
	public boolean isInitialized() {
		return (loopEndBrick != null);
	}

	@Override
	public void initialize() {
		loopEndBrick = new LoopEndBrick(this);
	}

	@Override
	public boolean isDraggableOver(Brick brick) {
		return (loopEndBrick != null);
	}

	@Override
	public List<NestingBrick> getAllNestingBrickParts() {
		List<NestingBrick> nestingBrickList = new ArrayList<NestingBrick>();
		nestingBrickList.add(this);
		nestingBrickList.add(loopEndBrick);

		return nestingBrickList;
	}
}
