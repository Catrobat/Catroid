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

package org.catrobat.catroid.ui.recyclerview.backpack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.recyclerview.adapter.ExtendedRVAdapter;
import org.catrobat.catroid.ui.recyclerview.adapter.RVAdapter;
import org.catrobat.catroid.ui.recyclerview.adapter.draganddrop.TouchHelperCallback;
import org.catrobat.catroid.ui.recyclerview.adapter.multiselection.MultiSelectionManager;
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.PluralsRes;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public abstract class ActorAndObjectRecyclerViewFragment<T> extends Fragment implements
		RVAdapter.OnItemClickListener<T> {

	protected View parentView;
	protected RecyclerView recyclerView;

	protected ExtendedRVAdapter<T> adapter;

	protected String sharedPreferenceDetailsKey = "";
	public boolean hasDetails = false;

	protected ItemTouchHelper touchHelper;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		parentView = inflater.inflate(R.layout.fragment_list_view, container, false);
		recyclerView = parentView.findViewById(R.id.recycler_view);
		setHasOptionsMenu(false);

		return parentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstance) {
		super.onActivityCreated(savedInstance);
		initializeAdapter();
	}

	public void onAdapterReady() {
		recyclerView.setAdapter(adapter);

		adapter.setOnItemClickListener(this);
		adapter.showRipples = false;
		adapter.showSettings = false;
		adapter.showDetails = false;
		setHasOptionsMenu(false);
		adapter.notifyDataSetChanged();

		ItemTouchHelper.Callback callback = new TouchHelperCallback(adapter);
		touchHelper = new ItemTouchHelper(callback);
		touchHelper.attachToRecyclerView(recyclerView);
	}

	@Override
	public void onResume() {
		super.onResume();
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onStop() {
		super.onStop();
		finishActionMode();
	}

	protected void finishActionMode() {
		adapter.clearSelection();
		setShowProgressBar(false);
	}

	@Override
	public void onItemClick(final T item, MultiSelectionManager selectionManager) {
		unpackItems(new ArrayList<>(Collections.singletonList(item)));
	}

	public void setShowProgressBar(boolean show) {
		parentView.findViewById(R.id.progress_bar).setVisibility(show ? View.VISIBLE : View.GONE);
		recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
	}

	@Override
	public void onItemLongClick(T item, CheckableViewHolder holder) {
		onItemClick(item, null);
	}

	@Override
	public void onSettingsClick(T item, View view) {

	}

	protected abstract void initializeAdapter();

	protected abstract void unpackItems(List<T> selectedItems);

	@PluralsRes
	protected abstract int getDeleteAlertTitleId();
	protected abstract void deleteItems(List<T> selectedItems);
}
