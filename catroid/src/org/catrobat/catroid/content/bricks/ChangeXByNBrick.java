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

public class ChangeXByNBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;
	private int xMovement;
	private Sprite sprite;

	private transient View view;

	public ChangeXByNBrick() {

	}

	public ChangeXByNBrick(Sprite sprite, int xMovement) {
		this.sprite = sprite;
		this.xMovement = xMovement;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		sprite.look.aquireXYWidthHeightLock();
		int xPosition = (int) sprite.look.getXPosition();

		if (xPosition > 0 && xMovement > 0 && xPosition + xMovement < 0) {
			xPosition = Integer.MAX_VALUE;
		} else if (xPosition < 0 && xMovement < 0 && xPosition + xMovement > 0) {
			xPosition = Integer.MIN_VALUE;
		} else {
			xPosition += xMovement;
		}

		sprite.look.setXYPosition(xPosition, sprite.look.getYPosition());
		sprite.look.releaseXYWidthHeightLock();
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {

		view = View.inflate(context, R.layout.brick_change_x, null);

		TextView textX = (TextView) view.findViewById(R.id.brick_change_x_prototype_text_view);
		EditText editX = (EditText) view.findViewById(R.id.brick_change_x_edit_text);
		editX.setText(String.valueOf(xMovement));

		textX.setVisibility(View.GONE);
		editX.setVisibility(View.VISIBLE);
		editX.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_change_x, null);
	}

	@Override
	public Brick clone() {
		return new ChangeXByNBrick(getSprite(), xMovement);
	}

	@Override
	public void onClick(View view) {
		ScriptActivity activity = (ScriptActivity) view.getContext();

		BrickTextDialog editDialog = new BrickTextDialog() {
			@Override
			protected void initialize() {
				input.setText(String.valueOf(xMovement));
				input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
				input.setSelectAllOnFocus(true);
			}

			@Override
			protected boolean handleOkButton() {
				try {
					xMovement = Integer.parseInt(input.getText().toString());
				} catch (NumberFormatException exception) {
					Toast.makeText(getActivity(), R.string.error_no_number_entered, Toast.LENGTH_SHORT).show();
				}

				return true;
			}
		};

		editDialog.show(activity.getSupportFragmentManager(), "dialog_change_x_by_brick");
	}
}
