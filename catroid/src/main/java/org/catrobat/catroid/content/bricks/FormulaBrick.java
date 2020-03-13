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
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.CallSuper;
import androidx.annotation.VisibleForTesting;

public abstract class FormulaBrick extends BrickBaseType implements View.OnClickListener {

	@XStreamAlias("formulaList")
	ConcurrentFormulaHashMap formulaMap = new ConcurrentFormulaHashMap();

	public transient BiMap<BrickField, Integer> brickFieldToTextViewIdMap = HashBiMap.create(2);

	public Formula getFormulaWithBrickField(BrickField brickField) throws IllegalArgumentException {
		if (formulaMap.containsKey(brickField)) {
			return formulaMap.get(brickField);
		} else {
			throw new IllegalArgumentException("Incompatible Brick Field: " + this.getClass().getSimpleName()
					+ " does have BrickField." + brickField.toString());
		}
	}

	public void setFormulaWithBrickField(BrickField brickField, Formula formula) throws IllegalArgumentException {
		if (formulaMap.containsKey(brickField)) {
			formulaMap.replace(brickField, formula);
		} else {
			throw new IllegalArgumentException("Incompatible Brick Field: Cannot set BrickField."
					+ brickField.toString() + " fot " + this.getClass().getSimpleName());
		}
	}

	protected void addAllowedBrickField(BrickField brickField, int textViewResourceId) {
		formulaMap.putIfAbsent(brickField, new Formula(0));
		brickFieldToTextViewIdMap.put(brickField, textViewResourceId);
	}

	@Override
	@CallSuper
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
		for (Formula formula : formulaMap.values()) {
			formula.addRequiredResources(requiredResourcesSet);
		}
	}

	public void replaceFormulaBrickField(BrickField oldBrickField, BrickField newBrickField) {
		if (formulaMap.containsKey(oldBrickField)) {
			Formula brickFormula = formulaMap.get(oldBrickField);
			formulaMap.remove(oldBrickField);
			formulaMap.put(newBrickField, brickFormula);
		}
	}

	@Override
	public Brick clone() throws CloneNotSupportedException {
		FormulaBrick clone = (FormulaBrick) super.clone();
		clone.formulaMap = formulaMap.clone();
		return clone;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);
		for (BiMap.Entry<BrickField, Integer> entry : brickFieldToTextViewIdMap.entrySet()) {
			TextView brickFieldView = view.findViewById(entry.getValue());
			brickFieldView.setText(getFormulaWithBrickField(entry.getKey()).getTrimmedFormulaString(context));
		}
		return view;
	}

	public void setClickListeners() {
		for (BiMap.Entry<BrickField, Integer> entry : brickFieldToTextViewIdMap.entrySet()) {
			TextView brickFieldView = view.findViewById(entry.getValue());
			brickFieldView.setOnClickListener(this);
		}
	}

	public List<Formula> getFormulas() {
		return new ArrayList<>(formulaMap.values());
	}

	@VisibleForTesting
	public ConcurrentFormulaHashMap getFormulaMap() {
		return formulaMap;
	}

	public TextView getTextView(BrickField brickField) {
		return view.findViewById(brickFieldToTextViewIdMap.get(brickField));
	}

	public void highlightTextView(BrickField brickField) {
		TextView formulaTextField = view.findViewById(brickFieldToTextViewIdMap.get(brickField));

		formulaTextField.getBackground().mutate()
				.setColorFilter(view.getContext().getResources()
						.getColor(R.color.brick_field_highlight), PorterDuff.Mode.SRC_ATOP);
	}

	@Override
	public void onClick(View view) {
		showFormulaEditorToEditFormula(view);
	}

	public void showFormulaEditorToEditFormula(View view) {
		if (brickFieldToTextViewIdMap.inverse().containsKey(view.getId())) {
			FormulaEditorFragment.showFragment(view.getContext(), this, getBrickFieldFromTextViewId(view.getId()));
		} else {
			FormulaEditorFragment.showFragment(view.getContext(), this, getDefaultBrickField());
		}
	}

	public BrickField getDefaultBrickField() {
		return formulaMap.keys().nextElement();
	}

	boolean isBrickFieldANumber(BrickField brickField) {
		return getFormulaWithBrickField(brickField).isNumber();
	}

	public View getCustomView(Context context) {
		throw new IllegalStateException("There is no custom view for the " + getClass().getSimpleName() + ".");
	}

	public Brick.BrickField getBrickFieldFromTextViewId(int textViewId) {
		return brickFieldToTextViewIdMap.inverse().get(textViewId);
	}

	protected void setSecondsLabel(View view, BrickField brickField) {
		TextView textView = view.findViewById(R.id.brick_seconds_label);
		Context context = textView.getContext();

		if (getFormulaWithBrickField(brickField).isNumber()) {
			try {
				Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
				Double formulaValue = formulaMap.get(brickField).interpretDouble(sprite);
				textView.setText(context.getResources().getQuantityString(R.plurals.second_plural,
						Utils.convertDoubleToPluralInteger(formulaValue)));
				return;
			} catch (InterpretationException e) {
				Log.e(getClass().getSimpleName(),
						"Interpretation of formula failed, "
								+ "fallback to quantity \"other\" for \"second(s)\" label.", e);
			}
		}
		textView.setText(context.getResources()
				.getQuantityString(R.plurals.second_plural, Utils.TRANSLATION_PLURAL_OTHER_INTEGER));
	}
}
