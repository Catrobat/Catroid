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
package org.catrobat.catroid.test.camera;

import android.hardware.Camera;

import junit.framework.TestCase;

import org.catrobat.catroid.camera.CameraManager;
import org.catrobat.catroid.uitest.annotation.Device;

public class CameraManagerTest extends TestCase {

	public void testCameraNotAvailable() {
		Camera camera = null;
		camera = Camera.open();

		try {
			boolean started = CameraManager.getInstance().startCamera();
			assertFalse("Expected camera not to be able to start when hardware already in use", started);
		} catch (Exception exc) {
			String errorMsg = "Unavailable camera should not cause an exception \"" + exc.getMessage() + "\"";
			fail(errorMsg);
		} finally {
			if (camera != null) {
				camera.release();
			}
		}
	}

	public void testCameraRelease() {
		CameraManager.getInstance().startCamera();
		CameraManager.getInstance().releaseCamera();

		Camera camera = null;
		try {
			camera = Camera.open();
		} catch (Exception exc) {
			fail("Camera was not propperly released");
		} finally {
			if (camera != null) {
				camera.release();
			}
		}
	}

	@Device
	public void testDoubleStart() {
		boolean success = CameraManager.getInstance().startCamera();
		assertTrue("Camera was not started properly", success);
		try {
			CameraManager.getInstance().startCamera();
		} catch (Exception e) {
			fail("Secound start of camera should be ignored but produced exception: " + e.getMessage());
		}
		CameraManager.getInstance().releaseCamera();
	}

	public void testGetInstance() {
		assertNotNull("Could not get instance of CameraManager", CameraManager.getInstance());
	}
}
