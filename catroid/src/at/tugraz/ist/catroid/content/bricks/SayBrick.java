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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class SayBrick implements Brick {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private String text = "";

	@XStreamOmitField
	private transient View view;

	public SayBrick(Sprite sprite) {
		this.sprite = sprite;
	}

	public SayBrick(Sprite sprite, String text) {
		this.sprite = sprite;
		this.text = text;
	}

	public void execute() {
		// sprite.getBubble().setSpeechBubble(text, R.drawable.speech_bubble, R.drawable.speech_bubble_inv);
	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public View getView(final Context context, int brickId, BaseExpandableListAdapter adapter) {

		if (view == null) {
			view = View.inflate(context, R.layout.toolbox_brick_say, null);
		}

		EditText editText = (EditText) view.findViewById(R.id.toolbox_brick_say_edit_text);
		editText.setText(text);
		editText.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(context);
				final EditText input = new EditText(context);
				input.setText(text);
				dialog.setView(input);
				dialog.setOnCancelListener((OnCancelListener) context);
				dialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						text = (input.getText().toString()).trim();
						dialog.cancel();
					}
				});
				dialog.setNeutralButton(context.getString(R.string.cancel_button),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
						});

				dialog.show();
			}
		});

		return view;
	}

	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.toolbox_brick_say, null);
	}

	@Override
	public Brick clone() {
		return new SayBrick(this.sprite, this.text);
	}

}
