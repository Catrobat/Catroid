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

package org.catrobat.catroid.ui.filepicker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.recyclerview.adapter.RVAdapter;
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableViewHolder;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;

public class FilePickerAdapter extends RVAdapter<File> {

	protected FilePickerAdapter(List<File> items) {
		super(items);
	}

	@NonNull
	@Override
	public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_file, parent, false);
		return new FileViewHolder(view);
	}

	@Override
	public void onBindViewHolder(CheckableViewHolder holder, int position) {
		super.onBindViewHolder(holder, position);

		File item = items.get(position);
		holder.title.setText(item.getName());
		((FileViewHolder) holder).subtitle.setText(item.getAbsolutePath());
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	public interface OnItemClickListener {

		void onItemClick(File item);
		boolean onItemLongClick(File item);
	}
}
