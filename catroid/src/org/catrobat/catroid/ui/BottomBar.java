/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.ui;

import android.app.Activity;
import android.view.View;

import org.catrobat.catroid.R;

public final class BottomBar {

	// Suppress default constructor for noninstantiability
	private BottomBar() {
		throw new AssertionError();
	}

	public static void showBottomBar(Activity activity) {
		activity.findViewById(R.id.bottom_bar).setVisibility(View.VISIBLE);
	}

	public static void hideBottomBar(Activity activity) {
		activity.findViewById(R.id.bottom_bar).setVisibility(View.GONE);
	}

	public static void showAddButton(Activity activity) {
		activity.findViewById(R.id.button_add).setVisibility(View.VISIBLE);
		updateSeparator(activity);
	}

	public static void hideAddButton(Activity activity) {
		activity.findViewById(R.id.button_add).setVisibility(View.GONE);
		updateSeparator(activity);
	}

	public static void showPlayButton(Activity activity) {
		activity.findViewById(R.id.button_play).setVisibility(View.VISIBLE);
		updateSeparator(activity);
	}

	public static void hidePlayButton(Activity activity) {
		activity.findViewById(R.id.button_play).setVisibility(View.GONE);
		updateSeparator(activity);
	}

	private static void updateSeparator(Activity activity) {
		if (activity.findViewById(R.id.button_play).getVisibility() == View.VISIBLE
				&& activity.findViewById(R.id.button_add).getVisibility() == View.VISIBLE) {
			activity.findViewById(R.id.bottom_bar_separator).setVisibility(View.VISIBLE);
		} else {
			activity.findViewById(R.id.bottom_bar_separator).setVisibility(View.GONE);
		}
	}
}
