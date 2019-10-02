/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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

package org.catrobat.catroid.transfers.project

import android.app.IntentService
import android.content.Intent
import android.os.ResultReceiver
import android.util.Log
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants.CATROBAT_EXTENSION
import org.catrobat.catroid.common.Constants.TMP_PATH
import org.catrobat.catroid.utils.ToastUtil
import java.io.File

class ProjectDownloadService: IntentService("ProjectDownloadService") {



    // TODO: CATROID-167
    // TODO: create notification for startforeground
    // TODO: update notification with downloadprogress

    override fun onHandleIntent(intent: Intent?) {
        val downloadIntent = intent
            ?: return logWarning("Called ProjectDownloadService with null intent - aborting")
        val projectName = downloadIntent.getStringExtra(EXTRA_DOWNLOAD_NAME)
            ?: return logWarning("Called ProjectDownloadService with null projectName -  aborting")
        val url = downloadIntent.getStringExtra(EXTRA_URL)
            ?: return logWarning("Called ProjectDownloadService without url - aborting")
        val resultReceiver = downloadIntent.getParcelableExtra(EXTRA_RESULT_RECEIVER) as? ResultReceiver
            ?: return logWarning("Called ProjectDownloadService without resultReceiver - aborting")
        val renameAfterDownload = downloadIntent.getBooleanExtra(EXTRA_RENAME_AFTER_DOWNLOAD, false)

        val destinationFile = File(TMP_PATH, DOWNLOAD_FILE_NAME)
        if ((destinationFile.parentFile.isDirectory or destinationFile.parentFile.mkdirs()).not()) {
            ToastUtil.showError(this, R.string.error_project_download)
            return
        }


    }

    private fun logWarning(warningMessage: String) {
        Log.w(TAG, warningMessage)
    }

    companion object {
        val TAG: String = ProjectDownloadService::class.java.simpleName
        const val EXTRA_DOWNLOAD_NAME = "downloadName"
        const val EXTRA_URL = "url"
        const val EXTRA_RESULT_RECEIVER = "resultReceiver"
        const val EXTRA_RENAME_AFTER_DOWNLOAD = "renameAfterDownload"
        private const val DOWNLOAD_FILE_NAME = "down$CATROBAT_EXTENSION"
    }
}
