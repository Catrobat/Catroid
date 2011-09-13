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

public class ChangeYByBrick implements Brick, OnDismissListener {
	private static final long serialVersionUID = 1L;
	private int yMovement;
	private Sprite sprite;
	public static final transient int BRICK_BEHAVIOUR = Brick.BACKGROUND_BRICK | Brick.NORMAL_BRICK;
	public static final transient int BRICK_RESSOURCES = NO_RESOURCES;

	@XStreamOmitField
	private transient View view;

	public ChangeYByBrick(Sprite sprite, int yMovement) {
		this.sprite = sprite;
		this.yMovement = yMovement;
	}

	public void execute() {
		int yPosition = sprite.getYPosition();

		if (yPosition > 0 && yMovement > 0 && yPosition + yMovement < 0) {
			yPosition = Integer.MAX_VALUE;
		} else if (yPosition < 0 && yMovement < 0 && yPosition + yMovement > 0) {
			yPosition = Integer.MIN_VALUE;
		} else {
			yPosition += yMovement;
		}

		sprite.setXYPosition(sprite.getXPosition(), yPosition);
	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public View getView(Context context, int brickId, BaseExpandableListAdapter adapter) {

		if (view == null) {
			view = View.inflate(context, R.layout.toolbox_brick_change_y, null);
		}

		EditText editY = (EditText) view.findViewById(R.id.toolbox_brick_change_y_edit_text);
		editY.setText(String.valueOf(yMovement));

		EditIntegerDialog dialogY = new EditIntegerDialog(context, editY, yMovement, true);
		dialogY.setOnDismissListener(this);
		dialogY.setOnCancelListener((OnCancelListener) context);

		editY.setOnClickListener(dialogY);

		return view;
	}

	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.toolbox_brick_change_y, null);
	}

	@Override
	public Brick clone() {
		return new ChangeYByBrick(getSprite(), yMovement);
	}

	public void onDismiss(DialogInterface dialog) {
		EditIntegerDialog inputDialog = (EditIntegerDialog) dialog;
		yMovement = inputDialog.getValue();
		dialog.cancel();
	}
}
