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
package org.catrobat.catroid.utils;

import android.content.Context;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;

public final class ToastUtil {

	private static SuperToast customToast;

	private ToastUtil() {
	}

	public static void showError(Context context, String message) {
		createToast(context, message, true);
	}

	public static void showError(Context context, int messageId) {
		createToast(context, context.getResources().getString(messageId), true);
	}

	public static void showSuccess(Context context, String message) {
		createToast(context, message, false);
	}

	public static void showSuccess(Context context, int messageId) {
		createToast(context, context.getResources().getString(messageId), false);
	}

	private static void createToast(Context context, String message, boolean error) {

		if (customToast == null || !customToast.isShowing()) {

			customToast = new SuperToast(context);
			customToast.setText(message);
			customToast.setTextSize(Style.TEXTSIZE_MEDIUM);
			customToast.setAnimations(Style.ANIMATIONS_POP);
			setLook(error);

			customToast.show();
		} else {

			setLook(error);
			customToast.setText(message);
		}
	}

	private static void setLook(boolean error) {

		if (error) {
			customToast.setDuration(Style.DURATION_SHORT);
			customToast.setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_RED));
		} else {
			customToast.setDuration(Style.DURATION_VERY_SHORT);
			customToast.setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_GREEN));
		}
	}
}
