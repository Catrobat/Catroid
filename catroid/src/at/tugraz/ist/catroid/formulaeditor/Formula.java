/**
+ *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.formulaeditor;

import java.io.Serializable;

import android.view.View;
import android.widget.EditText;

public class Formula implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final int ROOT_ELEMENT = 0;
	private transient FormulaElement root;
	private String textRepresentation = "0";
	private transient Integer formulaTextFieldId = null;

	//	public Formula() {
	//		root = new FormulaElement(FormulaElement.ElementType.VALUE, "0", null);
	//		textRepresentation = "0";
	//	}
	public Formula() {

	}

	public Object readResolve() {
		CalcGrammarParser parser = CalcGrammarParser.getFormulaParser(textRepresentation);
		root = parser.parseFormula();

		return this;
	}

	public Formula(FormulaElement formEle) {
		root = formEle;
		textRepresentation = root.getEditTextRepresentation();
	}

	public Formula(String value) {
		root = new FormulaElement(FormulaElement.ElementType.VALUE, value, null);
		textRepresentation = root.getEditTextRepresentation();
	}

	//	public Formula(String value, int formulaTextFieldId) {
	//		root = new FormulaElement(FormulaElement.ELEMENT_VALUE, value, null);
	//		this.formulaTextFieldId = formulaTextFieldId;
	//	}

	public Double interpret() {

		//Log.i("info", root.getTreeString());
		return root.interpretRecursive();
	}

	public String getEditTextRepresentation() {
		//return root.getEditTextRepresentation();
		return textRepresentation;
	}

	public void setRoot(FormulaElement formula) {
		root = formula;
		textRepresentation = root.getEditTextRepresentation();

	}

	public void setTextFieldId(int id) {
		formulaTextFieldId = id;
	}

	public void refreshTextField(View view) {
		if (formulaTextFieldId != null && root != null) {
			EditText formulaTextField = (EditText) view.findViewById(formulaTextFieldId);
			formulaTextField.setText(textRepresentation);
		}

	}
}
