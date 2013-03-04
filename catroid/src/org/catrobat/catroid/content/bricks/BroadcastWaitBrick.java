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

import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.MessageContainer;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.dialogs.BrickTextDialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class BroadcastWaitBrick extends BrickBaseType {

	private static final long serialVersionUID = 1L;
	private transient ProjectManager projectManager;
	private String broadcastMessage = "";

	public BroadcastWaitBrick() {

	}

	public BroadcastWaitBrick(Sprite sprite) {
		this.sprite = sprite;
		this.projectManager = ProjectManager.getInstance();
	}

	@Override
	public void execute() {
		Vector<BroadcastScript> receiver = MessageContainer.getReceiverOfMessage(broadcastMessage);
		if (receiver == null) {
			return;
		}
		if (receiver.size() == 0) {
			return;
		}
		CountDownLatch simultaneousStart = new CountDownLatch(1);
		CountDownLatch wait = new CountDownLatch(receiver.size());

		for (BroadcastScript receiverScript : receiver) {
			receiverScript.executeBroadcastWait(simultaneousStart, wait);
		}
		simultaneousStart.countDown();

		try {
			wait.await();
		} catch (InterruptedException e) {
		}
	}

	public void setSelectedMessage(String selectedMessage) {
		this.broadcastMessage = selectedMessage;
		MessageContainer.addMessage(this.broadcastMessage);
	}

	private Object readResolve() {
		projectManager = ProjectManager.getInstance();
		if (broadcastMessage != null && projectManager.getCurrentProject() != null) {
			MessageContainer.addMessage(broadcastMessage);
		}
		return this;
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {

		if (view == null) {
			view = View.inflate(context, R.layout.brick_broadcast_wait, null);

			checkbox = (CheckBox) view.findViewById(R.id.brick_broadcast_wait_checkbox);
			final Brick brickInstance = this;

			checkbox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					checked = !checked;
					adapter.handleCheck(brickInstance, checked);
				}
			});
		}
		final Spinner broadcastSpinner = (Spinner) view.findViewById(R.id.brick_broadcast_wait_spinner);
		broadcastSpinner.setAdapter(MessageContainer.getMessageAdapter(context));
		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			broadcastSpinner.setClickable(true);
			broadcastSpinner.setEnabled(true);
		} else {
			broadcastSpinner.setClickable(false);
			broadcastSpinner.setEnabled(false);
		}

		broadcastSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			private boolean start = true;

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				if (start) {
					start = false;
					return;
				}
				broadcastMessage = ((String) parent.getItemAtPosition(pos)).trim();
				if (broadcastMessage == context.getString(R.string.broadcast_nothing_selected)) {
					broadcastMessage = "";
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		int position = MessageContainer.getPositionOfMessageInAdapter(broadcastMessage);
		if (position > 0) {
			broadcastSpinner.setSelection(position);
		}

		Button newBroadcastMessage = (Button) view.findViewById(R.id.brick_broadcast_wait_button_new_message);
		newBroadcastMessage.setClickable(true);
		newBroadcastMessage.setFocusable(true);
		newBroadcastMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (checkbox.getVisibility() == View.VISIBLE) {
					return;
				}
				ScriptActivity activity = (ScriptActivity) context;

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
						MessageContainer.addMessage(broadcastMessage);
						int position = MessageContainer.getPositionOfMessageInAdapter(broadcastMessage);

						broadcastSpinner.setSelection(position);

						return true;
					}
				};

				editDialog.show(activity.getSupportFragmentManager(), "dialog_broadcast_wait_brick");
			}
		});
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_broadcast_wait, null);
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_broadcast_wait_layout);
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);
		this.alphaValue = (alphaValue);
		return view;
	}

	@Override
	public Brick clone() {
		return new BroadcastWaitBrick(sprite);
	}
}
