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
package org.catrobat.catroid.utils;

import android.graphics.PointF;

import java.util.TreeMap;

public final class TouchUtil {

	private static TouchUtil instance;

	private TreeMap<Integer, PointF> currentlyTouchedFinger;
	private float lastKnownX;
	private float lastKnownY;

	private TouchUtil() {
		currentlyTouchedFinger = new TreeMap<>();
		lastKnownX = 0.0f;
		lastKnownY = 0.0f;
	}

	private static TouchUtil getInstance() {
		if (instance == null) {
			instance = new TouchUtil();
		}
		return instance;
	}

	public static void updatePosition(float x, float y, int pointer) {
		touchDown(x, y, pointer);
	}

	public static void touchDown(float x, float y, int pointer) {
		getInstance().lastKnownX = x;
		getInstance().lastKnownY = y;
		getInstance().currentlyTouchedFinger.put(pointer + 1, new PointF(x, y));
	}

	public static void touchUp(int pointer) {
		getInstance().currentlyTouchedFinger.remove(pointer + 1);
	}

	public static boolean isFingerTouching() {
		return getInstance().currentlyTouchedFinger.size() > 0;
	}

	public static boolean isFingerTouching(int pointer) {
		return getInstance().currentlyTouchedFinger.containsKey(pointer);
	}

	public static float getLastKnownX() {
		return getInstance().lastKnownX;
	}

	public static float getLastKnownY() {
		return getInstance().lastKnownY;
	}

	public static float getX(int pointer) {
		if (isFingerTouching(pointer)) {
			return getInstance().currentlyTouchedFinger.get(pointer).x;
		}

		return 0.0f;
	}

	public static float getY(int pointer) {
		if (isFingerTouching(pointer)) {
			return getInstance().currentlyTouchedFinger.get(pointer).y;
		}

		return 0.0f;
	}
}
