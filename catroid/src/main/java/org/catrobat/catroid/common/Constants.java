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
package org.catrobat.catroid.common;

import android.os.Environment;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.CatroidApplication;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import androidx.annotation.IntDef;
import androidx.exifinterface.media.ExifInterface;

import static org.catrobat.catroid.common.FlavoredConstants.BASE_URL_HTTPS;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;

public final class Constants {

	public static final double CURRENT_CATROBAT_LANGUAGE_VERSION = 1.12;
	public static final String REMOTE_DISPLAY_APP_ID = "CEBB9229";
	public static final int CAST_CONNECTION_TIMEOUT = 5000; //in milliseconds
	public static final int CAST_NOT_SEEING_DEVICE_TIMEOUT = 3000; //in
	public static final long PROGESSIVE_INPUT_DELAY = 400;
	public static final long PROGESSIVE_INPUT_COUNTDOWN_INTERVALL = 500;
	public static final long RETROFIT_WRITE_TIMEOUT = 15;

	public static final String PLATFORM_NAME = "Android";
	public static final int APPLICATION_BUILD_NUMBER = 0; // updated from jenkins nightly/release build
	public static final String APPLICATION_BUILD_NAME = ""; // updated from jenkins nightly/release build
	public static final String CODE_XML_FILE_NAME = "code.xml";
	public static final String PERMISSIONS_FILE_NAME = "permissions.txt";
	public static final String TMP_CODE_XML_FILE_NAME = "tmp_" + CODE_XML_FILE_NAME;
	public static final String UNDO_CODE_XML_FILE_NAME = "undo_" + CODE_XML_FILE_NAME;

	public static final String DEVICE_VARIABLE_JSON_FILE_NAME = "DeviceVariables.json";
	public static final String DEVICE_LIST_JSON_FILE_NAME = "DeviceLists.json";

