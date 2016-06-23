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
package org.catrobat.catroid.test.camera;

import android.content.Context;
import android.test.AndroidTestCase;

import org.catrobat.catroid.camera.CameraManager;
import org.catrobat.catroid.common.DefaultProjectHandler;
import org.catrobat.catroid.uitest.annotation.Device;

public class CameraManagerTest extends AndroidTestCase {

	private Context cameraContext = null;

	public void testCameraStart() {
		if (createTestProject()) {
			boolean success = CameraManager.getInstance().startCamera();
			assertTrue("Camera was not started properly", success);

			CameraManager.getInstance().releaseCamera();
			assertFalse("Camera was not released properly", CameraManager.getInstance().isReady());
		}
	}

	public void testCameraRelease() {
		if (createTestProject()) {
			boolean success = CameraManager.getInstance().startCamera();
			assertTrue("Camera was not started properly", success);

			CameraManager.getInstance().releaseCamera();
			assertFalse("Camera was not released properly", CameraManager.getInstance().isReady());

			try {
				success = CameraManager.getInstance().startCamera();
				CameraManager.getInstance().releaseCamera();
				assertTrue("Camera was not started properly after releasing the first one", success);
				assertFalse("Camera was not released properly", CameraManager.getInstance().isReady());
			} catch (Exception exc) {
				fail("Camera was not properly released");
			}
		}
	}

	@Device
	public void testDoubleStart() {
		if (createTestProject()) {
			boolean success = CameraManager.getInstance().startCamera();
			assertTrue("Camera was not started properly", success);

			try {
				success = CameraManager.getInstance().startCamera();
				CameraManager.getInstance().releaseCamera();
				assertFalse("Camera was started after not releasing the first one", success);
				assertFalse("Camera was not released properly", CameraManager.getInstance().isReady());
			} catch (Exception e) {
				fail("Second start of camera should be ignored but produced exception: " + e.getMessage());
			}
		}
	}

	public void testGetInstance() {
		assertNotNull("Could not get instance of CameraManager", CameraManager.getInstance());
	}

	private boolean createTestProject() {
		if (!CameraManager.getInstance().hasFrontCamera() && !CameraManager.getInstance().hasBackCamera()) {
			return false;
		}
		if (cameraContext == null) {
			cameraContext = getContext();
		}
		CameraManager.getInstance().setCameraContext(cameraContext);

		try {
			DefaultProjectHandler.createAndSaveDefaultProject(cameraContext);
		} catch (Exception e) {
			fail("Could not create and save default project");
		}
		return true;
	}
}
