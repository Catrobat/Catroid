/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.formulaeditor;

import java.io.Serializable;

import org.catrobat.catroid.R;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.view.View;
import android.widget.EditText;

public class Formula implements Serializable {

	private static final long serialVersionUID = 1L;
	private FormulaElement formulaTree;
	private transient Integer formulaTextFieldId = null;
	private transient Drawable originalEditTextDrawable = null;
	private transient InternFormula internFormula = null;

	//	public Formula() {
	//		root = new FormulaElement(FormulaElement.ElementType.VALUE, "0", null);
	//		textRepresentation = "0";
	//	}
	public Formula() {

	}

	public Object readResolve() {

		if (formulaTree == null) {
			formulaTree = new FormulaElement(ElementType.NUMBER, "0 ", null);
		}

		internFormula = new InternFormula(formulaTree.getInternTokenList());

		return this;
	}

	public Formula(FormulaElement formEle) {
		formulaTree = formEle;

		internFormula = new InternFormula(formulaTree.getInternTokenList());
	}

	public Formula(String value) {

		try {
			Double.parseDouble(value);
			formulaTree = new FormulaElement(ElementType.NUMBER, value, null);
		} catch (NumberFormatException nfe) {
			formulaTree = new FormulaElement(ElementType.NUMBER, "0", null);
		}
		internFormula = new InternFormula(formulaTree.getInternTokenList());

	}

	//	public Formula(String value, int formulaTextFieldId) {
	//		root = new FormulaElement(FormulaElement.ELEMENT_VALUE, value, null);
	//		this.formulaTextFieldId = formulaTextFieldId;
	//	}

	//	public Double interpret() {
	//
	//		//Log.i("info", root.getTreeString());
	//
	//		return root.interpretRecursive();
	//	}

	public int interpretInteger() {
		return interpretInteger(1, 0);
	}

	public int interpretInteger(int minValue, int maxValue) {
		Object interpretedValue = formulaTree.interpretRecursive();

		int interpretedIntValue = 0;

		if (interpretedValue.getClass().equals(Double.class)) {
			interpretedIntValue = ((Double) interpretedValue).intValue();
		} else if (interpretedValue.getClass().equals(Integer.class)) {
			interpretedIntValue = ((Integer) interpretedValue).intValue();
		}

		if (minValue <= maxValue) {

			interpretedIntValue = Math.min(maxValue, interpretedIntValue);
			interpretedIntValue = Math.max(minValue, interpretedIntValue);
		}

		return interpretedIntValue;

	}

	public float interpretFloat() {
		return interpretFloat(1f, 0f);
	}

	public float interpretFloat(float minValue, float maxValue) {
		Object interpretedValue = formulaTree.interpretRecursive();

		float interpretedFloatValue = 0;

		if (interpretedValue.getClass().equals(Double.class)) {
			interpretedFloatValue = ((Double) interpretedValue).floatValue();
		} else if (interpretedValue.getClass().equals(Integer.class)) {
			interpretedFloatValue = ((Integer) interpretedValue).floatValue();
		}

		if (minValue <= maxValue) {

			interpretedFloatValue = Math.min(maxValue, interpretedFloatValue);
			interpretedFloatValue = Math.max(minValue, interpretedFloatValue);
		}

		return interpretedFloatValue;
	}

	@Override
	public String toString() {
		//return root.getEditTextRepresentation();
		return "DEPRICATED - XML UPDATE"; //TODO remove when xml update is finished
	}

	public void setRoot(FormulaElement formula) {
		formulaTree = formula;
		internFormula = new InternFormula(formula.getInternTokenList());

	}

	public void setTextFieldId(int id) {
		formulaTextFieldId = id;
	}

	public void refreshTextField(View view) {
		if (formulaTextFieldId != null && formulaTree != null && view != null) {
			EditText formulaTextField = (EditText) view.findViewById(formulaTextFieldId);
			if (formulaTextField == null) {
				return;
			}
			internFormula.generateExternFormulaStringAndInternExternMapping(view.getContext());//TODO refactor - currently too slow

			formulaTextField.setText(internFormula.getExternFormulaString());
			//			if (textRepresentation.length() > 5) {
			//				formulaTextField.setText(textRepresentation.substring(0, 5) + "...");
			//			} else {
			//				formulaTextField.setText(textRepresentation);
			//			}
		}

	}

	public void refreshTextField(View view, String formulaString, int position) {
		if (formulaTextFieldId != null && formulaTree != null && view != null) {
			EditText formulaTextField = (EditText) view.findViewById(formulaTextFieldId);
			if (formulaTextField == null) {
				return;
			}
			formulaTextField.setText(formulaString);
			Layout formulaTextFieldLayout = formulaTextField.getLayout();
			if (position < 0 || formulaTextFieldLayout == null) {
				return;
			}
			int char_count = formulaTextFieldLayout.getLineVisibleEnd(0);
			if (formulaString.length() > char_count && char_count > 0) {
				int start = position - (char_count / 2);
				int end = position + (char_count / 2) + 1;
				if (end > formulaString.length() - 1) {
					end = formulaString.length() - 1;
					start = end - char_count;
				}
				if (start < 0) {
					start = 0;
					end = char_count;
				}
				formulaTextField.setText(formulaString.substring(start, end));
			}
		}
	}

	public void removeTextFieldHighlighting(View brickView, int orientation) {
		if (orientation == Configuration.ORIENTATION_LANDSCAPE || originalEditTextDrawable == null) {
			return;
		}

		EditText formulaTextField = (EditText) brickView.findViewById(formulaTextFieldId);

		int width = formulaTextField.getWidth();
		formulaTextField.setBackgroundDrawable(originalEditTextDrawable);
		formulaTextField.setWidth(width);
		originalEditTextDrawable = null;
	}

	public void highlightTextField(View brickView, int orientation) {
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			return;
		}
		Drawable highlightBackground = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			highlightBackground = brickView.getResources().getDrawable(R.drawable.textfield_pressed_android4);
		} else {
			highlightBackground = brickView.getResources().getDrawable(R.drawable.textfield_pressed);
		}

		EditText formulaTextField = (EditText) brickView.findViewById(formulaTextFieldId);

		if (originalEditTextDrawable == null) {
			originalEditTextDrawable = formulaTextField.getBackground();
		}
		int width = formulaTextField.getWidth();
		width = Math.max(width, 130);
		formulaTextField.setBackgroundDrawable(highlightBackground);
		formulaTextField.setWidth(width);
	}

	public void prepareToRemove() {
		originalEditTextDrawable = null;
		formulaTextFieldId = null;
	}

	public InternFormulaState getInternFormulaState() {
		return internFormula.getInternFormulaState();
	}

	public boolean containsElement(FormulaElement.ElementType elementType) {
		if (formulaTree.containsElement(elementType)) {
			return true;
		}

		return false;

	}
}
