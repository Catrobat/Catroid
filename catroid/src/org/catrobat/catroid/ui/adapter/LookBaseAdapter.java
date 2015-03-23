/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.catrobat.catroid.common.LookData;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

public class LookBaseAdapter extends ArrayAdapter<LookData> implements ScriptActivityAdapterInterface {

	protected ArrayList<LookData> lookDataItems;
	protected Context context;

	private OnLookEditListener onLookEditListener;

	private int selectMode;
	private boolean showDetails;
	private SortedSet<Integer> checkedLooks = new TreeSet<Integer>();

	public LookBaseAdapter(final Context context, int currentPlayingposition) {
		super(context, currentPlayingposition);
	}

	public LookBaseAdapter(final Context context, int resource, int textViewResourceId, ArrayList<LookData> items,
			boolean showDetails) {
		super(context, resource, textViewResourceId, items);
		this.context = context;
		this.showDetails = showDetails;
		this.lookDataItems = items;
		this.selectMode = ListView.CHOICE_MODE_NONE;
	}

	public OnLookEditListener getOnLookEditListener() {
		return onLookEditListener;
	}

	public void setOnLookEditListener(OnLookEditListener listener) {
		onLookEditListener = listener;
	}

	@Override
	public void setShowDetails(boolean showDetails) {
		this.showDetails = showDetails;
	}

	@Override
	public boolean getShowDetails() {
		return showDetails;
	}

	@Override
	public void setSelectMode(int mode) {
		selectMode = mode;
	}

	@Override
	public int getSelectMode() {
		return selectMode;
	}

	@Override
	public int getAmountOfCheckedItems() {
		return checkedLooks.size();
	}

	@Override
	public SortedSet<Integer> getCheckedItems() {
		return checkedLooks;
	}

	public ArrayList<LookData> getLookDataItems() {
		return lookDataItems;
	}

	public void addCheckedItem(int position) {
		checkedLooks.add(position);
	}

	@Override
	public void clearCheckedItems() {
		checkedLooks.clear();
	}

	public interface OnLookEditListener {

		void onLookEdit(View view);

		void onLookChecked();
	}
}
