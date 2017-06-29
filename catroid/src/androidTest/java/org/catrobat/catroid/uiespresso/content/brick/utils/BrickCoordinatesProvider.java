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

package org.catrobat.catroid.uiespresso.content.brick.utils;

import android.support.test.espresso.action.CoordinatesProvider;
import android.util.Log;
import android.view.View;

public enum BrickCoordinatesProvider implements CoordinatesProvider{
	UPPER_LEFT_CORNER {
		@Override
		public float[] calculateCoordinates(View view) {
			final int[] viewsCoordinates = new int[2];
			view.getLocationOnScreen(viewsCoordinates);
			float[] coordinates = {viewsCoordinates[0], viewsCoordinates[1]};
			Log.d(TAG, "UpperLeftCorner: " + "x: " + coordinates[0] + "y: " + coordinates[1]);
			return coordinates;
		}
	},

	DOWN_ONE_POSITION {
		@Override
		public float[] calculateCoordinates(View view) {
			final int[] viewsCoordinates = new int[2];
			view.getLocationOnScreen(viewsCoordinates);
			int displayHeight = view.getContext().getResources().getDisplayMetrics().heightPixels;
			float dragDestinationCoordinate = viewsCoordinates[1] + view.getHeight() * (HEIGHT_MULTIPLICATOR + 1);
			if (dragDestinationCoordinate > displayHeight) {
				dragDestinationCoordinate = displayHeight;
			}
			float[] coordinates = {viewsCoordinates[0], dragDestinationCoordinate};
			Log.d(TAG, "DownOnePosition: " + "x: " + coordinates[0] + "y: " + coordinates[1]);
			return coordinates;
		}
	},

	UP_ONE_POSITION {
		@Override
		public float[] calculateCoordinates(View view) {
			final int[] viewsCoordinates = new int[2];
			view.getLocationOnScreen(viewsCoordinates);
			float dragDestinationCoordinate = viewsCoordinates[1] - view.getHeight() * HEIGHT_MULTIPLICATOR;
			if (dragDestinationCoordinate < 0) {
				dragDestinationCoordinate = 0;
			}
			float[] coordinates = {viewsCoordinates[0], dragDestinationCoordinate};
			Log.d(TAG, "UpOnePosition: " + "x: " + coordinates[0] + "y: " + coordinates[1]);
			return coordinates;
		}
	};

	private static final String TAG = BrickCoordinatesProvider.class.getSimpleName();

	//since it is unknown how tall the brick below/above is, we assume that dragging the brick 1.2 times its own height
	//should be sufficient to move it over the neighboring brick
	private static final float HEIGHT_MULTIPLICATOR = 1.2f;
}
