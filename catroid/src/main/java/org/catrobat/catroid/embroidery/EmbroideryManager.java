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

package org.catrobat.catroid.embroidery;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class EmbroideryManager {
	private ArrayList<PointF> stitchPoints;
	private Lock stitchPointLock;

	public EmbroideryManager() {
		stitchPoints = new ArrayList<>();
		stitchPointLock = new ReentrantLock();
	}

	public void addStitchPoint(PointF point) {
		if (stitchPoints.isEmpty() || isNewStitchPoint(point)) {
			stitchPointLock.lock();
			try {
				stitchPoints.add(point);
			} finally {
				stitchPointLock.unlock();
			}
		}
	}

	private Boolean isNewStitchPoint(PointF point) {
		return !stitchPoints.get(stitchPoints.size() - 1).equals(point);
	}

	public ArrayList<PointF> getStitchPoints() {
		return stitchPoints;
	}

	public void clearStitchPoints() {
		stitchPointLock.lock();
		try {
			stitchPoints.clear();
		} finally {
			stitchPointLock.unlock();
		}
	}

	public void lockStitchpoints() {
		stitchPointLock.lock();
	}

	public void unlockStitchpoints() {
		stitchPointLock.unlock();
	}
}
