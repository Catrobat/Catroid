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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.utils.Utils;

public class GoNStepsBackBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	public GoNStepsBackBrick() {
		addAllowedBrickField(BrickField.STEPS, R.id.brick_go_back_edit_text);
	}

	public GoNStepsBackBrick(int steps) {
		this(new Formula(steps));
	}

	public GoNStepsBackBrick(Formula formula) {
		this();
		setFormulaWithBrickField(BrickField.STEPS, formula);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_go_back;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);

		TextView times = view.findViewById(R.id.brick_go_back_layers_text_view);

		if (getFormulaWithBrickField(BrickField.STEPS).isNumber()) {
			try {
				times.setText(view.getResources().getQuantityString(
						R.plurals.brick_go_back_layer_plural,
						Utils.convertDoubleToPluralInteger(getFormulaWithBrickField(BrickField.STEPS).interpretDouble(
								ProjectManager.getInstance().getCurrentSprite()))
				));
			} catch (InterpretationException interpretationException) {
				Log.d(getClass().getSimpleName(), "Couldn't interpret Formula.", interpretationException);
			}
		} else {
			times.setText(view.getResources().getQuantityString(R.plurals.brick_go_back_layer_plural,
					Utils.TRANSLATION_PLURAL_OTHER_INTEGER));
		}

		return view;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory()
				.createGoNStepsBackAction(sprite, getFormulaWithBrickField(BrickField.STEPS)));
	}
}
