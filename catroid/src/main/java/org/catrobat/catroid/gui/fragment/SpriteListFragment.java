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

package org.catrobat.catroid.gui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.data.SceneInfo;
import org.catrobat.catroid.data.SpriteInfo;
import org.catrobat.catroid.gui.activity.SpriteActivity;
import org.catrobat.catroid.gui.adapter.ListItem;
import org.catrobat.catroid.gui.adapter.RecyclerViewAdapter;
import org.catrobat.catroid.gui.adapter.ViewHolder;
import org.catrobat.catroid.gui.dialog.RenameItemDialog;
import org.catrobat.catroid.projecthandler.ProjectHolder;
import org.catrobat.catroid.storage.DirectoryPathInfo;

public class SpriteListFragment extends RecyclerViewListFragment<SpriteInfo> {

	public static final String TAG = SpriteListFragment.class.getSimpleName();
	public static final String SELECTED_SPRITE = "SPRITE_NAME";

	private SceneInfo scene;

	@Override
	public void onActivityCreated(Bundle savedInstance) {
		String name = getActivity().getIntent().getStringExtra(SceneListFragment.SELECTED_SCENE);
		scene = ProjectHolder.getInstance().getCurrentProject().getSceneByName(name);
		super.onActivityCreated(savedInstance);
	}

	@Override
	protected RecyclerViewAdapter<SpriteInfo> createAdapter() {
		return new RecyclerViewAdapter<SpriteInfo>(scene.getSprites()) {

			@Override
			public void onBindViewHolder(final ViewHolder holder, final int position) {
				super.onBindViewHolder(holder, position);

				if (holder.getAdapterPosition() == 0) {
					holder.itemView.setOnLongClickListener(null);

					ListItem item = items.get(holder.getAdapterPosition());
					item.setName(getString(R.string.background));
					holder.nameView.setText(item.getName());

					holder.reorderIcon.setOnLongClickListener(null);
					holder.reorderIcon.setVisibility(View.INVISIBLE);
				}
			}

			@Override
			public boolean onItemMove(int fromPosition, int toPosition) {
				return fromPosition == 0 || toPosition == 0 || super.onItemMove(fromPosition, toPosition);
			}
		};
	}

	@Override
	protected Class getItemType() {
		return SpriteInfo.class;
	}

	@Override
	protected DirectoryPathInfo getCurrentDirectory() {
		return scene.getDirectoryInfo();
	}

	@Override
	public void addItem(String name) {
		SpriteInfo sprite = new SpriteInfo(name, getCurrentDirectory());
		adapter.addItem(sprite);
	}

	@Override
	public void onItemClick(SpriteInfo item) {
		Intent intent = new Intent(getActivity(), SpriteActivity.class);
		intent.putExtra(SceneListFragment.SELECTED_SCENE, scene.getName());
		intent.putExtra(SELECTED_SPRITE, item.getName());
		getActivity().startActivity(intent);
	}

	@Override
	protected void showRenameDialog(String name) {
		RenameItemDialog dialog = new RenameItemDialog(R.string.rename_sprite_dialog, name, this);
		dialog.show(getFragmentManager(), RenameItemDialog.TAG);
	}
}
