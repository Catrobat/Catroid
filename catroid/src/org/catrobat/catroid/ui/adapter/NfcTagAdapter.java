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
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.view.ActionMode;

import org.catrobat.catroid.common.NfcTagData;
import org.catrobat.catroid.ui.controller.NfcTagController;
import org.catrobat.catroid.ui.fragment.NfcTagFragment;

import java.util.ArrayList;
import java.util.Iterator;

public class NfcTagAdapter extends NfcTagBaseAdapter implements ScriptActivityAdapterInterface {

	private NfcTagFragment nfcTagFragment;

	public NfcTagAdapter(final Context context, int resource, int textViewResourceId, ArrayList<NfcTagData> items,
                         boolean showDetails) {
		super(context, resource, textViewResourceId, items, showDetails);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (nfcTagFragment == null) {
			return convertView;
		}
		return nfcTagFragment.getView(position, convertView);
	}

    public void onDestroyActionModeRename(ActionMode mode, ListView listView) {
        Iterator<Integer> iterator = checkedNfcTags.iterator();

        if (iterator.hasNext()) {
            int position = iterator.next();
            nfcTagFragment.setSelectedNfcTagData((NfcTagData) listView.getItemAtPosition(position));
            nfcTagFragment.showRenameDialog();
        }
        nfcTagFragment.clearCheckedNfcTagsAndEnableButtons();
    }

    public void onDestroyActionModeCopy(ActionMode mode) {
        Iterator<Integer> iterator = checkedNfcTags.iterator();

        while (iterator.hasNext()) {
            int position = iterator.next();
            NfcTagController.getInstance().copyNfcTag(position, nfcTagFragment.getNfcTagDataList(), this);
        }
        nfcTagFragment.clearCheckedNfcTagsAndEnableButtons();
    }

	public void setNfcTagFragment(NfcTagFragment nfcTagFragment) {
		this.nfcTagFragment = nfcTagFragment;
	}

    @Override
    public ArrayList<NfcTagData> getNfcTagDataItems() {
        return nfcTagDataItems;
    }
}