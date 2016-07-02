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

package org.catrobat.catroid.transfers;

import android.support.annotation.Nullable;
import android.util.Log;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

final public class ScratchConverterWebSocketClient implements ScratchConverterClient {

	private static final String TAG = ScratchConverterWebSocketClient.class.getSimpleName();

	public enum NotificationType {
		ERROR(0),
		JOB_FAILED(1),
		JOB_RUNNING(2),
		JOB_ALREADY_RUNNING(3),
		JOB_READY(4),
		JOB_OUTPUT(5),
		JOB_PROGRESS(6),
		JOB_FINISHED(7),
		JOB_DOWNLOAD(8),
		JOBS_INFO(9),
		CLIENT_ID(10),
		RENEW_CLIENT_ID(11);

		private int typeID;

		private static Map<Integer, NotificationType> map = new HashMap<>();

		static {
			for (NotificationType legEnum : NotificationType.values()) {
				map.put(legEnum.typeID, legEnum);
			}
		}

		NotificationType(final int typeID) {
			this.typeID = typeID;
		}

		public static NotificationType valueOf(int typeID) {
			return map.get(typeID);
		}
	}

	int clientID;
	String projectTitle;
	String statusLine;
	String consoleText;
	boolean connected;
	boolean temp;
	static ScratchConverterWebSocketClient instance = null;

	public ScratchConverterWebSocketClient() {
		this.clientID = 7; // TODO: ...
		this.connected = false;
		this.temp = false;
		this.projectTitle = null;
		this.consoleText = "";
	}

	private void reset() {
		this.consoleText = "";
		this.clientID = 7; // TODO: ...
		this.temp = false;
	}

	@Nullable
	public static ScratchConverterWebSocketClient getInstance() {
		if (instance == null) {
			// do it in a thread safe way
			synchronized (ScratchConverterClient.class) {
				if (instance == null) {
					try {
						instance = new ScratchConverterWebSocketClient();
					} catch (Exception ex) {
						Log.w(TAG, "Unable to create disk cache!");
						return null;
					}
				}
			}
		}
		return instance;
	}

	public String getProjectTitle() {
		return projectTitle;
	}

	public String getStatusLine() {
		return statusLine;
	}

	public String getConsoleText() {
		return consoleText;
	}

