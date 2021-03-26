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

package org.catrobat.catroid.test.content.project;

import android.content.Context;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.exceptions.CompatibilityProjectException;
import org.catrobat.catroid.exceptions.OutdatedVersionProjectException;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.test.StaticSingletonInitializer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import static org.catrobat.catroid.common.Constants.CURRENT_CATROBAT_LANGUAGE_VERSION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({XstreamSerializer.class, ProjectManager.class})
public class LoadProjectsTest {

	private static final double INVALID_LANGUAGE_VERSION = 0.1234;
	private static final double TOO_BIG_LANGUAGE_VERSION = 123.04;
	private static final double OLDEST_LANGUAGE_VERSION = 0.8;
	private static final String RESTORE_PROJECT_STRING = "restorePreviousProject";
	private ProjectManager projectManagerSpy;
	private File fileMock;
	private Context contextMock;
	private Project projectMock;

	@Before
	public void setUp() throws Exception {
		fileMock = Mockito.mock(File.class);
		contextMock = Mockito.mock(Context.class);
		projectMock = Mockito.mock(Project.class);
		StaticSingletonInitializer.initializeStaticSingletonMethods();
		projectManagerSpy = PowerMockito.spy(ProjectManager.getInstance());
		XstreamSerializer xstreamSerializerMock = PowerMockito.mock(XstreamSerializer.class);

		PowerMockito.mockStatic(XstreamSerializer.class);
		PowerMockito.mockStatic(ProjectManager.class);

		when(XstreamSerializer.getInstance()).thenReturn(xstreamSerializerMock);
		when(projectMock.getRequiredResources()).thenReturn(new Brick.ResourcesSet());
		doReturn(projectMock).when(xstreamSerializerMock).loadProject(Mockito.any(), Mockito.any());
	}

	@Test(expected = CompatibilityProjectException.class)
	public void testInvalidLanguageVersion() throws Exception {
		when(projectMock.getCatrobatLanguageVersion()).thenReturn(INVALID_LANGUAGE_VERSION);

		try {
			projectManagerSpy.loadProject(fileMock, contextMock);
		} finally {
			PowerMockito.verifyPrivate(projectManagerSpy, times(1)).invoke(RESTORE_PROJECT_STRING,
					any());
		}
	}

	@Test(expected = OutdatedVersionProjectException.class)
	public void testTooBigLanguageVersion() throws Exception {
		when(projectMock.getCatrobatLanguageVersion()).thenReturn(TOO_BIG_LANGUAGE_VERSION);

		try {
			projectManagerSpy.loadProject(fileMock, contextMock);
		} finally {
			PowerMockito.verifyPrivate(projectManagerSpy, times(1)).invoke(RESTORE_PROJECT_STRING,
					any());
		}
	}

	@Test
	public void testOldestLanguageVersion() throws Exception {
		when(projectMock.getCatrobatLanguageVersion()).thenReturn(OLDEST_LANGUAGE_VERSION);

		projectManagerSpy.loadProject(fileMock, contextMock);

		verify(projectMock, times(1)).setScreenMode(any());

		PowerMockito.verifyStatic(ProjectManager.class, times(1));
		ProjectManager.updateCollisionFormulasTo993(projectMock);

		PowerMockito.verifyStatic(ProjectManager.class, times(1));
		ProjectManager.updateSetPenColorFormulasTo994(projectMock);

		PowerMockito.verifyStatic(ProjectManager.class, times(1));
		ProjectManager.updateArduinoValuesTo995(projectMock);

		PowerMockito.verifyStatic(ProjectManager.class, times(1));
		ProjectManager.updateCollisionScriptsTo996(projectMock);

		PowerMockito.verifyStatic(ProjectManager.class, times(1));
		ProjectManager.makeShallowCopiesDeepAgain(projectMock);

		PowerMockito.verifyStatic(ProjectManager.class, times(1));
		ProjectManager.updateScriptsToTreeStructure(projectMock);

		PowerMockito.verifyStatic(ProjectManager.class, times(1));
		ProjectManager.removePermissionsFile(projectMock);

		PowerMockito.verifyStatic(ProjectManager.class, times(1));
		ProjectManager.updateBackgroundIndexTo9999995(projectMock);

		PowerMockito.verifyStatic(ProjectManager.class, times(1));
		ProjectManager.flattenAllLists(projectMock);

		PowerMockito.verifyStatic(ProjectManager.class, times(1));
		ProjectManager.updateDirectionProperty(projectMock);
	}

	@Test
	public void testCurrentLanguageVersion() throws Exception {
		when(projectMock.getCatrobatLanguageVersion()).thenReturn(CURRENT_CATROBAT_LANGUAGE_VERSION);

		projectManagerSpy.loadProject(fileMock, contextMock);

		verify(projectMock, times(0)).setScreenMode(any());

		PowerMockito.verifyStatic(ProjectManager.class, times(0));
		ProjectManager.updateCollisionFormulasTo993(projectMock);

		PowerMockito.verifyStatic(ProjectManager.class, times(0));
		ProjectManager.updateSetPenColorFormulasTo994(projectMock);

		PowerMockito.verifyStatic(ProjectManager.class, times(0));
		ProjectManager.updateArduinoValuesTo995(projectMock);

		PowerMockito.verifyStatic(ProjectManager.class, times(0));
		ProjectManager.updateCollisionScriptsTo996(projectMock);

		PowerMockito.verifyStatic(ProjectManager.class, times(0));
		ProjectManager.makeShallowCopiesDeepAgain(projectMock);

		PowerMockito.verifyStatic(ProjectManager.class, times(0));
		ProjectManager.updateScriptsToTreeStructure(projectMock);

		PowerMockito.verifyStatic(ProjectManager.class, times(0));
		ProjectManager.removePermissionsFile(projectMock);

		PowerMockito.verifyStatic(ProjectManager.class, times(0));
		ProjectManager.updateDirectionProperty(projectMock);

		if (!BuildConfig.FEATURE_LIST_AS_BASIC_DATATYPE) {
			PowerMockito.verifyStatic(ProjectManager.class, times(1));
		} else {
			PowerMockito.verifyStatic(ProjectManager.class, times(0));
		}
		ProjectManager.flattenAllLists(projectMock);
	}
}
