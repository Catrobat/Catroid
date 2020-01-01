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
import org.catrobat.catroid.ui.recyclerview.RVButton;
import org.catrobat.catroid.ui.recyclerview.viewholder.ButtonVH;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ButtonAdapter extends RecyclerView.Adapter<ButtonVH> {

	public List<RVButton> items;
	private OnItemClickListener onItemClickListener;

	public ButtonAdapter(List<RVButton> items) {
		this.items = items;
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	@NonNull
	@Override
	public ButtonVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_button, parent, false);
		return new ButtonVH(view);
	}

	@Override
	public void onBindViewHolder(@NonNull final ButtonVH holder, int position) {
		final RVButton item = items.get(position);
		holder.image.setImageDrawable(item.drawable);
		holder.title.setText(item.title);

		if (item.subtitle != null) {
			holder.subtitle.setText(item.subtitle);
			holder.subtitle.setVisibility(View.VISIBLE);
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
