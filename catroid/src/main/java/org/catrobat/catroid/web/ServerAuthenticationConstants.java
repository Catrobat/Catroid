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

package org.catrobat.catroid.web;

import org.catrobat.catroid.common.Constants;

public final class ServerAuthenticationConstants {
	public static final Integer SERVER_RESPONSE_REGISTER_OK = 201;
	public static final Integer SERVER_RESPONSE_TOKEN_OK = 200;
	public static final Integer SERVER_RESPONSE_USER_DELETED = 204;
	public static final Integer SERVER_RESPONSE_INVALID_UPLOAD_TOKEN = 401;
	public static final Integer SERVER_RESPONSE_REGISTER_UNPROCESSABLE_ENTITY = 422;
	public static final int DEPRECATED_TOKEN_LENGTH = 32;
	public static final String GOOGLE_LOGIN_CATROWEB_SERVER_CLIENT_ID = "427226922034"
			+ "-r016ige5kb30q9vflqbt1h0i3arng8u1.apps.googleusercontent.com";

	public static final String FILE_SURVEY_URL_HTTP = Constants.MAIN_URL_HTTPS + "/api/survey/";

	private ServerAuthenticationConstants() {
		throw new AssertionError("No.");
	}
}
