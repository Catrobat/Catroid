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

package org.catrobat.catroid.transfers.project

import android.app.IntentService
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.ResultReceiver
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import android.util.Log
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.Constants.*
import org.catrobat.catroid.io.ProjectAndSceneScreenshotLoader
import org.catrobat.catroid.io.ZipArchiver
import org.catrobat.catroid.ui.MainMenuActivity
import org.catrobat.catroid.utils.DeviceSettingsProvider
import org.catrobat.catroid.utils.ImageEditing
import org.catrobat.catroid.utils.ToastUtil
import org.catrobat.catroid.utils.Utils
import org.catrobat.catroid.utils.notifications.StatusBarNotificationManager
import org.catrobat.catroid.web.ServerCalls
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*

val UPLOAD_FILE_NAME = "upload" + CATROBAT_EXTENSION

class ProjectUploadService : IntentService("ProjectUploadService") {

    private val TAG = ProjectUploadService::class.java.simpleName

    override fun onHandleIntent(projectUploadIntent: Intent?) {
        val intent = projectUploadIntent ?: return

        val projectPath = intent.getStringExtra(EXTRA_PROJECT_PATH) ?: return
        val projectDirectory = File(projectPath)
        if (projectDirectory.listFiles().isEmpty()) return
        val projectName = intent.getStringExtra(EXTRA_UPLOAD_NAME)

        val resultReceiver = intent.getParcelableExtra(EXTRA_RESULT_RECEIVER) as? ResultReceiver ?: return
        val uploadName = intent.getStringExtra(EXTRA_UPLOAD_NAME)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN)
        val username = sharedPreferences.getString(Constants.USERNAME, Constants.NO_USERNAME)

        scaleSceneScreenshots(projectName, intent.getStringArrayExtra(EXTRA_SCENE_NAMES))

        StatusBarNotificationManager.getInstance().createNotificationChannel(applicationContext)
        startForeground(StatusBarNotificationManager.UPLOAD_NOTIFICATION_SERVICE_ID, createUploadNotification(uploadName))

        val archive = createProjectArchive(projectDirectory) ?: return
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

        val reUploadBundle = Bundle().apply {
            putString(EXTRA_PROJECT_NAME, projectName)
            putString(EXTRA_PROJECT_DESCRIPTION, intent.getStringExtra(EXTRA_PROJECT_DESCRIPTION))
            putString(EXTRA_PROJECT_PATH, projectPath)
            putStringArray(EXTRA_SCENE_NAMES, intent.getStringArrayExtra(EXTRA_SCENE_NAMES))
            putString(EXTRA_USER_EMAIL, getUserEmail(intent.getStringExtra(EXTRA_PROVIDER)))
            putString(EXTRA_LANGUAGE, Locale.getDefault().language)
        }

