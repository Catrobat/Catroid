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
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.Toast;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class SetYBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;
	private int yPosition;
	private Sprite sprite;

	@XStreamOmitField
	private transient View view;

	public SetYBrick(Sprite sprite, int yPosition) {
		this.sprite = sprite;
		this.yPosition = yPosition;
	}

	public void execute() {
		sprite.setXYPosition(sprite.getXPosition(), yPosition);
	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public View getView(Context context, int brickId, BaseExpandableListAdapter adapter) {

		if (view == null) {
			view = View.inflate(context, R.layout.toolbox_brick_set_y, null);
		}

		EditText editY = (EditText) view.findViewById(R.id.toolbox_brick_set_y_edit_text);
		editY.setText(String.valueOf(yPosition));

		editY.setOnClickListener(this);

		return view;
	}

	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.toolbox_brick_set_y, null);
	}

	@Override
	public Brick clone() {
		return new SetYBrick(getSprite(), yPosition);
	}

	public void onClick(View view) {
		final Context context = view.getContext();

		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		final EditText input = new EditText(context);
		input.setText(String.valueOf(yPosition));
		input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL
				| InputType.TYPE_NUMBER_FLAG_SIGNED);
		input.setSelectAllOnFocus(true);
		dialog.setView(input);
		dialog.setOnCancelListener((OnCancelListener) context);
		dialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				try {
					yPosition = Integer.parseInt(input.getText().toString());
				} catch (NumberFormatException exception) {
					Toast.makeText(context, R.string.error_no_number_entered, Toast.LENGTH_SHORT);
				}
				dialog.cancel();
			}
		});
		dialog.setNeutralButton(context.getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		dialog.show();

	}
}
