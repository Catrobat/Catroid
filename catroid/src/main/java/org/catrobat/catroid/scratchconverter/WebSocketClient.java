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

package org.catrobat.catroid.scratchconverter;

import android.util.Log;

import com.google.android.gms.common.images.WebImage;
import com.google.common.base.Preconditions;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.WebSocket.StringCallback;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.scratchconverter.protocol.BaseMessageHandler;
import org.catrobat.catroid.scratchconverter.protocol.Job;
import org.catrobat.catroid.scratchconverter.protocol.MessageListener;
import org.catrobat.catroid.scratchconverter.protocol.command.AuthenticateCommand;
import org.catrobat.catroid.scratchconverter.protocol.command.CancelDownloadCommand;
import org.catrobat.catroid.scratchconverter.protocol.command.Command;
import org.catrobat.catroid.scratchconverter.protocol.command.RetrieveInfoCommand;
import org.catrobat.catroid.scratchconverter.protocol.command.ScheduleJobCommand;
import org.catrobat.catroid.scratchconverter.protocol.message.base.BaseMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.base.ClientIDMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.base.ErrorMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.base.InfoMessage;

public final class WebSocketClient<T extends MessageListener & StringCallback>
		implements Client, BaseMessageHandler, CompletedCallback {

	private interface ConnectCallback {
		void onSuccess();
		void onFailure(ClientException ex);
	}

	private static final String TAG = WebSocketClient.class.getSimpleName();

	private Client.State state;
	private long clientID;
	private final T messageListener;
	private AsyncHttpClient asyncHttpClient = AsyncHttpClient.getDefaultInstance();
	private WebSocket webSocket;
	private Client.ConnectAuthCallback connectAuthCallback;
	private ConvertCallback convertCallback;

	public WebSocketClient(final long clientID, final T messageListener) {
		this.clientID = clientID;
		this.state = State.NOT_CONNECTED;
		messageListener.setBaseMessageHandler(this);
		this.messageListener = messageListener;
		this.webSocket = null;
		this.connectAuthCallback = null;
		this.convertCallback = null;
	}

	public boolean isConnected() {
		return state == State.CONNECTED || state == State.CONNECTED_AUTHENTICATED;
	}

	@Override
	public boolean isClosed() {
		return state == State.NOT_CONNECTED;
	}

	@Override
	public boolean isAuthenticated() {
		return state == State.CONNECTED_AUTHENTICATED;
	}

	public void setAsyncHttpClient(final AsyncHttpClient asyncHttpClient) {
		this.asyncHttpClient = asyncHttpClient;
	}

	public void setConvertCallback(final ConvertCallback callback) {
		convertCallback = callback;
	}

	private void connect(final ConnectCallback connectCallback) {
		if (state == State.CONNECTED) {
			connectCallback.onSuccess();
			return;
		}
		Preconditions.checkState(webSocket == null);
		Preconditions.checkState(asyncHttpClient != null);

		final WebSocketClient client = this;
		asyncHttpClient.websocket(Constants.SCRATCH_CONVERTER_WEB_SOCKET, null, new
				AsyncHttpClient.WebSocketConnectCallback() {
					@Override
					public void onCompleted(Exception ex, final WebSocket newWebSocket) {
						Preconditions.checkState(state != State.CONNECTED && webSocket == null);
						if (ex != null) {
							connectCallback.onFailure(new ClientException(ex));
							return;
						}

						state = State.CONNECTED;
						webSocket = newWebSocket;

						// onMessage callback
						webSocket.setStringCallback(messageListener);

						// onClose callback
						webSocket.setClosedCallback(client);
						connectCallback.onSuccess();
					}
				});
	}

	@Override
	public void onCompleted(Exception ex) {
		// Note: this is the central connection-closed callback-method
		// (called when the server or client closes the connection):
		state = State.NOT_CONNECTED;
		connectAuthCallback.onConnectionClosed(new ClientException(ex));
	}

	public void close() {
		Preconditions.checkState(state != State.NOT_CONNECTED);
		Preconditions.checkState(webSocket != null);
		Preconditions.checkState(connectAuthCallback != null);
		state = State.NOT_CONNECTED;
		webSocket.close();
	}

	private void authenticate() {
		Preconditions.checkState(state == State.CONNECTED);
		Preconditions.checkState(webSocket != null);
		sendCommand(new AuthenticateCommand(clientID));
	}

	public void connectAndAuthenticate(final ConnectAuthCallback connectAuthCallback) {
		this.connectAuthCallback = connectAuthCallback;

		switch (state) {
			case NOT_CONNECTED:
				connect(new ConnectCallback() {
					@Override
					public void onSuccess() {
						Log.i(TAG, "Successfully connected to WebSocket server");
						authenticate();
					}

					@Override
					public void onFailure(ClientException ex) {
						connectAuthCallback.onConnectionFailure(ex);
					}
				});
				break;

			case CONNECTED:
				Log.i(TAG, "Already connected to WebSocket server!");
				authenticate();
				break;

			case CONNECTED_AUTHENTICATED:
				Log.i(TAG, "Already authenticated!");
				connectAuthCallback.onSuccess(clientID);
				break;
		}
	}

	@Override
	public void retrieveInfo() {
		Preconditions.checkState(state == State.CONNECTED_AUTHENTICATED);
		Preconditions.checkState(clientID != INVALID_CLIENT_ID);
		sendCommand(new RetrieveInfoCommand());
	}

	@Override
	public boolean isJobInProgress(long jobID) {
		return messageListener.isJobInProgress(jobID);
	}

	@Override
	public int getNumberOfJobsInProgress() {
		return messageListener.getNumberOfJobsInProgress();
	}

	@Override
	public void convertProgram(final long jobID, final String title, final WebImage image, final boolean verbose,
			final boolean force) {
		Preconditions.checkState(state == State.CONNECTED_AUTHENTICATED);
		Preconditions.checkState(clientID != INVALID_CLIENT_ID);
		final Job job = new Job(jobID, title, image);

		if (state != State.CONNECTED_AUTHENTICATED || webSocket == null) {
			convertCallback.onConversionFailure(job, new ClientException("Not connected!"));
			return;
		}

		if (!messageListener.scheduleJob(job, force, convertCallback)) {
			Log.e(TAG, "Cannot schedule job since another job of the same Scratch program is already running (job ID "
					+ "is: " + jobID + ")");
			convertCallback.onConversionFailure(job, new ClientException("Cannot start this job since the job "
					+ "already exists and is in progress! Set force-flag to true to restart the conversion while it "
					+ "is running!"));
			return;
		}

		Log.i(TAG, "Scheduling new job with ID: " + jobID);
		sendCommand(new ScheduleJobCommand(jobID, force, verbose));
	}

	@Override
	public void cancelDownload(final long jobID) {
		Preconditions.checkState(state == State.CONNECTED_AUTHENTICATED);
		Preconditions.checkState(clientID != INVALID_CLIENT_ID);
		sendCommand(new CancelDownloadCommand(jobID));
	}

	@Override
	public void onUserCanceledConversion(long jobID) {
		Preconditions.checkState(state == State.CONNECTED_AUTHENTICATED);
		Preconditions.checkState(clientID != INVALID_CLIENT_ID);
		messageListener.onUserCanceledConversion(jobID);
	}

	@Override
	public void onBaseMessage(BaseMessage baseMessage) {
		if (baseMessage instanceof InfoMessage) {
			final InfoMessage infoMessage = (InfoMessage) baseMessage;
			convertCallback.onInfo(infoMessage.getCatrobatLanguageVersion(), infoMessage.getJobList());

			final Job[] jobs = infoMessage.getJobList();
			for (Job job : jobs) {
				DownloadFinishedCallback downloadCallback = messageListener.restoreJobIfRunning(job, convertCallback);
				if (downloadCallback != null) {
					Log.i(TAG, "Downloading missed converted project...");
					convertCallback.onConversionFinished(job, downloadCallback, job.getDownloadURL(), null);
				}
			}
			return;
		}

		if (baseMessage instanceof ErrorMessage) {
			final ErrorMessage errorMessage = (ErrorMessage) baseMessage;
			Log.e(TAG, errorMessage.getMessage());

			if (state == State.CONNECTED) {
				Preconditions.checkState(connectAuthCallback != null);
				connectAuthCallback.onAuthenticationFailure(new ClientException(errorMessage.getMessage()));
			} else if (state == State.CONNECTED_AUTHENTICATED) {
				convertCallback.onConversionFailure(null, new ClientException(errorMessage.getMessage()));
			} else {
				convertCallback.onError(errorMessage.getMessage());
			}
			return;
		}

		if (baseMessage instanceof ClientIDMessage) {
			Preconditions.checkState(state == State.CONNECTED);

			final ClientIDMessage clientIDMessage = (ClientIDMessage) baseMessage;
			long oldClientID = clientID;
			clientID = clientIDMessage.getClientID();

			if (clientID != oldClientID) {
				Log.d(TAG, "New Client ID: " + clientID);
			}

			state = State.CONNECTED_AUTHENTICATED;
			connectAuthCallback.onSuccess(clientID);
			return;
		}

		Log.e(TAG, "No handler implemented for base message: " + baseMessage);
	}

	private void sendCommand(final Command command) {
		Preconditions.checkArgument(command != null);
		Preconditions.checkState(state == State.CONNECTED || state == State.CONNECTED_AUTHENTICATED);
		Preconditions.checkState(webSocket != null);

		final String dataToSend = command.toJson().toString();
		Log.d(TAG, "Sending: " + dataToSend);
		webSocket.send(dataToSend);
	}
}
