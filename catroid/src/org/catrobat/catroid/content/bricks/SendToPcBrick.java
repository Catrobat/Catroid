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

import java.util.List;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.ui.dialogs.BrickTextDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class SendToPcBrick extends BrickBaseType implements OnClickListener {

	private static final long serialVersionUID = 1L;
	private int key = 0;

	public SendToPcBrick(Sprite sprite) {
		this.sprite = sprite;
	}

	public SendToPcBrick() {

	}

	@Override
	public int getRequiredResources() {
		return CONNECTION_TO_PC;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		SendToPcBrick copyBrick = (SendToPcBrick) clone();
		copyBrick.sprite = sprite;
		return copyBrick;
	}

	@Override
	public Brick clone() {
		return new SendToPcBrick(getSprite());
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_send_to_pc, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_send_to_pc_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});
		TextView text = (TextView) view.findViewById(R.id.brick_send_to_pc_prototype_text_view);
		EditText edit = (EditText) view.findViewById(R.id.brick_send_to_pc_edit_text);

		text.setVisibility(View.GONE);
		edit.setVisibility(View.VISIBLE);
		edit.setOnClickListener(this);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_send_to_pc, null);
		TextView textSendToPc = (TextView) prototypeView.findViewById(R.id.brick_send_to_pc_prototype_text_view);
		textSendToPc.setText(R.string.default_key);
		return prototypeView;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_send_to_pc_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textKey = (TextView) view.findViewById(R.id.brick_send_to_pc_label);
			EditText editKey = (EditText) view.findViewById(R.id.brick_send_to_pc_edit_text);
			textKey.setTextColor(textKey.getTextColors().withAlpha(alphaValue));
			editKey.setTextColor(editKey.getTextColors().withAlpha(alphaValue));
			editKey.getBackground().setAlpha(alphaValue);

			this.alphaValue = (alphaValue);

		}

		return view;
	}

	@Override
	public void onClick(View v) {

		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
		showSelectLetterDialog(view.getContext());
	}

	private void showSelectLetterDialog(final Context context) {
		BrickTextDialog editDialog = new BrickTextDialog() {

			@Override
			protected void initialize() {
			}

			@Override
			protected boolean handleOkButton() {
				final char newLetter;
				try {
					newLetter = (input.getText().charAt(0));
				} catch (IndexOutOfBoundsException e) {
					dismiss();
					return false;
				}
				command = newLetter;
				letter = newLetter;
				return true;
			}

			@Override
			public void onDismiss(DialogInterface dialog) {
				super.onDismiss(dialog);
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
						if (s.length() > 1) {
							s.replace(0, s.length(), String.valueOf(s.charAt(s.length() - 1)));
						}
					}
				};
			}
		};

		editDialog.show(((FragmentActivity) context).getSupportFragmentManager(), "dialog_send_brick");
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		sequence.addAction(ExtendedActions.sendToPc(sprite));
		return null;
	}
}
