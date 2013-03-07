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

public class GlideToBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;
	private int xDestination;
	private int yDestination;
	private int durationInMilliSeconds;
	private Sprite sprite;

	private transient View view;
	private transient View prototype;

	public GlideToBrick() {

	}

	public GlideToBrick(Sprite sprite, int xDestination, int yDestination, int durationInMilliSeconds) {
		this.sprite = sprite;
		this.xDestination = xDestination;
		this.yDestination = yDestination;
		this.durationInMilliSeconds = durationInMilliSeconds;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	public int getDurationInMilliSeconds() {
		return durationInMilliSeconds;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {

		view = View.inflate(context, R.layout.brick_glide_to, null);

		TextView textX = (TextView) view.findViewById(R.id.brick_glide_to_prototype_text_view_x);
		EditText editX = (EditText) view.findViewById(R.id.brick_glide_to_edit_text_x);
		editX.setText(String.valueOf(xDestination));
		editX.setOnClickListener(this);

		TextView textY = (TextView) view.findViewById(R.id.brick_glide_to_prototype_text_view_y);
		EditText editY = (EditText) view.findViewById(R.id.brick_glide_to_edit_text_y);
		editY.setText(String.valueOf(yDestination));
		editY.setOnClickListener(this);

		TextView textDuration = (TextView) view.findViewById(R.id.brick_glide_to_prototype_text_view_duration);
		EditText editDuration = (EditText) view.findViewById(R.id.brick_glide_to_edit_text_duration);
		editDuration.setText(String.valueOf(durationInMilliSeconds / 1000.0));

		TextView times = (TextView) view.findViewById(R.id.brick_glide_to_seconds_text_view);
		times.setText(view.getResources().getQuantityString(R.plurals.second_plural,
				Utils.convertDoubleToPluralInteger(durationInMilliSeconds / 1000.0)));

		textDuration.setVisibility(View.GONE);
		editDuration.setVisibility(View.VISIBLE);
		textX.setVisibility(View.GONE);
		editX.setVisibility(View.VISIBLE);
		textY.setVisibility(View.GONE);
		editY.setVisibility(View.VISIBLE);

		editDuration.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return setDefaultValues(context);
	}

	@Override
	public Brick clone() {
		return new GlideToBrick(getSprite(), xDestination, yDestination, getDurationInMilliSeconds());
	}

	@Override
	public View setDefaultValues(Context context) {
		prototype = View.inflate(context, R.layout.brick_glide_to, null);
		TextView textX = (TextView) prototype.findViewById(R.id.brick_glide_to_prototype_text_view_x);
		textX.setText(xDestination + "");
		TextView textY = (TextView) prototype.findViewById(R.id.brick_glide_to_prototype_text_view_y);
		textY.setText(yDestination + "");
		TextView textDuration = (TextView) prototype.findViewById(R.id.brick_glide_to_prototype_text_view_duration);
		textDuration.setText((durationInMilliSeconds / 1000) + "");
		return prototype;
	}

	@Override
	public void onClick(final View view) {
		ScriptActivity activity = (ScriptActivity) view.getContext();

		BrickTextDialog editDialog = new BrickTextDialog() {
			@Override
			protected void initialize() {
				if (view.getId() == R.id.brick_glide_to_edit_text_x) {
					input.setText(String.valueOf(xDestination));
					input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
				} else if (view.getId() == R.id.brick_glide_to_edit_text_y) {
					input.setText(String.valueOf(yDestination));
					input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
				} else if (view.getId() == R.id.brick_glide_to_edit_text_duration) {
					input.setText(String.valueOf(durationInMilliSeconds / 1000.0));
					input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL
							| InputType.TYPE_NUMBER_FLAG_SIGNED);
				}

				input.setSelectAllOnFocus(true);
			}

			@Override
			protected boolean handleOkButton() {
				try {
					if (view.getId() == R.id.brick_glide_to_edit_text_x) {
						xDestination = Integer.parseInt(input.getText().toString());
					} else if (view.getId() == R.id.brick_glide_to_edit_text_y) {
						yDestination = Integer.parseInt(input.getText().toString());
					} else if (view.getId() == R.id.brick_glide_to_edit_text_duration) {
						durationInMilliSeconds = (int) Math
								.round(Double.parseDouble(input.getText().toString()) * 1000);
					}
				} catch (NumberFormatException exception) {
					Toast.makeText(getActivity(), R.string.error_no_number_entered, Toast.LENGTH_SHORT).show();
				}

				return true;
			}
		};

		editDialog.show(activity.getSupportFragmentManager(), "dialog_glide_to_brick");
	}

	@Override
	public SequenceAction addActionToSequence(SequenceAction sequence) {
		float durationInSeconds = durationInMilliSeconds / 1000f;
		sequence.addAction(ExtendedActions.glideTo(Float.valueOf(xDestination), Float.valueOf(yDestination),
				durationInSeconds));
		return null;
	}
}
