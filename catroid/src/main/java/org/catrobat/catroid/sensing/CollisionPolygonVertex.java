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

package org.catrobat.catroid.sensing;

import android.graphics.PointF;

public class CollisionPolygonVertex {
	public float startX;
	public float startY;
	public float endX;
	public float endY;

	public CollisionPolygonVertex(float startX, float startY, float endX, float endY) {
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CollisionPolygonVertex)) {
			return false;
		}
		if (o == this) {
			return true;
		}

		CollisionPolygonVertex other = (CollisionPolygonVertex) o;
		return other.startX == startX && other.startY == startY && other.endX == endX && other.endY == endY;
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	public void extend(float x, float y) {
		endX = x;
		endY = y;
	}

	public String toString() {
		return startX + "/" + startY + " -> " + endX + "/" + endY;
	}

	public void flip() {
		float xTemp = startX;
		float yTemp = startY;
		startX = endX;
		startY = endY;
		endX = xTemp;
		endY = yTemp;
	}

	public PointF getStartPoint() {
		return new PointF(startX, startY);
	}

	public PointF getEndPoint() {
		return new PointF(endX, endY);
	}

	public boolean isConnected(CollisionPolygonVertex other) {
		boolean connected = other.startX == this.endX
				&& other.startY == this.endY;
		if (connected) {
			return true;
		} else if (isConnectedBackwards(other)) {
			other.flip();
			return true;
		}
		return false;
	}

	private boolean isConnectedBackwards(CollisionPolygonVertex other) {
		return other.endX == this.endX
				&& other.endY == this.endY;
	}
}
