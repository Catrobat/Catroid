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
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.utils.Utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RepeatBrick extends FormulaBrick implements LoopBeginBrick {

	private static final long serialVersionUID = 1L;

	private transient LoopEndBrick loopEndBrick;

	public RepeatBrick() {
		addAllowedBrickField(BrickField.TIMES_TO_REPEAT, R.id.brick_repeat_edit_text);
	}

	public RepeatBrick(int timesToRepeat) {
		this(new Formula(timesToRepeat));
	}

	public RepeatBrick(Formula timesToRepeat) {
		this();
		setFormulaWithBrickField(BrickField.TIMES_TO_REPEAT, timesToRepeat);
	}

	@Override
	public BrickBaseType clone() throws CloneNotSupportedException {
		RepeatBrick clone = (RepeatBrick) super.clone();
		clone.loopEndBrick = null;
		return clone;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_repeat;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);

		TextView label = view.findViewById(R.id.brick_repeat_time_text_view);
		if (getFormulaWithBrickField(BrickField.TIMES_TO_REPEAT).isSingleNumberFormula()) {
			try {
				label.setText(view.getResources().getQuantityString(
						R.plurals.time_plural,
						Utils.convertDoubleToPluralInteger(getFormulaWithBrickField(BrickField.TIMES_TO_REPEAT)
								.interpretDouble(ProjectManager.getInstance().getCurrentSprite()))
				));
			} catch (InterpretationException interpretationException) {
				Log.d(getClass().getSimpleName(), "Couldn't interpret Formula", interpretationException);
			}
		} else {
			label.setText(view.getResources().getQuantityString(R.plurals.time_plural,
					Utils.TRANSLATION_PLURAL_OTHER_INTEGER));
		}

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = super.getPrototypeView(context);
		TextView label = prototypeView.findViewById(R.id.brick_repeat_time_text_view);
		label.setText(context.getResources().getQuantityString(R.plurals.time_plural,
				Utils.convertDoubleToPluralInteger(BrickValues.REPEAT)));
		return prototypeView;
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		ScriptSequenceAction repeatSequence = (ScriptSequenceAction) ActionFactory.eventSequence(sequence.getScript());
		Action action = sprite.getActionFactory()
				.createRepeatAction(sprite, getFormulaWithBrickField(BrickField.TIMES_TO_REPEAT), repeatSequence);
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
		List<NestingBrick> nestingBrickList = new ArrayList<>();
		nestingBrickList.add(this);
		nestingBrickList.add(loopEndBrick);
		return nestingBrickList;
	}
}
