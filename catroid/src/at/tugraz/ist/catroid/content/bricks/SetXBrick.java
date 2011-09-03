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
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.ui.dialogs.EditIntegerDialog;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class SetXBrick implements Brick, OnDismissListener {
	private static final long serialVersionUID = 1L;
	private int xPosition;
	private Sprite sprite;

	@XStreamOmitField
	private transient View view;

	public SetXBrick(Sprite sprite, int xPosition) {
		this.sprite = sprite;
		this.xPosition = xPosition;
	}

	public void execute() {
		sprite.costume.aquireXYWidthHeightLock();
		sprite.costume.setXPosition(xPosition);
		sprite.costume.releaseXYWidthHeightLock();
	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public View getView(Context context, int brickId, BaseExpandableListAdapter adapter) {

		if (view == null) {
			view = View.inflate(context, R.layout.toolbox_brick_set_x, null);
		}

		EditText editX = (EditText) view.findViewById(R.id.toolbox_brick_set_x_edit_text);
		editX.setText(String.valueOf(xPosition));

		EditIntegerDialog dialogX = new EditIntegerDialog(context, editX, xPosition, true);
		dialogX.setOnDismissListener(this);
		dialogX.setOnCancelListener((OnCancelListener) context);

		editX.setOnClickListener(dialogX);

		return view;
	}

	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.toolbox_brick_set_x, null);
	}

	@Override
	public Brick clone() {
		return new SetXBrick(getSprite(), xPosition);
	}

	public void onDismiss(DialogInterface dialog) {
		EditIntegerDialog inputDialog = (EditIntegerDialog) dialog;
		xPosition = inputDialog.getValue();

		dialog.cancel();
	}
}
