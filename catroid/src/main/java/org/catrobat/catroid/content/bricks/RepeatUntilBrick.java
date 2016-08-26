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
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RepeatUntilBrick extends FormulaBrick implements LoopBeginBrick {
	private static final long serialVersionUID = 1L;

	private transient View prototypeView;

	protected transient LoopEndBrick loopEndBrick;
	private transient long beginLoopTime;

	private transient LoopBeginBrick copy;

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
	public Brick clone() {
		return new RepeatUntilBrick(getFormulaWithBrickField(BrickField.REPEAT_UNTIL_CONDITION).clone());
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		FormulaEditorFragment.showFragment(view, this, BrickField.REPEAT_UNTIL_CONDITION);
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_repeat_until, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		setCheckboxView(R.id.brick_repeat_until_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				checked = !checked;
				if (!checked) {
					for (Brick currentBrick : adapter.getCheckedBricks()) {
						currentBrick.setCheckedBoolean(false);
					}
				}
				adapter.handleCheck(brickInstance, checked);
			}
		});

		TextView text = (TextView) view.findViewById(R.id.brick_repeat_until_prototype_text_view);
		TextView edit = (TextView) view.findViewById(R.id.brick_repeat_until_edit_text);
		getFormulaWithBrickField(BrickField.REPEAT_UNTIL_CONDITION).setTextFieldId(R.id.brick_repeat_until_edit_text);
		getFormulaWithBrickField(BrickField.REPEAT_UNTIL_CONDITION).refreshTextField(view);

		text.setVisibility(View.GONE);
		edit.setVisibility(View.VISIBLE);

		edit.setOnClickListener(this);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_repeat_until, null);
		TextView textRepeat = (TextView) prototypeView.findViewById(R.id.brick_repeat_until_prototype_text_view);
		textRepeat.setText(String.valueOf(BrickValues.IF_CONDITION));
		return prototypeView;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		Action repeatSequence = sprite.getActionFactory().createSequence();

		Action action = sprite.getActionFactory().createRepeatUntilAction(sprite, getFormulaWithBrickField(BrickField.REPEAT_UNTIL_CONDITION),
				repeatSequence);
		sequence.addAction(action);
		LinkedList<SequenceAction> returnActionList = new LinkedList<SequenceAction>();
		returnActionList.add((SequenceAction) repeatSequence);
		return returnActionList;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		RepeatUntilBrick copyBrick = (RepeatUntilBrick) clone();
		copy = copyBrick;
		return copyBrick;
	}

	@Override
	public long getBeginLoopTime() {
		return beginLoopTime;
	}

	@Override
	public void setBeginLoopTime(long beginLoopTime) {
		this.beginLoopTime = beginLoopTime;
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
	public LoopBeginBrick getCopy() {
		return copy;
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
	public List<NestingBrick> getAllNestingBrickParts(boolean sorted) {
		List<NestingBrick> nestingBrickList = new ArrayList<NestingBrick>();
		nestingBrickList.add(this);
		nestingBrickList.add(loopEndBrick);

		return nestingBrickList;
	}

	@Override
	public void updateReferenceAfterMerge(Scene into, Scene from) {
	}
}
