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

package org.catrobat.catroid.web;

import org.catrobat.catroid.common.Constants;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WebConnection {
	private final OkHttpClient okHttpClient;
	private static final String USER_AGENT = "Mozilla/5.0 (compatible; Catrobatbot/1.0; +https://catrob.at/bot)";
	private static final String EXCEPTION_MESSAGE_TIMEOUT = "timeout";
	private static final String EXCEPTION_MESSAGE_CANCELED = "Canceled";

	private Call call;
	private String url;
	private WeakReference<WebRequestListener> weakListenerReference;

	public WebConnection(OkHttpClient okHttpClient) {
		this.okHttpClient = okHttpClient;
	}

	public void setListener(WebRequestListener listener) {
		this.weakListenerReference = new WeakReference<>(listener);
	}

	public void setUrl(String url) {
		this.url = url;
	}

	private synchronized WebRequestListener popListener() {
		if (weakListenerReference != null) {
			WebRequestListener listener = weakListenerReference.get();
			weakListenerReference = null;
			return listener;
		}
		return null;
	}

	public synchronized void sendWebRequest() {
		try {
			new URL(url);
		} catch (MalformedURLException e) {
			WebRequestListener listener = weakListenerReference.get();
			if (listener != null) {
				listener.onRequestFinished(Integer.toString(Constants.ERROR_BAD_REQUEST));
			}
			return;
		}

		Request request = new Request.Builder()
				.url(url)
				.header("User-Agent", USER_AGENT)
				.build();

		call = okHttpClient.newCall(request);
		call.enqueue(createCallback());
	}

	private Callback createCallback() {
		return new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				WebRequestListener listener = popListener();
				if (listener != null) {
					if (EXCEPTION_MESSAGE_TIMEOUT.equals(e.getMessage())) {
						listener.onRequestFinished(Integer.toString(Constants.ERROR_TIMEOUT));
					} else if (EXCEPTION_MESSAGE_CANCELED.equals(e.getMessage())) {
						listener.onCancelledCall();
					} else {
						listener.onRequestFinished(Integer.toString(Constants.ERROR_SERVER_ERROR));
					}
				}
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				WebRequestListener listener = popListener();
				if (listener != null) {
					if (response.isSuccessful()) {
						listener.onRequestFinished(response.body().string());
					} else {
						listener.onRequestFinished(String.valueOf(response.code()));
					}
				}
			}
		};
	}

	public void cancelCall() {
		if (call != null) {
			okHttpClient.dispatcher().executorService().execute(new Runnable() {
				@Override
				public void run() {
					call.cancel();
				}
			});
		}
	}

	public interface WebRequestListener {
		void onRequestFinished(String responseString);
		void onCancelledCall();
	}
}
