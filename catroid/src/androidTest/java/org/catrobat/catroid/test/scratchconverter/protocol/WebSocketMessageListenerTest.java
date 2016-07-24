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

import android.net.Uri;
import android.test.AndroidTestCase;

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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class WebSocketMessageListenerTest extends AndroidTestCase {

	private static final long VALID_CLIENT_ID = 1;
	private static final long JOB_ID_OF_JOB_HANDLER = 1;
	private static final long JOB_ID_OF_UNSCHEDULED_JOB_THAT_HAS_NO_JOB_HANDLER = 2;
	private WebSocketMessageListener webSocketMessageListener;
	private BaseMessageHandler baseMessageHandlerMock;
	private JobHandler jobHandlerMock;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		System.setProperty("dexmaker.dexcache", getContext().getCacheDir().getPath());
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
	public void testReceivingInvalidStringMessageShouldRaiseNoException() {
		webSocketMessageListener.onStringAvailable(null);
		webSocketMessageListener.onStringAvailable("");
	}

	public void testReceivingClientIDMessage() {
		final long expectedClientID = VALID_CLIENT_ID;

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull("BaseMessage must not be null", invocation.getArguments()[0]);
				assertTrue("First argument must be of instance ClientIDMessage",
						invocation.getArguments()[0] instanceof ClientIDMessage);
				ClientIDMessage clientIDMessage = (ClientIDMessage) invocation.getArguments()[0];
				assertEquals("Wrong client ID extracted", expectedClientID, clientIDMessage.getClientID());
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

	public void testReceivingErrorMessage() {
		final String expectedErrorMessage = "Error message successfully extracted";

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull("BaseMessage must not be null", invocation.getArguments()[0]);
				assertTrue("First argument must be of instance ErrorMessage",
						invocation.getArguments()[0] instanceof ErrorMessage);
				ErrorMessage errorMessage = (ErrorMessage) invocation.getArguments()[0];
				assertEquals("Wrong error message extracted", expectedErrorMessage, errorMessage.getMessage());
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

	public void testReceivingInfoMessage() {
		final float expectedCatrobatLanguageVersion = Constants.CURRENT_CATROBAT_LANGUAGE_VERSION;
		final String expectedProgramImageURL = "https://cdn2.scratch.mit.edu/get_image/project/11656680_480x360.png";
		final WebImage expectedProgramImage = new WebImage(Uri.parse(expectedProgramImageURL));
		final Job expectedFirstJob = new Job(1, "Program 1", expectedProgramImage);
		expectedFirstJob.setState(Job.State.FINISHED);
		expectedFirstJob.setProgress((short) 10);
		expectedFirstJob.setAlreadyDownloaded(true);
		expectedFirstJob.setDownloadURL("http://scratch2.catrob.at/download?job_id=1&client_id=1&fname=Program%201");
		final Job expectedSecondJob = new Job(2, "Program 2", null);
		expectedSecondJob.setState(Job.State.FINISHED);
		expectedSecondJob.setProgress((short) 20);
		expectedFirstJob.setAlreadyDownloaded(false);
		expectedSecondJob.setDownloadURL("http://scratch2.catrob.at/download?job_id=2&client_id=1&fname=Program%202");

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull("BaseMessage must not be null", invocation.getArguments()[0]);
				assertTrue("First argument must be of instance InfoMessage",
						invocation.getArguments()[0] instanceof InfoMessage);
				InfoMessage infoMessage = (InfoMessage) invocation.getArguments()[0];
				assertEquals("Wrong catrobat language version extracted",
						expectedCatrobatLanguageVersion, infoMessage.getCatrobatLanguageVersion());
				Job[] jobs = infoMessage.getJobList();
				assertTrue("Wrong number of jobs extracted", jobs.length == 2);

				// first job
				assertEquals("Wrong state extracted of first job", expectedFirstJob.getState(), jobs[0].getState());
				assertEquals("Wrong ID extracted of first job", expectedFirstJob.getJobID(), jobs[0].getJobID());
				assertEquals("Wrong title extracted of first job", expectedFirstJob.getTitle(), jobs[0].getTitle());
				assertEquals("Wrong image extracted of first job",
						expectedFirstJob.getImage().getUrl(), jobs[0].getImage().getUrl());
				assertEquals("Wrong progress value extracted of first job",
						expectedFirstJob.getProgress(), jobs[0].getProgress());
				assertEquals("Wrong alreadyDownloaded value extracted of first job",
						expectedFirstJob.isAlreadyDownloaded(), jobs[0].isAlreadyDownloaded());
				assertEquals("Wrong downloadURL extracted of first job",
						expectedFirstJob.getDownloadURL(), jobs[0].getDownloadURL());

				// second job
				assertEquals("Wrong state extracted of second job", expectedSecondJob.getState(), jobs[1].getState());
				assertEquals("Wrong ID extracted of second job", expectedSecondJob.getJobID(), jobs[1].getJobID());
				assertEquals("Wrong title extracted of second job", expectedSecondJob.getTitle(), jobs[1].getTitle());
				assertNull("Wrong image extracted of second job", jobs[1].getImage());
				assertEquals("Wrong progress value extracted of second job",
						expectedSecondJob.getProgress(), jobs[1].getProgress());
				assertEquals("Wrong alreadyDownloaded value extracted of second job",
						expectedSecondJob.isAlreadyDownloaded(), jobs[1].isAlreadyDownloaded());
				assertEquals("Wrong downloadURL extracted of second job",
						expectedSecondJob.getDownloadURL(), jobs[1].getDownloadURL());
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

	public void testReceivingJobAlreadyRunningMessage() {
		final String expectedJobTitle = "My program";
		final String expectedJobImageURL = "https://cdn2.scratch.mit.edu/get_image/project/11656680_480x360.png";

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull("JobMessage must not be null", invocation.getArguments()[0]);
				assertTrue("First argument must be of instance JobAlreadyRunningMessage",
						invocation.getArguments()[0] instanceof JobAlreadyRunningMessage);
				JobAlreadyRunningMessage alreadyRunningMessage;
				alreadyRunningMessage = (JobAlreadyRunningMessage) invocation.getArguments()[0];
				assertEquals("Wrong job ID extracted", JOB_ID_OF_JOB_HANDLER, alreadyRunningMessage.getJobID());
				assertEquals("Wrong job title extracted", expectedJobTitle, alreadyRunningMessage.getJobTitle());
				assertEquals("Wrong job image URL extracted",
						expectedJobImageURL, alreadyRunningMessage.getJobImageURL());
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

	public void testReceivingJobFailedMessage() {
		final String expectedJobFailedMessage = "Failed to convert the program!";

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull("JobMessage must not be null", invocation.getArguments()[0]);
				assertTrue("First argument must be of instance JobFailedMessage",
						invocation.getArguments()[0] instanceof JobFailedMessage);
				JobFailedMessage failedMessage = (JobFailedMessage) invocation.getArguments()[0];
				assertEquals("Wrong job ID extracted", JOB_ID_OF_JOB_HANDLER, failedMessage.getJobID());
				assertEquals("Wrong job failed message extracted",
						expectedJobFailedMessage, failedMessage.getMessage());
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

	public void testReceivingJobFinishedMessage() {
		final String expectedDownloadURL = "http://scratch2.catrob.at/download?job_id=1&client_id=1&fname=My%20program";
		final String expectedCachedUTCDateString = "2016-08-02 17:30:01";

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull("JobMessage must not be null", invocation.getArguments()[0]);
				assertTrue("First argument must be of instance JobFinishedMessage",
						invocation.getArguments()[0] instanceof JobFinishedMessage);
				JobFinishedMessage finishedMessage = (JobFinishedMessage) invocation.getArguments()[0];
				assertEquals("Wrong job ID extracted", JOB_ID_OF_JOB_HANDLER, finishedMessage.getJobID());
				assertEquals("Wrong job download URL extracted", expectedDownloadURL, finishedMessage.getDownloadURL());
				final Date cachedUTCDate = finishedMessage.getCachedUTCDate();
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
				dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
				assertEquals("Wrong job cached date extracted",
						expectedCachedUTCDateString, dateFormat.format(cachedUTCDate));
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

	public void testReceivingJobOutputMessage() {
		final String[] expectedLines = new String[] { "line1", "line2" };

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull("JobMessage must not be null", invocation.getArguments()[0]);
				assertTrue("First argument must be of instance JobOutputMessage",
						invocation.getArguments()[0] instanceof JobOutputMessage);
				JobOutputMessage outputMessage = (JobOutputMessage) invocation.getArguments()[0];
				assertEquals("Wrong job ID extracted", JOB_ID_OF_JOB_HANDLER, outputMessage.getJobID());
				final String[] lines = outputMessage.getLines();
				assertEquals("Wrong number of lines extracted", 2, lines.length);
				assertEquals("Wrong first output line extracted", expectedLines[0], lines[0]);
				assertEquals("Wrong second output line extracted", expectedLines[1], lines[1]);
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

	public void testReceivingJobProgressMessage() {
		final short expectedProgress = 21;

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull("JobMessage must not be null", invocation.getArguments()[0]);
				assertTrue("First argument must be of instance JobProgressMessage",
						invocation.getArguments()[0] instanceof JobProgressMessage);
				JobProgressMessage progressMessage = (JobProgressMessage) invocation.getArguments()[0];
				assertEquals("Wrong job ID extracted", JOB_ID_OF_JOB_HANDLER, progressMessage.getJobID());
				assertEquals("Wrong job progress value extracted", expectedProgress, progressMessage.getProgress());
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

	public void testReceivingJobReadyMessage() {
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull("JobMessage must not be null", invocation.getArguments()[0]);
				assertTrue("First argument must be of instance JobReadyMessage",
						invocation.getArguments()[0] instanceof JobReadyMessage);
				JobReadyMessage readyMessage = (JobReadyMessage) invocation.getArguments()[0];
				assertEquals("Wrong job ID extracted", JOB_ID_OF_JOB_HANDLER, readyMessage.getJobID());
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

	public void testReceivingJobRunningMessage() {
		final String expectedJobTitle = "My program";
		final String expectedJobImageURL = "https://cdn2.scratch.mit.edu/get_image/project/11656680_480x360.png";

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull("JobMessage must not be null", invocation.getArguments()[0]);
				assertTrue("First argument must be of instance JobRunningMessage",
						invocation.getArguments()[0] instanceof JobRunningMessage);
				JobRunningMessage runningMessage = (JobRunningMessage) invocation.getArguments()[0];
				assertEquals("Wrong job ID extracted", JOB_ID_OF_JOB_HANDLER, runningMessage.getJobID());
				assertEquals("Wrong job title extracted", expectedJobTitle, runningMessage.getJobTitle());
				assertEquals("Wrong job image URL extracted",
						expectedJobImageURL, runningMessage.getJobImageURL());
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
				assertNotNull("JobMessage must not be null", invocation.getArguments()[0]);
				assertEquals("First argument must be convertCallbackMock",
						convertCallbackMock, invocation.getArguments()[0]);
				return null;
			}
		}).when(jobHandlerMock).setCallback(any(Client.ConvertCallback.class));
		when(jobHandlerMock.isInProgress()).thenReturn(false);

		assertTrue("Job was not scheduled",
				webSocketMessageListener.scheduleJob(expectedJob, expectedForceValue, convertCallbackMock));
		verify(jobHandlerMock, times(1)).setCallback(any(Client.ConvertCallback.class));
		verify(jobHandlerMock, times(1)).onJobScheduled();
		verify(jobHandlerMock, times(1)).isInProgress();
		verifyNoMoreInteractions(jobHandlerMock);
	}

	public void testReschedulingAlreadyRunningJobWithDisabledForceFlagShouldFail() {
		final String expectedJobTitle = "My program";
		final String expectedJobImageURL = "https://cdn2.scratch.mit.edu/get_image/project/11656680_480x360.png";
		final WebImage expectedJobImage = new WebImage(Uri.parse(expectedJobImageURL));
		final Job expectedJob = new Job(JOB_ID_OF_JOB_HANDLER, expectedJobTitle, expectedJobImage);
		final boolean expectedForceValue = false;

		final Client.ConvertCallback convertCallbackMock = Mockito.mock(Client.ConvertCallback.class);
		when(jobHandlerMock.isInProgress()).thenReturn(true);

		assertFalse("Already running job should not be rescheduled when force parameter set to false",
				webSocketMessageListener.scheduleJob(expectedJob, expectedForceValue, convertCallbackMock));
		verify(jobHandlerMock, times(1)).isInProgress();
		verifyNoMoreInteractions(jobHandlerMock);
	}

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
				assertNotNull("JobMessage must not be null", invocation.getArguments()[0]);
				assertEquals("First argument must be convertCallbackMock",
						convertCallbackMock, invocation.getArguments()[0]);
				return null;
			}
		}).when(jobHandlerMock).setCallback(any(Client.ConvertCallback.class));

		assertTrue("Already running Job has not been rescheduled despite of enabled force flag",
				webSocketMessageListener.scheduleJob(expectedJob, expectedForceValue, convertCallbackMock));
		verify(jobHandlerMock, times(1)).setCallback(any(Client.ConvertCallback.class));
		verify(jobHandlerMock, times(1)).onJobScheduled();
		verifyNoMoreInteractions(jobHandlerMock);
	}

	public void testRestoreJobStatus() {
		final String expectedJobTitle = "My program";
		final String expectedJobImageURL = "https://cdn2.scratch.mit.edu/get_image/project/11656680_480x360.png";
		final WebImage expectedJobImage = new WebImage(Uri.parse(expectedJobImageURL));
		final Job expectedJob = new Job(JOB_ID_OF_JOB_HANDLER, expectedJobTitle, expectedJobImage);

		final Client.ConvertCallback convertCallbackMock = Mockito.mock(Client.ConvertCallback.class);

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull("JobMessage must not be null", invocation.getArguments()[0]);
				assertEquals("First argument must be convertCallbackMock",
						convertCallbackMock, invocation.getArguments()[0]);
				return null;
			}
		}).when(jobHandlerMock).setCallback(any(Client.ConvertCallback.class));

		Client.DownloadFinishedCallback callback = webSocketMessageListener.restoreJobIfRunning(expectedJob,
				convertCallbackMock);
		assertNull("Already running Job has not been rescheduled despite of enabled force flag", callback);
		verify(jobHandlerMock, times(1)).setCallback(any(Client.ConvertCallback.class));
		verifyNoMoreInteractions(jobHandlerMock);
	}

	public void testIsJobInProgressOfRunningJobShouldReturnTrue() {
		when(jobHandlerMock.isInProgress()).thenReturn(true);
		assertTrue("isJobInProgress() call must return true",
				webSocketMessageListener.isJobInProgress(JOB_ID_OF_JOB_HANDLER));
		verify(jobHandlerMock, times(1)).isInProgress();
		verifyNoMoreInteractions(jobHandlerMock);
	}

	public void testIsJobInProgressOfUnscheduledJobShouldReturnFalse() {
		assertFalse("isJobInProgress() call must return false",
				webSocketMessageListener.isJobInProgress(JOB_ID_OF_UNSCHEDULED_JOB_THAT_HAS_NO_JOB_HANDLER));
		verifyZeroInteractions(jobHandlerMock);
	}

	public void testOnUserCanceledConversionEventOfRunningJobShouldForwardCallToCorrespondingJobHandler() {
		webSocketMessageListener.onUserCanceledConversion(JOB_ID_OF_JOB_HANDLER);
		verify(jobHandlerMock, times(1)).onUserCanceledConversion();
		verifyNoMoreInteractions(jobHandlerMock);
	}

	public void testOnUserCanceledConversionEventOfUnscheduledJobShouldNotForwardCallToCorrespondingJobHandler() {
		webSocketMessageListener.onUserCanceledConversion(JOB_ID_OF_UNSCHEDULED_JOB_THAT_HAS_NO_JOB_HANDLER);
		verifyZeroInteractions(jobHandlerMock);
	}

	public void testGetNumberOfJobsInProgressWhenThereExistsOnlyOneRunningJobShouldReturnOne() {
		when(jobHandlerMock.isInProgress()).thenReturn(true);
		assertEquals("isJobInProgress() call must return true",
				webSocketMessageListener.getNumberOfJobsInProgress(), 1);
		verify(jobHandlerMock, times(1)).isInProgress();
		verifyNoMoreInteractions(jobHandlerMock);
	}

	public void testGetNumberOfJobsInProgressWhenThereExistsOnlyOneButNotRunningJobShouldReturnZero() {
		when(jobHandlerMock.isInProgress()).thenReturn(false);
		assertEquals("isJobInProgress() call must return true",
				webSocketMessageListener.getNumberOfJobsInProgress(), 0);
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
		assertNotNull("Cannot serialize given data to JSON! Returned null", jsonMessage);
		assertTrue("Cannot serialize given data to JSON! Returned empty JSON string", jsonMessage.length() > 0);
		return jsonMessage;
	}

	private String createJsonJobMessage(final JobMessage.Type type, final Map<String, Object> data) {
		final Map<String, Object> payloadMap = new HashMap<>();
		payloadMap.put(JsonKeys.CATEGORY.toString(), CategoryType.JOB.getCategoryID());
		payloadMap.put(JsonKeys.TYPE.toString(), type.getTypeID());
		payloadMap.put(JsonKeys.DATA.toString(), data);

		final JSONObject jsonObject = new JSONObject(payloadMap);
		final String jsonMessage = jsonObject.toString();
		assertNotNull("Cannot serialize given data to JSON! Returned null", jsonMessage);
		assertTrue("Cannot serialize given data to JSON! Returned empty JSON string", jsonMessage.length() > 0);
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
				put(JsonKeys.JsonJobDataKeys.ALREADY_DOWNLOADED.toString(), job.isAlreadyDownloaded());
				put(JsonKeys.JsonJobDataKeys.DOWNLOAD_URL.toString(), job.getDownloadURL());
			}
		};
	}
}
