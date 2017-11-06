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

package org.catrobat.catroid.test.utils;

import android.content.Context;
import android.test.AndroidTestCase;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.utils.TextSizeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestTextSizeUtil extends AndroidTestCase {

	Map<ViewGroup, List<TextView>> viewGroups = new HashMap<>();

	public void setUp() throws Exception {
		super.setUp();
		putNewEntryInMap(R.layout.activity_script);
		putNewEntryInMap(R.layout.activity_prestage);

		TextSizeUtil.setLargeText(true);
	}

	public void tearDown() throws Exception {
		super.tearDown();
		viewGroups.clear();
		TextSizeUtil.setLargeText(false);
	}

	public void testEnlargeViewGroup() {
		for (Map.Entry<ViewGroup, List<TextView>> entry : viewGroups.entrySet()) {
			List<TextView> textViews = entry.getValue();

			List<Float> normalSize = getTextSizes(textViews);
			TextSizeUtil.enlargeViewGroup(entry.getKey());
			List<Float> doubleSize = getTextSizes(textViews);

			checkTextSizes(normalSize, doubleSize);
		}
	}

	private void checkTextSizes(List<Float> normalSize, List<Float> doubledSize) {
		for (int i = 0; i < normalSize.size(); i++) {
			assertEquals("Wrong Size!", normalSize.get(i) * 1.5f, doubledSize.get(i));
		}
	}

	private List<Float> getTextSizes(List<TextView> textViews) {
		List<Float> sizes = new ArrayList<>();
		for (TextView textview : textViews) {
			sizes.add(textview.getTextSize());
		}
		return sizes;
	}

	private void putNewEntryInMap(int layoutId) {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(layoutId, null);
		viewGroups.put(viewGroup, getAllTextViews(viewGroup));
	}

	private List<TextView> getAllTextViews(ViewGroup viewGroup) {
		List<TextView> textViewsInViewGroup = new ArrayList<>();
		getAllTextViews(viewGroup, textViewsInViewGroup);
		return textViewsInViewGroup;
	}

	private void getAllTextViews(ViewGroup viewGroup, List<TextView> textViews) {
		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			if (viewGroup.getChildAt(i) instanceof TextView) {
				textViews.add((TextView) viewGroup.getChildAt(i));
			} else if (viewGroup.getChildAt(i) instanceof ViewGroup) {
				getAllTextViews((ViewGroup) viewGroup.getChildAt(i), textViews);
			}
		}
	}
}
