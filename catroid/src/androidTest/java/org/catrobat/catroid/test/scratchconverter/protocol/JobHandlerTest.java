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

package org.catrobat.catroid.test.scratchconverter.protocol;

import android.support.test.runner.AndroidJUnit4;

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
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(AndroidJUnit4.class)
public class JobHandlerTest {

	private static final long JOB_ID_OF_JOB_HANDLER = 1;
	private static final long WRONG_JOB_ID = 2;
	private Client.ConvertCallback convertCallbackMock;
	private Job expectedJob;
	private JobHandler jobHandler;

	@Before
	public void setUp() throws Exception {
		expectedJob = new Job(JOB_ID_OF_JOB_HANDLER, "My program", null);
		convertCallbackMock = Mockito.mock(Client.ConvertCallback.class);
		jobHandler = new JobHandler(expectedJob, convertCallbackMock);
	}

	//------------------------------------------------------------------------------------------------------------------
	// Receive job-message event tests
	//------------------------------------------------------------------------------------------------------------------
	@Test
	public void testReceivedJobMessageWithWrongJobIDShouldFail() {
		expectedJob.setState(Job.State.SCHEDULED);
		try {
			jobHandler.onJobMessage(new JobReadyMessage(WRONG_JOB_ID));
			fail("onJobMessage() should throw exception, but returned unexpectedly");
		} catch (IllegalArgumentException ex) {
			assertEquals(Job.State.SCHEDULED, expectedJob.getState());
			verifyZeroInteractions(convertCallbackMock);
		}
	}

	@Test
	public void testReceivedJobMessageWhenJobIsNotInProgressShouldFail() {
		// tests all not-in-progress states! -> see: Job.State.isInProgress()
		for (Job.State givenState : new Job.State[] {Job.State.UNSCHEDULED, Job.State.FINISHED, Job.State.FAILED}) {
			expectedJob.setState(givenState);
			try {
				jobHandler.onJobMessage(new JobReadyMessage(WRONG_JOB_ID));
				fail("onJobMessage() should throw exception, but returned unexpectedly");
			} catch (IllegalArgumentException ex) {
				assertEquals(givenState, expectedJob.getState());
				verifyZeroInteractions(convertCallbackMock);
			}
		}
	}

