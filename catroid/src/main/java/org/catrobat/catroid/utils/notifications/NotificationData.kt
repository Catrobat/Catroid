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

package org.catrobat.catroid.utils.notifications

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import java.io.Serializable

data class NotificationData(
    var notificationIconResId: Int,
    var programName: String,
    var titleWorking: String,
    var titleDone: String,
    var textWorking: String,
    var textDone: String,
    var progressInPercent: Int = 0,
    var maxProgress: Int = 0,
    var ongoing: Boolean = false,
    var autoCancel: Boolean = false,
    val notificationID: Int
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 42
    }

    fun toNotification(context: Context, channelId: String, contentIntent: PendingIntent?): Notification {
        val title = (if (progressInPercent < maxProgress) titleWorking else titleDone) + " " + programName
        val text = if (progressInPercent < maxProgress) textWorking else textDone

        return NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(notificationIconResId)
                .setProgress(maxProgress, progressInPercent, false)
                .setOngoing(ongoing)
                .setAutoCancel(autoCancel)
                .setContentIntent(contentIntent)
                .build()
    }
}
