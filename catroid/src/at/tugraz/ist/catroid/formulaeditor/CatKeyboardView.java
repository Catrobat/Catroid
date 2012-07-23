/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

package at.tugraz.ist.catroid.formulaeditor;

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
	private FormulaEditorEditText editText;
	//	boolean isShifted;
	private CatKeyboard symbolsNumbers;
	//	CatKeyboard symbols_shifted;
	private CatKeyboard symbolsFunctions;
	private CatKeyboard symbolsSensors;

	public CatKeyboardView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setOnKeyboardActionListener(this);
		this.editText = null;
		//		this.isShifted = false;
		this.symbolsNumbers = null;
		//		this.symbols_shifted = null;

		if (Locale.getDefault().getDisplayLanguage().contentEquals(Locale.GERMAN.getDisplayLanguage())) {
			this.symbolsNumbers = new CatKeyboard(this.getContext(), R.xml.symbols_de_numbers);
			//			this.symbols_shifted = new CatKeyboard(this.getContext(), R.xml.symbols_de_shift);
			this.symbolsFunctions = new CatKeyboard(this.getContext(), R.xml.symbols_de_functions);
			this.symbolsSensors = new CatKeyboard(this.getContext(), R.xml.symbols_de_sensors);
			//			Log.i("info", "FormulaEditorDialog.onCreate() - DisplayLanguage is DE");
		} else if (Locale.getDefault().getDisplayLanguage().contentEquals(Locale.ENGLISH.getDisplayLanguage())) {
			this.symbolsNumbers = new CatKeyboard(this.getContext(), R.xml.symbols_eng_numbers);
			//			this.symbols_shifted = new CatKeyboard(this.getContext(), R.xml.symbols_eng_shift);
			this.symbolsFunctions = new CatKeyboard(this.getContext(), R.xml.symbols_eng_functions);
			this.symbolsSensors = new CatKeyboard(this.getContext(), R.xml.symbols_eng_sensors);
			//			Log.i("info", "FormulaEditorDialog.onCreate() - DisplayLanguage is ENG");
		}
		Log.i("info", "CatKeyBoardView() - DisplayLanguage:" + Locale.getDefault().getDisplayLanguage());
		this.setKeyboard(symbolsNumbers);
		//		this.symbols.setShifted(false);
		//		this.symbols_shifted.setShifted(true);
		//		this.setBackgroundColor(0xFF6103);
		//		this.awakenScrollBars();
		//
		//		ArrayList<Key> keys = (ArrayList<Key>) this.symbols.getKeys();
		//
		//				for (int i = 0; i < keys.size(); i++) {
		//					Key key = keys.get(i);
		//					key.iconPreview = key.icon;
		//					key.popupCharacters = key.label;
		//				}

		//    public CatKeyboardView(Context context, AttributeSet attrs, int defStyle) {
		//        super(context, attrs, defStyle);
	}

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
			case KeyEvent.KEYCODE_POWER:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_POWER));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_SHIFT_RIGHT:
				this.handleKeyboardChange();

				//				String displayLanguage = Locale.getDefault().getDisplayLanguage();
				//				if (displayLanguage.contentEquals(Locale.ENGLISH.getDisplayLanguage())) {
				//				if (!this.isShifted) {
				//					CatKeyboard shiftedCatKeyboard = new CatKeyboard(this.getContext(), R.xml.symbols_eng_shift);
				//					this.setKeyboard(shiftedCatKeyboard);
				//					this.isShifted = true;
				//					//						this.setShifted(true);
				//					//						this.symbols.setShifted(true);
				//					//						this.symbols_shifted.setShifted(true);
				//
				//				} else {
				//					CatKeyboard shiftedCatKeyboard = new CatKeyboard(this.getContext(), R.xml.symbols_eng_numbers);
				//					this.setKeyboard(shiftedCatKeyboard);
				//					this.isShifted = false;
				//					//						this.setShifted(false);
				//					//						this.symbols.setShifted(false);
				//					//						this.symbols_shifted.setShifted(false);
				//				}
				//			} else if (displayLanguage.contentEquals(Locale.GERMAN.getDisplayLanguage())) {
				//				if (!this.isShifted) {
				//					CatKeyboard shiftedCatKeyboard = new CatKeyboard(this.getContext(), R.xml.symbols_de_shift);
				//					this.setKeyboard(shiftedCatKeyboard);
				//					this.isShifted = true;
				//					//						this.setShifted(true);
				//					//						this.symbols.setShifted(true);
				//					//						this.symbols_shifted.setShifted(true);
				//				} else {
				//					CatKeyboard shiftedCatKeyboard = new CatKeyboard(this.getContext(), R.xml.symbols_de);
				//					this.setKeyboard(shiftedCatKeyboard);
				//					this.isShifted = false;
				//					//						this.setShifted(false);
				//					//						this.symbols.setShifted(false);
				//					//						this.symbols_shifted.setShifted(false);
				//
				//				}
				//			}

				requestLayout();
				break;
			case CatKeyEvent.KEYCODE_SPACE:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_SPACE));
				editText.checkAndModifyKeyInput(cKE);
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
			case CatKeyEvent.KEYCODE_SENSOR6:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_SENSOR6));
				editText.checkAndModifyKeyInput(cKE);
				break;
		}

	}

	/**
	 * 
	 */
	private void handleKeyboardChange() {

		if (this.getKeyboard() == this.symbolsNumbers) {
			Log.i("info", "Keyboard change from Numbers -> Fuctions");
			this.setKeyboard(this.symbolsFunctions);
			return;
		}
		if (this.getKeyboard() == this.symbolsFunctions) {
			Log.i("info", "Keyboard change from Functions -> Sensors");
			this.setKeyboard(this.symbolsSensors);
			return;
		}
		if (this.getKeyboard() == this.symbolsSensors) {
			Log.i("info", "Keyboard change from Sensors -> Numbers");
			this.setKeyboard(this.symbolsNumbers);
			return;
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

		//		super.swipeDown();
	}

	@Override
	public void swipeLeft() {
		Log.i("info", "swipeRight()");

		if (this.getKeyboard() == this.symbolsNumbers) {
			this.setKeyboard(this.symbolsSensors);
			return;
		}
		if (this.getKeyboard() == this.symbolsFunctions) {
			this.setKeyboard(this.symbolsNumbers);
			return;
		}
		if (this.getKeyboard() == this.symbolsSensors) {
			this.setKeyboard(this.symbolsFunctions);
			return;
		}

	}

	@Override
	public void swipeRight() {

		Log.i("info", "swipeRight()");
		this.onKey(KeyEvent.KEYCODE_SHIFT_RIGHT, null);
		//		super.swipeRight();
	}

	@Override
	public void swipeUp() {

		//		super.swipeUp();
	}

	public void onPress(int primaryCode) {
		//		Log.i("info", "CatKeybaordView.onPress(): " + primaryCode);

	}

	public void onRelease(int primaryCode) {
		//		Log.i("info", "CatKeybaordView.onRelease(): " + primaryCode);

	}

	public void onText(CharSequence text) {
		//		Log.i("info", "CatKeybaordView.onText(): ");

	}
}
