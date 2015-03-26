/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.facedetection;

import android.annotation.TargetApi;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.os.Build;

import org.catrobat.catroid.camera.CameraManager;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class IcsFaceDetector extends FaceDetector implements FaceDetectionListener {

	private boolean running = false;

	public IcsFaceDetector() {
	}

	@Override
	public boolean startFaceDetection() {
		if (running) {
			return true;
		}
		Camera camera = CameraManager.getInstance().getCamera();
		if (camera == null) {
			return false;
		}
		camera.setFaceDetectionListener(this);
		running = CameraManager.getInstance().startCamera();
		camera.startFaceDetection();
		return running;
	}

	@Override
	public void stopFaceDetection() {
		if (!running) {
			return;
		}
		running = false;
		CameraManager.getInstance().releaseCamera();
	}

	@Override
	public void onFaceDetection(Face[] faces, Camera camera) {
		boolean detected = faces.length > 0;
		onFaceDetected(detected);
		if (detected) {
			int maxConfidence = faces[0].score;
			int bestFaceIndex = 0;
			for (int i = 1; i < faces.length; i++) {
				if (faces[i].score > maxConfidence) {
					maxConfidence = faces[i].score;
					bestFaceIndex = i;
				}
			}
			Face bestFace = faces[bestFaceIndex];
			Rect faceBounds = bestFace.rect;
			Point centerPoint = new Point(faceBounds.centerX(), faceBounds.centerY());
			Point portraitCenterPoint = new Point(centerPoint.y, centerPoint.x);
			Point relationSize = getRelationForFacePosition();
			Point relativePoint = new Point(portraitCenterPoint.x * relationSize.x / 2000, portraitCenterPoint.y
					* relationSize.y / 2000);
			int faceSize = (faceBounds.right - faceBounds.left) / 10;
			faceSize = faceSize > 100 ? 100 : faceSize;
			onFaceDetected(relativePoint, faceSize);
		}
	}
}
