/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;

import java.util.HashSet;
import java.util.Set;

import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

public final class SnackbarUtil {

	private static final int MAX_LINES = 5;

	private SnackbarUtil() {
		throw new AssertionError("no");
	}

	public static final String SHOWN_HINT_LIST = "shown_hint_list";

	public static void showHintSnackbar(final Activity activity, @StringRes int resourceId) {
		final String messageId = activity.getResources().getResourceName(resourceId);

		if (!wasHintAlreadyShown(activity, messageId) && areHintsEnabled(activity)) {
			View contentView = activity.findViewById(android.R.id.content);
			if (contentView == null) {
				return;
			}

			Snackbar snackbar = Snackbar.make(contentView, resourceId, Snackbar.LENGTH_INDEFINITE);
			snackbar.setAction(R.string.got_it, new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					setHintShown(activity, messageId);
				}
			});
			snackbar.setActionTextColor(ContextCompat.getColor(activity, R.color.solid_black));
			View snackbarView = snackbar.getView();
			TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
			textView.setMaxLines(MAX_LINES);
			textView.setTextColor(ContextCompat.getColor(activity, R.color.solid_white));
			snackbarView.setBackgroundColor(ContextCompat.getColor(activity, R.color.snackbar));
			snackbar.show();
		}
	}

	public static void setHintShown(Activity activity, String messageId) {
		Set<String> hintList = getStringSetFromSharedPreferences(activity);
		hintList.add(messageId);
		PreferenceManager.getDefaultSharedPreferences(activity).edit()
				.putStringSet(SnackbarUtil.SHOWN_HINT_LIST, hintList)
				.apply();
	}

	public static boolean wasHintAlreadyShown(Activity activity, String messageId) {
		Set<String> hintList = getStringSetFromSharedPreferences(activity);
		return hintList.contains(messageId);
	}

	public static boolean areHintsEnabled(Activity activity) {
		return PreferenceManager.getDefaultSharedPreferences(activity).getBoolean(SettingsFragment.SETTINGS_SHOW_HINTS, false);
	}

	private static Set<String> getStringSetFromSharedPreferences(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return new HashSet<>(prefs.getStringSet(SnackbarUtil.SHOWN_HINT_LIST, new HashSet<String>()));
	}
}
