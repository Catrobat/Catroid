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

package org.catrobat.catroid.common
import android.os.Environment
import org.catrobat.catroid.CatroidApplication
import java.io.File
import org.catrobat.catroid.common.Constants.MAIN_URL_HTTPS
class FlavoredConstants private constructor() {
  init{
    throw AssertionError("No.")
  }
  companion object {
    // Web:
    val BASE_URL_HTTPS = MAIN_URL_HTTPS + "/pocketcode/"
    val POCKET_CODE_EXTERNAL_STORAGE_FOLDER_NAME = "Pocket Code"
    val DEFAULT_ROOT_DIRECTORY = CatroidApplication.getAppContext().getFilesDir()
    val EXTERNAL_STORAGE_ROOT_DIRECTORY = File(
      Environment.getExternalStorageDirectory(), POCKET_CODE_EXTERNAL_STORAGE_FOLDER_NAME)
    // Media Library:
    val LIBRARY_BASE_URL = BASE_URL_HTTPS + "download-media/"
    val LIBRARY_LOOKS_URL = BASE_URL_HTTPS + "media-library/looks"
    val LIBRARY_BACKGROUNDS_URL_PORTRAIT = BASE_URL_HTTPS + "media-library/backgrounds-portrait"
    val LIBRARY_BACKGROUNDS_URL_LANDSCAPE = BASE_URL_HTTPS + "media-library/backgrounds-landscape"
    val LIBRARY_SOUNDS_URL = BASE_URL_HTTPS + "media-library/sounds"
    val PRIVACY_POLICY_URL = "https://catrob.at/privacypolicy"
  }
}
