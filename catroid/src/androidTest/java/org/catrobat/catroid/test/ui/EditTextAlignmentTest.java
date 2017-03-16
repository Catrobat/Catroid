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
package org.catrobat.catroid.test.ui;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.UiThreadTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.BrickLayout.LayoutParams;
import org.catrobat.catroid.ui.BrickLayout.LayoutParams.InputType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class EditTextAlignmentTest {

	@Rule
	public UiThreadTestRule uiThreadTestRule = new UiThreadTestRule();

	private static LayoutInflater inflater;
	private Context context;

	@Before
	public void setUp() throws Exception {

		context = InstrumentationRegistry.getTargetContext();
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/*
	 * all bricks in R.layout must have "_brick_" in their names
	 * and all bricks in R.id which have an EditText must have the same name as in R.layout plus "_edit_text" at the end
	 * of their names
	 */
	@Test
	@UiThreadTest
	public void testAllBricks() throws IllegalArgumentException, IllegalAccessException {

		Field[] layoutFields = R.layout.class.getFields();

		for (Field layoutField : layoutFields) {
			String layoutName = layoutField.getName();

			if (layoutName.startsWith("brick_")) {
				int brickId = layoutField.getInt(null);
				View brickView = inflater.inflate(brickId, null);
				ArrayList<View> allChildsOfLayout = getAllChildren(brickView);

				for (View child : allChildsOfLayout) {
					if (child.getId() != View.NO_ID) {
						String idName = child.getResources().getResourceName(child.getId());
						if (idName.contains(layoutName) && idName.contains("_edit_text")) {
							TextView edit = (TextView) child;
							LayoutParams layoutParams = (LayoutParams) edit.getLayoutParams();

							if (layoutParams.getInputType() == InputType.NUMBER) {
								assertEquals("Brick " + layoutName + " does not have correct gravity (Gravity.RIGHT)",
										Gravity.RIGHT, Gravity.RIGHT & edit.getGravity());
							} else {
								assertEquals("Brick " + layoutName + " does not have correct gravity (Gravity.LEFT)",
										Gravity.LEFT, Gravity.LEFT & edit.getGravity());
							}
						}
					}
				}
			}
		}
	}

	private ArrayList<View> getAllChildren(View v) {

		if (!(v instanceof ViewGroup)) {
			ArrayList<View> viewArrayList = new ArrayList<View>();
			viewArrayList.add(v);
			return viewArrayList;
		}

		ArrayList<View> result = new ArrayList<View>();

		ViewGroup vg = (ViewGroup) v;
		for (int i = 0; i < vg.getChildCount(); i++) {

			View child = vg.getChildAt(i);

			ArrayList<View> viewArrayList = new ArrayList<View>();
			viewArrayList.add(v);
			viewArrayList.addAll(getAllChildren(child));

			result.addAll(viewArrayList);
		}
		return result;
	}
}
