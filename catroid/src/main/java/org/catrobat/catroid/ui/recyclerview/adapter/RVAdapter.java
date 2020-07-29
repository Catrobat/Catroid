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

package org.catrobat.catroid.ui.recyclerview.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.recyclerview.adapter.draganddrop.TouchHelperAdapterInterface;
import org.catrobat.catroid.ui.recyclerview.adapter.multiselection.MultiSelectionManager;
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableVH;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.IntDef;
import androidx.recyclerview.widget.RecyclerView;

public abstract class RVAdapter<T> extends RecyclerView.Adapter<CheckableVH> implements TouchHelperAdapterInterface {

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({SINGLE, PAIRS, MULTIPLE})
	@interface SelectionType {
	}

	public static final int SINGLE = 0;
	public static final int PAIRS = 1;
	public static final int MULTIPLE = 2;

	List<T> items;
	public boolean showCheckBoxes = false;

	@SelectionType
	public int selectionMode = MULTIPLE;

	MultiSelectionManager selectionManager = new MultiSelectionManager();
	private SelectionListener selectionListener;
	private OnItemClickListener<T> onItemClickListener;

	RVAdapter(List<T> items) {
		this.items = items;
	}

	public void setSelectionListener(SelectionListener selectionListener) {
		this.selectionListener = selectionListener;
	}

	public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	@Override
	public CheckableVH onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_with_checkbox, parent, false);
		return new CheckableVH(view);
	}

	@Override
	public void onBindViewHolder(CheckableVH holder, int position) {
		T item = items.get(position);

		holder.checkBox.setOnClickListener(v -> onCheckBoxClick(holder.getAdapterPosition()));

		holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(item));

		holder.itemView.setOnLongClickListener(v -> {
			onItemClickListener.onItemLongClick(item, holder);
			return true;
		});

		holder.checkBox.setVisibility(showCheckBoxes ? View.VISIBLE : View.GONE);
		holder.checkBox.setChecked(selectionManager.isPositionSelected(position));
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
	public boolean onItemMove(int srcPosition, int targetPosition) {
		if (Math.abs(srcPosition - targetPosition) <= 1) {
			Collections.swap(items, srcPosition, targetPosition);
		} else {
			T movedItem = items.get(srcPosition);
			items.remove(movedItem);
			items.add(targetPosition, movedItem);
		}
		notifyItemMoved(srcPosition, targetPosition);
		selectionManager.updateSelection(srcPosition, targetPosition);
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

		void onItemClick(T item);

		void onItemLongClick(T item, CheckableVH holder);
	}
}
