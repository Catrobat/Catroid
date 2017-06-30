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

package org.catrobat.catroid.utils;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginSimpleBrick;
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicElseSimpleBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndSimpleBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicBeginSimpleBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicEndBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicEndSimpleBrick;
import org.catrobat.catroid.content.bricks.RepeatUntilBrick;
import org.catrobat.catroid.content.bricks.RepeatUntilSimpleBrick;
import org.catrobat.catroid.content.bricks.WaitUntilBrick;
import org.catrobat.catroid.content.bricks.WaitUntilSimpleBrick;
import org.catrobat.catroid.formulaeditor.InternToExternGenerator;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.SettingsActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SimpleBrickUtil {

	public static boolean isInFormulaEditor = false;
	public static boolean uploadProject = false;
	public static String latestData;

	private SimpleBrickUtil() {
	}

	public static boolean isUserVariable(String variable) {
		Scene currentScene = ProjectManager.getInstance().getCurrentScene();
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();

		return currentScene.getDataContainer().existProjectVariableWithName(variable) || currentScene.getDataContainer()
				.existSpriteVariableByName(variable, currentSprite);
	}

	public static String getSensorByString(Context context, String sensor) {
		HashMap<String, Integer> test = InternToExternGenerator.getInternToExternHashMap();

		for (Map.Entry<String, Integer> entry : test.entrySet()) {
			if (sensor.equals(context.getString(entry.getValue()))) {
				return entry.getKey();
			}
		}
		return "";
	}

	public static ArrayAdapter<String> createArrayAdapterOperator(Context context) {
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);

		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		arrayAdapter.addAll(
				context.getString(R.string.formula_editor_logic_equal),
				context.getString(R.string.formula_editor_logic_notequal),
				context.getString(R.string.formula_editor_logic_lesserthan),
				context.getString(R.string.formula_editor_logic_leserequal),
				context.getString(R.string.formula_editor_logic_greaterthan),
				context.getString(R.string.formula_editor_logic_greaterequal)
		);

		return arrayAdapter;
	}

	private static ArrayList<String> createArrayAdapterUserData() {
		Scene currentScene = ProjectManager.getInstance().getCurrentScene();
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();

		List<UserVariable> projectVariables = currentScene.getDataContainer().getProjectVariables();
		List<UserList> projectLists = currentScene.getDataContainer().getProjectLists();

		List<UserVariable> userVariables = currentScene.getDataContainer().getVariableListForSprite(currentSprite);
		List<UserList> userList = currentScene.getDataContainer().getUserListListForSprite(currentSprite);

		ArrayList<String> arrayList = new ArrayList<>();

		for (UserVariable user : projectVariables) {
			arrayList.add(user.getName());
		}
		for (UserList user : projectLists) {
			arrayList.add(user.getName());
		}
		for (UserVariable user : userVariables) {
			arrayList.add(user.getName());
		}
		for (UserList user : userList) {
			arrayList.add(user.getName());
		}

		arrayList.add(CatroidApplication.getAppContext().getString(R.string.simple_brick_new_data_dialog));

		return arrayList;
	}

	public static String convertXML(Object source) {
		String sourceName = source.getClass().getSimpleName();

		if (sourceName.equals(IfLogicBeginSimpleBrick.class.getSimpleName())) {
			return IfLogicBeginBrick.class.getSimpleName();
		} else if (sourceName.equals(IfLogicElseSimpleBrick.class.getSimpleName())) {
			return IfLogicElseBrick.class.getSimpleName();
		} else if (sourceName.equals(IfLogicEndSimpleBrick.class.getSimpleName())) {
			return IfLogicEndBrick.class.getSimpleName();
		} else if (sourceName.equals(IfThenLogicBeginSimpleBrick.class.getSimpleName())) {
			return IfThenLogicBeginBrick.class.getSimpleName();
		} else if (sourceName.equals(IfThenLogicEndSimpleBrick.class.getSimpleName())) {
			return IfThenLogicEndBrick.class.getSimpleName();
		} else if (sourceName.equals(WaitUntilSimpleBrick.class.getSimpleName())) {
			return WaitUntilBrick.class.getSimpleName();
		} else if (sourceName.equals(RepeatUntilSimpleBrick.class.getSimpleName())) {
			return RepeatUntilBrick.class.getSimpleName();
		}

		return source.getClass().getSimpleName();
	}

	public static class SpinnerAdapterWrapper implements SpinnerAdapter {
		protected Context context;
		protected ArrayAdapter<String> spinnerAdapter;

		public SpinnerAdapterWrapper(Context context, ArrayAdapter<String> spinnerAdapter) {
			this.context = context;
			this.spinnerAdapter = spinnerAdapter;
		}

		@Override
		public void registerDataSetObserver(DataSetObserver paramDataSetObserver) {
			spinnerAdapter.registerDataSetObserver(paramDataSetObserver);
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver paramDataSetObserver) {
			spinnerAdapter.unregisterDataSetObserver(paramDataSetObserver);
		}

		@Override
		public int getCount() {
			return spinnerAdapter.getCount();
		}

		@Override
		public Object getItem(int paramInt) {
			return spinnerAdapter.getItem(paramInt);
		}

		@Override
		public long getItemId(int paramInt) {
			return spinnerAdapter.getItemId(paramInt);
		}

		@Override
		public boolean hasStableIds() {
			return spinnerAdapter.hasStableIds();
		}

		@Override
		public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
			return spinnerAdapter.getView(paramInt, paramView, paramViewGroup);
		}

		@Override
		public int getItemViewType(int paramInt) {
			return spinnerAdapter.getItemViewType(paramInt);
		}

		@Override
		public int getViewTypeCount() {
			return spinnerAdapter.getViewTypeCount();
		}

		@Override
		public boolean isEmpty() {
			return spinnerAdapter.isEmpty();
		}

		@Override
		public View getDropDownView(int paramInt, View paramView, ViewGroup paramViewGroup) {
			View dropDownView = spinnerAdapter.getDropDownView(paramInt, paramView, paramViewGroup);

			dropDownView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
					return false;
				}
			});

			TextSizeUtil.enlargeTextView((TextView) dropDownView);

			return dropDownView;
		}
	}

	public static class MyExpandableListAdapter extends BaseExpandableListAdapter {
		protected Context context;

		private ArrayList<String> groups = new ArrayList<>();
		private ArrayList<ArrayList<String>> child = new ArrayList<>();

		public MyExpandableListAdapter(Context context) {
			this.context = context;

			groups.add(context.getString(R.string.simple_brick_group_object));
			groups.add(context.getString(R.string.simple_brick_group_device));
			groups.add(context.getString(R.string.simple_brick_group_data));
			groups.add(context.getString(R.string.simple_brick_group_number));

			ArrayList<String> objectList = new ArrayList<>();
			objectList.add(context.getString(R.string.formula_editor_object_transparency));
			objectList.add(context.getString(R.string.formula_editor_object_brightness));
			objectList.add(context.getString(R.string.formula_editor_object_color));
			objectList.add(context.getString(R.string.formula_editor_object_x));
			objectList.add(context.getString(R.string.formula_editor_object_y));
			objectList.add(context.getString(R.string.formula_editor_object_size));
			objectList.add(context.getString(R.string.formula_editor_object_rotation));
			objectList.add(context.getString(R.string.formula_editor_object_layer));
			objectList.add(context.getString(R.string.formula_editor_object_distance_to));
			objectList.add(context.getString(R.string.formula_editor_object_x_velocity));
			objectList.add(context.getString(R.string.formula_editor_object_y_velocity));
			objectList.add(context.getString(R.string.formula_editor_object_angular_velocity));
			objectList.add(context.getString(R.string.formula_editor_object_look_name));
			objectList.add(context.getString(R.string.formula_editor_object_look_number));
			objectList.add(context.getString(R.string.formula_editor_object_background_name));
			objectList.add(context.getString(R.string.formula_editor_object_background_number));

			ArrayList<String> deviceList = new ArrayList<>();
			deviceList.add(context.getString(R.string.formula_editor_sensor_loudness));
			deviceList.add(context.getString(R.string.formula_editor_sensor_x_acceleration));
			deviceList.add(context.getString(R.string.formula_editor_sensor_y_acceleration));
			deviceList.add(context.getString(R.string.formula_editor_sensor_z_acceleration));
			deviceList.add(context.getString(R.string.formula_editor_sensor_x_inclination));
			deviceList.add(context.getString(R.string.formula_editor_sensor_y_inclination));

			deviceList.add(context.getString(R.string.formula_editor_sensor_face_detected));
			deviceList.add(context.getString(R.string.formula_editor_sensor_face_size));
			deviceList.add(context.getString(R.string.formula_editor_sensor_face_x_position));
			deviceList.add(context.getString(R.string.formula_editor_sensor_face_y_position));
			deviceList.add(context.getString(R.string.formula_editor_function_index_of_last_finger));
			deviceList.add(context.getString(R.string.formula_editor_function_finger_x));
			deviceList.add(context.getString(R.string.formula_editor_function_finger_y));
			deviceList.add(context.getString(R.string.formula_editor_function_is_finger_touching));

			deviceList.add(context.getString(R.string.formula_editor_sensor_compass_direction));
			deviceList.add(context.getString(R.string.formula_editor_sensor_latitude));
			deviceList.add(context.getString(R.string.formula_editor_sensor_longitude));
			deviceList.add(context.getString(R.string.formula_editor_sensor_location_accuracy));
			deviceList.add(context.getString(R.string.formula_editor_sensor_altitude));
			deviceList.add(context.getString(R.string.formula_editor_sensor_date_day));
			deviceList.add(context.getString(R.string.formula_editor_sensor_date_weekday));
			deviceList.add(context.getString(R.string.formula_editor_sensor_date_month));
			deviceList.add(context.getString(R.string.formula_editor_sensor_date_year));
			deviceList.add(context.getString(R.string.formula_editor_sensor_time_second));
			deviceList.add(context.getString(R.string.formula_editor_sensor_time_minute));
			deviceList.add(context.getString(R.string.formula_editor_sensor_time_hour));

			deviceList.add(context.getString(R.string.formula_editor_function_collision));
			deviceList.add(context.getString(R.string.formula_editor_function_collides_with_edge));
			deviceList.add(context.getString(R.string.formula_editor_function_touched));

			if (SettingsActivity.isMindstormsNXTSharedPreferenceEnabled(context)) {
				deviceList.add(context.getString(R.string.formula_editor_sensor_lego_nxt_1));
				deviceList.add(context.getString(R.string.formula_editor_sensor_lego_nxt_2));
				deviceList.add(context.getString(R.string.formula_editor_sensor_lego_nxt_3));
				deviceList.add(context.getString(R.string.formula_editor_sensor_lego_nxt_4));
			}

			if (SettingsActivity.isMindstormsEV3SharedPreferenceEnabled(context)) {
				deviceList.add(context.getString(R.string.formula_editor_sensor_lego_ev3_1));
				deviceList.add(context.getString(R.string.formula_editor_sensor_lego_ev3_2));
				deviceList.add(context.getString(R.string.formula_editor_sensor_lego_ev3_3));
				deviceList.add(context.getString(R.string.formula_editor_sensor_lego_ev3_4));
			}

			if (SettingsActivity.isMindstormsEV3SharedPreferenceEnabled(context)) {
				deviceList.add(context.getString(R.string.formula_editor_phiro_sensor_front_left));
				deviceList.add(context.getString(R.string.formula_editor_phiro_sensor_front_right));
				deviceList.add(context.getString(R.string.formula_editor_phiro_sensor_side_left));
				deviceList.add(context.getString(R.string.formula_editor_phiro_sensor_side_right));
				deviceList.add(context.getString(R.string.formula_editor_phiro_sensor_bottom_left));
				deviceList.add(context.getString(R.string.formula_editor_phiro_sensor_bottom_right));
			}

			if (SettingsActivity.isDroneSharedPreferenceEnabled(context)) {
				deviceList.add(context.getString(R.string.formula_editor_sensor_drone_battery_status));
				deviceList.add(context.getString(R.string.formula_editor_sensor_drone_emergency_state));
				deviceList.add(context.getString(R.string.formula_editor_sensor_drone_flying));
				deviceList.add(context.getString(R.string.formula_editor_sensor_drone_initialized));
				deviceList.add(context.getString(R.string.formula_editor_sensor_drone_usb_active));
				deviceList.add(context.getString(R.string.formula_editor_sensor_drone_usb_remaining_time));
				deviceList.add(context.getString(R.string.formula_editor_sensor_drone_camera_ready));
				deviceList.add(context.getString(R.string.formula_editor_sensor_drone_record_ready));
				deviceList.add(context.getString(R.string.formula_editor_sensor_drone_recording));
				deviceList.add(context.getString(R.string.formula_editor_sensor_drone_num_frames));
			}

			if (SettingsActivity.isNfcSharedPreferenceEnabled(context)) {
				deviceList.add(context.getString(R.string.formula_editor_nfc_tag_id));
				deviceList.add(context.getString(R.string.formula_editor_nfc_tag_message));
			}

			ArrayList<String> numberList = new ArrayList<>();
			numberList.add("");

			child.add(objectList);
			child.add(deviceList);
			child.add(createArrayAdapterUserData());
			child.add(numberList);
		}

		public Object getChild(int groupPosition, int childPosition) {
			return child.get(groupPosition).get(childPosition);
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public int getChildrenCount(int groupPosition) {
			return child.get(groupPosition).size();
		}

		public TextView getGenericView() {
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);

			TextView textView = new TextView(context);
			textView.setLayoutParams(lp);

			textView.setPadding(100, 5, 0, 30);

			return textView;
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			View inflatedView = View.inflate(context.getApplicationContext(),
					R.layout.child, null);
			inflatedView.setPadding(50, 0, 0, 10);
			TextView textView = (TextView) inflatedView.findViewById(R.id.textView1);
			textView.setTextSize(20);
			textView.setText(getChild(groupPosition, childPosition).toString());

			TextSizeUtil.enlargeTextView(textView);

			return inflatedView;
		}

		public Object getGroup(int groupPosition) {
			return groups.get(groupPosition);
		}

		public int getGroupCount() {
			return groups.size();
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView textView = getGenericView();
			textView.setTextSize(25);
			textView.setText(getGroup(groupPosition).toString());

			TextSizeUtil.enlargeTextView(textView);

			return textView;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		public boolean hasStableIds() {
			return true;
		}
	}
}
