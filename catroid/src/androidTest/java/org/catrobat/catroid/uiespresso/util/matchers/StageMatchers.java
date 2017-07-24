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

package org.catrobat.catroid.uiespresso.util.matchers;

import android.support.test.espresso.matcher.BoundedMatcher;
import android.view.View;

import com.badlogic.gdx.backends.android.surfaceview.GLSurfaceView20;

import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public final class StageMatchers {
	// Suppress default constructor for noninstantiability
	private StageMatchers() {
		throw new AssertionError();
	}

	//usage: in stage activity call onView(isFocusable()).check(matches(StageMatchers.isColorAtPx(COLOR, X, Y)));
	public static Matcher<View> isColorAtPx(final byte[] color, final int x, final int y) {
		return new BoundedMatcher<View, GLSurfaceView20>(GLSurfaceView20.class) {

			@Override
			protected boolean matchesSafely(GLSurfaceView20 view) {
				byte[] testPixels = StageActivity.stageListener.getPixels(x, y, 1, 1);
				return UiTestUtils.comparePixelRgbaArrays(testPixels, color);
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("Look if pixel at y=" + Integer.toString(x)
						+ " y=" + Integer.toString(y) + " is white");
			}
		};
	}
}
