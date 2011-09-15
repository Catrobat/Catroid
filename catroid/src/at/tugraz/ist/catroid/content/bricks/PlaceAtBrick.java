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

public class PlaceAtBrick implements Brick, OnDismissListener {
	private static final long serialVersionUID = 1L;
	private int xPosition;
	private int yPosition;
	private Sprite sprite;

	@XStreamOmitField
	private transient View view;

	public PlaceAtBrick(Sprite sprite, int xPosition, int yPosition) {
		this.sprite = sprite;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
	}

	public int getRequiredRessources() {
		return NO_RESSOURCES;
	}

	public void execute() {
		sprite.setXYPosition(xPosition, yPosition);
	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public View getView(Context context, int brickId, BaseExpandableListAdapter adapter) {

		if (view == null) {
			view = View.inflate(context, R.layout.toolbox_brick_place_at, null);
		}

		EditText editX = (EditText) view.findViewById(R.id.toolbox_brick_place_at_x_edit_text);
		editX.setText(String.valueOf(xPosition));

		EditIntegerDialog dialogX = new EditIntegerDialog(context, editX, xPosition, true);
		dialogX.setOnDismissListener(this);
		dialogX.setOnCancelListener((OnCancelListener) context);

		editX.setOnClickListener(dialogX);

		EditText editY = (EditText) view.findViewById(R.id.toolbox_brick_place_at_y_edit_text);
		editY.setText(String.valueOf(yPosition));

		EditIntegerDialog dialogY = new EditIntegerDialog(context, editY, yPosition, true);
		dialogY.setOnDismissListener(this);
		dialogY.setOnCancelListener((OnCancelListener) context);

		editY.setOnClickListener(dialogY);

		return view;
	}

	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.toolbox_brick_place_at, null);
	}

	@Override
	public Brick clone() {
		return new PlaceAtBrick(getSprite(), xPosition, yPosition);
	}

	public void onDismiss(DialogInterface dialog) {
		EditIntegerDialog inputDialog = (EditIntegerDialog) dialog;
		if (inputDialog.getRefernecedEditTextId() == R.id.toolbox_brick_place_at_x_edit_text) {
			xPosition = inputDialog.getValue();
		} else if (inputDialog.getRefernecedEditTextId() == R.id.toolbox_brick_place_at_y_edit_text) {
			yPosition = inputDialog.getValue();
		} else {
			throw new RuntimeException("Received illegal id from EditText: " + inputDialog.getRefernecedEditTextId());
		}

		dialog.cancel();
	}
}
