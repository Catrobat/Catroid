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
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.MessageContainer;
import org.catrobat.catroid.content.BroadcastMessage;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.dialogs.BrickTextDialog;

import java.util.List;

public class BroadcastReceiverBrick extends ScriptBrick implements BroadcastMessage {
	private static final long serialVersionUID = 1L;

	private BroadcastScript receiveScript;
	private transient String broadcastMessage;

	public BroadcastReceiverBrick(String broadcastMessage) {
		this.broadcastMessage = broadcastMessage;
	}

	public BroadcastReceiverBrick(BroadcastScript receiveScript) {
		this.receiveScript = receiveScript;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		BroadcastReceiverBrick copyBrick = (BroadcastReceiverBrick) clone();
		copyBrick.receiveScript = receiveScript;
		return copyBrick;
	}

	@Override
	public Brick clone() {
		return new BroadcastReceiverBrick(new BroadcastScript(getBroadcastMessage()));
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public String getBroadcastMessage() {
		if (receiveScript == null) {
			return broadcastMessage;
		}
		return receiveScript.getBroadcastMessage();
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}
		if (receiveScript == null) {
			receiveScript = new BroadcastScript(broadcastMessage);
			MessageContainer.addMessage(getBroadcastMessage());
		}

		view = View.inflate(context, R.layout.brick_broadcast_receive, null);
		view = getViewWithAlpha(alphaValue);
		setCheckboxView(R.id.brick_broadcast_receive_checkbox);

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
		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			broadcastSpinner.setClickable(true);
			broadcastSpinner.setEnabled(true);
		} else {
			broadcastSpinner.setClickable(false);
			broadcastSpinner.setEnabled(false);
		}

		broadcastSpinner.setAdapter(MessageContainer.getMessageAdapter(context));
		broadcastSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String selectedMessage = broadcastSpinner.getSelectedItem().toString();
				if (selectedMessage.equals(context.getString(R.string.new_broadcast_message))) {
					showNewMessageDialog(broadcastSpinner);
				} else {
					receiveScript.setBroadcastMessage(selectedMessage);
					broadcastMessage = selectedMessage;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		setSpinnerSelection(broadcastSpinner);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_broadcast_receive, null);
		Spinner broadcastReceiverSpinner = (Spinner) prototypeView.findViewById(R.id.brick_broadcast_receive_spinner);
		broadcastReceiverSpinner.setFocusableInTouchMode(false);
		broadcastReceiverSpinner.setFocusable(false);
		broadcastReceiverSpinner.setEnabled(false);

		SpinnerAdapter broadcastReceiverSpinnerAdapter = MessageContainer.getMessageAdapter(context);
		broadcastReceiverSpinner.setAdapter(broadcastReceiverSpinnerAdapter);
		setSpinnerSelection(broadcastReceiverSpinner);
		return prototypeView;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_broadcast_receive_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);
			this.alphaValue = alphaValue;
		}
		return view;
	}

	@Override
	public Script getScriptSafe() {
		return receiveScript;
	}

	private void setSpinnerSelection(Spinner spinner) {
		int position = MessageContainer.getPositionOfMessageInAdapter(spinner.getContext(), getBroadcastMessage());
		spinner.setSelection(position, true);
	}

	// TODO: BroadcastBrick and BroadcastReceiverBrick contain this identical method.
	private void showNewMessageDialog(final Spinner spinner) {
		final Context context = spinner.getContext();
		BrickTextDialog editDialog = new BrickTextDialog() {

			@Override
			protected void initialize() {
				inputTitle.setText(R.string.dialog_new_broadcast_message_name);
			}

			@Override
			protected boolean handleOkButton() {
				String newMessage = input.getText().toString().trim();
				if (newMessage.isEmpty() || newMessage.equals(context.getString(R.string.new_broadcast_message))) {
					dismiss();
					return false;
				}

				receiveScript.setBroadcastMessage(newMessage);
				broadcastMessage = newMessage;
				MessageContainer.addMessage(newMessage);
				setSpinnerSelection(spinner);
				return true;
			}

			@Override
			public void onDismiss(DialogInterface dialog) {
				setSpinnerSelection(spinner);
				super.onDismiss(dialog);
			}

			@Override
			protected String getTitle() {
				return getString(R.string.dialog_new_broadcast_message_title);
			}
		};

		editDialog.show(((FragmentActivity) context).getSupportFragmentManager(), "dialog_broadcast_brick");
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		return null;
	}
}
