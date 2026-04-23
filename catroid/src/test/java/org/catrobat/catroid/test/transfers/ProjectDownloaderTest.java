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

package org.catrobat.catroid.test.transfers;

import android.content.Context;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.recyclerview.dialog.ReplaceExistingProjectDialogFragment;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.web.ProjectDownloader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ProjectDownloader.class, ReplaceExistingProjectDialogFragment.class, ToastUtil.class, URLDecoder.class})
public class ProjectDownloaderTest {

	private static final String URL = "https://share.catrob.at/pocketcode/download/71489.catrobat?fname=Pet%20Simulator";
	private static final String PROJECT_NAME = "Pet Simulator";

	private ProjectDownloader downloaderSpy = null;
	private AppCompatActivity activityMock;
	private ProjectDownloader.ProjectDownloadQueue queueMock = null;

	@Before
	public void setUp() {
		queueMock = Mockito.mock(ProjectDownloader.ProjectDownloadQueue.class);
		downloaderSpy = PowerMockito.spy(new ProjectDownloader(queueMock, URL, null));
		activityMock = Mockito.mock(AppCompatActivity.class);
	}

	@Test
	public void testShowNotificationIfDecodeProjectNameFailed() throws UnsupportedEncodingException {
		PowerMockito.mockStatic(ToastUtil.class);
		PowerMockito.mockStatic(URLDecoder.class);
		PowerMockito.when(URLDecoder.decode(anyString(), anyString())).thenThrow(new UnsupportedEncodingException());
		downloaderSpy.download(activityMock);

		PowerMockito.verifyStatic(ToastUtil.class, times(1));
		ToastUtil.showError(eq(activityMock), eq(R.string.error_could_not_decode_project_name_from_url));
		verifyNoMoreInteractions(queueMock);
		verify(downloaderSpy, never()).startService(anyString(), any(Context.class));
	}
	@Test
	public void testShowDialogIfProjectAlreadyExists() {
		PowerMockito.mockStatic(ReplaceExistingProjectDialogFragment.class);
		ReplaceExistingProjectDialogFragment dialog = Mockito.mock(ReplaceExistingProjectDialogFragment.class);
		when(ReplaceExistingProjectDialogFragment.newInstance(eq(PROJECT_NAME), any(ProjectDownloader.class))).thenReturn(dialog);
		PowerMockito.mockStatic(ProjectDownloader.Companion.getClass());
		PowerMockito.when(ReplaceExistingProjectDialogFragment.projectExistsInDirectory(PROJECT_NAME)).thenReturn(true);
		FragmentManager transaction = Mockito.mock(FragmentManager.class);
		Mockito.when(activityMock.getSupportFragmentManager()).thenReturn(transaction);

		downloaderSpy.download(activityMock);

		verify(dialog, Mockito.times(1)).show(eq(transaction), anyString());
		Mockito.verify(downloaderSpy, Mockito.never())
				.downloadOverwriteExistingProject(any(Context.class), anyString());
		Mockito.verify(downloaderSpy, Mockito.never())
				.startService(anyString(), any(Context.class));
	}

	@Test
	public void testDownloadOverwriteExistingProjectProjectNotInDownloadQueue() {
		PowerMockito.doNothing().when(downloaderSpy).startService(eq(PROJECT_NAME), any(Context.class));

		downloaderSpy.downloadOverwriteExistingProject(activityMock, PROJECT_NAME);

		verify(downloaderSpy, times(1)).startService(eq(PROJECT_NAME), eq(activityMock));
		verify(queueMock, times(1)).enqueue(eq(PROJECT_NAME));
		verify(queueMock, times(1)).alreadyInQueue(eq(PROJECT_NAME));

		verifyNoMoreInteractions(queueMock);
	}

	@Test
	public void testDownloadOverwriteExistingProjectProjectInDownloadQueue() {
		PowerMockito.mockStatic(ToastUtil.class);

		PowerMockito.doNothing().when(downloaderSpy).startService(eq(PROJECT_NAME), any(Context.class));
		when(queueMock.alreadyInQueue(eq(PROJECT_NAME))).thenReturn(true);

		String errorMessage = "test error";
		when(activityMock.getString(eq(R.string.error_project_already_in_queue), anyString())).thenReturn(errorMessage);

		downloaderSpy.downloadOverwriteExistingProject(activityMock, PROJECT_NAME);

		verify(queueMock, times(1)).alreadyInQueue(eq(PROJECT_NAME));

		PowerMockito.verifyStatic(ToastUtil.class, times(1));
		ToastUtil.showError(eq(activityMock), eq(errorMessage));
		verifyNoMoreInteractions(queueMock);
	}
}
