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

package org.catrobat.catroid.test.scratchconverter;

import org.catrobat.catroid.scratchconverter.Client;
import org.catrobat.catroid.scratchconverter.protocol.Job;
import org.catrobat.catroid.scratchconverter.protocol.JobHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(JUnit4.class)
public class JobHandlerEventTest {

	private Client.ConvertCallback convertCallbackMock;
	private Job jobMock;
	private JobHandler jobHandler;

	@Before
	public void setUp() throws Exception {
		jobMock = mock(Job.class);
		convertCallbackMock = mock(Client.ConvertCallback.class);
		jobHandler = new JobHandler(jobMock, convertCallbackMock);
	}

	@Test
	public void testReceivedOnDownloadStartedEvent() {
		jobHandler.onDownloadStarted("");

		verify(jobMock, times(1)).setDownloadState(Job.DownloadState.DOWNLOADING);
		verify(jobMock, times(1)).setState(Job.State.FINISHED);

		verify(jobMock, times(1)).getJobID();
		verifyNoMoreInteractions(jobMock);
		verifyZeroInteractions(convertCallbackMock);
	}

	@Test
	public void testReceivedOnDownloadFinishedEvent() {
		jobHandler.onDownloadFinished("", "");

		verify(jobMock, times(1)).setDownloadState(Job.DownloadState.DOWNLOADED);
		verify(jobMock, times(1)).setState(Job.State.FINISHED);

		verify(jobMock, times(1)).getJobID();
		verifyNoMoreInteractions(jobMock);
		verifyZeroInteractions(convertCallbackMock);
	}

	@Test
	public void testReceivedOnUserCanceledDownloadEvent() {
		jobHandler.onUserCanceledDownload("");

		verify(jobMock, times(1)).setDownloadState(Job.DownloadState.CANCELED);
		verify(jobMock, times(1)).setState(Job.State.FINISHED);

		verify(jobMock, times(1)).getJobID();
		verifyNoMoreInteractions(jobMock);
		verifyZeroInteractions(convertCallbackMock);
	}

	@Test
	public void testReceivedOnUserCanceledConversionEvent() {
		jobHandler.onUserCanceledConversion();

		verify(jobMock, times(1)).setState(Job.State.FINISHED);

		verify(jobMock, times(1)).getJobID();
		verifyNoMoreInteractions(jobMock);
		verifyZeroInteractions(convertCallbackMock);
	}

	@Test
	public void testReceivedOnJobScheduledEvent() {
		jobHandler.onJobScheduled();

		verify(jobMock, times(1)).setState(Job.State.SCHEDULED);
		verify(convertCallbackMock, times(1)).onJobScheduled(jobMock);

		verify(jobMock, times(1)).getJobID();
		verifyNoMoreInteractions(jobMock);
		verifyZeroInteractions(convertCallbackMock);
	}
}
