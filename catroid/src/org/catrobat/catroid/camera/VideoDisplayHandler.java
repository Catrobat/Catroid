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
package org.catrobat.catroid.camera;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;

import org.catrobat.catroid.stage.StageListener;

import java.io.ByteArrayOutputStream;

public class VideoDisplayHandler implements Camera.PreviewCallback {

	private static VideoDisplayHandler instance;

	//private List<>

	public static void registerSprite() {
		if (instance == null) {
			instance = new VideoDisplayHandler();
		}

	}

	public static void unregisterSprite() {
		if (instance == null) {
			return;
		}

	}

	public static void startVideoStream() {
		if (instance == null) {
			instance = new VideoDisplayHandler();
		}
		CameraManager.getInstance().addOnPreviewFrameCallback(instance);
		// camera is currently only started by face detection
	}

	public static void stopVideoStream() {
		if (instance == null) {
			return;
		}
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		Parameters parameters = camera.getParameters();
		int imageFormat = parameters.getPreviewFormat();
		byte[] decodableBytes;
		if (imageFormat == ImageFormat.RGB_565 || imageFormat == ImageFormat.JPEG) {
			decodableBytes = data;
		} else {
			int width = parameters.getPreviewSize().width;
			int height = parameters.getPreviewSize().height;
			YuvImage image = new YuvImage(data, imageFormat, width, height, null);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			image.compressToJpeg(new Rect(0, 0, width, height), 50, out);
			decodableBytes = out.toByteArray();
		}
		StageListener.testData = decodableBytes;
	}
}
