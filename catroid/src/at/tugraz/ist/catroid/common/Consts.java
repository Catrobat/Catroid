/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.common;

import android.os.Environment;

public final class Consts {

	public static final String PROJECT_EXTENTION = ".xml";
	public static final String CATROID_EXTENTION = ".catroid";

	public static final String DEFAULT_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + "/catroid";
	public static final String TMP_PATH = DEFAULT_ROOT + "/tmp";
	public static final String IMAGE_DIRECTORY = "/images";
	public static final String SOUND_DIRECTORY = "/sounds";

	public static final String NO_MEDIA_FILE = ".nomedia";

	//Costume:
	public static final int MAX_REL_COORDINATES = 1000;
	public static final int MAX_COSTUME_WIDTH = 1500;
	public static final int MAX_COSTUME_HEIGHT = 1500;

	//Upload:
	public static final String TOKEN = "token";
	public static final String DEVICE_IMEI = "deviceIMEI";
	public static final String USER_EMAIL = "userEmail";
	public static final String USER_LANGUAGE = "userLanguage";
	public static final String REG_USER_NAME = "registrationUsername";
	public static final String REG_USER_PASSWORD = "registrationPassword";
	public static final String REG_USER_COUNTRY = "registrationCountry";
	public static final String REG_USER_LANGUAGE = "registrationLanguage";
	public static final String REG_USER_EMAIL = "registrationEmail";

	public static final String BASE_URL = "http://catroidtest.ist.tugraz.at/";
	public static final String BASE_URL_TEST = "http://catroidtest.ist.tugraz.at/";
	public static final String FILE_UPLOAD_URL = BASE_URL + "api/upload/upload.json";
	public static final String TEST_FILE_UPLOAD_URL = BASE_URL_TEST + "api/upload/upload.json";
	public static final String TEST_FILE_DOWNLOAD_URL = BASE_URL_TEST + "catroid/download/";
	public static final String CHECK_TOKEN_URL = BASE_URL + "api/checkToken/check.json";
	public static final String TEST_CHECK_TOKEN_URL = BASE_URL_TEST + "api/checkToken/check.json";
	public static final String REGISTRATION_URL = BASE_URL + "api/registration/registrationRequest.json";
	public static final String TEST_REGISTRATION_URL = BASE_URL_TEST + "api/registration/registrationRequest.json";

	//DefaultProject:
	public static final String NORMAL_CAT = "normalCat";
	public static final String BANZAI_CAT = "banzaiCat";
	public static final String CHESHIRE_CAT = "cheshireCat";
	public static final String BACKGROUND = "background";

	//Download:
	public static final String PROJECTNAME_TAG = "fname=";

	//Stage:
	public static final String SCREENSHOT_FILE_NAME = "thumbnail.png";

	//Various:
	public static final int BUFFER_8K = 8 * 1024;
	public static final String PAINTROID_DOWNLOAD_LINK = "https://code.google.com/p/catroid/downloads/detail?name=Paintroid_0.6.4b.apk&can=2&q=";

	// Service
	public static final String SERVICE_PROJECT_NAME = "project_name";
	public static final String SERVICE_PROJECT_DESCRIPTION = "project_description";
	public static final String SERVICE_PROJECT_PATH = "project_path";
	public static final String SERVICE_TOKEN = "user_token";

}
