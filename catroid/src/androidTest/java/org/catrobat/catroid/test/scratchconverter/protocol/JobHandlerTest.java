/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

import android.test.AndroidTestCase;

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
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

public class JobHandlerTest extends AndroidTestCase {

	private static final long JOB_ID_OF_JOB_HANDLER = 1;
	private static final long WRONG_JOB_ID = 2;
	private Client.ConvertCallback convertCallbackMock;
	private Job expectedJob;
	private JobHandler jobHandler;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		expectedJob = new Job(JOB_ID_OF_JOB_HANDLER, "My program", null);
		convertCallbackMock = Mockito.mock(Client.ConvertCallback.class);
		jobHandler = new JobHandler(expectedJob, convertCallbackMock);
	}

	//------------------------------------------------------------------------------------------------------------------
	// Receive job-message event tests
	//------------------------------------------------------------------------------------------------------------------
	public void testReceivedJobMessageWithWrongJobIDShouldFail() {
		expectedJob.setState(Job.State.SCHEDULED);
		try {
			jobHandler.onJobMessage(new JobReadyMessage(WRONG_JOB_ID));
			fail("onJobMessage() should throw exception, but returned unexpectedly");
		} catch (IllegalArgumentException ex) {
			assertEquals("State of expectedJob changed unexpectedly", Job.State.SCHEDULED, expectedJob.getState());
			verifyZeroInteractions(convertCallbackMock);
		} catch (Exception ex) {
			fail("Unexpected exception thrown!");
		}
	}

	public void testReceivedJobMessageWhenJobIsNotInProgressShouldFail() {
		// tests all not-in-progress states! -> see: Job.State.isInProgress()
		for (Job.State givenState : new Job.State[] { Job.State.UNSCHEDULED, Job.State.FINISHED, Job.State.FAILED }) {
			expectedJob.setState(givenState);
			try {
				jobHandler.onJobMessage(new JobReadyMessage(WRONG_JOB_ID));
				fail("onJobMessage() should throw exception, but returned unexpectedly");
			} catch (IllegalArgumentException ex) {
				assertEquals("State of expectedJob changed unexpectedly", givenState, expectedJob.getState());
				verifyZeroInteractions(convertCallbackMock);
			} catch (Exception ex) {
				fail("Unexpected exception thrown!");
			}
		}
	}

	public void testReceivedJobReadyMessageWhenHandlerIsInScheduledStateShouldWork() {
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull("First argument of onConversionReady() call must not be null",
						invocation.getArguments()[0]);
				final Job job = (Job) invocation.getArguments()[0];
				assertEquals("Forwarded parameter job does not equal expected job!", expectedJob, job);
				return null;
			}
		}).when(convertCallbackMock).onConversionReady(any(Job.class));

		expectedJob.setState(Job.State.SCHEDULED);
		jobHandler.onJobMessage(new JobReadyMessage(JOB_ID_OF_JOB_HANDLER));

		assertEquals("Expecting state to be READY after processed JobReadyMessage", Job.State.READY,
				expectedJob.getState());
		verify(convertCallbackMock, times(1)).onConversionReady(any(Job.class));
		verifyNoMoreInteractions(convertCallbackMock);
	}

	public void testReceivedJobReadyMessageWhenHandlerIsNotInScheduledStateButInProgressShouldBeIgnored() {
		for (Job.State givenState : new Job.State[] { Job.State.READY, Job.State.RUNNING }) {
			expectedJob.setState(givenState);
			jobHandler.onJobMessage(new JobReadyMessage(JOB_ID_OF_JOB_HANDLER));
			assertEquals("State of expectedJob changed unexpectedly", givenState, expectedJob.getState());
		}

		verifyZeroInteractions(convertCallbackMock);
	}

	public void testReceivedJobAlreadyRunningMessageWhenHandlerIsInScheduledStateShouldWork() {
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull("First argument of onConversionStart() call must not be null",
						invocation.getArguments()[0]);
				final Job job = (Job) invocation.getArguments()[0];
				assertEquals("Forwarded parameter job does not equal expected job!", expectedJob, job);
				return null;
			}
		}).when(convertCallbackMock).onConversionStart(any(Job.class));

		expectedJob.setState(Job.State.SCHEDULED);
		final String expectedProgramImageURL = "https://cdn2.scratch.mit.edu/get_image/project/11656680_480x360.png";
		jobHandler.onJobMessage(new JobAlreadyRunningMessage(JOB_ID_OF_JOB_HANDLER, expectedJob.getTitle(),
				expectedProgramImageURL));

		assertEquals("Expecting state to be RUNNING after processed JobAlreadyRunningMessage",
				Job.State.RUNNING, expectedJob.getState());
		assertEquals("Image of expectedJob not set properly!",
				expectedProgramImageURL, expectedJob.getImage().getUrl().toString());
		verify(convertCallbackMock, times(1)).onConversionStart(any(Job.class));
		verifyNoMoreInteractions(convertCallbackMock);
	}

	public void testReceivedJobAlreadyRunningMessageWhenHandlerIsNotInScheduledStateButInProgressShouldBeIgnored() {
		for (Job.State givenState : new Job.State[] { Job.State.READY, Job.State.RUNNING }) {
			expectedJob.setState(givenState);
			final String expectedProgramImageURL = "https://cdn2.scratch.mit.edu/get_image/project/11656680_480x360.png";
			jobHandler.onJobMessage(new JobAlreadyRunningMessage(JOB_ID_OF_JOB_HANDLER, expectedJob.getTitle(),
					expectedProgramImageURL));

			assertEquals("State of expectedJob changed unexpectedly", givenState, expectedJob.getState());
			assertNull("Image of expectedJob not set properly!", expectedJob.getImage());
		}

		verifyZeroInteractions(convertCallbackMock);
	}

	public void testReceivedJobProgressMessageWhenHandlerIsInRunningStateShouldWork() {
		final short expectedProgress = 31;

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull("First argument of onJobProgress() call must not be null",
						invocation.getArguments()[0]);
				assertNotNull("Second argument of onJobProgress() call must not be null",
						invocation.getArguments()[1]);
				final Job job = (Job) invocation.getArguments()[0];
				final short progress = (short) invocation.getArguments()[1];
				assertEquals("Forwarded parameter job does not equal expected job!", expectedJob, job);
				assertEquals("Forwarded parameter progress does not equal expected progress value!",
						expectedProgress, progress);
				return null;
			}
		}).when(convertCallbackMock).onJobProgress(any(Job.class), any(Short.class));

		expectedJob.setState(Job.State.RUNNING);
		jobHandler.onJobMessage(new JobProgressMessage(JOB_ID_OF_JOB_HANDLER, expectedProgress));

		assertEquals("Expecting state to be RUNNING after processed JobProgressMessage",
				Job.State.RUNNING, expectedJob.getState());
		assertEquals("Progress value of expectedJob not set properly!", expectedProgress, expectedJob.getProgress());
		verify(convertCallbackMock, times(1)).onJobProgress(any(Job.class), any(Short.class));
		verifyNoMoreInteractions(convertCallbackMock);
	}

	public void testReceivedJobProgressMessageWhenHandlerIsNotInRunningStateButInProgressShouldBeIgnored() {
		for (Job.State givenState : new Job.State[] { Job.State.SCHEDULED, Job.State.READY }) {
			final short expectedProgress = 31;
			expectedJob.setState(givenState);
			jobHandler.onJobMessage(new JobProgressMessage(JOB_ID_OF_JOB_HANDLER, expectedProgress));

			assertEquals("State of expectedJob changed unexpectedly", givenState, expectedJob.getState());
		}

		verifyZeroInteractions(convertCallbackMock);
	}

	public void testReceivedJobOutputMessageWhenHandlerIsInRunningStateShouldWork() {
		final String[] expectedLines = new String[] { "line1", "line2" };

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull("First argument of onJobOutput() call must not be null",
						invocation.getArguments()[0]);
				assertNotNull("Second argument of onJobOutput() call must not be null",
						invocation.getArguments()[1]);
				final Job job = (Job) invocation.getArguments()[0];
				final String[] lines = (String[]) invocation.getArguments()[1];
				assertEquals("Forwarded parameter job does not equal expected job!", expectedJob, job);
				assertEquals("Forwarded parameter lines has wrong length!", expectedLines.length, lines.length);
				for (int index = 0; index < expectedLines.length; index++) {
					assertEquals("Lines #" + index + " does not equal expected line #" + index + "!",
							expectedLines[index], lines[index]);
				}
				return null;
			}
		}).when(convertCallbackMock).onJobOutput(any(Job.class), any(String[].class));

		expectedJob.setState(Job.State.RUNNING);
		jobHandler.onJobMessage(new JobOutputMessage(JOB_ID_OF_JOB_HANDLER, expectedLines));

		assertEquals("Expecting state to be RUNNING after processed JobOutputMessage",
				Job.State.RUNNING, expectedJob.getState());
		verify(convertCallbackMock, times(1)).onJobOutput(any(Job.class), any(String[].class));
		verifyNoMoreInteractions(convertCallbackMock);
	}

	public void testReceivedJobOutputMessageWhenHandlerIsNotInRunningStateButInProgressShouldBeIgnored() {
		for (Job.State givenState : new Job.State[] { Job.State.SCHEDULED, Job.State.READY }) {
			final String[] expectedLines = new String[] { "line1", "line2" };

			expectedJob.setState(givenState);
			jobHandler.onJobMessage(new JobOutputMessage(JOB_ID_OF_JOB_HANDLER, expectedLines));

			assertEquals("State of expectedJob changed unexpectedly", givenState, expectedJob.getState());
		}

		verifyZeroInteractions(convertCallbackMock);
	}

	public void testReceivedJobFinishedMessageWhenHandlerIsInScheduledOrRunningStateShouldWork() {
		final String expectedDownloadURL = Constants.SCRATCH_CONVERTER_BASE_URL
				+ "/download?job_id=1&client_id=1&fname=My%20program";
		final Date expectedCacheDate = new Date();

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull("First argument of onConversionFinished() call must not be null",
						invocation.getArguments()[0]);
				assertNotNull("Second argument of onConversionFinished() call must not be null",
						invocation.getArguments()[1]);
				assertNotNull("Third argument of onConversionFinished() call must not be null",
						invocation.getArguments()[2]);
				assertNotNull("Fourth argument of onConversionFinished() call must not be null",
						invocation.getArguments()[3]);

				final Job job = (Job) invocation.getArguments()[0];
				final Client.DownloadFinishedCallback downloadFinishedCallback =
						(Client.DownloadFinishedCallback) invocation.getArguments()[1];
				final String downloadURL = (String) invocation.getArguments()[2];
				final Date cacheDate = (Date) invocation.getArguments()[3];

				assertEquals("Forwarded parameter job does not equal expected job!", expectedJob, job);
				assertEquals("Forwarded parameter downloadFinishedCallback should be the job-handler!",
						jobHandler, downloadFinishedCallback);
				assertEquals("Forwarded parameter downloadURL does not equal expectedDownloadURL!",
						expectedDownloadURL, downloadURL);
				assertEquals("Forwarded parameter cacheDate does not equal exectedCacheDate!",
						expectedCacheDate, cacheDate);
				return null;
			}
		}).when(convertCallbackMock).onConversionFinished(any(Job.class), any(Client.DownloadFinishedCallback.class),
				any(String.class), any(Date.class));

		for (Job.State givenState : new Job.State[] { Job.State.SCHEDULED, Job.State.RUNNING }) {
			expectedJob.setState(givenState);
			jobHandler.onJobMessage(new JobFinishedMessage(JOB_ID_OF_JOB_HANDLER, expectedDownloadURL,
					expectedCacheDate));
			assertEquals("Expecting state to be FINISHED after processed JobFinishedMessage",
					Job.State.FINISHED, expectedJob.getState());
			assertEquals("Download URL of expectedJob not set properly!", expectedDownloadURL,
					expectedJob.getDownloadURL());
		}

		verify(convertCallbackMock, times(2)).onConversionFinished(any(Job.class),
				any(Client.DownloadFinishedCallback.class), any(String.class), any(Date.class));
		verifyNoMoreInteractions(convertCallbackMock);
	}

	public void testReceivedJobFinishedMessageWhenHandlerIsNotInScheduledOrRunningStateButInProgressShouldBeIgnored() {
		final Job.State givenState = Job.State.READY;
		expectedJob.setState(givenState);
		final String expectedDownloadURL = Constants.SCRATCH_CONVERTER_BASE_URL
				+ "/download?job_id=1&client_id=1&fname=My%20program";
		final Date expectedCacheDate = new Date();
		jobHandler.onJobMessage(new JobFinishedMessage(JOB_ID_OF_JOB_HANDLER, expectedDownloadURL,
				expectedCacheDate));

		assertEquals("State of expectedJob changed unexpectedly", givenState, expectedJob.getState());
		assertNull("Image of expectedJob not set properly!", expectedJob.getImage());
		verifyZeroInteractions(convertCallbackMock);
	}

	public void testReceivedJobFailedMessageWhenHandlerIsInScheduledStateShouldWork() {
		final String expectedErrorMessage = "Error message successfully received";

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull("First argument of onConversionFailure() call must not be null",
						invocation.getArguments()[0]);
				assertNotNull("Second argument of onConversionFailure() call must not be null",
						invocation.getArguments()[1]);

				final Job job = (Job) invocation.getArguments()[0];
				final ClientException clientException = (ClientException) invocation.getArguments()[1];

				assertEquals("Forwarded parameter job does not equal expected job!", expectedJob, job);
				assertEquals("Forwarded parameter errorMessage does not equal expectedErrorMessage!",
						"Job failed - Reason: " + expectedErrorMessage, clientException.getMessage());
				return null;
			}
		}).when(convertCallbackMock).onConversionFailure(any(Job.class), any(ClientException.class));

		for (Job.State givenState : new Job.State[] { Job.State.SCHEDULED, Job.State.RUNNING }) {
			expectedJob.setState(givenState);
			jobHandler.onJobMessage(new JobFailedMessage(JOB_ID_OF_JOB_HANDLER, expectedErrorMessage));
			assertEquals("Expecting state to be FAILED after processed JobFailedMessage",
					Job.State.FAILED, expectedJob.getState());
		}

		verify(convertCallbackMock, times(2)).onConversionFailure(any(Job.class), any(ClientException.class));
		verifyNoMoreInteractions(convertCallbackMock);
	}

	public void testReceivedJobFailedMessageWhenHandlerIsNotInScheduledOrRunningStateButInProgressShouldBeIgnored() {
		final Job.State givenState = Job.State.READY;
		final String errorMessage = "Error message successfully received";

		expectedJob.setState(givenState);
		jobHandler.onJobMessage(new JobFailedMessage(JOB_ID_OF_JOB_HANDLER, errorMessage));

		assertEquals("State of expectedJob changed unexpectedly", givenState, expectedJob.getState());
		verifyZeroInteractions(convertCallbackMock);
	}

	public void testReceivedJobRunningMessageWhenHandlerIsInReadyStateShouldWork() {
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull("First argument of onConversionStart() call must not be null",
						invocation.getArguments()[0]);
				final Job job = (Job) invocation.getArguments()[0];
				assertEquals("Forwarded parameter job does not equal expected job!", expectedJob, job);
				return null;
			}
		}).when(convertCallbackMock).onConversionStart(any(Job.class));

		expectedJob.setState(Job.State.READY);
		final String programImageURL = "https://cdn2.scratch.mit.edu/get_image/project/11656680_480x360.png";
		jobHandler.onJobMessage(new JobRunningMessage(JOB_ID_OF_JOB_HANDLER, expectedJob.getTitle(), programImageURL));

		assertEquals("Expecting state to be RUNNING after processed JobRunningMessage",
				Job.State.RUNNING, expectedJob.getState());
		verify(convertCallbackMock, times(1)).onConversionStart(any(Job.class));
		verifyNoMoreInteractions(convertCallbackMock);
	}

	public void testReceivedJobRunningMessageWhenHandlerIsNotInReadyStateButInProgressShouldBeIgnored() {
		for (Job.State givenState : new Job.State[] { Job.State.SCHEDULED, Job.State.RUNNING }) {
			expectedJob.setState(givenState);
			final String programImageURL = "https://cdn2.scratch.mit.edu/get_image/project/11656680_480x360.png";
			jobHandler.onJobMessage(new JobRunningMessage(JOB_ID_OF_JOB_HANDLER, expectedJob.getTitle(),
					programImageURL));

			assertEquals("State of expectedJob changed unexpectedly", givenState, expectedJob.getState());
			assertNull("Image of expectedJob not set properly!", expectedJob.getImage());
			verifyZeroInteractions(convertCallbackMock);
		}
	}

	//------------------------------------------------------------------------------------------------------------------
	// Other event tests
	//------------------------------------------------------------------------------------------------------------------
	public void testReceivedOnJobScheduledEvent() {
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull("First argument of onConversionStart() call must not be null",
						invocation.getArguments()[0]);
				final Job job = (Job) invocation.getArguments()[0];
				assertEquals("Forwarded parameter job does not equal expected job!", expectedJob, job);
				return null;
			}
		}).when(convertCallbackMock).onJobScheduled(any(Job.class));

		expectedJob.setState(Job.State.UNSCHEDULED);
		jobHandler.onJobScheduled();

		assertEquals("Expecting state to be SCHEDULED after onJobScheduled() called",
				Job.State.SCHEDULED, expectedJob.getState());

		verify(convertCallbackMock, times(1)).onJobScheduled(any(Job.class));
		verifyNoMoreInteractions(convertCallbackMock);
	}

	public void testReceivedOnDownloadStartedEvent() {
		final String expectedDownloadURL = Constants.SCRATCH_CONVERTER_BASE_URL
				+ "/download?job_id=1&client_id=1&fname=My%20program";

		expectedJob.setState(Job.State.RUNNING);
		expectedJob.setDownloadState(Job.DownloadState.NOT_DOWNLOADED);
		jobHandler.onDownloadStarted(expectedDownloadURL);

		assertEquals("Expecting downloadState to be DOWNLOADING after onDownloadStarted() called",
				Job.DownloadState.DOWNLOADING, expectedJob.getDownloadState());
		assertEquals("Expecting state to be FINISHED after onDownloadStarted() called",
				Job.State.FINISHED, expectedJob.getState());
		verifyZeroInteractions(convertCallbackMock);
	}

	public void testReceivedOnDownloadFinishedEvent() {
		final String expectedDownloadURL = Constants.SCRATCH_CONVERTER_BASE_URL
				+ "/download?job_id=1&client_id=1&fname=My%20program";

		expectedJob.setState(Job.State.RUNNING);
		expectedJob.setDownloadState(Job.DownloadState.DOWNLOADING);
		jobHandler.onDownloadFinished(expectedJob.getTitle(), expectedDownloadURL);

		assertEquals("Expecting downloadState to be DOWNLOADED after onDownloadFinished() called",
				Job.DownloadState.DOWNLOADED, expectedJob.getDownloadState());
		assertEquals("Expecting state to be FINISHED after onDownloadFinished() called",
				Job.State.FINISHED, expectedJob.getState());
		verifyZeroInteractions(convertCallbackMock);
	}

	public void testReceivedOnUserCanceledDownloadEvent() {
		final String expectedDownloadURL = Constants.SCRATCH_CONVERTER_BASE_URL
				+ "/download?job_id=1&client_id=1&fname=My%20program";

		expectedJob.setState(Job.State.RUNNING);
		expectedJob.setDownloadState(Job.DownloadState.DOWNLOADING);
		jobHandler.onUserCanceledDownload(expectedDownloadURL);

		assertEquals("Expecting downloadState to be NOT_DOWNLOADED after onUserCanceledDownload() called",
				Job.DownloadState.NOT_DOWNLOADED, expectedJob.getDownloadState());
		assertEquals("Expecting state to be FINISHED after onUserCanceledDownload() called",
				Job.State.FINISHED, expectedJob.getState());
		verifyZeroInteractions(convertCallbackMock);
	}

	public void testReceivedOnUserCanceledConversionEvent() {
		expectedJob.setState(Job.State.RUNNING);
		jobHandler.onUserCanceledConversion();

		assertEquals("Expecting state to be FINISHED after onUserCanceledConversion() called",
				Job.State.FINISHED, expectedJob.getState());
		verifyZeroInteractions(convertCallbackMock);
	}

	//------------------------------------------------------------------------------------------------------------------
	// Wrapper method tests
	//------------------------------------------------------------------------------------------------------------------
	public void testIsJobInProgressWrapperCalled() {
		for (Job.State givenState : Job.State.values()) {
			expectedJob.setState(givenState);
			boolean expectedResult = expectedJob.isInProgress();
			assertTrue("Expecting wrapper to return same value as expectedJob.isInProgress() call",
					expectedResult == jobHandler.isInProgress());
		}
		verifyZeroInteractions(convertCallbackMock);
	}

	public void testIsGetJobIDWrapperCalled() {
		final long expectedJobID = expectedJob.getJobID();
		assertEquals("Expecting wrapper to return same value as expectedJob.getJobID() call",
				expectedJobID, jobHandler.getJobID());
		verifyZeroInteractions(convertCallbackMock);
	}
}
