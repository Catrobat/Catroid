/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.ui.fragment;

import org.catrobat.catroid.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;

public class FormulaEditorListFragment extends SherlockListFragment implements Dialog.OnKeyListener {

	public static final String OBJECT_TAG = "objectFragment";
	public static final String MATH_TAG = "mathFragment";
	public static final String LOGIC_TAG = "logicFragment";
	public static final String SENSOR_TAG = "sensorFragment";

	public static final String ACTION_BAR_TITLE_BUNDLE_ARGUMENT = "actionBarTitle";
	public static final String FRAGMENT_TAG_BUNDLE_ARGUMENT = "fragmentTag";

	public static final String[] TAGS = { OBJECT_TAG, MATH_TAG, LOGIC_TAG, SENSOR_TAG };

	private static final int[] OBJECT_ITEMS = { R.string.formula_editor_look_x, R.string.formula_editor_look_y,
			R.string.formula_editor_look_ghosteffect, R.string.formula_editor_look_brightness,
			R.string.formula_editor_look_size, R.string.formula_editor_look_rotation,
			R.string.formula_editor_look_layer };

	private static final int[] LOGIC_ITEMS = { R.string.formula_editor_logic_equal,
			R.string.formula_editor_logic_notequal, R.string.formula_editor_logic_lesserthan,
			R.string.formula_editor_logic_leserequal, R.string.formula_editor_logic_greaterthan,
			R.string.formula_editor_logic_greaterequal, R.string.formula_editor_logic_and,
			R.string.formula_editor_logic_or, R.string.formula_editor_logic_not };

	private static final int[] MATH_ITEMS = { R.string.formula_editor_function_sin,
			R.string.formula_editor_function_cos, R.string.formula_editor_function_tan,
			R.string.formula_editor_function_ln, R.string.formula_editor_function_log,
			R.string.formula_editor_function_pi, R.string.formula_editor_function_sqrt,
			R.string.formula_editor_function_rand, R.string.formula_editor_function_abs,
			R.string.formula_editor_function_round };

	private final int[] SENSOR_ITEMS = { R.string.formula_editor_sensor_x_acceleration,
			R.string.formula_editor_sensor_y_acceleration, R.string.formula_editor_sensor_z_acceleration,
			R.string.formula_editor_sensor_compass_direction, R.string.formula_editor_sensor_x_inclination,
			R.string.formula_editor_sensor_y_inclination };

	private String tag;
	private String[] items;
	private String actionBarTitle;
	private int[] itemsIds;

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		FormulaEditorFragment formulaEditor = (FormulaEditorFragment) getSherlockActivity().getSupportFragmentManager()
				.findFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		if (formulaEditor != null) {
			formulaEditor.addResourceToActiveFormula(itemsIds[position]);
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
		this.tag = getArguments().getString(FRAGMENT_TAG_BUNDLE_ARGUMENT);

		itemsIds = new int[] {};

		if (tag == OBJECT_TAG) {
			itemsIds = OBJECT_ITEMS;
		} else if (tag == MATH_TAG) {
			itemsIds = MATH_ITEMS;
		} else if (tag == LOGIC_TAG) {
			itemsIds = LOGIC_ITEMS;
		} else if (tag == SENSOR_TAG) {
			itemsIds = SENSOR_ITEMS;
		}

		items = new String[itemsIds.length];
		int index = 0;
		for (Integer item : itemsIds) {
			items[index] = getString(item);
			index++;
		}

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
				R.layout.fragment_formula_editor_list_item, items);
		setListAdapter(arrayAdapter);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		for (int index = 0; index < menu.size(); index++) {
			menu.getItem(index).setVisible(false);
		}

		getSherlockActivity().getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSherlockActivity().getSupportActionBar().setTitle(actionBarTitle);
		getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(false);

		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_formula_editor_list, container, false);
		return fragmentView;
	}

	public void showFragment(Context context) {
		SherlockFragmentActivity activity = (SherlockFragmentActivity) context;
		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		FragmentTransaction fragTransaction = fragmentManager.beginTransaction();

		Fragment formulaEditorFragment = fragmentManager
				.findFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		fragTransaction.hide(formulaEditorFragment);
		fragTransaction.show(this);
		fragTransaction.commit();
	}

	@Override
	public boolean onKey(DialogInterface d, int keyCode, KeyEvent event) {
		Log.i("info", "onKey() in FE-ListFragment! keyCode: " + keyCode);
		boolean returnValue = false;
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				Log.i("info", "KEYCODE_BACK pressed in FE-ListFragment!");
				FragmentTransaction fragTransaction = getSherlockActivity().getSupportFragmentManager()
						.beginTransaction();
				fragTransaction.hide(this);
				fragTransaction.show(getSherlockActivity().getSupportFragmentManager().findFragmentByTag(
						FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG));
				fragTransaction.commit();
				returnValue = true;
				break;
		}
		return returnValue;
	}

}
