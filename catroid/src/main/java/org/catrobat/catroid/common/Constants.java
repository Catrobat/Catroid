/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
package org.catrobat.catroid.common;

import android.os.Environment;

public final class Constants {

	// Reflection in testcases needed
	// http://stackoverflow.com/questions/1615163/modifying-final-fields-in-java?answertab=votes#tab-top

	public static final float CURRENT_CATROBAT_LANGUAGE_VERSION = Float.valueOf(0.991f);

	public static final String REMOTE_DISPLAY_APP_ID = "CEBB9229";
	public static final int CAST_CONNECTION_TIMEOUT = 5000; //in milliseconds
	public static final int CAST_NOT_SEEING_DEVICE_TIMEOUT = 3000; //in milliseconds

	public static final String PLATFORM_NAME = "Android";
	public static final int APPLICATION_BUILD_NUMBER = 0; // updated from jenkins nightly/release build
	public static final String APPLICATION_BUILD_NAME = ""; // updated from jenkins nightly/release build
	public static final String PROJECTCODE_NAME = "code.xml";
	public static final String PROJECTPERMISSIONS_NAME = "permissions.txt";
	public static final String PROJECTPERMISSIONS_NAME_TMP = "tmp_" + PROJECTPERMISSIONS_NAME;
	public static final String PROJECTCODE_NAME_TMP = "tmp_" + PROJECTCODE_NAME;

	public static final String CATROBAT_EXTENSION = ".catrobat";
	public static final String IMAGE_STANDARD_EXTENSION = ".png";
	public static final String SOUND_STANDARD_EXTENSION = ".wav";

	//Extensions:
	public static final String[] IMAGE_EXTENSIONS = { ".png", ".jpg", ".jpeg", ".png", ".gif" };
	public static final String[] SOUND_EXTENSIONS = { ".wav", ".mp3", ".mpga", ".wav", ".ogy" };

	public static final String DEFAULT_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ "/Pocket Code";
	public static final String TMP_PATH = DEFAULT_ROOT + "/tmp";
	public static final String TMP_IMAGE_PATH = TMP_PATH + "/PocketPaintImage.tmp";
	public static final String TEXT_TO_SPEECH_TMP_PATH = TMP_PATH + "/textToSpeech";
	public static final String IMAGE_DIRECTORY = "images";
	public static final String SOUND_DIRECTORY = "sounds";
	public static final String BACKPACK_DIRECTORY = "backpack";
	public static final String TMP_LOOKS_PATH = TMP_PATH + "/looks";
	public static final String TMP_SOUNDS_PATH = TMP_PATH + "/sounds";

	public static final String BACKPACK_SOUND_DIRECTORY = "backpack_sound";
	public static final String BACKPACK_IMAGE_DIRECTORY = "backpack_image";
	public static final String NUMBER_OF_BRICKS_INSERTED_FROM_BACKPACK = "NUMBER_OF_BRICKS_INSERTED";
	public static final String FILENAME_SEPARATOR = "_";

	public static final String NO_MEDIA_FILE = ".nomedia";

	public static final String TEXT_TO_SPEECH = "TEXT_TO_SPEECH";
	public static final String BLUETOOTH_LEGO_NXT = "BLUETOOTH_LEGO_NXT";
	public static final String BLUETOOTH_PHIRO_PRO = "BLUETOOTH_PHIRO_PRO";
	public static final String ARDRONE_SUPPORT = "ARDRONE_SUPPORT";
	public static final String CAMERA_FLASH = "CAMERA_FLASH";
	public static final String VIBRATOR = "VIBRATOR";
	public static final String FACE_DETECTION = "FACE_DETECTION";
	public static final String NFC = "NFC";

	public static final int NO_POSITION = -1;

