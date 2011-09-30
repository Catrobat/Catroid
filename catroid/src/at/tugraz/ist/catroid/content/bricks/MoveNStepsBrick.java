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
import android.content.DialogInterface.OnCancelListener;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Toast;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.utils.Utils;

public class MoveNStepsBrick implements Brick, OnClickListener {

	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private double steps;

	private transient View view;

	public MoveNStepsBrick(Sprite sprite, double steps) {
		this.sprite = sprite;
		this.steps = steps;
	}

	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	public void execute() {
		sprite.costume.aquireXYWidthHeightLock();

		double radians = Math.toRadians(sprite.costume.rotation);

		int newXPosition = (int) Math.round(sprite.costume.getXPosition() + steps * Math.cos(radians));
		int newYPosition = (int) Math.round(sprite.costume.getYPosition() + steps * Math.sin(radians));

		sprite.costume.setXYPosition(newXPosition, newYPosition);
		sprite.costume.releaseXYWidthHeightLock();

	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public View getView(Context context, int brickId, BaseAdapter adapter) {

		view = View.inflate(context, R.layout.toolbox_brick_move_n_steps, null);

		EditText edit = (EditText) view.findViewById(R.id.toolbox_brick_move_n_steps_edit_text);

		edit.setText(String.valueOf(steps));

		edit.setOnClickListener(this);

		return view;
	}

	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.toolbox_brick_move_n_steps, null);
		return view;
	}

	@Override
	public Brick clone() {
		return new MoveNStepsBrick(getSprite(), steps);
	}

	public void onClick(View view) {
		final Context context = view.getContext();

		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		final EditText input = new EditText(context);
		input.setText(String.valueOf(steps));
		input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL
				| InputType.TYPE_NUMBER_FLAG_SIGNED);
		input.setSelectAllOnFocus(true);
		dialog.setView(input);
		dialog.setOnCancelListener((OnCancelListener) context);
		dialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				try {
					steps = Double.parseDouble(input.getText().toString());
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

		AlertDialog finishedDialog = dialog.create();
		finishedDialog.setOnShowListener(Utils.getBrickDialogOnClickListener(context, input));

		finishedDialog.show();

	}

}
