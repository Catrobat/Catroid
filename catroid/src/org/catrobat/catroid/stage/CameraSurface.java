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

import java.io.IOException;

public class CameraSurface extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = CameraSurface.class.getSimpleName();

	private SurfaceHolder holder;
	private Camera camera;

	public CameraSurface(Context context) {
		super(context);

		camera = Camera.open();
		holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceCreated(holder);
	}

	public CameraSurface(Context context, Camera camera) {
		super(context);

		this.camera = camera;
		holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceCreated(holder);
	}

	@Override
	public void surfaceCreated(SurfaceHolder surfaceHolder) {
		try {
			camera.setPreviewDisplay(surfaceHolder);
		} catch (IOException e) {
			Log.e("ERROR", "Camera error on surfaceCreated " + e.getMessage());
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
		if (holder.getSurface() == null) {
			return;
		}

		try {
			camera.stopPreview();
		} catch (Exception e) {
			Log.e(TAG, "Camera not running on surfaceChanged ");
		}

		try {
			camera.setPreviewDisplay(holder);
			camera.startPreview();
		} catch (Exception e) {
			Log.e("ERROR", "Camera error on surfaceChanged " + e.getMessage());
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
		holder.removeCallback(this);
	}
}
