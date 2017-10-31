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
package org.catrobat.catroid.common;

import android.os.Environment;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.R;

import java.util.Calendar;

public final class Constants {

	// Reflection in testcases needed
	// http://stackoverflow.com/questions/1615163/modifying-final-fields-in-java?answertab=votes#tab-top

	public static final float CURRENT_CATROBAT_LANGUAGE_VERSION = Float.valueOf(0.995f);

	public static final String REMOTE_DISPLAY_APP_ID = "CEBB9229";
	public static final int CAST_CONNECTION_TIMEOUT = 5000; //in milliseconds
	public static final int CAST_NOT_SEEING_DEVICE_TIMEOUT = 3000; //in milliseconds

	public static final String PLATFORM_NAME = "Android";
	public static final int APPLICATION_BUILD_NUMBER = 0; // updated from jenkins nightly/release build
	public static final String APPLICATION_BUILD_NAME = ""; // updated from jenkins nightly/release build
	public static final String PROJECTCODE_NAME = "code.xml";
	public static final String PROJECTPERMISSIONS_NAME = "permissions.txt";
	public static final String PROJECTCODE_NAME_TMP = "tmp_" + PROJECTCODE_NAME;
	public static final String SCENES_ENABLED_TAG = "<scenesEnabled>";

	public static final String CATROBAT_EXTENSION = ".catrobat";
	public static final String IMAGE_STANDARD_EXTENSION = ".png";
	public static final String SOUND_STANDARD_EXTENSION = ".wav";
	public static final char REMIX_URL_PREFIX_INDICATOR = '[';
	public static final char REMIX_URL_SUFIX_INDICATOR = ']';
	public static final char REMIX_URL_SEPARATOR = ',';
	public static final char REMIX_URL_PREFIX_REPLACE_INDICATOR = '(';
	public static final char REMIX_URL_SUFIX_REPLACE_INDICATOR = ')';
	public static final char REMIX_URL_REPLACE_SEPARATOR = ';';

	//Extensions:
	public static final String[] IMAGE_EXTENSIONS = {".png", ".jpg", ".jpeg", ".png", ".gif"};
	public static final String[] SOUND_EXTENSIONS = {".wav", ".mp3", ".mpga", ".wav", ".ogy"};

	public static final String DEFAULT_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ "/Pocket Code";
	public static final String TMP_PATH = DEFAULT_ROOT + "/tmp";
	public static final String TMP_IMAGE_PATH = TMP_PATH + "/PocketPaintImage.tmp";
	public static final String TEXT_TO_SPEECH_TMP_PATH = TMP_PATH + "/textToSpeech";
	public static final String IMAGE_DIRECTORY = "images";
	public static final String SOUND_DIRECTORY = "sounds";
	public static final String SCENES_DIRECTORY = "scenes";
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
	public static final String JUMPING_SUMO_SUPPORT = "JUMPING_SUMO_SUPPORT";
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
	public static final String FLAVORED_BASE_URL_HTTPS = MAIN_URL_HTTPS + "/"
			+ CatroidApplication.getAppContext().getString(R.string.flavor) + "/";
	public static final String LIBRARY_LOOKS_URL = BASE_URL_HTTPS + "pocket-library/looks";
	public static final String LIBRARY_BACKGROUNDS_URL_PORTRAIT = BASE_URL_HTTPS + "pocket-library/backgrounds-portrait";
	public static final String LIBRARY_BACKGROUNDS_URL_LANDSCAPE = BASE_URL_HTTPS + "pocket-library/backgrounds-landscape";
	public static final String LIBRARY_SOUNDS_URL = BASE_URL_HTTPS + "pocket-library/sounds";
	public static final String LIBRARY_BASE_URL = BASE_URL_HTTPS + "download-media/";
	public static final String SHARE_PROGRAM_URL = BASE_URL_HTTPS + "program/";

	public static final String CATROBAT_TERMS_OF_USE_URL = BASE_URL_HTTPS + "termsOfUse";

	public static final String CATROBAT_ABOUT_URL = "http://www.catrobat.org/";

