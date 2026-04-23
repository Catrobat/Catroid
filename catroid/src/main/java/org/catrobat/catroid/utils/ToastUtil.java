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

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import org.catrobat.catroid.ui.UiUtils;

import androidx.annotation.StringRes;

public final class ToastUtil {

	private static final int DEFAULT_COLOR = 0;
	private ToastUtil() {
	}

	public static void showError(Context context, String message) {
		createToast(context, message, Toast.LENGTH_SHORT, DEFAULT_COLOR);
	}

	public static void showError(Context context, @StringRes int messageId) {
		createToast(context, context.getResources().getString(messageId), Toast.LENGTH_SHORT, DEFAULT_COLOR);
	}

	public static void showErrorWithColor(Context context, @StringRes int messageId, int color) {
		createToast(context, context.getResources().getString(messageId), Toast.LENGTH_LONG, color);
	}

	public static void showSuccess(Context context, String message) {
		createToast(context, message, Toast.LENGTH_SHORT, DEFAULT_COLOR);
	}

	public static void showSuccess(Context context, @StringRes int messageId) {
		createToast(context, context.getResources().getString(messageId), Toast.LENGTH_SHORT, DEFAULT_COLOR);
	}

	public static void showInfoLong(Context context, String message) {
		createToast(context, message, Toast.LENGTH_LONG, DEFAULT_COLOR);
	}

	private static void createToast(Context context, String message, int duration, int color) {
		Activity activity;
		if (context instanceof Activity) {
			activity = (Activity) context;
		} else if ((activity = UiUtils.getActivityFromContextWrapper(context)) == null) {
			return;
		}

		activity.runOnUiThread(() -> {
			Toast toast = Toast.makeText(context, message, duration);
			if (color != DEFAULT_COLOR) {
				TextView textViewOfToast = toast.getView().findViewById(android.R.id.message);
				textViewOfToast.setTextColor(color);
			}
			toast.show();
		});
	}
}
