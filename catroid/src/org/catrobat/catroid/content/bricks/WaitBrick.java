/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.dialogs.BrickTextDialog;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WaitBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;
	private int timeToWaitInMilliSeconds;
	private Sprite sprite;

	private transient View view;

	public WaitBrick(Sprite sprite, int timeToWaitInMilliseconds) {
		this.timeToWaitInMilliSeconds = timeToWaitInMilliseconds;
		this.sprite = sprite;
	}

	public WaitBrick() {

	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		long startTime = System.currentTimeMillis();
		int timeToWait = timeToWaitInMilliSeconds;
		while (System.currentTimeMillis() <= (startTime + timeToWait)) {
			if (!sprite.isAlive(Thread.currentThread())) {
				break;
			}
			if (sprite.isPaused) {
				timeToWait = timeToWait - (int) (System.currentTimeMillis() - startTime);
				while (sprite.isPaused) {
					if (sprite.isFinished) {
						return;
					}
					Thread.yield();
				}
				startTime = System.currentTimeMillis();
			}
			Thread.yield();
		}
	}

	@Override
	public Sprite getSprite() {
		return sprite;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {
		view = View.inflate(context, R.layout.brick_wait, null);

		TextView text = (TextView) view.findViewById(R.id.brick_wait_prototype_text_view);
		EditText edit = (EditText) view.findViewById(R.id.brick_wait_edit_text);
		edit.setText((timeToWaitInMilliSeconds / 1000.0) + "");

		text.setVisibility(View.GONE);
		edit.setVisibility(View.VISIBLE);
		edit.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_wait, null);
	}

	@Override
	public Brick clone() {
		return new WaitBrick(getSprite(), timeToWaitInMilliSeconds);
	}

	@Override
	public void setDefaultValues(Context context) {
		View prototype = View.inflate(context, R.layout.brick_wait, null);
		TextView textX = (TextView) prototype.findViewById(R.id.brick_wait_text_view);
		textX.setText(timeToWaitInMilliSeconds + "");
	}

	@Override
	public void onClick(View view) {
		ScriptActivity activity = (ScriptActivity) view.getContext();

		BrickTextDialog editDialog = new BrickTextDialog() {
			@Override
			protected void initialize() {
				input.setText(String.valueOf(timeToWaitInMilliSeconds / 1000.0));
				input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
				input.setSelectAllOnFocus(true);
			}

			@Override
			protected boolean handleOkButton() {
				try {
					timeToWaitInMilliSeconds = (int) (Double.parseDouble(input.getText().toString()) * 1000);
				} catch (NumberFormatException exception) {
					Toast.makeText(getActivity(), R.string.error_no_number_entered, Toast.LENGTH_SHORT).show();
				}

				return true;
			}
		};

		editDialog.show(activity.getSupportFragmentManager(), "dialog_wait_brick");
	}
}
