/**
 *  Catroid: An on-device graphical programming language for Android devices
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

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;

public class FormulaRepresentation {

	private transient View view;
	String function = null;
	String value1 = null;
	String operator = null;
	String value2 = null;

	public FormulaRepresentation(String function, String value1, String operator, String value2) {
		this.function = function;
		this.value1 = value1;
		this.operator = operator;
		this.value2 = value2;

	}

	public FormulaRepresentation(String value1) {
		this.value1 = value1;
	}

	public View getView(Context context, OnClickListener listener) {
		if (value2 == null) {
			return getFormulaViewOneField(context, listener);
		} else {
			return getFormulaViewTwoFields(context, listener);
		}
	}

	private View getFormulaViewTwoFields(Context context, OnClickListener listener) {

		view = View.inflate(context, R.layout.formula_2_fields, null);

		EditText editOne = (EditText) view.findViewById(R.id.formula_edit_text_one);
		editOne.setText(value1);
		editOne.setOnClickListener(listener);

		EditText editTwo = (EditText) view.findViewById(R.id.formula_edit_text_two);
		editTwo.setText(value2);
		editTwo.setOnClickListener(listener);

		TextView tv = (TextView) view.findViewById(R.id.formula_operator);
		tv.setText(operator);

		return view;
	}

	private View getFormulaViewOneField(Context context, OnClickListener listener) {

		view = View.inflate(context, R.layout.formula_1_field, null);

		EditText editOne = (EditText) view.findViewById(R.id.formula_one_field_edit_text);
		editOne.setText(value1);
		editOne.setOnClickListener(listener);

		return view;
	}

	//	public void onClick(final View view) {
	//		final Context context = view.getContext();
	//
	//	}

}
