/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.test.ui;

import android.app.Activity;

import org.catrobat.catroid.ui.runtimepermissions.RequiresPermissionTask;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class RequiresPermissionTaskTest {

	private RequiresPermissionTask task;
	private Activity activity;
	private String permission = "permission";
	private boolean taskCalled = false;

	@Before
	public void setUp() {
		activity = mock(AppCompatActivity.class);
		List<String> permissions = new ArrayList<>();
		permissions.add(permission);
		task = new RequiresPermissionTask(0, permissions, 0) {
			@Override
			public void task() {
				taskCalled = true;
			}
		};
	}
	@Test
	public void executeTaskIfPermissionGrantedTest() {
		when(activity.checkPermission(eq(permission), anyInt(), anyInt())).thenReturn(PERMISSION_GRANTED);
		task.execute(activity);
		assertTrue(taskCalled);
	}
}
