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

package org.catrobat.catroid.test.utiltests;

import android.hardware.Camera;

import org.catrobat.catroid.camera.CameraManager;
import org.catrobat.catroid.utils.FlashUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertFalse;

import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CameraManager.class, Camera.class})
public class FlashUtilTest {

	private CameraManager oldCameraManagerInstance;
	private CameraManager cameraManagerMock;

	@Before
	public void setUp() {
		oldCameraManagerInstance = CameraManager.getInstance();
		cameraManagerMock = PowerMockito.mock(CameraManager.class);
		Camera cameraMock = PowerMockito.mock(Camera.class);
		Camera.Parameters parametersMock = PowerMockito.mock(Camera.Parameters.class);
		when(cameraMock.getParameters()).thenReturn(parametersMock);
		when(cameraManagerMock.getCurrentCamera()).thenReturn(cameraMock);

		CameraManager.setInstance(cameraManagerMock);
		FlashUtil.reset();
	}

	@After
	public void resetInstance() {
		CameraManager.setInstance(oldCameraManagerInstance);
	}

	private void simFlashAvailable(boolean flashAvailable) {
		when(cameraManagerMock.isCameraFlashAvailable()).thenReturn(flashAvailable);
		when(cameraManagerMock.hasCurrentCameraFlash()).thenReturn(flashAvailable);
		when(cameraManagerMock.startCamera()).thenReturn(flashAvailable);
	}

	@Test
	public void testInitializeFlashWithoutCameraFlash() {
		simFlashAvailable(false);
		FlashUtil.initializeFlash();

		assertFalse(FlashUtil.isAvailable());
	}

	@Test
	public void testInitializeFlashWithCameraFlash() {
		simFlashAvailable(true);
		FlashUtil.initializeFlash();

		assertTrue(FlashUtil.isAvailable());
	}

	@Test
	public void testFlashOnWithoutCameraFlash() {
		simFlashAvailable(false);
		FlashUtil.initializeFlash();
		FlashUtil.flashOn();

		assertFalse(FlashUtil.isOn());
	}

	@Test
	public void testFlashOnWithCameraFlash() {
		simFlashAvailable(true);
		FlashUtil.initializeFlash();
		FlashUtil.flashOn();

		assertTrue(FlashUtil.isOn());
	}

	@Test
	public void testFlashOffWithoutCameraFlash() {
		simFlashAvailable(false);
		FlashUtil.initializeFlash();
		FlashUtil.flashOff();

		assertFalse(FlashUtil.isOn());
	}

	@Test
	public void testFlashOffWithCameraFlash() {
		simFlashAvailable(true);
		FlashUtil.initializeFlash();
		FlashUtil.flashOff();

		assertFalse(FlashUtil.isOn());
	}

	@Test
	public void testPauseFlashWithoutCameraFlash() {
		simFlashAvailable(false);
		FlashUtil.initializeFlash();
		FlashUtil.pauseFlash();

		assertFalse(FlashUtil.isPaused());
	}

	@Test
	public void testResumeFlashWithoutCameraFlash() {
		simFlashAvailable(false);
		FlashUtil.initializeFlash();
		FlashUtil.pauseFlash();
		FlashUtil.resumeFlash();

		assertFalse(FlashUtil.isPaused());
	}

	@Test
	public void testPauseFlashWithCameraFlash() {
		simFlashAvailable(true);
		FlashUtil.initializeFlash();
		FlashUtil.pauseFlash();

		assertTrue(FlashUtil.isPaused());
	}

	@Test
	public void testResumeFlashWithCameraFlash() {
		simFlashAvailable(true);
		FlashUtil.initializeFlash();
		FlashUtil.pauseFlash();

		assertTrue(FlashUtil.isPaused());

		FlashUtil.resumeFlash();

		assertFalse(FlashUtil.isPaused());
	}
}

