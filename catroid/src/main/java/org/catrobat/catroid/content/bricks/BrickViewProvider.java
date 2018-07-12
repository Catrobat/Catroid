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
package org.catrobat.catroid.content.bricks;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

public final class BrickViewProvider {

	private static final int ALPHA_FULL = 255;
	private static final int ALPHA_GRAYED = 100;

	private BrickViewProvider() {
	}

	public static void setAlphaOnView(@NonNull View view, boolean full) {
		getBrickLayout(view).setAlpha(convertAlphaValueToFloat(full ? ALPHA_FULL : ALPHA_GRAYED));
	}

	public static void setSaturationOnView(View view, boolean grayScale) {
		Drawable background = getBrickLayout(view).getBackground();

		if (grayScale) {
			ColorMatrix matrix = new ColorMatrix();
			matrix.setSaturation(0);
			ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
			background.setColorFilter(filter);
		} else {
			background.clearColorFilter();
		}
	}

	public static void setSpinnerClickable(View view, boolean clickable) {
		if (view == null) {
			return;
		}
		if (view instanceof Spinner) {
			view.setClickable(clickable);
			view.setEnabled(clickable);
			view.setFocusable(false);
		}

		if (view instanceof ViewGroup) {
			ViewGroup viewGroup = (ViewGroup) view;
			for (int pos = 0; pos < viewGroup.getChildCount(); pos++) {
				setSpinnerClickable(viewGroup.getChildAt(pos), clickable);
			}
		}
	}

	private static View getBrickLayout(View view) {
		return ((ViewGroup) view).getChildAt(1);
	}

	private static float convertAlphaValueToFloat(int alphaValue) {
		return alphaValue / (float) ALPHA_FULL;
	}
}
