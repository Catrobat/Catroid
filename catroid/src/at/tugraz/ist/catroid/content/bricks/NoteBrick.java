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

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;

public class NoteBrick implements Brick {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private String note = "";

	public NoteBrick(Sprite sprite) {
		this.sprite = sprite;
	}

	public NoteBrick(Sprite sprite, String note) {
		this.sprite = sprite;
		this.note = note;
	}

	public void execute() {
		// nothing to execute
	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public String getNote() {
		return this.note;
	}

	public View getView(Context context, int brickId, BaseExpandableListAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View brickView = inflater.inflate(R.layout.construction_brick_note, null);

		EditText editText = (EditText) brickView.findViewById(R.id.edit_text_note);
		editText.setText(note);
		editText.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				note = s.toString();
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
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
