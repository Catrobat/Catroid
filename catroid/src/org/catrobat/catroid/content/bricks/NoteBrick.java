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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class NoteBrick implements Brick {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private String note = "";

	private transient View view;

	public NoteBrick(Sprite sprite) {
		this.sprite = sprite;
	}

	public NoteBrick() {

	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	public NoteBrick(Sprite sprite, String note) {
		this.sprite = sprite;
		this.note = note;
	}

	@Override
	public void execute() {
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	public String getNote() {
		return this.note;
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter adapter) {

		view = View.inflate(context, R.layout.brick_note, null);

		TextView textHolder = (TextView) view.findViewById(R.id.brick_note_prototype_text_view);
		EditText editText = (EditText) view.findViewById(R.id.brick_note_edit_text);
		editText.setText(note);

		textHolder.setVisibility(View.GONE);
		editText.setVisibility(View.VISIBLE);

		editText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ScriptActivity activity = (ScriptActivity) view.getContext();

				BrickTextDialog editDialog = new BrickTextDialog() {
					@Override
					protected void initialize() {
						input.setText(note);
						input.setSelectAllOnFocus(true);
					}

					@Override
					protected boolean getPositiveButtonEnabled() {
						return true;
					}

					@Override
					protected TextWatcher getInputTextChangedListener(Button buttonPositive) {
						return new TextWatcher() {
							@Override
							public void onTextChanged(CharSequence s, int start, int before, int count) {
							}

							@Override
							public void beforeTextChanged(CharSequence s, int start, int count, int after) {
							}

							@Override
							public void afterTextChanged(Editable s) {
							}
						};
					}

					@Override
					protected boolean handleOkButton() {
						note = (input.getText().toString()).trim();
						return true;
					}
				};

				editDialog.show(activity.getSupportFragmentManager(), "dialog_note_brick");
			}
		});

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_note, null);
	}

	@Override
	public Brick clone() {
		return new NoteBrick(this.sprite, this.note);
	}
}
