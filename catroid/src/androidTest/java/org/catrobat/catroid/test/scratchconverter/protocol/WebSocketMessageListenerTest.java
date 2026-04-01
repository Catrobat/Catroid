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

import android.net.Uri;

import com.google.android.gms.common.images.WebImage;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.scratchconverter.Client;
import org.catrobat.catroid.scratchconverter.protocol.BaseMessageHandler;
import org.catrobat.catroid.scratchconverter.protocol.Job;
import org.catrobat.catroid.scratchconverter.protocol.JobHandler;
import org.catrobat.catroid.scratchconverter.protocol.JsonKeys;
import org.catrobat.catroid.scratchconverter.protocol.JsonKeys.JsonDataKeys;
import org.catrobat.catroid.scratchconverter.protocol.WebSocketMessageListener;
import org.catrobat.catroid.scratchconverter.protocol.message.Message.CategoryType;
import org.catrobat.catroid.scratchconverter.protocol.message.base.BaseMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.base.ClientIDMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.base.ErrorMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.base.InfoMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.job.JobAlreadyRunningMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.job.JobFailedMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.job.JobFinishedMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.job.JobMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.job.JobOutputMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.job.JobProgressMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.job.JobReadyMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.job.JobRunningMessage;
import org.catrobat.catroid.test.utils.Reflection;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class WebSocketMessageListenerTest {

	private static final long VALID_CLIENT_ID = 1;
	private static final long JOB_ID_OF_JOB_HANDLER = 1;
	private static final long JOB_ID_OF_UNSCHEDULED_JOB_THAT_HAS_NO_JOB_HANDLER = 2;
	private WebSocketMessageListener webSocketMessageListener;
	private BaseMessageHandler baseMessageHandlerMock;
	private JobHandler jobHandlerMock;

	@Before
	public void setUp() throws Exception {
		System.setProperty("dexmaker.dexcache", ApplicationProvider.getApplicationContext().getCacheDir().getPath());
		baseMessageHandlerMock = Mockito.mock(BaseMessageHandler.class);

		webSocketMessageListener = new WebSocketMessageListener();
		webSocketMessageListener.setBaseMessageHandler(baseMessageHandlerMock);

		jobHandlerMock = Mockito.mock(JobHandler.class);

		// add job handler to map
		Map<Long, JobHandler> jobHandlers = (Map<Long, JobHandler>) Reflection.getPrivateField(
				WebSocketMessageListener.class, webSocketMessageListener, "jobHandlers");
		jobHandlers.put(JOB_ID_OF_JOB_HANDLER, jobHandlerMock);
	}

	//------------------------------------------------------------------------------------------------------------------
	// WebSocket.StringCallback interface tests
	//------------------------------------------------------------------------------------------------------------------
	@Test
	public void testReceivingInvalidStringMessageShouldRaiseNoException() {
		webSocketMessageListener.onStringAvailable(null);
		webSocketMessageListener.onStringAvailable("");
	}

	@Test
	public void testReceivingClientIDMessage() {
		final long expectedClientID = VALID_CLIENT_ID;

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				assertThat(invocation.getArguments()[0], is(instanceOf(ClientIDMessage.class)));
				ClientIDMessage clientIDMessage = (ClientIDMessage) invocation.getArguments()[0];
				assertEquals(expectedClientID, clientIDMessage.getClientID());
				return null;
			}
		}).when(baseMessageHandlerMock).onBaseMessage(any(BaseMessage.class));

		final String jsonMessage = createJsonBaseMessage(BaseMessage.Type.CLIENT_ID, new HashMap<String, Object>() {
			{
				put(JsonDataKeys.CLIENT_ID.toString(), expectedClientID);
			}
		});

		webSocketMessageListener.onStringAvailable(jsonMessage);
		verify(baseMessageHandlerMock, times(1)).onBaseMessage(any(BaseMessage.class));
		verifyNoMoreInteractions(baseMessageHandlerMock);
	}

	@Test
	public void testReceivingErrorMessage() {
		final String expectedErrorMessage = "Error message successfully extracted";

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				assertThat(invocation.getArguments()[0], is(instanceOf(ErrorMessage.class)));
				ErrorMessage errorMessage = (ErrorMessage) invocation.getArguments()[0];
				assertEquals(expectedErrorMessage, errorMessage.getMessage());
				return null;
			}
		}).when(baseMessageHandlerMock).onBaseMessage(any(BaseMessage.class));

		final String jsonMessage = createJsonBaseMessage(BaseMessage.Type.ERROR, new HashMap<String, Object>() {
			{
				put(JsonDataKeys.MSG.toString(), expectedErrorMessage);
			}
		});

		webSocketMessageListener.onStringAvailable(jsonMessage);
		verify(baseMessageHandlerMock, times(1)).onBaseMessage(any(BaseMessage.class));
		verifyNoMoreInteractions(baseMessageHandlerMock);
	}

	@Test
	public void testReceivingInfoMessage() {
		final double expectedCatrobatLanguageVersion = Constants.CURRENT_CATROBAT_LANGUAGE_VERSION;
		final String expectedProgramImageURL = "https://cdn2.scratch.mit.edu/get_image/project/11656680_480x360.png";
		final WebImage expectedProgramImage = new WebImage(Uri.parse(expectedProgramImageURL));
		final Job expectedFirstJob = new Job(1, "Program 1", expectedProgramImage);
		expectedFirstJob.setState(Job.State.FINISHED);
		expectedFirstJob.setProgress((short) 10);
		expectedFirstJob.setDownloadURL("http://scratch2.catrob.at/download?job_id=1&client_id=1&fname=Program%201");
		final Job expectedSecondJob = new Job(2, "Program 2", null);
		expectedSecondJob.setState(Job.State.FINISHED);
		expectedSecondJob.setProgress((short) 20);
		expectedSecondJob.setDownloadURL("http://scratch2.catrob.at/download?job_id=2&client_id=1&fname=Program%202");

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				assertThat(invocation.getArguments()[0], is(instanceOf(InfoMessage.class)));
				InfoMessage infoMessage = (InfoMessage) invocation.getArguments()[0];
				assertEquals(expectedCatrobatLanguageVersion, infoMessage.getCatrobatLanguageVersion());
				Job[] jobs = infoMessage.getJobList();
				assertEquals(2, jobs.length);

				// first job
				assertEquals(expectedFirstJob.getState(), jobs[0].getState());
				assertEquals(expectedFirstJob.getJobID(), jobs[0].getJobID());
				assertEquals(expectedFirstJob.getTitle(), jobs[0].getTitle());
				assertEquals(expectedFirstJob.getImage().getUrl(), jobs[0].getImage().getUrl());
				assertEquals(expectedFirstJob.getProgress(), jobs[0].getProgress());
				assertEquals(expectedFirstJob.getDownloadURL(), jobs[0].getDownloadURL());

				// second job
				assertEquals(expectedSecondJob.getState(), jobs[1].getState());
				assertEquals(expectedSecondJob.getJobID(), jobs[1].getJobID());
				assertEquals(expectedSecondJob.getTitle(), jobs[1].getTitle());
				assertNull(jobs[1].getImage());
				assertEquals(expectedSecondJob.getProgress(), jobs[1].getProgress());
				assertEquals(expectedSecondJob.getProgress(), jobs[1].getProgress());
				assertEquals(expectedSecondJob.getDownloadURL(), jobs[1].getDownloadURL());
				return null;
			}
		}).when(baseMessageHandlerMock).onBaseMessage(any(InfoMessage.class));

		final String jsonMessage = createJsonBaseMessage(BaseMessage.Type.INFO, new HashMap<String, Object>() {
			{
				put(JsonDataKeys.CATROBAT_LANGUAGE_VERSION.toString(), expectedCatrobatLanguageVersion);
				put(JsonDataKeys.JOBS_INFO.toString(), new ArrayList<HashMap<String, Object>>() {
					{
						add(jobToJson(expectedFirstJob));
						add(jobToJson(expectedSecondJob));
					}
				});
			}
		});

		webSocketMessageListener.onStringAvailable(jsonMessage);
		verify(baseMessageHandlerMock, times(1)).onBaseMessage(any(BaseMessage.class));
		verifyNoMoreInteractions(baseMessageHandlerMock);
	}

	@Test
	public void testReceivingJobAlreadyRunningMessage() {
		final String expectedJobTitle = "My program";
		final String expectedJobImageURL = "https://cdn2.scratch.mit.edu/get_image/project/11656680_480x360.png";

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				assertThat(invocation.getArguments()[0], is(instanceOf(JobAlreadyRunningMessage.class)));
				JobAlreadyRunningMessage alreadyRunningMessage;
				alreadyRunningMessage = (JobAlreadyRunningMessage) invocation.getArguments()[0];
				assertEquals(JOB_ID_OF_JOB_HANDLER, alreadyRunningMessage.getJobID());
				assertEquals(expectedJobTitle, alreadyRunningMessage.getJobTitle());
				assertEquals(expectedJobImageURL, alreadyRunningMessage.getJobImageURL());
				return null;
			}
		}).when(jobHandlerMock).onJobMessage(any(JobMessage.class));

		final String jsonMessage = createJsonJobMessage(JobMessage.Type.JOB_ALREADY_RUNNING,
				new HashMap<String, Object>() {
					{
						put(JsonDataKeys.JOB_ID.toString(), JOB_ID_OF_JOB_HANDLER);
						put(JsonDataKeys.JOB_TITLE.toString(), expectedJobTitle);
						put(JsonDataKeys.JOB_IMAGE_URL.toString(), expectedJobImageURL);
					}
				});

		webSocketMessageListener.onStringAvailable(jsonMessage);
		verify(jobHandlerMock, times(1)).onJobMessage(any(JobMessage.class));
		verifyNoMoreInteractions(jobHandlerMock);
	}

	@Test
	public void testReceivingJobFailedMessage() {
		final String expectedJobFailedMessage = "Failed to convert the program!";

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				assertThat(invocation.getArguments()[0], is(instanceOf(JobFailedMessage.class)));
				JobFailedMessage failedMessage = (JobFailedMessage) invocation.getArguments()[0];
				assertEquals(JOB_ID_OF_JOB_HANDLER, failedMessage.getJobID());
				assertEquals(expectedJobFailedMessage, failedMessage.getMessage());
				return null;
			}
		}).when(jobHandlerMock).onJobMessage(any(JobMessage.class));

		final String jsonMessage = createJsonJobMessage(JobMessage.Type.JOB_FAILED,
				new HashMap<String, Object>() {
					{
						put(JsonDataKeys.JOB_ID.toString(), JOB_ID_OF_JOB_HANDLER);
						put(JsonDataKeys.MSG.toString(), expectedJobFailedMessage);
					}
				});

		webSocketMessageListener.onStringAvailable(jsonMessage);
		verify(jobHandlerMock, times(1)).onJobMessage(any(JobMessage.class));
		verifyNoMoreInteractions(jobHandlerMock);
	}

	@Test
	public void testReceivingJobFinishedMessage() {
		final String expectedDownloadURL = "http://scratch2.catrob.at/download?job_id=1&client_id=1&fname=My%20program";
		final String expectedCachedUTCDateString = "2016-08-02 17:30:01";

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				assertThat(invocation.getArguments()[0], is(instanceOf(JobFinishedMessage.class)));
				JobFinishedMessage finishedMessage = (JobFinishedMessage) invocation.getArguments()[0];
				assertEquals(JOB_ID_OF_JOB_HANDLER, finishedMessage.getJobID());
				assertEquals(expectedDownloadURL, finishedMessage.getDownloadURL());
				final Date cachedUTCDate = finishedMessage.getCachedDate();
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
				dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
				assertEquals(expectedCachedUTCDateString, dateFormat.format(cachedUTCDate));
				return null;
			}
		}).when(jobHandlerMock).onJobMessage(any(JobMessage.class));

		final String jsonMessage = createJsonJobMessage(JobMessage.Type.JOB_FINISHED,
				new HashMap<String, Object>() {
					{
						put(JsonDataKeys.JOB_ID.toString(), JOB_ID_OF_JOB_HANDLER);
						put(JsonDataKeys.URL.toString(), expectedDownloadURL);
						put(JsonDataKeys.CACHED_UTC_DATE.toString(), expectedCachedUTCDateString);
					}
				});

		webSocketMessageListener.onStringAvailable(jsonMessage);
		verify(jobHandlerMock, times(1)).onJobMessage(any(JobMessage.class));
		verifyNoMoreInteractions(jobHandlerMock);
	}

	@Test
	public void testReceivingJobOutputMessage() {
		final String[] expectedLines = new String[] {"line1", "line2"};

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				assertThat(invocation.getArguments()[0], is(instanceOf(JobOutputMessage.class)));
				JobOutputMessage outputMessage = (JobOutputMessage) invocation.getArguments()[0];
				assertEquals(JOB_ID_OF_JOB_HANDLER, outputMessage.getJobID());
				final String[] lines = outputMessage.getLines();
				assertEquals(2, lines.length);
				assertEquals(expectedLines[0], lines[0]);
				assertEquals(expectedLines[1], lines[1]);
				return null;
			}
		}).when(jobHandlerMock).onJobMessage(any(JobMessage.class));

		final String jsonMessage = createJsonJobMessage(JobMessage.Type.JOB_OUTPUT, new HashMap<String, Object>() {
			{
				put(JsonDataKeys.JOB_ID.toString(), JOB_ID_OF_JOB_HANDLER);
				put(JsonDataKeys.LINES.toString(), expectedLines);
			}
		});

		webSocketMessageListener.onStringAvailable(jsonMessage);
		verify(jobHandlerMock, times(1)).onJobMessage(any(JobMessage.class));
		verifyNoMoreInteractions(jobHandlerMock);
	}

	@Test
	public void testReceivingJobProgressMessage() {
		final short expectedProgress = 21;

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				assertThat(invocation.getArguments()[0], is(instanceOf(JobProgressMessage.class)));
				JobProgressMessage progressMessage = (JobProgressMessage) invocation.getArguments()[0];
				assertEquals(JOB_ID_OF_JOB_HANDLER, progressMessage.getJobID());
				assertEquals(expectedProgress, progressMessage.getProgress());
				return null;
			}
		}).when(jobHandlerMock).onJobMessage(any(JobMessage.class));

		final String jsonMessage = createJsonJobMessage(JobMessage.Type.JOB_PROGRESS, new HashMap<String, Object>() {
			{
				put(JsonDataKeys.JOB_ID.toString(), JOB_ID_OF_JOB_HANDLER);
				put(JsonDataKeys.PROGRESS.toString(), expectedProgress);
			}
		});

		webSocketMessageListener.onStringAvailable(jsonMessage);
		verify(jobHandlerMock, times(1)).onJobMessage(any(JobMessage.class));
		verifyNoMoreInteractions(jobHandlerMock);
	}

	@Test
	public void testReceivingJobReadyMessage() {
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				assertThat(invocation.getArguments()[0], is(instanceOf(JobReadyMessage.class)));
				JobReadyMessage readyMessage = (JobReadyMessage) invocation.getArguments()[0];
				assertEquals(JOB_ID_OF_JOB_HANDLER, readyMessage.getJobID());
				return null;
			}
		}).when(jobHandlerMock).onJobMessage(any(JobMessage.class));

		final String jsonMessage = createJsonJobMessage(JobMessage.Type.JOB_READY, new HashMap<String, Object>() {
			{
				put(JsonDataKeys.JOB_ID.toString(), JOB_ID_OF_JOB_HANDLER);
			}
		});

		webSocketMessageListener.onStringAvailable(jsonMessage);
		verify(jobHandlerMock, times(1)).onJobMessage(any(JobMessage.class));
		verifyNoMoreInteractions(jobHandlerMock);
	}

	@Test
	public void testReceivingJobRunningMessage() {
		final String expectedJobTitle = "My program";
		final String expectedJobImageURL = "https://cdn2.scratch.mit.edu/get_image/project/11656680_480x360.png";

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				assertThat(invocation.getArguments()[0], is(instanceOf(JobRunningMessage.class)));
				JobRunningMessage runningMessage = (JobRunningMessage) invocation.getArguments()[0];
				assertEquals(JOB_ID_OF_JOB_HANDLER, runningMessage.getJobID());
				assertEquals(expectedJobTitle, runningMessage.getJobTitle());
				assertEquals(expectedJobImageURL, runningMessage.getJobImageURL());
				return null;
			}
		}).when(jobHandlerMock).onJobMessage(any(JobMessage.class));

		final String jsonMessage = createJsonJobMessage(JobMessage.Type.JOB_RUNNING, new HashMap<String, Object>() {
			{
				put(JsonDataKeys.JOB_ID.toString(), JOB_ID_OF_JOB_HANDLER);
				put(JsonDataKeys.JOB_TITLE.toString(), expectedJobTitle);
				put(JsonDataKeys.JOB_IMAGE_URL.toString(), expectedJobImageURL);
			}
		});

		webSocketMessageListener.onStringAvailable(jsonMessage);
		verify(jobHandlerMock, times(1)).onJobMessage(any(JobMessage.class));
		verifyNoMoreInteractions(jobHandlerMock);
	}

	//------------------------------------------------------------------------------------------------------------------
	// MessageListener interface tests
	//------------------------------------------------------------------------------------------------------------------

	@Test
	public void testScheduleJobNotInProgress() {
		final String expectedJobTitle = "My program";
		final String expectedJobImageURL = "https://cdn2.scratch.mit.edu/get_image/project/11656680_480x360.png";
		final WebImage expectedJobImage = new WebImage(Uri.parse(expectedJobImageURL));
		final Job expectedJob = new Job(JOB_ID_OF_JOB_HANDLER, expectedJobTitle, expectedJobImage);
		final boolean expectedForceValue = false;

		final Client.ConvertCallback convertCallbackMock = Mockito.mock(Client.ConvertCallback.class);

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				assertEquals(convertCallbackMock, invocation.getArguments()[0]);
				return null;
			}
		}).when(jobHandlerMock).setCallback(any(Client.ConvertCallback.class));
		when(jobHandlerMock.isInProgress()).thenReturn(false);

		assertTrue(webSocketMessageListener.scheduleJob(expectedJob, expectedForceValue, convertCallbackMock));
		verify(jobHandlerMock, times(1)).setCallback(any(Client.ConvertCallback.class));
		verify(jobHandlerMock, times(1)).onJobScheduled();
		verify(jobHandlerMock, times(1)).isInProgress();
		verifyNoMoreInteractions(jobHandlerMock);
	}

	@Test
	public void testReschedulingAlreadyRunningJobWithDisabledForceFlagShouldFail() {
		final String expectedJobTitle = "My program";
		final String expectedJobImageURL = "https://cdn2.scratch.mit.edu/get_image/project/11656680_480x360.png";
		final WebImage expectedJobImage = new WebImage(Uri.parse(expectedJobImageURL));
		final Job expectedJob = new Job(JOB_ID_OF_JOB_HANDLER, expectedJobTitle, expectedJobImage);
		final boolean expectedForceValue = false;

		final Client.ConvertCallback convertCallbackMock = Mockito.mock(Client.ConvertCallback.class);
		when(jobHandlerMock.isInProgress()).thenReturn(true);

		assertFalse(webSocketMessageListener.scheduleJob(expectedJob, expectedForceValue, convertCallbackMock));
		verify(jobHandlerMock, times(1)).isInProgress();
		verifyNoMoreInteractions(jobHandlerMock);
	}

	@Test
	public void testReschedulingAlreadyRunningJobWithEnabledForceFlagShouldWork() {
		final String expectedJobTitle = "My program";
		final String expectedJobImageURL = "https://cdn2.scratch.mit.edu/get_image/project/11656680_480x360.png";
		final WebImage expectedJobImage = new WebImage(Uri.parse(expectedJobImageURL));
		final Job expectedJob = new Job(JOB_ID_OF_JOB_HANDLER, expectedJobTitle, expectedJobImage);
		final boolean expectedForceValue = true;

		final Client.ConvertCallback convertCallbackMock = Mockito.mock(Client.ConvertCallback.class);

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				assertEquals(convertCallbackMock, invocation.getArguments()[0]);
				return null;
			}
		}).when(jobHandlerMock).setCallback(any(Client.ConvertCallback.class));

		assertTrue(webSocketMessageListener.scheduleJob(expectedJob, expectedForceValue, convertCallbackMock));
		verify(jobHandlerMock, times(1)).setCallback(any(Client.ConvertCallback.class));
		verify(jobHandlerMock, times(1)).onJobScheduled();
		verifyNoMoreInteractions(jobHandlerMock);
	}

	@Test
	public void testRestoreJobStatus() {
		final String expectedJobTitle = "My program";
		final String expectedJobImageURL = "https://cdn2.scratch.mit.edu/get_image/project/11656680_480x360.png";
		final WebImage expectedJobImage = new WebImage(Uri.parse(expectedJobImageURL));
		final Job expectedJob = new Job(JOB_ID_OF_JOB_HANDLER, expectedJobTitle, expectedJobImage);

		final Client.ConvertCallback convertCallbackMock = Mockito.mock(Client.ConvertCallback.class);

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				assertEquals(convertCallbackMock, invocation.getArguments()[0]);
				return null;
			}
		}).when(jobHandlerMock).setCallback(any(Client.ConvertCallback.class));

		Client.ProjectDownloadCallback callback = webSocketMessageListener.restoreJobIfRunning(expectedJob,
				convertCallbackMock);
		assertNull(callback);
		verify(jobHandlerMock, times(1)).setCallback(any(Client.ConvertCallback.class));
		verifyNoMoreInteractions(jobHandlerMock);
	}

	@Test
	public void testIsJobInProgressOfRunningJobShouldReturnTrue() {
		when(jobHandlerMock.isInProgress()).thenReturn(true);
		assertTrue(webSocketMessageListener.isJobInProgress(JOB_ID_OF_JOB_HANDLER));
		verify(jobHandlerMock, times(1)).isInProgress();
		verifyNoMoreInteractions(jobHandlerMock);
	}

	@Test
	public void testIsJobInProgressOfUnscheduledJobShouldReturnFalse() {
		assertFalse(webSocketMessageListener.isJobInProgress(JOB_ID_OF_UNSCHEDULED_JOB_THAT_HAS_NO_JOB_HANDLER));
		verifyZeroInteractions(jobHandlerMock);
	}

	@Test
	public void testOnUserCanceledConversionEventOfRunningJobShouldForwardCallToCorrespondingJobHandler() {
		webSocketMessageListener.onUserCanceledConversion(JOB_ID_OF_JOB_HANDLER);
		verify(jobHandlerMock, times(1)).onUserCanceledConversion();
		verifyNoMoreInteractions(jobHandlerMock);
	}

	@Test
	public void testOnUserCanceledConversionEventOfUnscheduledJobShouldNotForwardCallToCorrespondingJobHandler() {
		webSocketMessageListener.onUserCanceledConversion(JOB_ID_OF_UNSCHEDULED_JOB_THAT_HAS_NO_JOB_HANDLER);
		verifyZeroInteractions(jobHandlerMock);
	}

	@Test
	public void testGetNumberOfJobsInProgressWhenThereExistsOnlyOneRunningJobShouldReturnOne() {
		when(jobHandlerMock.isInProgress()).thenReturn(true);
		assertEquals(webSocketMessageListener.getNumberOfJobsInProgress(), 1);
		verify(jobHandlerMock, times(1)).isInProgress();
		verifyNoMoreInteractions(jobHandlerMock);
	}

	@Test
	public void testGetNumberOfJobsInProgressWhenThereExistsOnlyOneButNotRunningJobShouldReturnZero() {
		when(jobHandlerMock.isInProgress()).thenReturn(false);
		assertEquals(webSocketMessageListener.getNumberOfJobsInProgress(), 0);
		verify(jobHandlerMock, times(1)).isInProgress();
		verifyNoMoreInteractions(jobHandlerMock);
	}

	//------------------------------------------------------------------------------------------------------------------
	// helpers for tests
	//------------------------------------------------------------------------------------------------------------------
	private String createJsonBaseMessage(final BaseMessage.Type type, final Map<String, Object> data) {
		final Map<String, Object> payloadMap = new HashMap<>();
		payloadMap.put(JsonKeys.CATEGORY.toString(), CategoryType.BASE.getCategoryID());
		payloadMap.put(JsonKeys.TYPE.toString(), type.getTypeID());
		payloadMap.put(JsonKeys.DATA.toString(), data);

		final JSONObject jsonObject = new JSONObject(payloadMap);
		final String jsonMessage = jsonObject.toString();
		assertNotNull(jsonMessage);
		assertThat(jsonMessage.length(), is(greaterThan(0)));
		return jsonMessage;
	}

	private String createJsonJobMessage(final JobMessage.Type type, final Map<String, Object> data) {
		final Map<String, Object> payloadMap = new HashMap<>();
		payloadMap.put(JsonKeys.CATEGORY.toString(), CategoryType.JOB.getCategoryID());
		payloadMap.put(JsonKeys.TYPE.toString(), type.getTypeID());
		payloadMap.put(JsonKeys.DATA.toString(), data);

		final JSONObject jsonObject = new JSONObject(payloadMap);
		final String jsonMessage = jsonObject.toString();
		assertNotNull(jsonMessage);
		assertThat(jsonMessage.length(), is(greaterThan(0)));
		return jsonMessage;
	}

	private HashMap<String, Object> jobToJson(final Job job) {
		return new HashMap<String, Object>() {
			{
				put(JsonKeys.JsonJobDataKeys.STATE.toString(), job.getState().getStateID());
				put(JsonKeys.JsonJobDataKeys.JOB_ID.toString(), job.getJobID());
				put(JsonKeys.JsonJobDataKeys.TITLE.toString(), job.getTitle());
				if (job.getImage() != null) {
					put(JsonKeys.JsonJobDataKeys.IMAGE_URL.toString(), job.getImage().getUrl().toString());
				} else {
					put(JsonKeys.JsonJobDataKeys.IMAGE_URL.toString(), null);
				}
				put(JsonKeys.JsonJobDataKeys.PROGRESS.toString(), job.getProgress());
				put(JsonKeys.JsonJobDataKeys.DOWNLOAD_URL.toString(), job.getDownloadURL());
			}
		};
	}
}
