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

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.io.CustomKeyboard;
import org.catrobat.catroid.ui.dialogs.KeyboardDialog;

import java.util.List;

public class SendToPcBrick extends BrickBaseType {

	private static final long serialVersionUID = 1L;
	private transient KeyboardView keyboardView;
	private transient EditText editTextKey;
	private transient CustomKeyboard keyboard;
	private int key;

	public SendToPcBrick(Sprite sprite) {
		this.sprite = sprite;
		this.key = CustomKeyboard.KEY_CONTROL;
		keyboard = null;
	}

	public SendToPcBrick(Sprite sprite, int key) {
		this.sprite = sprite;
		this.key = key;
		keyboard = null;
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
		return new SendToPcBrick(getSprite(), key);
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
		TextView textForSendKey = (TextView) view.findViewById(R.id.brick_send_to_pc_prototype_text_view);
		textForSendKey.setVisibility(View.GONE);
		editTextKey = (EditText) view.findViewById(R.id.brick_send_to_pc_edit_text);
		initializeForKeyboard();
		return view;
	}

	public void initializeForKeyboard() {
		editTextKey.setClickable(true);
		editTextKey.setEnabled(true);
		editTextKey.setFocusable(true);
		editTextKey.setVisibility(View.VISIBLE);
		KeyboardDialog keyboardDialog = new KeyboardDialog();
		if (keyboardDialog != null) {
			keyboard = new CustomKeyboard(keyboardDialog, view.getContext(), this);
		}
		keyboard.registerEditText(view, editTextKey);
		if (key != 0) {
			setEditText();
		}
	}

	public void hideCustomKeyboard() {
		keyboardView = (KeyboardView) view.findViewById(R.id.keyboard_view);
		keyboardView.setVisibility(View.GONE);
		keyboardView.setEnabled(false);
		Activity activity = (Activity) view.getContext();
		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	public void showCustomKeyboard(Context context) {
		Keyboard keyboard = new Keyboard(context, R.xml.send_to_pc_keyboard);
		keyboardView = (KeyboardView) view.findViewById(R.id.keyboard_view);
		keyboardView.setFocusable(false);
		keyboardView.setFocusableInTouchMode(false);
		keyboardView.setKeyboard(keyboard);
		keyboardView.setEnabled(true);
		keyboardView.setVisibility(View.VISIBLE);
		if (view != null) {
			((InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
					view.getWindowToken(), 0);
		}
	}

	public void setKey(int key) {
		this.key = key;
		setEditText();
	}

	public int getKey() {
		return key;
	}

	public void setEditText() {
		switch (key) {
			case CustomKeyboard.KEY_ALT:
				editTextKey.setText(R.string.key_alt);
				break;
			case CustomKeyboard.KEY_ALT_GR:
				editTextKey.setText(R.string.key_alt_gr);
				break;
			case CustomKeyboard.KEY_BACKSPACE:
				editTextKey.setText(R.string.key_back_space);
				break;
			case CustomKeyboard.KEY_TAB:
				editTextKey.setText(R.string.key_tab);
				break;
			case CustomKeyboard.KEY_ENTER:
				editTextKey.setText(R.string.key_enter);
				break;
			case CustomKeyboard.KEY_SHIFT:
				editTextKey.setText(R.string.key_shift);
				break;
			case CustomKeyboard.KEY_CONTROL:
				editTextKey.setText(R.string.key_control);
				break;
			case CustomKeyboard.KEY_CAPSLOCK:
				editTextKey.setText(R.string.key_caps_lock);
				break;
			case CustomKeyboard.KEY_ESCAPE:
				editTextKey.setText(R.string.key_escape);
				break;
			case CustomKeyboard.KEY_ARROW_UP:
				editTextKey.setText(R.string.key_arrow_up);
				break;
			case CustomKeyboard.KEY_SPACE:
				editTextKey.setText(R.string.key_space);
				break;
			case CustomKeyboard.KEY_ARROW_LEFT:
				editTextKey.setText(R.string.key_arrow_left);
				break;
			case CustomKeyboard.KEY_ARROW_RIGHT:
				editTextKey.setText(R.string.key_arrow_right);
				break;
			case CustomKeyboard.KEY_ARROW_DOWN:
				editTextKey.setText(R.string.key_arrow_down);
				break;
			default:
				break;
		}
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_send_to_pc, null);
		TextView textForSendKey = (TextView) prototypeView.findViewById(R.id.brick_send_to_pc_prototype_text_view);
		textForSendKey.setText(R.string.default_key_text);
		editTextKey = (EditText) prototypeView.findViewById(R.id.brick_send_to_pc_edit_text);
		editTextKey.setVisibility(View.GONE);
		prototypeView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		return prototypeView;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {
			View layout = view.findViewById(R.id.brick_send_to_pc_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);
			TextView textKey = (TextView) view.findViewById(R.id.brick_send_to_pc_label);
			editTextKey = (EditText) view.findViewById(R.id.brick_send_to_pc_edit_text);
			textKey.setTextColor(textKey.getTextColors().withAlpha(alphaValue));
			editTextKey.setTextColor(editTextKey.getTextColors().withAlpha(alphaValue));
			editTextKey.getBackground().setAlpha(alphaValue);
			this.alphaValue = (alphaValue);
		}

		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		sequence.addAction(ExtendedActions.sendToPc(this));
		return null;
	}
}