	//Broadcast system:
	public static final String ACTION_SPRITE_SEPARATOR = "#";
	public static final String BROADCAST_NOTIFY_ACTION = "broadcast_notify";
	public static final String START_SCRIPT = "start_script";
	public static final String BROADCAST_SCRIPT = "broadcast_script";
	public static final String RASPI_SCRIPT = "raspi_pinchange_script";
	public static final String RASPI_BROADCAST_PREFIX = "#RASPI#";
	public static final String RASPI_BROADCAST_INTERRUPT_PREFIX = RASPI_BROADCAST_PREFIX + "interrupt ";
	public static final String OPENING_BRACE = "(";

	//Web:
	public static final String MAIN_URL_HTTPS = "https://share.catrob.at";
	public static final String BASE_URL_HTTPS = MAIN_URL_HTTPS + "/pocketcode/";
	public static final String LIBRARY_LOOKS_URL = "https://share.catrob.at/pocketcode/pocket-library/looks";
	public static final String LIBRARY_BACKGROUNDS_URL = "https://share.catrob.at/pocketcode/pocket-library/backgrounds";
	public static final String LIBRARY_SOUNDS_URL = "https://share.catrob.at/pocketcode/pocket-library/sounds";
	public static final String LIBRARY_BASE_URL = "https://share.catrob.at/pocketcode/download-media/";
	public static final String SHARE_PROGRAM_URL = "https://share.catrob.at/pocketcode/program/";

	public static final String CATROBAT_TERMS_OF_USE_URL = BASE_URL_HTTPS + "termsOfUse";

	public static final String CATROBAT_ABOUT_URL = "http://www.catrobat.org/";
	public static final String ABOUT_POCKETCODE_LICENSE_URL = "http://developer.catrobat.org/licenses";

	public static final String CATROBAT_HELP_URL = BASE_URL_HTTPS + "help";
	public static final String CATROBAT_TOKEN_LOGIN_URL = BASE_URL_HTTPS + "tokenlogin?username=";
	public static final String CATROBAT_TOKEN_LOGIN_AMP_TOKEN = "&token=";

	public static final String STANDALONE_URL = "http://catrob.at/csadttwt";

	public static final String TOKEN = "token";
	public static final String NO_TOKEN = "no_token";
	public static final String FACEBOOK_TOKEN_REFRESH_NEEDED = "FACEBOOK_TOKEN_REFRESH_NEEDED";
	public static final String USERNAME = "username";
	public static final String NO_USERNAME = "no_username";
	public static final String EMAIL = "email";
	public static final String NO_EMAIL = "no_email";
	public static final String LOCALE = "locale";
	public static final String RESTRICTED_USER = "restricted_user";
	public static final String FACEBOOK_ID = "FACEBOOK_ID";
	public static final String NO_FACEBOOK_ID = "NO_FACEBOOK_ID";
	public static final String FACEBOOK_USERNAME = "FACEBOOK_USERNAME";
	public static final String NO_FACEBOOK_USERNAME = "NO_FACEBOOK_USERNAME";
	public static final String FACEBOOK_LOCALE = "FACEBOOK_LOCALE";
	public static final String NO_FACEBOOK_LOCALE = "NO_FACEBOOK_LOCALE";
	public static final String FACEBOOK_EMAIL = "FACEBOOK_EMAIL";
	public static final String NO_FACEBOOK_EMAIL = "NO_FACEBOOK_EMAIL";
	public static final String JSON_ERROR_CODE = "errorCode";
	public static final int ERROR_CODE_FACEBOOK_SESSION_EXPIRED = 190;

	public static final String GOOGLE_ID = "GOOGLE_ID";
	public static final String NO_GOOGLE_ID = "NO_GOOGLE_ID";
	public static final String GOOGLE_USERNAME = "GOOGLE_USERNAME";
	public static final String NO_GOOGLE_USERNAME = "NO_GOOGLE_USERNAME";
	public static final String GOOGLE_LOCALE = "GOOGLE_LOCALE";
	public static final String NO_GOOGLE_LOCALE = "NO_GOOGLE_LOCALE";
	public static final String GOOGLE_EMAIL = "GOOGLE_EMAIL";
	public static final String NO_GOOGLE_EMAIL = "NO_GOOGLE_EMAIL";
	public static final String GOOGLE_ID_TOKEN = "GOOGLE_ID_TOKEN";
	public static final String NO_GOOGLE_ID_TOKEN = "NO_GOOGLE_ID_TOKEN";
	public static final String GOOGLE_EXCHANGE_CODE = "GOOGLE_EXCHANGE_CODE";
	public static final String NO_GOOGLE_EXCHANGE_CODE = "NO_GOOGLE_EXCHANGE_CODE";
	public static final String CURRENT_OAUTH_PROVIDER = "PROVIDER";
	public static final String FACEBOOK = "FACEBOOK";
	public static final String GOOGLE_PLUS = "GPLUS";
	public static final String NO_OAUTH_PROVIDER = "NATIVE";

