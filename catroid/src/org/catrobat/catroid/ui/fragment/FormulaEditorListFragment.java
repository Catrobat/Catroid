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
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.camera.CameraManager;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.ui.SettingsActivity;

import java.util.Arrays;

public class FormulaEditorListFragment extends ListFragment implements Dialog.OnKeyListener {

	public static final String OBJECT_TAG = "objectFragment";
	public static final String FUNCTION_TAG = "functionFragment";
	public static final String LOGIC_TAG = "logicFragment";
	public static final String SENSOR_TAG = "sensorFragment";

	public static final String ACTION_BAR_TITLE_BUNDLE_ARGUMENT = "actionBarTitle";
	public static final String FRAGMENT_TAG_BUNDLE_ARGUMENT = "fragmentTag";

	public static final String[] TAGS = {OBJECT_TAG, FUNCTION_TAG, LOGIC_TAG, SENSOR_TAG};

	private static final int[] OBJECT_ITEMS = {R.string.formula_editor_object_x, R.string.formula_editor_object_y,
			R.string.formula_editor_object_transparency, R.string.formula_editor_object_brightness,
			R.string.formula_editor_object_size, R.string.formula_editor_object_rotation,
			R.string.formula_editor_object_layer};

	private static final int[] LOGIC_ITEMS = {R.string.formula_editor_logic_equal,
			R.string.formula_editor_logic_notequal, R.string.formula_editor_logic_lesserthan,
			R.string.formula_editor_logic_leserequal, R.string.formula_editor_logic_greaterthan,
			R.string.formula_editor_logic_greaterequal, R.string.formula_editor_logic_and,
			R.string.formula_editor_logic_or, R.string.formula_editor_logic_not, R.string.formula_editor_function_true,
			R.string.formula_editor_function_false};

	private static final int[] FUNCTIONS_ITEMS = {R.string.formula_editor_function_sin,
			R.string.formula_editor_function_cos, R.string.formula_editor_function_tan,
			R.string.formula_editor_function_ln, R.string.formula_editor_function_log,
			R.string.formula_editor_function_pi, R.string.formula_editor_function_sqrt,
			R.string.formula_editor_function_rand, R.string.formula_editor_function_abs,
			R.string.formula_editor_function_round, R.string.formula_editor_function_mod,
			R.string.formula_editor_function_arcsin, R.string.formula_editor_function_arccos,
			R.string.formula_editor_function_arctan, R.string.formula_editor_function_exp,
			R.string.formula_editor_function_floor, R.string.formula_editor_function_ceil,
			R.string.formula_editor_function_max, R.string.formula_editor_function_min,
			R.string.formula_editor_function_length, R.string.formula_editor_function_number_of_items, R.string.formula_editor_function_letter,
			R.string.formula_editor_function_join, R.string.formula_editor_function_list_item, R.string.formula_editor_function_contains};

	private static final int[] FUNCTIONS_PARAMETERS = {R.string.formula_editor_function_sin_parameter,
			R.string.formula_editor_function_cos_parameter, R.string.formula_editor_function_tan_parameter,
			R.string.formula_editor_function_ln_parameter, R.string.formula_editor_function_log_parameter,
			R.string.formula_editor_function_pi_parameter, R.string.formula_editor_function_sqrt_parameter,
			R.string.formula_editor_function_rand_parameter, R.string.formula_editor_function_abs_parameter,
			R.string.formula_editor_function_round_parameter, R.string.formula_editor_function_mod_parameter,
			R.string.formula_editor_function_arcsin_parameter, R.string.formula_editor_function_arccos_parameter,
			R.string.formula_editor_function_arctan_parameter, R.string.formula_editor_function_exp_parameter,
			R.string.formula_editor_function_floor_parameter, R.string.formula_editor_function_ceil_parameter,
			R.string.formula_editor_function_max_parameter, R.string.formula_editor_function_min_parameter,
			R.string.formula_editor_function_length_parameter, R.string.formula_editor_function_number_of_items_parameter, R.string.formula_editor_function_letter_parameter,
			R.string.formula_editor_function_join_parameter, R.string.formula_editor_function_list_item_parameter,
			R.string.formula_editor_function_contains_parameter};

	private static final int[] DEFAULT_SENSOR_ITEMS = { R.string.formula_editor_sensor_loudness };

	private static final int[] ACCELERATION_SENSOR_ITEMS = { R.string.formula_editor_sensor_x_acceleration,
			R.string.formula_editor_sensor_y_acceleration, R.string.formula_editor_sensor_z_acceleration };

	private static final int[] INCLINATION_SENSOR_ITEMS = { R.string.formula_editor_sensor_x_inclination,
			R.string.formula_editor_sensor_y_inclination };

	private static final int[] COMPASS_SENSOR_ITEMS = { R.string.formula_editor_sensor_compass_direction};

	private static final int[] NXT_SENSOR_ITEMS = { R.string.formula_editor_sensor_lego_nxt_1,
			R.string.formula_editor_sensor_lego_nxt_2, R.string.formula_editor_sensor_lego_nxt_3,
			R.string.formula_editor_sensor_lego_nxt_4};

	private static final int[] SENSOR_ITEMS_DRONE = { R.string.formula_editor_sensor_drone_battery_status,
			R.string.formula_editor_sensor_drone_emergency_state, R.string.formula_editor_sensor_drone_flying,
			R.string.formula_editor_sensor_drone_initialized, R.string.formula_editor_sensor_drone_usb_active,
			R.string.formula_editor_sensor_drone_usb_remaining_time, R.string.formula_editor_sensor_drone_camera_ready,
			R.string.formula_editor_sensor_drone_record_ready, R.string.formula_editor_sensor_drone_recording,
			R.string.formula_editor_sensor_drone_num_frames };

