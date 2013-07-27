/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.facedetection;

import android.annotation.TargetApi;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.os.Build;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class IcsFaceDetector extends FaceDetector implements FaceDetectionListener {

	private Camera camera;

	@Override
	public void startFaceDetection() {
		Log.d("Blah", "ICS Start");
		if (camera != null) {
			return;
		}
		camera = Camera.open();
		if (camera == null) {
			return;
		}
		camera.setFaceDetectionListener(this);
		camera.startPreview();
		camera.startFaceDetection();
	}

	@Override
	public void stopFaceDetection() {
		Log.d("Blah", "ICS Stop");
		if (camera == null) {
			return;
		}
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	@Override
	public void onFaceDetection(Face[] faces, Camera camera) {
		if (faces.length > 0) {
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

	public static boolean isSupported() {
		//		if (true) {
		//			return false;
		//		}
		int currentApi = android.os.Build.VERSION.SDK_INT;
		if (currentApi < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return false;
		}
		int possibleFaces = 0;
		Camera camera = null;
		try {
			camera = Camera.open();
			possibleFaces = camera.getParameters().getMaxNumDetectedFaces();
			camera.release();
		} catch (Exception exc) {
		} finally {
			if (camera != null) {
				camera.release();
			}
		}
		return possibleFaces > 0;
	}
}
