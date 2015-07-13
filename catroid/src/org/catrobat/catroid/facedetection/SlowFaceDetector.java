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

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;

import org.catrobat.catroid.camera.CameraManager;
import org.catrobat.catroid.camera.JpgPreviewCallback;

public class SlowFaceDetector extends org.catrobat.catroid.facedetection.FaceDetector implements JpgPreviewCallback {

	private static final int NUMBER_OF_FACES = 1;

	@Override
	public boolean startFaceDetection() {
		CameraManager.getInstance().addOnJpgPreviewFrameCallback(this);
		return CameraManager.getInstance().startCamera();
	}

	@Override
	public void stopFaceDetection() {
		CameraManager.getInstance().removeOnJpgPreviewFrameCallback(this);
		CameraManager.getInstance().releaseCamera();
	}

	@Override
	public void onFrame(byte[] data) {
		Bitmap preview = BitmapFactory.decodeByteArray(data, 0, data.length);
		detectFaces(preview);
		preview.recycle();
	}

	private void detectFaces(Bitmap bitmap) {
		if (bitmap == null) {
			return;
		}
		int height = bitmap.getWidth();
		int width = bitmap.getHeight();

		Matrix rotateAndInvertX = new Matrix();
		int rotationAngle = CameraManager.getInstance().getOrientation();
		boolean invertX = CameraManager.getInstance().isFacingBack();
		rotateAndInvertX.postRotate(rotationAngle);
		rotateAndInvertX.postScale(invertX ? -1 : 1, 1);
		Bitmap portraitBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
				rotateAndInvertX, true);
		Bitmap rgb565Bitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
		Paint paint = new Paint();
		paint.setDither(true);

		Canvas canvas = new Canvas();
		canvas.setBitmap(rgb565Bitmap);
		canvas.drawBitmap(portraitBitmap, 0, 0, paint);

		FaceDetector detector = new FaceDetector(width, height, NUMBER_OF_FACES);
		Face[] faces = new Face[NUMBER_OF_FACES];
		int numberOfFaces = detector.findFaces(rgb565Bitmap, faces);

		boolean detected = numberOfFaces > 0;
		onFaceDetected(detected);
		if (detected) {
			PointF centerPoint = new PointF();
			faces[0].getMidPoint(centerPoint);
			float eyeDistance = faces[0].eyesDistance();
			onFaceFound(centerPoint, eyeDistance, width, height);
		}
	}

	private void onFaceFound(PointF centerPoint, float eyeDistance, int detectionWidth, int detectionHeight) {
		Point intPoint = new Point((int) centerPoint.x, (int) centerPoint.y);
		Point relationSize = getRelationForFacePosition();
		Point relativePoint = new Point((intPoint.x - detectionWidth / 2) * relationSize.x / detectionWidth,
				(intPoint.y - detectionHeight / 2) * relationSize.y / detectionHeight);
		int estimatedFaceWidth = (int) (eyeDistance * 2);
		int relativeFaceSize = 200 * estimatedFaceWidth / detectionWidth;
		relativeFaceSize = relativeFaceSize > 100 ? 100 : relativeFaceSize;
		onFaceDetected(relativePoint, relativeFaceSize);
	}
}
