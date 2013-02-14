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
import org.catrobat.catroid.content.actions.ExtendedActions;
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

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class PlaceAtBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;
	private int xPosition;
	private int yPosition;
	private Sprite sprite;

	private transient View view;

	public PlaceAtBrick() {

	}

	public PlaceAtBrick(Sprite sprite, int xPosition, int yPosition) {
		this.sprite = sprite;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		//		sprite.costume.aquireXYWidthHeightLock();
		//		sprite.costume.setXYPosition(xPosition, yPosition);
		//		sprite.costume.releaseXYWidthHeightLock();
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {

		view = View.inflate(context, R.layout.brick_place_at, null);
		TextView textX = (TextView) view.findViewById(R.id.brick_place_at_prototype_text_view_x);
		EditText editX = (EditText) view.findViewById(R.id.brick_place_at_edit_text_x);
		editX.setText(String.valueOf(xPosition));

		textX.setVisibility(View.GONE);
		editX.setVisibility(View.VISIBLE);
		editX.setOnClickListener(this);

		TextView textY = (TextView) view.findViewById(R.id.brick_place_at_prototype_text_view_y);
		EditText editY = (EditText) view.findViewById(R.id.brick_place_at_edit_text_y);
		editY.setText(String.valueOf(yPosition));

		textY.setVisibility(View.GONE);
		editY.setVisibility(View.VISIBLE);
		editY.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_place_at, null);
	}

	@Override
	public Brick clone() {
		return new PlaceAtBrick(getSprite(), xPosition, yPosition);
	}

	@Override
	public void onClick(final View view) {
		ScriptActivity activity = (ScriptActivity) view.getContext();

		BrickTextDialog editDialog = new BrickTextDialog() {
			@Override
			protected void initialize() {
				if (view.getId() == R.id.brick_place_at_edit_text_x) {
					input.setText(String.valueOf(xPosition));
				} else if (view.getId() == R.id.brick_place_at_edit_text_y) {
					input.setText(String.valueOf(yPosition));
				}
				input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
				input.setSelectAllOnFocus(true);
			}

			@Override
			protected boolean handleOkButton() {
				try {
					if (view.getId() == R.id.brick_place_at_edit_text_x) {
						xPosition = Integer.parseInt(input.getText().toString());
					} else if (view.getId() == R.id.brick_place_at_edit_text_y) {
						yPosition = Integer.parseInt(input.getText().toString());
					}
				} catch (NumberFormatException exception) {
					Toast.makeText(getActivity(), R.string.error_no_number_entered, Toast.LENGTH_SHORT).show();
				}

				return true;
			}
		};

		editDialog.show(activity.getSupportFragmentManager(), "dialog_place_at_brick");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.catrobat.catroid.content.bricks.Brick#addActionToSequence(com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
	 * )
	 */
	@Override
	public SequenceAction addActionToSequence(SequenceAction sequence) {
		sequence.addAction(ExtendedActions.placeAt(sprite, Float.valueOf(xPosition), Float.valueOf(yPosition)));
		return null;
	}
}
