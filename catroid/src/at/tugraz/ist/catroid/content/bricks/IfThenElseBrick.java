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
package at.tugraz.ist.catroid.content.bricks;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.formulaeditor.Formula;
import at.tugraz.ist.catroid.ui.dialogs.FormulaEditorDialog;

public class IfThenElseBrick implements Brick, OnClickListener, OnItemSelectedListener {
	private static final long serialVersionUID = 1L;

	public static enum LogicOperator {
		MORE_THAN(">"), LESS_THAN("<"), EQUAL_TO("=");

		private String logicOperator;

		private LogicOperator(String operator) {
			logicOperator = operator;
		}

		public String getLogicOperator() {
			return logicOperator;
		}
	}

	private Formula conditionToCheckFormula1;
	private Formula conditionToCheckFormula2;
	private String operator;
	protected Sprite sprite;
	protected ElseBrick elseBrick;
	protected LoopEndBrick loopEndBrick;
	private int position;
	private transient Brick instance = null;
	private transient FormulaEditorDialog formulaEditor;
	public transient boolean editorActive = false;
	private transient LogicOperator logicOperator;

	protected Object readResolve() {
		// initialize direction if parsing from xml with XStream
		for (LogicOperator logicOperator : LogicOperator.values()) {
			if (logicOperator.getLogicOperator().equalsIgnoreCase(operator)) {
				this.logicOperator = logicOperator;
				break;
			}
		}
		return this;
	}

	public IfThenElseBrick(Sprite sprite, int conditionToCheck1, LogicOperator logicOperator, int conditionToCheck2) {
		this.sprite = sprite;
		this.logicOperator = logicOperator;
		conditionToCheckFormula1 = new Formula(Integer.toString(conditionToCheck1));
		conditionToCheckFormula2 = new Formula(Integer.toString(conditionToCheck2));
	}

	public IfThenElseBrick(Sprite sprite, Formula conditionToCheck1, LogicOperator logicOperator,
			Formula conditionToCheck2) {
		this.sprite = sprite;
		this.logicOperator = logicOperator;
		conditionToCheckFormula1 = conditionToCheck1;
		conditionToCheckFormula2 = conditionToCheck2;
	}

	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	public void execute() {
		Script script = elseBrick.getScript();
		if (!checkCondition()) {
			script.setExecutingBrickIndex(script.getBrickList().indexOf(elseBrick));
			System.err.println("Not Called!!!! ");

			while (!sprite.isFinished) {

				if (this.checkCondition()) {
					script.setExecutingBrickIndex(script.getBrickList().indexOf(this));
					Log.i(this.getClass().toString(), "First time false,then true!!!!");
					return;
				}
			}
		}
	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public ElseBrick getElseBrick() {
		return this.elseBrick;
	}

	public void setElseBrick(ElseBrick elseBrick) {
		this.elseBrick = elseBrick;
	}

	public boolean checkCondition() {
		double conditionToCheck1 = conditionToCheckFormula1.interpret().doubleValue();
		System.out.println("Value :" + conditionToCheck1);
		double conditionToCheck2 = conditionToCheckFormula2.interpret().doubleValue();
		boolean conditionPass = false;
		switch (operator.charAt(0)) {
			case '<':
				if (conditionToCheck1 < conditionToCheck2) {
					conditionPass = true;
				}
				break;
			case '>':
				if (conditionToCheck1 > conditionToCheck2) {
					conditionPass = true;
				}
				break;
			case '=':
				if (conditionToCheck1 == conditionToCheck2) {
					conditionPass = true;
				}
				break;
		}
		return conditionPass;
	}

	@Override
	public Brick clone() {
		return new IfThenElseBrick(getSprite(), conditionToCheckFormula1, logicOperator, conditionToCheckFormula2);
	}

	public View getView(Context context, int brickId, BaseAdapter adapter) {

		if (instance == null) {
			instance = this;
		}

		View view = View.inflate(context, R.layout.brick_if, null);

		TextView text1 = (TextView) view.findViewById(R.id.brick_if_text_view1);
		EditText edit1 = (EditText) view.findViewById(R.id.brick_if_edit_text1);

		conditionToCheckFormula1.setTextFieldId(R.id.brick_if_edit_text1);
		conditionToCheckFormula1.refreshTextField(view);

		text1.setVisibility(View.GONE);
		edit1.setVisibility(View.VISIBLE);

		edit1.setOnClickListener(this);

		TextView text2 = (TextView) view.findViewById(R.id.brick_if_text_view2);
		EditText edit2 = (EditText) view.findViewById(R.id.brick_if_edit_text2);

		conditionToCheckFormula2.setTextFieldId(R.id.brick_if_edit_text2);
		conditionToCheckFormula2.refreshTextField(view);

		text2.setVisibility(View.GONE);
		edit2.setVisibility(View.VISIBLE);

		edit2.setOnClickListener(this);

		ArrayAdapter<CharSequence> arrayAdapter = new ArrayAdapter<CharSequence>(context,
				android.R.layout.simple_spinner_item);
		for (LogicOperator logicOperator : LogicOperator.values()) {
			arrayAdapter.add(logicOperator.getLogicOperator());
		}
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner spinner = (Spinner) view.findViewById(R.id.logic_operator_spinner);
		spinner.setAdapter(arrayAdapter);

		spinner.setClickable(true);
		spinner.setFocusable(true);

		spinner.setOnItemSelectedListener(this);
		spinner.setSelection(position);

		return view;
	}

	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_if, null);
	}

	public void onClick(View view) {
		final Context context = view.getContext();

		if (!editorActive) {
			editorActive = true;
			formulaEditor = new FormulaEditorDialog(context, instance);
			formulaEditor.setOnDismissListener(new OnDismissListener() {
				public void onDismiss(DialogInterface editor) {

					//size = formulaEditor.getReturnValue();
					formulaEditor.dismiss();
					editorActive = false;
				}
			});
			formulaEditor.show();
		}

		switch (view.getId()) {
			case R.id.brick_if_edit_text1:
				formulaEditor.setInputFocusAndFormula(conditionToCheckFormula1);
				break;

			case R.id.brick_if_edit_text2:
				formulaEditor.setInputFocusAndFormula(conditionToCheckFormula2);
				break;
		}
	}

	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		logicOperator = LogicOperator.values()[position];
		this.position = position;
		operator = logicOperator.getLogicOperator();
	}

	public void onNothingSelected(AdapterView<?> arg0) {
	}

}
