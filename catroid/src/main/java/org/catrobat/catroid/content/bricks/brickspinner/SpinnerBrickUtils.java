/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.content.bricks.brickspinner;

import android.content.Context;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.esotericsoftware.minlog.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ParameterizedBrick;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class SpinnerBrickUtils {

	private static final String TAG = SpinnerBrickUtils.class.getSimpleName();

	private SpinnerBrickUtils() {
	}

	public static int getSpinnerIdByIdName(String spinnerViewId) {
		try {
			Field field = R.id.class.getDeclaredField(spinnerViewId);
			return field.getInt(field);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			Log.error(TAG, "Failed to get spinner id by id name.", e);
		}
		return -1;
	}

	public static Spinner getSpinnerByViewId(Brick brick, int spinnerViewId, Context context) {
		if (spinnerViewId >= 0) {
			return brick.getView(context).findViewById(spinnerViewId);
		}
		return null;
	}

	public static List<Object> getSpinnerItems(Brick brick, int spinnerViewId, Context context) {
		if (brick instanceof ParameterizedBrick && spinnerViewId == R.id.brick_param_expected_list) {
			brick = ((ParameterizedBrick) brick).getEndBrick();
		}
		List<Object> availableSelectionItems = new ArrayList<>();
		Spinner foundSpinner = getSpinnerByViewId(brick, spinnerViewId, context);
		if (foundSpinner != null) {
			SpinnerAdapter spinnerAdapter = foundSpinner.getAdapter();
			for (int index = 0; index < spinnerAdapter.getCount(); ++index) {
				Object foundItem = spinnerAdapter.getItem(index);
				availableSelectionItems.add(foundItem);
			}
		}
		return availableSelectionItems;
	}
}