	public static final String POCKET_CODE_EXTERNAL_EXPORT_STORAGE_DIRECTORY_NAME = "Catrobat";
	public static final File EXTERNAL_STORAGE_ROOT_EXPORT_DIRECTORY = new File(
			Environment.getExternalStorageDirectory(), POCKET_CODE_EXTERNAL_EXPORT_STORAGE_DIRECTORY_NAME);
	public static final File DOWNLOAD_DIRECTORY =
			new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "");
	public static final String CATROBAT_EXTENSION = ".catrobat";
	public static final String ZIP_EXTENSION = ".zip";
	public static final String DEFAULT_IMAGE_EXTENSION = ".png";
	public static final String JPEG_IMAGE_EXTENSION = ".jpg";
	public static final String DEFAULT_SOUND_EXTENSION = ".wav";
	public static final String EMBROIDERY_FILE_EXTENSION = ".dst";
	public static final String TEXT_FILE_EXTENSION = ".txt";
	public static final char REMIX_URL_PREFIX_INDICATOR = '[';
	public static final char REMIX_URL_SUFIX_INDICATOR = ']';
	public static final char REMIX_URL_SEPARATOR = ',';
	public static final char REMIX_URL_PREFIX_REPLACE_INDICATOR = '(';
	public static final char REMIX_URL_SUFIX_REPLACE_INDICATOR = ')';
	public static final char REMIX_URL_REPLACE_SEPARATOR = ';';

	// Files and Directories:
	public static final String NO_MEDIA_FILE = ".nomedia";

	public static final String IMAGE_DIRECTORY_NAME = "images";
	public static final String SOUND_DIRECTORY_NAME = "sounds";
	public static final String SCREENSHOT_AUTOMATIC_FILE_NAME = "automatic_screenshot" + DEFAULT_IMAGE_EXTENSION;
	public static final String SCREENSHOT_MANUAL_FILE_NAME = "manual_screenshot" + DEFAULT_IMAGE_EXTENSION;
	public static final File TMP_LOOK_FILE =
			new File(DEFAULT_ROOT_DIRECTORY, "temporary_look_file" + DEFAULT_IMAGE_EXTENSION);

	// Recent Bricks Directory
	public static final String RECENT_BRICKS_DIRECTORY = "recent_bricks";
	public static final String RECENT_BRICKS_FILE = "recent_bricks.json";

	// Backpack Directories
	public static final String BACKPACK_DIRECTORY_NAME = "backpack";
	public static final String BACKPACK_JSON_FILE_NAME = "backpack.json";
	public static final String BACKBACK_SCENES_DIRECTORY_NAME = "scenes";
	public static final String BACKPACK_SOUND_DIRECTORY_NAME = "backpack_sound";
	public static final String BACKPACK_IMAGE_DIRECTORY_NAME = "backpack_image";

	// Trusted domains for Web access bricks
	public static final String TRUSTED_DOMAINS_FILE_NAME = "trustedDomains.json";
	public static final String TRUSTED_USER_DOMAINS_FILE_NAME = "trustedUserDomains.json";
	public static final String TRUST_LIST_JSON_ARRAY_NAME = "domains";
	public static final File TRUSTED_USER_DOMAINS_FILE = new File(DEFAULT_ROOT_DIRECTORY, TRUSTED_USER_DOMAINS_FILE_NAME);
	public static final int JSON_INDENTATION = 4;

	// Temporary Files and Directories:
	public static final File CACHE_DIRECTORY = CatroidApplication.getAppContext().getCacheDir();

	public static final String CACHED_PROJECT_ZIP_FILE_NAME = "projectImportCache.zip";

	public static final File POCKET_PAINT_CACHE_DIRECTORY = new File(CACHE_DIRECTORY, "pocketPaint");
	public static final File CAMERA_CACHE_DIRECTORY = new File(CACHE_DIRECTORY, "camera");
	public static final File SOUND_RECORDER_CACHE_DIRECTORY = new File(CACHE_DIRECTORY, "soundRecorder");
	public static final File MEDIA_LIBRARY_CACHE_DIRECTORY = new File(CACHE_DIRECTORY, "mediaLibrary");

	public static final String TMP_IMAGE_FILE_NAME = "image";

	public static final String TMP_DIRECTORY_NAME = "tmp";
	public static final String TMP_PATH = CACHE_DIRECTORY.getAbsolutePath() + "/" + TMP_DIRECTORY_NAME;
	public static final String TEXT_TO_SPEECH_TMP_PATH = TMP_PATH + "/textToSpeech";

	// Web:
	private static final String MAIN_URL_PRODUCTION = "https://share.catrob.at";
	public static final String UPLOAD_URL = "https://upload.catrob.at";
	private static final String WEB_TEST_URL = BuildConfig.WEB_TEST_URL;
	public static final String MAIN_URL_HTTPS = BuildConfig.WEB_TEST_FLAG ? WEB_TEST_URL : MAIN_URL_PRODUCTION;

	// Default "flavor" in the web which equals "pocketcode"
	public static final String BASE_APP_URL_HTTPS = MAIN_URL_HTTPS + "/app/";

	public static final String SHARE_PROJECT_URL = BASE_URL_HTTPS + "/project/";

	public static final String CATROBAT_ABOUT_URL = "https://www.catrobat.org/";
	public static final String CATROBAT_FORMULA_WIKI_URL = "https://wiki.catrobat.org/bin/view/Documentation/FormulaEditor";
	public static final String ABOUT_POCKETCODE_LICENSE_URL = "https://catrob.at/licenses";
	public static final String WEB_REQUEST_WIKI_URL = "https://catrob.at/webbricks";

	public static final String CATROBAT_TERMS_OF_USE_URL = BASE_URL_HTTPS + "termsOfUse";
	public static final String CATROBAT_FUNCTIONS_WIKI_URL = CATROBAT_FORMULA_WIKI_URL + "/Functions/";
	public static final String CATROBAT_LOGIC_WIKI_URL = CATROBAT_FORMULA_WIKI_URL + "/Logic/";
	public static final String CATROBAT_SENSORS_WIKI_URL = CATROBAT_FORMULA_WIKI_URL + "/Sensors/";
	public static final String CATROBAT_OBJECT_WIKI_URL = CATROBAT_FORMULA_WIKI_URL + "/Properties/";
	public static final String CATROBAT_DELETE_ACCOUNT_URL = BASE_URL_HTTPS + "profile/edit";
	public static final String CATROBAT_TERMS_OF_USE_TOKEN_FLAVOR_URL = "?flavorName=";
	public static final String CATROBAT_TERMS_OF_USE_TOKEN_VERSION_URL = "&versionCode=";
	public static final int CATROBAT_TERMS_OF_USE_ACCEPTED = 1;

	public static final String PLAY_STORE_PAGE_LINK = "https://play.google.com/store/apps/details?id=";
	public static final String HUAWEI_APP_GALLERY_LINK = "https://catrob.at/HuaweiAppGallery";

	public static final String USERNAME_COOKIE_NAME = "CATRO_LOGIN_USER";
	public static final String TOKEN_COOKIE_NAME = "CATRO_LOGIN_TOKEN";

	public static final String USER_AGENT = "Mozilla/5.0 (compatible; Catrobatbot/1.0; +https://catrob.at/bot)";

	// HTTP status codes:
	public static final int ERROR_BAD_REQUEST = 400;
	public static final int ERROR_TOO_MANY_REQUESTS = 429;
	public static final int ERROR_SERVER_ERROR = 500;
	public static final int ERROR_TIMEOUT = 504;
	public static final int ERROR_AUTHENTICATION_REQUIRED = 511;

	// XStream:
	public static final String BLUETOOTH_LEGO_NXT = "BLUETOOTH_LEGO_NXT";
	public static final String NFC = "NFC";

	//Broadcast system:
	public static final String RASPI_BROADCAST_PREFIX = "#RASPI#";

	// Login:
	public static final String CATROBAT_TOKEN_LOGIN_AMP_TOKEN = "&token=";
	public static final String TOKEN = "token";
	public static final String NO_TOKEN = "no_token";
	public static final String USERNAME = "username";
	public static final String NO_USERNAME = "no_username";
	public static final String EMAIL = "email";
	public static final String NO_EMAIL = "no_email";
	public static final String LOCALE = "locale";
	public static final String RESTRICTED_USER = "restricted_user";
	public static final String JSON_ERROR_CODE = "errorCode";

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
	public static final String GOOGLE_PLUS = "GPLUS";
	public static final String NO_OAUTH_PROVIDER = "NATIVE";

	public static final String REQUEST_MOBILE = "mobile";

	public static final String FLAVOR_DEFAULT = "PocketCode";
	public static final String PLATFORM_DEFAULT = "Android";

	public static final String WHATSAPP_URI = "whatsapp://";

	// Scratch Converter
	public static final long INVALID_SCRATCH_PROGRAM_ID = 0;

	public static final String SCRATCH_CONVERTER_HOST = "scratch2.catrob.at";
	public static final String SCRATCH_SEARCH_URL = "https://api.scratch.mit.edu/search/projects";
	public static final int SCRATCH_CONVERTER_MAX_NUMBER_OF_JOBS_PER_CLIENT = 3;
	public static final String SCRATCH_CONVERTER_WEB_SOCKET = "wss://" + SCRATCH_CONVERTER_HOST + "/convertersocket";
	public static final String SCRATCH_CONVERTER_BASE_URL = "https://" + SCRATCH_CONVERTER_HOST + "/";
	public static final String SCRATCH_CONVERTER_API_BASE_URL = SCRATCH_CONVERTER_BASE_URL + "api/v1/";
	public static final String SCRATCH_CONVERTER_API_DEFAULT_PROJECTS_URL = SCRATCH_CONVERTER_API_BASE_URL + "projects/";
	public static final int SCRATCH_SECOND_RELEASE_PUBLISHED_DATE_YEAR = 2013;
	public static final int SCRATCH_SECOND_RELEASE_PUBLISHED_DATE_MONTH = Calendar.MAY;
	public static final int SCRATCH_SECOND_RELEASE_PUBLISHED_DATE_DAY = 9;
	public static final int SCRATCH_HTTP_REQUEST_MIN_TIMEOUT = 1_000; // in ms
	public static final int SCRATCH_HTTP_REQUEST_MAX_NUMBER_OF_RETRIES = 2;
	public static final int SCRATCH_IMAGE_DEFAULT_WIDTH = 480;
	public static final int SCRATCH_IMAGE_DEFAULT_HEIGHT = 360;
	public static final String DATE_FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	public static final String INTENT_SCRATCH_PROGRAM_DATA = "scratchProgramData";
	public static final int INTENT_REQUEST_CODE_CONVERT = 1;

	// Lego
	@Retention(RetentionPolicy.SOURCE)
	@IntDef({NXT, EV3})
	public @interface LegoSensorType {
	}

	public static final int NXT = 0;
	public static final int EV3 = 1;

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({PORT_1, PORT_2, PORT_3, PORT_4})
	public @interface LegoPort {
	}

	public static final int PORT_1 = 0;
	public static final int PORT_2 = 1;
	public static final int PORT_3 = 2;
	public static final int PORT_4 = 3;

	// Pocket Paint
	public static final String EXTRA_PICTURE_PATH_POCKET_PAINT = "org.catrobat.extra.PAINTROID_PICTURE_PATH";
	public static final String POCKET_PAINT_INTENT_ACTIVITY_NAME = "org.catrobat.paintroid.MainActivity";

	// Intent Extra / Bundle Keys
	public static final String EXTRA_PROJECT_NAME = "projectName";
	public static final String EXTRA_PROJECT_DESCRIPTION = "projectDescription";
	public static final String EXTRA_PROJECT_PATH = "projectPath";
	public static final String EXTRA_PROJECT_ID = "projectId";
	public static final String EXTRA_SCENE_NAMES = "sceneNames";
	public static final String EXTRA_USER_EMAIL = "userEmail";
	public static final String EXTRA_LANGUAGE = "language";
	public static final String EXTRA_RESULT_RECEIVER = "resultReceiver";
	public static final String EXTRA_PROVIDER = "provider";
	public static final String EXTRA_UPLOAD_NAME = "uploadName";

	public static final int UPLOAD_RESULT_RECEIVER_RESULT_CODE = 1;

	//Various:
	public static final int BUFFER_8K = 8 * 1024;
	public static final String PREF_PROJECTNAME_KEY = "projectName";
	public static final int MAX_FILE_NAME_LENGTH = 127;
	public static final double COORDINATE_TRANSFORMATION_OFFSET = 0.5;

	public static final String COLLISION_PNG_META_TAG_KEY = "CollisionPolygonVertices";
	public static final int COLLISION_VERTEX_LIMIT = 100;
	public static final float COLLISION_POLYGON_CREATION_EPSILON = 10.0f;
	public static final String COLLISION_POLYGON_METADATA_PATTERN = "((((\\d+\\.\\d+);(\\d+\\.\\d+);){2,}(\\d+\\.\\d+);(\\d+\\.\\d+))\\|)*((\\d+\\.\\d+);(\\d+\\.\\d+);){2,}(\\d+\\.\\d+);(\\d+\\.\\d+)";
	public static final float COLLISION_WITH_FINGER_TOUCH_RADIUS = 50;

	public static final int CAST_IDLE_BACKGROUND_COLOR = 0x66000000;

	public static final int Z_INDEX_BACKGROUND = 0;

	public static final int Z_INDEX_NUMBER_VIRTUAL_LAYERS = 2;

	public static final int Z_INDEX_FIRST_SPRITE = Z_INDEX_BACKGROUND + Z_INDEX_NUMBER_VIRTUAL_LAYERS + 1;

	public static final int SAY_BRICK = 0;
	public static final int THINK_BRICK = 1;
	public static final int MAX_STRING_LENGTH_BUBBLES = 16;
	public static final int BORDER_THICKNESS_BUBBLES = 5;
	public static final int TEXT_SIZE_BUBBLE = 30;
	public static final int LINE_SPACING_BUBBLES = 3;
	public static final int PADDING_TOP = 40;
	public static final int OFFSET_FOR_THINK_BUBBLES_AND_ARROW = 40;

	public static final int UPLOAD_IMAGE_SCALE_WIDTH = 480;
	public static final int UPLOAD_IMAGE_SCALE_HEIGHT = 480;

	public static final int TEXT_FROM_CAMERA_SENSOR_HASHCODE = 1613638780;

	public static final int MAX_NUMBER_OF_CHECKED_TAGS = 3;

	//Services + Notifications
	public static final int UPDATE_DOWNLOAD_PROGRESS = 101;
	public static final int UPDATE_DOWNLOAD_ERROR = 105;
	public static final int MAX_PERCENT = 100;

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

	//ExifTags for Exifremover
	public static final ArrayList<String> EXIFTAGS_FOR_EXIFREMOVER =
			new ArrayList<String>(Arrays.asList(
			ExifInterface.TAG_ARTIST,
			ExifInterface.TAG_DATETIME,
			ExifInterface.TAG_DATETIME_ORIGINAL,
			ExifInterface.TAG_DATETIME_DIGITIZED,
			ExifInterface.TAG_EXPOSURE_TIME,
			ExifInterface.TAG_FLASH,
			ExifInterface.TAG_FILE_SOURCE,
			ExifInterface.TAG_GPS_ALTITUDE,
			ExifInterface.TAG_GPS_ALTITUDE_REF,
			ExifInterface.TAG_GPS_AREA_INFORMATION,
			ExifInterface.TAG_GPS_DATESTAMP,
			ExifInterface.TAG_GPS_DEST_BEARING,
			ExifInterface.TAG_GPS_DEST_BEARING_REF,
			ExifInterface.TAG_GPS_DEST_DISTANCE,
			ExifInterface.TAG_GPS_DEST_DISTANCE_REF,
			ExifInterface.TAG_GPS_DEST_LATITUDE,
			ExifInterface.TAG_GPS_DEST_LATITUDE_REF,
			ExifInterface.TAG_GPS_DEST_LONGITUDE,
			ExifInterface.TAG_GPS_DEST_LONGITUDE_REF,
			ExifInterface.TAG_GPS_LATITUDE,
			ExifInterface.TAG_GPS_LATITUDE_REF,
			ExifInterface.TAG_GPS_LONGITUDE,
			ExifInterface.TAG_GPS_LONGITUDE_REF,
			ExifInterface.TAG_GPS_MAP_DATUM,
			ExifInterface.TAG_GPS_SATELLITES,
			ExifInterface.TAG_GPS_TIMESTAMP,
			ExifInterface.TAG_GPS_PROCESSING_METHOD,
			ExifInterface.TAG_GPS_DATESTAMP,
			ExifInterface.TAG_MAKE,
			ExifInterface.TAG_MODEL,
			ExifInterface.TAG_IMAGE_DESCRIPTION,
			ExifInterface.TAG_STRIP_OFFSETS,
			ExifInterface.TAG_SOFTWARE,
			ExifInterface.TAG_CAMARA_OWNER_NAME
	));

	public static final String FLAVOR_POCKET_CODE = "catroid";
	public static final String FLAVOR_EMBROIDERY_DESIGNER = "embroideryDesigner";
	public static final String FLAVOR_LEGO_NXT_EV3 = "mindstorms";
	public static final String FLAVOR_PHIRO = "phiro";
	public static final String FLAVOR_LUNA_AND_CAT = "lunaAndCat";
	public static final String FLAVOR_CREATE_AT_SCHOOL = "createAtSchool";

	public static final String PREFRENCE_PLAYSTORE_EMBROIDERY_URL = "https://play.google.com/store/apps/details?id=org.catrobat.catroid.embroiderydesigner";
	public static final String PREFRENCE_APPGALLERY_EMBROIDERY_URL = "https://appgallery.huawei.com/app/C100085769";
	public static final String PREFRENCE_PLAYSTORE_MINDSTORMS_URL = "https://play.google.com/store/apps/details?id=org.catrobat.catroid";
	public static final String PREFRENCE_APPGALLERY_MINDSTORMS_URL = "https://appgallery.huawei.com/app/C100085769";
	public static final String PREFRENCE_PLAYSTORE_PHIRO_URL = "https://play.google.com/store/apps/details?id=org.catrobat.catroid.phiro";
	public static final String PREFRENCE_APPGALLERY_PHIRO_URL = "https://appgallery.huawei.com/app/C100085769";

	public static final String DEVICE_BRAND_HUAWEI = "huawei";

	private Constants() {
		throw new AssertionError("No.");
	}
}
