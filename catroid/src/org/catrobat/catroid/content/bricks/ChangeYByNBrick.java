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
import org.catrobat.catroid.content.Script;
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

public class ChangeYByNBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;
	private int yMovement;
	private Sprite sprite;

	private transient View view;

	public ChangeYByNBrick() {

	}

	public ChangeYByNBrick(Sprite sprite, int yMovement) {
		this.sprite = sprite;
		this.yMovement = yMovement;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		sprite.costume.aquireXYWidthHeightLock();
		int yPosition = (int) sprite.costume.getYPosition();

		if (yPosition > 0 && yMovement > 0 && yPosition + yMovement < 0) {
			yPosition = Integer.MAX_VALUE;
		} else if (yPosition < 0 && yMovement < 0 && yPosition + yMovement > 0) {
			yPosition = Integer.MIN_VALUE;
		} else {
			yPosition += yMovement;
		}

		sprite.costume.setXYPosition(sprite.costume.getXPosition(), yPosition);
		sprite.costume.releaseXYWidthHeightLock();
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		ChangeYByNBrick copyBrick = (ChangeYByNBrick) clone();
		copyBrick.sprite = sprite;
		return copyBrick;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {

		view = View.inflate(context, R.layout.brick_change_y, null);

		TextView textY = (TextView) view.findViewById(R.id.brick_change_y_prototype_text_view);
		EditText editY = (EditText) view.findViewById(R.id.brick_change_y_edit_text);
		editY.setText(String.valueOf(yMovement));

		textY.setVisibility(View.GONE);
		editY.setVisibility(View.VISIBLE);
		editY.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_change_y, null);
	}

	@Override
	public Brick clone() {
		return new ChangeYByNBrick(getSprite(), yMovement);
	}

	@Override
	public void onClick(View view) {
		ScriptActivity activity = (ScriptActivity) view.getContext();

		BrickTextDialog editDialog = new BrickTextDialog() {
			@Override
			protected void initialize() {
				input.setText(String.valueOf(yMovement));
				input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
				input.setSelectAllOnFocus(true);
			}

			@Override
			protected boolean handleOkButton() {
				try {
					yMovement = Integer.parseInt(input.getText().toString());
				} catch (NumberFormatException exception) {
					Toast.makeText(getActivity(), R.string.error_no_number_entered, Toast.LENGTH_SHORT).show();
				}

				return true;
			}
		};

		editDialog.show(activity.getSupportFragmentManager(), "dialog_change_y_by_brick");
	}
}
