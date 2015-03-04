/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.utils.Utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RepeatBrick extends FormulaBrick implements LoopBeginBrick, OnClickListener {
	private static final long serialVersionUID = 1L;

//	private transient View prototypeView;

	protected transient LoopEndBrick loopEndBrick;
	private transient long beginLoopTime;

	private transient LoopBeginBrick copy;

	public RepeatBrick() {
		addAllowedBrickField(BrickField.TIMES_TO_REPEAT);
	}

	public RepeatBrick(int timesToRepeatValue) {
		initializeBrickFields(new Formula(timesToRepeatValue));
	}

	public RepeatBrick(Formula timesToRepeat) {
		initializeBrickFields(timesToRepeat);
	}

	@Override
	public int getRequiredResources() {
		return getFormulaWithBrickField(BrickField.TIMES_TO_REPEAT).getRequiredResources();
	}

	private void initializeBrickFields(Formula timesToRepeat) {
		addAllowedBrickField(BrickField.TIMES_TO_REPEAT);
		setFormulaWithBrickField(BrickField.TIMES_TO_REPEAT, timesToRepeat);
	}

	@Override
	public Brick clone() {
		return new RepeatBrick(getFormulaWithBrickField(BrickField.TIMES_TO_REPEAT).clone());
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
//OK
		view = View.inflate(context, R.layout.brick_repeat, null);

		TextView edit = (TextView) view.findViewById(R.id.brick_repeat_edit_text);
		getFormulaWithBrickField(BrickField.TIMES_TO_REPEAT).setTextFieldId(R.id.brick_repeat_edit_text);
		getFormulaWithBrickField(BrickField.TIMES_TO_REPEAT).refreshTextField(view);

		TextView times = (TextView) view.findViewById(R.id.brick_repeat_time_text_view);

		if (getFormulaWithBrickField(BrickField.TIMES_TO_REPEAT).isSingleNumberFormula()) {
			try {
				times.setText(view.getResources().getQuantityString(
						R.plurals.time_plural,
						Utils.convertDoubleToPluralInteger(getFormulaWithBrickField(BrickField.TIMES_TO_REPEAT)
								.interpretDouble(ProjectManager.getInstance().getCurrentSprite()))
				));
			} catch (InterpretationException interpretationException) {
				Log.d(getClass().getSimpleName(), "Couldn't interpret Formula", interpretationException);
			}
		} else {

			// Random Number to get into the "other" keyword for values like 0.99 or 2.001 seconds or degrees
			// in hopefully all possible languages
			times.setText(view.getResources().getQuantityString(R.plurals.time_plural,
					Utils.TRANSLATION_PLURAL_OTHER_INTEGER));
		}

		edit.setOnClickListener(this);
		return view;
	}


	@Override
	public void onClick(View view) {
		if (!clickAllowed()) {
			return;
		}
		FormulaEditorFragment.showFragment(view, this, getFormulaWithBrickField(BrickField.TIMES_TO_REPEAT));
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		SequenceAction repeatSequence = ExtendedActions.sequence();
		Action action = ExtendedActions.repeat(sprite, getFormulaWithBrickField(BrickField.TIMES_TO_REPEAT),
				repeatSequence);
		sequence.addAction(action);
		LinkedList<SequenceAction> returnActionList = new LinkedList<SequenceAction>();
		returnActionList.add(repeatSequence);
		return returnActionList;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		//loopEndBrick will be set in the LoopEndBrick's copyBrickForSprite method
		RepeatBrick copyBrick = (RepeatBrick) clone();
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
}
