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
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.recyclerview.viewholder.ViewHolder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import androidx.annotation.IntDef;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryListRVAdapter extends RecyclerView.Adapter<ViewHolder> {

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({DEFAULT, COLLISION, NXT, EV3})
	public @interface CategoryListItemType{}
	public static final int DEFAULT = 0;
	public static final int COLLISION = 1;
	public static final int NXT = 2;
	public static final int EV3 = 3;

	public static class CategoryListItem {
		@Nullable
		public String header;
		public int nameResId;
		public String text;
		public @CategoryListItemType int type;

		public CategoryListItem(int nameResId, String text, @CategoryListItemType int type) {
			if (nameResId == R.string.formula_editor_function_regex_assistant) {
				this.text = "\t\t\t\t\t" + text;
			} else {
				this.text = text;
			}
			this.nameResId = nameResId;
			this.type = type;
		}
	}

	private List<CategoryListItem> items;
	private OnItemClickListener onItemClickListener;

	public CategoryListRVAdapter(List<CategoryListItem> items) {
		this.items = items;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
		final CategoryListItem item = items.get(position);

		if (holder.getItemViewType() == R.layout.view_holder_category_list_item_with_headline) {
			TextView headlineView = holder.itemView.findViewById(R.id.headline);
			headlineView.setText(items.get(position).header);
		}

		holder.title.setText(item.text);
		holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(item));
	}

	@Override
	public @LayoutRes int getItemViewType(int position) {
		return items.get(position).header != null
				? R.layout.view_holder_category_list_item_with_headline
				: R.layout.view_holder_category_list_item;
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		onItemClickListener = listener;
	}

	public interface OnItemClickListener {
		void onItemClick(CategoryListItem item);
	}
}
