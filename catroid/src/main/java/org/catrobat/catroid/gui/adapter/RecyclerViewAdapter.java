/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.gui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.R;
import org.catrobat.catroid.projecthandler.ProjectHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecyclerViewAdapter<T extends ListItem> extends RecyclerView.Adapter<ViewHolder> implements
		TouchHelperAdapterInterface {

	public static final String TAG = RecyclerViewAdapter.class.getSimpleName();

	protected List<T> items = new ArrayList<>();

	private MultiSelectionManager selectionManager = new MultiSelectionManager();
	private SelectionListener selectionListener;
	private OnItemClickListener<T> onItemClickListener;

	public RecyclerViewAdapter(List<T> items) {
		this.items = items;
	}

	@Override
	public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {
		final T item = items.get(position);

		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (selectionManager.isSelectionActive()) {
					onItemSelected(holder.getAdapterPosition(), holder);
				} else {
					onItemClickListener.onItemClick(item);
				}
			}
		});

		holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				onItemSelected(holder.getAdapterPosition(), holder);
				return true;
			}
		});

		holder.reorderIcon.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				onItemClickListener.onReorderIconClick(holder);
				return true;
			}
		});

		holder.nameView.setText(item.getName());
		holder.imageSwitcher.setImageDrawable(item.getThumbnail());
		holder.updateBackground(selectionManager.isPositionSelected(holder.getAdapterPosition()));
	}

	public void setSelectionListener(SelectionListener selectionListener) {
		this.selectionListener = selectionListener;
	}

	public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	private void onItemSelected(int itemPosition, ViewHolder holder) {
		selectionManager.toggleSelection(itemPosition);
		selectionListener.onSelectionChanged(selectionManager.isSelectionActive());
		holder.updateBackground(selectionManager.isPositionSelected(itemPosition));
	}

	@Override
	public boolean onItemMove(int fromPosition, int toPosition) {
		Collections.swap(items, fromPosition, toPosition);
		notifyItemMoved(fromPosition, toPosition);

		selectionManager.updateSelection(fromPosition, toPosition);
		updateProject();
		return true;
	}

	public void addItem(T item) throws ArrayIndexOutOfBoundsException {
		if (selectionManager.isSelectionActive()) {
			throw new ArrayIndexOutOfBoundsException("ERROR: Cannot Add or Remove items while in multiSelection.");
		}

		items.add(item);
		notifyDataSetChanged();
		updateProject();
	}

	public void removeItem(T item) throws ArrayIndexOutOfBoundsException {
		if (selectionManager.isSelectionActive()) {
			throw new ArrayIndexOutOfBoundsException("ERROR: Cannot Add or Remove items while in multiSelection.");
		}

		items.remove(item);
		notifyDataSetChanged();
		updateProject();
	}

	public boolean isItemNameUnique(String name) {
		for (T item : items) {
			if (item.getName().equals(name)) {
				return false;
			}
		}

		return true;
	}

	public List<T> getSelectedItems() {
		List<T> selectedItems = new ArrayList<>();

		for (int position : selectionManager.getSelectedPositions()) {
			selectedItems.add(items.get(position));
		}

		return selectedItems;
	}

	public int getSelectedItemCount() {
		return selectionManager.getSelectedPositions().size();
	}

	public void clearSelection() {
		selectionManager.clearSelection();
		notifyDataSetChanged();
		updateProject();
	}

	public void updateProject() {
		try {
			ProjectHolder.getInstance().serialize(ProjectHolder.getInstance().getCurrentProject());
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	public interface SelectionListener {

		void onSelectionChanged(boolean isSelectionActive);
	}

	public interface OnItemClickListener<T> {

		void onItemClick(T item);

		void onReorderIconClick(RecyclerView.ViewHolder viewHolder);
	}
}
