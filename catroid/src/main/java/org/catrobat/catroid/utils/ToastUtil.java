/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.Toast;

import org.catrobat.catroid.ui.UiUtils;

public final class ToastUtil {

	private ToastUtil() {
	}

	public static void showError(Context context, String message) {
		createToast(context, message);
	}

	public static void showError(Context context, @StringRes int messageId) {
		createToast(context, context.getResources().getString(messageId));
	}

	public static void showSuccess(Context context, String message) {
		createToast(context, message);
	}

	public static void showSuccess(Context context, @StringRes int messageId) {
		createToast(context, context.getResources().getString(messageId));
	}

	private static void createToast(Context context, String message) {
		Activity activity = UiUtils.getActivityFromContextWrapper(context);
		if (activity == null) {
			return;
		}
		View contentView = activity.findViewById(android.R.id.content);
		if (contentView == null) {
			return;
		}

		Toast toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT);
		toast.show();
	}
}
