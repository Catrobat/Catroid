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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.io.SoundManager;
import at.tugraz.ist.catroid.ui.dialogs.EditDoubleDialog;

public class SetVolumeToBrick implements Brick, OnDismissListener {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private double volume;

	public SetVolumeToBrick(Sprite sprite, double volume) {
		this.sprite = sprite;
		this.volume = volume;
	}

	public void execute() {
		if (volume < 0.0) {
			volume = 0.0;
		} else if (volume > 100.0) {
			volume = 100.0;
		}
		SoundManager.getInstance().setVolume(getVolume());
	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public double getVolume() {
		return volume;
	}

	public View getView(Context context, int brickId, BaseExpandableListAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.construction_brick_set_volume_to, null);

		EditText edit = (EditText) view.findViewById(R.id.construction_brick_set_volume_to_edit_text);
		edit.setText(String.valueOf(volume));

		EditDoubleDialog dialog = new EditDoubleDialog(context, edit, volume, false);
		dialog.setOnDismissListener(this);
		dialog.setOnCancelListener((OnCancelListener) context);

		edit.setOnClickListener(dialog);

		return view;
	}

	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.toolbox_brick_set_volume_to, null);
		return view;
	}

	@Override
	public Brick clone() {
		return new SetVolumeToBrick(getSprite(), getVolume());
	}

	public void onDismiss(DialogInterface dialog) {
		EditDoubleDialog inputDialog = (EditDoubleDialog) dialog;
		volume = inputDialog.getValue();
		dialog.cancel();
	}
}
