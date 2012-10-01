/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content.bricks;

import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Spinner;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.BroadcastScript;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.dialogs.BrickTextDialog;

public class BroadcastBrick implements Brick {

	private static final long serialVersionUID = 1L;
	private transient ProjectManager projectManager;
	private Sprite sprite;
	private String broadcastMessage = "";

	private transient View view;

	public BroadcastBrick(Sprite sprite) {
		this.sprite = sprite;
		this.projectManager = ProjectManager.getInstance();
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		final Vector<BroadcastScript> receiver = projectManager.getMessageContainer().getReceiverOfMessage(
				broadcastMessage);
		if (receiver == null) {
			return;
		}
		if (receiver.size() == 0) {
			return;
		}
		Thread startThread = new Thread(new Runnable() {
			@Override
			public void run() {
				CountDownLatch simultaneousStart = new CountDownLatch(1);
				for (BroadcastScript receiverScript : receiver) {
					receiverScript.executeBroadcast(simultaneousStart);
				}
				simultaneousStart.countDown();
			}
		});
		startThread.start();
	}

	@Override
	public Sprite getSprite() {
		return sprite;
	}

	public void setSelectedMessage(String message) {
		broadcastMessage = message;
		projectManager.getMessageContainer().addMessage(broadcastMessage);
	}

	private Object readResolve() {
		projectManager = ProjectManager.getInstance();
		if (broadcastMessage != null && projectManager.getCurrentProject() != null) {
			projectManager.getMessageContainer().addMessage(broadcastMessage);
		}
		return this;
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter adapter) {

		view = View.inflate(context, R.layout.brick_broadcast, null);

		final Spinner broadcastSpinner = (Spinner) view.findViewById(R.id.broadcast_spinner);
		broadcastSpinner.setAdapter(projectManager.getMessageContainer().getMessageAdapter(context));
		broadcastSpinner.setClickable(true);
		broadcastSpinner.setFocusable(true);

		broadcastSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			private boolean start = true;

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (start) {
					start = false;
					return;
				}
				broadcastMessage = ((String) parent.getItemAtPosition(position)).trim();
				if (broadcastMessage == context.getString(R.string.broadcast_nothing_selected)) {
					broadcastMessage = "";
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		int position = projectManager.getMessageContainer().getPositionOfMessageInAdapter(broadcastMessage);
		if (position > 0) {
			broadcastSpinner.setSelection(position);
		}

		Button newBroadcastMessage = (Button) view.findViewById(R.id.broadcast_new_message);
		newBroadcastMessage.setClickable(true);
		newBroadcastMessage.setFocusable(true);

		newBroadcastMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ScriptTabActivity activity = (ScriptTabActivity) context;

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

						broadcastMessage = newMessage;
						projectManager.getMessageContainer().addMessage(broadcastMessage);
						int position = projectManager.getMessageContainer().getPositionOfMessageInAdapter(
								broadcastMessage);

						broadcastSpinner.setSelection(position);

						return true;
					}
				};

				editDialog.show(activity.getSupportFragmentManager(), "dialog_broadcast_brick");
			}
		});
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_broadcast, null);
	}

	@Override
	public Brick clone() {
		return new BroadcastBrick(sprite);
	}

	public BroadcastBrick() {

	}
}
