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
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.camera.CameraManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.ui.adapter.CategoryListAdapter;
import org.catrobat.catroid.ui.dialogs.FormulaEditorChooseSpriteDialog;
import org.catrobat.catroid.ui.dialogs.LegoSensorPortConfigDialog;
import org.catrobat.catroid.utils.DividerUtil;
import org.catrobat.catroid.utils.TextSizeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FormulaEditorCategoryListFragment extends ListFragment implements Dialog.OnKeyListener, CategoryListAdapter.OnListItemClickListener {

	public static String tag = FormulaEditorCategoryListFragment.class.getSimpleName();

	public static final String OBJECT_TAG = "objectFragment";
	public static final String FUNCTION_TAG = "functionFragment";
	public static final String LOGIC_TAG = "logicFragment";
	public static final String SENSOR_TAG = "sensorFragment";

	public static final String ACTION_BAR_TITLE_BUNDLE_ARGUMENT = "actionBarTitle";
	public static final String FRAGMENT_TAG_BUNDLE_ARGUMENT = "fragmentTag";

	public static final String[] TAGS = {OBJECT_TAG, FUNCTION_TAG, LOGIC_TAG, SENSOR_TAG};
	private String actionBarTitle;
	private int[] itemsIds;
	private int[] parameterIds;
	private CategoryListAdapter adapter;

	protected Map<String, FormulaEditorCategory> categoryMap = new HashMap<>();

	protected static final int[] OBJECT_GENERAL_PROPERTIES_ITEMS = {R.string.formula_editor_object_transparency,
			R.string.formula_editor_object_brightness, R.string.formula_editor_object_color/*,
			R.string.formula_editor_object_distance_to*/};

	protected static final int[] OBJECT_PHYSICAL_PROPERTIES_ITEMS = {R.string.formula_editor_object_x,
			R.string.formula_editor_object_y, R.string.formula_editor_object_size,
			R.string.formula_editor_object_rotation, R.string.formula_editor_object_layer,
			R.string.formula_editor_function_collision,
			R.string.formula_editor_function_collides_with_edge, R.string.formula_editor_function_touched,
			R.string.formula_editor_object_x_velocity, R.string.formula_editor_object_y_velocity,
			R.string.formula_editor_object_angular_velocity};

	protected static final int[] OBJECT_ITEMS_LOOK = {R.string.formula_editor_object_look_number,
			R.string.formula_editor_object_look_name};

	protected static final int[] OBJECT_ITEMS_BACKGROUND = {R.string.formula_editor_object_background_number,
			R.string.formula_editor_object_background_name};

	protected static final int[] LOGIC_BOOLEAN_OPERATORS_ITEMS = {R.string.formula_editor_logic_and,
			R.string.formula_editor_logic_or, R.string.formula_editor_logic_not,
			R.string.formula_editor_function_true, R.string.formula_editor_function_false};

	protected static final int[] LOGIC_COMPARISON_OPERATORS_ITEMS = {R.string.formula_editor_logic_equal,
			R.string.formula_editor_logic_notequal, R.string.formula_editor_logic_lesserthan,
			R.string.formula_editor_logic_leserequal, R.string.formula_editor_logic_greaterthan,
			R.string.formula_editor_logic_greaterequal};

	protected static final int[] FUNCTIONS_MATH_ITEMS = {R.string.formula_editor_function_sin,
			R.string.formula_editor_function_cos, R.string.formula_editor_function_tan,
			R.string.formula_editor_function_ln, R.string.formula_editor_function_log,
			R.string.formula_editor_function_pi, R.string.formula_editor_function_sqrt,
			R.string.formula_editor_function_rand, R.string.formula_editor_function_abs,
			R.string.formula_editor_function_round, R.string.formula_editor_function_mod,
			R.string.formula_editor_function_arcsin, R.string.formula_editor_function_arccos,
			R.string.formula_editor_function_arctan, R.string.formula_editor_function_exp,
			R.string.formula_editor_function_power,
			R.string.formula_editor_function_floor, R.string.formula_editor_function_ceil,
			R.string.formula_editor_function_max, R.string.formula_editor_function_min};

	protected static final int[] FUNCTIONS_MATH_PARAMETERS = {R.string.formula_editor_function_sin_parameter,
			R.string.formula_editor_function_cos_parameter, R.string.formula_editor_function_tan_parameter,
			R.string.formula_editor_function_ln_parameter, R.string.formula_editor_function_log_parameter,
			R.string.formula_editor_function_pi_parameter, R.string.formula_editor_function_sqrt_parameter,
			R.string.formula_editor_function_rand_parameter, R.string.formula_editor_function_abs_parameter,
			R.string.formula_editor_function_round_parameter, R.string.formula_editor_function_mod_parameter,
			R.string.formula_editor_function_arcsin_parameter, R.string.formula_editor_function_arccos_parameter,
			R.string.formula_editor_function_arctan_parameter, R.string.formula_editor_function_exp_parameter,
			R.string.formula_editor_function_power_parameter,
			R.string.formula_editor_function_floor_parameter, R.string.formula_editor_function_ceil_parameter,
			R.string.formula_editor_function_max_parameter, R.string.formula_editor_function_min_parameter};

	protected static final int[] FUNCTIONS_STRINGS_ITEMS = {R.string.formula_editor_function_length,
			R.string.formula_editor_function_letter, R.string.formula_editor_function_join};

	protected static final int[] FUNCTIONS_STRINGS_PARAMETERS = {R.string.formula_editor_function_length_parameter,
			R.string.formula_editor_function_letter_parameter, R.string.formula_editor_function_join_parameter};

	protected static final int[] FUNCTIONS_LISTS_ITEMS = {R.string.formula_editor_function_number_of_items,
			R.string.formula_editor_function_list_item, R.string.formula_editor_function_contains};

	protected static final int[] FUNCTIONS_LISTS_PARAMETERS = {R.string.formula_editor_function_number_of_items_parameter,
			R.string.formula_editor_function_list_item_parameter, R.string.formula_editor_function_contains_parameter};

	protected static final int[] DEFAULT_SENSOR_ITEMS = {R.string.formula_editor_sensor_loudness, R.string
			.formula_editor_function_touched};

	protected static final int[] DATE_AND_TIME_SENSOR_ITEMS = {R.string.formula_editor_sensor_date_year, R.string.formula_editor_sensor_date_month, R.string.formula_editor_sensor_date_day, R.string.formula_editor_sensor_date_weekday,
			R.string.formula_editor_sensor_time_hour, R.string.formula_editor_sensor_time_minute, R.string.formula_editor_sensor_time_second};

	protected static final int[] ACCELERATION_SENSOR_ITEMS = {R.string.formula_editor_sensor_x_acceleration,
			R.string.formula_editor_sensor_y_acceleration, R.string.formula_editor_sensor_z_acceleration};

	protected static final int[] INCLINATION_SENSOR_ITEMS = {R.string.formula_editor_sensor_x_inclination,
			R.string.formula_editor_sensor_y_inclination};

	protected static final int[] COMPASS_SENSOR_ITEMS = {R.string.formula_editor_sensor_compass_direction};

	protected static final int[] GPS_SENSOR_ITEMS = {R.string.formula_editor_sensor_latitude, R.string
			.formula_editor_sensor_longitude, R.string.formula_editor_sensor_location_accuracy, R.string
			.formula_editor_sensor_altitude};

	protected static final int[] NXT_SENSOR_ITEMS = {R.string.formula_editor_sensor_lego_nxt_touch,
			R.string.formula_editor_sensor_lego_nxt_sound, R.string.formula_editor_sensor_lego_nxt_light,
			R.string.formula_editor_sensor_lego_nxt_light_active, R.string.formula_editor_sensor_lego_nxt_ultrasonic};

	protected static final int[] NFC_TAG_ITEMS = {R.string.formula_editor_nfc_tag_id,
			R.string.formula_editor_nfc_tag_message};

	protected static final int[] SENSOR_ITEMS_DRONE = {R.string.formula_editor_sensor_drone_battery_status,
			R.string.formula_editor_sensor_drone_emergency_state, R.string.formula_editor_sensor_drone_flying,
			R.string.formula_editor_sensor_drone_initialized, R.string.formula_editor_sensor_drone_usb_active,
			R.string.formula_editor_sensor_drone_usb_remaining_time, R.string.formula_editor_sensor_drone_camera_ready,
			R.string.formula_editor_sensor_drone_record_ready, R.string.formula_editor_sensor_drone_recording,
			R.string.formula_editor_sensor_drone_num_frames};

	protected static final int[] EV3_SENSOR_ITEMS = {R.string.formula_editor_sensor_lego_ev3_sensor_touch,
			R.string.formula_editor_sensor_lego_ev3_sensor_infrared, R.string.formula_editor_sensor_lego_ev3_sensor_color,
			R.string.formula_editor_sensor_lego_ev3_sensor_color_ambient, R.string
			.formula_editor_sensor_lego_ev3_sensor_color_reflected};

	protected static final int[] PHIRO_SENSOR_ITEMS = {R.string.formula_editor_phiro_sensor_front_left,
			R.string.formula_editor_phiro_sensor_front_right, R.string.formula_editor_phiro_sensor_side_left,
			R.string.formula_editor_phiro_sensor_side_right, R.string.formula_editor_phiro_sensor_bottom_left,
			R.string.formula_editor_phiro_sensor_bottom_right};

	protected static final int[] ARDUINO_SENSOR_ITEMS = {R.string.formula_editor_function_arduino_read_pin_value_analog,
			R.string.formula_editor_function_arduino_read_pin_value_digital};

	protected static final int[] FACE_DETECTION_SENSOR_ITEMS = {R.string.formula_editor_sensor_face_detected,
			R.string.formula_editor_sensor_face_size, R.string.formula_editor_sensor_face_x_position,
			R.string.formula_editor_sensor_face_y_position};

	protected static final int[] TOUCH_DEDECTION_SENSOR_ITEMS = {R.string.formula_editor_function_finger_x, R.string.formula_editor_function_finger_y,
			R.string.formula_editor_function_is_finger_touching, R.string.formula_editor_function_multi_finger_x,
			R.string.formula_editor_function_multi_finger_y,
			R.string.formula_editor_function_is_multi_finger_touching,
			R.string.formula_editor_function_index_of_last_finger};

	protected static final int[] TOUCH_DEDECTION_PARAMETERS = {R.string.formula_editor_function_no_parameter, R.string.formula_editor_function_no_parameter,
			R.string.formula_editor_function_no_parameter, R.string.formula_editor_function_touch_parameter,
			R.string.formula_editor_function_touch_parameter,
			R.string.formula_editor_function_touch_parameter,
			R.string.formula_editor_function_no_parameter};

	protected static final int[] RASPBERRY_SENSOR_ITEMS = {R.string.formula_editor_function_raspi_read_pin_value_digital};

	protected static final int[] RASPBERRY_SENSOR_PARAMETERS = {R.string.formula_editor_function_pin_default_parameter};

	protected static final int[] CAST_GAMEPAD_SENSOR_ITEMS = {R.string.formula_editor_sensor_gamepad_a_pressed,
			R.string.formula_editor_sensor_gamepad_b_pressed, R.string.formula_editor_sensor_gamepad_up_pressed,
			R.string.formula_editor_sensor_gamepad_down_pressed, R.string.formula_editor_sensor_gamepad_left_pressed,
			R.string.formula_editor_sensor_gamepad_right_pressed
	};

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
			DialogFragment dialog = new LegoSensorPortConfigDialog(itemsIds[position], LegoSensorPortConfigDialog.Lego.NXT);
			dialog.setTargetFragment(this, getTargetRequestCode());
			dialog.show(this.getActivity().getFragmentManager(), LegoSensorPortConfigDialog.DIALOG_FRAGMENT_TAG);
		} else if (isEV3Item(position)) {
			DialogFragment dialog = new LegoSensorPortConfigDialog(itemsIds[position], LegoSensorPortConfigDialog.Lego.EV3);
			dialog.setTargetFragment(this, getTargetRequestCode());
			dialog.show(this.getActivity().getFragmentManager(), LegoSensorPortConfigDialog.DIALOG_FRAGMENT_TAG);
		} else {
			FormulaEditorFragment formulaEditor = (FormulaEditorFragment) getActivity().getFragmentManager()
					.findFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
			if (formulaEditor != null) {
				if (itemsIds[position] == R.string.formula_editor_function_collision) {
					showChooseSpriteDialog(formulaEditor);
				} else {

					formulaEditor.addResourceToActiveFormula(itemsIds[position]);
					formulaEditor.updateButtonsOnKeyboardAndInvalidateOptionsMenu();
				}
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

	private boolean isEV3Item(int position) {
		String clickedItem = getString(itemsIds[position]);
		for (int index = 0; index < EV3_SENSOR_ITEMS.length; index++) {
			if (getString(EV3_SENSOR_ITEMS[index]).equals(clickedItem)) {
				return true;
			}
		}
		return false;
	}

	private void showChooseSpriteDialog(FormulaEditorFragment fragment) {
		final FormulaEditorFragment formulaEditor = fragment;
		final FormulaEditorChooseSpriteDialog dialog = FormulaEditorChooseSpriteDialog.newInstance();
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialogInterface) {
				if (dialog.getSuccessStatus()) {
					Sprite firstSprite = ProjectManager.getInstance().getCurrentSprite();
					Sprite secondSprite = null;
					for (Sprite sprite : ProjectManager.getInstance().getCurrentScene().getSpriteList()) {
						if (sprite.getName().compareTo(dialog.getSprite()) == 0) {
							secondSprite = sprite;
							firstSprite.createCollisionPolygons();
							secondSprite.createCollisionPolygons();
						}
					}
					if (secondSprite != null) {
						formulaEditor.addCollideFormulaToActiveFormula(secondSprite.getName());
					}
				}
			}
		});
		dialog.showDialog(this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		FormulaEditorFragment formulaEditor = (FormulaEditorFragment) getActivity().getFragmentManager()
				.findFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		int item = getLegoPort(resultCode, data.getType().equals("NXT"));
		if (formulaEditor != null && item != -1) {
			formulaEditor.addResourceToActiveFormula(item);
			formulaEditor.updateButtonsOnKeyboardAndInvalidateOptionsMenu();
		}
		KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK);
		onKey(null, keyEvent.getKeyCode(), keyEvent);
	}

	private int getLegoPort(int port, boolean nxt) {
		switch (port) {
			case 0:
				return nxt ? R.string.formula_editor_sensor_lego_nxt_1 : R.string.formula_editor_sensor_lego_ev3_1;
			case 1:
				return nxt ? R.string.formula_editor_sensor_lego_nxt_2 : R.string.formula_editor_sensor_lego_ev3_2;
			case 2:
				return nxt ? R.string.formula_editor_sensor_lego_nxt_3 : R.string.formula_editor_sensor_lego_ev3_3;
			case 3:
				return nxt ? R.string.formula_editor_sensor_lego_nxt_4 : R.string.formula_editor_sensor_lego_ev3_4;
			default:
				return -1;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		initCategories();

		this.actionBarTitle = getArguments().getString(ACTION_BAR_TITLE_BUNDLE_ARGUMENT);
	}

	private void initCategories() {
		initObjects();
		initFunctions();
		initLogic();
		initSensors();
	}

	private void initObjects() {
		if (categoryMap.containsKey(OBJECT_TAG)) {
			return;
		}

		ProjectManager projectManager = ProjectManager.getInstance();
		Sprite currentSprite = projectManager.getCurrentSprite();

		FormulaEditorCategory category = new FormulaEditorCategory();
		category.addHeader(getString(R.string.formula_editor_object_general));
		category.addItems(OBJECT_GENERAL_PROPERTIES_ITEMS);
		if (projectManager.getCurrentScene().isBackgroundObject(currentSprite)) {
			category.addItems(OBJECT_ITEMS_BACKGROUND);
		} else {
			category.addItems(OBJECT_ITEMS_LOOK);
		}
		category.addHeader(getString(R.string.formula_editor_object_movement));
		category.addItems(OBJECT_PHYSICAL_PROPERTIES_ITEMS);
		categoryMap.put(OBJECT_TAG, category);
	}

	private void initFunctions() {
		if (categoryMap.containsKey(FUNCTION_TAG)) {
			return;
		}

		FormulaEditorCategory category = new FormulaEditorCategory();
		category.addHeader(getString(R.string.formula_editor_functions_maths));
		category.addItems(FUNCTIONS_MATH_ITEMS);
		category.addParameters(FUNCTIONS_MATH_PARAMETERS);

		category.addHeader(getString(R.string.formula_editor_functions_strings));
		category.addItems(FUNCTIONS_STRINGS_ITEMS);
		category.addParameters(FUNCTIONS_STRINGS_PARAMETERS);

		category.addHeader(getString(R.string.formula_editor_functions_lists));
		category.addItems(FUNCTIONS_LISTS_ITEMS);
		category.addParameters(FUNCTIONS_LISTS_PARAMETERS);

		categoryMap.put(FUNCTION_TAG, category);
	}

	private void initLogic() {
		if (categoryMap.containsKey(LOGIC_TAG)) {
			return;
		}

		FormulaEditorCategory category = new FormulaEditorCategory();
		category.addHeader(getString(R.string.formula_editor_logic_boolean));
		category.addItems(LOGIC_BOOLEAN_OPERATORS_ITEMS);

		category.addHeader(getString(R.string.formula_editor_logic_comparison));
		category.addItems(LOGIC_COMPARISON_OPERATORS_ITEMS);

		categoryMap.put(LOGIC_TAG, category);
	}

	private void initSensors() {
		if (categoryMap.containsKey(SENSOR_TAG)) {
			return;
		}

		Context context = this.getActivity().getApplicationContext();

		FormulaEditorCategory category = new FormulaEditorCategory();

		if (BuildConfig.PHIRO_CODE && SettingsActivity.isPhiroSharedPreferenceEnabled(context)) {
			category.addHeader(getString(R.string.formula_editor_device_phiro));
			category.addItems(PHIRO_SENSOR_ITEMS);
			category.addParameters(createEmptyParametersList(PHIRO_SENSOR_ITEMS.length));
		}

		category.addHeader(getString(R.string.formula_editor_device_sensors));
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

		if (!BuildConfig.PHIRO_CODE && SettingsActivity.isPhiroSharedPreferenceEnabled(context)) {
			category.addHeader(getString(R.string.formula_editor_device_phiro));
			category.addItems(PHIRO_SENSOR_ITEMS);
			category.addParameters(createEmptyParametersList(PHIRO_SENSOR_ITEMS.length));
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

	@Override
	public void onStart() {
		super.onStart();

		String tag = getArguments().getString(FRAGMENT_TAG_BUNDLE_ARGUMENT);

		itemsIds = categoryMap.get(tag).categoryItemIds;
		parameterIds = categoryMap.get(tag).categoryParameterIds;
		Map<Integer, String> header = categoryMap.get(tag).headerOrderMap;

		List<String> items = new ArrayList<>();

		for (int index = 0; index < itemsIds.length; index++) {
			items.add(index < parameterIds.length ? getString(itemsIds[index]) + getString(parameterIds[index])
					: getString(itemsIds[index]));
		}

		adapter = new CategoryListAdapter(getActivity(), items, header);
		setListAdapter(adapter);
		adapter.setOnListItemClickListener(this);
	}

	protected int[] createEmptyParametersList(int length) {
		int[] noParametersList = new int[length];

		for (int i = 0; i < length; i++) {
			//Dirty hack until further insight is gained
			try {
				Log.i(tag, "Trying to get string resource: " + getString(R.string.formula_editor_function_no_parameter));
				noParametersList[i] = R.string.formula_editor_function_no_parameter;
			} catch (Resources.NotFoundException exception) {
				Log.e(tag, "formula_editor_function_no_parameter not found!" + Log.getStackTraceString(exception));
			}
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
		View view = inflater.inflate(R.layout.fragment_formula_editor_list, container, false);
		container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				getListView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
				DividerUtil.setDivider(getActivity(), getListView());
				TextSizeUtil.enlargeViewGroup((ViewGroup) getView());
			}
		});
		return view;
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

	public class FormulaEditorCategory {
		private int[] categoryItemIds = new int[] {};
		private int[] categoryParameterIds = new int[] {};
		private Map<Integer, String> headerOrderMap = new TreeMap<>();

		public void addHeader(String text) {
			int position = 0;
			if (categoryItemIds != null) {
				position = categoryItemIds.length;
			}
			headerOrderMap.put(position, text);
		}

		public void addItems(int[] items) {
			if (categoryItemIds == null) {
				System.arraycopy(items, 0, categoryItemIds, 0, items.length);
			} else {
				categoryItemIds = concatAll(categoryItemIds, items);
			}
		}

		public void addParameters(int[] parameters) {
			if (categoryParameterIds == null) {
				System.arraycopy(parameters, 0, categoryParameterIds, 0, parameters.length);
			} else {
				categoryParameterIds = concatAll(categoryParameterIds, parameters);
			}
		}
	}
}
