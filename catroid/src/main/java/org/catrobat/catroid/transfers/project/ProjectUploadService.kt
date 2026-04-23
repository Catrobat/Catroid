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

package org.catrobat.catroid.transfers.project

import android.app.IntentService
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.ResultReceiver
import android.preference.PreferenceManager
import android.util.Log
import androidx.core.app.NotificationCompat
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.Constants.CATROBAT_EXTENSION
import org.catrobat.catroid.common.Constants.EMAIL
import org.catrobat.catroid.common.Constants.EXTRA_LANGUAGE
import org.catrobat.catroid.common.Constants.EXTRA_PROJECT_DESCRIPTION
import org.catrobat.catroid.common.Constants.EXTRA_PROJECT_NAME
import org.catrobat.catroid.common.Constants.EXTRA_PROJECT_PATH
import org.catrobat.catroid.common.Constants.EXTRA_PROVIDER
import org.catrobat.catroid.common.Constants.EXTRA_RESULT_RECEIVER
import org.catrobat.catroid.common.Constants.EXTRA_SCENE_NAMES
import org.catrobat.catroid.common.Constants.EXTRA_UPLOAD_NAME
import org.catrobat.catroid.common.Constants.EXTRA_USER_EMAIL
import org.catrobat.catroid.common.Constants.GOOGLE_EMAIL
import org.catrobat.catroid.common.Constants.GOOGLE_PLUS
import org.catrobat.catroid.common.Constants.MAX_PERCENT
import org.catrobat.catroid.common.Constants.NO_EMAIL
import org.catrobat.catroid.common.Constants.NO_GOOGLE_EMAIL
import org.catrobat.catroid.common.Constants.UPLOAD_RESULT_RECEIVER_RESULT_CODE
import org.catrobat.catroid.io.ProjectAndSceneScreenshotLoader
import org.catrobat.catroid.io.ZipArchiver
import org.catrobat.catroid.ui.MainMenuActivity
import org.catrobat.catroid.utils.DeviceSettingsProvider
import org.catrobat.catroid.utils.ToastUtil
import org.catrobat.catroid.utils.Utils
import org.catrobat.catroid.utils.notifications.StatusBarNotificationManager
import org.catrobat.catroid.web.CatrobatWebClient
import org.catrobat.catroid.web.ServerCalls
import java.io.File
import java.util.Locale

const val UPLOAD_FILE_NAME = "upload$CATROBAT_EXTENSION"

class ProjectUploadService : IntentService("ProjectUploadService") {

