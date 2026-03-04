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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;

import static org.catrobat.catroid.common.Constants.CURRENT_CATROBAT_LANGUAGE_VERSION;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoadProjectsTest {

	private static final double INVALID_LANGUAGE_VERSION = 0.1234;
	private static final double TOO_BIG_LANGUAGE_VERSION = 123.04;
	private static final double OLDEST_LANGUAGE_VERSION = 0.8;
	private ProjectManager projectManagerSpy;
	private File fileMock;
	private Context contextMock;
	private Project projectMock;

	private MockedStatic<XstreamSerializer> xstreamSerializerMockStatic;
	private MockedStatic<ProjectManager> projectManagerMock;

	@Before
	public void setUp() throws Exception {
		fileMock = Mockito.mock(File.class);
		contextMock = Mockito.mock(Context.class);
		projectMock = Mockito.mock(Project.class);
		StaticSingletonInitializer.initializeStaticSingletonMethods();
		projectManagerSpy = Mockito.spy(ProjectManager.getInstance());
		XstreamSerializer xstreamSerializerMock = Mockito.mock(XstreamSerializer.class);

		xstreamSerializerMockStatic = Mockito.mockStatic(XstreamSerializer.class);
		projectManagerMock = Mockito.mockStatic(ProjectManager.class);

		projectManagerMock.when(ProjectManager::getInstance).thenReturn(projectManagerSpy);
		xstreamSerializerMockStatic.when(XstreamSerializer::getInstance).thenReturn(xstreamSerializerMock);
		when(projectMock.getRequiredResources()).thenReturn(new Brick.ResourcesSet());
		doReturn(projectMock).when(xstreamSerializerMock).loadProject(Mockito.any(),
				Mockito.any());
	}

	@After
	public void tearDown() {
		xstreamSerializerMockStatic.close();
		projectManagerMock.close();
	}

	@Test(expected = CompatibilityProjectException.class)
	public void testInvalidLanguageVersion() throws Exception {
		when(projectMock.getCatrobatLanguageVersion()).thenReturn(INVALID_LANGUAGE_VERSION);

		Project previousProject = projectManagerSpy.getCurrentProject();
		try {
			projectManagerSpy.loadProject(fileMock, contextMock);
		} finally {
			assertEquals(previousProject, projectManagerSpy.getCurrentProject());
		}
	}

	@Test(expected = OutdatedVersionProjectException.class)
	public void testTooBigLanguageVersion() throws Exception {
		when(projectMock.getCatrobatLanguageVersion()).thenReturn(TOO_BIG_LANGUAGE_VERSION);

		Project previousProject = projectManagerSpy.getCurrentProject();
		try {
			projectManagerSpy.loadProject(fileMock, contextMock);
		} finally {
			assertEquals(previousProject, projectManagerSpy.getCurrentProject());
		}
	}

	@Test
	public void testOldestLanguageVersion() throws Exception {
		when(projectMock.getCatrobatLanguageVersion()).thenReturn(OLDEST_LANGUAGE_VERSION);

		projectManagerSpy.loadProject(fileMock, contextMock);

		verify(projectMock, times(1)).setScreenMode(any());

		projectManagerMock.verify(() ->
				ProjectManager.updateCollisionFormulasTo993(projectMock), times(1)
		);

		projectManagerMock.verify(() ->
				ProjectManager.updateSetPenColorFormulasTo994(projectMock), times(1)
		);

		projectManagerMock.verify(() ->
				ProjectManager.updateArduinoValuesTo995(projectMock), times(1)
		);

		projectManagerMock.verify(() ->
				ProjectManager.updateCollisionScriptsTo996(projectMock), times(1)
		);

		projectManagerMock.verify(() ->
				ProjectManager.makeShallowCopiesDeepAgain(projectMock), times(1)
		);

		projectManagerMock.verify(() ->
				ProjectManager.updateScriptsToTreeStructure(projectMock), times(1)
		);

		projectManagerMock.verify(() ->
				ProjectManager.removePermissionsFile(projectMock), times(1)
		);

		projectManagerMock.verify(() ->
				ProjectManager.updateBackgroundIndexTo9999995(projectMock), times(1)
		);

		projectManagerMock.verify(() ->
				ProjectManager.flattenAllLists(projectMock), times(1)
		);

		projectManagerMock.verify(() ->
				ProjectManager.updateDirectionProperty(projectMock), times(1)
		);
	}

	@Test
	public void testCurrentLanguageVersion() throws Exception {
		when(projectMock.getCatrobatLanguageVersion()).thenReturn(CURRENT_CATROBAT_LANGUAGE_VERSION);

		projectManagerSpy.loadProject(fileMock, contextMock);

		verify(projectMock, times(0)).setScreenMode(any());

		projectManagerMock.verify(() ->
				ProjectManager.updateCollisionFormulasTo993(projectMock), times(0)
		);

		projectManagerMock.verify(() ->
				ProjectManager.updateSetPenColorFormulasTo994(projectMock), times(0)
		);

		projectManagerMock.verify(() ->
				ProjectManager.updateArduinoValuesTo995(projectMock), times(0)
		);

		projectManagerMock.verify(() ->
				ProjectManager.updateCollisionScriptsTo996(projectMock), times(0)
		);

		projectManagerMock.verify(() ->
				ProjectManager.makeShallowCopiesDeepAgain(projectMock), times(0)
		);

		projectManagerMock.verify(() ->
				ProjectManager.updateScriptsToTreeStructure(projectMock), times(0)
		);

		projectManagerMock.verify(() ->
				ProjectManager.removePermissionsFile(projectMock), times(0)
		);

		projectManagerMock.verify(() ->
				ProjectManager.updateDirectionProperty(projectMock), times(0)
		);

		projectManagerMock.verify(() ->
				ProjectManager.flattenAllLists(projectMock), times(
				BuildConfig.FEATURE_LIST_AS_BASIC_DATATYPE ? 0 : 1
		));
	}
}
