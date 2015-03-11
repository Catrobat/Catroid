/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.MessageContainer;
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick;
import org.catrobat.catroid.ui.dialogs.BrickTextDialog;

/**
 * Create View for {@code BroadcastReceiverBrick}.
 * Created by Illya Boyko on 04/03/15.
 */
public class BroadcastReceiverBrickViewFactory extends BrickViewFactory {
	public BroadcastReceiverBrickViewFactory(Context context, LayoutInflater inflater) {
		super(context, inflater);
	}

	View createBroadcastReceiverBrickView(final BroadcastReceiverBrick brick, ViewGroup parent) {

		View view = createSimpleBrickView(parent, R.layout.brick_broadcast_receive);

		// XXX method moved to to DragAndDropListView since it is not working on 2.x
		//		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		//
		//			@Override
		//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		//				checked = isChecked;
		//				if (!checked) {
		//					for (Brick currentBrick : adapter.getCheckedBricks()) {
		//						currentBrick.setCheckedBoolean(false);
		//					}
		//				}
		//				adapter.handleCheck(brickInstance, checked);
		//			}
		//		});

		final Spinner broadcastSpinner = (Spinner) view.findViewById(R.id.brick_broadcast_receive_spinner);
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
					brick.setNewBroadcastMessage(selectedMessage);

				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		setSpinnerSelection(brick, broadcastSpinner);
		return view;
	}

	// TODO: BroadcastBrick and BroadcastReceiverBrick contain this identical method.
	private void showNewMessageDialog(final BroadcastReceiverBrick brick, final Spinner spinner) {
		final Context context = spinner.getContext();
		BrickTextDialog editDialog = new BrickTextDialog() {

			@Override
			protected void initialize() {
				inputTitle.setText(R.string.dialog_new_broadcast_message_name);
			}

			@Override
			protected boolean handleOkButton() {
				String newMessage = (input.getText().toString()).trim();
				if (newMessage.isEmpty() || newMessage.equals(context.getString(R.string.new_broadcast_message))) {
					dismiss();
					return false;
				}

				brick.setNewBroadcastMessage(newMessage);
				MessageContainer.addMessage(newMessage);
				setSpinnerSelection(brick, spinner);
				return true;
			}

			@Override
			public void onDismiss(DialogInterface dialog) {
				setSpinnerSelection(brick, spinner);
				super.onDismiss(dialog);
			}

			@Override
			protected String getTitle() {
				return getString(R.string.dialog_new_broadcast_message_title);
			}
		};

		editDialog.show(((FragmentActivity) context).getSupportFragmentManager(), "dialog_broadcast_brick");
	}

	private void setSpinnerSelection(BroadcastReceiverBrick brick, Spinner spinner) {
		int position = MessageContainer.getPositionOfMessageInAdapter(spinner.getContext(), brick.getBroadcastMessage());
		spinner.setSelection(position, true);
	}

	//
}
