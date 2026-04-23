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

package org.catrobat.catroid.ui.recyclerview.adapter.draganddrop;

import org.catrobat.catroid.R;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class TouchHelperCallback extends ItemTouchHelper.Callback {

	public static final String TAG = TouchHelperCallback.class.getSimpleName();

	private TouchHelperAdapterInterface adapterInterface;

	public TouchHelperCallback(TouchHelperAdapterInterface adapterInterface) {
		this.adapterInterface = adapterInterface;
	}

	@Override
	public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
		final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
		return makeMovementFlags(dragFlags, 0);
	}

	@Override
	public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
		return adapterInterface.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
	}

	@Override
	public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
		switch (actionState) {
			case ItemTouchHelper.ACTION_STATE_DRAG:
				viewHolder.itemView.setBackgroundResource(R.drawable.button_background_pressed);
				break;
		}
	}

	@Override
	public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
		super.clearView(recyclerView, viewHolder);
		viewHolder.itemView.setBackgroundResource(R.drawable.button_background_selector);
	}

	@Override
	public boolean isLongPressDragEnabled() {
		return false;
	}

	@Override
	public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
	}
}
