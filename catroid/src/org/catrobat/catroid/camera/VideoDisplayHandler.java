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

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Sprite;

import java.io.ByteArrayOutputStream;

public class VideoDisplayHandler implements Camera.PreviewCallback {

	private static VideoDisplayHandler instance;

	private VideoLookData videoLookData = new VideoLookData();
	private boolean started = false;

	public static void registerSprite(Sprite sprite) {
		if (instance == null) {
			instance = new VideoDisplayHandler();
		}
		VideoLook look = new VideoLook(sprite);
		look.createBrightnessContrastShader();
		sprite.look = look;
	}

	public static void unregisterSprite() {
		if (instance == null) {
			return;
		}
		// not supported yet
	}

	public static void startVideoStream() {
		if (instance == null) {
			instance = new VideoDisplayHandler();
		}
		if (instance.started) {
			return;
		}
		instance.started = true;
		CameraManager.getInstance().addOnPreviewFrameCallback(instance);
		// camera is currently only started by face detection
	}

	public static void stopVideoStream() {
		if (instance == null) {
			return;
		}
		instance.started = false;
		CameraManager.getInstance().removeOnPreviewFrameCallback(instance);
		instance.videoLookData = new VideoLookData(); // TODO why?
	}

	public static byte[] getDecodeableBytesFromCameraFrame(byte[] cameraData, Camera camera) {
		Parameters parameters = camera.getParameters();
		int imageFormat = parameters.getPreviewFormat();
		byte[] decodableBytes;
		if (imageFormat == ImageFormat.RGB_565 || imageFormat == ImageFormat.JPEG) {
			decodableBytes = cameraData;
		} else {
			int width = parameters.getPreviewSize().width;
			int height = parameters.getPreviewSize().height;
			YuvImage image = new YuvImage(cameraData, imageFormat, width, height, null);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			image.compressToJpeg(new Rect(0, 0, width, height), 50, out);
			decodableBytes = out.toByteArray();
		}
		return decodableBytes;
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		byte[] decodeableBytes = getDecodeableBytesFromCameraFrame(data, camera);
		videoLookData.setVideoFrameData(decodeableBytes);
	}

	public static LookData getVideoLookData() {
		if (instance == null) {
			return null;
		}
		return instance.videoLookData;
	}
}
