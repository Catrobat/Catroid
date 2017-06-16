/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
package org.catrobat.catroid.phiro.ui.fragment;

import android.content.Context;
import android.os.Bundle;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.camera.CameraManager;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.ui.fragment.FormulaEditorCategoryListFragment;

public class PhiroFormulaEditorCategoryListFragment extends FormulaEditorCategoryListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		initCategories();
		super.onCreate(savedInstanceState);
	}

	private void initCategories() {
		initSensors();
	}

	private void initSensors() {
		if (categoryMap.containsKey(SENSOR_TAG)) {
			return;
		}

		Context context = this.getActivity().getApplicationContext();

		FormulaEditorCategory category = new FormulaEditorCategory();
		category.addHeader(getString(R.string.formula_editor_device));

		if (SettingsActivity.isPhiroSharedPreferenceEnabled(context)) {
			category.addHeader(getString(R.string.formula_editor_device_phiro));
			category.addItems(PHIRO_SENSOR_ITEMS);
			category.addParameters(createEmptyParametersList(PHIRO_SENSOR_ITEMS.length));
		}

		category.addHeader(getString(R.string.formula_editor_device_general_device_sensors));
		category.addItems(DEFAULT_SENSOR_ITEMS);
		category.addParameters(createEmptyParametersList(DEFAULT_SENSOR_ITEMS.length));

		if (SensorHandler.getInstance(context).accelerationAvailable()) {
			category.addItems(ACCELERATION_SENSOR_ITEMS);
			category.addParameters(createEmptyParametersList(ACCELERATION_SENSOR_ITEMS.length));
		}

		if (SensorHandler.getInstance(context).inclinationAvailable()) {
			category.addItems(INCLINATION_SENSOR_ITEMS);
			category.addParameters(createEmptyParametersList(INCLINATION_SENSOR_ITEMS.length));
		}

		if (SensorHandler.getInstance(context).compassAvailable()) {
			category.addItems(COMPASS_SENSOR_ITEMS);
			category.addParameters(createEmptyParametersList(COMPASS_SENSOR_ITEMS.length));
		}

		category.addItems(GPS_SENSOR_ITEMS);
		category.addParameters(createEmptyParametersList(GPS_SENSOR_ITEMS.length));

		category.addHeader(getString(R.string.formula_editor_device_touch_detection));
		category.addItems(TOUCH_DEDECTION_SENSOR_ITEMS);
		category.addParameters(TOUCH_DEDECTION_PARAMETERS);

		if (CameraManager.getInstance().hasBackCamera() || CameraManager.getInstance().hasFrontCamera()) {
			category.addHeader(getString(R.string.formula_editor_device_face_detection));
			category.addItems(FACE_DETECTION_SENSOR_ITEMS);
			category.addParameters(createEmptyParametersList(FACE_DETECTION_SENSOR_ITEMS.length));
		}

		category.addHeader(getString(R.string.formula_editor_device_date_and_time));
		category.addItems(DATE_AND_TIME_SENSOR_ITEMS);

		if (SettingsActivity.isMindstormsNXTSharedPreferenceEnabled(context)) {
			category.addHeader(getString(R.string.formula_editor_device_lego));
			category.addItems(NXT_SENSOR_ITEMS);
			category.addParameters(createEmptyParametersList(NXT_SENSOR_ITEMS.length));
		}

		if (SettingsActivity.isMindstormsEV3SharedPreferenceEnabled(context)) {
			category.addItems(EV3_SENSOR_ITEMS);
		}

		if (SettingsActivity.isArduinoSharedPreferenceEnabled(context)) {
			category.addHeader(getString(R.string.formula_editor_device_arduino));
			category.addItems(ARDUINO_SENSOR_ITEMS);
			category.addParameters(createEmptyParametersList(ARDUINO_SENSOR_ITEMS.length));
		}

		if (SettingsActivity.isDroneSharedPreferenceEnabled(context)) {
			category.addHeader(getString(R.string.formula_editor_device_drone));
			category.addItems(SENSOR_ITEMS_DRONE);
			category.addParameters(createEmptyParametersList(SENSOR_ITEMS_DRONE.length));
		}

		if (SettingsActivity.isRaspiSharedPreferenceEnabled(context)) {
			category.addHeader(getString(R.string.formula_editor_device_raspberry));
			category.addItems(RASPBERRY_SENSOR_PARAMETERS);
			category.addParameters(RASPBERRY_SENSOR_PARAMETERS);
		}

		if (SettingsActivity.isNfcSharedPreferenceEnabled(context)) {
			category.addHeader(getString(R.string.formula_editor_device_nfc));
			category.addItems(NFC_TAG_ITEMS);
			category.addParameters(createEmptyParametersList(NFC_TAG_ITEMS.length));
		}

		if (ProjectManager.getInstance().getCurrentProject().isCastProject()) {
			category.addHeader(getString(R.string.formula_editor_device_cast));
			category.addItems(CAST_GAMEPAD_SENSOR_ITEMS);
			category.addParameters(createEmptyParametersList(CAST_GAMEPAD_SENSOR_ITEMS.length));
		}

		categoryMap.put(SENSOR_TAG, category);
	}
}
