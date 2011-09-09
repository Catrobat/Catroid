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
import at.tugraz.ist.catroid.ui.dialogs.EditDoubleDialog;

public class TurnRightBrick extends Brick implements OnDismissListener {

	private static final long serialVersionUID = 1L;

	private Sprite sprite;

	private double degrees;

	private transient View view;

	public TurnRightBrick(Sprite sprite, double degrees) {
		this.sprite = sprite;
		this.degrees = degrees;
	}

	@Override
	public void execute() {
		sprite.setDirection(sprite.getDirection() + degrees);
	}

	@Override
	public Sprite getSprite() {
		return sprite;
	}

	@Override
	public View getView(Context context, int brickId, BaseExpandableListAdapter adapter) {

		if (view == null) {
			view = View.inflate(context, R.layout.toolbox_brick_turn_right, null);
		}

		EditText editDegrees = (EditText) view.findViewById(R.id.toolbox_brick_turn_right_edit_text);
		editDegrees.setText(String.valueOf(degrees));

		EditDoubleDialog dialog = new EditDoubleDialog(context, editDegrees, degrees);
		dialog.setOnDismissListener(this);
		dialog.setOnCancelListener((OnCancelListener) context);

		editDegrees.setOnClickListener(dialog);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.toolbox_brick_turn_right, null);
	}

	@Override
	public Brick clone() {
		return new TurnRightBrick(getSprite(), degrees);
	}

	public void onDismiss(DialogInterface dialog) {
		EditDoubleDialog doubleDialog = (EditDoubleDialog) dialog;
		degrees = doubleDialog.getValue();
		dialog.cancel();
	}

}
