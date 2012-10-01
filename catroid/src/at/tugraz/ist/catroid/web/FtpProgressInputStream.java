/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.web;

import java.io.IOException;
import java.io.InputStream;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class FtpProgressInputStream extends InputStream {

	private final static String TAG = FtpProgressInputStream.class.getSimpleName();
	private static final Integer DATA_STREAM_UPDATE_SIZE = 1024 * 20; //20 KB
	private InputStream inputStream;
	private Handler handler;

	private Integer progress;
	private boolean connectionClosed;

	public FtpProgressInputStream(InputStream inputStream, Handler handler) { //handler?
		this.inputStream = inputStream;
		this.handler = handler;

		this.progress = 0;
		this.connectionClosed = false;
	}

	@Override
	public int read() throws IOException {
		int count = inputStream.read();
		return updateProgress(count);
	}

	@Override
	public int read(byte[] buffer, int offset, int length) throws IOException {
		int count = inputStream.read(buffer, offset, length);
		return updateProgress(count);
	}

	@Override
	public void close() throws IOException {
		super.close();
		if (connectionClosed) {
			throw new IOException("Connection is already closed");
		}
		connectionClosed = true;
	}

	private int updateProgress(int count) {
		if (count > 0) {
			progress += count;
			//send for every 20 kilobytes read a message to update the progress
			if ((progress % DATA_STREAM_UPDATE_SIZE) == 0) {
				sendUpdateIntent(progress, false);
			}
		} else if (count == -1) {
			sendUpdateIntent(100, true);
		}
		return count;
	}

	private void sendUpdateIntent(long progress, boolean endOfFileReached) {
		Bundle progressBundle = new Bundle();
		progressBundle.putLong("currentUploadProgress", progress);
		progressBundle.putBoolean("endOfFileReached", endOfFileReached);
		Message progressMessage = Message.obtain();
		progressMessage.setData(progressBundle);
		handler.sendMessage(progressMessage);
	}

}
