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

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public class MoveNStepsBrick extends FormulaBrick implements OnClickListener {

	private static final long serialVersionUID = 1L;

	public MoveNStepsBrick() {
		addAllowedBrickField(BrickField.STEPS);
	}

	public MoveNStepsBrick(double stepsValue) {
		initializeBrickFields(new Formula(stepsValue));
	}

	public MoveNStepsBrick(Formula steps) {
		initializeBrickFields(steps);
	}

	private void initializeBrickFields(Formula steps) {
		addAllowedBrickField(BrickField.STEPS);
		setFormulaWithBrickField(BrickField.STEPS, steps);
	}

	@Override
	public int getRequiredResources() {
		return getFormulaWithBrickField(BrickField.STEPS).getRequiredResources();
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {

		view = View.inflate(context, R.layout.brick_move_n_steps, null);

		TextView edit = (TextView) view.findViewById(R.id.brick_move_n_steps_edit_text);

		getFormulaWithBrickField(BrickField.STEPS).setTextFieldId(R.id.brick_move_n_steps_edit_text);
		getFormulaWithBrickField(BrickField.STEPS).refreshTextField(view);

		TextView times = (TextView) view.findViewById(R.id.brick_move_n_steps_step_text_view);

		if (getFormulaWithBrickField(BrickField.STEPS).isSingleNumberFormula()) {
            try{
				times.setText(view.getResources().getQuantityString(
						R.plurals.brick_move_n_step_plural,
						Utils.convertDoubleToPluralInteger(getFormulaWithBrickField(BrickField.STEPS).interpretDouble(
								ProjectManager.getInstance().getCurrentSprite()))
				));
            }catch(InterpretationException interpretationException){
                Log.d(getClass().getSimpleName(), "Couldn't interpret Formula.", interpretationException);
            }
		} else {
			// Random Number to get into the "other" keyword for values like 0.99 or 2.001 seconds or degrees
			// in hopefully all possible languages
			times.setText(view.getResources().getQuantityString(R.plurals.brick_move_n_step_plural,
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
		FormulaEditorFragment.showFragment(view, this, getFormulaWithBrickField(BrickField.STEPS));
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.moveNSteps(sprite, getFormulaWithBrickField(BrickField.STEPS)));
		return null;
	}
}
