/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.catrobat.catroid.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

import org.catrobat.catroid.ui.adapter.ProjectAdapter;

public class EditTextImeOverride extends EditText {
	private EditTextImeBackListener onImeBackListener;
	private ProjectAdapter.ViewHolder holder;
	private EditTextImeOverride editText;

	public EditTextImeOverride(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK &&
				event.getAction() == KeyEvent.ACTION_UP) {
			if (onImeBackListener != null)
				onImeBackListener.onImeBack(holder, editText);
		}
		return super.dispatchKeyEvent(event);
	}

	public void setOnEditTextImeBackListener(EditTextImeBackListener listener, ProjectAdapter.ViewHolder holder,
			EditTextImeOverride editText) {
		this.holder = holder;
		this.editText = editText;
		onImeBackListener = listener;
	}

	public interface EditTextImeBackListener {
		void onImeBack(ProjectAdapter.ViewHolder holder, EditTextImeOverride editText);
	}
}
