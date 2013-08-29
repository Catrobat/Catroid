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

import android.hardware.Camera;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CameraManager implements Camera.PreviewCallback {

	private static CameraManager instance;

	private Camera camera;
	private List<Camera.PreviewCallback> callbacks = new ArrayList<Camera.PreviewCallback>();

	public static CameraManager getInstance() {
		if (instance == null) {
			instance = new CameraManager();
		}
		return instance;
	}

	public CameraManager() {

	}

	public Camera getCamera() {
		if (camera == null) {
			createCamera();
		}
		return camera;
	}

	public boolean createCamera() {
		if (camera != null) {
			return false;
		}
		camera = Camera.open();
		camera.setPreviewCallback(this);
		return camera != null;
	}

	public void startCamera() {
		if (camera == null) {
			return;
		}
		camera.startPreview();
	}

	public void releaseCamera() {
		if (camera == null) {
			return;
		}
		camera.setPreviewCallback(null);
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	public void addOnPreviewFrameCallback(Camera.PreviewCallback callback) {
		if (callbacks.contains(callback)) {
			Log.e("Blah", "already added");
			return;
		}
		callbacks.add(callback);
	}

	public void removeOnPreviewFrameCallback(Camera.PreviewCallback callback) {
		callbacks.remove(callback);
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		for (Camera.PreviewCallback callback : callbacks) {
			callback.onPreviewFrame(data, camera);
		}
	}

}
