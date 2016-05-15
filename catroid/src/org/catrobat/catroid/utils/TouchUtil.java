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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public final class TouchUtil {

	private static TouchUtil instance;

	private TreeMap<Integer, Integer> currentlyTouchingIndices;
	private ArrayList<PointF> touches;
	private ArrayList<Boolean> isTouching;

	private TouchUtil() {
		currentlyTouchingIndices = new TreeMap<>();
		touches = new ArrayList<>();
		isTouching = new ArrayList<>();
	}

	private static TouchUtil getInstance() {
		if (instance == null) {
			instance = new TouchUtil();
		}
		return instance;
	}

	public static void reset() {
		getInstance().currentlyTouchingIndices.clear();
		getInstance().touches.clear();
		getInstance().isTouching.clear();
	}

	public static void updatePosition(float x, float y, int pointer) {
		int index = getInstance().currentlyTouchingIndices.get(pointer);
		getInstance().touches.set(index, new PointF(x, y));
	}

	public static void touchDown(float x, float y, int pointer) {
		if(getInstance().currentlyTouchingIndices.containsKey(pointer)) {
			touchUp(pointer);
		}
		getInstance().currentlyTouchingIndices.put(pointer, getInstance().touches.size());
		getInstance().touches.add(new PointF(x, y));
		getInstance().isTouching.add(true);
		getInstance().fireTouchEvent();
	}

	public static void touchUp(int pointer) {
		int index = getInstance().currentlyTouchingIndices.get(pointer);
		getInstance().isTouching.set(index, false);
		getInstance().currentlyTouchingIndices.remove(pointer);
	}

	public static boolean isFingerTouching(int index) {
		if(index < 1 || index > getInstance().isTouching.size()){
			return false;
		}
		return getInstance().isTouching.get(index - 1);
	}

	public static int getLastTouchIndex() {
		return getInstance().touches.size();
	}

	public static float getX(int index) {
		if (index < 1 || index > getInstance().isTouching.size()) {
			return 0.0f;
		}

		return getInstance().touches.get(index - 1).x;
	}

	public static float getY(int index) {
		if (index < 1 || index > getInstance().isTouching.size()) {
			return 0.0f;
		}

		return getInstance().touches.get(index - 1).y;
	}

	private void fireTouchEvent() {
		List<Sprite> spriteList = ProjectManager.getInstance().getCurrentProject().getSpriteList();

		for (Sprite sprite : spriteList) {
			sprite.createTouchDownAction();
		}
	}
}