	public void convertProject(final long scratchProjectID, final String projectTitle) {
		reset();
		if (connected) {
			// TODO: handle this...
		}

		this.projectTitle = projectTitle;

		AsyncHttpClient.getDefaultInstance().websocket("ws://scratch2.catrob.at/convertersocket", null, new
				AsyncHttpClient.WebSocketConnectCallback() {
					@Override
					public void onCompleted(Exception ex, final WebSocket webSocket) {
						if (ex != null) {
							ex.printStackTrace();
							return;
						}
						connected = true;
						Log.d(TAG, "Sending set_client_ID request!");

						Map<String, Object> map = new HashMap<String, Object>() {{
							put("cmd", "set_client_ID");
							put("args", new HashMap<String, String>() {{
								put("clientID", String.valueOf(clientID));
							}});
						}};
						JSONObject jsonObject = new JSONObject(map);
						Log.d(TAG, "Sending: " + jsonObject.toString());
						webSocket.send(jsonObject.toString());
						webSocket.setStringCallback(new WebSocket.StringCallback() {
							public void onStringAvailable(String message) {
								Log.d(TAG, message);
								final JSONObject jsonMessage;

								try {
									if (! temp) {
										temp = true;
										Log.d(TAG, "Sending start job!");
										Map<String, Object> map = new HashMap<String, Object>() {{
											put("cmd", "schedule_job");
											put("args", new HashMap<String, String>() {{
												put("clientID", String.valueOf(clientID));
												put("url", "https://scratch.mit.edu/projects/" + scratchProjectID);
												put("force", "1");
											}});
										}};
										JSONObject jsonObject = new JSONObject(map);
										String sendMessage = jsonObject.toString().replace("\\", "");
										Log.d(TAG, "Sending: " + sendMessage);
										webSocket.send(sendMessage);
										return;
									}

									jsonMessage = new JSONObject(message);
									if (jsonMessage.length() == 0) {
										return;
									}

									// case ERROR: { "msg" }
									NotificationType type = NotificationType.valueOf(jsonMessage.getInt("type"));
									if (type == NotificationType.ERROR) {
										Log.e(TAG, "ERROR: " + jsonMessage.getString("msg"));
										return;
									}

									JSONObject jsonData = jsonMessage.getJSONObject("data");
									if (jsonData == null) {
										Log.e(TAG, "ERROR: Invalid message received! Data field is empty.");
										return;
									}

									// case JOB_FAILED: { "jobID" }
									if (type == NotificationType.JOB_FAILED) {
										if (jsonData.getLong("jobID") != scratchProjectID) {
											return;
										}
										Log.e(TAG, "Job for converting Scratch project '" + scratchProjectID
												+ "' failed!");
										return;
									}

									// case JOB_RUNNING: { "jobID" }
									if (type == NotificationType.JOB_RUNNING) {
										Log.i(TAG, "Job running...");
										statusLine = "Job running...";
										return;
									}

									// case JOB_ALREADY_RUNNING: { "jobID" }
									if (type == NotificationType.JOB_ALREADY_RUNNING) {
										Log.i(TAG, "Job already running!");
										statusLine = "Job already running!";
										return;
									}

									// case JOB_READY: { "jobID" }
									if (type == NotificationType.JOB_READY) {
										Log.i(TAG, "Waiting for worker to process this job...");
										statusLine = "Waiting for worker to process this job...";
										return;
									}

									// case JOB_OUTPUT: { "jobID", "line" }
									if (type == NotificationType.JOB_OUTPUT) {
										final String projectIDForMessage = jsonData.getString("jobID");
										final JSONArray jsonLines = jsonData.getJSONArray("lines");
										for (int i = 0; i < jsonLines.length(); ++i) {
											final String line = jsonLines.getString(i);
											Log.i(TAG, line);
											consoleText += line + "\n";
										}
										return;
									}

									// case JOB_PROGRESS: { "jobID", "progress" }
									if (type == NotificationType.JOB_PROGRESS) {
										final double progress = jsonData.getDouble("progress");
										final double roundedProgress = Math.round(progress);
										Log.i(TAG, roundedProgress + "%");
										statusLine = roundedProgress + "%";
										return;
									}

									// case JOB_FINISHED: { "jobID" }
									if (type == NotificationType.JOB_FINISHED) {
										Log.i(TAG, "Job finished!");
										statusLine = "Job finished!";
										return;
									}

									// case JOB_DOWNLOAD: { "jobID", "url" }
									if (type == NotificationType.JOB_DOWNLOAD) {
										if (jsonData.getLong("jobID") != scratchProjectID) {
											return;
										}
										//var download_url = location.protocol + "//" + location.host + result.data["url"];
										return;
									}

									// case JOBS_INFO: { "jobsInfo" }
									if (type == NotificationType.JOBS_INFO) {
										// TODO: implement...
										return;
									}

									// case CLIENT_ID: { "clientID" }
									// (retrieve_client_id & set_client_id)
									if (type == NotificationType.CLIENT_ID) {
											/*
											if (socketHandler.clientID != null && socketHandler.clientID != result.data["clientID"]) {
												alert("Server echoed invalid client ID back. This should never happen!");
												return;
											}
											*/
										clientID = jsonData.getInt("clientID");
											/* TODO: store...
											if (typeof(Storage) !== "undefined") {
												localStorage.setItem("clientID", socketHandler.clientID);
											}
											*/
										return;
									}

									// RENEW_CLIENT_ID: { "clientID" }
									// (set_client_id, if given clientID is not valid any more)
									if (type == NotificationType.RENEW_CLIENT_ID) {
											/*
											if (clientID == 0) {
												alert("Server sent renew-clientID-request with same client ID back. This should never happen!");
												return;
											}
											*/
										clientID = jsonData.getInt("clientID");
											/* TODO: store...
											if (typeof(Storage) !== "undefined") {
												localStorage.setItem("clientID", socketHandler.clientID);
											}
											*/
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
					}
				});
				/*
				webSocket.setDataCallback(new DataCallback() {
					public void onDataAvailable(DataEmitter emitter, ByteBufferList byteBufferList) {
						if (! started) {
							Log.d(TAG, "Sending start job!");
							webSocket.send("{'cmd':'schedule_job','args':{'clientID':'7','url':" +
									"'https://scratch.mit.edu/projects/82443924/','force':True}}");
							started = true;
						}
						Log.d(TAG, "I got some bytes!");
						// note that this data has been read
						byteBufferList.recycle();
					}
				});
				*/
	}
}
