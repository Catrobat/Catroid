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

package org.catrobat.catroid.web;

import android.util.Log;

public class WebScratchProgramException extends Exception {
	private static final String TAG = WebconnectionException.class.getSimpleName();

	private static final long serialVersionUID = 1L;

	public static final int ERROR_PROGRAM_NOT_ACCESSIBLE = 1001;

	private final int statusCode;
	private final String message;

	public WebScratchProgramException(int statusCode, String message) {
		super(message);
		if (message == null) {
			message = "Unknown Error, no exception message given.";
		}
		this.statusCode = statusCode;
		this.message = message;
	}

	public int getStatusCode() {
		return statusCode;
	}

	@Override
	public String getMessage() {
		Log.d(TAG, "Error #" + statusCode + ": " + message);
		return message;
	}
}
