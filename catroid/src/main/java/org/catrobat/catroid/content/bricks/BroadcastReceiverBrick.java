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
package org.catrobat.catroid.content.bricks;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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
import org.catrobat.catroid.physics.PhysicsCollision;
import org.catrobat.catroid.ui.dialogs.BrickTextDialog;

import java.util.List;

public class BroadcastReceiverBrick extends BrickBaseType implements ScriptBrick, BroadcastMessage {
	private static final long serialVersionUID = 1L;

	private BroadcastScript receiveScript;
	private transient String broadcastMessage;

	public BroadcastReceiverBrick(String broadcastMessage) {
		this.broadcastMessage = broadcastMessage;
	}

	public BroadcastReceiverBrick(BroadcastScript receiveScript) {
		this.receiveScript = receiveScript;

		if (receiveScript != null && receiveScript.isCommentedOut()) {
			setCommentedOut(true);
		}
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		BroadcastReceiverBrick copyBrick = (BroadcastReceiverBrick) clone();
		copyBrick.receiveScript = receiveScript;
		return copyBrick;
	}

	@Override
	public Brick clone() {
		BroadcastScript broadcastScript = new BroadcastScript(getBroadcastMessage());
		if (receiveScript != null) {
			broadcastScript.setCommentedOut(receiveScript.isCommentedOut());
		}
		return new BroadcastReceiverBrick(broadcastScript);
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
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);
		setCheckboxView(R.id.brick_broadcast_receive_checkbox);
		final Spinner broadcastSpinner = (Spinner) view.findViewById(R.id.brick_broadcast_receive_spinner);

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

		SpinnerAdapter broadcastReceiverSpinnerAdapter = MessageContainer.getMessageAdapter(context);
		broadcastReceiverSpinner.setAdapter(broadcastReceiverSpinnerAdapter);
		setSpinnerSelection(broadcastReceiverSpinner);
		return prototypeView;
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
		BrickTextDialog editDialog = new BrickTextDialog(R.string.dialog_new_broadcast_message_title, R.string
				.dialog_new_broadcast_message_name, context.getString(R.string.new_broadcast_message)) {

			@Override
			protected boolean handlePositiveButtonClick() {
				String newMessage = input.getText().toString().trim();
				if (newMessage.equals(context.getString(R.string.new_broadcast_message))) {
					dismiss();
					return false;
				}

				if (newMessage.contains(PhysicsCollision.COLLISION_MESSAGE_CONNECTOR)) {
					input.setError(getString(R.string.brick_broadcast_invalid_symbol));
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
		};

		editDialog.show(((Activity) context).getFragmentManager(), "dialog_broadcast_brick");
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		return null;
	}

	@Override
	public void setCommentedOut(boolean commentedOut) {
		super.setCommentedOut(commentedOut);
		getScriptSafe().setCommentedOut(commentedOut);
	}
}
