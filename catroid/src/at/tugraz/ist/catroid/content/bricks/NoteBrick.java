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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;

public class NoteBrick implements Brick {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private String note = "";

	private final transient int MAXLINES = 14;

	public NoteBrick(Sprite sprite) {
		this.sprite = sprite;
	}

	public NoteBrick(Sprite sprite, String note) {
		this.sprite = sprite;
		this.note = note;
	}

	public void execute() {
	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public String getNote() {
		return this.note;
	}

	public View getView(final Context context, int brickId, BaseExpandableListAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View brickView = inflater.inflate(R.layout.construction_brick_note, null);

		EditText editText = (EditText) brickView.findViewById(R.id.construction_brick_note_edit_text);
		editText.setText(note);
		editText.setMaxLines(MAXLINES);
		editText.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(context);
				final EditText input = new EditText(context);
				input.setText(note);
				dialog.setView(input);
				dialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						note = (input.getText().toString()).trim();
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

		return brickView;
	}

	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View brickView = inflater.inflate(R.layout.toolbox_brick_note, null);
		return brickView;
	}

	@Override
	public Brick clone() {
		return new NoteBrick(this.sprite, this.note);
	}
}
