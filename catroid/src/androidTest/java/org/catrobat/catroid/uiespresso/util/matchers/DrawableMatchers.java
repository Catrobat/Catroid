/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public final class DrawableMatchers {
	private DrawableMatchers() {
		throw new AssertionError();
	}

	public static Matcher<View> withDrawable(final int expectedId) {
		return new TypeSafeMatcher<View>() {
			private String resourceName;
			static final int EMPTY = -1;
			static final int ANY = -2;

			@Override
			protected boolean matchesSafely(View target) {
				if (!(target instanceof ImageView)) {
					return false;
				}
				ImageView imageView = (ImageView) target;
				if (expectedId == EMPTY) {
					return imageView.getDrawable() == null;
				}
				if (expectedId == ANY) {
					return imageView.getDrawable() != null;
				}
				Resources resources = target.getContext().getResources();
				Drawable expectedDrawable = resources.getDrawable(expectedId);
				resourceName = resources.getResourceEntryName(expectedId);

				if (expectedDrawable == null) {
					return false;
				}

				Bitmap bitmap = getBitmap(imageView.getDrawable());
				Bitmap otherBitmap = getBitmap(expectedDrawable);
				return bitmap.sameAs(otherBitmap);
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("with drawable from resource id: ");
				description.appendValue(expectedId);
				if (resourceName != null) {
					description.appendText("[");
					description.appendText(resourceName);
					description.appendText("]");
				}
			}

			private Bitmap getBitmap(Drawable drawable) {
				Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(bitmap);
				drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
				drawable.draw(canvas);
				return bitmap;
			}
		};
	}
}
