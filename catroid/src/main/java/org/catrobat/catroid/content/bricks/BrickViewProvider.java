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
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.R;

public final class BrickViewProvider {

	private BrickViewProvider() {
	}

	public static final int ALPHA_FULL = 255;
	public static final int ALPHA_GREYED = 100;

	public static View createView(Context context, int layout) {
		View view = View.inflate(context, layout, null);

		return view;
	}

	public static View createPrototypeView(Context context, int layout) {
		View prototypeView = View.inflate(context, layout, null);

		return prototypeView;
	}

	public static View setAlphaOnView(View view, int alphaValue) {
		if (view != null) {
			getBrickLayout(view).setAlpha(convertAlphaValueToFloat(alphaValue));
		}
		return view;
	}

	public static void changeBrickState(Brick brick, boolean enabled) {
		if (brick.getCheckBox() != null) {
			brick.getCheckBox().setEnabled(enabled);
		}
		if (enabled) {
			setAlphaOnView(((BrickBaseType) brick).view, ALPHA_FULL);
			brick.setAlpha(ALPHA_FULL);
		} else {
			setAlphaOnView(((BrickBaseType) brick).view, ALPHA_GREYED);
			brick.setAlpha(ALPHA_GREYED);
		}
	}

	private static View getBrickLayout(View view) {
		return ((ViewGroup) view).getChildAt(1);
	}

	private static float convertAlphaValueToFloat(int alphaValue) {
		return alphaValue / (float) ALPHA_FULL;
	}
}