    override fun onHandleIntent(projectUploadIntent: Intent?) {
        val intent = projectUploadIntent
            ?: return logWarning("Called ProjectUploadService with null intent!")

        val projectPath = intent.getStringExtra(EXTRA_PROJECT_PATH)
            ?: return logWarning("Called ProjectUploadService without project path!")

        val projectDirectory = File(projectPath)
        if (projectDirectory.listFiles().isEmpty()) {
            return logWarning("Called ProjectUploadService with empty project directory!")
        }

        val resultReceiver = intent.getParcelableExtra(EXTRA_RESULT_RECEIVER) as? ResultReceiver
            ?: return logWarning("Called ProjectUploadService without resultReceiver!")

        val projectName = intent.getStringExtra(EXTRA_UPLOAD_NAME)
            ?: return logWarning("Called ProjectUploadService with empty project name!")

        val notificationID = StatusBarNotificationManager.getNextNotificationID()
        startForeground(
            notificationID,
            createUploadNotification(projectName)
        )

        val reUploadBundle = Bundle().apply {
            putString(EXTRA_PROJECT_NAME, projectName)
            putString(EXTRA_PROJECT_DESCRIPTION, intent.getStringExtra(EXTRA_PROJECT_DESCRIPTION))
            putString(EXTRA_PROJECT_PATH, projectPath)
            putStringArray(EXTRA_SCENE_NAMES, intent.getStringArrayExtra(EXTRA_SCENE_NAMES))
            putString(EXTRA_USER_EMAIL, getUserEmail(intent.getStringExtra(EXTRA_PROVIDER)))
            putString(EXTRA_LANGUAGE, Locale.getDefault().language)
        }

        ProjectUpload(
            projectDirectory = projectDirectory,
            projectName = projectName,
            projectDescription = intent.getStringExtra(EXTRA_PROJECT_DESCRIPTION) ?: "",
            userEmail = getUserEmail(intent.getStringExtra(EXTRA_PROVIDER)),
            sceneNames = intent.getStringArrayExtra(EXTRA_SCENE_NAMES),
            archiveDirectory = File(cacheDir, UPLOAD_FILE_NAME),
            zipArchiver = ZipArchiver(),
            screenshotLoader = ProjectAndSceneScreenshotLoader(
                applicationContext.resources.getDimensionPixelSize(R.dimen.project_thumbnail_width),
                applicationContext.resources.getDimensionPixelSize(R.dimen.project_thumbnail_height)
            ),
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this),
            serverCalls = ServerCalls(CatrobatWebClient.client)
        ).start(
            successCallback = { projectId ->
                Log.v(TAG, "Upload successful")
                stopForeground(true)
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                notificationManager?.notify(notificationID, createUploadFinishedNotification(projectName))

                ToastUtil.showSuccess(this, R.string.notification_upload_finished)
                val result = Bundle().apply { putString(Constants.EXTRA_PROJECT_ID, projectId) }
                resultReceiver.send(UPLOAD_RESULT_RECEIVER_RESULT_CODE, result)
            },
            errorCallback = { errorCode, errorMessage ->
                Log.e(TAG, errorMessage)
                stopForeground(true)
                ToastUtil.showError(this, resources.getString(R.string.error_project_upload) + " " + errorMessage)
                StatusBarNotificationManager(applicationContext)
                    .createUploadRejectedNotification(applicationContext, errorCode, errorMessage, reUploadBundle)
                resultReceiver.send(0, null)
            }
        )
    }

    override fun onDestroy() {
        Utils.invalidateLoginTokenIfUserRestricted(applicationContext)
        super.onDestroy()
    }

    private fun createUploadNotification(programName: String): Notification {
        StatusBarNotificationManager(applicationContext).createNotificationChannel(applicationContext)

        var uploadIntent = Intent(applicationContext, MainMenuActivity::class.java)
        uploadIntent.action = Intent.ACTION_MAIN
        uploadIntent = uploadIntent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                applicationContext,
                StatusBarNotificationManager.UPLOAD_PENDING_INTENT_REQUEST_CODE,
                uploadIntent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getActivity(
                applicationContext,
                StatusBarNotificationManager.UPLOAD_PENDING_INTENT_REQUEST_CODE,
                uploadIntent, PendingIntent.FLAG_CANCEL_CURRENT
            )
        }

        return NotificationCompat.Builder(applicationContext, StatusBarNotificationManager.CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle(
                applicationContext.getString(R.string.notification_upload_title_pending) + " " + programName
            )
            .setContentText(applicationContext.getString(R.string.notification_upload_pending))
            .setOngoing(true)
            .setProgress(MAX_PERCENT, 0, true)
            .build()
    }

    private fun createUploadFinishedNotification(programName: String): Notification {
        var uploadIntent = Intent(applicationContext, MainMenuActivity::class.java)
        uploadIntent.action = Intent.ACTION_MAIN
        uploadIntent = uploadIntent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                applicationContext,
                StatusBarNotificationManager.UPLOAD_PENDING_INTENT_REQUEST_CODE,
                uploadIntent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getActivity(
                applicationContext,
                StatusBarNotificationManager.UPLOAD_PENDING_INTENT_REQUEST_CODE,
                uploadIntent, PendingIntent.FLAG_CANCEL_CURRENT
            )
        }


        return NotificationCompat.Builder(applicationContext, StatusBarNotificationManager.CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle(
                applicationContext.getString(R.string.notification_upload_title_finished) + " " + programName
            )
            .setContentText(applicationContext.getString(R.string.notification_upload_finished))
            .setAutoCancel(true)
            .setOngoing(false)
            .setProgress(0, 0, false)
            .build()
    }

    private fun getUserEmail(provider: String?): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        val email = when (provider) {
            GOOGLE_PLUS -> sharedPreferences.getString(GOOGLE_EMAIL, NO_GOOGLE_EMAIL)
            else -> sharedPreferences.getString(EMAIL, NO_EMAIL)
        }

        val result = if (email == NO_EMAIL) {
            DeviceSettingsProvider.getUserEmail(this)
        } else {
            email
        }

        return result ?: ""
    }

    private fun logWarning(warningMessage: String) {
        Log.w(TAG, warningMessage)
    }

    companion object {
        private val TAG = ProjectUploadService::class.java.simpleName
    }
}
