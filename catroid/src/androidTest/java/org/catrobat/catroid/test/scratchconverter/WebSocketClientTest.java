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

import android.net.Uri;

import com.google.android.gms.common.images.WebImage;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpClient.WebSocketConnectCallback;
import com.koushikdutta.async.http.WebSocket;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.scratchconverter.Client;
import org.catrobat.catroid.scratchconverter.Client.ConvertCallback;
import org.catrobat.catroid.scratchconverter.Client.ProjectDownloadCallback;
import org.catrobat.catroid.scratchconverter.ClientException;
import org.catrobat.catroid.scratchconverter.WebSocketClient;
import org.catrobat.catroid.scratchconverter.protocol.Job;
import org.catrobat.catroid.scratchconverter.protocol.MessageListener;
import org.catrobat.catroid.scratchconverter.protocol.WebSocketMessageListener;
import org.catrobat.catroid.scratchconverter.protocol.command.AuthenticateCommand;
import org.catrobat.catroid.scratchconverter.protocol.command.RetrieveInfoCommand;
import org.catrobat.catroid.scratchconverter.protocol.command.ScheduleJobCommand;
import org.catrobat.catroid.scratchconverter.protocol.message.base.ClientIDMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.base.ErrorMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.base.InfoMessage;
import org.catrobat.catroid.test.utils.Reflection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Field;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(AndroidJUnit4.class)
public class WebSocketClientTest {

	private static final int VALID_CLIENT_ID = 1;
	private MessageListener messageListenerMock;
	private AsyncHttpClient asyncHttpClientMock;
	private WebSocketClient webSocketClient;

	@Before
	public void setUp() throws Exception {
		System.setProperty("dexmaker.dexcache", ApplicationProvider.getApplicationContext().getCacheDir().getPath());
		messageListenerMock = Mockito.mock(WebSocketMessageListener.class);
		asyncHttpClientMock = Mockito.mock(AsyncHttpClient.class);
		webSocketClient = new WebSocketClient(VALID_CLIENT_ID, messageListenerMock);
	}

