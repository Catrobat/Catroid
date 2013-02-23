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
package org.catrobat.catroid.ui.dialogs;

import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;

import android.app.AlertDialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.widget.TextView;
import org.catrobat.catroid.R;

public class FormulaEditorComputeDialog extends AlertDialog implements SensorEventListener {

	private Formula formulaToCompute = null;

	private Context context;

	private TextView computeTextView;

	public FormulaEditorComputeDialog(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_formulaeditor_compute);

		setCanceledOnTouchOutside(true);
		computeTextView = (TextView) findViewById(R.id.formula_editor_compute_dialog_textview);
		computeTextView.setText("Hello 5");
		showFormulaResult();
	}

	public void setFormula(Formula formula) {
		formulaToCompute = formula;

		if (formula.containsElement(ElementType.SENSOR)) {
			SensorHandler.startSensorListener(context);
			SensorHandler.registerListener(this);
		}

	}

	@Override
	protected void onStop() {
		SensorHandler.unregisterListener(this);
		SensorHandler.stopSensorListeners();
		super.onStop();
	}

	private void showFormulaResult() {
		float result = formulaToCompute.interpretFloat();
		if (computeTextView == null) {
			return;
		}

		result *= 100;
		result = Math.round(result) / 100f;

		computeTextView.setText(result + "");

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
			case Sensor.TYPE_LINEAR_ACCELERATION:
				showFormulaResult();
				break;
			case Sensor.TYPE_ROTATION_VECTOR:
				showFormulaResult();
				break;
		}

	}

}