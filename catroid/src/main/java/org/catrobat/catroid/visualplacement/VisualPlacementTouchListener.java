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

package org.catrobat.catroid.visualplacement;

import android.os.SystemClock;
import android.view.MotionEvent;
import android.widget.ImageView;

public class VisualPlacementTouchListener {

	private Mode mode;
	private float previousY;
	private float previousX;
	private long lastTimeMove = 0;

	private void setMode(Mode mode) {
		this.mode = mode;
	}

	public boolean onTouch(ImageView imageView, MotionEvent event, CoordinateInterface coordinateInterface) {
		if (event.getPointerId(0) == 0) {
			float currentX = event.getRawX();
			float currentY = event.getRawY();

			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					setMode(Mode.TAP);
					previousX = currentX;
					previousY = currentY;
					lastTimeMove = SystemClock.elapsedRealtime();
					break;
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:
					if (mode == Mode.TAP) {
						imageView.setX(event.getX() - (float) imageView.getWidth() / 2);
						imageView.setY(event.getY() - (float) imageView.getHeight() / 2);
					}
					break;
				case MotionEvent.ACTION_MOVE:
					long delayTime = SystemClock.elapsedRealtime() - lastTimeMove;
					if (delayTime > 100) {
						setMode(Mode.MOVE);
						float dX = currentX - previousX;
						float dY = currentY - previousY;
						imageView.setX(imageView.getX() + dX);
						imageView.setY(imageView.getY() + dY);
						previousX = currentX;
						previousY = currentY;
					}
					break;
			}
			coordinateInterface.setXCoordinate(imageView.getX());
			coordinateInterface.setYCoordinate(-imageView.getY());
			return true;
		} else {
			return false;
		}
	}

	public enum Mode {MOVE, TAP}
}
