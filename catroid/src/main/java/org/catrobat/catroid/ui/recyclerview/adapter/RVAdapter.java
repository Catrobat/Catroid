/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.ui.recyclerview.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.recyclerview.adapter.draganddrop.TouchHelperAdapterInterface;
import org.catrobat.catroid.ui.recyclerview.adapter.multiselection.MultiSelectionManager;
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableViewHolder;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.IntDef;
import androidx.recyclerview.widget.RecyclerView;

public abstract class RVAdapter<T> extends RecyclerView.Adapter<CheckableViewHolder> implements TouchHelperAdapterInterface {

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({SINGLE, PAIRS, MULTIPLE})
	@interface SelectionType {
	}

	public static final int SINGLE = 0;
	public static final int PAIRS = 1;
	public static final int MULTIPLE = 2;

	protected List<T> items;
	public boolean showCheckBoxes = false;
	public boolean showRipples = true;
	public boolean showSettings = true;

	@SelectionType
	public int selectionMode = MULTIPLE;

	MultiSelectionManager selectionManager = new MultiSelectionManager();
	private SelectionListener selectionListener;
	private OnItemClickListener<T> onItemClickListener;

	protected RVAdapter(List<T> items) {
		this.items = items;
	}

	public void setSelectionListener(SelectionListener selectionListener) {
		this.selectionListener = selectionListener;
	}

	public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	@Override
	public CheckableViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_with_checkbox, parent, false);
		return new CheckableViewHolder(view);
	}

	@Override
	public void onBindViewHolder(CheckableViewHolder holder, int position) {
		T item = items.get(position);

		holder.checkBox.setOnClickListener(v -> onCheckBoxClick(holder.getAdapterPosition()));
		holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(item, selectionManager));

		if (holder.settings != null) {
			holder.settings.setOnClickListener(v -> onItemClickListener.onSettingsClick(item, v));
		}

		holder.checkBox.setVisibility(showCheckBoxes ? View.VISIBLE : View.GONE);
		holder.checkBox.setChecked(selectionManager.isPositionSelected(position));

		ImageView ripples = holder.itemView.findViewById(R.id.ic_ripples);
		if (ripples != null && showRipples) {
			ripples.setVisibility(View.VISIBLE);
			holder.itemView.setOnLongClickListener(v -> {
				onItemClickListener.onItemLongClick(item, holder);
				return true;
			});
		} else if (ripples != null && !showRipples) {
			ripples.setVisibility(View.GONE);
			holder.itemView.setOnLongClickListener(v -> true);
		}

		ImageButton settings = holder.itemView.findViewById(R.id.settings_button);
		if (settings != null && showSettings) {
			settings.setVisibility(View.VISIBLE);
		} else if (settings != null && !showSettings) {
			settings.setVisibility(View.GONE);
		}
	}

	protected void onCheckBoxClick(int position) {
		switch (selectionMode) {
			case SINGLE:
				boolean currentState = selectionManager.isPositionSelected(position);

				for (int i : selectionManager.getSelectedPositions()) {
					selectionManager.setSelectionTo(false, i);
					notifyItemChanged(i);
				}

				selectionManager.setSelectionTo(!currentState, position);
				notifyItemChanged(position);
				break;
			case PAIRS:
				if (selectionManager.getSelectedPositions().size() < 2) {
					selectionManager.toggleSelection(position);
				} else {
					selectionManager.setSelectionTo(false, position);
					notifyItemChanged(position);
				}
				break;
			case MULTIPLE:
			default:
				selectionManager.toggleSelection(position);
				break;
		}
		selectionListener.onSelectionChanged(selectionManager.getSelectedPositions().size());
	}

	public boolean add(int i, T item) {
		items.add(i, item);
		notifyItemInserted(items.indexOf(item));
		return true;
	}

	public boolean add(T item) {
		if (items.add(item)) {
			notifyItemInserted(items.indexOf(item));
			return true;
		}
		return false;
	}

	public boolean remove(T item) {
		if (items.remove(item)) {
			notifyItemRemoved(items.indexOf(item));
			return true;
		}
		return false;
	}

	public List<T> getItems() {
		return items;
	}

	public void setItems(List<T> items) {
		this.items.clear();
		this.items.addAll(items);
		notifyDataSetChanged();
	}

	@Override
	public boolean onItemMove(int sourcePosition, int targetPosition) {
		if (Math.abs(sourcePosition - targetPosition) <= 1) {
			Collections.swap(items, sourcePosition, targetPosition);
		} else {
			T movedItem = items.get(sourcePosition);
			items.remove(movedItem);
			items.add(targetPosition, movedItem);
		}
		notifyItemMoved(sourcePosition, targetPosition);
		selectionManager.updateSelection(sourcePosition, targetPosition);
		return true;
	}

	public List<T> getSelectedItems() {
		List<T> selectedItems = new ArrayList<>();
		for (int position : selectionManager.getSelectedPositions()) {
			selectedItems.add(items.get(position));
		}
		return selectedItems;
	}

	public boolean setSelection(T item, boolean selection) {
		if (items.indexOf(item) == -1) {
			return false;
		}
		selectionManager.setSelectionTo(selection, items.indexOf(item));
		return true;
	}

	public boolean toggleSelection(T item) {
		if (items.indexOf(item) == -1) {
			return false;
		}
		selectionManager.toggleSelection(items.indexOf(item));
		notifyItemChanged(items.indexOf(item));
		return true;
	}

	public void toggleSelection() {
		if (selectionManager.getSelectedPositions().size() == getSelectableItemCount()) {
			clearSelection();
		} else {
			selectAll();
		}
		selectionListener.onSelectionChanged(selectionManager.getSelectedPositions().size());
	}

	public void selectAll() {
		for (T item : items) {
			selectionManager.setSelectionTo(true, items.indexOf(item));
		}
		notifyDataSetChanged();
	}

	public void clearSelection() {
		selectionManager.clearSelection();
		notifyDataSetChanged();
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	public int getSelectableItemCount() {
		return getItemCount();
	}

	public int getSelectedItemCount() {
		return selectionManager.getSelectedPositions().size();
	}

	public interface SelectionListener {

		void onSelectionChanged(int selectedItemCnt);
	}

	public interface OnItemClickListener<T> {

		void onItemClick(T item, MultiSelectionManager selectionManager);

		void onItemLongClick(T item, CheckableViewHolder holder);

		void onSettingsClick(T item, View view);
	}
}
