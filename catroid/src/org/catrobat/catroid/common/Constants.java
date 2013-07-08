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
package org.catrobat.catroid.common;

import android.os.Environment;

public final class Constants {

	public static final float SUPPORTED_CATROBAT_LANGUAGE_VERSION = 0.8f;
	public static final String PLATFORM_NAME = "Android";
	public static final int APPLICATION_BUILD_NUMBER = 0; // updated from jenkins nightly/release build
	public static final String APPLICATION_BUILD_NAME = ""; // updated from jenkins nightly/release build
	public static final String PROJECTCODE_NAME = "code.xml";
	public static final String CATROBAT_EXTENTION = ".catrobat";
	public static final String RECORDING_EXTENTION = ".mp3";

	public static final String DEFAULT_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ "/Pocket Code";
	public static final String TMP_PATH = DEFAULT_ROOT + "/tmp";
	public static final String IMAGE_DIRECTORY = "images";
	public static final String SOUND_DIRECTORY = "sounds";

	public static final String NO_MEDIA_FILE = ".nomedia";

	public static final int NO_POSITION = -1;

	//Web:
	public static final String TOKEN = "token";
	public static final String NO_TOKEN = "no_token";
	public static final String USERNAME = "username";
	public static final String NO_USERNAME = "no_username";

	// Pocket Paint
	public static final String EXTRA_PICTURE_PATH_POCKET_PAINT = "org.catrobat.extra.PAINTROID_PICTURE_PATH";
	public static final String EXTRA_PICTURE_NAME_POCKET_PAINT = "org.catrobat.extra.PAINTROID_PICTURE_NAME";
	public static final String EXTRA_X_VALUE_POCKET_PAINT = "org.catrobat.extra.PAINTROID_X";
	public static final String EXTRA_Y_VALUE_POCKET_PAINT = "org.catrobat.extra.PAINTROID_Y";
	public static final String POCKET_PAINT_PACKAGE_NAME = "org.catrobat.paintroid.pocketpaint";
	public static final String POCKET_PAINT_INTENT_ACTIVITY_NAME = "org.catrobat.paintroid.pocketpaint.MainActivity";

	//Various:
	public static final int BUFFER_8K = 8 * 1024;
	public static final String POCKET_PAINT_DOWNLOAD_LINK = "market://details?id="
			+ POCKET_PAINT_PACKAGE_NAME;
	public static final String PREF_PROJECTNAME_KEY = "projectName";

	//Services + Notifications
	public static final int UPDATE_UPLOAD_PROGRESS = 100;
	public static final int UPDATE_DOWNLOAD_PROGRESS = 101;
	public static final int UPLOAD_NOTIFICATION = 102;
	public static final int DOWNLOAD_NOTIFICATION = 103;
	public static final int COPY_NOTIFICATION = 104;
}
