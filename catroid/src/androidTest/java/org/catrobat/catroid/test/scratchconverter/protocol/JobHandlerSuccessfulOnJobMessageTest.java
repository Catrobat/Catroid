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

package org.catrobat.catroid.test.scratchconverter.protocol;

import com.google.android.gms.common.images.WebImage;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.scratchconverter.Client;
import org.catrobat.catroid.scratchconverter.ClientException;
import org.catrobat.catroid.scratchconverter.protocol.Job;
import org.catrobat.catroid.scratchconverter.protocol.JobHandler;
import org.catrobat.catroid.scratchconverter.protocol.message.job.JobAlreadyRunningMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.job.JobFailedMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.job.JobFinishedMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.job.JobOutputMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.job.JobProgressMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.job.JobReadyMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.job.JobRunningMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalMatchers;
import org.mockito.stubbing.Answer;

import java.util.Date;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(AndroidJUnit4.class)
public class JobHandlerSuccessfulOnJobMessageTest {

	private static final long JOB_ID_OF_JOB_HANDLER = 1;
	private Client.ConvertCallback convertCallbackMock;
	private Job jobSpy;
	private JobHandler jobHandler;

	@Before
	public void setUp() throws Exception {
		jobSpy = spy(new Job(JOB_ID_OF_JOB_HANDLER, "My program", null));
		convertCallbackMock = mock(Client.ConvertCallback.class);
		jobHandler = new JobHandler(jobSpy, convertCallbackMock);
	}

	@Test
	public void testReceivedJobAlreadyRunningMessageWhenHandlerIsInScheduledState() {
		jobSpy.setState(Job.State.SCHEDULED);
		jobHandler.onJobMessage(new JobAlreadyRunningMessage(JOB_ID_OF_JOB_HANDLER, jobSpy.getTitle(), Constants.SCRATCH_CONVERTER_BASE_URL));

		verify(jobSpy, times(1)).setState(Job.State.RUNNING);
		verify(jobSpy, times(1)).setImage(any(WebImage.class));
		verify(convertCallbackMock, times(1)).onConversionStart(jobSpy);
		verifyNoMoreInteractions(convertCallbackMock);
	}

	@Test
	public void testReceivedJobProgressMessageWhenHandlerIsInRunningState() {
		final short expectedProgress = 31;

		jobSpy.setState(Job.State.RUNNING);
		jobHandler.onJobMessage(new JobProgressMessage(JOB_ID_OF_JOB_HANDLER, expectedProgress));

		verify(jobSpy, times(1)).setState(Job.State.RUNNING);
		verify(jobSpy, times(1)).setProgress(eq(expectedProgress));
		verify(convertCallbackMock, times(1)).onJobProgress(eq(jobSpy), eq(expectedProgress));
		verifyNoMoreInteractions(convertCallbackMock);
	}

	@Test
	public void testReceivedJobReadyMessageWhenHandlerIsInScheduledState() {
		jobSpy.setState(Job.State.SCHEDULED);
		jobHandler.onJobMessage(new JobReadyMessage(JOB_ID_OF_JOB_HANDLER));

		assertEquals(Job.State.READY, jobSpy.getState());
		verify(convertCallbackMock, times(1)).onConversionReady(eq(jobSpy));
		verifyNoMoreInteractions(convertCallbackMock);
	}

	@Test
	public void testReceivedJobOutputMessageWhenHandlerIsInRunningState() {
		final String[] expectedLines = new String[] {"line1", "line2"};

		jobSpy.setState(Job.State.RUNNING);
		jobHandler.onJobMessage(new JobOutputMessage(JOB_ID_OF_JOB_HANDLER, expectedLines));

		assertEquals(Job.State.RUNNING, jobSpy.getState());
		verify(convertCallbackMock, times(1)).onJobOutput(eq(jobSpy), AdditionalMatchers.aryEq(expectedLines));
		verifyNoMoreInteractions(convertCallbackMock);
	}

