/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
import android.util.Log;
import android.view.WindowManager;

import org.catrobat.catroid.common.ScreenValues;

public final class ScreenValueHandler {

	private ScreenValueHandler() {
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
			ScreenValues.setToDefaultScreenSize();
		}
	}

	public static void updateScreenWidthAndHeight(Context context, Double widthInCm,
			Double heightInCm) {

		if (context != null) {


			if (widthInCm!=null || widthInCm !=0.0 || heightInCm!=null || heightInCm!=0.0){
				WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
				DisplayMetrics displayMetrics = new DisplayMetrics();
				windowManager.getDefaultDisplay().getMetrics(displayMetrics);

				ScreenValues.SCREEN_WIDTH = cmToPixels(widthInCm, displayMetrics).intValue();
				ScreenValues.SCREEN_HEIGHT = cmToPixels(heightInCm, displayMetrics).intValue();

				Log.e(ScreenValueHandler.class.getSimpleName(), "updateScreenWidthAndHeight: W X H ->"
						+ ScreenValues.SCREEN_WIDTH + " X "+ScreenValues.SCREEN_HEIGHT);
			}else{
				updateScreenWidthAndHeight(context);
			}



		} else {
			ScreenValues.setToDefaultScreenSize();
		}
	}

	private static Double cmToPixels(Double valueInCm, DisplayMetrics dispMetrics){

		Long densityPerCm = Math.round(dispMetrics.densityDpi / 2.54);
		Log.e(ScreenValueHandler.class.getSimpleName(), "cmToPixels: dpi => "+densityPerCm );
		return valueInCm * densityPerCm;
	}


}
