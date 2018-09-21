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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.recyclerview.adapter.draganddrop.TouchHelperAdapterInterface;
import org.catrobat.catroid.ui.recyclerview.adapter.multiselection.MultiSelectionManager;
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableVH;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class RVAdapter<T> extends RecyclerView.Adapter<CheckableVH> implements
		TouchHelperAdapterInterface {

	List<T> items;
	public boolean allowMultiSelection = true;
	public boolean showCheckBoxes = false;

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
	public CheckableVH onCreateViewHolder(final ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_with_checkbox, parent, false);
		return new CheckableVH(view);
	}

	@Override
	public void onBindViewHolder(final CheckableVH holder, int position) {
		final T item = items.get(position);

		holder.checkBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onCheckBoxClick(holder.getAdapterPosition());
			}
		});

		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onItemClickListener.onItemClick(item);
			}
		});

		holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				onItemClickListener.onItemLongClick(item, holder);
				return true;
			}
		});

		holder.checkBox.setVisibility(showCheckBoxes ? View.VISIBLE : View.GONE);
		holder.checkBox.setChecked(selectionManager.isPositionSelected(position));
	}

	protected void onCheckBoxClick(int position) {
		if (!allowMultiSelection) {
			boolean currentState = selectionManager.isPositionSelected(position);

			for (int i : selectionManager.getSelectedPositions()) {
				selectionManager.setSelectionTo(false, i);
				notifyItemChanged(i);
			}

			selectionManager.setSelectionTo(!currentState, position);
			notifyItemChanged(position);
		} else {
			selectionManager.toggleSelection(position);
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
	public boolean onItemMove(int fromPosition, int toPosition) {
		Collections.swap(items, fromPosition, toPosition);
		notifyItemMoved(fromPosition, toPosition);
		selectionManager.updateSelection(fromPosition, toPosition);
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

	public interface SelectionListener {

		void onSelectionChanged(int selectedItemCnt);
	}

	public interface OnItemClickListener<T> {

		void onItemClick(T item);

		void onItemLongClick(T item, CheckableVH holder);
	}
}
