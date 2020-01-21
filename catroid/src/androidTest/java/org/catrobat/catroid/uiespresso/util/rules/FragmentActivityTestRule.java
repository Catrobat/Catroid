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
package org.catrobat.catroid.uiespresso.util.rules;

import android.app.Activity;
import android.content.Intent;

public class FragmentActivityTestRule<T extends Activity> extends BaseActivityTestRule<T> {

	private Intent launchIntent;

	public FragmentActivityTestRule(Class<T> activityClass, String extraFragementPosition, int fragment) {
		super(activityClass, true, false);
		launchIntent = new Intent();
		launchIntent.putExtra(extraFragementPosition, fragment);
	}

	public void launchActivity() {
		super.launchActivity(launchIntent);
	}
}
