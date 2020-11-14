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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.ui.UiUtils;
import org.catrobat.catroid.ui.recyclerview.fragment.ScriptFragment;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class BrickSpinner<T extends Nameable> implements AdapterView.OnItemSelectedListener {

	private Spinner spinner;
	private BrickSpinnerAdapter adapter;
	private Integer spinnerid;
	private T previousItem;
	private T undoItem;
	boolean wasUndo;

	private OnItemSelectedListener<T> onItemSelectedListener;

	public BrickSpinner(Integer spinnerId, @NonNull View parent, List<Nameable> items) {
		spinnerid = spinnerId;
		adapter = new BrickSpinnerAdapter(parent.getContext(), android.R.layout.simple_spinner_item, items);
		spinner = parent.findViewById(spinnerId);
		spinner.setAdapter(adapter);
		spinner.setSelection(0);
		spinner.setOnItemSelectedListener(this);
		wasUndo = false;
	}

	public void setOnItemSelectedListener(OnItemSelectedListener<T> onItemSelectedListener) {
		this.onItemSelectedListener = onItemSelectedListener;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		Nameable item = adapter.getItem(position);

		if (onItemSelectedListener == null || item == null) {
			return;
		}

		showUndo(view, false);

		if (item.getClass().equals(NewOption.class)) {
			return;
		}

		onSelectionChanged(view, item);

		if (item.getClass().equals(StringOption.class)) {
			onItemSelectedListener.onStringOptionSelected(spinnerid, item.getName());
			return;
		}
		onItemSelectedListener.onItemSelected(spinnerid, (T) item);
	}

	private void onSelectionChanged(View view, Nameable item) {
		if (previousItem != null && previousItem != item && !wasUndo) {
			showUndo(view, true);
			undoItem = previousItem;
		}
		previousItem = (T) item;
		wasUndo = false;
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	public void add(@NonNull T item) {
		adapter.add(item);
	}

	public List<T> getItems() {
		return (List<T>) adapter.getItems();
	}

	public void setSelection(int position) {
		spinner.setSelection(position);
	}

	public void setSelection(@Nullable String itemName) {
		spinner.setOnItemSelectedListener(null);

		int position = adapter.getPosition(itemName);
		position = consolidateSpinnerSelection(position);

		spinner.setSelection(position);
		onSelectionSet(adapter.getItem(position));
		spinner.setOnItemSelectedListener(this);
	}

	public void setSelection(@Nullable T item) {
		spinner.setOnItemSelectedListener(null);

		int position = adapter.getPosition(item);
		position = consolidateSpinnerSelection(position);

		if (position < adapter.getCount()) {
			spinner.setSelection(position);
			onSelectionSet(adapter.getItem(position));
			spinner.setOnItemSelectedListener(this);
		}
	}

	public void undoSelection() {
		setSelection(undoItem);
		wasUndo = true;
	}

	private int consolidateSpinnerSelection(int position) {
		if (position == -1) {
			if (adapter.containsNewOption()) {
				position = adapter.getCount() > 1 ? 1 : 0;
			} else {
				position = 0;
			}
		}
		return position;
	}

	private void onSelectionSet(Nameable selectedItem) {
		if (onItemSelectedListener != null) {
			if (selectedItem.getClass().equals(NewOption.class)) {
				onItemSelectedListener.onItemSelected(spinnerid, null);
				return;
			}
			if (selectedItem.getClass().equals(StringOption.class)) {
				onItemSelectedListener.onStringOptionSelected(spinnerid, selectedItem.getName());
				return;
			}
			onItemSelectedListener.onItemSelected(spinnerid, (T) selectedItem);
		}
	}

	private void showUndo(View view, boolean visible) {
		FragmentActivity activity = null;
		if (view != null) {
			activity = UiUtils.getActivityFromView(view);
		}
		if (activity == null) {
			return;
		}

		Fragment currentFragment = activity.getSupportFragmentManager().findFragmentById(R.id.fragment_container);
		if (currentFragment instanceof ScriptFragment && activity instanceof SpriteActivity) {
			((ScriptFragment) currentFragment).setCurrentSpinner(this);
			((SpriteActivity) activity).showUndoSpinnerSelection(visible);
		}
	}

	public boolean isNewOptionSelected() {
		return spinner.getSelectedItemId() == 0;
	}

	@VisibleForTesting
	public class BrickSpinnerAdapter extends ArrayAdapter<Nameable> {

		private List<Nameable> items;

		BrickSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<Nameable> items) {
			super(context, resource, items);
			this.items = items;
		}

		@Override
		public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext())
						.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
			}

			final Nameable item = getItem(position);
			((TextView) convertView).setText(item.getName());
			convertView.setOnTouchListener(new View.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getActionIndex() == MotionEvent.ACTION_DOWN && item.getClass().equals(NewOption.class)) {
						onItemSelectedListener.onNewOptionSelected(spinnerid);
					}
					return false;
				}
			});
			return convertView;
		}

		@NonNull
		@Override
		public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext())
						.inflate(android.R.layout.simple_spinner_item, parent, false);
			}

			Nameable item = getItem(position);
			((TextView) convertView).setText(item.getName());
			return convertView;
		}

		int getPosition(@Nullable String itemName) {
			for (int position = 0; position < getCount(); position++) {
				if (getItem(position).getName().equals(itemName)) {
					return position;
				}
			}

			return -1;
		}

		List<Nameable> getItems() {
			return items;
		}

		boolean containsNewOption() {
			for (Nameable item : items) {
				if (item instanceof NewOption) {
					return true;
				}
			}
			return false;
		}
	}

	public interface OnItemSelectedListener<T> {

		void onNewOptionSelected(Integer spinnerId);

		void onStringOptionSelected(Integer spinnerId, String string);

		void onItemSelected(Integer spinnerId, @Nullable T item);
	}
}
