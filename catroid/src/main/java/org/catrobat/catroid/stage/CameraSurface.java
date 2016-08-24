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

package org.catrobat.catroid.stage;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.catrobat.catroid.camera.CameraManager;

public class CameraSurface extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = CameraSurface.class.getSimpleName();

	private Camera camera = null;

	public CameraSurface(Context context) {
		super(context);

		// We're implementing the Callback interface and want to get notified
		// about certain surface events.
		getHolder().addCallback(this);
		// We're changing the surface to a PUSH surface, meaning we're receiving
		// all buffer data from another component - the camera, in this case.
		getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceCreated(SurfaceHolder surfaceHolder) {
		camera = CameraManager.getInstance().getCurrentCamera();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// This method is called when the surface changes, e.g. when it's size is set.
		// We use the opportunity to initialize the camera preview display dimensions

		// We also assign the preview display to this surface...
		synchronized (CameraManager.getInstance().cameraChangeLock) {
			try {
				if (camera != null) {
					camera.stopPreview();
					camera.setPreviewDisplay(holder);
					camera.startPreview();
				}
			} catch (Exception e) {
				Log.e(TAG, "Error at surfaceChanged");
				Log.e(TAG, e.getMessage());
			}
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
		this.getHolder().removeCallback(this);
		//camera.stopPreview();
		//camera.release();
		//camera = null;
	}
}
