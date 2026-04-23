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

package org.catrobat.catroid.uiespresso.content.brick.utils;

import android.widget.TableRow;

import org.catrobat.catroid.uiespresso.util.wrappers.ViewInteractionWrapper;
import org.catrobat.paintroid.colorpicker.PresetSelectorView;

import androidx.test.espresso.ViewInteraction;

import static org.hamcrest.Matchers.allOf;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withParentIndex;

public final class ColorPickerInteractionWrapper extends ViewInteractionWrapper {
	private ColorPickerInteractionWrapper(ViewInteraction viewInteraction) {
		super(viewInteraction);
	}

	public static ViewInteraction onColorPickerPresetButton(int row, int column) {
		return onView(allOf(
				isDescendantOfA(isAssignableFrom(PresetSelectorView.class)),
				withParent(allOf(isAssignableFrom(TableRow.class), withParentIndex(row))),
				withParentIndex(column)));
	}
}
