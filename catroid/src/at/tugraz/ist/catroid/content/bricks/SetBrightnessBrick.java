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

public class SetBrightnessBrick implements Brick, OnDismissListener {
	private static final long serialVersionUID = 1L;
	private double brightnessValue;
	private Sprite sprite;

	private transient View view;

	public SetBrightnessBrick(Sprite sprite, double brightnessValue) {
		this.sprite = sprite;
		this.brightnessValue = brightnessValue;
	}

	public void execute() {
		sprite.costume.setBrightnessValue((float) this.brightnessValue / 100);
	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public double getBrightnessValue() {
		return brightnessValue;
	}

	public View getView(Context context, int brickId, BaseExpandableListAdapter adapter) {

		if (view == null) {
			view = View.inflate(context, R.layout.toolbox_brick_set_brightness, null);
		}

		EditText editX = (EditText) view.findViewById(R.id.toolbox_brick_set_brightness_edit_text);
		editX.setText(String.valueOf(brightnessValue));

		EditDoubleDialog dialogX = new EditDoubleDialog(context, editX, brightnessValue, true);
		dialogX.setOnDismissListener(this);
		dialogX.setOnCancelListener((OnCancelListener) context);

		editX.setOnClickListener(dialogX);

		return view;
	}

	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.toolbox_brick_set_brightness, null);
	}

	@Override
	public Brick clone() {
		return new SetBrightnessBrick(getSprite(), getBrightnessValue());
	}

	public void onDismiss(DialogInterface dialog) {
		EditDoubleDialog inputDialog = (EditDoubleDialog) dialog;
		this.brightnessValue = inputDialog.getValue();

		dialog.cancel();
	}
}