        uploadProject(
                projectName = projectName, projectDescription = intent.getStringExtra(EXTRA_PROJECT_DESCRIPTION),
                projectPath = archive.absolutePath, userEmail = getUserEmail(intent.getStringExtra(EXTRA_PROVIDER)),
                language = Locale.getDefault().language, token = token, username = username,
                successCallback = ServerCalls.UploadSuccessCallback { projectId, successUsername, successToken ->
                    sharedPreferences.edit().run {
                                putString(TOKEN, successToken).commit()
                                putString(USERNAME, successUsername).commit()
                            }

                    Log.v(TAG, "Upload successful")
                    stopForeground(true)
                    notificationManager?.notify(StatusBarNotificationManager.UPLOAD_NOTIFICATION_ID, createUploadFinishedNotification(uploadName))

                    ToastUtil.showSuccess(this, R.string.notification_upload_finished)
                    resultReceiver.send(UPLOAD_RESULT_RECEIVER_RESULT_CODE, Bundle().apply { putInt(EXTRA_PROJECT_ID, projectId) })
                    archive.delete()
                },
                errorCallback = ServerCalls.UploadErrorCallback { errorCode, errorMessage ->
                    Log.e(TAG, errorMessage)
                    ToastUtil.showError(this, resources.getText(R.string.error_project_upload).toString() + " " + errorMessage)
                    stopForeground(true)
                    StatusBarNotificationManager.getInstance().createUploadRejectedNotification(applicationContext, errorCode,
                            errorMessage, reUploadBundle)
                }
        )
    }

    override fun onDestroy() {
        Utils.invalidateLoginTokenIfUserRestricted(applicationContext)
        super.onDestroy()
    }

    private fun createUploadNotification(programName: String): Notification {
        var uploadIntent = Intent(applicationContext, MainMenuActivity::class.java)
        uploadIntent.action = Intent.ACTION_MAIN
        uploadIntent = uploadIntent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        val pendingIntent = PendingIntent.getActivity(applicationContext, StatusBarNotificationManager.UPLOAD_PENDING_INTENT_REQUEST_CODE, uploadIntent, PendingIntent
                .FLAG_CANCEL_CURRENT)

        return NotificationCompat.Builder(applicationContext, StatusBarNotificationManager.CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_stat)
                .setContentTitle(applicationContext.getString(R.string.notification_upload_title_pending) + " " + programName)
                .setContentText(applicationContext.getString(R.string.notification_upload_pending))
                .setOngoing(true)
                .setProgress(100, 0, true)
                .build()
    }

    private fun createUploadFinishedNotification(programName: String): Notification {
        var uploadIntent = Intent(applicationContext, MainMenuActivity::class.java)
        uploadIntent.action = Intent.ACTION_MAIN
        uploadIntent = uploadIntent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        val pendingIntent = PendingIntent.getActivity(applicationContext, StatusBarNotificationManager.UPLOAD_PENDING_INTENT_REQUEST_CODE, uploadIntent, PendingIntent
                .FLAG_CANCEL_CURRENT)

        return NotificationCompat.Builder(applicationContext, StatusBarNotificationManager.CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_stat)
                .setContentTitle(applicationContext.getString(R.string.notification_upload_title_finished) + " " + programName)
                .setContentText(applicationContext.getString(R.string.notification_upload_finished))
                .setAutoCancel(true)
                .setOngoing(false)
                .setProgress(0, 0, false)
                .build()
    }

    private fun scaleSceneScreenshots(projectName: String, sceneNames: Array<String>?) {
        val screenshotLoader = ProjectAndSceneScreenshotLoader(applicationContext)
        sceneNames?.map { screenshotLoader.getScreenshotFile(projectName, it, false) }
                ?.filter { it.exists() && it.length() > 0 }
                ?.forEach {
                    try {
                        ImageEditing.scaleImageFile(it, 480, 480)
                    } catch (ex: FileNotFoundException) {
                        Log.e(TAG, Log.getStackTraceString(ex))
                    }
                }
    }

    private fun createProjectArchive(projectDirectory: File): File? {
        val archive = File(CACHE_DIR.absolutePath, UPLOAD_FILE_NAME)
        return try {
            ZipArchiver().zip(archive, projectDirectory.listFiles())
            archive
        } catch (ioException: IOException) {
            Log.e(TAG, Log.getStackTraceString(ioException))
            archive.delete()
            null
        }
    }

    private fun getUserEmail(provider: String): String? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        val email = when (provider) {
            FACEBOOK -> sharedPreferences.getString(FACEBOOK_EMAIL, NO_FACEBOOK_EMAIL)
            GOOGLE_PLUS -> sharedPreferences.getString(GOOGLE_EMAIL, NO_GOOGLE_EMAIL)
            else -> sharedPreferences.getString(EMAIL, NO_EMAIL)
        }

        return if (email.equals(NO_EMAIL)) {
            DeviceSettingsProvider.getUserEmail(this)
        } else {
            email
        }
    }

    private fun uploadProject(projectName: String, projectDescription: String, projectPath: String, userEmail: String?,
                              language: String, token: String, username: String, successCallback: ServerCalls.UploadSuccessCallback,
                              errorCallback: ServerCalls.UploadErrorCallback) {
        ServerCalls.getInstance().uploadProject(projectName, projectDescription, projectPath, userEmail, language,
                token, username, successCallback, errorCallback)
    }
}