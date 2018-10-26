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

package org.catrobat.catroid.ui.recyclerview.fragment;

import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.camera.CameraManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.Constants.LegoSensorType;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.ui.dialogs.LegoSensorPortConfigDialog;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.ui.recyclerview.adapter.CategoryListRVAdapter;
import org.catrobat.catroid.ui.recyclerview.adapter.CategoryListRVAdapter.CategoryListItem;
import org.catrobat.catroid.ui.recyclerview.adapter.CategoryListRVAdapter.CategoryListItemType;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CategoryListFragment extends Fragment implements CategoryListRVAdapter.OnItemClickListener {

	public static final String OBJECT_TAG = "objectFragment";
	public static final String FUNCTION_TAG = "functionFragment";
	public static final String LOGIC_TAG = "logicFragment";
	public static final String SENSOR_TAG = "sensorFragment";
	public static final String ACTION_BAR_TITLE_BUNDLE_ARGUMENT = "actionBarTitle";
	public static final String FRAGMENT_TAG_BUNDLE_ARGUMENT = "fragmentTag";
	public static final String TAG = CategoryListFragment.class.getSimpleName();

	private static final List<Integer> OBJECT_GENERAL_PROPERTIES = Arrays.asList(
			R.string.formula_editor_object_transparency,
			R.string.formula_editor_object_brightness,
			R.string.formula_editor_object_color);
	private static final List<Integer> OBJECT_LOOK = Arrays.asList(R.string.formula_editor_object_look_number,
			R.string.formula_editor_object_look_name);
	private static final List<Integer> OBJECT_BACKGROUND = Arrays.asList(R.string.formula_editor_object_background_number,
			R.string.formula_editor_object_background_name);
	private static final List<Integer> OBJECT_PHYSICAL_1 = Arrays.asList(R.string.formula_editor_object_x,
			R.string.formula_editor_object_y, R.string.formula_editor_object_size,
			R.string.formula_editor_object_rotation, R.string.formula_editor_object_layer);
	private static final List<Integer> OBJECT_PHYSICAL_COLLISION = Arrays.asList(R.string.formula_editor_function_collision);
	private static final List<Integer> OBJECT_PHYSICAL_2 = Arrays.asList(R.string.formula_editor_function_collides_with_edge,
			R.string.formula_editor_function_touched, R.string.formula_editor_object_x_velocity,
			R.string.formula_editor_object_y_velocity, R.string.formula_editor_object_angular_velocity);
	private static final List<Integer> MATH_FUNCTIONS = Arrays.asList(R.string.formula_editor_function_sin,
			R.string.formula_editor_function_cos, R.string.formula_editor_function_tan,
			R.string.formula_editor_function_ln, R.string.formula_editor_function_log,
			R.string.formula_editor_function_pi, R.string.formula_editor_function_sqrt,
			R.string.formula_editor_function_rand, R.string.formula_editor_function_abs,
			R.string.formula_editor_function_round, R.string.formula_editor_function_mod,
			R.string.formula_editor_function_arcsin, R.string.formula_editor_function_arccos,
			R.string.formula_editor_function_arctan, R.string.formula_editor_function_exp,
			R.string.formula_editor_function_power, R.string.formula_editor_function_floor,
			R.string.formula_editor_function_ceil, R.string.formula_editor_function_max,
			R.string.formula_editor_function_min);
	private static final List<Integer> MATH_PARAMS = Arrays.asList(R.string.formula_editor_function_sin_parameter,
			R.string.formula_editor_function_cos_parameter, R.string.formula_editor_function_tan_parameter,
			R.string.formula_editor_function_ln_parameter, R.string.formula_editor_function_log_parameter,
			R.string.formula_editor_function_pi_parameter, R.string.formula_editor_function_sqrt_parameter,
			R.string.formula_editor_function_rand_parameter, R.string.formula_editor_function_abs_parameter,
			R.string.formula_editor_function_round_parameter, R.string.formula_editor_function_mod_parameter,
			R.string.formula_editor_function_arcsin_parameter, R.string.formula_editor_function_arccos_parameter,
			R.string.formula_editor_function_arctan_parameter, R.string.formula_editor_function_exp_parameter,
			R.string.formula_editor_function_power_parameter, R.string.formula_editor_function_floor_parameter,
			R.string.formula_editor_function_ceil_parameter, R.string.formula_editor_function_max_parameter,
			R.string.formula_editor_function_min_parameter);
	private static final List<Integer> STRING_FUNCTIONS = Arrays.asList(R.string.formula_editor_function_length,
			R.string.formula_editor_function_letter, R.string.formula_editor_function_join);
	private static final List<Integer> STRING_PARAMS = Arrays.asList(R.string.formula_editor_function_length_parameter,
			R.string.formula_editor_function_letter_parameter, R.string.formula_editor_function_join_parameter);
	private static final List<Integer> LIST_FUNCTIONS = Arrays.asList(R.string.formula_editor_function_number_of_items,
			R.string.formula_editor_function_list_item, R.string.formula_editor_function_contains);
	private static final List<Integer> LIST_PARAMS = Arrays.asList(R.string.formula_editor_function_number_of_items_parameter,
			R.string.formula_editor_function_list_item_parameter,
			R.string.formula_editor_function_contains_parameter);
	private static final List<Integer> LOGIC_BOOL = Arrays.asList(R.string.formula_editor_logic_and,
			R.string.formula_editor_logic_or, R.string.formula_editor_logic_not,
			R.string.formula_editor_function_true, R.string.formula_editor_function_false);
	private static final List<Integer> LOCIG_COMPARISION = Arrays.asList(R.string.formula_editor_logic_equal,
			R.string.formula_editor_logic_notequal, R.string.formula_editor_logic_lesserthan,
			R.string.formula_editor_logic_leserequal, R.string.formula_editor_logic_greaterthan,
			R.string.formula_editor_logic_greaterequal);
	private static final List<Integer> SENSORS_DEFAULT = Arrays.asList(R.string.formula_editor_sensor_loudness,
			R.string.formula_editor_function_touched);
	private static final List<Integer> SENSORS_ACCELERATION = Arrays.asList(R.string.formula_editor_sensor_x_acceleration,
			R.string.formula_editor_sensor_y_acceleration, R.string.formula_editor_sensor_z_acceleration);
	private static final List<Integer> SENSORS_INCLINATION = Arrays.asList(R.string.formula_editor_sensor_x_inclination,
			R.string.formula_editor_sensor_y_inclination);
	private static final List<Integer> SENSORS_COMPASS = Arrays.asList(R.string.formula_editor_sensor_compass_direction);
	private static final List<Integer> SENSORS_GPS = Arrays.asList(R.string.formula_editor_sensor_latitude,
			R.string.formula_editor_sensor_longitude, R.string.formula_editor_sensor_location_accuracy,
			R.string.formula_editor_sensor_altitude);
	private static final List<Integer> SENSORS_TOUCH = Arrays.asList(R.string.formula_editor_function_finger_x,
			R.string.formula_editor_function_finger_y, R.string.formula_editor_function_is_finger_touching,
			R.string.formula_editor_function_multi_finger_x, R.string.formula_editor_function_multi_finger_y,
			R.string.formula_editor_function_is_multi_finger_touching,
			R.string.formula_editor_function_index_of_last_finger);
	private static final List<Integer> SENSORS_TOUCH_PARAMS = Arrays.asList(R.string.formula_editor_function_no_parameter,
			R.string.formula_editor_function_no_parameter, R.string.formula_editor_function_no_parameter,
			R.string.formula_editor_function_touch_parameter, R.string.formula_editor_function_touch_parameter,
			R.string.formula_editor_function_touch_parameter, R.string.formula_editor_function_no_parameter);
	private static final List<Integer> SENSORS_FACE_DETECTION = Arrays.asList(R.string.formula_editor_sensor_face_detected,
			R.string.formula_editor_sensor_face_size, R.string.formula_editor_sensor_face_x_position,
			R.string.formula_editor_sensor_face_y_position);
	private static final List<Integer> SENSORS_DATE_TIME = Arrays.asList(R.string.formula_editor_sensor_date_year,
			R.string.formula_editor_sensor_date_month, R.string.formula_editor_sensor_date_day,
			R.string.formula_editor_sensor_date_weekday, R.string.formula_editor_sensor_time_hour,
			R.string.formula_editor_sensor_time_minute, R.string.formula_editor_sensor_time_second);
	private static final List<Integer> SENSORS_NXT = Arrays.asList(R.string.formula_editor_sensor_lego_nxt_touch,
			R.string.formula_editor_sensor_lego_nxt_sound, R.string.formula_editor_sensor_lego_nxt_light,
			R.string.formula_editor_sensor_lego_nxt_light_active,
			R.string.formula_editor_sensor_lego_nxt_ultrasonic);
	private static final List<Integer> SENSORS_EV3 = Arrays.asList(R.string.formula_editor_sensor_lego_ev3_sensor_touch,
			R.string.formula_editor_sensor_lego_ev3_sensor_infrared,
			R.string.formula_editor_sensor_lego_ev3_sensor_color,
			R.string.formula_editor_sensor_lego_ev3_sensor_color_ambient,
			R.string.formula_editor_sensor_lego_ev3_sensor_color_reflected,
			R.string.formula_editor_sensor_lego_ev3_sensor_hitechnic_color,
			R.string.formula_editor_sensor_lego_ev3_sensor_nxt_temperature_c,
			R.string.formula_editor_sensor_lego_ev3_sensor_nxt_temperature_f,
			R.string.formula_editor_sensor_lego_ev3_sensor_nxt_light,
			R.string.formula_editor_sensor_lego_ev3_sensor_nxt_light_active,
			R.string.formula_editor_sensor_lego_ev3_sensor_nxt_sound,
			R.string.formula_editor_sensor_lego_ev3_sensor_nxt_ultrasonic);
	private static final List<Integer> SENSORS_PHIRO = Arrays.asList(R.string.formula_editor_phiro_sensor_front_left,
			R.string.formula_editor_phiro_sensor_front_right,
			R.string.formula_editor_phiro_sensor_side_left,
			R.string.formula_editor_phiro_sensor_side_right,
			R.string.formula_editor_phiro_sensor_bottom_left,
			R.string.formula_editor_phiro_sensor_bottom_right);
	private static final List<Integer> SENSORS_ARDUINO = Arrays.asList(R.string.formula_editor_function_arduino_read_pin_value_analog,
			R.string.formula_editor_function_arduino_read_pin_value_digital);
	private static final List<Integer> SENSORS_DRONE = Arrays.asList(R.string.formula_editor_sensor_drone_battery_status,
			R.string.formula_editor_sensor_drone_emergency_state, R.string.formula_editor_sensor_drone_flying,
			R.string.formula_editor_sensor_drone_initialized, R.string.formula_editor_sensor_drone_usb_active,
			R.string.formula_editor_sensor_drone_usb_remaining_time, R.string.formula_editor_sensor_drone_camera_ready,
			R.string.formula_editor_sensor_drone_record_ready, R.string.formula_editor_sensor_drone_recording,
			R.string.formula_editor_sensor_drone_num_frames);
	private static final List<Integer> SENSORS_RASPBERRY = Arrays.asList(R.string.formula_editor_function_raspi_read_pin_value_digital);
	private static final List<Integer> SENSORS_RASPBERRY_PARAMS = Arrays.asList(R.string.formula_editor_function_pin_default_parameter);
	private static final List<Integer> SENSORS_NFC = Arrays.asList(R.string.formula_editor_nfc_tag_id,
			R.string.formula_editor_nfc_tag_message);
	private static final List<Integer> SENSORS_CAST_GAMEPAD = Arrays.asList(R.string.formula_editor_sensor_gamepad_a_pressed,
			R.string.formula_editor_sensor_gamepad_b_pressed,
			R.string.formula_editor_sensor_gamepad_up_pressed,
			R.string.formula_editor_sensor_gamepad_down_pressed,
			R.string.formula_editor_sensor_gamepad_left_pressed,
			R.string.formula_editor_sensor_gamepad_right_pressed);

	private RecyclerView recyclerView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View parent = inflater.inflate(R.layout.fragment_list_view, container, false);
		recyclerView = parent.findViewById(R.id.recycler_view);
		setHasOptionsMenu(true);
		return parent;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initializeAdapter();
	}

	@Override
	public void onResume() {
		super.onResume();
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getArguments()
				.getString(ACTION_BAR_TITLE_BUNDLE_ARGUMENT));
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		for (int index = 0; index < menu.size(); index++) {
			menu.getItem(index).setVisible(false);
		}

		((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void onItemClick(CategoryListItem item) {
		switch (item.type) {
			case CategoryListRVAdapter.NXT:
				showLegoSensorPortConfigDialog(item.nameResId, Constants.NXT);
				break;

			case CategoryListRVAdapter.EV3:
				showLegoSensorPortConfigDialog(item.nameResId, Constants.EV3);
				break;

			case CategoryListRVAdapter.COLLISION:
				showSelectSpriteDialog();
				break;

			case CategoryListRVAdapter.DEFAULT:
				((FormulaEditorFragment) getFragmentManager()
						.findFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG))
						.addResourceToActiveFormula(item.nameResId);
				getActivity().onBackPressed();
				break;
		}
	}

	private void showLegoSensorPortConfigDialog(int itemNameResId, @LegoSensorType final int type) {

		new LegoSensorPortConfigDialog.Builder(getContext(), type, itemNameResId)
				.setPositiveButton(getString(R.string.ok), new LegoSensorPortConfigDialog.OnClickListener() {

					@Override
					public void onPositiveButtonClick(DialogInterface dialog, int selectedPort, Enum selectedSensor) {
						if (type == Constants.NXT) {
							SettingsFragment.setLegoMindstormsNXTSensorMapping(getActivity(),
									(NXTSensor.Sensor) selectedSensor, SettingsFragment.NXT_SENSORS[selectedPort]);
						} else if (type == Constants.EV3) {
							SettingsFragment.setLegoMindstormsEV3SensorMapping(getActivity(),
									(EV3Sensor.Sensor) selectedSensor, SettingsFragment.EV3_SENSORS[selectedPort]);
						}

						FormulaEditorFragment formulaEditor = (FormulaEditorFragment) getFragmentManager()
								.findFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);

						TypedArray sensorPorts = type == Constants.NXT
								? getResources().obtainTypedArray(R.array.formula_editor_nxt_ports)
								: getResources().obtainTypedArray(R.array.formula_editor_ev3_ports);
						int resourceId = sensorPorts.getResourceId(selectedPort, 0);
						if (resourceId != 0) {
							formulaEditor.addResourceToActiveFormula(resourceId);
							formulaEditor.updateButtonsOnKeyboardAndInvalidateOptionsMenu();
						}

						getActivity().onBackPressed();
					}
				})
				.create()
				.show();
	}

	private void showSelectSpriteDialog() {
		final Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		List<Sprite> sprites = ProjectManager.getInstance().getCurrentlyEditedScene().getSpriteList();
		final List<Sprite> selectableSprites = new ArrayList<>();

		for (Sprite sprite : sprites) {
			if (sprites.indexOf(sprite) != 0 && sprite != currentSprite) {
				selectableSprites.add(sprite);
			}
		}

		String[] selectableSpriteNames = new String[selectableSprites.size()];
		for (int i = 0; i < selectableSprites.size(); i++) {
			selectableSpriteNames[i] = selectableSprites.get(i).getName();
		}

		new AlertDialog.Builder(getActivity())
				.setTitle(R.string.formula_editor_function_collision)
				.setItems(selectableSpriteNames, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Sprite selectedSprite = selectableSprites.get(which);

						currentSprite.createCollisionPolygons();
						selectedSprite.createCollisionPolygons();

						((FormulaEditorFragment) getFragmentManager()
								.findFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG))
								.addCollideFormulaToActiveFormula(selectedSprite.getName());
						getActivity().onBackPressed();
					}
				})
				.create()
				.show();
	}

	private void initializeAdapter() {
		String argument = getArguments().getString(FRAGMENT_TAG_BUNDLE_ARGUMENT);

		List<CategoryListItem> items;
		if (OBJECT_TAG.equals(argument)) {
			items = getObjectItems();
		} else if (FUNCTION_TAG.equals(argument)) {
			items = getFunctionItems();
		} else if (LOGIC_TAG.equals(argument)) {
			items = getLogicItems();
		} else if (SENSOR_TAG.equals(argument)) {
			items = getSensorItems();
		} else {
			throw new IllegalArgumentException("Argument for CategoryListFragent null or unknown: " + argument);
		}

		CategoryListRVAdapter adapter = new CategoryListRVAdapter(items);
		adapter.setOnItemClickListener(this);
		recyclerView.setAdapter(adapter);
	}

	private List<CategoryListItem> addHeader(List<CategoryListItem> subCategory, String header) {
		subCategory.get(0).header = header;
		return subCategory;
	}

	private List<CategoryListItem> toCategoryListItems(List<Integer> nameResIds) {
		return toCategoryListItems(nameResIds, null, CategoryListRVAdapter.DEFAULT);
	}

	private List<CategoryListItem> toCategoryListItems(List<Integer> nameResIds, List<Integer> paramResIds) {
		return toCategoryListItems(nameResIds, paramResIds, CategoryListRVAdapter.DEFAULT);
	}

	private List<CategoryListItem> toCategoryListItems(List<Integer> nameResIds, @CategoryListItemType int type) {
		return toCategoryListItems(nameResIds, null, type);
	}

	private List<CategoryListItem> toCategoryListItems(List<Integer> nameResIds, @Nullable List<Integer> paramResIds,
			@CategoryListItemType int type) {
		if (paramResIds != null && paramResIds.size() != nameResIds.size()) {
			throw new IllegalArgumentException("Sizes of paramResIds and nameResIds parameters do not fit");
		}

		List<CategoryListItem> result = new ArrayList<>();
		for (int i = 0; i < nameResIds.size(); i++) {
			String param = "";
			if (paramResIds != null) {
				param = getString(paramResIds.get(i));
			}
			result.add(new CategoryListItem(nameResIds.get(i), getString(nameResIds.get(i)) + param, type));
		}

		return result;
	}

	private List<CategoryListItem> getObjectItems() {
		List<CategoryListItem> result = new ArrayList<>();
		result.addAll(getObjectGeneralPropertiesItems());
		result.addAll(getObjectPhysicalPropertiesItems());

		return result;
	}

	private List<CategoryListItem> getFunctionItems() {
		List<CategoryListItem> result = new ArrayList<>();
		result.addAll(addHeader(toCategoryListItems(MATH_FUNCTIONS, MATH_PARAMS),
				getString(R.string.formula_editor_functions_maths)));
		result.addAll(addHeader(toCategoryListItems(STRING_FUNCTIONS, STRING_PARAMS),
				getString(R.string.formula_editor_functions_strings)));
		result.addAll(addHeader(toCategoryListItems(LIST_FUNCTIONS, LIST_PARAMS),
				getString(R.string.formula_editor_functions_lists)));

		return result;
	}

	private List<CategoryListItem> getLogicItems() {
		List<CategoryListItem> result = new ArrayList<>();
		result.addAll(addHeader(toCategoryListItems(LOGIC_BOOL), getString(R.string.formula_editor_logic_boolean)));
		result.addAll(addHeader(toCategoryListItems(LOCIG_COMPARISION),
				getString(R.string.formula_editor_logic_comparison)));
		return result;
	}

	private List<CategoryListItem> getSensorItems() {
		List<CategoryListItem> result = new ArrayList<>();
		result.addAll(getDeviceSensorItems());
		result.addAll(getTouchDetectionSensorItems());
		result.addAll(getFaceDetectionSensorItems());
		result.addAll(getDateTimeSensorItems());
		result.addAll(getNxtSensorItems());
		result.addAll(getEv3SensorItems());
		result.addAll(getPhiroSensorItems());
		result.addAll(getArduinoSensorItems());
		result.addAll(getDroneSensorItems());
		result.addAll(getRaspberrySensorItems());
		result.addAll(getNfcItems());
		result.addAll(getCastGamepadSensorItems());
		return result;
	}

	private List<CategoryListItem> getObjectGeneralPropertiesItems() {
		List<Integer> resIds = new ArrayList<>(OBJECT_GENERAL_PROPERTIES);
		resIds.addAll(ProjectManager.getInstance().getCurrentSpritePosition() == 0 ? OBJECT_BACKGROUND : OBJECT_LOOK);
		return addHeader(toCategoryListItems(resIds), getString(R.string.formula_editor_object_general));
	}

	private List<CategoryListItem> getObjectPhysicalPropertiesItems() {
		List<CategoryListItem> result = toCategoryListItems(OBJECT_PHYSICAL_1);
		result.addAll(toCategoryListItems(OBJECT_PHYSICAL_COLLISION, CategoryListRVAdapter.COLLISION));
		result.addAll(toCategoryListItems(OBJECT_PHYSICAL_2));
		return addHeader(result, getString(R.string.formula_editor_object_movement));
	}

	private List<CategoryListItem> getDeviceSensorItems() {
		List<CategoryListItem> deviceSensorItems = new ArrayList<>();
		deviceSensorItems.addAll(toCategoryListItems(SENSORS_DEFAULT));

		SensorHandler sensorHandler = SensorHandler.getInstance(getActivity());
		deviceSensorItems.addAll(sensorHandler.accelerationAvailable() ? toCategoryListItems(SENSORS_ACCELERATION)
				: Collections.<CategoryListItem>emptyList());
		deviceSensorItems.addAll(sensorHandler.inclinationAvailable() ? toCategoryListItems(SENSORS_INCLINATION)
				: Collections.<CategoryListItem>emptyList());
		deviceSensorItems.addAll(sensorHandler.compassAvailable() ? toCategoryListItems(SENSORS_COMPASS)
				: Collections.<CategoryListItem>emptyList());
		deviceSensorItems.addAll(toCategoryListItems(SENSORS_GPS));

		return addHeader(deviceSensorItems, getString(R.string.formula_editor_device_sensors));
	}

	private List<CategoryListItem> getTouchDetectionSensorItems() {
		return addHeader(toCategoryListItems(SENSORS_TOUCH, SENSORS_TOUCH_PARAMS), getString(R.string.formula_editor_device_touch_detection)
		);
	}

	private List<CategoryListItem> getFaceDetectionSensorItems() {
		return CameraManager.getInstance().hasBackCamera() || !CameraManager.getInstance().hasFrontCamera()
				? addHeader(toCategoryListItems(SENSORS_FACE_DETECTION), getString(R.string.formula_editor_device_face_detection))
				: Collections.<CategoryListItem>emptyList();
	}

	private List<CategoryListItem> getDateTimeSensorItems() {
		return addHeader(toCategoryListItems(SENSORS_DATE_TIME), getString(R.string.formula_editor_device_date_and_time));
	}

	private List<CategoryListItem> getNxtSensorItems() {
		return SettingsFragment.isMindstormsNXTSharedPreferenceEnabled(getActivity().getApplicationContext())
				? addHeader(toCategoryListItems(SENSORS_NXT, CategoryListRVAdapter.NXT), getString(R.string.formula_editor_device_lego_nxt))
				: Collections.<CategoryListItem>emptyList();
	}

	private List<CategoryListItem> getEv3SensorItems() {
		return SettingsFragment.isMindstormsEV3SharedPreferenceEnabled(getActivity().getApplicationContext())
				? addHeader(toCategoryListItems(SENSORS_EV3, CategoryListRVAdapter.EV3), getString(R.string.formula_editor_device_lego_ev3))
				: Collections.<CategoryListItem>emptyList();
	}

	private List<CategoryListItem> getPhiroSensorItems() {
		return SettingsFragment.isPhiroSharedPreferenceEnabled(getActivity().getApplicationContext())
				? addHeader(toCategoryListItems(SENSORS_PHIRO), getString(R.string.formula_editor_device_phiro))
				: Collections.<CategoryListItem>emptyList();
	}

	private List<CategoryListItem> getArduinoSensorItems() {
		return SettingsFragment.isArduinoSharedPreferenceEnabled(getActivity().getApplicationContext())
				? addHeader(toCategoryListItems(SENSORS_ARDUINO), getString(R.string.formula_editor_device_arduino))
				: Collections.<CategoryListItem>emptyList();
	}

	private List<CategoryListItem> getDroneSensorItems() {
		return SettingsFragment.isDroneSharedPreferenceEnabled(getActivity().getApplicationContext())
				? addHeader(toCategoryListItems(SENSORS_DRONE), getString(R.string.formula_editor_device_drone))
				: Collections.<CategoryListItem>emptyList();
	}

	private List<CategoryListItem> getRaspberrySensorItems() {
		return SettingsFragment.isRaspiSharedPreferenceEnabled(getActivity().getApplicationContext())
				? addHeader(toCategoryListItems(SENSORS_RASPBERRY, SENSORS_RASPBERRY_PARAMS), getString(R.string.formula_editor_device_raspberry))
				: Collections.<CategoryListItem>emptyList();
	}

	private List<CategoryListItem> getNfcItems() {
		return SettingsFragment.isNfcSharedPreferenceEnabled(getActivity().getApplicationContext())
				? addHeader(toCategoryListItems(SENSORS_NFC), getString(R.string.formula_editor_device_nfc))
				: Collections.<CategoryListItem>emptyList();
	}

	private List<CategoryListItem> getCastGamepadSensorItems() {
		return ProjectManager.getInstance().getCurrentProject().isCastProject()
				? addHeader(toCategoryListItems(SENSORS_CAST_GAMEPAD), getString(R.string.formula_editor_device_cast))
				: Collections.<CategoryListItem>emptyList();
	}
}
