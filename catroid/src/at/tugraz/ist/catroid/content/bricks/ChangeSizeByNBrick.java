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

public class ChangeSizeByNBrick extends Brick implements OnDismissListener {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private double size;

	private transient View view;

	public ChangeSizeByNBrick(Sprite sprite, double size) {
		super.brickBehavior = Brick.NORMAL_BRICK | Brick.BACKGROUND_BRICK;
		this.sprite = sprite;
		this.size = size;
	}

	@Override
	public void execute() {
		double newSize = sprite.getSize() + size;
		if (newSize < 0.01) {
			newSize = 0.01;
		}
		sprite.setSize(newSize);
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public View getView(Context context, int brickId, BaseExpandableListAdapter adapter) {

		if (view == null) {
			view = View.inflate(context, R.layout.toolbox_brick_change_size_by_n, null);
		}

		EditText edit = (EditText) view.findViewById(R.id.toolbox_brick_change_size_by_edit_text);
		edit.setText(String.valueOf(size));

		EditDoubleDialog dialog = new EditDoubleDialog(context, edit, size, true);
		dialog.setOnDismissListener(this);
		dialog.setOnCancelListener((OnCancelListener) context);

		edit.setOnClickListener(dialog);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.toolbox_brick_change_size_by_n, null);
	}

	@Override
	public Brick clone() {
		return new ChangeSizeByNBrick(getSprite(), size);
	}

	public void onDismiss(DialogInterface dialog) {
		size = ((EditDoubleDialog) dialog).getValue();
		dialog.cancel();
	}
}
