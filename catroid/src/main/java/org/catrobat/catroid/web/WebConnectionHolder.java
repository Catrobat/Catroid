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

import android.support.annotation.VisibleForTesting;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionSpec;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

public class WebConnectionHolder {
	private static final int MAX_CONNECTIONS = 10;
	private static final long TIMEOUT_DURATION = 60L;

	private List<WebConnection> connections = new ArrayList<>(MAX_CONNECTIONS);
	public final OkHttpClient okHttpClient;

	public WebConnectionHolder() {
		OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
		httpClientBuilder.connectTimeout(TIMEOUT_DURATION, TimeUnit.SECONDS);
		httpClientBuilder.readTimeout(TIMEOUT_DURATION, TimeUnit.SECONDS);
		httpClientBuilder.writeTimeout(TIMEOUT_DURATION, TimeUnit.SECONDS);

		ConnectionSpec modernTlsSpec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS).build();
		ConnectionSpec compatibleTlsSpec = new ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS).build();
		ConnectionSpec cleartextSpec = new ConnectionSpec.Builder(ConnectionSpec.CLEARTEXT).build();
		httpClientBuilder.connectionSpecs(ImmutableList.of(modernTlsSpec, compatibleTlsSpec, cleartextSpec));

		Dispatcher dispatcher = new Dispatcher();
		dispatcher.setMaxRequests(10);
		dispatcher.setMaxRequestsPerHost(10);
		httpClientBuilder.dispatcher(dispatcher);
		okHttpClient = httpClientBuilder.build();
	}

	public synchronized void onPause() {
		for (WebConnection connection : connections) {
			connection.cancelCall();
		}
		connections.clear();
	}

	public synchronized boolean addConnection(WebConnection connection) {
		if (connections.size() >= MAX_CONNECTIONS) {
			return false;
		}
		connections.add(connection);
		return true;
	}

	public synchronized void removeConnection(WebConnection connection) {
		connections.remove(connection);
	}

	@VisibleForTesting
	public List<WebConnection> getConnections() {
		return connections;
	}

	@VisibleForTesting
	public void setConnections(List<WebConnection> connections) {
		this.connections = connections;
	}
}
