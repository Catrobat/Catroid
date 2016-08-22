/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
package org.catrobat.catroid.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.camera.CameraManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.ui.adapter.CategoryListAdapter;
import org.catrobat.catroid.ui.dialogs.LegoNXTSensorPortConfigDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FormulaEditorCategoryListFragment extends ListFragment implements Dialog.OnKeyListener, CategoryListAdapter.OnListItemClickListener {

	public static final String OBJECT_TAG = "objectFragment";
	public static final String FUNCTION_TAG = "functionFragment";
	public static final String LOGIC_TAG = "logicFragment";
	public static final String SENSOR_TAG = "sensorFragment";

	public static final String ACTION_BAR_TITLE_BUNDLE_ARGUMENT = "actionBarTitle";
	public static final String FRAGMENT_TAG_BUNDLE_ARGUMENT = "fragmentTag";

	public static final String[] TAGS = { OBJECT_TAG, FUNCTION_TAG, LOGIC_TAG, SENSOR_TAG };
	private String actionBarTitle;
	private int[] itemsIds;
	private int[] parameterIds;
	private CategoryListAdapter adapter;

	private static final int[] OBJECT_GENERAL_PROPERTIES_ITEMS = { R.string.formula_editor_object_transparency,
			R.string.formula_editor_object_brightness, R.string.formula_editor_object_color };

	private static final int[] OBJECT_PHYSICAL_PROPERTIES_ITEMS = { R.string.formula_editor_object_x,
			R.string.formula_editor_object_y, R.string.formula_editor_object_size,
			R.string.formula_editor_object_rotation, R.string.formula_editor_object_layer,
			R.string.formula_editor_object_x_velocity, R.string.formula_editor_object_y_velocity,
			R.string.formula_editor_object_angular_velocity };

	private static final int[] OBJECT_ITEMS_LOOK = { R.string.formula_editor_object_look_number,
			R.string.formula_editor_object_look_name };

	private static final int[] OBJECT_ITEMS_BACKGROUND = { R.string.formula_editor_object_background_number,
			R.string.formula_editor_object_background_name };

	private static final int[] LOGIC_BOOLEAN_OPERATORS_ITEMS = { R.string.formula_editor_logic_and,
			R.string.formula_editor_logic_or, R.string.formula_editor_logic_not,
			R.string.formula_editor_function_true, R.string.formula_editor_function_false };

	private static final int[] LOGIC_COMPARISON_OPERATORS_ITEMS = { R.string.formula_editor_logic_equal,
			R.string.formula_editor_logic_notequal, R.string.formula_editor_logic_lesserthan,
			R.string.formula_editor_logic_leserequal, R.string.formula_editor_logic_greaterthan,
			R.string.formula_editor_logic_greaterequal };

	private static final int[] FUNCTIONS_MATH_ITEMS = { R.string.formula_editor_function_sin,
			R.string.formula_editor_function_cos, R.string.formula_editor_function_tan,
			R.string.formula_editor_function_ln, R.string.formula_editor_function_log,
			R.string.formula_editor_function_pi, R.string.formula_editor_function_sqrt,
			R.string.formula_editor_function_rand, R.string.formula_editor_function_abs,
			R.string.formula_editor_function_round, R.string.formula_editor_function_mod,
			R.string.formula_editor_function_arcsin, R.string.formula_editor_function_arccos,
			R.string.formula_editor_function_arctan, R.string.formula_editor_function_exp,
			R.string.formula_editor_function_power,
			R.string.formula_editor_function_floor, R.string.formula_editor_function_ceil,
			R.string.formula_editor_function_max, R.string.formula_editor_function_min };

	private static final int[] FUNCTIONS_MATH_PARAMETERS = { R.string.formula_editor_function_sin_parameter,
			R.string.formula_editor_function_cos_parameter, R.string.formula_editor_function_tan_parameter,
			R.string.formula_editor_function_ln_parameter, R.string.formula_editor_function_log_parameter,
			R.string.formula_editor_function_pi_parameter, R.string.formula_editor_function_sqrt_parameter,
			R.string.formula_editor_function_rand_parameter, R.string.formula_editor_function_abs_parameter,
			R.string.formula_editor_function_round_parameter, R.string.formula_editor_function_mod_parameter,
			R.string.formula_editor_function_arcsin_parameter, R.string.formula_editor_function_arccos_parameter,
			R.string.formula_editor_function_arctan_parameter, R.string.formula_editor_function_exp_parameter,
			R.string.formula_editor_function_power_parameter,
			R.string.formula_editor_function_floor_parameter, R.string.formula_editor_function_ceil_parameter,
			R.string.formula_editor_function_max_parameter, R.string.formula_editor_function_min_parameter };

	private static final int[] FUNCTIONS_STRINGS_ITEMS = { R.string.formula_editor_function_length,
			R.string.formula_editor_function_letter, R.string.formula_editor_function_join };

	private static final int[] FUNCTIONS_STRINGS_PARAMETERS = { R.string.formula_editor_function_length_parameter,
			R.string.formula_editor_function_letter_parameter, R.string.formula_editor_function_join_parameter };

	private static final int[] FUNCTIONS_LISTS_ITEMS = { R.string.formula_editor_function_number_of_items,
			R.string.formula_editor_function_list_item, R.string.formula_editor_function_contains };

	private static final int[] FUNCTIONS_LISTS_PARAMETERS = { R.string.formula_editor_function_number_of_items_parameter,
			R.string.formula_editor_function_list_item_parameter, R.string.formula_editor_function_contains_parameter };

	private static final int[] DEFAULT_SENSOR_ITEMS = { R.string.formula_editor_sensor_loudness };

	private static final int[] ACCELERATION_SENSOR_ITEMS = { R.string.formula_editor_sensor_x_acceleration,
			R.string.formula_editor_sensor_y_acceleration, R.string.formula_editor_sensor_z_acceleration };

	private static final int[] INCLINATION_SENSOR_ITEMS = { R.string.formula_editor_sensor_x_inclination,
			R.string.formula_editor_sensor_y_inclination };

	private static final int[] COMPASS_SENSOR_ITEMS = { R.string.formula_editor_sensor_compass_direction };

	private static final int[] GPS_SENSOR_ITEMS = { R.string.formula_editor_sensor_latitude, R.string
			.formula_editor_sensor_longitude, R.string.formula_editor_sensor_location_accuracy, R.string
			.formula_editor_sensor_altitude };

	private static final int[] NXT_SENSOR_ITEMS = { R.string.formula_editor_sensor_lego_nxt_touch,
			R.string.formula_editor_sensor_lego_nxt_sound, R.string.formula_editor_sensor_lego_nxt_light,
			R.string.formula_editor_sensor_lego_nxt_light_active, R.string.formula_editor_sensor_lego_nxt_ultrasonic };

	private static final int[] NFC_TAG_ID_ITEMS = { R.string.formula_editor_nfc_tag_id };

	private static final int[] SENSOR_ITEMS_DRONE = { R.string.formula_editor_sensor_drone_battery_status,
			R.string.formula_editor_sensor_drone_emergency_state, R.string.formula_editor_sensor_drone_flying,
			R.string.formula_editor_sensor_drone_initialized, R.string.formula_editor_sensor_drone_usb_active,
			R.string.formula_editor_sensor_drone_usb_remaining_time, R.string.formula_editor_sensor_drone_camera_ready,
			R.string.formula_editor_sensor_drone_record_ready, R.string.formula_editor_sensor_drone_recording,
			R.string.formula_editor_sensor_drone_num_frames };

	private static final int[] PHIRO_SENSOR_ITEMS = { R.string.formula_editor_phiro_sensor_front_left,
			R.string.formula_editor_phiro_sensor_front_right, R.string.formula_editor_phiro_sensor_side_left,
			R.string.formula_editor_phiro_sensor_side_right, R.string.formula_editor_phiro_sensor_bottom_left,
			R.string.formula_editor_phiro_sensor_bottom_right };

	private static final int[] ARDUINO_SENSOR_ITEMS = { R.string.formula_editor_function_arduino_read_pin_value_analog,
			R.string.formula_editor_function_arduino_read_pin_value_digital };

	private static final int[] FACE_DETECTION_SENSOR_ITEMS = { R.string.formula_editor_sensor_face_detected,
			R.string.formula_editor_sensor_face_size, R.string.formula_editor_sensor_face_x_position,
			R.string.formula_editor_sensor_face_y_position };

	private static final int[] TOUCH_DEDECTION_SENSOR_ITEMS = { R.string.formula_editor_function_finger_x, R.string.formula_editor_function_finger_y,
			R.string.formula_editor_function_is_finger_touching, R.string.formula_editor_function_multi_finger_x,
			R.string.formula_editor_function_multi_finger_y,
			R.string.formula_editor_function_is_multi_finger_touching,
			R.string.formula_editor_function_index_of_last_finger };

	private static final int[] TOUCH_DEDECTION_PARAMETERS = { R.string.formula_editor_function_no_parameter, R.string.formula_editor_function_no_parameter,
			R.string.formula_editor_function_no_parameter, R.string.formula_editor_function_touch_parameter,
			R.string.formula_editor_function_touch_parameter,
			R.string.formula_editor_function_touch_parameter,
			R.string.formula_editor_function_no_parameter };

	private static final int[] RASPBERRY_SENSOR_ITEMS = { R.string.formula_editor_function_raspi_read_pin_value_digital };

	private static final int[] RASPBERRY_SENSOR_PARAMETERS = { R.string.formula_editor_function_pin_default_parameter };

	private int[] concatAll(int[] first, int[]... rest) {
		int totalLength = first.length;
		for (int[] array : rest) {
			totalLength += array.length;
		}
		int[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (int[] array : rest) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}

	public void onListItemClick(int position) {
		if (isNXTItem(position)) {
			DialogFragment dialog = new LegoNXTSensorPortConfigDialog(itemsIds[position]);
			dialog.setTargetFragment(this, getTargetRequestCode());
			dialog.show(this.getActivity().getFragmentManager(), LegoNXTSensorPortConfigDialog.DIALOG_FRAGMENT_TAG);
		} else {
			FormulaEditorFragment formulaEditor = (FormulaEditorFragment) getActivity().getFragmentManager()
					.findFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
			if (formulaEditor != null) {
				formulaEditor.addResourceToActiveFormula(itemsIds[position]);
				formulaEditor.updateButtonsOnKeyboardAndInvalidateOptionsMenu();
			}
			KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK);
			onKey(null, keyEvent.getKeyCode(), keyEvent);
		}
	}

	private boolean isNXTItem(int position) {
		String clickedItem = getString(itemsIds[position]);
		for (int index = 0; index < NXT_SENSOR_ITEMS.length; index++) {
			if (getString(NXT_SENSOR_ITEMS[index]).equals(clickedItem)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		FormulaEditorFragment formulaEditor = (FormulaEditorFragment) getActivity().getFragmentManager()
				.findFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		int item = getNXTPort(resultCode);
		if (formulaEditor != null && item != -1) {
			formulaEditor.addResourceToActiveFormula(item);
			formulaEditor.updateButtonsOnKeyboardAndInvalidateOptionsMenu();
		}
		KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK);
		onKey(null, keyEvent.getKeyCode(), keyEvent);
	}

	private int getNXTPort(int port) {
		switch (port) {
			case 0:
				return R.string.formula_editor_sensor_lego_nxt_1;
			case 1:
				return R.string.formula_editor_sensor_lego_nxt_2;
			case 2:
				return R.string.formula_editor_sensor_lego_nxt_3;
			case 3:
				return R.string.formula_editor_sensor_lego_nxt_4;
			default:
				return -1;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		this.actionBarTitle = getArguments().getString(ACTION_BAR_TITLE_BUNDLE_ARGUMENT);
	}

	@Override
	public void onStart() {
		super.onStart();

		String tag = getArguments().getString(FRAGMENT_TAG_BUNDLE_ARGUMENT);

		itemsIds = new int[] {};
		parameterIds = new int[] {};
		Map<Integer, String> header = new TreeMap<>();

		if (tag.equals(OBJECT_TAG)) {
			header.put(0, getString(R.string.formula_editor_object_general));
			itemsIds = OBJECT_GENERAL_PROPERTIES_ITEMS;

			ProjectManager projectManager = ProjectManager.getInstance();
			Sprite currentSprite = projectManager.getCurrentSprite();
			if (projectManager.getCurrentProject().isBackgroundObject(currentSprite)) {
				itemsIds = concatAll(itemsIds, OBJECT_ITEMS_BACKGROUND);
			} else {
				itemsIds = concatAll(itemsIds, OBJECT_ITEMS_LOOK);
			}
			header.put(itemsIds.length, getString(R.string.formula_editor_object_physical));
			itemsIds = concatAll(itemsIds, OBJECT_PHYSICAL_PROPERTIES_ITEMS);
		} else if (tag.equals(FUNCTION_TAG)) {
			header.put(0, getString(R.string.formula_editor_functions_maths));
			itemsIds = FUNCTIONS_MATH_ITEMS;
			parameterIds = FUNCTIONS_MATH_PARAMETERS;

			header.put(itemsIds.length, getString(R.string.formula_editor_functions_strings));
			itemsIds = concatAll(itemsIds, FUNCTIONS_STRINGS_ITEMS);
			parameterIds = concatAll(parameterIds, FUNCTIONS_STRINGS_PARAMETERS);

			header.put(itemsIds.length, getString(R.string.formula_editor_functions_lists));
			itemsIds = concatAll(itemsIds, FUNCTIONS_LISTS_ITEMS);
			parameterIds = concatAll(parameterIds, FUNCTIONS_LISTS_PARAMETERS);
		} else if (tag.equals(LOGIC_TAG)) {
			header.put(0, getString(R.string.formula_editor_logic_boolean));
			itemsIds = LOGIC_BOOLEAN_OPERATORS_ITEMS;

			header.put(itemsIds.length, getString(R.string.formula_editor_logic_comparison));
			itemsIds = concatAll(itemsIds, LOGIC_COMPARISON_OPERATORS_ITEMS);
		} else if (tag.equals(SENSOR_TAG)) {
			header.put(0, getString(R.string.formula_editor_device));
			itemsIds = DEFAULT_SENSOR_ITEMS;
			parameterIds = createEmptyParametersList(DEFAULT_SENSOR_ITEMS.length);

			Context context = this.getActivity().getApplicationContext();

			if (SensorHandler.getInstance(context).accelerationAvailable()) {
				itemsIds = concatAll(itemsIds, ACCELERATION_SENSOR_ITEMS);
				parameterIds = concatAll(parameterIds, createEmptyParametersList(ACCELERATION_SENSOR_ITEMS.length));
			}

			if (SensorHandler.getInstance(context).inclinationAvailable()) {
				itemsIds = concatAll(itemsIds, INCLINATION_SENSOR_ITEMS);
				parameterIds = concatAll(parameterIds, createEmptyParametersList(INCLINATION_SENSOR_ITEMS.length));
			}

			if (SensorHandler.getInstance(context).compassAvailable()) {
				itemsIds = concatAll(itemsIds, COMPASS_SENSOR_ITEMS);
				parameterIds = concatAll(parameterIds, createEmptyParametersList(COMPASS_SENSOR_ITEMS.length));
			}

			itemsIds = concatAll(itemsIds, GPS_SENSOR_ITEMS);
			parameterIds = concatAll(parameterIds, createEmptyParametersList(GPS_SENSOR_ITEMS.length));

			if (SettingsActivity.isNfcSharedPreferenceEnabled(context)) {
				itemsIds = concatAll(itemsIds, NFC_TAG_ID_ITEMS);
				parameterIds = concatAll(parameterIds, createEmptyParametersList(NFC_TAG_ID_ITEMS.length));
			}

			header.put(itemsIds.length, getString(R.string.formula_editor_device_touch_detection));
			itemsIds = concatAll(itemsIds, TOUCH_DEDECTION_SENSOR_ITEMS);
			parameterIds = concatAll(parameterIds, TOUCH_DEDECTION_PARAMETERS);

			if (CameraManager.getInstance().hasBackCamera() || CameraManager.getInstance().hasFrontCamera()) {
				header.put(itemsIds.length, getString(R.string.formula_editor_device_face_detection));
				itemsIds = concatAll(itemsIds, FACE_DETECTION_SENSOR_ITEMS);
				parameterIds = concatAll(parameterIds, createEmptyParametersList(FACE_DETECTION_SENSOR_ITEMS.length));
			}

			if (SettingsActivity.isMindstormsNXTSharedPreferenceEnabled(context)) {
				header.put(itemsIds.length, getString(R.string.formula_editor_device_lego));
				itemsIds = concatAll(itemsIds, NXT_SENSOR_ITEMS);
				parameterIds = concatAll(parameterIds, createEmptyParametersList(NXT_SENSOR_ITEMS.length));
			}

			if (SettingsActivity.isPhiroSharedPreferenceEnabled(context)) {
				header.put(itemsIds.length, getString(R.string.formula_editor_device_phiro));
				itemsIds = concatAll(itemsIds, PHIRO_SENSOR_ITEMS);
				parameterIds = concatAll(parameterIds, createEmptyParametersList(PHIRO_SENSOR_ITEMS.length));
			}

			if (SettingsActivity.isArduinoSharedPreferenceEnabled(context)) {
				header.put(itemsIds.length, getString(R.string.formula_editor_device_arduino));
				itemsIds = concatAll(itemsIds, ARDUINO_SENSOR_ITEMS);
				parameterIds = concatAll(parameterIds, createEmptyParametersList(ARDUINO_SENSOR_ITEMS.length));
			}

			if (SettingsActivity.isDroneSharedPreferenceEnabled(context)) {
				header.put(itemsIds.length, getString(R.string.formula_editor_device_drone));
				itemsIds = concatAll(itemsIds, SENSOR_ITEMS_DRONE);
				parameterIds = concatAll(parameterIds, createEmptyParametersList(SENSOR_ITEMS_DRONE.length));
			}

			if (SettingsActivity.isRaspiSharedPreferenceEnabled(context)) {
				header.put(itemsIds.length, getString(R.string.formula_editor_device_raspberry));
				itemsIds = concatAll(itemsIds, RASPBERRY_SENSOR_ITEMS);
				parameterIds = concatAll(parameterIds, RASPBERRY_SENSOR_PARAMETERS);
			}
		}

		List<String> items = new ArrayList<>();

		for (int index = 0; index < itemsIds.length; index++) {
			items.add(index < parameterIds.length ? getString(itemsIds[index]) + getString(parameterIds[index])
					: getString(itemsIds[index]));
		}

		adapter = new CategoryListAdapter(getActivity(), items, header);
		setListAdapter(adapter);
		adapter.setOnListItemClickListener(this);
	}

	private int[] createEmptyParametersList(int length) {
		int[] noParametersList = new int[length];

		for (int i = 0; i < length; i++) {
			noParametersList[i] = R.string.formula_editor_function_no_parameter;
		}

		return noParametersList;
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		for (int index = 0; index < menu.size(); index++) {
			menu.getItem(index).setVisible(false);
		}

		getActivity().getActionBar().setDisplayShowTitleEnabled(true);
		getActivity().getActionBar().setTitle(actionBarTitle);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				closeCategoryListFragment();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_formula_editor_list, container, false);
	}

	public void showFragment(Context context) {
		Activity activity = (Activity) context;
		FragmentManager fragmentManager = activity.getFragmentManager();
		FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
		Fragment formulaEditorFragment = fragmentManager
				.findFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		fragTransaction.hide(formulaEditorFragment);

		fragTransaction.show(this);
		fragTransaction.commit();
	}

	@Override
	public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			closeCategoryListFragment();
			return true;
		}
		return false;
	}

	private void closeCategoryListFragment() {
		FragmentTransaction fragTransaction = getActivity().getFragmentManager().beginTransaction();
		fragTransaction.hide(this);
		fragTransaction.show(getActivity().getFragmentManager()
				.findFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG));
		fragTransaction.commit();
	}
}
