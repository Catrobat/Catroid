/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.common;

import android.os.Environment;

public final class Consts {

	//public static final String PROJECT_EXTENTION = ".xml";
	public static final String PROJECTCODE_NAME = "projectcode.xml";
	public static final String CATROID_EXTENTION = ".catroid";
	public static final String RECORDING_EXTENTION = ".mp3";

	public static final String DEFAULT_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + "/catroid";
	public static final String TMP_PATH = DEFAULT_ROOT + "/tmp";
	public static final String IMAGE_DIRECTORY = "images";
	public static final String SOUND_DIRECTORY = "sounds";

	public static final String NO_MEDIA_FILE = ".nomedia";

	//Web:
	public static final String TOKEN = "token";

	//Various:
	public static final int BUFFER_8K = 8 * 1024;
	public static final String PAINTROID_DOWNLOAD_LINK = "http://code.google.com/p/catroid/downloads";
	public static final String PREF_PROJECTNAME_KEY = "projectName";
}
