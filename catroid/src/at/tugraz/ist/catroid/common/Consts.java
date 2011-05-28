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

	public static final String DIRECTORY_NAME = "catroid";
	public static final String PROJECT_EXTENTION = ".spf";
	public static final String CATROID_EXTENTION = ".zip";

	public static final String DEFAULT_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + "/catroid";
	public static final String TMP_PATH = DEFAULT_ROOT + "/tmp";
	public static final String IMAGE_DIRECTORY = "/images";
	public static final String SOUND_DIRECTORY = "/sounds";

	//dialogs:
	public static final int DIALOG_NEW_PROJECT = 0;
	public static final int DIALOG_LOAD_PROJECT = 1;
	public static final int DIALOG_ABOUT = 2;
	public static final int DIALOG_NEW_SPRITE = 3;
	public static final int DIALOG_RENAME_SPRITE = 4;
	public static final int DIALOG_NEW_SCRIPT = 5;
	public static final int DIALOG_RENAME_SCRIPT = 6;
	public static final int DIALOG_ADD_BRICK = 7;
	public static final int DIALOG_UPLOAD_PROJECT = 8;
	public static final int DIALOG_CONTEXT_MENU = 9;

	//Costume:
	public static final int MAX_REL_COORDINATES = 1000;
	public static final int THUMBNAIL_WIDTH = 80;
	public static final int THUMBNAIL_HEIGHT = 80;
	public static final int MAX_COSTUME_WIDTH = 1000;
	public static final int MAX_COSTUME_HEIGHT = 1000;
	public static final int JPG_COMPRESSION_SETING = 95;

	//Animations:
	public static final int ANIMATION_DURATION_BRICK_SWITCHING = 500; //ms
	public static final int ANIMATION_DURATION_EXPAND = 500; //ms
	public static final int ANIMATION_EXPAND_DELAY = 50; //ms

	//Upload:
	public static final String FILE_UPLOAD_TAG = "upload";
	public static final String PROJECT_NAME_TAG = "projectTitle";
	public static final String PROJECT_DESCRIPTION_TAG = "projectDescription";
	public static final String PROJECT_CHECKSUM_TAG = "fileChecksum";
	public static final String TOKEN = "token";
	public static final String DEVICE_IMEI = "deviceIMEI";
	public static final String USER_EMAIL = "userEmail";
	public static final String USER_LANGUAGE = "userLanguage";

	public static final String FILE_UPLOAD_URL = "http://www.catroid.org/api/upload/upload.json";
	public static final String TEST_FILE_UPLOAD_URL = "http://catroidwebtest.ist.tugraz.at/api/upload/upload.json";
	public static final String TEST_FILE_DOWNLOAD_URL = "http://catroidwebtest.ist.tugraz.at/catroid/download/";

	//DefaultProject:
	public static final String NORMAL_CAT = "normalCat";
	public static final String BANZAI_CAT = "banzaiCat";
	public static final String CHESHIRE_CAT = "cheshireCat";
	public static final String BACKGROUND = "background";

	//Download:
	public static final String PROJECTNAME_TAG = "fname=";

	//Stage:
	public static final int SCREENSHOT_ICON_PADDING_TOP = 3;
	public static final int SCREENSHOT_ICON_PADDING_RIGHT = 3;
	public static final String SCREENSHOT_FILE_NAME = "thumbnail.png";

	//Various:
	public static final int BUFFER_8K = 8 * 1024;
}
