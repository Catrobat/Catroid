/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2025 The Catrobat Team
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

package org.catrobat.catroid.sync

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.catrobat.catroid.web.WebConnectionException
import org.koin.java.KoinJavaComponent.inject

class ProjectsCategoriesSyncWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val projectsCategoriesSync: ProjectsCategoriesSync
        by inject(ProjectsCategoriesSync::class.java)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val attempt = runAttemptCount + 1
        Log.d(TAG, "doWork() attempt $attempt")

        try {
            projectsCategoriesSync.sync()
            Result.success()
        } catch (webError: WebConnectionException) {
            if (attempt < MAX_RUN_ATTEMPTS) {
                Log.d(TAG, "sync failed, retrying", webError)
                Result.retry()
            } else {
                Log.w(TAG, "giving up after $attempt attempts", webError)
                Result.failure()
            }
        } finally {
            if (isStopped && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Log.e(TAG, "stopped by the system, stopReason = $stopReason")
            }
        }
    }

    companion object {
        private val TAG = ProjectsCategoriesSyncWorker::class.java.simpleName
        private const val MAX_RUN_ATTEMPTS = 3
    }
}
