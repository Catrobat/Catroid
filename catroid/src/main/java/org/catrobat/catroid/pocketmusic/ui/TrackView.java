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
package org.catrobat.catroid.pocketmusic.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TableLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrackView extends TableLayout {

	public static final int ROW_COUNT = 13;
	private List<TrackRowView> trackRowViews = new ArrayList<>(ROW_COUNT);
	private static final int[] BLACK_KEY_INDICES = {
			1, 3, 6, 8, 10
	};

	public TrackView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setStretchAllColumns(true);
		initializeRows();
		setWeightSum(ROW_COUNT);
	}

	private void initializeRows() {
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1.0f);
		for (int i = 0; i < ROW_COUNT; i++) {
			boolean isBlackRow = Arrays.binarySearch(BLACK_KEY_INDICES, i) > -1;
			trackRowViews.add(new TrackRowView(getContext(), isBlackRow));
			addView(trackRowViews.get(i), params);
		}
	}

	public List<TrackRowView> getTrackRowViews() {
		return trackRowViews;
	}
}
