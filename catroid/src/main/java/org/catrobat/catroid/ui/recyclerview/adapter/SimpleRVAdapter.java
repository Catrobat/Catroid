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

package org.catrobat.catroid.ui.recyclerview.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.recyclerview.SimpleRVItem;
import org.catrobat.catroid.ui.recyclerview.viewholder.SimpleVH;

import java.util.List;

public class SimpleRVAdapter extends RecyclerView.Adapter<SimpleVH> {

	public List<SimpleRVItem> items;
	private OnItemClickListener onItemClickListener;

	public SimpleRVAdapter(List<SimpleRVItem> items) {
		this.items = items;
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	@Override
	public SimpleVH onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_simple, parent, false);
		return new SimpleVH(view);
	}

	@Override
	public void onBindViewHolder(final SimpleVH holder, int position) {
		final SimpleRVItem item = items.get(position);
		holder.image.setImageDrawable(item.drawable);
		holder.title.setText(item.name);

		if (item.subTitle != null) {
			holder.subTitle.setText(item.subTitle);
			holder.subTitle.setVisibility(View.VISIBLE);
		}

		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onItemClickListener.onItemClick(item.id);
			}
		});
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	public interface OnItemClickListener {

		void onItemClick(int id);
	}
}
