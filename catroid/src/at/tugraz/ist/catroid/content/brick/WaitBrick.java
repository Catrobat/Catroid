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
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.exception.InterruptedRuntimeException;
import at.tugraz.ist.catroid.ui.dialogs.EditDoubleDialog;

public class WaitBrick implements Brick, OnDismissListener {
	private static final long serialVersionUID = 1L;
	private int timeToWaitInMilliSeconds;
	private Sprite sprite;

	public WaitBrick(Sprite sprite, int timeToWaitInMilliseconds) {
		this.timeToWaitInMilliSeconds = timeToWaitInMilliseconds;
		this.sprite = sprite;
	}

	public void execute() {
		long startTime = 0;
		try {
			startTime = System.currentTimeMillis();
			Thread.sleep(timeToWaitInMilliSeconds);
		} catch (InterruptedException e) {
			timeToWaitInMilliSeconds = timeToWaitInMilliSeconds - (int) (System.currentTimeMillis() - startTime);
			throw new InterruptedRuntimeException("WaitBrick was interrupted", e);
		}
	}

	public Sprite getSprite() {
		return sprite;
	}

	public long getWaitTime() {
		return timeToWaitInMilliSeconds;
	}

	public View getView(Context context, int brickId, BaseExpandableListAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.construction_brick_wait, null);

		EditText edit = (EditText) view.findViewById(R.id.InputValueEditText);
		edit.setText((timeToWaitInMilliSeconds / 1000.0) + "");

		EditDoubleDialog dialog = new EditDoubleDialog(context, edit, timeToWaitInMilliSeconds / 1000.0);
		dialog.setOnDismissListener(this);
		dialog.setOnCancelListener((OnCancelListener) context);

		edit.setOnClickListener(dialog);

		return view;
	}

	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.toolbox_brick_wait, null);
		return view;
	}

	@Override
	public Brick clone() {
		return new WaitBrick(getSprite(), timeToWaitInMilliSeconds);
	}

	public void onDismiss(DialogInterface dialog) {
		timeToWaitInMilliSeconds = (int) Math.round(((EditDoubleDialog) dialog).getValue() * 1000);
		dialog.cancel();
	}
}
