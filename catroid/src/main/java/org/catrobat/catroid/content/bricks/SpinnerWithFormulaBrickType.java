/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SpinnerWithFormulaBrickType extends FormulaBrick {
	private HashMap<Integer, List<Integer>> spinnersToPossibleSelections = new HashMap<>();
	private HashMap<Integer, Integer> spinnersToCurrentSelection = new HashMap<>();

	public void addSpinner(int spinnerID, List<Integer> possibleSelections) {
		spinnersToPossibleSelections.put(spinnerID, possibleSelections);
		spinnersToCurrentSelection.put(spinnerID, 0);
	}

	public int getSpinnerSelection(int spinnerID) {
		Integer selectedIndex = spinnersToCurrentSelection.get(spinnerID);
		List<Integer> possibleSelections = spinnersToPossibleSelections.get(spinnerID);
		if (possibleSelections != null && selectedIndex != null) {
			return possibleSelections.get(selectedIndex);
		}
		return -1;
	}

	public View getView(Context context) {
		super.getView(context);

		for (Map.Entry<Integer, List<Integer>> entry : spinnersToPossibleSelections.entrySet()) {
			Integer spinnerID = entry.getKey();

			Spinner spinner = view.findViewById(spinnerID);

			setupSpinner(spinner, createArrayAdapter(context, spinnerID), createItemSelectedListener(spinnerID));
			Integer currentSelection = spinnersToCurrentSelection.get(spinnerID);
			if (currentSelection != null) {
				spinner.setSelection(currentSelection);
			}
		}
		return view;
	}

	private void setupSpinner(Spinner spinner, ArrayAdapter<String> adapter, AdapterView.OnItemSelectedListener onItemSelectedListener) {
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(onItemSelectedListener);
	}

	private ArrayAdapter<String> createArrayAdapter(Context context, int spinnerID) {
		List<Integer> spinnerValues = spinnersToPossibleSelections.get(spinnerID);
		List<String> spinnerStrings = new ArrayList<>();
		if (spinnerValues != null) {
			for (int value : spinnerValues) {
				spinnerStrings.add(context.getString(value));
			}
		}

		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, spinnerStrings);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		return spinnerAdapter;
	}

	private AdapterView.OnItemSelectedListener createItemSelectedListener(int spinnerID) {
		return new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view1, int position, long l) {
				spinnersToCurrentSelection.put(spinnerID, position);
			}
			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
			}
		};
	}
}
