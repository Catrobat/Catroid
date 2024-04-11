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
package org.catrobat.catroid.utils;

import android.graphics.PointF;
import android.util.SparseIntArray;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.EventWrapper;
import org.catrobat.catroid.content.eventids.EventId;

import java.util.ArrayList;

public final class TouchUtil {

	private static final SparseIntArray CURRENTLY_TOUCHING_POINTERS_TO_TOUCH_INDEX = new SparseIntArray();

	private static final ArrayList<PointF> TOUCHES = new ArrayList<>();
	private static final ArrayList<Boolean> IS_TOUCHING = new ArrayList<>();

	private TouchUtil() {
		// static class, nothing to do
	}

	public static void reset() {
		CURRENTLY_TOUCHING_POINTERS_TO_TOUCH_INDEX.clear();
		TOUCHES.clear();
		IS_TOUCHING.clear();
	}

	public static void updatePosition(float x, float y, int pointer) {
		int index = CURRENTLY_TOUCHING_POINTERS_TO_TOUCH_INDEX.get(pointer);
		TOUCHES.set(index, new PointF(x, y));
	}

	public static void touchDown(float x, float y, int pointer) {
		if (CURRENTLY_TOUCHING_POINTERS_TO_TOUCH_INDEX.indexOfKey(pointer) >= 0) {
			return;
		}
		CURRENTLY_TOUCHING_POINTERS_TO_TOUCH_INDEX.put(pointer, TOUCHES.size());
		TOUCHES.add(new PointF(x, y));
		IS_TOUCHING.add(true);
		fireTouchEvent();
	}

	public static void touchUp(int pointer) {
		if (CURRENTLY_TOUCHING_POINTERS_TO_TOUCH_INDEX.indexOfKey(pointer) < 0) {
			return;
		}
		int index = CURRENTLY_TOUCHING_POINTERS_TO_TOUCH_INDEX.get(pointer);
		IS_TOUCHING.set(index, false);
		CURRENTLY_TOUCHING_POINTERS_TO_TOUCH_INDEX.delete(pointer);
	}

	public static double isTouching() {
		for (int i = 0; i < IS_TOUCHING.size(); i++) {
			if (IS_TOUCHING.get(i)) {
				return 1d;
			}
		}
		return 0d;
	}

	public static boolean isFingerTouching(int index) {
		if (index < 1 || index > IS_TOUCHING.size()) {
			return false;
		}
		return IS_TOUCHING.get(index - 1);
	}

	public static int getLastTouchIndex() {
		return TOUCHES.size();
	}

	public static float getX(int index) {
		if ((index < 1) || index > IS_TOUCHING.size()) {
			return 0.0f;
		}

		return TOUCHES.get(index - 1).x;
	}

	public static float getY(int index) {
		if (index < 1 || index > IS_TOUCHING.size()) {
			return 0.0f;
		}

		return TOUCHES.get(index - 1).y;
	}

	public static void setDummyTouchForTest(float x, float y) {
		TOUCHES.add(new PointF(x, y));
		IS_TOUCHING.add(false);
	}

	public static void setDummyTouchForSensorTest(float x, float y, boolean touching) {
		PointF p = new PointF(x, y);
		p.x = x;
		p.y = y;
		TOUCHES.add(p);
		IS_TOUCHING.add(touching);
	}

	public static void setTouchPointerDummyForTest(int pointer, int value) {
		CURRENTLY_TOUCHING_POINTERS_TO_TOUCH_INDEX.put(pointer, value);
	}

	private static void fireTouchEvent() {
		EventWrapper event = new EventWrapper(new EventId(EventId.TAP_BACKGROUND), false);
		ProjectManager.getInstance().getCurrentProject().fireToAllSprites(event);
	}

	public static ArrayList<PointF> getCurrentTouchingPoints() {
		ArrayList<PointF> points = new ArrayList<>();
		for (int i = 0; i < CURRENTLY_TOUCHING_POINTERS_TO_TOUCH_INDEX.size(); i++) {
			int index = CURRENTLY_TOUCHING_POINTERS_TO_TOUCH_INDEX.valueAt(i);
			points.add(TOUCHES.get(index));
		}
		return points;
	}

	public static int getNumberOfCurrentTouches() {
		return CURRENTLY_TOUCHING_POINTERS_TO_TOUCH_INDEX.size();
	}

	public static int getIndexOfCurrentTouch(int index) {
		if ((index < 1) || index > CURRENTLY_TOUCHING_POINTERS_TO_TOUCH_INDEX.size()) {
			return 0;
		}
		return CURRENTLY_TOUCHING_POINTERS_TO_TOUCH_INDEX.valueAt(index - 1) + 1;
	}
}
