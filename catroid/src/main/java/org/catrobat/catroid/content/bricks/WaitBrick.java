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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.utils.IconsUtil;
import org.catrobat.catroid.utils.TextSizeUtil;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public class WaitBrick extends FormulaBrick {
	private static final long serialVersionUID = 1L;

	private transient View prototypeView;

	public WaitBrick() {
		addAllowedBrickField(BrickField.TIME_TO_WAIT_IN_SECONDS);
	}

	public WaitBrick(int timeToWaitInMillisecondsValue) {
		initializeBrickFields(new Formula(timeToWaitInMillisecondsValue / 1000.0));
	}

	public WaitBrick(Formula timeToWaitInSecondsFormula) {
		initializeBrickFields(timeToWaitInSecondsFormula);
	}

	private void initializeBrickFields(Formula timeToWaitInSeconds) {
		addAllowedBrickField(BrickField.TIME_TO_WAIT_IN_SECONDS);
		setFormulaWithBrickField(BrickField.TIME_TO_WAIT_IN_SECONDS, timeToWaitInSeconds);
	}

	@Override
	public int getRequiredResources() {
		return getFormulaWithBrickField(BrickField.TIME_TO_WAIT_IN_SECONDS).getRequiredResources();
	}

	public Formula getTimeToWait() {
		return getFormulaWithBrickField(BrickField.TIME_TO_WAIT_IN_SECONDS);
	}

	public void setTimeToWait(Formula timeToWaitInSeconds) {
		setFormulaWithBrickField(BrickField.TIME_TO_WAIT_IN_SECONDS, timeToWaitInSeconds);
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_wait, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		IconsUtil.addIcon(context, (TextView) view.findViewById(R.id.brick_wait_label),
				context.getString(R.string.category_control));

		setCheckboxView(R.id.brick_wait_checkbox);
		TextView edit = (TextView) view.findViewById(R.id.brick_wait_edit_text);
		getFormulaWithBrickField(BrickField.TIME_TO_WAIT_IN_SECONDS).setTextFieldId(R.id.brick_wait_edit_text);
		getFormulaWithBrickField(BrickField.TIME_TO_WAIT_IN_SECONDS).refreshTextField(view);

		TextView times = (TextView) view.findViewById(R.id.brick_wait_second_text_view);

		if (getFormulaWithBrickField(BrickField.TIME_TO_WAIT_IN_SECONDS).isSingleNumberFormula()) {
			try {
				times.setText(view.getResources().getQuantityString(
						R.plurals.second_plural,
						Utils.convertDoubleToPluralInteger(getFormulaWithBrickField(BrickField.TIME_TO_WAIT_IN_SECONDS)
								.interpretDouble(ProjectManager.getInstance().getCurrentSprite()))
				));
			} catch (InterpretationException interpretationException) {
				Log.d(getClass().getSimpleName(), "Couldn't interpret Formula.", interpretationException);
			}
		} else {

			// Random Number to get into the "other" keyword for values like 0.99 or 2.001 seconds or degrees
			// in hopefully all possible languages
			times.setText(view.getResources().getQuantityString(R.plurals.second_plural,
					Utils.TRANSLATION_PLURAL_OTHER_INTEGER));
		}

		edit.setOnClickListener(this);

		TextSizeUtil.enlargeViewGroup((ViewGroup) view);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_wait, null);
		TextView textWait = (TextView) prototypeView.findViewById(R.id.brick_wait_edit_text);
		textWait.setText(String.valueOf(BrickValues.WAIT / 1000));
		TextView times = (TextView) prototypeView.findViewById(R.id.brick_wait_second_text_view);
		times.setText(context.getResources().getQuantityString(R.plurals.second_plural,
				Utils.convertDoubleToPluralInteger(BrickValues.WAIT / 1000)));
		return prototypeView;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createWaitAction(sprite,
				getFormulaWithBrickField(BrickField.TIME_TO_WAIT_IN_SECONDS)));
		return null;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		FormulaEditorFragment.showFragment(view, this, BrickField.TIME_TO_WAIT_IN_SECONDS);
	}

	@Override
	public void updateReferenceAfterMerge(Scene into, Scene from) {
	}
}
