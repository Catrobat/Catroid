/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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
package org.catrobat.catroid.ui

import android.content.Intent
import android.util.Log
import org.catrobat.catroid.BuildConfig
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.io.ZipArchiver
import org.catrobat.catroid.io.asynctask.ProjectLoader
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.utils.FileMetaDataExtractor
import java.io.File
import java.io.IOException

abstract class BaseCastActivityWithProjectLoader : BaseCastActivity(), ProjectLoader.ProjectLoadListener {
    override fun onLoadFinished(success: Boolean) {
        if (BuildConfig.FEATURE_APK_GENERATOR_ENABLED && success) {
            startActivityForResult(
                Intent(this, StageActivity::class.java), StageActivity.REQUEST_START_STAGE
            )
        }
    }

    protected fun prepareStandaloneProject() {
        try {
            val inputStream = assets.open(BuildConfig.START_PROJECT + ".zip")
            val projectDir = File(
                FlavoredConstants.DEFAULT_ROOT_DIRECTORY,
                FileMetaDataExtractor.encodeSpecialCharsForFileSystem(
                    BuildConfig.PROJECT_NAME
                )
            )
            ZipArchiver()
                .unzip(inputStream, projectDir)
            ProjectLoader(projectDir, this)
                .setListener(this)
                .loadProjectAsync()
        } catch (e: IOException) {
            Log.e("STANDALONE", "Cannot unpack standalone project: ", e)
        }
    }
}