	@Test
	public void testReceivedJobReadyMessageWhenHandlerIsInScheduledStateShouldWork() {
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				final Job job = (Job) invocation.getArguments()[0];
				assertEquals(expectedJob, job);
				return null;
			}
		}).when(convertCallbackMock).onConversionReady(any(Job.class));

		expectedJob.setState(Job.State.SCHEDULED);
		jobHandler.onJobMessage(new JobReadyMessage(JOB_ID_OF_JOB_HANDLER));

		assertEquals(Job.State.READY, expectedJob.getState());
		verify(convertCallbackMock, times(1)).onConversionReady(any(Job.class));
		verifyNoMoreInteractions(convertCallbackMock);
	}

	@Test
	public void testReceivedJobReadyMessageWhenHandlerIsNotInScheduledStateButInProgressShouldBeIgnored() {
		for (Job.State givenState : new Job.State[] {Job.State.READY, Job.State.RUNNING}) {
			expectedJob.setState(givenState);
			jobHandler.onJobMessage(new JobReadyMessage(JOB_ID_OF_JOB_HANDLER));
			assertEquals(givenState, expectedJob.getState());
		}

		verifyZeroInteractions(convertCallbackMock);
	}

	@Test
	public void testReceivedJobAlreadyRunningMessageWhenHandlerIsInScheduledStateShouldWork() {
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				final Job job = (Job) invocation.getArguments()[0];
				assertEquals(expectedJob, job);
				return null;
			}
		}).when(convertCallbackMock).onConversionStart(any(Job.class));

		expectedJob.setState(Job.State.SCHEDULED);
		final String expectedProgramImageURL = "https://cdn2.scratch.mit.edu/get_image/project/11656680_480x360.png";
		jobHandler.onJobMessage(new JobAlreadyRunningMessage(JOB_ID_OF_JOB_HANDLER, expectedJob.getTitle(),
				expectedProgramImageURL));

		assertEquals(Job.State.RUNNING, expectedJob.getState());
		assertEquals(expectedProgramImageURL, expectedJob.getImage().getUrl().toString());
		verify(convertCallbackMock, times(1)).onConversionStart(any(Job.class));
		verifyNoMoreInteractions(convertCallbackMock);
	}

	@Test
	public void testReceivedJobAlreadyRunningMessageWhenHandlerIsNotInScheduledStateButInProgressShouldBeIgnored() {
		for (Job.State givenState : new Job.State[] {Job.State.READY, Job.State.RUNNING}) {
			expectedJob.setState(givenState);
			final String expectedProgramImageURL = "https://cdn2.scratch.mit.edu/get_image/project/11656680_480x360.png";
			jobHandler.onJobMessage(new JobAlreadyRunningMessage(JOB_ID_OF_JOB_HANDLER, expectedJob.getTitle(),
					expectedProgramImageURL));

			assertEquals(givenState, expectedJob.getState());
			assertNull(expectedJob.getImage());
		}

		verifyZeroInteractions(convertCallbackMock);
	}

	@Test
	public void testReceivedJobProgressMessageWhenHandlerIsInRunningStateShouldWork() {
		final short expectedProgress = 31;

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				assertNotNull(invocation.getArguments()[1]);
				final Job job = (Job) invocation.getArguments()[0];
				final short progress = (short) invocation.getArguments()[1];
				assertEquals(expectedJob, job);
				assertEquals(expectedProgress, progress);
				return null;
			}
		}).when(convertCallbackMock).onJobProgress(any(Job.class), any(Short.class));

		expectedJob.setState(Job.State.RUNNING);
		jobHandler.onJobMessage(new JobProgressMessage(JOB_ID_OF_JOB_HANDLER, expectedProgress));

		assertEquals(Job.State.RUNNING, expectedJob.getState());
		assertEquals(expectedProgress, expectedJob.getProgress());
		verify(convertCallbackMock, times(1)).onJobProgress(any(Job.class), any(Short.class));
		verifyNoMoreInteractions(convertCallbackMock);
	}

	@Test
	public void testReceivedJobProgressMessageWhenHandlerIsNotInRunningStateButInProgressShouldBeIgnored() {
		for (Job.State givenState : new Job.State[] {Job.State.SCHEDULED, Job.State.READY}) {
			final short expectedProgress = 31;
			expectedJob.setState(givenState);
			jobHandler.onJobMessage(new JobProgressMessage(JOB_ID_OF_JOB_HANDLER, expectedProgress));

			assertEquals(givenState, expectedJob.getState());
		}

		verifyZeroInteractions(convertCallbackMock);
	}

	@Test
	public void testReceivedJobOutputMessageWhenHandlerIsInRunningStateShouldWork() {
		final String[] expectedLines = new String[] {"line1", "line2"};

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				assertNotNull(invocation.getArguments()[1]);
				final Job job = (Job) invocation.getArguments()[0];
				final String[] lines = (String[]) invocation.getArguments()[1];
				assertEquals(expectedJob, job);
				assertEquals(expectedLines.length, lines.length);
				for (int index = 0; index < expectedLines.length; index++) {
					assertEquals(expectedLines[index], lines[index]);
				}
				return null;
			}
		}).when(convertCallbackMock).onJobOutput(any(Job.class), any(String[].class));

		expectedJob.setState(Job.State.RUNNING);
		jobHandler.onJobMessage(new JobOutputMessage(JOB_ID_OF_JOB_HANDLER, expectedLines));

		assertEquals(Job.State.RUNNING, expectedJob.getState());
		verify(convertCallbackMock, times(1)).onJobOutput(any(Job.class), any(String[].class));
		verifyNoMoreInteractions(convertCallbackMock);
	}

	@Test
	public void testReceivedJobOutputMessageWhenHandlerIsNotInRunningStateButInProgressShouldBeIgnored() {
		for (Job.State givenState : new Job.State[] {Job.State.SCHEDULED, Job.State.READY}) {
			final String[] expectedLines = new String[] {"line1", "line2"};

			expectedJob.setState(givenState);
			jobHandler.onJobMessage(new JobOutputMessage(JOB_ID_OF_JOB_HANDLER, expectedLines));

			assertEquals(givenState, expectedJob.getState());
		}

		verifyZeroInteractions(convertCallbackMock);
	}

	@Test
	public void testReceivedJobFinishedMessageWhenHandlerIsInScheduledOrRunningStateShouldWork() {
		final String expectedDownloadURL = Constants.SCRATCH_CONVERTER_BASE_URL
				+ "/download?job_id=1&client_id=1&fname=My%20program";
		final Date expectedCacheDate = new Date();

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				assertNotNull(invocation.getArguments()[1]);
				assertNotNull(invocation.getArguments()[2]);
				assertNotNull(invocation.getArguments()[3]);

				final Job job = (Job) invocation.getArguments()[0];
				final Client.DownloadCallback downloadCallback =
						(Client.DownloadCallback) invocation.getArguments()[1];
				final String downloadURL = (String) invocation.getArguments()[2];
				final Date cacheDate = (Date) invocation.getArguments()[3];

				assertEquals(expectedJob, job);
				assertEquals(jobHandler, downloadCallback);
				assertEquals(expectedDownloadURL, downloadURL);
				assertEquals(expectedCacheDate, cacheDate);
				return null;
			}
		}).when(convertCallbackMock).onConversionFinished(any(Job.class), any(Client.DownloadCallback.class),
				any(String.class), any(Date.class));

		for (Job.State givenState : new Job.State[] {Job.State.SCHEDULED, Job.State.RUNNING}) {
			expectedJob.setState(givenState);
			jobHandler.onJobMessage(new JobFinishedMessage(JOB_ID_OF_JOB_HANDLER, expectedDownloadURL,
					expectedCacheDate));
			assertEquals(Job.State.FINISHED, expectedJob.getState());
			assertEquals(expectedDownloadURL, expectedJob.getDownloadURL());
		}

		verify(convertCallbackMock, times(2)).onConversionFinished(any(Job.class),
				any(Client.DownloadCallback.class), any(String.class), any(Date.class));
		verifyNoMoreInteractions(convertCallbackMock);
	}

	@Test
	public void testReceivedJobFinishedMessageWhenHandlerIsNotInScheduledOrRunningStateButInProgressShouldBeIgnored() {
		final Job.State givenState = Job.State.READY;
		expectedJob.setState(givenState);
		final String expectedDownloadURL = Constants.SCRATCH_CONVERTER_BASE_URL
				+ "/download?job_id=1&client_id=1&fname=My%20program";
		final Date expectedCacheDate = new Date();
		jobHandler.onJobMessage(new JobFinishedMessage(JOB_ID_OF_JOB_HANDLER, expectedDownloadURL,
				expectedCacheDate));

		assertEquals(givenState, expectedJob.getState());
		assertNull(expectedJob.getImage());
		verifyZeroInteractions(convertCallbackMock);
	}

	@Test
	public void testReceivedJobFailedMessageWhenHandlerIsInScheduledStateShouldWork() {
		final String expectedErrorMessage = "Error message successfully received";

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				assertNotNull(invocation.getArguments()[1]);

				final Job job = (Job) invocation.getArguments()[0];
				final ClientException clientException = (ClientException) invocation.getArguments()[1];

				assertEquals(expectedJob, job);
				assertEquals("Job failed - Reason: " + expectedErrorMessage, clientException.getMessage());
				return null;
			}
		}).when(convertCallbackMock).onConversionFailure(any(Job.class), any(ClientException.class));

		for (Job.State givenState : new Job.State[] {Job.State.SCHEDULED, Job.State.RUNNING}) {
			expectedJob.setState(givenState);
			jobHandler.onJobMessage(new JobFailedMessage(JOB_ID_OF_JOB_HANDLER, expectedErrorMessage));
			assertEquals(Job.State.FAILED, expectedJob.getState());
		}

		verify(convertCallbackMock, times(2)).onConversionFailure(any(Job.class), any(ClientException.class));
		verifyNoMoreInteractions(convertCallbackMock);
	}

	@Test
	public void testReceivedJobFailedMessageWhenHandlerIsNotInScheduledOrRunningStateButInProgressShouldBeIgnored() {
		final Job.State givenState = Job.State.READY;
		final String errorMessage = "Error message successfully received";

		expectedJob.setState(givenState);
		jobHandler.onJobMessage(new JobFailedMessage(JOB_ID_OF_JOB_HANDLER, errorMessage));

		assertEquals(givenState, expectedJob.getState());
		verifyZeroInteractions(convertCallbackMock);
	}

	@Test
	public void testReceivedJobRunningMessageWhenHandlerIsInReadyStateShouldWork() {
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				final Job job = (Job) invocation.getArguments()[0];
				assertEquals(expectedJob, job);
				return null;
			}
		}).when(convertCallbackMock).onConversionStart(any(Job.class));

		expectedJob.setState(Job.State.READY);
		final String programImageURL = "https://cdn2.scratch.mit.edu/get_image/project/11656680_480x360.png";
		jobHandler.onJobMessage(new JobRunningMessage(JOB_ID_OF_JOB_HANDLER, expectedJob.getTitle(), programImageURL));

		assertEquals(Job.State.RUNNING, expectedJob.getState());
		verify(convertCallbackMock, times(1)).onConversionStart(any(Job.class));
		verifyNoMoreInteractions(convertCallbackMock);
	}

	@Test
	public void testReceivedJobRunningMessageWhenHandlerIsNotInReadyStateButInProgressShouldBeIgnored() {
		for (Job.State givenState : new Job.State[] {Job.State.SCHEDULED, Job.State.RUNNING}) {
			expectedJob.setState(givenState);
			final String programImageURL = "https://cdn2.scratch.mit.edu/get_image/project/11656680_480x360.png";
			jobHandler.onJobMessage(new JobRunningMessage(JOB_ID_OF_JOB_HANDLER, expectedJob.getTitle(),
					programImageURL));

			assertEquals(givenState, expectedJob.getState());
			assertNull(expectedJob.getImage());
			verifyZeroInteractions(convertCallbackMock);
		}
	}

	//------------------------------------------------------------------------------------------------------------------
	// Other event tests
	//------------------------------------------------------------------------------------------------------------------
	@Test
	public void testReceivedOnJobScheduledEvent() {
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				final Job job = (Job) invocation.getArguments()[0];
				assertEquals(expectedJob, job);
				return null;
			}
		}).when(convertCallbackMock).onJobScheduled(any(Job.class));

		expectedJob.setState(Job.State.UNSCHEDULED);
		jobHandler.onJobScheduled();

		assertEquals(Job.State.SCHEDULED, expectedJob.getState());

		verify(convertCallbackMock, times(1)).onJobScheduled(any(Job.class));
		verifyNoMoreInteractions(convertCallbackMock);
	}

	@Test
	public void testReceivedOnDownloadStartedEvent() {
		final String expectedDownloadURL = Constants.SCRATCH_CONVERTER_BASE_URL
				+ "/download?job_id=1&client_id=1&fname=My%20program";

		expectedJob.setState(Job.State.RUNNING);
		expectedJob.setDownloadState(Job.DownloadState.READY);
		jobHandler.onDownloadStarted(expectedDownloadURL);

		assertEquals(Job.DownloadState.DOWNLOADING, expectedJob.getDownloadState());
		assertEquals(Job.State.FINISHED, expectedJob.getState());
		verifyZeroInteractions(convertCallbackMock);
	}

	@Test
	public void testReceivedOnDownloadFinishedEvent() {
		final String expectedDownloadURL = Constants.SCRATCH_CONVERTER_BASE_URL
				+ "/download?job_id=1&client_id=1&fname=My%20program";

		expectedJob.setState(Job.State.RUNNING);
		expectedJob.setDownloadState(Job.DownloadState.DOWNLOADING);
		jobHandler.onDownloadFinished(expectedJob.getTitle(), expectedDownloadURL);

		assertEquals(Job.DownloadState.DOWNLOADED, expectedJob.getDownloadState());
		assertEquals(Job.State.FINISHED, expectedJob.getState());
		verifyZeroInteractions(convertCallbackMock);
	}

	@Test
	public void testReceivedOnUserCanceledDownloadEvent() {
		final String expectedDownloadURL = Constants.SCRATCH_CONVERTER_BASE_URL
				+ "/download?job_id=1&client_id=1&fname=My%20program";

		expectedJob.setState(Job.State.RUNNING);
		expectedJob.setDownloadState(Job.DownloadState.DOWNLOADING);
		jobHandler.onUserCanceledDownload(expectedDownloadURL);

		assertEquals(Job.DownloadState.CANCELED, expectedJob.getDownloadState());
		assertEquals(Job.State.FINISHED, expectedJob.getState());
		verifyZeroInteractions(convertCallbackMock);
	}

	@Test
	public void testReceivedOnUserCanceledConversionEvent() {
		expectedJob.setState(Job.State.RUNNING);
		jobHandler.onUserCanceledConversion();

		assertEquals(Job.State.FINISHED, expectedJob.getState());
		verifyZeroInteractions(convertCallbackMock);
	}

	//------------------------------------------------------------------------------------------------------------------
	// Wrapper method tests
	//------------------------------------------------------------------------------------------------------------------
	@Test
	public void testIsJobInProgressWrapperCalled() {
		for (Job.State givenState : Job.State.values()) {
			expectedJob.setState(givenState);
			assertEquals(expectedJob.isInProgress(), jobHandler.isInProgress());
		}
		verifyZeroInteractions(convertCallbackMock);
	}

	@Test
	public void testIsGetJobIDWrapperCalled() {
		final long expectedJobID = expectedJob.getJobID();
		assertEquals(expectedJobID, jobHandler.getJobID());
		verifyZeroInteractions(convertCallbackMock);
	}
}