	public static final String CATROBAT_DELETE_ACCOUNT_URL = BASE_URL_HTTPS + "profile/edit";
	public static final String CATROBAT_HELP_URL = BASE_URL_HTTPS + "help";

	public static final String CATROBAT_TOKEN_LOGIN_AMP_TOKEN = "&token=";
	public static final String CATROBAT_TOKEN_LOGIN_AMP_USERNAME = "?username=";
	public static final String CATROBAT_TOKEN_LOGIN_URL = BASE_URL_HTTPS + "tokenlogin" + CATROBAT_TOKEN_LOGIN_AMP_USERNAME;

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

	// Scratch Converter
	public static final int DOWNLOAD_FILE_HTTP_TIMEOUT = 30_000;
	public static final long INVALID_SCRATCH_PROGRAM_ID = 0;
	public static final String SCRATCH_CONVERTER_CLIENT_ID_SHARED_PREFERENCE_NAME = "scratchconverter.clientID";
	public static final String SCRATCH_CONVERTER_DOWNLOAD_STATE_SHARED_PREFERENCE_NAME = "scratchconverter"
			+ ".downloadStatePref";
	public static final String SCRATCH_CONVERTER_HOST = "scratch2.catrob.at";
	public static final String SCRATCH_SEARCH_URL = "https://api.scratch.mit.edu/search/projects";
	public static final String SCRATCH_IMAGE_BASE_URL = "https://uploads.scratch.mit.edu/projects/thumbnails/";
	public static final int SCRATCH_CONVERTER_MAX_NUMBER_OF_JOBS_PER_CLIENT = 3;
	public static final String SCRATCH_CONVERTER_WEB_SOCKET = "ws://" + SCRATCH_CONVERTER_HOST + "/convertersocket";
	public static final String SCRATCH_CONVERTER_BASE_URL = "http://" + SCRATCH_CONVERTER_HOST + "/";
	public static final String SCRATCH_CONVERTER_API_BASE_URL = SCRATCH_CONVERTER_BASE_URL + "api/v1/";
	public static final String SCRATCH_CONVERTER_API_DEFAULT_PROJECTS_URL = SCRATCH_CONVERTER_API_BASE_URL + "projects/";
	public static final int SCRATCH_SECOND_RELEASE_PUBLISHED_DATE_YEAR = 2013;
	public static final int SCRATCH_SECOND_RELEASE_PUBLISHED_DATE_MONTH = Calendar.MAY;
	public static final int SCRATCH_SECOND_RELEASE_PUBLISHED_DATE_DAY = 9;
	public static final int SCRATCH_HTTP_REQUEST_MIN_TIMEOUT = 1_000; // in ms
	public static final int SCRATCH_HTTP_REQUEST_MAX_NUM_OF_RETRIES = 2;
	public static final int SCRATCH_IMAGE_DEFAULT_WIDTH = 480;
	public static final int SCRATCH_IMAGE_DEFAULT_HEIGHT = 360;
	public static final String DATE_FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	public static final long MEMORY_OBJECT_CACHE_EXPIRE_TIME = 120_000;                          // 2 minutes (in ms)
	public static final int MEMORY_OBJECT_CACHE_MAX_SIZE = 10_000;
	public static final String INTENT_SCRATCH_PROGRAM_DATA = "scratchProgramData";
	public static final int INTENT_REQUEST_CODE_SPEECH = 0;
	public static final int INTENT_REQUEST_CODE_CONVERT = 1;

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
	public static final String PROJECT_OPENED_FROM_TEMPLATES_LIST = "templatesList";
	public static final String MEDIA_TYPE_LOOK = "look";
	public static final String MEDIA_TYPE_SOUND = "sound";

	public static final String COLLISION_PNG_META_TAG_KEY = "CollisionPolygonVertices";
	public static final int COLLISION_VERTEX_LIMIT = 100;
	public static final float COLLISION_POLYGON_CREATION_EPSILON = 10.0f;
	public static final String COLLISION_POLYGON_METADATA_PATTERN = "((((\\d+\\.\\d+);(\\d+\\.\\d+);){2,}(\\d+\\.\\d+);(\\d+\\.\\d+))\\|)*((\\d+\\.\\d+);(\\d+\\.\\d+);){2,}(\\d+\\.\\d+);(\\d+\\.\\d+)";
	public static final float COLLISION_WITH_FINGER_TOUCH_RADIUS = 50;

