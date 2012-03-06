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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.BroadcastScript;
import at.tugraz.ist.catroid.content.Sprite;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class BroadcastReceiverBrick implements Brick {

	private static final long serialVersionUID = 1L;
	private transient final ProjectManager projectManager;
	protected BroadcastScript receiveScript;
	private Sprite sprite;

	@XStreamOmitField
	private transient View view;

	public BroadcastReceiverBrick(Sprite sprite, BroadcastScript receiveScript) {
		this.sprite = sprite;
		this.receiveScript = receiveScript;
		this.projectManager = ProjectManager.getInstance();
	}

	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	public void execute() {
	}

	public Sprite getSprite() {
		return sprite;
	}

	public View getView(final Context context, int brickId, BaseAdapter adapter) {

		view = View.inflate(context, R.layout.brick_broadcast_receive, null);

		final Spinner broadcastSpinner = (Spinner) view.findViewById(R.id.broadcast_spinner);
		broadcastSpinner.setAdapter(projectManager.messageContainer.getMessageAdapter(context));
		broadcastSpinner.setClickable(true);
		broadcastSpinner.setFocusable(true);
		broadcastSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			private boolean start = true;

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

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		int position = projectManager.messageContainer.getPositionOfMessageInAdapter(receiveScript
				.getBroadcastMessage());
		if (position > 0) {
			broadcastSpinner.setSelection(position);
		}

		Button newBroadcastMessage = (Button) view.findViewById(R.id.broadcast_new_message);
		newBroadcastMessage.setClickable(true);
		newBroadcastMessage.setFocusable(true);
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
						receiveScript.setBroadcastMessage(newMessage);

						int position = projectManager.messageContainer.getPositionOfMessageInAdapter(newMessage);

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

		broadcastSpinner.setFocusable(false);
		newBroadcastMessage.setFocusable(false);
		return view;
	}

	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_broadcast_receive, null);
	}

	@Override
	public Brick clone() {
		return new BroadcastReceiverBrick(sprite, receiveScript);
	}

}
