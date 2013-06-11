/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.pocketcode.ui;

import org.catrobat.catroid.pocketcode.R;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;

public class BottomBar {

	public static void disableButtons(Activity activity) {
		setButtonClickable(activity, false);
	}

	public static void enableButtons(Activity activity) {
		setButtonClickable(activity, true);
	}

	private static void setButtonClickable(Activity activity, boolean clickable) {
		LinearLayout bottomBarLayout = (LinearLayout) activity.findViewById(R.id.bottom_bar);

		if (bottomBarLayout != null) {
			bottomBarLayout.findViewById(R.id.button_add).setClickable(clickable);
			bottomBarLayout.findViewById(R.id.button_play).setClickable(clickable);
		}
	}

	public static void setButtonVisible(Activity activity) {
		LinearLayout bottomBarLayout = (LinearLayout) activity.findViewById(R.id.bottom_bar);

		if (bottomBarLayout != null) {
			bottomBarLayout.findViewById(R.id.button_add).setVisibility(LinearLayout.VISIBLE);
			bottomBarLayout.findViewById(R.id.button_play).setVisibility(LinearLayout.VISIBLE);
			bottomBarLayout.findViewById(R.id.bottom_bar).setVisibility(View.VISIBLE);
			bottomBarLayout.findViewById(R.id.bottom_bar_separator).setVisibility(View.VISIBLE);
			bottomBarLayout.findViewById(R.id.button_play).setVisibility(View.VISIBLE);
		}
	}
}
