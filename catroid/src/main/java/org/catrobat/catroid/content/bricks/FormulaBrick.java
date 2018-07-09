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
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.utils.FormatNumberUtil;
import org.catrobat.catroid.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public abstract class FormulaBrick extends BrickBaseType implements View.OnClickListener {

	protected transient BrickAdapter adapter;
	ConcurrentFormulaHashMap formulaMap = new ConcurrentFormulaHashMap();

	public Formula getFormulaWithBrickField(BrickField brickField) throws IllegalArgumentException {
		if (formulaMap.containsKey(brickField)) {
			return formulaMap.get(brickField);
		} else {
			throw new IllegalArgumentException("Incompatible Brick Field : " + brickField.toString());
		}
	}

	public void setFormulaWithBrickField(BrickField brickField, Formula formula) throws IllegalArgumentException {
		if (formulaMap.containsKey(brickField)) {
			formulaMap.replace(brickField, formula);
		} else {
			throw new IllegalArgumentException("Incompatible Brick Field : " + brickField.toString());
		}
	}

	protected void addAllowedBrickField(BrickField brickField) {
		formulaMap.putIfAbsent(brickField, new Formula(0));
	}

	protected void replaceFormulaBrickField(BrickField oldBrickField, BrickField newBrickField) {
		if (formulaMap.containsKey(oldBrickField)) {
			Formula brickFormula = formulaMap.get(oldBrickField);
			formulaMap.remove(oldBrickField);
			formulaMap.put(newBrickField, brickFormula);
		}
	}

	@Override
	public Brick clone() throws CloneNotSupportedException {
		FormulaBrick clonedBrick = (FormulaBrick) super.clone();
		clonedBrick.formulaMap = formulaMap.clone();
		return clonedBrick;
	}

	public List<Formula> getFormulas() {
		List<Formula> formulas = new ArrayList<>();

		for (BrickField brickField : formulaMap.keySet()) {
			formulas.add(formulaMap.get(brickField));
		}
		return formulas;
	}

	@Override
	public void onClick(View view) {
		if (adapter == null) {
			return;
		}
		if (adapter.getActionMode() != BrickAdapter.ActionModeEnum.NO_ACTION) {
			return;
		}
		if (adapter.isDragging) {
			return;
		}
		showFormulaEditorToEditFormula(view);
	}

	@Override
	public View getView(Context context, BrickAdapter adapter) {
		super.getView(context, adapter);
		this.adapter = adapter;
		return view;
	}

	public View getCustomView(Context context, int brickId, BaseAdapter brickAdapter) {
		return null;
	}

	public abstract void showFormulaEditorToEditFormula(View view);

	public void setSecondText(View view, int textViewId, int editTextDurationId, BrickField brickField) {
		TextView editDuration = (TextView) view.findViewById(editTextDurationId);
		getFormulaWithBrickField(brickField)
				.setTextFieldId(editTextDurationId);
		getFormulaWithBrickField(brickField).refreshTextField(view);

		TextView times = (TextView) view.findViewById(textViewId);

		if (getFormulaWithBrickField(brickField).isSingleNumberFormula()) {
			try {
				times.setText(view.getResources().getQuantityString(
						R.plurals.second_plural,
						Utils.convertDoubleToPluralInteger(getFormulaWithBrickField(brickField)
								.interpretDouble(ProjectManager.getInstance().getCurrentSprite()))
				));
			} catch (InterpretationException interpretationException) {
				Log.d(getClass().getSimpleName(), "Couldn't interpret Formula.", interpretationException);
			}
		} else {
			times.setText(view.getResources().getQuantityString(R.plurals.second_plural,
					Utils.TRANSLATION_PLURAL_OTHER_INTEGER));
		}

		editDuration.setOnClickListener(this);
	}

	public void setSecondText(Context context, View prototypeView, int textViewId) {
		TextView second = (TextView) prototypeView.findViewById(textViewId);
		second.setText(context.getResources().getQuantityString(R.plurals.second_plural,
				Utils.convertDoubleToPluralInteger(BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS / 1000)));
	}

	protected <T> String formatNumberForPrototypeView(T value) {

		String number = String.valueOf(value);

		return FormatNumberUtil.cutTrailingZeros(number);
	}
}
