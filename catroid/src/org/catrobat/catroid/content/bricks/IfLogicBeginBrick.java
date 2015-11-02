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
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class IfLogicBeginBrick extends FormulaBrick implements NestingBrick {
	private static final long serialVersionUID = 1L;
	private static final String TAG = IfLogicBeginBrick.class.getSimpleName();
	public static final int EXECUTE_ELSE_PART = -1;
	protected transient IfLogicElseBrick ifElseBrick;
	protected transient IfLogicEndBrick ifEndBrick;
	private transient IfLogicBeginBrick copy;

	public IfLogicBeginBrick() {
		addAllowedBrickField(BrickField.IF_CONDITION);
	}

	public IfLogicBeginBrick(int condition) {
		initializeBrickFields(new Formula(condition));
	}

	public IfLogicBeginBrick(Formula condition) {
		initializeBrickFields(condition);
	}

	private void initializeBrickFields(Formula ifCondition) {
		addAllowedBrickField(BrickField.IF_CONDITION);
		setFormulaWithBrickField(BrickField.IF_CONDITION, ifCondition);
	}

	@Override
	public int getRequiredResources() {
		return getFormulaWithBrickField(BrickField.IF_CONDITION).getRequiredResources();
	}

	public IfLogicElseBrick getIfElseBrick() {
		return ifElseBrick;
	}

	public IfLogicEndBrick getIfEndBrick() {
		return ifEndBrick;
	}

	public IfLogicBeginBrick getCopy() {
		return copy;
	}

	public void setIfElseBrick(IfLogicElseBrick elseBrick) {
		this.ifElseBrick = elseBrick;
	}

	public void setIfEndBrick(IfLogicEndBrick ifEndBrick) {
		this.ifEndBrick = ifEndBrick;
	}

	@Override
	public Brick clone() {
		return new IfLogicBeginBrick(getFormulaWithBrickField(BrickField.IF_CONDITION).clone());
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		FormulaEditorFragment.showFragment(view, this, BrickField.IF_CONDITION);
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}

		view = View.inflate(context, R.layout.brick_if_begin_if, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_if_begin_checkbox);
		final Brick brickInstance = this;

		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView prototypeTextView = (TextView) view.findViewById(R.id.brick_if_begin_prototype_text_view);
		TextView ifBeginTextView = (TextView) view.findViewById(R.id.brick_if_begin_edit_text);

		getFormulaWithBrickField(BrickField.IF_CONDITION).setTextFieldId(R.id.brick_if_begin_edit_text);
		getFormulaWithBrickField(BrickField.IF_CONDITION).refreshTextField(view);

		prototypeTextView.setVisibility(View.GONE);
		ifBeginTextView.setVisibility(View.VISIBLE);

		ifBeginTextView.setOnClickListener(this);

		return view;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_if_begin_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView ifLabel = (TextView) view.findViewById(R.id.if_label);
			TextView ifLabelEnd = (TextView) view.findViewById(R.id.if_label_second_part);
			TextView editX = (TextView) view.findViewById(R.id.brick_if_begin_edit_text);
			ifLabel.setTextColor(ifLabel.getTextColors().withAlpha(alphaValue));
			ifLabelEnd.setTextColor(ifLabelEnd.getTextColors().withAlpha(alphaValue));
			editX.setTextColor(editX.getTextColors().withAlpha(alphaValue));
			editX.getBackground().setAlpha(alphaValue);

			this.alphaValue = alphaValue;
		}

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_if_begin_if, null);
		TextView textIfBegin = (TextView) prototypeView.findViewById(R.id.brick_if_begin_prototype_text_view);
		textIfBegin.setText(String.valueOf(BrickValues.IF_CONDITION));
		return prototypeView;
	}

	@Override
	public boolean isInitialized() {
		return ifElseBrick != null;
	}

	@Override
	public void initialize() {
		ifElseBrick = new IfLogicElseBrick(this);
		ifEndBrick = new IfLogicEndBrick(ifElseBrick, this);
		Log.w(TAG, "Creating if logic stuff");
	}

	@Override
	public List<NestingBrick> getAllNestingBrickParts(boolean sorted) {
		//TODO: handle sorting
		List<NestingBrick> nestingBrickList = new ArrayList<NestingBrick>();
		if (sorted) {
			nestingBrickList.add(this);
			nestingBrickList.add(ifElseBrick);
			nestingBrickList.add(ifEndBrick);
		} else {
			nestingBrickList.add(this);
			nestingBrickList.add(ifEndBrick);
		}

		return nestingBrickList;
	}

	@Override
	public boolean isDraggableOver(Brick brick) {
		return brick != ifElseBrick;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		SequenceAction ifAction = ExtendedActions.sequence();
		SequenceAction elseAction = ExtendedActions.sequence();
		Action action = ExtendedActions.ifLogic(sprite, getFormulaWithBrickField(BrickField.IF_CONDITION), ifAction,
				elseAction); //TODO finish!!!
		sequence.addAction(action);

		LinkedList<SequenceAction> returnActionList = new LinkedList<SequenceAction>();
		returnActionList.add(elseAction);
		returnActionList.add(ifAction);

		return returnActionList;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		//ifEndBrick and ifElseBrick will be set in the copyBrickForSprite method of IfLogicEndBrick
		IfLogicBeginBrick copyBrick = (IfLogicBeginBrick) clone(); //Using the clone method because of its flexibility if new fields are added
		copyBrick.ifElseBrick = null; //if the Formula gets a field sprite, a separate copy method will be needed
		copyBrick.ifEndBrick = null;
		this.copy = copyBrick;
		return copyBrick;
	}
}
