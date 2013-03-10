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
import org.catrobat.catroid.utils.Utils;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class TurnLeftBrick implements Brick, OnClickListener {

	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private double degrees;

	private transient View view;
	private transient View prototypeView;

	public TurnLeftBrick(Sprite sprite, double degrees) {
		this.sprite = sprite;
		this.degrees = degrees;
	}

	public TurnLeftBrick() {

	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public Sprite getSprite() {
		return sprite;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {

		view = View.inflate(context, R.layout.brick_turn_left, null);

		TextView textDegrees = (TextView) view.findViewById(R.id.brick_turn_left_prototype_text_view);
		EditText editDegrees = (EditText) view.findViewById(R.id.brick_turn_left_edit_text);
		editDegrees.setText(String.valueOf(degrees));

		TextView times = (TextView) view.findViewById(R.id.brick_turn_left_degree_text_view);
		times.setText(view.getResources().getQuantityString(R.plurals.brick_turn_left_degree_plural,
				Utils.convertDoubleToPluralInteger(degrees)));

		textDegrees.setVisibility(View.GONE);
		editDegrees.setVisibility(View.VISIBLE);
		editDegrees.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_turn_left, null);
		TextView textDegrees = (TextView) prototypeView.findViewById(R.id.brick_turn_left_prototype_text_view);
		textDegrees.setText(String.valueOf(degrees));
		TextView times = (TextView) prototypeView.findViewById(R.id.brick_turn_left_degree_text_view);
		times.setText(context.getResources().getQuantityString(R.plurals.brick_turn_left_degree_plural,
				Utils.convertDoubleToPluralInteger(degrees)));
		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new TurnLeftBrick(getSprite(), degrees);
	}

	@Override
	public void onClick(View view) {
		ScriptActivity activity = (ScriptActivity) view.getContext();

		BrickTextDialog editDialog = new BrickTextDialog() {
			@Override
			protected void initialize() {
				input.setText(String.valueOf(degrees));
				input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
				input.setSelectAllOnFocus(true);
			}

			@Override
			protected boolean handleOkButton() {
				try {
					degrees = Double.parseDouble(input.getText().toString());
				} catch (NumberFormatException exception) {
					Toast.makeText(getActivity(), R.string.error_no_number_entered, Toast.LENGTH_SHORT).show();
				}

				return true;
			}
		};

		editDialog.show(activity.getSupportFragmentManager(), "dialog_turn_left_brick");
	}

	@Override
	public SequenceAction addActionToSequence(SequenceAction sequence) {
		sequence.addAction(ExtendedActions.turnLeft(sprite, (float) degrees));
		return null;
	}

}
