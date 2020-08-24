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

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.XmlHeader;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.exceptions.CompatibilityProjectException;
import org.catrobat.catroid.exceptions.OutdatedVersionProjectException;
import org.catrobat.catroid.io.ProjectLoadAndUpdate;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.io.asynctask.ProjectLoadStringProvider;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doReturn;

@RunWith(PowerMockRunner.class)
@PrepareForTest({XstreamSerializer.class})
public class LoadProjectsTest {
	private static final float INVALID_LANGUAGE_VERSION = 0.0001f;
	private static final float TOO_BIG_LANGUAGE_VERSION = 999.9999f;
	private static final float OLDEST_LANGUAGE_VERSION = 0.8f;
	private File fileMock;
	private Project projectMock;
	private XmlHeader xmlHeaderSpy;
	private ProjectLoadStringProvider stringProviderMock;
	private ProjectLoadAndUpdate projectLoadAndUpdateSpy;

	@Before
	public void setUp() throws Exception {
		fileMock = Mockito.mock(File.class);
		projectMock = Mockito.mock(Project.class);
		xmlHeaderSpy = Mockito.spy(new XmlHeader());
		when(projectMock.getRequiredResources()).thenReturn(new Brick.ResourcesSet());
		when(projectMock.getXmlHeader()).thenReturn(xmlHeaderSpy);
		StaticSingletonInitializer.initializeStaticSingletonMethods();

		XstreamSerializer xstreamSerializerMock = PowerMockito.mock(XstreamSerializer.class);
		PowerMockito.mockStatic(XstreamSerializer.class);
		when(XstreamSerializer.getInstance()).thenReturn(xstreamSerializerMock);

		doReturn(projectMock).when(xstreamSerializerMock).loadProject(any(), any());

		projectLoadAndUpdateSpy = Mockito.spy(new ProjectLoadAndUpdate());
		stringProviderMock = Mockito.mock(ProjectLoadStringProvider.class);
		initStringProviderMock();
	}

	@Test(expected = CompatibilityProjectException.class)
	public void testInvalidLanguageVersion() throws Exception {
		when(projectMock.getCatrobatLanguageVersion()).thenReturn(INVALID_LANGUAGE_VERSION);
		projectLoadAndUpdateSpy.loadProject(fileMock, stringProviderMock);
		verify(projectLoadAndUpdateSpy, times(1)).restorePreviousProject(any());
	}

	@Test(expected = OutdatedVersionProjectException.class)
	public void testTooBigLanguageVersion() throws Exception {
		when(projectMock.getCatrobatLanguageVersion()).thenReturn(TOO_BIG_LANGUAGE_VERSION);
		projectLoadAndUpdateSpy.loadProject(fileMock, stringProviderMock);
		verify(projectLoadAndUpdateSpy, times(1)).restorePreviousProject(any());
	}

	@Test
	public void testOldestLanguageVersion() throws Exception {
		when(projectMock.getCatrobatLanguageVersion()).thenReturn(OLDEST_LANGUAGE_VERSION);

		projectLoadAndUpdateSpy.loadProject(fileMock, stringProviderMock);

		verify(xmlHeaderSpy, times(1)).setScreenMode(any());
		verify(projectLoadAndUpdateSpy, times(1)).updateCollisionFormulasTo993(projectMock);
		verify(projectLoadAndUpdateSpy, times(1)).updateSetPenColorFormulasTo994(projectMock);
		verify(projectLoadAndUpdateSpy, times(1)).updateArduinoValuesTo995(projectMock);
		verify(projectLoadAndUpdateSpy, times(1)).updateCollisionScriptsTo996(projectMock);
		verify(projectLoadAndUpdateSpy, times(1)).makeShallowCopiesDeepAgain(projectMock);
		verify(projectLoadAndUpdateSpy, times(1)).updateScriptsToTreeStructure(projectMock);
		verify(projectLoadAndUpdateSpy, times(1)).removePermissionsFile(projectMock);
	}

	@Test
	public void testCurrentLanguageVersion() throws Exception {
		when(projectMock.getCatrobatLanguageVersion()).thenReturn(CURRENT_CATROBAT_LANGUAGE_VERSION);

		projectLoadAndUpdateSpy.loadProject(fileMock, stringProviderMock);

		verify(xmlHeaderSpy, times(0)).setScreenMode(any());
		verify(projectLoadAndUpdateSpy, times(0)).updateCollisionFormulasTo993(projectMock);
		verify(projectLoadAndUpdateSpy, times(0)).updateSetPenColorFormulasTo994(projectMock);
		verify(projectLoadAndUpdateSpy, times(0)).updateArduinoValuesTo995(projectMock);
		verify(projectLoadAndUpdateSpy, times(0)).updateCollisionScriptsTo996(projectMock);
		verify(projectLoadAndUpdateSpy, times(0)).makeShallowCopiesDeepAgain(projectMock);
		verify(projectLoadAndUpdateSpy, times(0)).updateScriptsToTreeStructure(projectMock);
		verify(projectLoadAndUpdateSpy, times(0)).removePermissionsFile(projectMock);
	}

	private void initStringProviderMock() {
		when(stringProviderMock.getOutdatedErrorMessage()).thenReturn("Outdated");
		when(stringProviderMock.getLoadErrorMessage()).thenReturn("Load");
		when(stringProviderMock.getDefaultSceneName()).thenReturn("DefaultScene");
		when(stringProviderMock.getBackgroundString()).thenReturn("Background");
		when(stringProviderMock.getCompatibilityErrorMessage()).thenReturn("Compatibility");
	}
}