	public static final String REQUEST_MOBILE = "mobile";

	public static final String FLAVOR_DEFAULT = "PocketCode";
	public static final String PLATFORM_DEFAULT = "Android";

	public static final String WHATSAPP_URI = "whatsapp://";

	// Pocket Paint
	public static final String EXTRA_PICTURE_PATH_POCKET_PAINT = "org.catrobat.extra.PAINTROID_PICTURE_PATH";
	public static final String EXTRA_PICTURE_NAME_POCKET_PAINT = "org.catrobat.extra.PAINTROID_PICTURE_NAME";
	public static final String EXTRA_X_VALUE_POCKET_PAINT = "org.catrobat.extra.PAINTROID_X";
	public static final String EXTRA_Y_VALUE_POCKET_PAINT = "org.catrobat.extra.PAINTROID_Y";
	public static final String POCKET_PAINT_PACKAGE_NAME = "org.catrobat.paintroid";
	public static final String POCKET_PAINT_DOWNLOAD_LINK = "market://details?id=" + POCKET_PAINT_PACKAGE_NAME;
	public static final String POCKET_PAINT_INTENT_ACTIVITY_NAME = "org.catrobat.paintroid.MainActivity";
	//Various:
	public static final int BUFFER_8K = 8 * 1024;
	public static final String PREF_PROJECTNAME_KEY = "projectName";
	public static final String PROJECTNAME_TO_LOAD = "projectNameToLoad";
	public static final String PROJECT_OPENED_FROM_PROJECTS_LIST = "projectList";
	public static final String MEDIA_TYPE_LOOK = "look";
	public static final String MEDIA_TYPE_SOUND = "sound";
	public static final String NO_VARIABLE_SELECTED = "No variable set";
	public static final String PROJECT_UPLOAD_NAME = "projectUploadName";
	public static final String PROJECT_UPLOAD_DESCRIPTION = "setProjectDescription";

	//Services + Notifications
	public static final int UPDATE_UPLOAD_PROGRESS = 100;
	public static final int UPDATE_DOWNLOAD_PROGRESS = 101;
	public static final int UPDATE_DOWNLOAD_ERROR = 105;

	//Up-/Download Status Codes
	public static final int STATUS_CODE_INTERNAL_SERVER_ERROR = 500;
	public static final int STATUS_CODE_UPLOAD_MISSING_DATA = 501;
	public static final int STATUS_CODE_UPLOAD_MISSING_CHECKSUM = 503;
	public static final int STATUS_CODE_UPLOAD_INVALID_CHECKSUM = 504;
	public static final int STATUS_CODE_UPLOAD_COPY_FAILED = 505;
	public static final int STATUS_CODE_UPLOAD_UNZIP_FAILED = 506;
	public static final int STATUS_CODE_UPLOAD_MISSING_XML = 507;
	public static final int STATUS_CODE_UPLOAD_RENAME_FAILED = 513;
	public static final int STATUS_CODE_UPLOAD_SAVE_THUMBNAIL_FAILED = 514;
	public static final int STATUS_CODE_UPLOAD_OLD_CATROBAT_LANGUAGE = 518;
	public static final int STATUS_CODE_UPLOAD_OLD_CATROBAT_VERSION = 519;

	// Suppress default constructor for noninstantiability
	private Constants() {
		throw new AssertionError();
	}
}
