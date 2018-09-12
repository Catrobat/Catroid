/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import org.catrobat.catroid.CatroidApplication;

import java.io.File;

import static org.catrobat.catroid.common.Constants.MAIN_URL_HTTPS;

public final class FlavoredConstants {
	// Web:
	public static final String BASE_URL_HTTPS = MAIN_URL_HTTPS + "/pocketcode/";

	public static final String POCKET_CODE_EXTERNAL_STORAGE_FOLDER_NAME = "Pocket Code";

	public static final File DEFAULT_ROOT_DIRECTORY = CatroidApplication.getAppContext().getFilesDir();

	public static final File EXTERNAL_STORAGE_ROOT_DIRECTORY = new File(
			Environment.getExternalStorageDirectory().getAbsolutePath(), POCKET_CODE_EXTERNAL_STORAGE_FOLDER_NAME);

	// Media Library:
	public static final String LIBRARY_BASE_URL = "https://share.catrob.at/pocketcode/download-media/";
	public static final String LIBRARY_LOOKS_URL = "https://share.catrob.at/pocketcode/pocket-library/looks";
	public static final String LIBRARY_BACKGROUNDS_URL_PORTRAIT = "https://share.catrob.at/pocketcode/pocket-library/backgrounds-portrait";
	public static final String LIBRARY_BACKGROUNDS_URL_LANDSCAPE = "https://share.catrob.at/pocketcode/pocket-library/backgrounds-landscape";
	public static final String LIBRARY_SOUNDS_URL = "https://share.catrob.at/pocketcode/pocket-library/sounds";

	private FlavoredConstants() {
		throw new AssertionError("No.");
	}
}