	//------------------------------------------------------------------------------------------------------------------
	// Connect, Disconnect & Authenticate tests
	//------------------------------------------------------------------------------------------------------------------
	@Test
	public void testAsyncConnectAndAuthenticateWhetherWebSocketObjectSettersAreCalledCorrectly() {
		final WebSocket webSocketMock = Mockito.mock(WebSocket.class);
		final Client.ConnectAuthCallback connectAuthCallbackMock = Mockito.mock(Client.ConnectAuthCallback.class);

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				assertEquals(messageListenerMock, invocation.getArguments()[0]);
				return null;
			}
		}).when(webSocketMock).setStringCallback(any(WebSocket.StringCallback.class));

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				assertEquals(webSocketClient, invocation.getArguments()[0]);
				return null;
			}
		}).when(webSocketMock).setClosedCallback(any(CompletedCallback.class));

		doAnswer(new Answer<Future<WebSocket>>() {
			@Override
			public Future<WebSocket> answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				assertNull(invocation.getArguments()[1]);
				assertNotNull(invocation.getArguments()[2]);
				assertEquals(Constants.SCRATCH_CONVERTER_WEB_SOCKET, invocation.getArguments()[0]);

				// call connectCallback.onCompleted(...)
				((WebSocketConnectCallback) invocation.getArguments()[2]).onCompleted(null, webSocketMock);
				verify(webSocketMock, times(1)).setStringCallback(any(WebSocket.StringCallback.class));
				verify(webSocketMock, times(1)).setClosedCallback(any(CompletedCallback.class));
				verify(webSocketMock, times(1)).send(anyString());
				verifyNoMoreInteractions(connectAuthCallbackMock);
				verifyNoMoreInteractions(webSocketMock);
				return null;
			}
		}).when(asyncHttpClientMock).websocket(anyString(), nullable(String.class), any(WebSocketConnectCallback
				.class));

		// run the test
		webSocketClient.setAsyncHttpClient(asyncHttpClientMock);
		webSocketClient.connectAndAuthenticate(connectAuthCallbackMock);
		verify(asyncHttpClientMock, times(1)).websocket(anyString(), nullable(String.class), any(WebSocketConnectCallback.class));
		verifyNoMoreInteractions(asyncHttpClientMock);
	}

	@Test
	public void testAsyncConnectAndAuthenticateSuccessfullyConnectedAndAuthenticatedWithValidClientID()
			throws Exception {
		final long expectedClientID = VALID_CLIENT_ID;

		Reflection.setPrivateField(WebSocketClient.class, webSocketClient, "clientID", expectedClientID);

		final AuthenticateCommand expectedAuthenticateCommand = new AuthenticateCommand(expectedClientID);

		final Client.ConnectAuthCallback connectAuthCallbackMock = Mockito.mock(Client.ConnectAuthCallback.class);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				final long newClientID = (long) invocation.getArguments()[0];
				assertFalse(webSocketClient.isClosed());
				assertTrue(webSocketClient.isConnected());
				assertTrue(webSocketClient.isAuthenticated());
				assertEquals(expectedClientID, newClientID);
				return null;
			}
		}).when(connectAuthCallbackMock).onSuccess(anyLong());

		final WebSocket webSocketMock = Mockito.mock(WebSocket.class);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				final String jsonCommand = (String) invocation.getArguments()[0];
				assertEquals(expectedAuthenticateCommand.toJson().toString(), jsonCommand);

				// simulate authentication response message from server:
				assertTrue(webSocketClient.isConnected());
				webSocketClient.onBaseMessage(new ClientIDMessage(expectedClientID));
				verify(connectAuthCallbackMock, times(1)).onSuccess(any(Long.class));
				verifyNoMoreInteractions(connectAuthCallbackMock);
				return null;
			}
		}).when(webSocketMock).send(any(String.class));

		doAnswer(new Answer<Future<WebSocket>>() {
			@Override
			public Future<WebSocket> answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				assertNull(invocation.getArguments()[1]);
				assertNotNull(invocation.getArguments()[2]);
				assertEquals(Constants.SCRATCH_CONVERTER_WEB_SOCKET, invocation.getArguments()[0]);

				// call connectCallback.onCompleted(...)
				WebSocketConnectCallback connectCallback = (WebSocketConnectCallback) invocation.getArguments()[2];
				connectCallback.onCompleted(null, webSocketMock);
				verify(webSocketMock, times(1)).setStringCallback(any(WebSocket.StringCallback.class));
				verify(webSocketMock, times(1)).setClosedCallback(any(CompletedCallback.class));
				verify(webSocketMock, times(1)).send(anyString());
				verifyNoMoreInteractions(connectAuthCallbackMock);
				verifyNoMoreInteractions(webSocketMock);
				return null;
			}
		}).when(asyncHttpClientMock).websocket(anyString(), anyString(), any(WebSocketConnectCallback.class));

		// run the test
		webSocketClient.setAsyncHttpClient(asyncHttpClientMock);
		webSocketClient.connectAndAuthenticate(connectAuthCallbackMock);
	}

	@Test
	public void testAsyncConnectAndAuthenticateSuccessfullyConnectedAndAuthenticatedWithInvalidClientID()
			throws Exception {
		final long unexpectedInvalidClientID = Client.INVALID_CLIENT_ID;

		Reflection.setPrivateField(WebSocketClient.class, webSocketClient, "clientID", unexpectedInvalidClientID);

		final AuthenticateCommand expectedAuthenticateCommand = new AuthenticateCommand(unexpectedInvalidClientID);

		final Client.ConnectAuthCallback connectAuthCallbackMock = Mockito.mock(Client.ConnectAuthCallback.class);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				final long newClientID = (long) invocation.getArguments()[0];
				assertFalse(webSocketClient.isClosed());
				assertTrue(webSocketClient.isConnected());
				assertTrue(webSocketClient.isAuthenticated());
				assertNotEquals(unexpectedInvalidClientID, newClientID);
				return null;
			}
		}).when(connectAuthCallbackMock).onSuccess(anyLong());

		final WebSocket webSocketMock = Mockito.mock(WebSocket.class);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);

				final String jsonCommand = (String) invocation.getArguments()[0];
				assertEquals(expectedAuthenticateCommand.toJson().toString(), jsonCommand);

				// simulate authentication response message from server:
				assertTrue(webSocketClient.isConnected());
				webSocketClient.onBaseMessage(new ClientIDMessage(VALID_CLIENT_ID));
				verify(connectAuthCallbackMock, times(1)).onSuccess(anyLong());
				verifyNoMoreInteractions(connectAuthCallbackMock);
				return null;
			}
		}).when(webSocketMock).send(anyString());

		doAnswer(new Answer<Future<WebSocket>>() {
			@Override
			public Future<WebSocket> answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				assertNull(invocation.getArguments()[1]);
				assertNotNull(invocation.getArguments()[2]);
				assertEquals(Constants.SCRATCH_CONVERTER_WEB_SOCKET, invocation.getArguments()[0]);

				// call connectCallback.onCompleted(...)
				WebSocketConnectCallback connectCallback = (WebSocketConnectCallback) invocation.getArguments()[2];
				connectCallback.onCompleted(null, webSocketMock);
				verify(webSocketMock, times(1)).setStringCallback(any(WebSocket.StringCallback.class));
				verify(webSocketMock, times(1)).setClosedCallback(any(CompletedCallback.class));
				verify(webSocketMock, times(1)).send(anyString());
				verifyNoMoreInteractions(connectAuthCallbackMock);
				verifyNoMoreInteractions(webSocketMock);
				return null;
			}
		}).when(asyncHttpClientMock).websocket(anyString(), anyString(), any(WebSocketConnectCallback.class));

		// run the test
		webSocketClient.setAsyncHttpClient(asyncHttpClientMock);
		webSocketClient.connectAndAuthenticate(connectAuthCallbackMock);
	}

	@Test
	public void testAsyncConnectAndAuthenticateConnectionFailed() {
		final String expectedCancelExceptionMessage = "Successfully canceled the connection by the server!";

		final Client.ConnectAuthCallback connectAuthCallbackMock = Mockito.mock(Client.ConnectAuthCallback.class);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				final ClientException ex = (ClientException) invocation.getArguments()[0];
				assertEquals("java.lang.Exception: " + expectedCancelExceptionMessage, ex.getMessage());
				assertTrue(webSocketClient.isClosed());
				return null;
			}
		}).when(connectAuthCallbackMock).onConnectionFailure(any(ClientException.class));

		// mocking asyncHttpClient.websocket(...)
		doAnswer(new Answer<Future<WebSocket>>() {
			@Override
			public Future<WebSocket> answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				assertNull(invocation.getArguments()[1]);
				assertNotNull(invocation.getArguments()[2]);
				assertEquals(Constants.SCRATCH_CONVERTER_WEB_SOCKET, invocation.getArguments()[0]);

				// call connectCallback.onCompleted(...)
				WebSocketConnectCallback connectCallback = (WebSocketConnectCallback) invocation.getArguments()[2];

				// simulate that the connection failed!
				final WebSocket webSocketMock = Mockito.mock(WebSocket.class);
				connectCallback.onCompleted(new Exception(expectedCancelExceptionMessage), webSocketMock);
				verify(connectAuthCallbackMock, times(1)).onConnectionFailure(any(ClientException.class));
				verifyNoMoreInteractions(connectAuthCallbackMock);
				verifyZeroInteractions(webSocketMock);
				return null;
			}
		}).when(asyncHttpClientMock).websocket(anyString(), anyString(), any(WebSocketConnectCallback.class));

		// run the test
		webSocketClient.setAsyncHttpClient(asyncHttpClientMock);
		webSocketClient.connectAndAuthenticate(connectAuthCallbackMock);
	}

	@Test
	public void testAsyncConnectAndAuthenticateAuthenticationFailed() throws Exception {

		final long clientID = VALID_CLIENT_ID;

		Reflection.setPrivateField(WebSocketClient.class, webSocketClient, "clientID", clientID);

		final String expectedErrorMessage = "Authentication failed!";

		final Client.ConnectAuthCallback connectAuthCallbackMock = Mockito.mock(Client.ConnectAuthCallback.class);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				final ClientException ex = (ClientException) invocation.getArguments()[0];
				assertEquals(expectedErrorMessage, ex.getMessage());
				assertTrue(webSocketClient.isConnected());
				assertFalse(webSocketClient.isAuthenticated());
				return null;
			}
		}).when(connectAuthCallbackMock).onAuthenticationFailure(any(ClientException.class));

		final WebSocket webSocketMock = Mockito.mock(WebSocket.class);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				// simulate authentication failed response message from server:
				webSocketClient.onBaseMessage(new ErrorMessage(expectedErrorMessage));
				verify(connectAuthCallbackMock, times(1)).onAuthenticationFailure(any(ClientException.class));
				verifyNoMoreInteractions(connectAuthCallbackMock);
				return null;
			}
		}).when(webSocketMock).send(any(String.class));

		doAnswer(new Answer<Future<WebSocket>>() {
			@Override
			public Future<WebSocket> answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				assertNull(invocation.getArguments()[1]);
				assertNotNull(invocation.getArguments()[2]);
				assertEquals(Constants.SCRATCH_CONVERTER_WEB_SOCKET, invocation.getArguments()[0]);

				// call connectCallback.onCompleted(...)
				WebSocketConnectCallback connectCallback = (WebSocketConnectCallback) invocation.getArguments()[2];
				connectCallback.onCompleted(null, webSocketMock);
				verify(webSocketMock, times(1)).setStringCallback(any(WebSocket.StringCallback.class));
				verify(webSocketMock, times(1)).setClosedCallback(any(CompletedCallback.class));
				verify(webSocketMock, times(1)).send(anyString());
				verifyNoMoreInteractions(connectAuthCallbackMock);
				verifyNoMoreInteractions(webSocketMock);
				return null;
			}
		}).when(asyncHttpClientMock).websocket(anyString(), anyString(), any(WebSocketConnectCallback.class));

		// run the test
		webSocketClient.setAsyncHttpClient(asyncHttpClientMock);
		webSocketClient.connectAndAuthenticate(connectAuthCallbackMock);
	}

	@Test
	public void testServerClosedConnectionAfterConnectionIsEstablished() throws Exception {
		final String expectedClosedExceptionMessage = "Successfully closed the connection!";

		final Client.ConnectAuthCallback connectAuthCallbackMock = Mockito.mock(Client.ConnectAuthCallback.class);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				final ClientException ex = (ClientException) invocation.getArguments()[0];
				assertTrue(webSocketClient.isClosed());
				assertEquals("java.lang.Exception: " + expectedClosedExceptionMessage, ex.getMessage());
				return null;
			}
		}).when(connectAuthCallbackMock).onConnectionClosed(any(ClientException.class));

		webSocketClient.setAsyncHttpClient(asyncHttpClientMock);

		Reflection.setPrivateField(WebSocketClient.class, webSocketClient, "state", Client.State.CONNECTED_AUTHENTICATED);

		Field connectAuthCallbackField = WebSocketClient.class.getDeclaredField("connectAuthCallback");
		connectAuthCallbackField.setAccessible(true);
		connectAuthCallbackField.set(webSocketClient, connectAuthCallbackMock);

		// now simulate that server closes the connection:
		webSocketClient.onCompleted(new Exception(expectedClosedExceptionMessage));
		verify(connectAuthCallbackMock, times(1)).onConnectionClosed(any(ClientException.class));
		verifyNoMoreInteractions(connectAuthCallbackMock);
	}

	@Test
	public void testClientClosedConnection() throws Exception {
		final String expectedExceptionMessage = "Client closed connection successfully!";

		final Client.ConnectAuthCallback connectAuthCallbackMock = Mockito.mock(Client.ConnectAuthCallback.class);
		final WebSocket webSocketMock = Mockito.mock(WebSocket.class);

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				webSocketClient.onCompleted(new Exception(expectedExceptionMessage));
				verify(connectAuthCallbackMock, times(1)).onConnectionClosed(any(ClientException.class));
				verifyNoMoreInteractions(connectAuthCallbackMock);
				return null;
			}
		}).when(webSocketMock).close();

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				final ClientException ex = (ClientException) invocation.getArguments()[0];
				assertTrue(webSocketClient.isClosed());
				assertEquals("java.lang.Exception: " + expectedExceptionMessage, ex.getMessage());
				return null;
			}
		}).when(connectAuthCallbackMock).onConnectionClosed(any(ClientException.class));

		webSocketClient.setAsyncHttpClient(asyncHttpClientMock);

		Reflection.setPrivateField(WebSocketClient.class, webSocketClient, "state", Client.State.CONNECTED_AUTHENTICATED);

		Field newField = WebSocketClient.class.getDeclaredField("webSocket");
		newField.setAccessible(true);
		newField.set(webSocketClient, webSocketMock);

		Field connectAuthCallbackField = WebSocketClient.class.getDeclaredField("connectAuthCallback");
		connectAuthCallbackField.setAccessible(true);
		connectAuthCallbackField.set(webSocketClient, connectAuthCallbackMock);

		webSocketClient.close();
		verify(webSocketMock, times(1)).close();
	}

	//------------------------------------------------------------------------------------------------------------------
	// Command tests
	//------------------------------------------------------------------------------------------------------------------
	@Test
	public void testSendRetrieveInfoCommand() throws Exception {
		final RetrieveInfoCommand expectedRetrieveInfoCommand = new RetrieveInfoCommand();

		final WebSocket webSocketMock = Mockito.mock(WebSocket.class);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);

				final String jsonCommand = (String) invocation.getArguments()[0];
				assertEquals(expectedRetrieveInfoCommand.toJson().toString(), jsonCommand);
				return null;
			}
		}).when(webSocketMock).send(any(String.class));

		Reflection.setPrivateField(WebSocketClient.class, webSocketClient, "state", Client.State.CONNECTED_AUTHENTICATED);

		Field newField = WebSocketClient.class.getDeclaredField("webSocket");
		newField.setAccessible(true);
		newField.set(webSocketClient, webSocketMock);

		webSocketClient.retrieveInfo();
		verify(webSocketMock, times(1)).send(any(String.class));
		verifyNoMoreInteractions(webSocketMock);
	}

	@Test
	public void testSendScheduleJobCommand() throws Exception {
		final long expectedJobID = 1;
		final boolean expectedForceValue = false;
		final boolean expectedVerboseValue = false;
		final String expectedProgramTitle = "Test program";
		final WebImage expectedProgramImage = new WebImage(Uri.parse("http://www.catrobat.org/images/logo.png"));

		final ScheduleJobCommand expectedScheduleJobCommand = new ScheduleJobCommand(expectedJobID,
				expectedForceValue, expectedVerboseValue);

		final ConvertCallback convertCallbackMock = Mockito.mock(ConvertCallback.class);

		doAnswer(new Answer<Boolean>() {
			@Override
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				assertNotNull(invocation.getArguments()[2]);
				final Job job = (Job) invocation.getArguments()[0];
				assertEquals(expectedJobID, job.getJobID());
				assertEquals(expectedProgramTitle, job.getTitle());
				assertEquals(expectedProgramImage, job.getImage());
				assertEquals(expectedForceValue, invocation.getArguments()[1]);
				assertEquals(convertCallbackMock, invocation.getArguments()[2]);
				return true;
			}
		}).when(messageListenerMock).scheduleJob(any(Job.class), any(Boolean.class), any(ConvertCallback.class));

		final WebSocket webSocketMock = Mockito.mock(WebSocket.class);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				final String jsonCommand = (String) invocation.getArguments()[0];
				assertEquals(expectedScheduleJobCommand.toJson().toString(), jsonCommand);
				return null;
			}
		}).when(webSocketMock).send(any(String.class));

		Reflection.setPrivateField(WebSocketClient.class, webSocketClient, "state", Client.State.CONNECTED_AUTHENTICATED);

		Field newField = WebSocketClient.class.getDeclaredField("webSocket");
		newField.setAccessible(true);
		newField.set(webSocketClient, webSocketMock);

		webSocketClient.setConvertCallback(convertCallbackMock);
		webSocketClient.convertProgram(expectedJobID, expectedProgramTitle, expectedProgramImage,
				expectedVerboseValue, expectedForceValue);

		verifyZeroInteractions(convertCallbackMock);
		verify(webSocketMock, times(1)).send(anyString());
		verifyNoMoreInteractions(webSocketMock);
	}

	//------------------------------------------------------------------------------------------------------------------
	// Event tests
	//------------------------------------------------------------------------------------------------------------------
	@Test
	public void testReceivedOnBaseMessageEventWithInfoMessage() throws Exception {
		final Job expectedUnscheduledJob = new Job(1, "Test program",
				new WebImage(Uri.parse("http://www.catrobat.org/images/logo.png")));
		expectedUnscheduledJob.setState(Job.State.UNSCHEDULED);

		final Job expectedFinishedJob = new Job(2, "Test program2", null);
		expectedFinishedJob.setState(Job.State.FINISHED);
		final String expectedDownloadURL = Constants.SCRATCH_CONVERTER_BASE_URL
				+ "/download?job_id=2&client_id=" + VALID_CLIENT_ID + "&fname=Test%20program2";
		expectedFinishedJob.setDownloadURL(expectedDownloadURL);

		final double expectedCatrobatLanguageVersion = Constants.CURRENT_CATROBAT_LANGUAGE_VERSION;

		final Job[] expectedJobs = new Job[] {expectedUnscheduledJob, expectedFinishedJob};
		final InfoMessage infoMessage = new InfoMessage(expectedCatrobatLanguageVersion, expectedJobs);

		final Client.ProjectDownloadCallback downloadCallbackMock = Mockito.mock(Client.ProjectDownloadCallback.class);
		final ConvertCallback convertCallbackMock = Mockito.mock(ConvertCallback.class);

		doAnswer(new Answer<Client.ProjectDownloadCallback>() {
			int counter = 0;

			@Override
			public Client.ProjectDownloadCallback answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				assertNotNull(invocation.getArguments()[1]);

				final Job job = (Job) invocation.getArguments()[0];
				final ConvertCallback convertCallback = (ConvertCallback) invocation.getArguments()[1];

				assertEquals(job.getTitle(), expectedJobs[counter].getTitle());
				assertEquals(expectedJobs[counter], job);
				assertEquals(convertCallbackMock, convertCallback);

				counter++;
				if (job.getState() == Job.State.FINISHED) {
					return downloadCallbackMock;
				}
				return null;
			}
		}).when(messageListenerMock).restoreJobIfRunning(any(Job.class), any(ConvertCallback.class));

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				assertNotNull(invocation.getArguments()[1]);

				final double catrobatLanguageVersion = (double) invocation.getArguments()[0];
				final Job[] jobs = (Job[]) invocation.getArguments()[1];

				assertEquals(expectedCatrobatLanguageVersion, catrobatLanguageVersion);
				assertEquals(expectedJobs.length, jobs.length);

				for (int i = 0; i < jobs.length; i++) {
					final Job job = jobs[i];
					final Job expectedJob = expectedJobs[i];
					assertEquals(expectedJob, job);
				}
				return null;
			}
		}).when(convertCallbackMock).onInfo(any(Double.class), any(Job[].class));

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				assertNotNull(invocation.getArguments()[1]);

				final Job finishedJob = (Job) invocation.getArguments()[0];
				final Client.ProjectDownloadCallback downloadCallback = (ProjectDownloadCallback)
						invocation.getArguments()[1];
				final String downloadURL = (String) invocation.getArguments()[2];

				assertEquals(downloadCallback, downloadCallbackMock);
				assertEquals(expectedDownloadURL, downloadURL);
				assertEquals(expectedFinishedJob, finishedJob);
				return null;
			}
		}).when(convertCallbackMock).onConversionAlreadyFinished(any(Job.class), any(ProjectDownloadCallback.class),
				any(String.class));

		Reflection.setPrivateField(WebSocketClient.class, webSocketClient, "state", Client.State.CONNECTED_AUTHENTICATED);

		webSocketClient.setConvertCallback(convertCallbackMock);
		webSocketClient.onBaseMessage(infoMessage);

		verify(convertCallbackMock, times(1)).onInfo(anyDouble(), any(Job[].class));
		verify(convertCallbackMock, times(1)).onConversionAlreadyFinished(any(Job.class),
				any(Client.ProjectDownloadCallback.class), anyString());
		verifyNoMoreInteractions(convertCallbackMock);
		verify(messageListenerMock, times(1)).setBaseMessageHandler(eq(webSocketClient));
		verify(messageListenerMock, times(2)).restoreJobIfRunning(any(Job.class), any(ConvertCallback.class));
		verifyNoMoreInteractions(messageListenerMock);
	}

	@Test
	public void testReceivedOnBaseMessageEventWithErrorMessageWhenClientIsNotConnected() throws Exception {
		final String expectedErrorMessageString = "Error message successfully received";
		final ErrorMessage expectedErrorMessage = new ErrorMessage(expectedErrorMessageString);

		final Client.ConnectAuthCallback connectAuthCallbackMock = Mockito.mock(Client.ConnectAuthCallback.class);
		final ConvertCallback convertCallbackMock = Mockito.mock(ConvertCallback.class);

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);
				final String errorMessageString = (String) invocation.getArguments()[0];
				assertEquals(expectedErrorMessageString, errorMessageString);
				return null;
			}
		}).when(convertCallbackMock).onError(any(String.class));

		Reflection.setPrivateField(WebSocketClient.class, webSocketClient, "state", Client.State.NOT_CONNECTED);

		Field connectAuthCallbackField = WebSocketClient.class.getDeclaredField("connectAuthCallback");
		connectAuthCallbackField.setAccessible(true);
		connectAuthCallbackField.set(webSocketClient, connectAuthCallbackMock);

		webSocketClient.setConvertCallback(convertCallbackMock);
		webSocketClient.onBaseMessage(expectedErrorMessage);

		verify(connectAuthCallbackMock, times(0)).onAuthenticationFailure(any(ClientException.class));
		verifyNoMoreInteractions(connectAuthCallbackMock);
		verify(convertCallbackMock, times(1)).onError(anyString());
		verifyNoMoreInteractions(convertCallbackMock);
	}

	@Test
	public void testReceivedOnBaseMessageEventWithErrorMessageWhenClientIsConnectedButNotAuthenticated()
			throws NoSuchFieldException, IllegalAccessException {
		final String expectedErrorMessageString = "Error message successfully received";
		final ErrorMessage expectedErrorMessage = new ErrorMessage(expectedErrorMessageString);

		final Client.ConnectAuthCallback connectAuthCallbackMock = Mockito.mock(Client.ConnectAuthCallback.class);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				final ClientException ex = (ClientException) invocation.getArguments()[0];
				assertEquals(expectedErrorMessageString, ex.getMessage());
				assertTrue(webSocketClient.isConnected());
				assertFalse(webSocketClient.isAuthenticated());
				return null;
			}
		}).when(connectAuthCallbackMock).onAuthenticationFailure(any(ClientException.class));

		final ConvertCallback convertCallbackMock = Mockito.mock(ConvertCallback.class);

		Field field = WebSocketClient.class.getDeclaredField("state");
		field.setAccessible(true);
		field.set(webSocketClient, Client.State.CONNECTED);

		Field connectAuthCallbackField = WebSocketClient.class.getDeclaredField("connectAuthCallback");
		connectAuthCallbackField.setAccessible(true);
		connectAuthCallbackField.set(webSocketClient, connectAuthCallbackMock);

		webSocketClient.setConvertCallback(convertCallbackMock);
		webSocketClient.onBaseMessage(expectedErrorMessage);

		verify(connectAuthCallbackMock, times(1)).onAuthenticationFailure(any(ClientException.class));
		verifyNoMoreInteractions(convertCallbackMock);
		verifyZeroInteractions(convertCallbackMock);
	}

	@Test
	public void testReceivedOnBaseMessageEventWithErrorMessageWhenClientIsConnectedAndAuthenticated()
			throws NoSuchFieldException, IllegalAccessException {
		final String expectedErrorMessageString = "Error message successfully received";
		final ErrorMessage expectedErrorMessage = new ErrorMessage(expectedErrorMessageString);

		final Client.ConnectAuthCallback connectAuthCallbackMock = Mockito.mock(Client.ConnectAuthCallback.class);
		final ConvertCallback convertCallbackMock = Mockito.mock(ConvertCallback.class);

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNull(invocation.getArguments()[0]);
				assertNotNull(invocation.getArguments()[1]);
				final ClientException ex = (ClientException) invocation.getArguments()[1];
				assertEquals(expectedErrorMessageString, ex.getMessage());
				assertTrue(webSocketClient.isConnected());
				assertTrue(webSocketClient.isAuthenticated());
				return null;
			}
		}).when(convertCallbackMock).onConversionFailure(nullable(Job.class), any(ClientException.class));

		Field field = WebSocketClient.class.getDeclaredField("state");
		field.setAccessible(true);
		field.set(webSocketClient, Client.State.CONNECTED_AUTHENTICATED);

		Field connectAuthCallbackField = WebSocketClient.class.getDeclaredField("connectAuthCallback");
		connectAuthCallbackField.setAccessible(true);
		connectAuthCallbackField.set(webSocketClient, connectAuthCallbackMock);

		webSocketClient.setConvertCallback(convertCallbackMock);
		webSocketClient.onBaseMessage(expectedErrorMessage);

		verify(connectAuthCallbackMock, times(0)).onAuthenticationFailure(any(ClientException.class));
		verifyNoMoreInteractions(connectAuthCallbackMock);
		verify(convertCallbackMock, times(1)).onConversionFailure(nullable(Job.class), any(ClientException.class));
		verifyNoMoreInteractions(convertCallbackMock);
	}

	@Test
	public void testReceivedUserCanceledConversionEvent() throws NoSuchFieldException, IllegalAccessException {
		final long expectedJobID = 1;

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);

				final Long jobID = (Long) invocation.getArguments()[0];
				assertEquals(expectedJobID, jobID.longValue());
				return null;
			}
		}).when(messageListenerMock).onUserCanceledConversion(any(Long.class));

		Field field = WebSocketClient.class.getDeclaredField("state");
		field.setAccessible(true);
		field.set(webSocketClient, Client.State.CONNECTED_AUTHENTICATED);

		webSocketClient.onUserCanceledConversion(expectedJobID);

		verify(messageListenerMock, times(1)).setBaseMessageHandler(eq(webSocketClient));
		verify(messageListenerMock, times(1)).onUserCanceledConversion(any(Long.class));
		verifyNoMoreInteractions(messageListenerMock);
	}

	//------------------------------------------------------------------------------------------------------------------
	// Wrapper method tests
	//------------------------------------------------------------------------------------------------------------------
	@Test
	public void testIsJobInProgressWrapperCalled() {
		final long expectedJobID1 = 1;
		final long expectedJobID2 = 2;

		doAnswer(new Answer<Boolean>() {
			@Override
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull(invocation.getArguments()[0]);

				final Long jobID = (Long) invocation.getArguments()[0];
				if (jobID.longValue() == expectedJobID1) {
					return true;
				} else if (jobID.longValue() == expectedJobID2) {
					return false;
				} else {
					fail("Unexpected jobID given: " + jobID);
					return false;
				}
			}
		}).when(messageListenerMock).isJobInProgress(any(Long.class));

		assertTrue(webSocketClient.isJobInProgress(expectedJobID1));
		assertFalse(webSocketClient.isJobInProgress(expectedJobID2));

		verify(messageListenerMock, times(1)).setBaseMessageHandler(eq(webSocketClient));
		verify(messageListenerMock, times(2)).isJobInProgress(any(Long.class));
		verifyNoMoreInteractions(messageListenerMock);
	}

	@Test
	public void testGetNumberOfJobsInProgressWrapperCalled() {
		final int firstNumberOfJobsResult = 1;
		final int secondNumberOfJobsResult = 2;

		doAnswer(new Answer<Integer>() {
			int counter = 0;

			@Override
			public Integer answer(InvocationOnMock invocation) throws Throwable {
				if (counter++ == 0) {
					return firstNumberOfJobsResult;
				} else {
					return secondNumberOfJobsResult;
				}
			}
		}).when(messageListenerMock).getNumberOfJobsInProgress();

		assertEquals(webSocketClient.getNumberOfJobsInProgress(), firstNumberOfJobsResult);
		assertEquals(webSocketClient.getNumberOfJobsInProgress(), secondNumberOfJobsResult);

		verify(messageListenerMock, times(1)).setBaseMessageHandler(eq(webSocketClient));
		verify(messageListenerMock, times(2)).getNumberOfJobsInProgress();
		verifyNoMoreInteractions(messageListenerMock);
	}
}
