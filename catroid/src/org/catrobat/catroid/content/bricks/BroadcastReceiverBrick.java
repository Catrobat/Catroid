/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.ScriptTabActivity;
import org.catrobat.catroid.ui.dialogs.BrickTextDialog;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Spinner;
import org.catrobat.catroid.R;

public class BroadcastReceiverBrick extends ScriptBrick {

	private static final long serialVersionUID = 1L;
	private transient final ProjectManager projectManager;
	private BroadcastScript receiveScript;
	private Sprite sprite;

	private transient View view;

	public BroadcastReceiverBrick(Sprite sprite, BroadcastScript receiveScript) {
		this.sprite = sprite;
		this.receiveScript = receiveScript;
		this.projectManager = ProjectManager.getInstance();
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
	}

	@Override
	public Sprite getSprite() {
		return sprite;
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter adapter) {
		if (receiveScript == null) {
			receiveScript = new BroadcastScript(sprite);
		}

		view = View.inflate(context, R.layout.brick_broadcast_receive, null);

		final Spinner broadcastSpinner = (Spinner) view.findViewById(R.id.broadcast_spinner);
		broadcastSpinner.setAdapter(projectManager.getMessageContainer().getMessageAdapter(context));
		broadcastSpinner.setClickable(true);
		broadcastSpinner.setFocusable(true);
		broadcastSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			private boolean start = true;

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				if (start) {
					start = false;
					return;
				}
				String message = ((String) parent.getItemAtPosition(pos)).trim();

				if (message == context.getString(R.string.broadcast_nothing_selected)) {
					receiveScript.setBroadcastMessage("");
				} else {
					receiveScript.setBroadcastMessage(message);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		int position = projectManager.getMessageContainer().getPositionOfMessageInAdapter(
				receiveScript.getBroadcastMessage());
		if (position > 0) {
			broadcastSpinner.setSelection(position);
		}

		Button newBroadcastMessage = (Button) view.findViewById(R.id.broadcast_new_message);
		newBroadcastMessage.setClickable(true);
		newBroadcastMessage.setFocusable(true);
		newBroadcastMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ScriptTabActivity activity = (ScriptTabActivity) view.getContext();

				BrickTextDialog editDialog = new BrickTextDialog() {
					@Override
					protected void initialize() {
					}

					@Override
					protected boolean handleOkButton() {
						String newMessage = (input.getText().toString()).trim();
						if (newMessage.length() == 0
								|| newMessage.equals(context.getString(R.string.broadcast_nothing_selected))) {
							dismiss();
							return false;
						}

						receiveScript.setBroadcastMessage(newMessage);
						int position = projectManager.getMessageContainer().getPositionOfMessageInAdapter(newMessage);

						broadcastSpinner.setSelection(position);

						return true;
					}
				};

				editDialog.show(activity.getSupportFragmentManager(), "dialog_broadcast_receiver_brick");
			}
		});

		broadcastSpinner.setFocusable(false);
		newBroadcastMessage.setFocusable(false);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_broadcast_receive, null);
	}

	@Override
	public Brick clone() {
		return new BroadcastReceiverBrick(sprite, null);
	}

	@Override
	public Script initScript(Sprite sprite) {
		if (receiveScript == null) {
			receiveScript = new BroadcastScript(sprite);
		}

		return receiveScript;
	}

	public BroadcastReceiverBrick() {
		this.projectManager = ProjectManager.getInstance();
	}
}
