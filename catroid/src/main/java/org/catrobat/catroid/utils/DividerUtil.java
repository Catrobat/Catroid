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
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.BrickLayout;

public final class DividerUtil {

	private static boolean elementSpacing = false;

	public static int dividerHeight = 60;

	private DividerUtil() {
	}

	public static void setDivider(Context context, LinearLayout linearLayout) {
		if (isElementSpacing()) {
			linearLayout.setDividerDrawable(context.getResources().getDrawable(R.drawable.divider));
			linearLayout.setDividerPadding(dividerHeight);
			linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
		}
	}

	public static void setDivider(Context context, ListView listView) {
		if (isElementSpacing()) {
			listView.setDividerHeight(dividerHeight);
		}
	}

	public static void setDivider(BrickLayout brickLayout) {
		if (isElementSpacing()) {
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(BrickLayout.LayoutParams.MATCH_PARENT, BrickLayout.LayoutParams.WRAP_CONTENT);
			lp.setMargins(0, 0, 0, dividerHeight);
			brickLayout.setLayoutParams(lp);
		}
	}

	public static void setDivider(Context context, ViewGroup viewGroup) {
		if (isElementSpacing()) {
			for (int i = 0; i < viewGroup.getChildCount(); i++) {
				if (viewGroup.getChildAt(i) instanceof BrickLayout) {
					setDivider((BrickLayout) viewGroup.getChildAt(i));
				}
			}
		}
	}

	public static boolean isElementSpacing() {
		return elementSpacing;
	}

	public static void setElementSpacing(boolean enabled) {
		elementSpacing = enabled;
	}
}
