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
package org.catrobat.catroid.test.transfers;

import android.content.Context;

import org.catrobat.catroid.R;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.web.ProjectDownloader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ProjectDownloaderTest {

	private static final String LEGACY_URL =
			"https://share.catrobat.org/pocketcode/download/71489.catrobat?fname=Pet%20Simulator";
	private static final String PROJECT_UUID = "63768cf1-5f07-11ea-a2ae-000c292a0f49";
	private static final String API_URL =
			"https://share.catrobat.org/api/projects/" + PROJECT_UUID + "/catrobat";
	private static final String PROJECT_NAME = "Pet Simulator";
	private static final String RESOLVED_NAME = "Resolved Name";

	private ProjectDownloader.ProjectDownloadQueue queueMock;
	private Context contextMock;

	@Before
	public void setUp() {
		queueMock = Mockito.mock(ProjectDownloader.ProjectDownloadQueue.class);
		contextMock = Mockito.mock(Context.class);
	}

	@Test
	public void testProjectNameFromLegacyDownloadUrl() {
		assertEquals(PROJECT_NAME, ProjectDownloader.Companion.getProjectNameFromUrl(LEGACY_URL));
	}

	@Test
	public void testProjectIdFromApiDownloadUrl() {
		assertEquals(PROJECT_UUID, ProjectDownloader.Companion.getProjectNameFromUrl(API_URL));
	}

	@Test
	public void testProjectNameFromUnknownUrlIsNull() {
		assertNull(ProjectDownloader.Companion.getProjectNameFromUrl("https://share.catrobat.org/pocketcode/"));
	}

	@Test
	public void testResolvedProjectNameIsUsedForDownload() {
		ProjectDownloader downloader =
				Mockito.spy(new ProjectDownloader(queueMock, API_URL, null, RESOLVED_NAME));
		doNothing().when(downloader).startService(anyString(), any(Context.class));

		downloader.downloadOverwriteExistingProject(contextMock, RESOLVED_NAME);

		verify(downloader, times(1)).startService(eq(RESOLVED_NAME), eq(contextMock));
		verify(queueMock, times(1)).enqueue(eq(RESOLVED_NAME));
	}

	@Test
	public void testDownloadOverwriteExistingProjectProjectNotInDownloadQueue() {
		ProjectDownloader downloader =
				Mockito.spy(new ProjectDownloader(queueMock, LEGACY_URL, null));
		doNothing().when(downloader).startService(anyString(), any(Context.class));

		downloader.downloadOverwriteExistingProject(contextMock, PROJECT_NAME);

		verify(downloader, times(1)).startService(eq(PROJECT_NAME), eq(contextMock));
		verify(queueMock, times(1)).alreadyInQueue(eq(PROJECT_NAME));
		verify(queueMock, times(1)).enqueue(eq(PROJECT_NAME));
		verifyNoMoreInteractions(queueMock);
	}

	@Test
	public void testDownloadOverwriteExistingProjectProjectInDownloadQueue() {
		ProjectDownloader downloader =
				Mockito.spy(new ProjectDownloader(queueMock, LEGACY_URL, null));
		doNothing().when(downloader).startService(anyString(), any(Context.class));
		when(queueMock.alreadyInQueue(eq(PROJECT_NAME))).thenReturn(true);

		String errorMessage = "test error";
		when(contextMock.getString(eq(R.string.error_project_already_in_queue), anyString()))
				.thenReturn(errorMessage);

		try (MockedStatic<ToastUtil> toastUtil = Mockito.mockStatic(ToastUtil.class)) {
			downloader.downloadOverwriteExistingProject(contextMock, PROJECT_NAME);

			toastUtil.verify(() -> ToastUtil.showError(contextMock, errorMessage), times(1));
		}

		verify(queueMock, times(1)).alreadyInQueue(eq(PROJECT_NAME));
		verify(downloader, never()).startService(anyString(), any(Context.class));
		verifyNoMoreInteractions(queueMock);
	}
}
