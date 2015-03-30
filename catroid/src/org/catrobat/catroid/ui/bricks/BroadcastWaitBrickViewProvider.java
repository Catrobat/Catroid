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

package org.catrobat.catroid.ui.bricks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.MessageContainer;
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick;

/**
 * Create View for {@code BroadcastWaitBrick}.
 * Created by illya on 04/03/15.
 */
public class BroadcastWaitBrickViewProvider extends BroadcastBrickViewProvider {
	public BroadcastWaitBrickViewProvider(Context context, LayoutInflater inflater) {
		super(context, inflater);
	}

	public View createBroadcastWaitBrickView(final BroadcastWaitBrick brick, ViewGroup parent) {
		View view = inflateBrickView(parent, R.layout.brick_broadcast_wait);

		final Spinner broadcastSpinner = (Spinner) view.findViewById(R.id.brick_broadcast_wait_spinner);
		broadcastSpinner.setFocusableInTouchMode(false);
		broadcastSpinner.setFocusable(false);

		broadcastSpinner.setAdapter(MessageContainer.getMessageAdapter(context));
		broadcastSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String selectedMessage = broadcastSpinner.getSelectedItem().toString();
				if (selectedMessage.equals(context.getString(R.string.new_broadcast_message))) {
					showNewMessageDialog(brick, broadcastSpinner);
				} else {
					brick.setBroadcastMessage(selectedMessage);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		setSpinnerSelection(brick, broadcastSpinner);
		return view;
	}


}