	@Test
	public void testReceivedJobFinishedMessageWhenHandlerIsInScheduledState() {
		final String expectedDownloadURL = "url";
		final Date expectedCacheDate = new Date();

		jobSpy.setState(Job.State.SCHEDULED);
		jobHandler.onJobMessage(new JobFinishedMessage(JOB_ID_OF_JOB_HANDLER, expectedDownloadURL, expectedCacheDate));
		verify(jobSpy, times(1)).setState(Job.State.FINISHED);
		verify(jobSpy, times(1)).setDownloadURL(eq(expectedDownloadURL));

		verify(convertCallbackMock, times(1)).onConversionFinished(eq(jobSpy),
				eq(jobHandler), eq(expectedDownloadURL), eq(expectedCacheDate));
		verifyNoMoreInteractions(convertCallbackMock);
	}

	@Test
	public void testReceivedJobFinishedMessageWhenHandlerIsInRunningState() {
		final String expectedDownloadURL = "url";
		final Date expectedCacheDate = new Date();

		jobSpy.setState(Job.State.RUNNING);
		jobHandler.onJobMessage(new JobFinishedMessage(JOB_ID_OF_JOB_HANDLER, expectedDownloadURL, expectedCacheDate));
		verify(jobSpy, times(1)).setState(Job.State.FINISHED);
		verify(jobSpy, times(1)).setDownloadURL(eq(expectedDownloadURL));

		verify(convertCallbackMock, times(1)).onConversionFinished(eq(jobSpy),
				eq(jobHandler), eq(expectedDownloadURL), eq(expectedCacheDate));
		verifyNoMoreInteractions(convertCallbackMock);
	}

	@Test
	public void testReceivedJobFailedMessageWhenHandlerIsInScheduledState() {
		final String expectedErrorMessage = "Error message successfully received";

		doAnswer((Answer<Void>) invocation -> {
			final ClientException clientException = (ClientException) invocation.getArguments()[1];
			assertEquals("Job failed - Reason: " + expectedErrorMessage, clientException.getMessage());
			return null;
		}).when(convertCallbackMock).onConversionFailure(any(Job.class), any(ClientException.class));

		jobSpy.setState(Job.State.SCHEDULED);
		jobHandler.onJobMessage(new JobFailedMessage(JOB_ID_OF_JOB_HANDLER, expectedErrorMessage));
		verify(jobSpy, times(1)).setState(Job.State.FAILED);

		verify(convertCallbackMock, times(1)).onConversionFailure(eq(jobSpy), any(ClientException.class));
		verifyNoMoreInteractions(convertCallbackMock);
	}

	@Test
	public void testReceivedJobFailedMessageWhenHandlerIsInRunningState() {
		final String expectedErrorMessage = "Error message successfully received";

		doAnswer((Answer<Void>) invocation -> {
			final ClientException clientException = (ClientException) invocation.getArguments()[1];
			assertEquals("Job failed - Reason: " + expectedErrorMessage, clientException.getMessage());
			return null;
		}).when(convertCallbackMock).onConversionFailure(any(Job.class), any(ClientException.class));

		jobSpy.setState(Job.State.RUNNING);
		jobHandler.onJobMessage(new JobFailedMessage(JOB_ID_OF_JOB_HANDLER, expectedErrorMessage));
		verify(jobSpy, times(1)).setState(Job.State.FAILED);

		verify(convertCallbackMock, times(1)).onConversionFailure(eq(jobSpy), any(ClientException.class));
		verifyNoMoreInteractions(convertCallbackMock);
	}

	@Test
	public void testReceivedJobRunningMessageWhenHandlerIsInReadyState() {
		jobSpy.setState(Job.State.READY);
		final String programImageURL = "url";
		jobHandler.onJobMessage(new JobRunningMessage(JOB_ID_OF_JOB_HANDLER, jobSpy.getTitle(), programImageURL));
		verify(jobSpy, times(1)).setState(Job.State.RUNNING);
		assertEquals(Job.State.RUNNING, jobSpy.getState());
		verify(convertCallbackMock, times(1)).onConversionStart(eq(jobSpy));
		verifyNoMoreInteractions(convertCallbackMock);
	}
}
