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
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public class GlideToBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	public GlideToBrick() {
		addAllowedBrickField(BrickField.X_DESTINATION);
		addAllowedBrickField(BrickField.Y_DESTINATION);
		addAllowedBrickField(BrickField.DURATION_IN_SECONDS);
	}

	public GlideToBrick(int xDestinationValue, int yDestinationValue, int durationInMilliSecondsValue) {
		initializeBrickFields(new Formula(xDestinationValue), new Formula(yDestinationValue), new Formula(
				durationInMilliSecondsValue / 1000.0));
	}

	public GlideToBrick(Formula xDestination, Formula yDestination, Formula durationInSeconds) {
		initializeBrickFields(xDestination, yDestination, durationInSeconds);
	}

	private void initializeBrickFields(Formula xDestination, Formula yDestination, Formula durationInSeconds) {
		addAllowedBrickField(BrickField.X_DESTINATION);
		addAllowedBrickField(BrickField.Y_DESTINATION);
		addAllowedBrickField(BrickField.DURATION_IN_SECONDS);
		setFormulaWithBrickField(BrickField.X_DESTINATION, xDestination);
		setFormulaWithBrickField(BrickField.Y_DESTINATION, yDestination);
		setFormulaWithBrickField(BrickField.DURATION_IN_SECONDS, durationInSeconds);
	}

	@Override
	public int getRequiredResources() {
		return getFormulaWithBrickField(BrickField.X_DESTINATION).getRequiredResources() | getFormulaWithBrickField(BrickField.Y_DESTINATION).getRequiredResources()
				| getFormulaWithBrickField(BrickField.DURATION_IN_SECONDS).getRequiredResources();
	}

	@Override
	protected int getLayoutRes() {
		return R.layout.brick_glide_to;
	}

	@Override
	public View onCreateView(Context context) {

		super.onCreateView(context);

		TextView editX = (TextView) view.findViewById(R.id.brick_glide_to_edit_text_x);
		getFormulaWithBrickField(BrickField.X_DESTINATION).setTextFieldId(R.id.brick_glide_to_edit_text_x);
		getFormulaWithBrickField(BrickField.X_DESTINATION).refreshTextField(view);
		editX.setOnClickListener(this);

		TextView editY = (TextView) view.findViewById(R.id.brick_glide_to_edit_text_y);
		getFormulaWithBrickField(BrickField.Y_DESTINATION).setTextFieldId(R.id.brick_glide_to_edit_text_y);
		getFormulaWithBrickField(BrickField.Y_DESTINATION).refreshTextField(view);
		editY.setOnClickListener(this);

		TextView editDuration = (TextView) view.findViewById(R.id.brick_glide_to_edit_text_duration);
		getFormulaWithBrickField(BrickField.DURATION_IN_SECONDS).setTextFieldId(R.id.brick_glide_to_edit_text_duration);
		getFormulaWithBrickField(BrickField.DURATION_IN_SECONDS).refreshTextField(view);

		TextView times = (TextView) view.findViewById(R.id.brick_glide_to_seconds_text_view);

		if (getFormulaWithBrickField(BrickField.DURATION_IN_SECONDS).isSingleNumberFormula()) {
			try {
				times.setText(view.getResources().getQuantityString(
						R.plurals.second_plural,
						Utils.convertDoubleToPluralInteger(getFormulaWithBrickField(BrickField.DURATION_IN_SECONDS)
								.interpretDouble(ProjectManager.getInstance().getCurrentSprite()))));
			} catch (InterpretationException interpretationException) {
				Log.d(getClass().getSimpleName(), "Couldn't interpret Formula.", interpretationException);
			}
		} else {

			// Random Number to get into the "other" keyword for values like 0.99 or 2.001 seconds or degrees
			// in hopefully all possible languages
			times.setText(view.getResources().getQuantityString(R.plurals.second_plural,
					Utils.TRANSLATION_PLURAL_OTHER_INTEGER));
		}

		editDuration.setOnClickListener(this);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = super.getPrototypeView(context);
		TextView textX = (TextView) prototypeView.findViewById(R.id.brick_glide_to_edit_text_x);
		TextView textY = (TextView) prototypeView.findViewById(R.id.brick_glide_to_edit_text_y);
		TextView textDuration = (TextView) prototypeView.findViewById(R.id.brick_glide_to_edit_text_duration);
		TextView times = (TextView) prototypeView.findViewById(R.id.brick_glide_to_seconds_text_view);
		textX.setText(formatNumberForPrototypeView(BrickValues.X_POSITION));
		textY.setText(formatNumberForPrototypeView(BrickValues.Y_POSITION));
		textDuration.setText(formatNumberForPrototypeView(BrickValues.DURATION));
		times.setText(context.getResources().getQuantityString(R.plurals.second_plural,
				Utils.convertDoubleToPluralInteger(BrickValues.DURATION)));
		return prototypeView;
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createGlideToAction(sprite,
				getFormulaWithBrickField(BrickField.X_DESTINATION),
				getFormulaWithBrickField(BrickField.Y_DESTINATION),
				getFormulaWithBrickField(BrickField.DURATION_IN_SECONDS)));
		return null;
	}

	public void showFormulaEditorToEditFormula(View view) {
		switch (view.getId()) {
			case R.id.brick_glide_to_edit_text_x:
				FormulaEditorFragment.showFragment(view, this, BrickField.X_DESTINATION);
				break;

			case R.id.brick_glide_to_edit_text_y:
				FormulaEditorFragment.showFragment(view, this, BrickField.Y_DESTINATION);
				break;

			case R.id.brick_glide_to_edit_text_duration:
			default:
				FormulaEditorFragment.showFragment(view, this, BrickField.DURATION_IN_SECONDS);
				break;
		}
	}
}
