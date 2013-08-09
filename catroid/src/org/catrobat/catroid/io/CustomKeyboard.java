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
package org.catrobat.catroid.io;

import java.util.List;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.SendToPcBrick;
import org.catrobat.catroid.ui.dialogs.KeyboardDialog;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class CustomKeyboard extends InputMethodService {

	private KeyboardView keyboardView;
	private EditText inputField;
	private KeyboardDialog keyboardDialog;

	public CustomKeyboard(KeyboardDialog keyboardDialog, Context context, SendToPcBrick sendToPcBrick) {
		keyboardDialog.initialize(context);
		keyboardView = (KeyboardView) keyboardDialog.getDialogView().findViewById(R.id.keyboard_view);
		this.keyboardDialog = keyboardDialog;
		keyboardDialog.setSendToPcBrick(sendToPcBrick);
	}

	public void registerEditText(View view, EditText editField) {
		this.inputField = editField;
		inputField.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showCustomKeyboard(v);
			}
		});
		inputField.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				showCustomKeyboard(v);
				return true;
			}
		});
		setUpKeyboard(view);
	}

	public void setUpKeyboard(View inflatedView) {
		Activity activity = (Activity) (inflatedView.getContext());
		final Keyboard keyboard = new Keyboard(activity, R.xml.send_to_pc_keyboard);

		if (keyboard != null) {
			keyboardView.setKeyboard(keyboard);
			keyboardView.setPreviewEnabled(false);
			keyboardView.setOnKeyboardActionListener(new OnKeyboardActionListener() {
				private Key actualKey = null;

				@Override
				public void onKey(int primaryCode, int[] keyCodes) {
					keyboardDialog.setKey(primaryCode);
				}

				@Override
				public void onPress(int primaryCode) {
				}

				@Override
				public void onRelease(int primaryCode) {
					List<Key> keyList = keyboard.getKeys();
					if (actualKey != null) {
						actualKey.pressed = false;
					}
					for (Key key : keyList) {
						if (key.codes[0] == primaryCode && key != actualKey) {
							actualKey = key;
							actualKey.onPressed();
							key.pressed = true;
							break;
						}
					}
					keyboardView.invalidateAllKeys();
				}

				@Override
				public void onText(CharSequence text) {

				}

				@Override
				public void swipeDown() {

				}

				@Override
				public void swipeLeft() {

				}

				@Override
				public void swipeRight() {

				}

				@Override
				public void swipeUp() {

				}
			});
		}
		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	public void showCustomKeyboard(View view) {
		keyboardDialog.startDialog();
		Activity activity = (Activity) (view.getContext());
		keyboardView.setVisibility(View.VISIBLE);
		keyboardView.setEnabled(true);
		if (view != null) {
			((InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
					view.getWindowToken(), 0);
		}
	}

	public void hideCustomKeyboard() {
		keyboardView.setVisibility(View.GONE);
		keyboardView.setEnabled(false);
	}
}
