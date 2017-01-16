/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
import android.view.ViewGroup;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.NfcTagData;

import java.util.List;

public class NfcTagListAdapter extends CheckBoxListAdapter<NfcTagData> {

	public static final String TAG = NfcTagListAdapter.class.getSimpleName();

	public NfcTagListAdapter(Context context, int resource, List<NfcTagData> listItems) {
		super(context, resource, listItems);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View listItemView = super.getView(position, convertView, parent);

		ListItemViewHolder listItemViewHolder = (ListItemViewHolder) listItemView.getTag();
		NfcTagData nfcTagData = getItem(position);

		listItemViewHolder.name.setText(nfcTagData.getNfcTagName());

		if (showDetails) {
			listItemViewHolder.details.setVisibility(View.VISIBLE);
			listItemViewHolder.leftTopDetails.setText(R.string.uid);
			listItemViewHolder.rightTopDetails.setText(nfcTagData.getNfcTagUid());
		}

		return listItemView;
	}
}
