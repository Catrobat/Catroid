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
package at.tugraz.ist.catroid.content.brick;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.ui.dialogs.brickdialogs.EditDoubleDialog;

public class ScaleCostumeBrick implements Brick, OnDismissListener {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private double scale;

	public ScaleCostumeBrick(Sprite sprite, double scale) {
		this.sprite = sprite;
		this.scale = scale;
	}

	public void execute() {
		sprite.setScale(scale);
		sprite.setToDraw(true);
	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public double getScale() {
		return scale;
	}

	public View getView(Context context, int brickId, BaseAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.construction_brick_scale_costume, null);
		EditText edit = (EditText) view.findViewById(R.id.EditText01);
		edit.setText(String.valueOf(scale));

		EditDoubleDialog dialog = new EditDoubleDialog(context, edit, scale);
		dialog.setOnDismissListener(this);
		dialog.setOnCancelListener((OnCancelListener) context);
		edit.setOnClickListener(dialog);

		return view;
	}

	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.toolbox_brick_scale_costume, null);
		return view;
	}

	@Override
	public Brick clone() {
		return new ScaleCostumeBrick(getSprite(), getScale());
	}

	public void onDismiss(DialogInterface dialog) {
		scale = ((EditDoubleDialog)dialog).getValue();
		dialog.cancel();
	}
}
