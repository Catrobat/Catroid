/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import org.catrobat.catroid.common.ScreenValues;

public final class ScreenValueHandler {

	private ScreenValueHandler() {
		throw new AssertionError();
	}

	public static void updateScreenWidthAndHeight(Context context) {
		if (context == null) {
			ScreenValues.setToDefaultScreenSize();
			return;
		}

		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		if (windowManager == null) {
			ScreenValues.setToDefaultScreenSize();
			return;
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			ScreenValues.currentScreenResolution = new Resolution(
					windowManager.getCurrentWindowMetrics().getBounds().width(),
					windowManager.getCurrentWindowMetrics().getBounds().height());
			return;
		}

		DisplayMetrics displayMetrics = new DisplayMetrics();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
		} else {
			windowManager.getDefaultDisplay().getMetrics(displayMetrics);
		}
		ScreenValues.currentScreenResolution = new Resolution(displayMetrics.widthPixels, displayMetrics.heightPixels);
	}
}
