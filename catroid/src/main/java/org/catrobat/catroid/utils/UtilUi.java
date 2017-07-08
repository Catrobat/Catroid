/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
package org.catrobat.catroid.utils;

import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.util.DisplayMetrics;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.ScreenValues;

public final class UtilUi {

	// Suppress default constructor for noninstantiability
	private UtilUi() {
		throw new AssertionError();
	}

	public static void updateScreenWidthAndHeight(Context context) {
		if (context != null) {
			WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			DisplayMetrics displayMetrics = new DisplayMetrics();
			windowManager.getDefaultDisplay().getMetrics(displayMetrics);
			ScreenValues.SCREEN_WIDTH = displayMetrics.widthPixels;
			ScreenValues.SCREEN_HEIGHT = displayMetrics.heightPixels;
		} else {
			//a null-context should never be passed. However, an educated guess is needed in that case.
			ScreenValues.setToDefaultSreenSize();
		}
	}

	public static View addSelectAllActionModeButton(LayoutInflater inflater, ActionMode mode, Menu menu) {
		mode.getMenuInflater().inflate(R.menu.menu_actionmode, menu);
		MenuItem item = menu.findItem(R.id.select_all);
		View view = item.getActionView();
		if (view.getId() == R.id.select_all) {
			View selectAllView = View.inflate(inflater.getContext(), R.layout.action_mode_select_all, null);
			item.setActionView(selectAllView);
			return selectAllView;
		}
		return null;
	}

	public static void setSelectAllActionModeButtonVisibility(View selectAllActionModeButton, boolean setVisible) {
		if (selectAllActionModeButton == null) {
			return;
		}

		if (setVisible) {
			selectAllActionModeButton.setVisibility(View.VISIBLE);
		} else {
			selectAllActionModeButton.setVisibility(View.GONE);
		}
	}

	public static void positionCursorForEditText(EditText groupNameEditText) {
		int position = groupNameEditText.length();
		Editable text = groupNameEditText.getText();
		Selection.setSelection(text, position);
	}
}
