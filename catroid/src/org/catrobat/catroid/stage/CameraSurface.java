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

import java.io.IOException;

public class CameraSurface extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = CameraSurface.class.getSimpleName();

	private SurfaceHolder mHolder;
	private Camera mCamera;

	public CameraSurface(Context context, Camera camera){
		super(context);

		mCamera = camera;
		//mCamera.setDisplayOrientation(90);
		//get the holder and set this class as the callback, so we can get camera data here
		mHolder = getHolder();
		mHolder.addCallback(this);
		// We're changing the surface to a PUSH surface, meaning we're receiving
		// all buffer data from another component - the camera, in this case.
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceCreated(mHolder);
	}
	@Override
	public void surfaceCreated(SurfaceHolder surfaceHolder) {
		try{
			//when the surface is created, we can set the camera to draw images in this surfaceholder
			Log.e(TAG, "Before Preview");
			mCamera.setPreviewDisplay(surfaceHolder);
			mCamera.startPreview();
		} catch (IOException e) {
			Log.d("ERROR", "Camera error on surfaceCreated " + e.getMessage());
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
		//before changing the application orientation, you need to stop the preview, rotate and then start it again
		if(mHolder.getSurface() == null)//check if the surface is ready to receive camera data
			return;

		try{
			mCamera.stopPreview();
		} catch (Exception e){
			//this will happen when you are trying the camera if it's not running
		}

		//now, recreate the camera preview
		try{
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();
		} catch (IOException e) {
			Log.d("ERROR", "Camera error on surfaceChanged " + e.getMessage());
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
		//our app has only one screen, so we'll destroy the camera in the surface
		//if you are unsing with more screens, please move this code your activity
		Log.e(TAG, "On Destruction");
		mCamera.stopPreview();
		mHolder.removeCallback(this);
	}
}