	private static final int[] EV3_SENSOR_ITEMS = { R.string.formula_editor_sensor_lego_ev3_1,
			R.string.formula_editor_sensor_lego_ev3_2, R.string.formula_editor_sensor_lego_ev3_3,
			R.string.formula_editor_sensor_lego_ev3_4 };

	private static final int[] PHIRO_SENSOR_ITEMS = { R.string.formula_editor_phiro_sensor_front_left,
			R.string.formula_editor_phiro_sensor_front_right, R.string.formula_editor_phiro_sensor_side_left,
			R.string.formula_editor_phiro_sensor_side_right, R.string.formula_editor_phiro_sensor_bottom_left,
			R.string.formula_editor_phiro_sensor_bottom_right };

	private static final int[] ARDUINO_SENSOR_ITEMS = { R.string.formula_editor_function_arduino_read_pin_value_analog,
			R.string.formula_editor_function_arduino_read_pin_value_digital };

	private static final int[] FACE_DETECTION_SENSOR_ITEMS = { R.string.formula_editor_sensor_face_detected,
			R.string.formula_editor_sensor_face_size, R.string.formula_editor_sensor_face_x_position,
			R.string.formula_editor_sensor_face_y_position };

	private static final int[] RASPBERRY_SENSOR_ITEMS = { R.string.formula_editor_function_raspi_read_pin_value_digital };

	private String actionBarTitle;
	private int[] itemsIds;

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		FormulaEditorFragment formulaEditor = (FormulaEditorFragment) getActivity().getFragmentManager()
				.findFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		if (formulaEditor != null) {
			formulaEditor.addResourceToActiveFormula(itemsIds[position]);
			formulaEditor.updateButtonsOnKeyboardAndInvalidateOptionsMenu();
		}
		KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK);
		onKey(null, keyEvent.getKeyCode(), keyEvent);
	}

	public FormulaEditorListFragment() {
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

		itemsIds = new int[]{};

		if (tag.equals(OBJECT_TAG)) {
			itemsIds = OBJECT_ITEMS;
		} else if (tag.equals(FUNCTION_TAG)) {
			itemsIds = FUNCTIONS_ITEMS;
		} else if (tag.equals(LOGIC_TAG)) {
			itemsIds = LOGIC_ITEMS;
		} else if (tag.equals(SENSOR_TAG)) {
			itemsIds = DEFAULT_SENSOR_ITEMS;

			Context context = this.getActivity().getApplicationContext();

			if (SensorHandler.getInstance(context).accelerationAvailable()) {
				itemsIds = concatAll(itemsIds, ACCELERATION_SENSOR_ITEMS);
			}

			if (SensorHandler.getInstance(context).inclinationAvailable()) {
				itemsIds = concatAll(itemsIds, INCLINATION_SENSOR_ITEMS);
			}

			if (SensorHandler.getInstance(context).compassAvailable()) {
				itemsIds = concatAll(itemsIds, COMPASS_SENSOR_ITEMS);
			}

			if (CameraManager.getInstance().hasBackCamera() || CameraManager.getInstance().hasFrontCamera()) {
				itemsIds = concatAll(itemsIds, FACE_DETECTION_SENSOR_ITEMS);
			}

			if (SettingsActivity.isMindstormsNXTSharedPreferenceEnabled(context)) {
				itemsIds = concatAll(itemsIds, NXT_SENSOR_ITEMS);
			}

			if (SettingsActivity.isMindstormsEV3SharedPreferenceEnabled(context)) {
				itemsIds = concatAll(itemsIds, EV3_SENSOR_ITEMS);
			}

			if (SettingsActivity.isPhiroSharedPreferenceEnabled(context)) {
				itemsIds = concatAll(itemsIds, PHIRO_SENSOR_ITEMS);
			}

			if (SettingsActivity.isArduinoSharedPreferenceEnabled(context)) {
				itemsIds = concatAll(itemsIds, ARDUINO_SENSOR_ITEMS);
			}

			if (SettingsActivity.isDroneSharedPreferenceEnabled(context)) {
				itemsIds = concatAll(itemsIds, SENSOR_ITEMS_DRONE);
			}

			if (SettingsActivity.isRaspiSharedPreferenceEnabled(context)) {
				itemsIds = concatAll(itemsIds, RASPBERRY_SENSOR_ITEMS);
			}
		}

		String[] items = new String[itemsIds.length];

		for (int index = 0; index < items.length; index++) {
			items[index] = tag.equals(FUNCTION_TAG) ? getString(itemsIds[index]) + getString(FUNCTIONS_PARAMETERS[index])
					: getString(itemsIds[index]);
		}

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
				R.layout.fragment_formula_editor_list_item, items);
		setListAdapter(arrayAdapter);
	}

	private static int[] concatAll(int[] first, int[]... rest) {
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

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		for (int index = 0; index < menu.size(); index++) {
			menu.getItem(index).setVisible(false);
		}

		getActivity().getActionBar().setDisplayShowTitleEnabled(true);
		getActivity().getActionBar().setTitle(actionBarTitle);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);

		super.onPrepareOptionsMenu(menu);
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
			FragmentTransaction fragTransaction = getActivity().getFragmentManager().beginTransaction();
			fragTransaction.hide(this);
			fragTransaction.show(getActivity().getFragmentManager()
					.findFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG));
			fragTransaction.commit();
			return true;
		}
		return false;
	}
}