	public static final int CAST_IDLE_BACKGROUND_COLOR = 0x66000000;

	// background sprite is always on index 0
	public static final int Z_INDEX_BACKGROUND = 0;

	// this offset reflects the offset caused by "virtual" layers (currently only PenActor)
	// which are sneaked in at the Stage creation when starting the scene.
	public static final int Z_INDEX_NUMBER_VIRTUAL_LAYERS = 1;

	// the minimum z index a real sprite layer can have
	public static final int Z_INDEX_FIRST_SPRITE = Z_INDEX_BACKGROUND + Z_INDEX_NUMBER_VIRTUAL_LAYERS + 1;

	public static final String NO_VARIABLE_SELECTED = "No variable set";
	public static final String PROJECT_UPLOAD_NAME = "projectUploadName";
	public static final String PROJECT_UPLOAD_DESCRIPTION = "setProjectDescription";
	public static final int SAY_BRICK = 0;
	public static final int THINK_BRICK = 1;
	public static final int MAX_STRING_LENGTH_BUBBLES = 16;
	public static final int BORDER_THICKNESS_BUBBLES = 5;
	public static final int TEXT_SIZE_BUBBLE = 30;
	public static final int LINE_SPACING_BUBBLES = 3;
	public static final int PADDING_TOP = 40;
	public static final int PADDING_LEFT = 30;
	public static final int OFFSET_FOR_THINK_BUBBLES_AND_ARROW = 40;
	public static final int JUMPING_SUMO_BATTERY_STATUS = 100;

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

	//create@school constants
	public static final String CREATE_AT_SCHOOL_USER = "nolbUser";
	public static final String TMP_TEMPLATES_PATH = TMP_PATH + "/templates";

	public static final String TRANSLATION_SCENE = "_scene_";
	public static final String TRANSLATION_SPRITE = "_sprite_";
	public static final String TRANSLATION_SOUND = "_sound_";
	public static final String TRANSLATION_LOOK = "_look_";
	public static final String TRANSLATION_SPRITE_VARIABLE = "_sprite_variable_";
	public static final String TRANSLATION_SPRITE_LIST = "_sprite_list_";
	public static final String TRANSLATION_NOTE = "_note_";
	public static final String TRANSLATION_BROADCAST_MESSAGE = "_broadcast_message_";
	public static final String TRANSLATION_PROJECT_VARIABLE = "_project_variable_";
	public static final String TRANSLATION_PROJECT_LIST = "_project_list_";
	public static final Integer MAX_LOGCAT_OUTPUT_CHARS = 3000;

	//standalone constants
	public static final String START_PROJECT = BuildConfig.START_PROJECT;
	public static final String ZIP_FILE_NAME = START_PROJECT + ".zip";
	public static final String STANDALONE_PROJECT_NAME = BuildConfig.PROJECT_NAME;

	//Restricted login
	public static final String RESTRICTED_LOGIN_ACCEPTED = "restrictedLoginAccepted";

	//Flavor constants
	public static final String FLAVOR_NAME = "flavor";

	//Multilingual feature
	public static final String LANGUAGE_TAG_KEY = "applicationLanguage";
	public static final String DEVICE_LANGUAGE = "deviceLanguage";
	public static final String[] LANGUAGE_CODE = {DEVICE_LANGUAGE, "az", "bs", "ca", "cs", "sr-rCS", "sr-rSP", "da",
			"de", "en-rAU", "en-rCA", "en-rGB", "en", "es", "fr", "gl", "hr", "in", "it", "sw-rKE", "hu", "mk", "ms",
			"nl", "no", "pl", "pt-rBR", "pt", "ru", "ro", "sq", "sl", "sk", "sv", "vi", "tr", "ml", "ta", "te", "th",
			"gu", "hi", "ja", "ko", "zh-rCN", "zh-rTW", "ar", "ur", "fa", "ps", "sd", "iw"};

	// Suppress default constructor for noninstantiability
	private Constants() {
		throw new AssertionError();
	}
}
