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
import org.catrobat.catroid.common.FlavoredConstants;

public final class ServerAuthenticationConstants {
	public static final String SIGNIN_USERNAME_KEY = "username";
	public static final String SIGNIN_OAUTH_ID_KEY = "id";
	public static final String SIGNIN_EMAIL_KEY = "email";
	public static final String SIGNIN_LOCALE_KEY = "locale";
	public static final String CATROBAT_USERNAME_KEY = "registrationUsername";
	public static final String CATROBAT_PASSWORD_KEY = "registrationPassword";
	public static final String CATROBAT_COUNTRY_KEY = "registrationCountry";
	public static final String CATROBAT_EMAIL_KEY = "registrationEmail";
	public static final String GOOGLE_LOGIN_URL_APPENDING = "api/loginWithGoogle/loginWithGoogle.json";
	public static final String LOGIN_URL_APPENDING = "api/login/Login.json";
	public static final String REGISTRATION_URL_APPENDING = "api/register/Register.json";
	public static final Integer SERVER_RESPONSE_REGISTER_OK = 201;
	public static final Integer SERVER_RESPONSE_TOKEN_OK = 200;
	public static final String JSON_STATUS_CODE = "statusCode";
	public static final String JSON_ANSWER = "answer";
	public static final String JSON_TOKEN = "token";
	public static final int TOKEN_LENGTH = 32;
	public static final String TOKEN_CODE_INVALID = "-1";
	public static final String GOOGLE_LOGIN_CATROWEB_SERVER_CLIENT_ID = "427226922034"
			+ "-r016ige5kb30q9vflqbt1h0i3arng8u1.apps.googleusercontent.com";

	public static final String FILE_TAG_URL_HTTP = FlavoredConstants.BASE_URL_HTTPS + "api/tags/getTags.json";
	public static final String FILE_SURVEY_URL_HTTP = Constants.MAIN_URL_HTTPS + "/api/survey/";
	public static final String SIGNIN_GOOGLE_CODE_KEY = "code";
	public static final String SIGNIN_ID_TOKEN = "id_token";
	public static final String OAUTH_TOKEN_AVAILABLE = "token_available";
	public static final String EMAIL_AVAILABLE = "email_available";
	public static final String USERNAME_AVAILABLE = "username_available";
	public static final String CHECK_TOKEN_URL = "api/checkToken/check.json";
	public static final String CHECK_GOOGLE_TOKEN_URL =
			FlavoredConstants.BASE_URL_HTTPS + "api/GoogleServerTokenAvailable/GoogleServerTokenAvailable.json";
	public static final String CHECK_EMAIL_AVAILABLE_URL =
			FlavoredConstants.BASE_URL_HTTPS + "api/EMailAvailable/EMailAvailable.json";
	public static final String CHECK_USERNAME_AVAILABLE_URL =
			FlavoredConstants.BASE_URL_HTTPS + "api/UsernameAvailable/UsernameAvailable.json";
	public static final String EXCHANGE_GOOGLE_CODE_URL =
			FlavoredConstants.BASE_URL_HTTPS + "api/exchangeGoogleCode/exchangeGoogleCode.json";

	private ServerAuthenticationConstants() {
		throw new AssertionError("No.");
	}
}
