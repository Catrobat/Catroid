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

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;
import android.view.ViewGroup;

import com.github.mrengineer13.snackbar.SnackBar;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.SettingsActivity;

import java.util.HashSet;
import java.util.Set;

public final class SnackbarUtil {

	private SnackbarUtil() {
	}

	public static final String SHOWN_HINT_LIST = "shown_hint_list";

	public static void showHintSnackbar(final Activity activity, @StringRes int resourceId) {
		final String messageId = activity.getResources().getResourceName(resourceId);
		final String message = activity.getString(resourceId);

		if (!wasHintAlreadyShown(activity, messageId) && areHintsEnabled(activity)) {
			SnackBar.Builder snackBarBuilder = new SnackBar.Builder(activity)
					.withMessage(message)
					.withActionMessage(activity.getResources().getString(R.string.got_it))
					.withTextColorId(R.color.solid_black)
					.withBackgroundColorId(R.color.holo_blue_light)
					.withOnClickListener(new SnackBar.OnMessageClickListener() {
						@Override
						public void onMessageClick(Parcelable token) {
							setHintShown(activity, messageId);
						}
					})
					.withDuration(SnackBar.PERMANENT_SNACK);
			ViewGroup viewGroup = (ViewGroup) snackBarBuilder.show().getContainerView();
			TextSizeUtil.enlargeViewGroup(viewGroup);
		}
	}

	private static void setHintShown(Activity activity, String messageId) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		Set<String> hintList = getStringSetFromSharedPreferences(activity);
		hintList.add(messageId);
		prefs.edit().putStringSet(SnackbarUtil.SHOWN_HINT_LIST, hintList).commit();
	}

	private static boolean wasHintAlreadyShown(Activity activity, String messageId) {
		Set<String> hintList = getStringSetFromSharedPreferences(activity);
		return hintList.contains(messageId);
	}

	private static boolean areHintsEnabled(Activity activity) {
		return PreferenceManager.getDefaultSharedPreferences(activity).getBoolean(SettingsActivity.SETTINGS_SHOW_HINTS, false);
	}

	private static Set<String> getStringSetFromSharedPreferences(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return new HashSet<>(prefs.getStringSet(SnackbarUtil.SHOWN_HINT_LIST, new HashSet<String>()));
	}
}
