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
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;

public class BrickTextView extends AppCompatTextView {

	private static final int SHADOW_RADIUS = 3;
	private static final int SHADOW_REPEAT = 3;
	private static boolean highContrast = false;

	public BrickTextView(Context context) {
		super(context);
		setProperties();
	}

	public BrickTextView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		setProperties();
	}

	public BrickTextView(Context context, AttributeSet attributeSet, int defStyle) {
		super(context, attributeSet, defStyle);
		setProperties();
	}

	private void setProperties() {
		setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextSize() * TextSizeUtil.getModifier());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (highContrast) {
			getPaint().setShadowLayer(SHADOW_RADIUS, 0, 0, Color.BLACK);
			getPaint().setShader(null);

			for (int i = 0; i < SHADOW_REPEAT; i++) {
				super.onDraw(canvas);
			}
		} else {
			super.onDraw(canvas);
		}
	}

	public static void setHighContrast(boolean enabled) {
		highContrast = enabled;
	}
}
