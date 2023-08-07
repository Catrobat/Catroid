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
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.ResultReceiver
import android.util.Log
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.Constants.CACHE_DIRECTORY
import org.catrobat.catroid.common.Constants.CATROBAT_EXTENSION
import org.catrobat.catroid.common.Constants.EXTRA_PROJECT_NAME
import org.catrobat.catroid.common.Constants.MAX_PERCENT
import org.catrobat.catroid.common.Constants.TMP_DIRECTORY_NAME
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.io.ZipArchiver
import org.catrobat.catroid.ui.MainMenuActivity
import org.catrobat.catroid.utils.FileMetaDataExtractor
import org.catrobat.catroid.utils.ToastUtil
import org.catrobat.catroid.utils.notifications.NotificationData
import org.catrobat.catroid.utils.notifications.StatusBarNotificationManager
import org.catrobat.catroid.utils.notifications.StatusBarNotificationManager.CHANNEL_ID
import org.catrobat.catroid.web.CatrobatServerCalls
import org.catrobat.catroid.web.CatrobatServerCalls.DownloadErrorCallback
import org.catrobat.catroid.web.CatrobatServerCalls.DownloadProgressCallback
import org.catrobat.catroid.web.CatrobatServerCalls.DownloadSuccessCallback
import org.catrobat.catroid.web.CatrobatWebClient
import java.io.File
import java.io.IOException

class ProjectDownloadService : IntentService("ProjectDownloadService") {

    companion object {
        val TAG: String = ProjectDownloadService::class.java.simpleName
        const val EXTRA_DOWNLOAD_NAME = "downloadName"
        const val EXTRA_URL = "url"
        const val EXTRA_RESULT_RECEIVER = "receiver"
        const val EXTRA_NOTIFICATION_DATA = "notificationData"
        private const val DOWNLOAD_FILE_NAME = "down$CATROBAT_EXTENSION"

        const val UPDATE_PROGRESS_EXTRA = "progress"

        const val UPDATE_PROGRESS_CODE = 1
        const val ERROR_CODE = 2
        const val SUCCESS_CODE = 3
    }

    override fun onHandleIntent(intent: Intent?) {
        val downloadIntent = intent
            ?: return logWarning("Called ProjectDownloadService with null intent - aborting")
        val projectName = downloadIntent.getStringExtra(EXTRA_DOWNLOAD_NAME)
            ?: return logWarning("Called ProjectDownloadService with null projectName -  aborting")
        val url = downloadIntent.getStringExtra(EXTRA_URL)
            ?: return logWarning("Called ProjectDownloadService without url - aborting")
        val resultReceiver = downloadIntent.getParcelableExtra<ResultReceiver>(EXTRA_RESULT_RECEIVER)
                ?: return logWarning("Called ProjectDownloadService without url - aborting")

        val zipFileString = File(File(CACHE_DIRECTORY, TMP_DIRECTORY_NAME), DOWNLOAD_FILE_NAME).absolutePath
        val destinationFile = File(zipFileString)

        if ((destinationFile.parentFile.isDirectory or destinationFile.parentFile.mkdirs()).not()) {
            ToastUtil.showError(this, R.string.error_project_download)
            return
        }

        val notificationData = downloadIntent.getSerializableExtra(EXTRA_NOTIFICATION_DATA) as NotificationData
        val notification = notificationData.toNotification(this, CHANNEL_ID, null)
        val id = notificationData.notificationID

        startForeground(id, notification)

        CatrobatServerCalls(CatrobatWebClient.client)
            .downloadProject(
                url,
                destinationFile,
                object : DownloadSuccessCallback {
                    override fun onSuccess() {
                        downloadSuccessCallback(
                            this@ProjectDownloadService,
                            projectName,
                            destinationFile,
                            resultReceiver
                        )
                    }
                },
                object : DownloadErrorCallback {
                    override fun onError(code: Int, message: String) {
                        downloadErrorCallback(this@ProjectDownloadService, resultReceiver, projectName)
                    }
                },
                object : DownloadProgressCallback {
                    override fun onProgress(progress: Long) {
                        downloadProgressCallback(this@ProjectDownloadService, resultReceiver, notificationData, progress)
                    }
                }
            )

        stopForeground(true)
    }

    private fun downloadSuccessCallback(
        context: Context,
        projectName: String,
        destinationFile: File,
        resultReceiver: ResultReceiver
    ) {
        val statusBarNotificationManager = StatusBarNotificationManager(context)
        val notificationData = statusBarNotificationManager.createProjectDownloadNotification(this, projectName)

        try {
            val projectNameForFileSystem = FileMetaDataExtractor.encodeSpecialCharsForFileSystem(projectName)
            val projectDir = File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, projectNameForFileSystem)
            ZipArchiver().unzip(destinationFile, projectDir)

            XstreamSerializer.renameProject(File(projectDir, Constants.CODE_XML_FILE_NAME), projectName)
            ProjectManager.getInstance().addNewDownloadedProject(projectName)

            val downloadIntent = Intent(context, MainMenuActivity::class.java)
            downloadIntent.setAction(Intent.ACTION_MAIN)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra(EXTRA_PROJECT_NAME, projectName)

            val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getActivity(
                    context, notificationData.notificationID, downloadIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                PendingIntent.getActivity(
                    context, notificationData.notificationID, downloadIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT
                )
            }
            statusBarNotificationManager.showOrUpdateNotification(
                context,
                notificationData,
                MAX_PERCENT,
                pendingIntent
            )
            resultReceiver.send(SUCCESS_CODE, Bundle())
        } catch (exception: IOException) {
            exception.message?.let {
                Log.i(TAG, it)
            }
            statusBarNotificationManager
                .abortProgressNotificationWithMessage(context, notificationData, R.string.error_project_download)

            resultReceiver.send(ERROR_CODE, Bundle())
        }
    }

    private fun downloadErrorCallback(
        context: Context,
        resultReceiver: ResultReceiver,
        projectName: String
    ) {
        val statusBarNotificationManager = StatusBarNotificationManager(context)
        val notificationData = statusBarNotificationManager.createProjectDownloadNotification(this, projectName)
        statusBarNotificationManager.abortProgressNotificationWithMessage(context, notificationData, R.string.error_project_download)

        resultReceiver.send(ERROR_CODE, Bundle())
    }

    private fun downloadProgressCallback(
        context: Context,
        resultReceiver: ResultReceiver,
        notificationData: NotificationData,
        progress: Long
    ) {
        StatusBarNotificationManager(context)
            .showOrUpdateNotification(context, notificationData, progress.toInt(), null)
        val bundle = Bundle()
        bundle.putInt(UPDATE_PROGRESS_EXTRA, progress.toInt())
        resultReceiver.send(UPDATE_PROGRESS_CODE, bundle)
    }

    private fun logWarning(warningMessage: String) {
        Log.w(TAG, warningMessage)
    }
}
