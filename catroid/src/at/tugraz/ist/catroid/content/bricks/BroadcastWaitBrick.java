/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content.bricks;

import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.BroadcastScript;
import at.tugraz.ist.catroid.content.Sprite;

/**
 * @author Johannes Iber
 * 
 */
public class BroadcastWaitBrick implements Brick {

	private static final long serialVersionUID = 1L;
	private transient ProjectManager projectManager;
	private Sprite sprite;
	private String selectedMessage = "";

	public BroadcastWaitBrick(Sprite sprite) {
		this.sprite = sprite;
		this.projectManager = ProjectManager.getInstance();
	}

	public void execute() {
		Vector<BroadcastScript> receiver = projectManager.messageContainer.getReceiverOfMessage(selectedMessage);
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

	public Sprite getSprite() {
		return sprite;
	}

	public void setSelectedMessage(String selectedMessage) {
		this.selectedMessage = selectedMessage;
		projectManager.messageContainer.addMessage(this.selectedMessage);
	}

	private Object readResolve() {
		projectManager = ProjectManager.getInstance();
		if (selectedMessage != null && projectManager.getCurrentProject() != null) {
			projectManager.messageContainer.addMessage(selectedMessage);
		}
		return this;
	}

	public View getView(final Context context, int brickId, BaseExpandableListAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View brickView = inflater.inflate(R.layout.construction_brick_broadcast_wait, null);
		final Spinner broadcastSpinner = (Spinner) brickView.findViewById(R.id.broadcast_spinner);
		broadcastSpinner.setAdapter(projectManager.messageContainer.getMessageAdapter(context));

		broadcastSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			private boolean start = true;

			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				if (start) {
					start = false;
					return;
				}
				selectedMessage = ((String) parent.getItemAtPosition(pos)).trim();
				if (selectedMessage == context.getString(R.string.broadcast_nothing_selected)) {
					selectedMessage = "";
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		int position = projectManager.messageContainer.getPositionOfMessageInAdapter(selectedMessage);
		if (position > 0) {
			broadcastSpinner.setSelection(position);
		}

		Button newBroadcastMessage = (Button) brickView.findViewById(R.id.broadcast_new_message);
		newBroadcastMessage.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				final EditText input = new EditText(context);

				builder.setView(input);
				builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String newMessage = (input.getText().toString()).trim();
						if (newMessage.length() == 0
								|| newMessage.equals(context.getString(R.string.broadcast_nothing_selected))) {
							dialog.cancel();
							return;
						}
						selectedMessage = newMessage;
						projectManager.messageContainer.addMessage(selectedMessage);
						int position = projectManager.messageContainer.getPositionOfMessageInAdapter(selectedMessage);

						broadcastSpinner.setSelection(position);
					}
				});
				builder.setNegativeButton(context.getString(R.string.cancel_button),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
						});

				AlertDialog alertDialog = builder.create();
				alertDialog.setOnShowListener(new OnShowListener() {
					public void onShow(DialogInterface dialog) {
						InputMethodManager inputManager = (InputMethodManager) context
								.getSystemService(Context.INPUT_METHOD_SERVICE);
						inputManager.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
					}
				});
				alertDialog.show();
			}
		});
		return brickView;
	}

	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.toolbox_brick_broadcast_wait, null);
		return view;
	}

	@Override
	public Brick clone() {
		return new BroadcastWaitBrick(sprite);
	}
}
