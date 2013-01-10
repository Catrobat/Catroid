/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.web;

public class WebconnectionException extends Exception {

	private static final long serialVersionUID = 1L;

	public static final int ERROR_JSON = 1001;
	public static final int ERROR_NETWORK = 1002;
	public static final int ERROR_UNKNOWN = 1003;

	private int statusCode;
	private String message;

	public WebconnectionException(int statusCode) {
		super();
		this.statusCode = statusCode;
	}

	public WebconnectionException(int statusCode, String message) {
		super(message);
		this.statusCode = statusCode;
		this.message = message;
	}

	public int getStatusCode() {
		return statusCode;
	}

	@Override
	public String getMessage() {
		if (message == null) {
			message = "Unknown Error, no exception message given.";
		}
		return message;
	}

}
