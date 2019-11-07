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
package org.catrobat.catroid.ui.dialogs;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService;
import org.catrobat.catroid.camera.CameraManager;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.facedetection.FaceDetectionHandler;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.formulaeditor.SensorLoudness;

import static org.catrobat.catroid.utils.NumberFormats.trimTrailingCharacters;

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
		if (ProjectManager.getInstance().isCurrentProjectLandscapeMode()) {
			setContentView(R.layout.dialog_formulaeditor_compute_landscape);
			computeTextView = (TextView) findViewById(R.id.formula_editor_compute_dialog_textview_landscape_mode);
		} else {
			setContentView(R.layout.dialog_formulaeditor_compute);
			computeTextView = (TextView) findViewById(R.id.formula_editor_compute_dialog_textview);
		}
		showFormulaResult();
	}

	public void setFormula(Formula formula) {
		formulaToCompute = formula;

		Brick.ResourcesSet resourcesSet = new Brick.ResourcesSet();
		formula.addRequiredResources(resourcesSet);

		if (resourcesSet.contains(Brick.MICROPHONE)) {
			SensorHandler.getInstance(getContext()).setSensorLoudness(new SensorLoudness());
		}

		if (resourcesSet.contains(Brick.FACE_DETECTION)) {
			CameraManager.makeInstance();
			FaceDetectionHandler.startFaceDetection();
		}

		if (resourcesSet.contains(Brick.BLUETOOTH_LEGO_NXT)) {
			BluetoothDeviceService btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE);
			btService.connectDevice(BluetoothDevice.LEGO_NXT, this.getContext());
		}

		if (resourcesSet.contains(Brick.BLUETOOTH_LEGO_EV3)) {
			BluetoothDeviceService btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE);
			btService.connectDevice(BluetoothDevice.LEGO_EV3, this.getContext());
		}

		if (resourcesSet.contains(Brick.BLUETOOTH_SENSORS_ARDUINO)) {
			BluetoothDeviceService btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE);
			btService.connectDevice(BluetoothDevice.ARDUINO, this.getContext());
		}

		if (resourcesSet.contains(Brick.BLUETOOTH_PHIRO)) {
			BluetoothDeviceService btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE);
			btService.connectDevice(BluetoothDevice.PHIRO, this.getContext());
		}

		if (formula.containsElement(ElementType.SENSOR)) {
			SensorHandler.startSensorListener(context);
			SensorHandler.registerListener(this);
		}
	}

	@Override
	protected void onStop() {
		SensorHandler.unregisterListener(this);

		ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE).pause();

		FaceDetectionHandler.stopFaceDetection();
		super.onStop();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		dismiss();
		return true;
	}

	private void showFormulaResult() {
		if (computeTextView == null) {
			return;
		}

		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		Formula.StringProvider stringProvider = new AndroidStringProvider(context);
		String result = formulaToCompute.getResultForComputeDialog(stringProvider, currentSprite);
		setDialogTextView(trimTrailingCharacters(result));
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		showFormulaResult();
	}

	private void setDialogTextView(final String newString) {
		computeTextView.post(() -> {
			computeTextView.setText(newString);

			ViewGroup.LayoutParams params = computeTextView.getLayoutParams();
			int height = computeTextView.getLineCount() * computeTextView.getLineHeight();
			int heightMargin = (int) (height * 0.5);
			params.width = ViewGroup.LayoutParams.MATCH_PARENT;
			params.height = height + heightMargin;
			computeTextView.setLayoutParams(params);
		});
	}

	private static class AndroidStringProvider implements Formula.StringProvider {
		private final Context context;

		AndroidStringProvider(Context context) {
			this.context = context;
		}

		@Override
		public String getTrue() {
			return context.getString(R.string.formula_editor_true);
		}

		@Override
		public String getFalse() {
			return context.getString(R.string.formula_editor_false);
		}
	}
}
