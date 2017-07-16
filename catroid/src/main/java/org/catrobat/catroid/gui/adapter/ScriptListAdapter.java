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
import android.widget.TextView;

import org.catrobat.catroid.data.brick.Brick;
import org.catrobat.catroid.data.brick.BrickField;
import org.catrobat.catroid.data.brick.BrickViewHolder;
import org.catrobat.catroid.projecthandler.ProjectHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScriptListAdapter extends RecyclerView.Adapter<BrickViewHolder> implements TouchHelperAdapterInterface {

	public static final String TAG = ScriptListAdapter.class.getSimpleName();

	private List<Brick> bricks = new ArrayList<>();

	private MultiSelectionManager selectionManager = new MultiSelectionManager();
	private SelectionListener selectionListener;

	private ReorderItemInterface reorderItemInterface;

	public ScriptListAdapter(List<Brick> bricks) {
		this.bricks = bricks;
	}

	@Override
	public BrickViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
		return new BrickViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final BrickViewHolder holder, int position) {
		Brick item = bricks.get(position);

		for (BrickField brickField : item.getBrickFields()) {
			((TextView) holder.itemView.findViewById(brickField.getViewId())).setText(brickField.getDisplayText(holder
					.itemView.getContext()));
			holder.itemView.findViewById(brickField.getViewId()).setOnClickListener(item);
		}

		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (selectionManager.isSelectionActive()) {
					handleItemSelection(holder.getAdapterPosition(), holder);
				}
			}
		});

		holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				handleItemSelection(holder.getAdapterPosition(), holder);
				return true;
			}
		});

		holder.reorderIcon.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				reorderItemInterface.onReorderIconClicked(holder);
				return true;
			}
		});

		holder.updateBackground(selectionManager.isPositionSelected(holder.getAdapterPosition()));
	}

	private void handleItemSelection(int itemPosition, BrickViewHolder holder) {
		selectionManager.toggleSelection(itemPosition);
		selectionListener.onSelectionChanged(selectionManager.isSelectionActive());
		holder.updateBackground(selectionManager.isPositionSelected(itemPosition));
	}

	@Override
	public int getItemViewType(int position) {
		return bricks.get(position).getResourceId();
	}

	public void setSelectionListener(SelectionListener selectionListener) {
		this.selectionListener = selectionListener;
	}

	public void setReorderItemInterface(ReorderItemInterface reorderItemInterface) {
		this.reorderItemInterface = reorderItemInterface;
	}

	@Override
	public boolean onItemMove(int fromPosition, int toPosition) {
		Collections.swap(bricks, fromPosition, toPosition);
		notifyItemMoved(fromPosition, toPosition);

		selectionManager.updateSelection(fromPosition, toPosition);

		updateProject();
		return true;
	}

	public void addItem(Brick brick) throws ArrayIndexOutOfBoundsException {
		if (selectionManager.isSelectionActive()) {
			throw new ArrayIndexOutOfBoundsException("ERROR: Cannot Add or Remove items while in multiSelection.");
		}

		bricks.add(brick);
		notifyDataSetChanged();
		updateProject();
	}

	public void removeItem(Brick brick) throws ArrayIndexOutOfBoundsException {
		if (selectionManager.isSelectionActive()) {
			throw new ArrayIndexOutOfBoundsException("ERROR: Cannot Add or Remove items while in multiSelection.");
		}

		bricks.remove(brick);
		notifyDataSetChanged();
		updateProject();
	}

	public List<Brick> getSelectedItems() {
		List<Brick> selectedItems = new ArrayList<>();

		for (int position : selectionManager.getSelectedPositions()) {
			selectedItems.add(bricks.get(position));
		}

		return selectedItems;
	}

	public void clearSelection() {
		selectionManager.clearSelection();
		notifyDataSetChanged();
		updateProject();
	}

	private void updateProject() {
		try {
			ProjectHolder.getInstance().serialize(ProjectHolder.getInstance().getCurrentProject());
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	@Override
	public int getItemCount() {
		return bricks.size();
	}

	public interface SelectionListener {

		void onSelectionChanged(boolean isSelectionActive);
	}

	public interface ReorderItemInterface {

		void onReorderIconClicked(RecyclerView.ViewHolder viewHolder);
	}
}
