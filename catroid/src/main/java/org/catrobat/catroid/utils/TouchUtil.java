/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

	private static SparseIntArray currentlyTouchingPointersToTouchIndex = new SparseIntArray();

	private static ArrayList<PointF> touches = new ArrayList<>();
	private static ArrayList<Boolean> isTouching = new ArrayList<>();

	private TouchUtil() {
		// static class, nothing to do
	}

	public static void reset() {
		currentlyTouchingPointersToTouchIndex.clear();
		touches.clear();
		isTouching.clear();
	}

	public static void updatePosition(float x, float y, int pointer) {
		int index = currentlyTouchingPointersToTouchIndex.get(pointer);
		touches.set(index, new PointF(x, y));
	}

	public static void touchDown(float x, float y, int pointer) {
		if (currentlyTouchingPointersToTouchIndex.indexOfKey(pointer) >= 0) {
			return;
		}
		currentlyTouchingPointersToTouchIndex.put(pointer, touches.size());
		touches.add(new PointF(x, y));
		isTouching.add(true);
		fireTouchEvent();
	}

	public static void touchUp(int pointer) {
		if (currentlyTouchingPointersToTouchIndex.indexOfKey(pointer) < 0) {
			return;
		}
		int index = currentlyTouchingPointersToTouchIndex.get(pointer);
		isTouching.set(index, false);
		currentlyTouchingPointersToTouchIndex.delete(pointer);
	}

	public static double isTouching() {
		for (int i = 0; i < isTouching.size(); i++) {
			if (isTouching.get(i)) {
				return 1d;
			}
		}
		return 0d;
	}

	public static boolean isFingerTouching(int index) {
		if (index < 1 || index > isTouching.size()) {
			return false;
		}
		return isTouching.get(index - 1);
	}

	public static int getLastTouchIndex() {
		return touches.size();
	}

	public static float getX(int index) {
		if ((index < 1) || index > isTouching.size()) {
			return 0.0f;
		}

		return touches.get(index - 1).x;
	}

	public static float getY(int index) {
		if (index < 1 || index > isTouching.size()) {
			return 0.0f;
		}

		return touches.get(index - 1).y;
	}

	public static void setDummyTouchForTest(float x, float y) {
		touches.add(new PointF(x, y));
		isTouching.add(false);
	}

	private static void fireTouchEvent() {
		EventWrapper event = new EventWrapper(new EventId(EventId.TAP_BACKGROUND), false);
		ProjectManager.getInstance().getCurrentProject().fireToAllSprites(event);
	}

	public static ArrayList<PointF> getCurrentTouchingPoints() {
		ArrayList<PointF> points = new ArrayList<>();
		for (int i = 0; i < currentlyTouchingPointersToTouchIndex.size(); i++) {
			int index = currentlyTouchingPointersToTouchIndex.valueAt(i);
			points.add(touches.get(index));
		}
		return points;
	}

	public static int getNumberOfCurrentTouches() {
		return currentlyTouchingPointersToTouchIndex.size();
	}

	public static int getIndexOfCurrentTouch(int index) {
		if ((index < 1) || index > currentlyTouchingPointersToTouchIndex.size()) {
			return 0;
		}
		return currentlyTouchingPointersToTouchIndex.valueAt(index - 1) + 1;
	}
}
