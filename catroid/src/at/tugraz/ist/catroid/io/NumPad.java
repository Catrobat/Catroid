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

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import at.tugraz.ist.catroid.R;

/**
 * Example of writing an input method for a soft keyboard. This code is
 * focused on simplicity over completeness, so it should in no way be considered
 * to be a complete soft keyboard implementation. Its purpose is to provide
 * a basic example for how you would get started writing an input method, to
 * be fleshed out as appropriate.
 */
public class NumPad extends InputMethodService implements KeyboardView.OnKeyboardActionListener {
	static final boolean DEBUG = false;

	/**
	 * This boolean indicates the optional example code for performing
	 * processing of hard keys in addition to regular text generation
	 * from on-screen interaction. It would be used for input methods that
	 * perform language translations (such as converting text entered on
	 * a QWERTY keyboard to Chinese), but may not be used for input methods
	 * that are primarily intended to be used for on-screen text entry.
	 */
	static final boolean PROCESS_HARD_KEYS = false;

	private KeyboardView numPadInputView;
	private StringBuilder composingText = new StringBuilder();
	private int lastDisplayWidth;
	private boolean capsLock;
	private long metaState;

	private LatinKeyboard symbolsKeyboard;
	private LatinKeyboard symbolsShiftedKeyboard;
	private LatinKeyboard currentKeyboard;

	private String wordSeparators;

	/**
	 * Main initialization of the input method component. Be sure to call
	 * to super class.
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		wordSeparators = getResources().getString(R.string.word_separators);
	}

	/**
	 * This is the point where you can do all of your UI initialization. It
	 * is called after creation and any configuration change.
	 */
	@Override
	public void onInitializeInterface() {
		if (symbolsKeyboard != null) {
			// Configuration changes can happen after the keyboard gets recreated,
			// so we need to be able to re-build the keyboards if the available
			// space has changed.
			int displayWidth = getMaxWidth();
			if (displayWidth == lastDisplayWidth) {
				return;
			}
			lastDisplayWidth = displayWidth;
		}
		symbolsKeyboard = new LatinKeyboard(this, R.xml.symbols);
		symbolsShiftedKeyboard = new LatinKeyboard(this, R.xml.symbols_shift);
	}

	/**
	 * Called by the framework when your view for creating input needs to
	 * be generated. This will be called the first time your input method
	 * is displayed, and every time it needs to be re-created such as due to
	 * a configuration change.
	 */
	@Override
	public View onCreateInputView() {
		numPadInputView = (KeyboardView) getLayoutInflater().inflate(R.layout.input, null);
		numPadInputView.setOnKeyboardActionListener(this);
		numPadInputView.setKeyboard(symbolsKeyboard);
		return numPadInputView;
	}

	/**
	 * Called by the framework when your view for showing candidates needs to
	 * be generated, like {@link #onCreateInputView}.
	 */
	@Override
	public View onCreateCandidatesView() {
		return null;
	}

	/**
	 * This is the main point where we do our initialization of the input method
	 * to begin operating on an application. At this point we have been
	 * bound to the client, and are now receiving all of the detailed information
	 * about the target of our edits.
	 */
	@Override
	public void onStartInput(EditorInfo attribute, boolean restarting) {
		super.onStartInput(attribute, restarting);

		// Reset our state.  We want to do this even if restarting, because
		// the underlying state of the text editor could have changed in any way.
		composingText.setLength(0);

		if (!restarting) {
			// Clear shift states.
			metaState = 0;
		}

		// We are now going to initialize our state based on the type of
		// text being edited.
		switch (attribute.inputType & EditorInfo.TYPE_MASK_CLASS) {
			case EditorInfo.TYPE_CLASS_NUMBER:
			case EditorInfo.TYPE_CLASS_DATETIME:
				// Numbers and dates default to the symbols keyboard, with
				// no extra features.
				currentKeyboard = symbolsKeyboard;
				break;

			case EditorInfo.TYPE_CLASS_PHONE:
				// Phones will also default to the symbols keyboard, though
				// often you will want to have a dedicated phone keyboard.
				currentKeyboard = symbolsKeyboard;
				break;

			default:
				currentKeyboard = symbolsKeyboard;
				updateShiftKeyState(attribute);
		}

		// Update the label on the enter key, depending on what the application
		// says it will do.
		currentKeyboard.setImeOptions(getResources(), attribute.imeOptions);
	}

	/**
	 * This is called when the user is done editing a field. We can use
	 * this to reset our state.
	 */
	@Override
	public void onFinishInput() {
		super.onFinishInput();

		// Clear current composing text and candidates.
		composingText.setLength(0);
		//updateCandidates();

		// We only hide the candidates window when finishing input on
		// a particular editor, to avoid popping the underlying application
		// up and down if the user is entering text into the bottom of
		// its window.
		setCandidatesViewShown(false);

		currentKeyboard = symbolsKeyboard;
		if (numPadInputView != null) {
			numPadInputView.closing();
		}
	}

