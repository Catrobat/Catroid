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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.BroadcastMessage;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.PhysicsCollision;
import org.catrobat.catroid.ui.dialogs.BrickTextDialog;

import java.util.List;

public class BroadcastReceiverBrick extends BrickBaseType implements ScriptBrick, BroadcastMessage {
	private static final long serialVersionUID = 1L;

	private BroadcastScript broadcastScript;
	private transient ArrayAdapter<String> messageAdapter;

	public BroadcastReceiverBrick(BroadcastScript broadcastScript) {
		this.broadcastScript = broadcastScript;
		if (broadcastScript != null && broadcastScript.isCommentedOut()) {
			setCommentedOut(true);
		}
	}

	@Override
	public Brick clone() {
		BroadcastScript broadcastScript = new BroadcastScript(getReceivedMessage());
		broadcastScript.setCommentedOut(broadcastScript.isCommentedOut());
		return new BroadcastReceiverBrick(broadcastScript);
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	public String getReceivedMessage() {
		return broadcastScript.getReceivedMessage();
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}

		view = View.inflate(context, R.layout.brick_broadcast_receive, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);
		setCheckboxView(R.id.brick_broadcast_receive_checkbox);
		final Spinner broadcastSpinner = (Spinner) view.findViewById(R.id.brick_broadcast_receive_spinner);

		broadcastSpinner.setAdapter(getMessageAdapter(context));
		broadcastSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String selectedMessage = broadcastSpinner.getSelectedItem().toString();
				if (selectedMessage.equals(context.getString(R.string.new_broadcast_message))) {
					showNewMessageDialog(broadcastSpinner);
				} else {
					broadcastScript.setReceivedMessage(selectedMessage);
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

		SpinnerAdapter broadcastReceiverSpinnerAdapter = getMessageAdapter(context);
		broadcastReceiverSpinner.setAdapter(broadcastReceiverSpinnerAdapter);
		setSpinnerSelection(broadcastReceiverSpinner);
		return prototypeView;
	}

	private ArrayAdapter<String> getMessageAdapter(Context context) {
		if (messageAdapter == null) {
			messageAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, ProjectManager.getInstance().getCurrentProject().getBroadcastMessages());
		}
		return messageAdapter;
	}

	@Override
	public Script getScriptSafe() {
		return broadcastScript;
	}

	private void setSpinnerSelection(Spinner spinner) {
		int position = getMessageAdapter(spinner.getContext()).getPosition(getReceivedMessage());
		spinner.setSelection(position, true);
	}

	// TODO: BroadcastBrick and BroadcastReceiverBrick contain this identical method.
	private void showNewMessageDialog(final Spinner spinner) {
		final Context context = spinner.getContext();
		BrickTextDialog editDialog = new BrickTextDialog(R.string.dialog_new_broadcast_message_title, R.string
				.dialog_new_broadcast_message_name, context.getString(R.string.new_broadcast_message)) {

			@Override
			protected boolean handlePositiveButtonClick() {
				String newMessage = inputLayout.getEditText().getText().toString().trim();
				if (newMessage.equals(context.getString(R.string.new_broadcast_message))) {
					dismiss();
					return false;
				}
				if (newMessage.contains(PhysicsCollision.COLLISION_MESSAGE_CONNECTOR)) {
					inputLayout.setError(getString(R.string.brick_broadcast_invalid_symbol));
					return false;
				}
				broadcastScript.setReceivedMessage(newMessage);
				ProjectManager.getInstance().getCurrentProject().addBroadcastMessage(newMessage);
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
