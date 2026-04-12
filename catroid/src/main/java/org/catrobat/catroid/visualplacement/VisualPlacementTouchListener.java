/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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

import android.view.MotionEvent;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class VisualPlacementTouchListener {

	private Mode mode;
	private static final long TAP_DELAY_THRESHOLD = 100;
	private static final long JITTER_DELAY_THRESHOLD = 30;
	private static final float JITTER_DISTANCE_THRESHOLD = 10.f * 10.f;
	private float previousX;
	private float previousY;
	private List<TouchEventData> recentTouchEventsData = new ArrayList<>();

	private ResizeRotateGestureDetector resizeRotateDetector;
	private BoundingBoxOverlay boundingBoxOverlay;

	private final class TouchEventData {
		private final long timeStamp;
		private final float xCoordinate;
		private final float yCoordinate;

		private TouchEventData(long time, float x, float y) {
			timeStamp = time;
			xCoordinate = x;
			yCoordinate = y;
		}
	}

	private void setMode(Mode mode) {
		this.mode = mode;
	}

	public void setResizeRotateDetector(ResizeRotateGestureDetector detector) {
		this.resizeRotateDetector = detector;
	}

	public void setBoundingBoxOverlay(BoundingBoxOverlay overlay) {
		this.boundingBoxOverlay = overlay;
	}

	public boolean onTouch(ImageView imageView, MotionEvent event, CoordinateInterface coordinateInterface) {
		if (imageView == null || coordinateInterface == null) {
			return false;
		}

		if (handleMultiTouch(event)) {
			return true;
		}

		return handleSingleTouch(imageView, event, coordinateInterface);
	}

	private boolean handleMultiTouch(MotionEvent event) {
		if (resizeRotateDetector == null) {
			return false;
		}
		if (event.getPointerCount() >= 2 || resizeRotateDetector.isTransforming()) {
			boolean wasTransforming = resizeRotateDetector.isTransforming();
			boolean handled = resizeRotateDetector.onTouchEvent(event);
			boolean isCurrentlyTransforming = resizeRotateDetector.isTransforming();

			if (isCurrentlyTransforming) {
				updateBoundingBox();
				return true;
			}
			if (wasTransforming && !isCurrentlyTransforming) {
				previousX = event.getRawX();
				previousY = event.getRawY();
				setMode(Mode.MOVE);
				updateBoundingBox();
				return true;
			}
		}
		return false;
	}

	private boolean handleSingleTouch(ImageView imageView, MotionEvent event, CoordinateInterface coordinateInterface) {
		if (event.getPointerCount() <= 0 || event.getPointerId(0) != 0) {
			return false;
		}

		float currentX = event.getRawX();
		float currentY = event.getRawY();
		long motionEventTime = event.getEventTime();

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				setMode(Mode.TAP);
				previousX = currentX;
				previousY = currentY;
				recentTouchEventsData.add(new TouchEventData(motionEventTime, currentX, currentY));
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				handleTouchUp(imageView, event, currentX, currentY, motionEventTime);
				break;
			case MotionEvent.ACTION_MOVE:
				handleTouchMove(imageView, event, currentX, currentY, motionEventTime);
				break;
		}
		coordinateInterface.setXCoordinate(imageView.getX());
		coordinateInterface.setYCoordinate(-imageView.getY());
		updateBoundingBox();
		return true;
	}

	private void handleTouchUp(ImageView imageView, MotionEvent event, float currentX, float currentY, long motionEventTime) {
		if (mode == Mode.TAP) {
			imageView.setX(event.getX() - (float) imageView.getWidth() / 2);
			imageView.setY(event.getY() - (float) imageView.getHeight() / 2);
		} else {
			removeObsoleteTouchEventsData(motionEventTime);
			float dX = currentX - previousX;
			float dY = currentY - previousY;
			if (!recentTouchEventsData.isEmpty() && recentTouchEventsData.size() > 1) {
				TouchEventData oldestEntry = recentTouchEventsData.get(0);
				float distanceCorrectionX = currentX - oldestEntry.xCoordinate;
				float distanceCorrectionY = currentY - oldestEntry.yCoordinate;
				double distance = distanceCorrectionX * distanceCorrectionX + distanceCorrectionY * distanceCorrectionY;
				if (distance < JITTER_DISTANCE_THRESHOLD && distance != 0) {
					dX -= distanceCorrectionX;
					dY -= distanceCorrectionY;
				}
			}
			imageView.setX(imageView.getX() + dX);
			imageView.setY(imageView.getY() + dY);
		}
	}

	private void handleTouchMove(ImageView imageView, MotionEvent event, float currentX, float currentY, long motionEventTime) {
		long delayTime = motionEventTime - event.getDownTime();
		if (delayTime > TAP_DELAY_THRESHOLD) {
			setMode(Mode.MOVE);
			float dX = currentX - previousX;
			float dY = currentY - previousY;
			imageView.setX(imageView.getX() + dX);
			imageView.setY(imageView.getY() + dY);
			recentTouchEventsData.add(new TouchEventData(motionEventTime, currentX, currentY));
			previousX = currentX;
			previousY = currentY;
			removeObsoleteTouchEventsData(motionEventTime);
		}
	}

	private void updateBoundingBox() {
		if (boundingBoxOverlay != null) {
			boundingBoxOverlay.updateOverlay();
		}
	}

	private void removeObsoleteTouchEventsData(long timeStamp) {
		List<TouchEventData> obsoleteTouchEventsData = new ArrayList<>();
		for (TouchEventData touchEventData : recentTouchEventsData) {
			if (touchEventData != null) {
				if ((timeStamp - touchEventData.timeStamp) > JITTER_DELAY_THRESHOLD) {
					obsoleteTouchEventsData.add(touchEventData);
				} else {
					break;
				}
			}
		}
		recentTouchEventsData.removeAll(obsoleteTouchEventsData);
	}

	public enum Mode {MOVE, TAP}
}
