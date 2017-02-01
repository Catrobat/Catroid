/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

import android.support.annotation.Nullable;

import com.google.android.gms.common.images.WebImage;

import org.catrobat.catroid.scratchconverter.protocol.Job;

import java.util.Date;

public interface Client {

	enum State {
		NOT_CONNECTED, CONNECTED, CONNECTED_AUTHENTICATED
	}

	long INVALID_CLIENT_ID = -1;

	void setConvertCallback(ConvertCallback convertCallback);
	boolean isClosed();
	boolean isAuthenticated();
	void connectAndAuthenticate(ConnectAuthCallback connectAuthCallback);
	void retrieveInfo();
	boolean isJobInProgress(long jobID);
	int getNumberOfJobsInProgress();
	void convertProgram(long jobID, String title, WebImage image, boolean verbose, boolean force);
	void onUserCanceledConversion(long jobID);
	void close();

	// callbacks
	interface ConnectAuthCallback {
		void onSuccess(long clientID);
		void onConnectionClosed(ClientException ex);
		void onConnectionFailure(ClientException ex);
		void onAuthenticationFailure(ClientException ex);
	}

	interface ConvertCallback {
		void onInfo(float supportedCatrobatLanguageVersion, Job[] jobs);
		void onJobScheduled(Job job);
		void onConversionReady(Job job);
		void onConversionStart(Job job);
		void onJobProgress(Job job, short progress);
		void onJobOutput(Job job, String[] lines);
		void onConversionFinished(Job job, DownloadCallback downloadCallback, String downloadURL, Date cachedDate);
		void onConversionAlreadyFinished(Job job, DownloadCallback downloadCallback, String downloadURL);
		void onConversionFailure(@Nullable Job job, ClientException ex);
		void onError(String errorMessage);
	}

	interface DownloadCallback {
		void onDownloadStarted(String url);
		void onDownloadProgress(short progress, String url);
		void onDownloadFinished(String catrobatProgramName, String url);
		void onUserCanceledDownload(String url);
	}
}
