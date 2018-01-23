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
import android.util.DisplayMetrics;
import android.view.WindowManager;

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
}
