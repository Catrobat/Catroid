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

import android.content.Context;
import android.inputmethodservice.Keyboard;

public class CatKeyboard extends Keyboard {

	//	private Key mEnterKey;

	public CatKeyboard(Context context, int xmlLayoutResId) {
		super(context, xmlLayoutResId);
	}

	public CatKeyboard(Context context, int layoutTemplateResId, CharSequence characters, int columns,
			int horizontalPadding) {
		super(context, layoutTemplateResId, characters, columns, horizontalPadding);
	}

	//	@Override
	//	protected Key createKeyFromXml(Resources res, Row parent, int x, int y, XmlResourceParser parser) {
	//		Key key = new CatKey(res, parent, x, y, parser);
	//		if (key.codes[0] == 10) {
	//			mEnterKey = key;
	//		}
	//		return key;
	//	}

	//	/**
	//	 * This looks at the ime options given by the current editor, to set the
	//	 * appropriate label on the keyboard's enter key (if it has one).
	//	 */
	//	void setImeOptions(Resources res, int options) {
	//		if (mEnterKey == null) {
	//			return;
	//		}
	//
	//		switch (options & (EditorInfo.IME_MASK_ACTION | EditorInfo.IME_FLAG_NO_ENTER_ACTION)) {
	//			case EditorInfo.IME_ACTION_GO:
	//				mEnterKey.iconPreview = null;
	//				mEnterKey.icon = null;
	////				mEnterKey.label = res.getText(R.string.label_go_key);
	//				break;
	//			case EditorInfo.IME_ACTION_NEXT:
	//				mEnterKey.iconPreview = null;
	//				mEnterKey.icon = null;
	////				mEnterKey.label = res.getText(R.string.label_next_key);
	//				break;
	//			case EditorInfo.IME_ACTION_SEARCH:
	////				mEnterKey.icon = res.getDrawable(R.drawable.sym_keyboard_search);
	//				mEnterKey.label = null;
	//				break;
	//			case EditorInfo.IME_ACTION_SEND:
	//				mEnterKey.iconPreview = null;
	//				mEnterKey.icon = null;
	////				mEnterKey.label = res.getText(R.string.label_send_key);
	//				break;
	//			default:
	////				mEnterKey.icon = res.getDrawable(R.drawable.sym_keyboard_return);
	//				mEnterKey.label = null;
	//				break;
	//		}
	//	}

}
