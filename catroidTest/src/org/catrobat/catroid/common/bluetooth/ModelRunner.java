/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.common.bluetooth;

import android.util.Log;

import org.catrobat.catroid.common.bluetooth.models.DeviceModel;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class ModelRunner extends Thread {

	public static final String TAG = ModelRunner.class.getSimpleName();

	private DataInputStream inStream;
	private OutputStream outStream;
	private DeviceModel model;

	ModelRunner(DeviceModel model, InputStream inStream, OutputStream outStream) {

		this.model = model;

		this.inStream = new DataInputStream(inStream);
		this.outStream = outStream;
	}

	@Override
	public void run() {
		try {
			model.start(inStream, outStream);
		} catch (IOException e) {
			Log.e(TAG, "Model execution terminated, Input or Output stream was closed.");
		}
	}

	public void stopModelRunner() {
		try {
			model.stop();
			outStream.close();
			inStream.close();
		} catch (IOException e) {
			Log.e(TAG, "An error occurred on stopping model.");
		}
	}
}