	@Override
	public void onStartInputView(EditorInfo attribute, boolean restarting) {
		super.onStartInputView(attribute, restarting);
		// Apply the selected keyboard to the input view.
		numPadInputView.setKeyboard(currentKeyboard);
		numPadInputView.closing();
	}

	/**
	 * Deal with the editor reporting movement of its cursor.
	 */
	@Override
	public void onUpdateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int candidatesStart,
			int candidatesEnd) {
		super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd);

		// If the current selection in the text view changes, we should
		// clear whatever candidate text we have.
		if (composingText.length() > 0 && (newSelStart != candidatesEnd || newSelEnd != candidatesEnd)) {
			composingText.setLength(0);
			//updateCandidates();
			InputConnection ic = getCurrentInputConnection();
			if (ic != null) {
				ic.finishComposingText();
			}
		}
	}

	/**
	 * Use this to monitor key events being delivered to the application.
	 * We get first crack at them, and can either resume them or let them
	 * continue to the app.
	 * //
	 */
	//    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
	//        switch (keyCode) {
	//            case KeyEvent.KEYCODE_BACK:
	//                // The InputMethodService already takes care of the back
	//                // key for us, to dismiss the input method if it is shown.
	//                // However, our keyboard could be showing a pop-up window
	//                // that back should dismiss, so we first allow it to do that.
	//                if (event.getRepeatCount() == 0 && numPadInputView != null) {
	//                    if (numPadInputView.handleBack()) {
	//                        return true;
	//                    }
	//                }
	//                break;
	//                
	//            case KeyEvent.KEYCODE_DEL:
	//                // Special handling of the delete key: if we currently are
	//                // composing text for the user, we want to modify that instead
	//                // of let the application to the delete itself.
	//                if (composingText.length() > 0) {
	//                    onKey(Keyboard.KEYCODE_DELETE, null);
	//                    return true;
	//                }
	//                break;
	//                
	//            case KeyEvent.KEYCODE_ENTER:
	//                // Let the underlying text editor always handle these.
	//                return false;
	//                
	//            default:
	//                // For all other keys
	//
	//        }
	//        
	//        return super.onKeyDown(keyCode, event);
	//    }

	/**
	 * Use this to monitor key events being delivered to the application.
	 * We get first crack at them, and can either resume them or let them
	 * continue to the app.
	 */
	//    @Override public boolean onKeyUp(int keyCode, KeyEvent event) {
	//        // If we want to do transformations on text being entered with a hard
	//        // keyboard, we need to process the up events to update the meta key
	//        // state we are tracking.
	//        if (PROCESS_HARD_KEYS) {
	////            if (mPredictionOn) {
	////                mMetaState = MetaKeyKeyListener.handleKeyUp(mMetaState,
	////                        keyCode, event);
	////            }
	//        }
	//        
	//        return super.onKeyUp(keyCode, event);
	//    }

	/**
	 * Helper function to commit any text being composed in to the editor.
	 */
	private void commitTypedToEditor(InputConnection inputConnection) {
		if (composingText.length() > 0) {
			inputConnection.commitText(composingText, composingText.length());
			Log.i("NumPad_Info", "NumPad.commitTyped(): numComposing:" + composingText);
			composingText.setLength(0);
		}
	}

	/**
	 * Helper to update the shift state of our keyboard based on the initial
	 * editor state.
	 */
	private void updateShiftKeyState(EditorInfo attr) {
		if (attr != null && numPadInputView != null && symbolsKeyboard == numPadInputView.getKeyboard()) {
			int caps = 0;
			EditorInfo ei = getCurrentInputEditorInfo();
			if (ei != null && ei.inputType != EditorInfo.TYPE_NULL) {
				caps = getCurrentInputConnection().getCursorCapsMode(attr.inputType);
			}
			numPadInputView.setShifted(capsLock || caps != 0);
		}
	}

	/**
	 * Helper to determine if a given character code is alphabetic.
	 */
	//    private boolean isAlphabet(int code) {
	//        if (Character.isLetter(code)) {
	//            return true;
	//        } else {
	//            return false;
	//        }
	//    }

	/**
	 * Helper to send a key down / key up pair to the current editor.
	 */
	private void keyDownUp(int keyEventCode) {
		getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
		getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
	}

	/**
	 * Helper to send a character to the editor as raw key events.
	 */
	private void sendKey(int keyCode) {
		switch (keyCode) {
			case '\n':
				keyDownUp(KeyEvent.KEYCODE_ENTER);
				break;
			default:
				if (keyCode >= '0' && keyCode <= '9') {
					keyDownUp(keyCode - '0' + KeyEvent.KEYCODE_0);
				} else {
					getCurrentInputConnection().commitText(String.valueOf((char) keyCode), 1);
				}
				break;
		}
	}

	// Implementation of KeyboardViewListener

	public void onKey(int primaryCode, int[] keyCodes) {
		if (isWordSeparator(primaryCode)) {
			// Handle separator
			if (composingText.length() > 0) {
				commitTypedToEditor(getCurrentInputConnection());
			}
			sendKey(primaryCode);
			updateShiftKeyState(getCurrentInputEditorInfo());
		} else if (primaryCode == Keyboard.KEYCODE_DELETE) {
			handleBackspace();
		} else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
			handleShift();
		} else if (primaryCode == Keyboard.KEYCODE_CANCEL) {
			handleClose();
			return;
		} else if (primaryCode == LatinKeyboardView.KEYCODE_OPTIONS) {
			// Show a menu or somethin'
		} else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE && numPadInputView != null) {
			Keyboard current = numPadInputView.getKeyboard();
			//            if (current == mSymbolsKeyboard || current == mSymbolsShiftedKeyboard) {
			//                current = mQwertyKeyboard;
			//            } else {
			//                current = mSymbolsKeyboard;
			//            }
			numPadInputView.setKeyboard(current);
			if (current == symbolsKeyboard) {
				current.setShifted(false);
			}
		} else {
			handleCharacter(primaryCode, keyCodes);
		}
	}

	public void onText(CharSequence text) {
		InputConnection ic = getCurrentInputConnection();
		if (ic == null) {
			return;
		}
		ic.beginBatchEdit();
		if (composingText.length() > 0) {
			commitTypedToEditor(ic);
		}
		ic.commitText(text, 0);
		ic.endBatchEdit();
		updateShiftKeyState(getCurrentInputEditorInfo());
	}

	/**
	 * Update the list of available candidates from the current composing
	 * text. This will need to be filled in by however you are determining
	 * candidates.
	 */

	private void handleBackspace() {
		final int length = composingText.length();
		if (length > 1) {
			composingText.delete(length - 1, length);
			getCurrentInputConnection().setComposingText(composingText, 1);
		} else if (length > 0) {
			composingText.setLength(0);
			getCurrentInputConnection().commitText("", 0);
		} else {
			keyDownUp(KeyEvent.KEYCODE_DEL);
		}
		updateShiftKeyState(getCurrentInputEditorInfo());
	}

	private void handleShift() {
		if (numPadInputView == null) {
			return;
		}

		Keyboard currentKeyboard = numPadInputView.getKeyboard();

		if (currentKeyboard == symbolsKeyboard) {
			symbolsKeyboard.setShifted(true);
			numPadInputView.setKeyboard(symbolsShiftedKeyboard);
			symbolsShiftedKeyboard.setShifted(true);
		} else if (currentKeyboard == symbolsShiftedKeyboard) {
			symbolsShiftedKeyboard.setShifted(false);
			numPadInputView.setKeyboard(symbolsKeyboard);
			symbolsKeyboard.setShifted(false);
		}
	}

	private void handleCharacter(int primaryCode, int[] keyCodes) {
		//        if (isInputViewShown()) {
		//            if (numPadInputView.isShifted()) {
		//                primaryCode = Character.toUpperCase(primaryCode);
		//            }
		//        }
		//        if (isAlphabet(primaryCode) ){//&& mPredictionOn) {
		//            composingText.append((char) primaryCode);
		//            getCurrentInputConnection().setComposingText(composingText, 1);
		//            updateShiftKeyState(getCurrentInputEditorInfo());
		//        } else {

		getCurrentInputConnection().commitText(String.valueOf((char) primaryCode), 1);
		//        }
	}

	private void handleClose() {
		commitTypedToEditor(getCurrentInputConnection());
		requestHideSelf(0);
		numPadInputView.closing();
	}

	private String getWordSeparators() {
		return wordSeparators;
	}

	/*
	 * seperators = .,;:!?\n()[]*&@{}/<>_+=|"
	 */
	public boolean isWordSeparator(int code) {
		String separators = this.getWordSeparators();
		return separators.contains(String.valueOf((char) code));
	}

	public void pickDefaultCandidate() {
		//        pickSuggestionManually(0);
	}

	public void swipeRight() {
		//        if (mCompletionOn) {
		//            pickDefaultCandidate();
		//        }
	}

	public void swipeLeft() {
		handleBackspace();
	}

	public void swipeDown() {
		handleClose();
	}

	public void swipeUp() {
	}

	public void onPress(int primaryCode) {
	}

	public void onRelease(int primaryCode) {
	}
}
