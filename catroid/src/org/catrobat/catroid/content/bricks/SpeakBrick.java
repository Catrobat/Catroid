/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.dialogs.BrickTextDialog;

import java.util.List;

public class SpeakBrick extends BrickBaseType {
	private static final long serialVersionUID = 1L;
	private String text = "";

	private transient View prototypeView;

	public SpeakBrick(String text) {
		this.text = text;
	}

	public SpeakBrick() {
	}

	@Override
	public int getRequiredResources() {
		return TEXT_TO_SPEECH;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		SpeakBrick copyBrick = (SpeakBrick) clone();
		return copyBrick;
	}

	public String getText() {
		return text;
	}

	@Override
	public View getView(final Context context, int brickId, final BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		view = View.inflate(context, R.layout.brick_speak, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_speak_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView textHolder = (TextView) view.findViewById(R.id.brick_speak_prototype_text_view);
		TextView textField = (TextView) view.findViewById(R.id.brick_speak_edit_text);
		textField.setText(text);

		textHolder.setVisibility(View.GONE);
		textField.setVisibility(View.VISIBLE);

		textField.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (checkbox.getVisibility() == View.VISIBLE) {
					return;
				}
				ScriptActivity activity = (ScriptActivity) context;

				BrickTextDialog editDialog = new BrickTextDialog() {
					@Override
					protected void initialize() {
						input.setText(text);
						input.setSelectAllOnFocus(true);
						inputTitle.setText(R.string.dialog_edit_speak_text);
					}

					@Override
					protected boolean handleOkButton() {
						text = (input.getText().toString()).trim();
						return true;
					}

					@Override
					protected String getTitle() {
						return getString(R.string.dialog_edit_speak_title);
					}
				};

				editDialog.show(activity.getSupportFragmentManager(), "dialog_speak_brick");
			}
		});
		return view;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_speak_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textSpeak = (TextView) view.findViewById(R.id.brick_speak_label);
			TextView editDegrees = (TextView) view.findViewById(R.id.brick_speak_edit_text);

			textSpeak.setTextColor(textSpeak.getTextColors().withAlpha(alphaValue));
			editDegrees.setTextColor(editDegrees.getTextColors().withAlpha(alphaValue));
			editDegrees.getBackground().setAlpha(alphaValue);

			this.alphaValue = (alphaValue);

		}

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_speak, null);
		TextView textSpeak = (TextView) prototypeView.findViewById(R.id.brick_speak_prototype_text_view);
		textSpeak.setText(text);
		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new SpeakBrick(this.text);
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.speak(text));
		return null;
	}

}
