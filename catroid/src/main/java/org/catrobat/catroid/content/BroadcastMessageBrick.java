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
package org.catrobat.catroid.content;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.content.bricks.BrickViewProvider;
import org.catrobat.catroid.ui.adapter.BroadcastSpinnerAdapter;
import org.catrobat.catroid.ui.recyclerview.dialog.NewBroadcastMessageDialog;

public abstract class BroadcastMessageBrick extends BrickBaseType implements NewBroadcastMessageDialog.NewBroadcastMessageInterface {
	protected transient BroadcastSpinnerAdapter messageAdapter;
	protected transient int viewId;
	private transient int spinnerId = R.id.brick_broadcast_spinner;
	private transient int checkboxId = R.id.brick_broadcast_checkbox;

	protected Object readResolve() {
		this.spinnerId = R.id.brick_broadcast_spinner;
		this.checkboxId = R.id.brick_broadcast_checkbox;
		return this;
	}

	@Override
	public void updateSpinnerSelection() {
		Spinner spinner = view.findViewById(spinnerId);
		setSpinnerSelection(spinner);
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	private BroadcastSpinnerAdapter getMessageAdapter(Context context) {
		if (messageAdapter == null) {
			messageAdapter = new BroadcastSpinnerAdapter(context);
		}
		messageAdapter.update();
		return messageAdapter;
	}

	private void setSpinnerSelection(Spinner spinner) {
		int position = messageAdapter.getPosition(getBroadcastMessage());
		spinner.setSelection(position, true);
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, viewId, null);
		Spinner broadcastSpinner = prototypeView.findViewById(spinnerId);

		BroadcastSpinnerAdapter broadcastSpinnerAdapter = getMessageAdapter(context);
		if (context.getString(R.string.new_broadcast_message).equals(getBroadcastMessage())) {
			setBroadcastMessage(broadcastSpinnerAdapter.getItem(1));
		}
		broadcastSpinner.setAdapter(broadcastSpinnerAdapter);
		setSpinnerSelection(broadcastSpinner);
		return prototypeView;
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {

		if (view == null) {
			alphaValue = 255;
		}
		view = View.inflate(context, viewId, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);
		setCheckboxView(checkboxId);
		final Spinner broadcastSpinner = view.findViewById(spinnerId);

		broadcastSpinner.setAdapter(getMessageAdapter(context));
		if (getBroadcastMessage().equals(context.getString(R.string.new_broadcast_message))) {
			setBroadcastMessage(messageAdapter.getItem(1));
		}
		setOnItemSelectedListener(broadcastSpinner, context);

		setSpinnerSelection(broadcastSpinner);
		return view;
	}

	private void setOnItemSelectedListener(final Spinner broadcastSpinner, final Context context) {
		broadcastSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String selectedMessage = broadcastSpinner.getSelectedItem().toString();
				if (selectedMessage.equals(context.getString(R.string.new_broadcast_message))) {
					showNewMessageDialog(broadcastSpinner);
				} else {
					setBroadcastMessage(selectedMessage);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	private void showNewMessageDialog(final Spinner spinner) {
		final Context context = spinner.getContext();
		NewBroadcastMessageDialog editDialog = new NewBroadcastMessageDialog(this, view.getContext().getString(R.string.new_broadcast_message));
		editDialog.show(((Activity) context).getFragmentManager(), "dialog_broadcast_brick");
	}

	public abstract String getBroadcastMessage();
}
