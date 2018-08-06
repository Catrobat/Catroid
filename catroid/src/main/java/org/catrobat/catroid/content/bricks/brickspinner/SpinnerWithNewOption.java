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

package org.catrobat.catroid.content.bricks.brickspinner;

import android.support.annotation.IdRes;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import org.catrobat.catroid.common.Nameable;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SpinnerWithNewOption<T extends Nameable> implements AdapterView.OnItemSelectedListener,
		SpinnerAdapterWithNewOption.OnNewOptionInDropDownClickListener {

	private List<T> items = new ArrayList<>();

	private SpinnerSelectionListener<T> spinnerSelectionListener;

	private Spinner spinner;
	private SpinnerAdapterWithNewOption spinnerAdapter;

	public SpinnerWithNewOption(@IdRes int spinnerId, @Nonnull View parent, List<T> items,
			SpinnerSelectionListener<T> spinnerSelectionListener) {

		this.items.addAll(items);
		this.spinnerSelectionListener = spinnerSelectionListener;

		List<String> names = new ArrayList<>();
		for (T item : items) {
			names.add(item.getName());
		}

		spinner = parent.findViewById(spinnerId);
		spinnerAdapter = new SpinnerAdapterWithNewOption(parent.getContext(), names);

		spinnerAdapter.setOnDropDownItemClickListener(this);

		spinner.setAdapter(spinnerAdapter);
		spinner.setSelection(0);

		spinner.setOnItemSelectedListener(this);
	}

	public void add(T item) {
		items.add(item);
		spinnerAdapter.add(item.getName());
		spinner.setSelection(spinnerAdapter.getPosition(item.getName()));
	}

	public void setSelection(@Nullable T item) {
		spinner.setSelection(spinnerAdapter.getPosition(item != null ? item.getName() : null));
	}

	@Override
	public boolean onNewOptionInDropDownClicked(View v) {
		return spinnerSelectionListener.onNewOptionClicked();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if (position != 0) {
			spinnerSelectionListener.onItemSelected(getItem(spinnerAdapter.getItem(position)));
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	@Nullable
	private T getItem(@Nullable String name) {
		if (name == null) {
			return null;
		}

		for (T item : items) {
			if (item.getName().equals(name)) {
				return item;
			}
		}

		return null;
	}

	public interface SpinnerSelectionListener<T extends Nameable> {

		boolean onNewOptionClicked();

		void onItemSelected(T item);
	}
}
