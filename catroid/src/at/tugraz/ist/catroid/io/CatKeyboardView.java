/*
 * Copyright (C) 2008-2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package at.tugraz.ist.catroid.io;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;

public class CatKeyboardView extends KeyboardView implements KeyboardView.OnKeyboardActionListener {

	static final int KEYCODE_OPTIONS = -100;
	FormulaEditorEditText editText = null;

	public CatKeyboardView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setOnKeyboardActionListener(this);

	}

	//    public CatKeyboardView(Context context, AttributeSet attrs, int defStyle) {
	//        super(context, attrs, defStyle);
	//    }

	public void setEditText(FormulaEditorEditText editText) {
		this.editText = editText;
	}

	@Override
	protected boolean onLongPress(Key key) {
		if (key.codes[0] == Keyboard.KEYCODE_CANCEL) {
			getOnKeyboardActionListener().onKey(KEYCODE_OPTIONS, null);
			return true;
		} else {
			Log.i("info", "CatKeyboard.onLongPress() called");
			return super.onLongPress(key);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i("info", "onKeyDown(), keyCode:" + String.valueOf(keyCode));
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (this.getVisibility() == KeyboardView.VISIBLE) {
					this.setVisibility(KeyboardView.GONE);
					return true;
				}
			default:
				return super.onKeyDown(keyCode, event);

		}

	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		return super.onKeyUp(keyCode, event);

	}

	@Override
	public void swipeDown() {
		// TODO Auto-generated method stub
		super.swipeDown();
	}

	@Override
	public void swipeLeft() {
		// TODO Auto-generated method stub
		super.swipeLeft();
	}

	@Override
	public void swipeRight() {
		// TODO Auto-generated method stub
		super.swipeRight();
	}

	@Override
	public void swipeUp() {
		// TODO Auto-generated method stub
		super.swipeUp();
	}

	public void onKey(int primaryCode, int[] keyCodes) {
		Log.i("info", "onKey: " + primaryCode);
	}

	public void onPress(int primaryCode) {
		Log.i("info", "onPress: " + primaryCode);

	}

	public void onRelease(int primaryCode) {
		// TODO Auto-generated method stub

	}

	public void onText(CharSequence text) {
		// TODO Auto-generated method stub

	}
}
