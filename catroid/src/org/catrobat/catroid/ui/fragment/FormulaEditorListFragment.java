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
import org.catrobat.catroid.formulaeditor.FormulaEditorEditText;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;

public class FormulaEditorListFragment extends SherlockListFragment implements Dialog.OnKeyListener {

	public static final String OBJECT_TAG = "objectFragment";
	public static final String MATH_TAG = "mathFragment";
	public static final String LOGIC_TAG = "logicFragment";
	public static final String SENSOR_TAG = "sensorFragment";

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
			R.string.formula_editor_sensor_z_orientation, R.string.formula_editor_sensor_x_orientation,
			R.string.formula_editor_sensor_y_orientation };

	private final String tag;
	private String[] items;
	private FormulaEditorEditText formulaEditorEditText;
	private String actionBarTitle;
	private int[] itemsIds;

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		formulaEditorEditText.handleKeyEvent(itemsIds[position], "");
		KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK);
		onKey(null, keyEvent.getKeyCode(), keyEvent);
	}

	public FormulaEditorListFragment(FormulaEditorEditText formulaEditorEditText, String actionBarTitle,
			String fragmentTag) {
		this.formulaEditorEditText = formulaEditorEditText;
		this.actionBarTitle = actionBarTitle;
		tag = fragmentTag;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

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
		super.onPrepareOptionsMenu(menu);
		menu.findItem(R.id.delete).setVisible(false);
		menu.findItem(R.id.copy).setVisible(false);
		menu.findItem(R.id.cut).setVisible(false);
		menu.findItem(R.id.show_details).setVisible(false);
		menu.findItem(R.id.insert_below).setVisible(false);
		menu.findItem(R.id.move).setVisible(false);
		menu.findItem(R.id.rename).setVisible(false);
		menu.findItem(R.id.show_details).setVisible(false);
		menu.findItem(R.id.settings).setVisible(false);

		getSherlockActivity().getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSherlockActivity().getSupportActionBar().setTitle(actionBarTitle);
		getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(false);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_formula_editor_list, container, false);
		return fragmentView;
	}

	public void showFragment(Context context) {
		FragmentActivity activity = (FragmentActivity) context;
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
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				Log.i("info", "KEYCODE_BACK pressed in FE-ListFragment!");
				FragmentTransaction fragTransaction = getSherlockActivity().getSupportFragmentManager()
						.beginTransaction();
				fragTransaction.hide(this);
				fragTransaction.show(getSherlockActivity().getSupportFragmentManager().findFragmentByTag(
						FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG));
				fragTransaction.commit();
				return true;
			default:
				break;
		}
		return false;
	}

}
