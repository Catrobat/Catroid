/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.ui.runtimepermissions.RequiresPermissionTask;
import org.junit.After;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.util.Collections;

import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StageLifeCycleControllerTest {

	@After
	public void tearDown() {
		StageActivity.stageListener = null;
	}

	@Test
	public void testStageDestroyCallsManageLoadAndFinishWhenStageListenerIsNull() {
		StageActivity mockStageActivity = mock(StageActivity.class);
		ProjectManager mockProjectManager = mock(ProjectManager.class);
		Project mockProject = mock(Project.class);

		try (MockedStatic<ProjectManager> pmMock = mockStatic(ProjectManager.class);
				MockedStatic<RequiresPermissionTask> rpMock = mockStatic(RequiresPermissionTask.class);
				MockedStatic<StageResourceHolder> srhMock = mockStatic(StageResourceHolder.class)) {

			pmMock.when(ProjectManager::getInstance).thenReturn(mockProjectManager);
			when(mockProjectManager.getCurrentProject()).thenReturn(mockProject);
			when(mockProjectManager.getCurrentlyEditedScene()).thenReturn(null);

			srhMock.when(() -> StageResourceHolder.getProjectsRuntimePermissionList(anyInt()))
					.thenReturn(Collections.emptyList());
			rpMock.when(() -> RequiresPermissionTask.checkPermission(any(), any()))
					.thenReturn(true);

			StageActivity.stageListener = null;

			StageLifeCycleController.stageDestroy(mockStageActivity);

			verify(mockStageActivity).manageLoadAndFinish();
			assertNull(StageActivity.stageListener);
		}
	}

	@Test
	public void testStageDestroyCallsStageListenerFinishWhenNotNull() {
		StageActivity mockStageActivity = mock(StageActivity.class);
		ProjectManager mockProjectManager = mock(ProjectManager.class);
		Project mockProject = mock(Project.class);
		StageListener mockStageListener = mock(StageListener.class);

		try (MockedStatic<ProjectManager> pmMock = mockStatic(ProjectManager.class);
				MockedStatic<RequiresPermissionTask> rpMock = mockStatic(RequiresPermissionTask.class);
				MockedStatic<StageResourceHolder> srhMock = mockStatic(StageResourceHolder.class)) {

			pmMock.when(ProjectManager::getInstance).thenReturn(mockProjectManager);
			when(mockProjectManager.getCurrentProject()).thenReturn(mockProject);
			when(mockProjectManager.getCurrentlyEditedScene()).thenReturn(null);

			srhMock.when(() -> StageResourceHolder.getProjectsRuntimePermissionList(anyInt()))
					.thenReturn(Collections.emptyList());
			rpMock.when(() -> RequiresPermissionTask.checkPermission(any(), any()))
					.thenReturn(true);

			StageActivity.stageListener = mockStageListener;

			StageLifeCycleController.stageDestroy(mockStageActivity);

			verify(mockStageListener).finish();
			verify(mockStageActivity).manageLoadAndFinish();
			assertNull(StageActivity.stageListener);
		}
	}
}
