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

import java.util.Locale;

import android.content.Context;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import at.tugraz.ist.catroid.R;

public class CatKeyboardView extends KeyboardView implements KeyboardView.OnKeyboardActionListener {

	//	static final int KEYCODE_OPTIONS = -100;
	FormulaEditorEditText editText;
	boolean isShifted;

	public CatKeyboardView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setOnKeyboardActionListener(this);
		editText = null;
		isShifted = false;

	}

	//    public CatKeyboardView(Context context, AttributeSet attrs, int defStyle) {
	//        super(context, attrs, defStyle);
	//    }

	public void setEditText(FormulaEditorEditText editText) {
		this.editText = editText;
	}

	@Override
	protected boolean onLongPress(Key key) {
		//			if (key.codes[0] == Keyboard.KEYCODE_CANCEL) {
		//				getOnKeyboardActionListener().onKey(KEYCODE_OPTIONS, null);
		//				return true;
		//			} else {
		Log.i("info", "CatKeyboard.onLongPress() called");
		return super.onLongPress(key);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.inputmethodservice.KeyboardView.OnKeyboardActionListener#onKey(int, int[])
	 */
	public void onKey(int primaryCode, int[] keyCodes) {
		Log.i("info", "CatKeyboarView.onKey(), primaryCode:" + String.valueOf(primaryCode));

		CatKeyEvent cKE = null;

		switch (primaryCode) {
			case KeyEvent.KEYCODE_0:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_0));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_1:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_1));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_2:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_2));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_3:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_3));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_4:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_4));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_5:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_5));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_6:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_6));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_7:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_7));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_8:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_8));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_9:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_9));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_DEL:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_COMMA:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_COMMA));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_PLUS:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_PLUS));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_MINUS:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MINUS));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_STAR:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_STAR));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_SLASH:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SLASH));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_PERIOD:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_PERIOD));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_ENTER:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_SHIFT_RIGHT:
				String displayLanguage = Locale.getDefault().getDisplayLanguage();
				if (displayLanguage.contentEquals(Locale.ENGLISH.getDisplayLanguage())) {
					if (!this.isShifted) {
						CatKeyboard shiftedCatKeyboard = new CatKeyboard(this.getContext(), R.xml.symbols_eng_shift);
						this.setKeyboard(shiftedCatKeyboard);
						this.isShifted = true;
					} else {
						CatKeyboard shiftedCatKeyboard = new CatKeyboard(this.getContext(), R.xml.symbols_eng);
						this.setKeyboard(shiftedCatKeyboard);
						this.isShifted = false;
					}
				} else if (displayLanguage.contentEquals(Locale.GERMAN.getDisplayLanguage())) {
					if (!this.isShifted) {
						CatKeyboard shiftedCatKeyboard = new CatKeyboard(this.getContext(), R.xml.symbols_de_shift);
						this.setKeyboard(shiftedCatKeyboard);
						this.isShifted = true;
					} else {
						CatKeyboard shiftedCatKeyboard = new CatKeyboard(this.getContext(), R.xml.symbols_de);
						this.setKeyboard(shiftedCatKeyboard);
						this.isShifted = false;
					}
				}

				requestLayout();
				break;
			case CatKeyEvent.KEYCODE_SIN:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_SIN));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_COS:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_COS));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_TAN:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_TAN));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_LN:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_LN));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_LOG:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_LOG));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_PI:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_PI));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_SQUAREROOT:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_SQUAREROOT));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_EULER:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_EULER));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_RANDOM:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_RANDOM));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_SENSOR1:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_SENSOR1));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_SENSOR2:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_SENSOR2));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_SENSOR3:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_SENSOR3));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_SENSOR4:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_SENSOR4));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_SENSOR5:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_SENSOR5));
				editText.checkAndModifyKeyInput(cKE);
				break;

		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i("info", "CatKeyboarView.onKeyDown(), keyCode:" + String.valueOf(keyCode));
		switch (keyCode) {
			default:
				return super.onKeyDown(keyCode, event);

		}

	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.i("info", "CatKeyboarView.onKeyUp(), keyCode:" + String.valueOf(keyCode));
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

	public void onPress(int primaryCode) {
		//Log.i("info", "onPress: " + primaryCode);

	}

	public void onRelease(int primaryCode) {
		// TODO Auto-generated method stub

	}

	public void onText(CharSequence text) {
		// TODO Auto-generated method stub

	}
}
