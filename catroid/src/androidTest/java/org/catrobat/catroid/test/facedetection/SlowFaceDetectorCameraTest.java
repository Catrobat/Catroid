/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
package org.catrobat.catroid.test.facedetection;

import android.hardware.Camera;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.camera.CameraManager;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.facedetection.SlowFaceDetector;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SlowFaceDetectorCameraTest {
	private Camera camera;

	@Rule
	public GrantPermissionRule runtimePermissionRule = GrantPermissionRule.grant(android.Manifest.permission.CAMERA);

	@Before
	public void setUp() throws Exception {
		CameraManager.makeInstance();
		ScreenValues.SCREEN_WIDTH = 720;
		ScreenValues.SCREEN_HEIGHT = 1080;
		ProjectManager.getInstance().setCurrentProject(new Project());
	}

	@After
	public void tearDown() {
		if (camera != null) {
			camera.release();
		}
	}

	@Test
	public void testStartAndStop() {
		SlowFaceDetector detector = new SlowFaceDetector();
		assertNotNull(detector);

		assertTrue(detector.startFaceDetection());

		detector.stopFaceDetection();

		camera = Camera.open();
		assertNotNull(Camera.open());
	}
}
